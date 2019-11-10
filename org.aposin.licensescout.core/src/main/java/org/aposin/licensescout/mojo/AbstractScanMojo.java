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
package org.aposin.licensescout.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.BuildInfo;
import org.aposin.licensescout.configuration.DatabaseConfiguration;
import org.aposin.licensescout.configuration.Output;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.database.DatabaseWriter;
import org.aposin.licensescout.exporter.CsvExporter;
import org.aposin.licensescout.exporter.GeneralStatistics;
import org.aposin.licensescout.exporter.HtmlExporter;
import org.aposin.licensescout.exporter.IDetectionStatusStatistics;
import org.aposin.licensescout.exporter.ILegalStatusStatistics;
import org.aposin.licensescout.exporter.IReportExporter;
import org.aposin.licensescout.exporter.OutputResult;
import org.aposin.licensescout.exporter.ReportConfiguration;
import org.aposin.licensescout.exporter.TxtExporter;
import org.aposin.licensescout.filter.CleanArchiveListFilter;
import org.aposin.licensescout.filter.IArchiveListFilter;
import org.aposin.licensescout.filter.VendorArchiveListFilter;
import org.aposin.licensescout.finder.AbstractFinder;
import org.aposin.licensescout.finder.FinderResult;
import org.aposin.licensescout.license.DetectionStatus;
import org.aposin.licensescout.license.GlobalFilters;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseCheckedList;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.model.Notices;
import org.aposin.licensescout.model.Providers;
import org.aposin.licensescout.util.CryptUtil;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.MavenLog;
import org.xml.sax.SAXException;

/**
 * Scans directory for licenses (either JAVA Jars or NPM packages).
 *
 */
public abstract class AbstractScanMojo extends AbstractMojo {

    /**
     * Directory to scan for archives.
     */
    @Parameter(property = "scanDirectory", required = false)
    protected File scanDirectory;

    /**
     * Location of the output file (will be combined with output filename).
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = false)
    private File outputDirectory;

    /**
     * Specification of output types and filenames.
     */
    @Parameter(property = "outputs", required = false)
    private List<Output> outputs;

    /**
     * Name of the file to read known licenses from.
     */
    @Parameter(property = "licensesFilename", required = false)
    private File licensesFilename;

    /**
     * Name of the file to read known providers from.
     */
    @Parameter(property = "providersFilename", required = false)
    private File providersFilename;

    /**
     * Name of the file to read license notices from.
     */
    @Parameter(property = "noticesFilename", required = false)
    private File noticesFilename;

    /**
     * Name of the file to read checked archives from.
     */
    @Parameter(defaultValue = "checkedarchives.csv", property = "checkedArchivesFilename", required = false)
    private File checkedArchivesFilename;

    /**
     * Name of the file to read license URL mappings from.
     */
    @Parameter(defaultValue = "urlmappings.csv", property = "licenseUrlMappingsFilename", required = false)
    private String licenseUrlMappingsFilename;

    /**
     * Name of the file to read license name mappings from.
     */
    @Parameter(defaultValue = "namemappings.csv", property = "licenseNameMappingsFilename", required = false)
    private String licenseNameMappingsFilename;

    /**
     * Name of the file to read global filter patterns from.
     */
    @Parameter(defaultValue = "globalFilters.csv", property = "globalFiltersFilename", required = false)
    private String globalFiltersFilename;

    /**
     * Name of the file to read of vendor names to filter out from.
     * This is alternative to filteredVendorNames. If both are given, the entries are merged.
     */
    @Parameter(property = "filteredVendorNamesFilename", required = false)
    private String filteredVendorNamesFilename;

    /**
     * If cleaning the output should be active.
     */
    @Parameter(defaultValue = "false", property = "cleanOutputActive", required = false)
    private boolean cleanOutputActive;

    /**
     * List of legal states that should be filtered out if cleanOutput is active.
     */
    @Parameter(property = "cleanOutputLegalStates", required = false)
    private LegalStatus[] cleanOutputLegalStates;

    /**
     * List of licenses that should be filtered out if cleanOutput is active, given by their SPDX identifier.
     */
    @Parameter(property = "cleanOutputLicenseSpdxIdentifiers", required = false)
    private String[] cleanOutputLicenseSpdxIdentifiers;

    /**
     * List of vendor names to filter out.
     * This is alternative to filteredVendorNamesFilename. If both are given, the entries are merged.
     */
    @Parameter(property = "filteredVendorNames", required = false)
    private List<String> filteredVendorNames;

    /**
     * Base URL for fetching Maven central artifacts from a server.
     * This can be Maven central itself (like the default value) or a mirror of maven central on a Nexus or other artifact server.
     */
    @Parameter(defaultValue = "https://repo.maven.apache.org/maven2/", property = "nexusCentralBaseUrl", required = false)
    private String nexusCentralBaseUrl;

    /**
     * Timeout for connecting to artifact server. This timeout is used when
     * connecting to an artifact server (as configured with
     * {@link #nexusCentralBaseUrl}) to retrieve parent POMs. The value is in
     * milliseconds
     */
    @Parameter(defaultValue = "1000", property = "connectTimeout", required = false)
    private int connectTimeout;

    /**
     * Whether the license XML file should be validated while reading in.
     */
    @Parameter(defaultValue = "false", property = "validateLicenseXml", required = false)
    private boolean validateLicenseXml;

    /**
     * Whether the license XML file should be validated while reading in.
     */
    @Parameter(defaultValue = "true", property = "showDocumentationUrl", required = false)
    private boolean showDocumentationUrl;

    /**
     * Whether a skeleton archive XML file of all found archives should be written.
     * 
     * <p>If enabled, the file is written to {@link #archiveXmlSkeletonFile}.</p>
     * 
     * @see #archiveXmlSkeletonFile
     */
    @Parameter(defaultValue = "false", property = "writeArchiveXmlSkeleton", required = false)
    private boolean writeArchiveXmlSkeleton;

    /**
     * File name a skeleton archive XML file of all found archives should be written to.
     * 
     * <p>Only used if {@link #writeArchiveXmlSkeleton} is true. </p>
     * 
     * @see #writeArchiveXmlSkeleton
     */
    @Parameter(defaultValue = "archiveSkeleton.xml", property = "archiveXmlSkeletonFile", required = false)
    private File archiveXmlSkeletonFile;

    /**
     * Whether a skeleton archive CSV file of all found archives should be written.
     * 
     * <p>If enabled, the file is written to {@link #archiveXmlSkeletonFile}.</p>
     * @see #archiveCsvSkeletonFile
     */
    @Parameter(defaultValue = "false", property = "writeArchiveCsvSkeleton", required = false)
    private boolean writeArchiveCsvSkeleton;

    /**
     * File name a skeleton archive CSV file of all found archives should be written to.
     * 
     * <p>Only used if {@link #writeArchiveCsvSkeleton} is true. </p>
     * 
     * @see #writeArchiveCsvSkeleton
     */
    @Parameter(defaultValue = "archiveSkeleton.csv", property = "archiveCsvSkeletonFile", required = false)
    private File archiveCsvSkeletonFile;

    /**
     * The name of the build to use when writing database entries.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     */
    @Parameter(property = "buildName", required = false)
    private String buildName;

    /**
     * The version of the build to use when writing database entries.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     */
    @Parameter(property = "buildVersion", required = false)
    private String buildVersion;

    /**
     * The URL of the build itself (point to Jenkins).
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     */
    @Parameter(property = "buildUrl", required = false)
    private String buildUrl;

    /**
     * Whether the resulting reports should be written to a database.
     * 
     * <p>If enabled, the reports are written to the database configured with {@link #resultDatabaseConfiguration}.</p>
     * 
     * @see #resultDatabaseConfiguration
     */
    @Parameter(defaultValue = "false", property = "writeResultsToDatabase", required = false)
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
    @Parameter(defaultValue = "false", property = "writeResultsToDatabaseForSnapshotBuilds", required = false)
    private boolean writeResultsToDatabaseForSnapshotBuilds;

    /**
     * Database configuration for the database the reports should be written to.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     * @see #writeResultsToDatabase
     */
    @Parameter(property = "resultDatabaseConfiguration", required = false)
    private DatabaseConfiguration resultDatabaseConfiguration;

    /**
     * Skips the execution.
     */
    @Parameter(defaultValue = "false", property = "skip", required = false)
    protected boolean skip;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException {
        final ILFLog log = new MavenLog(getLog());

        if (skip)
        {
        	log.info("Not executing because skip is configured as true.");
        	return;
        }
        
        final Providers providers = readProviders(log);
        final Notices notices = readNotices(log);
        final LicenseStoreData licenseStoreData = init(notices, log);

        final GlobalFilters globalFilters = readGlobalFilters(log);
        final LicenseCheckedList checkedArchives = readCheckedArchives(licenseStoreData, notices, providers, log);
        final List<License> cleanOutputLicenses = createCleanOutputLicenseList(log, licenseStoreData);
        final List<String> finalFilteredVendorNames = readAndCollectFilteredVendorNames(log);

        checkParameters(log);

        prepareOutput(log);

        logNpmExcludedDirectoryNames(log);
        ArchiveType archiveType = getArchiveType();
        final RunParameters runParameters = new RunParameters();
        runParameters.setNexusCentralBaseUrl(nexusCentralBaseUrl);
        runParameters.setConnectTimeout(connectTimeout);
        final AbstractFinder finder = createFinder(licenseStoreData, runParameters, log);
        getLog().info("Starting scan on " + scanDirectory.getAbsolutePath() + "...");

        try {
            final FinderResult finderResult = finder.findLicenses();
            if (finderResult == null) {
                getLog().info("No finder results.");
                return;
            }
            getLog().info("Evaluating licenses...");
            LicenseUtil.evaluateLicenses(checkedArchives, finderResult.getArchiveFiles(), licenseStoreData);
            filterGlobal(finderResult.getArchiveFiles(), globalFilters, log);

            final IArchiveListFilter vendorFilter = new VendorArchiveListFilter(finalFilteredVendorNames, log, true);
            vendorFilter.filter(finderResult.getArchiveFiles());
            final IArchiveListFilter filter = new CleanArchiveListFilter(log, cleanOutputActive,
                    Arrays.asList(cleanOutputLegalStates), cleanOutputLicenses);
            filter.filter(finderResult.getArchiveFiles());

            writeArchiveSkeletonFile(finderResult.getArchiveFiles());
            final BuildInfo buildInfo = createBuildInfo();
            writeResultsToDatabase(buildInfo, finderResult.getArchiveFiles(), log);

            final OutputResult outputResult = createOutputResult(finderResult);
            outputResult.setPomResolutionUsed(finder.isPomResolutionUsed());
            final ReportConfiguration reportConfiguration = createReportConfiguration(archiveType);

            doOutput(log, outputResult, reportConfiguration);
        } catch (Exception e) {
            throw new MojoExecutionException("Internal error occured: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Obtains the archive type handled by this MOJO.
     * 
     * @return an archive type
     */
    protected abstract ArchiveType getArchiveType();

    /**
     * @param log
     */
    private void prepareOutput(final ILFLog log) {
        createDirectoryIfNotExists(outputDirectory);
        for (final Output output : outputs) {
            final File outputFile = new File(outputDirectory, output.getFilename());
            log.info("using " + output.getType() + " output file: " + outputFile.getAbsolutePath());
            final File templateFile = output.getTemplate();
            if (templateFile != null) {
                if (templateFile.isFile() && templateFile.canRead()) {
                    log.info("using template: " + templateFile.getAbsolutePath());
                } else {
                    log.warn("not using template because it is not a file or it cannot be read: "
                            + templateFile.getAbsolutePath());
                    // unset so that furthers steps only need to check for null
                    output.setTemplate(null);
                }
            }
        }
    }

    private BuildInfo createBuildInfo() {
        if (writeResultsToDatabase) {
            // NOTE: these parameters are only used for writing information to the database
            if (StringUtils.isEmpty(buildName)) {
                getLog().warn("Parameter buildName not configured");
            }
            if (StringUtils.isEmpty(buildVersion)) {
                getLog().warn("Parameter buildVersion not configured");
            }
            if (StringUtils.isEmpty(buildUrl)) {
                getLog().warn("Parameter buildUrl not configured");
            }
        }
        String licenseReportCsvUrl = null;
        String licenseReportHtmlUrl = null;
        String licenseReportTxtUrl = null;
        for (final Output output : outputs) {
            switch (output.getType()) {
                case CSV:
                    licenseReportCsvUrl = output.getUrl();
                    if (StringUtils.isEmpty(licenseReportCsvUrl)) {
                        getLog().warn("Parameter licenseReportCsvUrl not configured");
                    }
                    break;
                case HTML:
                    licenseReportHtmlUrl = output.getUrl();
                    if (StringUtils.isEmpty(licenseReportHtmlUrl)) {
                        getLog().warn("Parameter licenseReportHtmlUrl not configured");
                    }
                    break;
                case TXT:
                    licenseReportTxtUrl = output.getUrl();
                    if (StringUtils.isEmpty(licenseReportTxtUrl)) {
                        getLog().warn("Parameter licenseReportTxtUrl not configured");
                    }
                    break;
                default:
                    getLog().warn("Unhandled output type: " + output.getType());
            }
        }
        // Note: date is used from the database
        return new BuildInfo(buildName, buildVersion, null, buildUrl, licenseReportCsvUrl, licenseReportHtmlUrl,
                licenseReportTxtUrl);
    }

    private void writeArchiveSkeletonFile(final List<Archive> archives) throws MojoExecutionException {
        writeArchiveXmlSkeletonFile(archives);
        writeArchiveCsvSkeletonFile(archives);
    }

    /**
     * @param archives
     * @throws MojoExecutionException
     */
    private void writeArchiveCsvSkeletonFile(final List<Archive> archives) throws MojoExecutionException {
        if (writeArchiveCsvSkeleton) {
            if (archiveCsvSkeletonFile != null) {
                getLog().info("writing archive CSV skeleton file to: " + archiveCsvSkeletonFile);
                try {
                    LicenseCheckedList.writeCsvSkeletonFile(archiveCsvSkeletonFile, archives);
                } catch (IOException e) {
                    throw new MojoExecutionException("cannot write archive CSV skeleton file", e);
                }
            } else {
                getLog().info("Not writing archive CSV skeleton file (not configured)");
            }
        } else {
            getLog().info("Not writing archive CSV skeleton file (not configured)");
        }
    }

    /**
     * @param archives
     * @throws MojoExecutionException
     */
    private void writeArchiveXmlSkeletonFile(final List<Archive> archives) throws MojoExecutionException {
        if (writeArchiveXmlSkeleton) {
            if (archiveXmlSkeletonFile != null) {
                getLog().info("writing archive XML skeleton file to: " + archiveXmlSkeletonFile);
                try {
                    LicenseCheckedList.writeXmlSkeletonFile(archiveXmlSkeletonFile, archives);
                } catch (IOException e) {
                    throw new MojoExecutionException("cannot write archive XML skeleton file", e);
                }
            } else {
                getLog().info("Not writing archive XML skeleton file (not configured)");
            }
        } else {
            getLog().info("Not writing archive XML skeleton file (not configured)");
        }
    }

    private void writeResultsToDatabase(final BuildInfo buildInfo, final List<Archive> archives, final ILFLog log) {
        if (writeResultsToDatabase) {
            if (isSnapshotVersion(buildVersion) && !writeResultsToDatabaseForSnapshotBuilds) {
                getLog().info("Not writing results to database because is snapshot version");
            } else {
                if (resultDatabaseConfiguration != null && resultDatabaseConfiguration.getJdbcUrl() != null) {
                    getLog().info("Writing results to database " + resultDatabaseConfiguration.getJdbcUrl()
                            + " (user: '" + resultDatabaseConfiguration.getUsername() + "')");
                    DatabaseWriter.writeToDatabase(buildInfo, archives, resultDatabaseConfiguration, log);
                } else {
                    getLog().info("Not writing results to database (no database configuration)");
                }
            }
        } else {
            getLog().info("Not writing results to database (not configured)");
        }
    }

    private boolean hasUnacceptedState(final Archive archive) {
        final DetectionStatus detectionStatus = archive.getDetectionStatus();
        final LegalStatus legalStatus = archive.getLegalStatus();
        return (detectionStatus == DetectionStatus.MULTIPLE_DETECTED || detectionStatus == DetectionStatus.NOT_DETECTED
                || legalStatus == LegalStatus.NOT_ACCEPTED || legalStatus == LegalStatus.CONFLICTING
                || legalStatus == LegalStatus.UNKNOWN);
    }

    /**
     * @param log
     * @param outputResult
     * @param reportConfiguration
     * @throws Exception
     */
    private void doOutput(final ILFLog log, final OutputResult outputResult,
                          final ReportConfiguration reportConfiguration)
            throws Exception {
        for (final Output output : outputs) {
            final File outputFile = new File(outputDirectory, output.getFilename());
            final OutputFileType outputFileType = output.getType();
            final IReportExporter exporter = getReportExporter(outputFileType);
            reportConfiguration.setOutputFile(outputFile);
            reportConfiguration.setTemplateFile(output.getTemplate());
            exporter.export(outputResult, reportConfiguration);
            log.info("written output for " + outputFileType + " to "
                    + reportConfiguration.getOutputFile().getAbsolutePath());
        }
        if (outputs == null || outputs.isEmpty()) {
            log.info("No output files written because no output formats are configured.");
        }
    }

    /**
     * @param archiveType
     * @return a report configuration object
     */
    private ReportConfiguration createReportConfiguration(ArchiveType archiveType) {
        final ReportConfiguration reportConfiguration = new ReportConfiguration();
        reportConfiguration.setShowLicenseCandidateFilesColumn(false);
        reportConfiguration.setShowMessageDigestColumn(archiveType == ArchiveType.JAVA);
        reportConfiguration.setShowPathColumn(archiveType == ArchiveType.JAVA);
        reportConfiguration.setShowDocumentationUrl(isShowDocumentationUrl());
        return reportConfiguration;
    }

    /**
     * @param finderResult
     * @return an output result object
     */
    private OutputResult createOutputResult(final FinderResult finderResult) {
        final IDetectionStatusStatistics detectionStatusStatistics = LicenseUtil
                .calculateDetectionStatusStatistics(finderResult.getArchiveFiles());
        final ILegalStatusStatistics legalStatusStatistics = LicenseUtil
                .calculateLegalStatusStatistics(finderResult.getArchiveFiles());
        final GeneralStatistics generalStatistics = LicenseUtil
                .calculateGeneralStatistics(finderResult.getArchiveFiles());
        final OutputResult outputResult = new OutputResult();
        outputResult.setFinderResult(finderResult);
        outputResult.setGeneralStatistics(generalStatistics);
        outputResult.setDetectionStatusStatistics(detectionStatusStatistics);
        outputResult.setLegalStatusStatistics(legalStatusStatistics);
        outputResult.setMessageDigestAlgorithm(CryptUtil.getMessageDigestAlgorithm());
        return outputResult;
    }

    private void checkParameters(final ILFLog log) throws MojoExecutionException {
        if (scanDirectory != null) {
            if (!scanDirectory.exists()) {
                throw new MojoExecutionException("This search path does not exist: " + scanDirectory.getAbsolutePath());
            }
            log.info("using scan directory: " + scanDirectory.getAbsolutePath());
        } else {
            throw new MojoExecutionException("scanDirectory not configured");
        }
    }

    /**
     * @param log the logger
     * @return a license store data object
     * @throws MojoExecutionException
     */
    protected LicenseStoreData init(final Notices notices, final ILFLog log) throws MojoExecutionException {

        final LicenseStoreData licenseStoreData = readLicenses(notices, log);
        if (licenseStoreData != null) {
            readLicenseUrlMappings(licenseStoreData, log);
            readLicenseNameMappings(licenseStoreData, log);
        }

        return licenseStoreData;
    }

    /**
     * Creates a finder.
     * 
     * @param licenseStoreData
     * @param runParameters
     * @param log the logger
     * @return a finder
     */
    protected abstract AbstractFinder createFinder(LicenseStoreData licenseStoreData, RunParameters runParameters,
                                                   ILFLog log);

    /**
     * @param log the logger
     * @param licenseStoreData
     * @return a list of licenses that should not appear in cleaned output
     */
    protected List<License> createCleanOutputLicenseList(final ILFLog log, final LicenseStoreData licenseStoreData) {

        log.info("Clean output: " + (cleanOutputActive ? "active" : "not active"));
        final List<License> cleanOutputLicenses = new ArrayList<>();
        if (cleanOutputActive) {
            for (final String cleanOutputLicenseSpdxIdentifier : cleanOutputLicenseSpdxIdentifiers) {
                final License license = licenseStoreData.getLicenseBySpdxIdentifier(cleanOutputLicenseSpdxIdentifier);
                if (license != null) {
                    cleanOutputLicenses.add(license);
                    log.info("Adding license to clean output filter list by SPDX identifier: "
                            + cleanOutputLicenseSpdxIdentifier);
                } else {
                    log.warn("Cannot find license by SPDX identifier: " + cleanOutputLicenseSpdxIdentifier);
                }
            }
        }
        return cleanOutputLicenses;
    }

    protected IReportExporter getReportExporter(final OutputFileType outputFileType) {
        switch (outputFileType) {
            case CSV:
                return CsvExporter.getInstance();
            case HTML:
                return HtmlExporter.getInstance();
            case TXT:
                return TxtExporter.getInstance();
            default:
                throw new UnsupportedOperationException("Unhandled OutputFileType: " + outputFileType);
        }
    }

    /**
     * @param licenseStoreData
     * @param log the logger
     * @throws MojoExecutionException
     */
    protected void readLicenseUrlMappings(final LicenseStoreData licenseStoreData, final ILFLog log)
            throws MojoExecutionException {
        if (licenseUrlMappingsFilename != null) {
            final File file = new File(licenseUrlMappingsFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading license URL mappings from " + licenseUrlMappingsFilename);
                try {
                    licenseStoreData.readUrlMappings(licenseUrlMappingsFilename, log);
                } catch (IOException e) {
                    throw new MojoExecutionException("cannot read license URL mappings", e);
                }
            } else {
                log.error("not reading license URL mappings because the file does not exist or is not readable: "
                        + licenseUrlMappingsFilename);
            }
        } else {
            log.info("not reading license URL mappings (not configured)");
        }
    }

    /**
     * @param licenseStoreData
     * @param log the logger
     * @throws MojoExecutionException
     */
    protected void readLicenseNameMappings(final LicenseStoreData licenseStoreData, final ILFLog log)
            throws MojoExecutionException {
        if (licenseNameMappingsFilename != null) {
            final File file = new File(licenseNameMappingsFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading license name mappings from " + licenseNameMappingsFilename);
                try {
                    licenseStoreData.readNameMappings(licenseNameMappingsFilename, log);
                } catch (IOException e) {
                    throw new MojoExecutionException("cannot read license name mappings", e);
                }
            } else {
                log.error("not reading license name mappings because the file does not exist or is not readable: "
                        + licenseNameMappingsFilename);
            }
        } else {
            log.info("not reading license name mappings (not configured)");
        }
    }

    /**
     * @param log the logger
     * @return a global filters object
     * @throws MojoExecutionException
     */
    protected GlobalFilters readGlobalFilters(final ILFLog log) throws MojoExecutionException {
        final GlobalFilters globalFilters = new GlobalFilters();
        if (globalFiltersFilename != null) {
            final File file = new File(globalFiltersFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading global filters from " + globalFiltersFilename);
                try {
                    globalFilters.read(globalFiltersFilename);
                } catch (IOException e) {
                    throw new MojoExecutionException("cannot read global filters", e);
                }
            } else {
                log.error("not reading global filters because the file does not exist or is not readable: "
                        + globalFiltersFilename);
            }
        } else {
            log.info("not reading global filters (not configured)");
        }
        return globalFilters;
    }

    /**
     * @param log the logger
     * @return a license store data object
     * @throws MojoExecutionException
     */
    protected LicenseStoreData readLicenses(final Notices notices, final ILFLog log) throws MojoExecutionException {
        if (licensesFilename != null) {
            if (licensesFilename.exists() && licensesFilename.canRead()) {
                log.info("reading licenses from " + licensesFilename);
                try {
                    final LicenseStoreData licenseStoreData = new LicenseStoreData();
                    licenseStoreData.readLicenses(licensesFilename, notices, validateLicenseXml, log);
                    return licenseStoreData;
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new MojoExecutionException("cannot read licenses", e);
                }
            } else {
                log.error(
                        "not reading licenses because the file does not exist or is not readable: " + licensesFilename);
            }
        } else {
            log.info("not reading licenses (not configured)");
        }
        return null;
    }

    /**
     * Reads providers.
     * 
     * @param log the logger
     * @return a providers data object
     * @throws MojoExecutionException
     */
    protected Providers readProviders(final ILFLog log) throws MojoExecutionException {
        if (providersFilename != null) {
            if (providersFilename.exists() && providersFilename.canRead()) {
                log.info("reading providers from " + providersFilename);
                try {
                    final Providers providers = new Providers();
                    providers.readProviders(providersFilename, validateLicenseXml, log);
                    return providers;
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new MojoExecutionException("cannot read providers", e);
                }
            } else {
                log.error("not reading providers because the file does not exist or is not readable: "
                        + providersFilename);
            }
        } else {
            log.info("not reading providers (not configured)");
        }
        return null;
    }

    /**
     * Reads notices.
     * 
     * @param log the logger
     * @return a notices data object
     * @throws MojoExecutionException
     */
    protected Notices readNotices(final ILFLog log) throws MojoExecutionException {
        if (noticesFilename != null) {
            if (noticesFilename.exists() && noticesFilename.canRead()) {
                log.info("reading notices from " + noticesFilename);
                try {
                    final Notices notices = new Notices();
                    notices.readNotices(noticesFilename, validateLicenseXml, log);
                    return notices;
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new MojoExecutionException("cannot read notices", e);
                }
            } else {
                log.error("not reading notices because the file does not exist or is not readable: " + noticesFilename);
            }
        } else {
            log.info("not reading notices (not configured)");
        }
        return null;
    }

    /**
     * @param licenseStoreData
     * @param log the logger
     * @return an object containing information on manaually checked archives
     * @throws MojoExecutionException
     */
    protected LicenseCheckedList readCheckedArchives(final LicenseStoreData licenseStoreData, final Notices notices,
                                                     final Providers providers, final ILFLog log)
            throws MojoExecutionException {
        final LicenseCheckedList checkedArchives = new LicenseCheckedList();
        if (checkedArchivesFilename != null) {
            if (checkedArchivesFilename.exists() && checkedArchivesFilename.canRead()) {
                log.info("reading checked archives list from " + checkedArchivesFilename);
                try {
                    checkedArchives.readCsv(checkedArchivesFilename, licenseStoreData, providers, notices, log);
                } catch (IOException e) {
                    throw new MojoExecutionException("cannot read check archives list", e);
                }
            } else {
                log.error("not reading checked archives list because the file does not exist or is not readable: "
                        + checkedArchivesFilename);
            }
        } else {
            log.info("not reading checked archives list (not configured)");
        }
        return checkedArchives;
    }

    /**
     * @param log the logger
     * @return a list of vendor names to filter out
     * @throws MojoExecutionException
     */
    protected List<String> readAndCollectFilteredVendorNames(final ILFLog log) throws MojoExecutionException {
        final List<String> resultFilteredVendorNames = new ArrayList<>();
        for (final String vendorName : filteredVendorNames) {
            resultFilteredVendorNames.add(vendorName);
            log.info("Using vendor name to filter (from maven configuration): '" + vendorName + "'");
        }
        if (filteredVendorNamesFilename != null) {
            final File file = new File(filteredVendorNamesFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading vendor names to filter out from " + filteredVendorNamesFilename);
                try {
                    final List<String> tmpResultList = readFilteredVendorNamesFromFile(filteredVendorNamesFilename,
                            log);
                    resultFilteredVendorNames.addAll(tmpResultList);
                } catch (IOException e) {
                    throw new MojoExecutionException("cannot read filtered vendor names list", e);
                }
            } else {
                log.error("not reading vendor names to filter out because the file does not exist or is not readable: "
                        + filteredVendorNamesFilename);
            }
        } else {
            log.info("not reading vendor names to filter out from file (not configured)");
        }
        return resultFilteredVendorNames;
    }

    /**
     * Reads a CSV file containing vendor names to filter out.
     * 
     * @param filename a filename of the file to read
     * @param log the logger
     * @throws IOException if an error occurred while reading from the file
     */
    protected static List<String> readFilteredVendorNamesFromFile(final String filename, final ILFLog log)
            throws IOException {
        final List<String> resultList = new ArrayList<>();
        String line = "";

        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {

                // ignore lines commented out
                if (line.startsWith("#") || StringUtils.isEmpty(line)) {
                    continue;
                }
                resultList.add(line);
                log.info("Using vendor name to filter (from configuration file): '" + line + "'");
            }
        }
        return resultList;
    }

    /**
     * @param directory
     */
    protected void createDirectoryIfNotExists(final File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
            getLog().info("created directory " + directory.getAbsoluteFile());
        }
    }

    /**
     * @param archiveFiles
     * @param globalFilters
     * @param log the logger
     */
    protected static void filterGlobal(final List<Archive> archiveFiles, final GlobalFilters globalFilters,
                                       final ILFLog log) {
        final Iterator<Archive> iter = archiveFiles.iterator();
        while (iter.hasNext()) {
            final Archive archive = iter.next();
            if (globalFilters.isFiltered(archive)) {
                log.info("Archive filtered out by global filter: " + archive.getPath());
                iter.remove();
            }
        }
    }

    /**
     * @param log the logger
     */
    protected void logNpmExcludedDirectoryNames(final ILFLog log) {
        // DO NOTHING
    }

    /**
     * @return the showDocumentationUrl
     */
    protected final boolean isShowDocumentationUrl() {
        return showDocumentationUrl;
    }

    private static boolean isSnapshotVersion(final String version) {
        return version.endsWith("-SNAPSHOT");
    }
}
