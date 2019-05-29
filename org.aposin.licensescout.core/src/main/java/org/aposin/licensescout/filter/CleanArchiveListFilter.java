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
/**
 * 
 */
package org.aposin.licensescout.filter;

import java.util.List;
import java.util.Set;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.util.ILFLog;

/**
 */
public class CleanArchiveListFilter extends SimpleArchiveListFilter {

    private final List<LegalStatus> legalStatesToFilterOut;
    private final List<License> licensesToFilterOut;

    /**
     * @param log the logger
     * @param filterActive true if the filter should be active, false otherwise
     * @param legalStatesToFilterOut 
     * @param licensesToFilterOut 
     */
    public CleanArchiveListFilter(final ILFLog log, final boolean filterActive,
            final List<LegalStatus> legalStatesToFilterOut, List<License> licensesToFilterOut) {
        super(log, filterActive);
        this.legalStatesToFilterOut = legalStatesToFilterOut;
        this.licensesToFilterOut = licensesToFilterOut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFilterLogMessageTemplate() {
        return "Archive filtered out by clean filter: ";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean archiveIncluded(Archive archive) {
        return !legalStatesToFilterOut.contains(archive.getLegalStatus())
                && !hasLicenseToFilterOut(archive.getLicenses());
    }

    private boolean hasLicenseToFilterOut(final Set<License> actualLicenses) {
        for (License actualLicense : actualLicenses) {
            if (licensesToFilterOut.contains(actualLicense)) {
                return true;
            }
        }
        return false;
    }
}
