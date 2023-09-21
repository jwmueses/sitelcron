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
import jm.cont.clas.Sector;
import jm.cont.clas.Sucursal;
import jm.cont.clas.Ubicacion;
import jm.fac.clas.FacturaVenta;
import jm.ser.clas.Plan;
import jm.web.DatosDinamicos;

/**
 *
 * @author wilso
 */
public class FrmConsultaDocumento extends HttpServlet {

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
        try {
            if (op.compareTo("f") == 0) {
                FacturaVenta ObjFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
                String fi = request.getParameter("fi");
                String ff = request.getParameter("ff");
                String numero = request.getParameter("numero");
                String objeto = request.getParameter("obj");
                html.append("obj»" + objeto + "^frm»");
                try {
                    ResultSet rs = ObjFacturaVenta.getFacturasCliente(id_cliente, fi, ff, numero);
                    html.append("<div class=\"table-responsive\">");
                    html.append("<table class=\"table\">");
                    html.append("<thead class=\" text-primary\">");
//                    html.append("<th>VENDEDOR</th>");
                    html.append("<th>N° FACTURA</th>");
                    html.append("<th>FECHA EMISIÓN</th>");
                    html.append("<th>CLAVE ACCESO</th>");
                    html.append("<th>SUBTOTAL</th>");
                    html.append("<th>DESCUENTO</th>");
                    html.append("<th>IVA</th>");
                    html.append("<th>TOTAL</th>");
                    html.append("<th>&nbsp;&nbsp;</th>");
                    html.append("</thead><tbody>");
                    int i = 1;
                    while (rs.next()) {
                        html.append("<tr>");
                        String id_factura_venta = (rs.getString("id_factura_venta") != null ? rs.getString("id_factura_venta") : "");
                        String clave_acceso = (rs.getString("clave_acceso") != null ? rs.getString("clave_acceso") : "");
                        String estado_documento = (rs.getString("estado_documento") != null ? rs.getString("estado_documento") : "");
//                        html.append("<td>" + (rs.getString("vendedor") != null ? rs.getString("vendedor") : "") + "</td>");
                        html.append("<td>" + (rs.getString("numero_factura") != null ? rs.getString("numero_factura") : "") + "</td>");
                        html.append("<td>" + (rs.getString("fecha_emision") != null ? rs.getString("fecha_emision") : "") + "</td>");
                        html.append("<td>" + (rs.getString("clave_acceso") != null ? rs.getString("clave_acceso") : "") + "</td>");
                        html.append("<td>" + (rs.getString("subtotal") != null ? rs.getString("subtotal") : "") + "</td>");
                        html.append("<td>" + (rs.getString("descuento") != null ? rs.getString("descuento") : "") + "</td>");
                        html.append("<td>" + (rs.getString("iva_2") != null ? rs.getString("iva_2") : "") + "</td>");
                        html.append("<td>" + (rs.getString("total") != null ? rs.getString("total") : "") + "</td>");
                        html.append("<td>");
                        if (estado_documento.compareTo("p") != 0) {
                            html.append("<a href=\"javascript:;\" id='btn_pdff" + i + "' class=\"btn btn-primary btn-round\" style=\"background:#16c65b\" onclick=\"fac_genXmlSri('" + clave_acceso + "', '1','pdf','f');\">PDF</a>");
                            html.append("<a href=\"javascript:;\" id='btn_xmlf" + i + "' class=\"btn btn-primary btn-round\" style=\"background:#16c65b\" onclick=\"fac_genXmlSri('" + id_factura_venta + "', '" + id_cliente + "','xml','f');\">XML</a>");
                        }
                        html.append("</td>");
                        html.append("</tr>");
                        i++;
                    }
                    html.append("</tbody>");
                    html.append("</table>");
                    html.append("</div>");
                } catch (Exception e) {
                    System.out.println("error");
                } finally {
                    ObjFacturaVenta.cerrar();
                }
            } else if (op.compareTo("n") == 0) {
                FacturaVenta ObjFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
                String fi = request.getParameter("fi");
                String ff = request.getParameter("ff");
                String numero = request.getParameter("numero");
                String objeto = request.getParameter("obj");
                html.append("obj»" + objeto + "^frm»");
                try {
                    ResultSet rs = ObjFacturaVenta.getNotasCreditoVentaCliente(id_cliente, fi, ff, numero);
                    html.append("<div class=\"table-responsive\">");
                    html.append("<table class=\"table\">");
                    html.append("<thead class=\" text-primary\">");
//                    html.append("<th>VENDEDOR</th>");
                    html.append("<th>N° NOTA CREDITO</th>");
                    html.append("<th>FECHA EMISIÓN</th>");
                    html.append("<th>CLAVE ACCESO</th>");
                    html.append("<th>SUBTOTAL</th>");
                    html.append("<th>DESCUENTO</th>");
                    html.append("<th>IVA</th>");
                    html.append("<th>TOTAL</th>");
                    html.append("<th>&nbsp;&nbsp;</th>");
                    html.append("</thead><tbody>");
                    int i = 1;
                    while (rs.next()) {
                        html.append("<tr>");
                        String id_nota_credito_venta = (rs.getString("id_nota_credito_venta") != null ? rs.getString("id_nota_credito_venta") : "");
                        String clave_acceso = (rs.getString("clave_acceso") != null ? rs.getString("clave_acceso") : "");
                        String estado_documento = (rs.getString("estado_documento") != null ? rs.getString("estado_documento") : "");
//                        html.append("<td>" + (rs.getString("vendedor") != null ? rs.getString("vendedor") : "") + "</td>");
                        html.append("<td>" + (rs.getString("numero_nota") != null ? rs.getString("numero_nota") : "") + "</td>");
                        html.append("<td>" + (rs.getString("fecha_emision") != null ? rs.getString("fecha_emision") : "") + "</td>");
                        html.append("<td>" + (rs.getString("clave_acceso") != null ? rs.getString("clave_acceso") : "") + "</td>");
                        html.append("<td>" + (rs.getString("valor") != null ? rs.getString("valor") : "") + "</td>");
                        html.append("<td>" + (rs.getString("descuento") != null ? rs.getString("descuento") : "") + "</td>");
                        html.append("<td>" + (rs.getString("iva_2") != null ? rs.getString("iva_2") : "") + "</td>");
                        html.append("<td>" + (rs.getString("total") != null ? rs.getString("total") : "") + "</td>");
                        html.append("<td>");
                        if (estado_documento.compareTo("p") != 0) {
                            html.append("<a href=\"javascript:;\" id='btn_pdfn" + i + "' class=\"btn btn-primary btn-round\" style=\"background:#16c65b\" onclick=\"fac_genXmlSri('" + clave_acceso + "', '4','pdf','n');\">PDF</a>");
                            html.append("<a href=\"javascript:;\" id='btn_xmln" + i + "' class=\"btn btn-primary btn-round\" style=\"background:#16c65b\" onclick=\"fac_genXmlSri('" + id_nota_credito_venta + "', '" + id_cliente + "','xml','n');\">XML</a>");
                        }
                        html.append("</td>");
                        html.append("</tr>");
                        i++;
                    }
                    html.append("</tbody>");
                    html.append("</table>");
                    html.append("</div>");
                } catch (Exception e) {
                    System.out.println("error");
                } finally {
                    ObjFacturaVenta.cerrar();
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
