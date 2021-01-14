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
package org.aposin.licensescout.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.model.Notices;
import org.aposin.licensescout.model.Providers;
import org.aposin.licensescout.util.NullLog;

/**
 * Unit tests for {@link LicenseUtil}.
 * 
 * @see LicenseUtil
 *
 */
public class LicenseUtilTestUtil {

    private static final String CONFIGURATION_BASE_PATH = "src/test/resources/configuration/";
    private static final String CHECKEDARCHIVES_PATH = CONFIGURATION_BASE_PATH + "checkedarchives.csv";
    private static final String LICENSES_PATH = CONFIGURATION_BASE_PATH + "licenses.xml";

    /**
     * Create {@link LicenseStoreData} for tests.
     * @return an initialized {@link LicenseStoreData} object
     * @throws Exception 
     */
    public static LicenseStoreData createLicenseStoreData() throws Exception {
        final LicenseStoreData licenseStoreData = new LicenseStoreData();
        final File licensesFile = new File(LICENSES_PATH);
        try (final InputStream inputStream = new FileInputStream(licensesFile)) {
            licenseStoreData.readLicenses(inputStream, new Notices(), false, new NullLog());
        }
        return licenseStoreData;
    }

    /**
     * @param licenseStoreData the data object containing information on licenses
     * @return an object containing checked archives information
     * @throws IOException
     */
    public static LicenseCheckedList createLicenseCheckedList(final LicenseStoreData licenseStoreData)
            throws IOException {
        TestUtil.setDefaultMessageDigestAlgorithm();
        final LicenseCheckedList checkedArchives = new LicenseCheckedList();
        final File checkedArchivesPathname = new File(CHECKEDARCHIVES_PATH);
        try (final InputStream inputStream = new FileInputStream(checkedArchivesPathname)) {
            checkedArchives.readCsv(inputStream, "UTF-8", licenseStoreData, new Providers(), new Notices(), new NullLog());
        }
        return checkedArchives;
    }

    /**
     * @param licenseIdentifiers list of SPDX identifiers - may be empty
     * @param licenseStoreData the data object containing information on licenses
     * @return a list of licenses, maybe empty
     */
    public static List<License> createLicenseList(final String[] licenseIdentifiers,
                                                  final LicenseStoreData licenseStoreData) {
        final List<License> licenses = new ArrayList<>();
        for (final String licenseIdentifier : licenseIdentifiers) {
            final License license = licenseStoreData.getLicenseBySpdxIdentifier(licenseIdentifier);
            if (license == null) {
                System.err.println("License not found in store: " + licenseIdentifier);
            }
            licenses.add(license);
        }
        return licenses;
    }

    /**
     * Compares two collections of licenses for equality.
     * @param a one collection
     * @param b another collection
     * @return true if the collections are equal, false otherwise
     */
    public static boolean areEqual(final Collection<License> a, final Collection<License> b) {
        boolean collectionsAreEqual = a.containsAll(b) && b.containsAll(a);
        return collectionsAreEqual;
    }

}
