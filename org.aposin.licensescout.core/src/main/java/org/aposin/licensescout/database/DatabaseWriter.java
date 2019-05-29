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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * Writes alarms to a database.
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
        try (final Connection connection = DatabaseUtil.getConnection(databaseConfiguration);
                final Statement statement = connection.createStatement();) {
            final int buildPK = insertBuild(buildInfo, statement);
            for (final Archive archive : archives) {
                final int libraryPK = insertLibrary(buildPK, archive, statement);
                final List<License> detectedLicenses = archive.getDetectedLicenses();
                for (final License detectedLicense : detectedLicenses) {
                    insertDetectedLicense(libraryPK, detectedLicense, statement);
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    /**
     * @param buildInfo
     * @param statement
     * @throws SQLException
     */
    private static int insertBuild(final BuildInfo buildInfo, final Statement statement) throws SQLException {
        final StringBuilder sb = new StringBuilder();
        // NOTE: Datetime is set by the DBMS
        sb.append(
                "INSERT INTO Builds (Buildname, Version, URL_Build, URL_Licensereport_CSV, URL_Licensereport_HTML, URL_Licensereport_TXT) VALUES ('");
        sb.append(buildInfo.getName());
        sb.append("','");
        sb.append(buildInfo.getVersion());
        sb.append("','");
        sb.append(buildInfo.getBuildUrl());
        sb.append("','");
        sb.append(buildInfo.getLicenseReportCsvUrl());
        sb.append("','");
        sb.append(buildInfo.getLicenseReportHtmlUrl());
        sb.append("','");
        sb.append(buildInfo.getLicenseReportTxtUrl());
        sb.append("')");
        final String sql = sb.toString();
        statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        try (final ResultSet keysResultSet = statement.getGeneratedKeys();) {
            if (keysResultSet.next()) {
                return keysResultSet.getInt("GENERATED_KEY");
            }
        }
        return -1;
    }

    /**
     * @param statement
     * @throws SQLException
     */
    private static int insertLibrary(final int buildPK, final Archive archive, final Statement statement)
            throws SQLException {
        final StringBuilder sb = new StringBuilder();
        sb.append(
                "INSERT INTO LibraryData (FK_Build_Id, Selected_License, Filename, Provider, Version, Type, Message_Digest, Detection_Status, Legal_Status, Documentation_Link)");
        sb.append("VALUES ('");
        sb.append(buildPK);
        sb.append("','");
        sb.append(getLicenseName(archive));
        sb.append("','");
        sb.append(archive.getFileName());
        sb.append("','");
        sb.append(getProviderName(archive));
        sb.append("','");
        sb.append(getLibraryVersion(archive));
        sb.append("','");
        sb.append(archive.getArchiveType().name());
        sb.append("','");
        sb.append(archive.getMessageDigestString());
        sb.append("','");
        sb.append(archive.getDetectionStatus().name());
        sb.append("','");
        sb.append(archive.getLegalStatus().name());
        sb.append("','");
        sb.append(archive.getDocumentationUrl());
        sb.append("')");
        final String sql = sb.toString();
        statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        try (final ResultSet keysResultSet = statement.getGeneratedKeys();) {
            if (keysResultSet.next()) {
                return keysResultSet.getInt("GENERATED_KEY");
            }
        }
        return -1;
    }

    /**
     * @param statement
     * @throws SQLException
     */
    private static void insertDetectedLicense(final int libraryPK, final License license, final Statement statement)
            throws SQLException {
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO DetectedLicenses (FK_LibraryData_Id, License_Name)");
        sb.append("VALUES ('");
        sb.append(libraryPK);
        sb.append("','");
        sb.append(getLicenseName(license));
        sb.append("')");
        final String sql = sb.toString();
        statement.executeUpdate(sql);
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
