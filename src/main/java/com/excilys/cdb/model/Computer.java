package main.java.com.excilys.cdb.model;

import java.util.Date;

public class Computer {
	private int id;
	private String name;
	private Date introduced;
	private Date discontinued;
	private Company manufacturer;
	
	public Computer() {
	}

	public Computer(String _name) {
		this.name = _name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getIntroduced() {
		return introduced;
	}

	public void setIntroduced(Date introduced) {
		this.introduced = introduced;
	}

	public Date getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(Date discontinued) {
		this.discontinued = discontinued;
	}

	public Company getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Company manufacturer) {
		this.manufacturer = manufacturer;
	}
}
