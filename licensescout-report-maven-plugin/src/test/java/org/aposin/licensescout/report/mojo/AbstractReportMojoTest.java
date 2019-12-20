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
package org.aposin.licensescout.report.mojo;

import org.aposin.licensescout.archive.ArchiveType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Base class for unit tests of subclasses of {@link AbstractReportMojo}.
 *
 */
public abstract class AbstractReportMojoTest {

    protected abstract Class<?> getExpectedFinderClass();

    /**
     * Test case for the method {@link AbstractReportMojo#getArchiveType()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetArchiveType() throws Exception {
        final AbstractReportMojo reportMojo = createMojo();
        Assert.assertEquals("wrong type returned by getArchiveType()", getExpectedArchiveType(),
                reportMojo.getArchiveType());
    }

    protected abstract AbstractReportMojo createMojo();

    protected abstract ArchiveType getExpectedArchiveType();
}
