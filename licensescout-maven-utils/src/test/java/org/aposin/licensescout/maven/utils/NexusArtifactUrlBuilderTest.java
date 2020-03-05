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
package org.aposin.licensescout.maven.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link NexusArtifactUrlBuilder}.
 *
 */
public class NexusArtifactUrlBuilderTest {

    /**
     * Test case for the method {@link NexusArtifactUrlBuilder#buildArtifactUrl(String, LSArtifact, String)}.
     * 
     */
    @Test
    public void testBuildWithoutClassifier() {
        final IArtifactUrlBuilder artifactUrlBuilder = new NexusArtifactUrlBuilder();
        final String artifactBaseUrl = "http://server/repo/";
        final LSArtifact artifact = new LSArtifact("group.id", "artifactId", "version", "");
        final String type = "html";
        final String result = artifactUrlBuilder.buildArtifactUrl(artifactBaseUrl, artifact, type);
        Assert.assertEquals("url", "http://server/repo/group/id/artifactId/version/artifactId-version.html", result);
    }

    /**
     * Test case for the method {@link NexusArtifactUrlBuilder#buildArtifactUrl(String, LSArtifact, String)}.
     * 
     */
    @Test
    public void testBuildWithClassifier() {
        final IArtifactUrlBuilder artifactUrlBuilder = new NexusArtifactUrlBuilder();
        final String artifactBaseUrl = "http://server/repo/";
        final LSArtifact artifact = new LSArtifact("group.id", "artifactId", "version", "classifier");
        final String type = "html";
        final String result = artifactUrlBuilder.buildArtifactUrl(artifactBaseUrl, artifact, type);
        Assert.assertEquals("url", "http://server/repo/group/id/artifactId/version/artifactId-version-classifier.html",
                result);
    }
}
