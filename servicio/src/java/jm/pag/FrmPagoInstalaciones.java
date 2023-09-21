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
import jm.pag.clas.Pagos;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmPagoInstalaciones extends HttpServlet {

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
        String txt = request.getParameter("txt");
        Pagos ObjPagos = new Pagos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            html.append("obj»div_instalaciones^frm»");
            int ok = ObjPagos.esCliente(txt);
            if (ok > 0) {
                ResultSet rs = ObjPagos.getInstalaciones(txt);
                if (rs != null) {
                    if (ObjPagos.getFilas(rs) > 0) {
                        html.append("<div class=\"card-header card-header-primary\">");
                        html.append("<h4 class=\"card-title \">Servicios Prestados</h4>");
                        html.append("<p class=\"card-category\">Eliga el servicio que desea notificar su pago</p>");
                        html.append("</div>");
                        html.append("<div class=\"card-body\">");
                        html.append("<div class=\"table-responsive\">");
                        html.append("<table class=\"table\">");
                        html.append("<thead class=\" text-primary\">");
//                    html.append("<th>N°</th>");
                        html.append("<th>Cod. pago</th>");
                        html.append("<th>Sector</th>");
                        html.append("<th>Dirección</th>");
                        html.append("<th>Estado</th>");
                        html.append("<th>Periodo de pago</th>");
                        html.append("<th>Valor a pagar</th>");
                        html.append("<th>Convenio de pago</th>");
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
                            html.append("<td>" + (rs.getString("txt_convenio_pago") != null ? rs.getString("txt_convenio_pago") : "") + "</td>");
                            html.append("<td><a href=\"javascript:;\" id='btn_notificar" + i + "' class=\"btn btn-primary btn-round\" onclick=\"pag_subirpago('" + id_instalacion + "','" + i + "','" + id_prefactura + "')\">Notificar</a></td>");
                            html.append("</tr>");
                            i++;
                        }
                        html.append("</tbody>");
                        html.append("</table>");
                        html.append("</div>");
                        html.append("</div>");
                    } else {
                        html.append("No se ha encontro pagos pendientes.");
                    }
                    rs.close();
                } else {
                    html.append("No se ha encontro pagos pendientes.");
                }
            } else {
                html.append("No se ha encontrado servicios prestados a este número de identificacón.");
            }
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjPagos.cerrar();
            out.print(html);
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
