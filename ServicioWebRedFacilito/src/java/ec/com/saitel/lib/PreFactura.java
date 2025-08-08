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

package ec.com.saitel.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Jorge
 */
public class PreFactura extends DataBase{
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    
    public PreFactura(String m, int p, String db, String u, String c)
    {
        super(m, p, db, u, c);
        _ip = m;
        _puerto = p;
        _db = db;
        _usuario = u;
        _clave = c;
    }
    public boolean calcularPreFactura(String idF)
    {
        boolean r = false;
        try{
            ResultSet res = this.consulta("select proc_calcularPreFactura("+idF+", false);");
            if(res.next()){
                r = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return r;
    }

    public ResultSet getPreFactura(String idF)
    {
        return this.consulta("SELECT *, (periodo + '1 month'::interval)::date as periodo_suspension FROM vta_prefactura WHERE id_prefactura="+idF+";");
    }

    public String getUltimoPeriodo()
    {
        String periodo_suspension = Fecha.getFecha("SQL");
        try{
            ResultSet res = this.consulta("SELECT max(periodo) FROM tbl_prefactura");
            if(res.next()){
                periodo_suspension = (res.getString(1)!=null) ? res.getString(1) : periodo_suspension;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return periodo_suspension;
    }

    public ResultSet getPreFacturaRubros(String id_sucursal, String id_instalacion, String periodo)
    {
        /*String periodo_ini = "";
        String periodo_fin = "";
        if(periodo.indexOf("-")>0){
            String vec[] = periodo.split("-");
            periodo_ini = vec[0] + "-" + vec[1] + "-01";
            periodo_fin = vec[0] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[0]), Integer.parseInt(vec[1]));
        }else{
            String vec[] = periodo.split("/");
            periodo_ini = vec[2] + "-" + vec[1] + "-01";
            periodo_fin = vec[2] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[2]), Integer.parseInt(vec[1]));
        }*/
        int anio = Fecha.datePart("anio", periodo);
        int mes = Fecha.datePart("mes", periodo);
        String ini = anio + "-" + mes + "-01";
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        return this.consulta("SELECT * FROM vta_prefactura_rubro WHERE id_sucursal="+id_sucursal+" and id_instalacion="+id_instalacion+" and periodo between '"+ini+"' and '"+fin+"';");
    }
    
    public ResultSet getPreFacturaRubrosOrdenTrabajo(String id_instalacion)
    {
        return this.consulta("SELECT PR.*, P.codigo, I.porcentaje, I.codigo as codigo_iva, (monto / canproductos)::numeric(18,4) as precioUnitario, "+ 
            "(monto * I.porcentaje::numeric / 100)::numeric(13,2) as iva_12, " +
            "(monto + (monto * I.porcentaje::numeric / 100 ))::numeric(13,2) as total " +
            "FROM (((tbl_prefactura_rubro as PR inner join vta_producto_n as P on PR.idproductos::int=P.id_producto) " +
            "inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) " +
            "inner join tbl_iva as I on I.id_iva=SP.id_iva) " +
            "WHERE id_rubro is null and estadocobro=false and tiporubro='p' and id_instalacion="+id_instalacion+
            " union " +
            "SELECT PR.*, P.codigo_activo as codigo, (select valor::int as porcentaje from tbl_configuracion where parametro='p_iva1'), " +
            "(select I.codigo as codigo_iva from tbl_iva as I inner join tbl_configuracion as C on C.valor::int=I.porcentaje where parametro='p_iva1'), "+ 
            "monto as precioUnitario, " +
            "(select (PR.monto * valor::numeric / 100 )::numeric(13,2) as iva_12 from tbl_configuracion where parametro='p_iva1'), " +
            "(select (monto + (PR.monto * valor::numeric / 100))::numeric(13,2) as total from tbl_configuracion where parametro='p_iva1') " +
            "FROM (tbl_prefactura_rubro as PR inner join vta_activo_n as P on PR.idproductos::int=P.id_activo) " +
            "WHERE id_rubro is null and estadocobro=false and tiporubro='1' and id_instalacion="+id_instalacion);
    }
    
    public ResultSet getPreFacturaRubrosInstalacion(String id_instalacion, String periodo)
    {
        String periodo_ini = "";
        String periodo_fin = "";
        if(periodo.indexOf("-")>0){
            String vec[] = periodo.split("-");
            periodo_ini = vec[0] + "-" + vec[1] + "-01";
            periodo_fin = vec[0] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[0]), Integer.parseInt(vec[1]));
        }else{
            String vec[] = periodo.split("/");
            periodo_ini = vec[2] + "-" + vec[1] + "-01";
            periodo_fin = vec[2] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[2]), Integer.parseInt(vec[1]));
        }
        return this.consulta("SELECT * FROM vta_instalacion_rubro WHERE id_instalacion="+id_instalacion+" and periodo_cobro between '"+periodo_ini+"' and '"+periodo_fin+"';");
    }

    public ResultSet getPlan(String idPlan, String periodo)
    {
        return this.consulta("select * from vta_plan_tarifa where id_plan_servicio="+idPlan+" and '"+periodo+"' between vigente_desde and vigente_hasta");
    }

    public String concatenarValores(String id_articulos, String cantidades, String precios_costo, String precios_unitarios,
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

    public String concatenarValores(String id_retenciones, String bases_imponibles, String valores_retenidos)
    {
        String param = "";
        String vecRet [] = id_retenciones.split(",");
        String vecBI [] = bases_imponibles.split(",");
        String vecVal [] = valores_retenidos.split(",");
        for(int i=0; i<vecRet.length; i++){
            param += "['"+vecRet[i]+"','"+vecBI[i]+"','"+vecVal[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }

    public String verificarStock(int id_sucursal, String id_productos, String cantidades)
    {
        ResultSet rs = null;
        String codigo = "";
        int cantidad = 1;
        String vecProd [] = id_productos.split(",");
        String vecCant [] = cantidades.split(",");
        for(int i=0; i<vecProd.length; i++){
            try{
                rs = this.consulta("select P.codigo, SP.stock_sucursal - "+vecCant[i]+" "
                        + "from vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto "
                        + "where SP.id_sucursal="+id_sucursal+" and SP.id_producto="+vecProd[i]);
                if(rs.next()){
                    cantidad = rs.getString(2)!=null ? rs.getInt(2) : 1;
                    if(cantidad < 0){
                        codigo = rs.getString("codigo")!=null ? rs.getString("codigo") : "";
                        break;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return codigo;
    }

    public boolean actualizar(String id_prefactura, String id_instalacion, String periodo, String id_productos, String cantidades,
                              String precios_unitarios, String subtotales, String descuentos, String ivas, String totales)
    {
        String periodo_ini = "";
        String periodo_fin = "";
        if(periodo.indexOf("-")>0){
            String vec[] = periodo.split("-");
            periodo_ini = vec[0] + "-" + vec[1] + "-01";
            periodo_fin = vec[0] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[0]), Integer.parseInt(vec[1]));
        }else{
            String vec[] = periodo.split("/");
            periodo_ini = vec[2] + "-" + vec[1] + "-01";
            periodo_fin = vec[2] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[2]), Integer.parseInt(vec[1]));
        }
        List sql = new ArrayList();
        String vecProd [] = id_productos.split(",");
        String vecCat [] = cantidades.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecDes [] = descuentos.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        sql.add("update tbl_prefactura set dias_conexion="+vecCat[0]+" where id_prefactura="+id_prefactura);
        sql.add("delete from tbl_instalacion_rubro where id_instalacion="+id_instalacion+" and periodo_cobro between '"+periodo_ini+"' and '"+periodo_fin+"';");
        for(int i=1; i<vecProd.length; i++){
            sql.add("insert into tbl_instalacion_rubro(id_prefactura, id_instalacion, periodo_cobro, id_producto, cantidad, p_u, p_st, descuento, iva, total) "
               + "values("+id_prefactura+", "+id_instalacion+", '"+periodo+"', "+vecProd[i]+", "+vecCat[i]+", "+vecPU[i]+", "+vecSubt[i]+", "+vecDes[i]+", "+vecIva[i]+", "+vecTot[i]+");");
        }
        return this.transacciones(sql);
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

    public String concatenarValores(String idCliAnts, String montos_vajar)
    {
        String param = "";
        String idCliAnt [] = idCliAnts.split(",");
        String monto_vajar [] = montos_vajar.split(",");
        for(int i=0; i<idCliAnt.length; i++){
            param += "['"+idCliAnt[i]+"','"+monto_vajar[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }

    public String emitir(String id_sucursal, int id_punto_emision, String id_prefactura, String id_instalacion, String usuario, String serie_factura, String num_factura, 
            String autorizacion, String ruc, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos, 
            String id_plan_cuenta_banco, String son, String concepto, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, 
            String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, 
            String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, 
            String paramRet, String paramAsiento, String xmlFirmado, String dias_conexion, String ids_productos, String cantidades, 
            String preciosUnitarios, String descuentos, String subtotales, String ivas, String totales, String tipoRubros, String idsPrefacturaRubro, 
            String idCliAnt, String monto_vajar, String estado_servicio, String claveAcceso)
    {
        String idFact = "-1";
        try{
            String fecha_prefactura = "";
            String periodo = "";
            int anio = Fecha.getAnio();
            int mes= Fecha.getMes();
            try{
                ResultSet rsPreFact = this.consulta("select fecha_prefactura, periodo from tbl_prefactura where id_prefactura="+id_prefactura);
                if(rsPreFact.next()){
                    fecha_prefactura = rsPreFact.getString("fecha_prefactura")!=null ? rsPreFact.getString("fecha_prefactura") : "";
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
            String paramMontosVajar = this.concatenarValores(idCliAnt, monto_vajar);
            ResultSet res = this.consulta("select facturaVenta("+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+usuario+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', now()::date, '"+direccion+
                    "', '"+telefono+"', '"+id_forma_pago+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+concepto+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_3+", "+descuento+
                    ", "+iva_2+", "+iva_3+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+
                    "', "+id_plan_cuenta_anticipo+", "+paramMontosVajar+");");  /* 42 param */
            if(res.next()){
                String idFactComp = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                String vecFactComp[] = idFactComp.split(":");
                idFact = vecFactComp[0];
                if(idFact.compareTo("-1")!=0){
                    this.ejecutar("update tbl_factura_venta set id_instalacion="+id_instalacion+", ip='"+ip+"', radusername='"+radusername+"', clave_acceso='"+claveAcceso+"', estado_documento='f', conciliado=false where id_factura_venta="+idFact+";");
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
                    
                    /*   sistema juridico   */
                    this.ejecutar("update tbl_gestion_cobranzas set gestion_final='PAGAN Y CONTINUAN' where id_gestion="+id_prefactura+";");
                    
                    
                    if(forma_pago.compareTo("d")==0){
                        this.ejecutar("update tbl_instalacion set estado_servicio='c' where estado_servicio='a' and id_instalacion="+id_instalacion+" and id_instalacion in "
                                + "(select P.id_instalacion from tbl_prefactura as P inner join tbl_factura_venta as F on P.id_factura_venta=F.id_factura_venta "
                                + "where getFechaSuspensionCreditos(fecha_prefactura) < now()::date and forma_pago='d' and deuda>0) and id_instalacion not in "
                                + "(select distinct id_instalacion from tbl_anticipo_internet where now()::date between fecha_ini and fecha_fin);");
                    }
                    
                    if(idsPrefacturaRubro.compareTo("")!=0){
                        this.ejecutar("update tbl_prefactura_rubro set estadocobro=true where id_prefactura_rubro in (" + idsPrefacturaRubro + ")");
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
            }
            /*ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
            String paramProductos = this.concatenarValores(id_productos, cantidades, precios_costo, precios_unitarios, subtotales, descuentos, ivas, totales, descripcion);
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_emitirFactura("+id_sucursal+", "+id_punto_emision+", "+id_prefactura+", '"+usuario+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+ "', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total+", "+paramProductos+
                    ", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRetencion+", '"+concepto+"');");
            if(res.next()){
                idFact = (res.getString(1)!=null) ? res.getString(1) : "-1";
                res.close();
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return idFact;
    }
//
//    public String facturar(HttpServletRequest request, String DOCS_ELECTRONICOS, int id_sucursal_sesion, String id_sucursal, int id_punto_emision, 
//            String id_prefactura, String id_instalacion, String id_cliente, String usuario, String serie_factura, String num_factura, 
//            String autorizacion, String ruc, String id_forma_pago, String codigo_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, double saldo_doc, String gastos_bancos, 
//            String id_plan_cuenta_banco, String son, String concepto, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, 
//            String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, 
//            String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, 
//            String paramRet, String paramAsiento, String dias_conexion, String ids_productos, String descripciones, String cantidades, 
//            String preciosUnitarios, String descuentos, String subtotales, String ivas, String pIvas, String codigoIvas, String totales, String tipoRubros, 
//            String idsPrefacturaRubro, String anticipo_ini, int num_meses, String idCliAnt, String monto_vajar, String txtPeriodo, String modulo, String estado_servicio)
//    {
//        String r = "";
//        String error;
//        
//        PreFactura objPreFactura = new PreFactura(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        DocumentoBanco objDocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        FacturaElectronica objFE = new FacturaElectronica();
//        
//        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        String ambiente = conf.getValor("ambiente");
//        String tipoEmision = conf.getValor("tipo_emision"); // 1=normal    2=Indisponibilidad del sistema
//        //String clave_certificado = conf.getValor("clave_certificado");
//        String ruc_empresa = conf.getValor("ruc");
//        String razon_social_empresa = conf.getValor("razon_social");
//        String nombre_comercial = conf.getValor("nombre_comercial");
//        String num_resolucion = conf.getValor("num_resolucion");
//        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
//        String dir_matriz = conf.getValor("dir_matriz");
//        
//        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        String direccion_sucursal = objSucursal.getDireccion( String.valueOf(id_punto_emision) );
//        
//        
//        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        String num_cuenta = "";
//        String tipo_documento_cliente = "05";
//        String razon_social = "";
//        String direccion = "";
//        try{
//            //ResultSet rsCliente = objCliente.getCliente(id_cliente);
//            //ResultSet rsCliente = objCliente.getClientePorRuc(ruc);
//            /*if(rsCliente.next()){
//                num_cuenta = rsCliente.getString("num_cuenta")!=null ? rsCliente.getString("num_cuenta") : "";
//                tipo_documento_cliente = rsCliente.getString("tipo_documento")!=null ? rsCliente.getString("tipo_documento") : "05";
//                razon_social = rsCliente.getString("razon_social")!=null ? rsCliente.getString("razon_social") : "";
//                direccion = rsCliente.getString("direccion")!=null ? rsCliente.getString("direccion") : "";
//                rsCliente.close();
//            }*/
//            try{
//                ResultSet rsInstal = this.consulta("select C.id_cliente, C.tipo_documento, C.num_cuenta, razon_social, id_plan_cuenta_anticipo, direccion, telefono, direccion_instalacion, ip::varchar, radusername "
//                    + "from tbl_instalacion as I inner join tbl_cliente as C on I.id_cliente=C.id_cliente where I.id_instalacion=" + id_instalacion);
//                if(rsInstal.next()){
//                    num_cuenta = rsInstal.getString("num_cuenta")!=null ? rsInstal.getString("num_cuenta") : "";
//                    tipo_documento_cliente = rsInstal.getString("tipo_documento")!=null ? rsInstal.getString("tipo_documento") : "05";
//                    id_cliente = rsInstal.getString("id_cliente")!=null ? rsInstal.getString("id_cliente") : "";
//                    razon_social = rsInstal.getString("razon_social")!=null ? rsInstal.getString("razon_social") : "";
//                    direccion = rsInstal.getString("direccion")!=null ? rsInstal.getString("direccion") : "";
//                }
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//                
//
//            String prod_sin_stock = objPreFactura.verificarStock(id_sucursal_sesion, ids_productos, cantidades);
//            if(prod_sin_stock.compareTo("")==0){
//
//                if(!objPreFactura.facturaDuplicada(serie_factura, num_factura)){
//
//                    String xmlFirmado = "";
//                    String estadoDocumento = "p";
//                    String certificado = DOCS_ELECTRONICOS + "certificado.p12";
//                    String rutaSalida = DOCS_ELECTRONICOS + "firmados";
//                    String claveAcceso = "";
//                    String autorizacionXml = "";
//                    String respuestaAutoriz = "";
//                    String vecSerie[] = serie_factura.split("-");
//
//                    claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
//
//                    autorizacionXml = objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
//                            Cadena.setSecuencial(num_factura), dir_matriz, Fecha.getFecha("SQL"), direccion_sucursal, num_resolucion, oblga_contabilidad, 
//                            tipo_documento_cliente, razon_social, ruc, subtotal, descuento, subtotal_0, subtotal_2, iva_2, subtotal_3, iva_3, total, codigo_forma_pago, 
//                            ids_productos, descripciones, cantidades, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion);
//                    /*String documentoXml = DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
//                    objFE.salvar(documentoXml);
//                    error = objFE.getError();
//
//                    if(error.compareTo("")==0){
//                        estadoDocumento = "g";
//                        String archivoSalida = claveAcceso + ".xml";
//                        FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
//                        firmaDigital.execute();
//                        error = firmaDigital.getError();
//
//                        if(error.compareTo("")==0){
//                            estadoDocumento = "f";
//                            autorizacionXml = this.getStringFromFile(DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");*/
//                            
//                            String id_factura = this.emitir(id_sucursal, id_punto_emision, id_prefactura, id_instalacion, usuario, serie_factura, num_factura, autorizacion, ruc,
//                                    id_forma_pago, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco, son, concepto, subtotal, subtotal_0, subtotal_2, subtotal_3, 
//                                    descuento, iva_2, iva_3, total, "array[" + paramArtic + "]", ret_num_serie, ret_num_retencion, ret_autorizacion, ret_fecha_emision, 
//                                    ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal, ret_impuesto_retenido, "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", 
//                                    xmlFirmado, dias_conexion, ids_productos, cantidades, preciosUnitarios, descuentos, subtotales, ivas, totales, tipoRubros, idsPrefacturaRubro, 
//                                    idCliAnt, monto_vajar, estado_servicio);
//
//                            if(id_factura.compareTo("-1")!=0){
//
//                                objFacturaVenta.setDocumentoElectronico(id_factura, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz);
//                                
//                                if(saldo_doc > 0){
//                                    if(objDocumentoBanco.hayDocumento(num_comp_pago)){
//                                       objDocumentoBanco.actualizar(num_comp_pago, total);
//                                    }else{
//                                       double saldo = saldo_doc - ( Float.parseFloat(total) - Float.parseFloat(ret_impuesto_retenido) );
//                                       objDocumentoBanco.insertar(usuario, num_comp_pago, String.valueOf(saldo_doc), saldo);
//                                    }
//                                }
//                                
//
//                                if(anticipo_ini.compareTo("")!=0){
//                                    if(num_meses > 0){
//                                        int num_meses1 = num_meses - 1;
//                                        String anticipo_fin = anticipo_ini;
//                                        if(num_meses1>=1){
//                                            anticipo_fin = Fecha.add(anticipo_ini, Calendar.MONTH, num_meses1);
//                                        }
//                                        int anio = Fecha.datePart("anio", anticipo_fin);
//                                        int mes = Fecha.datePart("mes", anticipo_fin);
//                                        anticipo_fin = Fecha.getUltimoDiaMes(anio, mes) + "/" + mes + "/" + anio;
//                                        if(objPreFactura.ingresarAnticipo(id_instalacion, anticipo_ini, num_meses, id_factura)){
//                                            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
//                                            auditoria.setRegistro(request, "REGISTRO DE ANTICIPO DEL PAGO DEL SERVICIO DE INTERNET PARA EL CLIENTE CON RUC "+ruc+" CON NUMERO DE FACTURA "+serie_factura+"-"+num_factura);
//                                            auditoria.cerrar();
//                                        }else{
//                                            r = objPreFactura.getError();
//                                        }
//                                    }
//                                }
//
//                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
//                                auditoria.setRegistro(request, "EMISION DE LA FACTURA NRO. "+serie_factura+"-"+num_factura+" CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txtPeriodo);
//                                auditoria.cerrar();
//
//        /*   desde aqui                        
//                                Robot objRobot = new Robot(this.gene_ip, this.gene_puerto, this.gene_db, usuario, clave);
//                                objPreFactura.quitarPrefactura(id_instalacion);   // en el caso de haber hecho una suspension para el periodo
//                                objRobot.generarPrefacturasFaltantes(id_instalacion);
//                                objRobot.cerrar();
//
//                                String rad_db = conf.getValor("rad_db");
//                                String rad_ip = conf.getValor("rad_ip");
//                                String rad_puerto = conf.getValor("rad_puerto");
//                                String rad_usuario = conf.getValor("rad_usuario");
//                                String rad_clave = conf.getValor("rad_clave");
//
//                                String radusername = "";
//                                String radclave = "";
//                                String estado_servicio = "";
//                                Instalacion objInstalacion = new Instalacion(this.gene_ip, this.gene_puerto, this.gene_db, usuario, clave);
//                                try{
//                                    ResultSet rsInstalacion = objInstalacion.getInstalacion(id_instalacion);
//                                    if(rsInstalacion.next()){
//                                        radusername = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
//                                        radclave = (rsInstalacion.getString("radclave")!=null) ? rsInstalacion.getString("radclave") : "";
//                                        estado_servicio = (rsInstalacion.getString("estado_servicio")!=null) ? rsInstalacion.getString("estado_servicio") : "";
//                                        rsInstalacion.close();
//                                    }
//                                }catch(Exception e){
//                                    e.printStackTrace();
//                                }finally{
//                                    objInstalacion.cerrar();
//                                }
//
//
//                                if(estado_servicio.toLowerCase().compareTo("a")==0){
//                                    Radius dbRadius = new Radius(rad_ip, Integer.parseInt(rad_puerto), rad_db, rad_usuario, rad_clave);
//                                    dbRadius.setClave(radusername, radclave);
//                                    dbRadius.cerrar();
//                                }
//            / hasta aqui */
//
//                                if(modulo.compareTo("2")==0){   
//                                    objPreFactura.ejecutar("update tbl_documento_banco_tarjeta_credito set factura_emitida=true where num_tarjeta='"+num_cuenta+"' and id_instalacion="+id_instalacion);
//                                }
//                                
//                                r = "idFactura=" + id_factura + ";^msg»"+r;
//
//                            }else{
//                                r = objPreFactura.getError();
//                            }
//                    /*    }else{
//                            r = error;
//                        }
//                    }else{
//                        r = error;
//                    }*/
//                }else{
//                    r = "El número de factura "+serie_factura+"-"+num_factura+" ya ha sido emitido.";
//                }
//
//            }else{
//                r = "Stock insuficiente. El stock del producto de código: " + prod_sin_stock + " ha disminuido por una venta realizada desde otra caja ubicada en la sucursal.";
//            }
//            
//        }catch(Exception e){
//            error = e.getMessage();
//        }finally{
//            objPreFactura.cerrar();
//            objFacturaVenta.cerrar();
//            objDocumentoBanco.cerrar();
//            objSucursal.cerrar();
//            objCliente.cerrar();
//            conf.cerrar();
//        }    
//        return r;
//    }

    
    public boolean activarEmision(String idI, String periodo)
    {
        boolean conPrefacturas = true;
        try{
            String vec[] = periodo.indexOf("-")>0 ? periodo.split("-") : periodo.split("/");
            String anio = periodo.indexOf("-")>0 ? vec[0] : vec[2];
            ResultSet res = this.consulta("SELECT count(*) FROM tbl_prefactura where id_instalacion='"+idI+"' and periodo<'"+anio+"-"+vec[1]+"-01' and fecha_emision is null;");
            if(res.next()){
                int mesDeudas = (res.getString(1)!=null) ? res.getInt(1) : 0;
                if(mesDeudas > 0){
                    conPrefacturas = false;
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        boolean conFacturaCredito = true;
        try{
            ResultSet res = this.consulta("SELECT count(*) FROM tbl_factura_venta where id_instalacion='"+idI+"' and deuda > 0");
            if(res.next()){
                int aCredito = (res.getString(1)!=null) ? res.getInt(1) : 0;
                if(aCredito > 0){
                    conPrefacturas = false;
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return conPrefacturas && conFacturaCredito;
    }
    
    public boolean generarPreFactura(String anio, String mes, String id_instalacion)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_generarPreFactura("+anio+", "+mes+", "+id_instalacion+");");
            if(res.next()){
                long pk = res.getString(1)!=null ? res.getLong(1) : -1;
                ok = pk>0;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    /*public boolean ingresarAnticipo(String id_instalacion, String fecha_ini, String fecha_fin)
    {
        return this.ejecutar("insert into tbl_anticipo_internet(id_instalacion, fecha_ini, fecha_fin) "
                + "values("+id_instalacion+", '"+fecha_ini+"', '"+fecha_fin+"');");
    }*/
    
    public boolean ingresarAnticipo(String id_instalacion, String fecha_ini, int numMeses, String id_factura)
    {
        int anio = Fecha.datePart("anio", fecha_ini);
        int mes = Fecha.datePart("mes", fecha_ini);
        fecha_ini = anio + "-" + mes + "-01";
        return this.ejecutar("insert into tbl_anticipo_internet(id_instalacion, fecha_ini, fecha_fin, id_factura_venta) "
                + "values("+id_instalacion+", '"+fecha_ini+"', '"+fecha_ini+"'::date + '"+(numMeses-1)+" month'::interval, "+id_factura+");");
    }
    
    

    public ResultSet getPrimerPeriodoDeudaPrefactura(String id_instalacion)
    {
        try{
            ResultSet rs = this.consulta("SELECT id_prefactura FROM vta_prefactura where id_instalacion="+id_instalacion+" and fecha_emision is null order by fecha_prefactura");
            if(rs.next()){
                String idPreFactura = rs.getString("id_prefactura")!=null ? rs.getString("id_prefactura") : "";
                if(idPreFactura.compareTo("")!=0){
                    ResultSet res = this.consulta("select proc_calcularPreFactura("+idPreFactura+", false);");
                    if(res.next()){
                        boolean r = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                        if(r){
                            return this.consulta("SELECT *, total+comision_cash as total_comision FROM vta_prefactura where id_prefactura="+idPreFactura);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
        
        //return this.consulta("SELECT *, total+comision_cash as total_comision FROM vta_prefactura where id_instalacion="+id_instalacion+" and fecha_emision is null order by fecha_prefactura");
    }
    
    public float getTotalPrefactura(String id_instalacion, String periodo)
    {
        float total = 0;
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_prefactura where id_instalacion="+id_instalacion+" and periodo='"+periodo+"'");
            if(res.next()){
                total = res.getString("total")!=null ? res.getFloat("total") : 0;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return total;
    }
    
    
    
    /*  Quitar prefacturas    */
    
    public boolean quitarPrefactura(String id_instalacion)
    {
        String periodo = Fecha.getFecha("ISO");
        try{
            ResultSet rs = this.consulta("select max(periodo) as periodo from tbl_prefactura");
            if(rs.next()){
                periodo = rs.getString("periodo")!=null ?  rs.getString("periodo") : periodo;
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        int anio = Fecha.datePart("anio", periodo);
        int mes = Fecha.datePart("mes", periodo);
        String inicio = anio + "-" + mes + "-01";
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        return this.ejecutar("delete from tbl_prefactura where id_instalacion=" + id_instalacion + " and fecha_emision is null and periodo='"+inicio+"' and "
                + "id_instalacion in (select id_instalacion from tbl_instalacion_suspension where id_instalacion=" + id_instalacion + " and eliminado=false and fecha_inicio between '"+inicio+"' and '"+fin+"')" );
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
    
}

