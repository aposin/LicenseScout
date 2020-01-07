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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper around the java.util logger.
 * 
 */
public class JavaUtilLog implements ILFLog {

    private final Logger log;

    /**
     * Constructor.
     * @param log a java.util logger
     */
    public JavaUtilLog(final Logger log) {
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled() {
        return log.isLoggable(Level.FINE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(CharSequence content) {
        log.fine(content.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(CharSequence content, Throwable error) {
        log.fine(content + ": " + error.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(Throwable error) {
        log.fine("" + error.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled() {
        return log.isLoggable(Level.INFO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(CharSequence content) {
        log.info(content.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(CharSequence content, Throwable error) {
        log.info(content + ": " + error.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(Throwable error) {
        log.info(error.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled() {
        return log.isLoggable(Level.WARNING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(CharSequence content) {
        log.log(Level.WARNING, content.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(CharSequence content, Throwable error) {
        log.log(Level.WARNING, content + ": " + error.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(Throwable error) {
        log.log(Level.WARNING, error.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled() {
        return log.isLoggable(Level.SEVERE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(CharSequence content) {
        log.severe(content.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(CharSequence content, Throwable error) {
        log.severe(content + ": " + error.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Throwable error) {
        log.severe(error.getMessage());
    }

}
