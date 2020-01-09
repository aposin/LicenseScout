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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.aposin.licensescout.configuration.ExecutionDatabaseConfiguration;
import org.aposin.licensescout.configuration.ExecutionOutput;
import org.aposin.licensescout.util.ILSLog;

/**
 * Helper class for handling database configurations and outputs.
 * 
 * <p>Used to convert Maven configuration objects to execution configuration objects.</p>
 *
 * @see DatabaseConfiguration
 * @see ExecutionDatabaseConfiguration
 * @see Output
 * @see ExecutionOutput
 */
public class ConfigurationHelper {

    private static final IArtifactUrlBuilder NEXUS_ARTIFACT_URL_BUILDER = new NexusArtifactUrlBuilder();

    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigurationHelper() {
        // EMPTY
    }

    /**
     * Converts Maven database configuration to execution database configuration.
     * 
     * <p>This resolves the server definition given by ID from the settings and takes the DB connection
     * credentials from there.</p>
     * 
     * @param databaseConfiguration
     * @param settings the settings.xml object
     * @param log the logger
     * @return a database configuration for the execution engine
     * @throws MojoExecutionException if the server ID is not found in settings, or username or password are not specified in the server definition
     */
    public static ExecutionDatabaseConfiguration getExecutionDatabaseConfiguration(final DatabaseConfiguration databaseConfiguration,
                                                                                   final Settings settings,
                                                                                   final ILSLog log)
            throws MojoExecutionException {
        if (databaseConfiguration == null) {
            return null;
        }
        final ExecutionDatabaseConfiguration executionDatabaseConfiguration = new ExecutionDatabaseConfiguration();
        executionDatabaseConfiguration.setJdbcUrl(databaseConfiguration.getJdbcUrl());
        final String serverId = databaseConfiguration.getServerId();
        log.info("Resolving database server ID: " + serverId);
        final Server server = settings.getServer(serverId);
        if (server == null) {
            throw new MojoExecutionException(
                    String.format("No definition found for server ID %s in settings.xml", serverId));
        }
        final String username = server.getUsername();
        final String password = server.getPassword();
        if (username == null) {
            throw new MojoExecutionException(
                    String.format("No username defined in server specification with ID %s in settings.xml", serverId));
        }
        if (password == null) {
            throw new MojoExecutionException(
                    String.format("No password defined in server specification with ID %s in settings.xml", serverId));
        }
        executionDatabaseConfiguration.setUsername(username);
        executionDatabaseConfiguration.setPassword(password);
        return executionDatabaseConfiguration;
    }

    /**
     * Converts list of Maven output configurations to list of execution output configurations.
     * 
     * @param outputs a list of Maven configuration outputs
     * @param artifactBaseUrl the base URL for creating default URLs for the report file on an artifact server
     * @param reportProtoTypeArtifact 
     * @return a list of execution outputs
     */
    public static List<ExecutionOutput> getExecutionOutputs(final List<Output> outputs, final String artifactBaseUrl,
                                                            final LSArtifact reportProtoTypeArtifact) {
        final List<ExecutionOutput> executionOutputs = new ArrayList<>();
        for (final Output output : outputs) {
            final ExecutionOutput executionOutput = new ExecutionOutput();
            executionOutput.setType(output.getType());
            executionOutput.setFilename(output.getFilename());
            executionOutput.setUrl(getArtifactUrl(output, artifactBaseUrl, reportProtoTypeArtifact));
            executionOutput.setTemplate(output.getTemplate());
            executionOutput.setTemplateEncoding(output.getTemplateEncoding());
            executionOutput.setOutputEncoding(output.getOutputEncoding());
            executionOutputs.add(executionOutput);
        }
        return executionOutputs;
    }

    /**
     * @param output
     * @param artifactBaseUrl 
     * @param reportProtoTypeArtifact 
     * @return a string containing the URL of an artifact on the artifact server
     */
    private static String getArtifactUrl(final Output output, final String artifactBaseUrl,
                                         final LSArtifact reportProtoTypeArtifact) {
        if (!StringUtils.isEmpty(output.getUrl()) || StringUtils.isEmpty(artifactBaseUrl)) {
            return output.getUrl();
        }
        return NEXUS_ARTIFACT_URL_BUILDER.buildArtifactUrl(artifactBaseUrl, reportProtoTypeArtifact,
                output.getType().getArtifactType());
    }
}
