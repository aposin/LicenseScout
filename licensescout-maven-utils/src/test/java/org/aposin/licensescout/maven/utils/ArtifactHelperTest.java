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
public class ArtifactHelperTest {

    /**
     * Test case for the method {@link ArtifactHelper#getArtifactFile(org.aposin.licensescout.maven.utils.IRepositoryParameters, org.aposin.licensescout.maven.utils.ArtifactItem)}.
     * 
     * @throws Exception
     */
    @Test(expected = NullPointerException.class)
    public void testGetArtifactFileNullArguments() throws Exception {
        ArtifactHelper.getArtifactFile(null, null);
    }

    /**
     * Test case for the method {@link ArtifactHelper#getArtifactFile(org.aposin.licensescout.maven.utils.IRepositoryParameters, org.aposin.licensescout.maven.utils.ArtifactItem)}.
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
     * Test case for the method {@link ArtifactHelper#getArtifactFile(org.aposin.licensescout.maven.utils.IRepositoryParameters, org.aposin.licensescout.maven.utils.ArtifactItem)}.
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
     * @return
     * @throws MojoExecutionException
     */
    private File callGetArtifactFile(ArtifactItem artifactItem) throws MojoExecutionException {
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

        final File resultFile = ArtifactHelper.getArtifactFile(repositoryParameters, artifactItem);
        return resultFile;
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
                "http://repo1.maven.org/maven2/").build();
        return Arrays.asList(remoteRepository);
    }
}
