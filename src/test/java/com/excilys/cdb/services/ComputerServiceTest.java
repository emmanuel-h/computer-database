package com.excilys.cdb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        Mockito.when(computerDAO.findAllWithPaging(1, 10)).thenReturn(computers);

        Optional<Page<Computer>> resultsPageOptional = service.getAllComputersWithPaging(1, 10);
        if (resultsPageOptional.isPresent()) {
            Page<Computer> resultsPage = resultsPageOptional.get();
            assertTrue(resultsPage.getResults().get(0).equals(computer));
        }
        Mockito.verify(computerDAO).findAllWithPaging(1, 10);
    }

    /**
     * Test if the getAllComputers method work correctly when an invalid results per page is asked.
     */
    @Test
    public void getAllComputersWithInvalidResultsPerPage() {
        Mockito.when(computerDAO.findAllWithPaging(1, -1)).thenReturn(null);
        Optional<Page<Computer>> resultsPageNegative = service.getAllComputersWithPaging(1, -1);
        assertFalse(resultsPageNegative.isPresent());
        Mockito.verify(computerDAO).findAllWithPaging(1, -1);
    }

    /**
     * Test if the getAllComputers method work correctly when an invalid page is asked.
     */
    @Test
    public void getAllComputersWithInvalidPageNumber() {
        Mockito.when(computerDAO.findAllWithPaging(-1, 10)).thenReturn(null);
        Optional<Page<Computer>> resultsPageNegative = service.getAllComputersWithPaging(-1, 10);
        assertFalse(resultsPageNegative.isPresent());
        Mockito.verify(computerDAO).findAllWithPaging(-1, 10);
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
        Mockito.when(computerDAO.add(computer)).thenReturn(computer.getId());
        long computerCreatedId = service.createComputer(computer);
        assertEquals(computerCreatedId, 1L);
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
        Mockito.when(computerDAO.add(computer)).thenReturn(1L);
        long id = service.createComputer(computer);
        assertEquals(id, 1L);
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
        Mockito.when(computerDAO.update(computer)).thenReturn(computer);
        Optional<Computer> computerUpdatedOptional = service.updateComputer(computer);
        if (computerUpdatedOptional.isPresent()) {
            Computer computerUpdated = computerUpdatedOptional.get();
            assertEquals(computerUpdated.getId(), computer.getId());
            assertTrue(computerUpdated.getName().equals(computer.getName()));
        }
    }

    /**
     * Test if the deletion of an existing computer works.
     */
    @Test
    public void deleteComputer() {
        Mockito.when(computerDAO.delete(1L)).thenReturn(true);
        boolean resultDelete = service.deleteComputer(1L);
        assertTrue(resultDelete);
    }

    /**
     * Test if the deletion of an non existing computer works.
     */
    @Test
    public void deleteUnknownComputer() {
        Mockito.when(computerDAO.delete(1L)).thenReturn(false);
        boolean resultDelete = service.deleteComputer(1L);
        assertFalse(resultDelete);
    }

    /**
     * Test if the computer count works.
     */
    @Test
    public void countComputer() {
        Mockito.when(computerDAO.count()).thenReturn(11);
        int count = service.countComputers();
        assertEquals(11, count);
    }

    /**
     * Test if searching a valid computer works.
     * @throws ComputerServiceException If the search fails
     */
    @Test
    public void getOneComputerPresent() throws ComputerServiceException {
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .build();
        Mockito.when(computerDAO.findById(1L)).thenReturn(Optional.of(computer));
        Computer computerToTest = service.getOneComputer(1L);
        assertEquals(computerToTest, computer);
    }

    /**
     * Test if searching an invalid computer doesn't work.
     * @throws ComputerServiceException If the computer is not found
     */
    @Test
    public void getOneComputerNotPresent() throws ComputerServiceException {
        Mockito.when(computerDAO.findById(1L)).thenReturn(Optional.empty());
        exception.expect(ComputerServiceException.class);
        service.getOneComputer(1L);
    }

    /**
     * Test if regular multiple deletion works.
     */
    @Test
    public void deleteMultipleComputers() {
        Mockito.when(computerDAO.deleteMultiple("(1,2)")).thenReturn(true);
        boolean test = service.deleteMultipleComputers("(1,2)");
        assertTrue(test);
    }

    /**
     * Test if invalid multiple deletion doesn't works.
     */
    @Test
    public void deleteMultipleComputersWithInvalidString() {
        boolean test = service.deleteMultipleComputers("1,2)");
        assertFalse(test);
    }

    /**
     * Test if searching a computer works.
     */
    @Test
    public void searchComputer() {
        List<Computer> computers = new ArrayList<>();
        computers.add(new Computer.Builder("Test search").build());
        Page<Computer> page = new Page<>();
        page.setCurrentPage(1);
        page.setMaxPage(2);
        page.setResultsPerPage(10);
        Mockito.when(computerDAO.searchComputer("pp", 1, 10)).thenReturn(page);
        Optional<Page<Computer>> pageToTest = service.searchComputer("pp", 1, 10);
        assertTrue(pageToTest.isPresent());
        assertEquals(pageToTest.get(), page);
    }

    /**
     * Test if counting results of a search works.
     */
    @Test
    public void countSearchedComputers() {
        Mockito.when(computerDAO.countSearchedComputers("pp")).thenReturn(12);
        int test = service.countSearchedComputers("pp");
        assertEquals(test, 12);
    }

}
