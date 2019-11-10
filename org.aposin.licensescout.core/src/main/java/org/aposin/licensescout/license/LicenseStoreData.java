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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.model.Notice;
import org.aposin.licensescout.model.Notices;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.sax.AbstractSaxHandler;
import org.aposin.licensescout.util.sax.IElementHandler;
import org.aposin.licensescout.util.sax.NopElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Collection of licenses and mappings of names and URLs to licenses.
 * 
 * <p>Initialisation:</p>
 * <ol>
 * <li>Create an instance</li>
 * <li>Call {@link #readLicenses(File, Notices, boolean, ILFLog)} to read in the licenses from a file</li>
 * <li>Call {@link #readNameMappings(String, ILFLog)} and {@link #readUrlMappings(String, ILFLog)}</li>
 * </ol>
 */
public class LicenseStoreData {

    /**
     * Maps detection strings to lists of licenses.
     */
    private Map<String, List<License>> detectionStringMap = new HashMap<>();
    /**
     * Maps SPDX identifiers to licenses
     */
    private Map<String, License> spdxStore = new HashMap<>();
    /**
     * Maps license URLs associated with a license directly to licenses.
     */
    private Map<String, License> urlStore = new HashMap<>();
    /**
     * Maps license URLs from the external URL mapping to licenses.
     */
    private Map<String, List<License>> urlMappings = new HashMap<>();
    /**
     * Maps license names from the external name mapping to licenses.
     */
    private Map<String, List<License>> nameMappings = new HashMap<>();

    /**
     * Constructor.
     */
    public LicenseStoreData() {
        // DO NOTHING
    }

    private void addToSpdxAndUrlStore(final License license, final ILFLog log) {
        final String spdxIdentifier = license.getSpdxIdentifier();
        if (!StringUtils.isEmpty(spdxIdentifier)) {
            final License existingLicense = spdxStore.get(spdxIdentifier);
            if (existingLicense != null && !existingLicense.equals(license)) {
                log.warn("double SPDX identifier: '" + spdxIdentifier + "'");
            }
            spdxStore.put(spdxIdentifier, license);
        }
        final String url = license.getUrlPublic();
        addToUrlStore(url, license, log);
        for (final String secondaryUrl : license.getSecondaryUrls()) {
            addToUrlStore(secondaryUrl, license, log);
        }
    }

    private void addToUrlStore(final String url, final License license, ILFLog log) {
        if (!StringUtils.isEmpty(url)) {
            final License existingLicense = urlStore.get(url);
            if (existingLicense != null && !existingLicense.equals(license)) {
                log.warn("double License URL: '" + url + "'");
            }
            urlStore.put(url, license);
        }
    }

    /**
     * Gets the license from the license store by the SPDX identifier.
     * 
     * @param spdxIdentifier the SPDX short identifier
     * @return the license or null
     */
    public License getLicenseBySpdxIdentifier(final String spdxIdentifier) {
        return spdxStore.get(spdxIdentifier);
    }

    /**
     * Gets the license from the license store by the public URL.
     * 
     * @param url the URL
     * @return the license or null
     */
    public License getLicenseByPublicUrl(final String url) {
        return urlStore.get(url);
    }

    /**
     * Gets the license from the license store by the public URL.
     * 
     * @param url the URL
     * @return a list of licenses or null
     */
    public List<License> getLicensesFromUrlMapping(final String url) {
        return urlMappings.get(url);
    }

    /**
     * Gets the license from the license store by license name.
     * 
     * @param licenseName a license name
     * @return a list of licenses or null
     */
    public List<License> getLicensesFromNameMapping(final String licenseName) {
        return nameMappings.get(licenseName);
    }

    /**
     * Exposes the internal data structure of the license store.
     * 
     * <p>Used for automatic detection of licenses by strings in {@link LicenseUtil#detectLicenses(BufferedReader, LicenseStoreData)}.</p>
     * 
     * @return a set of license definitions
     */
    public Set<Entry<String, List<License>>> getLicenseDetectionStringMap() {
        return detectionStringMap.entrySet();
    }

    /**
     * Reads known licenses from an XML file.
     * 
     * @param file
     * @param notices 
     * @param validateXml true if the license XML file should be validated, false otherwise
     * @param log the logger
     * @throws IOException
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public void readLicenses(final File file, final Notices notices, boolean validateXml, final ILFLog log)
            throws IOException, ParserConfigurationException, SAXException {

        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(validateXml);
        final SAXParser saxParser = spf.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new LicenseSaxHandler(notices, log));
        xmlReader.parse(file.toURI().toString());

        addNoManualInformationLicense(log);
    }

    private void addNoManualInformationLicense(final ILFLog log) {
        final License license = new License(LicenseSpdxIdentifier.NO_MANUAL_INFORMATION, "no manual information",
                LegalStatus.UNKNOWN, "", "", "", "", null);
        addToSpdxAndUrlStore(license, log);
    }

    /**
	 * Reads license URL mappings from a CSV file.
	 * 
	 * @param filename
	 * @param log the logger
	 * @throws IOException
	 */
	public void readUrlMappings(final String filename, final ILFLog log) throws IOException {
	    String line = "";
	    final String cvsSplitBy = ",";
	
	    try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
	
	        while ((line = br.readLine()) != null) {
	            if (StringUtils.isEmpty(line) || line.startsWith("#")) {
	                continue;
	            }
	            final String[] values = line.split(cvsSplitBy);
	            final String url = values[0].trim();
	            final int numLicenseNames = values.length - 1;
	            final List<License> licenses = new ArrayList<>();
	            for (int i = 0; i < numLicenseNames; i++) {
	                final String spdxIdentifier = values[i + 1].trim();
	                final License license = getLicenseBySpdxIdentifier(spdxIdentifier);
	                if (license != null) {
	                    licenses.add(license);
	                } else {
	                    log.info("readUrlMappings: SPDX identifier not found: " + spdxIdentifier);
	                }
	            }
	            urlMappings.put(url, licenses);
	        }
	    }
	}

	/**
	 * Reads license name mappings from a CSV file.
	 * 
	 * @param filename
	 * @param log the logger
	 * @throws IOException
	 */
	public void readNameMappings(final String filename, final ILFLog log) throws IOException {
	    String line = "";
	    final String cvsSplitBy = ",";
	
	    try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
	
	        while ((line = br.readLine()) != null) {
	            if (StringUtils.isEmpty(line) || line.startsWith("#")) {
	                continue;
	            }
	            final String[] values = line.split(cvsSplitBy);
	            final String mappedName = values[0].trim();
	            final int numLicenseNames = values.length - 1;
	            final List<License> licenses = new ArrayList<>();
	            for (int i = 0; i < numLicenseNames; i++) {
	                final String spdxIdentifier = values[i + 1].trim();
	                final License license = getLicenseBySpdxIdentifier(spdxIdentifier);
	                if (license != null) {
	                    licenses.add(license);
	                } else {
	                    log.info("readNameMappings: SPDX identifier not found: " + spdxIdentifier);
	                }
	            }
	            nameMappings.put(mappedName, licenses);
	        }
	    }
	}

	private class LicenseSaxHandler extends AbstractSaxHandler {
    	
        private static final String ELEMENT_LICENSES = "licenses";
        private static final String ELEMENT_LICENSE = "license";
        private static final String ELEMENT_SPDX_IDENTIFIER = "spdxIdentifier";
        private static final String ELEMENT_LEGAL_STATUS = "legalStatus";
        private static final String ELEMENT_NAME = "name";
        private static final String ELEMENT_AUTHOR = "author";
        private static final String ELEMENT_VERSION = "version";
        private static final String ELEMENT_ALTERNATIVE_VERSION = "alternativeVersion";
        private static final String ELEMENT_PUBLIC_URL = "publicUrl";
        private static final String ELEMENT_SECONDARY_URL = "secondaryUrl";
        private static final String ELEMENT_LICENSE_SET = "licenseSet";
        private static final String ELEMENT_DETECTION_STRING = "detectionString";
        private static final String ELEMENT_NOTICE = "notice";
        private static final String ELEMENT_LICENSE_TEXT = "licenseText";

        private final Notices notices;
        private final Map<String, License> licenseInternalIds = new HashMap<>();
        private String licenseInternalId;
        private String spdxIdentifier;
        private LegalStatus legalStatus;
        private String name;
        private String author;
        private String version;
        private List<String> alternativeVersions;
        private String publicUrl;
        private List<String> secondaryUrls;
        private List<License> licenseSet;
        private List<String> detectionStrings;
        private Notice notice;
        private String licenseText;

        private final IElementHandler standardLicenseHandler = new StandardLicenseHandler();
        private final IElementHandler licenseSetLicenseHandler = new LicenseSetLicenseHandler();

        /**
         * Constructor.
         * @param notices 
         * @param log the logger
         */
        public LicenseSaxHandler(final Notices notices, final ILFLog log) {
            super(log);
            this.notices = notices;
            setElementHandler(new NopElementHandler(ELEMENT_LICENSES));
            setElementHandler(standardLicenseHandler);
            setElementHandler(new SpdxIdentifierElementHandler());
            setElementHandler(new LegalStatusElementHandler());
            setElementHandler(new NameElementHandler());
            setElementHandler(new AuthorElementHandler());
            setElementHandler(new VersionElementHandler());
            setElementHandler(new AlternativeVersionElementHandler());
            setElementHandler(new PublicUrlElementHandler());
            setElementHandler(new SecondaryUrlElementHandler());
            setElementHandler(new LicenseSetElementHandler());
            setElementHandler(new DetectionStringElementHandler());
            setElementHandler(new NoticeElementHandler());
            setElementHandler(new LicenseTextElementHandler());
        }

        /**
         * @return the notices
         */
        protected final Notices getNotices() {
            return notices;
        }

        private class StandardLicenseHandler implements IElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_LICENSE;
            }

            /** {@inheritDoc} */
            @Override
            public void startElement(String uri, String localName, String qName, final Attributes attributes) {
                licenseInternalId = attributes.getValue(ATTRIBUTE_ID);
                spdxIdentifier = null;
                legalStatus = null;
                name = null;
                author = null;
                version = null;
                alternativeVersions = new ArrayList<>();
                publicUrl = null;
                secondaryUrls = new ArrayList<>();
                notice = null;
                licenseText = null;
            }

            /** {@inheritDoc} */
            @Override
            public void endElement(String uri, String localName, String qName) {
                if (name == null) {
                    getLog().warn("No name for license ID: '" + licenseInternalId + "'");
                }
                if (licenseText == null) {
                    getLog().warn("No license text for license ID: '" + licenseInternalId + "'");
                    licenseText = "No license text";
                }
                final License license = new License(spdxIdentifier, name, legalStatus, author, version, publicUrl,
                        licenseText, notice);
                for (final String alternativeVersion : alternativeVersions) {
                    license.addAlternativeVersion(alternativeVersion);
                }
                for (final String secondaryUrl : secondaryUrls) {
                    license.addSecondaryUrl(secondaryUrl);
                }
                licenseInternalIds.put(licenseInternalId, license);
                addToSpdxAndUrlStore(license, getLog());
            }
        }

        private class LicenseSetLicenseHandler implements IElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_LICENSE;
            }

            /** {@inheritDoc} */
            @Override
            public void startElement(String uri, String localName, String qName, final Attributes attributes)
                    throws SAXException {
                final String licenseInternalIdref = attributes.getValue(ATTRIBUTE_IDREF);
                if (!StringUtils.isEmpty(licenseInternalIdref)) {
                    final License license = licenseInternalIds.get(licenseInternalIdref);
                    if (license != null) {
                        licenseSet.add(license);
                    } else {
                        throw new SAXException("no license found with id: '" + licenseInternalIdref + "'");
                    }
                } else {
                    throw new SAXException("no idref attribute");
                }
            }

            /** {@inheritDoc} */
            @Override
            public void endElement(String uri, String localName, String qName) {
                // DO NOTHING
            }
        }

        private class LicenseSetElementHandler implements IElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_LICENSE_SET;
            }

            /** {@inheritDoc} */
            @Override
            public void startElement(String uri, String localName, String qName, final Attributes attributes) {
                detectionStrings = new ArrayList<>();
                licenseSet = new ArrayList<>();
                setInLicenseSet(true);
            }

            /** {@inheritDoc} */
            @Override
            public void endElement(String uri, String localName, String qName) {
                for (final String detectionString : detectionStrings) {
                    addToDetectionStringMap(detectionString, licenseSet, getLog());
                }
                setInLicenseSet(false);
            }

            private void setInLicenseSet(final boolean inLicenseSet) {
                setElementHandler(inLicenseSet ? licenseSetLicenseHandler : standardLicenseHandler);
            }

            /**
             * @param detectionString
             * @param licenses
             * @param log the logger
             */
            private void addToDetectionStringMap(final String detectionString, final List<License> licenses,
                                                 final ILFLog log) {
                if (!StringUtils.isEmpty(detectionString)) {
                    if (detectionStringMap.containsKey(detectionString)) {
                        log.warn("duplicated detection string: '" + detectionString + "'");
                    }
                    detectionStringMap.put(detectionString, licenses);
                }
            }

        }

        private class SpdxIdentifierElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_SPDX_IDENTIFIER;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                spdxIdentifier = text;
            }
        }

        private class NameElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_NAME;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                name = text;
            }
        }

        private class LegalStatusElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_LEGAL_STATUS;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                legalStatus = LegalStatus.valueOf(LegalStatus.class, text);
            }
        }

        private class AuthorElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_AUTHOR;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                author = text;
            }
        }

        private class VersionElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_VERSION;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                version = text;
            }
        }

        private class PublicUrlElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_PUBLIC_URL;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                publicUrl = text;
            }
        }

        private class SecondaryUrlElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_SECONDARY_URL;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                secondaryUrls.add(text);
            }
        }

        private class AlternativeVersionElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_ALTERNATIVE_VERSION;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                alternativeVersions.add(text);
            }
        }

        private class DetectionStringElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_DETECTION_STRING;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                detectionStrings.add(text);
            }
        }

        private class NoticeElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_NOTICE;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                final String identifier = text.trim();
                notice = getNotices().getNoticeByIdentifier(identifier);
                if (notice == null) {
                    getLog().warn("Cannot find notice with ID: '" + identifier + "'");
                }
            }
        }

        private class LicenseTextElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_LICENSE_TEXT;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                licenseText = text;
            }
        }
    }

}