package com.excilys.cdb.dtos;

import com.excilys.cdb.model.Computer;

public class ComputerDTO {

    private long id;
    private String name;
    private String introduced;
    private String discontinued;
    private String manufacturer;

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

}
