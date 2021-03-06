package com.excilys.cdb.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.cdb.Page;
import com.excilys.cdb.controllers.exception.NoContentFoundException;
import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.exceptions.company.CompanyUnknownException;
import com.excilys.cdb.exceptions.computer.ComputerException;
import com.excilys.cdb.mappers.ComputerConvertor;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.ComputerService;

@RestController
@RequestMapping("/computer")
@CrossOrigin(origins = "*", allowCredentials="true", allowedHeaders= {"x-auth-token","x-requested-with","x-xsrf-token","X-Requested-With","Content-Type"})
public class ComputerController {

    private static final String NO_RESULTS_FOUND = "No results found";
    private ComputerService computerService;

    public ComputerController(ComputerService computerService) {
        this.computerService = computerService;
    }

    //	@PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<List<ComputerDTO>> listComputer(@RequestParam("page")int page,
            @RequestParam("results")int results) throws NoContentFoundException {
        Optional<Page<Computer>> pageOptional = computerService.getAllComputersWithPaging(page, results);
        Collection<Computer> computers = pageOptional.orElseThrow(() -> new NoContentFoundException(NO_RESULTS_FOUND)).getResults();
        Page<ComputerDTO>  pageComputer = new Page<>(pageOptional.get());
        pageComputer.setResults(computers.stream().map(computer -> ComputerConvertor.toDTO(computer)).collect(Collectors.toList()));
        return ResponseEntity.ok(pageComputer.getResults());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ComputerDTO> getComputer(@PathVariable("id") long id) {
        try {
            Computer computer = computerService.getOneComputer(id);
            return ResponseEntity.ok(ComputerConvertor.toDTO(computer));
        } catch (ComputerException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/count")   
    public ResponseEntity<Integer> countComputers(){
        return ResponseEntity.ok(computerService.countComputers());
    }

    @GetMapping(params = "search")
    public ResponseEntity<List<ComputerDTO>> searchComputers(@RequestParam("search") String search,
            @RequestParam("page")int page, @RequestParam("results")int results,
            @RequestParam("searchByComputerName") final boolean searchByComputerName) throws NoContentFoundException {
        Optional<Page<Computer>> pageOptional = computerService.searchComputer(search, page, results, searchByComputerName);
        List<Computer> computers = pageOptional.orElseThrow(() -> new NoContentFoundException(NO_RESULTS_FOUND)).getResults();
        List<ComputerDTO> computerDTOs = computers.stream().map(computer -> ComputerConvertor.toDTO(computer)).collect(Collectors.toList());
        return ResponseEntity.ok(computerDTOs);
    }

    @GetMapping(value = "/count", params = "search")
    public ResponseEntity<Integer> countSearchComputers(@RequestParam("search") String search,
    		@RequestParam("searchByComputerName") final boolean searchByComputerName){
        return ResponseEntity.ok(computerService.countSearchedComputers(search, searchByComputerName));
    }

    @PostMapping
    public ResponseEntity<Long> addComputer(@RequestBody ComputerDTO computerDTO){
        Computer computer = ComputerConvertor.fromDTO(computerDTO);
        try {
            long id = computerService.createComputer(computer);
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } catch (ComputerException | CompanyUnknownException e) {
            return new ResponseEntity<>(-1L, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ComputerDTO> updateComputer(@PathVariable("id") long id, @RequestBody ComputerDTO computerDTO) {
        Computer computer = ComputerConvertor.fromDTO(computerDTO);
        try {
            Optional<Computer> computerUpdatedOptional = computerService.updateComputer(computer);
            if(!computerUpdatedOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            ComputerDTO computerDTOUpdated = ComputerConvertor.toDTO(computerUpdatedOptional.get());
            return new ResponseEntity<>(computerDTOUpdated, HttpStatus.OK);
        } catch (ComputerException | CompanyUnknownException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteComputer(@PathVariable("id") long id) {
        boolean deleteSuccess;
		try {
			deleteSuccess = computerService.deleteComputer(id);
		} catch (CompanyUnknownException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
        if(!deleteSuccess) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void>deleteMultipleComputers(@RequestBody Collection<Long> toDeleteIds) {
        StringBuilder stringBuilder = new StringBuilder("(");
        for(long id: toDeleteIds) {
            stringBuilder.append(id);
            stringBuilder.append(',');
        }
        stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
        stringBuilder.append(")");
        boolean deleteSuccess = computerService.deleteMultipleComputers(stringBuilder.toString());
        if(!deleteSuccess) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
    
    @GetMapping("/sort")
    public ResponseEntity<List<ComputerDTO>> findAllWithPagingAndSorting(
    		@RequestParam("sort") final String sort, @RequestParam("page") final int page,
    		@RequestParam("results") final int results, @RequestParam("asc") final boolean asc){
		Optional<Page<Computer>> pageOptional = computerService.findAllWithPagingAndSorting(page, results, sort, asc);
		if(!pageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		List<ComputerDTO> computerDTOs = pageOptional.get().getResults().stream().map(
				computer -> ComputerConvertor.toDTO(computer)).collect(Collectors.toList());
		return ResponseEntity.ok(computerDTOs);
    }
    
    @GetMapping(value = "/sort", params = "search")
    public ResponseEntity<List<ComputerDTO>> findAllWithPagingAndSortingAndSearch(@RequestParam("search") final String search,
    		@RequestParam("sort") final String sort, @RequestParam("page") final int page,
    		@RequestParam("results") final int results, @RequestParam("asc") final boolean asc,
    		@RequestParam("searchByComputerName") final boolean searchByComputerName){
		Optional<Page<Computer>> pageOptional = computerService.findAllWithPagingAndSortingAndSearch(
				search, page, results, sort, asc, searchByComputerName);
		if(!pageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		List<ComputerDTO> computerDTOs = pageOptional.get().getResults().stream().map(
				computer -> ComputerConvertor.toDTO(computer)).collect(Collectors.toList());
		return ResponseEntity.ok(computerDTOs);
    }
}
