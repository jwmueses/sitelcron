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
import jm.pag.clas.Pagos;
import jm.web.Fecha;
import jm.web.Utils;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmInstalacionReporte extends HttpServlet {

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
        Pagos ObjPagos = new Pagos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        String objeto = "obj»div_contenedor^frm»";
        String tipo = request.getParameter("tipo") != null ? request.getParameter("tipo") : "";
        int mes = Fecha.getMes();
        String fecha_ini = Fecha.getAnio() + "-" + (mes > 9 ? mes : "0" + mes) + "-01";
        String fecha_fin = Fecha.getFecha("ISO");
        try {
            if (tipo.compareTo("p") == 0) {
                objeto = "obj»div_contenedor^fun»pag_consultarpago();^frm»";
                html.append("<div class=\"card-header card-header-primary\">");
                html.append("<h4 class=\"card-title \">Servicios Prestados</h4>");
                html.append("<p class=\"card-category\">Eliga el servicio que desea notificar su pago</p>");
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
                html.append("<a class=\"nav-link active\" href=\"#pagopendiente\" data-toggle=\"tab\">");
                html.append("Pagos Pendientes");
                html.append("<div class=\"ripple-container\"></div>");
                html.append("</a>");
                html.append("</li>");
                html.append("<li class=\"nav-item\">");
                html.append("<a class=\"nav-link\" href=\"#pagoreportado\" data-toggle=\"tab\">");
                html.append("Pagos Reportados");
                html.append("<div class=\"ripple-container\"></div>");
                html.append("</a>");
                html.append("</li>");
                html.append("</ul>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
                ///
                html.append("<div class=\"tab-content\">");
                html.append("<div class=\"tab-pane active\" id=\"pagopendiente\">");
                html.append("<div class='content' style=''><div class='container-fluid'>");
                html.append("<br>");
                html.append("<div class='row'>");
                html.append("<div class=\"col-md-12\">");
                ResultSet rs = ObjPagos.getInstalacionesClientePago(id_cliente);
                if (rs != null) {
                    if (ObjPagos.getFilas(rs) > 0) {

                        html.append("<div class=\"table-responsive\">");
                        html.append("<table class=\"table\">");
                        html.append("<thead class=\" text-primary\">");
                        html.append("<th>COD. PAGO</th>");
                        html.append("<th>SECTOR</th>");
                        html.append("<th>DIRECCION</th>");
                        html.append("<th>ESTADO</th>");
                        html.append("<th>PERIODO DE PAGO</th>");
                        html.append("<th>VALOR A PAGAR</th>");
                        html.append("<th>&nbsp;&nbsp;</th>");
                        html.append("</thead><tbody>");
                        int i = 1;
                        while (rs.next()) {
                            html.append("<tr>");
//                        html.append("<td>" + i + "</td>");
                            String id_instalacion = (rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "");
                            String id_prefactura = (rs.getString("id_prefactura") != null ? rs.getString("id_prefactura") : "");
                            html.append("<td>" + (rs.getString("cod_pichincha") != null ? rs.getString("cod_pichincha") : "") + "</td>");
                            html.append("<td>" + (rs.getString("sector") != null ? rs.getString("sector") : "") + "</td>");
                            html.append("<td>" + (rs.getString("direccion_instalacion") != null ? rs.getString("direccion_instalacion") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_estado_servicio") != null ? rs.getString("txt_estado_servicio") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_periodo") != null ? rs.getString("txt_periodo") : "") + "</td>");
                            html.append("<td>" + (rs.getString("total") != null ? rs.getString("total") : "") + "</td>");
                            html.append("<td><a href=\"javascript:;\" id='btn_notificar" + i + "' class=\"btn btn-primary btn-round\" onclick=\"pag_subirpago('" + id_instalacion + "','" + i + "','" + id_prefactura + "')\">Notificar</a></td>");
                            html.append("</tr>");
                            i++;
                        }
                        html.append("</tbody>");
                        html.append("</table>");
                        html.append("</div>");
                    } else {
                        html.append("No se ha encontro pagos pendientes.");
                    }
                }
                html.append("</div>");
                html.append("</div>");
                html.append("</div></div>");
                html.append("</div>");
                html.append("<div class=\"tab-pane\" id=\"pagoreportado\">");
                html.append("<div class='content' style=''><div class='container-fluid'>");
                html.append("<br>");
                html.append("<input type='hidden' id='id_cliente' name='id_cliente' value='" + id_cliente + "' >");
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
                html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_filtro' onclick=\"pag_consultarpago();\" >Filtrar</button>");
                html.append("</div>");
                html.append("</div>");
                html.append("<br>");
                html.append("<div class='row'>");
                html.append("<div class=\"col-md-12\" id='div_reportados'>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div></div>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
                ///
                html.append("</div>");
            } else if (tipo.compareTo("r") == 0) {
                ResultSet rs = ObjPagos.getInstalacionesClienteReclamo(id_cliente);
                if (rs != null) {
                    if (ObjPagos.getFilas(rs) > 0) {
                        html.append("<div class=\"card-header card-header-primary\">");
                        html.append("<h4 class=\"card-title \">Servicios Prestados</h4>");
                        html.append("<p class=\"card-category\">Eliga el servicio que desea notificar su reclamo</p>");
                        html.append("</div>");
                        html.append("<div class=\"card-body\">");
                        html.append("<div class=\"table-responsive\">");
                        html.append("<table class=\"table\">");
                        html.append("<thead class=\" text-primary\">");
//                    html.append("<th>N°</th>");
                        html.append("<th>COD. PAGO</th>");
                        html.append("<th>SECTOR</th>");
                        html.append("<th>DIRECCION</th>");
                        html.append("<th>ESTADO</th>");
                        html.append("<th>SECTOR</th>");
                        html.append("<th>SUCURSAL</th>");
                        html.append("<th>&nbsp;&nbsp;</th>");
                        html.append("</thead><tbody>");
                        int i = 1;
                        while (rs.next()) {
                            html.append("<tr>");
//                        html.append("<td>" + i + "</td>");
                            String id_instalacion = (rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "");
                            String estado_servicio = (rs.getString("estado_servicio") != null ? rs.getString("estado_servicio") : "");
                            html.append("<td>" + (rs.getString("cod_pichincha") != null ? rs.getString("cod_pichincha") : "") + "</td>");
                            html.append("<td>" + (rs.getString("sector") != null ? rs.getString("sector") : "") + "</td>");
                            html.append("<td>" + (rs.getString("direccion_instalacion") != null ? rs.getString("direccion_instalacion") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_estado_servicio") != null ? rs.getString("txt_estado_servicio") : "") + "</td>");
                            html.append("<td>" + (rs.getString("sector") != null ? rs.getString("sector") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_sucursal") != null ? rs.getString("txt_sucursal") : "") + "</td>");
//                            html.append("<td><a href=\"javascript:;\" id='btn_notificar" + i + "' class=\"btn btn-primary btn-round\" onclick=\"rec_reportarreclamo('" + id_instalacion + "', '" + i + "');\">Reportar</a></td>");
                            html.append("<td><a href=\"javascript:;\" id='btn_notificar" + i + "' class=\"btn btn-primary btn-round\" onclick=\"gen_cargarinstalacion('" + tipo + "', 'div_cargar_instalacion', '" + id_instalacion + "', '" + i + "','" + estado_servicio + "');\">REPORTAR</a></td>");
                            html.append("</tr>");
                            i++;
                        }
                        html.append("</tbody>");
                        html.append("</table>");
                        html.append("</div>");
                        html.append("</div>");
                        html.append("<br>");
                        html.append("<div class='row'>");
                        html.append("<div class=\"col-md-12\" id='div_cargar_instalacion'>");
                        ////

                        //
                        html.append("</div>");
                        html.append("</div>");
                    } else {
                        html.append("No se ha encontrado servicios a su nombre.");
                    }
                }
            } else if (tipo.compareTo("s") == 0) {
                ResultSet rs = ObjPagos.getInstalacionesClienteReclamo(id_cliente);
                if (rs != null) {
                    if (ObjPagos.getFilas(rs) > 0) {
                        html.append("<div class=\"card-header card-header-primary\">");
                        html.append("<h4 class=\"card-title \">Servicios Prestados</h4>");
                        html.append("<p class=\"card-category\">Eliga el servicio que desea notificar su reclamo</p>");
                        html.append("</div>");
                        html.append("<div class=\"card-body\">");
                        html.append("<div class=\"table-responsive\">");
                        html.append("<table class=\"table\">");
                        html.append("<thead class=\" text-primary\">");
//                    html.append("<th>N°</th>");
                        html.append("<th>COD. PAGO</th>");
                        html.append("<th>SECTOR</th>");
                        html.append("<th>DIRECCION</th>");
                        html.append("<th>ESTADO</th>");
                        html.append("<th>SECTOR</th>");
                        html.append("<th>SUCURSAL</th>");
                        html.append("<th>&nbsp;&nbsp;</th>");
                        html.append("</thead><tbody>");
                        int i = 1;
                        while (rs.next()) {
                            html.append("<tr>");
//                        html.append("<td>" + i + "</td>");
                            String id_instalacion = (rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "");
                            String estado_servicio = (rs.getString("estado_servicio") != null ? rs.getString("estado_servicio") : "");
                            html.append("<td>" + (rs.getString("cod_pichincha") != null ? rs.getString("cod_pichincha") : "") + "</td>");
                            html.append("<td>" + (rs.getString("sector") != null ? rs.getString("sector") : "") + "</td>");
                            html.append("<td>" + (rs.getString("direccion_instalacion") != null ? rs.getString("direccion_instalacion") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_estado_servicio") != null ? rs.getString("txt_estado_servicio") : "") + "</td>");
                            html.append("<td>" + (rs.getString("sector") != null ? rs.getString("sector") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_sucursal") != null ? rs.getString("txt_sucursal") : "") + "</td>");
//                            if (estado_servicio.compareTo("a") == 0 || estado_servicio.compareTo("s") == 0) {
                            html.append("<td><a href=\"javascript:;\" id='btn_notificar" + i + "' class=\"btn btn-primary btn-round\" onclick=\"gen_cargarinstalacion('" + tipo + "', 'div_cargar_instalacion', '" + id_instalacion + "', '" + i + "','" + estado_servicio + "');\">Suspender</a></td>");
//                            }
                            html.append("</tr>");
                            i++;
                        }
                        html.append("</tbody>");
                        html.append("</table>");
                        html.append("</div>");
                        html.append("</div>");
                        html.append("<br>");
                        html.append("<div class='row'>");
                        html.append("<div class=\"col-md-12\" id='div_cargar_instalacion'>");
                        ////

                        //
                        html.append("</div>");
                        html.append("</div>");
                    } else {
                        html.append("No se ha encontro instalaciones pendientes.");
                    }
                }
            } else {
                html.append("No se ha encontrado algo para procesar");
            }
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjPagos.cerrar();
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
