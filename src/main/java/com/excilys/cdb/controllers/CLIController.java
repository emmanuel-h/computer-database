package com.excilys.cdb.controllers;

import java.util.Optional;

import com.excilys.cdb.exceptions.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.CompanyService;
import com.excilys.cdb.services.ComputerService;
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
     * The computer service connected to the model.
     */
    private ComputerService computerService;

    /**
     * The company service connected to the model.
     */
    private CompanyService companyService;

    /**
     * ChoiceMenu is used for the switch case concerning the return value of the UI.
     * @author emmanuelh
     */
    private enum ChoiceMenu {
        DELETECOMPANY("0"), LISTCOMPUTERS("1"), LISTCOMPANIES("2"), SHOWCOMPUTERDETAILS("3"), CREATECOMPUTER("4"), UPDATECOMPUTER(
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

    private final String COMPUTER = "computer";
    private final String COMPANY = "company";

    /**
     * Blank constructor.
     */
    public CLIController() {

    }

    /**
     * Constructor with an UI and a service.
     * @param ui       The UI
     * @param companyService  The company service
     * @param computerService The computer service
     */
    public CLIController(CLI ui, CompanyService companyService, ComputerService computerService) {
        this.ui = ui;
        this.companyService = companyService;
        this.computerService = computerService;
    }

    /**
     * The main method of the controller, making the discussion between UI and service.
     */
    public void run() {
        boolean stop = false;
        ChoiceMenu choice;
        Optional<Page<Computer>> computers;
        Optional<Page<Company>> companies;
        Computer computer;
        Computer computerToUpdate;
        int id;
        String choicePage;
        while (!stop) {
            try {
                choice = ChoiceMenu.get(ui.home());
                switch (choice) {
                case DELETECOMPANY:
                    id = ui.askId(COMPANY);
                    companyService.deleteCompany(id);
                    break;
                case LISTCOMPUTERS:
                    computers = computerService.getAllComputersWithPaging(1, 5);
                    choicePage = ui.displayComputers(computers);
                    while (choicePage.equals("p")) {
                        id = ui.askPage();
                        computers = computerService.getAllComputersWithPaging(id, 5);
                        choicePage = ui.displayComputers(computers);
                    }
                    break;
                case LISTCOMPANIES:
                    companies = companyService.getAllCompaniesWithPaging(1, 5);
                    choicePage = ui.displayCompanies(companies);
                    while (choicePage.equals("p")) {
                        id = ui.askPage();
                        companies = companyService.getAllCompaniesWithPaging(id, 5);
                        choicePage = ui.displayCompanies(companies);
                    }
                    break;
                case SHOWCOMPUTERDETAILS:
                    id = ui.askId(COMPUTER);
                    computer = computerService.getOneComputer(id);
                    ui.showComputerDetails(computer);
                    break;
                case CREATECOMPUTER:
                    computer = ui.createComputer();
                    computerService.createComputer(computer);
                    break;
                case UPDATECOMPUTER:
                    id = ui.askId(COMPUTER);
                    computerToUpdate = computerService.getOneComputer(id);
                    computer = ui.updateComputer(computerToUpdate);
                    computerService.updateComputer(computer);
                    break;
                case DELETECOMPUTER:
                    id = ui.deleteComputer();
                    computerService.deleteComputer(id);
                    break;
                case PAGECOMPANY:
                case QUIT:
                    return;
                default:
                    break;
                }
            } catch (ComputerServiceException e) {
                ui.displayException(e.getMessage());
            }
        }
    }

}
