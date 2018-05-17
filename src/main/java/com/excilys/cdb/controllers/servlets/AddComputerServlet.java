package com.excilys.cdb.controllers.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exceptions.ComputerServiceException;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.CompanyService;
import com.excilys.cdb.services.ComputerService;

/**
 * Servlet implementation class AddComputeit arServlet.
 */
@WebServlet(name = "AddComputerServlet", urlPatterns = { "/addComputer" })
public class AddComputerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ComputerService computerService;
    private CompanyService companyService;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(AddComputerServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddComputerServlet() {
        super();
        try {
            computerService = ComputerService.getInstance();
            companyService = CompanyService.getInstance();
        } catch (FactoryException e) {
            LOGGER.warn("Error when creating the service: " + e.getMessage());
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final List<Company> companies = companyService.findAllCompanies();
        String message = null;

        String todo = request.getParameter("todo");
        System.out.println("llll");
        try {
            if (null != todo) {
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
            }
        } catch (DateTimeParseException e) {
            message = "Error when parsing the dates";
        }

        request.setAttribute("companies", companies);
        request.setAttribute("message", message);
        this.getServletContext().getRequestDispatcher("/pages/addComputer.jsp").forward(request, response);
    }

}
