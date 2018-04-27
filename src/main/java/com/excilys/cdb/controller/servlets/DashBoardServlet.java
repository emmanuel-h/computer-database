package com.excilys.cdb.controller.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.GeneralService;
import com.excilys.cdb.utils.ComputerConvertor;
import com.excilys.cdb.utils.Page;

/**
 * Servlet implementation class Servlet.
 */
@WebServlet(name = "DashBoardServlet", urlPatterns = { "/dashboard" })
public class DashBoardServlet extends HttpServlet {


    private static final long serialVersionUID = 1L;

    private GeneralService service;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(GeneralService.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashBoardServlet() {
        super();
        try {
            service = GeneralService.getInstance();
        } catch (GeneralServiceException e) {
            LOGGER.warn("Erreur lors de la création du service : " + e.getMessage());
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // First retrieve the different variables
        int numberOfComputers = service.countComputers();
        int resultsPerPage = (null != request.getParameter("results")) ? Integer.parseInt(request.getParameter("results")) : 10;
        int currentPage = (null != request.getParameter("page")) ? Integer.parseInt(request.getParameter("page")) : 1;

        // Test if there is no computers to display for the asked page
        if (currentPage * resultsPerPage > numberOfComputers) {
            currentPage = (int) Math.ceil((double) numberOfComputers / (double) resultsPerPage);
        }

        Page<Computer> page = service.getAllComputers(currentPage, resultsPerPage);
        List<ComputerDTO> computerList = new ArrayList<>();
        for (Computer computer : page.getResults()) {
            computerList.add(ComputerConvertor.computerToDTO(computer));
        }
        request.setAttribute("nbComputers", numberOfComputers);
        request.setAttribute("computerList", computerList);
        request.setAttribute("results", page.getResultsPerPage());
        request.setAttribute("maxPage", page.getMaxPage());
        request.setAttribute("page", page.getCurrentPage());

        this.getServletContext().getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }

}
