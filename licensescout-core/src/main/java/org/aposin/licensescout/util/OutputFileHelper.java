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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.configuration.ExecutionOutput;

/**
 * Utility methods for assembling the filenames of output files.
 * 
 */
public class OutputFileHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private OutputFileHelper() {
        // DO NOTHING
    }

    /**
     * Assembles the output filename for an output.
     * @param output an output configuration
     * @return the filename
     */
    public static String getOutputFilename(final ExecutionOutput output) {
        final String configuredFilename = output.getFilename();
        final StringBuffer filename = new StringBuffer(
                StringUtils.isEmpty(configuredFilename) ? "licensereport" : configuredFilename);
        if (StringUtils.isEmpty(FilenameUtils.getExtension(filename.toString()))) {
            filename.append('.').append(output.getType().getDefaultExtension());
        }
        return filename.toString();
    }

}
