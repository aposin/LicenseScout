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
import org.aposin.licensescout.configuration.ExecutionDatabaseConfiguration;
import org.aposin.licensescout.configuration.ExecutionOutput;
import org.aposin.licensescout.license.IArtifactServerUtil;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.util.ILSLog;

/**
 * Parameter object containing all information for a LicenseScout execution.
 *
 * @see Executor
 */
public class ExecutionParameters {

    private ArchiveType archiveType;

    /**
     * Location to scan for archives.
     */
    private ScanLocation scanLocation;

    /**
     * Location of the output file (will be combined with output filename).
     */
    private File outputDirectory;

    /**
     * Specification of output types and filenames.
     */
    private List<ExecutionOutput> outputs;

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
     * If the Plug-in in case of an error should terminate with a condition that lets the build fail.
     * 
     * @see errorLegalStates
     */
    private boolean failOnError;

    /**
     * List of legal states that should be considered an error.
     * 
     * <p>The listed states lead to a build error if {@link #failOnError} is active.</p>
     * 
     * @see LegalStatus
     */
    private LegalStatus[] errorLegalStates;

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
    private ExecutionDatabaseConfiguration resultDatabaseConfiguration;

    /**
     * NPM only.
     */
    private List<String> npmExcludedDirectoryNames;

    /**
     * NOTE: is not simply called 'log' because in this case {@code BeanUtils#copyProperties(Object, Object)} would use the value of AbstractMojo.getLog()
     * to initialize this value, however, they have incompatible types and this leads to a runtime error.
     */
    private ILSLog lsLog;

    private List<IReportExporterFactory> exporterFactories;

    private IArtifactServerUtil artifactServerUtil;

    /**
     * Algorithm to use for message digests.
     * 
     * <p>The configured name is passed to the Java Cryptography extension (JCE) to obtain an algorithm implementation.
     * Therefore, the name should match an algorithm name supported by one of the configured cryptography providers.</p>
     * 
     * @since 1.4.0
     */
    private String messageDigestAlgorithm;

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
    public final ScanLocation getScanLocation() {
        return scanLocation;
    }

    /**
     * @param scanLocation the scan location to set
     */
    public final void setScanLocation(ScanLocation scanLocation) {
        this.scanLocation = scanLocation;
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
    public final List<ExecutionOutput> getOutputs() {
        return outputs;
    }

    /**
     * @param outputs the outputs to set
     */
    public final void setOutputs(List<ExecutionOutput> outputs) {
        this.outputs = outputs;
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
     * @return the failOnError
     */
    public final boolean isFailOnError() {
        return failOnError;
    }

    /**
     * @param failOnError the failOnError to set
     */
    public final void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * @return the errorLegalStates
     */
    public final LegalStatus[] getErrorLegalStates() {
        return errorLegalStates;
    }

    /**
     * @param errorLegalStates the errorLegalStates to set
     */
    public final void setErrorLegalStates(LegalStatus[] errorLegalStates) {
        this.errorLegalStates = errorLegalStates;
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
    public final ExecutionDatabaseConfiguration getResultDatabaseConfiguration() {
        return resultDatabaseConfiguration;
    }

    /**
     * @param resultDatabaseConfiguration the resultDatabaseConfiguration to set
     */
    public final void setResultDatabaseConfiguration(ExecutionDatabaseConfiguration resultDatabaseConfiguration) {
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
    public final ILSLog getLsLog() {
        return lsLog;
    }

    /**
     * @param lsLog the log to set
     */
    public final void setLsLog(ILSLog lsLog) {
        this.lsLog = lsLog;
    }

    /**
     * @return the exporterFactories
     */
    public final List<IReportExporterFactory> getExporterFactories() {
        return exporterFactories;
    }

    /**
     * @param exporterFactories the exporterFactories to set
     */
    public final void setExporterFactories(List<IReportExporterFactory> exporterFactories) {
        this.exporterFactories = exporterFactories;
    }

    /**
     * @return the artifactServerUtil
     */
    public final IArtifactServerUtil getArtifactServerUtil() {
        return artifactServerUtil;
    }

    /**
     * @param artifactServerUtil the artifactServerUtil to set
     */
    public final void setArtifactServerUtil(IArtifactServerUtil artifactServerUtil) {
        this.artifactServerUtil = artifactServerUtil;
    }

    /**
     * @return the messageDigestAlgorithm
     */
    public final String getMessageDigestAlgorithm() {
        return messageDigestAlgorithm;
    }

    /**
     * @param messageDigestAlgorithm the messageDigestAlgorithm to set
     */
    public final void setMessageDigestAlgorithm(String messageDigestAlgorithm) {
        this.messageDigestAlgorithm = messageDigestAlgorithm;
    }

}
