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
import java.util.List;

import org.aposin.licensescout.core.test.util.CreateJarFileHelper;
import org.aposin.licensescout.core.test.util.TestUtil;

/**
 * Unit test for {@link ZipConfigFileHandler}.
 * 
 * <p>The test packs configuration files from "src/test/resources/configuration_sample_test/"
 * into a temporary JAR file at "src/test/resources/configuration_sample_test.zip".</p>
 */
public class ZipConfigFileHandlerTest extends AbstractConfigFileHandlerTest {

    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    protected ConfigFileHandler createConfigFileHandler() throws IOException {
        final File baseDirectory = new File("src/test/resources/configuration_sample_test/");
        final List<File> files = CreateJarFileHelper.collectFiles(baseDirectory);
        final File configBundleFile = new File("src/test/resources/configuration_sample_test.zip");
        CreateJarFileHelper.createJarArchiveZip(configBundleFile, files, baseDirectory.toPath());
        final ConfigFileParameters configFileParameters = new ConfigFileParameters();
        return new ZipConfigFileHandler(configBundleFile, configFileParameters, TestUtil.createTestLog());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTemplateFilename() {
        return "license_report_txt_custom.vm";
    }

}
