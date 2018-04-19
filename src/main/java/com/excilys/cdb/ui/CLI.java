package main.java.com.excilys.cdb.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.utils.Page;

/**
 * Part of the package UI, allowing the app to be displayed in a terminal
 * 
 * @author emmanuelh
 *
 */
public class CLI {

	/**
	 * The scanner used to ask user entries
	 */
	private Scanner scanner;
	
	/**
	 * The logger
	 */
	private final Logger logger = LoggerFactory.getLogger(CLI.class);
	
	private final String REGEX_DATE = "^((18|19|20|21)\\\\d\\\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";
	
	public CLI() {
		scanner = new Scanner(System.in);
	}
	
	/**
	 * The menu of the homepage of the CLI
	 * 
	 * @return The user choice
	 */
	public String home() {
		System.out.println("\n*****MENU*****");
		System.out.println("1- List computers");
		System.out.println("2- List companies");
		System.out.println("3- Show computer details");
		System.out.println("4- Create a computer");
		System.out.println("5- Update a computer");
		System.out.println("6- Delete a computer");
		System.out.println("7- Quit");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.matches("[1-7]"));
		return choice;
	}
	
	/**
	 * Display the list of the computers, default is page 1 with 5 results
	 * 
	 * @param computers The computers to display
	 * @return 			m if the user wants to go back to the menu,
	 * 					p if the user wants to see a particular page
	 */
	public String displayComputers(Page<Computer> computers){
		System.out.println("\n*****COMPUTERS LIST*****");
		for(Computer computer : computers.getResults()) {
			System.out.println(computer);
		}
		return goToMenu();
	}
	
	/**
	 * Display the list of the companies, default is page 1 with 5 results
	 * 
	 * @param companies The companies to display
	 * @return			m if the user wants to go back to the menu,
	 * 					p if the user wants to see a particular page
	 */
	public String displayCompanies(Page<Company> companies) {
		System.out.println("\n*****COMPANIES LIST*****");
		for(Company company : companies.getResults()) {
			System.out.println(company);
		}
		return goToMenu();
	}
	
	/**
	 * Display the details of a computer
	 * 
	 * @param computer	The computer to display
	 */
	public void showComputerDetails(Computer computer) {
		System.out.println("\n*****COMPUTER "+computer.getId()+"*****");
		System.out.println(computer);
		System.out.println("m- go back to menu");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.equals("m"));
	}
	
	/**
	 * When a user wants to see a particular computer, this method ask him which one
	 * 
	 * @return	The id of the desired computer
	 */
	public int askComputerId() {
		System.out.println("Enter the id of the computer");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.matches("[0-9]+"));
		return Integer.parseInt(choice);
	}
	
	/**
	 * Display a form to create a computer
	 * 
	 * @return The computer created by the user
	 */
	public Computer createComputer() {
		
		// Get the name
		System.out.println("Name (mandatory)");
		String name;
		do {
			name = scanner.nextLine();
		} while(name.isEmpty());
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// Get the introduced date
		System.out.println("Introduced Date (yyyy-MM-dd) - s to skip");
		String date;
		do {
			date = scanner.nextLine();
		} while (!date.matches(REGEX_DATE) && !date.equals("s"));
		Date dateIntroduced = null;
		if(!date.equals("s")) {
			try {
				dateIntroduced = simpleDateFormat.parse(date);
			} catch (ParseException e) {
				logger.warn("Error in parsing the introduced date when creating a computer");
			}
		}

		// Get the discontinued date
		System.out.println("Discontinued Date (yyyy-MM-dd) - s to skip");
		do {
			date = scanner.nextLine();
		} while (!date.matches("^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && !date.equals("s"));
		Date dateDiscontinued = null;
		if(!date.equals("s")) {
			try {
				dateDiscontinued = simpleDateFormat.parse(date);
			} catch (ParseException e) {
				logger.warn("Error in parsing the discontinued date when creating a computer");
			}
		}
		
		// Get the manufacturer
		System.out.println("Manufacturer (id) - s to skip");
		String manufacturer;
		do{
			manufacturer = scanner.nextLine();
		} while (!manufacturer.matches("[0-9]+") && !manufacturer.equals("s"));
		Company company;
		if(manufacturer.equals("s")) {
			company = null;
		} else {
			company = new Company(Integer.parseInt(manufacturer));
		}
		
		// Make the object
		Computer computer = new Computer();
		computer.setName(name);
		computer.setIntroduced(dateIntroduced);
		computer.setDiscontinued(dateDiscontinued);
		computer.setManufacturer(company);
		
		return computer;
	}
	
	/**
	 * When the user wants to update a computer, ask him for his id
	 * 
	 * @return	The id of the computer to modify
	 */
	public int findComputerToModify() {
		System.out.println("Which computer do you want to modify ?");
		return 0;
	}
	
	/**
	 * Ask the user the id of the computer he wants to delete
	 * 
	 * @return the id of the computer
	 */
	public int deleteComputer() {
		System.out.println("Which computer do you want to delete ?");
		String id;
		do{
			id = scanner.nextLine();
		} while (!id.matches("[0-9]+") && !id.equals("s"));
		return Integer.parseInt(id);
	}
	
	/**
	 * Update the different informations of a computer
	 * 
	 * @param computerToUpdate	The original computer
	 * @return					The modified computer
	 */
	public Computer updateComputer(Computer computerToUpdate) {
		// Get the name
		System.out.println("Name (mandatory) (currently: " + computerToUpdate.getName()
		+ ") - s to skip");
		String name;
		do {
			name = scanner.nextLine();
		} while(name.isEmpty());
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// Get the introduced date
		System.out.println("Introduced Date (yyyy-MM-dd) (currently: " + computerToUpdate.getIntroduced()
				+ ") - s to skip");
		String date;
		do {
			date = scanner.nextLine();
		} while (!date.matches("^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && !date.equals("s"));
		Date dateIntroduced = null;
		if(!date.equals("s")) {
			try {
				dateIntroduced = simpleDateFormat.parse(date);
			} catch (ParseException e) {
				logger.warn("Error in parsing the introduced date when updating a computer");
			}
		} else {
			if(null != computerToUpdate.getIntroduced()) {
				dateIntroduced = computerToUpdate.getIntroduced();
			}
		}

		// Get the discontinued date
		System.out.println("Discontinued Date (yyyy-MM-dd) (currently:" + computerToUpdate.getDiscontinued()
				+ ") - s to skip");
		do {
			date = scanner.nextLine();
		} while (!date.matches(REGEX_DATE) && !date.equals("s"));
		Date dateDiscontinued = null;
		if(!date.equals("s")) {
			try {
				dateDiscontinued = simpleDateFormat.parse(date);
			} catch (ParseException e) {
				logger.warn("Error in parsing the discontinued date when updating a computer");
			}
		} else {
			if(null != computerToUpdate.getDiscontinued()) {
				dateDiscontinued = computerToUpdate.getDiscontinued();
			}
		}
		
		// Get the manufacturer
		System.out.println("Manufacturer (id) (currently: " + computerToUpdate.getManufacturer()
				+ ") - s to skip");
		String manufacturer;
		do{
			manufacturer = scanner.nextLine();
		} while (!manufacturer.matches("[0-9]+") && !manufacturer.equals("s"));
		Company company;
		if(manufacturer.equals("s")) {
			if(null != computerToUpdate.getManufacturer()) {
				company = computerToUpdate.getManufacturer();
			} else {
				company = null;
			}
		} else {
			company = new Company(Integer.parseInt(manufacturer));
		}
		
		// Make the object
		Computer computer = new Computer();
		computer.setId(computerToUpdate.getId());
		if(!name.equals("s")) {
			computer.setName(name);
		}
		computer.setIntroduced(dateIntroduced);
		computer.setDiscontinued(dateDiscontinued);
		computer.setManufacturer(company);
		
		return computer;
	}
	
	/**
	 * When a exception is throw that the user must see, it is displayed with this method
	 * 
	 * @param message	The message of the exception
	 */
	public void displayException(String message) {
		System.out.println("*****ERREUR*****");
		System.out.println(message);
	}

	/**
	 * Used when the user wants to see a particular page of the list of computers or companies
	 * 
	 * @return	The page number
	 */
	public int askPage() {
		System.out.println("Enter the page you want to see");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.matches("[0-9]+"));
		return Integer.parseInt(choice);
	}
	
	/**
	 * Ask the user if he wants to go back to the homepage, or if he wants to display another page
	 * 
	 * @return			m if the user wants to go back to the menu,
	 * 					p if the user wants to see a particular page
	 */
	private String goToMenu() {
		System.out.println("m- go back to menu, p- display a particular page");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.equals("m") && !choice.equals("p"));
		return choice;
	}
	
	/**
	 * Closing the scanner if the object is destroyed by the garbage collector
	 */
	@Override
	protected void finalize() throws Throwable {
		scanner.close();
	}
	
}
