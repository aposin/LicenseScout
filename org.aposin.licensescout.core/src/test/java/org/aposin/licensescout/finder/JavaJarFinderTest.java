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
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JavaUtilLog;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test case for {@link JavaJarFinder}.
 */
public class JavaJarFinderTest extends BaseFinderTest {

    private static final String BASE_PATH = "src/test/resources/scans/";
    private static final int EXPECTED_MESSAGE_DIGEST_STRING_LENGTH = 64;

    /**
     * Test method for
     * {@link org.aposin.licensescout.license.ArtifactServerUtil#ArtifactServerUtil(java.lang.String, int, org.aposin.licensescout.util.ILFLog)}.
     */
    @Test
    public void testPomResolutionUsed() {
        final Logger logger = Logger.getGlobal();
        final ILFLog log = new JavaUtilLog(logger);
        LicenseStoreData licenseStoreData = null;
        final RunParameters runParameters = createRunParameters();
        final JavaJarFinder finder = new JavaJarFinder(licenseStoreData, runParameters, log);
        Assert.assertFalse("isPomResolutionUsed()", finder.isPomResolutionUsed());
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanEmptyDirectory() throws Exception {
        final JavaJarFinder finder = createFinder();
        finder.debug = true;
        final File scanDirectory = getFile("empty");
        final int expectedArchiveCount = 0;
        final FinderResult finderResult = doScan(finder, scanDirectory);
        assertResultsBase(finderResult, scanDirectory, expectedArchiveCount);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedLicenseFile() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-unpacked-license-file/lib1");
        final File scanDirectoryPacked = getFile("java-packed-license-file");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertSingleArchive(finderResult, scanDirectoryPacked, true, true, 1);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedLicenseIncludedPom() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-unpacked-license-included-pom/lib1");
        final File scanDirectoryPacked = getFile("java-packed-license-included-pom");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertSingleArchive(finderResult, scanDirectoryPacked, true, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedLicenseManifest() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-unpacked-license-manifest/lib1");
        final File scanDirectoryPacked = getFile("java-packed-license-manifest");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertSingleArchive(finderResult, scanDirectoryPacked, true, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedLicenseNone() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-unpacked-license-none/lib1");
        final File scanDirectoryPacked = getFile("java-packed-license-none");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertSingleArchive(finderResult, scanDirectoryPacked, true, false, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedWithInnerLicenseFile() throws Exception {
        {
            final File scanDirectoryUnpacked = getFile("java-inner-license-file");
            final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-file/lib1/lib");
            createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);
        }

        final File scanDirectoryUnpacked = getFile("java-unpacked-with-inner-license-file/lib1");
        final File scanDirectoryPacked = getFile("java-packed-with-inner-license-file");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertArchiveWithInnerStandard(finderResult, scanDirectoryPacked, true, true, 1);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedWithInnerLicenseIncludedPom() throws Exception {
        {
            final File scanDirectoryUnpacked = getFile("java-inner-license-included-pom");
            final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-included-pom/lib1/lib");
            createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);
        }

        final File scanDirectoryUnpacked = getFile("java-unpacked-with-inner-license-included-pom/lib1");
        final File scanDirectoryPacked = getFile("java-packed-with-inner-license-included-pom");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertArchiveWithInnerStandard(finderResult, scanDirectoryPacked, true, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedWithInnerLicenseManifest() throws Exception {
        {
            final File scanDirectoryUnpacked = getFile("java-inner-license-manifest");
            final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-manifest/lib1/lib");
            createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);
        }

        final File scanDirectoryUnpacked = getFile("java-unpacked-with-inner-license-manifest/lib1");
        final File scanDirectoryPacked = getFile("java-packed-with-inner-license-manifest");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertArchiveWithInnerStandard(finderResult, scanDirectoryPacked, true, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaPackedWithInnerLicenseNone() throws Exception {
        {
            final File scanDirectoryUnpacked = getFile("java-inner-license-none");
            final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-none/lib1/lib");
            createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);
        }

        final File scanDirectoryUnpacked = getFile("java-unpacked-with-inner-license-none/lib1");
        final File scanDirectoryPacked = getFile("java-packed-with-inner-license-none");
        createJarLib1(scanDirectoryUnpacked, scanDirectoryPacked);
        final FinderResult finderResult = doScan(scanDirectoryPacked);
        assertArchiveWithInnerStandard(finderResult, scanDirectoryPacked, true, false, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedLicenseFile() throws Exception {
        final File scanDirectory = getFile("java-unpacked-license-file");
        final FinderResult finderResult = doScan(scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, false, true, 1);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedLicenseIncludedPom() throws Exception {
        final File scanDirectory = getFile("java-unpacked-license-included-pom");
        final FinderResult finderResult = doScan(scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, false, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Ignore(value = "ignored due to a bug in JavaJarFinder (the test is correct)")
    @Test
    public void testScanJavaUnpackedLicenseManifest() throws Exception {
        final File scanDirectory = getFile("java-unpacked-license-manifest");
        final FinderResult finderResult = doScan(scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, false, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedLicenseNone() throws Exception {
        final File scanDirectory = getFile("java-unpacked-license-none");
        final FinderResult finderResult = doScan(scanDirectory);
        assertSingleArchive(finderResult, scanDirectory, false, false, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedWithInnerLicenseFile() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-inner-license-file");
        final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-file/lib1/lib");
        createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);

        final File scanDirectory = getFile("java-unpacked-with-inner-license-file");
        final FinderResult finderResult = doScan(scanDirectory);
        assertArchiveWithInnerStandard(finderResult, scanDirectory, false, true, 1);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedWithInnerLicenseIncludedPom() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-inner-license-included-pom");
        final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-included-pom/lib1/lib");
        createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);

        final File scanDirectory = getFile("java-unpacked-with-inner-license-included-pom");
        final FinderResult finderResult = doScan(scanDirectory);
        assertArchiveWithInnerStandard(finderResult, scanDirectory, false, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedWithInnerLicenseManifest() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-inner-license-manifest");
        final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-manifest/lib1/lib");
        createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);

        final File scanDirectory = getFile("java-unpacked-with-inner-license-manifest");
        final FinderResult finderResult = doScan(scanDirectory);
        assertArchiveWithInnerStandard(finderResult, scanDirectory, false, true, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedWithInnerLicenseNone() throws Exception {
        final File scanDirectoryUnpacked = getFile("java-inner-license-none");
        final File scanDirectoryPacked = getFile("java-unpacked-with-inner-license-none/lib1/lib");
        createJarLib2(scanDirectoryUnpacked, scanDirectoryPacked);

        final File scanDirectory = getFile("java-unpacked-with-inner-license-none");
        final FinderResult finderResult = doScan(scanDirectory);
        assertArchiveWithInnerStandard(finderResult, scanDirectory, false, false, 0);
    }

    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testScanJavaUnpackedWithInnerSpecial() throws Exception {
        final File scanDirectory = getFile("java-unpacked-with-inner-special");
        final FinderResult finderResult = doScan(scanDirectory);
        assertArchiveWithInnerSpecial(finderResult, scanDirectory, false, true, 1);
    }

    private JavaJarFinder createFinder() throws Exception {
        final LicenseStoreData licenseStoreData = getLicenseStoreData();
        final RunParameters runParameters = createRunParameters();
        return new JavaJarFinder(licenseStoreData, runParameters, getLog());
    }

    private RunParameters createRunParameters() {
        final String serverBaseUrl = "https://repo.maven.apache.org/maven2_unaccessible/";
        final int timeout = 400;
        final RunParameters runParameters = new RunParameters();
        runParameters.setNexusCentralBaseUrl(serverBaseUrl);
        runParameters.setConnectTimeout(timeout);
        return runParameters;
    }

    private void createJarLib1(final File source, final File destDirectory) throws IOException {
        destDirectory.mkdirs();
        final File destFile = new File(destDirectory, "lib1.jar");
        final List<File> files = CreateJarFile.collectFiles(source);
        CreateJarFile.createJarArchiveZip(destFile, files, source.toPath());
    }

    private void createJarLib2(final File source, final File destDirectory) throws IOException {
        destDirectory.mkdirs();
        final File destFile = new File(destDirectory, "lib2.jar");
        final List<File> files = CreateJarFile.collectFiles(source);
        CreateJarFile.createJarArchiveZip(destFile, files, source.toPath());
    }

    /**
     * @param scanDirectory
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws Exception
     */
    private FinderResult doScan(final File scanDirectory)
            throws IOException, SAXException, ParserConfigurationException, Exception {
        final JavaJarFinder finder = createFinder();
        final FinderResult finderResult = doScan(finder, scanDirectory);
        return finderResult;
    }

    /**
     * @param finderResult
     * @param scanDirectory
     * @param isJar
     *            true if artifact is expected to be a JAR file (changes the
     *            expected names)
     * @param expectLicense
     * @param expectedLicenseCandidateFilesCount
     * 
     */
    private void assertSingleArchive(final FinderResult finderResult, final File scanDirectory, final boolean isJar,
                                     final boolean expectLicense, final int expectedLicenseCandidateFilesCount) {
        final License expectedLicense = getExpectedLicense(expectLicense);
        final String expectedFileName0 = isJar ? "lib1.jar" : "lib1";
        final String expectedPath0 = isJar ? "/lib1.jar" : "/lib1";
        assertResultsBase(finderResult, scanDirectory, 1);
        final Archive archive0 = finderResult.getArchiveFiles().get(0);
        final String expectedVendor = "Unknown";
        assertArchive(archive0, "archive[0]: ", expectedFileName0, expectedPath0, "0.0.2", expectedVendor,
                expectedLicense, isJar, expectedLicenseCandidateFilesCount);
    }

    /**
     * @param finderResult
     * @param scanDirectory
     * @param isJarOuter
     * @param expectLicenseInner
     * @param expectedLicenseCandidateFilesCountInner
     */
    private void assertArchiveWithInnerStandard(final FinderResult finderResult, final File scanDirectory,
                                                final boolean isJarOuter, final boolean expectLicenseInner,
                                                final int expectedLicenseCandidateFilesCountInner) {
        final String expectedFileNameInner = "lib2.jar";
        final String expectedPathInner = (isJarOuter ? "/lib1.jar!" : "/lib1") + "/lib/lib2.jar";
        final String expectedVersionInner = "0.0.3";
        final String expectedVendorInner = "Unknown";
        assertArchiveWithInner(finderResult, scanDirectory, isJarOuter, expectLicenseInner,
                expectedLicenseCandidateFilesCountInner, expectedFileNameInner, expectedPathInner, expectedVersionInner,
                expectedVendorInner, true);
    }

    /**
     * @param finderResult
     * @param scanDirectory
     * @param isJarOuter
     * @param expectLicenseInner
     * @param expectedLicenseCandidateFilesCountInner
     */
    private void assertArchiveWithInnerSpecial(final FinderResult finderResult, final File scanDirectory,
                                               final boolean isJarOuter, final boolean expectLicenseInner,
                                               final int expectedLicenseCandidateFilesCountInner) {
        final String expectedFileNameInner = "ckeditor";
        final String expectedPathInner = (isJarOuter ? "/lib1.jar!" : "/lib1") + "/ckeditor";
        assertArchiveWithInner(finderResult, scanDirectory, isJarOuter, expectLicenseInner,
                expectedLicenseCandidateFilesCountInner, expectedFileNameInner, expectedPathInner, "0.0.0", null,
                false);
    }

    /**
     * @param finderResult
     * @param scanDirectory
     * @param isJarOuter
     * @param expectLicenseInner
     * @param expectedLicenseCandidateFilesCountInner
     * @param expectedFileNameInner
     * @param expectedPathInner
     * @param expectedVersionInner
     * @param expectedVendorInner
     * @param expectMessageDigestInner
     */
    private void assertArchiveWithInner(final FinderResult finderResult, final File scanDirectory,
                                        final boolean isJarOuter, final boolean expectLicenseInner,
                                        final int expectedLicenseCandidateFilesCountInner,
                                        final String expectedFileNameInner, final String expectedPathInner,
                                        final String expectedVersionInner, final String expectedVendorInner,
                                        final boolean expectMessageDigestInner) {
        final License expectedLicenseInner = getExpectedLicense(expectLicenseInner);
        assertResultsBase(finderResult, scanDirectory, 2);

        // the outer
        final String expectedFileNameOuter = isJarOuter ? "lib1.jar" : "lib1";
        final String expectedPathOuter = isJarOuter ? "/lib1.jar" : "/lib1";
        final Archive archiveOuter = finderResult.getArchiveFiles().get(0);
        assertArchive(archiveOuter, "archive[0] (outer): ", expectedFileNameOuter, expectedPathOuter, "0.0.2",
                "Unknown", null, isJarOuter, 0);

        // the inner
        final Archive archiveInner = finderResult.getArchiveFiles().get(1);
        assertArchive(archiveInner, "archive[1] (inner): ", expectedFileNameInner, expectedPathInner,
                expectedVersionInner, expectedVendorInner, expectedLicenseInner, expectMessageDigestInner,
                expectedLicenseCandidateFilesCountInner);
    }

    private void assertArchive(final Archive archive, final String messagePrefix, final String expectedFileName,
                               final String expectedPath, final String expectedVersion, final String expectedVendor,
                               final License expectedLicense, final boolean expectMessageDigest,
                               final int expectedLicenseCandidateFilesCount) {
        assertEquals(messagePrefix + "archiveType", ArchiveType.JAVA, archive.getArchiveType());
        assertEquals(messagePrefix + "fileName", expectedFileName, archive.getFileName());
        assertEquals(messagePrefix + "path", expectedPath, archive.getPath());
        assertEquals(messagePrefix + "version", expectedVersion, archive.getVersion());
        assertEquals(messagePrefix + "vendor", expectedVendor, archive.getVendor());
        assertEquals(messagePrefix + "detectionStatus", null, archive.getDetectionStatus());
        assertEquals(messagePrefix + "detectedLicenses", null, archive.getDetectedLicenses());
        if (expectMessageDigest) {
            assertEquals(messagePrefix + "message digest string length", EXPECTED_MESSAGE_DIGEST_STRING_LENGTH,
                    archive.getMessageDigestString().length());
        } else {
            assertEquals(messagePrefix + "no message digest expected", "", archive.getMessageDigestString());
        }
        assertEquals(messagePrefix + "number of license candidate files", expectedLicenseCandidateFilesCount,
                archive.getLicenseCandidateFiles().size());

        final Set<License> licenses = archive.getLicenses();
        if (expectedLicense == null) {
            assertEquals(messagePrefix + "licenses size", 0, licenses.size());
        } else {
            assertNotNull(messagePrefix + "licenses not present", licenses);
            assertEquals(messagePrefix + "licenses size", 1, licenses.size());
            assertEquals(messagePrefix + "licenses[0]", true, licenses.contains(expectedLicense));
        }
    }

    private License getExpectedLicense(final boolean expectLicense) {
        final License expectedLicenseInner = expectLicense
                ? getLicenseStoreData().getLicenseBySpdxIdentifier("Apache-2.0")
                : null;
        return expectedLicenseInner;
    }

    private File getFile(final String relativePath) {
        return new File(BASE_PATH + relativePath);
    }
}
