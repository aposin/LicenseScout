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

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.BuildInfo;
import org.aposin.licensescout.configuration.ExecutionDatabaseConfiguration;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.license.DetectionStatus;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILSLog;
import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.CompositeTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.Test;

/**
 * Test case for {@link DatabaseWriter}.
 * 
 * <p>This test works by:</p>
 * <ol>
 * <li>Creating an in memory database instance (by opening the connection).</li>
 * <li>Creating the tables by running a script (using 'src/test/resources/database/licensescout-h2.sql').</li>
 * <li>Using the {@link DatabaseWriter} to insert data into the database.</li>
 * <li>Fetching the current contents of the database and comparing against an expected data set.</li>
 * <li>Dropping the database. This is done implicitly by terminating the VM.
 * Note that the maven-surefire-plugin usually forks for each test case.</li>
 * </ol>
 *
 * <p>NOTE: by using the parameter "DB_CLOSE_DELAY=-1" in the JDBC URL the database is configured to have the lifetime of the VM
 * (instead of the lifetime of a connection). This is necessary because there are multiple connections
 * in sequence (creating the tables, inserting data, fetching the results).</p>
 */
public class DatabaseWriterTest extends DBTestCase {

    private static final String DRIVER_CLASS = org.h2.Driver.class.getName();
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    // NOTE annotation "@Before" does not work in this case
    @Override
    public void setUp() throws Exception {
        super.setUp();
        createSchema();
    }

    private void createSchema() throws SQLException {
        RunScript.execute(JDBC_URL, USERNAME, PASSWORD, "src/test/resources/database/licensescout-h2.sql",
                StandardCharsets.UTF_8, false);
    }

    public DatabaseWriterTest(String name) {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, DRIVER_CLASS);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, JDBC_URL);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, USERNAME);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, PASSWORD);
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/test/resources/database/dataset.xml"));
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.NONE;
    }

    /**
     * Tests {@link DatabaseWriter#writeToDatabase(BuildInfo, List, ExecutionDatabaseConfiguration, ILSLog)}.
     * 
     * @throws Exception
     */
    @Test
    public void testWriteDatabase() throws Exception {

        final ILSLog log = TestUtil.createJavaUtilGlobalLog();
        final LicenseStoreData licenseStoreData = TestUtil.readLicenseStoreData(log);
        final BuildInfo buildInfo = createBuildInformation();
        final List<Archive> archives = new ArrayList<>();
        final Archive archive1 = createArchive1(licenseStoreData);
        archives.add(archive1);
        final Archive archive2 = createArchive2();
        archives.add(archive2);

        final ExecutionDatabaseConfiguration databaseConfiguration = createDatabaseConfiguration();
        DatabaseWriter.writeToDatabase(buildInfo, archives, databaseConfiguration, log);

        final List<String> tablesNamesToCheck = Arrays.asList("Builds", "LibraryData", "DetectedLicenses");
        final IDataSet expectedDataSet = new FlatXmlDataSetBuilder()
                .build(new File("src/test/resources/database/expectedForDatabaseWriterTest.xml"));

        assertDatabase(expectedDataSet, tablesNamesToCheck);
    }

    /**
     * Creates a build info object for testing.
     * @return a build info object
     */
    private BuildInfo createBuildInformation() {
        final BuildInfo buildInfo = new BuildInfo("name", "version", "date", "buildUrl", "licenseReportCsvUrl",
                "licenseReportHtmlUrl", "licenseReportTxtUrl");
        return buildInfo;
    }

    /**
     * @param licenseStoreData the data object containing information on licenses
     * @return an archive object for testing
     */
    private Archive createArchive1(final LicenseStoreData licenseStoreData) {
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName1", "version1", "path1");
        archive.setDetectionStatus(DetectionStatus.DETECTED);
        archive.setLegalStatus(LegalStatus.ACCEPTED);
        final List<License> detectedLicenses = new ArrayList<>();
        detectedLicenses.add(licenseStoreData.getLicenseBySpdxIdentifier("MIT"));
        detectedLicenses.add(licenseStoreData.getLicenseBySpdxIdentifier("Apache-2.0"));
        archive.setDetectedLicenses(detectedLicenses);
        archive.addLicense(licenseStoreData.getLicenseBySpdxIdentifier("MIT"), "path");
        return archive;
    }

    /**
     * @return an archive object for testing
     */
    private Archive createArchive2() {
        final Archive archive = new Archive(ArchiveType.JAVA, "fileName2", "version2", "path2");
        archive.setDetectionStatus(DetectionStatus.DETECTED);
        archive.setLegalStatus(LegalStatus.ACCEPTED);
        final List<License> detectedLicenses = new ArrayList<>();
        archive.setDetectedLicenses(detectedLicenses);
        return archive;
    }

    /**
     * @param expectedDataSet
     * @param tablesNamesToCheck
     * @throws SQLException
     * @throws Exception
     * @throws DataSetException
     * @throws DatabaseUnitException
     */
    private void assertDatabase(final IDataSet expectedDataSet, final List<String> tablesNamesToCheck)
            throws SQLException, Exception, DataSetException, DatabaseUnitException {
        final IDataSet databaseDataSet = getCurrentDataSet();
        checkDatabase(expectedDataSet, databaseDataSet, tablesNamesToCheck);
    }

    /**
     * @param expectedDataSet
     * @param databaseDataSet
     * @param tablesNamesToCheck
     * @throws DataSetException
     * @throws DatabaseUnitException
     */
    private void checkDatabase(final IDataSet expectedDataSet, final IDataSet databaseDataSet,
                               final List<String> tablesNamesToCheck)
            throws DataSetException, DatabaseUnitException {
        final DefaultColumnFilter columnFilter = createColumnFilter();
        for (final String tableName : tablesNamesToCheck) {
            final ITable actualTable = databaseDataSet.getTable(tableName);
            FilteredTableMetaData metaData = new FilteredTableMetaData(actualTable.getTableMetaData(), columnFilter);
            ITable actualTableFiltered = new CompositeTable(metaData, actualTable);
            final ITable expectedTable = expectedDataSet.getTable(tableName);
            Assertion.assertEquals(expectedTable, actualTableFiltered);
        }
    }

    /**
     * @return a column filter
     */
    private DefaultColumnFilter createColumnFilter() {
        String[] excludedColumnNameArray = new String[] { "Datetime" };
        final DefaultColumnFilter columnFilter = new DefaultColumnFilter();
        for (String columnName : excludedColumnNameArray) {
            columnFilter.excludeColumn(columnName);
        }
        return columnFilter;
    }

    /**
     * Obtains the dataset that represents the current state of the database.
     * @return a dataset
     * @throws Exception
     */
    private IDataSet getCurrentDataSet() throws Exception {
        return getConnection().createDataSet();
    }

    /**
     * Obtains a database configuration.
     * @return a database configuration
     */
    private ExecutionDatabaseConfiguration createDatabaseConfiguration() {
        final ExecutionDatabaseConfiguration databaseConfiguration = new ExecutionDatabaseConfiguration();
        databaseConfiguration.setJdbcUrl(JDBC_URL);
        databaseConfiguration.setUsername(USERNAME);
        databaseConfiguration.setPassword(PASSWORD);
        return databaseConfiguration;
    }
}