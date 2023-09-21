/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cont;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.cont.clas.Contrato;
import jm.cont.clas.Sector;
import jm.cont.clas.Ubicacion;
import jm.pag.clas.Instalacion;
import jm.pag.clas.Pagos;
import jm.ser.clas.Plan;
import jm.web.Archivo;
import jm.web.DatosDinamicos;

/**
 *
 * @author wilso
 */
public class FrmInstalacion extends HttpServlet {

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
        String id = request.getParameter("id");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        Instalacion ObjInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Ubicacion ObjUbicacion = new Ubicacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Sector ObjSector = new Sector(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Plan ObjPlan = new Plan(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Archivo ObjArchivo = new Archivo(this._ip_documental, this._puerto_documental, this._db_documental, this._usuario_documental, this._clave_documental);
        try {
            ResultSet rsInstalacion = ObjInstalacion.getInstalacion(id);
            String establecimiento = "";
            String ruc = "";
            String razon_social = "";
            String canton = "";
            String direccion = "";
            String telefono = "";
            String movil_claro = "";
            String movil_movistar = "";
            String ciudad = "";
            String convenio_pago = "1";
            String num_instalacion = "";
            String[][] convenio_pago1 = {{"0", "Prepago"}, {"1", "Postpago"}};
            String id_sucursal = "";
            String txt_sucursal = "";
            String tmppromocion[] = {"", "", "", "n", "n"};
            String display_promocion = "display:none;";
            String id_provincia = "";
            String id_ciudad = "";
            String id_parroquia = "";
            String id_sector = "";
            String direccion_instalacion = "";
            String tipo_cliente_plan = "c";
            String id_plan_actual = "";
            String[][] tipo_instalacion1 = {{"a", "Inal&aacute;mbrico (antena)"}, {"n", "inal&aacute;mbrico prepago (antena)"}, {"f", "Fibra"}, {"g", "gpon"}};
            String tipo_instalacion = "";
            String txt_estado_servicio = "";
            String id_contrato = "";
            try {
                if (rsInstalacion.next()) {
                    establecimiento = (rsInstalacion.getString("establecimiento") != null) ? rsInstalacion.getString("establecimiento") : "";
                    ruc = (rsInstalacion.getString("ruc") != null) ? rsInstalacion.getString("ruc") : "";
                    razon_social = (rsInstalacion.getString("razon_social") != null) ? rsInstalacion.getString("razon_social") : "";
                    direccion = (rsInstalacion.getString("direccion") != null) ? rsInstalacion.getString("direccion") : "";
                    telefono = (rsInstalacion.getString("telefono") != null) ? rsInstalacion.getString("telefono") : "";
                    movil_claro = (rsInstalacion.getString("movil_claro") != null) ? rsInstalacion.getString("movil_claro") : "";
                    movil_movistar = (rsInstalacion.getString("movil_movistar") != null) ? rsInstalacion.getString("movil_movistar") : "";
                    ciudad = (rsInstalacion.getString("ciudad") != null) ? rsInstalacion.getString("ciudad") : "";
                    num_instalacion = (rsInstalacion.getString("num_instalacion") != null) ? rsInstalacion.getString("num_instalacion") : "";
                    convenio_pago = (rsInstalacion.getString("convenio_pago") != null) ? rsInstalacion.getString("convenio_pago") : "1";
                    id_sucursal = (rsInstalacion.getString("id_sucursal") != null) ? rsInstalacion.getString("id_sucursal") : "";
                    txt_sucursal = (rsInstalacion.getString("txt_sucursal") != null) ? rsInstalacion.getString("txt_sucursal") : "";
                    id_provincia = (rsInstalacion.getString("id_provincia") != null) ? rsInstalacion.getString("id_provincia") : "";
                    id_ciudad = (rsInstalacion.getString("id_ciudad") != null) ? rsInstalacion.getString("id_ciudad") : "";
                    id_parroquia = (rsInstalacion.getString("id_parroquia") != null) ? rsInstalacion.getString("id_parroquia") : "";
                    id_sector = (rsInstalacion.getString("id_sector") != null) ? rsInstalacion.getString("id_sector") : "";
                    direccion_instalacion = (rsInstalacion.getString("direccion_instalacion") != null) ? rsInstalacion.getString("direccion_instalacion") : "";
                    id_plan_actual = (rsInstalacion.getString("id_plan_actual") != null) ? rsInstalacion.getString("id_plan_actual") : "";
                    txt_estado_servicio = (rsInstalacion.getString("txt_estado_servicio") != null) ? rsInstalacion.getString("txt_estado_servicio") : "";
                    id_contrato = (rsInstalacion.getString("id_contrato") != null) ? rsInstalacion.getString("id_contrato") : "";
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            ///datos procesador desde la instalacion
            tmppromocion = ObjInstalacion.getPromocionTiempo(id);
            display_promocion = tmppromocion[3].trim().compareTo("s") == 0 ? "" : display_promocion;
            ResultSet rsProvincias = ObjUbicacion.getUbicaciones("1");
            ResultSet rsCiudades = ObjUbicacion.getUbicaciones(id_provincia);
            ResultSet rsParroquias = ObjUbicacion.getUbicaciones(id_ciudad);
            ResultSet rsSectores = ObjSector.getSectores(id_sucursal);
            ResultSet rsPlanes = ObjPlan.getPlanes(id_sector, tipo_cliente_plan);
            html.append("obj»cmpdetalleinstalacion_html^foc»^fun»pag_formapago();^frm»");
            html.append("<form id='FrmInstalacion' >");
            html.append("<input type='hidden' id='id_instalacion' name='id_instalacion' value='" + id + "' />");
            html.append("<div class='row'>");
            html.append("<div class='col-md-12'>");
            /////
            html.append("<div class=\"card\">");
            html.append("<div class=\"card-header card-header-tabs card-header-primary\">");
            html.append("<div class=\"nav-tabs-navigation\">");
            html.append("<div class=\"nav-tabs-wrapper\">");
            html.append("<span class=\"nav-tabs-title\">#</span>");
            html.append("<ul class=\"nav nav-tabs\" data-tabs=\"tabs\">");
            html.append("<li class=\"nav-item\">");
            html.append("<a class=\"nav-link active\" href=\"#instalacion\" data-toggle=\"tab\">");
            html.append("Instalación");
            html.append("<div class=\"ripple-container\"></div>");
            html.append("</a>");
            html.append("</li>");
            html.append("<li class=\"nav-item\">");
            html.append("<a class=\"nav-link\" href=\"#contrato\" data-toggle=\"tab\">");
            html.append("Contrato");
            html.append("<div class=\"ripple-container\"></div>");
            html.append("</a>");
            html.append("</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ///
            html.append("<div class=\"tab-content\">");
            html.append("<div class=\"tab-pane active\" id=\"instalacion\">");
            html.append("<div class='content' style=''><div class='container-fluid'>");
            ///cliente
            html.append("<br>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Cédula o RUC:</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<label class=\"bmd-label-floating\">" + establecimiento + " - " + ruc + "</label>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Cliente</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<label class=\"bmd-label-floating\">" + razon_social + "</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Canton</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<label class=\"bmd-label-floating\">" + ciudad + "</label>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Telefonos</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<label class=\"bmd-label-floating\">" + telefono + " - " + movil_claro + " - " + movil_movistar + "</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Dirección</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<label class=\"bmd-label-floating\">" + direccion + "</label>");
            html.append("</div>");
            html.append("</div>");
            html.append("<hr>");
            ////instalacion
            html.append("<div class='row' style='" + display_promocion + "'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Promoción</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<label class=\"bmd-label-floating\">" + tmppromocion[2] + "</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Tiempo transcurrido</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append("<label class=\"bmd-label-floating\">" + tmppromocion[1] + "</label>");
            html.append("</div>");
            html.append("</div>");

            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Sucursal</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");;
            html.append("<label class=\"bmd-label-floating\">" + txt_sucursal + "</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Número Instalacioón</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");;
            html.append("<label class=\"bmd-label-floating\">" + id_sucursal + " - " + num_instalacion + "</label>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Convenio de cobro</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append(DatosDinamicos.combo("convenio_pago", convenio_pago, convenio_pago1, ""));
            html.append("</div>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Sector</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append(DatosDinamicos.combo(rsSectores, "id_sector", id_sector, "", " "));
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Provincia</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append(DatosDinamicos.combo(rsProvincias, "prv", id_provincia, "", ""));
            html.append("</div>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Canton</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append(DatosDinamicos.combo(rsCiudades, "ci", id_ciudad, "", ""));
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Parroquia</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append(DatosDinamicos.combo(rsParroquias, "prr", id_parroquia, "", ""));
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Dirección Instalacion</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-10\">");
            html.append("<textarea id=\"direccion_instalacion\" name=\"direccion_instalacion\" cols=\"30\" rows=\"2\"  class=\"form-control\">" + direccion_instalacion + "</textarea>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Tipo de Instalación</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append(DatosDinamicos.combo("tipo_instalacion", tipo_instalacion, tipo_instalacion1, ""));
            html.append("</div>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Plan actual vigente</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");
            html.append(DatosDinamicos.combo(rsPlanes, "id_plan_contratado", id_plan_actual, "", " "));
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Estado Servicio</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-4\">");;
            html.append("<label class=\"bmd-label-floating\">" + txt_estado_servicio + "</label>");
            html.append("</div>");
            html.append("</div>");
            html.append("<br>");
            ////
            html.append("</div></div>");
            html.append("</div>");
            html.append("<div class=\"tab-pane\" id=\"contrato\">");
            html.append("<div class='content' style=''><div class='container-fluid'>");
            Contrato ObjContrato = new Contrato(this._ip, this._puerto, this._db, this._usuario, this._clave);
            String id_sucursal_contrato = "";
            String numero_contrato = "";
            String fecha_contrato = "";
            String fecha_termino = "";
            String ruc_representante = "";
            String representante = "";
            try {
                ResultSet rsContrato = ObjContrato.getContratoInstalacion(id);
                if (rsContrato.next()) {
                    numero_contrato = rsContrato.getString("num_contrato") != null ? rsContrato.getString("num_contrato") : "";
                    id_sucursal_contrato = rsContrato.getString("id_sucursal") != null ? rsContrato.getString("id_sucursal") : "";
                    fecha_contrato = rsContrato.getString("fecha_contrato") != null ? rsContrato.getString("fecha_contrato") : "";
                    fecha_termino = rsContrato.getString("fecha_termino") != null ? rsContrato.getString("fecha_termino") : "";
                    ruc_representante = rsContrato.getString("ruc_representante") != null ? rsContrato.getString("ruc_representante") : "";
                    representante = rsContrato.getString("representante") != null ? rsContrato.getString("representante") : "";
                    rsContrato.close();
                }

            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            } finally {
                ObjContrato.cerrar();
            }
            String fichero64 = null;
            String contrato_texto = "";
            try {
                List lista = ObjArchivo.getArchivoDocumentalByteTexto("tbl_contrato", id_contrato, "contratotexto");
                if (!lista.isEmpty()) {
                    contrato_texto = (String) lista.get(0);
                    fichero64 = Base64.getEncoder().encodeToString((byte[]) lista.get(1));
                }

            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            /////
            html.append("<br>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">N° contrato</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">" + id_sucursal_contrato + " - " + numero_contrato + "</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Fecha contrato</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">" + fecha_contrato + "</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">Fecha termino</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-2\">");
            html.append("<label class=\"bmd-label-floating\">" + fecha_termino + "</label>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Ruc representante</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-10\">");
            html.append("<input type=\"text\" id='ruc_representante'name='ruc_representante' value='" + ruc_representante + "' class=\"form-control\" />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-2 margin12px\">");
            html.append("<label class=\"bmd-label-floating\">Representante</label>");
            html.append("</div>");
            html.append("<div class=\"col-md-10\">");
            html.append("<input type=\"text\" id='representante'name='representante' value='" + representante + "' class=\"form-control\" />");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='row'>");
            html.append("<div class=\"col-md-12\">");
            if (fichero64 != null) {
                html.append("<iframe  src =\"data:application/pdf;base64," + fichero64 + "\"  height=\"500px\" width=\"100%\"></iframe>");
            } else {
                html.append("<textarea  cols=\"30\" rows=\"20\"  class=\"form-control\">" + contrato_texto + "</textarea>");
            }
            html.append("</div>");
            html.append("</div>");
            html.append("<br>");
            //////
            html.append("</div></div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ///
            html.append("</div>");
            html.append("</div>");
            html.append("</form>");

            out.println(html.toString());
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            ObjInstalacion.cerrar();
            ObjUbicacion.cerrar();
            ObjSector.cerrar();
            ObjPlan.cerrar();
            ObjArchivo.cerrar();
            out.close();
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
