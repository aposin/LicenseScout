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
package org.aposin.licensescout.maven.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.aposin.licensescout.configuration.ExecutionDatabaseConfiguration;
import org.aposin.licensescout.configuration.ExecutionOutput;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.util.ILFLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ConfigurationHelper}.
 *
 */
public class ConfigurationHelperTest {

    /**
     * Test case for the method {@link ConfigurationHelper#getExecutionDatabaseConfiguration(DatabaseConfiguration, Settings, ILFLog)}.
     * @throws Exception 
     */
    @Test
    public void testGetExecutionDatabaseConfigurationSuccess() throws Exception {
        final Settings settings = createSettings();
        final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration("jdbcUrl", "serverId");
        final ExecutionDatabaseConfiguration executionDatabaseConfiguration = ConfigurationHelper
                .getExecutionDatabaseConfiguration(databaseConfiguration, settings, TestUtil.createTestLog());
        Assert.assertEquals("jdbcUrl", "jdbcUrl", executionDatabaseConfiguration.getJdbcUrl());
        Assert.assertEquals("username", "serverIdusername", executionDatabaseConfiguration.getUsername());
        Assert.assertEquals("password", "serverIdpassword", executionDatabaseConfiguration.getPassword());
    }

    /**
     * Test case for the method {@link ConfigurationHelper#getExecutionDatabaseConfiguration(DatabaseConfiguration, Settings, ILFLog)}.
     * @throws Exception 
     */
    @Test
    public void testGetExecutionDatabaseConfigurationNull() throws Exception {
        final Settings settings = createSettings();
        final DatabaseConfiguration databaseConfiguration = null;
        final ExecutionDatabaseConfiguration executionDatabaseConfiguration = ConfigurationHelper
                .getExecutionDatabaseConfiguration(databaseConfiguration, settings, TestUtil.createTestLog());
        Assert.assertNull("result ExecutionDatabaseConfiguration", executionDatabaseConfiguration);
    }

    /**
     * Test case for the method {@link ConfigurationHelper#getExecutionDatabaseConfiguration(DatabaseConfiguration, Settings, ILFLog)}.
     * @throws Exception 
     */
    @Test(expected = MojoExecutionException.class)
    public void testGetExecutionDatabaseConfigurationServerIdNotFound() throws Exception {
        final Settings settings = createSettings();
        final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration("jdbcUrl", "serverId_noexisting");
        ConfigurationHelper.getExecutionDatabaseConfiguration(databaseConfiguration, settings,
                TestUtil.createTestLog());
    }

    /**
     * Creates a settings instance for testing.
     * @return a settings instance
     * @throws SettingsBuildingException
     */
    private Settings createSettings() throws SettingsBuildingException {
        DefaultSettingsBuilderFactory settingsBuilderFactory = new DefaultSettingsBuilderFactory();
        SettingsBuilder settingsBuilder = settingsBuilderFactory.newInstance();
        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
        final File userSettingsFile = new File("src/test/resources/ConfigurationHelperTest/settings.xml");
        request.setUserSettingsFile(userSettingsFile);
        SettingsBuildingResult settingsBuildingResult = settingsBuilder.build(request);
        return settingsBuildingResult.getEffectiveSettings();
    }

    /**
     * Test case for the method {@link ConfigurationHelper#getExecutionOutputs(List, String, LSArtifact)}.
     * 
     */
    @Test
    public void testGetExecutionOutputsCustomUrl() {
        final Output output = new Output(OutputFileType.CSV, "filename", "url", new File("template"), "templatEncoding",
                "outputEncoding");
        final String expectedUrl = "url";
        assertGetExecutionOutputs(output, expectedUrl);
    }

    /**
     * Test case for the method {@link ConfigurationHelper#getExecutionOutputs(List, String, LSArtifact)}.
     * 
     */
    @Test
    public void testGetExecutionOutputsDefaultUrl() {
        final Output output = new Output(OutputFileType.CSV, "filename", null, new File("template"), "templatEncoding",
                "outputEncoding");
        final String expectedUrl = "http://server/repo/group/id/artifactId/version/artifactId-version.csv";
        assertGetExecutionOutputs(output, expectedUrl);
    }

    /**
     * @param output
     * @param expectedUrl
     */
    private void assertGetExecutionOutputs(final Output output, final String expectedUrl) {
        final List<Output> outputs = Arrays.asList(output);
        final String artifactBaseUrl = "http://server/repo/";
        final LSArtifact artifact = new LSArtifact("group.id", "artifactId", "version", "type", "");
        final List<ExecutionOutput> executionOutputs = ConfigurationHelper.getExecutionOutputs(outputs, artifactBaseUrl,
                artifact);
        Assert.assertEquals("resulting output count", 1, executionOutputs.size());
        final ExecutionOutput executionOutput = executionOutputs.get(0);
        Assert.assertEquals("outputFileType", OutputFileType.CSV, executionOutput.getType());
        Assert.assertEquals("url", expectedUrl, executionOutput.getUrl());
    }
}
