/**
 * It checks if the user is logged in and if he is an admin. If he is, it allows him to access the page, otherwise it
 * redirects him to the forbidden page
 */
package com.scoreDEI.filters;

import com.scoreDEI.Entities.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.LogRecord;

@Component
@Order(2)
public class AdminFilter implements java.util.logging.Filter, javax.servlet.Filter {

    /**
     * If the log level is greater than or equal to the level of the log record, then return true, otherwise return false.
     *
     * @param record The LogRecord object that contains the log message.
     * @return false
     */
    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }

    /**
     * > The `init` function is called when the filter is initialized
     *
     * @param filterConfig The FilterConfig object that contains the configuration information for this filter.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        javax.servlet.Filter.super.init(filterConfig);
    }

    /**
     * If the user is not logged in, redirect to the forbidden page
     *
     * @param servletRequest The request object that is passed to the servlet when the servlet is called.
     * @param servletResponse The response object that is passed to the filter.
     * @param filterChain This is the chain of filters that the request will go through.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        User u = (User) user;
        if(user == null || session.getAttribute("admin") == null) {
            response.sendRedirect("/forbidden");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    /**
     * This function is called when the filter is destroyed.
     */
    @Override
    public void destroy() {
        javax.servlet.Filter.super.destroy();
    }
}
