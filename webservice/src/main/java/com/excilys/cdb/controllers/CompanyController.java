package com.excilys.cdb.controllers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.cdb.Page;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.services.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {
	
	private CompanyService companyService;
	
	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@GetMapping(params = {"page", "results"})
	public ResponseEntity<Collection<Company>> listCompaniesWithPaging(@RequestParam(name = "page")int page,
			@RequestParam(name = "results")int results) {
		Optional<Page<Company>> pageOptional = companyService.getAllCompaniesWithPaging(page, results);
		if(!pageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return new ResponseEntity<>(pageOptional.get().getResults(), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<Collection<Company>> listAllCompanies() {
		return new ResponseEntity<>(companyService.findAllCompanies(), HttpStatus.OK);
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
	
	@GetMapping("/{id}")
	public ResponseEntity<Company> getCompany(@PathVariable("id") long id) {
		Optional<Company> company = companyService.getOneCompany(id);
		if(!company.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {

			return new ResponseEntity<>(company.get(), HttpStatus.OK);
		}
	}

}
