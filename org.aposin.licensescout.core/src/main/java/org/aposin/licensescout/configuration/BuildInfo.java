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

/**
 * Information on a build.
 * 
 */
public class BuildInfo {

    private final String name;
    private final String version;
    // Note: not used - date is taken from the database
    private final String date;
    private final String buildUrl;
    private final String licenseReportCsvUrl;
    private final String licenseReportHtmlUrl;
    private final String licenseReportTxtUrl;

    /**
     * @param name
     * @param version
     * @param date 
     * @param buildUrl 
     * @param licenseReportCsvUrl 
     * @param licenseReportHtmlUrl 
     * @param licenseReportTxtUrl 
     */
    public BuildInfo(final String name, final String version, final String date, final String buildUrl,
            final String licenseReportCsvUrl, final String licenseReportHtmlUrl, final String licenseReportTxtUrl) {
        this.name = name;
        this.version = version;
        this.date = date;
        this.buildUrl = buildUrl;
        this.licenseReportCsvUrl = licenseReportCsvUrl;
        this.licenseReportHtmlUrl = licenseReportHtmlUrl;
        this.licenseReportTxtUrl = licenseReportTxtUrl;
    }

    /**
     * @return the identifier
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the version
     */
    public final String getVersion() {
        return version;
    }

    /**
     * @return the date
     */
    public final String getDate() {
        return date;
    }

    /**
     * @return the url
     */
    public final String getBuildUrl() {
        return buildUrl;
    }

    /**
     * @return the licenseReportCsvUrl
     */
    public final String getLicenseReportCsvUrl() {
        return licenseReportCsvUrl;
    }

    /**
     * @return the licenseReportHtmlUrl
     */
    public final String getLicenseReportHtmlUrl() {
        return licenseReportHtmlUrl;
    }

    /**
     * @return the licenseReportTxtUrl
     */
    public final String getLicenseReportTxtUrl() {
        return licenseReportTxtUrl;
    }

}
