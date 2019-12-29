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
        final InputStream inputStream = configFileHandler.getProvidersInputStream();
        assertStream(inputStream, "getProvidersInputStream", 288);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetNoticesInputStream() throws IOException {
        final InputStream inputStream = configFileHandler.getNoticesInputStream();
        assertStream(inputStream, "getNoticesInputStream", 555);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetLicensesInputStream() throws IOException {
        final InputStream inputStream = configFileHandler.getLicensesInputStream();
        assertStream(inputStream, "getLicensesInputStream", 357508);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetLicenseUrlMappingsInputStream() throws IOException {
        final InputStream inputStream = configFileHandler.getLicenseUrlMappingsInputStream();
        assertStream(inputStream, "getLicenseUrlMappingsInputStream", 611);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetLicenseNameMappingsInputStream() throws IOException {
        final InputStream inputStream = configFileHandler.getLicenseNameMappingsInputStream();
        assertStream(inputStream, "getLicenseNameMappingsInputStream", 769);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetGlobalFiltersInputStream() throws IOException {
        final InputStream inputStream = configFileHandler.getGlobalFiltersInputStream();
        assertStream(inputStream, "getGlobalFiltersInputStream", 69);
    }

    @Test
    public void testGetCheckedArchivesInputStream() throws IOException {
        final InputStream inputStream = configFileHandler.getCheckedArchivesInputStream();
        assertStream(inputStream, "getCheckedArchivesInputStream", 1740);
    }

    /**
     * @throws IOException  
     */
    @Test
    public void testGetFilteredVendorNamesInputStream() throws IOException {
        final InputStream inputStream = configFileHandler.getFilteredVendorNamesInputStream();
        assertStream(inputStream, "getFilteredVendorNamesInputStream", 279);
    }

    /**
     * @param inputStream
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
