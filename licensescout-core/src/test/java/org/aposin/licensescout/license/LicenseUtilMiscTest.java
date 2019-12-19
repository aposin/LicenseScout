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
package org.aposin.licensescout.license;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link LicenseUtil}.
 * 
 * 
 * @see LicenseUtil
 * @see LicenseUtilGetMatchedVersionFromLineTest
 * @see LicenseUtilDetectLicensesTest
 *
 */
public class LicenseUtilMiscTest {

    /**
     * Test for {@link LicenseUtil#getLicenseNameWithVersion(License)}.
     */
    @Test
    public void testGetLicenseNameWithVersionActuallyPresent() {
        final License license = new License("spdxIdent", "name", LegalStatus.ACCEPTED, "author", "1.0", "url", "text",
                null);
        Assert.assertEquals("license name", "name Version 1.0", LicenseUtil.getLicenseNameWithVersion(license));
    }

    /**
     * Test for {@link LicenseUtil#getLicenseNameWithVersion(License)}.
     */
    @Test
    public void testGetLicenseNameWithVersionNullVersion() {
        final License license = new License("spdxIdent", "name", LegalStatus.ACCEPTED, "author", null, "url", "text",
                null);
        Assert.assertEquals("license name", "name", LicenseUtil.getLicenseNameWithVersion(license));
    }

}
