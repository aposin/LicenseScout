package org.aposin.licensescout.finder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class CreateJarFile {

    public static List<File> collectFiles(final File start) throws IOException {
        final List<File> files = new ArrayList<>();
        final SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            public java.nio.file.FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs)
                    throws java.io.IOException {
                files.add(file.toFile());
                return FileVisitResult.CONTINUE;
            };
        };
        Files.walkFileTree(start.toPath(), visitor);
        return files;
    }

    public static void createJarArchiveZip(final File archiveFile, final List<File> tobeJaredList, final Path basePath)
            throws IOException {
        try (final FileOutputStream fileOutputstream = new FileOutputStream(archiveFile);
                final ZipOutputStream out = new ZipOutputStream(fileOutputstream);) {
            for (final File tobeJared : tobeJaredList) {
                if (tobeJared == null || !tobeJared.exists() || tobeJared.isDirectory())
                    continue;
                final String relativePath = basePath.relativize(tobeJared.toPath()).toString().replace('\\', '/');
                // System.out.println("Adding " + relativePath);

                // Add archive entry
                final ZipEntry jarEntry = new ZipEntry(relativePath);
                jarEntry.setTime(tobeJared.lastModified());
                out.putNextEntry(jarEntry);

                // Write file to archive
                try (final FileInputStream in = new FileInputStream(tobeJared);) {
                    IOUtils.copy(in, out);
                }
            }
        }
    }
}