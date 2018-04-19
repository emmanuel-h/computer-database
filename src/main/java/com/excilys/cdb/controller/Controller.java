package main.java.com.excilys.cdb.controller;

import main.java.com.excilys.cdb.exceptions.GeneralServiceException;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.services.GeneralService;
import main.java.com.excilys.cdb.ui.CLI;
import main.java.com.excilys.cdb.utils.Page;

public class Controller {
	
	private CLI ui;
	private GeneralService service;
	
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
	
	public boolean run() {
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
				choice = ChoiceMenu.get(ui.accueil());
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
					return false;
				}
			} catch (GeneralServiceException e) {
				ui.displayException(e.getMessage());
			}
		}
		return true;
	}
	
}
