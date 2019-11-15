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
package org.aposin.licensescout.exporter;

import org.junit.Assert;

/**
 * Base class for exporter tests that verify the report by string comparison.
 * 
 * <p>Subclasses have to implement {@link #getReferenceContent(TestVariant)}
 * to provide the expected report content to compare against.</p>
 */
public abstract class AbstractStringCompareExporterTest extends AbstractExporterTest {

    /**
     * Obtains the reference content of the expected report for a specific test variant.
     * @param testVariant
     * @return the expected content as a string
     */
    protected abstract String getReferenceContent(TestVariant testVariant);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertResultContent(final TestVariant testVariant, final String resultContent) {
        final String referenceContent = getReferenceContent(testVariant);
        Assert.assertEquals(getExpectedOutputFileType().name() + " output file content", referenceContent,
                resultContent);
    }
}
