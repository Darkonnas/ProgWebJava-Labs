package com.example.lab1;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/")
public class CountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().printf("<p> The context count is: %d</p>%n", getContextCounter(request));
        response.getWriter().printf("<p> The session count is: %d</p>%n", getSessionCounter(request));
    }

    private int getContextCounter(HttpServletRequest request) {
        Object counter = request.getServletContext().getAttribute("counter");
        int count;

        if (counter == null) {
            request.getServletContext().setAttribute("counter", new AtomicInteger(1));
            count = 1;
        } else {
            count = ((AtomicInteger)counter).incrementAndGet();
        }

        return count;
    }

    private int getSessionCounter(HttpServletRequest request) {
        Object counter = request.getSession().getAttribute("counter");
        int count;

        if (counter == null) {
            request.getSession().setAttribute("counter", new AtomicInteger(1));
            count = 1;
        } else {
            count = ((AtomicInteger)counter).incrementAndGet();
        }

        return count;
    }
}
