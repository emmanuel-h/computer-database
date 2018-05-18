package com.excilys.cdb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.daos.CompanyDAO;
import com.excilys.cdb.daos.ComputerDAO;
import com.excilys.cdb.exceptions.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.Page;

@RunWith(MockitoJUnitRunner.class)
public class ComputerServiceTest {

    @Mock
    private CompanyDAO companyDAO;

    @Mock
    private ComputerDAO computerDAO;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private ComputerService service;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Logger LOGGER = LoggerFactory.getLogger(ComputerServiceTest.class);
    private final String SQL_EXCEPTION = "Unit test - SQL Exception : ";

    /**
     * Initialize the mocks.
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test if the getAllComputers method work correctly.
     */
    @Test
    public void getAllComputersWithValidPageNumber() {
        Computer computer = new Computer.Builder("test 1").id(1L).build();
        Page<Computer> computers = new Page<>();

        List<Computer> computerList = new ArrayList<>();
        computerList.add(computer);
        computers.setResults(computerList);

        try {
            Mockito.when(computerDAO.findAllWithPaging(1, 10)).thenReturn(computers);

            Optional<Page<Computer>> resultsPageOptional = service.getAllComputersWithPaging(1, 10);
            if (resultsPageOptional.isPresent()) {
                Page<Computer> resultsPage = resultsPageOptional.get();
                assertTrue(resultsPage.getResults().get(0).equals(computer));
            }
            Mockito.verify(computerDAO).findAllWithPaging(1, 10);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the getAllComputers method work correctly when an invalid results per page is asked.
     */
    @Test
    public void getAllComputersWithInvalidResultsPerPage() {

        try {
            Mockito.when(computerDAO.findAllWithPaging(1, -1)).thenReturn(null);

            Optional<Page<Computer>> resultsPageNegative = service.getAllComputersWithPaging(1, -1);
            assertFalse(resultsPageNegative.isPresent());
            Mockito.verify(computerDAO).findAllWithPaging(1, -1);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the getAllComputers method work correctly when an invalid page is asked.
     */
    @Test
    public void getAllComputersWithInvalidPageNumber() {

        try {
            Mockito.when(computerDAO.findAllWithPaging(-1, 10)).thenReturn(null);

            Optional<Page<Computer>> resultsPageNegative = service.getAllComputersWithPaging(-1, 10);
            assertFalse(resultsPageNegative.isPresent());
            Mockito.verify(computerDAO).findAllWithPaging(-1, 10);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the getAllComputers method work correctly when a SQLException is raised.
     */
    @Test
    public void getAllComputersWithSQLException() {
        try {
            Mockito.when(computerDAO.findAllWithPaging(1, 10)).thenThrow(SQLException.class);

            Optional<Page<Computer>> resultsPageException = service.getAllComputersWithPaging(1, 10);
            assertFalse(resultsPageException.isPresent());
            Mockito.verify(computerDAO).findAllWithPaging(1, 10);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the getOneComputer works properly.
     * @throws ComputerServiceException  If an SQL Exception is raise by the DAO
     */
    @Test
    public void getOneComputer() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test").id(1L).build();

        try {
            Mockito.when(computerDAO.findById(1L)).thenReturn(Optional.of(computer));
            Mockito.when(computerDAO.findById(42L)).thenThrow(SQLException.class);

            Computer receiveComputer = service.getOneComputer(1L);
            assertTrue(receiveComputer.getId() == 1L);

            exception.expect(ComputerServiceException.class);
            service.getOneComputer(42L);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if createComputer with a null parameter throws an exception.
     * @throws ComputerServiceException  If the computer is null
     */
    @Test
    public void createComputerWithNullParam() throws ComputerServiceException {
        exception.expect(ComputerServiceException.class);
        service.createComputer(null);
    }

    /**
     * Test if createComputer with a nameless computer throws an exception.
     * @throws ComputerServiceException  If the computer is nameless
     */
    @Test
    public void createComputerWithNoName() throws ComputerServiceException {
        Computer computer = new Computer();
        computer.setId(1L);

        exception.expect(ComputerServiceException.class);
        service.createComputer(computer);
    }

    /**
     * Test if createComputer with null dates works.
     * @throws ComputerServiceException  If the DAO throws an exception
     */
    @Test
    public void createComputerWithNullDates() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .discontinued(null)
                .build();

        try {
            Mockito.when(computerDAO.add(computer)).thenReturn(computer.getId());

            long computerCreatedId = service.createComputer(computer);
            assertEquals(computerCreatedId, 1L);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if creating a computer with an older introduced date than discontinued fail.
     * @throws ComputerServiceException  If the computer creation fail
     */
    @Test
    public void createComputerWithBadDates() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .introduced(LocalDate.of(2009, 11, 11))
                .discontinued(LocalDate.of(2008, 11, 11))
                .build();
        exception.expect(ComputerServiceException.class);
        service.createComputer(computer);
    }

    /**
     * Test if creating a computer with a nonexistent company fail.
     * @throws ComputerServiceException  If the computer creation fail
     */
    @Test
    public void createComputerWithUnknownManufacturer() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .manufacturer(new Company(2L, "nonexistent company"))
                .build();

        Mockito.when(companyService.getOneCompany(2L)).thenReturn(Optional.empty());

        exception.expect(ComputerServiceException.class);
        service.createComputer(computer);
    }

    /**
     * Test if a regular computer creation works.
     * @throws ComputerServiceException  If creation fail
     */
    @Test
    public void createValidComputer() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .build();

        try {
            Mockito.when(computerDAO.add(computer)).thenReturn(1L);

            long id = service.createComputer(computer);
            assertEquals(id, 1L);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if a computer is not updated when a SQL Exception is raised.
     * @throws ComputerServiceException  If there is a problem with the computer update inside the service method
     */
    @Test
    public void createComputerWithSQLException() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .build();

        try {
            Mockito.when(computerDAO.add(computer)).thenThrow(SQLException.class);

            long id = service.createComputer(computer);
            assertEquals(id, 0L);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if updateComputer with a null parameter throws an exception.
     * @throws ComputerServiceException  If the computer is null
     */
    @Test
    public void updateComputerWithNullParam() throws ComputerServiceException {
        exception.expect(ComputerServiceException.class);
        service.updateComputer(null);
    }

    /**
     * Test if updateComputer with a nameless computer throws an exception.
     * @throws ComputerServiceException  If the computer is nameless
     */
    @Test
    public void updateComputerWithNoName() throws ComputerServiceException {
        Computer computer = new Computer();
        computer.setId(1L);

        exception.expect(ComputerServiceException.class);
        service.updateComputer(computer);
    }

    /**
     * Test if updateComputer with null dates works.
     * @throws ComputerServiceException  If the DAO throws an exception
     */
    @Test
    public void updateComputerWithNullDates() throws ComputerServiceException {
        Company manufacturer = new Company(1L, "test company");
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .introduced(LocalDate.of(2007, 11, 11))
                .discontinued(LocalDate.of(2008, 11, 11))
                .manufacturer(manufacturer)
                .build();

        try {
            Mockito.when(computerDAO.update(computer)).thenReturn(computer);
            Mockito.when(companyService.getOneCompany(1L)).thenReturn(Optional.of(manufacturer));

            Optional<Computer> computerUpdatedOptional = service.updateComputer(computer);
            if (computerUpdatedOptional.isPresent()) {
                Computer computerUpdated = computerUpdatedOptional.get();
                assertEquals(computerUpdated.getId(), computer.getId());
                assertEquals(computerUpdated.getName(), computer.getName());
                assertEquals(computerUpdated.getIntroduced(), computer.getIntroduced());
                assertEquals(computerUpdated.getDiscontinued(), computer.getDiscontinued());
                assertEquals(computerUpdated.getManufacturer().getId(), computer.getManufacturer().getId());
            }
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if updating a computer with an older introduced date than discontinued fail.
     * @throws ComputerServiceException  If the computer update fail
     */
    @Test
    public void updateComputerWithBadDates() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .introduced(LocalDate.of(2009, 11, 11))
                .discontinued(LocalDate.of(2008, 11, 11))
                .build();
        exception.expect(ComputerServiceException.class);
        service.updateComputer(computer);
    }

    /**
     * Test if updating a computer with a nonexistent company fail.
     * @throws ComputerServiceException  If the computer update fail
     */
    @Test
    public void updateComputerWithUnknownManufacturer() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .manufacturer(new Company(2L, "nonexistent company"))
                .build();

        Mockito.when(companyService.getOneCompany(2L)).thenReturn(Optional.empty());

        exception.expect(ComputerServiceException.class);
        service.updateComputer(computer);
    }

    /**
     * Test if a regular computer update works.
     * @throws ComputerServiceException  If update fail
     */
    @Test
    public void updateValidComputer() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .build();

        try {
            Mockito.when(computerDAO.update(computer)).thenReturn(computer);

            Optional<Computer> computerUpdatedOptional = service.updateComputer(computer);
            if (computerUpdatedOptional.isPresent()) {
                Computer computerUpdated = computerUpdatedOptional.get();
                assertEquals(computerUpdated.getId(), computer.getId());
                assertTrue(computerUpdated.getName().equals(computer.getName()));
            }
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if a computer is not updated when a SQL Exception is raised.
     * @throws ComputerServiceException  If there is a problem with the computer update inside the service method
     */
    @Test
    public void updateComputerWithSQLException() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .build();

        try {
            Mockito.when(computerDAO.update(computer)).thenThrow(SQLException.class);

            Optional<Computer> computerUpdated = service.updateComputer(computer);
            assertFalse(computerUpdated.isPresent());
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the deletion of an existing computer works.
     */
    @Test
    public void deleteComputer() {
        try {
            Mockito.when(computerDAO.delete(1L)).thenReturn(true);

            boolean resultDelete = service.deleteComputer(1L);
            assertTrue(resultDelete);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the deletion of an non existing computer works.
     */
    @Test
    public void deleteUnknownComputer() {
        try {
            Mockito.when(computerDAO.delete(1L)).thenReturn(false);

            boolean resultDelete = service.deleteComputer(1L);
            assertFalse(resultDelete);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the deletion of an existing computer works with a SQLException.
     */
    @Test
    public void deleteComputerWithSQLException() {
        try {
            Mockito.when(computerDAO.delete(1L)).thenThrow(SQLException.class);

            boolean resultDelete = service.deleteComputer(1L);
            assertFalse(resultDelete);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the computer count works.
     */
    @Test
    public void countComputer() {
        try {
            Mockito.when(computerDAO.count()).thenReturn(11);

            int count = service.countComputers();
            assertEquals(11, count);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the computer count works when a SQL exception is raise.
     */
    @Test
    public void countComputerWithSQLException() {
        try {
            Mockito.when(computerDAO.count()).thenThrow(SQLException.class);

            int count = service.countComputers();
            assertEquals(-1, count);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }
}
