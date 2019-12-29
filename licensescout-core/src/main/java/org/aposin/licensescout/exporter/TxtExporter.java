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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.model.Notice;
import org.aposin.licensescout.model.Provider;

/**
 * The Exporter creates a text report listing all archives including license
 * information.
 * 
 * <p>This implementation uses Velocity templates to generate the
 * output.
 * The default template used (if none is configured by the user) is at
 * <code>templates/license_report_txt_default.vm</code>.</p>
 */
public class TxtExporter extends AbstractVelocityExporter {

    private static final String DEFAULT_TEMPLATES_LICENSE_REPORT_VM = "templates/license_report_txt_default.vm";

    private static final String DEFAULT_TEMPLATE_ENCODING = "UTF-8";

    private static final IReportExporter INSTANCE = new TxtExporter();

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
        return OutputFileType.TXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Template getDefaultTemplate() {
        return Velocity.getTemplate(DEFAULT_TEMPLATES_LICENSE_REPORT_VM, DEFAULT_TEMPLATE_ENCODING);
    }

    /**
     *
     */
    @Override
    protected void additionalSetup(final VelocityContext context, final OutputResult outputResult) {
        final List<Archive> archiveFiles = outputResult.getFinderResult().getArchiveFiles();

        final List<Provider> usedProviders = collectProviders(archiveFiles);
        final List<Notice> usedNotices = collectNotices(archiveFiles);
        final Map<License, List<Archive>> usedLicensesMap = collectLicenses(archiveFiles);
        final List<License> usedLicenses = getSortedLicenses(usedLicensesMap);

        context.put("usedProviders", usedProviders);
        context.put("usedNotices", usedNotices);
        context.put("usedLicensesMap", usedLicensesMap);
        context.put("usedLicenses", usedLicenses);
    }

    /**
     * @param archiveFiles
     * @return a list of licenses
     */
    private Map<License, List<Archive>> collectLicenses(final List<Archive> archiveFiles) {
        final Map<License, List<Archive>> licenseMap = new TreeMap<>();
        for (final Archive archive : archiveFiles) {
            final Set<License> licenses = archive.getLicenses();
            for (final License license : licenses) {
                List<Archive> archivesPerLicense = licenseMap.get(license);
                if (archivesPerLicense == null) {
                    archivesPerLicense = new ArrayList<>();
                    licenseMap.put(license, archivesPerLicense);
                }
                archivesPerLicense.add(archive);
            }
        }
        return licenseMap;
    }

    private List<License> getSortedLicenses(final Map<License, List<Archive>> usedLicensesMap) {
        final List<License> allLicenses = new ArrayList<>(usedLicensesMap.keySet());
        final List<License> distinctLicenses = new ArrayList<>(allLicenses);
        Collections.sort(distinctLicenses);
        return distinctLicenses;
    }

    /**
     * @param archiveFiles
     * @return a list of providers
     */
    private List<Provider> collectProviders(final List<Archive> archiveFiles) {
        final List<Provider> usedProviders = new ArrayList<>();
        for (final Archive archive : archiveFiles) {
            final Provider provider = archive.getProvider();
            if (provider != null && !usedProviders.contains(provider)) {
                usedProviders.add(provider);
            }
        }
        Collections.sort(usedProviders, new Comparator<Provider>() { // NOSONAR

            @Override
            public int compare(Provider o1, Provider o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return usedProviders;
    }

    /**
     * @param archiveFiles
     * @return 
     */
    private List<Notice> collectNotices(final List<Archive> archiveFiles) {
        final List<Notice> usedNotices = new ArrayList<>();
        for (final Archive archive : archiveFiles) {
            final Notice notice = archive.getNotice();
            addNotice(usedNotices, notice);
            for (License license : archive.getLicenses()) {
                final Notice notice2 = license.getNotice();
                addNotice(usedNotices, notice2);
            }
        }
        return usedNotices;
    }

    /**
     * @param usedNotices
     * @param notice
     */
    private void addNotice(final List<Notice> usedNotices, final Notice notice) {
        if (notice != null && !usedNotices.contains(notice)) {
            usedNotices.add(notice);
        }
    }

}
