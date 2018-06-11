package com.excilys.cdb.exceptions.company;

public class CompanyException extends Exception {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 4427932500845674426L;

    /**
     * Default constructor which take a print message.
     * @param message The exception's message
     */
    public CompanyException(String message) {
        super(message);
    }

}
