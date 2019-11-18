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
package org.aposin.licensescout.configuration;

import java.io.File;

import org.apache.maven.plugins.annotations.Parameter;
import org.aposin.licensescout.mojo.AbstractScanMojo;

/**
 * Output specification for {@link AbstractScanMojo}.
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
     */
    @Parameter(property = "filename", required = true)
    private String filename;

    /**
     * URL of the output file on Nexus.
     * 
     * <p>Only used if {@link AbstractScanMojo#writeResultsToDatabase} is true.</p>
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
     * @return the type
     */
    public final OutputFileType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public final void setType(OutputFileType type) {
        this.type = type;
    }

    /**
     * @return the filename
     */
    public final String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public final void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the url
     */
    public final String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public final void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the template
     */
    public final File getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public final void setTemplate(final File template) {
        this.template = template;
    }

    /**
     * @return the templateEncoding
     */
    public final String getTemplateEncoding() {
        return templateEncoding;
    }

    /**
     * @param templateEncoding the templateEncoding to set
     */
    public final void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = templateEncoding;
    }

    /**
     * @return the outputEncoding
     */
    public final String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * @param outputEncoding the outputEncoding to set
     */
    public final void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

}
