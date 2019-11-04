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
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.license.License;

/**
 * The Exporter creates a report listing all archives including license
 * information.
 * 
 * <p>This implementation is a base class for exporters based on Velocity templates
 * to generate the output.</p>
 * 
 * <p>Subclasses have to provide a default template by implementing {@link #getDefaultTemplate()}.</p>
 * <p>If necessary, subclasses can put additional information to the {@link VelocityContext} by overriding {@link #additionalSetup(VelocityContext, OutputResult)}.</p>
 */
public abstract class AbstractVelocityExporter implements IReportExporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public final void export(final OutputResult outputResult, final ReportConfiguration reportConfiguration)
            throws Exception {
        try (final FileWriter fileWriter = new FileWriter(reportConfiguration.getOutputFile());
                final BufferedWriter bw = new BufferedWriter(fileWriter)) {

            Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,classpath");
            Velocity.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
            /*
             * NOTE: setting the path to empty is necessary because the default is "." (current
             * directory) and then using absolute path names will not work.
             */
            Velocity.setProperty("file.resource.loader.path", "");
            Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

            Velocity.init();

            final VelocityContext context = createVelocityContext(outputResult, reportConfiguration);
            final Template template = getTemplate(reportConfiguration);

            try (final StringWriter sw = new StringWriter()) {
                if (template != null) {
                    template.merge(context, sw);
                }
                bw.write(sw.getBuffer().toString());
            }
        }
    }

    private VelocityContext createVelocityContext(final OutputResult outputResult,
                                                  final ReportConfiguration reportConfiguration) {
        final VelocityContext context = new VelocityContext();
        final List<Archive> archiveFiles = getSortedArchives(outputResult);
        context.put("archiveFiles", archiveFiles);

        context.put("sourcePath", outputResult.getFinderResult().getScanDirectory().getAbsolutePath());
        context.put("detectionStatusStatistics", outputResult.getDetectionStatusStatistics());
        context.put("legalStatusStatistics", outputResult.getLegalStatusStatistics());
        context.put("generalStatistics", outputResult.getGeneralStatistics());
        context.put("messageDigestAlgorithm", outputResult.getMessageDigestAlgorithm());
        context.put("reportConfiguration", reportConfiguration);
        context.put("pomResolutionUsed", outputResult.isPomResolutionUsed());

        final List<License> distinctLicenses = collectDistinctLicenses(archiveFiles);
        context.put("distinctLicenses", distinctLicenses);

        additionalSetup(context, outputResult);
        return context;
    }

    private List<Archive> getSortedArchives(final OutputResult outputResult) {
        final List<Archive> archiveFiles = outputResult.getFinderResult().getArchiveFiles();
        Collections.sort(archiveFiles);
        return archiveFiles;
    }

    private List<License> collectDistinctLicenses(final List<Archive> archiveFiles) {
        final Set<License> allLicenses = new HashSet<>();
        for (final Archive archive : archiveFiles) {
            allLicenses.addAll(archive.getLicenses());
        }
        final List<License> distinctLicenses = new ArrayList<>(allLicenses);

        Collections.sort(distinctLicenses);
        return distinctLicenses;
    }

    /**
     * Do additional setup of {@link VelocityContext}.
     * 
     * <p>This is a template method that can be used to put additional data collected from the outputResult to the
     * {@link VelocityContext}. Default is to do nothing.</p>
     * 
     * @param context the velocity context that can be modified
     * @param outputResult results from the license scan
     */
    protected void additionalSetup(final VelocityContext context, final OutputResult outputResult) {
        // EMPTY
    }

    /**
     * Obtains the velocity template to use in the export method.
     * 
     * @return the velocity template to use
     */
    protected Template getTemplate(final ReportConfiguration reportConfiguration) {
        if (reportConfiguration.getTemplateFile() != null) {
            return Velocity.getTemplate(reportConfiguration.getTemplateFile().getAbsolutePath());
        } else {
            return getDefaultTemplate();
        }
    }

    /**
     * Obtains the default velocity template to use in the export method.
     * 
     * <p>This method is called if no template is set in the configuration for the
     * output type.</p>
     * 
     * @return the velocity template to use
     */
    protected abstract Template getDefaultTemplate();
}
