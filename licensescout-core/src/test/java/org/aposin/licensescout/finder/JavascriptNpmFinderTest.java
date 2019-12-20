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
package org.aposin.licensescout.finder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link JavascriptNpmFinder}.
 * 
 * 
 */
public class JavascriptNpmFinderTest extends BaseFinderTest {

    /**
     * @throws Exception
     * 
     */
    @Test
    public void testScanNpmEmptyDirectory() throws Exception {
        final AbstractFinder finder = createFinder();
        final File scanDirectory = new File("src/test/resources/scans/empty");
        final int expectedArchiveCount = 0;
        final FinderResult finderResult = doScan(finder, scanDirectory);
        assertScanDirectory(finderResult, scanDirectory);
        assertArchiveFileCount(finderResult, expectedArchiveCount);
    }

    /**
     * @throws Exception
     * 
     */
    @Test
    public void testScanNpmLicenseFileApache() throws Exception {
        final AbstractFinder finder = createFinder();
        final File scanDirectory = new File("src/test/resources/scans/npm-license-file-apache");
        final FinderResult finderResult = doScan(finder, scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, true);
    }

    /**
     * @throws Exception
     * 
     */
    @Test
    public void testScanNpmLicenseFileMit() throws Exception {
        final AbstractFinder finder = createFinder();
        finder.debug = true;
        final File scanDirectory = new File("src/test/resources/scans/npm-license-file-mit");
        final FinderResult finderResult = doScan(finder, scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, "MIT License for lib1 3.2.0");
    }

    /**
     * @throws Exception
     * 
     */
    @Test
    public void testScanNpmLicenseFileUnknown() throws Exception {
        final AbstractFinder finder = createFinder();
        finder.debug = true;
        final File scanDirectory = new File("src/test/resources/scans/npm-license-file-unknown");
        final FinderResult finderResult = doScan(finder, scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, "Unknown License for lib1 3.2.0");
    }

    /**
     * @throws Exception
     * 
     */
    @Test
    public void testScanNpmLicensePackageJson() throws Exception {
        final AbstractFinder finder = createFinder();
        finder.debug = true;
        final File scanDirectory = new File("src/test/resources/scans/npm-license-package-json");
        final FinderResult finderResult = doScan(finder, scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, true);
    }

    /**
     * @throws Exception
     * 
     */
    @Test
    public void testScanNpmLicensePackageJsonMit() throws Exception {
        final AbstractFinder finder = createFinder();
        finder.debug = true;
        final File scanDirectory = new File("src/test/resources/scans/npm-license-package-json-mit");
        final FinderResult finderResult = doScan(finder, scanDirectory);
        final License expectedLicense = getLicenseStoreData().getLicenseBySpdxIdentifier("MIT");
        assertSingleArchive(finderResult, scanDirectory, expectedLicense);
    }

    /**
     * @throws Exception
     * 
     */
    @Test
    public void testScanNpmNoLicense() throws Exception {
        final AbstractFinder finder = createFinder();
        final File scanDirectory = new File("src/test/resources/scans/npm-no-license");
        final int expectedArchiveCount = 1;
        final FinderResult finderResult = doScan(finder, scanDirectory);
        assertScanDirectory(finderResult, scanDirectory);
        assertArchiveFileCount(finderResult, expectedArchiveCount);
        assertSingleArchive(finderResult, scanDirectory, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractFinder createFinder() {
        final LicenseStoreData licenseStoreData = getLicenseStoreData();
        final List<String> npmExcludedDirectoryNames = Arrays.asList("@test");
        return new JavascriptNpmFinder(licenseStoreData, getLog(), npmExcludedDirectoryNames);
    }

    /**
     * @param finderResult
     * @param scanDirectory
     * @param expectLicense
     * @param isJar
     *            true if artifact is expected to be a JAR file (changes the
     *            expected names)
     */
    private void assertSingleArchive(final FinderResult finderResult, final File scanDirectory,
                                     final boolean expectLicense) {
        final License expectedLicense = getExpectedLicense(expectLicense);
        assertSingleArchive(finderResult, scanDirectory, expectedLicense);
    }

    /**
     * @param finderResult
     * @param scanDirectory
     * @param expectedLicense
     * @param isJar
     *            true if artifact is expected to be a JAR file (changes the
     *            expected names)
     */
    private void assertSingleArchive(final FinderResult finderResult, final File scanDirectory,
                                     final Object expectedLicense) {
        assertScanDirectory(finderResult, scanDirectory);
        assertArchiveFileCount(finderResult, 1);
        final String expectedFileName = "lib1";
        final String expectedPath = "/lib1/3.2.0";
        String expectedVersion = "3.2.0";
        final Archive archive = finderResult.getArchiveFiles().get(0);
        assertEquals("archiveType", ArchiveType.JAVASCRIPT, archive.getArchiveType());
        assertEquals("fileName", expectedFileName, archive.getFileName());
        assertEquals("path", expectedPath, archive.getPath());
        assertEquals("version", expectedVersion, archive.getVersion());
        assertEquals("vendor", "Unknown", archive.getVendor());
        assertEquals("detectionStatus", null, archive.getDetectionStatus());
        assertEquals("detectedLicenses", null, archive.getDetectedLicenses());
        final Set<License> licenses = archive.getLicenses();
        if (expectedLicense == null) {
            // assertNull("licenses present", licenses);
            assertEquals("licenses size", 0, licenses.size());
        } else {
            assertNotNull("licenses not present", licenses);
            assertEquals("licenses size", 1, licenses.size());
            if (expectedLicense instanceof License) {
                Assert.assertTrue("licenses[0]", licenses.contains(expectedLicense));
            } else if (expectedLicense instanceof String) {
                assertEquals("licenses[0]", expectedLicense, licenses.iterator().next().getName());
            }
        }
    }

}
