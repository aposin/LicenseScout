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
package org.aposin.licensescout.core.test.util;

import java.io.File;
import java.util.logging.Logger;

import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JavaUtilLog;
import org.aposin.licensescout.util.NullLog;

//import java.util.logging.Logger;
//
//import org.aposin.licensescout.configuration.RunParameters;
//import org.aposin.licensescout.util.ILFLog;
//import org.aposin.licensescout.util.JavaUtilLog;
//import org.aposin.licensescout.util.NullLog;

/**
 * Utility methods for unit tests.
 *
 */
public abstract class TestUtil {

    /**
     * Determines the behaviour of {@link #createTestLog()}.
     */
    public static boolean DO_LOGGING = false;

    /**
     * Creates run parameters for testing.
     * @return a run parameters instance
     */
    public static RunParameters createRunParameters() {
        final String serverBaseUrl = "https://repo.maven.apache.org/maven2_unaccessible/";
        final int timeout = 400;
        final RunParameters runParameters = new RunParameters();
        runParameters.setNexusCentralBaseUrl(serverBaseUrl);
        runParameters.setConnectTimeout(timeout);
        return runParameters;
    }

    /**
     * Reads LicenseStoreData from a file for testing.
     * @param log
     * @return
     * @throws Exception
     */
    public static LicenseStoreData readLicenseStoreData(final ILFLog log) throws Exception {
        LicenseStoreData licenseStoreData = new LicenseStoreData();
        licenseStoreData.readLicenses(new File("src/test/resources/configuration/licenses.xml"), null, false, log);
        return licenseStoreData;
    }

    /**
     * Obtains a logger for testing that uses the java.util global logger.
     * @return an initialized log instance for testing
     */
    public static ILFLog createJavaUtilGlobalLog() {
        return new JavaUtilLog(Logger.getGlobal());
    }

    /**
     * Obtains a logger for testing that sinks all logging.
     * @return a logger
     */
    public static ILFLog createNullLog() {
        return new NullLog();
    }

    /**
     * Obtains a logger for testing depending on a flag.
     * @return a logger
     */
    public static ILFLog createTestLog() {
        return DO_LOGGING ? createJavaUtilGlobalLog() : createNullLog();
    }

}
