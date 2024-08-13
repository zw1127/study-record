package cn.javastudy.springboot.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InitServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = getServletConfig().getInitParameter("name");
        String sex = getServletConfig().getInitParameter("sex");

        resp.getOutputStream().println("name is " + name);
        resp.getOutputStream().println("sex is " + sex);

    }

}
