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

import java.io.File;
import java.util.ArrayList;

import org.aposin.licensescout.core.test.util.TestArtifactServerUtil;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.execution.ScanLocation;
import org.aposin.licensescout.license.IArtifactServerUtil;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILSLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link JavaJarFinder}.
 * 
 */
public class JavaJarFinderMiscTest extends BaseFinderTest {

    /**
     * Test method for {@link JavaJarFinder#isPomResolutionUsed()}.
     */
    @Test
    public void testPomResolutionUsed() {
        final ILSLog log = TestUtil.createTestLog();
        LicenseStoreData licenseStoreData = null;
        final IArtifactServerUtil artifactServerUtil = new TestArtifactServerUtil();
        final AbstractJavaFinder finder = new JavaJarFinder(licenseStoreData, artifactServerUtil, log);
        Assert.assertFalse("isPomResolutionUsed()", finder.isPomResolutionUsed());
    }

    /**
     * Test method for {@link JavaJarFinder#findLicenses()}.
     * 
     * <p>Uses scan location with empty list of files and directory not set.</p>
     * 
     * @throws Exception 
     */
    @Test
    public void testScanEmptyScanLocationFiles() throws Exception {
        final AbstractJavaFinder finder = createFinder();
        finder.debug = true;
        final File scanDirectory = null;
        final int expectedArchiveCount = 0;
        final ScanLocation scanLocation = new ScanLocation(new ArrayList<>());
        final FinderResult finderResult = doScan(finder, scanLocation);
        assertScanDirectory(finderResult, scanDirectory);
        assertArchiveFileCount(finderResult, expectedArchiveCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractJavaFinder createFinder() {
        final LicenseStoreData licenseStoreData = getLicenseStoreData();
        final IArtifactServerUtil artifactServerUtil = new TestArtifactServerUtil(true);
        return new JavaJarFinder(licenseStoreData, artifactServerUtil, getLog());
    }

}
