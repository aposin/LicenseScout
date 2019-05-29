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

/**
 * Legal state of a license (is it an open source license that is OK for Allianz).
 * 
 */
public enum LegalStatus {

    /**
     * License is accepted by Allianz as an open source license that can be used.
     */
    ACCEPTED,
    /**
     * License is not accepted by Allianz as an open source license that can be used.
     */
    NOT_ACCEPTED,
    /**
     * License that are accepted and not accepted in the same archive.
     */
    CONFLICTING,
    /**
     * Legal status is unknown.
     */
    UNKNOWN;
}
