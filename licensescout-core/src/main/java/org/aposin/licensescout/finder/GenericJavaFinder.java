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

import java.util.List;

import org.aposin.licensescout.license.IArtifactServerUtil;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.util.ILFLog;

/**
 * Unfinished skeleton for a new Finder that processes JAR files, but the collection is based on a Maven dependency tree.
 */
public class GenericJavaFinder extends AbstractJavaFinder {

    private List<EntryContainer> entryContainers;

    /**
     * Constructor.
     * @param licenseStoreData the data object containing information on licenses
     * @param artifactServerUtil a helper object for accessing artifact servers
     * @param log the logger 
     */
    public GenericJavaFinder(final LicenseStoreData licenseStoreData, final IArtifactServerUtil artifactServerUtil,
            final ILFLog log) {
        super(licenseStoreData, artifactServerUtil, log);
    }

    /**
     * Sets the list of entry containers.
     * @param entryContainers a list of entry containers
     */
    public void setEntryContainers(final List<EntryContainer> entryContainers) {
        this.entryContainers = entryContainers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void findLicensesImpl() throws Exception {
        if (entryContainers != null) {
            for (EntryContainer entryContainer : entryContainers) {
                // TODO: implementation
                entryContainer.toString();
            }
        }
    }

}
