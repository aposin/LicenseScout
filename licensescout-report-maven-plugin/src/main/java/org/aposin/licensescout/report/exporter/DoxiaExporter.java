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
package org.aposin.licensescout.report.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.maven.doxia.sink.Sink;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.exporter.IReportExporter;
import org.aposin.licensescout.exporter.OutputResult;
import org.aposin.licensescout.exporter.ReportConfiguration;
import org.aposin.licensescout.license.License;

/**
 * Report exporter creating a license report using the doxia framework.
 * 
 * @see <a href="https://maven.apache.org/doxia/doxia/doxia-sink-api/">Doxia Sink API</a>
 */
public class DoxiaExporter implements IReportExporter {

    private Sink sink;

    /**
     * Gets an instance of Exporter.
     * @param sinkProvider 
     * 
     * @return an exporter instance
     */
    public static IReportExporter getInstance(final ISinkProvider sinkProvider) {
        return new DoxiaExporter(sinkProvider);
    }

    /**
     * Constructor.
     * @param sinkProvider
     */
    private DoxiaExporter(final ISinkProvider sinkProvider) {
        this.sink = sinkProvider.getSink();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputFileType getOutputFileType() {
        return OutputFileType.DOXIA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void export(final OutputResult outputResult, final ReportConfiguration reportConfiguration)
            throws Exception {
        final List<Archive> archiveFiles = outputResult.getFinderResult().getArchiveFiles();
        try {
            createHeader();

            createBody(outputResult, reportConfiguration, archiveFiles);
            sink.flush();
        } finally {
            sink.close();
        }
    }

    /**
     * @param sink
     */
    private void createHeader() {
        sink.head();

        sink.title();
        sink.text("License report");
        sink.title_();

        sink.head_();
    }

    /**
     * @param outputResult
     * @param reportConfiguration
     * @param archiveFiles
     * @throws IOException
     */
    private void createBody(final OutputResult outputResult, final ReportConfiguration reportConfiguration,
                            final List<Archive> archiveFiles)
            throws IOException {
        sink.body();

        sink.paragraph();
        sink.text("Report with licenses found on libraries.");
        sink.paragraph_();

        sink.paragraph();
        sink.text("Scanned directory: " + outputResult.getFinderResult().getScanDirectory());
        sink.paragraph_();
        if (!outputResult.isPomResolutionUsed()) {
            sink.paragraph();
            sink.text("Parent POM resolution was not active. Results may be incomplete.");
            sink.paragraph_();
        }

        sink.table();
        createTableHeader(outputResult, reportConfiguration);
        Collections.sort(archiveFiles);

        for (final Archive archive : archiveFiles) {
            createTableRow(archive, reportConfiguration);
        }
        sink.table_();
        sink.body_();
    }

    private void createTableHeader(final OutputResult outputResult, final ReportConfiguration reportConfiguration) {
        sink.tableRow();
        createTableHeaderCell("Type");
        createTableHeaderCell("Filename");
        createTableHeaderCell("Version");
        createTableHeaderCell("Detection status");
        createTableHeaderCell("Legal status");
        createTableHeaderCell("Archive path");
        if (reportConfiguration.isShowDocumentationUrlColumn()) {
            createTableHeaderCell("Documentation");
        }
        createTableHeaderCell("Licenses");
        createTableHeaderCell("Message Digest (" + outputResult.getMessageDigestAlgorithm() + ")");
        sink.tableRow_();
    }

    private void createTableRow(final Archive archive, final ReportConfiguration reportConfiguration) {
        sink.tableRow();

        createTableCell(archive.getArchiveType().toString());
        createTableCell(archive.getFileName());
        createTableCell(archive.getVersion());
        createTableCell(archive.getDetectionStatus().name());
        createTableCell(archive.getLegalStatus().name());
        createTableCell(archive.getPath());
        if (reportConfiguration.isShowDocumentationUrlColumn()) {
            final String documentationUrl = archive.getDocumentationUrl();
            createTableCell(documentationUrl);
        }
        final Set<License> licensesSet = archive.getLicenses();
        sink.tableCell();
        if (!licensesSet.isEmpty()) {
            // sort by SPDX identifier so that we get a unique order for archives that appear multiple times
            final List<License> licensesList = new ArrayList<>(licensesSet);
            Collections.sort(licensesList, new Comparator<License>() { // NOSONAR

                /** {@inheritDoc} */
                @Override
                public int compare(final License o1, final License o2) {
                    return o1.getSpdxIdentifier().compareTo(o2.getSpdxIdentifier());
                }
            });
            sink.list();
            for (final License license : licensesList) {
                sink.listItem();
                sink.text(license.getSpdxIdentifier());
                sink.listItem_();
            }
            sink.list_();
        }
        sink.tableCell_();
        createTableCell(archive.getMessageDigestString());
        sink.tableRow_();
    }

    /**
     * @param text
     */
    private void createTableHeaderCell(String text) {
        sink.tableHeaderCell();
        sink.text(text);
        sink.tableHeaderCell_();
    }

    /**
     * @param text
     */
    private void createTableCell(String text) {
        sink.tableCell();
        sink.text(text);
        sink.tableCell_();
    }

}
