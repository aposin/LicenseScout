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

import org.eclipse.aether.util.artifact.JavaScopes;

/**
 * Maven dependency scope.
 * 
 * <p>See <a href="https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html">Introduction to the Dependency Mechanism</a></p>
 */
public enum ArtifactScope {

    /**
     * This is the default scope, used if none is specified. Compile dependencies are available in all classpaths of a project. Furthermore, those dependencies are propagated to dependent projects.
     */
    compile(JavaScopes.COMPILE),
    /**
     * This is much like compile, but indicates you expect the JDK or a container to provide the dependency at runtime. For example, when building a web application for the Java Enterprise Edition, you would set the dependency on the Servlet API and related Java EE APIs to scope provided because the web container provides those classes. This scope is only available on the compilation and test classpath, and is not transitive.
     */
    provided(JavaScopes.PROVIDED),
    /**
     * This scope indicates that the dependency is not required for compilation, but is for execution. It is in the runtime and test classpaths, but not the compile classpath.
     */
    runtime(JavaScopes.RUNTIME),
    /**
     * This scope indicates that the dependency is not required for normal use of the application, and is only available for the test compilation and execution phases. This scope is not transitive.
     */
    test(JavaScopes.TEST),
    /**
     * This scope is similar to provided except that you have to provide the JAR which contains it explicitly. The artifact is always available and is not looked up in a repository.
     */
    system(JavaScopes.SYSTEM);

    private final String scopeValue;

    private ArtifactScope(final String scopeValue) {
        this.scopeValue = scopeValue;
    }

    /**
     * @return the scopeValue
     */
    public final String getScopeValue() {
        return scopeValue;
    }
}
