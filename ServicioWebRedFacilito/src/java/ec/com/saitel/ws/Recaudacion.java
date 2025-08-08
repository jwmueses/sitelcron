/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.ws;

import ec.com.saitel.lib.Archivo;
import ec.com.saitel.lib.Cadena;
import ec.com.saitel.lib.Configuracion;
import ec.com.saitel.lib.Correo;
import ec.com.saitel.lib.DataBase;
import ec.com.saitel.lib.FacturaElectronica;
import ec.com.saitel.lib.Fecha;
import ec.com.saitel.lib.Matriz;
import ec.com.saitel.lib.Mikrotik;
import ec.com.saitel.lib.PreFactura;
import ec.gob.sri.FirmaXadesBes;
import ec.gob.sri.wsc.EnvioComprobantesWS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author sistemas
 */
@WebService(serviceName = "Recaudacion")
public class Recaudacion {

    private static final String maquina = "127.0.0.1";    //   192.168.217.16       127.0.0.1
    private static final int puerto = 5432;
    private static final String db = "db_isp";
    private static final String usuario = "postgres";
    private static final String clave = "Gi%9875.-*5+$)"; //  postgres    A0Lpni2       Gi%9875.-*5+$)
    
    private String _ipdocumental = "192.168.217.31";    //   192.168.217.16       127.0.0.1
    private int _puertodocumental = 5432;
    private String _dbdocumental = "db_isp_documentos";
    private String _usuariodocumental = "postgres";
    private String _clavedocumental = "Gi%9875.-*5+$)"; //  postgres    A0Lpni2       Gi%9875.-*5+$)
    
    private String ambiente = "2";  //  1=pruebas   2=produccion
    private static final String _dir = "/opt/lampp/htdocs/anexos/fe/"; //       /home/saitel/Documentos/fe/      /opt/lampp/htdocs/anexos/fe/
    private String _WSENVIO = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    private String _WSAUTORIZA = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
    
    private static final String _svrMail = "pro.turbo-smtp.com";      //      pro.turbo-smtp.com   
    private static final int _svrMailPuerto = 587;              //      465
    private static final String _remitante = "notificaciones_no_reply@saitel.ec"; //  notificaciones_no_reply@saitel.ec
    private static final String _remitanteClave = "R0dR9m7F";   //  R0dR9m7F
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "consulta")
    public String consulta(@WebParam(name = "clave") String clave1, @WebParam(name = "dni") String dni) 
    {
        //TODO write your implementation code here:
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        xml.append("<consulta>");
        String mensaje = "cliente no existe o digitado en forma incorrecta";
        DataBase objDB = new DataBase(maquina, puerto, db, usuario, clave);
        
        if(clave1.compareTo("")!=0){
            String puntoEmision[] = this.getPuntoEmision(clave1);
            if(puntoEmision[0].compareTo("-1")!=0){
                
                String fecha_inicio = Fecha.getAnio() + "-" + Fecha.getMes() + "-" + Fecha.getDia();
                long iniEjecucion = Fecha.getTimeStamp(fecha_inicio, "05:59");
                long finEjecucion = Fecha.getTimeStamp(fecha_inicio, "21:00");
                long actual = Fecha.getTimeStamp(fecha_inicio, Fecha.getHora());

                if(actual >= iniEjecucion && actual <= finEjecucion ){
                
                    boolean deudaPrefactura = true;
                    boolean deudaFacturaCredito = true;
                    //objDB.consulta("select proc_calcularprefacturaspendientes();");
                    try{

                        try{
                            ResultSet rsCliente = objDB.consulta("select * from tbl_cliente where lower(ruc)='"+dni.toLowerCase()+"'");
                            if(objDB.getFilas(rsCliente)>0){

                                // facturas a credito
                                String idInstalaciones = "";
                                /*try{
                                    ResultSet rs = objDB.consulta("select F.id_instalacion, F.id_factura_venta, F.razon_social, case when plan is not null then plan else 'venta de productos o servicios' end plan, " +
                                            "case when direccion_instalacion is not null then direccion_instalacion else F.direccion end direccion_instalacion, " +
                                            "case when txt_periodo is not null then txt_periodo else F.fecha_emision::varchar end txt_periodo, deuda, F.id_instalacion " +
                                            "from vta_prefactura_todas as P right outer join vta_factura_venta as F on F.id_factura_venta=P.id_factura_venta " +
                                            "where lower(F.ruc)='"+dni.toLowerCase()+"' and deuda>0 order by F.razon_social, id_factura_venta");
                                    if(objDB.getFilas(rs)==0){
                                        deudaFacturaCredito = false;
                                    }else{
                                        String aux = "";
                                        while(rs.next()){
                                            String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                                            String id_instalacion = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "";
                                            idInstalaciones += id_instalacion.compareTo("")!=0 ? id_instalacion + "," : "";

                                            if(razon_social.compareTo(aux)!=0){
                                                aux = razon_social;

                                                xml.append("<prefactura>");

                                                xml.append("<id>F");
                                                xml.append((rs.getString("id_factura_venta")!=null) ? rs.getString("id_factura_venta") : "0");
                                                xml.append("</id>");

                                                xml.append("<razon_social>");
                                                xml.append(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "");
                                                xml.append("</razon_social>");

                                                xml.append("<plan_servicio>");
                                                xml.append((rs.getString("plan")!=null) ? rs.getString("plan") : "");
                                                xml.append("</plan_servicio>");

                                                xml.append("<direccion>");
                                                xml.append(rs.getString("direccion_instalacion")!=null ? rs.getString("direccion_instalacion") : "");
                                                xml.append("</direccion>");

                                                xml.append("<periodo>");
                                                xml.append((rs.getString("txt_periodo")!=null) ? rs.getString("txt_periodo") : "");
                                                xml.append("</periodo>");

                                                xml.append("<total_pagar>");
                                                xml.append(rs.getString("deuda")!=null ? rs.getString("deuda") : "0.00");
                                                xml.append("</total_pagar>");

                                                xml.append("</prefactura>");
                                            }
                                        }
                                        mensaje = "ok";
                                        if(idInstalaciones.compareTo("")!=0){
                                            idInstalaciones = " and id_instalacion not in(" + idInstalaciones.substring(0, idInstalaciones.length()-1) + ")";
                                        }
                                        rs.close();
                                    }
                                }catch(Exception e){
                                    mensaje = "error 500";
                                }*/


                                // calculo los valores antes de realizar la consulta
                                ResultSet rsPrF = objDB.consulta("select id_prefactura from vta_prefactura where lower(ruc)='"+dni.toLowerCase()+"'");
                                if(rsPrF != null){
                                    while(rsPrF.next()){
                                        objDB.consulta("select proc_calcularPreFactura("+((rsPrF.getString("id_prefactura")!=null) ? rsPrF.getString("id_prefactura") : "0")+", true);");
                                    }
                                    rsPrF.close();
                                }

                                // realizo la consulta y devolver los datos
                                ResultSet rs = objDB.consulta("select id_prefactura, razon_social, plan, direccion_instalacion, txt_periodo, total, id_instalacion from vta_prefactura where lower(ruc)='"+dni.toLowerCase()+"' "+idInstalaciones+" order by id_instalacion, id_prefactura");
                                if(objDB.getFilas(rs)==0){
                                    deudaPrefactura = false;
                                }else{
                                    String aux = "";
                                    while(rs.next()){
                                        String id_instalacion = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "";
                                        if(id_instalacion.compareTo(aux)!=0){
                                            aux = id_instalacion;

                                            xml.append("<prefactura>");

                                            xml.append("<id>P");
                                            xml.append((rs.getString("id_prefactura")!=null) ? rs.getString("id_prefactura") : "0");
                                            xml.append("</id>");

                                            xml.append("<razon_social>");
                                            xml.append(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "");
                                            xml.append("</razon_social>");

                                            xml.append("<plan_servicio>");
                                            xml.append((rs.getString("plan")!=null) ? rs.getString("plan") : "");
                                            xml.append("</plan_servicio>");

                                            xml.append("<direccion>");
                                            xml.append(rs.getString("direccion_instalacion")!=null ? rs.getString("direccion_instalacion") : "");
                                            xml.append("</direccion>");

                                            xml.append("<periodo>");
                                            xml.append((rs.getString("txt_periodo")!=null) ? rs.getString("txt_periodo") : "");
                                            xml.append("</periodo>");

                                            xml.append("<total_pagar>");
                                            xml.append(rs.getString("total")!=null ? rs.getString("total") : "0.00");
                                            xml.append("</total_pagar>");

                                            xml.append("</prefactura>");
                                        }
                                    }
                                    mensaje = "ok";
                                    rs.close();
                                }

                                if(!deudaPrefactura && !deudaFacturaCredito){
                                    mensaje = "cliente con cedula o RUC "+dni+" no tiene deuda pendiente";
                                }
                            }
                        }catch(Exception e){
                            mensaje = "error 500";
                        }

                        objDB.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
                            "values('administrador', '127.0.0.1','"+this.getHora()+"','"+this.getFecha("ISO")+
                            "', 'CONSULTA DE PREFACTURA(S) A TRAVES DEL WEB SERVICES PARA EL CLIENTE CON NUMERO DE IDENTIFICACION "+dni+"');");
                    }catch(Exception e){
                         mensaje = "error 500";
                    }finally{
                        objDB.cerrar();
                    }
                    
                } else {
                    mensaje = "SISTEMA FUERA DE LINEA";
                }
                
            }else{
                mensaje = "Clave de acceso erronea";
            }
        }else{
            mensaje = "No se ha proporcionado una clave de acceso";
        }
        
        xml.append("<mensaje>");
        xml.append(mensaje);
        xml.append("</mensaje>");
        
        xml.append("</consulta>");
        return xml.toString();
    }

    
    
    
    
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "facturar")
    public String facturar(@WebParam(name = "clave") String clave, @WebParam(name = "idRegistroConsulta") String idRegistroConsulta, @WebParam(name = "numDocumento") String numDocumento) 
    {
        //TODO write your implementation code here:
        if(clave.compareTo("")!=0){
            
            String puntoEmision[] = this.getPuntoEmision(clave);
            if(puntoEmision[0].compareTo("-1")!=0){     //      idPuntoEmision
                
                String fecha_inicio = Fecha.getAnio() + "-" + Fecha.getMes() + "-" + Fecha.getDia();
                long iniEjecucion = Fecha.getTimeStamp(fecha_inicio, "05:59");
                long finEjecucion = Fecha.getTimeStamp(fecha_inicio, "21:00");
                long actual = Fecha.getTimeStamp(fecha_inicio, Fecha.getHora());

                if(actual >= iniEjecucion && actual <= finEjecucion ){
                    
                
                    float saldo = puntoEmision[1].compareTo("t")==0 ? this.getSaldoCuentaContable(puntoEmision[2]) - this.getSaldoPorContabilizar(puntoEmision[0]) : 0;      //  idPlanCuentaCaja de parametro
                
                    if(puntoEmision[1].compareTo("t")==0){
                        float fondoPrepago = Float.parseFloat( puntoEmision[3] );
                        if( (fondoPrepago/8) >= saldo  ){    // 12.5%
                            this.notificacion(3, puntoEmision[4]);
                        }else if( (fondoPrepago/4) >= saldo  ){ // 25%
                                  this.notificacion(2, puntoEmision[4]);
                        }else if( (fondoPrepago/2) >= saldo  ){ // 50%
                                  this.notificacion(1, puntoEmision[4]);
                              }
                    }

                    if( (puntoEmision[1].compareTo("t")==0 && saldo>0) || puntoEmision[1].compareTo("f")==0 ){      //  valida si es prepago

                        String idPrefactura = idRegistroConsulta.compareTo("")!=0 ? idRegistroConsulta : "P0";
                        if(idPrefactura.indexOf("P")==0){
                            return this.prefacturaEmitir(idRegistroConsulta.replace("P", ""), numDocumento, puntoEmision[0], puntoEmision[1], saldo);
                        /*}else if(id_prefactura.indexOf("F")==0){
                                  return this.facturaCobrar(idRegistroConsulta.replace("F", ""), numDocumento, idPuntoEmision);*/
                        }
                    }else{
                        return "Fondo contable sin saldo suficiente, para poder emitir la factura";
                    }
                    
                } else {
                    return "SISTEMA FUERA DE LINEA";
                }
            }else{
                return "Clave de acceso erronea";
            }
        }else{
            return "No se ha proporcionado una clave de acceso";
        }
            
        return "Error 500";
    }
        
    private String prefacturaEmitir(String idPrefactura, String num_documento, String idPuntoEmision, String prepago, float saldoCaja)
    {
        //DataBase objDB = new DataBase(maquina, puerto, db, usuario, clave);
        
        String mensaje = "Error 500";
        
        
        PreFactura objPrefactura = new PreFactura(maquina, puerto, db, usuario, clave);
        
        boolean tiempoOK = false;
        int k=0;
        while(true){
            try{
                ResultSet rs = objPrefactura.consulta("select valor from tbl_configuracion where parametro = 'bloqueo_libros'");
                if(rs.next()){
                    String bloqueo_libros = rs.getString("valor")!=null ? rs.getString("valor") : "";
                    if(bloqueo_libros.compareTo("false")==0){
                        objPrefactura.ejecutar("update tbl_configuracion set valor='true' where parametro = 'bloqueo_libros'");
                        tiempoOK = true;
                        break;
                    }
                    rs.close();        
                }
                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
            if(k >= 5){
                objPrefactura.cerrar();
                mensaje = "El sistema se encuentra demasiado ocupado, por favor, inténtelo más tarde.";
                break;
            }
            k++;
        }
        
        
        
        if( tiempoOK ) {
            
        
            Configuracion objDB = new Configuracion(maquina, puerto, db, usuario, clave);

            String clave_certificado = "";
//            String ruta_docs_generados = "/opt/lampp/htdocs/anexos/fe/generados/"; 
//            String ruta_docs_firmados = "/opt/lampp/htdocs/anexos/fe/firmados/"; 
//            String ruta_docs_autorizados = "/opt/lampp/htdocs/anexos/fe/autorizados/"; 
            String ruc_empresa = "1091728857001";
            String razon_social_empresa = "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
            String nombre_comercial = "SAITEL";
            String num_resolucion = "";
            String oblga_contabilidad = "";
            String dir_matriz = "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
    //        String email_empresa = objDB.getValor("email");
    //        String telefono_empresa = objDB.getValor("telf_matriz");
            String idPlanCuentaUtilidadActivo = "-1";
            String idPlanCuentaIvaActivos = "-1";
//            int actualizarMikrotiksEnLinea = -1;
            String modoSincronizacionMikrotiks = "scripts";
            /*String PAGINA_WEB = objDB.getValor("pagina_web");*/
    //        double  p_iva = Integer.parseInt( objDB.getValor("p_iva1") );
//            String DOCS_ELECTRONICOS = "/opt/lampp/htdocs/anexos/fe/";
            String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
            String transaccion = "";



            //String DOCS_ELECTRONICOS = Parametro.getRutaArchivos();
            try{
                String id_plan_cuenta_banco="10";
                ResultSet rsBanco = objDB.consulta("SELECT * FROM vta_banco where lower(banco) like '%pichincha%'");
                if(rsBanco.next()){
                    id_plan_cuenta_banco = rsBanco.getString("id_plan_cuenta")!=null ? rsBanco.getString("id_plan_cuenta") : "10";
                    rsBanco.close();
                }

                String desc_venta = "121";
                
                try{
                    ResultSet r = objDB.consulta("SELECT * FROM tbl_configuracion order by parametro;");
                    while(r.next()){
                        String parametro = r.getString("parametro")!=null ? r.getString("parametro") : "";
                        if(parametro.compareTo("desc_venta")==0){
                            desc_venta = r.getString("valor")!=null ? r.getString("valor") : "121";
                        }
                        if(parametro.compareTo("ambiente")==0){
                            ambiente = r.getString("valor")!=null ? r.getString("valor") : "2";
                        }
                        if(parametro.compareTo("tipoEmision")==0){
                            tipoEmision = r.getString("valor")!=null ? r.getString("valor") : "1";
                        }
                        if(parametro.compareTo("ruc")==0){
                            ruc_empresa = r.getString("valor")!=null ? r.getString("valor") : "1091728857001";
                        }
                        if(parametro.compareTo("clave_certificado")==0){
                            clave_certificado = r.getString("valor")!=null ? r.getString("valor") : "";
                        }
                        if(parametro.compareTo("razon_social")==0){
                            razon_social_empresa = r.getString("valor")!=null ? r.getString("valor") : "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
                        }
                        if(parametro.compareTo("nombre_comercial")==0){
                            nombre_comercial = r.getString("valor")!=null ? r.getString("valor") : "SAITEL";
                        }
                        if(parametro.compareTo("num_resolucion")==0){
                            num_resolucion = r.getString("valor")!=null ? r.getString("valor") : "";
                        }
                        if(parametro.compareTo("oblga_contabilidad")==0){
                            oblga_contabilidad = r.getString("valor")!=null ? r.getString("valor") : "SI";
                        }
                        if(parametro.compareTo("dir_matriz")==0){
                            dir_matriz = r.getString("valor")!=null ? r.getString("valor") : "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
                        }
                        if(parametro.compareTo("uti_venta_activos")==0){
                            idPlanCuentaUtilidadActivo = r.getString("valor")!=null ? r.getString("valor") : "-1";
                        }
                        if(parametro.compareTo("iva_cobrado")==0){
                            idPlanCuentaIvaActivos = r.getString("valor")!=null ? r.getString("valor") : "-1";
                        }
//                        if(parametro.compareTo("actualizarMikrotiksEnLinea")==0){
//                            actualizarMikrotiksEnLinea = r.getString("valor")!=null ? r.getInt("valor") : -1;
//                        }
                        if( parametro.compareTo("modoSincronizacionMikrotiks") == 0) {
                            modoSincronizacionMikrotiks = r.getString("valor")!=null ? r.getString("valor") : "scripts";
                        }
                    }
                    r.close();
                }catch(Exception e){
                    e.printStackTrace();
                }




                String matPuntosVirtuales[][] = this.getPuntoEmisionEmpresa(objPrefactura, idPuntoEmision);
                String usuarioCaja = "administrador";



                objDB.consulta("select proc_calcularPreFactura("+idPrefactura+", false);");


                ResultSet rsDetalleFactura = objDB.consulta("select SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                    "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"+
                    "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien  "+
                    "FROM ((vta_producto as P left join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
                    "inner join tbl_iva as I on I.id_iva=SP.id_iva) "+
                    "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "+
                    "group by SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, case when tiene_iva then '~' else '' end, "+
                    "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien order by id_sucursal, id_producto");
                String matDetalleFactura[][] = Matriz.ResultSetAMatriz(rsDetalleFactura);

                ResultSet rs = objDB.consulta("select *, total+comision_cash as total_comision from vta_prefactura where id_prefactura=" + idPrefactura);

                if(rs.next()){
                    try{
                        String id_instalacion = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "-1";
                        String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "-1";
//                        String id_cliente = rs.getString("id_cliente")!=null ? rs.getString("id_cliente") : "-1"; 
                        String tipo_documento = rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "05"; 
                        String ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : ""; 
                        String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : ""; 
                        String direccion = rs.getString("direccion")!=null ? rs.getString("direccion") : ""; 
                        String estado_servicio = rs.getString("estado_servicio")!=null ? rs.getString("estado_servicio") : "c"; 
                        String plan = rs.getString("plan")!=null ? rs.getString("plan") : "";
                        String id_producto = rs.getString("id_producto")!=null ? rs.getString("id_producto") : "-1";
                        int dias_conexion = rs.getString("dias_conexion")!=null ? rs.getInt("dias_conexion") : 30;
                        String valor_internet = rs.getString("valor_internet")!=null ? rs.getString("valor_internet") : "0";
                        String iva_internet = rs.getString("iva_internet")!=null ? rs.getString("iva_internet") : "0";
//                        double total_internet = rs.getString("total_internet")!=null ? rs.getDouble("total_internet") : 0;
                        String subtotal = rs.getString("subtotal")!=null ? rs.getString("subtotal") : "0";
                        String subtotal_2 = rs.getString("subtotal_2")!=null ? rs.getString("subtotal_2") : "0";
                        String subtotal_3 = rs.getString("subtotal_3")!=null ? rs.getString("subtotal_3") : "0";
                        String iva_2 = rs.getString("iva_2")!=null ? rs.getString("iva_2") : "0";
                        String iva_3 = rs.getString("iva_3")!=null ? rs.getString("iva_3") : "0";
                        String descuento = rs.getString("descuento")!=null ? rs.getString("descuento") : "0";
                        String total_pagar = rs.getString("total")!=null ? rs.getString("total") : "0";
                        String periodo = rs.getString("periodo")!=null ? rs.getString("periodo") : "";
                        String txt_periodo = rs.getString("txt_periodo")!=null ? rs.getString("txt_periodo") : "";
                        String ip = rs.getString("ip")!=null ? rs.getString("ip") : "";
                        //String fecha_pago = rs.getString("fecha_pago")!=null ? rs.getString("fecha_pago") : "";
                        //String hora_pago = rs.getString("hora_pago")!=null ? rs.getString("hora_pago") : "";
                        //double totalCash = rs.getString("total_cash")!=null ? rs.getDouble("total_cash") : 0;




                        if( (prepago.compareTo("t")==0 && saldoCaja >= Float.parseFloat(total_pagar) ) || prepago.compareTo("f")==0 ){      //  valida si es prepago

                        /*int p = Matriz.enMatriz(matPuntosVirtuales, id_sucursal, 0);
                        if(p!=-1){*/

                            //String idPuntoEmision = matPuntosVirtuales[p][1];
                            usuarioCaja = matPuntosVirtuales[0][2];
                            String serie_factura = matPuntosVirtuales[0][3];
                            String num_factura = matPuntosVirtuales[0][4];
                            //String direccion_sucursal = matPuntosVirtuales[0][5];
                            String idFormaPago=matPuntosVirtuales[0][6];
                            String formaPago=matPuntosVirtuales[0][7];
                            String formaPagoSaitel=matPuntosVirtuales[0][8];
                            String descripcionFormaPago=matPuntosVirtuales[0][9];
                            String idPlanCuentaCaja=matPuntosVirtuales[0][10];

                            //String num_comp_pago = fecha_pago.replace("-", "") + hora_pago.replace(":", "") + id_instalacion;
                            int pos = Matriz.enMatriz(matDetalleFactura, new String[]{id_sucursal,id_producto}, new int[]{0,1});
                            String p_u = String.valueOf( this.redondear(Double.parseDouble(valor_internet) / dias_conexion,  4) );
                            String totalpr = String.valueOf( this.redondear( Double.parseDouble(valor_internet) + Double.parseDouble(iva_internet) ) );

                            String ids_productos = id_producto;
                            String descripciones = "SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+txt_periodo+" ~";
                            String cantidades = String.valueOf( dias_conexion );
                            String preciosUnitarios = p_u;
                            String descuentos = "0";
                            String subtotales = valor_internet;
                            String ivas = iva_internet;
                            String pIvas = matDetalleFactura[pos][6];
                            String codigoIvas = matDetalleFactura[pos][11];
                            String totales = totalpr;
                            String tipoRubros = "p";
                            String idsPrefacturaRubro = "-1";

                            String matParamAsientoAx[][] = null;
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{idPlanCuentaCaja, total_pagar, "0"});      //  no se va al banco 
                            if(Float.parseFloat(descuento)>0){
                                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, descuento, "0"});
                                descuentos = descuento;
                            }
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{matDetalleFactura[pos][12], "0", String.valueOf(valor_internet)});//   id_planCuenta del servicio

                            String id_cuenta_iva = matDetalleFactura[pos][14];
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", iva_internet});


                            String paramArtic = "['"+id_producto+"', '"+dias_conexion+"', '"+p_u+"', '"+valor_internet+
                                "', '"+descuento+"', '"+iva_internet+"', '"+totalpr+"', '"+descripciones+
                                "', '"+matDetalleFactura[pos][4]+"', '"+matDetalleFactura[pos][9]+"', '"+pIvas+"', '"+codigoIvas+"', '"+tipoRubros+"','-1'],";


                            int anio = Fecha.datePart("anio", periodo);
                            int mes = Fecha.datePart("mes", periodo);
                            String ini = anio + "-" + mes + "-01";
                            String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
                            // rubros adicionales generados automaticamente por el sistema
                            ResultSet rsRubrosAdicionales = objDB.consulta("SELECT * FROM vta_prefactura_rubro WHERE id_sucursal="+id_sucursal+" and id_instalacion="+id_instalacion+" and tiporubro='a' and periodo between '"+ini+"' and '"+fin+"' "
                                    + "union "
                                    + "SELECT * FROM vta_prefactura_rubro WHERE id_sucursal=" + id_sucursal + " and id_instalacion=" + id_instalacion + " and tiporubro='p' and estadocobro='false' and periodo<='" + fin + "'");
                            String rubrosAdicionales[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales);
                            if(rubrosAdicionales!=null){
                                for(int a=0; a<rubrosAdicionales.length; a++){
                                    if(rubrosAdicionales[a][8].compareTo(id_sucursal)==0 && rubrosAdicionales[a][9].compareTo(id_instalacion)==0 && rubrosAdicionales[a][10].compareTo(periodo)==0){

                                        idsPrefacturaRubro += "," + rubrosAdicionales[a][27];
                                        ids_productos += "," + rubrosAdicionales[a][3];
                                        descripciones += "," + rubrosAdicionales[a][15];
                                        cantidades += ", 1";
                                        preciosUnitarios += "," + rubrosAdicionales[a][12];
                                        descuentos += ",0";
                                        subtotales += "," + rubrosAdicionales[a][12];
                                        ivas += "," + rubrosAdicionales[a][13];
                                        pIvas += "," + rubrosAdicionales[a][16];
                                        codigoIvas += "," + rubrosAdicionales[a][17];
                                        totales += "," + rubrosAdicionales[a][11];

                                        // idProd, cant, pu, subtotal, desc, iva, total, concepto, precioCosto, tipoRubro, pIva, CodIva, tipoActivo
                                        paramArtic += "['"+rubrosAdicionales[a][3]+"', '1', '"+rubrosAdicionales[a][12]+"', '"+rubrosAdicionales[a][12]+
                                        "', '0', '"+rubrosAdicionales[a][13]+"', '"+rubrosAdicionales[a][11]+"', '"+rubrosAdicionales[a][15]+
                                        "', '"+rubrosAdicionales[a][12]+"', '"+rubrosAdicionales[a][18]+"', '"+rubrosAdicionales[a][16]+
                                        "', '"+rubrosAdicionales[a][17]+"', 'p', '"+rubrosAdicionales[a][27]+"'],";

                                        id_cuenta_iva = rubrosAdicionales[a][18].compareTo("s")==0 ? rubrosAdicionales[a][21] : rubrosAdicionales[a][22];
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{rubrosAdicionales[a][19], "0", rubrosAdicionales[a][12]});
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", rubrosAdicionales[a][13]});
                                    }
                                }
                            }

                            //  rubros adicionales procedentes de ordenes de trabajo de servicios
                            ResultSet rsRubrosAdicionales1 = objDB.consulta("SELECT distinct PR.*, P.codigo, I.porcentaje, I.codigo as codigo_iva, (monto / canproductos)::numeric(18,4) as precioUnitario, "
                                    + "(monto * I.porcentaje::numeric / 100)::numeric(13,2) as iva_12, "
                                    + "(monto + (monto * I.porcentaje::numeric / 100 ))::numeric(13,2) as total, I.id_plan_cuenta_venta_servicio, I.id_plan_cuenta_venta_bien, P.id_plan_cuenta_venta "
                                    + "FROM (((tbl_prefactura_rubro as PR inner join vta_producto_n as P on PR.idproductos::int=P.id_producto) "
                                    + "inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                                    + "inner join tbl_iva as I on I.id_iva=SP.id_iva) "
                                    + "WHERE id_rubro is null and estadocobro=false and tiporubro='p' and id_instalacion=" + id_instalacion + " and periodo<='" + fin + "'");
                            String rubrosAdicionales1[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales1);
                            if(rubrosAdicionales1!=null){
                                for(int a=0; a<rubrosAdicionales1.length; a++){
                                    if(rubrosAdicionales1[a][1].compareTo(id_sucursal)==0 && rubrosAdicionales1[a][3].compareTo(id_instalacion)==0 && rubrosAdicionales1[a][5].compareTo(periodo)==0){

                                        idsPrefacturaRubro += "," + rubrosAdicionales1[a][0];
                                        ids_productos += "," + rubrosAdicionales1[a][9];
                                        descripciones += "," + rubrosAdicionales1[a][4];
                                        cantidades += "," + rubrosAdicionales1[a][10];
                                        preciosUnitarios += "," + rubrosAdicionales1[a][16];
                                        descuentos += ",0";
                                        subtotales += "," + rubrosAdicionales1[a][6];
                                        ivas += "," + rubrosAdicionales1[a][17]; 
                                        pIvas += "," + rubrosAdicionales1[a][14];
                                        codigoIvas += "," + rubrosAdicionales1[a][15];
                                        totales += "," + rubrosAdicionales1[a][18];

                                        // 0-idProd, 1-cant, 2-pu, 3-subtotal, 4-desc, 5-iva, 6-total, 7-concepto, 8-precioCosto, 9-tipoRubro, 10-pIva, 11-CodIva, 12-tipoActivo
                                        paramArtic += "['"+rubrosAdicionales1[a][9]+"', '"+rubrosAdicionales1[a][10]+"', '"+rubrosAdicionales1[a][16]+"', '"+rubrosAdicionales1[a][6]+
                                        "', '0', '"+rubrosAdicionales1[a][17]+"', '"+rubrosAdicionales1[a][18]+"', '"+rubrosAdicionales1[a][4]+
                                        "', '"+rubrosAdicionales1[a][16]+"', '"+rubrosAdicionales1[a][8]+"', '"+rubrosAdicionales1[a][14]+"', '"+rubrosAdicionales1[a][15]+"', 'p', '"+rubrosAdicionales1[a][0]+"'],";

                                        id_cuenta_iva = rubrosAdicionales1[a][8].compareTo("s")==0 ? rubrosAdicionales1[a][19] : rubrosAdicionales1[a][20];
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{rubrosAdicionales1[a][21], "0", rubrosAdicionales1[a][6]});
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", rubrosAdicionales1[a][17]});
                                    }
                                }
                            }

                            //  rubros adicionales procedentes de ordenes de trabajo, venta de activos
                            ResultSet rsRubrosAdicionales2 = objDB.consulta("SELECT distinct PR.*, P.codigo_activo as codigo, (select valor::int as porcentaje from tbl_configuracion where parametro='p_iva1'), "
                                    + "(select I.codigo as codigo_iva from tbl_iva as I inner join tbl_configuracion as C on C.valor::int=I.porcentaje where parametro='p_iva1'), "
                                    + "monto as precioUnitario, "
                                    + "(select (PR.monto * valor::numeric / 100 )::numeric(13,2) as iva_12 from tbl_configuracion where parametro='p_iva1'), "
                                    + "(select (monto + (PR.monto * valor::numeric / 100))::numeric(13,2) as total from tbl_configuracion where parametro='p_iva1'), "
                                    + "P.id_plan_cuenta, P.id_plan_cuenta_gasto, P.id_plan_cuenta_grupo, P.valor_depreciado "
                                    + "FROM (tbl_prefactura_rubro as PR inner join vta_activo_n as P on PR.idproductos::int=P.id_activo) "
                                    + "WHERE id_rubro is null and estadocobro=false and tiporubro='1' and id_instalacion=" + id_instalacion + " and periodo<='" + fin + "'");
                            String rubrosAdicionales2[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales2);
                            if(rubrosAdicionales2!=null){
                                for(int a=0; a<rubrosAdicionales2.length; a++){
                                    if(rubrosAdicionales2[a][1].compareTo(id_sucursal)==0 && rubrosAdicionales2[a][3].compareTo(id_instalacion)==0 && rubrosAdicionales2[a][5].compareTo(periodo)==0){

                                        idsPrefacturaRubro += "," + rubrosAdicionales2[a][0];
                                        ids_productos += "," + rubrosAdicionales2[a][9];
                                        descripciones += "," + rubrosAdicionales2[a][4];
                                        cantidades += ",1";
                                        preciosUnitarios += "," + rubrosAdicionales2[a][14];
                                        descuentos += ",0";
                                        subtotales += "," + rubrosAdicionales2[a][14];
                                        ivas += "," + rubrosAdicionales2[a][15];
                                        pIvas += "," + rubrosAdicionales2[a][12];
                                        codigoIvas += "," + rubrosAdicionales2[a][13];
                                        totales += "," + rubrosAdicionales2[a][16];

                                        // 0-idProd, 1-cant, 2-pu, 3-subtotal, 4-desc, 5-iva, 6-total, 7-concepto, 8-precioCosto, 9-tipoRubro, 10-pIva, 11-CodIva, 12-tipoActivo
                                        paramArtic += "['"+rubrosAdicionales2[a][9]+"', '1', '"+rubrosAdicionales2[a][14]+"', '"+rubrosAdicionales2[a][14]+
                                        "', '0', '"+rubrosAdicionales2[a][15]+"', '"+rubrosAdicionales2[a][16]+"', '"+rubrosAdicionales2[a][4]+
                                        "', '"+rubrosAdicionales2[a][14]+"', '"+rubrosAdicionales2[a][8]+"', '"+rubrosAdicionales2[a][12]+"', '"+rubrosAdicionales2[a][13]+"', '1', '"+rubrosAdicionales2[a][0]+"'],";

    //                                    id_cuenta_iva = rubrosAdicionales2[a][8].compareTo("s")==0 ? rubrosAdicionales2[a][17] : rubrosAdicionales2[a][18];
    //                                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{rubrosAdicionales2[a][19], "0", rubrosAdicionales2[a][14]});
    //                                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", rubrosAdicionales2[a][15]});

                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{rubrosAdicionales2[a][17], rubrosAdicionales2[a][20], "0"});
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{rubrosAdicionales2[a][19], "0", rubrosAdicionales2[a][14]});
                                        double diferencia = Float.parseFloat(rubrosAdicionales2[a][14]) - (Float.parseFloat(rubrosAdicionales2[a][14]) - Float.parseFloat(rubrosAdicionales2[a][20]) );
                                        if (diferencia > 0) {
                                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{idPlanCuentaUtilidadActivo, "0", String.valueOf(this.redondear(diferencia))});
                                        }
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{idPlanCuentaIvaActivos, "0", rubrosAdicionales2[a][15]});
                                    }
                                }
                            }



                            String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
                            String paramAsiento = "";
                            for(int i=0; i<matParamAsiento.length; i++){
                                paramAsiento += "['"+matParamAsiento[i][0]+"', '"+matParamAsiento[i][1]+"', '"+matParamAsiento[i][2]+"'],";
                            }
                            if(paramAsiento.compareTo("")!=0){
                                paramAsiento = paramAsiento.substring(0, paramAsiento.length()-1);
                            }

                            paramArtic = paramArtic.substring(0, paramArtic.length()-1);




                            /*String prod_sin_stock = this.verificarStock(id_sucursal, ids_productos, cantidades_prod);     //     no se necesita ya quye es solo el servicio
                            if(prod_sin_stock.compareTo("")==0){*/

                                if(!objPrefactura.facturaDuplicada(serie_factura, num_factura )){

                                    String estadoDocumento = "p";
                                    String certificado = _dir + "certificado.p12";
                                    String rutaSalida = _dir + "firmados";
                                    String claveAcceso = "";
                                    String autorizacionXml = "";
                                    String numAutorizacion = "";
                                    String fechaAutorizacion =  "null";
                                    String vecSerie[] = serie_factura.split("-");


                                    FacturaElectronica objFE = new FacturaElectronica();    //   se tiene que blanquear un nuevo archivo XML

                                    claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
//  PRUEBAS
//razon_social = "PRUEBAS SERVICIO DE RENTAS INTERNAS";

                                    String xmlFirmado =objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
                                            Cadena.setSecuencial(num_factura), dir_matriz, Fecha.getFecha("SQL"), dir_matriz, num_resolucion, oblga_contabilidad, 
                                            tipo_documento, razon_social, ruc, subtotal, descuento, "0", subtotal_2, iva_2, subtotal_3, iva_3, total_pagar, formaPago, 
                                            ids_productos, descripciones, cantidades, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion, plan);
                                    String documentoXml = _dir + "generados/" + claveAcceso + ".xml";
                                    objFE.salvar(documentoXml);
                                    String error = objFE.getError();

                                    if(error.compareTo("")==0){
                                        estadoDocumento = "g";
                                        String archivoSalida = claveAcceso + ".xml";
                                        FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
                                        firmaDigital.execute();
                                        error = firmaDigital.getError();



                                        if(error.compareTo("")==0){
                                            estadoDocumento = "f";
                                            autorizacionXml = this.getStringFromFile(_dir + "firmados/" + claveAcceso + ".xml");

                                            

                                            String idFactura = objPrefactura.emitir(id_sucursal, Integer.parseInt(idPuntoEmision), idPrefactura, id_instalacion, usuarioCaja, serie_factura, num_factura, "1119999999", ruc,
                                                    idFormaPago, formaPagoSaitel, descripcionFormaPago, "", num_documento, "0", id_plan_cuenta_banco, "", "Emisión de la factura por servicios de Internet Nro. " + serie_factura + "-" + num_factura, 
                                                    subtotal, "0", subtotal_2, subtotal_3, descuento, iva_2, iva_3, total_pagar, "array[" + paramArtic + "]", "", "0", 
                                                    "", "", "", "NULL", "0", "array[]::varchar[]", "array[" + paramAsiento + "]", xmlFirmado, String.valueOf(dias_conexion), ids_productos, cantidades, 
                                                    preciosUnitarios, descuentos, subtotales, ivas, totales, tipoRubros, idsPrefacturaRubro, "", "", estado_servicio, claveAcceso);


                                            if(idFactura.compareTo("-1")!=0){
                                                long numFacturaAux = Long.parseLong(num_factura) + 1;
                                                matPuntosVirtuales[0][4] = String.valueOf(numFacturaAux);

                                                objDB.ejecutar("update tbl_prefactura set por_emitir_factura=false where id_prefactura="+idPrefactura);

//                                                objDB.ejecutar("update tbl_factura_venta set conciliado=false, estado_documento='"+estadoDocumento+"', clave_acceso='"+claveAcceso
//                                                +"' where id_factura_venta="+idFactura);

                                                try {
                                                //  envio al sri
                                                    String rutaArchivoFirmado = _dir + "firmados";
                                                    ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                                                    File ArchivoXML = new File(rutaArchivoFirmado+ File.separatorChar + claveAcceso + ".xml");

                                                    respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, claveAcceso, _WSENVIO);
                                                    String estado = respuestaRecepcion.getEstado();
                                                    if(estado.equals("RECIBIDA")){
                                                        estadoDocumento = "r";
                                                        error = "";

    //                                                    System.out.print("Iniciando consulta de autorizacion: " + Fecha.getHora());
    //                                                    String respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(claveAcceso, claveAcceso + ".xml", _WSAUTORIZA);
    //                                                    if (respuestaAutoriz.equals("AUTORIZADO")) {
    //                                                        estadoDocumento = "a";
    //        //                                                objArchivo.setArchivoDocumentalTexto(autorizacionXml, num_factura, "" + id_sucursal, "tbl_factura_venta", id_factura_, "documentoxml", "public", "db_isp");
    //                                                        try {
    //                                                            autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
    //                                                            // obtengo en numero de autorizacion
    //                                                            Xml xml = new Xml();
    //                                                            xml.SetXml(autorizacionXml);
    //                                                            numAutorizacion = xml.getValor("numeroAutorizacion");
    //                                                            fechaAutorizacion = xml.getValor("fechaAutorizacion");
    //                                                            fechaAutorizacion = Fecha.FechaConverter( fechaAutorizacion );
//                                                                fechaAutorizacion = fechaAutorizacion.compareTo("")!=0 && fechaAutorizacion.toLowerCase().compareTo("null")!=0 ? "'" + fechaAutorizacion + "'" : "null";
    //                                                        } catch(Exception e){
    //                                                            e.printStackTrace();
    //                                                        }
    //        //                                                this.setDocumentosMail(objArchivo, clave_acceso, archivo);
    //
    //                                                    } else {
    //                                                        if (respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")) {
    //                                                            estadoDocumento = "n";
    //                                                        }
    //                                                        error = respuestaAutoriz;
    //                                                    }
    //                                                    System.out.print("Fin consulta de autorizacion: " + Fecha.getHora());

            //                                          ok = true;
                                                    } else { 
                                                        String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                                                        estadoDocumento = "n";
                                                        error = respuesta;
                                                    }
                                                } catch(Exception e) {
                                                    e.printStackTrace();
                                                }
                                                
                                                objDB.ejecutar("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', numero_autorizacion='" + numAutorizacion + "', autorizacion_fecha=" + fechaAutorizacion 
                                                    + ", clave_acceso='" + claveAcceso + "', documento_xml='" + autorizacionXml + "', mensaje='" + error.replace("|", ".").replace("\n", " ").replace("\r", " ")
                                                    + "' where (estado_documento<>'a' or estado_documento is null) and id_factura_venta=" + idFactura);
                                                
                                                Archivo objArchivo = new Archivo(_ipdocumental, _puertodocumental, _dbdocumental, _usuariodocumental, _clavedocumental);
                                                objArchivo.setArchivoDocumentalTexto(autorizacionXml, num_factura, "" + id_sucursal, "tbl_factura_venta", idFactura, "documentoxml", "public", "db_isp");
                                                objArchivo.cerrar();
//                                                objFacturaVenta.setDocumentoElectronico(idFactura, estadoDocumento, claveAcceso, autorizacionXml, error, numAutorizacion, fechaAutorizacion);    
                                                
                                                
                                                transaccion = "EMISION DE LA FACTURA NRO. "+serie_factura+"-"+num_factura+" CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txt_periodo;

                                                if( modoSincronizacionMikrotiks.compareTo("apis") == 0 ) {
                                                    Mikrotik objMikrotik = new Mikrotik(maquina, puerto, db, usuario, clave);
                                                    objMikrotik.conectar(id_sucursal, ip);
                                                    objMikrotik.actualizarInstalacionEnServidor(id_instalacion);
                                                    objMikrotik.MikrotikCerrar();
                                                }
                                                
                                                mensaje = "ok";

                                            }else{
                                                transaccion += "CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txt_periodo + ". Error al emitir la factura. " + objDB.getError();
                                            }
                                        }else{
                                            System.out.println("Error al firmar la factura en formato xml. " + error);
                                        }
                                    }else{
                                        System.out.println("Error al generar la factura en formato xml. " + error);
                                    }
                                }else{
                                    transaccion = "CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txt_periodo + ". El número de factura "+serie_factura+"-"+num_factura+" ya ha sido emitido.";
                                }

                        }else{
                            mensaje = "El saldo contable no cubre el total de la factura";
                            transaccion = "CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txt_periodo;
                        }
                    }catch(Exception e){
                        transaccion = "Error en la emision de la prefactura ID: P" + idPrefactura + ". " + e.getMessage() + ". " + objDB.getError();
                        //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
                    }

                    rs.close();
                }else{
                    mensaje = "Número de ID P"+idPrefactura+" no identificado";
                }

                objDB.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
                              "values('"+usuarioCaja+"','127.0.0.1','"+Fecha.getHora()+"','"+Fecha.getFecha("ISO")+"', '"+transaccion  + ". MSGFACILITO -> " + mensaje + "');");

            }catch(Exception e){
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
            }finally{
                try{
                    objPrefactura.ejecutar("update tbl_configuracion set valor='false' where parametro = 'bloqueo_libros'");
                }catch(Exception e){
                    e.printStackTrace();
                }

                String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de emisión de facturas WS";
                System.out.println(msg);

                objDB.cerrar();
                objPrefactura.cerrar();
                //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", "sistemas@saitel.ec", "", "CONTABILIZACION", new StringBuilder(msg), true);
            }
        
        
        }
        
        return mensaje.replace("\n", ". ");
    }
    
    
    private String facturaCobrar(String id_factura, String num_documento, String idPuntoEmision)
    {
        String mensaje = "Error 500";
        
        String bloqueo_libros = "true";
        Configuracion objDB = new Configuracion(maquina, puerto, db, usuario, clave);
        while(true){
            bloqueo_libros = objDB.getValor("bloqueo_libros");
            if(bloqueo_libros.compareTo("false")==0) {
                objDB.setValor("bloqueo_libros", "true");
                break;
            }
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        
        try{
            ResultSet rs = objDB.consulta("select * from vta_factura_venta where id_factura_venta=" + id_factura);
            if(rs.next()){
                String fecha_proceso = this.getFecha("ISO");
                String fecha_efectivo = fecha_proceso;
                String id_plan_cuenta = rs.getString("id_plan_cuenta")!=null ? rs.getString("id_plan_cuenta") : "12";
                String id_instalacion = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "";
                String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                String detalle = "PAGO DESDE SERVICIO WEB "+num_documento+", de la venta efectuada a " + razon_social;
                String total = rs.getString("deuda")!=null ? rs.getString("deuda") : "0";
                String num_cheque = "";
                String banco = "";
                String son = "";
                
                String ret_num_retencion = "";
                String ret_num_serie = "";
                String ret_autorizacion = "";
                String ret_fecha_emision = "null";
                String ret_impuesto_retenido = "0";
                String ret_ejercicio_fiscal = "0";
                
                String id_plan_cuenta_banco="0";
                
                try{
                    ResultSet rsPE = objDB.consulta("SELECT * from vta_empresa WHERE id_punto_emision="+idPuntoEmision);
                    if(rsPE.next()){
                        id_plan_cuenta_banco = (rsPE.getString("id_plan_cuenta_caja")!=null) ? rsPE.getString("id_plan_cuenta_caja") : "";
                        banco = (rsPE.getString("empresa")!=null) ? rsPE.getString("empresa") : "";
                        if(banco.length()>=50){
                            banco = banco.substring(0, 49);
                        }
                        rsPE.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                /*try{
                    ResultSet rsBnc = objDB.consulta("SELECT * from tbl_banco WHERE facturar_servicio_web_coopmego=true");
                    if(rsBnc.next()){
                        id_plan_cuenta_banco = (rsBnc.getString("id_plan_cuenta")!=null) ? rsBnc.getString("id_plan_cuenta") : "0";
                        banco = (rsBnc.getString("banco")!=null) ? rsBnc.getString("banco") : "";
                        rsBnc.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }*/
                
                
                
                String id_sucursal = "1";
                long num_compIngr = 0;
                try{
                    ResultSet rsComp = objDB.consulta("SELECT max(num_comprobante) FROM tbl_comprobante_ingreso;");
                    if(rsComp.next()){
                        num_compIngr = (rsComp.getString(1)!=null) ? rsComp.getLong(1) : 0;
                        num_compIngr++;
                        rsComp.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            
                String param = this.concatenarValores(id_plan_cuenta_banco+","+id_plan_cuenta, total+",0", "0,"+total);
                String paramAbonos = this.concatenarValores(id_factura, total, "F");
                        
                ResultSet rsCobro = objDB.consulta("select proc_comprobanteIngresoCobro("+id_sucursal+", "+idPuntoEmision+", 'servicioweb', "+num_compIngr+", '"+fecha_proceso+"', '"+fecha_efectivo+"', '"+
                razon_social+ "', 'w', '"+num_cheque+"', '"+banco+"', '"+num_documento+"', 0, "+total+", "+total+", '"+son+"', '"+detalle+"', "+param+", "+paramAbonos+
                ", "+total+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                ", "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+id_factura+", array[]::varchar[]);");
                if(rsCobro.next()){
                    String id_factura_cobro_id_ingreso = (rsCobro.getString(1)!=null) ? rsCobro.getString(1) :"-1:-1";    
                    if(id_factura_cobro_id_ingreso.compareTo("-1:-1") != 0){
                        
                        mensaje = "ok";
                        if(id_instalacion.compareTo("")!=0){
                            
                            String radusername = "";
                            String id_plan_actual = "";
                            String estado_servicio = "";
                            try{
                                ResultSet rsInstalacion = objDB.consulta("SELECT *, toDateSQL(fecha_instalacion) as sql_fecha_instalacion FROM vta_instalacion where id_instalacion="+id_instalacion);
                                if(rsInstalacion.next()){
                                    radusername = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
                                    id_plan_actual = (rsInstalacion.getString("id_plan_actual")!=null) ? rsInstalacion.getString("id_plan_actual") : "";
                                    estado_servicio = (rsInstalacion.getString("estado_servicio")!=null) ? rsInstalacion.getString("estado_servicio") : "";
                                    rsInstalacion.close();
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            
                            if(estado_servicio.toLowerCase().compareTo("a")==0){
                                String value="suspendidos";
                                try{
                                    ResultSet rsPlan = objDB.consulta("SELECT substr( plan, 1, 4) || substr( split_part(solo_plan, ' ', 2), 1, 5) || substr( split_part(solo_plan, ' ', 3), 1, 4) || burst_limit::varchar from vta_plan_servicio where id_plan_servicio="+id_plan_actual);
                                    if(rsPlan.next()){
                                        value = rsPlan.getString(1)!=null ? rsPlan.getString(1) : "suspendidos";
                                        rsPlan.close();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                                String rad_db = this.getValor(objDB, "rad_db");
                                String rad_ip = this.getValor(objDB, "rad_ip");
                                String rad_puerto = this.getValor(objDB, "rad_puerto");
                                String rad_usuario = this.getValor(objDB, "rad_usuario");
                                String rad_clave = this.getValor(objDB, "rad_clave");
                                DataBase dbRadius = new DataBase(rad_ip, Integer.parseInt(rad_puerto), rad_db, rad_usuario, rad_clave);
                                dbRadius.ejecutar("UPDATE radusergroup SET groupname='"+value+"' WHERE username in ('"+radusername+"')");
                                dbRadius.cerrar();
                            }
                        }

                        objDB.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
                                        "values('servicioweb', '127.0.0.1', '"+this.getHora()+"', '"+this.getFecha("ISO")+
                                        "', 'INGRESO DE NUEVO COMPROBANTE DE INGRESO A TRAVES DEL WEB SERVICES CON NUMERO: "+num_compIngr+" POR CONCEPTO: "+detalle+");");
                    }
                    rsCobro.close();
                }
                
                rs.close();
            }   
            
        }catch(Exception e){
            mensaje = "Numero de ID F"+id_factura+" no identificado;";
        }finally{
            objDB.ejecutar("update tbl_configuracion set valor='false' where parametro='bloqueo_libros'");
            objDB.cerrar();
        }
        
        return mensaje.replace("\n", ". ");
    }
   
    
    @WebMethod(operationName = "reversar")
    public String reversar(@WebParam(name = "clave") String clave, @WebParam(name = "idRegistroConsulta") String idRegistroConsulta) 
    {
        //TODO write your implementation code here:
//        if(clave.compareTo("")!=0){
//            String puntoEmision[] = this.getPuntoEmision(clave);
//            if(puntoEmision[0].compareTo("-1")!=0){
//                String idPrefactura = idRegistroConsulta.compareTo("")!=0 ? idRegistroConsulta : "P0";
//                if(idPrefactura.indexOf("P")==0){
//                    return this.anularFactura(idRegistroConsulta.replace("P", ""));
//                /*}else if(id_prefactura.indexOf("F")==0){
//                          return this.anularCobro(idRegistroConsulta.replace("F", ""));*/
//                }
//            }else{
//                return "Clave de acceso erronea";
//            }
//        }else{
//            return "No se ha proporcionado una clave de acceso";
//        }
//        return "Error 500";
        
        return "La anulación de facturas se encuentra desabilitada. Por favor, comuníquese con el departamento financiero de SAITEL para anulación de facturas.";
    }
    
    
    private String anularFactura(String idPrefactura)
    {
        DataBase objDB = new DataBase(maquina, puerto, db, usuario, clave);
        String mensaje = "ID P"+idPrefactura+" no identificado";
        try{

            ResultSet rsPF = objDB.consulta("SELECT P.id_factura_venta, F.estado_documento FROM tbl_prefactura as P inner join tbl_factura_venta as F on P.id_factura_venta=F.id_factura_venta where P.id_prefactura="+idPrefactura);
            if(rsPF.next()){
                
                String id_factura_venta = rsPF.getString("id_factura_venta")!=null ? rsPF.getString("id_factura_venta") : "";
                if(id_factura_venta.compareTo("")!=0){
                    
                    mensaje = "La factura se encuentra autorizada por el SRI, comuníquese con las oficinas de SAITEL para que le ayuden anulando el documento.";
                    String estado_documento = rsPF.getString("estado_documento")!=null ? rsPF.getString("estado_documento") : "";
                    if(estado_documento.compareTo("a")!=0){
                        
                        mensaje = "Error 500";
                        ResultSet res = objDB.consulta("select proc_anularFacturaVenta("+id_factura_venta+");");
                        if(res.next()){
                            
                            boolean ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                            if(ok){
                                mensaje = "ok";
                                try{
                                    ResultSet rsFactura = objDB.consulta("SELECT * FROM tbl_factura_venta where id_factura_venta="+id_factura_venta);
                                    if(rsFactura.next()){
                                        String serie_factura = (rsFactura.getString("serie_factura")!=null) ? rsFactura.getString("serie_factura") : "";
                                        String num_factura = (rsFactura.getString("num_factura")!=null) ? rsFactura.getString("num_factura") : "";
                                        objDB.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
                                            "values('servicioweb', '127.0.0.1','"+this.getHora()+"','"+this.getFecha("ISO")+
                                            "', 'ANULACION DE LA FACTURA DE VENTA: "+serie_factura+"-"+num_factura + " Y DE TODOS SUS DOCUMENTOS ADJUNTOS A TRAVES DEL WEB SERVICES');");
                                        rsFactura.close();
                                    }
                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }
                            } 
                            
                        }
                        
                    }
                    
                }
                
            }
            
        }catch(Exception e){
            mensaje = "Error 500";
            e.printStackTrace();
        }finally{
            objDB.cerrar();
        }
            
        return mensaje;
    }
    
    
    
    
    
    
//_____________________________________________________________________________________________

    
    
    private String[] getPuntoEmision(String clave1)
    {
        String puntoEmision[] = new String[]{"-1","f","-1","0",""};
        DataBase objDB = new DataBase(maquina, puerto, db, usuario, clave);
        try{
            ResultSet rsCliente = objDB.consulta("SELECT E.id_punto_emision, prepago, id_plan_cuenta_caja, fondo_prepago, correo from tbl_empresa as E inner join tbl_punto_emision as P on P.id_punto_emision=E.id_punto_emision where clave='"+clave1+"'");
            if(rsCliente.next()){
                puntoEmision[0] = rsCliente.getString("id_punto_emision")!=null ? rsCliente.getString("id_punto_emision") : "-1";
                puntoEmision[1] = rsCliente.getString("prepago")!=null ? rsCliente.getString("prepago") : "f";
                puntoEmision[2] = rsCliente.getString("id_plan_cuenta_caja")!=null ? rsCliente.getString("id_plan_cuenta_caja") : "-1";
                puntoEmision[3] = rsCliente.getString("fondo_prepago")!=null ? rsCliente.getString("fondo_prepago") : "0";
                puntoEmision[4] = rsCliente.getString("correo")!=null ? rsCliente.getString("correo") : "";
                rsCliente.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDB.cerrar();
        }
        return puntoEmision;
    }
    
    
    private float getSaldoPorContabilizar(String idPuntoEmision)
    {
        float saldo_punto_emision = 0;
        DataBase objDB = new DataBase(maquina, puerto, db, usuario, clave);
        try{
            ResultSet rsSaldo = objDB.consulta("select sum(total) from tbl_factura_venta where contabilizado=false and anulado=false and id_punto_emision="+idPuntoEmision);
            if(rsSaldo.next()){
                saldo_punto_emision = rsSaldo.getString(1)!=null ? rsSaldo.getFloat(1) : 0;
                rsSaldo.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDB.cerrar();
        }
        return saldo_punto_emision;
    }
    
    private float getSaldoCuentaContable(String id_plan_cuenta)
    {
        float saldo_punto_emision = 0;
        DataBase objDB = new DataBase(maquina, puerto, db, usuario, clave);
        try{
            ResultSet rsSaldo = objDB.consulta("select case tipo_cuenta when 0 then saldo_deudor when 1 then saldo_acreedor else 0 end " +
            "from tbl_libro_diario_mayor as L inner join tbl_plan_cuenta as P on P.id_plan_cuenta=L.id_plan_cuenta " +
            "where L.id_plan_cuenta="+id_plan_cuenta+" and id_libro_diario_mayor = (select max(id_libro_diario_mayor) from tbl_libro_diario_mayor where id_plan_cuenta="+id_plan_cuenta+")");
            if(rsSaldo.next()){
                saldo_punto_emision = rsSaldo.getString(1)!=null ? rsSaldo.getFloat(1) : 0;
                rsSaldo.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDB.cerrar();
        }
        return saldo_punto_emision;
    }
    
    
    private String[][] getPuntoEmisionEmpresa(PreFactura objPrefactura, String idPuntoEmision)
    {
        ResultSet rs = objPrefactura.consulta("SELECT P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, " + 
            "case when max(num_factura)>0 then max(num_factura)+1 else 1 end, direccion_establecimiento, " +
            "FP.id_forma_pago, FP.codigo, FP.codigo_interno, FP.descripcion, P.id_plan_cuenta_caja " +
            "from ((tbl_punto_emision as P left join tbl_factura_venta as F on F.serie_factura=P.fac_num_serie) " + 
            "inner join tbl_empresa as E on E.id_punto_emision=P.id_punto_emision) " +
            "inner join tbl_forma_pago as FP on FP.id_empresa=E.id_empresa " +
            "where P.id_punto_emision=" + idPuntoEmision +  
            " group by P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, direccion_establecimiento, FP.id_forma_pago, FP.codigo, FP.codigo_interno, FP.descripcion");
        return Matriz.ResultSetAMatriz(rs);
    }
    
    private boolean notificacion(int num, String destino)
    {
        String asunto = "SAITEL - alerta de saldo";
        StringBuilder txt = new StringBuilder();
        txt.append("<p>Estimados REPORNE (Red de Servicios Facilito)</p>");
        switch(num) {
            case 1: txt.append("<h3>SAITEL informa que su fondo prepagado a llegado al consumo del 50%.</h3><p>Atentamente,</p><p>SAITEL</p>"); break;
            case 2: txt.append("<h2 style='color:orange'>SAITEL informa que su fondo prepagado a llegado al consumo del 25%.</h2><p>Atentamente,</p><p>SAITEL</p>"); break;
            case 3: txt.append("<h1 style='color:red'>SAITEL informa que su fondo prepagado a llegado al consumo del 15%.</h1><p>Atentamente,</p><p>SAITEL</p>"); break;
        }
        return Correo.enviar(_svrMail, _svrMailPuerto, _remitante, _remitanteClave, destino, "", "", asunto, txt, true, null);
    }
    
    private String getValor(DataBase objDB, String param)
    {
        String valor = "";
        try{
            ResultSet r = objDB.consulta("SELECT valor FROM tbl_configuracion where parametro='"+param+"';");
            if(r.next()){
                valor = (r.getString("valor")!=null) ? r.getString("valor") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return valor;
    }
    
    private String concatenarValores(String idsCuentas, String debe, String haber)
    {
        String param = "";
        String vecCuentas [] = idsCuentas.split(",");
        String vecDebe [] = debe.split(",");
        String vecHaber [] = haber.split(",");
        for(int i=0; i<vecCuentas.length; i++){
            if(Float.parseFloat(vecDebe[i])!=0 || Float.parseFloat(vecHaber[i])!=0){
                param += "['"+vecCuentas[i]+"','"+vecDebe[i]+"','"+vecHaber[i]+"'],";
            }
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    private String concatenarValores(String id_articulos, String cantidades, String precios_costo, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales, String descripcion)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecPC [] = precios_costo.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecDes [] = descuentos.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        String vecDescMas [] = descripcion.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"', '"+vecDescMas[i]+"','"+vecPC[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    private String getFecha(String tipo)
    {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH) + 1;
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        String cad = anio + "-" + mes + "-" + dia;
        if(tipo.toUpperCase().compareTo("SQL")==0){
            cad = dia + "/" + mes + "/" + anio;
        }
        return cad;
    }
    
    private String getHora()
    {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);
        int segundo = cal.get(Calendar.SECOND);
        String cad = hora + ":" + minuto + ":" + segundo;
        return cad;
    }

    private String redondear(double valor)
    {
        double valor_red =  (Math.round(valor * Math.pow(10, 2)) / Math.pow(10, 2));
        return String.valueOf(valor_red);
    }

    private double redondear(double valor, int decimales)
    {
        return (Math.round(valor * Math.pow(10, decimales)) / Math.pow(10, decimales));
    }
    
    private String setFecha(String fecha)
    {
        String vecFecha[];
        String anio = "0";
        String mes = "0";
        String dia = "0";
        if(fecha.indexOf("/")>0){
            vecFecha = fecha.replace("'", "").replace("\"", "").split("/");
            anio = vecFecha[2];
            mes = vecFecha[1];
            dia = vecFecha[0];
        }else{
            vecFecha = fecha.replace("'", "").replace("\"", "").split("-");
            anio = vecFecha[0];
            mes = vecFecha[1];
            dia = vecFecha[2];
        }
        String fechaRetorno = (dia.length()==1?"0"+dia:dia) + "/" + (mes.length()==1?"0"+mes:mes) + "/" +anio;
        
        return fechaRetorno;
    }
    
    private String setSecuencial(String numero)
    {
        String relleno = "";
        for(int i=0; i<(9-numero.length()); i++){
            relleno += "0";
        }
        return relleno+numero;
    }
    
    private String leer(String directorio, String archivo)
    {
        String fichero = "";
        File file = new File(directorio, archivo);
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new FileReader(file));
            StringBuilder lectura = new StringBuilder();
            String linea = "";
            while ((linea = buffer.readLine()) != null) {
                lectura.append(linea);
            }
            fichero = lectura.toString();
            buffer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fichero;
    }
    
    private int enMatriz(String mat[][], String clave[])
    {
        int p=-1;
        if(mat!=null){
            for(int i=0; i<mat.length; i++){
                if(mat[i][0].compareTo(clave[0])==0 && mat[i][1].compareTo(clave[1])==0 && mat[i][2].compareTo(clave[2])==0){
                    p = i;
                    break;
                }
            }
        }
        return p;
    }
    
    private String getStringFromFile(String archivo) throws IOException {
        File file = new File(archivo);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            cadXml.append(linea);
        }
        return cadXml.toString();
    }
    
}
