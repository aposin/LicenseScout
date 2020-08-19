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
package org.aposin.licensescout.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Base class for unit test of implementations of {@link ConfigFileHandler}
 */
public abstract class AbstractConfigFileHandlerTest {

    private ConfigFileHandler configFileHandler;

    /**
     * Initializes the ConfigFileHandler used for testing
     * @throws IOException
     */
    @Before
    public void initConfigFileHandler() throws IOException {
        this.configFileHandler = createConfigFileHandler();
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetProvidersInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getProvidersInputStream()) {
            assertStream(inputStream, "getProvidersInputStream", 288);
        }
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetNoticesInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getNoticesInputStream()) {
            assertStream(inputStream, "getNoticesInputStream", 555);
        }
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetLicensesInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getLicensesInputStream()) {
            assertStream(inputStream, "getLicensesInputStream", 357508);
        }
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetLicenseUrlMappingsInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getLicenseUrlMappingsInputStream()) {
            assertStream(inputStream, "getLicenseUrlMappingsInputStream", 611);
        }
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetLicenseNameMappingsInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getLicenseNameMappingsInputStream()) {
            assertStream(inputStream, "getLicenseNameMappingsInputStream", 769);
        }
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetGlobalFiltersInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getGlobalFiltersInputStream()) {
            assertStream(inputStream, "getGlobalFiltersInputStream", 69);
        }
    }

    @Test
    public void testGetCheckedArchivesInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getCheckedArchivesInputStream()) {
            assertStream(inputStream, "getCheckedArchivesInputStream", 1740);
        }
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetFilteredVendorNamesInputStream() throws IOException {
        try (final InputStream inputStream = configFileHandler.getFilteredVendorNamesInputStream()) {
            assertStream(inputStream, "getFilteredVendorNamesInputStream", 279);
        }
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testHasTemplateFileSuccess() throws IOException {
        final boolean result = configFileHandler.hasTemplateFile(getTemplateFilename());
        Assert.assertTrue("hasTemplateFile()", result);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testHasTemplateFileNull() throws IOException {
        final boolean result = configFileHandler.hasTemplateFile(null);
        Assert.assertFalse("hasTemplateFile()", result);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testHasTemplateFileNotExisting() throws IOException {
        final boolean result = configFileHandler.hasTemplateFile("not_existing");
        Assert.assertFalse("hasTemplateFile()", result);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetTemplateFileSuccess() throws IOException {
        final File templateFile = configFileHandler.getTemplateFile(getTemplateFilename());
        Assert.assertTrue("getTemplateFile(): file exists", templateFile.exists());
        Assert.assertTrue("getTemplateFile(): file readable", templateFile.canRead());
        Assert.assertEquals("getTemplateFile(): file length", 2316, templateFile.length());
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetTemplateFileNull() throws IOException {
        final File templateFile = configFileHandler.getTemplateFile(null);
        Assert.assertNull("getTemplateFile()", templateFile);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetTemplateFileNotExisting() throws IOException {
        final File templateFile = configFileHandler.getTemplateFile("not_existing");
        Assert.assertNull("getTemplateFile()", templateFile);
    }

    /**
     * Obtain the filename to use for a custom template file.
     * 
     * @return a filename
     */
    protected abstract String getTemplateFilename();

    /**
     * @param inputStream an input stream to read the file contents from
     * @param messagePrefix
     * @param expectedStreamLength
     * @throws IOException
     */
    private void assertStream(InputStream inputStream, final String messagePrefix, final int expectedStreamLength)
            throws IOException {
        Assert.assertNotNull(messagePrefix + " stream present", inputStream);
        final byte[] bytes = IOUtils.toByteArray(inputStream);
        Assert.assertEquals(messagePrefix + " stream length", expectedStreamLength, bytes.length);
    }

    /**
     * Creates the ConfigFileHandler to test.
     * @return a {@code ConfigFileHandler} instance for testing
     * @throws IOException 
     */
    protected abstract ConfigFileHandler createConfigFileHandler() throws IOException;

}
