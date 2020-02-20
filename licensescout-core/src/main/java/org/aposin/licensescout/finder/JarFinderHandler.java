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

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;
import org.aposin.licensescout.util.ArchiveMetaInformation;
import org.aposin.licensescout.util.ILSLog;
import org.aposin.licensescout.util.JarUtil;

/**
 * Handler for JAR files.
 *
 */
public class JarFinderHandler extends AbstractFinderHandler<JarEntry, JarEntryContainer, JarInputStream> {

    /**
     * @param log the logger
     */
    public JarFinderHandler(final ILSLog log) {
        super(log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseDirectoryRecursion() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntryName(final JarEntry entry) {
        return entry.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFile(final JarEntry entry) {
        return !entry.isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirectory(final JarEntry entry) {
        return entry.isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JarEntryContainer createEntryContainer(final JarInputStream ecBase) throws IOException {
        final byte[] archiveBytes = IOUtils.toByteArray(ecBase);
        if (archiveBytes.length == 0) {
            getLog().warn("file has length of zero bytes");
        }
        return new JarEntryContainer(archiveBytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveMetaInformation getArchiveMetaInformationFromManifest(final JarEntryContainer entryContainer)
            throws IOException {
        try (final InputStream inputStream = entryContainer.getInputStream()) {
            return JarUtil.getArchiveMetaInformationFromManifest(inputStream, getLog());
        }
    }

}