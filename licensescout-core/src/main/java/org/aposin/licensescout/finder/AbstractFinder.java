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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.util.ILFLog;

/**
 * Base class for implementations that scan a directory for artifacts that need a license.
 *  
 * @see JavaJarFinder
 * @see JavascriptNpmFinder
 */
public abstract class AbstractFinder {

    private final LicenseStoreData licenseStoreData;

    private final ILFLog log;

    private File scanDirectory;

    /**
     * List of archives found during the scanning.
     */
    private final List<Archive> archiveFiles = new ArrayList<>();

    /**
     * If set to true, the list of found archives including their licenses is dumped to the log with level DEBUG after the scanning run.
     * 
     * @see #printArchiveList(List)
     */
    boolean debug = false;

    /**
     * Constructor.
     * @param licenseStoreData the data object containing information on licenses
     * @param log the logger 
     */
    public AbstractFinder(final LicenseStoreData licenseStoreData, final ILFLog log) {
        this.licenseStoreData = licenseStoreData;
        this.log = log;
    }

    /**
     * @return the licenseStoreData
     */
    protected final LicenseStoreData getLicenseStoreData() {
        return licenseStoreData;
    }

    /**
     * Obtains the logger.
     * 
     * @return the logger
     * 
     * @see #AbstractFinder(LicenseStoreData, ILFLog)
     */
    protected final ILFLog getLog() {
        return log;
    }

    /**
     * Sets the directory to scan.
     * 
     * @param scanDirectory the directory to scan
     */
    public final void setScanDirectory(final File scanDirectory) {
        this.scanDirectory = scanDirectory;
    }

    /**
     * Obtains the directory to scan.
     * 
     * @return the scanDirectory
     */
    protected final File getScanDirectory() {
        return scanDirectory;
    }

    /**
     * Adds an archive to the list of found archives.
     * @param foundArchive an archive
     */
    protected void addToArchiveFiles(final Archive foundArchive) {
        getLog().debug("adding archive for: " + foundArchive.getPath());
        archiveFiles.add(foundArchive);
    }

    /**
     * @return the results
     * @throws Exception 
     * @throws IOException
     */
    public FinderResult findLicenses() throws Exception {
        findLicensesImpl();
        if (debug) {
            printArchiveList(archiveFiles);
        }
        getLog().info("Finished scanning for licenses in " + getScanDirectory().getAbsolutePath());
        return new FinderResult(getScanDirectory(), archiveFiles);
    }

    /**
     * Checks the filename if the file is a potential license file.
     * @param fileName
     * @return true if the file is a potential license file
     */
    protected abstract boolean isCandidateLicenseFile(String fileName);

    /**
     * @throws Exception 
     */
    protected abstract void findLicensesImpl() throws Exception;

    /**
     * @param is
     * @param fileName
     * @param licenseStoreData the data object containing information on licenses
     * @return a license or <code>null</code>
     * @throws IOException
     */
    protected Collection<License> checkFileForLicenses(final InputStream is, final String fileName,
                                                       final LicenseStoreData licenseStoreData)
            throws IOException {

        if (isCandidateLicenseFile(fileName)) {
            getLog().debug("Checking file for licenses: " + fileName);
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            final Collection<License> detectLicenses = LicenseUtil.detectLicenses(br, licenseStoreData);
            getLog().debug("Result licenses: " + detectLicenses.toString());
            return detectLicenses;
        }
        return null;
    }

    /**
     * @param archiveFiles
     */
    public void printArchiveList(final List<Archive> archiveFiles) {
        for (final Archive archive : archiveFiles) {
            getLog().debug(archive.getFileName() + "     " + archive.getPath());
            for (final License license : archive.getLicenses()) {
                getLog().debug(license.getName());
            }
        }
    }

    protected void addLicenses(final Archive archive, final Collection<License> licenses, final String filePath) {
        if (licenses != null) {
            for (final License license : licenses) {
                if (license != null) {
                    archive.addLicense(license, filePath);
                }
            }
        }
    }

    /**
     * Obtains if POM resolution is active.
     * 
     * @return true if POM resolution is active, false otherwise
     */
    public abstract boolean isPomResolutionUsed();
}
