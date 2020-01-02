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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.license.IArtifactServerUtil;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ArchiveMetaInformation;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JarUtil;

/**
 * Scan for licenses in an Eclipse plugins directory.
 * 
 * <p>The implementation does a recursive search through the file system starting with the directory set with {@link #setScanDirectory(File)} as
 * a starting point.</p>
 *  
 */
// TODO: rename to JavaDirectoryFinder
public class JavaJarFinder extends AbstractJavaFinder {

    protected enum ScanMode {
        /**
         * In a directory in the file system that is not an archive.
         */
        DIRECTORY(),
        /**
         * In a directory in the file system that is an archive (i.e. in an unpacked archive).
         */
        UNPACKED_ARCHIVE();
    }

    private final List<String> specialArchiveNames = new ArrayList<>();
    private final FinderHandler<File, FileSystemEntryContainer, File> fileSystemFinderHandler;
    /**
     * Constructor.
     * 
     * @param licenseStoreData
     * @param artifactServerUtil 
     * @param log the logger
     */
    public JavaJarFinder(final LicenseStoreData licenseStoreData, final IArtifactServerUtil artifactServerUtil, final ILFLog log) {
        super(licenseStoreData, artifactServerUtil, log);
        fileSystemFinderHandler = new FilesystemFinderHandler(log);

        initSpecialArchiveNames();
    }

    private void initSpecialArchiveNames() {
        specialArchiveNames.add("ckeditor");
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
                ArchiveMetaInformation archiveMetaInformation = null;
                if (manifestFile != null) {
                    archiveMetaInformation = JarUtil.getArchiveMetaInformationFromManifestFile(manifestFile, getLog());
                }
                final Archive foundArchive = createAndAddArchive(entryName, archiveMetaInformation, filePath);
                addLicenseFromManifest(foundArchive, archiveMetaInformation, filePath);
                parseUnpackedJarArchive(foundArchive, file, filePath);
            } else {
                getLog().debug("parseFile(): recognized as normal directory");
                final File[] children = file.listFiles();
                for (final File child : children) {
                    final String newFilePath = filePath + '/' + child.getName();
                    parseFile(child, newFilePath);
                }
            }
        } else { // is file
            if (isArchiveName(entryName)) {
                getLog().debug("parseFile(): recognized as archive file");
                handleArchiveFile(entryName, entryContainer, filePath);
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
            final String newFilePath = filePath + '/' + entryName;
            getLog().debug("parseUnpackedJarArchive(): processing " + newFilePath);
            if (isSpecialArchive(entryName)) {
                getLog().debug("parseUnpackedJarArchive(): recognized as special archive");
                handleSpecialArchive(entryName, entryContainer, newFilePath);
            } else {
                if (finderHandler.isDirectory(entry)) {
                    getLog().debug("parseUnpackedJarArchive(): recognized as directory");
                    if (finderHandler.isUseDirectoryRecursion()) {
                        parseUnpackedJarArchive(archive, entryContainer.getFile(), newFilePath);
                    }
                } else { // is file
                    if (isArchiveName(entryName)) {
                        getLog().debug("parseUnpackedJarArchive(): recognized as archive file");
                        handleArchiveFile(entryName, entryContainer, newFilePath);
                    } else {
                        getLog().debug("parseUnpackedJarArchive(): recognized as normal file");
                        handleArchiveNormalFile(archive, entryName, entryContainer, newFilePath);
                    }
                }
            }
        }
    }

    private void handleSpecialArchive(final String entryName, final FileSystemEntryContainer entryContainer,
                                      final String newFilePath)
            throws Exception {
        final String version = "0.0.0";
        final Archive foundArchive = createAndAddArchive(entryName, version, newFilePath);
        parseUnpackedJarArchive(foundArchive, entryContainer.getFile(), newFilePath);
    }

    private void handleArchiveFile(final String entryName, final FileSystemEntryContainer entryContainer,
                                   final String filePath)
            throws IOException, Exception {
        final FinderHandler<File, FileSystemEntryContainer, File> finderHandler = fileSystemFinderHandler;
        final ArchiveMetaInformation archiveMetaInformation = finderHandler
                .getArchiveMetaInformationFromManifest(entryContainer);
        final Archive foundArchive = createAndAddArchive(entryName, archiveMetaInformation, filePath);
        addMessageDigest(finderHandler, entryContainer, foundArchive);
        final String newFilePath = filePath + "!";
        addLicenseFromManifest(foundArchive, archiveMetaInformation, newFilePath);
        try (final FileInputStream archiveFileInputStream = new FileInputStream(entryContainer.getFile())) {
            parsePackedJarArchive(foundArchive, archiveFileInputStream, entryContainer.getFile(), newFilePath);
        }
    }

    private Archive createAndAddArchive(final String fileName, final ArchiveMetaInformation archiveMetaInformation,
                                        final String filePath) {
        String version = null;
        String vendor = null;
        if (archiveMetaInformation != null) {
            version = archiveMetaInformation.getVersion();
            vendor = archiveMetaInformation.getVendor();
        }
        final Archive foundArchive = createAndAddArchive(fileName, version, filePath);
        foundArchive.setVendor(vendor);
        return foundArchive;
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
}
