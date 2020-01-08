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
package org.aposin.licensescout.maven.utils;

import java.io.File;

import org.apache.maven.plugins.annotations.Parameter;
import org.aposin.licensescout.configuration.OutputFileType;

/**
 * Output specification for scan MOJOs.
 * 
 * <p>This is an auxiliary class for the Maven configuration of the license scout.</p>
 * 
 */
public class Output {

    /**
     * Type of the output file.
     */
    @Parameter(property = "type", required = true)
    private OutputFileType type;

    /**
     * Name of the output file (will be combined with output directory).
     * 
     * <p>If the configured filename has no extension, the default extension of the {@link OutputFileType} is appended.<p>
     */
    @Parameter(property = "filename", required = false, defaultValue = "licensereport")
    private String filename;

    /**
     * URL of the report file on artifact server.
     * 
     * <p>This information is only written to the database for information. If not configured, a default value is calculated (see {@link IArtifactUrlBuilder}).</p>
     * 
     * <p>Only used if {@code AbstractScanMojo#writeResultsToDatabase} is true.</p>
     */
    @Parameter(property = "url", required = false)
    private String url;

    /**
     * Path of a template file (optional).
     * 
     * <p>Only used for types {@link OutputFileType#TXT} and
     * {@link OutputFileType#HTML}.</p>
     */
    @Parameter(property = "template", required = false)
    private File template;

    /**
     * Encoding of the output file.
     * 
     * <p>A JAVA encoding name to use for reading the template file.
     * If not configured, the value of 'project.build.sourceEncoding' is used.
     * If this is not configured, too, the platform specific default encoding is used.</p>
     */
    @Parameter(property = "templateEncoding", required = false, defaultValue = "${project.build.sourceEncoding}")
    private String templateEncoding;

    /**
     * Encoding of the output file.
     * 
     * <p>A JAVA encoding name to use for the output file.
     * If not configured, the value of 'project.reporting.outputEncoding' is used.
     * If this is not configured, too, the platform specific default encoding is used.</p>
     */
    @Parameter(property = "encoding", required = false, defaultValue = "${project.reporting.outputEncoding}")
    private String outputEncoding;

    /**
     * Constructor for use by the Maven runtime system.
     */
    public Output() {
        // EMPTY
    }

    /**
     * Constructor for testing.
     * 
     * @param type
     * @param filename
     * @param url
     * @param template
     * @param templateEncoding
     * @param outputEncoding
     */
    public Output(OutputFileType type, String filename, String url, File template, String templateEncoding,
            String outputEncoding) {
        this.type = type;
        this.filename = filename;
        this.url = url;
        this.template = template;
        this.templateEncoding = templateEncoding;
        this.outputEncoding = outputEncoding;
    }

    /**
     * @return the type
     */
    public final OutputFileType getType() {
        return type;
    }

    /**
     * @return the filename
     */
    public final String getFilename() {
        return filename;
    }

    /**
     * @return the url
     */
    public final String getUrl() {
        return url;
    }

    /**
     * @return the template
     */
    public final File getTemplate() {
        return template;
    }

    /**
     * @return the templateEncoding
     */
    public final String getTemplateEncoding() {
        return templateEncoding;
    }

    /**
     * @return the outputEncoding
     */
    public final String getOutputEncoding() {
        return outputEncoding;
    }

}
