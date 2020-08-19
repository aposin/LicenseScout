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

import org.aposin.licensescout.core.test.util.TestUtil;

/**
 * Unit test for {@link FilesystemConfigFileHandler}
 * 
 * <p>The test reads configuration files from "src/test/resources/configuration_sample_test/".</p>
 */
public class FileSystemConfigFileHandlerTest extends AbstractConfigFileHandlerTest {

    private static final String BASE_DIRECTORY = "src/test/resources/configuration_sample_test/";

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConfigFileHandler createConfigFileHandler() {
        final ConfigFileParameters configFileParameters = new ConfigFileParameters();

        configFileParameters.setProvidersFilename(new File(BASE_DIRECTORY + "providers.xml"));
        configFileParameters.setNoticesFilename(new File(BASE_DIRECTORY + "notices.xml"));
        configFileParameters.setLicensesFilename(new File(BASE_DIRECTORY + "licenses.xml"));
        configFileParameters.setLicenseUrlMappingsFilename(new File(BASE_DIRECTORY + "urlmappings.csv"));
        configFileParameters.setLicenseNameMappingsFilename(new File(BASE_DIRECTORY + "namemappings.csv"));
        configFileParameters.setGlobalFiltersFilename(new File(BASE_DIRECTORY + "globalfilters.csv"));
        configFileParameters.setCheckedArchivesFilename(new File(BASE_DIRECTORY + "checkedarchives.csv"));
        configFileParameters.setFilteredVendorNamesFilename(new File(BASE_DIRECTORY + "filteredvendornames.csv"));

        return new FilesystemConfigFileHandler(configFileParameters, TestUtil.createTestLog());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTemplateFilename() {
        return "src/test/resources/configuration_sample_test/license_report_txt_custom.vm";
    }

}
