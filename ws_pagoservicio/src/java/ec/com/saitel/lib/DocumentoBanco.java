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
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class DocumentoBanco extends DataBase{
    public DocumentoBanco(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public float getDocumentoBanco(String num_doc)
    {
        float saldo = 0;
        try{
            ResultSet res = this.consulta("SELECT monto FROM tbl_documento_banco_tmp where documento='"+num_doc+"' and '"+num_doc+"' not in (select num_documento from tbl_arqueo_caja_documento_cierre)");
            if(res.next()){
                saldo = (res.getString(1)!=null) ? res.getFloat(1) : 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return saldo;
    }
    
    public float getSaldoDocumento(String num_doc)
    {
        float saldo = 0;
        try{
            ResultSet res = this.consulta("SELECT saldo FROM tbl_documento_banco where num_documento='"+num_doc+"' and saldo>0");
            if(res.next()){
                saldo = (res.getString(1)!=null) ? res.getFloat(1) : 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return saldo;
    }

    public float getDebitosBanco(String doc, String cuenta)
    {
        float saldo = 0;

        /*int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        String ini = anio + "-" + mes + "-01";
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);*/
        try{
            ResultSet res = this.consulta("SELECT valor FROM tbl_documento_banco_debito where item='"+doc+"' and cuenta='"+cuenta+"'");
            if(res.next()){
                saldo = (res.getString(1)!=null) ? res.getFloat(1) : 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return saldo;
    }

    public boolean hayDocumento(String num_doc)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_documento_banco where num_documento='"+num_doc+"' and saldo>=0 ");
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

    public ResultSet getPagosDocumentoBanco(String num_doc)
    {
        int anio = Fecha.getAnio();
        return this.consulta("select * from tbl_comprobante_ingreso where num_comp_pago='"+num_doc+"' and fecha_proceso between '"+anio+"-01-01' and '"+anio+"-12-31' order by id_comprobante_ingreso;");
    }
    
    public ResultSet getUtilizacionDocumentoBanco(String num_doc)
    {
        return this.consulta("select cajero, num_comprobante::varchar as numero, fecha_proceso as fecha_emision, total, 'comprobante ingreso' as documento, anulado from tbl_comprobante_ingreso where num_comp_pago = '"+num_doc+"' " +
"union " +
"select vendedor as cajero, serie_factura || '-' || num_factura as numero, fecha_emision, total, 'Factura venta' as documento, anulado from tbl_factura_venta where num_comp_pago = '"+num_doc+"' " +
"union " +
"select A.cajero, A.num_documento::varchar as numero, A.fecha as fecha_emision, C.monto as total, 'Arqueo caja' as documento, A.anulado from tbl_arqueo_caja_documento_cierre as C inner join tbl_arqueo_caja as A on A.id_arqueo_caja=C.id_arqueo_caja where C.num_documento='"+num_doc+"' "
        + "order by fecha_emision desc");
    }
    
    public boolean insertar(String usuario, String num_documento, String monto)
    {
        return this.ejecutar("INSERT INTO tbl_documento_banco(usuario, num_documento, monto, saldo)"
                + " VALUES('"+usuario+"', '"+num_documento+"', "+monto+", "+monto+")");
    }

    public boolean insertar(String usuario, String num_documento, String monto, double saldo)
    {
        return this.ejecutar("INSERT INTO tbl_documento_banco(usuario, num_documento, monto, saldo)"
                + " VALUES('"+usuario+"', '"+num_documento+"', "+monto+", "+saldo+")");
    }
    
    public boolean actualizar(String num_doc, String saldo)
    {
        return this.ejecutar("UPDATE tbl_documento_banco SET saldo=saldo-"+saldo+" WHERE num_documento='"+num_doc+"'");
    }
    public boolean actualizarSaldo(String num_doc, String saldo)
    {
        return this.ejecutar("UPDATE tbl_documento_banco SET saldo=saldo+"+saldo+" WHERE num_documento='"+num_doc+"'");
    }

    /* DEBITOS */

    public boolean hayCuenta(String item, String cuenta)
    {
        boolean ok = false;
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        String ini = anio + "-" + mes + "-01";
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_documento_banco_debito where item='"+item+"' and cuenta='"+cuenta+"' and fecha between '"+ini+"' and '"+fin+"'");
            if(this.getFilas(res)>0){
                ok = true;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }


    /*   TRANSACCIONES   -    MOVIMIENTOS   */


    public ResultSet getMovDocumentoBanco(String id)
    {
        return this.consulta("select * from tbl_documento_banco_tmp where id_documento_banco_tmp="+id);
    }

    public boolean estaDuplicado(String id, String documento)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_documento_banco_tmp where documento='"+documento+"' and id_documento_banco_tmp<>"+id);
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

    public boolean insertarMov(String fecha, String concepto, String documento, String monto, String de_banco)
    {
        return this.ejecutar("INSERT INTO tbl_documento_banco_tmp(fecha, concepto, documento, monto, de_banco)"
                + " VALUES('"+fecha+"', '"+concepto+"', '"+documento+"', "+monto+", "+de_banco+")");
    }

    public boolean actualizarMov(String id, String fecha, String concepto, String documento, String monto)
    {
        return this.ejecutar("update tbl_documento_banco_tmp set fecha='"+fecha+"', concepto='"+concepto+
                "', documento='"+documento+"', monto="+monto+" where id_documento_banco_tmp="+id);
    }

}