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
package org.aposin.licensescout.mojo;

import java.util.List;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.finder.AbstractFinder;
import org.aposin.licensescout.finder.JavascriptNpmFinder;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILFLog;

/**
 * Scans NPM package directory for licenses.
 *
 */
@Mojo(name = "scanNpm", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class NpmScanMojo extends AbstractScanMojo {

    /**
     * List of directory names that are ignored in the scan of NPM directories.
     */
    @Parameter(property = "npmExcludedDirectoryNames", required = false)
    protected List<String> npmExcludedDirectoryNames;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ArchiveType getArchiveType() {
        return ArchiveType.JAVASCRIPT;
    }

    /**
     * @param log the logger
     */
    @Override
    protected void logNpmExcludedDirectoryNames(final ILFLog log) {
        if (npmExcludedDirectoryNames.isEmpty()) {
            log.info("No directory name to exclude in NPM scan configured.");
        } else {
            for (final String excludedDirectoryName : npmExcludedDirectoryNames) {
                log.info("Using directory name to exclude in NPM scan: " + excludedDirectoryName);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractFinder createFinder(final LicenseStoreData licenseStoreData, final RunParameters runParameters,
                                          final ILFLog log) {
        final AbstractFinder javascriptFinder = new JavascriptNpmFinder(licenseStoreData, log,
                npmExcludedDirectoryNames);
        javascriptFinder.setScanDirectory(scanDirectory);
        return javascriptFinder;
    }

}
