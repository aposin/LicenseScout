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
 * Provider is a supplier of a piece of software.
 * 
 */
public class Provider implements Comparable<Provider> {

    private final String identifier;
    private final String name;
    private final String url;

    /**
     * Constructor.
     * 
     * @param identifier
     * @param name
     * @param url 
     */
    public Provider(final String identifier, final String name, final String url) {
        this.identifier = identifier;
        this.name = name;
        this.url = url;
    }

    /**
     * @return the identifier
     */
    public final String getIdentifier() {
        return identifier;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the url
     */
    public final String getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Provider other) {
        return this.getIdentifier().compareTo(other.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Provider) {
            return compareTo((Provider) obj) == 0;
        }
        return false;
    }
}
