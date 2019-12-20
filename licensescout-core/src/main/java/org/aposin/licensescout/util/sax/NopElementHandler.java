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

import org.xml.sax.Attributes;

/**
 * Element handler that does nothing.
 * 
 */
public class NopElementHandler implements IElementHandler {

    private final String elementName;

    /**
     * Constructor without element name.
     */
    public NopElementHandler() {
        this(null);
    }

    /**
     * Constructor with element name.
     * 
     * @param elementName
     */
    public NopElementHandler(final String elementName) {
        this.elementName = elementName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getElementName() {
        return elementName;
    }

    /** {@inheritDoc} */
    @Override
    public void startElement(String uri, String localName, String qName, final Attributes attributes) {
        // DO NOTHING
    }

    /** {@inheritDoc} */
    @Override
    public void endElement(String uri, String localName, String qName) {
        // DO NOTHING
    }
}