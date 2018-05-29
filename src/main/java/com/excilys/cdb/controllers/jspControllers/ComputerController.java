package com.excilys.cdb.controllers.jspControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private MessageSource messageSource;
    private ComputerService computerService;
    private CompanyService companyService;

    private static final String RESULTS_PER_PAGE = "10";
    private static final String CURRENT_PAGE = "1";

    // Arguments
    private static final String DASHBOARD = "dashboard";
    private static final String PAGE = "page";
    private static final String MAX_PAGE = "maxPage";
    private static final String RESULTS = "results";
    private static final String NB_COMPUTERS = "nbComputers";
    private static final String COMPUTER_LIST = "computerList";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_TYPE = "messageType";
    private static final String SEARCH = "search";
    private static final String CREATE_COMPUTER = "createComputer";

    // Actions
    private static final String DELETE_COMPUTERS = "deleteComputers";
    private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    private static final String ADD_COMPUTER = "addComputer";
    private static final String EDIT_COMPUTER = "editComputer";
    private static final String EDIT_COMPUTER_ACTION = "editComputerAction";
    private static final String REDIRECT_400 = "redirect:/400";

    // Parameters
    private static final String COMPUTER = "computer";
    private static final String COMPANY_ID = "companyId";
    private static final String COMPANIES = "companies";
    private static final String ID = "id";

    // Dashboard messages
    private static final String DASHBOARD_MESSAGE_COMPUTERCREATED = "dashboard.message.computerCreated";
    private static final String DASHBOARD_MESSAGE_COMPUTERNOTCREATED = "dashboard.message.computerNotCreated";
    private static final String DASHBOARD_MESSAGE_COMPUTERMODIFIED = "dashboard.message.computerModified";
    private static final String DASHBOARD_MESSAGE_UPDATE_FAILED = "dashboard.message.updateFailed";
    private static final String DASHBOARD_MESSAGE_BADID = "dashboard.message.badId";

    private String message = null;
    private MessageType messageType = MessageType.INFO;

    /**
     * Which type of message to display in the dashboard.
     */
    private enum MessageType { INFO, ERROR, CREATION };

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(ComputerController.class);

    /**
     * Initialize the services.
     * @param computerService   The computer service
     * @param companyService    The company service
     * @param messageSource     The message source
     */
    public ComputerController(ComputerService computerService, CompanyService companyService, MessageSource messageSource) {
        this.computerService = computerService;
        this.companyService = companyService;
        this.messageSource = messageSource;
    }

    /**
     * Display the original dashboard with paging.
     * @param currentPage       The page to display
     * @param resultsPerPage    The number of results per page to display
     * @param locale            The user locale
     * @return                  The ModelAndView completed
     */
    @GetMapping(value = {"/", "/" + DASHBOARD})
    public ModelAndView dashboard(
            @RequestParam(value = PAGE, defaultValue = CURRENT_PAGE) int currentPage,
            @RequestParam(value = RESULTS, defaultValue = RESULTS_PER_PAGE) int resultsPerPage,
            Locale locale) {
        int numberOfComputers = computerService.countComputers();

        if (currentPage * resultsPerPage > numberOfComputers) {
            currentPage = (int) Math.ceil((double) numberOfComputers / (double) resultsPerPage);
        }
        Optional<Page<Computer>> pageOptional = computerService.getAllComputersWithPaging(currentPage, resultsPerPage);

        List<ComputerDTO> computerList = new ArrayList<>();
        Page<Computer> pageComputer = new Page<>();
        if (pageOptional.isPresent()) {
            pageComputer = pageOptional.get();
            for (Computer computer : pageComputer.getResults()) {
                computerList.add(ComputerConvertor.toDTO(computer));
            }
        }
        ModelAndView modelAndView = new ModelAndView(DASHBOARD);
        constructModelAndView(modelAndView, numberOfComputers, computerList, resultsPerPage,
                pageComputer.getMaxPage(), pageComputer.getCurrentPage());
        modelAndView.addObject(MESSAGE, message);
        modelAndView.addObject(MESSAGE_TYPE, messageType);
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
    @GetMapping(value = {"/" + DASHBOARD}, params = {SEARCH})
    public ModelAndView dashboardWithSearch(
            @RequestParam(value = SEARCH) String search,
            @RequestParam(value = PAGE, defaultValue = CURRENT_PAGE) int currentPage,
            @RequestParam(value = RESULTS, defaultValue = RESULTS_PER_PAGE) int resultsPerPage,
            Locale locale) {
        int numberOfComputers = computerService.countSearchedComputers(search);

        if (currentPage * resultsPerPage > numberOfComputers) {
            currentPage = (int) Math.ceil((double) numberOfComputers / (double) resultsPerPage);
        }

        Optional<Page<Computer>> pageOptional = computerService.searchComputer(search, currentPage, resultsPerPage);

        ModelAndView modelAndView = new ModelAndView(DASHBOARD);
        modelAndView.addObject(SEARCH, search);

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
    @PostMapping("/" + DELETE_COMPUTERS)
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
        return REDIRECT_DASHBOARD;
    }

    /**
     * Display the page allowing the create a computer.
     * @param locale    The user locale
     * @return          The ModelAndView
     */
    @GetMapping("/" + ADD_COMPUTER)
    public ModelAndView displayAddComputer(Locale locale) {
        final List<Company> companies = companyService.findAllCompanies();
        ModelAndView modelAndView = new ModelAndView(ADD_COMPUTER);
        modelAndView.addObject(COMPUTER, new ComputerDTO());
        modelAndView.addObject(COMPANIES, companies);
        return modelAndView;
    }

    /**
     * Display the page allowing the create a computer.
     * @param computerDTO   The computer to validate
     * @param locale        The user locale
     * @param bindingResult The binding result
     * @return              The redirection
     */
    @PostMapping("/" + CREATE_COMPUTER)
    public String addComputer(@ModelAttribute(COMPUTER) @Valid ComputerDTO computerDTO, Locale locale,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return REDIRECT_400;
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
            message = messageSource.getMessage(DASHBOARD_MESSAGE_COMPUTERCREATED, new Object[] {idNewComputer}, locale);
            messageType = MessageType.CREATION;
        } catch (ComputerServiceException e) {
            LOGGER.warn(e.toString());
            messageType = MessageType.ERROR;
            message = messageSource.getMessage(DASHBOARD_MESSAGE_COMPUTERNOTCREATED, null, locale);
        }
        return REDIRECT_DASHBOARD;
    }

    /**
     * Display the page to edit a computer.
     * @param id        The computer's identifier
     * @param locale    The user locale
     * @param model     The model
     * @return          The desired redirection
     */
    @GetMapping(value = "/" + EDIT_COMPUTER, params = {ID})
    public String displayEditComputer(@RequestParam(value = ID) long id, Locale locale, ModelMap model) {
        final List<Company> companies = companyService.findAllCompanies();
        Computer computerFull;
        try {
            computerFull = computerService.getOneComputer(id);
            ComputerDTO computer = ComputerConvertor.toDTO(computerFull);
            model.addAttribute(COMPUTER, computer);
            if (null != computerFull.getManufacturer()) {
                model.addAttribute(COMPANY_ID, computerFull.getManufacturer().getId());
            } else {
                model.addAttribute(COMPANY_ID, -1L);
            }
            model.addAttribute(COMPANIES, companies);
        } catch (ComputerServiceException e) {
            messageType = MessageType.ERROR;
            message = messageSource.getMessage(DASHBOARD_MESSAGE_BADID, null, locale);
            return REDIRECT_DASHBOARD;
        }
        return EDIT_COMPUTER;
    }

    /**
     * Save the modification of a computer.
     * @param computerDTO   The computer send by the jsp
     * @param locale        The user locale
     * @param bindingResult The BindingResult
     * @return              The redirection
     */
    @PostMapping(value = "/" + EDIT_COMPUTER_ACTION)
    public String saveEditComputer(@ModelAttribute(COMPUTER) @Valid ComputerDTO computerDTO,
            Locale locale, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return REDIRECT_400;
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
            messageType = MessageType.CREATION;
            message = messageSource.getMessage(DASHBOARD_MESSAGE_COMPUTERMODIFIED, new Object[] {computer2.get().getId()}, locale);
        } catch (ComputerServiceException e) {
            messageType = MessageType.ERROR;
            message = messageSource.getMessage(DASHBOARD_MESSAGE_UPDATE_FAILED, new Object[] {null}, locale);
        }
        return REDIRECT_DASHBOARD;
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
        modelAndView.addObject(NB_COMPUTERS, numberOfComputers);
        modelAndView.addObject(COMPUTER_LIST, computerList);
        modelAndView.addObject(RESULTS, resultsPerPage);
        modelAndView.addObject(MAX_PAGE, maxPage);
        modelAndView.addObject(PAGE, currentPage);
    }
}
