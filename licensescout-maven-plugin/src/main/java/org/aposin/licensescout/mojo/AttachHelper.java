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
package org.aposin.licensescout.mojo;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.aposin.licensescout.configuration.ExecutionOutput;
import org.aposin.licensescout.execution.ExecutionParameters;
import org.aposin.licensescout.util.OutputFileHelper;

/**
 * Helper class for {@link AbstractScanMojo}.
 *
 */
public class AttachHelper {

    /**
     * Attaches generated report files as secondary artifacts.
     * 
     * @param mavenProject 
     * @param mavenProjectHelper 
     * @param executionParameters 
     * @param attachReportsClassifier the classifier value to for the attached report files
     * 
     */
    public static void attachReports(final MavenProject mavenProject, final MavenProjectHelper mavenProjectHelper,
                                     final ExecutionParameters executionParameters,
                                     final String attachReportsClassifier) {
        for (final ExecutionOutput output : executionParameters.getOutputs()) {
            final String artifactType = output.getType().getArtifactType();
            final File artifactFile = new File(executionParameters.getOutputDirectory(),
                    OutputFileHelper.getOutputFilename(output));
            executionParameters.getLsLog().info("attaching artifact: " + artifactFile.getAbsolutePath());
            mavenProjectHelper.attachArtifact(mavenProject, artifactType, attachReportsClassifier, artifactFile);
        }
    }
}
