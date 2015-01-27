package hudson.plugins.simpleupdatesite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: lanwen
 * Date: 27.01.15
 * Time: 1:30
 */
public class RequestLoggingFilter implements Filter {
    private static Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private ServletContext context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        chain.doFilter(request, response);
        LOG.info("{} {} {}", req.getMethod(), req.getRequestURI(), res.getStatus());
    }

    @Override
    public void destroy() {

    }
}
