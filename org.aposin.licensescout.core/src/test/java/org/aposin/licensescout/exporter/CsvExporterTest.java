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

import org.aposin.licensescout.configuration.OutputFileType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link CsvExporter}.
 */
public class CsvExporterTest extends AbstractExporterTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        exporter = CsvExporter.getInstance();
    }

    protected OutputFileType getExpectedOutputFileType() {
        return OutputFileType.CSV;
    }

    /**
     * Test method for {@link org.aposin.licensescout.exporter.CsvExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     */
    @Test
    public void testExportEmptyArchiveListWithoutDocumentationUrl() throws Exception {
        final String messageDigestAlgorithm = "ABC";
        final String resultContent = runExporterEmptyArchiveList(messageDigestAlgorithm, false);
        final String referenceContent = getReferenceHeader(messageDigestAlgorithm, false);
        Assert.assertEquals("CSV output file contents", referenceContent, resultContent);
    }

    /**
     * Test method for {@link org.aposin.licensescout.exporter.CsvExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     */
    @Test
    public void testExportEmptyArchiveListWithDocumentationUrl() throws Exception {
        final String messageDigestAlgorithm = "ABC";
        final String resultContent = runExporterEmptyArchiveList(messageDigestAlgorithm, true);
        final String referenceContent = getReferenceHeader(messageDigestAlgorithm, true);
        Assert.assertEquals("CSV output file contents", referenceContent, resultContent);
    }

    /**
     * Test method for {@link org.aposin.licensescout.exporter.CsvExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     */
    @Test
    public void testExportWithArchiveListWithoutDocumentationUrl() throws Exception {
        final String messageDigestAlgorithm = "ABC";
        final String resultContent = runExporterWithArchiveList(messageDigestAlgorithm, false);
        final String referenceContent = getReferenceHeader(messageDigestAlgorithm, false)
                + "\"JAVA\",\"fileName01\",\"version01\",\"\",\"DETECTED\",\"CONFLICTING\",\"path01\",\"spdxIdentifier11\""
                + getNl();
        Assert.assertEquals("CSV output file contents", referenceContent, resultContent);
    }

    private String getReferenceHeader(final String messageDigestAlgorithm, final boolean withDocumentationUrl) {
        final String nl = getNl();
        final String referenceContent = "\"Type\",\"Filename\",\"Version\",\"Message Digest (" + messageDigestAlgorithm
                + ")\",\"Detection status\",\"Legal status\",\"Archive path\","
                + (withDocumentationUrl ? "\"Documentation\"," : "")
                + "\"License 1\",\"License 2\",\"License 3\",\"License 4\"" + nl;
        return referenceContent;
    }

}
