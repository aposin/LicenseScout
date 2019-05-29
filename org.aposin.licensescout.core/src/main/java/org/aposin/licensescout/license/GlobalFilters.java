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
package org.aposin.licensescout.license;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveIdentifier;
import org.aposin.licensescout.archive.ArchiveIdentifierPattern;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.archive.PatternType;

/**
 * Contains a set of patterns for archives that should be filtered out.
 * 
 */
public class GlobalFilters {

    private Set<ArchiveIdentifierPattern> filters = new HashSet<>();

    /**
     * Constructor.
     */
    public GlobalFilters() {
        // DO NOTHING
    }

    /**
     * Reads global filters from a file.
     * 
     * @param filename a filename
     * @throws IOException if an error occurs
     */
    public void read(final String filename) throws IOException {
        String line = "";

        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {

                // ignore lines commented out
                if (line.startsWith("#") || StringUtils.isEmpty(line)) {
                    continue;
                }
                ArchiveIdentifier archiveIdentifier = null;
                final ArchiveType archiveType = ArchiveType.JAVA;
                // use comma as separator
                final String name = line;
                if (name.length() >= 0 && name.charAt(0) == '=') {
                    PatternType patternType;
                    int patternStartIndex;
                    if (name.length() >= 1 && name.charAt(1) == '=') {
                        patternType = PatternType.PATTERN_ON_PATH;
                        patternStartIndex = 2;
                    } else {
                        patternType = PatternType.PATTERN_ON_FILENAME;
                        patternStartIndex = 1;
                    }
                    final String regex = name.substring(patternStartIndex);
                    archiveIdentifier = new ArchiveIdentifierPattern(archiveType, patternType, regex);
                } else {
                    System.out.println("read line with error, no pattern found");
                    archiveIdentifier = null;
                }
                if (archiveIdentifier != null) {
                    filters.add((ArchiveIdentifierPattern) archiveIdentifier);
                }
            }
        }
    }

    /** Checks if an archive should be filtered out by global filters.
     * 
     * @param archive
     * @return true if the archive should be filtered out, false otherwise
     */
    public boolean isFiltered(final Archive archive) {
        final Iterator<ArchiveIdentifierPattern> iter = filters.iterator();
        final String name = archive.getFileName();
        final String path = archive.getPath();
        while (iter.hasNext()) {
            final ArchiveIdentifierPattern aip = iter.next();
            final Pattern pattern = aip.getPattern();
            final String checkedString = aip.getPatternType() == PatternType.PATTERN_ON_FILENAME ? name : path;
            final Matcher matcher = pattern.matcher(checkedString);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

}
