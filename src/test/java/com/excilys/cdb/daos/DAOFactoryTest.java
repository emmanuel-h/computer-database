package com.excilys.cdb.daos;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.excilys.cdb.daos.DAOFactory.DaoTypes;
import com.excilys.cdb.exceptions.FactoryException;

public class DAOFactoryTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Test the getDAO method.
     * @throws FactoryException If there is a problem inside the FactoryDAO
     */
    @Test
    public void testGetDAO() throws FactoryException {
        CompanyDAO companyDAO = (CompanyDAO) DAOFactory.getDAO(DaoTypes.COMPANY);
        assertTrue(companyDAO instanceof CompanyDAO);
        ComputerDAO computerDAO = (ComputerDAO) DAOFactory.getDAO(DaoTypes.COMPUTER);
        assertTrue(computerDAO instanceof ComputerDAO);
    }

    /**
     * Test when entering an invalid DAOType.
     * @throws FactoryException If there is a problem inside the FactoryDAO
     */
    @Test
    public void testGetDAOInvalidDAOType() throws FactoryException {
        exception.expect(FactoryException.class);
        DAOFactory.getDAO(null);
    }

}
