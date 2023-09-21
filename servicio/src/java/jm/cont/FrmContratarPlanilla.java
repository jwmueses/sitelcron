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
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.cont.clas.Promocion;
import jm.cont.clas.Ubicacion;
import jm.pag.clas.Configuracion;
import jm.ser.clas.Plan;
import jm.web.Archivo;
import jm.web.DatosDinamicos;
import jm.web.Utils;

/**
 *
 * @author wilso
 */
public class FrmContratarPlanilla extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    public void init(ServletConfig config) throws ServletException {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
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
        String parametros_url = (String) sesion.getAttribute("parametros_url");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        String objeto = "obj»div_contenedor^fun»fac_setPromocionInstalacion();^frm»";
        String id_sucursal = request.getParameter("id_sucursal");
        String id_sector = request.getParameter("id_sector");
        String id_plan = request.getParameter("id_plan");
        Ubicacion ObjUbicacion = new Ubicacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Promocion ObjPromocion = new Promocion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Archivo ObjArchivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Configuracion ObjConfiguracion = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String iva_vigente = ObjConfiguracion.getValor("p_iva1");
        ObjConfiguracion.cerrar();
        Plan ObjPlan = new Plan(this._ip, this._puerto, this._db, this._usuario, this._clave);
        ResultSet rsprecios = ObjPlan.getPrecioPlanSector(id_plan, id_sector);
        String jsonprecios = ObjPlan.getJSON(rsprecios);
        ObjPlan.cerrar();
        byte[] fichero = ObjArchivo.getArchivo(2);
        String fichero64 = null;
        try {
            fichero64 = Base64.getEncoder().encodeToString(fichero);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        ObjArchivo.cerrar();
        try {
            String id_provincia = "";
            String id_ciudad = "";
            String id_parroquia = "";
            String tipo_cliente_instalacion = "c";
            //ResultSet rsProvincias = ObjUbicacion.getUbicaciones("1");
            ResultSet rsProvincias = ObjUbicacion.getUbicacionSucursal(id_sucursal);
            ResultSet rsCiudades = ObjUbicacion.getUbicaciones(id_provincia);
            ResultSet rsParroquias = ObjUbicacion.getUbicaciones(id_ciudad);
            ResultSet rsPromociones = ObjPromocion.getPromocionesInstalaciones(Integer.parseInt(id_sucursal));
            ResultSet rsPromocionesPlanes = ObjPromocion.getPromocionesPlanes(id_sucursal, 'i');
            String[][] convenio_pago1 = {{"", " SELECCIONE "}, {"0", "Prepago"}, {"1", "Postpago"}};
            String convenio_pago = Integer.parseInt(id_sucursal) == 11 ? "0" : "1";
            html.append("<form id='FrmContratarPlanilla' action=\"FrmContratarPlanillaGuardar\" method=\"post\" enctype=\"multipart/form-data\" target='procesaTransferencia' onsubmit=\"return con_guardarcontrato(this);\" autocomplete='off'>");
            html.append("<input type='hidden' id='id_sector' name='id_sector' value='" + id_sector + "' />");
            html.append("<input type='hidden' id='id_plan' name='id_plan' value='" + id_plan + "' />");
            html.append("<input type='hidden' id='iva_vigente' name='iva_vigente' value='" + iva_vigente + "' />");
            html.append("<input type='hidden' id='id_sucursal' name='id_sucursal' value='" + id_sucursal + "' />");
            html.append("<input type='hidden' id='tiempo_permanencia' name='tiempo_permanencia' value='24' />");
            html.append("<input type='hidden' id='costo_facturado' name='costo_facturado' value='0' />");
            html.append("<div class=\"card-header card-header-primary\">");
            html.append("<h4 class=\"card-title \">PASO 1.- DIRECCION INMUEBLE QUE RECIBE EL SERVICIO  </h4>");
            html.append("</div>");
            html.append("<div class=\"card-body\">");

            html.append("<div class=\"row\">");
            /*columna uno*/
            html.append("<div class=\"col-md-6\">");
            /*contenido*/
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">PROVINCIA</label>");
            html.append("<input type='hidden' id='prv_detalle' name='prv_detalle' value=''/>");
            html.append("<div id='cob0'>" + DatosDinamicos.combo(rsProvincias, "prv", id_provincia, "_pX='getUbicacion(:cob2:,:prr:,:ci:,200);';_pY='getUbicacion(:cob2:,:prr:,:ci:,200);';getUbicacion('cob1','ci','prv',200);", " SELECCIONE ") + "</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">CANTÓN</label>");
            html.append("<input type='hidden' id='ci_detalle' name='ci_detalle' value=''/>");
            html.append("<div id='cob1'>" + DatosDinamicos.combo(rsCiudades, "ci", id_ciudad, "getUbicacion('cob2','prr','ci',200);", "") + "</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">PARROQUIA</label>");
            html.append("<input type='hidden' id='prr_detalle' name='prr_detalle' value=''/>");
            html.append("<div id='cob2'>" + DatosDinamicos.combo(rsParroquias, "prr", id_parroquia, "", "") + "</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<textarea placeholder=\"Direccion calles:\" cols=\"30\" rows=\"3\" class=\"form-control\" required id='direccion_inmuble' name='direccion_inmuble'></textarea>");
            html.append("</div>");
            html.append("</div>");
            /*fin de contenido*/
            html.append("</div>");
            /*fin columna uno
            inicio columna dos*/
            html.append("<div class=\"col-md-6\">");
            /*e contenido*/
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<label class=\"bmd-label-floating\">CROQUIS</label>");
            html.append("<div class=\"fileinput fileinput-new text-center\" data-provides=\"fileinput\">");
            html.append("<div class=\"fileinput-preview fileinput-exists thumbnail\"><img src=\"img/image_placeholder.jpg\" alt=\"\" width=\"200\"/></div>");
            html.append("<div>");
            html.append("<span class=\"btn btn-sm btn-rose btn-round btn-file\">");
            html.append("<input type=\"file\" name=\"file\" id=\"file\">");
            html.append("<div class=\"ripple-container\"></div></span>");
            html.append("<a href=\"javascript:void(0);\" class=\"btn btn-sm btn-danger btn-round fileinput-exists\" data-dismiss=\"fileinput\"><i class=\"fa fa-times\"></i>Quitar</a>");
            html.append("</div>");
            html.append("</div>");
            html.append("<a href=\"https://www.google.com/maps\" target=\"_blank\">CAPTURE SU UBICACION EXACTA CON GOOGLE MAPS</a>");
            html.append("</div>");
            html.append("</div>");
            /*fin de contenido*/
            html.append("</div>");
            /*fin columna dos*/
            html.append("</div>");
            html.append("<br><br><div class=\"card-header card-header-primary\">");
            html.append("<h4 class=\"card-title \">PASO 2.- PROMOCIONES VIGENTES Y FORMA DE PAGO</h4>");
            html.append("</div>");
            html.append("<br><div class=\"row\">");
            /*columna uno*/
            html.append("<div class=\"col-md-6\">");
            /*contenido*/
            if (ObjPromocion.getFilas(rsPromociones) > 0) {
                html.append("<input type='hidden' id='nompro' name='nompro' value=''/>");
                html.append("<input type='hidden' id='benpro' name='benpro' value=''/>");
                html.append("<input type='hidden' id='tiepro' name='tiepro' value=''/>");
                html.append("<input type='hidden' id='idprom' name='idprom' value=''/>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-12\">");
                html.append("<div class=\"form-group\">");
                html.append("<label class=\"bmd-label-floating\">PROMOCIONES</label>");
                html.append("<div id='axPromociones' style='display:none'>" + ObjPromocion.getJSON(rsPromociones) + "</div>");
                html.append("<div id='axPromocionesPlanes' style='display:none'>" + ObjPromocion.getJSON(rsPromocionesPlanes) + "</div>");
                int i = 0;
                boolean checked = false;
                int tmpi = 0;
                try {
                    while (rsPromociones.next()) {
                        String id_promocion = rsPromociones.getString("id_promocion") != null ? rsPromociones.getString("id_promocion") : "-1";
                        String promocion = rsPromociones.getString("promocion") != null ? rsPromociones.getString("promocion") : "";
                        if (!checked && tipo_cliente_instalacion.trim().compareTo("c") == 0 && id_promocion.trim().compareTo("4") != 0) {
                            checked = true;
                        }
                        html.append("<br><label><input type='radio' id='setPromo" + i + "' name='setPromo' " + (tmpi == 0 && checked ? "checked" : "") + " value='" + id_promocion + "' onclick=\"fac_setPromocionInstalacion()\" />&nbsp;&nbsp; " + promocion + "</label>");
                        if (checked) {
                            tmpi++;
                        }
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                html.append("<br><label><input type='radio' id='setPromo" + i + "' name='setPromo' " + (tmpi == 0 || tipo_cliente_instalacion.trim().compareTo("i") == 0 ? "checked" : "") + " value='-1' onclick=\"fac_setPromocionInstalacion()\" />&nbsp;&nbsp; No aplica promoci&oacute;n</label>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
            } else {
                html.append("<div id='axPromociones' style='display:none'>{tbl:]}</div>");
                html.append("<div id='axPromocionesPlanes' style='display:none'>{tbl:]}</div>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-12\">");
                html.append("<div class=\"form-group\">");
                html.append("<label class=\"bmd-label-floating\">NO EXISTE PROMOCIONES VIGENTES</label>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
            }
            /*fin de contenido*/
            html.append("</div>");
            /*fin columna uno
            inicio columna dos*/
            html.append("<div class=\"col-md-6\">");
            /*e contenido*/
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">CONVENIO DE COBRO</label>");
            html.append("<div  id='axConvenioPago'>" + DatosDinamicos.combo("convenio_pago", convenio_pago, convenio_pago1, "fac_instSetDatosFactura();") + "</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            /*fin de contenido*/
            html.append("</div>");
            /*fin columna dos*/
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div id='detalle_promocion' style='background-color: red; color:#ffffff; padding:5px;';></div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<br><br><div class=\"card-header card-header-primary\">");
            html.append("<h4 class=\"card-title \">PASO 3.- DETALLE DE FACTURACIÓN POR EL TIPO DE SERVICIO </h4>");
            html.append("<div id='axprec' style='display:none'>" + jsonprecios + "</div>");
            html.append("</div>");
            html.append("<br><div class=\"row\">");
            html.append("<div class=\"col-md-12\">");

            html.append("<div class=\"table-responsive\">");
            html.append("<table class=\"table\">");
            html.append("<thead class=\" text-primary\">");
            html.append("<th>DESCRIPCION</th>");
            html.append("<th>SUBTOTAL</th>");
            html.append("<th>DESCUENTO</th>");
            html.append("<th>IVA</th>");
            html.append("<th>TOTAL</th>");
            html.append("</thead><tbody>");
            int i = 0;
            html.append("<tr id='ite" + i + "'>");
            html.append("<td>COSTO DE INSTALACION</td>");
            html.append("<td><input type='text' id='sub" + i + "' name='sub" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("<td><input type='text' id='des" + i + "' name='des" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("<td><input type='text' id='iva" + i + "' name='iva" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("<td><input type='text' id='tot" + i + "' name='sub" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("</tr>");
            i++;
            html.append("<tr id='ite" + i + "' >");
            html.append("<td>ANTICIPO DE SERVICIO DE CONSUMO</td>");
            html.append("<td><input type='text' id='sub" + i + "' name='sub" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("<td><input type='text' id='des" + i + "' name='des" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("<td><input type='text' id='iva" + i + "' name='iva" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("<td><input type='text' id='tot" + i + "' name='sub" + i + "' value='0'  class=\"form-control\" readonly /></td>");
            html.append("</tr>");
            html.append("</tbody>");
            html.append("<tfoot>");
            html.append("<input type='hidden' id='itemh' name='itemh' value='" + i + "'/>");
            html.append("<tr>");
            html.append("<td colspan=\"3\"></td>");
            html.append("<td>SUBTOTAL</td>");
            html.append("<td><input type='text' id='fsub' name='fsub' value='0'  class=\"form-control\" readonly /></td>");
            html.append("</tr>");
            html.append("<tr>");
            html.append("<td colspan=\"3\"></td>");
            html.append("<td>DESCUENTOS</td>");
            html.append("<td><input type='text' id='fdes' name='fdes' value='0'  class=\"form-control\" readonly /></td>");
            html.append("</tr>");
            html.append("<tr>");
            html.append("<td colspan=\"3\"></td>");
            html.append("<td>IVA " + iva_vigente + " % </td>");
            html.append("<td><input type='text' id='fiva' name='fiva' value='0'  class=\"form-control\" readonly /></td>");
            html.append("</tr>");
            html.append("<tr>");
            html.append("<td colspan=\"3\"></td>");
            html.append("<td>TOTAL</td>");
            html.append("<td><input type='text' id='ftot' name='ftot' value='0'  class=\"form-control\" readonly /></td>");
            html.append("</tr>");
            html.append("</tfoot>");
            html.append("</table>");
            html.append("</div>");

            html.append("<br><br><div class=\"card-header card-header-primary\">");
            html.append("<h4 class=\"card-title \">PASO 4.- CONTRATO </h4>");
            html.append("</div>");
            html.append("<br><div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">R.U.C. / C.I. DEL REPRESENTANTE .....(OPCIONAL)</label>");
            html.append("<input type=\"text\" id='ruc_representante' name='ruc_representante' class=\"form-control\" maxlength=\"13\">");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">REPRESENTANTE .....(OPCIONAL)</label>");
            html.append("<input type=\"text\" id='representante' name='representante' class=\"form-control\">");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<iframe  src =\"data:application/pdf;base64," + fichero64 + "\"  height=\"500px\" width=\"100%\"></iframe> ");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-6\">");
            html.append("<label style='padding:14px;align=right; color:red;'> ACEPTA TODOS LOS TERMINOS Y CONDICIONES DEL CONTRATO &nbsp;&nbsp; <input type='checkbox' id='acepta_contrato' name='acepta_contrato'  value='true' required /></label>");
            html.append("</div>");
            html.append("<div class=\"col-md-6\">");
            html.append("<iframe name='procesaTransferencia' src='#' style='width:0;height:0;border:0px solid #fff;'></iframe>");
            html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_contratar' onclick=\"con_guardarcontrato(_('FrmContratarPlanilla'))\">CONTRATAR SERVICIO</button>");
            html.append("</div>");
            html.append("</div>");

            html.append("</div>");
            html.append("</div>");
            /////
            html.append("</div>");
            html.append("</form>");
        } catch (Exception e) {
            System.out.println("error al cargar el formulario" + e.getMessage());
        } finally {
            ObjUbicacion.cerrar();
            ObjPromocion.cerrar();
            out.print(Utils.putcardbody(objeto, html.toString()));
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
