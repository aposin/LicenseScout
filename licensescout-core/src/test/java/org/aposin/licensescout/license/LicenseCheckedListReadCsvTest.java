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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Set;

import org.aposin.licensescout.archive.ArchiveIdentifierPattern;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.model.LSMessageDigest;
import org.aposin.licensescout.model.Notices;
import org.aposin.licensescout.model.Providers;
import org.aposin.licensescout.util.CryptUtil;
import org.aposin.licensescout.util.ILSLog;
import org.aposin.licensescout.util.MiscUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link LicenseCheckedList#readCsv(InputStream, LicenseStoreData, Providers, Notices, ILSLog)}.
 * 
 * @see LicenseCheckedList
 */
public class LicenseCheckedListReadCsvTest {

    private static final String CONFIGURATION_BASE_PATH = "src/test/resources/LicenseCheckedListReadCsvTest/";

    /**
     * Tests {@link LicenseCheckedList#readCsv(InputStream, LicenseStoreData, Providers, Notices, ILSLog)} with SHA-265.
     * 
     * @throws Exception 
     */
    @Test
    public void testReadCsvSha256() throws Exception {
        assertReadCsv("SHA-256", "checkedarchivesSha256.csv",
                "D11CDFDDC6F3CF3B48560004AA50795222F4ED8A44983651BBC9302A96AD4F46");
    }

    /**
     * Tests {@link LicenseCheckedList#readCsv(InputStream, LicenseStoreData, Providers, Notices, ILSLog)} with SHA3-512.
     * 
     * @throws Exception 
     */
    @Test
    public void testReadCsvSha3S512() throws Exception {
        assertReadCsv("SHA3-512", "checkedarchivesSha3_512.csv",
                "D11CDFDDC6F3CF3B48560004AA50795222F4ED8A44983651BBC9302A96AD4F46D11CDFDDC6F3CF3B48560004AA50795222F4ED8A44983651BBC9302A96AD4F46");
    }

    /**
     * @param messageDigestAlgorithmName
     * @param checkedArchivesFilename
     * @param messageDigestValueString
     * @throws Exception
     */
    private void assertReadCsv(final String messageDigestAlgorithmName, final String checkedArchivesFilename,
                               final String messageDigestValueString)
            throws Exception {
        CryptUtil.setMessageDigestAlgorithm(messageDigestAlgorithmName);
        final ILSLog log = TestUtil.createTestLog();
        final LicenseStoreData licenseStoreData = LicenseUtilTestUtil.createLicenseStoreData();
        final Providers providers = new Providers();
        final Notices notices = new Notices();
        final LicenseCheckedList checkedArchives = new LicenseCheckedList();
        try (final InputStream inputStream = new FileInputStream(CONFIGURATION_BASE_PATH + checkedArchivesFilename)) {
            checkedArchives.readCsv(inputStream, "UTF-8", licenseStoreData, providers, notices, log);
        }
        checkArchiveLicensesVersion(checkedArchives, ArchiveType.JAVA, "not_existing", "not_existing", false);
        checkArchiveLicensesVersion(checkedArchives, ArchiveType.JAVASCRIPT, "testarchive1", "0.0.4", true);
        checkArchiveLicensesVersion(checkedArchives, ArchiveType.JAVA, "testarchive2", "0.0.4", false);
        checkArchiveLicensesMD(checkedArchives, ArchiveType.JAVA, "testarchive2", messageDigestValueString, true);
        checkArchiveLicensesVersion(checkedArchives, ArchiveType.JAVASCRIPT, "testarchive8", "", false);
        final Set<Entry<ArchiveIdentifierPattern, LicenseResult>> patternArchives = checkedArchives
                .getManualPatternArchives();
        Assert.assertEquals("patternArchives size", 2, patternArchives.size());
    }

    /**
     * @param checkedArchives
     * @param archiceType
     * @param archiveName
     * @param version
     * @param expectedExistance
     */
    private void checkArchiveLicensesVersion(final LicenseCheckedList checkedArchives, final ArchiveType archiceType,
                                             final String archiveName, final String version,
                                             final boolean expectedExistance) {
        final LicenseResult licenseResult = checkedArchives.getManualLicense(archiceType, archiveName, version);
        checkArchiveLicensesCommon(licenseResult, expectedExistance);
    }

    /**
     * @param checkedArchives
     * @param archiceType
     * @param archiveName
     * @param messageDigestString
     * @param expectedExistance
     */
    private void checkArchiveLicensesMD(final LicenseCheckedList checkedArchives, final ArchiveType archiceType,
                                        final String archiveName, final String messageDigestString,
                                        final boolean expectedExistance) {
        final LSMessageDigest messageDigest = MiscUtil.getLSMessageDigestFromHexString(messageDigestString);
        final LicenseResult licenseResult = checkedArchives.getManualLicense(archiceType, archiveName, messageDigest);
        checkArchiveLicensesCommon(licenseResult, expectedExistance);
    }

    /**
     * @param licenseResult
     * @param expectedExistance
     */
    private void checkArchiveLicensesCommon(final LicenseResult licenseResult, final boolean expectedExistance) {
        if (expectedExistance) {
            Assert.assertNotNull("existing archive", licenseResult);
            Assert.assertEquals("License count", 1, licenseResult.getLicenses().size());
        } else {
            Assert.assertNull("not existing archive", licenseResult);
        }
    }
}
