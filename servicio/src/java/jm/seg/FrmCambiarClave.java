/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.pag.clas.Instalacion;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author wilso
 */
public class FrmCambiarClave extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    private String _ip_documental = null;
    private int _puerto_documental = 5432;
    private String _db_documental = null;
    private String _usuario_documental = null;
    private String _clave_documental = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
        this._ip_documental = config.getServletContext().getInitParameter("_IP_DOCUMENTAL");
        this._puerto_documental = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO_DOCUMENTAL"));
        this._db_documental = config.getServletContext().getInitParameter("_DB_DOCUMENTAL");
        this._usuario_documental = config.getServletContext().getInitParameter("_USUARIO_DOCUMENTAL");
        this._clave_documental = config.getServletContext().getInitParameter("_CLAVE_DOCUMENTAL");
        this._dir = config.getServletContext().getInitParameter("_DIR");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String id_cliente_portal = (String) sesion.getAttribute("id_cliente_portal");
        response.setContentType("Stext/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        Instalacion ObjInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        StringBuilder html = new StringBuilder();
        try {
            html.append("obj»cmpclave_html^foc»^fun»^frm»");
            html.append("<form id='FrmCambiarClave' action='FrmCambiarClaveGuardar' onsubmit=\"return seg_cambiarclaveguardar(this);\" autocomplete='off'> ");
            html.append("<input type='hidden' id='id_cliente_portal' name='id_cliente_portal' value='" + id_cliente_portal + "' />");
            html.append("<div class='row' id='div_imprimir'>");
            html.append("<div class='col-md-12'>");
            html.append("<div class=\"card\">");
            html.append("<div class=\"card-body\">");
            ///////
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">CONTRASEÑA ANTERIOR</label>");
            html.append("<input type=\"password\" id='clave_anterior'name='clave_anterior' class=\"form-control\" required value='' >");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">CONTRASEÑA NUEVA</label>");
            html.append("<input type=\"password\" id='clave_nueva'name='clave_nueva' class=\"form-control\" required value='' >");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">REPETIR CONTRASEÑA NUEVA</label>");
            html.append("<input type=\"password\" id='clave_nueva1'name='clave_nueva1' class=\"form-control\" required value='' >");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ///////
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ////
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<button type=\"submit\" class=\"btn btn-primary pull-right\" id='btn_imprimir' onclick=\"\" >GUARDAR</button>");
            html.append("</div>");
            html.append("</div>");
            ///
            html.append("</form>");
            out.println(html.toString());
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjInstalacion.cerrar();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
