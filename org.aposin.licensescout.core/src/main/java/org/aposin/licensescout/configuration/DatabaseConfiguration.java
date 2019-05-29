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

import org.apache.maven.plugins.annotations.Parameter;
import org.aposin.licensescout.mojo.AbstractScanMojo;

/**
 * Database configuration specification for {@link AbstractScanMojo}.
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
     * The user name to use for a database.
     */
    @Parameter(property = "username", required = true)
    private String username;

    /**
     * The password to use for a database.
     * 
     */
    @Parameter(property = "password", required = false)
    private String password;

    /**
     * @return the jdbcUrl
     */
    public final String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * @param jdbcUrl the jdbcUrl to set
     */
    public final void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * @return the username
     */
    public final String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public final void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public final String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public final void setPassword(String password) {
        this.password = password;
    }

}
