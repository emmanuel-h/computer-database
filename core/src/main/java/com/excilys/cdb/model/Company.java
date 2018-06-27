package com.excilys.cdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * The class describing a company.
 * @author emmanuelh
 */
@Entity
@Table(name = "company")
public class Company {

    /**
     * The identifier of the company.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * The name of the company.
     */
    @Column(name = "name")
    @NotBlank
    private String name;
    

    @Column(name = "number_of_computers")
    private int numberOfComputers;
    

    @Column(name = "image_url")
    @NotBlank
    @Size(max = 255)
    private String imageUrl;

    /**
     * Empty constructor.
     */
    public Company() {
    }

    /**
     * Constructor with id.
     * @param id The identifier
     */
    public Company(long id) {
        this.id = id;
    }

    /**
     * Constructor with all parameters.
     * @param id The identifier
     * @param name The name if the company
     */

    public Company(long id, @NotBlank String name, int numberOfComputers, @NotBlank String imageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.numberOfComputers = numberOfComputers;
		this.imageUrl = imageUrl;
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
    

    public int getNumberOfComputers() {
		return numberOfComputers;
	}

	public void setNumberOfComputers(int numberOfComputers) {
		this.numberOfComputers = numberOfComputers;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public void decreaseNumberOfComputers() {
		this.numberOfComputers --;
	}
	
	public void increaseNumberOfComputers() {
		this.numberOfComputers ++;
	}

	@Override
    public String toString() {
        return "Company [id=" + id + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) id;
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
