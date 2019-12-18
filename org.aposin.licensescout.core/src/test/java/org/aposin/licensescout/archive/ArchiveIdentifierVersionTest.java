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
package org.aposin.licensescout.archive;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ArchiveIdentifierVersion}.
 */
public class ArchiveIdentifierVersionTest {

    /**
     * Test method for {@link ArchiveIdentifierVersion#toString()}.
     */
    @Test
    public void testToString() {
        final ArchiveIdentifierVersion ai = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        Assert.assertNotNull("toString()", ai.toString());
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#hashCode()}.
     */
    @Test
    public void testHashCodeSame() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        Assert.assertEquals("hashCode()", ai1.hashCode(), ai2.hashCode());
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#hashCode()}.
     */
    @Test
    public void testHashCodeDifferentArchiveType() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVASCRIPT, "abc", "1.0");
        Assert.assertNotEquals("hashCode()", ai1.hashCode(), ai2.hashCode());
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#hashCode()}.
     */
    @Test
    public void testHashCodeDifferentName() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "def", "1.0");
        Assert.assertNotEquals("hashCode()", ai1.hashCode(), ai2.hashCode());
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#hashCode()}.
     */
    @Test
    public void testHashCodeDifferentVersion() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "2.0");
        Assert.assertNotEquals("hashCode()", ai1.hashCode(), ai2.hashCode());
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsSame() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = ai1;
        Assert.assertTrue("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsIdentical() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        Assert.assertTrue("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsNull() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = null;
        Assert.assertFalse("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsObject() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final Object ai2 = new Object();
        Assert.assertFalse("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsArchiveIdentifier() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifier ai2 = new ArchiveIdentifier(ArchiveType.JAVA, NameMatchingType.PATTERN, "abc") {
            // EMPTY
        };
        Assert.assertFalse("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentArchiveType() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVASCRIPT, "abc", "1.0");
        Assert.assertFalse("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentName() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "def", "1.0");
        Assert.assertFalse("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentVersion() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "2.0");
        Assert.assertFalse("equals()", ai1.equals(ai2));
    }

    /**
     * Test method for {@link ArchiveIdentifierVersion#equals(Object)}.
     */
    @Test
    public void testEqualsDifferentNameAndVersion() {
        final ArchiveIdentifierVersion ai1 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "abc", "1.0");
        final ArchiveIdentifierVersion ai2 = new ArchiveIdentifierVersion(ArchiveType.JAVA, "def", "2.0");
        Assert.assertFalse("equals()", ai1.equals(ai2));
    }

}
