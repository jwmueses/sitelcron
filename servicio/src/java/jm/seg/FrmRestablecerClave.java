/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.pag.clas.Notificacion;
import jm.seg.clas.Auditoria;
import jm.seg.clas.Usuario;
import jm.web.Correo;
import jm.web.Fecha;
import jm.web.Utils;

/**
 *
 * @author wilso
 */
public class FrmRestablecerClave extends HttpServlet {

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
        this._ip_documental = config.getServletContext().getInitParameter("_IP_DOCUMENTAL");
        this._puerto_documental = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO_DOCUMENTAL"));
        this._db_documental = config.getServletContext().getInitParameter("_DB_DOCUMENTAL");
        this._usuario_documental = config.getServletContext().getInitParameter("_USUARIO_DOCUMENTAL");
        this._clave_documental = config.getServletContext().getInitParameter("_CLAVE_DOCUMENTAL");
        this._dir = config.getServletContext().getInitParameter("_DIR");
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
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        String codec = request.getParameter("codec") != null ? request.getParameter("codec") : "";
        String tok = request.getParameter("tok") != null ? request.getParameter("tok") : "";
        String ex = request.getParameter("ex") != null ? request.getParameter("ex") : "";
        Usuario ObjUsuario = new Usuario(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Notificacion ObjNotificacion = new Notificacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                    + "contáctese con el administrador del sistema para mayor información.";
            if (codec.trim().compareTo("") == 0 && tok.trim().compareTo("") == 0 && ex.trim().compareTo("") == 0) {
                String proceso = "FALLIDO";
                String usuarior = request.getParameter("usuarior");
                String emailr = request.getParameter("emailr");
                String ok = ObjUsuario.getCorreoCienteActualRuc(usuarior);
                if (ok.compareTo("") != 0) {
                    if (emailr.compareTo(ok) == 0) {
                        String clave = Utils.codigoenlace(10);
                        String cliente = ObjUsuario.getCampoCliente(usuarior, "razon_social");
                        Properties parametros = new Properties();
                        parametros.setProperty("starttls", "true");
                        StringBuilder mensaje = new StringBuilder();
                        String plantilla = ObjNotificacion.getNotificacionHtml("restablecer_clave");
                        String email_notificacion = ObjNotificacion.getNotificacionEmail("mail_notificacion_restablecer");
                        String fecha = Fecha.getFecha("ISO");
                        fecha = Fecha.getDiaSemana(fecha) + " ," + Fecha.getFechaSolicitud(fecha) + " " + Fecha.getHora();
                        plantilla = plantilla.replaceAll("#fecha_actual#", fecha);
                        plantilla = plantilla.replaceAll("#cliente_actual#", cliente);
                        plantilla = plantilla.replaceAll("#usuario_actual#", usuarior);
                        plantilla = plantilla.replaceAll("#clave_actual#", clave);
                        plantilla = plantilla.replaceAll("#enlace_portal#", " <a href=\"" + Utils.getUrlActual(request, "/servicio") + "\">Acceder ahora</a>");
                        mensaje.append(plantilla);
                        boolean oki = Correo.enviar(this._mail_svr, this._mail_puerto, this._mail_remitente, this._mail_remitente_clave, emailr, "", email_notificacion, "RESTABLECER CONTRASEÑA DE TU CUENTA", mensaje, true, null, parametros);
                        if (oki) {
                            proceso = "OK";
                            ObjUsuario.ejecutar("update tbl_cliente_portal set clave=md5('" + clave + "'),clave_temporal=true where usuario='" + usuarior + "';");
                            r = "msg»Se ha enviado un mensaje con una contraseña temporal.^fun»ocultarlogin('l');";
                        }
                    } else {
                        r = "msg»El correo ingresado no esta vinculado a su cuenta.";
                    }
                } else {
                    r = "msg»No se encuentra registrado ningun usuario con " + usuarior;
                }

                ObjAuditoria.setRegistro(request, "PORTAL WEB INTENTO DE CAMBIO DE CLAVE : " + usuarior + " EMAIL " + emailr + " PROCESO " + proceso);
            }
            out.print(r);
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjAuditoria.cerrar();
            ObjUsuario.cerrar();
            ObjNotificacion.cerrar();
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
