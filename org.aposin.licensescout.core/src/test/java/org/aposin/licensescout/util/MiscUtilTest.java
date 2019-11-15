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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link MiscUtil}.
 * 
 */
public class MiscUtilTest {

    private static final String MESSAGE_DIGEST_STRING = "83A2CA29DBAA80C0D6B6369448D0EDC0ACF30CA6E35C86F1E4C43A4915179F0D";
    private static final byte[] MESSAGE_DIGEST_BYTE_ARRAY = new byte[] { -125, -94, -54, 41, -37, -86, -128, -64, -42,
            -74, 54, -108, 72, -48, -19, -64, -84, -13, 12, -90, -29, 92, -122, -15, -28, -60, 58, 73, 21, 23, -97,
            13 };

    /**
     * Test for {@link MiscUtil#getByteArrayFromHexString(String)}.
     */
    @Test
    public void testGetByteArrayFromHexString() {
        Assert.assertArrayEquals("byte array", MESSAGE_DIGEST_BYTE_ARRAY,
                MiscUtil.getByteArrayFromHexString(MESSAGE_DIGEST_STRING));
    }

    /**
     * Test for {@link MiscUtil#getHexString(byte[])}.
     */
    @Test
    public void testGetHexString() {
        Assert.assertEquals("hex string", MESSAGE_DIGEST_STRING, MiscUtil.getHexString(MESSAGE_DIGEST_BYTE_ARRAY));
    }

    /**
     * Test for {@link MiscUtil#getLSMessageDigestFromHexString(String)}.
     */
    @Test
    public void testGetBytes() {
        Assert.assertArrayEquals("byte array", MESSAGE_DIGEST_BYTE_ARRAY,
                MiscUtil.getLSMessageDigestFromHexString(MESSAGE_DIGEST_STRING).getBytes());
    }

    /**
     * Test case for the method {@link MiscUtil#createDirectoryIfNotExists(File)}.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateDirectoryIfNotExists() throws Exception {
        final File directory = new File("target/testdir");
        directory.delete();
        final ILFLog log = TestUtil.createNullLog();
        MiscUtil.createDirectoryIfNotExists(directory, log);
        Assert.assertEquals(
                "createDirectoryIfNotExists(): expected directory not existing after creating or is not a directory",
                true, directory.exists() && directory.isDirectory());
    }

}
