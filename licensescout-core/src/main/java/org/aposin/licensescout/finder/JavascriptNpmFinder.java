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

import static org.aposin.licensescout.archive.ArchiveType.JAVASCRIPT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveIdentifierVersion;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.model.Author;
import org.aposin.licensescout.model.LicenseText;
import org.aposin.licensescout.util.ILSLog;
import org.aposin.licensescout.util.JsonUtil;

/**
 * Scan for licenses in an NPM cache directory.
 * 
 * <p>Certain directories can be excluded from being recognized as an archive by giving their exact name as an element in the exclude list
 * passed to the constructor (see {@link #JavascriptNpmFinder(LicenseStoreData, ILSLog, List)}).</p>
 *  
 */
public class JavascriptNpmFinder extends AbstractFinder {

    private final List<String> dirNameExcludeList = new ArrayList<>();

    /**
     * Constructor.
     * 
     * @param licenseStoreData the data object containing information on licenses
     * @param log the logger
     * @param npmExcludedDirectoryNames list of directory names to ignore in scan of NPM directory
     */
    public JavascriptNpmFinder(final LicenseStoreData licenseStoreData, final ILSLog log,
            final List<String> npmExcludedDirectoryNames) {
        super(licenseStoreData, log);
        this.dirNameExcludeList.addAll(npmExcludedDirectoryNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateLicenseFile(final String fileName) {
        final String fileNameLowerCase = fileName.toLowerCase();
        return fileNameLowerCase.contains("license") || fileNameLowerCase.contains("readme");
    }

    protected boolean isLicenseTextFile(final String fileName) {
        final String fileNameLowerCase = fileName.toLowerCase();
        return fileNameLowerCase.contains("license");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void findLicensesImpl() throws IOException {
        final File root = getScanDirectory();
        final String filePath = "";
        traverseDirectory(root, filePath);
    }

    private void traverseDirectory(final File directory, final String filePath) throws IOException {
        getLog().debug("traverseDirectory(): processing " + directory.getAbsolutePath());
        final File[] entries = directory.listFiles();
        for (final File entry : entries) {
            final String entryName = entry.getName();
            final String newFilePath = filePath + "/" + entryName;
            if (!dirNameExcludeList.contains(entryName)) {
                getLog().debug("checking directory: " + entryName);
                if (isArchive(entry)) {
                    processArchive(entry, newFilePath);
                } else if (entry.isDirectory()) {
                    traverseDirectory(entry, newFilePath);
                }
            } else {
                getLog().info("excluded directory: " + newFilePath);
            }
        }
    }

    private void processArchive(final File file, final String filePath) throws IOException {
        getLog().debug("processArchive(): processing " + file.getAbsolutePath());
        final File packageJsonFile = new File(file, "package.json");
        final String packageJsonFilePath = filePath + "/package.json";
        final ArchiveIdentifierVersion archiveIdentifier = JsonUtil.getNPMArchiveDescription(packageJsonFile);
        final Archive foundArchive = createAndAddArchive(archiveIdentifier, filePath);
        final String vendorName = JsonUtil.getNPMArchiveVendorName(packageJsonFile);
        foundArchive.setVendor(vendorName);
        getLog().debug("vendor name: " + vendorName);
        final Author author = JsonUtil.getNPMAuthor(packageJsonFile);
        if (author != null) {
            getLog().debug("author name / email: " + author.getName() + " / " + author.getEmail());
        } else {
            getLog().debug("No author information found");
        }
        foundArchive.setAuthor(author);
        final String npmArchiveLicenseName = JsonUtil.getNPMArchiveLicenseName(packageJsonFile);
        getLog().debug("license name: " + npmArchiveLicenseName);
        final List<License> licenses = LicenseUtil.mapNpmLicenseName(npmArchiveLicenseName, getLicenseStoreData());
        if (licenses != null && !licenses.isEmpty()) {
            for (final License license : licenses) {
                foundArchive.addLicense(license, packageJsonFilePath);
            }
        } else {
            getLog().warn("No licenses and no name mapping found for license name: '" + npmArchiveLicenseName + "'");
            parseArchiveDirForLicenseFile(foundArchive, file, filePath);
        }
        parseArchiveDirForLicenseText(foundArchive, file);

        processLicenses(foundArchive);
    }

    /**
     * @param foundArchive
     */
    private void processLicenses(final Archive foundArchive) {
        final boolean noLicenseInformationFound = foundArchive.getLicenses().isEmpty();

        // check for MIT license or no license and existing license text file
        if (foundArchive.getLicenseText() != null) {
            boolean containsMITLicense = false;
            final Iterator<License> licenseIterator = foundArchive.getLicenses().iterator();
            while (licenseIterator.hasNext()) {
                final License license = licenseIterator.next();
                if (license.getSpdxIdentifier().equals("MIT")) {
                    containsMITLicense = true;
                    licenseIterator.remove();
                    break;
                }
            }
            if (containsMITLicense) {
                // create a new License and replace it for the existing MIT license.
                License additionalMitLicencse = new License(
                        "MIT_" + foundArchive.getFileName() + "_" + foundArchive.getVersion(),
                        "MIT License for " + foundArchive.getFileName() + " " + foundArchive.getVersion(),
                        LegalStatus.ACCEPTED, "", "", "", foundArchive.getLicenseText().getText(), null);
                foundArchive.addLicense(additionalMitLicencse,
                        "generated from License file " + foundArchive.getLicenseText().getPath());
            } else if (noLicenseInformationFound) {
                // create a new unknown license from the text found
                License unknownLicencse = new License(
                        "unknown_" + foundArchive.getFileName() + "_" + foundArchive.getVersion(),
                        "Unknown License for " + foundArchive.getFileName() + " " + foundArchive.getVersion(),
                        LegalStatus.UNKNOWN, "", "", "", foundArchive.getLicenseText().getText(), null);
                foundArchive.addLicense(unknownLicencse,
                        "generated from License file " + foundArchive.getLicenseText().getPath());
            }
        }
    }

    private Archive createAndAddArchive(final ArchiveIdentifierVersion archiveIdentifier, final String filePath) {
        final Archive foundArchive = new Archive(JAVASCRIPT, archiveIdentifier.getName(),
                archiveIdentifier.getVersion(), filePath);
        addToArchiveFiles(foundArchive);
        return foundArchive;
    }

    private void parseArchiveDirForLicenseFile(final Archive archive, final File parent, final String filePath)
            throws IOException {
        getLog().debug("parseArchiveDir(): processing " + parent.getAbsolutePath());
        final File[] entries = parent.listFiles();
        for (final File entry : entries) {
            if (entry.isFile()) {
                final String entryName = entry.getName();
                final String newFilePath = filePath + '/' + entryName;
                if (isCandidateLicenseFile(entryName)) {
                    archive.addLicenseCandidateFile(newFilePath);
                }
                try (final FileInputStream is = new FileInputStream(entry)) {
                    final Collection<License> licenses = checkFileForLicenses(is, entryName, getLicenseStoreData());
                    addLicenses(archive, licenses, newFilePath);
                }
            }
        }
    }

    private void parseArchiveDirForLicenseText(final Archive archive, final File parent)
            throws IOException {
        getLog().debug("parseArchiveDir(): processing " + parent.getAbsolutePath());
        final File[] entries = parent.listFiles();
        for (final File entry : entries) {
            if (entry.isFile()) {
                final String entryName = entry.getName();
                if (isLicenseTextFile(entryName)) {
                    final String text = readFile(entry.toPath(), StandardCharsets.UTF_8);
                    final LicenseText licenseText = new LicenseText(text, entry.getAbsolutePath());
                    getLog().info("Setting license text from file " + entry.getAbsolutePath());
                    archive.setLicenseText(licenseText);
                }
            }
        }
    }

    private static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    /**
     * Checks if is archive.
     * 
     * @param file the file name
     * @return true, if is archive
     */
    private static boolean isArchive(final File file) {
        if (!file.isDirectory()) {
            return false;
        }
        final File packageJsonFile = new File(file, "package.json");
        return packageJsonFile.exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPomResolutionUsed() {
        return false;
    }

}
