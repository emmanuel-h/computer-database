package com.excilys.cdb.controller;

import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.GeneralService;
import com.excilys.cdb.ui.CLI;
import com.excilys.cdb.utils.Page;

/**
 * The controller make the discussion between the UI and the service layer.
 * @author emmanuelh
 */
public class CLIController {

    /**
     * The displaying UI.
     */
    private CLI ui;

    /**
     * The service connected to the model.
     */
    private GeneralService service;

    /**
     * ChoiceMenu is used for the switch case concerning the return value of the UI.
     * @author emmanuelh
     */
    private enum ChoiceMenu {
        LISTCOMPUTERS("1"), LISTCOMPANIES("2"), SHOWCOMPUTERDETAILS("3"), CREATECOMPUTER("4"), UPDATECOMPUTER(
                "5"), DELETECOMPUTER("6"), QUIT("7"), PAGECOMPANY("pc");
        private String choice;

        /**
         * Constructor with the choice desired.
         * @param choice   The choice wanted
         */
        ChoiceMenu(String choice) {
            this.choice = choice;
        }

        /**
         * Return the choice wanted.
         * @param choice    The choice wanted
         * @return          The ChoiceMenu corresponding to the String choice
         */
        public static ChoiceMenu get(String choice) {
            for (ChoiceMenu choiceMenu : ChoiceMenu.values()) {
                if (choiceMenu.choice.equals(choice)) {
                    return choiceMenu;
                }
            }
            return null;
        }
    };

    /**
     * Blank constructor.
     */
    public CLIController() {

    }

    /**
     * Constructor with an UI and a service.
     * @param ui       The UI
     * @param service  The service
     */
    public CLIController(CLI ui, GeneralService service) {
        this.ui = ui;
        this.service = service;
    }

    /**
     * The main method of the controller, making the discussion between UI and service.
     */
    public void run() {
        boolean stop = false;
        ChoiceMenu choice;
        Page<Computer> computers;
        Page<Company> companies;
        Computer computer;
        Computer computerToUpdate;
        int id;
        String choicePage;
        while (!stop) {
            try {
                choice = ChoiceMenu.get(ui.home());
                switch (choice) {
                case LISTCOMPUTERS:
                    computers = service.getAllComputers(1, 5);
                    choicePage = ui.displayComputers(computers);
                    while (choicePage.equals("p")) {
                        id = ui.askPage();
                        computers = service.getAllComputers(id, 5);
                        choicePage = ui.displayComputers(computers);
                    }
                    break;
                case LISTCOMPANIES:
                    companies = service.getAllCompanies(1, 5);
                    choicePage = ui.displayCompanies(companies);
                    while (choicePage.equals("p")) {
                        id = ui.askPage();
                        companies = service.getAllCompanies(id, 5);
                        choicePage = ui.displayCompanies(companies);
                    }
                    break;
                case SHOWCOMPUTERDETAILS:
                    id = ui.askComputerId();
                    computer = service.getOneComputer(id);
                    ui.showComputerDetails(computer);
                    break;
                case CREATECOMPUTER:
                    computer = ui.createComputer();
                    service.createComputer(computer);
                    break;
                case UPDATECOMPUTER:
                    id = ui.askComputerId();
                    computerToUpdate = service.getOneComputer(id);
                    computer = ui.updateComputer(computerToUpdate);
                    service.updateComputer(computer);
                    break;
                case DELETECOMPUTER:
                    id = ui.deleteComputer();
                    service.deleteComputer(id);
                    break;
                case PAGECOMPANY:
                case QUIT:
                    return;
                }
            } catch (GeneralServiceException e) {
                ui.displayException(e.getMessage());
            }
        }
    }

}
