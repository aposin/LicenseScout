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

import java.util.Arrays;

import org.aposin.licensescout.util.MiscUtil;

/**
 * Container for a message digest.
 * 
 */
public class LSMessageDigest {

    private final byte[] bytes;

    /**
     * Constructor.
     * @param bytes the content of the message digest
     */
    public LSMessageDigest(final byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Obtains the content of the message digest.
     * @return the bytes
     */
    public final byte[] getBytes() {
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytes);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LSMessageDigest other = (LSMessageDigest) obj;
        return Arrays.equals(bytes, other.bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MiscUtil.getHexString(this.getBytes());
    }

    public static final String toString(LSMessageDigest messageDigest) {
        if (messageDigest != null) {
            return messageDigest.toString();
        } else {
            return "null";
        }
    }
}
