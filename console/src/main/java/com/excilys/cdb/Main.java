package com.excilys.cdb;

import com.excilys.cdb.controller.CLIController;
import com.excilys.cdb.ui.CLI;

public class Main {

    /**
     * Start of the program.
     * @param args  The arguments of the program
     */
    public static void main(String[] args) {
    	CLI ui = new CLI();
        CLIController controller = new CLIController(ui);
        controller.run();
    }
}
