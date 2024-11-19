/**
* @version 1.0
* @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* FACTURAPYMES! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package cron.trabajos;
import ec.gob.sri.FirmaXadesBes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class FacturaVenta extends DataBase{
    
    DataBase objDocumental;
    String diaCortesCreditos = "10";
    CorreoSaitel objCorreo = null;
    
    public FacturaVenta(DataBase objDocumental, String m, int p, String db, String u, String c){
        super(m, p, db, u, c);

        this.objCorreo = new CorreoSaitel( Parametro.getRed_social_ip(), Parametro.getRed_social_esquema(), Parametro.getRed_social_puerto(), Parametro.getRed_social_db(), Parametro.getRed_social_usuario(), Parametro.getRed_social_clave() );
        this.objDocumental = objDocumental;
    }
    
    public void emitir(ResultSet rs, List matAnticipos, String matDebitos[][], int pminValorVajaCredito)
    {
        
        String DOCS_ELECTRONICOS = Parametro.getRutaArchivos();
        
        String matPuntosVirtuales[][] = this.getPuntosEmisionVirtuales();
        
        
        
        String id_plan_cuenta_banco="10";
        try{
            ResultSet rsBanco = this.consulta("SELECT * FROM vta_banco where lower(banco) like '%pichincha%'");
            if(rsBanco.next()){
                id_plan_cuenta_banco = rsBanco.getString("id_plan_cuenta")!=null ? rsBanco.getString("id_plan_cuenta") : "10";
                rsBanco.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
            
        String desc_venta = "121";
        String ambiente = "2";      // 1=pruebas    2=produccion
        String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
        String clave_certificado = "";
        String ruc_empresa = "1091728857001";
        String razon_social_empresa = "SOLUCIONES AVANZADAS INFORMATICAS, TELECOMUNICACIONES Y ELECTRICAS SAITEL";
        String nombre_comercial = "";
        String emalSaitel = "";
        String numContSaitel = "";
        String sitioWeb = "";
        String num_resolucion = "";
        String oblga_contabilidad = "SI";
        String dir_matriz = "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
        String idPlanCuentaUtilidadActivo = "-1";
        String idPlanCuentaIvaActivos = "-1";
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_configuracion order by parametro;");
            while(r.next()){
                String parametro = r.getString("parametro")!=null ? r.getString("parametro") : "";
                if(parametro.compareTo("desc_venta")==0){
                    desc_venta = r.getString("valor")!=null ? r.getString("valor") : "121";
                }
                if(parametro.compareTo("ambiente")==0){
                    ambiente = r.getString("valor")!=null ? r.getString("valor") : "2";
                }
                if(parametro.compareTo("clave_certificado")==0){
                    clave_certificado = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("ruc")==0){
                    ruc_empresa = r.getString("valor")!=null ? r.getString("valor") : "1091728857001";
                }
                if(parametro.compareTo("razon_social")==0){
                    razon_social_empresa = r.getString("valor")!=null ? r.getString("valor") : "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
                }
                if(parametro.compareTo("nombre_comercial")==0){
                    nombre_comercial = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("email_info")==0){
                    emalSaitel = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("numeros_soporte")==0){
                    numContSaitel = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("pagina_web")==0){
                    sitioWeb = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("num_resolucion")==0){
                    num_resolucion = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("oblga_contabilidad")==0){
                    oblga_contabilidad = r.getString("valor")!=null ? r.getString("valor") : "SI";
                }
                if(parametro.compareTo("uti_venta_activos")==0){
                    idPlanCuentaUtilidadActivo = r.getString("valor")!=null ? r.getString("valor") : "-1";
                }
                if(parametro.compareTo("iva_cobrado")==0){
                    idPlanCuentaIvaActivos = r.getString("valor")!=null ? r.getString("valor") : "-1";
                }
                if(parametro.compareTo("dia_cortes_creditos")==0){
                    diaCortesCreditos = r.getString("valor")!=null ? r.getString("valor") : "-1";
                }
                
            }
            r.close();
        }catch(Exception e){
            e.printStackTrace();
        }
            



         
        
        ResultSet rsDetalleFactura = this.consulta("select SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"+
                "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien  "+
                "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
                "inner join tbl_iva as I on I.id_iva=SP.id_iva) "+
                "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "+
                "group by SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, case when tiene_iva then '~' else '' end, "+
                "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien order by id_sucursal, id_producto");
        String matDetalleFactura[][] = Matriz.ResultSetAMatriz(rsDetalleFactura);
            
        
        Promocion objPromocion = new Promocion();
        try{    
            while(rs.next()){
                String id_prefactura = rs.getString("id_prefactura")!=null ? rs.getString("id_prefactura") : "-1";
//                System.out.println("id_prefactura " + id_prefactura);
                try{
                    String id_instalacion = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "-1";
                    String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "-1";
                    String id_cliente = rs.getString("id_cliente")!=null ? rs.getString("id_cliente") : "-1"; 
                    String tipo_documento = rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "05"; 
                    String ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : ""; 
                    String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : ""; 
                    int edad = (rs.getString("edad") != null) ? rs.getInt("edad") : 0;
                    String id_plan_cuenta_x_cobrar = (rs.getString("id_plan_cuenta_x_cobrar") != null) ? rs.getString("id_plan_cuenta_x_cobrar") : "";
                    String carne_conadis = (rs.getString("carne_conadis") != null) ? rs.getString("carne_conadis") : "";
                    String direccion = rs.getString("direccion")!=null ? rs.getString("direccion") : ""; 
                    String estado_servicio = rs.getString("estado_servicio")!=null ? rs.getString("estado_servicio") : "c"; 
                    String id_plan_actual = rs.getString("id_plan_actual")!=null ? rs.getString("id_plan_actual") : "-1";
                    String num_cuenta = rs.getString("num_cuenta")!=null ? rs.getString("num_cuenta") : "";
                    String plan = rs.getString("plan")!=null ? rs.getString("plan") : "";
                    String id_producto = rs.getString("id_producto")!=null ? rs.getString("id_producto") : "-1";
                    int dias_conexion = rs.getString("dias_conexion")!=null ? rs.getInt("dias_conexion") : 30;
                    double valor_internet = rs.getString("valor_internet")!=null ? rs.getDouble("valor_internet") : 0;
                    String iva_internet = rs.getString("iva_internet")!=null ? rs.getString("iva_internet") : "0";
                    double total_internet = rs.getString("total_internet")!=null ? rs.getDouble("total_internet") : 0;
                    String subtotal = rs.getString("subtotal")!=null ? rs.getString("subtotal") : "0";
                    String subtotal_2 = rs.getString("subtotal_2")!=null ? rs.getString("subtotal_2") : "0";
                    String subtotal_3 = rs.getString("subtotal_3")!=null ? rs.getString("subtotal_3") : "0";
                    String iva_2 = rs.getString("iva_2")!=null ? rs.getString("iva_2") : "0";
                    String iva_3 = rs.getString("iva_3")!=null ? rs.getString("iva_3") : "0";
                    String descuento = rs.getString("descuento")!=null ? rs.getString("descuento") : "0";
                    String total_pagar = rs.getString("total")!=null ? rs.getString("total") : "0";
                    String periodo = rs.getString("periodo")!=null ? rs.getString("periodo") : "";
                    String txt_periodo = rs.getString("txt_periodo")!=null ? rs.getString("txt_periodo") : "";
                    String fecha_pago = rs.getString("fecha_pago")!=null ? rs.getString("fecha_pago") : "";
                    String hora_pago = rs.getString("hora_pago")!=null ? rs.getString("hora_pago") : "";
                    String registro_archivo_cash = rs.getString("registro_archivo_cash")!=null ? rs.getString("registro_archivo_cash") : "";
                    
                    String convenio_pago = (rs.getString("convenio_pago") != null) ? rs.getString("convenio_pago") : "";
                    String forma_pago_cliente = (rs.getString("forma_pago") != null) ? rs.getString("forma_pago") : "";
                    String tipo_cuenta = (rs.getString("tipo_cuenta") != null) ? rs.getString("tipo_cuenta") : "";
                    String tarjeta_credito_caduca = (rs.getString("tarjeta_credito_caduca") != null) ? rs.getString("tarjeta_credito_caduca") : Fecha.getFecha("ISO");
                    String id_plan_cuenta_anticipo = (rs.getString("id_plan_cuenta_anticipo") != null) ? rs.getString("id_plan_cuenta_anticipo") : "-1";
                    //double totalCash = rs.getString("total_cash")!=null ? rs.getDouble("total_cash") : 0;
                    String emailCliente = rs.getString("email")!=null ? rs.getString("email") : "";
                            
                    
                    
                    try {
                        ResultSet r = this.consulta("SELECT nombre_comercial, mail_info, num_contacto, sitio_web FROM tbl_sucursal where id_sucursal=" + id_sucursal);
                        if (r.next()) {
                            nombre_comercial = (r.getString("nombre_comercial") != null) ? r.getString("nombre_comercial") : "";
                            emalSaitel = (r.getString("mail_info") != null) ? r.getString("mail_info") : "";
                            numContSaitel = (r.getString("num_contacto") != null) ? r.getString("num_contacto") : "";
                            sitioWeb = (r.getString("sitio_web") != null) ? r.getString("sitio_web") : "";
                            r.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    
                    String idFormaPago="97";        //  id forma de pago cash
                    String formaPago="20";          //  con utilizacion del sistema financiero
                    String formaPagoCodInterno="h"; //  cash
                    
                    
                    
                    // CONVENIO DE DEBITO
                    
                    if( Matriz.enMatriz(matDebitos, id_instalacion, 0) >=0 && registro_archivo_cash.compareTo("")==0){
                        idFormaPago="99";        //  id forma de pago cash
                        formaPago="20";          //  con utilizacion del sistema financiero
                        formaPagoCodInterno="d";
                    }
                    
                   
                    //  POR ANTICIPOS
                    String idCliAnt = "";
                    String monto_vajar = "";
                    if(matAnticipos != null){
                        if(!matAnticipos.isEmpty()){    
                            idFormaPago="98";
                            formaPagoCodInterno="a";

                            double totalPagar = Double.parseDouble(total_pagar);
                            double minTotalPagarACredito = Double.parseDouble(total_pagar) * pminValorVajaCredito / 100;
                            double abonos = 0;
                            double axUltimoAbono = 0;
                            String axIdCliAnt = "";
                            String axMontoVajar = "";
                            Iterator it = matAnticipos.iterator();
                            while(it.hasNext()){
                                String matAnticipo[] = (String[])it.next();

    //                            System.out.println("mat anticipo " + matAnticipo[1] + " - " + matAnticipo[3]);

                                if(matAnticipo[1].compareTo(id_cliente)==0 && matAnticipo[3].compareTo(id_instalacion)==0){    //  verificao si el cliente tiene registrado anticipo
                                    if( Double.parseDouble(matAnticipo[2]) >= totalPagar){    //  si el anticipo cubre el total de la factura 
                                        idCliAnt = matAnticipo[0];
                                        monto_vajar = total_pagar;
                                        break;
                                    } else {        //  si el anticipo no cubre el monto
                                        abonos += Double.parseDouble(matAnticipo[2]);
                                        axUltimoAbono = Double.parseDouble(matAnticipo[2]);
                                        axIdCliAnt += matAnticipo[0] + ",";
                                        axMontoVajar += matAnticipo[2] + ",";
                                        if(abonos >= totalPagar){     //  si el o los abonos cubren el total se salta al pago
                                            break;
                                        }
                                    }
                                }
                            }

                            if(axIdCliAnt.compareTo("")!=0 && abonos>0){
                                idCliAnt = axIdCliAnt.substring(0, axIdCliAnt.length()-1);
                                monto_vajar = axMontoVajar = axMontoVajar.substring(0, axMontoVajar.length()-1);
                                if(abonos == totalPagar){       //  si los abonos cubre el total de la factura se vaja la factura completa
                                    monto_vajar = axMontoVajar;
                                } else if(abonos > totalPagar){     // se cubre más del total de la factura se vaja la factura completa
                                            monto_vajar = axMontoVajar.substring( 0, axMontoVajar.lastIndexOf(",") )  + ","  +  Addons.redondear(totalPagar - (abonos - axUltimoAbono) ) + ","; //    el ultimo abono debe ser 
                                } else if(abonos >= minTotalPagarACredito){     //  si los abono cubren por lo menos el minimo a vajar se vana credito
                                            monto_vajar = axMontoVajar;
                                }
                            }
                        }
                    }    
                        
                    
                     
                    
                    int p = Matriz.enMatriz(matPuntosVirtuales, id_sucursal, 0);
                    if(p!=-1){
                        
                        
                        String idPuntoEmision = matPuntosVirtuales[p][1];
                        String usuario = matPuntosVirtuales[p][2];
                        String serie_factura = matPuntosVirtuales[p][3];
                        String num_factura = matPuntosVirtuales[p][4];
                        String direccion_sucursal = matPuntosVirtuales[p][5];
                        String num_comp_pago = registro_archivo_cash.compareTo("")!=0 
                                ? registro_archivo_cash 
                                : fecha_pago.replace("-", "") + hora_pago.replace(":", "") + id_instalacion;
                        int pos = Matriz.enMatriz(matDetalleFactura, new String[]{id_sucursal,id_producto}, new int[]{0,1});
                        String p_u = String.valueOf( Addons.redondear(valor_internet / dias_conexion,  4) );
                        String totalpr = String.valueOf( Addons.redondear( total_internet ) );

                        String ids_productos = id_producto;
                        String descripciones = "SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+txt_periodo+" ~";
                        String cantidades = String.valueOf( dias_conexion );
                        String preciosUnitarios = p_u;
//                        int p_des0 = ((edad>=65 || carne_conadis.compareTo("")!=0) && Float.parseFloat(descuento)>0) ? 50 : 0;
                        String descuentos = "0";
                        String subtotales = String.valueOf(valor_internet);
                        String ivas = iva_internet;
                        String pIvas = matDetalleFactura[pos][6];
                        String codigoIvas = matDetalleFactura[pos][11];
                        String totales = totalpr;
                        String tipoRubros = "p";
                        String idsPrefacturaRubro = "";
                        
                        
                        
                        //  promociones
                        if( objPromocion.aplicarPromocion(id_sucursal, edad, carne_conadis, convenio_pago, forma_pago_cliente, tipo_cuenta, num_cuenta, tarjeta_credito_caduca, 
                                id_plan_actual, total_internet, Float.parseFloat(descuento), Integer.parseInt(matDetalleFactura[pos][6]) ) ){
                            descuento = String.valueOf( objPromocion.getDescuento() );
                            ivas = iva_internet = String.valueOf( objPromocion.getIva() );
                            totales = totalpr = String.valueOf( objPromocion.getTotal() );
                        }
                        
                        
                        

                        String matParamAsientoAx[][] = null;
                        
                        if(formaPagoCodInterno.compareTo("d")==0){
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_x_cobrar, String.valueOf(total_pagar), "0"});
//                        }else if(forma_pago.compareTo("e")==0){
//                                 matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_caja, String.valueOf(ax_total), "0"});
                        }else if(formaPagoCodInterno.compareTo("c")==0 || formaPagoCodInterno.compareTo("p")==0 || formaPagoCodInterno.compareTo("h")==0){
                                 matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_banco, String.valueOf(total_pagar), "0"});
//                        }else if(forma_pago.compareTo("t")==0 || forma_pago.compareTo("j")==0){
//                                 float gastosBancarios = Float.parseFloat(gastos_bancos);
//                                 if(gastosBancarios==0){
//                                     matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_banco, String.valueOf(total_pagar), "0"});
//                                 }else{
//                                     double diferencia = Addons.redondear( Float.parseFloat(total_pagar) - gastosBancarios );
//                                     matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_banco, String.valueOf(diferencia), "0"});
//                                     matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{idPlanCuentaGastosBancos, String.valueOf(gastosBancarios), "0"});
//                                 }
                        }else if(formaPagoCodInterno.compareTo("a")==0){
                                 matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_anticipo, String.valueOf(total_pagar), "0"});
                        }
                        
//                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_banco, total_pagar, "0"});
                        if(Float.parseFloat(descuento)>0){
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, descuento, "0"});
                            descuentos = descuento;
                        }
                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{matDetalleFactura[pos][12], "0", String.valueOf(valor_internet)});//   id_planCuenta del servicio

                        String id_cuenta_iva = matDetalleFactura[pos][14];
                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", iva_internet});


                        String paramArtic = "['"+id_producto+"', '"+dias_conexion+"', '"+p_u+"', '"+valor_internet+
                            "', '"+descuento+"', '"+iva_internet+"', '"+totalpr+"', '"+descripciones+
                            "', '"+matDetalleFactura[pos][4]+"', '"+matDetalleFactura[pos][9]+"', '"+pIvas+"', '"+codigoIvas+"', '"+tipoRubros+"', '-1'],";

                        int anio = Fecha.datePart("anio", periodo);
                        int mes = Fecha.datePart("mes", periodo);
                        String ini = anio + "-" + mes + "-01";
                        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        
                        ResultSet rsRubrosAdicionales = this.consulta(  
                            "SELECT distinct id_rubro, usuario, fecha_creacion, id_producto, rubro, temporal, fecha_inicio, fecha_fin, id_sucursal, id_instalacion, " +
                            "periodo, monto, subtotal_12, iva_12, codigo, descripcion, porcentaje, codigo_iva, tipo, id_plan_cuenta_venta, " +
                            "id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien, canproductos, rubro_prefactura, tiporubro, estadocobro, id_prefactura_rubro " +
                            "from vta_prefactura_rubro "+ 
                            "WHERE estadocobro=false and id_instalacion="+id_instalacion+" and id_sucursal="+id_sucursal+" and tiporubro='a' and periodo between '" + ini + "' and '" + fin + "' " +
                            "union all " +
                            "SELECT distinct id_rubro, usuario, fecha_creacion, id_producto, rubro, temporal, fecha_inicio, fecha_fin, id_sucursal, id_instalacion, " +
                            "periodo, monto, subtotal_12, iva_12, codigo, descripcion, porcentaje, codigo_iva, tipo, id_plan_cuenta_venta, " +
                            "id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien, canproductos, rubro_prefactura, tiporubro, estadocobro, id_prefactura_rubro " +
                            "from vta_prefactura_rubro "+ 
                            "WHERE estadocobro=false and id_instalacion="+id_instalacion+" and id_sucursal="+id_sucursal+" and tiporubro='p' and estadocobro='false' and periodo <= '" + fin + "' " +
                            "union all " +
                    //  suministros
                            "SELECT distinct PR.id_rubro, 'administrador' as usuario, now()::date as fecha_creacion, PR.idproductos::int8 as id_producto, PR.rubro, true as temporal, \n" +
                            "now()::date as fecha_inicio, now()::date as fecha_fin, PR.id_sucursal, PR.id_instalacion, \n" +
                            "PR.periodo, (PR.monto + (PR.monto * I.porcentaje::numeric / 100))::numeric(13,2) as total, PR.monto as subtotal_12, \n" +
                            "(PR.monto * I.porcentaje::numeric / 100)::numeric(13,2) as iva_12, P.codigo, descripcion, porcentaje, I.codigo as codigo_iva, tipo, id_plan_cuenta_venta, \n" +
                            "I.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien, PR.canproductos, PR.rubro as rubro_prefactura, PR.tiporubro, PR.estadocobro, id_prefactura_rubro \n" +
                            "FROM (((tbl_prefactura_rubro as PR inner join vta_producto_n as P on PR.idproductos::int=P.id_producto) \n" +
                            "inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) \n" +
                            "inner join tbl_iva as I on I.id_iva=SP.id_iva) \n" +
                            "WHERE PR.id_rubro is null and PR.estadocobro=false and PR.tiporubro='p' and PR.id_instalacion=" + id_instalacion + " and PR.periodo<='" + fin + "' " + 
                    // activos
                            "union all " + 
                            "SELECT distinct id_rubro, 'administrador' as usuario, now()::date as fecha_creacion, idproductos::int8 as id_producto, rubro, " + 
                            "true as temporal, now()::date as fecha_inicio, now()::date as fecha_fin, PR.id_sucursal, id_instalacion, periodo,  " +
                            "(select PR.monto + (PR.monto * valor::numeric / 100 )::numeric(13,2) from tbl_configuracion where parametro='p_iva1') as monto,  " +
                            "monto as subtotal_12,  " +
                            "(select (PR.monto * valor::numeric / 100 )::numeric(13,2) from tbl_configuracion where parametro='p_iva1') as iva_12,  " +
                            "P.codigo_activo as codigo, P.descripcion, porcentaje, " +
                            "(select I.codigo from tbl_iva as I inner join tbl_configuracion as C on C.valor::int=I.porcentaje where parametro='p_iva1') as codigo_iva,  " +
                            "'a' as tipo,  " + 
                            "(select D.id_plan_cuenta_grupo from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion where A.id_activo=P.id_activo) as id_plan_cuenta_venta,  " +
                            "(select I.id_iva from tbl_iva as I inner join tbl_configuracion as C on C.valor::int=I.porcentaje where parametro='p_iva1') as id_iva,  " +
                            "(select D.id_plan_cuenta_grupo from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion where A.id_activo=P.id_activo) as id_plan_cuenta_venta_servicio,  " +
                            "(select D.id_plan_cuenta_grupo from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion where A.id_activo=P.id_activo) as id_plan_cuenta_venta_bien,  " +
                            "canproductos, rubro as rubro_prefactura, PR.tiporubro, PR.estadocobro, PR.id_prefactura_rubro " +
                            "FROM (tbl_prefactura_rubro as PR inner join vta_activo_n as P on PR.idproductos::int=P.id_activo)  " +
                            "WHERE id_rubro is null and estadocobro=false and tiporubro='1' and id_instalacion="+id_instalacion+" and periodo<='"+fin+"'"
                        );
                        String rubrosAdicionales[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales);
                        if(rubrosAdicionales!=null){
                            for(int a=0; a<rubrosAdicionales.length; a++){
//                                if(rubrosAdicionales[a][8].compareTo(id_sucursal)==0 && rubrosAdicionales[a][9].compareTo(id_instalacion)==0 && rubrosAdicionales[a][10].compareTo(periodo)==0){
                                    double pU = Addons.redondear( Double.parseDouble(rubrosAdicionales[a][12]) / Double.parseDouble(rubrosAdicionales[a][23]) , 4 );
                                    if(rubrosAdicionales[a][18].compareTo("1")==0){ // activo
                                        try {
                                            
                                            paramArtic += "['"+rubrosAdicionales[a][3]+"', '"+rubrosAdicionales[a][23]+"', '"+pU+"', '"+rubrosAdicionales[a][12]+
                                            "', '0', '"+rubrosAdicionales[a][13]+"', '"+rubrosAdicionales[a][11]+"', '"+rubrosAdicionales[a][15]+
                                            "', '"+pU+"', '"+rubrosAdicionales[a][18]+"', '"+rubrosAdicionales[a][16]+"', '"+rubrosAdicionales[a][17]+"', '1', '"+rubrosAdicionales[a][27]+"'],";
                                            
                                            double pVenta = Double.parseDouble(rubrosAdicionales[a][12]);
                                            ResultSet rsActivo = this.consulta("select D.id_plan_cuenta_grupo, D.id_plan_cuenta, sum(A.valor_compra) as valor_compra, sum(A.valor_depreciado) as valor_depreciado, "
                                                + "sum(valor_compra) - sum(valor_depreciado) as valor_util \n"
                                                + "from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion \n"
                                                + "where A.id_activo in (" + id_producto + ") group by D.id_plan_cuenta_grupo, D.id_plan_cuenta;");
                                            while (rsActivo.next()) {
                                                String valor_compra = rsActivo.getString("valor_compra") != null ? rsActivo.getString("valor_compra") : "0";
                                                String valor_depreciado = rs.getString("valor_depreciado") != null ? rsActivo.getString("valor_depreciado") : "0";
                                                String valor_util = rsActivo.getString("valor_util") != null ? rsActivo.getString("valor_util") : "0";
                                                String id_plan_cuenta_grupo = rsActivo.getString("id_plan_cuenta_grupo") != null ? rsActivo.getString("id_plan_cuenta_grupo") : "";
                                                String id_plan_cuenta_venta = rsActivo.getString("id_plan_cuenta") != null ? rsActivo.getString("id_plan_cuenta") : "";

                                                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_venta, valor_depreciado, "0"});
                                                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_grupo, "0", valor_compra});
                                                double diferencia = pVenta - Double.parseDouble(valor_util);
                                                if (diferencia > 0) {
                                                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{idPlanCuentaUtilidadActivo, "0", String.valueOf(Addons.redondear(diferencia))});
                                                }
                                                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{idPlanCuentaIvaActivos, "0", rubrosAdicionales[a][13]});

                                            }
                                            rsActivo.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        
                                    }else{
                                        
                                        paramArtic += "['"+rubrosAdicionales[a][3]+"', '"+rubrosAdicionales[a][23]+"', '"+pU+"', '"+rubrosAdicionales[a][12]+
                                            "', '0', '"+rubrosAdicionales[a][13]+"', '"+rubrosAdicionales[a][11]+"', '"+rubrosAdicionales[a][15]+
                                            "', '"+pU+"', '"+rubrosAdicionales[a][18]+"', '"+rubrosAdicionales[a][16]+"', '"+rubrosAdicionales[a][17]+"', 'p', '"+rubrosAdicionales[a][27]+"'],";
                                        
                                        id_cuenta_iva = rubrosAdicionales[a][18].compareTo("s")==0 ? rubrosAdicionales[a][21] : rubrosAdicionales[a][22];
                                        
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{rubrosAdicionales[a][19], "0", rubrosAdicionales[a][12]});
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", rubrosAdicionales[a][13]});
                                    }
                                    
                                    idsPrefacturaRubro += rubrosAdicionales[a][27] + ",";
                                    
                                    ids_productos += "," + rubrosAdicionales[a][3];
                                    descripciones += "," + rubrosAdicionales[a][15]; 
                                    cantidades += "," + rubrosAdicionales[a][23];
                                    preciosUnitarios += "," + pU;
                                    descuentos += ",0";
                                    subtotales += "," + rubrosAdicionales[a][12];
                                    ivas += "," + rubrosAdicionales[a][13];
                                    pIvas += "," + rubrosAdicionales[a][16]; 
                                    codigoIvas += "," + rubrosAdicionales[a][17];
                                    totales = rubrosAdicionales[a][11];
//                                }
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

                            if(!this.facturaDuplicada(serie_factura, num_factura )){

                                String xmlFirmado = "";
                                String estadoDocumento = "";
                                String certificado = DOCS_ELECTRONICOS + "certificado.p12";
                                String rutaSalida = DOCS_ELECTRONICOS + "firmados";
                                String claveAcceso = "";
                                String autorizacionXml = "";
                                //String respuestaAutoriz = "";
                                String vecSerie[] = serie_factura.split("-");

                                
                                FacturaElectronica objFE = new FacturaElectronica();    //   se tiene que blanquear un nuevo archivo XML

                                claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
                                
                                objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, emalSaitel, numContSaitel, sitioWeb, 
                                        "01", vecSerie[0], vecSerie[1], 
                                        Cadena.setSecuencial(num_factura), dir_matriz, Fecha.getFecha("SQL"), direccion_sucursal, num_resolucion, oblga_contabilidad, 
                                        tipo_documento, razon_social, ruc, subtotal, descuento, "0", subtotal_2, iva_2, subtotal_3, iva_3, total_pagar, formaPago, 
                                        ids_productos, descripciones, cantidades, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion, plan, emailCliente);
                                String documentoXml = DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
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
                                        autorizacionXml = this.getStringFromFile(DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");

                                        
                                        
                                        String idFactura = this.emitir(id_sucursal, Integer.parseInt(idPuntoEmision), id_prefactura, id_instalacion, usuario, serie_factura, num_factura, "1119999999", ruc,
                                                idFormaPago, formaPagoCodInterno, "Pichincha", "", num_comp_pago, "0", id_plan_cuenta_banco, "", "Emisión de la factura por servicios de Internet Nro. " + serie_factura + "-" + num_factura, 
                                                subtotal, "0", subtotal_2, subtotal_3, descuento, iva_2, iva_3, total_pagar, "array[" + paramArtic + "]", "", "0", 
                                                "", "", "", "NULL", "0", "array[]::varchar[]", "array[" + paramAsiento + "]", xmlFirmado, String.valueOf(dias_conexion), ids_productos, cantidades, 
                                                preciosUnitarios, descuentos, subtotales, ivas, totales, tipoRubros, idsPrefacturaRubro, idCliAnt, monto_vajar, estado_servicio);


                                        if(idFactura.compareTo("-1")!=0){
                                            long numFacturaAux = Long.parseLong(num_factura) + 1;
                                            matPuntosVirtuales[p][4] = String.valueOf(numFacturaAux);
                                            
                                            this.ejecutar("update tbl_prefactura set por_emitir_factura=false where id_prefactura="+id_prefactura);
                                            
                                            this.ejecutar("update tbl_factura_venta set estado_documento='"+estadoDocumento+"', clave_acceso='"+claveAcceso
                                            +"' where id_factura_venta="+idFactura);
                                            
                                            //   registro de documento en la base documental
                                            this.objDocumental.ejecutar("insert into tbl_documentos(documentotexto, numero_documento, id_sucursal, tabla, id_tabla, campo_tabla) values('"+
                                                    autorizacionXml+"', "+id_sucursal+serie_factura+num_factura+", "+id_sucursal+", 'tbl_factura_venta', "+idFactura+", 'documentoxml')");

                                            String transaccion = "EMISION DE LA FACTURA NRO. "+serie_factura+"-"+num_factura+" CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txt_periodo;
                                            this.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
                                                    "values('"+usuario+"','127.0.0.1','"+Fecha.getHora()+"','"+Fecha.getFecha("ISO")+"', '"+transaccion+"');");
                                            
                                        }else{
                                            System.out.println("Error al emitir la factura " + serie_factura + "-" + num_factura + " del cliente con RUC " + ruc + ". " + this.getError() );
                                        }
                                    }else{
                                        System.out.println("Error al firmar la factura en formato xml. " + error);
                                    }
                                }else{
                                    System.out.println("Error al generar la factura en formato xml. " + error);
                                }
                            }else{
                                System.out.println("El número de factura "+serie_factura+"-"+num_factura+" ya ha sido emitido.");
                            }

                    }else{
                        System.out.println("La sucursal de código " + id_sucursal + "No tiene un punto de emisión virtual");
                    }
                }catch(Exception e){
                    String msg = "Error en la emision de la prefactura ID: " + id_prefactura + ". " + e.getMessage() + ". " + this.getError();
                    System.out.println(msg);
                    objCorreo.enviar("sistemas@saitel.ec", "Error en la emision de prefactura" , msg, null);
                    //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
                }
                
            }       //  del wile
            objPromocion.cerrar();
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
        }   
        
        
        
        //  genera documentos xml de facturas provenientes del servicio Web
        this.procesarXmlSriTodos();
    }
    
    
    private String emitir(String id_sucursal, int id_punto_emision, String id_prefactura, String id_instalacion, String usuario, String serie_factura, String num_factura, 
            String autorizacion, String ruc, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos, 
            String id_plan_cuenta_banco, String son, String concepto, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, 
            String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, 
            String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, 
            String paramRet, String paramAsiento, String xmlFirmado, String dias_conexion, String ids_productos, String cantidades, 
            String preciosUnitarios, String descuentos, String subtotales, String ivas, String totales, String tipoRubros, String idsPrefacturaRubro,
            String idCliAnt, String monto_vajar, String estado_servicio)
    {
        String idFact = "-1";
        try{
//            String fecha_prefactura = "";
            String periodo = "";
            int anio = Fecha.getAnio();
            int mes= Fecha.getMes();
            try{
                ResultSet rsPreFact = this.consulta("select fecha_prefactura, periodo from tbl_prefactura where id_prefactura="+id_prefactura);
                if(rsPreFact.next()){
//                    fecha_prefactura = rsPreFact.getString("fecha_prefactura")!=null ? rsPreFact.getString("fecha_prefactura") : "";
                    periodo = rsPreFact.getString("periodo")!=null ? rsPreFact.getString("periodo") : "";
                    anio = Fecha.datePart("anio", periodo);
                    mes = Fecha.datePart("mes", periodo);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
            String id_cliente = "";
            String razon_social = "";
            String id_plan_cuenta_anticipo = "";
            String direccion = "";
            String telefono = "";
            //String direccion_instalacion = "";
            String ip = "";
            String radusername = "";
            try{
                ResultSet rsInstal = this.consulta("select C.id_cliente, razon_social, id_plan_cuenta_anticipo, direccion, telefono, direccion_instalacion, ip::varchar, radusername "
                    + "from tbl_instalacion as I inner join tbl_cliente as C on I.id_cliente=C.id_cliente where I.id_instalacion=" + id_instalacion);
                if(rsInstal.next()){
                    id_cliente = rsInstal.getString("id_cliente")!=null ? rsInstal.getString("id_cliente") : "";
                    razon_social = rsInstal.getString("razon_social")!=null ? rsInstal.getString("razon_social") : "";
                    id_plan_cuenta_anticipo = rsInstal.getString("id_plan_cuenta_anticipo")!=null ? rsInstal.getString("id_plan_cuenta_anticipo") : "232";
                    direccion = rsInstal.getString("direccion")!=null ? rsInstal.getString("direccion") : "";
                    telefono = rsInstal.getString("telefono")!=null ? rsInstal.getString("telefono") : "";
                    //direccion_instalacion = rsInstal.getString("direccion_instalacion")!=null ? rsInstal.getString("direccion_instalacion") : "";
                    ip = rsInstal.getString("ip")!=null ? rsInstal.getString("ip") : "";
                    radusername = rsInstal.getString("radusername")!=null ? rsInstal.getString("radusername") : "";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
            ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
            
        
            String anticipos = "";
            String vecIdCliAnt[] = idCliAnt.split(",");
            String vecMonto_vajar[] = monto_vajar.split(",");
            for (int i = 0; i < vecIdCliAnt.length; i++) {
                anticipos += "['" + vecIdCliAnt[i] + "','" + vecMonto_vajar[i] + "'],";
            }
            anticipos = "array[" + anticipos.substring(0, anticipos.length() - 1) + "]";
            
            
//System.out.println("select facturaVenta("+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+usuario+"', '"+serie_factura+
//                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', now()::date, '"+direccion+
//                    "', '"+telefono+"', '"+id_forma_pago+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
//                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+concepto+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_3+", "+descuento+
//                    ", "+iva_2+", "+iva_3+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
//                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+
//                    "', "+id_plan_cuenta_anticipo+", "+anticipos+");");
            
            ResultSet res = this.consulta("select facturaVenta("+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+usuario+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', now()::date, '"+direccion+
                    "', '"+telefono+"', '"+id_forma_pago+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+concepto+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_3+", "+descuento+
                    ", "+iva_2+", "+iva_3+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+
                    "', "+id_plan_cuenta_anticipo+", "+anticipos+");");  // 42 param          array[['5384','18.33']]
            
            if(res.next()){
                String idFactComp = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                String vecFactComp[] = idFactComp.split(":");
                idFact = vecFactComp[0];
                if(idFact.compareTo("-1")!=0){
                    this.ejecutar("update tbl_factura_venta set id_instalacion="+id_instalacion+", ip='"+ip+"', radusername='"+radusername+"' where id_factura_venta="+idFact+";");
                    this.ejecutar("update tbl_prefactura set id_factura_venta="+idFact+", fecha_emision=now()::date, es_fact_impago=false, dias_conexion="+dias_conexion+" where id_prefactura="+id_prefactura+";");
                    
                    if(estado_servicio.compareTo("c")==0||estado_servicio.compareTo("n")==0){
                        this.ejecutar("UPDATE tbl_instalacion SET estado_servicio = "
                                + "case "
                                + " when "+id_instalacion+" in (select distinct id_instalacion from tbl_instalacion_suspension where eliminado=false and now()::date between fecha_inicio and fecha_termino) "
                                + " then 's' "
                                + " else 'a' "
                                + "end "
                                + "where estado_servicio in ('c', 'n') and id_instalacion="+id_instalacion+";");
                    }
                    
                    if(estado_servicio.compareTo("e")==0){
                        this.ejecutar("UPDATE tbl_instalacion SET estado_servicio='t' where estado_servicio in ('e') and id_instalacion="+id_instalacion);
                    }
                    
                    //   sistema juridico  
                    this.ejecutar("update tbl_gestion_cobranzas set gestion_final='PAGAN Y CONTINUAN' where id_gestion="+id_prefactura+";");
                    
                    
                    if(forma_pago.compareTo("d")==0){
                        this.ejecutar("update tbl_instalacion set estado_servicio='c' where estado_servicio='a' and id_instalacion="+id_instalacion+" and id_instalacion in "
                                + "(select P.id_instalacion from tbl_prefactura as P inner join tbl_factura_venta as F on P.id_factura_venta=F.id_factura_venta "
                                + "where getFechaSuspensionCreditos(fecha_prefactura, "+diaCortesCreditos+") < now()::date and forma_pago='d' and deuda>0) and id_instalacion not in "
                                + "(select distinct id_instalacion from tbl_anticipo_internet where now()::date between fecha_ini and fecha_fin);");
                    }
                    
                    if(idsPrefacturaRubro.compareTo("")!=0){
                        this.ejecutar("update tbl_prefactura_rubro set estadocobro=true where id_prefactura_rubro in (" + idsPrefacturaRubro.substring(0, idsPrefacturaRubro.length()-1 ) + ")");
                    }
                    
                    List sql = new ArrayList();
                    sql.add("delete from tbl_instalacion_rubro where id_instalacion="+id_instalacion+" and periodo_cobro between '"+anio + "-" + mes + "-01' and '"+anio + "-" + mes + "-"+ Fecha.getUltimoDiaMes(anio, mes) +"';");
                    String vec_ids_productos[] = ids_productos.split(",");
                    String vec_cantidades[] = cantidades.split(",");
                    String vec_p_u[] = preciosUnitarios.split(",");
                    String vec_descuentos[] = descuentos.split(",");
                    String vec_subtotales[] = subtotales.split(",");
                    String vec_ivas[] = ivas.split(",");
                    String vec_totales[] = totales.split(",");
//                    String vec_tipoRubros[] = tipoRubros.split(",");
//                    String vec_idsPrefacturaRubro[] = idsPrefacturaRubro.split(",");
                    for(int i=1; i<vec_ids_productos.length; i++){
                        sql.add("insert into tbl_instalacion_rubro(id_prefactura, id_instalacion, periodo_cobro, id_producto, cantidad, p_u, p_st, descuento, iva, total) "
                                + "values("+id_prefactura+", "+id_instalacion+", '"+periodo+"', "+vec_ids_productos[i]+", "+vec_cantidades[i]+", "+vec_p_u[i]+", "+
                                vec_subtotales[i]+", "+vec_descuentos[i]+", "+vec_ivas[i]+", "+vec_totales[i]+");");
//                        if(vec_tipoRubros[i].compareTo("p")==0 || vec_tipoRubros[i].compareTo("1")==0){
//                            this.ejecutar("update tbl_prefactura_rubro set estadocobro=true where id_prefactura_rubro="+vec_idsPrefacturaRubro[i]);
//                        }
                    }
                    this.transacciones(sql);
                    
                    }
                
                
                
                res.close();
            } else{
                String msg = "Error al ejecutar emision de la factura " + serie_factura + "-" + num_factura + " del cliente con RUC " + ruc + ". " + this.getError();
                System.out.println( msg );
            }
        }catch(Exception e){
            String msg = "Error en el proceso de emitir la factura " + serie_factura + "-" + num_factura + " del cliente con RUC " + ruc + ". " + this.getError() + e.getMessage();
            System.out.println( msg );
            objCorreo.enviar("sistemas@saitel.ec", "Error en la emision de prefactura" , msg, null);
        }
        return idFact;
    }

    
    public boolean facturaDuplicada(String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_factura_venta where serie_factura='"+serie+"' and num_factura="+numero);
        if(this.getFilas(res)>0){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
    
    public String[][] getPuntosEmisionVirtuales()
    {
        ResultSet rs = this.consulta("SELECT P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, " + 
            "case when max(num_factura)>0 then max(num_factura)+1 else 1 end, direccion_establecimiento " +
            "from tbl_punto_emision as P left join tbl_factura_venta as F on F.serie_factura=P.fac_num_serie " +
            "where caja_virtual=true " +
            "group by P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, direccion_establecimiento " +
            "order by id_sucursal");
        return Matriz.ResultSetAMatriz(rs);
    }
    
    
    public String generarFacturaXml(String idFacturaVenta)
    {
        
        String DOCS_ELECTRONICOS = Parametro.getRutaArchivos();
        
        
        String ambiente = "2";      // 1=pruebas    2=produccion
        String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
        String ruc_empresa = "1091728857001";
        String razon_social_empresa = "SOLUCIONES AVANZADAS INFORMATICAS, TELECOMUNICACIONES Y ELECTRICAS SAITEL";
        String nombre_comercial = "";
        String emalSaitel = "";
        String numContSaitel = "";
        String sitioWeb = "";
        String num_resolucion = "";
        String oblga_contabilidad = "SI";
        String dir_matriz = "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
        String direccion_sucursal = "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_configuracion order by parametro;");
            while(r.next()){
                String parametro = r.getString("parametro")!=null ? r.getString("parametro") : "";
                if(parametro.compareTo("ambiente")==0){
                    ambiente = r.getString("valor")!=null ? r.getString("valor") : "2";
                }
                if(parametro.compareTo("ruc")==0){
                    ruc_empresa = r.getString("valor")!=null ? r.getString("valor") : "1091728857001";
                }
                if(parametro.compareTo("razon_social")==0){
                    razon_social_empresa = r.getString("valor")!=null ? r.getString("valor") : "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
                }
                if(parametro.compareTo("nombre_comercial")==0){
                    nombre_comercial = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("dir_matriz")==0){
                    dir_matriz = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("email_info")==0){
                    emalSaitel = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("numeros_soporte")==0){
                    numContSaitel = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("pagina_web")==0){
                    sitioWeb = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("num_resolucion")==0){
                    num_resolucion = r.getString("valor")!=null ? r.getString("valor") : "";
                }
                if(parametro.compareTo("oblga_contabilidad")==0){
                    oblga_contabilidad = r.getString("valor")!=null ? r.getString("valor") : "SI";
                }
                
            }
            r.close();
        }catch(Exception e){
            e.printStackTrace();
        }
            


        try{    
            
            ResultSet rs = this.consulta("select * from vta_factura_venta where id_factura_venta = " + idFacturaVenta);
            if(rs.next()){
                String claveAcceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "0";
                String id_sucursal = rs.getString("txt_sucursal")!=null ? rs.getString("id_sucursal") : "0";
                String serie_factura = rs.getString("serie_factura")!=null ? rs.getString("serie_factura") : "000-000";
                String num_factura = rs.getString("num_factura")!=null ? rs.getString("num_factura") : "0";
                String tipo_documento_cliente = rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "05";
                String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                String direccion = rs.getString("direccion")!=null ? rs.getString("direccion") : "";
                String email = rs.getString("email")!=null ? rs.getString("email") : "";
                String ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : "";
                String id_forma_pago = rs.getString("id_forma_pago")!=null ? rs.getString("id_forma_pago") : "1";
                String subtotal = rs.getString("subtotal")!=null ? rs.getString("subtotal") : "0";
                String subtotal_2 = rs.getString("subtotal_2")!=null ? rs.getString("subtotal_2") : "0";
                String subtotal_3 = rs.getString("subtotal_3")!=null ? rs.getString("subtotal_3") : "0";
                String descuento = rs.getString("descuento")!=null ? rs.getString("descuento") : "0";
                String iva_2 = rs.getString("iva_2")!=null ? rs.getString("iva_2") : "0";
                String iva_3 = rs.getString("iva_3")!=null ? rs.getString("iva_3") : "0";
                String total = rs.getString("total")!=null ? rs.getString("total") : "0";

                String ubicacionNombreComercial[] = this.getDireccionDePuntoEmision(String.valueOf(id_sucursal));
                direccion_sucursal = ubicacionNombreComercial[0].compareTo("")!=0 ? ubicacionNombreComercial[0] : direccion_sucursal;
                nombre_comercial = ubicacionNombreComercial[1].compareTo("")!=0 ? ubicacionNombreComercial[1] : nombre_comercial;
                String email_info = ubicacionNombreComercial[2].compareTo("")!=0 ? ubicacionNombreComercial[2] : emalSaitel;
                String num_contacto = ubicacionNombreComercial[3].compareTo("")!=0 ? ubicacionNombreComercial[3] : numContSaitel;
                String sitio_web = ubicacionNombreComercial[4].compareTo("")!=0 ? ubicacionNombreComercial[4] : sitioWeb;
        
                String codigoFormaPago = "01";
                try{
                    ResultSet rs1 = this.consulta("select codigo from tbl_forma_pago where id_forma_pago=" + id_forma_pago);
                    if(rs1.next()){
                        codigoFormaPago = rs1.getString("codigo")!=null ? rs1.getString("codigo") : "01";
                        rs1.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                String plan = "";
                try{
                    ResultSet rs2 = this.consulta("select plan from vta_prefactura where id_factura_venta=" + idFacturaVenta);
                    if(rs2.next()){
                        plan = rs2.getString("plan")!=null ? rs2.getString("plan") : "";
                        rs2.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
//                ids_factura_venta += id_factura_venta + ",";
                
                String ids_productos = "";
                String descripciones = "";
                String cantidades_prod = "";
                String preciosUnitarios = "";
                String descuentos = "";
                String subtotales = "";
                String ivas = "";
                String pIvas = "";
                String codigoIvas = "";
                try{
                    ResultSet rs2 = this.consulta("select * from vta_factura_venta_detalle where id_factura_venta = " + idFacturaVenta);
                    while(rs2.next()){
                        ids_productos += rs2.getString("id_producto")!=null ? rs2.getString("id_producto")+"," : "";
                        descripciones += rs2.getString("descripcion_mas")!=null ? rs2.getString("descripcion_mas")+"," : "";
                        cantidades_prod += rs2.getString("cantidad")!=null ? rs2.getString("cantidad")+"," : "";
                        preciosUnitarios += rs2.getString("p_u")!=null ? rs2.getString("p_u")+"," : "";
                        descuentos += rs2.getString("descuento")!=null ? rs2.getString("descuento")+"," : "";
                        subtotales += rs2.getString("p_st")!=null ? rs2.getString("p_st")+"," : "";
                        ivas += rs2.getString("iva")!=null ? rs2.getString("iva")+"," : "";
                        pIvas += rs2.getString("p_iva")!=null ? rs2.getString("p_iva")+"," : "";
                        codigoIvas += rs2.getString("codigo_iva")!=null ? rs2.getString("codigo_iva")+"," : "";
                    }
                    if(ids_productos.compareTo("")!=0){
                        ids_productos = ids_productos.substring(0, ids_productos.length()-1);
                        descripciones = descripciones.substring(0, descripciones.length()-1);
                        cantidades_prod = cantidades_prod.substring(0, cantidades_prod.length()-1);
                        preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length()-1);
                        descuentos = descuentos.substring(0, descuentos.length()-1);
                        subtotales = subtotales.substring(0, subtotales.length()-1);
                        ivas = ivas.substring(0, ivas.length()-1);
                        pIvas = pIvas.substring(0, pIvas.length()-1);
                        codigoIvas = codigoIvas.substring(0, codigoIvas.length()-1);
                        
                        rs2.close();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
                
                
                String vecSerie[] = serie_factura.split("-");


                FacturaElectronica objFE = new FacturaElectronica();    //   se tiene que blanquear un nuevo archivo XML

                claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);

                String facturaXml = objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, emalSaitel, numContSaitel, sitioWeb, 
                        "01", vecSerie[0], vecSerie[1], 
                        Cadena.setSecuencial(num_factura), dir_matriz, Fecha.getFecha("SQL"), direccion_sucursal, num_resolucion, oblga_contabilidad, 
                        tipo_documento_cliente, razon_social, ruc, subtotal, descuento, "0", subtotal_2, iva_2, subtotal_3, iva_3, total, codigoFormaPago, 
                        ids_productos, descripciones, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion, plan, email);
                String documentoXml = DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                objFE.salvar(documentoXml);
                String error = objFE.getError();

                if(error.compareTo("")==0){
                    return facturaXml;
                }else{
                    System.out.println("Error al generar la factura en formato xml. " + error);
                }
                            
                    
            }       //  del wile
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
        }   
        
        return "";
        
    }
    
    public boolean procesarXmlSriTodos()
    {
        String DOCS_ELECTRONICOS = Parametro.getRutaArchivos();
        String claveCertificado = "";
        DataBase objTransaccional = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de generacion de documentos XML de facturas en estado 'P'");
        try{
            ResultSet r = objTransaccional.consulta("SELECT * FROM tbl_configuracion where parametro='clave_certificado';");
            if(r.next()){
                claveCertificado = r.getString("valor")!=null ? r.getString("valor") : "121";
                ResultSet rs = objTransaccional.consulta("select id_factura_venta, clave_acceso from tbl_factura_venta where lower(estado_documento) = 'p' and not anulado order by fecha_emision");
                while(rs.next()){
                    String id_factura_venta = rs.getString("id_factura_venta")!=null ? rs.getString("id_factura_venta") : "";
                    String claveAcceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
//                    String cadenaXml = rs.getString("documento_xml")!=null ? rs.getString("documento_xml") : "";
                    String cadenaXml = "";
                    
                    ResultSet rs1 = this.objDocumental.consulta("select documentotexto from tbl_documentos where tabla='tbl_factura_venta' and id_tabla='"+id_factura_venta+"';");
                    if( this.objDocumental.getFilas(rs1) > 0 ){
                        if(rs1.next()){
                            cadenaXml = rs1.getString("documentotexto")!=null ? rs1.getString("documentotexto") : "";
                            rs1.close();
                        }
                    }else{
                        cadenaXml = this.generarFacturaXml(id_factura_venta);
//                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": no se encontro documento en documento de factura ID " + id_factura_venta + " en DB documental");
                    }
                    
                    if( cadenaXml.compareTo("") != 0 ) {
                        if( !this.firmarXml(id_factura_venta, DOCS_ELECTRONICOS, claveAcceso, cadenaXml, claveCertificado) ){
                            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": error al tratar de firmar el documento " + claveAcceso + ".xml");
                        }
                    } else {
                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": no se encontro documento en documento de factura ID " + id_factura_venta + " en DB documental");
                    }
                }
                r.close();
                return true;
            }
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generacion de documentos XML de facturas en estado 'P'");
            objTransaccional.cerrar();
        }
        return false;
    }
    
    private boolean guardarXml(String rutaArchivo, String cadenaXml)
    {
        try {
            FileWriter fw = new FileWriter(rutaArchivo);
            PrintWriter writer = new PrintWriter(fw);
            writer.print(cadenaXml);
            writer.close();
            return true;
	} catch (IOException e) {
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": error al guardar documento " + rutaArchivo);
	}
        return false;
    }
    
    private boolean firmarXml(String idFactura, String DOCS_ELECTRONICOS, String claveAcceso, String cadenaXml, String claveCertificado)
    {
        String certificado = DOCS_ELECTRONICOS + "certificado.p12";
        String rutaSalida = DOCS_ELECTRONICOS + "firmados/";
        String rutaXml = DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
        if(this.guardarXml(rutaXml, cadenaXml)){
            String estadoDocumento = "g";
            String archivoSalida = claveAcceso + ".xml";
            FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, claveCertificado, rutaXml, rutaSalida, archivoSalida);
            firmaDigital.execute();

            if(firmaDigital.getError().compareTo("")==0){
                try{
                    estadoDocumento = "f";
                    String autorizacionXml = this.getStringFromFile(DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                    if( this.objDocumental.ejecutar("update tbl_documentos set documentotexto='"+autorizacionXml+"' where tabla='tbl_factura_venta' and id_tabla="+idFactura) ){
                        return this.ejecutar("update tbl_factura_venta set estado_documento='"+estadoDocumento+"' where id_factura_venta="+idFactura);
                    }
                }catch(Exception e){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": error al tratar de firmar el documento");
                }
            }
        }
        return false;
    }
    
    /* miselanea de funsiones */
    private String getStringFromFile(String archivo) throws IOException 
    {
        File file = new File(archivo);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        //String cadXml = "";
        String linea;
        while((linea=br.readLine())!=null){
            cadXml.append(linea);
            //cadXml += linea;
        }
        return cadXml.toString();
    }
    
    
    public String[] getDireccionDePuntoEmision(String id) {
        String ubicacion[] = new String[]{"","","","",""};
        if (id.compareTo("-0") != 0) {
            try {
                ResultSet r = this.consulta("SELECT S.ubicacion, S.nombre_comercial, P.direccion_establecimiento, S.mail_info, S.num_contacto, S.sitio_web FROM tbl_sucursal as S inner join tbl_punto_emision as P on S.id_sucursal=P.id_sucursal "
                        + "where P.id_punto_emision=" + id);
                if (r.next()) {
                    ubicacion[0] = (r.getString("ubicacion") != null) ? r.getString("ubicacion") : "";
                    ubicacion[1] = (r.getString("nombre_comercial") != null) ? r.getString("nombre_comercial") : "";
                    ubicacion[2] = (r.getString("mail_info") != null) ? r.getString("mail_info") : "";
                    ubicacion[3] = (r.getString("num_contacto") != null) ? r.getString("num_contacto") : "";
                    ubicacion[4] = (r.getString("sitio_web") != null) ? r.getString("sitio_web") : "";
                    String direccion_establecimiento = (r.getString("direccion_establecimiento") != null) ? r.getString("direccion_establecimiento") : "DIRECCION DE LA SUCURSAL";

                    if (direccion_establecimiento.compareTo("DIRECCION DE LA SUCURSAL") != 0) {
                        ubicacion[0] = direccion_establecimiento;
                    }
                    r.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ubicacion;
    }
    
    public void cerrarConexiones()
    {
        //this.objDocumental.cerrar();
        try{
            super.cerrar();
            this.objCorreo.cerrar();
        }catch (Exception ec) {
            System.out.println(ec.getMessage());
        }
    }
    
}

