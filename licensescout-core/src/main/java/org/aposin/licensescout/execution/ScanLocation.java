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
package org.aposin.licensescout.execution;

import java.io.File;
import java.util.List;

/**
 * Location to be scanned, either a directory or a list of files.
 *
 * @see Executor
 */
public class ScanLocation {

    /**
     * Directory to scan for archives.
     */
    private final File scanDirectory;

    private final List<File> scanFiles;

    public ScanLocation(final File scanDirectory) {
        this.scanDirectory = scanDirectory;
        this.scanFiles = null;
    }

    public ScanLocation(final List<File> scanFiles) {
        this.scanDirectory = null;
        this.scanFiles = scanFiles;
    }

    /**
     * @return the scanDirectory
     */
    public final File getScanDirectory() {
        return scanDirectory;
    }

    /**
     * @return the scanFiles
     */
    public final List<File> getScanFiles() {
        return scanFiles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ScanLocation [scanDirectory=" + scanDirectory + ", scanFiles=" + scanFiles + "]";
    }

    /**
     * @return a string that can be used in a log file
     */
    public String toLogString() {
        if (scanFiles != null) {
            return scanFiles.toString();
        } else if (scanDirectory != null) {
            return scanDirectory.getAbsolutePath();
        } else {
            return "<not configured>";
        }
    }

}
