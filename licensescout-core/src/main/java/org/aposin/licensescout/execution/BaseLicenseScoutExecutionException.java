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
package org.aposin.licensescout.execution;

/**
 * Base class for checked exception during the main execution of the LicenseScout.
 * 
 * @see Executor
 */
public class BaseLicenseScoutExecutionException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7177068769438055391L;

    /**
     * Constructor.
     * @param message
     */
    public BaseLicenseScoutExecutionException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause
     */
    public BaseLicenseScoutExecutionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     */
    public BaseLicenseScoutExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
