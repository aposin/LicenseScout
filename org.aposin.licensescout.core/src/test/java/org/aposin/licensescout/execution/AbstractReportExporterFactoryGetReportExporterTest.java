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

import org.aposin.licensescout.configuration.OutputFileType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Base class for unit test for implementations of {@link IReportExporterFactory}.
 *
 * <p>Tests:</p>
 * <ul>
 * <li>{@link IReportExporterFactory#getReportExporter(OutputFileType)} with valid values for {@link OutputFileType}</li>
 * </ul>
 */
@RunWith(Parameterized.class)
public abstract class AbstractReportExporterFactoryGetReportExporterTest extends AbstractReportExporterFactoryTest {

    private final OutputFileType outputFileType;
    private final Class<?> expectedReportExporterClass;

    /**
     * Constructor.
     * @param outputFileType
     * @param expectedReportExporterClass
     */
    protected AbstractReportExporterFactoryGetReportExporterTest(OutputFileType outputFileType,
            Class<?> expectedReportExporterClass) {
        this.outputFileType = outputFileType;
        this.expectedReportExporterClass = expectedReportExporterClass;
    }

    /**
     * Test case for the method {@link IReportExporterFactory#getReportExporter(OutputFileType)}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetReportExporter() throws Exception {
        Assert.assertEquals("Report exporter type for " + outputFileType.name(), expectedReportExporterClass,
                getFactory().getReportExporter(outputFileType).getClass());
    }
}
