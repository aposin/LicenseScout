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
 * Test case for {@link TxtExporter}.
 */
public class TxtExporterTest extends AbstractStringCompareExporterTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IReportExporter createExporter() {
        return TxtExporter.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OutputFileType getExpectedOutputFileType() {
        return OutputFileType.TXT;
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListCustomTemplateUtf8() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.TXT_UTF_8);
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListCustomTemplateUtf16BE() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.TXT_UTF_16BE);
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListCustomTemplateUtf16LE() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.TXT_UTF_16LE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getOutputFilename() {
        return "licensereport.txt";
    }

    @Override
    protected String getReferenceContent(final TestVariant testVariant) {
        final String nl = "\n";
        final StringBuffer referenceContent = new StringBuffer();
        referenceContent.append(nl + "License Report");
        referenceContent.append(nl + nl);
        referenceContent.append("In the following sections you can find " + nl);
        referenceContent.append("- List of used/linked software projects" + nl);
        referenceContent.append("- Notices to the used artifacts" + nl);
        referenceContent.append("- List of used artifacts with license info and vendor plugin names" + nl);
        referenceContent.append("- License texts" + nl);
        referenceContent.append(nl + nl + nl);
        referenceContent.append("====================================================================" + nl);
        referenceContent.append(nl);
        referenceContent.append("This project contains software from the following providers:" + nl);
        referenceContent.append(nl);
        referenceContent.append("====================================================================" + nl);
        referenceContent.append("Notices" + nl);
        referenceContent.append("====================================================================" + nl);
        referenceContent.append(nl + nl);
        referenceContent.append("====================================================================" + nl);
        referenceContent.append("Artifacts by Licenses" + nl);
        referenceContent.append("====================================================================" + nl);
        referenceContent.append(nl);
        if (testVariant.isWithArchives()) {
            referenceContent.append("name11 Version version" + nl);
            referenceContent.append(nl);
            referenceContent.append(nl);
            referenceContent.append("* fileName01" + nl);
            referenceContent.append(nl);
            referenceContent.append(nl);
            referenceContent.append(nl);
        }
        referenceContent.append(nl + nl + nl);

        referenceContent.append("====================================================================" + nl);
        referenceContent.append("License texts" + nl);
        referenceContent.append("====================================================================" + nl);
        referenceContent.append(nl + nl);
        if (testVariant.isWithArchives()) {
            referenceContent.append("name11 Version version" + nl);
            referenceContent.append(nl + nl);
            referenceContent.append("text" + nl);
            referenceContent.append(nl);
        }
        return referenceContent.toString();
    }

}
