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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

/**
 * Helper class for handling artifacts.
 *
 */
public class ArtifactHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private ArtifactHelper() {
        // EMPTY
    }

    /**
     * Resolves an artifact and returns the local file.
     * @param repositoryParameters 
     * @param artifactItem the description of thr artifact to be resolved
     * @return a file object if a configuration file bundle was found, otherwise null
     * @throws MojoExecutionException 
     */
    public static File getArtifactFile(final IRepositoryParameters repositoryParameters,
                                       final ArtifactItem artifactItem)
            throws MojoExecutionException {
        final Artifact artifact = resolve(repositoryParameters, artifactItem);
        if (artifact != null) {
            return artifact.getFile();
        }
        return null;
    }

    private static Artifact createDefaultArtifact(final ArtifactItem artifactItem) {
        final String type = StringUtils.isEmpty(artifactItem.getType()) ? "jar" : artifactItem.getType();
        return new DefaultArtifact(artifactItem.getGroupId(), artifactItem.getArtifactId(),
                artifactItem.getClassifier(), type, artifactItem.getVersion());
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

    /**
     * Calculates the set of transitive dependencies of the passed artifacts.
     * 
     * @param repositoryParameters
     * @param artifacts
     * @param artifactScope
     * @return a list of File locations where the JARs of the dependencies are located in the local file system
     * @throws DependencyResolutionException
     */
    public static List<File> getDependencies(final IRepositoryParameters repositoryParameters,
                                             final List<ArtifactItem> artifacts, final ArtifactScope artifactScope)
            throws DependencyResolutionException {
        final RepositorySystem system = repositoryParameters.getRepositorySystem();
        final RepositorySystemSession session = repositoryParameters.getRepositorySystemSession();
        final DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(artifactScope.getScopeValue());
        final Set<File> artifactFiles = new HashSet<>();
        for (final ArtifactItem artifactItem : artifacts) {
            Artifact artifact = createDefaultArtifact(artifactItem);
            final CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(new Dependency(artifact, artifactScope.getScopeValue()));
            collectRequest.setRepositories(repositoryParameters.getRemoteRepositories());
            final DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
            final DependencyResult dependencyResult = system.resolveDependencies(session, dependencyRequest);
            final List<ArtifactResult> artifactResults = dependencyResult.getArtifactResults();
            for (final ArtifactResult artifactResult : artifactResults) {
                artifactFiles.add(artifactResult.getArtifact().getFile());
            }
        }
        return new ArrayList<>(artifactFiles);
    }

}
