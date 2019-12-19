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
package org.aposin.licensescout.maven.utils;

import org.apache.maven.plugin.logging.Log;
import org.aposin.licensescout.util.ILFLog;

/**
 * Wrapper around the Maven plugin logger.
 * 
 */
public class MavenLog implements ILFLog {

    private final Log log;

    /**
     * @param log
     */
    public MavenLog(final Log log) {
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(CharSequence content) {
        log.debug(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(CharSequence content, Throwable error) {
        log.debug(content, error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(Throwable error) {
        log.debug(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(CharSequence content) {
        log.info(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(CharSequence content, Throwable error) {
        log.info(content, error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(Throwable error) {
        log.info(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(CharSequence content) {
        log.warn(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(CharSequence content, Throwable error) {
        log.warn(content, error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(Throwable error) {
        log.warn(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(CharSequence content) {
        log.error(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(CharSequence content, Throwable error) {
        log.error(content, error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Throwable error) {
        log.error(error);
    }

}
