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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for implementations of {@link ILFLog}.
 * 
 */
public abstract class AbstractLogTest {

    private static final String MESSAGE = "message";
    private static final Throwable THROWABLE = new IllegalArgumentException();

    private ILFLog log;

    /**
     * @return the log
     */
    protected final ILFLog getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    protected final void setLog(ILFLog log) {
        this.log = log;
    }

    protected abstract boolean getExpectedEnabled();

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#isDebugEnabled()}.
     */
    @Test
    public void testIsDebugEnabled() {
        Assert.assertEquals("isDebugEnabled()", getExpectedEnabled(), log.isDebugEnabled());
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#debug(java.lang.CharSequence)}.
     */
    @Test
    public void testDebugCharSequence() {
        log.debug(MESSAGE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#debug(java.lang.CharSequence, java.lang.Throwable)}.
     */
    @Test
    public void testDebugCharSequenceThrowable() {
        log.debug(MESSAGE, THROWABLE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#debug(java.lang.Throwable)}.
     */
    @Test
    public void testDebugThrowable() {
        log.debug(THROWABLE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#isInfoEnabled()}.
     */
    @Test
    public void testIsInfoEnabled() {
        Assert.assertEquals("isInfoEnabled()", getExpectedEnabled(), log.isInfoEnabled());
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#info(java.lang.CharSequence)}.
     */
    @Test
    public void testInfoCharSequence() {
        log.info(MESSAGE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#info(java.lang.CharSequence, java.lang.Throwable)}.
     */
    @Test
    public void testInfoCharSequenceThrowable() {
        log.info(MESSAGE, THROWABLE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#info(java.lang.Throwable)}.
     */
    @Test
    public void testInfoThrowable() {
        log.info(THROWABLE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#isWarnEnabled()}.
     */
    @Test
    public void testIsWarnEnabled() {
        Assert.assertEquals("isWarnEnabled()", getExpectedEnabled(), log.isWarnEnabled());
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#warn(java.lang.CharSequence)}.
     */
    @Test
    public void testWarnCharSequence() {
        log.warn(MESSAGE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#warn(java.lang.CharSequence, java.lang.Throwable)}.
     */
    @Test
    public void testWarnCharSequenceThrowable() {
        log.warn(MESSAGE, THROWABLE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#warn(java.lang.Throwable)}.
     */
    @Test
    public void testWarnThrowable() {
        log.warn(THROWABLE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#isErrorEnabled()}.
     */
    @Test
    public void testIsErrorEnabled() {
        Assert.assertEquals("isErrorEnabled()", getExpectedEnabled(), log.isErrorEnabled());
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#error(java.lang.CharSequence)}.
     */
    @Test
    public void testErrorCharSequence() {
        log.error(MESSAGE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#error(java.lang.CharSequence, java.lang.Throwable)}.
     */
    @Test
    public void testErrorCharSequenceThrowable() {
        log.error(MESSAGE, THROWABLE);
        Assert.assertNotNull(log);
    }

    /**
     * Test method for {@link org.aposin.licensescout.util.NullLog#error(java.lang.Throwable)}.
     */
    @Test
    public void testErrorThrowable() {
        log.error(THROWABLE);
        Assert.assertNotNull(log);
    }

}
