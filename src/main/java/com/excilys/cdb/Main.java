package com.excilys.cdb;

import com.excilys.cdb.controller.CLIController;
import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.services.GeneralService;
import com.excilys.cdb.ui.CLI;

public class Main {

    private static CLI ui;
    private static GeneralService service;

    /**
     * Start of the program.
     * @param args  The arguments of the program
     */
    public static void main(String[] args) {
        try {
            // Initialize the static variables
            ui = new CLI();
            service = GeneralService.getInstance();
            CLIController controller = new CLIController(ui, service);
            controller.run();
        } catch (GeneralServiceException e) {

        }
    }
}