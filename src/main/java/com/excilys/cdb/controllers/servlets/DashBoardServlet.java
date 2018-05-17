package com.excilys.cdb.controllers.servlets;

import java.io.IOException;
import java.util.ArrayList;
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
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.ComputerService;
import com.excilys.cdb.utils.ComputerConvertor;
import com.excilys.cdb.utils.Page;
import com.excilys.cdb.validators.ComputersToDeleteValidator;

/**
 * Servlet implementation class Servlet.
 */
@WebServlet(name = "DashBoardServlet", urlPatterns = { "/dashboard" })
public class DashBoardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ComputerService computerService;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(DashBoardServlet.class);

    private final String DELETE_COMPUTER = "deleteComputer";
    private final String SEARCH_COMPUTER = "search";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashBoardServlet() {
        super();
        try {
            computerService = ComputerService.getInstance();
        } catch (FactoryException e) {
            LOGGER.warn("Error when creating the service: " + e.getMessage());
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String todo = request.getParameter("todo");
        String search = request.getParameter("search");
        int numberOfComputers = SEARCH_COMPUTER.equals(todo) ? computerService.countSearchedComputers(search) : computerService.countComputers();
        int resultsPerPage = (null != request.getParameter("results")) ? Integer.parseInt(request.getParameter("results")) : 10;
        int currentPage = (null != request.getParameter("page")) ? Integer.parseInt(request.getParameter("page")) : 1;

        if (currentPage * resultsPerPage > numberOfComputers) {
            currentPage = (int) Math.ceil((double) numberOfComputers / (double) resultsPerPage);
        }

        Optional<Page<Computer>> pageOptional;

        if (null != todo && SEARCH_COMPUTER.equals(todo)) {
            pageOptional = computerService.searchComputer(search, currentPage, resultsPerPage);
            request.setAttribute("todo", todo);
            request.setAttribute("search", search);
        } else {
            pageOptional = computerService.getAllComputersWithPaging(currentPage, resultsPerPage);
        }

        List<ComputerDTO> computerList = new ArrayList<>();
        if (pageOptional.isPresent()) {
            Page<Computer> page = pageOptional.get();
            for (Computer computer : page.getResults()) {
                computerList.add(ComputerConvertor.computerToDTO(computer));
            }
            request.setAttribute("nbComputers", numberOfComputers);
            request.setAttribute("computerList", computerList);
            request.setAttribute("results", page.getResultsPerPage());
            request.setAttribute("maxPage", page.getMaxPage());
            request.setAttribute("page", page.getCurrentPage());
        } else {
            request.setAttribute("nbComputers", numberOfComputers);
            request.setAttribute("computerList", computerList);
            request.setAttribute("results", resultsPerPage);
            request.setAttribute("maxPage", 1);
            request.setAttribute("page", 1);
        }

        if (null != todo && DELETE_COMPUTER.equals(todo)) {
            deleteComputer(request, response);
        }
        this.getServletContext().getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Delete one or a list of computers.
     * @param request   The request
     * @param response  The response
     */
    private void deleteComputer(HttpServletRequest request, HttpServletResponse response) {
        String toDelete = request.getParameter("selection");
        StringBuilder stringBuilder = new StringBuilder(toDelete);
        stringBuilder.insert(0, "(");
        stringBuilder.append(")");
        if (ComputersToDeleteValidator.verifyListToDelete(stringBuilder.toString())) {
            computerService.deleteMultipleComputers(stringBuilder.toString());
        }
    }

}
