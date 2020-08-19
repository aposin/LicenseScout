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
import java.io.IOException;
import java.io.InputStream;

/**
 * Handler for reading configuration files.
 * 
 */
public interface ConfigFileHandler {

    /**
     * Provides an InputStream for reading the providers configuration file.
     * @return an InputStream for reading the providers configuration file
     * @throws IOException
     */
    public InputStream getProvidersInputStream() throws IOException;

    /**
     * Provides an InputStream for reading the notices configuration file.
     * @return an InputStream for reading the notices configuration file
     * @throws IOException
     */
    public InputStream getNoticesInputStream() throws IOException;

    /**
     * Provides an InputStream for reading the licenses configuration file.
     * @return an InputStream for reading the licenses configuration file
     * @throws IOException
     */
    public InputStream getLicensesInputStream() throws IOException;

    /**
     * Provides an InputStream for reading the URL mapping configuration file.
     * @return an InputStream for reading the URL mapping configuration file
     * @throws IOException
     */
    public InputStream getLicenseUrlMappingsInputStream() throws IOException;

    /**
     * Provides an InputStream for reading the name mapping configuration file.
     * @return an InputStream for reading the name mapping configuration file
     * @throws IOException
     */
    public InputStream getLicenseNameMappingsInputStream() throws IOException;

    /**
     * Provides an InputStream for reading the global filters configuration file.
     * @return an InputStream for reading the global filters configuration file
     * @throws IOException
     */
    public InputStream getGlobalFiltersInputStream() throws IOException;

    /**
     * Provides an InputStream for reading the checked archives configuration file.
     * @return an InputStream for reading the checked archives configuration file
     * @throws IOException
     */
    public InputStream getCheckedArchivesInputStream() throws IOException;

    /**
     * Provides an InputStream for reading the filtered vendor names configuration file.
     * @return an InputStream for reading the filtered vendor names configuration file
     * @throws IOException
     */
    public InputStream getFilteredVendorNamesInputStream() throws IOException;

    /**
     * Checks if a template file is available.
     * @param filename a filename of a template file
     * @return true if the template file exists, false otherwise
     * @throws IOException
     */
    public boolean hasTemplateFile(String filename) throws IOException;

    public File getTemplateFile(String filename) throws IOException;
}
