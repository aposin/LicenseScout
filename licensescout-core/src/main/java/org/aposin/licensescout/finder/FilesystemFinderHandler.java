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
import java.io.IOException;

import org.aposin.licensescout.util.ArchiveMetaInformation;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.JarUtil;

public class FilesystemFinderHandler extends AbstractFinderHandler<File, FileSystemEntryContainer, File> {

    /**
     * Constructor.
     * 
     * @param log the logger
     */
    public FilesystemFinderHandler(ILFLog log) {
        super(log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseDirectoryRecursion() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntryName(final File entry) {
        return entry.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFile(final File file) {
        return file.isFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirectory(final File file) {
        return file.isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileSystemEntryContainer createEntryContainer(final File ecBase) throws IOException {
        return new FileSystemEntryContainer(ecBase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveMetaInformation getArchiveMetaInformationFromManifest(final FileSystemEntryContainer entryContainer)
            throws IOException {
        return JarUtil.getArchiveMetaInformationFromManifest(entryContainer.getFile(), getLog());
    }

}