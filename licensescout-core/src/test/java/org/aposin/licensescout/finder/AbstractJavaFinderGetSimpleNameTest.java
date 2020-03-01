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
package org.aposin.licensescout.finder;

import java.util.Arrays;

import org.aposin.licensescout.configuration.FinderParameters;
import org.aposin.licensescout.execution.ExecutionParameters;
import org.aposin.licensescout.license.LicenseStoreData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link AbstractJavaFinder#getSimpleName(String)}.
 *
 */
@RunWith(Parameterized.class)
public class AbstractJavaFinderGetSimpleNameTest {

    private final String entryName;
    private final String expectedSimpleName;

    /**
     * Constructor.
     * @param entryName
     * @param expectedSimpleName
     */
    public AbstractJavaFinderGetSimpleNameTest(String entryName, String expectedSimpleName) {
        this.entryName = entryName;
        this.expectedSimpleName = expectedSimpleName;
    }

    /**
     * Test case for the method {@link FinderFactory#createFinder(ExecutionParameters, LicenseStoreData, FinderParameters)}.
     * 
     * @throws Exception
     */
    @Test
    public void testIsCandidateLicenseFile() throws Exception {
        final AbstractJavaFinder finder = new TestAbstractJavaFinder();
        Assert.assertEquals("getSimpleName()", expectedSimpleName, finder.getSimpleName(entryName));
    }

    /**
     * @return the parameters
     */
    @Parameters(name = "{index}: entryName {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] { //
                { "", "" }, //
                { "abc", "abc" }, //
                { "/abc.txt", "abc.txt" }, //
                { "./123/abc.txt", "abc.txt" }, //
                { "//./abc.html", "abc.html" }, //
        });
    }

    private class TestAbstractJavaFinder extends AbstractJavaFinder {

        public TestAbstractJavaFinder() {
            super(null, null, null);
        }

        @Override
        protected void findLicensesImpl() throws Exception {
            // DO NOTHING
        }
    }
}
