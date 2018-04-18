package main.java.com.excilys.cdb;

import main.java.com.excilys.cdb.controller.Controller;
import main.java.com.excilys.cdb.exceptions.GeneralServiceException;
import main.java.com.excilys.cdb.services.GeneralService;
import main.java.com.excilys.cdb.ui.CLI;

public class Main {

	private static CLI ui;
	private static GeneralService service;

	
	public static void main(String[] args) {
		try {
			//Initialize the static variables
			ui = new CLI();
			service = new GeneralService();
			Controller controller = new Controller(ui, service);
			controller.run();
		} catch (GeneralServiceException e) {

		}
	}
}
