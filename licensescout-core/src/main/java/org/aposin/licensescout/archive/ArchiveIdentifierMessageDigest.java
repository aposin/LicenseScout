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

import org.aposin.licensescout.model.LSMessageDigest;
import org.aposin.licensescout.util.MiscUtil;

/**
 * Identifier for an archive bases on name and hash code.
 * 
 * 
 * @see ArchiveIdentifierVersion
 *
 */
public class ArchiveIdentifierMessageDigest extends ArchiveIdentifier {

    private final LSMessageDigest messageDigest;

    /**
     * Constructor.
     * 
     * @param archiveType type of the archive
     * @param name
     * @param messageDigest a message digest as byte array
     */
    public ArchiveIdentifierMessageDigest(final ArchiveType archiveType, final String name,
            final LSMessageDigest messageDigest) {
        super(archiveType, NameMatchingType.EXACT, name);
        this.messageDigest = messageDigest;
    }

    /**
     * Constructor.
     * 
     * @param archiveType type of the archive
     * @param name
     * @param messageDigestHexString a message digest as hex string
     */
    public ArchiveIdentifierMessageDigest(final ArchiveType archiveType, final String name,
            final String messageDigestHexString) {
        this(archiveType, name, MiscUtil.getLSMessageDigestFromHexString(messageDigestHexString));
    }

    /**
     * @return the messageDigest
     */
    public final LSMessageDigest getMessageDigest() {
        return messageDigest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(messageDigest);
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
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ArchiveIdentifierMessageDigest other = (ArchiveIdentifierMessageDigest) obj;
        return Objects.equals(messageDigest, other.messageDigest);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String toString() {
        return "ArchiveIdentifierMessageDigest [archiveType=" + getArchiveType() + ", nameMatchingType="
                + getNameMatchingType() + ", name=" + getName() + ", messageDigest=" + messageDigest + "]";
    }

}
