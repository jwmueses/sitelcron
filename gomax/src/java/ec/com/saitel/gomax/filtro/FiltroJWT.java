/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.filtro;
import ec.com.saitel.gomax.dao.SuscripcionDAO;
import ec.com.saitel.gomax.model.Suscripcion;
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
 * @author Jorge
 */
@WebFilter(
    urlPatterns = {
//        "/gomaxtv/planestv/*",
        "/gomaxtv/suscripciones/*"    
    }
)
public class FiltroJWT implements Filter 
{
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String jwt = httpRequest.getHeader("saitel-tv-jwt")!=null ? httpRequest.getHeader("saitel-tv-jwt") : "";

        if( jwt.compareTo("") != 0 ) { 
            SuscripcionDAO suscripcionDAO = new SuscripcionDAO();
            Suscripcion suscripcion = suscripcionDAO.getSuscripcionPorJwt(jwt);
            suscripcionDAO.cerrar();
            if(suscripcion.getCorreoCuenta() != null) {
                if(suscripcion.getCorreoCuenta().compareTo("") != 0) {
                    chain.doFilter(request, response);
                } else {
                    ((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() + "/index.html");
                } 
            } else {
                    ((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() + "/index.html");
            }
        } else {
            ((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() + "/index.html");
        }
    }
     
    @Override
    public void destroy() {
    }    

}