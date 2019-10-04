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
package org.aposin.licensescout.archive;

import java.util.Objects;

/**
 * Identifier for an archive bases on name and version.
 * 
 * @see ArchiveIdentifierMessageDigest
 */
public class ArchiveIdentifierVersion extends ArchiveIdentifier {

    private final String version;

    /**
     * Constructor.
     * @param archiveType type of the archive
     * @param name
     * @param version
     */
    public ArchiveIdentifierVersion(final ArchiveType archiveType, final String name, final String version) {
        super(archiveType, NameMatchingType.EXACT, name);
        this.version = version;
    }

    /**
     * @return the version
     */
    public final String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ArchiveIdentifierVersion)) { return false; }
        if (!super.equals(o)) { return false; }
        ArchiveIdentifierVersion that = (ArchiveIdentifierVersion) o;
        return Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVersion());
    }
}
