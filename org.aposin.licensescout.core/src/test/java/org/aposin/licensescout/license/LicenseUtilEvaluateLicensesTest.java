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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.util.MiscUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link LicenseUtil#evaluateLicenses(LicenseCheckedList, java.util.Collection, LicenseStoreData)}.
 * 
 * @see LicenseUtil
 * @see LicenseUtilGetMatchedVersionFromLineTest
 * @see LicenseUtilDetectLicensesTest
 *
 */
public class LicenseUtilEvaluateLicensesTest {

    /**
     * @throws Exception 
     */
    @Test
    public void testEvaluateLicensesNotDetected() throws Exception {
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName", "1.0", "path");
        final String[] originalDetectedLicenseIdentifiers = new String[0];
        final DetectionStatus expectedDetectionStatus = DetectionStatus.NOT_DETECTED;
        final LegalStatus expectedLegalStatus = LegalStatus.UNKNOWN;
        final String[] expectedLicenseIdentifiers = new String[0];

        doTestEvaluateLicenses(archive, originalDetectedLicenseIdentifiers, expectedDetectionStatus,
                expectedLegalStatus, expectedLicenseIdentifiers);
    }

    /**
     * @throws Exception 
     */
    @Test
    public void testEvaluateLicensesSingleDetectedNotOverridden() throws Exception {
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName", "1.0", "path");
        final String[] originalDetectedLicenseIdentifiers = new String[] { "Apache-2.0" };
        final DetectionStatus expectedDetectionStatus = DetectionStatus.DETECTED;
        final LegalStatus expectedLegalStatus = LegalStatus.ACCEPTED;
        final String[] expectedLicenseIdentifiers = new String[] { "Apache-2.0" };

        doTestEvaluateLicenses(archive, originalDetectedLicenseIdentifiers, expectedDetectionStatus,
                expectedLegalStatus, expectedLicenseIdentifiers);
    }

    /**
     * @throws Exception 
     */
    @Test
    public void testEvaluateLicensesMultipleDetectedNotOverridden() throws Exception {
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName", "1.0", "path");
        final String[] originalDetectedLicenseIdentifiers = new String[] { "Apache-2.0", "NPL-1.0" };
        final DetectionStatus expectedDetectionStatus = DetectionStatus.MULTIPLE_DETECTED;
        final LegalStatus expectedLegalStatus = LegalStatus.ACCEPTED;
        final String[] expectedLicenseIdentifiers = new String[] { "Apache-2.0", "NPL-1.0" };

        doTestEvaluateLicenses(archive, originalDetectedLicenseIdentifiers, expectedDetectionStatus,
                expectedLegalStatus, expectedLicenseIdentifiers);
    }

    /**
     * JAVASCRIPT, blob, 0.0.4, MIT
     * @throws Exception 
     */
    @Test
    public void testEvaluateLicensesManualDetectedWithVersion() throws Exception {
        final Archive archive = new Archive(ArchiveType.JAVASCRIPT, "testarchive1", "0.0.4", "path");
        final String[] originalDetectedLicenseIdentifiers = new String[0];
        final DetectionStatus expectedDetectionStatus = DetectionStatus.MANUAL_DETECTED;
        final LegalStatus expectedLegalStatus = LegalStatus.ACCEPTED;
        final String[] expectedLicenseIdentifiers = new String[] { "MIT" };

        doTestEvaluateLicenses(archive, originalDetectedLicenseIdentifiers, expectedDetectionStatus,
                expectedLegalStatus, expectedLicenseIdentifiers);
    }

    /**
     * 
     * @throws Exception 
     */
    @Test
    public void testEvaluateLicensesManualDetectedWithMessageDigest() throws Exception {
        final Archive archive = new Archive(ArchiveType.JAVA, "testarchive2", "", "path");
        archive.setMessageDigest(
                MiscUtil.getByteArrayFromHexString("D11CDFDDC6F3CF3B48560004AA50795222F4ED8A44983651BBC9302A96AD4F46"));
        final String[] originalDetectedLicenseIdentifiers = new String[0];
        final DetectionStatus expectedDetectionStatus = DetectionStatus.MANUAL_DETECTED;
        final LegalStatus expectedLegalStatus = LegalStatus.ACCEPTED;
        final String[] expectedLicenseIdentifiers = new String[] { "DOM4J" };

        doTestEvaluateLicenses(archive, originalDetectedLicenseIdentifiers, expectedDetectionStatus,
                expectedLegalStatus, expectedLicenseIdentifiers);
    }

    /**
     * JAVASCRIPT, blob, 0.0.4, MIT
     * @throws Exception 
     */
    @Test
    public void testEvaluateLicensesManualSelectedWithVersion() throws Exception {
        final Archive archive = new Archive(ArchiveType.JAVASCRIPT, "testarchive3", "0.0.4", "path");
        final String[] originalDetectedLicenseIdentifiers = new String[] { "Apache-2.0", "LGPL-2.1" };
        final DetectionStatus expectedDetectionStatus = DetectionStatus.MANUAL_SELECTED;
        final LegalStatus expectedLegalStatus = LegalStatus.ACCEPTED;
        final String[] expectedLicenseIdentifiers = new String[] { "MIT" };

        doTestEvaluateLicenses(archive, originalDetectedLicenseIdentifiers, expectedDetectionStatus,
                expectedLegalStatus, expectedLicenseIdentifiers);
    }

    /**
     * 
     * @throws Exception 
     */
    @Test
    public void testEvaluateLicensesManualSelectedWithMessageDigest() throws Exception {
        final Archive archive = new Archive(ArchiveType.JAVA, "testarchive4", "", "path");
        archive.setMessageDigest(
                MiscUtil.getByteArrayFromHexString("D11CDFDDC6F3CF3B48560004AA50795222F4ED8A44983651BBC9302A96AD4F46"));
        final String[] originalDetectedLicenseIdentifiers = new String[] { "Apache-2.0", "LGPL-2.1" };
        final DetectionStatus expectedDetectionStatus = DetectionStatus.MANUAL_SELECTED;
        final LegalStatus expectedLegalStatus = LegalStatus.ACCEPTED;
        final String[] expectedLicenseIdentifiers = new String[] { "DOM4J" };

        doTestEvaluateLicenses(archive, originalDetectedLicenseIdentifiers, expectedDetectionStatus,
                expectedLegalStatus, expectedLicenseIdentifiers);
    }

    /**
     * @param archive
     * @param originalDetectedLicenseIdentifiers 
     * @param expectedDetectionStatus
     * @param expectedLegalStatus
     * @param expectedLicenseIdentifiers
     * @throws Exception
     * @throws IOException
     */
    private void doTestEvaluateLicenses(final Archive archive, String[] originalDetectedLicenseIdentifiers,
                                        final DetectionStatus expectedDetectionStatus,
                                        final LegalStatus expectedLegalStatus,
                                        final String[] expectedLicenseIdentifiers)
            throws Exception, IOException {
        final LicenseStoreData licenseStoreData = LicenseUtilTestUtil.createLicenseStoreData();
        final List<License> originalDetectedLicenses = LicenseUtilTestUtil
                .createLicenseList(originalDetectedLicenseIdentifiers, licenseStoreData);
        for (final License license : originalDetectedLicenses) {
            archive.addLicense(license, "path");
        }
        final List<Archive> archives = new ArrayList<>();
        archives.add(archive);
        final LicenseCheckedList licenseCheckedList = LicenseUtilTestUtil.createLicenseCheckedList(licenseStoreData);
        final List<License> expectedLicenses = LicenseUtilTestUtil.createLicenseList(expectedLicenseIdentifiers,
                licenseStoreData);
        LicenseUtil.evaluateLicenses(licenseCheckedList, archives, licenseStoreData);
        Assert.assertEquals("archive list length", 1, archives.size());
        Assert.assertEquals("DetectionStatus", expectedDetectionStatus, archive.getDetectionStatus());
        Assert.assertEquals("LegalStatus", expectedLegalStatus, archive.getLegalStatus());
        final Collection<License> resultLicenses = archive.getLicenses();
        Assert.assertTrue("license list", LicenseUtilTestUtil.areEqual(resultLicenses, expectedLicenses));
    }
}
