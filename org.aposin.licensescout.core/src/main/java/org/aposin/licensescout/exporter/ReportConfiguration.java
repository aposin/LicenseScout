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

import java.io.File;

/**
 * Properties for configuring the report output stage.
 * 
 * <p>Note: the showXxx variables are also accessed by the velocity template - be careful in renaming them,
 * because a normal Java search will not find the references.</p>
 * 
 */
public class ReportConfiguration {

    private boolean showLicenseCandidateFilesColumn;
    private boolean showMessageDigestColumn;
    private boolean showPathColumn;
    private boolean showDocumentationUrl;
    private File outputFile;

    /**
     * @return the totalArchiveCount
     */
    public final boolean getShowLicenseCandidateFilesColumn() {
        return showLicenseCandidateFilesColumn;
    }

    /**
     * @param totalArchiveCount the totalArchiveCount to set
     */
    public final void setShowLicenseCandidateFilesColumn(final boolean totalArchiveCount) {
        this.showLicenseCandidateFilesColumn = totalArchiveCount;
    }

    /**
     * @return the showMessageDigest
     */
    public final boolean isShowMessageDigestColumn() {
        return showMessageDigestColumn;
    }

    /**
     * @param showMessageDigest the showMessageDigest to set
     */
    public final void setShowMessageDigestColumn(boolean showMessageDigest) {
        this.showMessageDigestColumn = showMessageDigest;
    }

    /**
     * @return the showPathColumn
     */
    public final boolean isShowPathColumn() {
        return showPathColumn;
    }

    /**
     * @param showPathColumn the showPathColumn to set
     */
    public final void setShowPathColumn(final boolean showPathColumn) {
        this.showPathColumn = showPathColumn;
    }

    /**
     * @return the showDocumentationUrl
     */
    public final boolean isShowDocumentationUrl() {
        return showDocumentationUrl;
    }

    /**
     * @param showDocumentationUrl the showDocumentationUrl to set
     */
    public final void setShowDocumentationUrl(final boolean showDocumentationUrl) {
        this.showDocumentationUrl = showDocumentationUrl;
    }

    /**
     * @return the outputFile
     */
    public final File getOutputFile() {
        return outputFile;
    }

    /**
     * @param outputFile the outputFile to set
     */
    public final void setOutputFile(final File outputFile) {
        this.outputFile = outputFile;
    }

}
