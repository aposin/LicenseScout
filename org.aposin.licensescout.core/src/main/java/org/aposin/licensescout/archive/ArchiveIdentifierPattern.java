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
import java.util.regex.Pattern;

/**
 * Identifier for an archive bases on a regular expression for the name.
 * 
 * <p>Always uses {@link NameMatchingType#PATTERN} to designate using regular expressions. {@link PatternType} that determines if the regex is applied on either the archive name or the archive path
 * is passed to the constructor.</p> 
 * 
 * @see ArchiveIdentifierVersion
 * @see PatternType
 *
 */
public class ArchiveIdentifierPattern extends ArchiveIdentifier {

    private final PatternType patternType;
    private final Pattern pattern;

    /**
     * Constructor.
     * 
     * @param archiveType type of the archive
     * @param patternType 
     * @param regex the regular expression
     */
    public ArchiveIdentifierPattern(final ArchiveType archiveType, final PatternType patternType, final String regex) {
        super(archiveType, NameMatchingType.PATTERN, regex);
        this.patternType = patternType;
        pattern = Pattern.compile(regex);
    }

    /**
     * @return the patternType
     */
    public final PatternType getPatternType() {
        return patternType;
    }

    /**
     * @return the pattern
     */
    public final Pattern getPattern() {
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(pattern.pattern(), patternType);
        return result;
    }

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
        ArchiveIdentifierPattern other = (ArchiveIdentifierPattern) obj;
        return Objects.equals(pattern.pattern(), other.pattern.pattern()) && patternType == other.patternType;
    }

}
