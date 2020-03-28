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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.license.License;

/**
 * Exporter creating CSV files containing only the list of archives.
 * 
 * <p>This exporter writes a single line for each found archive. The line contains:</p>
 * <ol>
 * <li>file name</li>
 * <li>version (may be an empty string)</li>
 * <li>Message digest (may be an empty string)</li>
 * <li>detection status</li>
 * <li>legal status</li>
 * <li>file path</li>
 * <li>list of licenses identifiers (maybe empty)</li>
 * </ol>
 * 
 * <p>Note that depending on the number of licenses associated with an archive the number of fields in a line may vary.
 * SPDX identifiers are used as license names</p>
 */
public class CsvExporter implements IReportExporter {

    private static final IReportExporter INSTANCE = new CsvExporter();

    /**
     * Gets the single instance of Exporter.
     * 
     * @return single instance of Exporter
     */
    public static IReportExporter getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputFileType getOutputFileType() {
        return OutputFileType.CSV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void export(final OutputResult outputResult, final ReportConfiguration reportConfiguration)
            throws Exception {
        final Charset charset = ExporterUtil.getOutputCharset(reportConfiguration);
        final List<Archive> archiveFiles = outputResult.getFinderResult().getArchiveFiles();
        try (final FileWriter fileWriter = new FileWriter(reportConfiguration.getOutputFile(), charset);
                final BufferedWriter bw = new BufferedWriter(fileWriter)) {

            writeHeader(outputResult, bw, reportConfiguration);
            Collections.sort(archiveFiles);

            for (final Archive archive : archiveFiles) {
                write(archive, bw, reportConfiguration);
            }
        }
    }

    private static void write(final Archive archive, final Writer writer, final ReportConfiguration reportConfiguration)
            throws IOException {
        final StringBuilder buffer = new StringBuilder();
        addQuotedString(buffer, archive.getArchiveType().toString());
        buffer.append(',');
        addQuotedString(buffer, archive.getFileName());
        buffer.append(',');
        addQuotedString(buffer, archive.getVersion());
        buffer.append(',');
        addQuotedString(buffer, archive.getMessageDigestString());
        buffer.append(',');
        addQuotedString(buffer, archive.getDetectionStatus().name());
        buffer.append(',');
        addQuotedString(buffer, archive.getLegalStatus().name());
        buffer.append(',');
        addQuotedString(buffer, archive.getPath());
        if (reportConfiguration.isShowDocumentationUrl()) {
            buffer.append(',');
            final String documentationUrl = archive.getDocumentationUrl();
            addQuotedString(buffer, documentationUrl);
        }
        final Set<License> licensesSet = archive.getResultingLicenses();
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
            for (final License license : licensesList) {
                buffer.append(',');
                addQuotedString(buffer, license.getSpdxIdentifier());
            }
        }
        writer.write(buffer.toString());
        writer.write(System.lineSeparator());
    }

    private static void writeHeader(final OutputResult outputResult, final Writer writer,
                                    final ReportConfiguration reportConfiguration)
            throws IOException {
        final StringBuilder buffer = new StringBuilder();
        addQuotedString(buffer, "Type");
        buffer.append(',');
        addQuotedString(buffer, "Filename");
        buffer.append(',');
        addQuotedString(buffer, "Version");
        buffer.append(',');
        addQuotedString(buffer, "Message Digest (" + outputResult.getMessageDigestAlgorithm() + ")");
        buffer.append(',');
        addQuotedString(buffer, "Detection status");
        buffer.append(',');
        addQuotedString(buffer, "Legal status");
        buffer.append(',');
        addQuotedString(buffer, "Archive path");
        if (reportConfiguration.isShowDocumentationUrl()) {
            buffer.append(',');
            addQuotedString(buffer, "Documentation");
        }
        final String[] licenseHeaders = new String[] { "License 1", "License 2", "License 3", "License 4" };
        for (final String licenseHeader : licenseHeaders) {
            buffer.append(',');
            addQuotedString(buffer, licenseHeader);
        }
        writer.write(buffer.toString());
        writer.write(System.lineSeparator());
    }

    /**
     * @param buffer the buffer to write to
     * @param text the text to output. May be null - in this case an empty string is output
     */
    private static void addQuotedString(final StringBuilder buffer, final String text) {
        buffer.append('"');
        if (text != null) {
            buffer.append(text);
        }
        buffer.append('"');
    }
}
