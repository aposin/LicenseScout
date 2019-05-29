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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link LicenseUtil}.
 * 
 * @see LicenseUtil
 */
public class LicenseUtilDetectLicensesTest {

    private static final boolean DUMP_RESULTS = false;

    /**
     * Test for {@link LicenseUtil#detectLicenses(java.io.BufferedReader, LicenseStoreData)}
     * @throws Exception 
     */
    @Test
    public void testDetectLicensesEmpty() throws Exception {
        final String licenseTextRelativeFilenam = "empty/empty.txt";
        final String[] expectedLicenseIdentifiers = new String[0];
        final String prefix = "empty";
        doTestDetectLicenses(licenseTextRelativeFilenam, expectedLicenseIdentifiers, prefix);
    }

    /**
     * Test for {@link LicenseUtil#detectLicenses(java.io.BufferedReader, LicenseStoreData)}
     * @throws Exception 
     */
    @Test
    public void testDetectLicensesApache2() throws Exception {
        final String licenseTextRelativeFilenam = "apache2/LICENSE-2.0.txt";
        final String[] expectedLicenseIdentifiers = new String[] { "Apache-2.0" };
        final String prefix = "Apache-2.0";
        doTestDetectLicenses(licenseTextRelativeFilenam, expectedLicenseIdentifiers, prefix);
    }

    /**
     * Test for {@link LicenseUtil#detectLicenses(java.io.BufferedReader, LicenseStoreData)}
     * @throws Exception 
     */
    @Test
    public void testDetectLicensesCommonsPool2Notice() throws Exception {
        final String licenseTextRelativeFilenam = "commons_pool2/NOTICE.txt";
        final String[] expectedLicenseIdentifiers = new String[] { "PublicDomain" };
        final String prefix = "commons_pool2";
        doTestDetectLicenses(licenseTextRelativeFilenam, expectedLicenseIdentifiers, prefix);
    }

    /**
     * Test for {@link LicenseUtil#detectLicenses(java.io.BufferedReader, LicenseStoreData)}
     * @throws Exception 
     */
    @Test
    public void testDetectLicensesNewCkeditor() throws Exception {
        final String licenseTextRelativeFilenam = "ckeditor/LICENSE.md";
        final String[] expectedLicenseIdentifiers = new String[] { "GPL-2.0", "LGPL-2.1", "MPL-1.1", "NPL-1.0" };
        final String prefix = "ckeditor";
        doTestDetectLicenses(licenseTextRelativeFilenam, expectedLicenseIdentifiers, prefix);
    }

    /**
     * Test for {@link LicenseUtil#detectLicenses(java.io.BufferedReader, LicenseStoreData)}
     * @throws Exception 
     */
    @Test
    public void testDetectLicensesNewJavaAnnotationSource12() throws Exception {
        final String licenseTextRelativeFilenam = "javax_annotation_source12/LICENSE_CDDL.txt";
        final String[] expectedLicenseIdentifiers = new String[] { "CDDL-1.0", "GPL-2.0", "LGPL-2.0" };
        final String prefix = "javax.annotation.source12 LICENSE_CDDL";
        doTestDetectLicenses(licenseTextRelativeFilenam, expectedLicenseIdentifiers, prefix);
    }

    /**
     * @param licenseTextRelativeFilenam
     * @param expectedLicenseIdentifiers
     * @param prefix 
     * @throws Exception
     */
    private void doTestDetectLicenses(final String licenseTextRelativeFilenam,
                                      final String[] expectedLicenseIdentifiers, final String prefix)
            throws Exception {
        final BufferedReader bufferedReader = getLicenseBufferedReader(licenseTextRelativeFilenam);
        final LicenseStoreData licenseStoreData = LicenseUtilTestUtil.createLicenseStoreData();
        final List<License> expectedLicenses = LicenseUtilTestUtil.createLicenseList(expectedLicenseIdentifiers,
                licenseStoreData);
        final Collection<License> result = callDetectLicenses(bufferedReader, licenseStoreData);
        final String message = "detected licenses (" + prefix + ")";
        if (DUMP_RESULTS) {
            System.out.println(message);
            System.out.println("Result: " + result.toString());
        }
        Assert.assertTrue(message, LicenseUtilTestUtil.areEqual(result, expectedLicenses));
    }

    private Collection<License> callDetectLicenses(BufferedReader bufferedReader, LicenseStoreData licenseStoreData)
            throws IOException {
        return LicenseUtil.detectLicenses(bufferedReader, licenseStoreData);
    }

    private BufferedReader getLicenseBufferedReader(final String filename) throws IOException {
        final String completeFilename = "/licensetexts/" + filename;
        final InputStream is = getClass().getResourceAsStream(completeFilename);
        final InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        return new BufferedReader(isr);
    }

}
