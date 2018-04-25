package com.excilys.cdb.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.services.GeneralService;
import com.excilys.cdb.utils.Page;

/**
 * Servlet implementation class Servlet.
 */
@WebServlet(name = "DashBoardServlet", urlPatterns = { "/dashboard" })
public class DashBoardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    GeneralService service;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashBoardServlet() {
        super();
        try {
            service = GeneralService.getInstance();
        } catch (GeneralServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        int numberOfComputers = service.countComputers();
        Page<Computer> firstPage = service.getAllComputers(1, 10);

        request.setAttribute("nbComputers", numberOfComputers);
        request.setAttribute("computerList", firstPage.getResults());

        this.getServletContext().getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }

}
