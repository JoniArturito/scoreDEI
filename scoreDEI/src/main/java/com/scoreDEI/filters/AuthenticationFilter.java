/**
 * It implements the Filter interface and overrides the doFilter method
 */
package com.scoreDEI.filters;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

@Component
@Order(1)
public class AuthenticationFilter implements Filter, javax.servlet.Filter {

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
     * If the user is not logged in, redirect to the login page. Otherwise, continue with the request
     *
     * @param servletRequest The request object that is passed to the servlet.
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

        if(user == null) {
            response.sendRedirect("/login");
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
