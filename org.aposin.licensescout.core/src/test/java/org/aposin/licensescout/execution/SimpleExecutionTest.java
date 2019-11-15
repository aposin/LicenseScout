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

import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.Output;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.finder.LicenseScoutExecutionException;
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
        final ArrayList<Output> outputs = new ArrayList<>();
        outputs.add(createOutput(OutputFileType.CSV, "licensereport.csv"));
        outputs.add(createOutput(OutputFileType.HTML, "licensereport.html"));
        outputs.add(createOutput(OutputFileType.TXT, "licensereport.txt"));
        final ExecutionParameters executionParameters = createExecutionParameters(ArchiveType.JAVA, scanDirectory,
                outputs);
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

    private void assertExecution(final ExecutionParameters executionParameters) throws LicenseScoutExecutionException {
        final Executor executor = new Executor(executionParameters);
        executor.execute();
    }

    private ExecutionParameters createExecutionParametersNoOutputs(final ArchiveType archiveType,
                                                                     final File scanDirectory) {
        final ArrayList<Output> outputs = new ArrayList<>();
        return createExecutionParameters(archiveType, scanDirectory, outputs);
    }

    private ExecutionParameters createExecutionParameters(final ArchiveType archiveType, final File scanDirectory,
                                                          final ArrayList<Output> outputs) {
        final ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.setArchiveType(archiveType);
        executionParameters.setFilteredVendorNames(new ArrayList<>());
        executionParameters.setScanDirectory(scanDirectory);
        executionParameters.setOutputDirectory(new File("target"));
        executionParameters.setOutputs(outputs);
        executionParameters.setCleanOutputActive(false);
        // NOTE: exception occurs if the following is not set
        executionParameters.setCleanOutputLegalStates(new LegalStatus[0]);
        executionParameters.setLsLog(TestUtil.createJavaUtilGlobalLog());
        executionParameters.setNpmExcludedDirectoryNames(new ArrayList<>());
        return executionParameters;
    }

    private Output createOutput(final OutputFileType outputFileType, final String filename) {
        final Output output = new Output();
        output.setType(outputFileType);
        output.setFilename(filename);
        return output;
    }

}
