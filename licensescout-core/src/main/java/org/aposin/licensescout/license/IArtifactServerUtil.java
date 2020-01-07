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

import java.io.InputStream;

import org.aposin.licensescout.archive.Archive;

/**
 * Utility methods related to accessing a server that is caching Maven central.
 * 
 * <p>The implementation of this class checks if the server is reachable under the configured URL.</p>
 */
public interface IArtifactServerUtil {

    /**
     * Obtains the cached value of the server access test.
     * @return true if an artifact server is accessible, false otherwise
     */
    boolean isCachedCheckAccess();

    /**
     * Adds licenses to an archive from information found in a Maven POM file.
     * 
     * @param inputStream input source of the POM file
     * @param archive the archive to add the licenses to
     * @param filePath the symbolic path of the POM file (for information only)
     * @param licenseStoreData the data object containing information on licenses
     * @return true if one or more licenses have been added, false otherwise
     */
    boolean addLicensesFromPom(InputStream inputStream, Archive archive, String filePath,
                               LicenseStoreData licenseStoreData);

}