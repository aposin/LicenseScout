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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.BuildInfo;
import org.aposin.licensescout.configuration.ConfigFileHandler;
import org.aposin.licensescout.configuration.ExecutionDatabaseConfiguration;
import org.aposin.licensescout.configuration.ExecutionOutput;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.configuration.FinderParameters;
import org.aposin.licensescout.database.DatabaseWriter;
import org.aposin.licensescout.exporter.GeneralStatistics;
import org.aposin.licensescout.exporter.IDetectionStatusStatistics;
import org.aposin.licensescout.exporter.ILegalStatusStatistics;
import org.aposin.licensescout.exporter.IReportExporter;
import org.aposin.licensescout.exporter.OutputResult;
import org.aposin.licensescout.exporter.ReportConfiguration;
import org.aposin.licensescout.filter.CleanArchiveListFilter;
import org.aposin.licensescout.filter.IArchiveListFilter;
import org.aposin.licensescout.filter.VendorArchiveListFilter;
import org.aposin.licensescout.finder.AbstractFinder;
import org.aposin.licensescout.finder.FinderFactory;
import org.aposin.licensescout.finder.FinderResult;
import org.aposin.licensescout.license.GlobalFilters;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseCheckedList;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.model.Alarm;
import org.aposin.licensescout.model.AlarmCause;
import org.aposin.licensescout.model.Notices;
import org.aposin.licensescout.model.Providers;
import org.aposin.licensescout.util.CryptUtil;
import org.aposin.licensescout.util.ILSLog;
import org.aposin.licensescout.util.MiscUtil;
import org.aposin.licensescout.util.OutputFileHelper;
import org.xml.sax.SAXException;

/**
 * Engine that contains the main control flow of the LicenseScout.
 *
 */
public class Executor {

    private final ExecutionParameters executionParameters;
    private final ConfigFileHandler configFileHandler;

    /**
     * Constructor.
     * @param executionParameters 
     * @param configFileHandler 
     */
    public Executor(final ExecutionParameters executionParameters, final ConfigFileHandler configFileHandler) {
        this.executionParameters = executionParameters;
        this.configFileHandler = configFileHandler;
    }

    /**
     * @return the executionParameters
     */
    protected final ExecutionParameters getExecutionParameters() {
        return executionParameters;
    }

    /**
     * @return the configFileHandler
     */
    protected final ConfigFileHandler getConfigFileHandler() {
        return configFileHandler;
    }

    protected final ILSLog getLog() {
        return getExecutionParameters().getLsLog();
    }

    /**
     * Main execution of the LicenseScout.
     * 
     * <p>This executes:</p>
     * <ol>
     * <li>Reads configuration files:
     * <ol type="a">
     * <li>Reads providers file</li>
     * <li>Reads notices file</li>
     * <li>Reads licenses file</li>
     * <li>Reads URL mappings file</li>
     * <li>Reads name mappings file</li>
     * <li>Reads global filters file</li>
     * <li>Reads checked archives file</li>
     * <li>Reads filtered vendor names file</li>
     * </ol>
     * </li>
     * 
     * <li>Checks parameters</li>
     * <li></li>
     * <li></li>
     * <li></li>
     * <li></li>
     * <li></li>
     * </ol>
     * 
     * @throws LicenseScoutExecutionException if an unrecoverable condition occurs during the execution or
     * if another exception is caught
     * @throws LicenseScoutFailOnErrorException if a condition is raised thath should lead to a fail-on-error
     */
    public void execute() throws LicenseScoutExecutionException, LicenseScoutFailOnErrorException {

        CryptUtil.setMessageDigestAlgorithm(getExecutionParameters().getMessageDigestAlgorithm());

        final Providers providers = readProviders(getConfigFileHandler(), getLog());
        final Notices notices = readNotices(getConfigFileHandler(), getLog());
        final LicenseStoreData licenseStoreData = init(notices, getLog());

        final GlobalFilters globalFilters = readGlobalFilters(getLog());
        final LicenseCheckedList checkedArchives = readCheckedArchives(licenseStoreData, notices, providers, getLog());
        final List<License> cleanOutputLicenses = createCleanOutputLicenseList(getLog(), licenseStoreData);
        final List<String> finalFilteredVendorNames = readAndCollectFilteredVendorNames(getLog());

        checkParameters(getLog());

        prepareOutput(getLog());

        logNpmExcludedDirectoryNames(getLog());
        ArchiveType archiveType = getExecutionParameters().getArchiveType();
        final FinderParameters finderParameters = new FinderParameters();
        finderParameters.setArtifactServerUtil(getExecutionParameters().getArtifactServerUtil());
        final AbstractFinder finder = FinderFactory.getInstance().createFinder(executionParameters, licenseStoreData,
                finderParameters);
        getLog().info("Starting scan on " + getExecutionParameters().getScanLocation().toLogString() + "...");

        OutputResult outputResult;
        try {
            final FinderResult finderResult = finder.findLicenses();
            if (finderResult == null) {
                getLog().info("No finder results.");
                return;
            }
            getLog().info("Evaluating licenses...");
            LicenseUtil.evaluateLicenses(checkedArchives, finderResult.getArchiveFiles(), licenseStoreData);
            filterGlobal(finderResult.getArchiveFiles(), globalFilters, getLog());

            final IArchiveListFilter vendorFilter = new VendorArchiveListFilter(finalFilteredVendorNames, getLog(),
                    true);
            vendorFilter.filter(finderResult.getArchiveFiles());
            final IArchiveListFilter filter = new CleanArchiveListFilter(getLog(),
                    getExecutionParameters().isCleanOutputActive(),
                    Arrays.asList(getExecutionParameters().getCleanOutputLegalStates()), cleanOutputLicenses);
            filter.filter(finderResult.getArchiveFiles());

            writeArchiveSkeletonFile(finderResult.getArchiveFiles());
            final BuildInfo buildInfo = createBuildInfo();
            writeResultsToDatabase(buildInfo, finderResult.getArchiveFiles(), getLog());

            outputResult = createOutputResult(finderResult);
            outputResult.setPomResolutionUsed(finder.isPomResolutionUsed());
            final ReportConfiguration reportConfiguration = createReportConfiguration(archiveType);

            doOutput(getLog(), outputResult, reportConfiguration);

        } catch (Exception e) {
            throw new LicenseScoutExecutionException(e);
        }
        if (getExecutionParameters().isFailOnError()) {
            handleFailOnError(outputResult);
        }
    }

    private void handleFailOnError(final OutputResult outputResult) throws LicenseScoutFailOnErrorException {
        List<Alarm> alarms = collectAlarms(outputResult);
        if (!alarms.isEmpty()) {
            final StringBuilder message = new StringBuilder();
            message.append("artifacts have unaccepted states: ");
            for (final Alarm alarm : alarms) {
                final Archive archive = alarm.getArchive();
                message.append(archive.getFileName());
                message.append("[");
                switch (alarm.getAlarmCause()) {
                    case PROVIDER_MISSING:
                        break;
                    case UNACCEPTED_DETECTION_STATUS:
                        message.append(archive.getDetectionStatus().toString());
                        break;
                    case UNACCEPTED_LEGAL_STATUS:
                        message.append(archive.getLegalStatus().toString());
                        break;
                    default:
                        break;
                }
                message.append("] ");
            }
            throw new LicenseScoutFailOnErrorException(message.toString());
        }
    }

    private List<Alarm> collectAlarms(final OutputResult outputResult) {
        final List<Alarm> alarms = new ArrayList<>();
        final List<LegalStatus> errorLegalStates = Arrays.asList(getExecutionParameters().getErrorLegalStates());
        for (final Archive archive : outputResult.getFinderResult().getArchiveFiles()) {
            if (errorLegalStates.contains(archive.getLegalStatus())) {
                Alarm alarm = new Alarm(AlarmCause.UNACCEPTED_LEGAL_STATUS, archive);
                alarms.add(alarm);
            }
        }
        return alarms;
    }

    /**
     * @param log the logger
     */
    private void prepareOutput(final ILSLog log) {
        MiscUtil.createDirectoryIfNotExists(getExecutionParameters().getOutputDirectory(), getLog());
        for (final ExecutionOutput output : getExecutionParameters().getOutputs()) {
            final File outputFile = new File(getExecutionParameters().getOutputDirectory(),
                    OutputFileHelper.getOutputFilename(output));
            log.info("using " + output.getType() + " output file: " + outputFile.getAbsolutePath());
            final File templateFile = output.getTemplate();
            if (templateFile != null) {
                try {
                    if (getConfigFileHandler().hasTemplateFile(templateFile.getAbsolutePath())) {
                        log.info("using template: " + templateFile.getAbsolutePath());
                    } else {
                        log.warn("not using template because it is not present or it cannot be read: "
                                + templateFile.getAbsolutePath());
                        // unset so that furthers steps only need to check for null
                        output.setTemplate(null);
                    }
                } catch (IOException e) {
                    log.warn("not using template because presence cannot be verified: "
                            + templateFile.getAbsolutePath());
                    // unset so that furthers steps only need to check for null
                    output.setTemplate(null);
                }
            }
        }

    }

    private BuildInfo createBuildInfo() {
        checkBuildParameters();
        String licenseReportCsvUrl = null;
        String licenseReportHtmlUrl = null;
        String licenseReportTxtUrl = null;
        for (final ExecutionOutput output : getExecutionParameters().getOutputs()) {
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
                    break;
            }
        }
        // Note: date is used from the database
        return new BuildInfo(getExecutionParameters().getBuildName(), getExecutionParameters().getBuildVersion(), null,
                getExecutionParameters().getBuildUrl(), licenseReportCsvUrl, licenseReportHtmlUrl, licenseReportTxtUrl);
    }

    private void checkBuildParameters() {
        if (getExecutionParameters().isWriteResultsToDatabase()) {
            // NOTE: these parameters are only used for writing information to the database
            if (StringUtils.isEmpty(getExecutionParameters().getBuildName())) {
                getLog().warn("Parameter buildName not configured");
            }
            if (StringUtils.isEmpty(getExecutionParameters().getBuildVersion())) {
                getLog().warn("Parameter buildVersion not configured");
            }
            if (StringUtils.isEmpty(getExecutionParameters().getBuildUrl())) {
                getLog().warn("Parameter buildUrl not configured");
            }
        }
    }

    private void writeArchiveSkeletonFile(final List<Archive> archives) throws LicenseScoutExecutionException {
        writeArchiveXmlSkeletonFile(archives);
        writeArchiveCsvSkeletonFile(archives);
    }

    /**
     * @param archives
     * @throws LicenseScoutExecutionException
     */
    private void writeArchiveCsvSkeletonFile(final List<Archive> archives) throws LicenseScoutExecutionException {
        if (getExecutionParameters().isWriteArchiveCsvSkeleton()) {
            if (getExecutionParameters().getArchiveCsvSkeletonFile() != null) {
                getLog().info("writing archive CSV skeleton file to: "
                        + getExecutionParameters().getArchiveCsvSkeletonFile());
                try {
                    LicenseCheckedList.writeCsvSkeletonFile(getExecutionParameters().getArchiveCsvSkeletonFile(),
                            archives);
                } catch (IOException e) {
                    throw new LicenseScoutExecutionException("cannot write archive CSV skeleton file", e);
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
     * @throws LicenseScoutExecutionException
     */
    private void writeArchiveXmlSkeletonFile(final List<Archive> archives) throws LicenseScoutExecutionException {
        if (getExecutionParameters().isWriteArchiveXmlSkeleton()) {
            if (getExecutionParameters().getArchiveXmlSkeletonFile() != null) {
                getLog().info("writing archive XML skeleton file to: "
                        + getExecutionParameters().getArchiveXmlSkeletonFile());
                try {
                    LicenseCheckedList.writeXmlSkeletonFile(getExecutionParameters().getArchiveXmlSkeletonFile(),
                            archives);
                } catch (IOException e) {
                    throw new LicenseScoutExecutionException("cannot write archive XML skeleton file", e);
                }
            } else {
                getLog().info("Not writing archive XML skeleton file (not configured)");
            }
        } else {
            getLog().info("Not writing archive XML skeleton file (not configured)");
        }
    }

    private void writeResultsToDatabase(final BuildInfo buildInfo, final List<Archive> archives, final ILSLog log) {
        if (getExecutionParameters().isWriteResultsToDatabase()) {
            if (isSnapshotVersion(getExecutionParameters().getBuildVersion())
                    && !getExecutionParameters().isWriteResultsToDatabaseForSnapshotBuilds()) {
                getLog().info("Not writing results to database because is snapshot version");
            } else {
                ExecutionDatabaseConfiguration resultDatabaseConfiguration = getExecutionParameters()
                        .getResultDatabaseConfiguration();
                if (getExecutionParameters().getResultDatabaseConfiguration() != null
                        && resultDatabaseConfiguration.getJdbcUrl() != null) {
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

    /**
     * @param log the logger
     * @param outputResult
     * @param reportConfiguration
     * @throws Exception
     */
    private void doOutput(final ILSLog log, final OutputResult outputResult,
                          final ReportConfiguration reportConfiguration)
            throws Exception {
        for (final ExecutionOutput output : getExecutionParameters().getOutputs()) {
            final File outputFile = new File(getExecutionParameters().getOutputDirectory(),
                    OutputFileHelper.getOutputFilename(output));
            final OutputFileType outputFileType = output.getType();
            final IReportExporter exporter = getReportExporter(outputFileType);
            reportConfiguration.setOutputFile(outputFile);
            reportConfiguration.setTemplateFile(output.getTemplate());
            reportConfiguration.setTemplateEncoding(output.getTemplateEncoding());
            reportConfiguration.setOutputEncoding(output.getOutputEncoding());
            exporter.export(outputResult, reportConfiguration);
            log.info("written output for " + outputFileType + " to "
                    + reportConfiguration.getOutputFile().getAbsolutePath());
        }
        if (getExecutionParameters().getOutputs() == null || getExecutionParameters().getOutputs().isEmpty()) {
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
        reportConfiguration.setShowDocumentationUrl(getExecutionParameters().isShowDocumentationUrl());
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

    private void checkParameters(final ILSLog log) throws LicenseScoutExecutionException {
        final ScanLocation scanLocation = getExecutionParameters().getScanLocation();
        if (scanLocation != null) {
            List<File> scanFiles = scanLocation.getScanFiles();
            if (scanFiles != null && !scanFiles.isEmpty()) {
                // OK
                log.info("using scan files: " + scanFiles);
            } else {
                final File scanDirectory = scanLocation.getScanDirectory();
                if (scanDirectory != null) {
                    if (!scanDirectory.exists()) {
                        throw new LicenseScoutExecutionException(
                                "This scan directory does not exist: " + scanDirectory.getAbsolutePath());
                    }
                    log.info("using scan directory: " + scanDirectory.getAbsolutePath());
                } else {
                    throw new LicenseScoutExecutionException("neither scanFiles nor scanDirectory configured");
                }
            }
        } else {
            throw new LicenseScoutExecutionException("scan location not configured");
        }
    }

    /**
     * @param notices the data object containing information on notices
     * @param log the logger
     * @return a license store data object
     * @throws LicenseScoutExecutionException 
     */
    private LicenseStoreData init(final Notices notices, final ILSLog log) throws LicenseScoutExecutionException {

        final LicenseStoreData licenseStoreData = readLicenses(notices, log);
        if (licenseStoreData != null) {
            readLicenseUrlMappings(licenseStoreData, log);
            readLicenseNameMappings(licenseStoreData, log);
        }

        return licenseStoreData;
    }

    /**
     * @param log the logger
     * @param licenseStoreData the data object containing information on licenses
     * @return a list of licenses that should not appear in cleaned output
     */
    protected List<License> createCleanOutputLicenseList(final ILSLog log, final LicenseStoreData licenseStoreData) {

        log.info("Clean output: " + (getExecutionParameters().isCleanOutputActive() ? "active" : "not active"));
        final List<License> cleanOutputLicenses = new ArrayList<>();
        if (getExecutionParameters().isCleanOutputActive()) {
            for (final String cleanOutputLicenseSpdxIdentifier : getExecutionParameters()
                    .getCleanOutputLicenseSpdxIdentifiers()) {
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
        final List<IReportExporterFactory> reportExporterFactories = new ArrayList<>();
        if (getExecutionParameters().getExporterFactories() != null) {
            reportExporterFactories.addAll(getExecutionParameters().getExporterFactories());
        }
        if (reportExporterFactories.isEmpty()) {
            throw new UnsupportedOperationException("No Report exporter factories available");
        }
        for (IReportExporterFactory factory : reportExporterFactories) {
            if (factory.getSupportedOutputFileTypes().contains(outputFileType)) {
                return factory.getReportExporter(outputFileType);
            }
        }
        throw new UnsupportedOperationException("Unhandled OutputFileType: " + outputFileType);
    }

    /**
     * @param licenseStoreData the data object containing information on licenses
     * @param log the logger
     * @throws LicenseScoutExecutionException
     */
    protected void readLicenseUrlMappings(final LicenseStoreData licenseStoreData, final ILSLog log)
            throws LicenseScoutExecutionException {
        try (final InputStream inputStream = getConfigFileHandler().getLicenseUrlMappingsInputStream()) {
            if (inputStream != null) {
                licenseStoreData.readUrlMappings(inputStream, log);
            }
        } catch (IOException e) {
            throw new LicenseScoutExecutionException("cannot read license URL mappings", e);
        }
    }

    /**
     * @param licenseStoreData the data object containing information on licenses
     * @param log the logger
     * @throws LicenseScoutExecutionException
     */
    protected void readLicenseNameMappings(final LicenseStoreData licenseStoreData, final ILSLog log)
            throws LicenseScoutExecutionException {
        try (final InputStream inputStream = getConfigFileHandler().getLicenseNameMappingsInputStream()) {
            if (inputStream != null) {
                licenseStoreData.readNameMappings(inputStream, log);
            }
        } catch (IOException e) {
            throw new LicenseScoutExecutionException("cannot read license name mappings", e);
        }
    }

    /**
     * @param log the logger
     * @return a global filters object
     * @throws LicenseScoutExecutionException
     */
    protected GlobalFilters readGlobalFilters(final ILSLog log) throws LicenseScoutExecutionException {
        final GlobalFilters globalFilters = new GlobalFilters();
        try (final InputStream inputStream = getConfigFileHandler().getGlobalFiltersInputStream()) {
            if (inputStream != null) {
                globalFilters.read(inputStream);
            }
        } catch (IOException e) {
            throw new LicenseScoutExecutionException("cannot read global filters", e);
        }
        return globalFilters;
    }

    /**
     * @param notices the data object containing information on notices
     * @param log the logger
     * @return a license store data object
     * @throws LicenseScoutExecutionException 
     */
    protected LicenseStoreData readLicenses(final Notices notices, final ILSLog log)
            throws LicenseScoutExecutionException {
        try (final InputStream inputStream = getConfigFileHandler().getLicensesInputStream()) {
            if (inputStream != null) {
                final LicenseStoreData licenseStoreData = new LicenseStoreData();
                licenseStoreData.readLicenses(inputStream, notices, getExecutionParameters().isValidateLicenseXml(),
                        log);
                return licenseStoreData;
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new LicenseScoutExecutionException("cannot read licenses", e);
        }
        return null;

    }

    /**
     * Reads providers.
     * 
     * @param configFileHandler 
     * @param log the logger
     * @return a providers data object
     * @throws LicenseScoutExecutionException
     */
    protected Providers readProviders(final ConfigFileHandler configFileHandler, final ILSLog log)
            throws LicenseScoutExecutionException {
        try (InputStream inputStream = configFileHandler.getProvidersInputStream()) {
            if (inputStream != null) {
                final Providers providers = new Providers();
                providers.readProviders(inputStream, getExecutionParameters().isValidateLicenseXml(), log);
                return providers;
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new LicenseScoutExecutionException("cannot read providers", e);
        }
        return null;
    }

    /**
     * Reads notices.
     * @param configFileHandler 
     * @param log the logger
     * @return a notices data object
     * @throws LicenseScoutExecutionException
     */
    protected Notices readNotices(final ConfigFileHandler configFileHandler, final ILSLog log)
            throws LicenseScoutExecutionException {
        try (InputStream inputStream = configFileHandler.getNoticesInputStream()) {
            if (inputStream != null) {
                final Notices notices = new Notices();
                notices.readNotices(inputStream, getExecutionParameters().isValidateLicenseXml(), log);
                return notices;
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new LicenseScoutExecutionException("cannot read notices", e);
        }
        return null;
    }

    /**
     * @param licenseStoreData the data object containing information on licenses
     * @param notices the data object containing information on notices
     * @param providers the data object containing information on providers
     * @param log the logger
     * @return an object containing information on manually checked archives
     * @throws LicenseScoutExecutionException
     */
    protected LicenseCheckedList readCheckedArchives(final LicenseStoreData licenseStoreData, final Notices notices,
                                                     final Providers providers, final ILSLog log)
            throws LicenseScoutExecutionException {
        final LicenseCheckedList checkedArchives = new LicenseCheckedList();
        try (final InputStream inputStream = getConfigFileHandler().getCheckedArchivesInputStream()) {
            if (inputStream != null) {
                checkedArchives.readCsv(inputStream, licenseStoreData, providers, notices, log);
            }
        } catch (IOException e) {
            throw new LicenseScoutExecutionException("cannot read check archives list", e);
        }
        return checkedArchives;
    }

    /**
     * @param log the logger
     * @return a list of vendor names to filter out
     * @throws LicenseScoutExecutionException
     */
    protected List<String> readAndCollectFilteredVendorNames(final ILSLog log) throws LicenseScoutExecutionException {
        final List<String> resultFilteredVendorNames = new ArrayList<>();
        for (final String vendorName : getExecutionParameters().getFilteredVendorNames()) {
            resultFilteredVendorNames.add(vendorName);
            log.info("Using vendor name to filter (from maven configuration): '" + vendorName + "'");
        }
        try (final InputStream inputStream = getConfigFileHandler().getFilteredVendorNamesInputStream()) {
            if (inputStream != null) {
                final List<String> tmpResultList = readFilteredVendorNamesFromFile(inputStream, log);
                resultFilteredVendorNames.addAll(tmpResultList);
            }
        } catch (IOException e) {
            throw new LicenseScoutExecutionException("cannot read filtered vendor names list", e);
        }
        return resultFilteredVendorNames;
    }

    /**
     * Reads a CSV file containing vendor names to filter out.
     * 
     * @param inputStream an input stream to read the file contents from
     * @param log the logger
     * @return a list of strings
     * @throws IOException if an error occurred while reading from the file
     */
    protected static List<String> readFilteredVendorNamesFromFile(final InputStream inputStream, final ILSLog log)
            throws IOException {
        final List<String> resultList = new ArrayList<>();
        String line = "";

        try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
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
     * @param archiveFiles
     * @param globalFilters
     * @param log the logger
     */
    protected static void filterGlobal(final List<Archive> archiveFiles, final GlobalFilters globalFilters,
                                       final ILSLog log) {
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
    protected void logNpmExcludedDirectoryNames(final ILSLog log) {
        if (getExecutionParameters().getArchiveType() == ArchiveType.JAVASCRIPT) {
            if (getExecutionParameters().getNpmExcludedDirectoryNames().isEmpty()) {
                log.info("No directory name to exclude in NPM scan configured.");
            } else {
                for (final String excludedDirectoryName : getExecutionParameters().getNpmExcludedDirectoryNames()) {
                    log.info("Using directory name to exclude in NPM scan: " + excludedDirectoryName);
                }
            }
        }
    }

    private static boolean isSnapshotVersion(final String version) {
        return version.endsWith("-SNAPSHOT");
    }

}
