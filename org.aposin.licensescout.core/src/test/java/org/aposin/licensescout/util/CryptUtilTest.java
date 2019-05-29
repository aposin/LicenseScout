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
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link CryptUtil}.
 * 
 */
public class CryptUtilTest {

    private static final String EXPECTED_MESSAGE_DIGEST_STRING = "58D1E17FFE5109A7AE296CAAFCADFDBE6A7D176F0BC4AB01E12A689B0499D8BD";

    /**
     * Test for {@link CryptUtil#calculateMessageDigest(File)}.
     * 
     * @throws IOException
     */
    @Test
    public void testCalculateMessageDigest() throws IOException {
        final File file = new File("src/test/resources/licensetexts/apache2/LICENSE-2.0.txt");
        final byte[] result = CryptUtil.calculateMessageDigest(file);
        System.out.println("result hexstring: " + MiscUtil.getHexString(result));
        final String referenceString = EXPECTED_MESSAGE_DIGEST_STRING;
        final byte[] reference = getByteArrayFromHexString(referenceString);
        Assert.assertArrayEquals(reference, result);
    }

    private byte[] getByteArrayFromHexString(final String hexString) {
        final int numBytes = hexString.length() / 2;
        final byte[] bytes = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            String s = hexString.substring(i * 2, i * 2 + 2);
            byte b = (byte) Integer.parseUnsignedInt(s, 16);
            bytes[i] = b;
        }
        return bytes;
    }

}
