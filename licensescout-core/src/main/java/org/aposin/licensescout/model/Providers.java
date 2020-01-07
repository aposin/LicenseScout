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

import java.io.IOException;
import java.io.InputStream;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Collection of providers.

 * <ol>
 * <li>Create an instance</li>
 * <li>Call {@link #readProviders(InputStream, boolean, ILFLog)} to read in the providers from a file</li>
 * </ol>
 */
public class Providers {

    /**
     * Maps provider identifiers to providers
     */
    private Map<String, Provider> store = new HashMap<>();

    /**
     * Constructor.
     */
    public Providers() {
        // DO NOTHING
    }

    private void addToStore(final Provider provider, final ILFLog log) { // NOSONAR
        final String identifier = provider.getIdentifier();
        if (!StringUtils.isEmpty(identifier)) {
            final Provider existingProvider = store.get(identifier);
            if (existingProvider != null && !existingProvider.equals(provider)) {
                log.warn("double provider identifier: '" + identifier + "'");
            }
            store.put(identifier, provider);
        }
    }

    /**
     * Gets the provider from the provider store by its identifier.
     * 
     * @param identifier the provider identifier
     * @return the license or null
     */
    public Provider getProviderByIdentifier(final String identifier) {
        return store.get(identifier);
    }

    /**
     * Reads providers from an XML file.
     * 
     * @param inputStream an input stream to read the file contents from
     * @param validateXml true if the XML content read should be validated, false otherwise
     * @param log the logger
     * @throws IOException
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public void readProviders(final InputStream inputStream, boolean validateXml, final ILFLog log)
            throws IOException, ParserConfigurationException, SAXException {

        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(validateXml);
        final SAXParser saxParser = spf.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new ProviderSaxHandler(log));
        xmlReader.parse(new InputSource(inputStream));
    }

    private class ProviderSaxHandler extends AbstractSaxHandler {

        private static final String ELEMENT_PROVIDERS = "providers";
        private static final String ELEMENT_PROVIDER = "provider";
        private static final String ELEMENT_NAME = "name";
        private static final String ELEMENT_URL = "url";

        private String providerIdentifier;
        private String name;
        private String url;

        /**
         * Constructor.
         * @param log the logger
         */
        public ProviderSaxHandler(final ILFLog log) {
            super(log);
            setElementHandler(new NopElementHandler(ELEMENT_PROVIDERS));
            setElementHandler(new StandardProviderHandler());
            setElementHandler(new NameElementHandler());
            setElementHandler(new UrlElementHandler());
        }
        
        private class StandardProviderHandler implements IElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_PROVIDER;
            }

            /** {@inheritDoc} */
            @Override
            public void startElement(String uri, String localName, String qName, final Attributes attributes) {
                providerIdentifier = attributes.getValue(ATTRIBUTE_ID);
                name = null;
                url = null;
            }

            /** {@inheritDoc} */
            @Override
            public void endElement(String uri, String localName, String qName) {
                if (name == null) {
                    getLog().warn("No name for provider ID: '" + providerIdentifier + "'");
                }
                if (url == null) {
                    getLog().warn("No URL for provider ID: '" + providerIdentifier + "'");
                }
                final Provider provider = new Provider(providerIdentifier, name, url);
                addToStore(provider, getLog());
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

        private class UrlElementHandler extends AbstractTextElementHandler {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getElementName() {
                return ELEMENT_URL;
            }

            /** {@inheritDoc} */
            @Override
            protected void processText(final String text) {
                url = text;
            }
        }
    }

}
