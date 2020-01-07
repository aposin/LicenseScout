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

import org.aposin.licensescout.model.LSMessageDigest;
import org.aposin.licensescout.util.ArchiveMetaInformation;

/**
 * A handler abstracting an underlying hierarchical storage for file-like objects.
 * 
 * <p>This interface is used to use common code for handling normal file systems and entries read from a JAR file.</p>
 * <p>Related to this interface is the interface {@link EntryContainer}, which represents the contents of a single file-like object.</p>
 * 
 * @param <F> type of the entry object (used for detection of file/directory nature)
 * @param <C> type of entry container objects
 * @param <I> type of the entry object (used for creation of entry container objects)
 * 
 * @see EntryContainer
 * @see FilesystemFinderHandler
 * @see JarFinderHandler
 */
public interface FinderHandler<F, C extends EntryContainer, I> {

    /**
     * Checks if the algorithm should go into a recursion for non-archive directories.
     * 
     * @return true if directory recursion should be used, false otherwise
     */
    public boolean isUseDirectoryRecursion();

    /**
     * Obtains the entry name from an entry object.
     * @param entry an entry object
     * @return the entry name
     */
    public String getEntryName(F entry);

    /**
     * Checks if an entry object represents a file.
     * @param file an entry object
     * @return true if the entry object represents a file, false otherwise
     */
    public boolean isFile(F file);

    /**
     * Checks if an entry object represents a directory.
     * @param file an entry object
     * @return true if the entry object represents a directory, false otherwise
     */
    public boolean isDirectory(F file);

    /**
     * Creates an entry container object from an entry object.
     * 
     * @param ecBase the object the entry container is based on. Note that this is a File object in case of a file system handler or an InputStream in case of a JAR handler.
     * @return an entry container object
     * @throws IOException
     */
    public C createEntryContainer(I ecBase) throws IOException;

    /**
     * Obtains information from the MANIFEST.MF of an entry container.
     * @param entryContainer
     * @return an object containing meta data from the MANIFEST.MF
     * @throws IOException
     */
    public ArchiveMetaInformation getArchiveMetaInformationFromManifest(C entryContainer) throws IOException;

    /**
     * Calculates the message digest for an entry container.
     * @param entryContainer
     * @return a message digest object
     * @throws IOException
     */
    public LSMessageDigest calculateMessageDigest(C entryContainer) throws IOException;

}