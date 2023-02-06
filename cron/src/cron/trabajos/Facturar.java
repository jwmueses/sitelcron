/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
import java.sql.ResultSet;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge
 */
public class Facturar implements Job{
    
    DataBase objDataBase = null;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        String doc_ip = Parametro.getDocumentalIp();      //  127.0.0.1     pruebas = 192.168.217.16
        int doc_puerto = Parametro.getDocumentalPuerto();
        String doc_db = Parametro.getDocumentalBaseDatos();
        String doc_usuario = Parametro.getDocumentalUsuario();
        String doc_clave = Parametro.getDocumentalClave();
        DataBase objDocumental = new DataBase( doc_ip, doc_puerto, doc_db, doc_usuario, doc_clave );
        
        this.objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        
        
        
        // generacion de prefacturas faltantes
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de generación de prefacturas faltantes del período actual");
        this.objDataBase.consulta("select proc_generarPrefacturasFaltantes();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generación de prefacturas faltantes del período actual");

            
            
            
            
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de emisión de facturas del cash");
        
//        String DOCS_ELECTRONICOS = Parametro.getRutaArchivos();
        try{
//            String id_plan_cuenta_banco="10";
//            ResultSet rsBanco = this.objDataBase.consulta("SELECT * FROM vta_banco where lower(banco) like '%pichincha%'");
//            if(rsBanco.next()){
//                id_plan_cuenta_banco = rsBanco.getString("id_plan_cuenta")!=null ? rsBanco.getString("id_plan_cuenta") : "10";
//                rsBanco.close();
//            }
//            
//            String desc_venta = "121";
//            String ambiente = "2";
//            String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
//            String clave_certificado = "";
//            String ruc_empresa = "1091728857001";
//            String razon_social_empresa = "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
//            String nombre_comercial = "SAITEL";
//            String num_resolucion = "";
//            String oblga_contabilidad = "SI";
//            String dir_matriz = "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
//            try{
//                ResultSet r = this.objDataBase.consulta("SELECT * FROM tbl_configuracion order by parametro;");
//                while(r.next()){
//                    String parametro = r.getString("parametro")!=null ? r.getString("parametro") : "";
//                    if(parametro.compareTo("desc_venta")==0){
//                        desc_venta = r.getString("valor")!=null ? r.getString("valor") : "121";
//                    }
//                    if(parametro.compareTo("ambiente")==0){
//                        ambiente = r.getString("valor")!=null ? r.getString("valor") : "2";
//                    }
//                    if(parametro.compareTo("tipoEmision")==0){
//                        tipoEmision = r.getString("valor")!=null ? r.getString("valor") : "1";
//                    }
//                    if(parametro.compareTo("clave_certificado")==0){
//                        clave_certificado = r.getString("valor")!=null ? r.getString("valor") : "";
//                    }
//                    if(parametro.compareTo("ruc")==0){
//                        ruc_empresa = r.getString("valor")!=null ? r.getString("valor") : "1091728857001";
//                    }
//                    if(parametro.compareTo("razon_social")==0){
//                        razon_social_empresa = r.getString("valor")!=null ? r.getString("valor") : "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
//                    }
//                    if(parametro.compareTo("nombre_comercial")==0){
//                        nombre_comercial = r.getString("valor")!=null ? r.getString("valor") : "SAITEL";
//                    }
//                    if(parametro.compareTo("num_resolucion")==0){
//                        num_resolucion = r.getString("valor")!=null ? r.getString("valor") : "";
//                    }
//                    if(parametro.compareTo("oblga_contabilidad")==0){
//                        oblga_contabilidad = r.getString("valor")!=null ? r.getString("valor") : "SI";
//                    }
//                    if(parametro.compareTo("dir_matriz")==0){
//                        dir_matriz = r.getString("valor")!=null ? r.getString("valor") : "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
//                    }
//                    
//                }
//                r.close();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            
//            
//            String matPuntosVirtuales[][] = this.getPuntosEmisionVirtuales();
//            
//            
//            try{
//                ResultSet rsRecalcular = this.objDataBase.consulta("select id_prefactura from tbl_prefactura where por_emitir_factura=true and (recalcular=true or total_internet=0);");
//                while(rsRecalcular.next()){
//                    String idPrefactura = rsRecalcular.getString("id_prefactura")!=null ? rsRecalcular.getString("id_prefactura") : "-1";
//                    this.objDataBase.consulta("select proc_calcularPreFactura("+idPrefactura+", false);");
//                }
//                rsRecalcular.close();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            
//            
//            ResultSet rsDetalleFactura = this.objDataBase.consulta("select SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
//                "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"+
//                "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien  "+
//                "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
//                "inner join tbl_iva as I on I.id_iva=SP.id_iva) "+
//                "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "+
//                "group by SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, case when tiene_iva then '~' else '' end, "+
//                "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien order by id_sucursal, id_producto");
//            String matDetalleFactura[][] = Matriz.ResultSetAMatriz(rsDetalleFactura);
            
            int anio = Fecha.getAnio();
            int mes= Fecha.getMes();
            String periodoHasta = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
// rubros para el el 1 de cada mes por convenios de debitos
//String unionConvenios = " or (forma_pago in('TAR', 'CTA') and num_cuenta<>'' and num_cuenta is not null)";
//        
//            ResultSet rsRubrosAdicionales = this.objDataBase.consulta(  
////  rubros adicionales
//"SELECT distinct id_rubro, " +
//"usuario, " +
//"fecha_creacion, " +
//"PR.id_producto, " +
//"rubro, " +
//"temporal, " +
//"fecha_inicio, " +
//"fecha_fin, " +
//"PR.id_sucursal, " +
//"PR.id_instalacion, " +
//"PR.periodo, " +
//"monto, " +
//"subtotal_12, " +
//"iva_12, " +
//"codigo, " +
//"descripcion, " +
//"porcentaje, " +
//"codigo_iva, " +
//"tipo, " +
//"id_plan_cuenta_venta, " +
//"id_iva, " +
//"id_plan_cuenta_venta_servicio, " +
//"id_plan_cuenta_venta_bien, " +
//"canproductos, " +
//"rubro_prefactura, " +
//"tiporubro, " +
//"estadocobro, " +
//"id_prefactura_rubro " +
//" vta_prefactura_rubro as PR inner join tbl_prefactura as P on P.id_instalacion=PR.id_instalacion and PR.periodo=P.periodo " +
//" WHERE estadocobro=false and por_emitir_factura=true " + 
//(Fecha.getDia()==1 ? unionConvenios : "") +
////  productos de suministros
//"union " +  
//"SELECT id_rubro, "
//+ "'administrador' as usuario, "
//+ "now()::date as fecha_creacion, "
//+ "idproductos::int8 as id_producto, "
//+ "rubro, "
//+ "true as temporal, "
//+ "now()::date as fecha_inicio, "
//+ "now()::date as fecha_fin, "
//+ "SP.id_sucursal, "
//+ "id_instalacion, "
//+ "periodo, "
//+ "(select PR.monto + (PR.monto * valor::numeric / 100 )::numeric(13,2) from tbl_configuracion where parametro='p_iva1') as monto, "
//+ "monto as subtotal_12, "
//+ "(select (PR.monto * valor::numeric / 100 )::numeric(13,2) from tbl_configuracion where parametro='p_iva1') as iva_12, "
//+ "P.codigo, "
//+ "descripcion, "
//+ "porcentaje, "
//+ "(select I.codigo as codigo_iva from tbl_iva as I inner join tbl_configuracion as C on C.valor::int=I.porcentaje where parametro='p_iva1') as codigo_iva, "
//+ "tipo, "
//+ "id_plan_cuenta_venta, "
//+ "I.id_iva, "
//+ "id_plan_cuenta_venta_servicio, "
//+ "id_plan_cuenta_venta_bien, "
//+ "canproductos, "
//+ "rubro as rubro_prefactura, "
//+ "PR.tiporubro, "
//+ "PR.estadocobro, "
//+ "PR.id_prefactura_rubro " +
//"FROM (((tbl_prefactura_rubro as PR inner join vta_producto_n as P on PR.idproductos::int=P.id_producto)  " +
//"inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto)  " +
//"inner join tbl_iva as I on I.id_iva=SP.id_iva)  " +
//"WHERE id_rubro is null and estadocobro=false and tiporubro='p' and periodo<='"+periodoHasta+"' " +
//// activos
//"union  " + 
//"SELECT id_rubro, "
//+ "'administrador' as usuario, "
//+ "now()::date as fecha_creacion, "
//+ "idproductos::int8 as id_producto, "
//+ "rubro, "
//+ "true as temporal, " +
//"now()::date as fecha_inicio, "
//+ "now()::date as fecha_fin, "
//+ "PR.id_sucursal, "
//+ "id_instalacion, "
//+ "periodo,  " +
//"(select PR.monto + (PR.monto * valor::numeric / 100 )::numeric(13,2) from tbl_configuracion where parametro='p_iva1') as monto,  " +
//"monto as subtotal_12,  " +
//"(select (PR.monto * valor::numeric / 100 )::numeric(13,2) from tbl_configuracion where parametro='p_iva1') as iva_12,  " +
//"P.codigo_activo as codigo, "
//+ "P.descripcion, "
//+ "porcentaje, " +
//"(select I.codigo from tbl_iva as I inner join tbl_configuracion as C on C.valor::int=I.porcentaje where parametro='p_iva1') as codigo_iva,  " +
//"'a' as tipo,  " + 
//"(select D.id_plan_cuenta_grupo from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion where A.id_activo=P.id_activo) as id_plan_cuenta_venta,  " +
//"(select I.id_iva from tbl_iva as I inner join tbl_configuracion as C on C.valor::int=I.porcentaje where parametro='p_iva1') as id_iva,  " +
//"(select D.id_plan_cuenta_grupo from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion where A.id_activo=P.id_activo) as id_plan_cuenta_venta_servicio,  " +
//"(select D.id_plan_cuenta_grupo from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion where A.id_activo=P.id_activo) as id_plan_cuenta_venta_bien,  " +
//"canproductos, "
//+ "rubro as rubro_prefactura,  " +
//"PR.tiporubro, "
//+ "PR.estadocobro, "
//+ "PR.id_prefactura_rubro " +
//"FROM (tbl_prefactura_rubro as PR inner join vta_activo_n as P on PR.idproductos::int=P.id_activo)  " +
//"WHERE id_rubro is null and estadocobro=false and tiporubro='1' and periodo<='"+periodoHasta+"'");
//            String rubrosAdicionales[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales);

            String periodoActual = Fecha.getAnio() + "-" + Fecha.getMes() + "-01";

            String clientesConvenioTarjeta = " union " // convenio de tarjeta de credito prepago mes actual y postpago mes anterior
                    + "select F.*, total as total_comision from vta_prefactura_todas as F inner join tbl_instalacion as I  on F.id_instalacion=I.id_instalacion \n"
                    + "where num_cuenta<>'' and fecha_emision is null and tarjeta_credito_caduca - '7 day'::interval > now()::date and num_cuenta is not null and I.estado_servicio in ('a', 'c', 'r') and I.fecha_instalacion is not null and forma_pago='TAR' \n"
                    + "and set_convenio_tarjeta=true \n"
                    + "and ((I.convenio_pago ='1' and periodo=('"+periodoActual+"'::date - '1 month'::interval)::date ) or (I.convenio_pago ='0' and periodo='"+periodoActual+"')) \n"
                    + "union \n"    // convenio de cuenta de banco  prepago mes actual y postpago mes anterior
                    + "select F.*, total as total_comision from vta_prefactura_todas as F inner join tbl_instalacion as I  on F.id_instalacion=I.id_instalacion \n"
                    + "where num_cuenta<>'' and fecha_emision is null and num_cuenta is not null and I.estado_servicio in ('a', 'c', 'r') and I.fecha_instalacion is not null and forma_pago='CTA' \n"
                    + "and set_convenio_cuenta=true \n"
                    + "and ((I.convenio_pago ='1' and periodo=('"+periodoActual+"'::date - '1 month'::interval)::date ) or (I.convenio_pago ='0' and periodo='"+periodoActual+"'))";
            
            ResultSet rs = this.objDataBase.consulta("select *, total+comision_cash as total_comision from vta_prefactura_todas where por_emitir_destino='Pichincha' and por_emitir_factura=true"
                    + (Fecha.getDia()==1 ? clientesConvenioTarjeta : "") );
            
            //  instalaciones que tienen convenios de debito
            ResultSet rsConveniosTarjetas = null;
            if(Fecha.getDia()==1){
                rsConveniosTarjetas = this.objDataBase.consulta("select id_instalacion \n" +
                    "from vta_instalacion \n" +
                    "where num_cuenta<>'' and num_cuenta is not null and estado_servicio in ('a', 'c', 'r', 's') and fecha_instalacion is not null and ((forma_pago ='TAR' and set_convenio_tarjeta=true) or (forma_pago ='CTA')) \n" +
                    " and set_convenio_tarjeta=true \n"
                    + "union \n"
                    + "select F.id_instalacion from vta_prefactura_todas as F inner join tbl_instalacion as I  on F.id_instalacion=I.id_instalacion \n"
                    + "where num_cuenta<>'' and fecha_emision is null and num_cuenta is not null and I.estado_servicio in ('a', 'c', 'r', 's') and I.fecha_instalacion is not null and forma_pago in ('TAR', 'CTA')" +
                    "order by id_instalacion");
            }
            String matConveniosTarjetas[][] = Matriz.ResultSetAMatriz(rsConveniosTarjetas);
            
            
            FacturaVenta objFacturaVenta = new FacturaVenta( objDocumental, Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
            objFacturaVenta.consulta("select generar_comprobantes_roles()");
            objFacturaVenta.emitir(rs, null, matConveniosTarjetas, 100);  //   .., .., anticipos, % min generar deuda  emisión con forma de pago Cash Management
            objFacturaVenta.consulta("select proc_verificaNumFacturasRepetidas()");
            objFacturaVenta.cerrarConexiones();
            
//            Promocion objPromocion = new Promocion();
//            
//            while(rs.next()){
//                String id_prefactura = rs.getString("id_prefactura")!=null ? rs.getString("id_prefactura") : "-1";
//                try{
//                    String id_instalacion = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "-1";
//                    String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "-1";
//                    String id_cliente = rs.getString("id_cliente")!=null ? rs.getString("id_cliente") : "-1"; 
//                    String tipo_documento = rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "05"; 
//                    String ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : ""; 
//                    String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : ""; 
//                    int edad = (rs.getString("edad") != null) ? rs.getInt("edad") : 0;
//                    String carne_conadis = (rs.getString("carne_conadis") != null) ? rs.getString("carne_conadis") : "";
//                    String direccion = rs.getString("direccion")!=null ? rs.getString("direccion") : ""; 
//                    String estado_servicio = rs.getString("estado_servicio")!=null ? rs.getString("estado_servicio") : "c"; 
//                    String id_plan_actual = rs.getString("id_plan_actual")!=null ? rs.getString("id_plan_actual") : "-1";
//                    String num_cuenta = rs.getString("num_cuenta")!=null ? rs.getString("num_cuenta") : "";
//                    String plan = rs.getString("plan")!=null ? rs.getString("plan") : "";
//                    String id_producto = rs.getString("id_producto")!=null ? rs.getString("id_producto") : "-1";
//                    int dias_conexion = rs.getString("dias_conexion")!=null ? rs.getInt("dias_conexion") : 30;
//                    String valor_internet = rs.getString("valor_internet")!=null ? rs.getString("valor_internet") : "0";
//                    String iva_internet = rs.getString("iva_internet")!=null ? rs.getString("iva_internet") : "0";
//                    double subt_internet = rs.getString("total_internet")!=null ? rs.getDouble("total_internet") : 0;
//                    String subtotal = rs.getString("subtotal")!=null ? rs.getString("subtotal") : "0";
//                    String subtotal_2 = rs.getString("subtotal_2")!=null ? rs.getString("subtotal_2") : "0";
//                    String subtotal_3 = rs.getString("subtotal_3")!=null ? rs.getString("subtotal_3") : "0";
//                    String iva_2 = rs.getString("iva_2")!=null ? rs.getString("iva_2") : "0";
//                    String iva_3 = rs.getString("iva_3")!=null ? rs.getString("iva_3") : "0";
//                    String descuento = rs.getString("descuento")!=null ? rs.getString("descuento") : "0";
//                    String total_pagar = rs.getString("total")!=null ? rs.getString("total") : "0";
//                    String periodo = rs.getString("periodo")!=null ? rs.getString("periodo") : "";
//                    String txt_periodo = rs.getString("txt_periodo")!=null ? rs.getString("txt_periodo") : "";
//                    String fecha_pago = rs.getString("fecha_pago")!=null ? rs.getString("fecha_pago") : "";
//                    String hora_pago = rs.getString("hora_pago")!=null ? rs.getString("hora_pago") : "";
//                    
//                    String convenio_pago = (rs.getString("convenio_pago") != null) ? rs.getString("convenio_pago") : "";
//                    String forma_pago = (rs.getString("forma_pago") != null) ? rs.getString("forma_pago") : "";
//                    String tipo_cuenta = (rs.getString("tipo_cuenta") != null) ? rs.getString("tipo_cuenta") : "";
//                    String tarjeta_credito_caduca = (rs.getString("tarjeta_credito_caduca") != null) ? rs.getString("tarjeta_credito_caduca") : Fecha.getFecha("ISO");
//                    //double totalCash = rs.getString("total_cash")!=null ? rs.getDouble("total_cash") : 0;
//      
//                            
//                    String idFormaPago="97";
//                    String formaPago="20";
//
//                    
//                    int p = Matriz.enMatriz(matPuntosVirtuales, id_sucursal, 0);
//                    if(p!=-1){
//                        
//                        
//                        String idPuntoEmision = matPuntosVirtuales[p][1];
//                        String usuario = matPuntosVirtuales[p][2];
//                        String serie_factura = matPuntosVirtuales[p][3];
//                        String num_factura = matPuntosVirtuales[p][4];
//                        String direccion_sucursal = matPuntosVirtuales[p][5];
//                        String num_comp_pago = fecha_pago.replace("-", "") + hora_pago.replace(":", "") + id_instalacion;
//                        int pos = Matriz.enMatriz(matDetalleFactura, new String[]{id_sucursal,id_producto}, new int[]{0,1});
//                        String p_u = String.valueOf( Addons.redondear(subt_internet / dias_conexion,  4) );
//                        String totalpr = String.valueOf( Addons.redondear( Double.parseDouble(valor_internet) + Double.parseDouble(iva_internet) ) );
//
//                        String ids_productos = id_producto;
//                        String descripciones = "SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+txt_periodo+" ~";
//                        String cantidades = String.valueOf( dias_conexion );
//                        String preciosUnitarios = p_u;
//                        int p_des0 = ((edad>=65 || carne_conadis.compareTo("")!=0) && Float.parseFloat(descuento)>0) ? 50 : 0;
//                        String descuentos = "0";
//                        String subtotales = String.valueOf(subt_internet);
//                        String ivas = iva_internet;
//                        String pIvas = matDetalleFactura[pos][6];
//                        String codigoIvas = matDetalleFactura[pos][11];
//                        String totales = totalpr;
//                        String tipoRubros = "p";
//                        String idsPrefacturaRubro = "-1";
//                        
//                        
//                        
//                        //  promociones
//                        if( objPromocion.aplicarPromocion(id_sucursal, edad, carne_conadis, convenio_pago, forma_pago, tipo_cuenta, num_cuenta, tarjeta_credito_caduca, 
//                                id_plan_actual, subt_internet, Float.parseFloat(descuento), Integer.parseInt(matDetalleFactura[pos][6]) ) ){
//                            descuento = String.valueOf( objPromocion.getDescuento() );
//                            ivas = iva_internet = String.valueOf( objPromocion.getIva() );
//                            totales = totalpr = String.valueOf( objPromocion.getTotal() );
//                        }
//                        
//                        
//                        
//
//                        String matParamAsientoAx[][] = null;
//                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_banco, total_pagar, "0"});
//                        if(Float.parseFloat(descuento)>0){
//                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, descuento, "0"});
//                            descuentos = descuento;
//                        }
//                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{matDetalleFactura[pos][12], "0", String.valueOf(subt_internet)});//   id_planCuenta del servicio
//
//                        String id_cuenta_iva = matDetalleFactura[pos][14];
//                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", iva_internet});
//
//
//                        String paramArtic = "['"+id_producto+"', '"+dias_conexion+"', '"+p_u+"', '"+subt_internet+
//                            "', '"+descuento+"', '"+iva_internet+"', '"+totalpr+"', '"+descripciones+
//                            "', '"+matDetalleFactura[pos][4]+"', '"+matDetalleFactura[pos][9]+"', '"+pIvas+"', '"+codigoIvas+"', '"+tipoRubros+"'],";
//
//                        if(rubrosAdicionales!=null){
//                            for(int a=0; a<rubrosAdicionales.length; a++){
//                                if(rubrosAdicionales[a][8].compareTo(id_sucursal)==0 && rubrosAdicionales[a][9].compareTo(id_instalacion)==0 && rubrosAdicionales[a][10].compareTo(periodo)==0){
//
//                                    paramArtic += "['"+rubrosAdicionales[a][3]+"', '1', '"+rubrosAdicionales[a][12]+"', '"+rubrosAdicionales[a][12]+
//                                    "', '0', '"+rubrosAdicionales[a][13]+"', '"+rubrosAdicionales[a][11]+"', '"+rubrosAdicionales[a][15]+
//                                    "', '"+rubrosAdicionales[a][12]+"', '"+rubrosAdicionales[a][18]+"', '"+rubrosAdicionales[a][16]+"', '"+rubrosAdicionales[a][17]+"', 'p'],";
//
//                                    id_cuenta_iva = rubrosAdicionales[a][18].compareTo("s")==0 ? rubrosAdicionales[a][21] : rubrosAdicionales[a][22];
//                                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{rubrosAdicionales[a][19], "0", rubrosAdicionales[a][12]});
//                                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", rubrosAdicionales[a][13]});
//                                }
//                            }
//                        }
//
//                        String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
//                        String paramAsiento = "";
//                        for(int i=0; i<matParamAsiento.length; i++){
//                            paramAsiento += "['"+matParamAsiento[i][0]+"', '"+matParamAsiento[i][1]+"', '"+matParamAsiento[i][2]+"'],";
//                        }
//                        if(paramAsiento.compareTo("")!=0){
//                            paramAsiento = paramAsiento.substring(0, paramAsiento.length()-1);
//                        }
//
//                        paramArtic = paramArtic.substring(0, paramArtic.length()-1);
//
//                        
//                
//                
//                        /*String prod_sin_stock = this.verificarStock(id_sucursal, ids_productos, cantidades_prod);     //     no se necesita ya quye es solo el servicio
//                        if(prod_sin_stock.compareTo("")==0){*/
//
//                            if(!this.facturaDuplicada(serie_factura, num_factura )){
//
//                                String xmlFirmado = "";
//                                String estadoDocumento = "";
//                                String certificado = DOCS_ELECTRONICOS + "certificado.p12";
//                                String rutaSalida = DOCS_ELECTRONICOS + "firmados";
//                                String claveAcceso = "";
//                                String autorizacionXml = "";
//                                //String respuestaAutoriz = "";
//                                String vecSerie[] = serie_factura.split("-");
//
//                                
//                                FacturaElectronica objFE = new FacturaElectronica();    //   se tiene que blanquear un nuevo archivo XML
//
//                                claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
//                                
//                                objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
//                                        Cadena.setSecuencial(num_factura), dir_matriz, Fecha.getFecha("SQL"), direccion_sucursal, num_resolucion, oblga_contabilidad, 
//                                        tipo_documento, razon_social, ruc, subtotal, descuento, "0", subtotal_2, iva_2, subtotal_3, iva_3, total_pagar, formaPago, 
//                                        ids_productos, descripciones, cantidades, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion);
//                                String documentoXml = DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
//                                objFE.salvar(documentoXml);
//                                String error = objFE.getError();
//
//                                if(error.compareTo("")==0){
//                                    estadoDocumento = "g";
//                                    String archivoSalida = claveAcceso + ".xml";
//                                    FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
//                                    firmaDigital.execute();
//                                    error = firmaDigital.getError();
//                                    
//                    
//
//                                    if(error.compareTo("")==0){
//                                        estadoDocumento = "f";
//                                        autorizacionXml = this.getStringFromFile(DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
//
//                                        
//                                        
//                                        String idFactura = this.emitir(id_sucursal, Integer.parseInt(idPuntoEmision), id_prefactura, id_instalacion, usuario, serie_factura, num_factura, "1119999999", ruc,
//                                                idFormaPago, "h", "Pichincha", "", num_comp_pago, "0", id_plan_cuenta_banco, "", "Emisión de la factura por servicios de Internet Nro. " + serie_factura + "-" + num_factura, 
//                                                subtotal, "0", subtotal_2, subtotal_3, descuento, iva_2, iva_3, total_pagar, "array[" + paramArtic + "]", "", "0", 
//                                                "", "", "", "NULL", "0", "array[]::varchar[]", "array[" + paramAsiento + "]", xmlFirmado, String.valueOf(dias_conexion), ids_productos, cantidades, 
//                                                preciosUnitarios, descuentos, subtotales, ivas, totales, tipoRubros, idsPrefacturaRubro, "", "", estado_servicio);
//
//
//                                        if(idFactura.compareTo("-1")!=0){
//                                            long numFacturaAux = Long.parseLong(num_factura) + 1;
//                                            matPuntosVirtuales[p][4] = String.valueOf(numFacturaAux);
//                                            
//                                            this.objDataBase.ejecutar("update tbl_prefactura set por_emitir_factura=false where id_prefactura="+id_prefactura);
//                                            
//                                            this.objDataBase.ejecutar("update tbl_factura_venta set estado_documento='"+estadoDocumento+"', clave_acceso='"+claveAcceso
//                                            +"', documento_xml='"+autorizacionXml+"' where id_factura_venta="+idFactura);
//
//                                            String transaccion = "EMISION DE LA FACTURA NRO. "+serie_factura+"-"+num_factura+" CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txt_periodo;
//                                            this.objDataBase.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
//                                                    "values('"+usuario+"','127.0.0.1','"+Fecha.getHora()+"','"+Fecha.getFecha("ISO")+"', '"+transaccion+"');");
//                                            
//                                        }else{
//                                            System.out.println("Error al emitir la factura. " + this.objDataBase.getError());
//                                        }
//                                    }else{
//                                        System.out.println("Error al firmar la factura en formato xml. " + error);
//                                    }
//                                }else{
//                                    System.out.println("Error al generar la factura en formato xml. " + error);
//                                }
//                            }else{
//                                System.out.println("El número de factura "+serie_factura+"-"+num_factura+" ya ha sido emitido.");
//                            }
//
//                    }else{
//                        System.out.println("La sucursal de código " + id_sucursal + "No tiene un punto de emisión virtual");
//                        break;
//                    }
//                }catch(Exception e){
//                    String msg = "Error en la emision de la prefactura ID: " + id_prefactura + ". " + e.getMessage() + ". " + objDataBase.getError();
//                    System.out.println(msg);
//                    //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
//                }
//                
//            }
//            rs.close();
//            objPromocion.cerrar();

        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
        }finally{
            String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de emisión de facturas del cash";
            System.out.println(msg);
            this.objDataBase.cerrar();
            objDocumental.cerrar();
            //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", "sistemas@saitel.ec", "", "CONTABILIZACION", new StringBuilder(msg), true);
        }
        
        
        
        
        
        //  ENVIO DE DOCUMENTOS ELECTRONICOS AL SRI
        
        DocumentosElectronicosSri objDocumentosElectronicosSri = new DocumentosElectronicosSri();
        objDocumentosElectronicosSri.execute();
        
    }
    
    
    
//    private String emitir(String id_sucursal, int id_punto_emision, String id_prefactura, String id_instalacion, String usuario, String serie_factura, String num_factura, 
//            String autorizacion, String ruc, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos, 
//            String id_plan_cuenta_banco, String son, String concepto, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, 
//            String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, 
//            String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, 
//            String paramRet, String paramAsiento, String xmlFirmado, String dias_conexion, String ids_productos, String cantidades, 
//            String preciosUnitarios, String descuentos, String subtotales, String ivas, String totales, String tipoRubros, String idsPrefacturaRubro,
//            String idCliAnt, String monto_vajar, String estado_servicio)
//    {
//        String idFact = "-1";
//        try{
//            String fecha_prefactura = "";
//            String periodo = "";
//            int anio = Fecha.getAnio();
//            int mes= Fecha.getMes();
//            try{
//                ResultSet rsPreFact = this.objDataBase.consulta("select fecha_prefactura, periodo from tbl_prefactura where id_prefactura="+id_prefactura);
//                if(rsPreFact.next()){
//                    fecha_prefactura = rsPreFact.getString("fecha_prefactura")!=null ? rsPreFact.getString("fecha_prefactura") : "";
//                    periodo = rsPreFact.getString("periodo")!=null ? rsPreFact.getString("periodo") : "";
//                    anio = Fecha.datePart("anio", periodo);
//                    mes = Fecha.datePart("mes", periodo);
//                }
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            
//            
//            String id_cliente = "";
//            String razon_social = "";
//            String id_plan_cuenta_anticipo = "";
//            String direccion = "";
//            String telefono = "";
//            //String direccion_instalacion = "";
//            String ip = "";
//            String radusername = "";
//            try{
//                ResultSet rsInstal = this.objDataBase.consulta("select C.id_cliente, razon_social, id_plan_cuenta_anticipo, direccion, telefono, direccion_instalacion, ip::varchar, radusername "
//                    + "from tbl_instalacion as I inner join tbl_cliente as C on I.id_cliente=C.id_cliente where I.id_instalacion=" + id_instalacion);
//                if(rsInstal.next()){
//                    id_cliente = rsInstal.getString("id_cliente")!=null ? rsInstal.getString("id_cliente") : "";
//                    razon_social = rsInstal.getString("razon_social")!=null ? rsInstal.getString("razon_social") : "";
//                    id_plan_cuenta_anticipo = rsInstal.getString("id_plan_cuenta_anticipo")!=null ? rsInstal.getString("id_plan_cuenta_anticipo") : "232";
//                    direccion = rsInstal.getString("direccion")!=null ? rsInstal.getString("direccion") : "";
//                    telefono = rsInstal.getString("telefono")!=null ? rsInstal.getString("telefono") : "";
//                    //direccion_instalacion = rsInstal.getString("direccion_instalacion")!=null ? rsInstal.getString("direccion_instalacion") : "";
//                    ip = rsInstal.getString("ip")!=null ? rsInstal.getString("ip") : "";
//                    radusername = rsInstal.getString("radusername")!=null ? rsInstal.getString("radusername") : "";
//                }
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            
//            
//            ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
//            
//        
//            
//            
//            
//            ResultSet res = this.objDataBase.consulta("select facturaVenta("+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+usuario+"', '"+serie_factura+
//                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', now()::date, '"+direccion+
//                    "', '"+telefono+"', '"+id_forma_pago+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
//                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+concepto+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_3+", "+descuento+
//                    ", "+iva_2+", "+iva_3+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
//                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+
//                    "', "+id_plan_cuenta_anticipo+", array[]::varchar[]);");  // 42 param 
//            
//            if(res.next()){
//                String idFactComp = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
//                String vecFactComp[] = idFactComp.split(":");
//                idFact = vecFactComp[0];
//                if(idFact.compareTo("-1")!=0){
//                    this.objDataBase.ejecutar("update tbl_factura_venta set id_instalacion="+id_instalacion+", ip='"+ip+"', radusername='"+radusername+"' where id_factura_venta="+idFact+";");
//                    this.objDataBase.ejecutar("update tbl_prefactura set id_factura_venta="+idFact+", fecha_emision=now()::date, es_fact_impago=false, dias_conexion="+dias_conexion+" where id_prefactura="+id_prefactura+";");
//                    
//                    if(estado_servicio.compareTo("c")==0||estado_servicio.compareTo("n")==0){
//                        this.objDataBase.ejecutar("UPDATE tbl_instalacion SET estado_servicio = "
//                                + "case "
//                                + " when "+id_instalacion+" in (select distinct id_instalacion from tbl_instalacion_suspension where eliminado=false and now()::date between fecha_inicio and fecha_termino) "
//                                + " then 's' "
//                                + " else 'a' "
//                                + "end "
//                                + "where estado_servicio in ('c', 'n') and id_instalacion="+id_instalacion+";");
//                    }
//                    
//                    if(estado_servicio.compareTo("e")==0){
//                        this.objDataBase.ejecutar("UPDATE tbl_instalacion SET estado_servicio='t' where estado_servicio in ('e') and id_instalacion="+id_instalacion);
//                    }
//                    
//                    //   sistema juridico  
//                    this.objDataBase.ejecutar("update tbl_gestion_cobranzas set gestion_final='PAGAN Y CONTINUAN' where id_gestion="+id_prefactura+";");
//                    
//                    
//                    if(forma_pago.compareTo("d")==0){
//                        this.objDataBase.ejecutar("update tbl_instalacion set estado_servicio='c' where estado_servicio='a' and id_instalacion="+id_instalacion+" and id_instalacion in "
//                                + "(select P.id_instalacion from tbl_prefactura as P inner join tbl_factura_venta as F on P.id_factura_venta=F.id_factura_venta "
//                                + "where getFechaSuspensionCreditos(fecha_prefactura) < now()::date and forma_pago='d' and deuda>0) and id_instalacion not in "
//                                + "(select distinct id_instalacion from tbl_anticipo_internet where now()::date between fecha_ini and fecha_fin);");
//                    }
//                    
//                    List sql = new ArrayList();
//                    sql.add("delete from tbl_instalacion_rubro where id_instalacion="+id_instalacion+" and periodo_cobro between '"+anio + "-" + mes + "-01' and '"+anio + "-" + mes + "-"+ Fecha.getUltimoDiaMes(anio, mes) +"';");
//                    String vec_ids_productos[] = ids_productos.split(",");
//                    String vec_cantidades[] = cantidades.split(",");
//                    String vec_p_u[] = preciosUnitarios.split(",");
//                    String vec_descuentos[] = descuentos.split(",");
//                    String vec_subtotales[] = subtotales.split(",");
//                    String vec_ivas[] = ivas.split(",");
//                    String vec_totales[] = totales.split(",");
//                    String vec_tipoRubros[] = tipoRubros.split(",");
//                    String vec_idsPrefacturaRubro[] = idsPrefacturaRubro.split(",");
//                    for(int i=1; i<vec_ids_productos.length; i++){
//                        sql.add("insert into tbl_instalacion_rubro(id_prefactura, id_instalacion, periodo_cobro, id_producto, cantidad, p_u, p_st, descuento, iva, total) "
//                                + "values("+id_prefactura+", "+id_instalacion+", '"+periodo+"', "+vec_ids_productos[i]+", "+vec_cantidades[i]+", "+vec_p_u[i]+", "+
//                                vec_subtotales[i]+", "+vec_descuentos[i]+", "+vec_ivas[i]+", "+vec_totales[i]+");");
//                        if(vec_tipoRubros[i].compareTo("p")==0 || vec_tipoRubros[i].compareTo("1")==0){
//                            this.objDataBase.ejecutar("update tbl_prefactura_rubro set estadocobro=true where id_prefactura_rubro="+vec_idsPrefacturaRubro[i]);
//                        }
//                    }
//                    this.objDataBase.transacciones(sql);
//                    
//                }
//                
//                
//                
//                res.close();
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return idFact;
//    }
//
//    
//    public boolean facturaDuplicada(String serie, String numero)
//    {
//        ResultSet res = this.objDataBase.consulta("SELECT * FROM tbl_factura_venta where serie_factura='"+serie+"' and num_factura="+numero);
//        if(this.objDataBase.getFilas(res)>0){
//            return true;
//        }
//        try{
//            res.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return false;
//    }
//    
//    
//    public String[][] getPuntosEmisionVirtuales()
//    {
//        ResultSet rs = this.objDataBase.consulta("SELECT P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, " + 
//            "case when max(num_factura)>0 then max(num_factura)+1 else 1 end, direccion_establecimiento " +
//            "from tbl_punto_emision as P inner join tbl_factura_venta as F on F.serie_factura=P.fac_num_serie " +
//            "where caja_virtual=true " +
//            "group by P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, direccion_establecimiento " +
//            "order by id_sucursal");
//        return Matriz.ResultSetAMatriz(rs);
//    }
//    
//    
//    private String getStringFromFile(String archivo) throws IOException 
//    {
//        File file = new File(archivo);
//        FileReader fr = new FileReader(file);
//        BufferedReader br = new BufferedReader(fr);
//        StringBuilder cadXml = new StringBuilder();
//        //String cadXml = "";
//        String linea;
//        while((linea=br.readLine())!=null){
//            cadXml.append(linea);
//            //cadXml += linea;
//        }
//        return cadXml.toString();
//    }
     
}
