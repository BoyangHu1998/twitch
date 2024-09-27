package com.boyangh.twitch;

import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = {"/game"})
public class HelloServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.getWriter().print("Hello World");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String gamename = request.getParameter("gamename");
        response.getWriter().println("Game name: " + gamename);
    }
}
