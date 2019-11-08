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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * Utility methods for calculating message digests.
 * 
 */
public class CryptUtil {

    private static final int READ_BUFFER_SIZE = 4096;
    private static final String DEFAULT_MD_ALGORITHM = "SHA-256";

    private static String messageDigestAlgorithm = DEFAULT_MD_ALGORITHM;

    private CryptUtil() {
        // DO NOTHING
    }

    /**
     * Calculates a message digest over the binary content of a file.
     * 
     * @param file
     * @return the message digest
     * @throws IOException
     */
    public static byte[] calculateMessageDigest(final File file) throws IOException {
        try (final FileInputStream fis = new FileInputStream(file)) {
            return calculateMessageDigest(fis);
        }
    }

    /**
     * Calculates a message digest from an input stream.
     * 
     * @param is an input stream
     * @return a message digest
     * @throws IOException
     */
    public static byte[] calculateMessageDigest(InputStream is) throws IOException {
        final MessageDigest md = getMessageDigestInstance();
        final byte[] buffer = new byte[READ_BUFFER_SIZE];
        int read;
        while ((read = is.read(buffer)) != -1) {
            md.update(buffer, 0, read);
        }
        return md.digest();
    }

    private static MessageDigest getMessageDigestInstance() {
        try {
            return MessageDigest.getInstance(messageDigestAlgorithm);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the messageDigestAlgorithm
     */
    public static final String getMessageDigestAlgorithm() {
        return messageDigestAlgorithm;
    }

    /**
     * @param messageDigestAlgorithm the messageDigestAlgorithm to set
     */
    public static final void setMessageDigestAlgorithm(final String messageDigestAlgorithm) {
        CryptUtil.messageDigestAlgorithm = messageDigestAlgorithm;
    }

}
