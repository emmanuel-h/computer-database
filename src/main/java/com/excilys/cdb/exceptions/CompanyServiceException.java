package com.excilys.cdb.exceptions;

public class CompanyServiceException extends Exception {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 4427932500845674426L;

    /**
     * Default constructor which take a print message.
     * @param message The exception's message
     */
    public CompanyServiceException(String message) {
        super(message);
    }

}
