package com.snayber.api_jdbc.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Filtro CORS para asegurar que todas las solicitudes, incluyendo las preflight OPTIONS,
 * sean manejadas correctamente.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8080}")
    private List<String> allowedOrigins;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        String origin = request.getHeader("Origin");
        
        // Verificar si el origen está permitido
        boolean originAllowed = false;
        if (origin != null) {
            for (String allowedOrigin : allowedOrigins) {
                if (allowedOrigin.equals(origin) || 
                    (allowedOrigin.contains("localhost") && origin.contains("localhost"))) {
                    originAllowed = true;
                    break;
                }
            }
        } else {
            // Si no hay origen (solicitudes desde mismo servidor), permitir
            originAllowed = true;
        }
        
        if (originAllowed && origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else if (originAllowed && origin == null) {
            // Para solicitudes del mismo servidor, usar el primer origen permitido
            response.setHeader("Access-Control-Allow-Origin", allowedOrigins.get(0));
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Para solicitudes OPTIONS (preflight), enviar una respuesta 200 OK
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
