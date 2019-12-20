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
 * Author information (used for NPM packages).
 * 
 */
public class Author {

    private final String name;
    private final String email;

    /**
     * Constructor.
     * 
     * @param name
     * @param email
     */
    public Author(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the email
     */
    public final String getEmail() {
        return email;
    }

}
