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

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Database configuration specification for scan MOJOs.
 * 
 * <p>This is an auxiliary class for the Maven configuration of the license scout.</p>
 * 
 */
public class DatabaseConfiguration {

    /**
     * JDBC URL for a database.
     */
    @Parameter(property = "jdbcUrl", required = true)
    private String jdbcUrl;

    /**
     * The server ID referencing a server definition in settings.xml.
     */
    @Parameter(property = "serverId", required = true)
    private String serverId;

    /**
     * Constructor used by the Maven runtime system.
     */
    public DatabaseConfiguration() {
        // EMPTY
    }

    /**
     * Constructor for testing.
     * 
     * @param jdbcUrl
     * @param serverId
     */
    public DatabaseConfiguration(String jdbcUrl, String serverId) {
        this.jdbcUrl = jdbcUrl;
        this.serverId = serverId;
    }

    /**
     * @return the jdbcUrl
     */
    public final String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * @return the serverId
     */
    public final String getServerId() {
        return serverId;
    }
}
