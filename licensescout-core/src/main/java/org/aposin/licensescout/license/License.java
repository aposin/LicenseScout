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
package org.aposin.licensescout.license;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.model.Notice;

/**
 * The Class License.
 */
public class License implements Comparable<License> {

    private final String spdxIdentifier;
    private final String name;
    private final LegalStatus legalStatus;
    private final String author;
    private final String version;
    private final List<String> alternativeVersions = new ArrayList<>();
    private final String urlPublic;
    private boolean versionDirty = true;
    private final List<String> secondaryUrls = new ArrayList<>();
    private final String text;
    private final Notice notice;

    /**
     * Instantiates a new license.
     * @param spdxIdentifier the SPDX short identifier of the license
     * @param name the name
     * @param legalStatus 
     * @param author the author
     * @param version the version
     * @param urlPublic the public URL
     * @param text 
     * @param notice a notice (may be null)
     */
    public License(final String spdxIdentifier, final String name, final LegalStatus legalStatus, final String author,
            final String version, final String urlPublic, final String text, final Notice notice) {
        this.spdxIdentifier = spdxIdentifier;
        this.name = name;
        this.legalStatus = legalStatus;
        this.author = author;
        this.version = version;
        this.urlPublic = urlPublic;
        this.text = text;
        this.notice = notice;
        this.versionDirty = StringUtils.isEmpty(version);
    }

    /**
     * @return the spdxIdentifier
     */
    public final String getSpdxIdentifier() {
        return spdxIdentifier;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the isAccepted
     */
    public final LegalStatus getLegalStatus() {
        return legalStatus;
    }

    /**
     * Gets the url public.
     * 
     * @return the url public
     */
    public final String getUrlPublic() {
        return urlPublic;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    public final String getVersion() {
        if (version != null) {
            return version.trim();
        } else {
            return "";
        }
    }

    /**
     * Adds an alternative version number used for detection.
     * @param version 
     */
    public final void addAlternativeVersion(final String version) {
        alternativeVersions.add(version);
    }

    /**
     * @return the alternativeVersions
     */
    public final List<String> getAlternativeVersions() {
        return alternativeVersions;
    }

    /**
     * Gets the author.
     * 
     * @return the author
     */
    public final String getAuthor() {
        return author;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return name != null ? name : "";
    }

    /**
     * Checks if is version dirty.
     * 
     * @return true, if is version dirty
     */
    public final boolean isVersionPresent() {
        return !versionDirty;
    }

    /**
     * @param url 
     */
    public final void addSecondaryUrl(final String url) {
        secondaryUrls.add(url);
    }

    /**
     * @return the secondaryPublicUrls
     */
    public final List<String> getSecondaryUrls() {
        return secondaryUrls;
    }

    /**
     * @return the text
     */
    public final String getText() {
        return text;
    }

    /**
     * @return the notice
     */
    public final Notice getNotice() {
        return notice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final License other) {
        int compareTo = this.getName().compareTo(other.getName());
        if (compareTo == 0) {
            return this.getVersion().compareTo(other.getVersion());
        }
        return compareTo;
    }
}
