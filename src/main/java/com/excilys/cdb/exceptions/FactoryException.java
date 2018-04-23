package com.excilys.cdb.exceptions;

public class FactoryException extends Exception {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = -4849420900384387880L;

    /**
     * Default constructor which take a print message.
     * @param message   The exception's message
     */
    public FactoryException(String message) {
        super(message);
    }
}
