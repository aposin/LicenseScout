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
package org.aposin.licensescout.exporter;

import org.aposin.licensescout.license.LicenseUtil;

/**
 * Container for general statistics information.
 * 
 * @see OutputResult
 * @see LicenseUtil#calculateGeneralStatistics(java.util.Collection)
 */
public class GeneralStatistics {

    private int totalArchiveCount;
    private int candidateLicenseFileCount;

    /**
     * @return the totalArchiveCount
     */
    public final int getTotalArchiveCount() {
        return totalArchiveCount;
    }

    /**
     * @param totalArchiveCount the totalArchiveCount to set
     */
    public final void setTotalArchiveCount(int totalArchiveCount) {
        this.totalArchiveCount = totalArchiveCount;
    }

    /**
     * @return the candidateLicenseFileCount
     */
    public final int getCandidateLicenseFileCount() {
        return candidateLicenseFileCount;
    }

    /**
     * @param candidateLicenseFileCount the candidateLicenseFileCount to set
     */
    public final void setCandidateLicenseFileCount(int candidateLicenseFileCount) {
        this.candidateLicenseFileCount = candidateLicenseFileCount;
    }

}
