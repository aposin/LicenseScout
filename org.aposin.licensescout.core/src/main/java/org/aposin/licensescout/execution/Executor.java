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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
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
import org.aposin.licensescout.finder.FinderFactory;
import org.aposin.licensescout.finder.FinderResult;
import org.aposin.licensescout.finder.LicenseScoutExecutionException;
import org.aposin.licensescout.license.GlobalFilters;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseCheckedList;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.model.Notices;
import org.aposin.licensescout.model.Providers;
import org.aposin.licensescout.util.CryptUtil;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.MiscUtil;
import org.xml.sax.SAXException;

/**
 * Engine that contains the main control flow of the LicenseScout.
 *
 */
public class Executor {

    private final ExecutionParameters executionParameters;

    /**
     * Constructor.
     * @param executionParameters 
     */
    public Executor(final ExecutionParameters executionParameters) {
        this.executionParameters = executionParameters;
    }

    /**
     * @return the executionParameters
     */
    protected final ExecutionParameters getExecutionParameters() {
        return executionParameters;
    }

    protected final ILFLog getLog() {
        return getExecutionParameters().getLog();
    }

    /**
     * Main execution of the LicenseScout.
     * @throws LicenseScoutExecutionException
     */
    public void execute() throws LicenseScoutExecutionException {

        final Providers providers = readProviders(getLog());
        final Notices notices = readNotices(getLog());
        final LicenseStoreData licenseStoreData = init(notices, getLog());

        final GlobalFilters globalFilters = readGlobalFilters(getLog());
        final LicenseCheckedList checkedArchives = readCheckedArchives(licenseStoreData, notices, providers, getLog());
        final List<License> cleanOutputLicenses = createCleanOutputLicenseList(getLog(), licenseStoreData);
        final List<String> finalFilteredVendorNames = readAndCollectFilteredVendorNames(getLog());

        checkParameters(getLog());

        prepareOutput(getLog());

        logNpmExcludedDirectoryNames(getLog());
        ArchiveType archiveType = getExecutionParameters().getArchiveType();
        final RunParameters runParameters = new RunParameters();
        runParameters.setNexusCentralBaseUrl(getExecutionParameters().getNexusCentralBaseUrl());
        runParameters.setConnectTimeout(getExecutionParameters().getConnectTimeout());
        final AbstractFinder finder = FinderFactory.createFinder(executionParameters, licenseStoreData, runParameters);
        getLog().info("Starting scan on " + getExecutionParameters().getScanDirectory().getAbsolutePath() + "...");

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

            final OutputResult outputResult = createOutputResult(finderResult);
            outputResult.setPomResolutionUsed(finder.isPomResolutionUsed());
            final ReportConfiguration reportConfiguration = createReportConfiguration(archiveType);

            doOutput(getLog(), outputResult, reportConfiguration);
        } catch (Exception e) {
            throw new LicenseScoutExecutionException(e);
        }
    }

    /**
     * @param log
     */
    private void prepareOutput(final ILFLog log) {
        MiscUtil.createDirectoryIfNotExists(getExecutionParameters().getOutputDirectory(), getLog());
        for (final Output output : getExecutionParameters().getOutputs()) {
            final File outputFile = new File(getExecutionParameters().getOutputDirectory(), output.getFilename());
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
        String licenseReportCsvUrl = null;
        String licenseReportHtmlUrl = null;
        String licenseReportTxtUrl = null;
        for (final Output output : getExecutionParameters().getOutputs()) {
            // TODO: rework
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
        return new BuildInfo(getExecutionParameters().getBuildName(), getExecutionParameters().getBuildVersion(), null,
                getExecutionParameters().getBuildUrl(), licenseReportCsvUrl, licenseReportHtmlUrl, licenseReportTxtUrl);
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

    private void writeResultsToDatabase(final BuildInfo buildInfo, final List<Archive> archives, final ILFLog log) {
        if (getExecutionParameters().isWriteResultsToDatabase()) {
            if (isSnapshotVersion(getExecutionParameters().getBuildVersion())
                    && !getExecutionParameters().isWriteResultsToDatabaseForSnapshotBuilds()) {
                getLog().info("Not writing results to database because is snapshot version");
            } else {
                DatabaseConfiguration resultDatabaseConfiguration = getExecutionParameters()
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
     * @param log
     * @param outputResult
     * @param reportConfiguration
     * @throws Exception
     */
    private void doOutput(final ILFLog log, final OutputResult outputResult,
                          final ReportConfiguration reportConfiguration)
            throws Exception {
        for (final Output output : getExecutionParameters().getOutputs()) {
            final File outputFile = new File(getExecutionParameters().getOutputDirectory(), output.getFilename());
            final OutputFileType outputFileType = output.getType();
            final IReportExporter exporter = getReportExporter(outputFileType);
            reportConfiguration.setOutputFile(outputFile);
            reportConfiguration.setTemplateFile(output.getTemplate());
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

    private void checkParameters(final ILFLog log) throws LicenseScoutExecutionException {
        final File scanDirectory = getExecutionParameters().getScanDirectory();
        if (scanDirectory != null) {
            if (!scanDirectory.exists()) {
                throw new LicenseScoutExecutionException(
                        "This scan directory does not exist: " + scanDirectory.getAbsolutePath());
            }
            log.info("using scan directory: " + scanDirectory.getAbsolutePath());
        } else {
            throw new LicenseScoutExecutionException("scanDirectory not configured");
        }
    }

    /**
     * @param log the logger
     * @return a license store data object
     * @throws LicenseScoutExecutionException 
     * @throws LicenseScoutExecutionException
     */
    private LicenseStoreData init(final Notices notices, final ILFLog log) throws LicenseScoutExecutionException {

        final LicenseStoreData licenseStoreData = readLicenses(notices, log);
        if (licenseStoreData != null) {
            readLicenseUrlMappings(licenseStoreData, log);
            readLicenseNameMappings(licenseStoreData, log);
        }

        return licenseStoreData;
    }

    /**
     * @param log the logger
     * @param licenseStoreData
     * @return a list of licenses that should not appear in cleaned output
     */
    protected List<License> createCleanOutputLicenseList(final ILFLog log, final LicenseStoreData licenseStoreData) {

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
     * @throws LicenseScoutExecutionException
     */
    protected void readLicenseUrlMappings(final LicenseStoreData licenseStoreData, final ILFLog log)
            throws LicenseScoutExecutionException {
        final String licenseUrlMappingsFilename = getExecutionParameters().getLicenseUrlMappingsFilename();
        if (licenseUrlMappingsFilename != null) {
            final File file = new File(licenseUrlMappingsFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading license URL mappings from " + licenseUrlMappingsFilename);
                try {
                    licenseStoreData.readUrlMappings(licenseUrlMappingsFilename, log);
                } catch (IOException e) {
                    throw new LicenseScoutExecutionException("cannot read license URL mappings", e);
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
     * @throws LicenseScoutExecutionException
     */
    protected void readLicenseNameMappings(final LicenseStoreData licenseStoreData, final ILFLog log)
            throws LicenseScoutExecutionException {
        final String licenseNameMappingsFilename = getExecutionParameters().getLicenseNameMappingsFilename();
        if (licenseNameMappingsFilename != null) {
            final File file = new File(licenseNameMappingsFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading license name mappings from " + licenseNameMappingsFilename);
                try {
                    licenseStoreData.readNameMappings(licenseNameMappingsFilename, log);
                } catch (IOException e) {
                    throw new LicenseScoutExecutionException("cannot read license name mappings", e);
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
     * @throws LicenseScoutExecutionException
     */
    protected GlobalFilters readGlobalFilters(final ILFLog log) throws LicenseScoutExecutionException {
        final GlobalFilters globalFilters = new GlobalFilters();
        final String globalFiltersFilename = getExecutionParameters().getGlobalFiltersFilename();
        if (globalFiltersFilename != null) {
            final File file = new File(globalFiltersFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading global filters from " + globalFiltersFilename);
                try {
                    globalFilters.read(globalFiltersFilename);
                } catch (IOException e) {
                    throw new LicenseScoutExecutionException("cannot read global filters", e);
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
     * @throws LicenseScoutExecutionException 
     */
    protected LicenseStoreData readLicenses(final Notices notices, final ILFLog log)
            throws LicenseScoutExecutionException {
        final File licensesFilename = getExecutionParameters().getLicensesFilename();
        if (licensesFilename != null) {
            if (licensesFilename.exists() && licensesFilename.canRead()) {
                log.info("reading licenses from " + licensesFilename);
                try {
                    final LicenseStoreData licenseStoreData = new LicenseStoreData();
                    licenseStoreData.readLicenses(licensesFilename, notices,
                            getExecutionParameters().isValidateLicenseXml(), log);
                    return licenseStoreData;
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new LicenseScoutExecutionException("cannot read licenses", e);
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
     * @throws LicenseScoutExecutionException
     */
    protected Providers readProviders(final ILFLog log) throws LicenseScoutExecutionException {
        final File providersFilename = getExecutionParameters().getProvidersFilename();
        if (providersFilename != null) {
            if (providersFilename.exists() && providersFilename.canRead()) {
                log.info("reading providers from " + providersFilename);
                try {
                    final Providers providers = new Providers();
                    providers.readProviders(providersFilename, getExecutionParameters().isValidateLicenseXml(), log);
                    return providers;
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new LicenseScoutExecutionException("cannot read providers", e);
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
     * @throws LicenseScoutExecutionException
     */
    protected Notices readNotices(final ILFLog log) throws LicenseScoutExecutionException {
        final File noticesFilename = getExecutionParameters().getNoticesFilename();
        if (noticesFilename != null) {
            if (noticesFilename.exists() && noticesFilename.canRead()) {
                log.info("reading notices from " + noticesFilename);
                try {
                    final Notices notices = new Notices();
                    notices.readNotices(noticesFilename, getExecutionParameters().isValidateLicenseXml(), log);
                    return notices;
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new LicenseScoutExecutionException("cannot read notices", e);
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
     * @return an object containing information on manually checked archives
     * @throws LicenseScoutExecutionException
     */
    protected LicenseCheckedList readCheckedArchives(final LicenseStoreData licenseStoreData, final Notices notices,
                                                     final Providers providers, final ILFLog log)
            throws LicenseScoutExecutionException {
        final LicenseCheckedList checkedArchives = new LicenseCheckedList();
        final File checkedArchivesFilename = getExecutionParameters().getCheckedArchivesFilename();
        if (checkedArchivesFilename != null) {
            if (checkedArchivesFilename.exists() && checkedArchivesFilename.canRead()) {
                log.info("reading checked archives list from " + checkedArchivesFilename);
                try {
                    checkedArchives.readCsv(checkedArchivesFilename, licenseStoreData, providers, notices, log);
                } catch (IOException e) {
                    throw new LicenseScoutExecutionException("cannot read check archives list", e);
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
     * @throws LicenseScoutExecutionException
     */
    protected List<String> readAndCollectFilteredVendorNames(final ILFLog log) throws LicenseScoutExecutionException {
        final List<String> resultFilteredVendorNames = new ArrayList<>();
        for (final String vendorName : getExecutionParameters().getFilteredVendorNames()) {
            resultFilteredVendorNames.add(vendorName);
            log.info("Using vendor name to filter (from maven configuration): '" + vendorName + "'");
        }
        final String filteredVendorNamesFilename = getExecutionParameters().getFilteredVendorNamesFilename();
        if (filteredVendorNamesFilename != null) {
            final File file = new File(filteredVendorNamesFilename);
            if (file.exists() && file.canRead()) {
                log.info("reading vendor names to filter out from " + filteredVendorNamesFilename);
                try {
                    final List<String> tmpResultList = readFilteredVendorNamesFromFile(filteredVendorNamesFilename,
                            log);
                    resultFilteredVendorNames.addAll(tmpResultList);
                } catch (IOException e) {
                    throw new LicenseScoutExecutionException("cannot read filtered vendor names list", e);
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
