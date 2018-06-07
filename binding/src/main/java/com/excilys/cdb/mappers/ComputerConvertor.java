package com.excilys.cdb.mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

public class ComputerConvertor {

    /**
     * Map a Computer into a ComputerDTO.
     * @param computer  The computer to transform
     * @return          The generated ComputerDTO.
     */
    public static ComputerDTO toDTO(Computer computer) {
        ComputerDTO computerDTO = new ComputerDTO(computer);
        return computerDTO;
    }

    /**
     * Map a ComputerDTO into a Computer.
     * @param computerDTO   The computerDTO to transform
     * @return              The generated Computer
     */
    public static Computer fromDTO(ComputerDTO computerDTO) {
        Computer computer = new Computer.Builder(computerDTO.getName())
                .id(computerDTO.getId())
                .build();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (null == computerDTO.getIntroduced() || computerDTO.getIntroduced().trim().isEmpty()) {
            computer.setIntroduced(null);
        } else {
            LocalDate introduced = LocalDate.parse(computerDTO.getIntroduced(), formatter);
            computer.setIntroduced(introduced);
        }
        if (null == computerDTO.getDiscontinued() || computerDTO.getDiscontinued().trim().isEmpty()) {
            computer.setDiscontinued(null);
        } else {
            LocalDate discontinued = LocalDate.parse(computerDTO.getDiscontinued(), formatter);
            computer.setDiscontinued(discontinued);
        }
        if (null == computerDTO.getManufacturer()) {
            computer.setManufacturer(null);
        } else {
            Company company = new Company();
            company.setName(computerDTO.getManufacturer());
            company.setId(computerDTO.getManufacturer_id());
            computer.setManufacturer(company);
        }
        return computer;
    }
}
