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

import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of artifact URL builder for Nexus.
 * 
 */
public class NexusArtifactUrlBuilder implements IArtifactUrlBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildArtifactUrl(final String artifactBaseUrl, final LSArtifact artifact, final String type) {
        final StringBuilder url = new StringBuilder(artifactBaseUrl);
        String groupId = artifact.getGroupId();
        String artifactId = artifact.getArtifactId();
        url.append(groupId.replace('.', '/'));
        url.append('/');
        url.append(artifactId);
        url.append('/');
        url.append(artifact.getVersion());
        url.append('/');
        url.append(artifactId);
        url.append('-');
        url.append(artifact.getVersion());
        if (!StringUtils.isEmpty(artifact.getClassifier())) {
            url.append('-');
            url.append(artifact.getClassifier());
        }
        url.append('.');
        url.append(type);
        return url.toString();

    }
}