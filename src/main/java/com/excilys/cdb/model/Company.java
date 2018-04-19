package main.java.com.excilys.cdb.model;

/**
 * the class describing a company
 * 
 * @author emmanuelh
 *
 */
public class Company {
	
	/**
	 * The identifier of the company
	 */
	long id;
	
	/**
	 * The name of the company
	 */
	String name;
	
	public Company() {
	}

	public Company(long id) {
		this.id = id;
	}
	
	public Company(long id, String name) {
		this.id = id;
		this.name = name;
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

	@Override
	public String toString() {
		return "Company [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int)id;
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
	    final Company other = (Company) obj;
	    if (this.id != other.id) {
	        return false;
	    }
	    return true;
	}
	

}
