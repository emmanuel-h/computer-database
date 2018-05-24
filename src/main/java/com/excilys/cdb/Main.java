package com.excilys.cdb;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.excilys.cdb.configs.SpringConfigCLI;
import com.excilys.cdb.controllers.CLIController;
import com.excilys.cdb.ui.CLI;

public class Main {

    /**
     * Start of the program.
     * @param args  The arguments of the program
     */
    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(SpringConfigCLI.class);
        CLI ui = new CLI();
        CLIController controller = new CLIController(ui, context);
        controller.run();
    }
}