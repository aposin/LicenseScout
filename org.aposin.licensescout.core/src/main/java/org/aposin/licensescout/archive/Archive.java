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
package org.aposin.licensescout.archive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.aposin.licensescout.license.DetectionStatus;
import org.aposin.licensescout.license.LegalStatus;
import org.aposin.licensescout.license.License;
import org.aposin.licensescout.model.Author;
import org.aposin.licensescout.model.LicenseText;
import org.aposin.licensescout.model.Notice;
import org.aposin.licensescout.model.Provider;
import org.aposin.licensescout.util.MiscUtil;

/**
 * Representation of an archive or other artifact found during scanning.
 * 
 */
public class Archive implements Comparable<Archive> {

    private final ArchiveType archiveType;

    private final String fileName;

    private final String version;

    private final String path;

    private final MultiValuedMap<License, String> licenseList = new ArrayListValuedHashMap<>();
    private List<License> detectedLicenses;

    private DetectionStatus detectionStatus;

    private LegalStatus legalStatus;

    private byte[] messageDigest;

    private final List<String> licenseCandidateFiles = new ArrayList<>();

    private String vendor;

    private String documentationUrl;

    private Provider provider;
    private Notice notice;

    // NOTE: author is currently not used
    private Author author;
    private LicenseText licenseText;

    /**
     * Instantiates a new archive.
     * @param archiveType the type of the archive
     * @param fileName the file name
     * @param version the version of the archive
     * @param path the path
     */
    public Archive(final ArchiveType archiveType, final String fileName, final String version, final String path) {
        super();
        this.archiveType = archiveType;
        this.fileName = fileName;
        this.version = version;
        this.path = path;
    }

    /**
     * @return the archiveType
     */
    public final ArchiveType getArchiveType() {
        return archiveType;
    }

    /**
     * Gets the file name.
     * 
     * @return the file name
     */
    public final String getFileName() {
        return fileName;
    }

    /**
     * @return the version
     */
    public final String getVersion() {
        return version;
    }

    /**
     * Gets the path.
     * 
     * @return the path
     */
    public final String getPath() {
        return path;
    }

    /**
     * Adds a license
     * @param license a license object
     * @param path the path to the file containing the license
     */
    public void addLicense(final License license, final String path) {
        licenseList.put(license, path);
    }

    /**
     * Clears the list of licenses.
     */
    public void clearLicenses() {
        licenseList.clear();
    }

    /**
     * Gets the licenses.
     * 
     * @return the licenses
     */
    public Set<License> getLicenses() {
        return licenseList.keySet();
    }

    /**
     * Gets the file paths.
     * 
     * @param license the license
     * @return the file paths
     */
    public Collection<String> getFilePaths(final License license) {
        return licenseList.get(license);
    }

    /**
     * @return the detectedLicenses
     */
    public final List<License> getDetectedLicenses() {
        return detectedLicenses;
    }

    /**
     * @param detectedLicenses the detectedLicenses to set
     */
    public final void setDetectedLicenses(List<License> detectedLicenses) {
        this.detectedLicenses = detectedLicenses;
    }

    /**
     * @return the detectionStatus
     */
    public final DetectionStatus getDetectionStatus() {
        return detectionStatus;
    }

    /**
     * @param detectionStatus the detectionStatus to set
     */
    public final void setDetectionStatus(DetectionStatus detectionStatus) {
        this.detectionStatus = detectionStatus;
    }

    /**
     * @return the legalStatus
     */
    public final LegalStatus getLegalStatus() {
        return legalStatus;
    }

    /**
     * @param legalStatus the legalStatus to set
     */
    public final void setLegalStatus(LegalStatus legalStatus) {
        this.legalStatus = legalStatus;
    }

    /**
     * @return the messageDigest
     */
    public final byte[] getMessageDigest() {
        return messageDigest;
    }

    /**
     * @param messageDigest the messageDigest to set
     */
    public final void setMessageDigest(final byte[] messageDigest) {
        this.messageDigest = messageDigest;
    }

    /**
     * @return the messageDigest
     */
    public final String getMessageDigestString() {
        if (messageDigest != null) {
            return MiscUtil.getHexString(messageDigest);
        } else {
            return "";
        }
    }

    /**
     * @return the licenseCandidateFiles
     */
    public final List<String> getLicenseCandidateFiles() {
        return licenseCandidateFiles;
    }

    /**
     * @param filePath
     */
    public final void addLicenseCandidateFile(final String filePath) {
        licenseCandidateFiles.add(filePath);
    }

    /**
     * @return the vendor
     */
    public final String getVendor() {
        return vendor;
    }

    /**
     * @param vendor the vendor to set
     */
    public final void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * @return the documentationUrl
     */
    public final String getDocumentationUrl() {
        return documentationUrl;
    }

    /**
     * @param documentationUrl the documentationUrl to set
     */
    public final void setDocumentationUrl(final String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    /**
     * @return the provider
     */
    public final Provider getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public final void setProvider(Provider provider) {
        this.provider = provider;
    }

    /**
     * @return the notice
     */
    public final Notice getNotice() {
        return notice;
    }

    /**
     * @param notice the notice to set
     */
    public final void setNotice(Notice notice) {
        this.notice = notice;
    }

    /**
     * @return the author
     */
    public final Author getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public final void setAuthor(Author author) {
        this.author = author;
    }

    /**
     * @return the licenseText
     */
    public final LicenseText getLicenseText() {
        return licenseText;
    }

    /**
     * @param licenseText the licenseText to set
     */
    public final void setLicenseText(LicenseText licenseText) {
        this.licenseText = licenseText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Archive other) {
        if (this == other) {
            return 0;
        }
        return this.getFileName().compareToIgnoreCase(other.getFileName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Archive) {
            return compareTo((Archive) other) == 0;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Archive [archiveType=");
        builder.append(archiveType);
        builder.append(", fileName=");
        builder.append(fileName);
        builder.append(", version=");
        builder.append(version);
        builder.append(", path=");
        builder.append(path);
        builder.append(", licenseList=");
        builder.append(licenseList);
        builder.append(", detectionStatus=");
        builder.append(detectionStatus);
        builder.append(", legalStatus=");
        builder.append(legalStatus);
        builder.append(", messageDigest=");
        builder.append(Arrays.toString(messageDigest));
        builder.append("]");
        return builder.toString();
    }

}
