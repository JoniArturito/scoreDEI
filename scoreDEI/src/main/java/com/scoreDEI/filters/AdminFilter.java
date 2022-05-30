package com.scoreDEI.filters;

import com.scoreDEI.Entities.User;
import com.scoreDEI.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserService userService;

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        javax.servlet.Filter.super.init(filterConfig);
    }

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

    @Override
    public void destroy() {
        javax.servlet.Filter.super.destroy();
    }
}
