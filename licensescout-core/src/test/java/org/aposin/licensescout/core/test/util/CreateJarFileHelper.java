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
package org.aposin.licensescout.core.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * Utility methods for creating JAR files. 
 */
public class CreateJarFileHelper {

    /**
     * Collects a list of files by walking a file tree hierarchically.
     * 
     * <p>This method can be used to collect the files to package with the
     * method {@link #createJarArchiveZip(File, List, Path)}.</p> 
     * 
     * @param startDirectory
     * @return a list of files
     * @throws IOException
     */
    public static List<File> collectFiles(final File startDirectory) throws IOException {
        final List<File> files = new ArrayList<>();
        final SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<>() {

            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                files.add(file.toFile());
                return FileVisitResult.CONTINUE;
            };
        };
        Files.walkFileTree(startDirectory.toPath(), visitor);
        return files;
    }

    /**
     * Packages files into a ZIP/JAR file.
     * 
     * @param archiveFile the resulting ZIP file
     * @param tobePackagedFiles list of files that should be packaged
     * @param basePath the path that is 'subtracted' from the path of a file to package to obtain the relative path that is used
     * as the entry name in the ZIP file
     * @throws IOException
     */
    public static void createJarArchiveZip(final File archiveFile, final List<File> tobePackagedFiles,
                                           final Path basePath)
            throws IOException {
        try (final FileOutputStream fileOutputstream = new FileOutputStream(archiveFile);
                final ZipOutputStream out = new ZipOutputStream(fileOutputstream)) {
            for (final File tobePackaged : tobePackagedFiles) {
                if (tobePackaged == null || !tobePackaged.exists() || tobePackaged.isDirectory()) {
                    continue;
                }
                final String relativePath = basePath.relativize(tobePackaged.toPath()).toString().replace('\\', '/');
                final ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(tobePackaged.lastModified());
                out.putNextEntry(entry);

                // Write file to archive
                try (final FileInputStream in = new FileInputStream(tobePackaged)) {
                    IOUtils.copy(in, out);
                }
            }
        }
    }
}