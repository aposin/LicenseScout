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

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Description of an artifact by Maven coordinates for use in Maven configuration.
 */
public class ArtifactItem {

    /**
     * Group Id of artifact.
     */
    @Parameter(property = "groupId", required = true)
    private String groupId;

    /**
     * Artifact ID of artifact.
     */
    @Parameter(property = "artifactId", required = true)
    private String artifactId;

    /**
     * Version of artifact.
     */
    @Parameter(property = "version", required = true)
    private String version;

    /**
     * Type of Artifact (War,Jar,etc)
     *
     */
    @Parameter(property = "type", required = false)
    private String type;

    /**
     * Classifier for Artifact (tests,sources,etc)
     *
     */
    @Parameter(property = "classifier", required = false)
    private String classifier;

    /**
     * Constructor used by the Maven runtime system.
     */
    public ArtifactItem() {
        // EMPTY
    }

    /**
     * Constructor for testing.
     * @param groupId
     * @param artifactId
     * @param version
     * @param type
     * @param classifier
     */
    public ArtifactItem(final String groupId, final String artifactId, final String version, final String type,
            final String classifier) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.classifier = classifier;
    }

    /**
     * @return the groupId
     */
    public final String getGroupId() {
        return groupId;
    }

    /**
     * @return the artifactId
     */
    public final String getArtifactId() {
        return artifactId;
    }

    /**
     * @return the version
     */
    public final String getVersion() {
        return version;
    }

    /**
     * @return the type
     */
    public final String getType() {
        return type;
    }

    /**
     * @return the classifier
     */
    public final String getClassifier() {
        return classifier;
    }

}
