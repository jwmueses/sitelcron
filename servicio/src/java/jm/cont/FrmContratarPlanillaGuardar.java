/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cont;

import ec.gob.sri.FirmaXadesBes;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import jm.cont.clas.Contrato;
import jm.fac.clas.FacturaElectronica;
import jm.fac.clas.FacturaVenta;
import jm.cont.clas.PdfDocumental;
import jm.cont.clas.Promocion;
import jm.fac.clas.ClavesContingenciaSRI;
import jm.fac.clas.FormaPago;
import jm.pag.clas.Instalacion;
import jm.pag.clas.Notificacion;
import jm.seg.clas.Auditoria;
import jm.seg.clas.Cliente;
import jm.ser.clas.Plan;
import jm.web.Addons;
import jm.web.Archivo;
import jm.web.Cadena;
import jm.web.Correo;
import jm.web.Fecha;
import jm.web.Matriz;
import jm.web.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author wilso
 */
@WebServlet("/upload")
@MultipartConfig
public class FrmContratarPlanillaGuardar extends HttpServlet {

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
    private String _DOCS_ELECTRONICOS = "";

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
        this._DOCS_ELECTRONICOS = config.getServletContext().getInitParameter("_DOCS_ELECTRONICOS");
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
        String ruc = (String) sesion.getAttribute("ruc");
        String razon_social = (String) sesion.getAttribute("razon_social");
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
        Contrato ObjContrato = new Contrato(this._ip, this._puerto, this._db, this._usuario, this._clave);
        FacturaVenta ObjFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        try {
            String err = "1";
            String id_sector = request.getParameter("id_sector");
            String id_plan = request.getParameter("id_plan");
            String iva_vigente = request.getParameter("iva_vigente");
            String id_sucursal = request.getParameter("id_sucursal");
            String prv = request.getParameter("prv");
            String prv_detalle = request.getParameter("prv_detalle");
            String ci = request.getParameter("ci");
            String ci_detalle = request.getParameter("ci_detalle");
            String prr = request.getParameter("prr");
            String prr_detalle = request.getParameter("prr_detalle");
            String direccion_inmuble = request.getParameter("direccion_inmuble");
            Part filePart = request.getPart("file");
            String nombre_imagen = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String nombre_nuevo = "saitel" + Fecha.getFecha("ISO") + "" + Fecha.getHoraMili();
            nombre_nuevo = (nombre_nuevo.replaceAll("-", "").replaceAll(":", "")) + "." + FilenameUtils.getExtension(nombre_imagen);
            String extension = FilenameUtils.getExtension(nombre_imagen);
            extension = extension.toLowerCase();
            String nompro = (request.getParameter("nompro") != null ? request.getParameter("nompro") : "");
            String benpro = (request.getParameter("benpro") != null ? request.getParameter("benpro") : "");
            String tiepro = (request.getParameter("tiepro") != null ? request.getParameter("tiepro") : "");
            String idprom = (request.getParameter("idprom") != null ? request.getParameter("idprom") : "");
            String convenio_pago = request.getParameter("convenio_pago");
            String itemh = request.getParameter("itemh");
            String fsub = request.getParameter("fsub");
            String fdes = request.getParameter("fdes");
            String fiva = request.getParameter("fiva");
            String ftot = request.getParameter("ftot");
            String tiempo_permanencia = (request.getParameter("tiempo_permanencia") != null ? request.getParameter("tiempo_permanencia") : "24");
            String costo_facturado = (request.getParameter("costo_facturado") != null ? request.getParameter("costo_facturado") : "0");
            String representante = request.getParameter("representante");
            String ruc_representante = request.getParameter("ruc_representante");
            if (extension.compareTo("jpg") == 0 || extension.compareTo("jpeg") == 0 || extension.compareTo("png") == 0) {
                InputStream fileContent = filePart.getInputStream();
                File file = new File(this._dir + "" + nombre_nuevo);
                FileUtils.copyInputStreamToFile(fileContent, file);
                String numero_contrato = ObjContrato.getNumeroContrato(id_sucursal);
                String fecha_actual = Fecha.getFecha("ISO");
                String fecha_fin = Fecha.add(fecha_actual, Calendar.YEAR, 1);
                PdfDocumental objPdfDocumental = new PdfDocumental();
                objPdfDocumental.setConexion(_ip, _puerto, _db, _usuario, _clave, _ip_documental, _puerto_documental, _db_documental, _usuario_documental, _clave_documental);
                byte[] croquis = Files.readAllBytes(file.toPath());
                /*Paso buscar un punto de emision  */
                String matPuntosVirtuales[][] = ObjFacturaVenta.getPuntosEmisionVirtuales();
                msg = "No se ha encontrado un punto de emisiona factura";
                int p = Matriz.enMatriz(matPuntosVirtuales, id_sucursal, 0);
                if (p != -1) {
                    /*datos de cliente para facturar*/
                    Cliente ObjCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);
                    String tipo_documento_cliente = "05";
                    String direccion = "";
                    String telefono = "";
                    try {
                        ResultSet rsCliente = ObjCliente.getCliente(id_cliente);
                        if (rsCliente.next()) {
                            tipo_documento_cliente = rsCliente.getString("tipo_documento") != null ? rsCliente.getString("tipo_documento") : "05";
                            direccion = rsCliente.getString("direccion") != null ? rsCliente.getString("direccion") : "";
                            telefono = rsCliente.getString("telefono") != null ? rsCliente.getString("telefono") : "";
                            rsCliente.close();
                        }
                    } catch (Exception e) {
                        System.out.println("Error portal obtner datos del cliente :" + e.getMessage());
                    } finally {
                        ObjCliente.cerrar();
                    }
                    /*verificar si tenemos promocion*/
                    String costos[][] = null;
                    Plan ObjPlan = new Plan(this._ip, this._puerto, this._db, this._usuario, this._clave);
                    try {
                        ResultSet rsprecios = ObjPlan.getPrecioPlanSector(id_plan, id_sector);
                        costos = Matriz.ResultSetAMatriz(rsprecios);
                    } catch (Exception e) {
                        System.out.println("Error portal obtener precios de instalacion y de plan actual :" + e.getMessage());
                    } finally {
                        ObjPlan.cerrar();
                    }
                    /*verificar si tenemos promocion*/
                    String promocion[][] = null;
                    if (idprom.trim().compareTo("") != 0) {
                        Promocion ObjPromocion = new Promocion(this._ip, this._puerto, this._db, this._usuario, this._clave);
                        try {
                            ResultSet rspromocion = ObjPromocion.getPromocionPlan(id_sucursal, id_sector, id_plan, idprom);
                            promocion = Matriz.ResultSetAMatriz(rspromocion);
                        } catch (Exception e) {
                            System.out.println("Error portal consulta de promocion :" + e.getMessage());
                        } finally {
                            ObjPromocion.cerrar();
                        }
                    }
                    /*obtenemos todos los productos */
                    String productos[][] = null;
                    try {
                        ResultSet rsDetalleFactura = ObjFacturaVenta.consulta("select SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "
                                + "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"
                                + "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien  "
                                + "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                                + "inner join tbl_iva as I on I.id_iva=SP.id_iva) "
                                + "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "
                                + "group by SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, case when tiene_iva then '~' else '' end, "
                                + "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien order by id_sucursal, id_producto");
                        productos = Matriz.ResultSetAMatriz(rsDetalleFactura);
                    } catch (Exception e) {
                        System.out.println("Error portal obtener productos de sucursal :" + e.getMessage());
                    }
                    /*validamos si existe costos y productos */
                    msg = "No se ha encontrado datos de costos y productos";
                    if (costos != null && productos != null) {
                        /*Datos de facturar*/
                        String idPuntoEmision = matPuntosVirtuales[p][1];
                        String usuario = matPuntosVirtuales[p][2];
                        String serie_factura = matPuntosVirtuales[p][3];
                        String num_factura = matPuntosVirtuales[p][4];
                        String direccion_sucursal = matPuntosVirtuales[p][5];
                        String autorizacion = "1119999999";
                        String desc_venta = "121";
                        String ambiente = "1";      // 1=pruebas    2=produccion
                        String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
                        String clave_certificado = "";
                        String ruc_empresa = "1091728857001";
                        String razon_social_empresa = "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
                        String nombre_comercial = "SAITEL";
                        String num_resolucion = "";
                        String oblga_contabilidad = "SI";
                        String dir_matriz = "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
                        iva_vigente = "12";
                        String id_forma_pago = "99";
                        String cxc = "";
                        try {
                            ResultSet r = ObjFacturaVenta.consulta("SELECT * FROM tbl_configuracion order by parametro;");
                            while (r.next()) {
                                String parametro = r.getString("parametro") != null ? r.getString("parametro") : "";
                                if (parametro.compareTo("desc_venta") == 0) {
                                    desc_venta = r.getString("valor") != null ? r.getString("valor") : "121";
                                }
                                if (parametro.compareTo("ambiente") == 0) {
                                    ambiente = r.getString("valor") != null ? r.getString("valor") : "2";
                                }
                                if (parametro.compareTo("tipoEmision") == 0) {
                                    tipoEmision = r.getString("valor") != null ? r.getString("valor") : "1";
                                }
                                if (parametro.compareTo("clave_certificado") == 0) {
                                    clave_certificado = r.getString("valor") != null ? r.getString("valor") : "";
                                }
                                if (parametro.compareTo("ruc") == 0) {
                                    ruc_empresa = r.getString("valor") != null ? r.getString("valor") : "1091728857001";
                                }
                                if (parametro.compareTo("razon_social") == 0) {
                                    razon_social_empresa = r.getString("valor") != null ? r.getString("valor") : "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
                                }
                                if (parametro.compareTo("nombre_comercial") == 0) {
                                    nombre_comercial = r.getString("valor") != null ? r.getString("valor") : "SAITEL";
                                }
                                if (parametro.compareTo("num_resolucion") == 0) {
                                    num_resolucion = r.getString("valor") != null ? r.getString("valor") : "";
                                }
                                if (parametro.compareTo("oblga_contabilidad") == 0) {
                                    oblga_contabilidad = r.getString("valor") != null ? r.getString("valor") : "SI";
                                }
                                if (parametro.compareTo("p_iva1") == 0) {
                                    iva_vigente = r.getString("valor") != null ? r.getString("valor") : "12";
                                }
                                if (parametro.compareTo("cxc") == 0) {
                                    cxc = r.getString("valor") != null ? r.getString("valor") : "12";
                                }
                            }
                            r.close();
                        } catch (Exception e) {
                            System.out.println("Error portal obtener datos de configuracion :" + e.getMessage());
                        }
                        String matParamAsientoAx[][] = null;
                        String paramArtic = "";
                        String id_producto_instalacion = "37";
                        int pos_instalacion = Matriz.enMatrizBuscar(productos, new String[]{id_sucursal, id_producto_instalacion}, new int[]{0, 1});
                        String id_producto_anticipo = "508";
                        int pos_anticipo = Matriz.enMatrizBuscar(productos, new String[]{id_sucursal, id_producto_anticipo}, new int[]{0, 1});
                        String ids_productos = "";
                        String descripciones = "";
                        String cantidades = "";
                        String preciosUnitarios = "";
                        String descuentos = "";
                        String subtotales = "";
                        String ivas = "";
                        String pIvas = "";
                        String codigoIvas = "";
                        double descuento_promocion = 0;
                        double iva_actual = Double.parseDouble(iva_vigente);
                        double total = 0;
                        double subtotal_final = 0;
                        double total_final = 0;
                        double descuento_final = 0;
                        double iva_final = 0;
                        if (promocion != null) {
                            try {
                                descuento_promocion = Double.parseDouble(promocion[0][2]);
                            } catch (Exception e) {
                                descuento_promocion = 0;
                                System.out.println("Error portal promocion descuento :" + e.getMessage());
                            }
                        }
                        /*calculo para la instalacion y costo de mes actual*/
                        int cantidad = 1;
                        double costo_instalacion = 0;
                        double descuento = 0;
                        double iva_calculado = 0;
                        double subtotal = 0;
                        if (descuento_promocion != 100) {
                            costo_instalacion = Double.parseDouble(costos[0][3]);
                            descuento = descuento_promocion > 0 ? ((costo_instalacion * descuento_promocion) / 100) : descuento_promocion;
                            iva_calculado = (costo_instalacion - descuento) * iva_actual / 100;
                            subtotal = costo_instalacion * cantidad;
                            ids_productos += productos[pos_instalacion][1] + ",";
                            descripciones += productos[pos_instalacion][3] + ",";
                            cantidades += cantidad + ",";
                            preciosUnitarios += Addons.redondearDecimales(costo_instalacion) + ",";
                            descuentos += Addons.redondearDecimales(descuento) + ",";
                            subtotales += Addons.redondearDecimales(subtotal) + ",";
                            ivas += Addons.redondearDecimales(iva_calculado) + ",";
                            pIvas += productos[pos_instalacion][6] + ",";
                            codigoIvas += productos[pos_instalacion][11] + ",";
                            total = (costo_instalacion - descuento + iva_calculado);
                            subtotal_final += subtotal;
                            descuento_final += descuento;
                            iva_final += iva_calculado;
                            total_final += total;
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{productos[pos_instalacion][12], "0", Addons.redondearDecimales(costo_instalacion)});
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{productos[pos_instalacion][14], "0", Addons.redondearDecimales(iva_calculado)});
                            if (descuento > 0) {
                                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, Addons.redondearDecimales(descuento), "0"});
                            }
                            paramArtic += "['" + productos[pos_instalacion][1] + "', '" + cantidad + "', '" + Addons.redondearDecimales(costo_instalacion) + "', '" + Addons.redondearDecimales(subtotal)
                                    + "', '" + Addons.redondearDecimales(descuento) + "', '" + Addons.redondearDecimales(iva_calculado) + "', '" + Addons.redondearDecimales(total) + "', '" + productos[pos_instalacion][3]
                                    + "', '" + Addons.redondearDecimales(costo_instalacion) + "', '" + productos[pos_instalacion][9] + "', '" + productos[pos_instalacion][6] + "', '" + productos[pos_instalacion][11] + "'],";

                        }
                        if (convenio_pago.compareTo("0") == 0) {
                            costo_instalacion = Double.parseDouble(costos[0][1]);
                            descuento = 0;
                            iva_calculado = (costo_instalacion - descuento) * iva_actual / 100;
                            cantidad = 1;
                            subtotal = costo_instalacion * cantidad;
                            ids_productos += productos[pos_anticipo][1] + ",";
                            descripciones += productos[pos_anticipo][3] + ",";
                            cantidades += cantidad + ",";
                            preciosUnitarios += Addons.redondearDecimales(costo_instalacion) + ",";
                            descuentos += Addons.redondearDecimales(descuento) + ",";
                            subtotales += Addons.redondearDecimales(subtotal) + ",";
                            ivas += Addons.redondearDecimales(iva_calculado) + ",";
                            pIvas += productos[pos_anticipo][6] + ",";
                            codigoIvas += productos[pos_anticipo][11] + ",";
                            total = (costo_instalacion - descuento + iva_calculado);
                            subtotal_final += subtotal;
                            descuento_final += descuento;
                            iva_final += iva_calculado;
                            total_final += total;
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{productos[pos_anticipo][12], "0", Addons.redondearDecimales(costo_instalacion)});
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{productos[pos_anticipo][14], "0", Addons.redondearDecimales(iva_calculado)});
                            if (descuento > 0) {
                                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, Addons.redondearDecimales(descuento), "0"});
                            }
                            paramArtic += "['" + productos[pos_anticipo][1] + "', '" + cantidad + "', '" + Addons.redondearDecimales(costo_instalacion) + "', '" + Addons.redondearDecimales(subtotal)
                                    + "', '" + Addons.redondearDecimales(descuento) + "', '" + Addons.redondearDecimales(iva_calculado) + "', '" + Addons.redondearDecimales(total) + "', '" + productos[pos_anticipo][3]
                                    + "', '" + Addons.redondearDecimales(costo_instalacion) + "', '" + productos[pos_anticipo][9] + "', '" + productos[pos_anticipo][6] + "', '" + productos[pos_anticipo][11] + "'],";
                        }
                        if (id_forma_pago.compareTo("99") == 0) {
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxc, Addons.redondearDecimales(total_final), "0"});
                        }
                        if (paramArtic.compareTo("") != 0) {
                            paramArtic = paramArtic.substring(0, paramArtic.length() - 1);
                            ids_productos = ids_productos.substring(0, ids_productos.length() - 1);
                            descripciones = descripciones.substring(0, descripciones.length() - 1);
                            cantidades = cantidades.substring(0, cantidades.length() - 1);
                            preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length() - 1);
                            descuentos = descuentos.substring(0, descuentos.length() - 1);
                            subtotales = subtotales.substring(0, subtotales.length() - 1);
                            ivas = ivas.substring(0, ivas.length() - 1);
                            pIvas = pIvas.substring(0, pIvas.length() - 1);
                            codigoIvas = codigoIvas.substring(0, codigoIvas.length() - 1);
                        }
                        String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
                        String paramAsiento = "";
                        for (int i = 0; i < matParamAsiento.length; i++) {
                            paramAsiento += "['" + matParamAsiento[i][0] + "', '" + matParamAsiento[i][1] + "', '" + matParamAsiento[i][2] + "'],";
                        }
                        if (paramAsiento.compareTo("") != 0) {
                            paramAsiento = paramAsiento.substring(0, paramAsiento.length() - 1);
                        }
                        /* crear el xml y otros datos  */
                        String ret_num_serie = "";
                        String ret_autorizacion = "";
                        String ret_num_retencion = "0";
                        String ret_fecha_emision = "";
                        String ret_ejercicio_fiscal_mes = "";
                        String ret_ejercicio_fiscal = "NULL";
                        String ret_impuesto_retenido = "0";
                        String paramRet = "";
                        String codigoFormaPago = "20";
                        String forma_pago = "d";
                        String num_cheque = "";
                        String banco = "";
                        String num_comp_pago = "";
                        String gastos_bancos = "0";
                        String id_plan_cuenta_banco = "0";
                        String son = "";
                        String observacion = "Ingresos por instalación Factura Nro. " + serie_factura + " - " + num_factura;
                        FormaPago objFormaPago = new FormaPago(this._ip, this._puerto, this._db, this._usuario, this._clave);
                        try {
                            codigoFormaPago = objFormaPago.getCodigoFormaPago(id_forma_pago);
                            forma_pago = objFormaPago.getCodigoInternoFormaPago(id_forma_pago);
                        } finally {
                            objFormaPago.cerrar();
                        }
                        String xmlFirmado = "";
                        String estadoDocumento = "";
                        String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
                        String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
                        String claveAcceso = "";
                        String autorizacionXml = "";
                        String respuestaAutoriz = "";
                        String doc_electronico = "0";
                        String subtotal_0 = "0";
                        String subtotal_2 = "0";
                        String subtotal_3 = "0";
                        String iva_3 = "0";
                        FacturaElectronica objFE = new FacturaElectronica();
                        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, this._usuario, this._clave);
                        boolean ok = true;
                        String error = "";
                        String fecha_emision = Fecha.getFecha("ISO");
                        try {
                            if (doc_electronico.compareTo("0") == 0) {
                                ok = false;
                                String vecSerie[] = serie_factura.split("-");
                                claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("ISO"), "01", ruc_empresa, ambiente, vecSerie[0] + vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
                                if (tipoEmision.compareTo("2") == 0) {
                                    claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
                                }
                                objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1],
                                        Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad,
                                        tipo_documento_cliente, razon_social, ruc, Addons.redondearDecimales(subtotal_final), Addons.redondearDecimales(descuento_final), subtotal_0, subtotal_2, Addons.redondearDecimales(iva_final), subtotal_3, iva_3, Addons.redondearDecimales(total_final), codigoFormaPago,
                                        ids_productos, descripciones, cantidades, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion);
                                String documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                                objFE.salvar(documentoXml);
                                error = objFE.getError();
                                if (error.compareTo("") == 0) {
                                    estadoDocumento = "g";
                                    String archivoSalida = claveAcceso + ".xml";
                                    FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
                                    firmaDigital.execute();
                                    error = firmaDigital.getError();
                                    if (error.compareTo("") == 0) {
                                        estadoDocumento = "f";
                                        if (tipoEmision.compareTo("1") == 0) {  //   emision normal
                                            autorizacionXml = Utils.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                                            ok = true;
                                        } else {
                                            autorizacionXml = Utils.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                                            ok = true;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error portal tratando de generar y fimrar xml :" + e.getMessage());
                            msg = e.getMessage();
                        }
                        objClavesSri.cerrar();
                        msg = error;
                        if (ok) {
                            msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                                    + "contáctese con el administrador del sistema para mayor información.";
                            double costo_sucursal = Double.parseDouble(costos[0][7]);
                            costo_sucursal = costo_sucursal + ((costo_sucursal * iva_actual) / 100);
                            double costo_sector = Double.parseDouble(costos[0][3]);
                            costo_sector = costo_sector + ((costo_sector * iva_actual) / 100);
                            double costo_facturar = costo_sucursal;
                            if (descuento_promocion > 0) {
                                if (costo_sector > costo_sucursal) {
                                    costo_facturar = costo_sector;
                                }
                            }
                            String id_factura_id_Instalacion = ObjInstalacion.insertar(numero_contrato, id_cliente, id_sucursal, fecha_actual, fecha_fin,
                                    ruc_representante, representante, convenio_pago, prv, ci, prr, id_sector, costos[0][5], "" + Addons.redondear(costo_facturar, 2),
                                    direccion_inmuble, id_plan, ruc, idPuntoEmision, serie_factura, num_factura, autorizacion, razon_social,
                                    fecha_emision, direccion, telefono, id_forma_pago, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos,
                                    id_plan_cuenta_banco, son, observacion, Addons.redondearDecimales(subtotal_final), subtotal_0, subtotal_2, subtotal_3, Addons.redondearDecimales(descuento_final), Addons.redondearDecimales(iva_final), iva_3,
                                    Addons.redondearDecimales(total_final), "array[" + paramArtic + "]", ret_num_serie, ret_num_retencion, ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal,
                                    ret_impuesto_retenido, "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", xmlFirmado, idprom, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz, usuario, costo_facturado, tiempo_permanencia);
                            if (id_factura_id_Instalacion.compareTo("-1;-1;-1") != 0) {
                                String id_principales[] = id_factura_id_Instalacion.split(";");
                                String nombre_contrato = "saitel" + Fecha.getFecha("ISO") + "" + Fecha.getHoraMili();
                                nombre_contrato = (nombre_contrato.replaceAll("-", "").replaceAll(":", "")) + ".pdf";
                                File fichero_contrato = new File(this._dir + "" + nombre_contrato);
                                nombre_contrato = objPdfDocumental.Generarcontrato(id_principales[0], fichero_contrato, croquis);
                                if (nombre_contrato != null) {
                                    Archivo ObjArchivo = new Archivo(this._ip_documental, this._puerto_documental, this._db_documental, this._usuario_documental, this._clave_documental);
                                    try {
                                        ObjArchivo.setArchivoDocumentales("tbl_contrato", id_principales[0], "contratotexto", nombre_contrato, fichero_contrato, "public", "db_isp");
                                        ObjArchivo.setArchivoDocumentalTexto(PdfDocumental.contrato_texto, "0", id_sucursal, "tbl_contrato", id_principales[0], "contratotexto", "public", "db_isp");
                                        ObjArchivo.setArchivoDocumentales("tbl_instalacion", id_principales[1], "imgcroquis", nombre_nuevo, file, "public", "db_isp");
                                    } catch (Exception e) {
                                        System.out.println("Error portal intentando guardar contrato de la documental :" + e.getMessage());
                                    } finally {
                                        ObjArchivo.cerrar();
                                    }
                                }
                                Notificacion ObjNotificacion = new Notificacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
                                Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
                                try {
                                    ObjAuditoria.setRegistro(request, "INGRESO DE UNA INSTALACION POR PORTAL IDS: " + id_factura_id_Instalacion);
                                    Properties parametros = new Properties();
                                    parametros.setProperty("starttls", "true");
                                    StringBuilder mensaje = new StringBuilder();
                                    String plantilla = ObjNotificacion.getNotificacionHtml("contratos_nuevos");
                                    String email_notificacion = ObjNotificacion.getNotificacionEmail("mail_notificacion_contratos", id_sucursal);
                                    String fecha = Fecha.getFecha("ISO");
                                    fecha = Fecha.getDiaSemana(fecha) + " ," + Fecha.getFechaSolicitud(fecha) + " " + Fecha.getHora();
                                    plantilla = plantilla.replaceAll("#cliente_actual#", razon_social);
                                    plantilla = plantilla.replaceAll("#fecha_actual#", fecha);
                                    plantilla = plantilla.replaceAll("#plan_contratado#", costos[0][4]);
                                    plantilla = plantilla.replaceAll("#valores_pagar#", "" + Addons.redondear(total_final) + " Dolares"
                                            + "<br> Nota: Si usted no cancela este valor no se procedera a realizar la instalación."
                                            + "<br> Realizado el pago, la instalacion se realizara durante un tiempo de 7 a 15 dias laborables.");
                                    mensaje.append(plantilla);
                                    List adjuntos = new ArrayList();
                                    adjuntos.add(fichero_contrato.getAbsolutePath());
                                    Correo.enviar(this._mail_svr, this._mail_puerto, this._mail_remitente, this._mail_remitente_clave, email, "", email_notificacion, "REPORTE DE CONTRATACION DE SERVICIO PORTA WEB", mensaje, true, adjuntos, parametros);
                                } catch (Exception e) {
                                    System.out.println("Error portal tratando de enviar correo al cliente  :" + e.getMessage());
                                } finally {
                                    ObjNotificacion.cerrar();
                                    ObjAuditoria.cerrar();
                                }
                                msg = "SE HA REGISTRADO SU CONTRATO EXITOSAMENTE PRONTO NOS CONTACTAREMOS CON USTED";
                                err = "0";

                            } else {
                                msg = "SE HA PRODUCIDO UN ERROR AL MOMENTO DE CONTRATAR EL SERVICIO INTENTE MAS TARDE";
                                Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
                                try {
                                    ObjAuditoria.setRegistro(request, "INTENTO DE REGISTRO DE UN CONTRATO CON FALLO  ");
                                } catch (Exception e) {
                                    System.out.println("Error portal tratando de entento de una instalacion  :" + e.getMessage());
                                } finally {
                                    ObjAuditoria.cerrar();
                                }
                            }
                        }
                    }
                }
            } else {
                msg = "Solo se permite formatos (jpg - jpeg - png";
            }
            String r = "<script language='javascript' type='text/javascript'>window.top.window.finTransferenciacontratacion(" + err + ", '" + msg + "');</script>";
            out.print(r);
        } catch (Exception e) {
            System.out.println("Error portal general al momento de intento de guardado :" + e.getMessage());
        } finally {
            ObjContrato.cerrar();
            ObjInstalacion.cerrar();
            ObjFacturaVenta.cerrar();
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
