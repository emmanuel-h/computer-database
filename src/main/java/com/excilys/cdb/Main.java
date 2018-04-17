package main.java.com.excilys.cdb;

import java.util.List;

import main.java.com.excilys.cdb.exceptions.GeneralServiceException;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.services.GeneralService;
import main.java.com.excilys.cdb.ui.CLI;

public class Main {

	private static CLI ui;
	private static GeneralService service;
	private static enum ChoiceMenu {LIST_COMPUTERS("1"), LIST_COMPANIES("2"), SHOW_COMPUTER_DETAILS("3"), CREATE_COMPUTER("4"),
		UPDATE_COMPUTER("5"), DELETE_COMPUTER("6"), QUIT("7");
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
	
	public static void main(String[] args) {
		try {
			//Initialize the static variables
			ui = new CLI();
			service = new GeneralService();
			
			boolean stop = false;
			ChoiceMenu choice;
			List<Computer> computers;
			List<Company> companies;
			Computer computer;
			while(!stop) {
				choice = ChoiceMenu.get(ui.accueil());
				switch(choice) {
				case LIST_COMPUTERS:
					computers = service.getAllComputers();
					ui.displayComputers(computers);
					break;
				case LIST_COMPANIES:
					companies = service.getAllCompanies();
					ui.displayCompanies(companies);
					break;
				case SHOW_COMPUTER_DETAILS:
					int id = ui.askComputerId();
					computer = service.getOneComputer(id);
					ui.showComputerDetails(computer);
					break;
				case CREATE_COMPUTER:
					computer = ui.createComputer();
					service.createComputer(computer);
					break;
				case QUIT:
					stop = true;
					break;
				}
			}
		} catch (GeneralServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
