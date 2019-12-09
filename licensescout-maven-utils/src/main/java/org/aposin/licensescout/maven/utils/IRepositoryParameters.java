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

import java.util.List;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Container for repository system run time components.
 * 
 * @see ArtifactHelper
 */
public interface IRepositoryParameters {

    /**
     * Obtains the Maven repository system session.
     * @return the Maven repository system session
     */
    public RepositorySystemSession getRepositorySystemSession();

    /**
     * Obtains the Maven repository system.
     * @return the Maven repository system
     */
    public RepositorySystem getRepositorySystem();

    /**
     * Obtains the Maven configured remote repositories.
     * @return a list of configured remote repositories
     */
    public List<RemoteRepository> getRemoteRepositories();
}