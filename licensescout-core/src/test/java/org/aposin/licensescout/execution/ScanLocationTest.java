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
package org.aposin.licensescout.execution;

import java.io.File;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ScanLocation}.
 *
 */
public class ScanLocationTest {

    /**
     * Test case for the method {@link ScanLocation#toString()}.
     * 
     * @throws Exception
     */
    @Test
    public void testToStringDirectory() throws Exception {
        final ScanLocation scanLocation = new ScanLocation(new File("."));
        final String result = scanLocation.toString();
        Assert.assertNotNull("toString()", result);
    }

    /**
     * Test case for the method {@link ScanLocation#toString()}.
     * 
     * @throws Exception
     */
    @Test
    public void testToStringFileListEmpty() throws Exception {
        final ScanLocation scanLocation = new ScanLocation(new ArrayList<>());
        final String result = scanLocation.toString();
        Assert.assertNotNull("toString()", result);
    }

    /**
     * Test case for the method {@link ScanLocation#toString()}.
     * 
     * @throws Exception
     */
    @Test
    public void testToLogStringDirectory() throws Exception {
        final ScanLocation scanLocation = new ScanLocation(new File("."));
        final String result = scanLocation.toLogString();
        Assert.assertNotNull("toLogString()", result);
    }

    /**
     * Test case for the method {@link ScanLocation#toString()}.
     * 
     * @throws Exception
     */
    @Test
    public void testToLogStringFileListEmpty() throws Exception {
        final ScanLocation scanLocation = new ScanLocation(new ArrayList<>());
        final String result = scanLocation.toLogString();
        Assert.assertNotNull("toLogString()", result);
    }

}
