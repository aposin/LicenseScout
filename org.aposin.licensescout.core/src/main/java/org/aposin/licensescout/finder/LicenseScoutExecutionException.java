package org.aposin.licensescout.finder;

import org.aposin.licensescout.execution.Executor;

/**
 * Checked exception during the main execution of the LicenseScout.
 * 
 * @see Executor
 */
public class LicenseScoutExecutionException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -126240217097149062L;

    /**
     * Constructor.
     * @param message
     */
    public LicenseScoutExecutionException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause
     */
    public LicenseScoutExecutionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     */
    public LicenseScoutExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
