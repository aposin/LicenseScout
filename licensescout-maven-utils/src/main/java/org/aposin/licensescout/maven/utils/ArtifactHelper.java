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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

/**
 * Helper class for handling artifacts.
 *
 */
public class ArtifactHelper {

    /**
     * Resolves an artifact and returns the local file.
     * @param repositoryParameters 
     * @param artifactItem the description of thr artifact to be resolved
     * @return a file object if a configuration file bundle was found, otherwise null
     * @throws InvalidRepositoryException 
     * @throws MojoExecutionException 
     */
    public static File getArtifactFile(final IRepositoryParameters repositoryParameters,
                                                  final ArtifactItem artifactItem)
            throws  MojoExecutionException {
        final Artifact artifact = resolve(repositoryParameters, artifactItem);
        if (artifact != null) {
            return artifact.getFile();
        }
        return null;
    }

    private static Artifact createDefaultArtifact(final ArtifactItem artifactItem) {
        return new DefaultArtifact(artifactItem.getGroupId(), artifactItem.getArtifactId(),
                artifactItem.getClassifier(), artifactItem.getType(), artifactItem.getVersion());
    }

    /**
     * Resolves an artifact via the repository system.
     * @param repositoryParameters
     * @param artifactItem
     * @return a file object if a configuration file bundle was found, otherwise null
     * @throws MojoExecutionException 
     */
    private static Artifact resolve(final IRepositoryParameters repositoryParameters, final ArtifactItem artifactItem)
            throws MojoExecutionException {
        try {
            final Artifact artifact = createDefaultArtifact(artifactItem);
            final ArtifactRequest request = new ArtifactRequest();
            request.setArtifact(artifact);
            request.setRepositories(repositoryParameters.getRemoteRepositories());
            final ArtifactResult result = repositoryParameters.getRepositorySystem()
                    .resolveArtifact(repositoryParameters.getRepositorySystemSession(), request);
            return result.getArtifact();
        } catch (IllegalArgumentException | ArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
