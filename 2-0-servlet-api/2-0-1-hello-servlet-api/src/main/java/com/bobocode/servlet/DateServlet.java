package com.bobocode.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

/**
 * To create a servlet you have to extend your class from {@link HttpServlet}.
 * Also add annotation {@link WebServlet} with parameter to map a path for URL.
 */
@WebServlet("/date")
public class DateServlet extends HttpServlet {

    /**
     * This method is overridden from {@link HttpServlet} class.
     * It called by the server (container) to allow servlets to handle GET requests.
     *
     * @param request  an {@link HttpServletRequest} object that contains the request
     *                 the client has made of the servlet.
     * @param response an {@link HttpServletResponse} object that contains the response
     *                 the servlet sends to the client.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        PrintWriter writer = response.getWriter();
        writer.write(LocalDate.now().toString());
        writer.flush();
    }
}