/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.fac;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.fac.clas.FacturaVenta;
import jm.pag.clas.Instalacion;
import jm.web.Fecha;
import jm.web.Utils;

/**
 *
 * @author wilso
 */
public class FrmDocumentoElectronico extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    public void init(ServletConfig config) throws ServletException {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
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
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        int mes = Fecha.getMes();
        String fecha_ini = Fecha.getAnio() + "-" + (mes > 9 ? mes : "0" + mes) + "-01";
        String fecha_fin = Fecha.getFecha("ISO");
        FacturaVenta ObjFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String objeto = "obj»div_contenedor^fun»^frm»";
        try {

            html.append("<div class=\"card-header card-header-primary colorheader\">");
            html.append("<h4 class=\"card-title \">Documentos Electronicos</h4>");
            html.append("<p class=\"card-category\">Descarge sus documentos como Fácturas - Notas de crédito </p>");
            html.append("</div>");
            html.append("<div class=\"card-body\">");
            ///
            html.append("<div class=\"card\">");
            html.append("<div class=\"card-header card-header-tabs card-header-primary\">");
            html.append("<div class=\"nav-tabs-navigation\">");
            html.append("<div class=\"nav-tabs-wrapper\">");
            html.append("<span class=\"nav-tabs-title\">#</span>");
            html.append("<ul class=\"nav nav-tabs\" data-tabs=\"tabs\">");
            html.append("<li class=\"nav-item\">");
            html.append("<a class=\"nav-link active\" href=\"#facturas\" data-toggle=\"tab\">");
            html.append("Facturas");
            html.append("<div class=\"ripple-container\"></div>");
            html.append("</a>");
            html.append("</li>");
            html.append("<li class=\"nav-item\">");
            html.append("<a class=\"nav-link\" href=\"#notascredito\" data-toggle=\"tab\">");
            html.append("Notas de Credito");
            html.append("<div class=\"ripple-container\"></div>");
            html.append("</a>");
            html.append("</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ///
            html.append("<div class=\"tab-content\">");
            html.append("<div class=\"tab-pane active\" id=\"facturas\">");
            html.append("<div class='content' style=''><div class='container-fluid'>");
            html.append("<br>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">Desde</label>");
            html.append("<input type=\"date\" id='fecha_inif' name='fecha_inif' class=\"form-control\" value='" + fecha_ini + "' />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">Hasta</label>");
            html.append("<input type=\"date\" id='fecha_finf' name='fecha_finf' class=\"form-control\" value='" + fecha_fin + "' />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">N° Factura</label>");
            html.append("<input type=\"text\" id='numero_f' name='numero_f' class=\"form-control\" value='' />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_filtro' onclick=\"fac_buscardocumentoselectronicos('f');\" >Filtrar</button>");
            html.append("</div>");
            html.append("</div>");
            html.append("<br>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-12\" id='div_f'>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div></div>");
            html.append("</div>");
            html.append("<div class=\"tab-pane\" id=\"notascredito\">");
            html.append("<div class='content' style=''><div class='container-fluid'>");
            html.append("<br>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">Desde</label>");
            html.append("<input type=\"date\" id='fecha_inin' name='fecha_inin' class=\"form-control\" value='" + fecha_ini + "' />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">Hasta</label>");
            html.append("<input type=\"date\" id='fecha_finn' name='fecha_finn' class=\"form-control\" value='" + fecha_fin + "' />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">N° Nota credito</label>");
            html.append("<input type=\"text\" id='numero_n' name='numero_n' class=\"form-control\" value='' />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_filtro' onclick=\"fac_buscardocumentoselectronicos('n');\" >Filtrar</button>");
            html.append("</div>");
            html.append("</div>");
            html.append("<br>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-12\" id='div_n'>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div></div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ///
            html.append("</div>");

        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjFacturaVenta.cerrar();
            out.print(Utils.putcardbody(objeto, html.toString()));
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
