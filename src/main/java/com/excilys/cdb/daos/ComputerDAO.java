package main.java.com.excilys.cdb.daos;

import java.sql.Connection;
import java.util.List;

import main.java.com.excilys.cdb.model.Computer;

public class ComputerDAO implements DAO<Computer> {

	public ComputerDAO(Connection connection) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Computer> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Computer findOneById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Computer t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Computer t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Computer t) {
		// TODO Auto-generated method stub
		return false;
	}

}
