package com.excilys.cdb.controller;

import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.GeneralService;
import com.excilys.cdb.ui.CLI;
import com.excilys.cdb.utils.Page;

/**
 * The controller make the discussion between the UI and the service layer.
 * 
 * @author emmanuelh
 *
 */
public class Controller {
	
	/**
	 * The displaying UI
	 */
	private CLI ui;
	
	/**
	 * The service connected to the model
	 */
	private GeneralService service;
	
	/**
	 * ChoiceMenu is used for the switch case concerning the return value of the UI
	 * 
	 * @author emmanuelh
	 *
	 */
	private static enum ChoiceMenu {LIST_COMPUTERS("1"), LIST_COMPANIES("2"), SHOW_COMPUTER_DETAILS("3"), CREATE_COMPUTER("4"),
		UPDATE_COMPUTER("5"), DELETE_COMPUTER("6"), QUIT("7"), PAGE_COMPANY("pc");
		private String choice;
		
		ChoiceMenu(String _choice){
			this.choice = _choice;
		}

		public static ChoiceMenu get(String choix) {
			for(ChoiceMenu choiceMenu : ChoiceMenu.values()) {
				if(choiceMenu.choice.equals(choix)) {
					return choiceMenu;
				}
			}
			return null;
		}
	};
	
	public Controller() {
		
	}
	
	public Controller(CLI _ui, GeneralService _service) {
		this.ui = _ui;
		this.service = _service;
	}
	
	/**
	 * The main method of the controller, making the discussion between UI and service
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
		while(!stop) {
			try {
				choice = ChoiceMenu.get(ui.home());
				switch(choice) {
				case LIST_COMPUTERS:
					computers = service.getAllComputers(1);
					choicePage = ui.displayComputers(computers);
					while(choicePage.equals("p")) {
						id = ui.askPage();
						computers = service.getAllComputers(id);
						choicePage = ui.displayComputers(computers);
					}
					break;
				case LIST_COMPANIES:
					companies = service.getAllCompanies(1);
					choicePage = ui.displayCompanies(companies);
					while(choicePage.equals("p")) {
						id = ui.askPage();
						companies = service.getAllCompanies(id);
						choicePage = ui.displayCompanies(companies);
					}
					break;
				case SHOW_COMPUTER_DETAILS:
					id = ui.askComputerId();
					computer = service.getOneComputer(id);
					ui.showComputerDetails(computer);
					break;
				case CREATE_COMPUTER:
					computer = ui.createComputer();
					service.createComputer(computer);
					break;
				case UPDATE_COMPUTER:
					id = ui.askComputerId();
					computerToUpdate = service.getOneComputer(id);
					computer = ui.updateComputer(computerToUpdate);
					service.updateComputer(computer);
					break;
				case DELETE_COMPUTER:
					id = ui.deleteComputer();
					service.deleteComputer(id);
					break;
				case PAGE_COMPANY:
				case QUIT:
					return;
				}
			} catch (GeneralServiceException e) {
				ui.displayException(e.getMessage());
			}
		}
	}
	
}
