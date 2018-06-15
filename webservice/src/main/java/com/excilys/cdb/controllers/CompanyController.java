package com.excilys.cdb.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.excilys.cdb.Page;
import com.excilys.cdb.controllers.exception.CompanyUpdateNotExistingException;
import com.excilys.cdb.controllers.exception.ConflictUpdateException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.services.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {
	
	private static final String COMPANY_NOT_EXIST_CANNOT_BE_UPDATED = "The company does not exist, cannot be updated";
    private static final String CONFLICT_UPDATE = "The company cannot be updated at this url";
    private CompanyService companyService;
	
	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	/**
	 * Get the companies by pages.
	 * @param page     Page requested
	 * @param results  Number of results requested
	 * @return 
	 */
	@GetMapping(params = {"page", "results"})
	public ResponseEntity<Page<Company>> listCompaniesWithPaging(@RequestParam(name = "page")int page,
			@RequestParam(name = "results")int results) {
		Optional<Page<Company>> pageOptional = companyService.getAllCompaniesWithPaging(page, results);
		if(!pageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return new ResponseEntity<>(pageOptional.get(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Company> getCompany(@PathVariable("id") long id) {
	    Optional<Company> company = companyService.getOneCompany(id);
	    if(!company.isPresent()) {
	        return ResponseEntity.notFound().build();
	    } else {
	        return new ResponseEntity<>(company.get(), HttpStatus.OK);
	    }
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
		boolean success = companyService.deleteCompany(id);
		if(!success) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().build();
		}
	}
	
	@PostMapping(consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Company> addCompany(@Valid @RequestBody final Company company,final UriComponentsBuilder ucb){
        final Long id = companyService.addCompany(company);
        return ResponseEntity.created(ucb.path("/{id}").buildAndExpand(id).toUri()).build();
	 
	}
	
	@PutMapping(path="/{id}",consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,produces=MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE )
	public ResponseEntity<Company> updateCompany(@Valid @RequestBody final Company company,@PathVariable final Long id) throws ConflictUpdateException, CompanyUpdateNotExistingException{
	   if(!id.equals(company.getId())) {
	       throw new ConflictUpdateException(CONFLICT_UPDATE);
	   }
	   final Company companyUpdated = companyService.updateCompany(company);
	   if(companyUpdated == null) {
	       throw new CompanyUpdateNotExistingException(COMPANY_NOT_EXIST_CANNOT_BE_UPDATED);
	   }
	   return ResponseEntity.ok(companyService.updateCompany(company));
	}

}
