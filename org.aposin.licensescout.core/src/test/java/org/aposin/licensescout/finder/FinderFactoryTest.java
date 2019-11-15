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

import java.util.ArrayList;
import java.util.Arrays;

import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.core.test.util.TestUtil;
import org.aposin.licensescout.execution.ExecutionParameters;
import org.aposin.licensescout.util.ILFLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link FinderFactory}.
 *
 */
@RunWith(Parameterized.class)
public class FinderFactoryTest {

    private final ArchiveType archiveType;
    private final Class<?> expectedFinderClass;

    /**
     * Constructor.
     * @param archiveType
     * @param expectedFinderClass
     */
    public FinderFactoryTest(ArchiveType archiveType, Class<?> expectedFinderClass) {
        this.archiveType = archiveType;
        this.expectedFinderClass = expectedFinderClass;
    }

    /**
     * Test case for the method {@link FinderFactory#createFinder(org.aposin.licensescout.license.LicenseStoreData, ILFLog)}.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateFinder() throws Exception {
        final ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.setArchiveType(archiveType);
        executionParameters.setLsLog(TestUtil.createJavaUtilGlobalLog());
        executionParameters.setNpmExcludedDirectoryNames(new ArrayList<>());
        final RunParameters runParameters = TestUtil.createRunParameters();
        final AbstractFinder finder = FinderFactory.getInstance().createFinder(executionParameters, null,
                runParameters);
        Assert.assertEquals("class type returned by createFinder()", expectedFinderClass, finder.getClass());
    }

    /**
     * @return the parameters
     */
    @Parameters(name = "{index}: ArchiveType {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] { //
                { ArchiveType.JAVA, JavaJarFinder.class }, //
                { ArchiveType.JAVASCRIPT, JavascriptNpmFinder.class }, //
        });
    }
}
