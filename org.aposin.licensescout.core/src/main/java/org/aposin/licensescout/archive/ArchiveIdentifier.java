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

/**
 * Base class for Identifier for an archive bases on name.
 * 
 * <p>An archive can be identified either by:</p>
 * <ol>
 * <li>Name and version ({@link ArchiveIdentifierVersion})</li>
 * <li>Name and message digest ({@link ArchiveIdentifierMessageDigest})</li>
 * <li>Regular expression on either the name or the path ({@link ArchiveIdentifierPattern})</li>
 * </ol>
 * 
 * @see ArchiveIdentifierVersion
 * @see ArchiveIdentifierMessageDigest
 * @see ArchiveIdentifierPattern
 */
public abstract class ArchiveIdentifier {

    private final ArchiveType archiveType;
    private final NameMatchingType nameMatchingType;
    private final String name;

    /**
     * Constructor.
     * 
     * @param archiveType type of the archive
     * @param name
     */
    protected ArchiveIdentifier(final ArchiveType archiveType, final NameMatchingType nameMatchingType,
            final String name) {
        super();
        this.archiveType = archiveType;
        this.nameMatchingType = nameMatchingType;
        this.name = name;
    }

    /**
     * @return the archiveType
     */
    public final ArchiveType getArchiveType() {
        return archiveType;
    }

    /**
     * @return the nameMatchingType
     */
    public final NameMatchingType getNameMatchingType() {
        return nameMatchingType;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((archiveType == null) ? 0 : archiveType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ArchiveIdentifier other = (ArchiveIdentifier) obj;
        if (archiveType != other.archiveType) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
