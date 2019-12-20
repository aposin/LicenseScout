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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class BuildInfoTest {

    /**
     * Test method for {@link BuildInfo}.
     */
    @Test
    public void testBuildInfo() {
        final String name = "NAME";
        final String version = "VERSION";
        final String date = "DATE";
        final String buildUrl = "BUILDURL";
        final String licenseReportCsvUrl = "licenseReportCsvUrl";
        final String licenseReportHtmlUrl = "licenseReportHtmlUrl";
        final String licenseReportTxtUrl = "licenseReportTxtUrl";
        final BuildInfo buildInfo = new BuildInfo(name, version, date, buildUrl, licenseReportCsvUrl,
                licenseReportHtmlUrl, licenseReportTxtUrl);
        Assert.assertEquals("name", name, buildInfo.getName());
        Assert.assertEquals("version", version, buildInfo.getVersion());
        Assert.assertEquals("date", date, buildInfo.getDate());
        Assert.assertEquals("buildUrl", buildUrl, buildInfo.getBuildUrl());
        Assert.assertEquals("licenseReportCsvUrl", licenseReportCsvUrl, buildInfo.getLicenseReportCsvUrl());
        Assert.assertEquals("licenseReportHtmlUrl", licenseReportHtmlUrl, buildInfo.getLicenseReportHtmlUrl());
        Assert.assertEquals("licenseReportTxtUrl", licenseReportTxtUrl, buildInfo.getLicenseReportTxtUrl());
    }

}
