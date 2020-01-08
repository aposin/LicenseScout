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
package org.aposin.licensescout.maven.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.license.IArtifactServerUtil;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILFLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ArtifactServerUtil}.
 * 
 * @see IArtifactServerUtil
 * @see ArtifactServerUtil
 */
public class ArtifactServerUtilTest {

    /**
     * Test method for
     * {@link ArtifactServerUtil#ArtifactServerUtil(java.lang.String, int, org.aposin.licensescout.util.ILFLog)}.
     */
    @Test
    public void testIsCachedCheckAccessRemoteRepo() {
        final String serverBaseUrl = "https://repo.maven.apache.org/maven2_unaccessible/";
        final boolean expectedCacheCheckAccess = false;
        assertIsCachedCheckAccess(serverBaseUrl, expectedCacheCheckAccess);
    }

    /**
     * Test method for
     * {@link ArtifactServerUtil#ArtifactServerUtil(java.lang.String, int, org.aposin.licensescout.util.ILFLog)}.
     */
    @Test
    public void testIsCachedCheckAccessLocalFile() {
        final String serverBaseUrl = "file:src/test/resources/ArtifactServerUtilTest/repo1/";
        final boolean expectedCacheCheckAccess = true;
        assertIsCachedCheckAccess(serverBaseUrl, expectedCacheCheckAccess);
    }

    /**
     * @param serverBaseUrl
     * @param expectedCacheCheckAccess
     */
    private void assertIsCachedCheckAccess(final String serverBaseUrl, final boolean expectedCacheCheckAccess) {
        final int timeout = 400;
        final IArtifactServerUtil artifactServerUtil = new ArtifactServerUtil(serverBaseUrl, timeout,
                TestUtil.createTestLog());
        Assert.assertEquals("isCachedCheckAccess()", expectedCacheCheckAccess,
                artifactServerUtil.isCachedCheckAccess());
    }

    /**
     * Test method for
     * {@link ArtifactServerUtil#addLicensesFromPom(InputStream, Archive, String, LicenseStoreData)}.
     * @throws Exception 
     */
    @Test
    public void testAddLicensesFromPomNoParentNoLicense() throws Exception {
        final String pomFilename = "src/test/resources/ArtifactServerUtilTest/poms/pom-no-parent-no-license.xml";
        final int expectedLicenseCount = 0;
        assertAddLicensesFromPom(pomFilename, expectedLicenseCount);
    }

    /**
     * Test method for
     * {@link ArtifactServerUtil#addLicensesFromPom(InputStream, Archive, String, LicenseStoreData)}.
     * @throws Exception 
     */
    @Test
    public void testAddLicensesFromPomNoParentWithLicense() throws Exception {
        final String pomFilename = "src/test/resources/ArtifactServerUtilTest/poms/pom-no-parent-with-license.xml";
        final int expectedLicenseCount = 1;
        assertAddLicensesFromPom(pomFilename, expectedLicenseCount);
    }

    /**
     * Test method for
     * {@link ArtifactServerUtil#addLicensesFromPom(InputStream, Archive, String, LicenseStoreData)}.
     * @throws Exception 
     */
    @Test
    public void testAddLicensesFromPomWithParentWithLicense() throws Exception {
        final String pomFilename = "src/test/resources/ArtifactServerUtilTest/poms/pom-with-parent.xml";
        final int expectedLicenseCount = 2;
        assertAddLicensesFromPom(pomFilename, expectedLicenseCount);
    }

    /**
     * @param pomFilename
     * @param expectedLicenseCount
     * @throws Exception 
     */
    private void assertAddLicensesFromPom(final String pomFilename, final int expectedLicenseCount) throws Exception {
        final ILFLog log = TestUtil.createTestLog();
        final IArtifactServerUtil artifactServerUtil = createArtifactServerUtil();
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName", "version", "path");
        final LicenseStoreData licenseStoreData = TestUtil.readLicenseStoreData(log);
        try (final InputStream pomInputStream = new FileInputStream(pomFilename)) {
            final boolean licensesAdded = artifactServerUtil.addLicensesFromPom(pomInputStream, archive, "",
                    licenseStoreData);
            Assert.assertEquals("licenses added", expectedLicenseCount > 0, licensesAdded);

        }
        final Set<License> licenses = archive.getLicenses();
        Assert.assertEquals("licenses count", expectedLicenseCount, licenses.size());
    }

    /**
     * @return an artifact server helper object for testing
     */
    private IArtifactServerUtil createArtifactServerUtil() {
        final int timeout = 400;
        final String serverBaseUrl = "file:src/test/resources/ArtifactServerUtilTest/repo1/";
        return new ArtifactServerUtil(serverBaseUrl, timeout, TestUtil.createTestLog());
    }

}
