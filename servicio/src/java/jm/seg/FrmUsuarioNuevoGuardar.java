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
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmUsuarioNuevoGuardar extends HttpServlet {

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
                String es = request.getParameter("es");
                String id = request.getParameter("id");
                String idi = request.getParameter("idi");
                String ruc = request.getParameter("ruc");
                String cliente = request.getParameter("cliente");
                String email = request.getParameter("email");
                String clave = request.getParameter("clave");
                String codigo = Utils.codigoenlace(40);
                if (codigo.compareTo("") != 0 && email.trim().compareTo("") != 0) {
                    int oki = ObjUsuario.esClienteSocioId(id);
                    if (oki == 0) {
                        String emailtmp = ObjUsuario.getCorreoCienteActual(id);
                        if (emailtmp.compareTo(email) == 0) {
                            String usuario = es + "" + ruc;
                            String pk = ObjUsuario.setClienteSocio(id, idi, email, usuario, clave, codigo);
                            if (pk.compareTo("-1") != 0) {
                                Properties parametros = new Properties();
                                parametros.setProperty("starttls", "true");
                                StringBuilder mensaje = new StringBuilder();
                                String plantilla = ObjNotificacion.getNotificacionHtml("activar_cuenta");
                                String email_notificacion = ObjNotificacion.getNotificacionEmail("mail_notificacion_nuevos");
                                String fecha = Fecha.getFecha("ISO");
                                fecha = Fecha.getDiaSemana(fecha) + " ," + Fecha.getFechaSolicitud(fecha) + " " + Fecha.getHora();
                                plantilla = plantilla.replaceAll("#fecha_actual#", fecha);
                                plantilla = plantilla.replaceAll("#cliente_actual#", cliente);
                                plantilla = plantilla.replaceAll("#usuario_actual#", usuario);
                                plantilla = plantilla.replaceAll("#clave_actual#", clave);
                                plantilla = plantilla.replaceAll("#activa_cuenta#", " <a href=\"" + Utils.getCurrentUrl(request, "codec=" + codigo + "&tok=" + Fecha.getHoraMili().replaceAll(":", "") + "&ex=" + pk) + "\">Activar cuenta ahora</a>"
                                        + "<br> Nota: Este enlace tiene una duración de 48 horas si usted no activo su cuenta todos los datos se eliminaran y tendra que repetir el proceso de registro");
                                mensaje.append(plantilla);
                                boolean ok = Correo.enviar(this._mail_svr, this._mail_puerto, this._mail_remitente, this._mail_remitente_clave, email, "", email_notificacion, "CONFIRMACION DE ACTIVACIÓN DE CUENTA", mensaje, true, null, parametros);
                                if (ok) {
                                    r = "msg»Se ha enviado un mensaje de confirmacion ha su cuenta de correo.^fun»limpiar();";
                                    ObjAuditoria.setRegistro(request, "PORTAL WEB REGISTRO DE NUEVO USUARIO: " + cliente + " EMAIL " + email);
                                } else {
                                    ObjUsuario.ejecutar("delete from tbl_cliente_portal where id_cliente_portal='" + pk + "';");
                                }
                            }
                        } else {
                            r = "msg»Las cuentas de correo no son iguales con nuestra informacion.^fun»limpiar();";
                        }
                    } else {
                        r = "msg»Actualmente el cliente ya tiene una cuenta, verfique su correo electronico.^fun»limpiar();";
                    }
                }
                out.print(r);
            } else {
                int ok = ObjUsuario.esEnlaceActivo(codec, ex);
                if (ok != -1) {
                    if (ok == 0) {
                        boolean oki = ObjUsuario.esActivaEnlace(codec, ex);
                        if (oki) {
                            oki = ObjUsuario.ejecutar("update tbl_cliente_portal set confirmado=1,fecha_activacion=now(),hora_activacion=now() where codigo_enlace='" + codec + "' and id_cliente_portal='" + ex + "'; ");
                            if (oki) {
                                r = "Su cuenta ha sido activada correctamente.";
                                ObjAuditoria.setRegistro(request, "PORTAL WEB REGISTRO ACTIVACION DE CUENTA ID CLIENTE: " + ex);
                            }
                        } else {
                            oki = ObjUsuario.ejecutar("delete from tbl_cliente_portal where codigo_enlace='" + codec + "' and id_cliente_portal='" + ex + "'; ");
                            if (oki) {
                                r = "El enlace que esta tratando de abrir ha caducado vuleva a registrarse.";
                            }
                        }
                    } else {
                        r = "El enlace que esta tratando de abrir  ya ha sido utilizado.";
                    }
                } else {
                    r = "El enlace que esta tratando de abrir no existe.";
                }
                request.getRequestDispatcher("/index.jsp?msg=" + r).forward(request, response);
            }

        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjUsuario.cerrar();
            ObjNotificacion.cerrar();
            ObjAuditoria.cerrar();
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
