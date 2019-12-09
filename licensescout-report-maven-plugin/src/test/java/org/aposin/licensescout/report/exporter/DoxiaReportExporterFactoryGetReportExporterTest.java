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
package org.aposin.licensescout.report.exporter;

import java.util.Arrays;
import java.util.Collection;

import org.apache.maven.doxia.sink.Sink;
import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.execution.AbstractReportExporterFactoryGetReportExporterTest;
import org.aposin.licensescout.execution.Executor;
import org.aposin.licensescout.execution.IReportExporterFactory;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Executor}.
 *
 */
public class DoxiaReportExporterFactoryGetReportExporterTest
        extends AbstractReportExporterFactoryGetReportExporterTest {

    /**
     * Constructor.
     * @param outputFileType
     * @param expectedReportExporterClass
     */
    public DoxiaReportExporterFactoryGetReportExporterTest(OutputFileType outputFileType,
            Class<?> expectedReportExporterClass) {
        super(outputFileType, expectedReportExporterClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IReportExporterFactory createFactory() {
        return new DoxiaReportExporterFactory(new ISinkProvider() {

            /** {@inheritDoc} */
            @Override
            public Sink getSink() {
                return null;
            }
        });
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { //
                { OutputFileType.DOXIA, DoxiaExporter.class }, //
        });
    }
}
