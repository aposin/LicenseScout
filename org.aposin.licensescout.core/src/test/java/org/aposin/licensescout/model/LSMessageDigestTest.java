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
package org.aposin.licensescout.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link LSMessageDigest}.
 * 
 */
public class LSMessageDigestTest {

    /**
     * Test for {@link LSMessageDigest#getBytes()}.
     */
    @Test
    public void testGetBytes() {
        byte[] bytes = new byte[] { -1, 0 };
        final LSMessageDigest md = new LSMessageDigest(bytes);
        Assert.assertArrayEquals("message digest bytes", bytes, md.getBytes());
    }

    /**
     * Test for {@link LSMessageDigest#equals(Object)}.
     */
    @Test
    public void testEqualsSameObjectEmpty() {
        final LSMessageDigest md1 = createMessageDigestEmpty();
        Assert.assertTrue("message digest", md1.equals(md1));
    }

    /**
     * Test for {@link LSMessageDigest#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentObjectsEmpty() {
        final LSMessageDigest md1 = createMessageDigestEmpty();
        final LSMessageDigest md2 = createMessageDigestEmpty();
        Assert.assertTrue("message digest", md1.equals(md2));
    }

    /**
     * Test for {@link LSMessageDigest#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentTypes() {
        final LSMessageDigest md1 = createMessageDigestA();
        final Object md2 = new Object();
        Assert.assertFalse("message digest", md1.equals(md2));
        Assert.assertFalse("message digest", md2.equals(md1));
    }

    /**
     * Test for {@link LSMessageDigest#equals(Object)}.
     */
    @Test
    public void testEqualsNull() {
        final LSMessageDigest md1 = createMessageDigestA();
        final Object md2 = null;
        Assert.assertFalse("message digest", md1.equals(md2));
    }

    /**
     * Test for {@link LSMessageDigest#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentObjectsDifferentContent() {
        final LSMessageDigest md1 = createMessageDigestA();
        final LSMessageDigest md2 = createMessageDigestB();
        Assert.assertFalse("message digest", md1.equals(md2));
    }

    /**
     * Test for {@link LSMessageDigest#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentObjectsSameContent() {
        final LSMessageDigest md1 = createMessageDigestA();
        final LSMessageDigest md2 = createMessageDigestA();
        Assert.assertTrue("message digest", md1.equals(md2));
    }

    private LSMessageDigest createMessageDigestEmpty() {
        return new LSMessageDigest(new byte[0]);
    }

    private LSMessageDigest createMessageDigestB() {
        return new LSMessageDigest(new byte[] { -1, 0 });
    }

    private LSMessageDigest createMessageDigestA() {
        return new LSMessageDigest(new byte[] { 0, -1 });
    }

}
