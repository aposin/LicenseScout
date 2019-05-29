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
package org.aposin.licensescout.util;

/**
 * Contains meta information extracted from MANIFEST.MF and pom.xml files.
 * 
 * <p>This class is a container for:</p>
 * <ul>
 * <li>Version</li>
 * <li>Raw License information (URL or license name)</li>
 * <li>Vendor</li>
 * </ul>
 * 
 * @see JarUtil
 */
public class ArchiveMetaInformation {

    /**
     * Immutable default result object containing null for all information.
     */
    public static final ArchiveMetaInformation NO_INFORMATION = new ArchiveMetaInformation(null, null, null);

    private final String version;
    private final String licenseUrl;
    private final String vendor;

    /**
     * @param version
     * @param licenseUrl
     * @param vendor
     */
    public ArchiveMetaInformation(final String version, final String licenseUrl, final String vendor) {
        this.version = version;
        this.licenseUrl = licenseUrl;
        this.vendor = vendor;
    }

    /**
     * @return the version
     */
    public final String getVersion() {
        return version;
    }

    /**
     * @return the licenseUrl
     */
    public final String getLicenseUrl() {
        return licenseUrl;
    }

    /**
     * @return the vendor
     */
    public final String getVendor() {
        return vendor;
    }

}
