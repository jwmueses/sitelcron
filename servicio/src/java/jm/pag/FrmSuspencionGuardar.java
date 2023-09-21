/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.pag.clas.Instalacion;
import jm.pag.clas.Notificacion;
import jm.seg.clas.Auditoria;
import jm.web.Correo;
import jm.web.Fecha;

/**
 *
 * @author wilso
 */
public class FrmSuspencionGuardar extends HttpServlet {

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
        Instalacion ObjInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Notificacion ObjNotificacion = new Notificacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String r = "msg»La fecha y/o el tiempo de suspensión entra en conflicto con otra existente.";
        StringBuilder html = new StringBuilder();
        String id = request.getParameter("id");
        String id_instalacion = request.getParameter("id_instalacion");
        int tiempo = Integer.parseInt(request.getParameter("tiempo"));
        String tipo = (tiempo == -1 || tiempo == -2 ? "d" : "t");
        String fecha_inicio = request.getParameter("fecha_inicio");
        String descripcion = request.getParameter("descripcion");
        int anio = Fecha.datePart("anio", fecha_inicio);
        int mes = Fecha.datePart("mes", fecha_inicio);
        int dia = Fecha.datePart("dia", fecha_inicio);
        String fecha_periodo = Fecha.getFecha("ISO");
        fecha_inicio = anio + "-" + mes + "-" + dia;
        String periodo = anio + "-" + mes + "-01";
        if (tipo.compareTo("t") == 0) {
            fecha_inicio = anio + "-" + mes + "-01";
        }

        if (tipo.compareTo("t") == 0) {
            mes += tiempo;
            if (mes > 12) {
                anio++;
                mes -= 12;
            }
        }
        String fecha_termino = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        List sql = null;
        try {
            if (id.compareTo("-1") == 0) {
                sql = new ArrayList();
                int tiempos = ObjInstalacion.getSuspencionAnio(id_instalacion);
                tiempos = tiempos + (tiempo + 1);
                if (tiempos <= 3) {
                    if (!ObjInstalacion.enConflictoSuspension(id, id_instalacion, fecha_inicio)) {
                        r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                                + "contáctese con el administrador del sistema para mayor información.";
                        String id_prefactura = ObjInstalacion.getPrefacturaSuspencion(periodo, id_instalacion);
                        String id_sucursal = ObjInstalacion.getPrefacturaSucursal(id_prefactura);
                        String pk = ObjInstalacion.setSuspension(id_instalacion, "administrador", tipo, fecha_inicio, fecha_termino, tiempo, descripcion, id_prefactura);
                        boolean ok = true;
                        if (pk.compareTo("-1") != 0) {
                            try {
                                if (Fecha.EsMenorFecha(fecha_inicio, fecha_periodo)) {
                                    sql.add("update tbl_instalacion set estado_servicio='s' where id_instalacion='" + id_instalacion + "';");
                                }
                            } catch (Exception e) {
                                System.out.println("Error portal" + e.getMessage());
                            }
//                            sql.add("update tbl_instalacion set estado_servicio='s' where id_instalacion='" + id_instalacion + "';");
                            if (id_prefactura.compareTo("-1") != 0) {
                                ok = ObjInstalacion.nocobroemergencia(periodo);
                                if (ok) {
//                                    sql.add("delete from tbl_prefactura  where id_prefactura='" + id_prefactura + "';");
                                    sql.add("update tbl_prefactura set dias_conexion='" + dia + "',detalle_suspencion=' antes de la suspencion temporal'  where id_prefactura='" + id_prefactura + "';");
                                } else {
                                    sql.add("update tbl_prefactura set dias_conexion='" + dia + "',detalle_suspencion=' antes de la suspencion temporal'  where id_prefactura='" + id_prefactura + "';");
                                }
                            }
                            ok = ObjInstalacion.transacciones(sql);
                        }
                        if (ok && pk.compareTo("-1") != 0) {
                            String adicional = (id_prefactura.compareTo("-1") != 0 ? " Se informa que usted debe pagar " + dia + " dias por el servicio prestado antes de la suspencion " : "");
                            Properties parametros = new Properties();
                            parametros.setProperty("starttls", "true");
                            StringBuilder mensaje = new StringBuilder();
                            String plantilla = ObjNotificacion.getNotificacionHtml("reporte_suspencion");
                            String email_notificacion = ObjNotificacion.getNotificacionEmail("mail_notificacion_suspenciones", id_sucursal);
                            String fecha = Fecha.getFecha("ISO");
                            fecha = Fecha.getDiaSemana(fecha) + " ," + Fecha.getFechaSolicitud(fecha) + " " + Fecha.getHora();
                            plantilla = plantilla.replaceAll("#fecha_actual#", fecha);
                            plantilla = plantilla.replaceAll("#cliente_actual#", cliente);
                            plantilla = plantilla.replaceAll("#fecha_inicio#", fecha_inicio);
                            plantilla = plantilla.replaceAll("#fecha_fin#", fecha_termino);
                            if (id_prefactura.compareTo("-1") != 0) {
                                descripcion += "<br> Nota: se informa que usted debe pagar " + dia + " dias por el servicio prestado antes de la suspencion revise pagos pendientes";
                            }
                            plantilla = plantilla.replaceAll("#motivo#", descripcion);

                            mensaje.append(plantilla);
                            Correo.enviar(this._mail_svr, this._mail_puerto, this._mail_remitente, this._mail_remitente_clave, email, "", email_notificacion, "REPORTE DE SUSPENCION PORTA WEB", mensaje, true, null, parametros);
                            ObjAuditoria.setRegistro(request, "PORTAL WEB REPORTE DE SUSPENCION DEL USUARIO: " + cliente + " EMAIL " + email + " ID " + pk);
                            r = "msg»Su servicio se ha suspendido correctamente." + adicional + "^fun» Ventana.cerrar('cmpsuspencion');_imprimir('PdfSuspension?id=" + pk + "'); pag_PagosPendiente('s');";
                        } else {
                            ObjInstalacion.ejecutar("delete from tbl_instalacion_suspencion where id_instalacion_suspencion='" + pk + "';");
                        }
                    }
                } else {
                    r = "msg»Su servicio no puede ser suspendido por mas de tres meses al año";
                }
            }
            out.print(r);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjInstalacion.cerrar();
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
