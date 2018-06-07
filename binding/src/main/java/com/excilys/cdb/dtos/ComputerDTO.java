package com.excilys.cdb.dtos;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.excilys.cdb.model.Computer;

public class ComputerDTO {

    private long id;

    @NotBlank
    private String name;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String introduced;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String discontinued;
    
    private String manufacturer;
    
    private long manufacturer_id;

    /**
     * Default constructor for ComputerDTO.
     */
    public ComputerDTO() {

    }

    /**
     * Constructor transforming a Computer into a ComputerDTO.
     * @param computer  The Computer to transform
     */
    public ComputerDTO(Computer computer) {
        this.id = computer.getId();
        this.name = computer.getName();
        if (null != computer.getIntroduced()) {
            this.introduced = computer.getIntroduced().toString();
        }
        if (null != computer.getDiscontinued()) {
            this.discontinued = computer.getDiscontinued().toString();
        }
        if (null != computer.getManufacturer()) {
            this.manufacturer = computer.getManufacturer().getName();
            this.manufacturer_id = computer.getManufacturer().getId();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduced() {
        return introduced;
    }

    public void setIntroduced(String introduced) {
        this.introduced = introduced;
    }

    public String getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(String discontinued) {
        this.discontinued = discontinued;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

	public long getManufacturer_id() {
		return manufacturer_id;
	}

	public void setManufacturer_id(long manufacturer_id) {
		this.manufacturer_id = manufacturer_id;
	}

}
