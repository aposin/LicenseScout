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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.execution.ScanLocation;
import org.aposin.licensescout.finder.FinderResult;
import org.aposin.licensescout.license.DetectionStatus;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.model.Provider;
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
        EMPTY_WITHOUT_DOCUMENTATION_URL_UTF_8(false, false, StandardCharsets.UTF_8),
        /**
         * 
         */
        EMPTY_WITH_DOCUMENTATION_URL_UTF_8(false, true, StandardCharsets.UTF_8),
        /**
         * 
         */
        ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8(true, false, StandardCharsets.UTF_8),
        /**
         * 
         */
        ARCHIVE_WITH_DOCUMENTATION_URL_UTF_8(true, true, StandardCharsets.UTF_8),
        /**
         * 
         */
        ARCHIVE_WITH_DOCUMENTATION_URL_UTF_16LE(true, true, StandardCharsets.UTF_16LE),
        /**
         * 
         */
        ARCHIVE_WITH_DOCUMENTATION_URL_UTF_16BE(true, true, StandardCharsets.UTF_16BE);

        private final boolean withArchives;
        private final boolean withDocumentationUrl;
        private final Charset outputCharset;

        /**
         * Constructor.
         * @param withArchives
         * @param withDocumentationUrl
         * @param outputCharset the output encoding
         */
        private TestVariant(boolean withArchives, boolean withDocumentationUrl, final Charset outputCharset) {
            this.withDocumentationUrl = withDocumentationUrl;
            this.withArchives = withArchives;
            this.outputCharset = outputCharset;
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

        /**
         * @return the charset
         */
        public final Charset getOutputCharset() {
            return outputCharset;
        }

    }

    /**
     * Variant of the test case regarding report template.
     */
    protected enum TemplateVariant {

        /**
         * 
         */
        DEFAULT(null, null),
        /**
         * 
         */
        TXT_UTF_8("license_report_txt_UTF-8.vm", StandardCharsets.UTF_8),
        /**
         * 
         */
        TXT_UTF_16BE("license_report_txt_UTF-16BE.vm", StandardCharsets.UTF_16BE),
        /**
         * 
         */
        TXT_UTF_16LE("license_report_txt_UTF-16LE.vm", StandardCharsets.UTF_16LE),
        /**
         * 
         */
        HTML_UTF_8("license_report_html_UTF-8.vm", StandardCharsets.UTF_8),
        /**
         * 
         */
        HTML_UTF_16BE("license_report_html_UTF-16BE.vm", StandardCharsets.UTF_16BE),
        /**
         * 
         */
        HTML_UTF_16LE("license_report_html_UTF-16LE.vm", StandardCharsets.UTF_16LE);

        private final String templatePath;
        private final Charset templateCharset;

        /**
         * Constructor.
         * @param templateFilename 
         * @param templateCharset 
         */
        private TemplateVariant(final String templateFilename, final Charset templateCharset) {
            this.templatePath = (templateFilename == null) ? null : "src/test/resources/templates/" + templateFilename;
            this.templateCharset = templateCharset;

        }

        /**
         * @return the templatePath
         */
        public final String getTemplatePath() {
            return templatePath;
        }

        /**
         * @return the templateCharset
         */
        public final Charset getTemplateCharset() {
            return templateCharset;
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
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportEmptyArchiveListWithoutDocumentationUrl() throws Exception {
        assertExport(TestVariant.EMPTY_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.DEFAULT);
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListWithoutDocumentationUrl() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.DEFAULT);
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListWithDocumentationUrlOutputUtf16LE() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITH_DOCUMENTATION_URL_UTF_16LE, TemplateVariant.DEFAULT);
    }

    /**
     * Test method for {@link IReportExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListWithDocumentationUrlOutputUtf16BE() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITH_DOCUMENTATION_URL_UTF_16BE, TemplateVariant.DEFAULT);
    }

    /**
     * Execute report generation and verify the resulting report content.
     * 
     * @param testVariant
     * @param templateVariant 
     * @throws Exception 
     */
    protected void assertExport(final TestVariant testVariant, TemplateVariant templateVariant) throws Exception {
        final String resultContent = runExporter(testVariant, templateVariant);
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
        final Provider provider = new Provider("Foundation", "Foundation name", "http://www.foundation.org/");
        archive.setProvider(provider);
        return archive;
    }

    /**
     * @param testVariant
     * @param templateVariant
     * @return the content of the resulting report file as a string
     * @throws Exception
     * @throws IOException
     */
    private String runExporter(final TestVariant testVariant, final TemplateVariant templateVariant)
            throws Exception, IOException {
        final List<Archive> archiveFiles = createArchiveList(testVariant);
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
        final ReportConfiguration reportConfiguration = createReportConfiguration(testVariant, templateVariant);
        getExporter().export(outputResult, reportConfiguration);
        return readOutputFileToString(testVariant.getOutputCharset());
    }

    private FinderResult createFinderResult(final List<Archive> archiveFiles) {
        final ScanLocation scanLocation = new ScanLocation(new File("."));
        return new FinderResult(scanLocation, archiveFiles);
    }

    /**
     * @param testVariant
     * @param templateVariant
     * @return a report configuration
     */
    private ReportConfiguration createReportConfiguration(final TestVariant testVariant,
                                                          final TemplateVariant templateVariant) {
        final ReportConfiguration reportConfiguration = new ReportConfiguration();
        final String templatePath = templateVariant.getTemplatePath();
        if (templatePath != null) {
            reportConfiguration.setTemplateFile(new File(templatePath));
        }
        if (templateVariant.getTemplateCharset() != null) {
            reportConfiguration.setTemplateEncoding(templateVariant.getTemplateCharset().name());
        }
        reportConfiguration.setOutputFile(getOutputFile());
        reportConfiguration.setShowDocumentationUrl(testVariant.isWithDocumentationUrl());
        reportConfiguration.setShowLicenseCandidateFilesColumn(false);
        reportConfiguration.setShowMessageDigestColumn(true);
        reportConfiguration.setShowPathColumn(true);
        reportConfiguration.setShowProviderColumn(true);
        reportConfiguration.setOutputEncoding(testVariant.getOutputCharset().name());
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

    /**
     * @param charset
     * @return the content of the output file as string
     * @throws IOException
     */
    private String readOutputFileToString(final Charset charset) throws IOException {
        final File outputFile = getOutputFile();
        try (final FileReader input = new FileReader(outputFile, charset)) {
            return IOUtils.toString(input);
        }
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
