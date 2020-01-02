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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a file from the file system.
 * 
 * <p>Holds a reference to the file object.</p>
 *
 * @see JarEntryContainer
 */
public class FileSystemEntryContainer implements EntryContainer {

    private final File file;

    /**
     * Constructor.
     * @param file a file
     */
    public FileSystemEntryContainer(final File file) {
        this.file = file;
    }

    /**
     * @return the file
     */
    public final File getFile() {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final InputStream getInputStream() throws IOException {
        return new FileInputStream(getFile());
    }

}