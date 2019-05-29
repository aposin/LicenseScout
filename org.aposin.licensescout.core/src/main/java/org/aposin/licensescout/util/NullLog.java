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
package org.aposin.licensescout.util;

/**
 * Does not log anything - similar to /dev/null - use for test cases.
 * 
 */
public class NullLog implements ILFLog {

    /**
     * Constructor.
     */
    public NullLog() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(CharSequence content) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(CharSequence content, Throwable error) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(Throwable error) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(CharSequence content) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(CharSequence content, Throwable error) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(Throwable error) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(CharSequence content) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(CharSequence content, Throwable error) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(Throwable error) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(CharSequence content) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(CharSequence content, Throwable error) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Throwable error) {
        // EMPTY
    }

}
