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
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ArtifactHelper}.
 *
 */
// NOTE: if you are working in an environment where maven central is not reachable, ignore this test class by uncommenting the annotation '@Ignore'.
//@Ignore
public class ArtifactHelperTest {

    /**
     * Test case for the method {@link ArtifactHelper#getArtifactFile(IRepositoryParameters, ArtifactItem)}.
     * 
     * @throws Exception
     */
    @Test(expected = NullPointerException.class)
    public void testGetArtifactFileNullArguments() throws Exception {
        ArtifactHelper.getArtifactFile(null, null);
    }

    /**
     * Test case for the method {@link ArtifactHelper#getArtifactFile(IRepositoryParameters, ArtifactItem)}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetArtifactFileExisting() throws Exception {
        ArtifactItem configBundleArtifact = new ArtifactItem("org.aposin.licensescout",
                "org.aposin.licensescout.configuration.sample", "1.3.1.005", "zip", "configuration");
        final File resultFile = callGetArtifactFile(configBundleArtifact);
        Assert.assertNotNull(resultFile);
    }

    /**
     * Test case for the method {@link ArtifactHelper#getArtifactFile(IRepositoryParameters, ArtifactItem)}.
     * 
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void testGetArtifactFileNotExisting() throws Exception {
        ArtifactItem configBundleArtifact = new ArtifactItem("org.aposin.licensescout",
                "org.aposin.licensescout.configuration.sample", "1.3.0.005", "zip", "configuration");
        final File resultFile = callGetArtifactFile(configBundleArtifact);
        Assert.assertNotNull(resultFile);
    }

    /**
     * @param artifactItem
     * @return a file object representing the artifact in the local repository
     * @throws MojoExecutionException
     */
    private File callGetArtifactFile(ArtifactItem artifactItem) throws MojoExecutionException {
        final IRepositoryParameters repositoryParameters = createRepositoryParameters();
        return ArtifactHelper.getArtifactFile(repositoryParameters, artifactItem);
    }

    private IRepositoryParameters createRepositoryParameters() {
        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession repositorySystemSession = newSession(repositorySystem);
        List<RemoteRepository> remoteRepositories = createRemoteRepositories();
        IRepositoryParameters repositoryParameters = new IRepositoryParameters() {

            @Override
            public RepositorySystemSession getRepositorySystemSession() {
                return repositorySystemSession;
            }

            @Override
            public RepositorySystem getRepositorySystem() {
                return repositorySystem;
            }

            @Override
            public List<RemoteRepository> getRemoteRepositories() {
                return remoteRepositories;
            }
        };
        return repositoryParameters;
    }

    /**
     * Test case for the method {@link ArtifactHelper#getDependencies(IRepositoryParameters, List, ArtifactScope)}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetDependencies1() throws Exception {
        final ArtifactScope scope = ArtifactScope.compile;
        ArtifactItem artifactItem = new ArtifactItem("org.eclipse.aether", "aether-impl", "1.0.0.v20140518", "jar", "");
        final int expectedDependencyCount = 4;

        final List<File> resultFiles = callGetDependencies(artifactItem, scope);
        Assert.assertNotNull(resultFiles);
        Assert.assertEquals(expectedDependencyCount, resultFiles.size());
    }

    /**
     * Test case for the method {@link ArtifactHelper#getDependencies(IRepositoryParameters, List, ArtifactScope)}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetDependencies2() throws Exception {
        final ArtifactScope scope = ArtifactScope.compile;
        ArtifactItem artifactItem = new ArtifactItem("org.eclipse.aether", "aether-impl", "1.0.0.v20140518", "", "");
        final int expectedDependencyCount = 4;

        final List<File> resultFiles = callGetDependencies(artifactItem, scope);
        Assert.assertNotNull(resultFiles);
        Assert.assertEquals(expectedDependencyCount, resultFiles.size());
    }

    private List<File> callGetDependencies(ArtifactItem artifactItem, final ArtifactScope artifactScope)
            throws DependencyResolutionException {
        final IRepositoryParameters repositoryParameters = createRepositoryParameters();
        final List<File> resultFiles = ArtifactHelper.getDependencies(repositoryParameters, Arrays.asList(artifactItem),
                artifactScope);
        return resultFiles;
    }

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }

    private static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository("target/local-repo");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
    }

    private List<RemoteRepository> createRemoteRepositories() {
        final RemoteRepository remoteRepository = new RemoteRepository.Builder("maven-central", "default",
                "https://repo1.maven.org/maven2/").build();
        return Arrays.asList(remoteRepository);
    }
}
