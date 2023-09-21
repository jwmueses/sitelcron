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
public class FrmPagoInfo extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        Pagos ObjPagos = new Pagos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        StringBuilder html = new StringBuilder();
        String id = request.getParameter("id");
        String txtcanal_pago = "";
        String numero_documento = "";
        String txtestado_tramite = "";
        String fecha_registro = "";
        String hora_registro = "";
        String fecha_estado3 = "";
        String usuario_estado3 = "";
        String hora_estado3 = "";
        String direccion_instalacion = "";
        String txt_estado_servicio = "";
        String txtprefactura = "";
        String fecha_emision = "";
        String txt_periodo = "";

        try {
            html.append("obj»cmppago_html^foc»^fun»^frm»");
            if (id.compareTo("-1") != 0) {
                ResultSet rs = ObjPagos.getPagoWeb(id);
                if (rs.next()) {
                    txtcanal_pago = (rs.getString("txtcanal_pago") != null ? rs.getString("txtcanal_pago") : "");
                    numero_documento = (rs.getString("numero_documento") != null ? rs.getString("numero_documento") : "");
                    txtestado_tramite = (rs.getString("txtestado_tramite") != null ? rs.getString("txtestado_tramite") : "");
                    fecha_registro = (rs.getString("fecha_registro") != null ? rs.getString("fecha_registro") : "");
                    hora_registro = (rs.getString("hora_registro") != null ? rs.getString("hora_registro") : "");
                    fecha_estado3 = (rs.getString("fecha_estado3") != null ? rs.getString("fecha_estado3") : "");
                    usuario_estado3 = (rs.getString("usuario_estado3") != null ? rs.getString("usuario_estado3") : "");
                    hora_estado3 = (rs.getString("hora_estado3") != null ? rs.getString("hora_estado3") : "");
                    direccion_instalacion = (rs.getString("direccion_instalacion") != null ? rs.getString("direccion_instalacion") : "");
                    txt_estado_servicio = (rs.getString("txt_estado_servicio") != null ? rs.getString("txt_estado_servicio") : "");
                    txtprefactura = (rs.getString("txtprefactura") != null ? rs.getString("txtprefactura") : "");
                    fecha_emision = (rs.getString("fecha_emision") != null ? rs.getString("fecha_emision") : "");
                    txt_periodo = (rs.getString("txt_periodo") != null ? rs.getString("txt_periodo") : "");
                    rs.close();
                }
                html.append("<form id='FrmPagoInfo' action=\"\" method=\"post\"  onsubmit=\"\" autocomplete='off'>");
                html.append("<div class='row' id='div_imprimir'>");
                html.append("<div class='col-md-12'>");
                html.append("<div class=\"card\">");
                html.append(" <div class=\"card-body\">");
                ////
                html.append("<fieldset class='jm_fieldset'>");
                html.append("<legend class='jm_legend'>Ticket de pago</legend>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Forma de pago</h6>");
                html.append("<p class=\"card-description\">" + txtcanal_pago + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">N° documento</h6>");
                html.append("<p class=\"card-description\">" + numero_documento + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Fecha registro</h6>");
                html.append("<p class=\"card-description\">" + fecha_registro + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Hora registro</h6>");
                html.append("<p class=\"card-description\">" + hora_registro + "</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Periodo Pago</h6>");
                html.append("<p class=\"card-description\">" + txt_periodo + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Estado de factura</h6>");
                html.append("<p class=\"card-description\">" + txtprefactura + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Fecha de emision</h6>");
                html.append("<p class=\"card-description\">" + fecha_emision + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Estado servicio</h6>");
                html.append("<p class=\"card-description\">" + txt_estado_servicio + "</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Estado de Tramite</h6>");
                html.append("<p class=\"card-description\">" + txtestado_tramite + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Fecha de procesamiento</h6>");
                html.append("<p class=\"card-description\">" + fecha_estado3 + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Hora de procesamiento</h6>");
                html.append("<p class=\"card-description\">" + hora_estado3 + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Usuario de procesamiento</h6>");
                html.append("<p class=\"card-description\">" + usuario_estado3 + "</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-12\">");
                html.append("<h6 class=\"card-title\">Direccion de instalacion</h6>");
                html.append("<p class=\"card-description\">" + direccion_instalacion + "</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("</fieldset>");
                ////
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
                ////
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-12\">");
                html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_imprimir' onclick=\"imprimirdivcss('div_imprimir','a5');\" >IMPRIMIR</button>");
                html.append("</div>");
                html.append("</div>");
                ///
                html.append("</form>");
            }
            out.println(html.toString());
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjPagos.cerrar();
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
