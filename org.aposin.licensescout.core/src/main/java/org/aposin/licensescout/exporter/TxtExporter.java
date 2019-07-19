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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.model.Notice;
import org.aposin.licensescout.model.Provider;

/**
 * The Exporter creates a HTML report listing all archives including license information.
 * 
 * <p>This implementation uses Velocity templates to generate the output. The template used here is at
 * <code>templates/license_report.vm</code>.</p>
 */
public class TxtExporter implements IReportExporter {

    private static final Charset OUTPUT_FILE_ENCODING = StandardCharsets.UTF_8;

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
    public void export(final OutputResult outputResult, final ReportConfiguration reportConfiguration)
            throws Exception {
        final String nl = System.lineSeparator();
        final File outputFile = reportConfiguration.getOutputFile();
        final List<Archive> archiveFiles = outputResult.getFinderResult().getArchiveFiles();
        try (Writer osWriter = new OutputStreamWriter(new FileOutputStream(outputFile), OUTPUT_FILE_ENCODING);
                final BufferedWriter bw = new BufferedWriter(osWriter);) {

            Collections.sort(archiveFiles);

            final List<Provider> usedProviders = collectProviders(archiveFiles);

            final List<Notice> usedNotices = collectNotices(archiveFiles);

            final Map<License, List<Archive>> usedLicensesMap = collectLicenses(archiveFiles);
            final List<License> usedLicenses = getSortedLicenses(usedLicensesMap);

            final StringBuilder sb = new StringBuilder();
            final String header = "License Report" + nl + nl
                    + " Copyright (c) 2018 Association for the promotion of open-source insurance software " + nl
                    + "and for the establishment of open interface standards in the insurance industry. " + nl
                    + "All Rights Reserved." + nl + nl
                    + "This product is licensed to you under the Apache License, Version 2.0 (the \"License\"). " + nl
                    + "You may not use this product except in compliance with the License. " + nl + "" + nl
                    + "In the following sections you can find " + nl + "- List of used/linked software projects" + nl
                    + "- Notices to the used artifacts" + nl
                    + "- List of used artifacts with license info and vendor plugin names" + nl + "- License texts" + nl
                    + nl;
            sb.append(header);

            sb.append(nl).append(nl);

            appendSeparatorLine(sb);
            sb.append(nl);

            sb.append("This project contains software from the following providers:").append(nl);
            for (Provider provider : usedProviders) {
                sb.append("* ").append(provider.getName());
                if (!StringUtils.isEmpty(provider.getUrl())) {
                    sb.append(" (").append(provider.getUrl()).append(")");
                }
                sb.append(nl);
            }

            sb.append(nl).append(nl);

            appendSeparatorLine(sb);
            sb.append("Notices").append(nl);
            appendSeparatorLine(sb);
            sb.append(nl);

            for (Notice notice : usedNotices) {
                sb.append(notice.getText()).append(nl);
                sb.append(nl).append(nl);
            }

            appendSeparatorLine(sb);
            sb.append("Artifacts by Licenses").append(nl);
            appendSeparatorLine(sb);
            sb.append(nl).append(nl);

            for (final Map.Entry<License, List<Archive>> entry : usedLicensesMap.entrySet()) {
                final License license = entry.getKey();
                final List<Archive> archives = entry.getValue();
                sb.append(LicenseUtil.getLicenseNameWithVersion(license));
                sb.append(nl).append(nl);
                for (final Archive archive : archives) {
                    sb.append("* ").append(archive.getFileName());
                    final Provider provider = archive.getProvider();
                    if (provider != null) {
                        sb.append(" / ").append(provider.getName());
                    }
                    sb.append(nl);
                }
                sb.append(nl).append(nl);
            }

            appendSeparatorLine(sb);
            sb.append("License texts").append(nl);
            appendSeparatorLine(sb);
            sb.append(nl).append(nl);

            for (final License license : usedLicenses) {
                sb.append(LicenseUtil.getLicenseNameWithVersion(license));
                sb.append(nl).append(nl);
                sb.append(license.getText());
                sb.append(nl).append(nl);
            }

            sb.append(nl).append(nl);

            bw.write(sb.toString());
        }
    }

    private void appendSeparatorLine(final StringBuilder sb) {
        sb.append("====================================================================");
        sb.append(System.lineSeparator());
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
