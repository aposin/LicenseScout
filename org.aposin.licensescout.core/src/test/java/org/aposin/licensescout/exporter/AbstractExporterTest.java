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
import java.io.FileNotFoundException;
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

    protected IReportExporter exporter;

    /**
     * @return the exporter
     */
    protected final IReportExporter getExporter() {
        return exporter;
    }

    /**
     * Test method for {@link org.aposin.licensescout.exporter.CsvExporter#getOutputFileType()}.
     */
    @Test
    public void testGetOutputFileType() {
        Assert.assertEquals("outputFileType", getExpectedOutputFileType(), getExporter().getOutputFileType());
    }

    protected abstract OutputFileType getExpectedOutputFileType();

    protected String runExporterEmptyArchiveList(final String messageDigestAlgorithm,
                                                 final boolean showDocumentationUrl)
            throws Exception, IOException, FileNotFoundException {
        final List<Archive> archiveFiles = new ArrayList<Archive>();
        return runExporter(messageDigestAlgorithm, archiveFiles, showDocumentationUrl);
    }

    protected String runExporterWithArchiveList(final String messageDigestAlgorithm, final boolean showDocumentationUrl)
            throws Exception, IOException, FileNotFoundException {
        final List<Archive> archiveFiles = createArchiveList();
        return runExporter(messageDigestAlgorithm, archiveFiles, showDocumentationUrl);
    }

    protected List<Archive> createArchiveList() {
        final List<Archive> archiveFiles = new ArrayList<>();
        final Archive archive = createArchive();
        archiveFiles.add(archive);
        return archiveFiles;
    }

    private Archive createArchive() {
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName01", "version01", "path01");
        archive.setDetectionStatus(DetectionStatus.DETECTED);
        archive.setLegalStatus(LegalStatus.CONFLICTING);
        License license = new License("spdxIdentifier11", "name11", LegalStatus.UNKNOWN, "author", "version",
                "urlPublic", "text", null);
        archive.addLicense(license, "licensepath");
        return archive;
    }

    private String runExporter(final String messageDigestAlgorithm, final List<Archive> archiveFiles,
                               final boolean showDocumentationUrl)
            throws Exception, IOException, FileNotFoundException {
        final OutputResult outputResult = new OutputResult();
        outputResult.setMessageDigestAlgorithm(messageDigestAlgorithm);
        final File scanDirectory = new File(".");
        final File outputFile = Paths.get("target", "output.out").toFile();
        final FinderResult finderResult = new FinderResult(scanDirectory, archiveFiles);
        outputResult.setFinderResult(finderResult);
        final ReportConfiguration reportConfiguration = new ReportConfiguration();
        reportConfiguration.setOutputFile(outputFile);
        reportConfiguration.setShowDocumentationUrl(showDocumentationUrl);
        getExporter().export(outputResult, reportConfiguration);

        return IOUtils.toString(new FileReader(outputFile));
    }

    protected static final String getNl() {
        return System.lineSeparator();
    }

}
