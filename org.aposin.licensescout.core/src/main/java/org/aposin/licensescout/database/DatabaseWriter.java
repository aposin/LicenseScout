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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.configuration.BuildInfo;
import org.aposin.licensescout.configuration.DatabaseConfiguration;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.model.Provider;
import org.aposin.licensescout.util.ILFLog;

/**
 * Writes build an library data to a database.
 * 
 */
public class DatabaseWriter {

    private DatabaseWriter() {
        // EMPTY
    }

    /**
     * Writes the information on one build execution to the database.
     * @param buildInfo
     * @param archives
     * @param databaseConfiguration 
     * @param log
     */
    public static void writeToDatabase(final BuildInfo buildInfo, final List<Archive> archives,
                                       final DatabaseConfiguration databaseConfiguration, final ILFLog log) {
        // for debugging
        DatabaseUtil.dumpDrivers(log);
        try (final Connection connection = DatabaseUtil.getConnection(databaseConfiguration)) {
            final int buildPK = insertBuild(buildInfo, connection);
            for (final Archive archive : archives) {
                final int libraryPK = insertLibrary(buildPK, archive, connection);
                final List<License> detectedLicenses = archive.getDetectedLicenses();
                for (final License detectedLicense : detectedLicenses) {
                    insertDetectedLicense(libraryPK, detectedLicense, connection);
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    /**
     * @param buildInfo
     * @param connection
     * @throws SQLException
     */
    private static int insertBuild(final BuildInfo buildInfo, final Connection connection) throws SQLException {
        // NOTE: Datetime is set by the DBMS
        try (final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Builds (Buildname, Version, URL_Build, URL_Licensereport_CSV, URL_Licensereport_HTML, URL_Licensereport_TXT) VALUES (?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, convertNull(buildInfo.getName()));
            statement.setString(2, convertNull(buildInfo.getVersion()));
            statement.setString(3, convertNull(buildInfo.getBuildUrl()));
            statement.setString(4, convertNull(buildInfo.getLicenseReportCsvUrl()));
            statement.setString(5, convertNull(buildInfo.getLicenseReportHtmlUrl()));
            statement.setString(6, convertNull(buildInfo.getLicenseReportTxtUrl()));
            statement.executeUpdate();
            try (final ResultSet keysResultSet = statement.getGeneratedKeys();) {
                if (keysResultSet.next()) {
                    return keysResultSet.getInt(1);
                }
            }
            return -1;
        }
    }

    private static String convertNull(final String s) {
        return s == null ? "(null)" : s;
    }

    /**
     * @param buildPK
     * @param archive
     * @param connection
     * @return
     * @throws SQLException
     */
    private static int insertLibrary(final int buildPK, final Archive archive, final Connection connection)
            throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO LibraryData (FK_Build_Id, Selected_License, Filename, Provider, Version, Type, Message_Digest, Detection_Status, Legal_Status, Documentation_Link) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, buildPK);
            statement.setString(2, getLicenseName(archive));
            statement.setString(3, convertNull(archive.getFileName()));
            statement.setString(4, getProviderName(archive));
            statement.setString(5, getLibraryVersion(archive));
            statement.setString(6, archive.getArchiveType().name());
            statement.setString(7, archive.getMessageDigestString());
            statement.setString(8, archive.getDetectionStatus().name());
            statement.setString(9, archive.getLegalStatus().name());
            statement.setString(10, convertNull(archive.getDocumentationUrl()));
            statement.executeUpdate();
            try (final ResultSet keysResultSet = statement.getGeneratedKeys();) {
                if (keysResultSet.next()) {
                    return keysResultSet.getInt(1);
                }
            }
            return -1;
        }
    }

    /**
     * @param libraryPK
     * @param license
     * @param connection
     * @throws SQLException
     */
    private static void insertDetectedLicense(final int libraryPK, final License license, final Connection connection)
            throws SQLException {
        try (final PreparedStatement statement = connection
                .prepareStatement("INSERT INTO DetectedLicenses (FK_LibraryData_Id, License_Name) VALUES (?, ?)")) {
            statement.setInt(1, libraryPK);
            statement.setString(2, getLicenseName(license));

            statement.executeUpdate();
        }
    }

    private static String getLicenseName(final Archive archive) {
        final Set<License> licenses = archive.getLicenses();
        switch (licenses.size()) {
            case 0:
                return "";
            case 1:
                return getLicenseName(licenses.iterator().next());
            default:
                return "not unique";
        }
    }

    private static String getProviderName(final Archive archive) {
        final Provider provider = archive.getProvider();
        if (provider != null) {
            return provider.getName();
        }
        return "";
    }

    private static String getLibraryVersion(final Archive archive) {
        final String version = archive.getVersion();
        if (version != null) {
            return version;
        }
        return "";
    }

    private static String getLicenseName(final License license) {
        return LicenseUtil.getLicenseNameWithVersion(license);
    }
}
