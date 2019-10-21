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
package org.aposin.licensescout.util.sax;

import java.util.HashMap;
import java.util.Map;

import org.aposin.licensescout.util.ILFLog;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class AbstractSaxHandler extends DefaultHandler {

    protected static final String ATTRIBUTE_ID = "id";
    protected static final String ATTRIBUTE_IDREF = "idref";
    protected static final String ATTRIBUTE_TYPE = "type";

    private final ILFLog log;
    private final Map<String, IElementHandler> elementHandlers = new HashMap<>();
    private StringBuilder characterCollector;
    private Locator locator;

    /**
     * Constructor.
     * @param log the logger
     */
    public AbstractSaxHandler(final ILFLog log) {
        this.log = log;
    }

    /**
     * @return the log
     */
    protected final ILFLog getLog() {
        return log;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName, final Attributes attributes)
            throws SAXException {
        final IElementHandler handler = elementHandlers.get(localName);
        if (handler != null) {
            handler.startElement(uri, localName, qName, attributes);
        } else {
            log.error("Unhandled element: '" + localName + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        final IElementHandler handler = elementHandlers.get(localName);
        if (handler != null) {
            handler.endElement(uri, localName, qName);
        } else {
            log.error("Unhandled element: '" + localName + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (characterCollector != null) {
            characterCollector.append(ch, start, length);
        }
    }

    protected final void setElementHandler(final IElementHandler elementHandler) {
        final String elementName = elementHandler.getElementName();
        if (elementName != null) {
            setElementHandler(elementName, elementHandler);
        } else {
            getLog().warn("Element name is null for element handler " + elementHandler.toString());
        }
    }

    protected final void setElementHandler(final String elementName, final IElementHandler elementHandler) {
        elementHandlers.put(elementName, elementHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    protected Locator getDocumentLocator() {
        return locator;
    }

    /**
     * Obtains the current line number from the locator, if present.
     * @return the current line number as a string if the locator is present or an empty string if no locator is present
     */
    protected String getLineNumberInformation() {
        if (getDocumentLocator() != null) {
            return Integer.toString(getDocumentLocator().getLineNumber());
        } else {
            return "";
        }
    }

	protected abstract class AbstractTextElementHandler implements IElementHandler {
	
	    /** {@inheritDoc} */
	    @Override
	    public final void startElement(final String uri, final String localName, final String qName,
	                                   final Attributes attributes) {
	        characterCollector = new StringBuilder();
	        startElementHook(uri, localName, qName, attributes);
	    }
	
	    protected void startElementHook(final String uri, final String localName, final String qName,
	                                    final Attributes attributes) {
	        // DO NOTHING
	    }
	
	    /** {@inheritDoc} */
	    @Override
	    public final void endElement(final String uri, final String localName, final String qName) {
	        final String tmp = getCollectedCharacters();
	        processText(tmp);
	    }
	
	    protected abstract void processText(String text);
	
	    private String getCollectedCharacters() {
	        final String tmp = characterCollector.toString();
	        characterCollector = null;
	        return tmp.trim();
	    }
	}
}