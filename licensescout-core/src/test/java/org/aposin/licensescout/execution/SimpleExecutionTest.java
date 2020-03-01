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
import java.util.ArrayList;
import java.util.Arrays;

import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.ConfigFileHandler;
import org.aposin.licensescout.configuration.ConfigFileParameters;
import org.aposin.licensescout.configuration.ExecutionOutput;
import org.aposin.licensescout.configuration.FilesystemConfigFileHandler;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.core.test.util.TestArtifactServerUtil;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.license.LegalStatus;
import org.junit.Test;

/**
 * Unit test for {@link Executor}.
 *
 */
public class SimpleExecutionTest {

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaNoOutputs() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        assertExecution(executionParameters);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaWithOutputs() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final ArrayList<ExecutionOutput> outputs = createTripleOutput();
        final ExecutionParameters executionParameters = createExecutionParameters(ArchiveType.JAVA, scanDirectory,
                outputs);
        assertExecution(executionParameters);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test(expected = LicenseScoutFailOnErrorException.class)
    public void testExecutionJavaFailOnErrorException() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/java-unpacked-license-none");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setFailOnError(true);
        executionParameters.setErrorLegalStates(
                new LegalStatus[] { LegalStatus.UNKNOWN, LegalStatus.CONFLICTING, LegalStatus.NOT_ACCEPTED });
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaFailOnErrorNoException() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/java-unpacked-license-manifest");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setFailOnError(true);
        executionParameters.setErrorLegalStates(
                new LegalStatus[] { LegalStatus.UNKNOWN, LegalStatus.CONFLICTING, LegalStatus.NOT_ACCEPTED });
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaWriteArchiveCsvSkeleton() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/java-unpacked-license-manifest");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setWriteArchiveCsvSkeleton(true);
        executionParameters.setArchiveCsvSkeletonFile(new File("target/archiveSkeleton.csv"));
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaWriteArchiveXmlSkeleton() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/java-unpacked-license-manifest");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setWriteArchiveXmlSkeleton(true);
        executionParameters.setArchiveXmlSkeletonFile(new File("target/archiveSkeleton.xml"));
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaCleanOuput() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/java-unpacked-license-manifest");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setCleanOutputActive(true);
        executionParameters.setCleanOutputLegalStates(new LegalStatus[] { LegalStatus.UNKNOWN });
        executionParameters.setCleanOutputLicenseSpdxIdentifiers(new String[] { "ABC", "EPL-1.0" });
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaReadConfigFiles() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaWriteToDatabaseNoDatabaseConfigured() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setWriteResultsToDatabase(true);
        executionParameters.setWriteResultsToDatabaseForSnapshotBuilds(false);
        executionParameters.setBuildVersion("1.0.0");
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaWriteToDatabaseNoDatabaseConfiguredSnapshotVersionNoWrite() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setWriteResultsToDatabase(true);
        executionParameters.setWriteResultsToDatabaseForSnapshotBuilds(false);
        executionParameters.setBuildVersion("1.0.0-SNAPSHOT");
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionJavaWriteToDatabaseNoDatabaseConfiguredSnapshotVersionWrite() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        executionParameters.setWriteResultsToDatabase(true);
        executionParameters.setWriteResultsToDatabaseForSnapshotBuilds(true);
        executionParameters.setBuildVersion("1.0.0-SNAPSHOT");
        assertExecution(executionParameters, true);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test(expected = LicenseScoutExecutionException.class)
    public void testExecutionJavaNoReportExporterFactory() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/java-unpacked-license-manifest");
        final ArrayList<ExecutionOutput> outputs = createTripleOutput();
        final ExecutionParameters executionParameters = createExecutionParameters(ArchiveType.JAVA, scanDirectory,
                outputs, false);
        assertExecution(executionParameters);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test(expected = LicenseScoutExecutionException.class)
    public void testExecutionJavaNoScanDirectory() throws Exception {
        final File scanDirectory = null;
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        assertExecution(executionParameters);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test(expected = LicenseScoutExecutionException.class)
    public void testExecutionJavaNotExistingScanDirectory() throws Exception {
        final File scanDirectory = new File("not_existing");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVA,
                scanDirectory);
        assertExecution(executionParameters);
    }

    /**
     * Test case for the method {@link Executor#execute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecutionNpmNoOutput() throws Exception {
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final ExecutionParameters executionParameters = createExecutionParametersNoOutputs(ArchiveType.JAVASCRIPT,
                scanDirectory);
        assertExecution(executionParameters);
    }

    private void assertExecution(final ExecutionParameters executionParameters)
            throws BaseLicenseScoutExecutionException {
        assertExecution(executionParameters, false);
    }

    /**
     * @param executionParameters
     * @param withConfigFiles
     * @throws LicenseScoutExecutionException
     * @throws LicenseScoutFailOnErrorException
     */
    private void assertExecution(final ExecutionParameters executionParameters, final boolean withConfigFiles)
            throws LicenseScoutExecutionException, LicenseScoutFailOnErrorException {
        final ConfigFileParameters configFileParameters = new ConfigFileParameters();
        if (withConfigFiles) {
            configFileParameters.setCheckedArchivesFilename(
                    new File("src/test/resources/configuration_sample_test/checkedarchives.csv"));
            configFileParameters.setFilteredVendorNamesFilename(
                    new File("src/test/resources/configuration_sample_test/filteredvendornames.csv"));
            configFileParameters.setGlobalFiltersFilename(
                    new File("src/test/resources/configuration_sample_test/globalfilters.csv"));
            configFileParameters
                    .setLicensesFilename(new File("src/test/resources/configuration_sample_test/licenses.xml"));
            configFileParameters.setLicenseNameMappingsFilename(
                    new File("src/test/resources/configuration_sample_test/namemappings.csv"));
            configFileParameters
                    .setNoticesFilename(new File("src/test/resources/configuration_sample_test/notices.xml"));
            configFileParameters
                    .setProvidersFilename(new File("src/test/resources/configuration_sample_test/providers.xml"));
            configFileParameters.setLicenseUrlMappingsFilename(
                    new File("src/test/resources/configuration_sample_test/urlmappings.csv"));
        }
        final ConfigFileHandler configFileHandler = new FilesystemConfigFileHandler(configFileParameters,
                TestUtil.createTestLog());
        final Executor executor = new Executor(executionParameters, configFileHandler);
        executor.execute();
    }

    /**
     * @return a list of three output definitions
     */
    private ArrayList<ExecutionOutput> createTripleOutput() {
        final ArrayList<ExecutionOutput> outputs = new ArrayList<>();
        outputs.add(createOutput(OutputFileType.CSV, "licensereport.csv"));
        outputs.add(createOutput(OutputFileType.HTML, "licensereport.html"));
        outputs.add(createOutput(OutputFileType.TXT, "licensereport.txt"));
        return outputs;
    }

    private ExecutionParameters createExecutionParametersNoOutputs(final ArchiveType archiveType,
                                                                   final File scanDirectory) {
        final ArrayList<ExecutionOutput> outputs = new ArrayList<>();
        return createExecutionParameters(archiveType, scanDirectory, outputs);
    }

    private ExecutionParameters createExecutionParameters(final ArchiveType archiveType, final File scanDirectory,
                                                          final ArrayList<ExecutionOutput> outputs) {
        return createExecutionParameters(archiveType, scanDirectory, outputs, true);
    }

    /**
     * @param archiveType
     * @param scanDirectory
     * @param outputs
     * @param withStandardReportExporterFactory
     * @return an execution parameters instance
     */
    private ExecutionParameters createExecutionParameters(final ArchiveType archiveType, final File scanDirectory,
                                                          final ArrayList<ExecutionOutput> outputs,
                                                          final boolean withStandardReportExporterFactory) {
        final ScanLocation scanLocation = new ScanLocation(scanDirectory);
        final ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.setArchiveType(archiveType);
        executionParameters.setFilteredVendorNames(new ArrayList<>());
        executionParameters.setScanLocation(scanLocation);
        executionParameters.setOutputDirectory(new File("target"));
        executionParameters.setOutputs(outputs);
        executionParameters.setCleanOutputActive(false);
        // NOTE: exception occurs if the following is not set
        executionParameters.setCleanOutputLegalStates(new LegalStatus[0]);
        executionParameters.setLsLog(TestUtil.createTestLog());
        executionParameters.setNpmExcludedDirectoryNames(new ArrayList<>());
        if (withStandardReportExporterFactory) {
            executionParameters.setExporterFactories(Arrays.asList(new StandardReportExporterFactory()));
        }
        executionParameters.setValidateLicenseXml(false);
        executionParameters.setShowDocumentationUrl(false);
        executionParameters.setArtifactServerUtil(new TestArtifactServerUtil());
        return executionParameters;
    }

    private ExecutionOutput createOutput(final OutputFileType outputFileType, final String filename) {
        final ExecutionOutput output = new ExecutionOutput();
        output.setType(outputFileType);
        output.setFilename(filename);
        return output;
    }

}
