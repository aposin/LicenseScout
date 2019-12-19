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
import java.util.List;

import org.aposin.licensescout.configuration.OutputFileType;
import org.aposin.licensescout.execution.IReportExporterFactory;
import org.aposin.licensescout.exporter.IReportExporter;

/**
 * Factory for creating report exporter instances.
 * 
 * <p>Supports the following output file types:</p>
 * <ul>
 * <li>{@link OutputFileType#DOXIA}</li>
 * </ul>
 */
public class DoxiaReportExporterFactory implements IReportExporterFactory {

    private ISinkProvider sinkProvider;

    /**
     * Constructor.
     * @param sinkProvider a provider for DOXIA Sink instances
     */
    public DoxiaReportExporterFactory(ISinkProvider sinkProvider) {
        this.sinkProvider = sinkProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OutputFileType> getSupportedOutputFileTypes() {
        return Arrays.asList(OutputFileType.DOXIA);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IReportExporter getReportExporter(final OutputFileType outputFileType) {
        switch (outputFileType) {
            case DOXIA:
                return DoxiaExporter.getInstance(sinkProvider);
            default:
                throw new IllegalArgumentException("Unhandled OutputFileType: " + outputFileType);
        }
    }

}
