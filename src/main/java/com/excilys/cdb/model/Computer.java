package com.excilys.cdb.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The class describing a computer.
 * @author emmanuelh
 */
@Entity
@Table(name = "computer")
public class Computer {

    /**
     * The identifier of the computer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    /**
     * The name of the computer (mandatory).
     */
    @Column(name = "name")
    private String name;

    /**
     * The introduced date.
     */
    @Column(name = "introduced")
    private LocalDate introduced;

    /**
     * The discontinued date.
     */
    @Column(name = "discontinued")
    private LocalDate discontinued;

    /**
     * The manufacturer of the computer.
     */
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company manufacturer;

    public static class Builder {
        private long id;
        private final String name;
        private LocalDate introduced;
        private LocalDate discontinued;
        private Company manufacturer;

        /**
         * Builder constructor for field name.
         * @param name  The name of the computer.
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Builder method to initialize the id.
         * @param id  The id of the computer
         * @return The builder initialized
         */
        public Builder id(long id) {
            this.id = id;
            return this;
        }

        /**
         * Builder method to initialize the introduced date.
         * @param introduced    The introduced date of the computer
         * @return              The builder initialized
         */
        public Builder introduced(LocalDate introduced) {
            this.introduced = introduced;
            return this;
        }

        /**
         * Builder method to initialize the discontinued date.
         * @param discontinued  The discontinued date of the computer
         * @return              The builder initialized
         */
        public Builder discontinued(LocalDate discontinued) {
            this.discontinued = discontinued;
            return this;
        }

        /**
         * Builder method to initialize the computer's manufacturer.
         * @param manufacturer The computer's manufacturer
         * @return              The builder initialized
         */
        public Builder manufacturer(Company manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        /**
         * Build the builder with all the desired variable initializations.
         * @return  The computer initialized.
         */
        public Computer build() {
            return new Computer(this);
        }
    }

    /**
     * Private constructor creating a Computer object from a Builder.
     * @param builder   The initial builder
     */
    private Computer(Builder builder) {
        id = builder.id;
        name = builder.name;
        introduced = builder.introduced;
        discontinued = builder.discontinued;
        manufacturer = builder.manufacturer;
    }

    /**
     * The blank constructor.
     */
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
        result = prime * result + (int) id;
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
