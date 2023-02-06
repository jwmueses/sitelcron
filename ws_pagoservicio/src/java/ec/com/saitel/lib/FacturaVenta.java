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
import ec.gob.sri.FirmaXadesBes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class FacturaVenta extends DataBase{
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    
    public FacturaVenta(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
        _ip = m;
        _puerto = p;
        _db = db;
        _usuario = u;
        _clave = c;
    }
    
    public ResultSet getFactura(String id)
    {
        return this.consulta("SELECT * FROM vta_factura_venta_retencion where id_factura_venta="+id+";");
    }
    
    public ResultSet getFacturasFirmadas()
    {
        return this.consulta("SELECT * FROM vta_factura_venta_all where estado_documento='f' and anulado=false and fecha_emision = '2017-12-04';");
    }
    public ResultSet getFacturaDetalleAggFirmadas(String id, String id_sucursal)
    {
        return this.consulta("select distinct replace( replace(array_agg(P.id_producto)::varchar, '{', ''), '}', '') as ids_productos,\n" +
            "replace( replace(array_agg(descripcion_mas)::varchar, '{', ''), '}', '') as descripciones, \n" +
            "replace( replace(array_agg(cantidad)::varchar, '{', ''), '}', '') as cantidades_prod,\n" +
            "replace( replace(array_agg(p_u)::varchar, '{', ''), '}', '') as preciosUnitarios,\n" +
            "replace( replace(array_agg(D.descuento)::varchar, '{', ''), '}', '') as descuentos,\n" +
            "replace( replace(array_agg(D.p_st)::varchar, '{', ''), '}', '') as subtotales,\n" +
            "replace( replace(array_agg(D.iva)::varchar, '{', ''), '}', '') as ivas,\n" +
            "replace( replace(array_agg(porcentaje)::varchar, '{', ''), '}', '') as pIvas,\n" +
            "replace( replace(array_agg(I.codigo)::varchar, '{', ''), '}', '')  as codigoIvas\n" +
            "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) \n" +
            "inner join tbl_iva as I on I.id_iva=SP.id_iva) \n" +
            "inner join tbl_factura_venta_detalle as D on D.id_producto=P.id_producto \n" +
            "where D.id_factura_venta=574814 and SP.id_sucursal=3");
    }

    public long getNumFactura(int id_punto_emision)
    {
        long num = 0;
        //long num_ini = 0;
        try{
            ResultSet res = this.consulta("SELECT max(num_factura) FROM tbl_factura_venta WHERE id_punto_emision="+id_punto_emision);
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : 0;
                if(num == 0){
                    ResultSet res1 = this.consulta("SELECT num_fact_inicial FROM tbl_punto_emision WHERE id_punto_emision="+id_punto_emision);
                    if(res1.next()){
                        num = (res1.getString(1)!=null) ? res1.getLong(1) : 0;
                        res1.close();
                    }
                }
                res.close();
            }
            /*ResultSet res2 = this.consulta("SELECT fac_sec_desde FROM tbl_punto_emision WHERE id_punto_emision="+id_punto_emision);
            if(res2.next()){
                num_ini = (res2.getString(1)!=null) ? res2.getLong(1) : 0;
                res2.close();
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
        num++;
        return num;
    }

    public ResultSet getFacturasPendientes(String idC)
    {
        return this.consulta("SELECT * FROM vta_factura_venta_all where id_cliente="+idC+" and deuda>0 and anulado=false");
    }
    
    public ResultSet getFacturaPendienteTarjetaCredito(String num_cuenta)
    {
        return this.consulta("SELECT * FROM vta_factura_venta_all where num_cuenta='"+num_cuenta+"' and deuda>0");
    }
    
    public boolean deudaPendiente(String idC)
    {
        boolean ok = false;
        try{
            ResultSet rs = this.consulta("SELECT * FROM vta_factura_venta_all where id_cliente="+idC+" and deuda>0 and anulado=false");
            if(this.getFilas(rs)>0){
               ok = true; 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean instalacionSinDeuda(String idIns)
    {
        boolean ok = false;
        try{
            ResultSet rs = this.consulta("SELECT id_instalacion FROM tbl_prefactura where id_instalacion="+idIns+" and fecha_emision is null "
                    + "union "
                    + "select id_instalacion from tbl_factura_venta where id_instalacion="+idIns+" and deuda>0 and anulado=false");
            if(this.getFilas(rs)==0){
               ok = true; 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public ResultSet getNumsFacturasPendientes(String idC)
    {
        return this.consulta("SELECT id_factura_venta, serie_factura || '-' || num_factura FROM vta_factura_venta_all "
                + "where id_cliente="+idC+" and id_factura_venta not in(select id_factura_venta from tbl_retencion_venta) and deuda>0 and anulado=false");
    }

    public ResultSet getFacturaDetalle(String id)
    {
        return this.consulta("select * from vta_factura_venta_detalle where id_factura_venta="+id);
    }
    
    public ResultSet getFacturaDetalleEdicion(String id, String id_sucursal)
    {
        return this.consulta("select distinct P.id_producto, P.codigo, D.descripcion_mas, SP.stock_sucursal+D.cantidad as stock_sucursal, "+ 
            "P.precio_costo, I.porcentaje as porcentaje_iva, I.codigo as codigo_iva, " +
            "D.id_factura_venta_detalle, D.cantidad, D.p_u, D.p_st, D.descuento, D.iva, D.total, D.id_lista_precio, D.p_descuento, SP.descuento, " +
            "case when tipo='s' then '~' else '' end as de_servicio, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) as costo_minimo, "+ 
            "round( (D.total-D.p_st)*100/D.p_st) as p_iv_aplicado, D.cod_iva, D.p_iva::int " +
            "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) " +
            "inner join tbl_iva as I on I.id_iva=SP.id_iva) " +
            "inner join tbl_factura_venta_detalle as D on D.id_producto=P.id_producto " +
            "where D.id_factura_venta="+id+" and SP.id_sucursal="+id_sucursal);
    }

    public ResultSet getFacturaDetalleActivo(String id)
    {
        return this.consulta("select * from vta_factura_venta_detalle_activo where id_factura_venta="+id);
    }
    
    public ResultSet getFacturaDetalleActivoEdicion(String id)
    {
        return this.consulta("SELECT A.id_activo, codigo_activo, D.descripcion_mas, valor_compra, valor_depreciado, "
            + "round(valor_compra-valor_depreciado, 2) as precio_costo, case when tiene_iva then '~' else '' end, D.cantidad, "
            + "D.p_u, D.p_st, D.p_descuento, d.descuento, D.iva, D.total, D.p_iva "
            + "FROM (tbl_activo as A left outer join tbl_bodega_activo as BA on A.id_activo=BA.id_activo) "
            + "inner join tbl_factura_venta_detalle_activo as D on D.id_activo=A.id_activo "
            + "where D.id_factura_venta="+id);
    }

    public ResultSet getCobro(String idFC)
    {
        return this.consulta("SELECT * FROM tbl_factura_venta_cobro where id_factura_venta_cobro="+idFC);
    }

    public ResultSet getCobroComprobante(String idFC)
    {
        return this.consulta("SELECT VC.*, C.fecha_en_efectivo, C.num_cheque, C.banco, C.num_comp_pago, C.gastos_bancos, C.id_plan_cuenta_banco "
                + "FROM tbl_factura_venta_cobro as VC inner join tbl_comprobante_ingreso as C on VC.id_comprobante_ingreso=C.id_comprobante_ingreso "
                + "where VC.id_factura_venta="+idFC);
    }
    
    public ResultSet getPagos(String idD)
    {
        return this.consulta("SELECT * FROM vta_factura_venta_pago where id_comprobante_ingreso="+idD+" and factura_anulada=false and pago_anulado=false");
    }

    public static String[][] getCuentasDistintas(String mat[][])
    {
        String distintos[][]=null;
        int i=0;
        int pos=-1;
        while (mat[i][1] != null) {
            pos = Matriz.enMatriz(distintos, mat[i][8], 1);  
            if (pos == -1) {
                distintos = Matriz.poner(mat, new String[] {mat[i][8], "0", mat[i][3]});
            }else{
                distintos[pos][2] = String.valueOf( Double.parseDouble(distintos[pos][2]) + Double.parseDouble(mat[i][3]) );
            }
            i++;
        }
        return distintos;
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

    public boolean facturaDuplicada(String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_factura_venta where serie_factura='"+serie+"' and num_factura="+numero+" and anulado=false");
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

    public String concatenarValores(String id_articulos, String descripciones, String cantidades, String precios_costo, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales)
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
        String vecDescrip [] = descripciones.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"','"+vecDescrip[i]+"','"+vecPC[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
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
    
    public String insertar(int id_sucursal, int id_punto_emision, String id_cliente, String vendedor, String serie_factura, String num_factura, String autorizacion,
                String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco,
                String num_cheque, String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion,
                String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic,
                String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes,
                String ret_ejercicio_fiscal, String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado, 
                String id_plan_cuenta_anticipo, String idCliAnt, String monto_vajar)
    {
        String num = "-1";
        try{
            ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
            String paramMontosVajar = this.concatenarValores(idCliAnt, monto_vajar);
            ResultSet res = this.consulta("select facturaVenta("+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+vendedor+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', '"+fecha_emision+"', '"+direccion+
                    "', '"+telefono+"', '"+id_forma_pago+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+observacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_3+", "+descuento+
                    ", "+iva_2+", "+iva_3+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+
                    "', "+id_plan_cuenta_anticipo+", "+paramMontosVajar+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    public boolean actualizar(String id, int id_sucursal, int id_punto_emision, String id_cliente, String vendedor, String serie_factura, String num_factura, String autorizacion,
                String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String forma_pago, String banco,
                String num_cheque, String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion,
                String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic,
                String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes,
                String ret_ejercicio_fiscal, String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado)
    {
        boolean ok = false;
        try{
            ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
            ResultSet res = this.consulta("select facturaVentaActualizar("+id+", "+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+vendedor+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', '"+fecha_emision+"', '"+direccion+
                    "', '"+telefono+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+observacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_3+", "+descuento+
                    ", "+iva_2+", "+iva_3+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+"');");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public boolean contabilizar(String usuario)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select contabilizarVentas('"+usuario+"')");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean contabilizarFactura(String idFactura)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select contabilizarFactura("+idFactura+")");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean estaContabilizado(String usuario)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select count(*) from tbl_factura_venta where contabilizado=false and vendedor='"+usuario+"'");
            if(res.next()){
                int count = res.getString(1)!=null ? res.getInt(1) : 0;
                if(count == 0){
                    ok=true;
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    
    
    public boolean procesarXmlSri(String DOCS_ELECTRONICOS, String claveAcceso)
    {
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String claveCertificado = conf.getValor("clave_certificado");
        //String rutaArchivo = DOCS_ELECTRONICOS + "generados/";
        try {
            ResultSet rs = this.consulta("select id_factura_venta, documento_xml from tbl_factura_venta where estado_documento='p' and clave_acceso='"+claveAcceso+"'");
            if(rs.next()){
                String id_factura_venta = rs.getString("id_factura_venta")!=null ? rs.getString("id_factura_venta") : "";
                String cadenaXml = rs.getString("documento_xml")!=null ? rs.getString("documento_xml") : "";
                //if(this.guardarXml(rutaArchivo + claveAcceso + ".xml", cadenaXml)){
                    return this.firmarXml(id_factura_venta, DOCS_ELECTRONICOS, claveAcceso, cadenaXml, claveCertificado);
                //}
            }
            return true;
	} catch (Exception e) {
            e.printStackTrace();
	}
        return false;
    }
    
    
    public boolean procesarXmlSriTodos(String DOCS_ELECTRONICOS)
    {
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String claveCertificado = conf.getValor("clave_certificado");
        //String rutaArchivo = DOCS_ELECTRONICOS + "generados/";
        try {
            ResultSet rs = this.consulta("select id_factura_venta, clave_acceso, documento_xml from tbl_factura_venta where estado_documento='p' order by fecha_emision");
            while(rs.next()){
                String id_factura_venta = rs.getString("id_factura_venta")!=null ? rs.getString("id_factura_venta") : "";
                String claveAcceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                String cadenaXml = rs.getString("documento_xml")!=null ? rs.getString("documento_xml") : "";
                //this.guardarXml(rutaArchivo + claveAcceso + ".xml", cadenaXml);
                this.firmarXml(id_factura_venta, DOCS_ELECTRONICOS, claveAcceso, cadenaXml, claveCertificado);
            }
            return true;
	} catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
	}
        return false;
    }
    
    private boolean firmarXml(String idFactura, String DOCS_ELECTRONICOS, String claveAcceso, String cadenaXml, String claveCertificado)
    {
        String certificado = DOCS_ELECTRONICOS + "certificado.p12";
        String rutaSalida = DOCS_ELECTRONICOS + "firmados";
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
                    return this.ejecutar("update tbl_factura_venta set estado_documento='"+estadoDocumento+"', documento_xml='"+autorizacionXml+"' where id_factura_venta="+idFactura);
                }catch(Exception e){
                    e.printStackTrace();
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
    
    
    public boolean setEstadoDocumento(String id_factura_venta, String num_factura, String estado, String claveAcceso, String autorizacionXml, String mensaje)
    {
        return this.ejecutar("update tbl_factura_venta set num_factura="+num_factura+", estado_documento='"+estado
                +"', clave_acceso='"+claveAcceso+"', documento_xml='"+autorizacionXml+"', mensaje='"+
                mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")+"' where id_factura_venta="+id_factura_venta);
    }
    
    public boolean setEstadoDocumento(String id_factura_venta, String num_factura, String estado, String autorizacionXml, String mensaje)
    {
        return this.ejecutar("update tbl_factura_venta set num_factura="+num_factura+", estado_documento='"+estado+
                "', documento_xml='"+autorizacionXml+"', mensaje='"+mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")
                +"' where id_factura_venta="+id_factura_venta);
    }
 
    public boolean setEstadoDocumento(String id_factura_venta, String estado, String autorizacionXml, String mensaje)
    {
        return this.ejecutar("update tbl_factura_venta set estado_documento='"+estado+"', documento_xml='"+autorizacionXml
                +"', mensaje='"+mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")+"' where id_factura_venta="+id_factura_venta);
    }
    
    public boolean setClaveDocumento(String id_factura_venta, String estado, String claveAcceso, String autorizacionXml)
    {
        return this.ejecutar("update tbl_factura_venta set estado_documento='"+estado+"', clave_acceso='"+claveAcceso
                +"', documento_xml='"+autorizacionXml+"' where id_factura_venta="+id_factura_venta);
    }
    public boolean setClaveDocumento(String id_factura_venta, String estado, String claveAcceso, String autorizacionXml, String num_factura)
    {
        return this.ejecutar("update tbl_factura_venta set num_factura="+num_factura+", estado_documento='"+estado+"', clave_acceso='"+claveAcceso
                +"', documento_xml='"+autorizacionXml+"' where id_factura_venta="+id_factura_venta);
    }
        
    public boolean setDocumentoElectronico(String id_factura_venta, String estado, String claveAcceso, String autorizacionXml, String mensaje)
    {
        return this.ejecutar("update tbl_factura_venta set estado_documento='"+estado+"', clave_acceso='"+claveAcceso
                +"', documento_xml='"+autorizacionXml+"', mensaje='"+mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")
                +"' where id_factura_venta="+id_factura_venta);
    }
    
    public boolean fechaAnulacion(String idFactura)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_fechaAnulacion("+idFactura+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean claveDuplicada(String clave_acceso)
    {
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_factura_venta where clave_acceso='"+clave_acceso+"';");
            if(this.getFilas(res)>0){
                return true;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
    public String getClaveAcceso(String id_factura_venta)
    {
        String num = "0";
        try{
            ResultSet res = this.consulta("SELECT clave_acceso FROM tbl_factura_venta WHERE id_factura_venta="+id_factura_venta);
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "0";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean setClaveAcceso(String id_factura_venta, String claveAcceso)
    {
        return this.ejecutar("update tbl_factura_venta set clave_acceso='"+claveAcceso+"' where id_factura_venta="+id_factura_venta);
    }
    
    public boolean anular(String idFactura)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularFacturaVenta("+idFactura+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean anular(String idFactura, String por_edicion)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularFacturaVenta("+idFactura+", "+por_edicion+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public boolean getDeActivo(String id_factura_venta)
    {
        boolean de_activo = false;
        try{
            ResultSet res = this.consulta("SELECT de_activo FROM tbl_factura_venta WHERE id_factura_venta="+id_factura_venta);
            if(res.next()){
                de_activo = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return de_activo;
    }

    
    
    
    /*  ANTICIPOS  */
    
    public boolean registrarAnticipo(String id_cliente, String id_factura_venta, String usuario, String monto, String saldo)
    {
        return this.ejecutar("insert into tbl_cliente_anticipo(id_cliente, id_factura_venta, alias, monto, saldo) "
                + "values("+id_cliente+", "+id_factura_venta+", '"+usuario+"', "+monto+", "+saldo+")");
    }
    public ResultSet getAnticipos(String id_cliente)
    {
        return this.consulta("select * from vta_cliente_anticipo where saldo>0 and id_cliente="+id_cliente);
    }
    public ResultSet getAnticiposLiquidados(String id_factura)
    {
        return this.consulta("select num_comprobante, monto, monto_pago from vta_cliente_anticipo as A inner join tbl_cliente_anticipo_liquidado as AL on AL.id_cliente_anticipo=A.id_cliente_anticipo where AL.id_factura_venta="+id_factura);
    }
    /*public boolean setLiquidacionSaldoAnticipo(String id_sucursal, String id_cliente_anticipo, String id_factura_venta, String saldo)
    {
        boolean ok = false;
        if(Float.parseFloat(saldo)>0){
            //List sql = new ArrayList();
            //sql.add("update tbl_cliente_anticipo set saldo=saldo-"+saldo+" where id_cliente_anticipo="+id_cliente_anticipo);
            //sql.add("insert into tbl_cliente_anticipo_consumo(id_cliente_anticipo, id_factura_venta, monto) values("+id_cliente_anticipo+", "+id_factura_venta+", "+saldo+");");
            //return this.transacciones(sql);
            try{
                ResultSet rs = this.consulta("select proc_liquidar_anticipo("+id_sucursal+", "+id_cliente_anticipo+", "+id_factura_venta+", "+saldo+");");
                if(rs.next()){
                    long id_comprobante_diario = rs.getString(1)!=null ? rs.getLong(1) : -1;
                    if(id_comprobante_diario != -1){
                        ok=true;
                    }
                    rs.close();
                }
            }catch(Exception e){
                this.setError(e.getMessage());
            }
        }
        return ok;
    }*/
    /*public double[] getAnticiposSaldos(String id_cliente)
    {
        double valores[] = new double[]{0,0,0};
        try{
            ResultSet rs = this.consulta("select sum(subtotal) as subtotal, sum(iva) as iva, sum(total) as total "
                    + "from tbl_abono where id_prefactura="+id_prefactura);
            if(rs.next()){
                valores[0] = rs.getString("subtotal")!=null ? rs.getDouble("subtotal") : 0;
                valores[1] = rs.getString("iva")!=null ? rs.getDouble("iva") : 0;
                valores[2] = rs.getString("total")!=null ? rs.getDouble("total") : 0;
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return valores;
    }*/
    
    
    

    /* RETENCIONES  */


    public ResultSet getRetencion(String id_factura)
    {
        return this.consulta("select * from tbl_retencion_venta where anulado=false and id_factura_venta="+id_factura+";");
    }

    public ResultSet getRetenciones(int id_punto_emision, String desde, String hora)
    {
        return this.consulta("SELECT R.*, C.tipo_pago FROM tbl_retencion_venta as R inner join tbl_factura_venta_cobro as C on C.id_factura_venta=R.id_factura_venta "
                + "where R.id_punto_emision="+id_punto_emision+" and (fecha || ' ' || hora)::timestamp >= '"+desde+" "+hora+"'::timestamp and C.anulado=false and R.anulado=false");
    }

    public ResultSet getFacturaRetencion(String id_retencion)
    {
        return this.consulta("select * from vta_factura_venta_retencion where id_retencion_venta="+id_retencion+";");
    }

    public ResultSet getRetencionDetalle(String id_retencion)
    {
        return this.consulta("select TR.*, R.id_retencion_venta_detalle, R.id_retencion_venta, R.base_imponible, R.valor_retenido, BI.campo_valor " +
                "from (tbl_retencion_venta_detalle as R inner join tbl_tabla_impuesto as TR on TR.id_tabla_impuesto=R.id_tabla_impuesto) "
                + "inner join tbl_retencion_base_imponible as BI on BI.codigo=R.codigo " +
                "where R.id_retencion_venta="+id_retencion+";");
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

    public boolean insertarRetencion(String id_factura, int id_sucursal, int id_punto_emision, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision,
            String ret_ejercicio_fiscal, String ret_impuesto_retenido, String id_retenciones, String bases_imponibles, String valores_retenidos)
    {
        boolean ok = false;
        try{
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_retencionVenta("+id_factura+", "+id_sucursal+", "+id_punto_emision+", '"+
                    ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', '"+ret_fecha_emision+"', "+
                    ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRetencion+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public String getFechaPlazoRetenciones(String fecha_emision, String dia_cortes_creditos)
    {
        int anio = Fecha.datePart("anio", fecha_emision);
        int mes = Fecha.datePart("mes", fecha_emision);
        int dia = Fecha.datePart("dia", fecha_emision);
        int dia_mes = Fecha.getUltimoDiaMes(anio, mes);
        if( dia > (dia_mes-7) ){
            mes++;
            if(mes>12){
                mes = 1;
                anio++;
            }
            dia_mes = Integer.parseInt(dia_cortes_creditos);
            if(dia_mes==32){
                dia_mes = Fecha.getUltimoDiaMes(anio, mes);
            }
        }
        return anio + "-" + mes + "-" + dia_mes;
    }
    
    /*
    public boolean actualizarRetencion(String id_retencion_venta, String id_factura, String ret_num_serie, String ret_num_retencion, String ret_autorizacion,
            String ret_fecha_emision, String ret_ejercicio_fiscal, String ret_impuesto_retenido, String id_retenciones, String bases_imponibles, String valores_retenidos)
    {
        boolean ok = false;
        try{
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_editarRetencionVenta("+id_retencion_venta+", "+id_factura+", '"+ret_num_serie+"', '"+ret_num_retencion+
                "', '"+ret_autorizacion+"', '"+ret_fecha_emision+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRetencion+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }*/

    public boolean anularRetencion(String id_retencion)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularRetencionRecibida("+id_retencion+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

}

