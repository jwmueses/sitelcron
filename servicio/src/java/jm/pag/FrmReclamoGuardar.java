/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import jm.pag.clas.Notificacion;
import jm.pag.clas.Pagos;
import jm.pag.clas.Reclamos;
import jm.seg.clas.Auditoria;
import jm.web.Archivo;
import jm.web.Correo;
import jm.web.Fecha;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
@WebServlet("/upload")
@MultipartConfig
public class FrmReclamoGuardar extends HttpServlet {

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

        HttpSession sesion = request.getSession(true);
        String cliente = (String) sesion.getAttribute("razon_social");
        String email = (String) sesion.getAttribute("email_noti");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        Reclamos ObjReclamos = new Reclamos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Archivo ObjArchivo = new Archivo(this._ip_documental, this._puerto_documental, this._db_documental, this._usuario_documental, this._clave_documental);
        Notificacion ObjNotificacion = new Notificacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        try {
            String err = "1";
            String id_instalacion = request.getParameter("id_instalacion");
            String indice = request.getParameter("indice");
            String contacto = request.getParameter("contacto");
            String tipo = request.getParameter("tipo");
            String codigo_encuesta = request.getParameter("codigo_encuesta");
            codigo_encuesta = codigo_encuesta.trim().compareTo("") != 0 ? "'" + codigo_encuesta + "'" : "NULL";
            String descripcion = request.getParameter("descripcion");
            boolean ok = true;
            File file = null;
            String nombre_nuevo = "";
            if (tipo.compareTo("1") == 0 || tipo.compareTo("3") == 0) {
                Part filePart = request.getPart("file");
                String nombre_imagen = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                nombre_nuevo = "saitel" + Fecha.getFecha("ISO") + "" + Fecha.getHoraMili();
                nombre_nuevo = (nombre_nuevo.replaceAll("-", "").replaceAll(":", "")) + "." + FilenameUtils.getExtension(nombre_imagen);
                String extension = FilenameUtils.getExtension(nombre_imagen);
                extension = extension.toLowerCase();
                if (extension.compareTo("jpg") == 0 || extension.compareTo("jpeg") == 0 || extension.compareTo("png") == 0 || extension.compareTo("pdf") == 0 || extension.compareTo("docx") == 0 || extension.compareTo("doc") == 0) {
                    InputStream fileContent = filePart.getInputStream();
                    file = new File(this._dir + "" + nombre_nuevo);
                    FileUtils.copyInputStreamToFile(fileContent, file);
                } else {
                    msg = "Solo se permite formatos (jpg - jpeg - png - pdf - docx - doc)";
                    ok = false;
                }
            }
            if (ok) {
                ResultSet rs = ObjReclamos.getInstalacion(id_instalacion);
                if (rs != null) {
                    if (rs.next()) {
                        String estado_servicio = (rs.getString("estado_servicio") != null ? rs.getString("estado_servicio") : "");
                        String txt_estado_servicio = (rs.getString("txt_estado_servicio") != null ? rs.getString("txt_estado_servicio") : "");
                        String razon_social = (rs.getString("razon_social") != null ? rs.getString("razon_social") : "");
                        String id_sucursal = (rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "");

                        if (estado_servicio.compareTo("p") == 0) {
                            msg = "Su servicio esta por instalar";
                        } else if (estado_servicio.compareTo("s") == 0) {
                            msg = "Su servicio esta suspendido";
                        } else if (estado_servicio.compareTo("c") == 0) {
                            msg = "Su servicio esta cortado";
                        } else if (estado_servicio.compareTo("n") == 0) {
                            msg = "Su servicio esta en central de riesgos";
                        } else if (estado_servicio.compareTo("a") == 0) {
                            ResultSet soporte = ObjReclamos.getSoportePendiente(id_instalacion);
                            if (soporte != null) {
                                if (soporte.next()) {
                                    id_sucursal = (soporte.getString("id_sucursal") != null ? soporte.getString("id_sucursal") : "");
                                    String num_soporte = (soporte.getString("num_soporte") != null ? soporte.getString("num_soporte") : "");
                                    msg = "Usted ya tiene Generado una orden de soporte, su número de ticket es es: " + id_sucursal + "-" + num_soporte + ".";
                                    soporte.close();
                                } else {
                                    String pk = ObjReclamos.setSoporte(id_instalacion, id_sucursal, razon_social, contacto, descripcion, tipo, codigo_encuesta);
                                    if (pk.compareTo("-1") != 0) {
                                        err = "0";
                                        if (file != null) {
                                            if (ObjArchivo.setArchivoDocumental("tbl_soporte", pk, "imgvelocidad", nombre_nuevo, file, "public", "db_ip")) {
                                            } else {
                                                err = "1";
                                                ObjReclamos.ejecutar("delete from tbl_soporte where id_soporte='" + pk + "';");
                                                ok = false;
                                            }
                                        }
                                        if (ok) {
                                            Properties parametros = new Properties();
                                            parametros.setProperty("starttls", "true");
                                            StringBuilder mensaje = new StringBuilder();
                                            String plantilla = ObjNotificacion.getNotificacionHtml("reporte_soporte");
                                            String email_notificacion = ObjNotificacion.getNotificacionEmail("mail_notificacion_soportes", id_sucursal);
                                            String fecha = Fecha.getFecha("ISO");
                                            fecha = Fecha.getDiaSemana(fecha) + " ," + Fecha.getFechaSolicitud(fecha) + " " + Fecha.getHora();
                                            plantilla = plantilla.replaceAll("#fecha_actual#", fecha);
                                            plantilla = plantilla.replaceAll("#cliente_actual#", cliente);
                                            plantilla = plantilla.replaceAll("#numero_contacto#", contacto);
                                            plantilla = plantilla.replaceAll("#problema#", descripcion);
                                            mensaje.append(plantilla);
                                            Correo.enviar(this._mail_svr, this._mail_puerto, this._mail_remitente, this._mail_remitente_clave, email, "", email_notificacion, "REPORTE DE RECLAMO PORTA WEB", mensaje, true, null, parametros);
                                            ObjAuditoria.setRegistro(request, "PORTAL WEB REPORTE DE RECLAMO DEL USUARIO: " + cliente + " EMAIL " + email + " ID " + pk);
                                            msg = "Se ha reportado su reclamo. En instante se verificara y nos comunicaremos con usted.";
                                        }
                                    }
                                }
                            }
                        } else {
                            msg = "No puede reportar un reclamo a este servicio porque esta en un estado " + txt_estado_servicio + " contacte a soporte tecnico.";
                        }
                        rs.close();
                    }
                }

            }
            String r = "<script language='javascript' type='text/javascript'>window.top.window.finTransferenciareclamo(" + err + ", '" + msg + "');</script>";
            out.print(r);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjReclamos.cerrar();
            ObjArchivo.cerrar();
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
