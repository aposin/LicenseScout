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

import org.aposin.licensescout.util.ILFLog;

/**
 * Helper class for instantiating ConfigFileHandler instances.
 * 
 * @see ConfigFileHandler
 */
public class ConfigFileHandlerHelper {

    private ConfigFileHandlerHelper() {
        // EMPTY
    }

    /**
     * @param configurationBundleFile
     * @param configFileParameters
     * @param log the logger
     * @return a configuration file handler
     */
    public static ConfigFileHandler createConfigFileHandler(final File configurationBundleFile,
                                                            final ConfigFileParameters configFileParameters,
                                                            final ILFLog log) {
        final ConfigFileHandler configFileHandler;
        if (configurationBundleFile != null) {
            configFileHandler = new ZipConfigFileHandler(configurationBundleFile, configFileParameters, log);
            log.info("reading configuration files from ZIP file: " + configurationBundleFile.getAbsolutePath());
        } else {
            configFileHandler = new FilesystemConfigFileHandler(configFileParameters, log);
            log.info("reading configuration files from file system");
        }
        return configFileHandler;
    }
}
