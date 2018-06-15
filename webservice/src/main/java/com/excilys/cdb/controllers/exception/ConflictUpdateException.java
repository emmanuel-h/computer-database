package com.excilys.cdb.controllers.exception;

public class ConflictUpdateException extends Exception {

    private static final long serialVersionUID = 8781907044894803387L;
    
    public ConflictUpdateException(final String message) {
        super(message);
    }

}
