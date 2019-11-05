package org.aposin.licensescout.finder;

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
public class CreateJarFile {

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
        final SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

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
                if (tobePackaged == null || !tobePackaged.exists() || tobePackaged.isDirectory())
                    continue;
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