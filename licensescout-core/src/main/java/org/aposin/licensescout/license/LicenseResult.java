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

import java.util.List;

import org.aposin.licensescout.model.Notice;
import org.aposin.licensescout.model.Provider;

/**
 * Container for license information associated with an exception matched to an archive.
 * 
 * <p>Contains:</p>
 * <ul>
 * <li>a list of licenses</li>
 * <li>a documentation URL (optional)</li>
 * </ul>
 * 
 * @see LicenseCheckedList
 *
 */
public class LicenseResult {

    private final List<License> licenses;
    private final String documentationUrl;
    private final Notice notice;
    private final Provider provider;

    /**
     * Constructor.
     * @param licenses list of licenses (may be empty, but should not be null)
     * @param documentationUrl an URL to add to the result object as a link to license documentation (may be empty)
     * @param notice a notice (may be null)
     * @param provider a provider (may be null)
     */
    public LicenseResult(final List<License> licenses, final String documentationUrl, final Notice notice,
            final Provider provider) {
        this.licenses = licenses;
        this.documentationUrl = documentationUrl;
        this.notice = notice;
        this.provider = provider;
    }

    /**
     * @return the licenses
     */
    public final List<License> getLicenses() {
        return licenses;
    }

    /**
     * @return the documentationUrl
     */
    public final String getDocumentationUrl() {
        return documentationUrl;
    }

    /**
     * @return the notice
     */
    public final Notice getNotice() {
        return notice;
    }

    /**
     * @return the provider
     */
    public final Provider getProvider() {
        return provider;
    }

}
