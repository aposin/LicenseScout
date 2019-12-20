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

import org.aposin.licensescout.finder.FinderResult;
import org.aposin.licensescout.finder.JavaJarFinder;

/**
 * Container for result that are output to a report.
 * 
 * <p>Contains a {@link FinderResult} instance and statistics information.</p>
 * 
 * @see JavaJarFinder
 * @see FinderResult
 */
public class OutputResult {

    private FinderResult finderResult;
    private IDetectionStatusStatistics detectionStatusStatistics;
    private ILegalStatusStatistics legalStatusStatistics;
    private GeneralStatistics generalStatistics;
    private String messageDigestAlgorithm;
    private boolean pomResolutionUsed;

    /**
     */
    public OutputResult() {
        super();
    }

    /**
     * @return the finderResult
     */
    public final FinderResult getFinderResult() {
        return finderResult;
    }

    /**
     * @param finderResult the finderResult to set
     */
    public final void setFinderResult(final FinderResult finderResult) {
        this.finderResult = finderResult;
    }

    /**
     * @return the detectionStatusStatistics
     */
    public final IDetectionStatusStatistics getDetectionStatusStatistics() {
        return detectionStatusStatistics;
    }

    /**
     * @param detectionStatusStatistics the detectionStatusStatistics to set
     */
    public final void setDetectionStatusStatistics(final IDetectionStatusStatistics detectionStatusStatistics) {
        this.detectionStatusStatistics = detectionStatusStatistics;
    }

    /**
     * @return the legalStatusStatistics
     */
    public final ILegalStatusStatistics getLegalStatusStatistics() {
        return legalStatusStatistics;
    }

    /**
     * @param legalStatusStatistics the legalStatusStatistics to set
     */
    public final void setLegalStatusStatistics(final ILegalStatusStatistics legalStatusStatistics) {
        this.legalStatusStatistics = legalStatusStatistics;
    }

    /**
     * @return the generalStatistics
     */
    public final GeneralStatistics getGeneralStatistics() {
        return generalStatistics;
    }

    /**
     * @param generalStatistics the generalStatistics to set
     */
    public final void setGeneralStatistics(final GeneralStatistics generalStatistics) {
        this.generalStatistics = generalStatistics;
    }

    /**
     * @return the messageDigestAlgorithm
     */
    public final String getMessageDigestAlgorithm() {
        return messageDigestAlgorithm;
    }

    /**
     * @param messageDigestAlgorithm the messageDigestAlgorithm to set
     */
    public final void setMessageDigestAlgorithm(final String messageDigestAlgorithm) {
        this.messageDigestAlgorithm = messageDigestAlgorithm;
    }

    /**
     * @return the pomResolutionUsed
     */
    public final boolean isPomResolutionUsed() {
        return pomResolutionUsed;
    }

    /**
     * @param pomResolutionUsed the pomResolutionUsed to set
     */
    public final void setPomResolutionUsed(final boolean pomResolutionUsed) {
        this.pomResolutionUsed = pomResolutionUsed;
    }

}
