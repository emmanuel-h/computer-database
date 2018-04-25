package com.excilys.cdb.utils;

import java.time.LocalDate;

import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

public class ComputerConvertor {

    /**
     * Map a Computer into a ComputerDTO.
     * @param computer  The computer to transform
     * @return          The generated ComputerDTO.
     */
    public static ComputerDTO computerToDTO(Computer computer) {
        ComputerDTO computerDTO = new ComputerDTO(computer);
        return computerDTO;
    }

    /**
     * Map a ComputerDTO into a Computer.
     * @param computerDTO   The computerDTO to transform
     * @return              The generated Computer
     */
    public static Computer dTOToComputer(ComputerDTO computerDTO) {
        Computer computer = new Computer.Builder(computerDTO.getName())
                .id(computerDTO.getId())
                .build();
        if (null == computerDTO.getIntroduced()) {
            computer.setIntroduced(null);
        } else {
            LocalDate introduced = LocalDate.parse(computerDTO.getIntroduced());
            computer.setIntroduced(introduced);
        }
        if (null == computerDTO.getDiscontinued()) {
            computer.setDiscontinued(null);
        } else {
            LocalDate discontinued = LocalDate.parse(computerDTO.getDiscontinued());
            computer.setDiscontinued(discontinued);
        }
        if (null == computerDTO.getManufacturer()) {
            computer.setManufacturer(null);
        } else {
            Company company = new Company();
            company.setName(computerDTO.getManufacturer());
            computer.setManufacturer(company);
        }
        return computer;
    }
}
