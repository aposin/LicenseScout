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
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JavaUtilLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

/**
 * Unit tests for {@link JavaJarFinder}.
 * 
 * 
 */
public class BaseFinderTest {

    private ILFLog log;
    private LicenseStoreData licenseStoreData;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Logger.getGlobal().setLevel(Level.FINEST);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);
        Logger.getGlobal().addHandler(consoleHandler);
        // Handler[] handlers = Logger.getGlobal().getHandlers();
        // handlers[0].setLevel(Level.FINEST);
    }

    @Before
    public void setUp() throws Exception {
        log = new JavaUtilLog(Logger.getGlobal());
        licenseStoreData = createLicenseStoreData();
    }

    /**
     * @return the log
     */
    protected final ILFLog getLog() {
        return log;
    }

    private LicenseStoreData createLicenseStoreData() throws IOException, ParserConfigurationException, SAXException {
        final LicenseStoreData licenseStoreData = new LicenseStoreData();
        licenseStoreData.readLicenses(new File("src/test/resources/scans/licenses.xml"), null, false, getLog());
        return licenseStoreData;
    }

    protected LicenseStoreData getLicenseStoreData() {
        return licenseStoreData;
    }

    protected FinderResult doScan(final AbstractFinder finder, final File scanDirectory) throws Exception {
        finder.setScanDirectory(scanDirectory);
        return finder.findLicenses();
    }

    protected void assertResultsBase(final FinderResult finderResult, final File scanDirectory,
            final int expectedArchiveCount) {
        Assert.assertEquals("scanDirectory", scanDirectory, finderResult.getScanDirectory());
        Assert.assertEquals("archiveFiles", expectedArchiveCount, finderResult.getArchiveFiles().size());
    }

}
