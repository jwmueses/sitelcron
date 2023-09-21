/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag;

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
import jm.web.Fecha;

/**
 *
 * @author wilso
 */
public class FrmConsultaInstalacion extends HttpServlet {

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
        HttpSession sesion = request.getSession(true);
        String id_cliente = (String) sesion.getAttribute("id_cliente");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        String op = request.getParameter("op");
        int mes = Fecha.getMes();
        String fecha_ini = Fecha.getAnio() + "-" + (mes > 9 ? mes : "0" + mes) + "-01";
        String fecha_fin = Fecha.getFecha("ISO");
        try {
            if (op.compareTo("r") == 0) {
                String objeto = request.getParameter("obj");
                String id_instalacion = request.getParameter("id_instalacion");
                String i = request.getParameter("i");
                String estado_servicio = request.getParameter("estado");
                html.append("obj»" + objeto + "^fun»rec_buscarreclamosrealiados();^frm»");
                try {
                    html.append("<div class=\"card\">");
                    html.append("<div class=\"card-header card-header-tabs card-header-primary\">");
                    html.append("<div class=\"nav-tabs-navigation\">");
                    html.append("<div class=\"nav-tabs-wrapper\">");
                    html.append("<span class=\"nav-tabs-title\">#</span>");
                    html.append("<ul class=\"nav nav-tabs\" data-tabs=\"tabs\">");
                    html.append("<li class=\"nav-item\">");
                    html.append("<a class=\"nav-link active\" href=\"#reclamoshechos\" data-toggle=\"tab\">");
                    html.append("Soportes realizados");
                    html.append("<div class=\"ripple-container\"></div>");
                    html.append("</a>");
                    html.append("</li>");
                    if (estado_servicio.compareTo("p") != 0) {
                        html.append("<li class=\"nav-item\">");
                        html.append("<a class=\"nav-link\" href=\"javascript:void(0);\"  onclick=\"rec_reportarreclamo('" + id_instalacion + "', '" + i + "');\" data-toggle=\"tab\">");
                        html.append("Nuevo Soporte");
                        html.append("<div class=\"ripple-container\"></div>");
                        html.append("</a>");
                        html.append("</li>");
                    }
                    html.append("</ul>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div>");
                    ///
                    html.append("<div class=\"tab-content\">");
                    html.append("<div class=\"tab-pane active\" id=\"reclamoshechos\">");
                    html.append("<div class='content' style=''><div class='container-fluid'>");
                    html.append("<br>");
                    html.append("<input type='hidden' id='id_cliente' name='id_cliente' value='" + id_cliente + "' >");
                    html.append("<input type='hidden' id='id_instalacion' name='id_instalacion' value='" + id_instalacion + "' >");
                    html.append("<div class=\"row\">");
                    html.append("<div class=\"col-md-3\">");
                    html.append("<div class=\"form-group bmd-form-group\">");
                    html.append("<label class=\"bmd-label-floating\">DESDE</label>");
                    html.append("<input type=\"date\" id='fi'name='fi' class=\"form-control\" value='" + fecha_ini + "' required>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("<div class=\"col-md-3\">");
                    html.append("<div class=\"form-group bmd-form-group\">");
                    html.append("<label class=\"bmd-label-floating\">HASTA</label>");
                    html.append("<input type=\"date\" id='ff'name='ff' class=\"form-control\" value='" + fecha_fin + "' required>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("<div class=\"col-md-3\">");
                    html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_filtro' onclick=\"rec_buscarreclamosrealiados();\" >Filtrar</button>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("<br>");
                    html.append("<div class='row'>");
                    html.append("<div class=\"col-md-12\" id='div_reportados'>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div></div>");
                    html.append("</div>");
                    html.append("<div class=\"tab-pane\" id=\"nuevoreclamo\">");
                    html.append("<div class='content' style=''><div class='container-fluid'>");
                    html.append("<br>Reportando");
                    html.append("</div></div>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div>");
                    ///
                } catch (Exception e) {
                    System.out.println("error");
                } finally {
                }
            } else if (op.compareTo("s") == 0) {
                String objeto = request.getParameter("obj");
                String id_instalacion = request.getParameter("id_instalacion");
                String estado_servicio = request.getParameter("estado");
                String i = request.getParameter("i");
                html.append("obj»" + objeto + "^fun»sus_buscarsuspencioneshechas();^frm»");
                try {
                    html.append("<div class=\"card\">");
                    html.append("<div class=\"card-header card-header-tabs card-header-primary\">");
                    html.append("<div class=\"nav-tabs-navigation\">");
                    html.append("<div class=\"nav-tabs-wrapper\">");
                    html.append("<span class=\"nav-tabs-title\">#</span>");
                    html.append("<ul class=\"nav nav-tabs\" data-tabs=\"tabs\">");
                    html.append("<li class=\"nav-item\">");
                    html.append("<a class=\"nav-link active\" href=\"#suspencionhecha\" data-toggle=\"tab\">");
                    html.append("Suspenciones realizadas");
                    html.append("<div class=\"ripple-container\"></div>");
                    html.append("</a>");
                    html.append("</li>");
                    if (estado_servicio.compareTo("a") == 0 || estado_servicio.compareTo("s") == 0) {
                        html.append("<li class=\"nav-item\">");
                        html.append("<a class=\"nav-link\" href=\"javascript:void(0);\"  onclick=\"sus_servicio('-1', '" + id_instalacion + "');\" data-toggle=\"tab\">");
                        html.append("Nueva suspencion");
                        html.append("<div class=\"ripple-container\"></div>");
                        html.append("</a>");
                        html.append("</li>");
                    }
                    html.append("</ul>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div>");
                    ///
                    html.append("<div class=\"tab-content\">");
                    html.append("<div class=\"tab-pane active\" id=\"suspencionhecha\">");
                    html.append("<div class='content' style=''><div class='container-fluid'>");
                    html.append("<br>");
                    html.append("<input type='hidden' id='id_cliente' name='id_cliente' value='" + id_cliente + "' >");
                    html.append("<input type='hidden' id='id_instalacion' name='id_instalacion' value='" + id_instalacion + "' >");
                    html.append("<div class=\"row\">");
                    html.append("<div class=\"col-md-3\">");
                    html.append("<div class=\"form-group bmd-form-group\">");
                    html.append("<label class=\"bmd-label-floating\">DESDE</label>");
                    html.append("<input type=\"date\" id='fi'name='fi' class=\"form-control\" value='" + fecha_ini + "' required>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("<div class=\"col-md-3\">");
                    html.append("<div class=\"form-group bmd-form-group\">");
                    html.append("<label class=\"bmd-label-floating\">HASTA</label>");
                    html.append("<input type=\"date\" id='ff'name='ff' class=\"form-control\" value='" + fecha_fin + "' required>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("<div class=\"col-md-3\">");
                    html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_filtro' onclick=\"sus_buscarsuspenciones();\" >Filtrar</button>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("<br>");
                    html.append("<div class='row'>");
                    html.append("<div class=\"col-md-12\" id='div_suspendidos'>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div></div>");
                    html.append("</div>");
                    html.append("<div class=\"tab-pane\" id=\"nuevasuspencion\">");
                    html.append("<div class='content' style=''><div class='container-fluid'>");
                    html.append("<br>Reportando");
                    html.append("</div></div>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div>");
                    ///
                } catch (Exception e) {
                    System.out.println("error");
                } finally {
                }
            }
            out.println(html.toString());
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            out.close();
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
