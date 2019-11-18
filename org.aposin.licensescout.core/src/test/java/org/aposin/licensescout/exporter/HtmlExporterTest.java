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

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.aposin.licensescout.configuration.OutputFileType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link HtmlExporter}.
 */
public class HtmlExporterTest extends AbstractExporterTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IReportExporter createExporter() {
        return HtmlExporter.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OutputFileType getExpectedOutputFileType() {
        return OutputFileType.HTML;
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListCustomTemplateUtf8() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.HTML_UTF_8);
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListCustomTemplateUtf16BE() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.HTML_UTF_16BE);
    }

    /**
     * Test method for {@link IReportExporter#export(OutputResult, ReportConfiguration)}.
     * @throws Exception 
     */
    @Test
    public void testExportWithArchiveListCustomTemplateUtf16LE() throws Exception {
        assertExport(TestVariant.ARCHIVE_WITHOUT_DOCUMENTATION_URL_UTF_8, TemplateVariant.HTML_UTF_16LE);
    }

    @Override
    protected void assertResultContent(TestVariant testVariant, final String resultContent) {
        final Document doc = Jsoup.parse(resultContent);
        final Elements metaElements = doc.getElementsByTag("meta");
        final Iterator<Element> iter = metaElements.iterator();
        while (iter.hasNext()) {
            final Element element = iter.next();
            final Attributes attributes = element.attributes();
            if (attributes.get("http-equiv") != null) {
                final String contentAttribute = attributes.get("content");
                final String expected = "charset=" + testVariant.getOutputCharset().name();
                Assert.assertTrue("Encoding", contentAttribute.endsWith(expected));
            }
        }
        Assert.assertNotNull("Detection statistics table present", doc.getElementById("detection_statistics_table"));
        Assert.assertNotNull("Legal statistics table present", doc.getElementById("legal_statistics_table"));
        Assert.assertNotNull("Genral statistics table present", doc.getElementById("general_statistics_table"));
        Assert.assertNotNull("Main table present", doc.getElementById("license_table"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getOutputFilename() {
        return "licensereport.html";
    }

}
