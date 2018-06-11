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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((discontinued == null) ? 0 : discontinued.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((introduced == null) ? 0 : introduced.hashCode());
		result = prime * result + ((manufacturer == null) ? 0 : manufacturer.hashCode());
		result = prime * result + (int) (manufacturer_id ^ (manufacturer_id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComputerDTO other = (ComputerDTO) obj;
		if (discontinued == null) {
			if (other.discontinued != null)
				return false;
		} else if (!discontinued.equals(other.discontinued))
			return false;
		if (id != other.id)
			return false;
		if (introduced == null) {
			if (other.introduced != null)
				return false;
		} else if (!introduced.equals(other.introduced))
			return false;
		if (manufacturer == null) {
			if (other.manufacturer != null)
				return false;
		} else if (!manufacturer.equals(other.manufacturer))
			return false;
		if (manufacturer_id != other.manufacturer_id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComputerDTO [id=" + id + ", name=" + name + ", introduced=" + introduced + ", discontinued="
				+ discontinued + ", manufacturer=" + manufacturer + ", manufacturer_id=" + manufacturer_id + "]";
	}

}
