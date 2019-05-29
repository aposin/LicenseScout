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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.license.License;

/**
 * The Exporter creates a HTML report listing all archives including license information.
 * 
 * <p>This implementation uses Velocity templates to generate the output. The template used here is at
 * <code>templates/license_report.vm</code>.</p>
 */
public class HtmlExporter implements IReportExporter {

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
    public void export(final OutputResult outputResult, final ReportConfiguration reportConfiguration)
            throws Exception {
        final List<Archive> archiveFiles = outputResult.getFinderResult().getArchiveFiles();
        FileWriter fileWriter = new FileWriter(reportConfiguration.getOutputFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);

        Collections.sort(archiveFiles);

        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        Velocity.init();

        VelocityContext context = new VelocityContext();

        context.put("archiveFiles", archiveFiles);
        context.put("sourcePath", outputResult.getFinderResult().getScanDirectory().getAbsolutePath());
        context.put("detectionStatusStatistics", outputResult.getDetectionStatusStatistics());
        context.put("legalStatusStatistics", outputResult.getLegalStatusStatistics());
        context.put("generalStatistics", outputResult.getGeneralStatistics());
        context.put("messageDigestAlgorithm", outputResult.getMessageDigestAlgorithm());
        context.put("reportConfiguration", reportConfiguration);

        final Set<License> allLicenses = new HashSet<>();
        for (final Archive archive : archiveFiles) {
            allLicenses.addAll(archive.getLicenses());
        }
        final List<License> distinctLicenses = new ArrayList<>(allLicenses);

        Collections.sort(distinctLicenses);

        context.put("distinctLicenses", distinctLicenses);

        final Template template = getTemplate();

        StringWriter sw = new StringWriter();

        if (template != null) {
            template.merge(context, sw);
        }
        bw.write(sw.getBuffer().toString());
        bw.close();
    }

    /**
     * Obtains the template
     * @return the template
     */
    private Template getTemplate() {
        return Velocity.getTemplate(TEMPLATES_LICENSE_REPORT_VM);
    }

}
