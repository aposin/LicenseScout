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
package org.aposin.licensescout.exporter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.finder.FinderResult;
import org.aposin.licensescout.license.DetectionStatus;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link HtmlExporter}.
 */
public abstract class AbstractExporterTest {

    /**
     * Variant of the test case.
     */
    protected enum TestVariant {
        /**
         * 
         */
        EMPTY_WITHOUT_DOCUMENTATION_URL(false, false),
        /**
         * 
         */
        EMPTY_WITH_DOCUMENTATION_URL(false, true),
        /**
         * 
         */
        ARCHIVE_WITHOUT_DOCUMENTATION_URL(true, false),
        /**
         * 
         */
        ARCHIVE_WITH_DOCUMENTATION_URL(true, true);

        private final boolean withArchives;
        private final boolean withDocumentationUrl;

        /**
         * Constructor.
         * @param withArchives
         * @param withDocumentationUrl
         */
        private TestVariant(boolean withArchives, boolean withDocumentationUrl) {
            this.withDocumentationUrl = withDocumentationUrl;
            this.withArchives = withArchives;
        }

        /**
         * @return the withDocumentationUrl
         */
        public final boolean isWithDocumentationUrl() {
            return withDocumentationUrl;
        }

        /**
         * @return the withArchives
         */
        public final boolean isWithArchives() {
            return withArchives;
        }

    }

    private IReportExporter exporter;

    /**
     * Creates the exporter instance.
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        exporter = createExporter();
    }

    /**
     * Creates the exporter instance.
     * @return the exporter instance
     */
    protected abstract IReportExporter createExporter();

    /**
     * @return the exporter
     */
    protected final IReportExporter getExporter() {
        return exporter;
    }

    /**
     * Test method for {@link IReportExporter#getOutputFileType()}.
     */
    @Test
    public void testGetOutputFileType() {
        Assert.assertEquals("outputFileType", getExpectedOutputFileType(), getExporter().getOutputFileType());
    }

    /**
     * Obtains the expected output file type.
     * @return the expected output file type
     */
    protected abstract OutputFileType getExpectedOutputFileType();

    /**
     * Test method for {@link IReportExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     */
    @Test
    public void testExportEmptyArchiveListWithoutDocumentationUrl() throws Exception {
        assertExport(TestVariant.EMPTY_WITHOUT_DOCUMENTATION_URL);
    }

    /**
     * Test method for {@link IReportExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     */
    @Test
    public void testExportWithArchiveListWithoutDocumentationUrl() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL);
    }

    /**
     * Execute report generation and verify the resulting report content.
     * 
     * @param testVariant
     * @throws Exception
     */
    protected void assertExport(final TestVariant testVariant) throws Exception {
        final List<Archive> archiveFiles = createArchiveList(testVariant);
        final String resultContent = runExporter(archiveFiles, testVariant.isWithDocumentationUrl());
        assertResultContent(testVariant, resultContent);
    }

    private List<Archive> createArchiveList(TestVariant testVariant) {
        final List<Archive> archiveFiles = new ArrayList<>();
        if (testVariant.isWithArchives()) {
            final Archive archive = createArchive();
            archiveFiles.add(archive);
        }
        return archiveFiles;
    }

    private Archive createArchive() {
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName01", "version01", "path01");
        archive.setDetectionStatus(DetectionStatus.DETECTED);
        archive.setLegalStatus(LegalStatus.CONFLICTING);
        License license = new License("spdxIdentifier11", "name11", LegalStatus.UNKNOWN, "author", "version",
                "urlPublic", "text", null);
        archive.addLicense(license, "licensepath");
        archive.setDocumentationUrl("docUrl01");
        return archive;
    }

    private String runExporter(final List<Archive> archiveFiles, final boolean showDocumentationUrl)
            throws Exception, IOException {
        final FinderResult finderResult = createFinderResult(archiveFiles);
        final OutputResult outputResult = new OutputResult();
        outputResult.setMessageDigestAlgorithm(getMessageDigestAlgorithm());
        outputResult.setDetectionStatusStatistics(new DetectionStatusStatistics());
        outputResult.setLegalStatusStatistics(new LegalStatusStatistics());
        final GeneralStatistics generalStatistics = new GeneralStatistics();
        generalStatistics.setCandidateLicenseFileCount(1001);
        generalStatistics.setTotalArchiveCount(201);
        outputResult.setGeneralStatistics(generalStatistics);
        outputResult.setFinderResult(finderResult);
        outputResult.setPomResolutionUsed(false);
        final ReportConfiguration reportConfiguration = createReportConfiguration(showDocumentationUrl);
        getExporter().export(outputResult, reportConfiguration);
    
        return readOutputFileToString();
    }

    private FinderResult createFinderResult(final List<Archive> archiveFiles) {
        final File scanDirectory = new File(".");
        return new FinderResult(scanDirectory, archiveFiles);
    }

    private ReportConfiguration createReportConfiguration(final boolean showDocumentationUrl) {
        final ReportConfiguration reportConfiguration = new ReportConfiguration();
        reportConfiguration.setOutputFile(getOutputFile());
        reportConfiguration.setShowDocumentationUrl(showDocumentationUrl);
        reportConfiguration.setShowLicenseCandidateFilesColumn(false);
        reportConfiguration.setShowMessageDigestColumn(true);
        reportConfiguration.setShowPathColumn(true);
        return reportConfiguration;
    }

    private File getOutputFile() {
        return Paths.get("target", getOutputFilename()).toFile();
    }

    /**
     * Obtains the filename for the report to write to and read from in the test case.
     * @return the filename for the report
     */
    protected abstract String getOutputFilename();

    /**
     * Verify the result content of the report.
     * 
     * @param testVariant
     * @param resultContent
     */
    protected abstract void assertResultContent(TestVariant testVariant, String resultContent);

    private String readOutputFileToString() throws IOException {
        final File outputFile = getOutputFile();
        return IOUtils.toString(new FileReader(outputFile));
    }

    /**
     * Obtains the name of the message digest algorithm.
     * @return the name of the message digest algorithm
     */
    protected static String getMessageDigestAlgorithm() {
        return "ABC";
    }

    /**
     * Obtains the system character newline sequence.
     * @return the system newline character sequence
     */
    protected static final String getNl() {
        return System.lineSeparator();
    }

}
