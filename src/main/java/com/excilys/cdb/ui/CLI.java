package main.java.com.excilys.cdb.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public class CLI {

	private Scanner scanner;
	
	public CLI() {
		scanner = new Scanner(System.in);
	}
	
	public String accueil() {
		resetDisplay();
		System.out.println("*****MENU*****");
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
	
	public void displayComputers(List<Computer> computers){
		resetDisplay();
		System.out.println("*****COMPUTERS LIST*****");
		for(Computer computer : computers) {
			System.out.println(computer);
		}
		goToMenu();
	}
	
	public void displayCompanies(List<Company> companies) {
		resetDisplay();
		System.out.println("*****COMPANIES LIST*****");
		for(Company company : companies) {
			System.out.println(company);
		}
		goToMenu();
	}
	
	public void showComputerDetails(Computer computer) {
		resetDisplay();
		System.out.println("*****COMPUTER "+computer.getId()+"*****");
		System.out.println(computer);
		goToMenu();
	}
	
	public int askComputerId() {
		System.out.println("Enter the id of the computer you want to see");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.matches("[0-9]+"));
		return Integer.parseInt(choice);
	}
	
	public Computer createComputer() {
		
		// Get the name
		System.out.println("Nom (mandatory)");
		String name;
		do {
			name = scanner.nextLine();
		} while(name.isEmpty());
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		// Get the introduced date
		System.out.println("Introduced Date (dd-MM-yyyy) - s to skip");
		String date;
		do {
			date = scanner.nextLine();
		} while (!date.matches("^((18|19|20|21)\\\\d\\\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && !date.equals("s"));
		Date dateIntroduced = null;
		if(!date.equals("s")) {
			try {
				dateIntroduced = simpleDateFormat.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Get the discontinued date
		System.out.println("Discontinued Date (dd-MM-yyyy) - s to skip");
		do {
			date = scanner.nextLine();
		} while (!date.matches("^((18|19|20|21)\\\\d\\\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && !date.equals("s"));
		Date dateDiscontinued = null;
		if(!date.equals("s")) {
			try {
				dateDiscontinued = simpleDateFormat.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Get the manufacturer
		int idManufacturer;
		System.out.println("Manufacturer (id) - s to skip");
		String manufacturer;
		do{
			manufacturer = scanner.nextLine();
		} while (!manufacturer.matches("[0-9]+") && !manufacturer.equals("s"));
		if(manufacturer.equals("s")) {
			
		}
		idManufacturer = Integer.parseInt(manufacturer);
		
		// Make the object
		Computer computer = new Computer();
		computer.setName(name);
		computer.setIntroduced(dateIntroduced);
		computer.setDiscontinued(dateDiscontinued);
		computer.setManufacturer(new Company(idManufacturer));
		
		return computer;
	}
	
	private void resetDisplay() {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
	private void goToMenu() {
		System.out.println("m- go back to menu");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.equals("m"));
	}
	
	@Override
	protected void finalize() throws Throwable {
		scanner.close();
	}
	
}
