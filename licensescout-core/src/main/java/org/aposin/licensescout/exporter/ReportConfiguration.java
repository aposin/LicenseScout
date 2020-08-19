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
    private boolean showDocumentationUrlColumn;
    private boolean showProviderColumn;
    private File outputFile;
    private File templateFile;
    private String templateEncoding;
    private String outputEncoding;

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
     * @return the showDocumentationUrlColumn
     */
    public final boolean isShowDocumentationUrlColumn() {
        return showDocumentationUrlColumn;
    }

    /**
     * @param showDocumentationUrlColumn the showDocumentationUrlColumn to set
     */
    public final void setShowDocumentationUrlColumn(final boolean showDocumentationUrlColumn) {
        this.showDocumentationUrlColumn = showDocumentationUrlColumn;
    }

    /**
     * @return the showProviderColumn
     */
    public final boolean isShowProviderColumn() {
        return showProviderColumn;
    }

    /**
     * @param showProviderColumn the showProviderColumn to set
     */
    public final void setShowProviderColumn(boolean showProviderColumn) {
        this.showProviderColumn = showProviderColumn;
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

    /**
     * @return the templateFile
     */
    public final File getTemplateFile() {
        return templateFile;
    }

    /**
     * @param templateFile the templateFile to set
     */
    public final void setTemplateFile(final File templateFile) {
        this.templateFile = templateFile;
    }

    /**
     * @return the templateEncoding
     */
    public final String getTemplateEncoding() {
        return templateEncoding;
    }

    /**
     * @param templateEncoding the templateEncoding to set
     */
    public final void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = templateEncoding;
    }

    /**
     * @return the encoding
     */
    public final String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public final void setOutputEncoding(String encoding) {
        this.outputEncoding = encoding;
    }

}
