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

/**
 * Parameter object containing all information for instantiating a ConfigFileHandler.
 *
 * @see ConfigFileHandler
 */
public class ConfigFileParameters {

    /**
     * Name of the file to read known licenses from.
     */
    private File licensesFilename;

    /**
     * Name of the file to read known providers from.
     */
    private File providersFilename;

    /**
     * Name of the file to read license notices from.
     */
    private File noticesFilename;

    /**
     * Name of the file to read checked archives from.
     */
    private File checkedArchivesFilename;

    /**
     * Name of the file to read license URL mappings from.
     */
    private String licenseUrlMappingsFilename;

    /**
     * Name of the file to read license name mappings from.
     */
    private String licenseNameMappingsFilename;

    /**
     * Name of the file to read global filter patterns from.
     */
    private String globalFiltersFilename;

    /**
     * Name of the file to read of vendor names to filter out from.
     * This is alternative to filteredVendorNames. If both are given, the entries are merged.
     */
    private String filteredVendorNamesFilename;

    /**
     * @return the licensesFilename
     */
    public final File getLicensesFilename() {
        return licensesFilename;
    }

    /**
     * @param licensesFilename the licensesFilename to set
     */
    public final void setLicensesFilename(File licensesFilename) {
        this.licensesFilename = licensesFilename;
    }

    /**
     * @return the providersFilename
     */
    public final File getProvidersFilename() {
        return providersFilename;
    }

    /**
     * @param providersFilename the providersFilename to set
     */
    public final void setProvidersFilename(File providersFilename) {
        this.providersFilename = providersFilename;
    }

    /**
     * @return the noticesFilename
     */
    public final File getNoticesFilename() {
        return noticesFilename;
    }

    /**
     * @param noticesFilename the noticesFilename to set
     */
    public final void setNoticesFilename(File noticesFilename) {
        this.noticesFilename = noticesFilename;
    }

    /**
     * @return the checkedArchivesFilename
     */
    public final File getCheckedArchivesFilename() {
        return checkedArchivesFilename;
    }

    /**
     * @param checkedArchivesFilename the checkedArchivesFilename to set
     */
    public final void setCheckedArchivesFilename(File checkedArchivesFilename) {
        this.checkedArchivesFilename = checkedArchivesFilename;
    }

    /**
     * @return the licenseUrlMappingsFilename
     */
    public final String getLicenseUrlMappingsFilename() {
        return licenseUrlMappingsFilename;
    }

    /**
     * @param licenseUrlMappingsFilename the licenseUrlMappingsFilename to set
     */
    public final void setLicenseUrlMappingsFilename(String licenseUrlMappingsFilename) {
        this.licenseUrlMappingsFilename = licenseUrlMappingsFilename;
    }

    /**
     * @return the licenseNameMappingsFilename
     */
    public final String getLicenseNameMappingsFilename() {
        return licenseNameMappingsFilename;
    }

    /**
     * @param licenseNameMappingsFilename the licenseNameMappingsFilename to set
     */
    public final void setLicenseNameMappingsFilename(String licenseNameMappingsFilename) {
        this.licenseNameMappingsFilename = licenseNameMappingsFilename;
    }

    /**
     * @return the globalFiltersFilename
     */
    public final String getGlobalFiltersFilename() {
        return globalFiltersFilename;
    }

    /**
     * @param globalFiltersFilename the globalFiltersFilename to set
     */
    public final void setGlobalFiltersFilename(String globalFiltersFilename) {
        this.globalFiltersFilename = globalFiltersFilename;
    }

    /**
     * @return the filteredVendorNamesFilename
     */
    public final String getFilteredVendorNamesFilename() {
        return filteredVendorNamesFilename;
    }

    /**
     * @param filteredVendorNamesFilename the filteredVendorNamesFilename to set
     */
    public final void setFilteredVendorNamesFilename(String filteredVendorNamesFilename) {
        this.filteredVendorNamesFilename = filteredVendorNamesFilename;
    }

}
