package main.java.com.excilys.cdb.model;

import java.time.LocalDate;

/**
 * The class describing a computer
 * 
 * @author emmanuelh
 *
 */
public class Computer {
	
	/**
	 * The identifier of the computer
	 */
	private long id;
	
	/**
	 * The name of the computer (mandatory)
	 */
	private String name;
	
	/**
	 * The introduced date
	 */
	private LocalDate introduced;
	
	/**
	 * The discontinued date
	 */
	private LocalDate discontinued;
	
	/**
	 * The manufacturer of the computer
	 */
	private Company manufacturer;
	
	public static class Builder {
		private long id;
		private final String name;
		private LocalDate introduced;
		private LocalDate discontinued;
		private Company manufacturer;
		
		public Builder(String _name) {
			name = _name;
		}
		
		public Builder id(long _id) {
			id = _id;
			return this;
		}
		
		public Builder introduced(LocalDate _introduced) {
			introduced = _introduced;
			return this;
		}
		
		public Builder discontinued(LocalDate _discontinued) {
			discontinued = _discontinued;
			return this;
		}
		
		public Builder manufacturer(Company _manufacturer) {
			manufacturer = _manufacturer;
			return this;
		}
		
		public Computer build() {
			return new Computer(this);
		}
	}
	
	private Computer(Builder builder) {
		id = builder.id;
		name = builder.name;
		introduced = builder.introduced;
		discontinued = builder.discontinued;
		manufacturer = builder.manufacturer;
	}
	
	public Computer() {
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

	public LocalDate getIntroduced() {
		return introduced;
	}

	public void setIntroduced(LocalDate introduced) {
		this.introduced = introduced;
	}

	public LocalDate getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(LocalDate discontinued) {
		this.discontinued = discontinued;
	}

	public Company getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Company manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Override
	public String toString() {
		return "Computer [id=" + id + ", name=" + name + ", introduced=" + introduced + ", discontinued=" + discontinued
				+ ", manufacturer=" + manufacturer + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((discontinued == null) ? 0 : discontinued.hashCode());
		result = prime * result + (int)id;
		result = prime * result + ((introduced == null) ? 0 : introduced.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    final Computer other = (Computer) obj;
	    if (this.id != other.id) {
	        return false;
	    }
	    return true;
	}
	
	
}
