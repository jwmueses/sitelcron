/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.filtro;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sistemas
 */
@WebFilter(
    urlPatterns = {
        "/gomaxtv/*"
    }
)
public class FiltroCors implements Filter 
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        httpResponse.setHeader("Access-Control-Allow-Origin", "*"); // Permitir solicitudes desde cualquier origen
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, saitel-tv-jwt");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        if (httpRequest.getMethod().equals("OPTIONS")) {
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, saitel-tv-jwt");
            return;
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
    
}
