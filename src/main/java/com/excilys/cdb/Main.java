package com.excilys.cdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.controllers.CLIController;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.services.CompanyService;
import com.excilys.cdb.services.ComputerService;
import com.excilys.cdb.ui.CLI;

public class Main {

    /**
     * A logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Start of the program.
     * @param args  The arguments of the program
     */
    public static void main(String[] args) {
        try {
            // Initialize the static variables
            CLI ui = new CLI();
            CompanyService companyService = CompanyService.getInstance();
            ComputerService computerService = ComputerService.getInstance();
            CLIController controller = new CLIController(ui, companyService, computerService);
            controller.run();
        } catch (FactoryException e) {
            LOGGER.warn("Error when instanciating the services");
        }
    }
}