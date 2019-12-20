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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.BuildInfo;
import org.aposin.licensescout.configuration.DatabaseConfiguration;
import org.aposin.licensescout.model.Alarm;
import org.aposin.licensescout.util.ILFLog;

/**
 * Writes alarms to a database.
 * 
 * @see Alarm
 *
 */
public class AlarmWriter {

    private AlarmWriter() {
        // EMPTY
    }

    /**
     * Writes alarms to the database.
     * 
     * @param buildInfo
     * @param alarms list of alarm objects
     * @param databaseConfiguration
     * @param log
     */
    public static void writeAlarms(final BuildInfo buildInfo, final List<Alarm> alarms,
                                   final DatabaseConfiguration databaseConfiguration, final ILFLog log) {
        try (final Connection connection = DatabaseUtil.getConnection(databaseConfiguration);
                final Statement statement = connection.createStatement();) {
            for (final Alarm alarm : alarms) {
                final StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO Alarms ");
                sb.append("VALUES ('");
                sb.append(buildInfo.getName());
                sb.append("',')");
                sb.append(buildInfo.getVersion());
                sb.append("',')");
                sb.append(buildInfo.getDate());
                sb.append("',')");
                sb.append(alarm.getAlarmCause().name());
                sb.append("',')");
                final Archive archive = alarm.getArchive();
                sb.append(archive.getFileName());
                sb.append("')");
                final String sql = sb.toString();
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }
}
