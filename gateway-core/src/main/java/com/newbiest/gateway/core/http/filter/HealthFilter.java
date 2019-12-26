package com.newbiest.gateway.core.http.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证Gateway的健康消息
 * Created by guoxunbo on 2019-12-20 15:11
 */
public class HealthFilter extends OncePerRequestFilter {

    static final String HEALTH_CHECK_PATH = "/health";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (HEALTH_CHECK_PATH.equals(request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("OK");
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
