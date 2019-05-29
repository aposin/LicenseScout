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
package org.aposin.licensescout.finder;

import java.io.File;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.exporter.OutputResult;

/**
 * Result from scanning for licenses.
 * 
 * <p>Contains:</p>
 * <ul>
 * <li>The scanned base directory</li>
 * <li>A list of archives found during the scanning</li>
 * </ul>
 * 
 * @see AbstractFinder
 * @see OutputResult
 */
public class FinderResult {

    private final File scanDirectory;
    private final List<Archive> archiveFiles;

    /**
     * @param scanDirectory 
     * @param archiveFiles
     */
    public FinderResult(final File scanDirectory, final List<Archive> archiveFiles) {
        super();
        this.scanDirectory = scanDirectory;
        this.archiveFiles = archiveFiles;
    }

    /**
     * @return the scanDirectory
     */
    public final File getScanDirectory() {
        return scanDirectory;
    }

    /**
     * @return the archiveFiles
     */
    public final List<Archive> getArchiveFiles() {
        return archiveFiles;
    }

}
