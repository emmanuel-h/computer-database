package com.excilys.cdb;

import com.excilys.cdb.controller.Controller;
import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.services.GeneralService;
import com.excilys.cdb.ui.CLI;

public class Main {

	private static CLI ui;
	private static GeneralService service;

	
	public static void main(String[] args) {
		try {
			//Initialize the static variables
			ui = new CLI();
			service = GeneralService.getInstance();
			Controller controller = new Controller(ui, service);
			controller.run();
		} catch (GeneralServiceException e) {

		}
	}
}
