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
package org.aposin.licensescout.filter;

import org.aposin.licensescout.util.ILFLog;

/**
 * Base class for implementations of archive list filters.
 * 
 */
public abstract class AbstractArchiveListFilter implements IArchiveListFilter {

    private final ILFLog log;
    private boolean filterActive;

    /**
     * Constructor.
     * 
     * @param log the logger
     */
    protected AbstractArchiveListFilter(final ILFLog log) {
        this.log = log;
    }

    /**
     * Constructor.
     * 
     * @param log the logger
     */
    protected AbstractArchiveListFilter(final ILFLog log, final boolean filterActive) {
        this(log);
        setFilterActive(filterActive);
    }

    /**
     * @return the logger
     */
    protected final ILFLog getLog() {
        return log;
    }

    /**
     * @return the filterActive
     */
    public final boolean isFilterActive() {
        return filterActive;
    }

    /**
     * @param filterActive the filterActive to set
     */
    public final void setFilterActive(final boolean filterActive) {
        this.filterActive = filterActive;
    }

}
