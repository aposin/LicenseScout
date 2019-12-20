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
package org.aposin.licensescout.exporter;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for report exporters.
 */
public class ExporterUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    private ExporterUtil() {
        // DO NOTHING
    }

    /**
     * Obtains the output charset.
     * @param reportConfiguration the report configuration
     * @return a charset according to the encoding name given in the report configuration, or the platform default
     */
    public static Charset getOutputCharset(final ReportConfiguration reportConfiguration) {
        return getCharset(reportConfiguration.getOutputEncoding());
    }

    /**
     * Obtains the template charset.
     * @param reportConfiguration the report configuration
     * @return a charset according to the encoding name given in the report configuration, or the platform default
     */
    public static Charset getTemplateCharset(final ReportConfiguration reportConfiguration) {
        return getCharset(reportConfiguration.getTemplateEncoding());
    }

    /**
     * Obtains a charset from an encoding name.
     * @param encodingName an encoding name
     * @return a charset according to the encoding name
     */
    private static Charset getCharset(final String encodingName) {
        final Charset charset;
        if (!StringUtils.isEmpty(encodingName) && Charset.isSupported(encodingName)) {
            charset = Charset.forName(encodingName);
        } else {
            charset = Charset.defaultCharset();
        }
        return charset;
    }

}
