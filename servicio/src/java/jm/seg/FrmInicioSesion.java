/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.pag.clas.Notificacion;
import jm.seg.clas.Auditoria;
import jm.seg.clas.Usuario;
import jm.web.Correo;
import jm.web.Fecha;
import jm.web.Utils;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmInicioSesion extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    private String _mail_svr = null;
    private int _mail_puerto = 587;
    private String _mail_remitente = null;
    private String _mail_remitente_clave = null;

    public void init(ServletConfig config) throws ServletException {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
        this._mail_svr = config.getServletContext().getInitParameter("_MAIL_SVR");
        this._mail_puerto = Integer.parseInt(config.getServletContext().getInitParameter("_MAIL_PUERTO"));
        this._mail_remitente = config.getServletContext().getInitParameter("_MAIL_REMITENTE");
        this._mail_remitente_clave = config.getServletContext().getInitParameter("_MAIL_REMITENTE_CLAVE");
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
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        String email = request.getParameter("email");
        String clave = request.getParameter("clave");
        Usuario ObjUsuario = new Usuario(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String r = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        try {
            ResultSet rs = ObjUsuario.getAccesoUsuario(email, clave);
            if (rs != null) {
                if (rs.next()) {
                    r = "Este cuenta aun no se encuentra confirmada verifique su correo electronico.";
                    boolean confirmado = (rs.getString("confirmado") != null ? rs.getBoolean("confirmado") : false);
                    if (confirmado) {
                        r = "Este cuenta se encuentra desabilitada contacte con soporte tecnico.";
                        boolean estado = (rs.getString("estado") != null ? rs.getBoolean("estado") : false);
                        if (estado) {
                            String id_cliente = (rs.getString("id_cliente") != null ? rs.getString("id_cliente") : "");
                            String ruc = (rs.getString("ruc") != null ? rs.getString("ruc") : "");
                            String id_instalacion = (rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "");
                            String razon_social0 = (rs.getString("razon_social0") != null ? rs.getString("razon_social0") : "");
                            String id_cliente_portal = (rs.getString("id_cliente_portal") != null ? rs.getString("id_cliente_portal") : "");
                            String email0 = (rs.getString("email0") != null ? rs.getString("email0") : "");
                            boolean clave_temporal = (rs.getString("clave_temporal") != null ? rs.getBoolean("clave_temporal") : false);
                            sesion.setAttribute("usuario_proceso", this._usuario);
                            sesion.setAttribute("usuario", email);
                            sesion.setAttribute("clave", clave);
                            sesion.setAttribute("id_cliente", id_cliente);
                            sesion.setAttribute("id_instalacion", id_instalacion);
                            sesion.setAttribute("ruc", ruc);
                            sesion.setAttribute("razon_social", razon_social0);
                            sesion.setAttribute("id_cliente_portal", id_cliente_portal);
                            sesion.setAttribute("email_noti", email0);
                            sesion.setAttribute("conteomail", "0");
                            sesion.setAttribute("clave_temporal", clave_temporal);
                            sesion.setAttribute("parametros_url", "");
                            ObjAuditoria.setRegistro(request, "PORTAL WEB INICIO DE SESION CLIENTE: " + razon_social0 + " EMAIL " + email0);
                            r = "0";
                        }
                    }

                    rs.close();
                } else {
                    r = "Error de credenciales.";
                }
            }
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjUsuario.cerrar();
            ObjAuditoria.cerrar();
        }
        if (r.compareTo("0") == 0) {
            response.sendRedirect("index.html");
        } else {
            r = new String(r.getBytes("UTF-8"), "ISO-8859-1");
            response.sendRedirect("index.jsp?msg=" + r);
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
