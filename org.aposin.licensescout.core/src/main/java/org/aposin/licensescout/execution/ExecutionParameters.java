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
package org.aposin.licensescout.execution;

import java.io.File;
import java.util.List;

import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.DatabaseConfiguration;
import org.aposin.licensescout.configuration.Output;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.util.ILFLog;

/**
 * Parameter object containing all information for a LicenseScout execution.
 *
 * @see Executor
 */
public class ExecutionParameters {

    private ArchiveType archiveType;

    /**
     * Directory to scan for archives.
     */
    private File scanDirectory;

    /**
     * Location of the output file (will be combined with output filename).
     */
    private File outputDirectory;

    /**
     * Specification of output types and filenames.
     */
    private List<Output> outputs;

    /**
     * Name of the file to read known licenses from.
     */
    private File licensesFilename;

    /**
     * Name of the file to read known providers from.
     */
    private File providersFilename;

    /**
     * Name of the file to read license notices from.
     */
    private File noticesFilename;

    /**
     * Name of the file to read checked archives from.
     */
    private File checkedArchivesFilename;

    /**
     * Name of the file to read license URL mappings from.
     */
    private String licenseUrlMappingsFilename;

    /**
     * Name of the file to read license name mappings from.
     */
    private String licenseNameMappingsFilename;

    /**
     * Name of the file to read global filter patterns from.
     */
    private String globalFiltersFilename;

    /**
     * Name of the file to read of vendor names to filter out from.
     * This is alternative to filteredVendorNames. If both are given, the entries are merged.
     */
    private String filteredVendorNamesFilename;

    /**
     * If cleaning the output should be active.
     */
    private boolean cleanOutputActive;

    /**
     * List of legal states that should be filtered out if cleanOutput is active.
     */
    private LegalStatus[] cleanOutputLegalStates;

    /**
     * List of licenses that should be filtered out if cleanOutput is active, given by their SPDX identifier.
     */
    private String[] cleanOutputLicenseSpdxIdentifiers;

    /**
     * List of vendor names to filter out.
     * This is alternative to filteredVendorNamesFilename. If both are given, the entries are merged.
     */
    private List<String> filteredVendorNames;

    /**
     * Base URL for fetching Maven central artifacts from a server.
     * This can be Maven central itself (like the default value) or a mirror of maven central on a Nexus or other artifact server.
     */
    private String nexusCentralBaseUrl;

    /**
     * Timeout for connecting to artifact server. This timeout is used when
     * connecting to an artifact server (as configured with
     * {@link #nexusCentralBaseUrl}) to retrieve parent POMs. The value is in
     * milliseconds
     */
    private int connectTimeout;

    /**
     * Whether the license XML file should be validated while reading in.
     */
    private boolean validateLicenseXml;

    /**
     * Whether the license XML file should be validated while reading in.
     */
    private boolean showDocumentationUrl;

    /**
     * Whether a skeleton archive XML file of all found archives should be written.
     * 
     * <p>If enabled, the file is written to {@link #archiveXmlSkeletonFile}.</p>
     * 
     * @see #archiveXmlSkeletonFile
     */
    private boolean writeArchiveXmlSkeleton;

    /**
     * File name a skeleton archive XML file of all found archives should be written to.
     * 
     * <p>Only used if {@link #writeArchiveXmlSkeleton} is true. </p>
     * 
     * @see #writeArchiveXmlSkeleton
     */
    private File archiveXmlSkeletonFile;

    /**
     * Whether a skeleton archive CSV file of all found archives should be written.
     * 
     * <p>If enabled, the file is written to {@link #archiveXmlSkeletonFile}.</p>
     * @see #archiveCsvSkeletonFile
     */
    private boolean writeArchiveCsvSkeleton;

    /**
     * File name a skeleton archive CSV file of all found archives should be written to.
     * 
     * <p>Only used if {@link #writeArchiveCsvSkeleton} is true. </p>
     * 
     * @see #writeArchiveCsvSkeleton
     */
    private File archiveCsvSkeletonFile;

    /**
     * The name of the build to use when writing database entries.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     */
    private String buildName;

    /**
     * The version of the build to use when writing database entries.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     */
    private String buildVersion;

    /**
     * The URL of the build itself (point to Jenkins).
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     */
    private String buildUrl;

    /**
     * Whether the resulting reports should be written to a database.
     * 
     * <p>If enabled, the reports are written to the database configured with {@link #resultDatabaseConfiguration}.</p>
     * 
     * @see #resultDatabaseConfiguration
     */
    private boolean writeResultsToDatabase;

    /**
     * Whether the resulting reports should be written to a database in case the build is a snapshot
     * (identified by the value of {@link #buildVersion} ending with "-SNAPSHOT").
     * 
     * <p>This setting only has an effect if {@link #writeResultsToDatabase} is enabled.</p>
     * 
     * @see #writeResultsToDatabase
     * @see #resultDatabaseConfiguration
     */
    private boolean writeResultsToDatabaseForSnapshotBuilds;

    /**
     * Database configuration for the database the reports should be written to.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     * @see #writeResultsToDatabase
     */
    private DatabaseConfiguration resultDatabaseConfiguration;

    /**
     * NPM only.
     */
    private List<String> npmExcludedDirectoryNames;

    private ILFLog log;

    /**
     * @return the archiveType
     */
    public final ArchiveType getArchiveType() {
        return archiveType;
    }

    /**
     * @param archiveType the archiveType to set
     */
    public final void setArchiveType(ArchiveType archiveType) {
        this.archiveType = archiveType;
    }

    /**
     * @return the scanDirectory
     */
    public final File getScanDirectory() {
        return scanDirectory;
    }

    /**
     * @param scanDirectory the scanDirectory to set
     */
    public final void setScanDirectory(File scanDirectory) {
        this.scanDirectory = scanDirectory;
    }

    /**
     * @return the outputDirectory
     */
    public final File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * @param outputDirectory the outputDirectory to set
     */
    public final void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return the outputs
     */
    public final List<Output> getOutputs() {
        return outputs;
    }

    /**
     * @param outputs the outputs to set
     */
    public final void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    /**
     * @return the licensesFilename
     */
    public final File getLicensesFilename() {
        return licensesFilename;
    }

    /**
     * @param licensesFilename the licensesFilename to set
     */
    public final void setLicensesFilename(File licensesFilename) {
        this.licensesFilename = licensesFilename;
    }

    /**
     * @return the providersFilename
     */
    public final File getProvidersFilename() {
        return providersFilename;
    }

    /**
     * @param providersFilename the providersFilename to set
     */
    public final void setProvidersFilename(File providersFilename) {
        this.providersFilename = providersFilename;
    }

    /**
     * @return the noticesFilename
     */
    public final File getNoticesFilename() {
        return noticesFilename;
    }

    /**
     * @param noticesFilename the noticesFilename to set
     */
    public final void setNoticesFilename(File noticesFilename) {
        this.noticesFilename = noticesFilename;
    }

    /**
     * @return the checkedArchivesFilename
     */
    public final File getCheckedArchivesFilename() {
        return checkedArchivesFilename;
    }

    /**
     * @param checkedArchivesFilename the checkedArchivesFilename to set
     */
    public final void setCheckedArchivesFilename(File checkedArchivesFilename) {
        this.checkedArchivesFilename = checkedArchivesFilename;
    }

    /**
     * @return the licenseUrlMappingsFilename
     */
    public final String getLicenseUrlMappingsFilename() {
        return licenseUrlMappingsFilename;
    }

    /**
     * @param licenseUrlMappingsFilename the licenseUrlMappingsFilename to set
     */
    public final void setLicenseUrlMappingsFilename(String licenseUrlMappingsFilename) {
        this.licenseUrlMappingsFilename = licenseUrlMappingsFilename;
    }

    /**
     * @return the licenseNameMappingsFilename
     */
    public final String getLicenseNameMappingsFilename() {
        return licenseNameMappingsFilename;
    }

    /**
     * @param licenseNameMappingsFilename the licenseNameMappingsFilename to set
     */
    public final void setLicenseNameMappingsFilename(String licenseNameMappingsFilename) {
        this.licenseNameMappingsFilename = licenseNameMappingsFilename;
    }

    /**
     * @return the globalFiltersFilename
     */
    public final String getGlobalFiltersFilename() {
        return globalFiltersFilename;
    }

    /**
     * @param globalFiltersFilename the globalFiltersFilename to set
     */
    public final void setGlobalFiltersFilename(String globalFiltersFilename) {
        this.globalFiltersFilename = globalFiltersFilename;
    }

    /**
     * @return the filteredVendorNamesFilename
     */
    public final String getFilteredVendorNamesFilename() {
        return filteredVendorNamesFilename;
    }

    /**
     * @param filteredVendorNamesFilename the filteredVendorNamesFilename to set
     */
    public final void setFilteredVendorNamesFilename(String filteredVendorNamesFilename) {
        this.filteredVendorNamesFilename = filteredVendorNamesFilename;
    }

    /**
     * @return the cleanOutputActive
     */
    public final boolean isCleanOutputActive() {
        return cleanOutputActive;
    }

    /**
     * @param cleanOutputActive the cleanOutputActive to set
     */
    public final void setCleanOutputActive(boolean cleanOutputActive) {
        this.cleanOutputActive = cleanOutputActive;
    }

    /**
     * @return the cleanOutputLegalStates
     */
    public final LegalStatus[] getCleanOutputLegalStates() {
        return cleanOutputLegalStates;
    }

    /**
     * @param cleanOutputLegalStates the cleanOutputLegalStates to set
     */
    public final void setCleanOutputLegalStates(LegalStatus[] cleanOutputLegalStates) {
        this.cleanOutputLegalStates = cleanOutputLegalStates;
    }

    /**
     * @return the cleanOutputLicenseSpdxIdentifiers
     */
    public final String[] getCleanOutputLicenseSpdxIdentifiers() {
        return cleanOutputLicenseSpdxIdentifiers;
    }

    /**
     * @param cleanOutputLicenseSpdxIdentifiers the cleanOutputLicenseSpdxIdentifiers to set
     */
    public final void setCleanOutputLicenseSpdxIdentifiers(String[] cleanOutputLicenseSpdxIdentifiers) {
        this.cleanOutputLicenseSpdxIdentifiers = cleanOutputLicenseSpdxIdentifiers;
    }

    /**
     * @return the filteredVendorNames
     */
    public final List<String> getFilteredVendorNames() {
        return filteredVendorNames;
    }

    /**
     * @param filteredVendorNames the filteredVendorNames to set
     */
    public final void setFilteredVendorNames(List<String> filteredVendorNames) {
        this.filteredVendorNames = filteredVendorNames;
    }

    /**
     * @return the nexusCentralBaseUrl
     */
    public final String getNexusCentralBaseUrl() {
        return nexusCentralBaseUrl;
    }

    /**
     * @param nexusCentralBaseUrl the nexusCentralBaseUrl to set
     */
    public final void setNexusCentralBaseUrl(String nexusCentralBaseUrl) {
        this.nexusCentralBaseUrl = nexusCentralBaseUrl;
    }

    /**
     * @return the connectTimeout
     */
    public final int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @param connectTimeout the connectTimeout to set
     */
    public final void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @return the validateLicenseXml
     */
    public final boolean isValidateLicenseXml() {
        return validateLicenseXml;
    }

    /**
     * @param validateLicenseXml the validateLicenseXml to set
     */
    public final void setValidateLicenseXml(boolean validateLicenseXml) {
        this.validateLicenseXml = validateLicenseXml;
    }

    /**
     * @return the showDocumentationUrl
     */
    public final boolean isShowDocumentationUrl() {
        return showDocumentationUrl;
    }

    /**
     * @param showDocumentationUrl the showDocumentationUrl to set
     */
    public final void setShowDocumentationUrl(boolean showDocumentationUrl) {
        this.showDocumentationUrl = showDocumentationUrl;
    }

    /**
     * @return the writeArchiveXmlSkeleton
     */
    public final boolean isWriteArchiveXmlSkeleton() {
        return writeArchiveXmlSkeleton;
    }

    /**
     * @param writeArchiveXmlSkeleton the writeArchiveXmlSkeleton to set
     */
    public final void setWriteArchiveXmlSkeleton(boolean writeArchiveXmlSkeleton) {
        this.writeArchiveXmlSkeleton = writeArchiveXmlSkeleton;
    }

    /**
     * @return the archiveXmlSkeletonFile
     */
    public final File getArchiveXmlSkeletonFile() {
        return archiveXmlSkeletonFile;
    }

    /**
     * @param archiveXmlSkeletonFile the archiveXmlSkeletonFile to set
     */
    public final void setArchiveXmlSkeletonFile(File archiveXmlSkeletonFile) {
        this.archiveXmlSkeletonFile = archiveXmlSkeletonFile;
    }

    /**
     * @return the writeArchiveCsvSkeleton
     */
    public final boolean isWriteArchiveCsvSkeleton() {
        return writeArchiveCsvSkeleton;
    }

    /**
     * @param writeArchiveCsvSkeleton the writeArchiveCsvSkeleton to set
     */
    public final void setWriteArchiveCsvSkeleton(boolean writeArchiveCsvSkeleton) {
        this.writeArchiveCsvSkeleton = writeArchiveCsvSkeleton;
    }

    /**
     * @return the archiveCsvSkeletonFile
     */
    public final File getArchiveCsvSkeletonFile() {
        return archiveCsvSkeletonFile;
    }

    /**
     * @param archiveCsvSkeletonFile the archiveCsvSkeletonFile to set
     */
    public final void setArchiveCsvSkeletonFile(File archiveCsvSkeletonFile) {
        this.archiveCsvSkeletonFile = archiveCsvSkeletonFile;
    }

    /**
     * @return the buildName
     */
    public final String getBuildName() {
        return buildName;
    }

    /**
     * @param buildName the buildName to set
     */
    public final void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    /**
     * @return the buildVersion
     */
    public final String getBuildVersion() {
        return buildVersion;
    }

    /**
     * @param buildVersion the buildVersion to set
     */
    public final void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    /**
     * @return the buildUrl
     */
    public final String getBuildUrl() {
        return buildUrl;
    }

    /**
     * @param buildUrl the buildUrl to set
     */
    public final void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    /**
     * @return the writeResultsToDatabase
     */
    public final boolean isWriteResultsToDatabase() {
        return writeResultsToDatabase;
    }

    /**
     * @param writeResultsToDatabase the writeResultsToDatabase to set
     */
    public final void setWriteResultsToDatabase(boolean writeResultsToDatabase) {
        this.writeResultsToDatabase = writeResultsToDatabase;
    }

    /**
     * @return the writeResultsToDatabaseForSnapshotBuilds
     */
    public final boolean isWriteResultsToDatabaseForSnapshotBuilds() {
        return writeResultsToDatabaseForSnapshotBuilds;
    }

    /**
     * @param writeResultsToDatabaseForSnapshotBuilds the writeResultsToDatabaseForSnapshotBuilds to set
     */
    public final void setWriteResultsToDatabaseForSnapshotBuilds(boolean writeResultsToDatabaseForSnapshotBuilds) {
        this.writeResultsToDatabaseForSnapshotBuilds = writeResultsToDatabaseForSnapshotBuilds;
    }

    /**
     * @return the resultDatabaseConfiguration
     */
    public final DatabaseConfiguration getResultDatabaseConfiguration() {
        return resultDatabaseConfiguration;
    }

    /**
     * @param resultDatabaseConfiguration the resultDatabaseConfiguration to set
     */
    public final void setResultDatabaseConfiguration(DatabaseConfiguration resultDatabaseConfiguration) {
        this.resultDatabaseConfiguration = resultDatabaseConfiguration;
    }

    /**
     * @return the npmExcludedDirectoryNames
     */
    public final List<String> getNpmExcludedDirectoryNames() {
        return npmExcludedDirectoryNames;
    }

    /**
     * @param npmExcludedDirectoryNames the npmExcludedDirectoryNames to set
     */
    public final void setNpmExcludedDirectoryNames(List<String> npmExcludedDirectoryNames) {
        this.npmExcludedDirectoryNames = npmExcludedDirectoryNames;
    }

    /**
     * @return the log
     */
    public final ILFLog getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public final void setLog(ILFLog log) {
        this.log = log;
    }

}
