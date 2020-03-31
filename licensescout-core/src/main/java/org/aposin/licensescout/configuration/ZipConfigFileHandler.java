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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.aposin.licensescout.util.ILSLog;

/**
 * Handler for licensescout configuration files read from a ZIP file.
 * 
 * <p>(This is used if the configuration is configured as a dependency to the LicensScout plugin.)</p>
 */
public class ZipConfigFileHandler extends AbstractConfigFileHandler {

    private File artifactFile;

    /**
     * Constructor.
     * @param artifactFile
     * @param configFileParameters
     * @param log the logger
     */
    public ZipConfigFileHandler(File artifactFile, ConfigFileParameters configFileParameters, final ILSLog log) {
        super(configFileParameters, log);
        this.artifactFile = artifactFile;
    }

    private InputStream getInputStream(final String entryName) throws IOException {
        try (ZipFile zipFile = new ZipFile(artifactFile)) {
            ZipEntry entry = zipFile.getEntry(entryName);
            InputStream zipInputStream = zipFile.getInputStream(entry);
            //Read the zip input stream fully into memory
            byte[] buffer = IOUtils.toByteArray(zipInputStream);
            return new ByteArrayInputStream(buffer);
        }
    }

    private boolean hasEntry(final String entryName) throws IOException {
        try (ZipFile zipFile = new ZipFile(artifactFile)) {
            ZipEntry entry = zipFile.getEntry(entryName);
            return entry != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getProvidersInputStream() throws IOException {
        return getInputStream("providers.xml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getNoticesInputStream() throws IOException {
        return getInputStream("notices.xml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getLicensesInputStream() throws IOException {
        return getInputStream("licenses.xml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getLicenseUrlMappingsInputStream() throws IOException {
        return getInputStream("urlmappings.csv");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getLicenseNameMappingsInputStream() throws IOException {
        return getInputStream("namemappings.csv");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getGlobalFiltersInputStream() throws IOException {
        return getInputStream("globalfilters.csv");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getCheckedArchivesInputStream() throws IOException {
        return getInputStream("checkedarchives.csv");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getFilteredVendorNamesInputStream() throws IOException {
        return getInputStream("filteredvendornames.csv");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTemplateFile(String filename) throws IOException {
        return hasEntry(filename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTemplateFile(String filename) throws IOException {
        final InputStream is = getInputStream(filename);
        final File tmpFile = File.createTempFile("template", ".vm");
        FileUtils.copyInputStreamToFile(is, tmpFile);
        return tmpFile;
    }

}
