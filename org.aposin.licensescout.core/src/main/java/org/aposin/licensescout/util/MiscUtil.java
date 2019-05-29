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

import javax.xml.bind.DatatypeConverter;

/**
 * Miscellaneous utility methods.
 * 
 */
public class MiscUtil {

    private MiscUtil() {
        // DO NOTHING
    }

    /**
     * Obtains a hex string from a byte array.
     * 
     * @param bytes a byte array
     * @return a hex string
     */
    public static final String getHexString(final byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);

    }

    /**
     * Obtains a byte array from a hex string.
     * 
     * @param str a byte array
     * @return a hex string
     */
    public static final byte[] getByteArrayFromHexString(final String str) {
        return DatatypeConverter.parseHexBinary(str);
    }
}
