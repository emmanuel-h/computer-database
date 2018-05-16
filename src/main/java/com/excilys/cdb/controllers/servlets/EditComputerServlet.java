package com.excilys.cdb.controllers.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.GeneralService;
import com.excilys.cdb.utils.ComputerConvertor;

/**
 * Servlet implementation class EditComputerServlet.
 */
@WebServlet(name = "EditComputerServlet", urlPatterns = { "/editComputer" })
public class EditComputerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private GeneralService service;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(GeneralService.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditComputerServlet() {
        super();
        try {
            service = GeneralService.getInstance();
        } catch (GeneralServiceException e) {
            LOGGER.warn("Error when creating the service : " + e.getMessage());
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = null;

        final List<Company> companies = service.findAllCompanies();
        String todo = request.getParameter("todo");

        long id;

        if (null != todo && "Edit".equals(todo)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            id = Long.parseLong(request.getParameter("id"));
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
                Optional<Computer> computer2 = service.updateComputer(computer);
                System.out.println(computer2);
                message = "Computer succesfully modified";
            } catch (GeneralServiceException e) {
                message = "Update not applied";
            }
        } else {
            id = Long.parseLong(request.getParameter("id"));
        }
        try {
            Computer computerFull = service.getOneComputer(id);
            ComputerDTO computer = ComputerConvertor.computerToDTO(computerFull);
            request.setAttribute("computer", computer);
            if (null != computerFull.getManufacturer()) {
                request.setAttribute("companyId", computerFull.getManufacturer().getId());
            } else {
                request.setAttribute("companyId", -1L);
            }
            request.setAttribute("companies", companies);
        } catch (GeneralServiceException e) {
            message = "Identifier is not valid";
        }
        request.setAttribute("message", message);
        this.getServletContext().getRequestDispatcher("/pages/editComputer.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
