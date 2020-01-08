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
package org.aposin.licensescout.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.aposin.licensescout.configuration.ExecutionDatabaseConfiguration;
import org.aposin.licensescout.util.ILSLog;

/**
 * 
 */
public class DatabaseUtil {

    private DatabaseUtil() {
        // EMPTY
    }

    /**
     * @param databaseConfiguration 
     * @return a database connection
     * @throws SQLException
     */
    public static Connection getConnection(final ExecutionDatabaseConfiguration databaseConfiguration) throws SQLException {
        return DriverManager.getConnection(databaseConfiguration.getJdbcUrl(), databaseConfiguration.getUsername(),
                databaseConfiguration.getPassword());
    }

    /**
     * Logs the registered JDBC drivers.
     * 
     * <p>This method is intended for debugging if JDBC drivers are not loaded as expected.</p>
     * 
     * @param log the logger the logger
     */
    public static void dumpDrivers(ILSLog log) {
        final Enumeration<Driver> registeredDrivers = DriverManager.getDrivers();
        log.info("DriverManager registered drivers:");
        while (registeredDrivers.hasMoreElements()) {
            Driver driver = registeredDrivers.nextElement();
            log.info("" + driver.toString());
        }
    }
}
