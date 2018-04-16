package main.java.com.excilys.cdb.daos;

import java.util.List;

import main.java.com.excilys.cdb.model.Computer;

public interface ComputerDAO {
	List<Computer> findAll();
	Computer findOneById(int id);
	boolean add(Computer computer);
	boolean delete(Computer computer);
	boolean update(Computer computer);
}
