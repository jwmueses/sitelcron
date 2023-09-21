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
import jm.seg.clas.Cliente;
import jm.web.Utils;

/**
 *
 * @author wilso
 */
public class FrmPerfilCliente extends HttpServlet {

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
        String id_cliente = (String) sesion.getAttribute("id_cliente");
        response.setContentType("Stext/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        Cliente ObjCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        StringBuilder html = new StringBuilder();
        try {
            String cedula = "";
            String establecimiento = "";
            String fecha_nacimiento = "";
            String razon_social = "";
            String direccion = "";
            String telefonos = "";
            String movil_claro = "";
            String movil_movistar = "";
            String correo_electronico = "";
            try {
                ResultSet rs = ObjCliente.getCliente(id_cliente);
                if (rs.next()) {
                    establecimiento = (rs.getString("establecimiento") != null ? rs.getString("establecimiento") : "");
                    cedula = (rs.getString("ruc") != null ? rs.getString("ruc") : "");
                    fecha_nacimiento = (rs.getString("fecha_nacimiento") != null ? rs.getString("fecha_nacimiento") : "");
                    razon_social = (rs.getString("razon_social") != null ? rs.getString("razon_social") : "");
                    direccion = (rs.getString("direccion") != null ? rs.getString("direccion") : "");
                    telefonos = (rs.getString("telefono") != null ? rs.getString("telefono") : "");
                    movil_claro = (rs.getString("movil_claro") != null ? rs.getString("movil_claro") : "");
                    movil_movistar = (rs.getString("movil_movistar") != null ? rs.getString("movil_movistar") : "");
                    correo_electronico = (rs.getString("email") != null ? rs.getString("email") : "");
                    rs.close();
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            String objeto = "obj»div_contenedor^foc»^fun»^frm»";
            html.append("<form id='FrmPerfilCliente' action='' onsubmit=\"\" autocomplete='off'> ");
            html.append("<input type='hidden' id='id_cliente' name='id_cliente_portal' value='" + id_cliente + "' />");
            html.append("<div class=\"card-header card-header-primary\">");
            html.append("<h4 class=\"card-title \">SAITEL - PERFIL DE CLIENTE</h4>");
            html.append("<p class=\"card-category\">Informacion acerca del cliente</p>");
            html.append("</div>");
            html.append("<div class=\"card-body\">");
            //////////
            html.append("<br><br><div class=\"row\">");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">ESTABLECIMIENTO</label>");
            html.append("<input type=\"text\" id='establecimiento' name='establecimiento' class=\"form-control\" required readonly value='" + establecimiento + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">CEDULA</label>");
            html.append("<input type=\"text\" id='cedula' name='cedula' class=\"form-control\" required readonly value='" + cedula + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">FECHA NACIMIENTO</label>");
            html.append("<input type=\"date\" id='fecha_nacimiento' name='fecha_nacimiento' class=\"form-control\" required readonly value='" + fecha_nacimiento + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-6\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">RAZON SOCIAL</label>");
            html.append("<input type=\"text\" id='razon_social' name='razon_social' class=\"form-control\" required  readonly value='" + razon_social + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-6\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">DIRECCION</label>");
            html.append("<textarea id=\"direccion\" name=\"direccion\" cols=\"30\" rows=\"5\" placeholder=\"Descríbanos su direccion.\" class=\"form-control\" required readonly>" + direccion + "</textarea>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">TELEFONOS</label>");
            html.append("<input type=\"text\" id='telefonos' name='telefonos' class=\"form-control\" required value='" + telefonos + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">MOVIL CLARO</label>");
            html.append("<input type=\"text\" id='movil_claro' name='movil_claro' class=\"form-control\" required value='" + movil_claro + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">MOVIL MOVISTAR</label>");
            html.append("<input type=\"text\" id='movil_movistar' name='movil_movistar' class=\"form-control\" required value='" + movil_movistar + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-6\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">CORREO ELECTRONICO</label>");
            html.append("<input type=\"email\" id='correo_electronico' name='correo_electronico' class=\"form-control\" required value='" + correo_electronico + "' >");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ////
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
//            html.append("<button type=\"submit\" class=\"btn btn-primary pull-right\" id='btn_imprimir' onclick=\"\" >GUARDAR</button>");
            html.append("</div>");
            html.append("</div>");

            ///
            html.append("</div>");
            html.append("</form>");
            out.println(Utils.putcardbody(objeto, html.toString()));
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjCliente.cerrar();
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
