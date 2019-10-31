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

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.aposin.licensescout.configuration.OutputFileType;

/**
 * The Exporter creates a HTML report listing all archives including license information.
 * 
 * <p>This implementation uses Velocity templates to generate the output. The template used here is at
 * <code>templates/license_report.vm</code>.</p>
 */
public class HtmlExporter extends AbstractVelocityExporter {

    private static final String TEMPLATES_LICENSE_REPORT_VM = "templates/license_report.vm";

    private static final IReportExporter INSTANCE = new HtmlExporter();

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
        return OutputFileType.HTML;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Template getDefaultTemplate() {
        return Velocity.getTemplate(TEMPLATES_LICENSE_REPORT_VM);
    }

}
