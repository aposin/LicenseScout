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

import java.util.Iterator;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.util.ILSLog;

/**
 * Filter implementation that allows to define a single method containing a predicate related to an archive.
 * 
 */
public abstract class SimpleArchiveListFilter extends AbstractArchiveListFilter {

    /**
     * @param log the logger
     * @param filterActive true if the filter should be active, false otherwise
     */
    public SimpleArchiveListFilter(final ILSLog log, boolean filterActive) {
        super(log);
        setFilterActive(filterActive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(List<Archive> archiveFiles) {
        if (isFilterActive()) {
            final Iterator<Archive> iter = archiveFiles.iterator();
            while (iter.hasNext()) {
                final Archive archive = iter.next();
                if (!archiveIncluded(archive)) {
                    getLog().info(getFilterLogMessageTemplate() + archive.getPath());
                    iter.remove();
                }
            }
        }
    }

    /**
     * Obtains a message template for the log message about an archive being removed from the list.
     * @return a message template
     */
    protected abstract String getFilterLogMessageTemplate();

    /**
     * Tests if an archive should be included by the filter.
     * 
     * @param archive an archive
     * @return true if the archive should be included, false otherwise (will result in the archive being removed from the list)
     */
    protected abstract boolean archiveIncluded(final Archive archive);

}
