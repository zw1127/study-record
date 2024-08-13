package cn.javastudy.springboot.web.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(filterName = "javaFilter", urlPatterns = "/*", initParams = {
    @WebInitParam(name = "name", value = "javastudy"),
    @WebInitParam(name = "code", value = "123456")})
public class JavaFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(JavaFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("java filter init.");
        String name = filterConfig.getInitParameter("name");
        String code = filterConfig.getInitParameter("code");
        LOG.info("name is " + name);
        LOG.info("code is " + code);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        LOG.info("java filter processing.");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.info("java filter destroy.");
    }

}
