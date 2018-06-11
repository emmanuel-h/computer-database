package com.excilys.cdb.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
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

import com.excilys.cdb.Page;
import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.exceptions.company.CompanyUnknownException;
import com.excilys.cdb.exceptions.computer.ComputerException;
import com.excilys.cdb.mappers.ComputerConvertor;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.ComputerService;

@RestController
@RequestMapping("/computer")
public class ComputerController {

	private ComputerService computerService;

	public ComputerController(ComputerService computerService) {
		this.computerService = computerService;
	}

	@GetMapping
	public ResponseEntity<Collection<ComputerDTO>> listComputer(@RequestParam(name = "page")int page,
			@RequestParam(name = "results")int results) {
		Optional<Page<Computer>> pageOptional = computerService.getAllComputersWithPaging(page, results);
		if(!pageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Collection<Computer> computers = pageOptional.get().getResults();
		List<ComputerDTO> computerDTOs = new ArrayList<>();
		for(Computer computer: computers) {
			computerDTOs.add(ComputerConvertor.toDTO(computer));
		}
		return new ResponseEntity<>(computerDTOs, HttpStatus.OK);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ComputerDTO> getComputer(@PathVariable("id") long id) {
		try {
			Computer computer = computerService.getOneComputer(id);
			return new ResponseEntity<>(ComputerConvertor.toDTO(computer), HttpStatus.OK);
		} catch (ComputerException e) {
			return ResponseEntity.notFound().build();
		}
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
		System.out.println(computer);
		try {
			Optional<Computer> computerUpdatedOptional = computerService.updateComputer(computer);
			if(!computerUpdatedOptional.isPresent()) {
				return ResponseEntity.notFound().build();
			}
			ComputerDTO computerDTOUpdated = ComputerConvertor.toDTO(computerUpdatedOptional.get());
			return new ResponseEntity<>(computerDTOUpdated, HttpStatus.OK);
		} catch (ComputerException | CompanyUnknownException e) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteComputer(@PathVariable("id") long id) {
		boolean deleteSuccess = computerService.deleteComputer(id);
		if(!deleteSuccess) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().build();
		}
	}
	
	@GetMapping(value = "/count")
	public ResponseEntity<Integer> countComputers(){
		return new ResponseEntity<>(computerService.countComputers(), HttpStatus.OK);
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
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().build();
		}
	}
	
	@GetMapping(params = "search")
	public ResponseEntity<Collection<ComputerDTO>> searchComputers(@RequestParam("search") String search,
			@RequestParam(name = "page")int page, @RequestParam(name = "results")int results) {
		Optional<Page<Computer>> pageOptional = computerService.searchComputer(search, page, results);
		if(!pageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Collection<Computer> computers = pageOptional.get().getResults();
		List<ComputerDTO> computerDTOs = new ArrayList<>();
		for(Computer computer: computers) {
			computerDTOs.add(ComputerConvertor.toDTO(computer));
		}
		return new ResponseEntity<>(computerDTOs, HttpStatus.OK);
	}
	
	@GetMapping(value = "/count", params = "search")
	public ResponseEntity<Integer> countSearchComputers(@RequestParam("search") String search){
		return new ResponseEntity<>(computerService.countSearchedComputers(search), HttpStatus.OK);
	}

}
