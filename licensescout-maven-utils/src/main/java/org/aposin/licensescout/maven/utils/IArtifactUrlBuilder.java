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

/**
 * Builds a URL of a report artifact as uploaded to an artifact server.
 * 
 * <p>Used to calculate values for {@link Output#getUrl()} if it is not configured by the user.<p>
 * <p>Implementations are specific to a certain artifact server (Nexus, Artifactory, ...).<p>
 * 
 * @see ConfigurationHelper
 */
public interface IArtifactUrlBuilder {

    /**
     * Builds a URL of a report artifact as uploaded to an artifact server.
     * @param artifactBaseUrl
     * @param artifact
     * @param type
     * @return a URL string
     */
    public String buildArtifactUrl(String artifactBaseUrl, LSArtifact artifact, String type);
}