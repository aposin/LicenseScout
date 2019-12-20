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

import java.util.List;

import org.aposin.licensescout.configuration.OutputFileType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Base class for unit test for implementations of {@link IReportExporterFactory}.
 *
 * <p>Tests:</p>
 * <ul>
 * <li>{@link IReportExporterFactory#getSupportedOutputFileTypes()}</li>
 * <li>{@link IReportExporterFactory#getReportExporter(OutputFileType)} with {@link OutputFileType#UNSUPPORTED}</li>
 * </ul>
 * 
 * <p>NOTE: {@link IReportExporterFactory} with valid values is tested in {@link AbstractReportExporterFactoryGetReportExporterTest}.</p>
 */
public abstract class AbstractReportExporterFactoryMiscTest extends AbstractReportExporterFactoryTest {

    /**
     * Test case for the method {@link IReportExporterFactory#getSupportedOutputFileTypes()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetSupportedOutputFileTypes() throws Exception {
        Assert.assertEquals("supported output file types", getExpectedOuputFileTypes(),
                getFactory().getSupportedOutputFileTypes());
    }

    /**
     * Obtains the expected list of {@link OutputFileType} returned by {@link IReportExporterFactory#getSupportedOutputFileTypes()}.
     * @return the expected list of output file types
     */
    protected abstract List<OutputFileType> getExpectedOuputFileTypes();

    /**
     * Test case for the method {@link IReportExporterFactory#getReportExporter(OutputFileType)}
     * with {@link OutputFileType#UNSUPPORTED}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetReportExporterUnsupportedType() throws Exception {
        getFactory().getReportExporter(OutputFileType.UNSUPPORTED);
    }

}
