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
package org.aposin.licensescout.finder;

import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.execution.ExecutionParameters;
import org.aposin.licensescout.license.LicenseStoreData;

/**
 * Factory for finder classes.
 *
 * @see ArchiveType
 * @see JavaJarFinder
 * @see JavascriptNpmFinder
 */
public class FinderFactory {

    private static final FinderFactory INSTANCE = new FinderFactory();

    /**
     * Private constructor to prevent instantiation.
     */
    private FinderFactory() {
        // DO NOTHING
    }

    public static FinderFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a finder instance for a specific ArchiveType.
     * @param executionParameters
     * @param licenseStoreData
     * @param runParameters
     * @return a finder instance
     * @throws LicenseScoutExecutionException if there is no finder class for the archive type
     */
    public AbstractFinder createFinder(final ExecutionParameters executionParameters,
                                       final LicenseStoreData licenseStoreData, final RunParameters runParameters)
            throws LicenseScoutExecutionException {
        final AbstractFinder finder;
        if (executionParameters.getArchiveType() == ArchiveType.JAVA) {
            finder = new JavaJarFinder(licenseStoreData, runParameters, executionParameters.getLsLog());
        } else if (executionParameters.getArchiveType() == ArchiveType.JAVASCRIPT) {
            finder = new JavascriptNpmFinder(licenseStoreData, executionParameters.getLsLog(),
                    executionParameters.getNpmExcludedDirectoryNames());
        } else {
            throw new LicenseScoutExecutionException(
                    "Cannot create finder instance for type: " + executionParameters.getArchiveType());
        }
        finder.setScanDirectory(executionParameters.getScanDirectory());
        return finder;
    }

}
