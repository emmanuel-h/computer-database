package com.excilys.cdb.controllers.jspControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private MessageSource messageSource;

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
     * @param currentPage       The page to display
     * @param resultsPerPage    The number of results per page to display
     * @param locale            The user locale
     * @return                  The ModelAndView completed
     */
    @GetMapping(value = {"/", "/dashboard"})
    public ModelAndView dashboard(
            @RequestParam(value = "page", defaultValue = CURRENT_PAGE) int currentPage,
            @RequestParam(value = "results", defaultValue = RESULTS_PER_PAGE) int resultsPerPage,
            Locale locale) {
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
     * @param currentPage       The page to display
     * @param resultsPerPage    The number of results per page to display
     * @param search            The search asked by the user
     * @param locale            The user locale
     * @return                  The ModelAndView completed
     */
    @GetMapping(value = {"/dashboard"}, params = {"search"})
    public ModelAndView dashboardWithSearch(
            @RequestParam(value = "search") String search,
            @RequestParam(value = "page", defaultValue = CURRENT_PAGE) int currentPage,
            @RequestParam(value = "results", defaultValue = RESULTS_PER_PAGE) int resultsPerPage,
            Locale locale) {
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
     * @param locale    The user locale
     * @return          The modelAndView
     */
    @PostMapping("/deleteComputers")
    public String deleteComputers(@RequestBody String selection,
            Locale locale) {
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
     * @param locale    The user locale
     * @return          The ModelAndView
     */
    @GetMapping("/addComputer")
    public ModelAndView displayAddComputer(Locale locale) {
        final List<Company> companies = companyService.findAllCompanies();
        ModelAndView modelAndView = new ModelAndView("addComputer");
        modelAndView.addObject("computer", new ComputerDTO());
        modelAndView.addObject("companies", companies);
        return modelAndView;
    }

    /**
     * Display the page allowing the create a computer.
     * @param computerDTO   The computer to validate
     * @param locale        The user locale
     * @param bindingResult The binding result
     * @return              The redirection
     */
    @PostMapping("/createComputer")
    public String addComputer(@ModelAttribute("computer") @Valid ComputerDTO computerDTO, Locale locale,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/400";
        }
        final List<Company> companies = companyService.findAllCompanies();
        Computer computer = ComputerConvertor.fromDTO(computerDTO);
        Company company = companies.stream()
                .filter(c -> c.getId() == Long.parseLong(computerDTO.getManufacturer()))
                .findFirst()
                .orElse(null);
        computer.setManufacturer(company);
        try {
            final long idNewComputer = computerService.createComputer(computer);
            message = messageSource.getMessage("dashboard.message.computerCreated", new Object[] {idNewComputer}, locale);
        } catch (ComputerServiceException e) {
            LOGGER.warn(e.toString());
            message = messageSource.getMessage("dashboard.message.computerNotCreated", new Object[] {e.getMessage()}, locale);
        }
        return "redirect:/dashboard";
    }

    /**
     * Display the page to edit a computer.
     * @param id        The computer's identifier
     * @param locale    The user locale
     * @param model     The model
     * @return          The ModelAndView
     */
    @GetMapping(value = "/editComputer", params = {"id"})
    public String displayEditComputer(@RequestParam(value = "id") long id, Locale locale, ModelMap model) {
        final List<Company> companies = companyService.findAllCompanies();
        Computer computerFull;
        try {
            computerFull = computerService.getOneComputer(id);
            ComputerDTO computer = ComputerConvertor.toDTO(computerFull);
            model.addAttribute("computer", computer);
            if (null != computerFull.getManufacturer()) {
                model.addAttribute("companyId", computerFull.getManufacturer().getId());
            } else {
                model.addAttribute("companyId", -1L);
            }
            model.addAttribute("companies", companies);
        } catch (ComputerServiceException e) {
            message = messageSource.getMessage("dashboard.message.badId", null, locale);
            return "redirect:/dashboard";
        }
        return "editComputer";
    }

    /**
     * Save the modification of a computer.
     * @param computerDTO   The computer send by the jsp
     * @param locale        The user locale
     * @param bindingResult The BindingResult
     * @return              The redirection
     */
    @PostMapping(value = "/editComputerAction", params = {"id"})
    public String saveEditComputer(@ModelAttribute("computer") @Valid ComputerDTO computerDTO,
            Locale locale, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/400";
        }
        final List<Company> companies = companyService.findAllCompanies();
        Company company = companies.stream()
                .filter(c -> c.getId() == Long.parseLong(computerDTO.getManufacturer()))
                .findFirst()
                .orElse(null);

        Computer computer = ComputerConvertor.fromDTO(computerDTO);
        computer.setManufacturer(company);
        try {
            Optional<Computer> computer2 = computerService.updateComputer(computer);
            message = messageSource.getMessage("dashboard.message.computerModified", new Object[] {computer2.get().getId()}, locale);
        } catch (ComputerServiceException e) {
            message = messageSource.getMessage("dashboard.message.updateFailed", new Object[] {null}, locale);
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
