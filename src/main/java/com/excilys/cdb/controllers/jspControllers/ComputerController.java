package com.excilys.cdb.controllers.jspControllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.exceptions.ComputerServiceException;
import com.excilys.cdb.model.Company;
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
    private String message = null;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(ComputerController.class);

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
        constructModelAndView(modelAndView, numberOfComputers, computerList, resultsPerPage,
                pageComputer.getMaxPage(), pageComputer.getCurrentPage());
        modelAndView.addObject("message", message);
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
            constructModelAndView(modelAndView, numberOfComputers, computerList, pageComputer.getResultsPerPage(),
                    pageComputer.getMaxPage(), pageComputer.getCurrentPage());
        } else {
            constructModelAndView(modelAndView, numberOfComputers, computerList, resultsPerPage, 1, 1);
        }
        return modelAndView;
    }

    /**
     * Delete the selected computers.
     * @param selection The computers identifiers
     * @return          The modelAndView
     */
    @PostMapping("/deleteComputers")
    public String deleteComputers(@RequestBody String selection) {
        selection = selection.replace("%2C", ",");
        selection = selection.replace("selection=", "");
        StringBuilder stringBuilder = new StringBuilder(selection);
        stringBuilder.insert(0, "(");
        stringBuilder.append(")");
        if (ComputersToDeleteValidator.verifyListToDelete(stringBuilder.toString())) {
            computerService.deleteMultipleComputers(stringBuilder.toString());
        }
        return "redirect:/dashboard";
    }

    /**
     * Display the page allowing the create a computer.
     * @return  THe ModelAndView
     */
    @GetMapping("/addComputer")
    public ModelAndView displayAddComputer() {
        final List<Company> companies = companyService.findAllCompanies();
        ModelAndView modelAndView = new ModelAndView("addComputer");
        modelAndView.addObject("companies", companies);
        return modelAndView;
    }

    /**
     * Display the page allowing the create a computer.
     * @param request   The web request
     * @return          The redirection
     */
    @PostMapping("/createComputer")
    public String addComputer(WebRequest request) {
        final List<Company> companies = companyService.findAllCompanies();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String name = request.getParameter("name");
        String introduced = request.getParameter("introduced");
        String discontinued = request.getParameter("discontinued");
        LocalDate introducedDate =  introduced == null || introduced.trim().isEmpty() ? null : LocalDate.parse(introduced, formatter);
        LocalDate discontinuedDate = discontinued == null || discontinued.trim().isEmpty() ? null : LocalDate.parse(discontinued, formatter);
        long companyId = request.getParameter("company") != null ? Long.parseLong(request.getParameter("company")) : -1;
        Company company = companies.stream()
                .filter(c -> c.getId() == companyId)
                .findFirst()
                .orElse(null);

        Computer computer = new Computer.Builder(name)
                .introduced(introducedDate)
                .discontinued(discontinuedDate)
                .manufacturer(company)
                .build();
        try {
            final long idNewComputer = computerService.createComputer(computer);
            message = "Computer created with id " + idNewComputer;
        } catch (ComputerServiceException e) {
            LOGGER.warn(e.getMessage());
            message = "Computer has not been created : " + e.getMessage();
        }
        return "redirect:/dashboard";
    }

    /**
     * Display the page to edit a computer.
     * @param id    The computer's identifier
     * @return      The ModelAndView
     */
    @GetMapping(value = "/editComputer", params = {"id"})
    public ModelAndView displayEditComputer(@RequestParam(value = "id") long id) {
        final List<Company> companies = companyService.findAllCompanies();
        ModelAndView modelAndView = new ModelAndView("editComputer");
        Computer computerFull;
        try {
            computerFull = computerService.getOneComputer(id);
            ComputerDTO computer = ComputerConvertor.toDTO(computerFull);
            modelAndView.addObject("computer", computer);
            if (null != computerFull.getManufacturer()) {
                modelAndView.addObject("companyId", computerFull.getManufacturer().getId());
            } else {
                modelAndView.addObject("companyId", -1L);
            }
            modelAndView.addObject("companies", companies);
        } catch (ComputerServiceException e) {
            message = "Identifier is not valid";
        }
        return modelAndView;
    }

    /**
     * Save the modification of a computer.
     * @param request   The web request
     * @return          The redirection
     */
    @PostMapping(value = "/editComputerAction", params = {"id"})
    public String saveEditComputer(WebRequest request) {
        final List<Company> companies = companyService.findAllCompanies();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        long id = Long.parseLong(request.getParameter("id"));
        String name = request.getParameter("computerName");
        String introduced = request.getParameter("introduced");
        String discontinued = request.getParameter("discontinued");
        LocalDate introducedDate =  introduced == null || introduced.trim().isEmpty() ? null : LocalDate.parse(introduced, formatter);
        LocalDate discontinuedDate = discontinued == null || discontinued.trim().isEmpty() ? null : LocalDate.parse(discontinued, formatter);
        long companyId = Long.parseLong(request.getParameter("companyId"));
        Company company = companies.stream()
                .filter(c -> c.getId() == companyId)
                .findFirst()
                .orElse(null);

        Computer computer = new Computer.Builder(name)
                .id(id)
                .introduced(introducedDate)
                .discontinued(discontinuedDate)
                .manufacturer(company)
                .build();
        try {
            Optional<Computer> computer2 = computerService.updateComputer(computer);
            message = "Computer " + computer2.get().getId() + " succesfully modified";
        } catch (ComputerServiceException e) {
            message = "Update not applied";
        }
        return "redirect:/dashboard";
    }

    /**
     * Fill a ModelAndView with the desired parameters.
     * @param modelAndView      The ModelAndView to fill
     * @param numberOfComputers The total count of computers
     * @param computerList      The list of computers to display
     * @param resultsPerPage    The number of computers to display per page
     * @param maxPage           The maximum page number
     * @param currentPage       The current page
     */
    private void constructModelAndView(ModelAndView modelAndView,
            int numberOfComputers, List<ComputerDTO> computerList, int resultsPerPage,
            int maxPage, int currentPage) {
        modelAndView.addObject("nbComputers", numberOfComputers);
        modelAndView.addObject("computerList", computerList);
        modelAndView.addObject("results", resultsPerPage);
        modelAndView.addObject("maxPage", maxPage);
        modelAndView.addObject("page", currentPage);
    }

}
