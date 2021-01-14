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
package org.aposin.licensescout.report.mojo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.ConfigFileHandler;
import org.aposin.licensescout.configuration.ConfigFileHandlerHelper;
import org.aposin.licensescout.configuration.ConfigFileParameters;
import org.aposin.licensescout.configuration.ExecutionOutput;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.execution.ExecutionParameters;
import org.aposin.licensescout.execution.Executor;
import org.aposin.licensescout.execution.IReportExporterFactory;
import org.aposin.licensescout.execution.LicenseScoutExecutionException;
import org.aposin.licensescout.execution.LicenseScoutFailOnErrorException;
import org.aposin.licensescout.execution.ScanLocation;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.maven.utils.ArtifactHelper;
import org.aposin.licensescout.maven.utils.ArtifactItem;
import org.aposin.licensescout.maven.utils.ArtifactScope;
import org.aposin.licensescout.maven.utils.ArtifactServerUtilHelper;
import org.aposin.licensescout.maven.utils.IRepositoryParameters;
import org.aposin.licensescout.maven.utils.MavenLog;
import org.aposin.licensescout.report.exporter.DoxiaReportExporterFactory;
import org.aposin.licensescout.report.exporter.ISinkProvider;
import org.aposin.licensescout.util.CryptUtil;
import org.aposin.licensescout.util.ILSLog;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyResolutionException;

/**
 * Creates a report integrated into a Maven site report of license information found by the LicenseScout.
 *
 */
public abstract class AbstractReportMojo extends AbstractMavenReport implements IRepositoryParameters {

    /**
     * Directory to scan for archives.
     * <p>This is the directory where the LicenseScout will start to traverse directories recursively.
     * If the directory does not exist, the LicenseScout will be terminated with an exception.</p>
     * <p>Either this parameter or {@link #scanArtifacts} is required.</p>
     * 
     * @since 1.1
     */
    @Parameter(property = "scanDirectory", required = false)
    private File scanDirectory;

    /**
     * List of artifacts that should be scanned for dependencies.
     * <p>If this parameter is configured, the LicenseScout does a Maven dependency resolution of the given artifacts using the scope configured with {@link #scanArtifactScope}.
     * The resulting set of artifacts are scanned for license information.</p>
     * <p>Either this parameter or {@link #scanDirectory} is required.</p>
     * 
     * @since 1.4.0
     */
    @Parameter(property = "scanArtifacts", required = false)
    private List<ArtifactItem> scanArtifacts;

    /**
     * Resolution scope for scanned artifacts.
     * 
     * <p>If artifacts to scan are given (by specifying {@link #scanArtifacts}), this determines the scope 
     * of resolving their dependencies that are taken into account.
     * If {@link #scanDirectory} is used, the value given has no effect.</p>
     * 
     * @since 1.4.0
     */
    @Parameter(property = "scanArtifactScope", required = false, defaultValue = "compile")
    private ArtifactScope scanArtifactScope;

    /**
     * Name of the file to read known licenses from.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-licenses">Licenses</a>.<p>
     * 
     * @since 1.1
     */
    @Parameter(property = "licensesFilename", required = false)
    private File licensesFilename;

    /**
     * Name of the file to read known providers from.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-providers">Providers</a>.<p>
     * 
     * @since 1.2.6
     */
    @Parameter(property = "providersFilename", required = false)
    private File providersFilename;

    /**
     * Name of the file to read license notices from.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-notices">Notices</a>.<p>
     * 
     * @since 1.2.6
     */
    @Parameter(property = "noticesFilename", required = false)
    private File noticesFilename;

    /**
     * Name of the file to read checked archives from.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-checked-archives">Checked Archives</a>.<p>
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "checkedarchives.csv", property = "checkedArchivesFilename", required = false)
    private File checkedArchivesFilename;

    /**
     * Name of the file to read license URL mappings from.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-license-url-mapping">License URL Mapping</a>.<p>
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "urlmappings.csv", property = "licenseUrlMappingsFilename", required = false)
    private File licenseUrlMappingsFilename;

    /**
     * Name of the file to read license name mappings from.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-license-name-mapping">License Name Mapping</a>.<p>
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "namemappings.csv", property = "licenseNameMappingsFilename", required = false)
    private File licenseNameMappingsFilename;

    /**
     * Name of the file to read global filter patterns from.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-global-filters">Global Filters</a>.<p>
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "globalFilters.csv", property = "globalFiltersFilename", required = false)
    private File globalFiltersFilename;

    /**
     * Name of the file to read of vendor names to filter out from.
     * This is alternative to {@link #filteredVendorNames}.
     * If both are given, the entries are merged.
     * 
     * <p>For the format of the file see <a href="../licensescout-documentation/usermanual/configuration.html#configuration-file-vendor-names">Vendor Names</a>.<p>
     * 
     * @since 1.1
     */
    @Parameter(property = "filteredVendorNamesFilename", required = false)
    private File filteredVendorNamesFilename;

    /**
     * If cleaning the output should be active.
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "false", property = "cleanOutputActive", required = false)
    private boolean cleanOutputActive;

    /**
     * List of legal states that should be filtered out if cleanOutput is active.
     * 
     * @see LegalStatus
     * @since 1.2.6
     */
    @Parameter(property = "cleanOutputLegalStates", required = false)
    private LegalStatus[] cleanOutputLegalStates;

    /**
     * List of licenses that should be filtered out if cleanOutput is active, given by their SPDX identifier.
     * 
     * @since 1.2.6
     */
    @Parameter(property = "cleanOutputLicenseSpdxIdentifiers", required = false)
    private String[] cleanOutputLicenseSpdxIdentifiers;

    /**
     * List of vendor names to filter out.
     * This is alternative to {@link #filteredVendorNamesFilename}. If both are given, the entries are merged.
     * 
     * @since 1.2.6
     */
    @Parameter(property = "filteredVendorNames", required = false)
    private List<String> filteredVendorNames;

    /**
     * Base URL for fetching Maven central artifacts from a server.
     * 
     * This can be Maven central itself (like the default value) or a mirror of maven central on a Nexus or other artifact server.
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "https://repo.maven.apache.org/maven2/", property = "nexusCentralBaseUrl", required = false)
    private String nexusCentralBaseUrl;

    /**
     * Timeout for connecting to artifact server. This timeout is used when
     * connecting to an artifact server (as configured with
     * {@link #nexusCentralBaseUrl}) to retrieve parent POMs. The value is in
     * milliseconds.
     * 
     * @since 1.3.1
     */
    @Parameter(defaultValue = "1000", property = "connectTimeout", required = false)
    private int connectTimeout;

    /**
     * Whether the license XML file should be validated while reading in.
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "false", property = "validateLicenseXml", required = false)
    private boolean validateLicenseXml;

    /**
     * Whether the documentation URL from the checked archives file should be displayed as a column in HTML and CSV reports.
     * 
     * @since 1.2.6
     */
    @Parameter(defaultValue = "true", property = "showDocumentationUrlColumn", required = false)
    private boolean showDocumentationUrlColumn;

    /**
     * Whether the candidate license files should be shown as a column in HTML reports.
     * 
     * @since 1.4.0
     */
    @Parameter(defaultValue = "false", property = "showLicenseCandidateFilesColumn", required = false)
    private boolean showLicenseCandidateFilesColumn;

    /**
     * Whether the provider should be shown as a column in HTML reports.
     * 
     * @since 1.4.0
     */
    @Parameter(defaultValue = "false", property = "showProviderColumn", required = false)
    private boolean showProviderColumn;

    /**
     * The name of the build to use when writing database entries.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true. </p>
     * 
     * @since 1.2
     */
    @Parameter(property = "buildName", required = false, defaultValue = "${project.artifactId}")
    private String buildName;

    /**
     * The version of the build to use when writing database entries.
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true.</p>
     * 
     * @since 1.2.6
     */
    @Parameter(property = "buildVersion", required = false, defaultValue = "${project.version}")
    private String buildVersion;

    /**
     * The URL of the build itself (point to Jenkins).
     * 
     * <p>Only used if {@link #writeResultsToDatabase} is true.</p>
     * 
     * @since 1.2.6
     */
    @Parameter(property = "buildUrl", required = false)
    private String buildUrl;

    /**
     * Skips the execution.
     * 
     * <p>For examples on using this parameter in a build see <a href="../licensescout-maven-plugin/examples/controlling-execution.html">Controlling the execution</a>.<p>
     * 
     * @since 1.3.1
     */
    @Parameter(defaultValue = "false", property = "skip", required = false)
    private boolean skip;
    /**
     * Configuration bundle artifact.
     * 
     * @since 1.3.1
     */
    @Parameter(property = "configurationBundle", required = false)
    private ArtifactItem configurationBundle;

    /**
     * The project's remote repositories to use for the resolution.
     * @since 1.3.1
     */
    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private List<RemoteRepository> remoteRepositories;

    /**
     * The Maven repository system.
     * @since 1.3.1
     */
    @Component
    private RepositorySystem repositorySystem;

    /**
     * The current repository/network configuration of Maven.
     * @since 1.3.1
     */
    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repositorySystemSession;

    /**
     * Algorithm to use for message digests.
     * 
     * <p>The configured name is passed to the Java Cryptography extension (JCE) to obtain an algorithm implementation.
     * Therefore, the name should match an algorithm name supported by one of the configured cryptography providers.</p>
     * 
     * @since 1.4.0
     */
    @Parameter(defaultValue = CryptUtil.DEFAULT_MD_ALGORITHM, property = "messageDigestAlgorithm", required = false)
    private String messageDigestAlgorithm;

    /**
     * Encoding of the configuration files.
     * 
     * <p>A JAVA encoding name to use for reading the configuration files.
     * If not configured, the value of 'project.build.sourceEncoding' is used.
     * If this is not configured, too, the platform specific default encoding is used.</p>
     * 
     * @since 1.4.0
     */
    @Parameter(property = "configurationFileEncoding", required = false, defaultValue = "${project.build.sourceEncoding}")
    private String configurationFileEncoding;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputName() {
        return "licensereport";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(Locale locale) {
        return "LicenseScout License Report";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(Locale locale) {
        return "A detailled report on licenses";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        final ILSLog log = new MavenLog(getLog());

        if (skip) {
            log.info("Not executing because skip is configured as true.");
            return;
        }
        File configurationBundleFile = null;
        try {
            if (configurationBundle != null) {
                configurationBundleFile = ArtifactHelper.getArtifactFile(this, configurationBundle);
            }
        } catch (MojoExecutionException e1) {
            throw new MavenReportException(e1.getLocalizedMessage(), e1);
        }

        final ExecutionParameters executionParameters = new ExecutionParameters();
        final ConfigFileParameters configFileParameters = new ConfigFileParameters();
        try {
            BeanUtils.copyProperties(executionParameters, this);
            BeanUtils.copyProperties(configFileParameters, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MavenReportException("Internal error occured: " + e.getLocalizedMessage(), e);
        }
        ExecutionOutput output = new ExecutionOutput();
        output.setType(OutputFileType.DOXIA);
        executionParameters.setOutputs(Arrays.asList(output));
        output.setOutputEncoding(getOutputEncoding());
        executionParameters.setArchiveType(getArchiveType());
        executionParameters.setLsLog(log);
        executionParameters.setOutputDirectory(new File(getOutputDirectory()));
        IReportExporterFactory doxiaFactory = new DoxiaReportExporterFactory(new ISinkProvider() {

            @Override
            public Sink getSink() {
                return AbstractReportMojo.this.getSink();
            }
        });
        executionParameters.setExporterFactories(Arrays.asList(doxiaFactory));
        ArtifactServerUtilHelper.createAndSetArtifactServerUtil(executionParameters);

        final ConfigFileHandler configFileHandler = ConfigFileHandlerHelper
                .createConfigFileHandler(configurationBundleFile, configFileParameters, log);

        final Executor executor = new Executor(executionParameters, configFileHandler);
        try {
            executor.execute();
        } catch (LicenseScoutExecutionException e) {
            throw new MavenReportException("Internal error occured: " + e.getLocalizedMessage(), e);
        } catch (LicenseScoutFailOnErrorException e) {
            throw new MavenReportException("Fail on error condition: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Obtains the archive type handled by this MOJO.
     * 
     * @return an archive type
     */
    protected abstract ArchiveType getArchiveType();

    // --------------getters -- NOTE: these are called by reflection from BeanUtils.copyProperties()

    /**
     * @return the showDocumentationUrlColumn
     */
    public final boolean isShowDocumentationUrlColumn() {
        return showDocumentationUrlColumn;
    }

    /**
     * @return the showLicenseCandidateFilesColumn
     */
    public final boolean isShowLicenseCandidateFilesColumn() {
        return showLicenseCandidateFilesColumn;
    }

    /**
     * @return the showProviderColumn
     */
    public final boolean isShowProviderColumn() {
        return showProviderColumn;
    }

    /**
     * @return the scan location
     */
    public final ScanLocation getScanLocation() {
        if (scanDirectory != null) {
            return new ScanLocation(scanDirectory);
        } else if (scanArtifacts != null) {
            try {
                List<File> scanFiles = ArtifactHelper.getDependencies(this, scanArtifacts, scanArtifactScope);
                return new ScanLocation(scanFiles);
            } catch (DependencyResolutionException e) {
                throw new IllegalStateException(e);
            }
        } else {
            return null;
        }
    }

    /**
     * @return the licensesFilename
     */
    public final File getLicensesFilename() {
        return licensesFilename;
    }

    /**
     * @return the providersFilename
     */
    public final File getProvidersFilename() {
        return providersFilename;
    }

    /**
     * @return the noticesFilename
     */
    public final File getNoticesFilename() {
        return noticesFilename;
    }

    /**
     * @return the checkedArchivesFilename
     */
    public final File getCheckedArchivesFilename() {
        return checkedArchivesFilename;
    }

    /**
     * @return the licenseUrlMappingsFilename
     */
    public final File getLicenseUrlMappingsFilename() {
        return licenseUrlMappingsFilename;
    }

    /**
     * @return the licenseNameMappingsFilename
     */
    public final File getLicenseNameMappingsFilename() {
        return licenseNameMappingsFilename;
    }

    /**
     * @return the globalFiltersFilename
     */
    public final File getGlobalFiltersFilename() {
        return globalFiltersFilename;
    }

    /**
     * @return the filteredVendorNamesFilename
     */
    public final File getFilteredVendorNamesFilename() {
        return filteredVendorNamesFilename;
    }

    /**
     * @return the cleanOutputActive
     */
    public final boolean isCleanOutputActive() {
        return cleanOutputActive;
    }

    /**
     * @return the cleanOutputLegalStates
     */
    public final LegalStatus[] getCleanOutputLegalStates() {
        return cleanOutputLegalStates;
    }

    /**
     * @return the cleanOutputLicenseSpdxIdentifiers
     */
    public final String[] getCleanOutputLicenseSpdxIdentifiers() {
        return cleanOutputLicenseSpdxIdentifiers;
    }

    /**
     * @return the filteredVendorNames
     */
    public final List<String> getFilteredVendorNames() {
        return filteredVendorNames;
    }

    /**
     * @return the nexusCentralBaseUrl
     */
    public final String getNexusCentralBaseUrl() {
        return nexusCentralBaseUrl;
    }

    /**
     * @return the connectTimeout
     */
    public final int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @return the validateLicenseXml
     */
    public final boolean isValidateLicenseXml() {
        return validateLicenseXml;
    }

    /**
     * @return the buildName
     */
    public final String getBuildName() {
        return buildName;
    }

    /**
     * @return the buildVersion
     */
    public final String getBuildVersion() {
        return buildVersion;
    }

    /**
     * @return the buildUrl
     */
    public final String getBuildUrl() {
        return buildUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<RemoteRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RepositorySystem getRepositorySystem() {
        return repositorySystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RepositorySystemSession getRepositorySystemSession() {
        return repositorySystemSession;
    }

    /**
     * Obtains the name of the message digest algorithm to use.
     * @return the name of the message digest algorithm to use
     */
    public String getMessageDigestAlgorithm() {
        return messageDigestAlgorithm;
    }

    /**
     * @return the configurationFileEncoding
     */
    public final String getConfigurationFileEncoding() {
        return configurationFileEncoding;
    }

}
