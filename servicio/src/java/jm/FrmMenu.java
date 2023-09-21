/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.pag.clas.Notificacion;
import jm.web.Correo;
import jm.web.Fecha;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmMenu extends HttpServlet {

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
        String email = (String) sesion.getAttribute("email_noti");
        String razon_social = (String) sesion.getAttribute("razon_social");
        String conteomail = (String) sesion.getAttribute("conteomail");
        boolean clave_temporal = (boolean) sesion.getAttribute("clave_temporal");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        Notificacion ObjNotificacion = new Notificacion(this._ip, this._puerto, this._db, this._usuario, this._clave);

        try {
            String funcion = "^fun»";
            if (clave_temporal) {
                funcion += "seg_cambiarclave();";
            }
            html.append("obj»div_menu^foc»" + funcion + "^frm»");
            html.append("<div class=\"user\">");
            html.append("<div class=\"user-info\">");
            html.append("<a data-toggle=\"collapse\" href=\"#divperfil\" class=\"username\">");
            html.append("<span>");
            html.append(razon_social);
            html.append("<b class=\"caret\"></b>");
            html.append("</span>");
            html.append("</a>");
            html.append("<div class=\"collapse\" id=\"divperfil\">");
            html.append("<ul class=\"nav\">");
            html.append("<li class=\"nav-item\">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"seg_perfilcliente();\" >");
            html.append("<i class=\"material-icons\">account_circle</i>");
            html.append("<span class=\"sidebar-normal\"> Perfil </span>");
            html.append("</a>");
            html.append("</li>");
            html.append("<li class=\"nav-item\">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"seg_cambiarclave();\" >");
            html.append("<i class=\"material-icons\">vpn_key</i>");
            html.append("<span class=\"sidebar-normal\"> Cambiar clave </span>");
            html.append("</a>");
            html.append("</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<ul class='nav'>");
            html.append("<li class='nav-item  '>");
            html.append("<a class='nav-link' href='index.html'>");
            html.append("<i class='material-icons'>home</i>");
            html.append("<p>Inicio</p>");
            html.append("</a>");
            html.append("</li>");
            /////////
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link collapsed\" data-toggle=\"collapse\" href=\"#mencontratos\" aria-expanded=\"false\">");
            html.append("<i class=\"material-icons\">insert_chart_outlined</i>");
            html.append("<p>Contratar Servicios");
            html.append("<b class=\"caret\"></b>");
            html.append("</p>");
            html.append("</a>");
            html.append("<div class=\"collapse\" id=\"mencontratos\" style=\"\">");
            html.append("<ul class=\"nav\">");
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"con_contratarcarga();\">");
            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">devices_other</i>Contratar un nuevo servicio</span>");
            html.append("</a>");
            html.append("</li>");
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"con_buscarcontratoshechos('c');\">");
            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">developer_board</i>Mis servicios contratados</span>");
            html.append("</a>");
            html.append("</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</li>");
            /////////
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link collapsed\" data-toggle=\"collapse\" href=\"#menpagos\" aria-expanded=\"false\">");
            html.append("<i class=\"material-icons\">attach_money</i>");
            html.append("<p>Facturacion");
            html.append("<b class=\"caret\"></b>");
            html.append("</p>");
            html.append("</a>");
            html.append("<div class=\"collapse\" id=\"menpagos\" style=\"\">");
            html.append("<ul class=\"nav\">");
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"fac_documentoselectronicos();\">");
            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">receipt</i>Documentos Electrónicos </span>");
            html.append("</a>");
            html.append("</li>");
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"pag_PagosPendiente('p')\">");
            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">payment</i> Reporte sus pagos </span>");
            html.append("</a>");
            html.append("</li>");
//            html.append("<li class=\"nav-item \">");
//            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"pag_consultarpago()\">");
//            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">record_voice_over</i> Tus reportes </span>");
//            html.append("</a>");
//            html.append("</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</li>");
            //////soporte
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link collapsed\" data-toggle=\"collapse\" href=\"#mensoporte\" aria-expanded=\"false\">");
            html.append("<i class=\"material-icons\">contact_support</i>");
            html.append("<p>Notificaciones");
            html.append("<b class=\"caret\"></b>");
            html.append("</p>");
            html.append("</a>");
            html.append("<div class=\"collapse\" id=\"mensoporte\" style=\"\">");
            html.append("<ul class=\"nav\">");
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"pag_PagosPendiente('r')\">");
            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">art_track</i> Soporte técnico </span>");
            html.append("</a>");
            html.append("</li>");
            html.append("<li class=\"nav-item \">");
            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"pag_PagosPendiente('s')\">");
            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">block</i> Suspención de servicio </span>");
            html.append("</a>");
            html.append("</li>");
//            html.append("<li class=\"nav-item \">");
//            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"rec_consultarreclamos()\">");
//            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">hearing</i> Tus reclamos </span>");
//            html.append("</a>");
//            html.append("</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</li>");
            ///////
            //////soporte
//            html.append("<li class=\"nav-item \">");
//            html.append("<a class=\"nav-link collapsed\" data-toggle=\"collapse\" href=\"#mensuspencion\" aria-expanded=\"false\">");
//            html.append("<i class=\"material-icons\">cast_connected</i>");
//            html.append("<p>Suspencion de servicio");
//            html.append("<b class=\"caret\"></b>");
//            html.append("</p>");
//            html.append("</a>");
//            html.append("<div class=\"collapse\" id=\"mensuspencion\" style=\"\">");
//            html.append("<ul class=\"nav\">");
//            html.append("<li class=\"nav-item \">");
//            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"pag_PagosPendiente('s')\">");
//            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">block</i> Nuevo suspencion </span>");
//            html.append("</a>");
//            html.append("</li>");
//            html.append("<li class=\"nav-item \">");
//            html.append("<a class=\"nav-link\" href=\"javascript:void(0);\" onclick=\"sus_buscarsuspenciones();\">");
//            html.append("<span class=\"sidebar-normal\"><i class=\"material-icons\">notes</i> Tus suspenciones </span>");
//            html.append("</a>");
//            html.append("</li>");
//            html.append("</ul>");
//            html.append("</div>");
//            html.append("</li>");
            ///////
            html.append("<div class='dropdown-divider'></div>");
            html.append("<li class='nav-item '>");
            html.append("<a class='nav-link' href='Salir'>");
            html.append("<i class='material-icons'>settings_power</i>");
            html.append("<p>Cerrar Sesion</p>");
            html.append("</a>");
            html.append("</li>");
            html.append("</ul>");
            if (conteomail.compareTo("0") == 0) {
                sesion.setAttribute("conteomail", "1");
                Properties parametros = new Properties();
                parametros.setProperty("starttls", "true");
                StringBuilder mensaje = new StringBuilder();
                String plantilla = ObjNotificacion.getNotificacionHtml("inicio_sesion");
                String email_notificacion = ObjNotificacion.getNotificacionEmail("mail_notificacion_inicios");
                String fecha = Fecha.getFecha("ISO");
                fecha = Fecha.getDiaSemana(fecha) + " ," + Fecha.getFechaSolicitud(fecha) + " " + Fecha.getHora();
                plantilla = plantilla.replaceAll("#fecha_actual#", fecha);
                plantilla = plantilla.replaceAll("#cliente_actual#", razon_social);
                plantilla = plantilla.replaceAll("#ip#", request.getRemoteAddr());
                mensaje.append(plantilla);
                Correo.enviar(this._mail_svr, this._mail_puerto, this._mail_remitente, this._mail_remitente_clave, email, "", email_notificacion, "INICIO DE SESION EN TU CUENTA", mensaje, true, null, parametros);
            }

        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjNotificacion.cerrar();
            out.print(html);
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
