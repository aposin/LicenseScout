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
package org.aposin.licensescout.core.test.util;

import java.io.InputStream;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.license.IArtifactServerUtil;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.license.LicenseStoreData;

/**
 * Artifact server util implementation for testing.
 * 
 */
public class TestArtifactServerUtil implements IArtifactServerUtil {

    private final boolean addLicense;

    /**
     * Constructor.
     */
    public TestArtifactServerUtil() {
        this(false);
    }

    /**
     * Constructor.
     * @param addLicense if true, {@link #addLicensesFromPom(InputStream, Archive, String, LicenseStoreData)}
     * adds an Apache 2.0 license
     */
    public TestArtifactServerUtil(boolean addLicense) {
        this.addLicense = addLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isCachedCheckAccess() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addLicensesFromPom(final InputStream inputStream, final Archive archive, final String filePath,
                                      final LicenseStoreData licenseStoreData) {
        // DO NOTHING
        if (addLicense && inputStream != null) {
            License license = licenseStoreData.getLicenseBySpdxIdentifier("Apache-2.0");
            archive.addDetectedLicense(license, "");
            return true;
        } else {
            return false;
        }
    }
}
