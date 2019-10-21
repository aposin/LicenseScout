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
package org.aposin.licensescout.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.util.ILFLog;
import org.aposin.licensescout.util.sax.AbstractSaxHandler;
import org.aposin.licensescout.util.sax.IElementHandler;
import org.aposin.licensescout.util.sax.NopElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Collection of notices.
 * 
 * <p>Initialisation:<p>
 * <ol>
 * <li>Create an instance</li>
 * <li>Call {@link #readNotices(File, boolean, ILFLog)} to read in the notices from a file</li>
 * </ol>
 */
public class Notices {

    /**
     * Maps notice identifiers to notices
     */
    private Map<String, Notice> store = new HashMap<>();

    /**
     * Constructor.
     */
    public Notices() {
        // DO NOTHING
    }

    private void addToStore(final Notice notice, final ILFLog log) {
        final String identifier = notice.getIdentifier();
        if (!StringUtils.isEmpty(identifier)) {
            final Notice existingNotice = store.get(identifier);
            if (existingNotice != null && !existingNotice.equals(notice)) {
                log.warn("double notice identifier: '" + identifier + "'");
            }
            store.put(identifier, notice);
        }
    }

    /**
     * Gets the notice by its identifier.
     * 
     * @param identifier the notice identifier
     * @return the notice or null
     */
    public Notice getNoticeByIdentifier(final String identifier) {
        return store.get(identifier);
    }

    /**
     * Reads known notices from an XML file.
     * 
     * @param file
     * @param validateXml true if the notices XML file should be validated, false otherwise
     * @param log the logger
     * @throws IOException
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public void readNotices(final File file, boolean validateXml, final ILFLog log)
            throws IOException, ParserConfigurationException, SAXException {

        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(validateXml);
        final SAXParser saxParser = spf.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new NoticeSaxHandler(log));
        xmlReader.parse(file.toURI().toString());
    }

    private class NoticeSaxHandler extends AbstractSaxHandler {
    	
        private static final String ELEMENT_NOTICES = "notices";
        private static final String ELEMENT_NOTICE = "notice";
        private static final String ELEMENT_TEXT = "text";

        private String identifier;
        private String text;


        /**
         * Constructor.
         * @param log the logger
         */
        public NoticeSaxHandler(final ILFLog log) {
            super(log);
            setElementHandler(new NopElementHandler(ELEMENT_NOTICES));
            setElementHandler(new StandardNoticeHandler());
            setElementHandler(new TextElementHandler());
        }
        
        private class StandardNoticeHandler implements IElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_NOTICE;
            }

            /** {@inheritDoc} */
            @Override
            public void startElement(String uri, String localName, String qName, final Attributes attributes) {
                identifier = attributes.getValue(ATTRIBUTE_ID);
                text = null;
            }

            /** {@inheritDoc} */
            @Override
            public void endElement(String uri, String localName, String qName) {
                if (text == null) {
                    getLog().warn("No text for notice ID: '" + identifier + "'");
                }
                final Notice notice = new Notice(identifier, text);
                addToStore(notice, getLog());
            }
        }

        private class TextElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_TEXT;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                NoticeSaxHandler.this.text = text;
            }
        }
    }

}
