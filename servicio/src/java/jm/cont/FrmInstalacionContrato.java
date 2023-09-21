/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cont;

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
import jm.pag.clas.Pagos;
import jm.web.Utils;

/**
 *
 * @author wilso
 */
public class FrmInstalacionContrato extends HttpServlet {

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
        Instalacion ObjInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        FacturaVenta ObjFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        String objeto = "obj»div_contenedor^frm»";
        String tipo = request.getParameter("tipo") != null ? request.getParameter("tipo") : "";
        try {
            if (tipo.compareTo("c") == 0) {
                ResultSet rs = ObjInstalacion.getInstalacionCliente(id_cliente);
                if (rs != null) {
                    if (ObjInstalacion.getFilas(rs) > 0) {
                        html.append("<div class=\"card-header card-header-primary\">");
                        html.append("<h4 class=\"card-title \">Servicios Prestados</h4>");
                        html.append("<p class=\"card-category\">Eliga el servicio que desea notificar su pago</p>");
                        html.append("</div>");
                        html.append("<div class=\"card-body\">");
                        html.append("<div class=\"table-responsive\">");
                        html.append("<table class=\"table\">");
                        html.append("<thead class=\" text-primary\">");
//                    html.append("<th>N°</th>");
                        html.append("<th>COD. PAGO</th>");
                        html.append("<th>SUCURSAL</th>");
                        html.append("<th>SECTOR</th>");
                        html.append("<th>DIRECCIÓN INSTALACIÓN</th>");
                        html.append("<th>PLAN ACTUAL</th>");
                        html.append("<th>ESTADO SERVICIO</th>");
                        html.append("<th>IP</th>");
                        html.append("<th>&nbsp;&nbsp;</th>");
                        html.append("</thead><tbody>");
                        int i = 1;
                        while (rs.next()) {
                            html.append("<tr>");
//                        html.append("<td>" + i + "</td>");
                            String id_instalacion = (rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "");
                            String id_factura_venta = (rs.getString("id_factura_venta") != null ? rs.getString("id_factura_venta") : "");
                            String costo_instalacion = (rs.getString("costo_instalacion") != null ? rs.getString("costo_instalacion") : "0");
                            costo_instalacion = costo_instalacion.trim().compareTo("") != 0 ? costo_instalacion : "0";
                            double costo_instalacion1 = Double.parseDouble(costo_instalacion);
                            html.append("<td>" + (rs.getString("cod_pichincha") != null ? rs.getString("cod_pichincha") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_sucursal") != null ? rs.getString("txt_sucursal") : "") + "</td>");
                            html.append("<td>" + (rs.getString("sector") != null ? rs.getString("sector") : "") + "</td>");
                            html.append("<td>" + (rs.getString("direccion_instalacion") != null ? rs.getString("direccion_instalacion") : "") + "</td>");
                            html.append("<td>" + (rs.getString("plan") != null ? rs.getString("plan") : "") + "</td>");
                            html.append("<td>" + (rs.getString("txt_estado_servicio") != null ? rs.getString("txt_estado_servicio") : "") + "</td>");
                            html.append("<td>" + (rs.getString("ip") != null ? rs.getString("ip") : "") + "</td>");
                            html.append("<td>");
                            String aprobado[] = ObjInstalacion.getInstalacionAprobado(id_instalacion);
                            if (aprobado[1].compareTo("") == 0 || aprobado[1].compareTo("a") == 0) {
                                boolean deuda = ObjFacturaVenta.InstalacionDeudaContrato(id_factura_venta);
                                //if (deuda) {
                                String estado_tramite = ObjFacturaVenta.InstalacionDeudaEstado(id_factura_venta);
                                if ((estado_tramite.trim().compareTo("") == 0 && id_factura_venta.compareTo("") == 0 && costo_instalacion1 > 0) || (estado_tramite.trim().compareTo("") == 0 && deuda)) {
                                    html.append("<a href=\"javascript:;\" id='btn_pagar" + i + "' class=\"btn btn-primary btn-round\" style=\"background:#fd7110\" onclick=\"pag_subirpago('" + id_instalacion + "','" + i + "','" + id_factura_venta + "','1');\">Pagar Contrato</a>");
                                } else {
                                    if (deuda && estado_tramite.trim().compareTo("") != 0) {
                                        html.append("<p style=\"font-size:13px; color:red;\" >PAGO: " + estado_tramite + "</p>");
                                    }
                                }
                                // }
                            } else {
                                html.append("<p style=\"font-size:13px; color:red;\" >FACTIBILIDAD  DE INSTALACION " + aprobado[2] + "</p>");
                            }
                            html.append("<a href=\"javascript:;\" id='btn_notificar" + i + "' class=\"btn btn-primary btn-round\" style=\"background:#16c65b\" onclick=\"con_instalacion('" + id_instalacion + "');\">Ver Contrato</a>");
                            html.append("</td>");
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
                }
            } else {
                html.append("No se ha encontrado algo para procesar");
            }
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjInstalacion.cerrar();
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
