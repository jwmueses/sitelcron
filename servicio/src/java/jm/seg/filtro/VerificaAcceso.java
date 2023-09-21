/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg.filtro;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class VerificaAcceso implements Filter {

    private FilterConfig config;
    private String urlAutenticar;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        //Tambien se pueden cargar los parametros que configura la url de salida
        this.urlAutenticar = config.getInitParameter("Salir");
        if (urlAutenticar == null || urlAutenticar.trim().length() == 0) {
            //Error al cargar la página
            throw new ServletException("El tiempo de la sesión ha caducado, por favor, cierre el sistema y vuelva a ingresar.");
        }
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest miReq = (HttpServletRequest) req;
        HttpSession sesion = miReq.getSession(true);
        String usuario = (String) sesion.getAttribute("usuario");
        String clave = (String) sesion.getAttribute("clave");
        usuario = (usuario != null) ? usuario : "";
        clave = (clave != null) ? clave : "";
        if (usuario.compareTo("") == 0 && clave.compareTo("") == 0) {
            ((HttpServletResponse) res).sendRedirect(((HttpServletRequest) req).getContextPath() + "/" + urlAutenticar);

        } else {
            chain.doFilter(req, res);
        }
    }

    public void destroy() {
        config = null;
    }

}
