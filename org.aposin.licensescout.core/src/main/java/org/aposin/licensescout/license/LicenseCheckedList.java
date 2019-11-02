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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveIdentifier;
import org.aposin.licensescout.archive.ArchiveIdentifierMessageDigest;
import org.aposin.licensescout.archive.ArchiveIdentifierPattern;
import org.aposin.licensescout.archive.ArchiveIdentifierVersion;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.archive.NameMatchingType;
import org.aposin.licensescout.archive.PatternType;
import org.aposin.licensescout.model.Notice;
import org.aposin.licensescout.model.Notices;
import org.aposin.licensescout.model.Provider;
import org.aposin.licensescout.model.Providers;
import org.aposin.licensescout.util.CryptUtil;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.sax.AbstractSaxHandler;
import org.aposin.licensescout.util.sax.IElementHandler;
import org.aposin.licensescout.util.sax.NopElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Contains a map of archives with manually assigned licenses.
 * 
 * <p>This map is used if a license cannot be detected automatically. The archive is identified either by name and version or by name and message digest</p>
 * 
 * <p>Note that this class contains a hard-coded constant for the length of the message digest values. This should correspond to the algorithm returned by {@link CryptUtil#getMessageDigestAlgorithm()}.
 * But while the algorithm can be changed, but this length of the message digests is currently fixed.</p>
 * 
 * @see LicenseUtil#evaluateLicenses(LicenseCheckedList, Archive, LicenseStoreData)
 * @see CryptUtil#getMessageDigestAlgorithm()
 */
public class LicenseCheckedList {

    private static final int TYPE_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int VERSION_OR_MD_INDEX = 2;
    private static final int DOC_URL_INDEX = 3;
    private static final int PROVIDER_INDEX = 4;
    private static final int NOTICE_INDEX = 5;
    private static final int LICENSE_NAMES_OFFSET = 6;

    /**
     * Note: this value is valid for SHA-256, should corresponds to the algorithm returned by {@link CryptUtil#getMessageDigestAlgorithm()}.
     * (Note that the algorithm can be changed by the method {@link CryptUtil#setMessageDigestAlgorithm(String)}, but this length of the hash is always hard-coded.)
     */
    private static final int MESSAGE_DIGEST_NUM_BYTES = 32;
    private static final int MESSAGE_DIGEST_NUM_CODED_CHARACTERS = MESSAGE_DIGEST_NUM_BYTES * 2;

    private Map<ArchiveIdentifier, LicenseResult> manualArchives = new HashMap<>();
    private Map<ArchiveIdentifierPattern, LicenseResult> manualPatternArchives = new HashMap<>();

    /**
     * Constructor.
     */
    public LicenseCheckedList() {
        // DO NOTHING
    }

    /**
     * Reads a CSV file containing checked archives.
     * 
     * @param filename a filename of the file to read
     * @param licenseStoreData 
     * @param providers 
     * @param notices 
     * @param log the logger
     * @throws IOException if an error occurred while reading from the file
     */
    public void readCsv(final File filename, final LicenseStoreData licenseStoreData, final Providers providers,
                        final Notices notices, final ILFLog log)
            throws IOException {
        String line = "";
        String cvsSplitBy = ",";
        int lineNumber = 0;
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {

            while ((line = br.readLine()) != null) {
                lineNumber++;
                // ignore lines commented out, empty lines and the header line
                if (line.startsWith("#") || StringUtils.isEmpty(line) || line.startsWith("Type")) {
                    continue;
                }
                ArchiveIdentifier archiveIdentifier = null;
                // use comma as separator
                final String[] values = line.split(cvsSplitBy);
                final ArchiveType archiveType = getArchiveType(values[TYPE_INDEX].trim());
                final String name = values[NAME_INDEX].trim();
                // either version or hash
                final String id = values[VERSION_OR_MD_INDEX].trim();
                if (name.length() >= 0 && name.charAt(0) == '=') {
                    PatternType patternType;
                    int patternStartIndex;
                    if (name.length() >= 1 && name.charAt(1) == '=') {
                        patternType = PatternType.PATTERN_ON_PATH;
                        patternStartIndex = 2;
                    } else {
                        patternType = PatternType.PATTERN_ON_FILENAME;
                        patternStartIndex = 1;
                    }
                    final String regex = name.substring(patternStartIndex);
                    archiveIdentifier = new ArchiveIdentifierPattern(archiveType, patternType, regex);
                } else if (id.length() >= 0) {
                    if (id.length() == MESSAGE_DIGEST_NUM_CODED_CHARACTERS) {
                        archiveIdentifier = new ArchiveIdentifierMessageDigest(archiveType, name, id);
                    } else {
                        archiveIdentifier = new ArchiveIdentifierVersion(archiveType, name, id);
                    }
                } else {
                    log.error("read line with error, no version or message digest");
                    archiveIdentifier = null;
                }
                final String documentationUrl = values[DOC_URL_INDEX].trim();
                final String providerId = values[PROVIDER_INDEX].trim();
                final int lineNumberFinal = lineNumber;
                final Provider provider = getProviderFromId(providers, providerId, log, ()-> filename.getName() + ": line " + lineNumberFinal);
                final String noticeId = values[NOTICE_INDEX].trim();
                final Notice notice = getNoticeFromId(notices, noticeId);

                if (archiveIdentifier != null) {
                    final LicenseResult licenseResult = fetchLicenseResult(licenseStoreData, log, values,
                            documentationUrl, notice, provider);
                    if (archiveIdentifier.getNameMatchingType() == NameMatchingType.EXACT) {
                        manualArchives.put(archiveIdentifier, licenseResult);
                    } else {
                        manualPatternArchives.put((ArchiveIdentifierPattern) archiveIdentifier, licenseResult);
                    }
                }
            }
        }
    }

    /**
     * Reads an XML file containing checked archives.
     * 
     * Note: currently not used.
     * 
     * @param file a filename of the file to read
     * @param licenseStoreData 
     * @param notices 
     * @param providers 
     * @param validateXml 
     * @param log the logger
     * @throws IOException if an error occurred while reading from the file
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    public void readXml(final File file, final LicenseStoreData licenseStoreData, final Notices notices,
                        final Providers providers, boolean validateXml, final ILFLog log)
            throws IOException, ParserConfigurationException, SAXException {

        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(validateXml);
        final SAXParser saxParser = spf.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new ArchiveSaxHandler(licenseStoreData, notices, providers, log));
        xmlReader.parse(file.toURI().toString());
    }

    /**
     * @param licenseStoreData
     * @param log
     * @param values
     * @param documentationUrl 
     * @return a result object
     */
    private LicenseResult fetchLicenseResult(final LicenseStoreData licenseStoreData, final ILFLog log,
                                             final String[] values, final String documentationUrl, final Notice notice,
                                             final Provider provider) {
        final int numLicenseNames = values.length - LICENSE_NAMES_OFFSET;
        final List<License> licenses = new ArrayList<>();
        if (numLicenseNames == 0) {
            licenses.add(licenseStoreData.getLicenseBySpdxIdentifier(LicenseSpdxIdentifier.NO_MANUAL_INFORMATION));
        } else {
            for (int i = 0; i < numLicenseNames; i++) {
                final String licenseName = values[i + LICENSE_NAMES_OFFSET].trim();
                final License license = licenseStoreData.getLicenseBySpdxIdentifier(licenseName);
                if (license == null) {
                    log.error("License not found in store: '" + licenseName + "'");
                } else {
                    licenses.add(license);
                }
            }
        }
        return new LicenseResult(licenses, documentationUrl, notice, provider);
    }

    private LicenseResult fetchLicenseResult(final List<License> licenses, final String documentationUrl,
                                             final Notice notice, final Provider provider) {
        return new LicenseResult(licenses, documentationUrl, notice, provider);
    }

    private static ArchiveType getArchiveType(final String name) {
        return ArchiveType.valueOf(name);
    }

    /**
     * Obtains manually set licenses for an archive given by name and version.
     * 
     * @param archiveType
     * @param archiveName
     * @param version
     * @return a list of licenses, maybe empty, but not null
     */
    public LicenseResult getManualLicense(final ArchiveType archiveType, final String archiveName,
                                          final String version) {
        final ArchiveIdentifier archiveIdentifier = new ArchiveIdentifierVersion(archiveType, archiveName, version);
        return getManualArchiveLicenses(archiveIdentifier);
    }

    /**
     * Obtains manually set licenses for an archive given by name and message digest.
     * 
     * @param archiveType
     * @param archiveName
     * @param messageDigest
     * @return a list of licenses, maybe empty
     */
    public LicenseResult getManualLicense(final ArchiveType archiveType, final String archiveName,
                                          final byte[] messageDigest) {
        final ArchiveIdentifier archiveIdentifier = new ArchiveIdentifierMessageDigest(archiveType, archiveName,
                messageDigest);
        return getManualArchiveLicenses(archiveIdentifier);
    }

    /**
     * @param archiveIdentifier
     * @return a list of licenses, maybe empty, but not null
     */
    private LicenseResult getManualArchiveLicenses(final ArchiveIdentifier archiveIdentifier) {
        return manualArchives.get(archiveIdentifier);
    }

    /**
     * Obtains a set of archive identifiers with patterns with associated list of licenses.
     * 
     * <p>These can be used to match archives against archive identifiers that contain a regular expression for the archive name or archive path.</p>
     * 
     * @return a set of archive identifier patterns
     */
    public Set<Entry<ArchiveIdentifierPattern, LicenseResult>> getManualPatternArchives() {
        return manualPatternArchives.entrySet();
    }

    /**
     * @param outputFile
     * @param archives
     * @throws IOException
     */
    public static void writeCsvSkeletonFile(final File outputFile, final List<Archive> archives) throws IOException {
        final String string = writeCsvSkeletonFile(archives);

        try (final FileWriter fileWriter = new FileWriter(outputFile);
                final BufferedWriter bw = new BufferedWriter(fileWriter);) {
            bw.write(string);
        }
    }

    /**
     * @return the collected CSV contents
     */
    private static String writeCsvSkeletonFile(final List<Archive> archives) {
        final StringBuilder sb = new StringBuilder();
        final String nl = "\n";
        // header line
        sb.append(
                "Type,Filename or Regex,Version or Message digest,Documentation URL, Provider,Notice,License 1,License 2,License 3")
                .append(nl);
        // body
        for (final Archive archive : archives) {
            sb.append(archive.getArchiveType().name()).append(",");
            sb.append(archive.getFileName()).append(",");
            if (!StringUtils.isEmpty(archive.getVersion())) {
                sb.append(archive.getVersion());
            } else if (archive.getMessageDigest() != null) {
                sb.append(archive.getMessageDigestString());
            } else {
                sb.append("TODO");
            }
            sb.append(",");
            sb.append("TODO_docURL,"); // documentation URL
            sb.append("TODO_provider,"); // provider
            sb.append("TODO_notice,"); // notice
            sb.append("TODO_license1");
            sb.append(nl);
        }
        return sb.toString();
    }

    /**
     * @param outputFile
     * @param archives
     * @throws IOException
     */
    public static void writeXmlSkeletonFile(final File outputFile, final List<Archive> archives) throws IOException {
        final String string = writeXmlSkeletonFile(archives);

        try (final FileWriter fileWriter = new FileWriter(outputFile);
                final BufferedWriter bw = new BufferedWriter(fileWriter);) {
            bw.write(string);
        }
    }

    /**
     * @return the collected XML string
     */
    private static String writeXmlSkeletonFile(final List<Archive> archives) {
        final StringBuilder sb = new StringBuilder();
        final String nl = "\n";
        beginLine(sb, 0).append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>").append(nl);
        beginLine(sb, 0).append("<archives>").append(nl);
        for (final Archive archive : archives) {
            beginLine(sb, 1).append("<archive>").append(nl);
            beginLine(sb, 2).append("<archiveType>").append(archive.getArchiveType().name()).append("</archiveType>")
                    .append(nl);
            beginLine(sb, 2).append("<name>").append(archive.getFileName()).append("</name>").append(nl);
            if (!StringUtils.isEmpty(archive.getVersion())) {
                beginLine(sb, 2).append("<version>").append(archive.getVersion()).append("</version>").append(nl);
            } else if (archive.getMessageDigest() != null) {
                beginLine(sb, 2).append("<messageDigest algorithm='").append(CryptUtil.getMessageDigestAlgorithm())
                        .append("'>").append(archive.getMessageDigestString()).append("</messageDigest>").append(nl);
            }
            beginLine(sb, 2).append("<documentationUrl>").append("TODO").append("</documentationUrl>").append(nl);
            beginLine(sb, 2).append("<provider>").append("TODO").append("</provider>").append(nl);
            beginLine(sb, 2).append("<notice>").append("TODO").append("</notice>").append(nl);
            beginLine(sb, 2).append("<licenses>").append(nl);
            beginLine(sb, 3).append("<license>").append("TODO").append("</license>").append(nl);
            beginLine(sb, 2).append("</licenses>").append(nl);
            beginLine(sb, 1).append("</archive>").append(nl);
        }
        beginLine(sb, 0).append("</archives>").append(nl);
        return sb.toString();
    }

    private static StringBuilder beginLine(final StringBuilder sb, final int indentationLeve) {
        final String indentationString = StringUtils.repeat("\t", indentationLeve);
        sb.append(indentationString);
        return sb;
    }

    private static Notice getNoticeFromId(final Notices notices, final String noticeId) {
        // NOTE: notice in license list is optional. So no error or warning here.
        return notices.getNoticeByIdentifier(noticeId);
    }

    private static Provider getProviderFromId(final Providers providers, final String providerId, final ILFLog log, final Supplier<String> context) {
        final Provider provider = providers.getProviderByIdentifier(providerId);
        if (provider == null) {
            String message = "Cannot find provider with ID: '" + providerId + "'";
            if (context != null && !StringUtils.isEmpty( context.get())) {
                message += " ("+ context.get() + ")";
            }
            log.warn(message);
        }
        return provider;
    }

	private class ArchiveSaxHandler extends AbstractSaxHandler {
		
	    private static final String ELEMENT_ARCHIVES = "archives";
	    private static final String ELEMENT_ARCHIVE = "archive";
	    private static final String ELEMENT_ARCHIVE_TYPE = "archiveType";
	    private static final String ELEMENT_NAME = "name";
	    private static final String ELEMENT_VERSION = "version";
	    private static final String ELEMENT_MESSAGE_DIGEST = "messageDigest";
	    private static final String ELEMENT_DOCUMENTATION_URL = "documentationUrl";
	    private static final String ELEMENT_PROVIDER = "provider";
	    private static final String ELEMENT_NOTICE = "notice";
	    private static final String ELEMENT_LICENSES = "licenses";
	    private static final String ELEMENT_LICENSE = "license";
	
	    private final LicenseStoreData licenseStoreData;
	    private final Notices notices;
	    private final Providers providers;
	
	    private String name;
	    private String version;
	    private PatternType patternType;
	    private List<License> licenses;
	    private ArchiveType archiveType;
	    private String messageDigest;
	    private String documentationUrl;
	    private Notice notice;
	    private Provider provider;
	
	    /**
	     * Constructor.
	     * @param licenseStoreData 
	     * @param notices 
	     * @param providers 
	     * @param log the logger
	     */
	    public ArchiveSaxHandler(final LicenseStoreData licenseStoreData, final Notices notices,
	            final Providers providers, final ILFLog log) {
	        super(log);
	        this.licenseStoreData = licenseStoreData;
	        this.notices = notices;
	        this.providers = providers;
	        setElementHandler(new NopElementHandler(ELEMENT_ARCHIVES));
	        setElementHandler(new StandardArchiveHandler());
	        setElementHandler(new ArchiveTypeElementHandler());
	        setElementHandler(new NameElementHandler());
	        setElementHandler(new VersionElementHandler());
	        setElementHandler(new MessageDigestElementHandler());
	        setElementHandler(new DocumentationUrlElementHandler());
	        setElementHandler(new NoticeElementHandler());
	        setElementHandler(new ProviderElementHandler());
	        setElementHandler(new NopElementHandler(ELEMENT_LICENSES));
	        setElementHandler(new LicenseElementHandler());
	    }
	
	    /**
	     * @return the notices
	     */
	    protected final Notices getNotices() {
	        return notices;
	    }
	
	    /**
	     * @return the providers
	     */
	    protected final Providers getProviders() {
	        return providers;
	    }
	
	    private class StandardArchiveHandler implements IElementHandler {
	
	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public String getElementName() {
	            return ELEMENT_ARCHIVE;
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        public void startElement(String uri, String localName, String qName, final Attributes attributes) {
	            archiveType = null;
	            name = null;
	            patternType = null;
	            version = null;
	            messageDigest = null;
	            licenses = new ArrayList<>();
	            documentationUrl = null;
	            notice = null;
	            provider = null;
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        public void endElement(String uri, String localName, String qName) {
	            ArchiveIdentifier archiveIdentifier = null;
	            if (archiveType == null) {
	                getLog().warn("No archive type for archive! Line: " + getLineNumberInformation());
	            }
	            if (name == null) {
	                getLog().warn("No name for archive! Line: " + getLineNumberInformation());
	            }
	            if (patternType != null) {
	                archiveIdentifier = new ArchiveIdentifierPattern(archiveType, patternType, name);
	            } else {
	                if (version != null) {
	                    archiveIdentifier = new ArchiveIdentifierVersion(archiveType, name, version);
	                }
	                if (messageDigest != null) {
	                    archiveIdentifier = new ArchiveIdentifierMessageDigest(archiveType, name, messageDigest);
	                }
	            }
	
	            if (archiveIdentifier != null) {
	                final LicenseResult licenseResult = fetchLicenseResult(licenses, documentationUrl, notice,
	                        provider);
	                if (archiveIdentifier.getNameMatchingType() == NameMatchingType.EXACT) {
	                    manualArchives.put(archiveIdentifier, licenseResult);
	                } else {
	                    manualPatternArchives.put((ArchiveIdentifierPattern) archiveIdentifier, licenseResult);
	                }
	            }
	        }
	    }
	
	    private class ArchiveTypeElementHandler extends AbstractTextElementHandler {
	
	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public String getElementName() {
	            return ELEMENT_ARCHIVE_TYPE;
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        protected void processText(final String text) {
	            archiveType = Enum.valueOf(ArchiveType.class, text);
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
	
	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        protected void startElementHook(final String uri, final String localName, final String qName,
	                                        final Attributes attributes) {
	            final String typeText = attributes.getValue(ATTRIBUTE_TYPE);
	            if (typeText != null) {
	                switch (typeText) {
	                    case "exact":
	                        patternType = null;
	                        break;
	                    case "regexname":
	                        patternType = PatternType.PATTERN_ON_FILENAME;
	                        break;
	                    case "regexpath":
	                        patternType = PatternType.PATTERN_ON_FILENAME;
	                        break;
	                    default:
	                        getLog().warn("Unknown matching type: '" + typeText + "'");
	                        break;
	                }
	            }
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        protected void processText(final String text) {
	            name = text;
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
	
	    private class MessageDigestElementHandler extends AbstractTextElementHandler {
	
	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public String getElementName() {
	            return ELEMENT_MESSAGE_DIGEST;
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        protected void processText(final String text) {
	            messageDigest = text;
	        }
	    }
	
	    private class DocumentationUrlElementHandler extends AbstractTextElementHandler {
	
	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public String getElementName() {
	            return ELEMENT_DOCUMENTATION_URL;
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        protected void processText(final String text) {
	            documentationUrl = text;
	        }
	    }
	
	    private class LicenseElementHandler extends AbstractTextElementHandler {
	
	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public String getElementName() {
	            return ELEMENT_LICENSE;
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        protected void processText(final String text) {
	            final License license = licenseStoreData.getLicenseBySpdxIdentifier(text);
	            if (license == null) {
	                getLog().warn("Cannot find license with SPDX ID '" + text + "'");
	            } else {
	                licenses.add(license);
	            }
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
	            final String identifier = text;
	            if (!StringUtils.isEmpty(identifier)) {
	                notice = getNoticeFromId(getNotices(), identifier);
	            }
	        }
	    }
	
	    private class ProviderElementHandler extends AbstractTextElementHandler {
	
	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public String getElementName() {
	            return ELEMENT_PROVIDER;
	        }
	
	        /** {@inheritDoc} */
	        @Override
	        protected void processText(final String text) {
	            final String identifier = text;
	            provider = getProviderFromId(getProviders(), identifier, getLog(), ()-> ArchiveSaxHandler.this.getLocationString());
	        }
	    }
	}
}
