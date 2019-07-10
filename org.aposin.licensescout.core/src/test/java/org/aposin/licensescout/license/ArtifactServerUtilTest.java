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

import org.aposin.licensescout.util.NullLog;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class ArtifactServerUtilTest {

    /**
     * Test method for
     * {@link org.aposin.licensescout.license.ArtifactServerUtil#ArtifactServerUtil(java.lang.String, int, org.aposin.licensescout.util.ILFLog)}.
     */
    @Test
    public void testArtifactServerUtil() {
        final String serverBaseUrl = "https://repo.maven.apache.org/maven2_unaccessible/";
        final int timeout = 400;
        final ArtifactServerUtil artifactServerUtil = new ArtifactServerUtil(serverBaseUrl, timeout, new NullLog());
        Assert.assertFalse("isCachedCheckAccess()", artifactServerUtil.isCachedCheckAccess());
    }

    /**
     * Test method for
     * {@link org.aposin.licensescout.license.ArtifactServerUtil#addLicensesFromPom(java.io.InputStream, org.aposin.licensescout.archive.Archive, java.lang.String, org.aposin.licensescout.license.LicenseStoreData, org.aposin.licensescout.util.ILFLog)}.
     */
    //	@Test
    public void testAddLicensesFromPom() {
        //		fail("Not yet implemented");
    }

}
