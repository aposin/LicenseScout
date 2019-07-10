/**
 * Copyright 2019 Association for the promotion of open-source insurance software and for the establishment of open interface standards in the insurance industry (Verein zur Förderung quelloffener Versicherungssoftware und Etablierung offener Schnittstellenstandards in der Versicherungsbranche)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aposin.licensescout.finder;

import static org.aposin.licensescout.archive.ArchiveType.JAVA;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.license.ArtifactServerUtil;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.util.ArchiveMetaInformation;
import org.aposin.licensescout.util.CryptUtil;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JarUtil;

/**
 * Scan for licenses in an Eclipse plugins directory.
 * 
 * <p>The implementation does a recursive search through the file system starting with the directory set with {@link #setScanDirectory(File)} as
 * a starting point.</p>
 *  
 */
public class JavaJarFinder extends AbstractFinder {

    private final List<String> specialArchiveNames = new ArrayList<>();
    private final FinderHandler<File, FileSystemEntryContainer, File> fileSystemFinderHandler;
    private final FinderHandler<JarEntry, JarEntryContainer, JarInputStream> jarFinderHandler;
    private final ArtifactServerUtil artifactServerUtil;

    /**
     * Constructor.
     * 
     * @param licenseStoreData
     * @param runParameters
     * @param log the logger
     */
    public JavaJarFinder(final LicenseStoreData licenseStoreData, final RunParameters runParameters, final ILFLog log) {
        super(licenseStoreData, log);
        artifactServerUtil = new ArtifactServerUtil(runParameters.getNexusCentralBaseUrl(),
                runParameters.getConnectTimeout(), log);

        fileSystemFinderHandler = new FilesystemFinderHandler(log);
        jarFinderHandler = new JarFinderHandler(log);

        initSpecialArchiveNames();
    }

    private void initSpecialArchiveNames() {
        specialArchiveNames.add("ckeditor");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateLicenseFile(final String fileName) {
        final String fileNameLowerCase = fileName.toLowerCase();
        final boolean isTextOrHtmlFile = fileNameLowerCase.endsWith("txt") || fileNameLowerCase.endsWith("html")
                || fileNameLowerCase.endsWith("htm");
        final boolean containsLicenseInFilename = fileNameLowerCase.contains("license")
                || fileNameLowerCase.contains("licence");
        final boolean containsNoticeInFilename = fileNameLowerCase.contains("notice");
        return (isTextOrHtmlFile || containsLicenseInFilename || containsNoticeInFilename)
                && !fileNameLowerCase.endsWith(".class");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void findLicensesImpl() throws Exception {
        final File root = getScanDirectory();
        final String filePath = "";
        parseFile(root, filePath);
    }

    /**
     * <p>This method is called recursively as long as no file or directory is detected as being an archive. For archives, the recursion continues with {@link #parsePackedJarArchive(Archive, InputStream, File, String)} or 
     * {@link #parseUnpackedJarArchive(Archive, File, String)}.</p>
     * 
     * @param file
     * @param filePath
     * 
     * @throws Exception
     */
    private void parseFile(final File file, final String filePath) throws Exception {
        getLog().debug("parseFile(): processing " + file.getAbsolutePath());
        final FinderHandler<File, FileSystemEntryContainer, File> finderHandler = fileSystemFinderHandler;
        final String entryName = file.getName();
        final FileSystemEntryContainer entryContainer = finderHandler.createEntryContainer(file);
        if (finderHandler.isDirectory(file)) {
            if (isArchiveDirectory(file)) {
                getLog().debug("parseFile(): recognized as archive directory");
                final File manifestFile = getManifestFile(file);
                String version = null;
                String vendor = null;
                if (manifestFile != null) {
                    final ArchiveMetaInformation archiveMetaInformation = JarUtil
                            .getArchiveMetaInformationFromManifestFile(manifestFile, getLog());
                    version = archiveMetaInformation.getVersion();
                    vendor = archiveMetaInformation.getVendor();
                }
                version = getVersionNotNull(version);
                final Archive foundArchive = new Archive(JAVA, entryName, version, filePath);
                foundArchive.setVendor(vendor);
                archiveFiles.add(foundArchive);
                parseUnpackedJarArchive(foundArchive, file, filePath);
            } else {
                getLog().debug("parseFile(): recognized as normal directory");
                final File[] children = file.listFiles();
                for (final File child : children) {
                    final String newFilePath = filePath + "/" + child.getName();
                    parseFile(child, newFilePath);
                }
            }
        } else { // is file
            if (isArchiveName(entryName)) {
                getLog().debug("parseFile(): recognized as archive file");
                final ArchiveMetaInformation archiveMetaInformation = finderHandler
                        .getArchiveMetaInformationFromManifest(entryContainer);
                String version = archiveMetaInformation.getVersion();
                version = getVersionNotNull(version);
                final Archive foundArchive = new Archive(JAVA, entryName, version, filePath);
                foundArchive.setVendor(archiveMetaInformation.getVendor());
                archiveFiles.add(foundArchive);
                addMessageDigest(finderHandler, entryContainer, foundArchive);
                final String newFilePath = filePath + "!";
                try (final FileInputStream archiveFileInputStream = new FileInputStream(file)) {
                    addLicenseFromManifest(foundArchive, archiveMetaInformation, newFilePath);
                    parsePackedJarArchive(foundArchive, archiveFileInputStream, file, newFilePath);
                }
            } else {
                getLog().debug("parseFile(): recognized as normal file - ignored");
            }
        }
    }

    private void parseUnpackedJarArchive(final Archive archive, final File parent, final String filePath)
            throws Exception {
        getLog().debug("parseUnpackedJarArchive(): processing " + parent.getAbsolutePath());
        final FinderHandler<File, FileSystemEntryContainer, File> finderHandler = fileSystemFinderHandler;
        final File[] entries = parent.listFiles();
        for (final File entry : entries) {
            final String entryName = finderHandler.getEntryName(entry);
            final FileSystemEntryContainer entryContainer = finderHandler.createEntryContainer(entry);
            final String newFilePath = filePath + "/" + entryName;
            getLog().debug("parseUnpackedJarArchive(): processing " + newFilePath);
            if (isSpecialArchive(entryName)) {
                getLog().debug("parseUnpackedJarArchive(): recognized as special archive");
                final Archive foundArchive = new Archive(JAVA, entryName, "0.0.0", newFilePath);
                archiveFiles.add(foundArchive);
                parseUnpackedJarArchive(foundArchive, entryContainer.getFile(), newFilePath);
                continue;
            }
            if (finderHandler.isDirectory(entry)) {
                getLog().debug("parseUnpackedJarArchive(): recognized as directory");
                if (finderHandler.isUseDirectoryRecursion()) {
                    parseUnpackedJarArchive(archive, entryContainer.getFile(), newFilePath);
                }
            } else { // is file
                if (isArchiveName(entryName)) {
                    getLog().debug("parseUnpackedJarArchive(): recognized as archive file");
                    final ArchiveMetaInformation archiveMetaInformation = finderHandler
                            .getArchiveMetaInformationFromManifest(entryContainer);
                    String version = archiveMetaInformation.getVersion();
                    version = getVersionNotNull(version);
                    final Archive foundArchive = new Archive(JAVA, entryName, version, newFilePath);
                    foundArchive.setVendor(archiveMetaInformation.getVendor());
                    archiveFiles.add(foundArchive);
                    addMessageDigest(finderHandler, entry, entryContainer, foundArchive);
                    final String newFilePath2 = newFilePath + "!";
                    addLicenseFromManifest(foundArchive, archiveMetaInformation, newFilePath2);
                    try (final FileInputStream archiveInputStream = new FileInputStream(entryContainer.getFile())) {
                        parsePackedJarArchive(foundArchive, archiveInputStream, entry, newFilePath2);
                    }
                } else {
                    getLog().debug("parseUnpackedJarArchive(): recognized as normal file");
                    if (isCandidateLicenseFile(entryName)) {
                        archive.addLicenseCandidateFile(newFilePath);
                    }
                    try (final InputStream inputStream = entryContainer.getInputStream()) {
                        final Collection<License> licenses = checkFileForLicenses(inputStream, entryName,
                                getLicenseStoreData());
                        addLicenses(archive, licenses, entry, newFilePath);
                    }
                    if (isPomFile(entryName)) {
                        addLicensesFromPom(entryContainer, archive, newFilePath, getLog());
                    }
                }
            }
        }
    }

    private void parsePackedJarArchive(final Archive archive, final InputStream fileInputStream, final File parent,
                                       final String filePath)
            throws Exception {
        final FinderHandler<JarEntry, JarEntryContainer, JarInputStream> finderHandler = jarFinderHandler;
        getLog().debug("parsePackedJarArchive(): processing " + parent.getAbsolutePath());
        try (final JarInputStream jarInputStream = new JarInputStream(fileInputStream)) {
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                final String entryName = finderHandler.getEntryName(entry);
                final String newFilePath = filePath + "/" + entryName;
                getLog().debug("parsePackedJarArchive(): processing " + newFilePath);
                if (isArchiveName(entryName)) {
                    final String newFilePath2 = newFilePath + "!";
                    final JarEntryContainer entryContainer = finderHandler.createEntryContainer(jarInputStream);
                    final ArchiveMetaInformation archiveMetaInformation = finderHandler
                            .getArchiveMetaInformationFromManifest(entryContainer);
                    final File file = new File(parent, entryName);
                    String version = null;
                    if (finderHandler.isFile(entry)) {
                        version = archiveMetaInformation.getVersion();
                    }
                    version = getVersionNotNull(version);
                    final String simpleName = getSimpleName(entryName);
                    final Archive foundArchive = new Archive(JAVA, simpleName, version, newFilePath);
                    foundArchive.setVendor(archiveMetaInformation.getVendor());
                    archiveFiles.add(foundArchive);
                    addMessageDigest(finderHandler, entry, entryContainer, foundArchive);
                    addLicenseFromManifest(foundArchive, archiveMetaInformation, newFilePath2);
                    parsePackedJarArchive(foundArchive, entryContainer.getInputStream(), file, newFilePath2);
                } else {
                    if (finderHandler.isFile(entry)) {
                        if (isCandidateLicenseFile(entryName)) {
                            archive.addLicenseCandidateFile(newFilePath);
                        }
                        final File file = new File(parent, entryName);
                        final Collection<License> licenses = checkFileForLicenses(jarInputStream, entryName,
                                getLicenseStoreData());
                        addLicenses(archive, licenses, file, newFilePath);
                        final JarEntryContainer entryContainer = finderHandler.createEntryContainer(jarInputStream);
                        if (isPomFile(entryName)) {
                            addLicensesFromPom(entryContainer, archive, newFilePath, getLog());
                        }
                    }
                }
            }
        }
    }

    private void addLicensesFromPom(final EntryContainer entryContainer, final Archive archive, final String filePath,
                                    final ILFLog log)
            throws Exception {
        try (final InputStream inputStream = entryContainer.getInputStream()) {
            artifactServerUtil.addLicensesFromPom(inputStream, archive, filePath, getLicenseStoreData(), log);
        }
    }

    private String getSimpleName(final String entryName) {
        final int pos = entryName.lastIndexOf('/');
        if (pos >= 0) {
            return entryName.substring(pos + 1);
        }
        return entryName;
    }

    private void addMessageDigest(final FinderHandler<File, FileSystemEntryContainer, File> finderHandler,
                                  final File entry, final FileSystemEntryContainer entryContainer,
                                  final Archive archive)
            throws IOException {
        if (finderHandler.isFile(entry)) {
            final byte[] md = finderHandler.calculateMessageDigest(entryContainer);
            archive.setMessageDigest(md);
        }
    }

    private void addMessageDigest(final FinderHandler<File, FileSystemEntryContainer, File> finderHandler,
                                  final FileSystemEntryContainer entryContainer, final Archive archive)
            throws IOException {
        if (finderHandler.isFile(entryContainer.getFile())) {
            final byte[] md = CryptUtil.calculateMessageDigest(entryContainer.getFile());
            archive.setMessageDigest(md);
        }
    }

    private void addMessageDigest(final FinderHandler<JarEntry, JarEntryContainer, JarInputStream> finderHandler,
                                  final JarEntry entry, final JarEntryContainer entryContainer, final Archive archive)
            throws IOException {
        if (finderHandler.isFile(entry)) {
            final byte[] md = finderHandler.calculateMessageDigest(entryContainer);
            archive.setMessageDigest(md);
        }
    }

    private String getVersionNotNull(String version) {
        if (version == null) {
            return "";
        }
        return version;
    }

    private void addLicenseFromManifest(final Archive archive, final ArchiveMetaInformation archiveMetaInformation,
                                        final String filePath) {
        String licenseUrl = archiveMetaInformation.getLicenseUrl();
        if (licenseUrl != null) {
            licenseUrl = licenseUrl.trim();
            final String[] licenseUrls = licenseUrl.split(",");
            boolean licenseFound = false;
            final String licenseFileName = filePath + "/META-INF/MANIFEST.MF";
            for (String licenseUrl2 : licenseUrls) {
                licenseUrl2 = licenseUrl2.trim();
                licenseFound |= LicenseUtil.handleLicenseUrl(licenseUrl2, archive, licenseFileName,
                        getLicenseStoreData(), getLog());
            }
            if (!licenseFound) {
                getLog().warn("License not found by URL: " + licenseUrl);
            }
        }
    }

    /**
     * Checks if is archive by checking if the name contains "jar".
     * 
     * <p>Note: this method only relies on the filename. It does not check containing files that indicate an archive.</p>
     * 
     * @param fileName the file name
     * @return true, if is archive
     */
    private static boolean isArchiveName(final String fileName) {
        return fileName.endsWith(".jar");
    }

    /**
     * Checks if passed file is an unpacked JAR file.
     * 
     * @param dir the file name
     * @return true, if is archive
     */
    private static boolean isArchiveDirectory(final File dir) {
        if (!dir.isDirectory()) {
            return false;
        }
        final String[] entries = dir.list();
        final List<String> entriesList = Arrays.asList(entries);
        return entriesList.contains("META-INF");
    }

    /**
     * Obtains a file object for "META-INF/MANIFEST.MF" in an unpacked JAR.
     * 
     * @param dir the file name
     * @return true, if is archive
     */
    private static File getManifestFile(final File dir) {
        final File[] entries1 = dir.listFiles();
        for (final File entry1 : entries1) {
            if ("META-INF".equalsIgnoreCase(entry1.getName())) {
                final File[] entries2 = entry1.listFiles();
                for (final File entry2 : entries2) {
                    if ("MANIFEST.MF".equalsIgnoreCase(entry2.getName())) {
                        return entry2;
                    }
                }
            }
        }
        return null;
    }

    private boolean isSpecialArchive(final String name) {
        return specialArchiveNames.contains(name);
    }

    private boolean isPomFile(final String name) {
        return name.endsWith("pom.xml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: use this value; output it in reports
    public boolean isPomResolutionUsed() {
        return artifactServerUtil.isCachedCheckAccess();
    }

    @FunctionalInterface
    private static interface EntryContainer {

        /**
         * Obtains an Input stream.
         * 
         * @return an Input stream
         * @throws IOException
         */
        public InputStream getInputStream() throws IOException;
    }

    private static class FileSystemEntryContainer implements EntryContainer {

        private final File file;

        /**
         * @param file
         */
        public FileSystemEntryContainer(final File file) {
            this.file = file;
        }

        /**
         * @return the file
         */
        public final File getFile() {
            return file;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final InputStream getInputStream() throws IOException {
            return new FileInputStream(getFile());
        }

    }

    private static class JarEntryContainer implements EntryContainer {

        private final byte[] contents;

        /**
         * @param contents
         */
        public JarEntryContainer(final byte[] contents) {
            this.contents = contents;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final InputStream getInputStream() {
            return new ByteArrayInputStream(contents);
        }
    }

    private static interface FinderHandler<F, C extends EntryContainer, I> {

        /**
         * Checks if the algorithm should go into a recursion for non-archive directories.
         * 
         * @return true if directory recursion should be used, false otherwise
         */
        public boolean isUseDirectoryRecursion();

        public String getEntryName(F entry);

        public boolean isFile(F file);

        public boolean isDirectory(F file);

        public C createEntryContainer(I ecBase) throws IOException;

        public ArchiveMetaInformation getArchiveMetaInformationFromManifest(C entryContainer) throws IOException;

        public byte[] calculateMessageDigest(C entryContainer) throws IOException;

    }

    private abstract static class AbstractFinderHandler<F, C extends EntryContainer, I>
            implements FinderHandler<F, C, I> {

        private final ILFLog log;

        /**
         * Constructor.
         * 
         * @param log the logger
         */
        public AbstractFinderHandler(final ILFLog log) {
            this.log = log;
        }

        /**
         * @return the log
         */
        protected final ILFLog getLog() {
            return log;
        }

    }

    private static class FilesystemFinderHandler extends AbstractFinderHandler<File, FileSystemEntryContainer, File> {

        /**
         * Constructor.
         * 
         * @param log the logger
         */
        public FilesystemFinderHandler(ILFLog log) {
            super(log);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isUseDirectoryRecursion() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getEntryName(final File entry) {
            return entry.getName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFile(final File file) {
            return file.isFile();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDirectory(final File file) {
            return file.isDirectory();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileSystemEntryContainer createEntryContainer(final File ecBase) throws IOException {
            return new FileSystemEntryContainer(ecBase);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ArchiveMetaInformation getArchiveMetaInformationFromManifest(final FileSystemEntryContainer entryContainer)
                throws IOException {
            return JarUtil.getArchiveMetaInformationFromManifest(entryContainer.getFile(), getLog());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] calculateMessageDigest(FileSystemEntryContainer entryContainer) throws IOException {
            return CryptUtil.calculateMessageDigest(entryContainer.getFile());
        }

    }

    private static class JarFinderHandler extends AbstractFinderHandler<JarEntry, JarEntryContainer, JarInputStream> {

        /**
         * @param log the logger
         */
        public JarFinderHandler(final ILFLog log) {
            super(log);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isUseDirectoryRecursion() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getEntryName(final JarEntry entry) {
            return entry.getName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFile(final JarEntry entry) {
            return !entry.isDirectory();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDirectory(final JarEntry entry) {
            return entry.isDirectory();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JarEntryContainer createEntryContainer(final JarInputStream ecBase) throws IOException {
            final byte[] archiveBytes = IOUtils.toByteArray(ecBase);
            return new JarEntryContainer(archiveBytes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ArchiveMetaInformation getArchiveMetaInformationFromManifest(final JarEntryContainer entryContainer)
                throws IOException {
            return JarUtil.getArchiveMetaInformationFromManifest(entryContainer.getInputStream(), getLog());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] calculateMessageDigest(final JarEntryContainer entryContainer) throws IOException {
            return CryptUtil.calculateMessageDigest(entryContainer.getInputStream());
        }

    }
}
