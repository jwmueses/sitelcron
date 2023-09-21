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
import jm.pag.clas.Reclamos;
import jm.web.Archivo;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmReclamoInfo extends HttpServlet {

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
        Reclamos ObjReclamos = new Reclamos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Archivo ObjArchivo = new Archivo(this._ip_documental, this._puerto_documental, this._db_documental, this._usuario_documental, this._clave_documental);
        String msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        StringBuilder html = new StringBuilder();
        String id = request.getParameter("id");
        String ruc = "";
        String razon_social = "";
        String sector = "";
        String ciudad = "";
        String direccion = "";
        String direccion_instalacion = "";
        String telefono = "";
        String movil_claro = "";
        String movil_movistar = "";
        String plan = "";
        String txt_fecha_llamada = "";
        String quien_llama = "";
        String telefono_llama = "";
        String problema = "";
        String diagnostico = "";
        String txt_fecha_solucion = "";
        String txt_estado = "";
        String numero_soportei = "";
        String id_orden_trabajo = "";
        try {
            html.append("obj»cmpreclamo_html^foc»^fun»^frm»");
            if (id.compareTo("-1") != 0) {
                ResultSet rs = ObjReclamos.getSoporte(id);
                if (rs.next()) {
                    ruc = (rs.getString("ruc") != null ? rs.getString("ruc") : "");
                    razon_social = (rs.getString("razon_social") != null ? rs.getString("razon_social") : "");
                    sector = (rs.getString("sector") != null ? rs.getString("sector") : "");
                    ciudad = (rs.getString("ciudad") != null ? rs.getString("ciudad") : "");
                    direccion = (rs.getString("direccion") != null ? rs.getString("direccion") : "");
                    direccion_instalacion = (rs.getString("direccion_instalacion") != null ? rs.getString("direccion_instalacion") : "");
                    telefono = (rs.getString("telefono") != null ? rs.getString("telefono") : "");
                    movil_claro = (rs.getString("movil_claro") != null ? rs.getString("movil_claro") : "");
                    movil_movistar = (rs.getString("movil_movistar") != null ? rs.getString("movil_movistar") : "");
                    plan = (rs.getString("plan") != null ? rs.getString("plan") : "");
                    txt_fecha_llamada = (rs.getString("txt_fecha_llamada") != null ? rs.getString("txt_fecha_llamada") : "");
                    quien_llama = (rs.getString("quien_llama") != null ? rs.getString("quien_llama") : "");
                    telefono_llama = (rs.getString("telefono_llama") != null ? rs.getString("telefono_llama") : "");
                    problema = (rs.getString("problema") != null ? rs.getString("problema") : "");
                    diagnostico = (rs.getString("diagnostico") != null ? rs.getString("diagnostico") : "");
                    txt_fecha_solucion = (rs.getString("txt_fecha_solucion") != null ? rs.getString("txt_fecha_solucion") : "");
                    txt_estado = (rs.getString("txt_estado") != null ? rs.getString("txt_estado") : "");
                    numero_soportei = (rs.getString("numero_soportei") != null ? rs.getString("numero_soportei") : "");
                    id_orden_trabajo = ObjReclamos.TieneOrdenSoporte(id);
                }
            }
            String id_sucursal_orden = "";
            String numero_orden = "";
            String txt_tipo_trabajo = "";
            String diagnostico_tecnico = "";
            String recomendacion = "";
            String responsable = "";
            String txt_estado_orden = "";
            try {
                ResultSet rs = ObjReclamos.getOrdenTrabajo(id_orden_trabajo);
                if (rs.next()) {
                    id_sucursal_orden = (rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "");
                    numero_orden = (rs.getString("num_orden") != null ? rs.getString("num_orden") : "");
                    txt_tipo_trabajo = (rs.getString("txt_tipo_trabajo") != null ? rs.getString("txt_tipo_trabajo") : "");
                    diagnostico_tecnico = (rs.getString("diagnostico_tecnico") != null ? rs.getString("diagnostico_tecnico") : "");
                    recomendacion = (rs.getString("recomendacion") != null ? rs.getString("recomendacion") : "");
                    responsable = (rs.getString("responsable") != null ? rs.getString("responsable") : "");
                    txt_estado_orden = (rs.getString("txt_estado") != null ? rs.getString("txt_estado") : "");
                    rs.close();
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            html.append("<form id='FrmReclamoInfo' action=\"\" method=\"post\"  onsubmit=\"\" autocomplete='off'>");
            html.append("<div class='row' id='div_imprimir'>");
            html.append("<div class='col-md-12'>");
            html.append("<div class=\"card\">");
            html.append(" <div class=\"card-body\">");
            ////////////77
            html.append("<fieldset class='jm_fieldset'>");
            html.append("<legend class='jm_legend'>Ticket de soporte</legend>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-3\">");
            html.append("<h6 class=\"card-title\">N° de ticket</h6>");
            html.append("<p class=\"card-description\">" + numero_soportei + "</p>");
            html.append("</div>");
            html.append("<div class=\"col-md-3\">");
            html.append("<h6 class=\"card-title\">Fecha y hora de reporte</h6>");
            html.append("<p class=\"card-description\">" + txt_fecha_llamada + "</p>");
            html.append("</div>");
            html.append("<div class=\"col-md-3\">");
            html.append("<h6 class=\"card-title\">Persona que llama</h6>");
            html.append("<p class=\"card-description\">" + quien_llama + "</p>");
            html.append("</div>");
            html.append("<div class=\"col-md-3\">");
            html.append("<h6 class=\"card-title\">Teléfono de contacto</h6>");
            html.append("<p class=\"card-description\">" + telefono_llama + "</p>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-3\">");
            html.append("<h6 class=\"card-title\">Fecha y hora de solución</h6>");
            html.append("<p class=\"card-description\">" + txt_fecha_solucion + "</p>");
            html.append("</div>");
            html.append("<div class=\"col-md-3\">");
            html.append("<h6 class=\"card-title\">Estado de soporte</h6>");
            html.append("<p class=\"card-description\">" + txt_estado + "</p>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<h6 class=\"card-title\">Problema</h6>");
            html.append("<p class=\"card-description\">" + problema + "</p>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<h6 class=\"card-title\">Diagnostico</h6>");
            html.append("<p class=\"card-description\">" + diagnostico + "</p>");
            html.append("</div>");
            html.append("</div>");
            html.append("</fieldset>");
            ////////////77
            if (id_orden_trabajo.compareTo("") != 0) {
                html.append("<fieldset class='jm_fieldset'>");
                html.append("<legend class='jm_legend'>Orden de trabajo</legend>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">N° de orden</h6>");
                html.append("<p class=\"card-description\">" + id_sucursal_orden + " - " + numero_orden + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Tipo de trabajo</h6>");
                html.append("<p class=\"card-description\">" + txt_tipo_trabajo + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Responsable</h6>");
                html.append("<p class=\"card-description\">" + responsable + "</p>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<h6 class=\"card-title\">Estado de orden</h6>");
                html.append("<p class=\"card-description\">" + txt_estado_orden + "</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-12\">");
                html.append("<h6 class=\"card-title\">Diagnostico del tecnico</h6>");
                html.append("<p class=\"card-description\">" + diagnostico_tecnico + "</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-12\">");
                html.append("<h6 class=\"card-title\">Recomendaciones</h6>");
                html.append("<p class=\"card-description\">" + recomendacion + "</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("</fieldset>");
            }

//            html.append("<fieldset class='jm_fieldset'>");
//            html.append("<legend class='jm_legend'>Informacion Cliente - Instalacion</legend>");
//            html.append("<div class=\"row\">");
//
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Cédula o RUC</h6>");
//            html.append("<p class=\"card-description\">" + ruc + "</p>");
//            html.append("</div>");
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Cliente</h6>");
//            html.append("<p class=\"card-description\">" + razon_social + "</p>");
//            html.append("</div>");
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Sector</h6>");
//            html.append("<p class=\"card-description\">" + sector + "</p>");
//            html.append("</div>");
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Cantón o ciudad</h6>");
//            html.append("<p class=\"card-description\">" + ciudad + "</p>");
//            html.append("</div>");
//
//            html.append("</div>");
//            html.append("<div class=\"row\">");
//
//            html.append("<div class=\"col-md-6\">");
//            html.append("<h6 class=\"card-title\">Dirección del cliente</h6>");
//            html.append("<p class=\"card-description\">" + direccion + "</p>");
//            html.append("</div>");
//            html.append("<div class=\"col-md-6\">");
//            html.append("<h6 class=\"card-title\">Dirección de instalación</h6>");
//            html.append("<p class=\"card-description\">" + direccion_instalacion + "</p>");
//            html.append("</div>");
//
//            html.append("</div>");
//            html.append("<div class=\"row\">");
//
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Teléfonos</h6>");
//            html.append("<p class=\"card-description\">" + telefono + "</p>");
//            html.append("</div>");
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Móvil claro</h6>");
//            html.append("<p class=\"card-description\">" + movil_claro + "</p>");
//            html.append("</div>");
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Móvil movistar</h6>");
//            html.append("<p class=\"card-description\">" + movil_movistar + "</p>");
//            html.append("</div>");
//            html.append("<div class=\"col-md-3\">");
//            html.append("<h6 class=\"card-title\">Plan actual</h6>");
//            html.append("<p class=\"card-description\">" + plan + "</p>");
//            html.append("</div>");
//
//            html.append("</div>");
//            html.append("</fieldset>");
            ///////
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
            out.println(html.toString());
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjReclamos.cerrar();
            ObjArchivo.cerrar();
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
