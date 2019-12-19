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
package org.aposin.licensescout.configuration;

/**
 * Parameters passed to the core of the LicenseScout from the parameter handling
 * layer.
 */
public class RunParameters {

    private String nexusCentralBaseUrl;
    private int connectTimeout;

    /**
     * @return the nexusCentralBaseUrl
     */
    public final String getNexusCentralBaseUrl() {
        return nexusCentralBaseUrl;
    }

    /**
     * @param nexusCentralBaseUrl the nexusCentralBaseUrl to set
     */
    public final void setNexusCentralBaseUrl(final String nexusCentralBaseUrl) {
        this.nexusCentralBaseUrl = nexusCentralBaseUrl;
    }

    /**
     * @return the connectTimeout
     */
    public final int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @param connectTimeout the connectTimeout to set
     */
    public final void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

}
