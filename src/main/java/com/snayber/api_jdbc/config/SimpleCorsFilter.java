package com.snayber.api_jdbc.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro CORS simple que agrega los headers necesarios para permitir
 * peticiones desde el frontend.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");

        // Permitir todos los orígenes localhost con cualquier puerto
        if (origin != null && (origin.startsWith("http://localhost") ||
                               origin.startsWith("https://localhost") ||
                               origin.startsWith("http://127.0.0.1") ||
                               origin.startsWith("https://127.0.0.1"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else if (origin == null) {
            // Si no hay origin, permitir cualquier origen (útil para Postman, etc)
            response.setHeader("Access-Control-Allow-Origin", "*");
        }

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control, Pragma");
        response.setHeader("Access-Control-Expose-Headers",
            "Authorization, Content-Type, Content-Length, X-Total-Count");

        // Manejar preflight requests (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // No necesita inicialización
    }

    @Override
    public void destroy() {
        // No necesita cleanup
    }
}

