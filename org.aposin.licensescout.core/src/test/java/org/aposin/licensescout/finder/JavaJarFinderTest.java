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

import java.util.logging.Logger;

import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JavaUtilLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link JavaJarFinder}.
 */
public class JavaJarFinderTest {

    /**
     * Test method for
     * {@link org.aposin.licensescout.license.ArtifactServerUtil#ArtifactServerUtil(java.lang.String, int, org.aposin.licensescout.util.ILFLog)}.
     */
    @Test
    public void testPomResolutionUsed() {
        final Logger logger = Logger.getGlobal();
        final ILFLog log = new JavaUtilLog(logger);
        LicenseStoreData licenseStoreData = null;
        final String serverBaseUrl = "https://repo.maven.apache.org/maven2_unaccessible/";
        final int timeout = 400;
        final RunParameters runParameters = new RunParameters();
        runParameters.setNexusCentralBaseUrl(serverBaseUrl);
        runParameters.setConnectTimeout(timeout);
        final JavaJarFinder finder = new JavaJarFinder(licenseStoreData, runParameters, log);
        Assert.assertFalse("isPomResolutionUsed()", finder.isPomResolutionUsed());
    }

}
