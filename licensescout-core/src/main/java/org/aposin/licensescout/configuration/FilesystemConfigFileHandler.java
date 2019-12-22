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
package org.aposin.licensescout.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.aposin.licensescout.util.ILFLog;

/**
 */
public class FilesystemConfigFileHandler extends AbstractConfigFileHandler {

    /**
     * Constructor.
     * 
     * @param configFileParameters
     * @param log 
     */
    public FilesystemConfigFileHandler(ConfigFileParameters configFileParameters, final ILFLog log) {
        super(configFileParameters, log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getProvidersInputStream() throws IOException {
        final File providersFilename = getExecutionParameters().getProvidersFilename();
        if (providersFilename != null) {
            if (providersFilename.exists() && providersFilename.canRead()) {
                getLog().info("reading providers from " + providersFilename);
                return new FileInputStream(providersFilename);
            } else {
                getLog().error("not reading providers because the file does not exist or is not readable: "
                        + providersFilename);
            }
        } else {
            getLog().info("not reading providers (not configured)");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getNoticesInputStream() throws IOException {
        final File noticesFilename = getExecutionParameters().getNoticesFilename();
        if (noticesFilename != null) {
            if (noticesFilename.exists() && noticesFilename.canRead()) {
                getLog().info("reading notices from " + noticesFilename);
                return new FileInputStream(noticesFilename);
            } else {
                getLog().error(
                        "not reading notices because the file does not exist or is not readable: " + noticesFilename);
            }
        } else {
            getLog().info("not reading notices (not configured)");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getLicensesInputStream() throws IOException {
        final File licensesFilename = getExecutionParameters().getLicensesFilename();
        if (licensesFilename != null) {
            if (licensesFilename.exists() && licensesFilename.canRead()) {
                getLog().info("reading licenses from " + licensesFilename);
                return new FileInputStream(licensesFilename);
            } else {
                getLog().error(
                        "not reading licenses because the file does not exist or is not readable: " + licensesFilename);
            }
        } else {
            getLog().info("not reading licenses (not configured)");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getLicenseUrlMappingsInputStream() throws IOException {
        final File licenseUrlMappingsFilename = getExecutionParameters().getLicenseUrlMappingsFilename();
        if (licenseUrlMappingsFilename != null) {
            if (licenseUrlMappingsFilename.exists() && licenseUrlMappingsFilename.canRead()) {
                getLog().info("reading license URL mappings from " + licenseUrlMappingsFilename);
                return new FileInputStream(licenseUrlMappingsFilename);
            } else {
                getLog().error("not reading license URL mappings because the file does not exist or is not readable: "
                        + licenseUrlMappingsFilename);
            }
        } else {
            getLog().info("not reading license URL mappings (not configured)");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getLicenseNameMappingsInputStream() throws IOException {
        final File licenseNameMappingsFilename = getExecutionParameters().getLicenseNameMappingsFilename();
        if (licenseNameMappingsFilename != null) {
            if (licenseNameMappingsFilename.exists() && licenseNameMappingsFilename.canRead()) {
                getLog().info("reading license name mappings from " + licenseNameMappingsFilename);
                return new FileInputStream(licenseNameMappingsFilename);
            } else {
                getLog().error("not reading license name mappings because the file does not exist or is not readable: "
                        + licenseNameMappingsFilename);
            }
        } else {
            getLog().info("not reading license name mappings (not configured)");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getGlobalFiltersInputStream() throws IOException {
        final File globalFiltersFilename = getExecutionParameters().getGlobalFiltersFilename();
        if (globalFiltersFilename != null) {
            if (globalFiltersFilename.exists() && globalFiltersFilename.canRead()) {
                getLog().info("reading global filters from " + globalFiltersFilename);
                return new FileInputStream(globalFiltersFilename);
            } else {
                getLog().error("not reading global filters because the file does not exist or is not readable: "
                        + globalFiltersFilename);
            }
        } else {
            getLog().info("not reading global filters (not configured)");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getCheckedArchivesInputStream() throws IOException {
        final File checkedArchivesFilename = getExecutionParameters().getCheckedArchivesFilename();
        if (checkedArchivesFilename != null) {
            if (checkedArchivesFilename.exists() && checkedArchivesFilename.canRead()) {
                getLog().info("reading checked archives list from " + checkedArchivesFilename);
                return new FileInputStream(checkedArchivesFilename);
            } else {
                getLog().error("not reading checked archives list because the file does not exist or is not readable: "
                        + checkedArchivesFilename);
            }
        } else {
            getLog().info("not reading checked archives list (not configured)");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getFilteredVendorNamesInputStream() throws IOException {
        final File filteredVendorNamesFilename = getExecutionParameters().getFilteredVendorNamesFilename();
        if (filteredVendorNamesFilename != null) {
            if (filteredVendorNamesFilename.exists() && filteredVendorNamesFilename.canRead()) {
                getLog().info("reading vendor names to filter out from " + filteredVendorNamesFilename);
                return new FileInputStream(filteredVendorNamesFilename);
            } else {
                getLog().error(
                        "not reading vendor names to filter out because the file does not exist or is not readable: "
                                + filteredVendorNamesFilename);
            }
        } else {
            getLog().info("not reading vendor names to filter out from file (not configured)");
        }
        return null;
    }
}
