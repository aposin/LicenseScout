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

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.util.ILFLog;

/**
 * Filters out by vendors given in the MANIFEST.MF or package.json.
 * 
 */
public class VendorArchiveListFilter extends SimpleArchiveListFilter {

    private final List<String> filteredVendorNames;

    /**
     * @param filteredVendorNames 
     * @param log the logger
     * @param filterActive true if the filter should be active, false otherwise
     */
    public VendorArchiveListFilter(final List<String> filteredVendorNames, final ILFLog log,
            final boolean filterActive) {
        super(log, filterActive);
        this.filteredVendorNames = filteredVendorNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFilterLogMessageTemplate() {
        return "Archive filtered out by vendor filter: ";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean archiveIncluded(final Archive archive) {
        return !filteredVendorNames.contains(archive.getVendor());
    }
}
