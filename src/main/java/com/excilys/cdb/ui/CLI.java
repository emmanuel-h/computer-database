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
		System.out.println("Enter the id of the computer");
		String choice;
		do{
			choice = scanner.nextLine();
		} while (!choice.matches("[0-9]+"));
		return Integer.parseInt(choice);
	}
	
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
		} while (!date.matches("^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && !date.equals("s"));
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
		System.out.println("Discontinued Date (yyyy-MM-dd) - s to skip");
		do {
			date = scanner.nextLine();
		} while (!date.matches("^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && !date.equals("s"));
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
	
	public int findComputerToModify() {
		System.out.println("Which computer do you want to modify ?");
		return 0;
	}
	
	public int deleteComputer() {
		System.out.println("Which computer do you want to delete ?");
		String id;
		do{
			id = scanner.nextLine();
		} while (!id.matches("[0-9]+") && !id.equals("s"));
		return Integer.parseInt(id);
	}
	
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		} while (!date.matches("^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && !date.equals("s"));
		Date dateDiscontinued = null;
		if(!date.equals("s")) {
			try {
				dateDiscontinued = simpleDateFormat.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
