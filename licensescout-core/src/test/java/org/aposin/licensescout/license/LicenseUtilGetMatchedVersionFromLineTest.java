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
package org.aposin.licensescout.license;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aposin.licensescout.license.LicenseUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for {@link LicenseUtil}.
 * 
 * @see LicenseUtil
 *
 */
@RunWith(Parameterized.class)
public class LicenseUtilGetMatchedVersionFromLineTest {

    private final String lineString;
    private final String message;
    private final String expectedVersion;

    /**
     * Constructor.
     * 
     * @param lineString
     * @param message
     * @param expectedVersion
     */
    public LicenseUtilGetMatchedVersionFromLineTest(final String lineString, final String message,
            final String expectedVersion) {
        this.lineString = lineString;
        this.message = message;
        this.expectedVersion = expectedVersion;
    }

    /**
     * Test for {@link LicenseUtil#getMatchedVersionFromLine(String)}.
     */
    @Test
    public void test() {
        final String result = LicenseUtil.getMatchedVersionFromLine(lineString);
        Assert.assertEquals(message, expectedVersion, result);
    }

    /**
     * @return data for parameterization
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { //
                { "", "[empty string]", null }, //
                { "VERSION 2", "VERSION 2", "2" }, //
                { "VERSION 2.1", "VERSION 2.1", "2.1" }, //
                { "V 2.1", "V 2.1", "2.1" }, //
                { "V2.1", "V2.1", "2.1" }, //
                { "The GNU General Public License (GPL) Version 2, June 1991",
                        "The GNU General Public License (GPL) Version 2, June 1991", "2" }, //
        });
    }

    /**
     * The main method.
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {

        // ".*VERSION.*(\\d\\.\\d).*"
        //        String line = "ECLIPSE PUBLIC LICENSE VERSION 1.0 (&QUOT;EPL&QUOT;).  A COPY OF THE EPL IS AVAILABLE ";
        //        String line = "ECLIPSE PUBLIC LICENSE VERSION 2 (&QUOT;EPL&QUOT;).  A COPY OF THE EPL IS AVAILABLE ";
        //        String line = "VERSION 2 (&QUOT;EPL&QUOT;).  A COPY OF THE EPL IS AVAILABLE ";
        String line = "VERSION 2";

        Pattern pattern = Pattern.compile(".*((VERSION|V).*(\\d\\.\\d)|(VERSION|V) (\\d)).*");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            String version = null;
            if (matcher.matches()) {
                if (/*matcher.groupCount() == 3 &&*/ matcher.group(2) != null) {
                    version = matcher.group(3);
                } else if (matcher.groupCount() >= 5 && matcher.group(4) != null) {
                    version = matcher.group(5);
                }
            }
            System.out.println("detected version: " + version);
            //            int i = matcher.groupCount();
            //            System.out.println("group count: " + i);
            //            while (i >= 0) {
            //                System.out.println("group " + i + ": '" + matcher.group(i) + "'");
            //                i--;
            //            }
        }
    }

}
