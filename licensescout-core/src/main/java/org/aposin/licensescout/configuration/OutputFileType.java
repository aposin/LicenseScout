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

import org.aposin.licensescout.execution.IReportExporterFactory;
import org.aposin.licensescout.exporter.IReportExporter;

/**
 * Type of output file.
 * 
 * <p>Reports of the license scout can be generated in different formats. This enumeration specifies the output format type.</p>
 * 
 * @see ExecutionOutput
 * @see IReportExporterFactory#getReportExporter(OutputFileType)
 * @see IReportExporter#getOutputFileType()
 */
public enum OutputFileType {

    /**
     * Complete report as HTML file. 
     */
    HTML("htm", "html"),
    /**
     * Only list of archives as CSV file.
     */
    CSV("csv", "csv"),
    /**
     * Text report for official license documentation.
     */
    TXT("txt", "txt"),
    /**
     * Output via the DOXIA framework.
     */
    DOXIA("html", "html"),
    /**
     * An unsupported type - only for test cases.
     */
    UNSUPPORTED(null, null);

    private final String artifactType;
    private final String defaultExtension;

    /**
     * @param artifactType
     * @param defaultExtension 
     */
    private OutputFileType(final String artifactType, final String defaultExtension) {
        this.artifactType = artifactType;
        this.defaultExtension = defaultExtension;
    }

    /**
     * @return the artifactType
     */
    public final String getArtifactType() {
        return artifactType;
    }

    /**
     * @return the defaultExtension
     */
    public final String getDefaultExtension() {
        return defaultExtension;
    }

}
