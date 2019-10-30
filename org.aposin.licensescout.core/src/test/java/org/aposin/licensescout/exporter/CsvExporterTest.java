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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.finder.FinderResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class CsvExporterTest {

    private IReportExporter exporter;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        exporter = CsvExporter.getInstance();
    }

    /**
     * Test method for {@link org.aposin.licensescout.exporter.CsvExporter#getOutputFileType()}.
     */
    @Test
    public void testGetOutputFileType() {
        Assert.assertEquals("outputFileType", OutputFileType.CSV, exporter.getOutputFileType());
    }

    /**
     * Test method for {@link org.aposin.licensescout.exporter.CsvExporter#export(org.aposin.licensescout.exporter.OutputResult, org.aposin.licensescout.exporter.ReportConfiguration)}.
     */
    @Test
    public void testExportEmptyArchiveList() throws Exception {
        final OutputResult outputResult = new OutputResult();
        final String messageDigestAlgorithm = "ABC";
        outputResult.setMessageDigestAlgorithm(messageDigestAlgorithm);
        final File scanDirectory = new File(".");
        final File outputFile = Paths.get("target", "output.csv").toFile();
        final List<Archive> archiveFiles = new ArrayList<Archive>();
        final FinderResult finderResult = new FinderResult(scanDirectory, archiveFiles);
        outputResult.setFinderResult(finderResult);
        outputResult.setPomResolutionUsed(true);
        final ReportConfiguration reportConfiguration = new ReportConfiguration();
        reportConfiguration.setOutputFile(outputFile);
        exporter.export(outputResult, reportConfiguration);

        final String resultContent = IOUtils.toString(new FileReader(outputFile));
        final String nl = System.lineSeparator();
        final String referenceContent = "\"Type\",\"Filename\",\"Version\",\"Message Digest (" + messageDigestAlgorithm
                + ")\",\"Detection status\",\"Legal status\",\"Archive path\",\"License 1\",\"License 2\",\"License 3\",\"License 4\""
                + nl;
        Assert.assertEquals("CSV output file contents", referenceContent, resultContent);
    }

}
