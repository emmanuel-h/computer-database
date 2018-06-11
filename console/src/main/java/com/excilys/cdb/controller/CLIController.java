package com.excilys.cdb.controller;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import com.excilys.cdb.Page;
import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.ui.CLI;

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
     * @param ui        The UI
     */
    public CLIController(CLI ui) {
        this.ui = ui;
    }

    /**
     * The main method of the controller, making the discussion between UI and service.
     */
    public void run() {
        boolean stop = false;
        ChoiceMenu choice;
        Page<ComputerDTO> computers;
        Page<Company> companies;
        ComputerDTO computerDTO;
        ComputerDTO computerToUpdate;
        int id;
        String choicePage;
        
        Client client;
        WebTarget webTarget;
        Invocation.Builder invocationBuilder;
        while (!stop) {
            choice = ChoiceMenu.get(ui.home());
			switch (choice) {
			case DELETECOMPANY:
			    id = ui.askId(COMPANY);

			    client = ClientBuilder.newClient();
			    webTarget = client.target("http://localhost:8081/webservice/company/" + id);
			    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			    invocationBuilder.delete();
			    break;
			case LISTCOMPUTERS:
				computers = new Page<>();
			    client = ClientBuilder.newClient();
			    webTarget = client.target("http://localhost:8081/webservice/computer?page=1&results=5");
			    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			    computers.setResults(invocationBuilder.get(new GenericType<List<ComputerDTO>>() {}));
			    computers.setCurrentPage(1);
			    computers.setResultsPerPage(5);
			    
			    choicePage = ui.displayComputers(computers);
			    
			    while (choicePage.equals("p")) {
			        id = ui.askPage();
			        
				    webTarget = client.target("http://localhost:8081/webservice/computer?page=" + id + "&results=5");
				    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
				    computers.setResults(invocationBuilder.get(new GenericType<List<ComputerDTO>>() {}));
				    computers.setCurrentPage(id);
				    
			        choicePage = ui.displayComputers(computers);
			    }
			    break;
			case LISTCOMPANIES:
				companies = new Page<>();
			    client = ClientBuilder.newClient();
			    webTarget = client.target("http://localhost:8081/webservice/company?page=1&results=5");
			    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			    companies.setResults(invocationBuilder.get(new GenericType<List<Company>>() {}));
			    companies.setCurrentPage(1);
			    companies.setResultsPerPage(5);
			    
			    choicePage = ui.displayCompanies(companies);
			    while (choicePage.equals("p")) {
			        id = ui.askPage();

				    webTarget = client.target("http://localhost:8081/webservice/company?page=" + id + "&results=5");
				    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
				    companies.setResults(invocationBuilder.get(new GenericType<List<Company>>() {}));
				    companies.setCurrentPage(1);
				    
			        choicePage = ui.displayCompanies(companies);
			    }
			    break;
			case SHOWCOMPUTERDETAILS:
			    id = ui.askId(COMPUTER);
			    client = ClientBuilder.newClient();
			    webTarget = client.target("http://localhost:8081/webservice/computer/" + id);
			    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			    computerDTO = invocationBuilder.get(ComputerDTO.class);
			    client.close();
			    ui.showComputerDetails(computerDTO);
			    break;
			case CREATECOMPUTER:
			    computerDTO = ui.createComputer();
			    client = ClientBuilder.newClient();
			    webTarget = client.target("http://localhost:8081/webservice/computer");
			    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			    invocationBuilder.post(Entity.entity(computerDTO, MediaType.APPLICATION_JSON));
			    client.close();
			    break;
			case UPDATECOMPUTER:
			    id = ui.askId(COMPUTER);
			    
			    // Verify that the computer exists.
			    client = ClientBuilder.newClient();
			    webTarget = client.target("http://localhost:8081/webservice/computer/" + id);
			    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			    computerToUpdate = invocationBuilder.get(ComputerDTO.class);
			    
			    // Ask the user the computer's changes to do.
			    computerDTO = ui.updateComputer(computerToUpdate);
			    
			    // Apply the desired changes.
			    invocationBuilder.put(Entity.entity(computerDTO, MediaType.APPLICATION_JSON));
			    client.close();
			    break;
			case DELETECOMPUTER:
			    id = ui.deleteComputer();

			    client = ClientBuilder.newClient();
			    webTarget = client.target("http://localhost:8081/webservice/computer/" + id);
			    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			    invocationBuilder.delete();
			    break;
			case PAGECOMPANY:
			case QUIT:
			    return;
			default:
			    break;
			}
        }
    }

}
