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
import org.junit.Test;

/**
 * Test case for {@link CsvExporter}.
 */
public class CsvExporterTest extends AbstractStringCompareExporterTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IReportExporter createExporter() {
        return CsvExporter.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OutputFileType getExpectedOutputFileType() {
        return OutputFileType.CSV;
    }

    /**
     * Test method for {@link IReportExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportEmptyArchiveListWithDocumentationUrl() throws Exception {
        assertExport(TestVariant.EMPTY_WITH_DOCUMENTATION_URL);
    }

    /**
     * Test method for {@link IReportExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListWithDocumentationUrl() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITH_DOCUMENTATION_URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getOutputFilename() {
        return "licensereport.csv";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getReferenceContent(final TestVariant testVariant) {
        final StringBuffer refContent = new StringBuffer();
        refContent.append(getReferenceHeader(testVariant.isWithDocumentationUrl()));
        if (testVariant.isWithArchives()) {
            refContent.append("\"JAVA\",\"fileName01\",\"version01\",\"\",\"DETECTED\",\"CONFLICTING\",\"path01\",");
            if (testVariant.isWithDocumentationUrl()) {
                refContent.append("\"docUrl01\",");
            }
            refContent.append("\"spdxIdentifier11\"");
            refContent.append(getNl());
        }
        return refContent.toString();
    }

    private String getReferenceHeader(final boolean withDocumentationUrl) {
        final String messageDigestAlgorithm = getMessageDigestAlgorithm();
        final String nl = getNl();
        final String referenceContent = "\"Type\",\"Filename\",\"Version\",\"Message Digest (" + messageDigestAlgorithm
                + ")\",\"Detection status\",\"Legal status\",\"Archive path\","
                + (withDocumentationUrl ? "\"Documentation\"," : "")
                + "\"License 1\",\"License 2\",\"License 3\",\"License 4\"" + nl;
        return referenceContent;
    }

}
