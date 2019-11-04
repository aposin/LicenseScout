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
package org.aposin.licensescout.mojo;

import java.io.File;
import java.util.logging.Logger;

import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.finder.AbstractFinder;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JavaUtilLog;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractScanMojoTest {

    /**
     * Test case for the method {@link AbstractScanMojo#createFinder(org.aposin.licensescout.license.LicenseStoreData, ILFLog)}.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateFinder() throws Exception {
        final AbstractScanMojo scanMojo = createMojo();
        final RunParameters runParameters = createRunParameters();
        final AbstractFinder finder = scanMojo.createFinder(null, runParameters, getLog());
        Assert.assertEquals("class type returned by createFinder()", getExpectedFinderClass(), finder.getClass());
    }

    private RunParameters createRunParameters() {
        final String serverBaseUrl = "https://repo.maven.apache.org/maven2_unaccessible/";
        final int timeout = 400;
        final RunParameters runParameters = new RunParameters();
        runParameters.setNexusCentralBaseUrl(serverBaseUrl);
        runParameters.setConnectTimeout(timeout);
        return runParameters;
    }

    protected abstract Class<?> getExpectedFinderClass();

    /**
     * Test case for the method {@link AbstractScanMojo#getArchiveType()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetArchiveType() throws Exception {
        final AbstractScanMojo scanMojo = createMojo();
        Assert.assertEquals("wrong type returned by getArchiveType()", getExpectedArchiveType(),
                scanMojo.getArchiveType());
    }

    protected abstract AbstractScanMojo createMojo();

    protected abstract ArchiveType getExpectedArchiveType();

    /**
     * Test case for the method {@link AbstractScanMojo#createDirectoryIfNotExists(File)}.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateDirectoryIfNotExists() throws Exception {
        final AbstractScanMojo scanMojo = createMojo();
        File directory = new File("./testdir");
        directory.delete();
        scanMojo.createDirectoryIfNotExists(directory);
        Assert.assertEquals(
                "createDirectoryIfNotExists(): expected directory not existing after creating or is not a directory",
                true, directory.exists() && directory.isDirectory());
    }

    /**
     * Obtains a logger.
     * @return a logger
     */
    protected static ILFLog getLog() {
        return new JavaUtilLog(Logger.getGlobal());
    }

}
