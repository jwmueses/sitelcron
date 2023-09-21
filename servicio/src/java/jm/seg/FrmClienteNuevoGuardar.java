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
import jm.seg.clas.Cliente;
import jm.seg.clas.Usuario;
import jm.web.Correo;
import jm.web.Fecha;
import jm.web.Utils;

/**
 *
 * @author wilso
 */
public class FrmClienteNuevoGuardar extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        Usuario ObjUsuario = new Usuario(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Notificacion ObjNotificacion = new Notificacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Cliente ObjCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                    + "contáctese con el administrador del sistema para mayor información.";
            String esnu = request.getParameter("esnu");
            String id = request.getParameter("idnu");
            String tipo_documentonu = request.getParameter("tipo_documentonu");
            String runu = request.getParameter("runu");
            String rsnu = request.getParameter("rsnu");
            String tenu = request.getParameter("tenu");
            String te_clnu = request.getParameter("te_clnu");
            String emailnu = request.getParameter("emailnu");
            String direccionnu = request.getParameter("direccionnu");
            String codigo = Utils.codigoenlace(40);
            String emailtmp = "";
            boolean ok = true;
            if (esnu.trim().compareTo("") == 0 && id.trim().compareTo("") == 0) {
                esnu = "001";
                id = ObjCliente.setCliente(esnu, tipo_documentonu, runu, rsnu, tenu, te_clnu, direccionnu, emailnu);
                emailtmp = emailnu;
            } else {
                int oki = ObjCliente.ExisteClienteId(id);
                if (oki > 0) {
                    ObjCliente.uptCliente(id, tenu, te_clnu);
                    emailtmp = ObjUsuario.getCorreoCienteActual(id);
                } else {
                    id = "-1";
                }
            }
            if (id.compareTo("-1") != 0) {
                if (emailtmp.compareTo(emailnu) == 0 || emailtmp.trim().compareTo("") == 0) {
                    String usuario = esnu + "" + runu;
                    String clave = Utils.codigoenlace(15);
                    String pk = ObjUsuario.setClienteSocio(id, "0", emailnu, usuario, clave, codigo, "true", "n");
                    if (pk.compareTo("-1") != 0) {
                        Properties parametros = new Properties();
                        parametros.setProperty("starttls", "true");
                        StringBuilder mensaje = new StringBuilder();
                        String plantilla = ObjNotificacion.getNotificacionHtml("activar_cuenta");
                        String email_notificacion = ObjNotificacion.getNotificacionEmail("mail_notificacion_nuevos");
                        String fecha = Fecha.getFecha("ISO");
                        fecha = Fecha.getDiaSemana(fecha) + " ," + Fecha.getFechaSolicitud(fecha) + " " + Fecha.getHora();
                        plantilla = plantilla.replaceAll("#fecha_actual#", fecha);
                        plantilla = plantilla.replaceAll("#cliente_actual#", rsnu);
                        plantilla = plantilla.replaceAll("#usuario_actual#", usuario);
                        plantilla = plantilla.replaceAll("#clave_actual#", clave);
                        plantilla = plantilla.replaceAll("#activa_cuenta#", " <a href=\"" + Utils.getCurrentUrl(request, "codec=" + codigo + "&tok=" + Fecha.getHoraMili().replaceAll(":", "") + "&ex=" + pk).replaceAll("FrmClienteNuevoGuardar", "FrmUsuarioNuevoGuardar") + "\">Activar cuenta ahora</a>"
                                + "<br> Nota: Este enlace tiene una duración de 48 horas si usted no activo su cuenta todos los datos se eliminaran y tendra que repetir el proceso de registro");
                        mensaje.append(plantilla);
                        ok = Correo.enviar(this._mail_svr, this._mail_puerto, this._mail_remitente, this._mail_remitente_clave, emailnu, "", email_notificacion, "CONFIRMACION DE ACTIVACIÓN DE CUENTA", mensaje, true, null, parametros);
                        if (ok) {
                            r = "msg»Se ha enviado un mensaje de confirmacion ha su cuenta de correo.^fun»limpiar('n');";
                            ObjAuditoria.setRegistro(request, "PORTAL WEB REGISTRO DE NUEVO USUARIO: " + rsnu + " EMAIL " + emailnu);
                        } else {
                            ObjUsuario.ejecutar("delete from tbl_cliente_portal where id_cliente_portal='" + pk + "';");
                        }
                    }
                } else {
                    r = "msg»Las cuentas de correo no son iguales con nuestra informacion.^fun»limpiar('n');";
                }
            }
            out.print(r);
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjUsuario.cerrar();
            ObjNotificacion.cerrar();
            ObjAuditoria.cerrar();
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
