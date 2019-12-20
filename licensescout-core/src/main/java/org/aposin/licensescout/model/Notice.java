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

/**
 * A license notice that needs to be published/shown.
 * 
 */
public class Notice implements Comparable<Notice> {

    private String identifier;
    private String text;

    /**
     * Constructor.
     * 
     * @param identifier
     * @param text
     */
    public Notice(final String identifier, final String text) {
        this.identifier = identifier;
        this.text = text;
    }

    /**
     * @return the identifier
     */
    public final String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the name
     */
    public final String getText() {
        return text;
    }

    /**
     * @param name the name to set
     */
    public final void setText(String name) {
        this.text = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Notice other) {
        return this.getIdentifier().compareTo(other.getIdentifier());
    }

}
