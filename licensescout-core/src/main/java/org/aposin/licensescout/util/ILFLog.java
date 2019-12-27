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
 * Wrapper interface for logging.
 * 
 * <p>This is basically a wrapper around the Maven log facility, so that the same code can also be run as a stand-alone application.
 * The logging has four levels: DEBUG, INFO, WARNING and ERROR.</p>
 * <p>There are two implementations: <code>MavenLog</code> is based on the maven Log interface. {@link JavaUtilLog} is based on java.util logging.</p>
 * <p>There is also an implementation {@link NullLog} that is used for test cases to sink all logging.</p>
 * 
 * @see "MavenLog"
 * @see JavaUtilLog
 * @see NullLog
 */
//TODO: rename to "ILSLog"
public interface ILFLog {

    /**
     * Checks if logging is enabled with level DEBUG.
     * @return true if logging is enabled with level DEBUG, false otherwise
     */
    boolean isDebugEnabled();

    /**
     * Log a message with level DEBUG.
     * @param content a message
     */
    void debug(CharSequence content);

    /**
     * Log a message and a throwable with level DEBUG.
     * @param content a message
     * @param error a throwable
     */
    void debug(CharSequence content, Throwable error);

    /**
     * Log a throwable with level DEBUG.
     * @param error a throwable
     */
    void debug(Throwable error);

    /**
     * Checks if logging is enabled with level INFO.
     * @return true if logging is enabled with level INFO, false otherwise
     */
    boolean isInfoEnabled();

    /**
     * Log a message with level INFO.
     * @param content a message
     */
    void info(CharSequence content);

    /**
     * Log a message and a throwable with level INFO.
     * @param content a message
     * @param error a throwable
     */
    void info(CharSequence content, Throwable error);

    /**
     * Log a throwable with level INFO.
     * @param error a throwable
     */
    void info(Throwable error);

    /**
     * Checks if logging is enabled with level WARNING.
     * @return true if logging is enabled with level WARNING, false otherwise
     */
    boolean isWarnEnabled();

    /**
     * Log a message with level WARNING.
     * @param content a message
     */
    void warn(CharSequence content);

    /**
     * Log a message and a throwable with level WARNING.
     * @param content a message
     * @param error a throwable
     */
    void warn(CharSequence content, Throwable error);

    /**
     * Log a throwable with level WARNING.
     * @param error a throwable
     */
    void warn(Throwable error);

    /**
     * Checks if logging is enabled with level ERROR.
     * @return true if logging is enabled with level ERROR, false otherwise
     */
    boolean isErrorEnabled();

    /**
     * Log a message with level ERROR.
     * @param content a message
     */
    void error(CharSequence content);

    /**
     * Log a message and a throwable with level ERROR.
     * @param content a message
     * @param error a throwable
     */
    void error(CharSequence content, Throwable error);

    /**
     * Log a throwable with level ERROR.
     * @param error a throwable
     */
    void error(Throwable error);
}
