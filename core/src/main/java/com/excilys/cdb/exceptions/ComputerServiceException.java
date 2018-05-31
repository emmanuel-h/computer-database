package com.excilys.cdb.exceptions;

public class ComputerServiceException extends Exception {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 7916519344044019219L;

    /**
     * Default constructor which take a print message.
     * @param message The exception's message
     */
    public ComputerServiceException(String message) {
        super(message);
    }

}
