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

public interface FinderHandler<F, C extends EntryContainer, I> {

    /**
     * Checks if the algorithm should go into a recursion for non-archive directories.
     * 
     * @return true if directory recursion should be used, false otherwise
     */
    public boolean isUseDirectoryRecursion();

    public String getEntryName(F entry);

    public boolean isFile(F file);

    public boolean isDirectory(F file);

    public C createEntryContainer(I ecBase) throws IOException;

    public ArchiveMetaInformation getArchiveMetaInformationFromManifest(C entryContainer) throws IOException;

    public LSMessageDigest calculateMessageDigest(C entryContainer) throws IOException;

}