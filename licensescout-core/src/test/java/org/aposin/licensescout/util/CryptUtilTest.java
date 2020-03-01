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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link CryptUtil}.
 * 
 */
public class CryptUtilTest {

    private static final String ALGORITHM_NAME_SHA_256 = "SHA-256";
    private static final String ALGORITHM_NAME_SHA3_512 = "SHA3-512";

    private static final String EXPECTED_MESSAGE_DIGEST_STRING_SHA_256 = "83A2CA29DBAA80C0D6B6369448D0EDC0ACF30CA6E35C86F1E4C43A4915179F0D";
    private static final String EXPECTED_MESSAGE_DIGEST_STRING_SHA3_512 = "B836B1356DE23CA4750FCC5CBA8AC827F39C48466EC08262E9C4C88829078087AC84D1E29CBE61EC8D3E4612F3A0A7B8CD94D4883EB6F21D77E1B2949F3BC5FF";

    /**
     * Test for {@link CryptUtil#getMessageDigestLength()} with algorithm 'SHA-256'.
     */
    @Test
    public void testGetMessageDigestLengthSha256() {
        String algorithmName = ALGORITHM_NAME_SHA_256;
        final int expectedMessageDigestLength = 32;
        assertGetMessageDigestLength(algorithmName, expectedMessageDigestLength);
    }

    /**
     * Test for {@link CryptUtil#getMessageDigestLength()} with algorithm 'SHA3-512'.
     */
    @Test
    public void testGetMessageDigestLengthSha3D512() {
        String algorithmName = ALGORITHM_NAME_SHA3_512;
        final int expectedMessageDigestLength = 64;
        assertGetMessageDigestLength(algorithmName, expectedMessageDigestLength);
    }

    /**
     * Test for {@link CryptUtil#getMessageDigestLength()} not existing algorithm.
     */
    @Test(expected = RuntimeException.class)
    public void testGetMessageDigestLengthNotExistingAlgorithm() {
        String algorithmName = "not_existing";
        final int expectedMessageDigestLength = 64;
        assertGetMessageDigestLength(algorithmName, expectedMessageDigestLength);
    }

    private void assertGetMessageDigestLength(String algorithmName, final int expectedMessageDigestLength) {
        CryptUtil.setMessageDigestAlgorithm(algorithmName);
        int result = CryptUtil.getMessageDigestLength();
        Assert.assertEquals("Message digest length", expectedMessageDigestLength, result);
    }

    /**
     * Test for {@link CryptUtil#calculateMessageDigest(InputStream)} with algorithm 'SHA-256'.
     * 
     * @throws IOException
     */
    @Test
    public void testCalculateMessageDigestSha256() throws IOException {
        final String algorithmName = ALGORITHM_NAME_SHA_256;
        final String referenceString = EXPECTED_MESSAGE_DIGEST_STRING_SHA_256;
        assertCalculateMessageDigest(algorithmName, referenceString);
    }

    /**
     * Test for {@link CryptUtil#calculateMessageDigest(InputStream)} with algorithm 'SHA3-512'.
     * 
     * @throws IOException
     */
    @Test
    public void testCalculateMessageDigestSha3D512() throws IOException {
        final String algorithmName = ALGORITHM_NAME_SHA3_512;
        final String referenceString = EXPECTED_MESSAGE_DIGEST_STRING_SHA3_512;
        assertCalculateMessageDigest(algorithmName, referenceString);
    }

    /**
     * Test for {@link CryptUtil#calculateMessageDigest(InputStream)} with not existing algorithm.
     * 
     * @throws IOException
     */
    @Test(expected = RuntimeException.class)
    public void testCalculateMessageDigestNotExistingAlgorithm() throws IOException {
        final String algorithmName = "not_existing";
        final String referenceString = "";
        assertCalculateMessageDigest(algorithmName, referenceString);
    }

    private void assertCalculateMessageDigest(final String algorithmName, final String referenceString)
            throws IOException {
        CryptUtil.setMessageDigestAlgorithm(algorithmName);
        final File file = new File("src/test/resources/licensetexts/apache2/LICENSE-2.0-without-newlines.txt");
        try (final FileInputStream fis = new FileInputStream(file)) {
            final byte[] result = CryptUtil.calculateMessageDigest(fis).getBytes();
            final byte[] reference = getByteArrayFromHexString(referenceString);
            Assert.assertArrayEquals(reference, result);
        }
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
