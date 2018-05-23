package com.excilys.cdb.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.CompanyService;
import com.excilys.cdb.services.ComputerService;
import com.excilys.cdb.utils.ComputerConvertor;
import com.excilys.cdb.utils.Page;
import com.excilys.cdb.validators.ComputersToDeleteValidator;

@Controller
public class ComputerController {

    private ComputerService computerService;
    private CompanyService companyService;

    private static final String RESULTS_PER_PAGE = "10";
    private static final String CURRENT_PAGE = "1";

    /**
     * Initialize the services.
     * @param computerService   The computer service
     * @param companyService    The company service
     */
    @Autowired
    public ComputerController(ComputerService computerService, CompanyService companyService) {
        this.computerService = computerService;
        this.companyService = companyService;
    }

    /**
     * Display the original dashboard with paging.
     * @param currentPage      The page to display
     * @param resultsPerPage   The number of results per page to display
     * @return          The ModelAndView completed
     */
    @GetMapping(value = {"/", "/dashboard"})
    public ModelAndView dashboard(
            @RequestParam(value = "page", defaultValue = CURRENT_PAGE) int currentPage,
            @RequestParam(value = "results", defaultValue = RESULTS_PER_PAGE) int resultsPerPage) {
        int numberOfComputers = computerService.countComputers();

        if (currentPage * resultsPerPage > numberOfComputers) {
            currentPage = (int) Math.ceil((double) numberOfComputers / (double) resultsPerPage);
        }

        Optional<Page<Computer>> pageOptional = computerService.getAllComputersWithPaging(currentPage, resultsPerPage);

        List<ComputerDTO> computerList = new ArrayList<>();
        Page<Computer> pageComputer = pageOptional.get();
        for (Computer computer : pageComputer.getResults()) {
            computerList.add(ComputerConvertor.toDTO(computer));
        }
        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("computerList", computerList);
        modelAndView.addObject("nbComputers", numberOfComputers);
        modelAndView.addObject("results", resultsPerPage);
        modelAndView.addObject("maxPage", pageComputer.getMaxPage());
        modelAndView.addObject("page", pageComputer.getCurrentPage());

        return modelAndView;
    }

    /**
     * Display the dashboard with the result of a paginated search.
     * @param currentPage      The page to display
     * @param resultsPerPage   The number of results per page to display
     * @param search    The search asked by the user
     * @return          The ModelAndView completed
     */
    @GetMapping(value = {"/dashboard"}, params = {"search"})
    public ModelAndView dashboardWithSearch(
            @RequestParam(value = "search") String search,
            @RequestParam(value = "page", defaultValue = CURRENT_PAGE) int currentPage,
            @RequestParam(value = "results", defaultValue = RESULTS_PER_PAGE) int resultsPerPage) {
        int numberOfComputers = computerService.countSearchedComputers(search);

        if (currentPage * resultsPerPage > numberOfComputers) {
            currentPage = (int) Math.ceil((double) numberOfComputers / (double) resultsPerPage);
        }

        Optional<Page<Computer>> pageOptional = computerService.searchComputer(search, currentPage, resultsPerPage);

        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("search", search);

        List<ComputerDTO> computerList = new ArrayList<>();
        if (pageOptional.isPresent()) {
            Page<Computer> pageComputer = pageOptional.get();
            for (Computer computer : pageComputer.getResults()) {
                computerList.add(ComputerConvertor.toDTO(computer));
            }
            modelAndView.addObject("nbComputers", numberOfComputers);
            modelAndView.addObject("computerList", computerList);
            modelAndView.addObject("results", pageComputer.getResultsPerPage());
            modelAndView.addObject("maxPage", pageComputer.getMaxPage());
            modelAndView.addObject("page", pageComputer.getCurrentPage());
        } else {
            modelAndView.addObject("nbComputers", numberOfComputers);
            modelAndView.addObject("computerList", computerList);
            modelAndView.addObject("results", resultsPerPage);
            modelAndView.addObject("maxPage", 1);
            modelAndView.addObject("page", 1);
        }
        return modelAndView;
    }

    /**
     * Delete the selected computers.
     * @param selection The computers identifiers
     * @return          The modelAndView
     */
    @PostMapping("/deleteComputers")
    public ModelAndView deleteComputers(@RequestBody String selection) {
        selection = selection.replace("%2C", ",");
        selection = selection.replace("selection=", "");
        StringBuilder stringBuilder = new StringBuilder(selection);
        stringBuilder.insert(0, "(");
        stringBuilder.append(")");
        if (ComputersToDeleteValidator.verifyListToDelete(stringBuilder.toString())) {
            computerService.deleteMultipleComputers(stringBuilder.toString());
        }
        return dashboard(Integer.parseInt(CURRENT_PAGE), Integer.parseInt(RESULTS_PER_PAGE));
    }

}
