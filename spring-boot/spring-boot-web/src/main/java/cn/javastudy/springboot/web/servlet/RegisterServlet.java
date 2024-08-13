package cn.javastudy.springboot.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

public class RegisterServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 114888619803838564L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = getServletConfig().getInitParameter("name");
        String sex = getServletConfig().getInitParameter("sex");

        resp.getOutputStream().println("name is " + name);
        resp.getOutputStream().println("sex is " + sex);
    }

}
