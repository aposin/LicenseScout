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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.license.IArtifactServerUtil;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.model.LSMessageDigest;
import org.aposin.licensescout.util.ArchiveMetaInformation;
import org.aposin.licensescout.util.ILFLog;

/**
 * Base class for implementations that scan a JAR files.
 *  
 */
public abstract class AbstractJavaFinder extends AbstractFinder {

    private final IArtifactServerUtil artifactServerUtil;
    private final FinderHandler<JarEntry, JarEntryContainer, JarInputStream> jarFinderHandler;

    /**
     * Constructor.
     * @param licenseStoreData the data object containing information on licenses
     * @param artifactServerUtil a helper object for accessing artifact servers
     * @param log the logger 
     */
    public AbstractJavaFinder(final LicenseStoreData licenseStoreData, final IArtifactServerUtil artifactServerUtil,
            final ILFLog log) {
        super(licenseStoreData, log);
        this.artifactServerUtil = artifactServerUtil;
        jarFinderHandler = new JarFinderHandler(log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPomResolutionUsed() {
        return artifactServerUtil.isCachedCheckAccess();
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
     * Adds license information from a POM file.
     * 
     * @param entryContainer an entry container representing the POM file
     * @param archive the archive object to add the license information to
     * @param filePath the symbolic path of the POM file (used for information only)
     * @throws IOException
     */
    protected void addLicensesFromPom(final EntryContainer entryContainer, final Archive archive, final String filePath)
            throws IOException {
        try (final InputStream inputStream = entryContainer.getInputStream()) {
            artifactServerUtil.addLicensesFromPom(inputStream, archive, filePath, getLicenseStoreData());
        }
    }

    /**
     * Processes a packed archive (an uncompressed JAR file).
     * 
     * @param archive archive object to add the license information to
     * @param fileInputStream input stream to read the JAR file
     * @param parent the parent (in a file system hierarchy) that contains the JAR file
     * @param filePath the symbolic path of the JAR file (for information only)
     * @throws Exception
     */
    protected void parsePackedJarArchive(final Archive archive, final InputStream fileInputStream, final File parent,
                                         final String filePath)
            throws Exception {
        final FinderHandler<JarEntry, JarEntryContainer, JarInputStream> finderHandler = jarFinderHandler;
        getLog().debug("parsePackedJarArchive(): processing " + parent.getAbsolutePath());
        try (final JarInputStream jarInputStream = new JarInputStream(fileInputStream)) {
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                final String entryName = finderHandler.getEntryName(entry);
                final String newFilePath = filePath + '/' + entryName;
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
                    final String simpleName = getSimpleName(entryName);
                    final Archive foundArchive = createAndAddArchive(simpleName, version, newFilePath);
                    foundArchive.setVendor(archiveMetaInformation.getVendor());
                    if (finderHandler.isFile(entry)) {
                        addMessageDigest(finderHandler, entryContainer, foundArchive);
                    }
                    addLicenseFromManifest(foundArchive, archiveMetaInformation, newFilePath2);
                    try (final InputStream inputStream = entryContainer.getInputStream()) {
                        parsePackedJarArchive(foundArchive, inputStream, file, newFilePath2);
                    }
                } else {
                    if (finderHandler.isFile(entry)) {
                        final JarEntryContainer entryContainer = finderHandler.createEntryContainer(jarInputStream);
                        handleArchiveNormalFile(archive, entryName, entryContainer, newFilePath);
                    }
                }
            }
        }
    }

    /**
     * Processes a normal file (not an archive and not a normal directory).
     * 
     * <p>The files this method is called for a are checked if they are candidates for files containing license information.
     * If so, they are scanned further.</p>
     * 
     * @param archive an archive object
     * @param entryName the entry name of the file to check
     * @param entryContainer the corresponding entry container
     * @param filePath the symbolic path of the file (for information only)
     * @throws IOException
     */
    protected void handleArchiveNormalFile(final Archive archive, final String entryName,
                                           final EntryContainer entryContainer, final String filePath)
            throws IOException {
        if (isCandidateLicenseFile(entryName)) {
            archive.addLicenseCandidateFile(filePath);
        }
        try (final InputStream inputStream = entryContainer.getInputStream()) {
            final Collection<License> licenses = checkFileForLicenses(inputStream, entryName, getLicenseStoreData());
            addLicenses(archive, licenses, filePath);
        }
        if (isPomFile(entryName)) {
            addLicensesFromPom(entryContainer, archive, filePath);
        }
    }

    /**
     * Creates an archive object and adds it to the list of found archives.
     * 
     * @param fileName a filename
     * @param versionParam a version number string (may be null or empty)
     * @param filePath the symbolic path of the archive file (for information only, may be null)
     * @return the created archive object
     * 
     * @see #addToArchiveFiles(Archive)
     */
    protected Archive createAndAddArchive(final String fileName, final String versionParam, final String filePath) {
        final String version = getVersionNotNull(versionParam);
        final Archive foundArchive = new Archive(JAVA, fileName, version, filePath);
        addToArchiveFiles(foundArchive);
        return foundArchive;
    }

    /**
     * Obtains the last part of a path name.
     * @param entryName a path name
     * @return the last part of a path name if the passed name contains '/', otherwise the passed name
     */
    protected String getSimpleName(final String entryName) {
        final int pos = entryName.lastIndexOf('/');
        if (pos >= 0) {
            return entryName.substring(pos + 1);
        }
        return entryName;
    }

    /**
     * Adds a message digest computed over an entry container to an archive object.
     * 
     * @param <C> the type of entry container
     * @param finderHandler a finder handler
     * @param entryContainer the entry container to use for computing the message digest
     * @param archive an archive object
     * @throws IOException
     */
    protected <C extends EntryContainer> void addMessageDigest(final FinderHandler<?, C, ?> finderHandler,
                                                               final C entryContainer, final Archive archive)
            throws IOException {
        final LSMessageDigest md = finderHandler.calculateMessageDigest(entryContainer);
        archive.setMessageDigest(md);
    }

    /**
     * Converts a version of null to an empty string.
     * 
     * @param version a version string or null
     * @return a non empty string
     */
    protected String getVersionNotNull(String version) {
        return StringUtils.defaultString(version);
    }

    /**
     * Adds license information from a MANIFEST.MF.
     * 
     * @param archive an archive object
     * @param archiveMetaInformation an object containing meta data from the MANIFEST.MF file
     * @param filePath the symbolic path of the manifest file (used for information only)
     */
    protected void addLicenseFromManifest(final Archive archive, final ArchiveMetaInformation archiveMetaInformation,
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
     * Checks if a filename represents a POM file.
     * @param name a filename
     * @return true if the name represents a POM file (detected from the name only), false otherwise
     */
    protected boolean isPomFile(final String name) {
        return name.endsWith("pom.xml");
    }

    /**
     * Checks if is archive by checking if the name contains "jar".
     * 
     * <p>Note: this method only relies on the filename. It does not check containing files that indicate an archive.</p>
     * 
     * @param fileName the file name
     * @return true, if is archive
     */
    protected static boolean isArchiveName(final String fileName) {
        return fileName.endsWith(".jar");
    }
}
