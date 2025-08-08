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
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class PuntoEmision extends DataBase{
    public PuntoEmision(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getPuntoEmision(String id)
    {
        return this.consulta("SELECT * from tbl_punto_emision WHERE id_punto_emision="+id);
    }
    public ResultSet getPuntosEmision()
    {
        return this.consulta("SELECT id_punto_emision,punto_emision from vta_punto_emision order by punto_emision");
    }
    public ResultSet getPuntosEmision(int id_sucursal)
    {
        return this.consulta("SELECT id_punto_emision,punto_emision from vta_punto_emision where id_sucursal="+id_sucursal+" order by punto_emision");
    }
    public ResultSet getPuntosEmision(String id_sucursal)
    {
        return this.consulta("SELECT id_punto_emision,punto_emision,ip_computador, prepago,caja_virtual,id_plan_cuenta_caja from vta_punto_emision where id_sucursal="+id_sucursal+" order by punto_emision");
    }
    public String[] getIdPuntoEmision(String usuario_caja)
    {
        String datos[] = new String[]{"-1", "false"};
        try{
            ResultSet res = this.consulta("SELECT id_punto_emision, prepago FROM tbl_punto_emision WHERE usuario_caja = '"+usuario_caja+"'");
            if(res.next()){
                datos[0] = res.getString("id_punto_emision")!=null ? res.getString("id_punto_emision") : "-1";
                datos[1] = res.getString("prepago")!=null ? (res.getString("prepago").compareTo("t")==0 ? "true" : "false" ) : "false";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return datos;
    }
    public ResultSet getUsuariosIdPCPuntosPrepago()
    {
        return this.consulta("SELECT usuario_caja, id_plan_cuenta_caja, id_sucursal from tbl_punto_emision where prepago=true order by usuario_caja");
    }        
    public String[][] getPuntosEmisionVirtuales()
    {
        ResultSet rs = this.consulta("SELECT id_sucursal, id_punto_emision, usuario_caja, fac_num_serie from tbl_punto_emision where caja_virtual=true order by id_sucursal");
        try{
            /*filas*/
            rs.last();
            int fil = rs.getRow();
            rs.beforeFirst();
            /*columnas*/
            ResultSetMetaData mdata = rs.getMetaData();
            int col = mdata.getColumnCount();
            /*parsear*/
            String ma[][] = new String[fil][col+1];
            int i=0;
            int k=0;
            int j=1;
            while(rs.next()){
                for(j=1; j<=col; j++) {
                    k = j-1;
                    ma[i][k] = (rs.getString(j)!=null) ? rs.getString(j) : "";
                }
                try{
                    ResultSet res = this.consulta("SELECT case when max(num_factura) is null then 1 else max(num_factura)+1 end  FROM tbl_factura_venta WHERE id_punto_emision="+ma[i][1]);
                    if(res.next()){
                        ma[i][col] = (res.getString(1)!=null) ? res.getString(1) : "1";
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
                i++;
            }
            return ma;
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
        return null;
    }
    public boolean estaDuplicado(String id, String punto_emision)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_punto_emision where lower(punto_emision)='"+punto_emision.toLowerCase()+"' and id_punto_emision<>"+id);
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
    public boolean ipDuplicada(String id, String ip)
    {
        String vec_ip[] = ip.split("-");
        String w = "";
        for(int i=0; i<vec_ip.length; i++){
            w += "usuario_caja = '"+vec_ip[i].trim()+"' or ";
        }
        if(w.compareTo("")!=0){
            w = "(" + w.substring(0, w.length()-3) + ")";
        }
        ResultSet r = this.consulta("SELECT * FROM tbl_punto_emision WHERE "+w+" and id_punto_emision<>"+id);
        if(this.getFilas(r)>0){
            return true;
        }
        try{
            r.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public String getIdCaja(int id_punto_emision)
    {
        String id = "-1";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_caja FROM tbl_punto_emision WHERE id_punto_emision="+id_punto_emision);
            if(res.next()){
                id = res.getString(1)!=null ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public int getIdCajaVirtual()
    {
        int id = -1;
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_caja FROM tbl_punto_emision WHERE caja_virtual=true");
            if(res.next()){
                id = res.getString(1)!=null ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public String getIdCaja(String cajero)
    {
        String id = "-1";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_caja FROM tbl_punto_emision WHERE usuario_caja='"+cajero+"'");
            if(res.next()){
                id = res.getString(1)!=null ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public String getIdDiferenciaCaja(int id_punto_emision)
    {
        String id = "-1";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_diferencia_caja FROM tbl_punto_emision WHERE id_punto_emision="+id_punto_emision);
            if(res.next()){
                id = res.getString(1)!=null ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public String insertar(String id_sucursal, String punto_emision, String direccion_establecimiento, String usuario_caja, String id_plan_cuenta_caja, String id_plan_cuenta_diferencia_caja, 
            String fac_se, String fac_des, String fac_has, String fac_cad, String fac_aut, String num_fact_inicial, String emision_efectivo, String prepago,  
            String caja_virtual, String formato_hoja)
    {
        if(caja_virtual.toLowerCase().compareTo("true")==0){
            this.ejecutar("update tbl_punto_emision set caja_virtual=false where id_sucursal="+id_sucursal);
        }
        return this.insert("INSERT INTO tbl_punto_emision(id_sucursal, punto_emision, direccion_establecimiento, usuario_caja, id_plan_cuenta_caja, id_plan_cuenta_diferencia_caja, fac_num_serie, "
                + "fac_sec_desde, fac_sec_hasta, fac_cad_facturero, fac_autorizacion, num_fact_inicial, emision_efectivo, prepago, caja_virtual, formato_hoja) " +
                "VALUES("+id_sucursal+", '"+punto_emision+"', '"+direccion_establecimiento+"', '"+usuario_caja+"', "+id_plan_cuenta_caja+", "+id_plan_cuenta_diferencia_caja+", '"+fac_se+
                "', '"+fac_des+"', '"+fac_has+"', '"+fac_cad+"', '"+fac_aut+"', "+num_fact_inicial+", "+emision_efectivo+", "+prepago+", "+caja_virtual+", "
                + "'"+formato_hoja+"')");
    }
    public boolean actualizar(String id, String id_sucursal, String punto_emision, String direccion_establecimiento, String usuario_caja, String id_plan_cuenta_caja, String id_plan_cuenta_diferencia_caja, String fac_se, 
            String fac_des, String fac_has, String fac_cad, String fac_aut, String num_fact_inicial, String emision_efectivo, String prepago, String caja_virtual,
            String formato_hoja)
    {
        List sql = new ArrayList();
        if(caja_virtual.toLowerCase().compareTo("true")==0){
            sql.add("update tbl_punto_emision set caja_virtual=false where id_sucursal="+id_sucursal);
        }
        sql.add("UPDATE tbl_punto_emision SET punto_emision='"+punto_emision+"', direccion_establecimiento='"+direccion_establecimiento+"', usuario_caja='"+usuario_caja+"', id_plan_cuenta_caja='"+id_plan_cuenta_caja+"', id_plan_cuenta_diferencia_caja='"+
                id_plan_cuenta_diferencia_caja+"', fac_num_serie='"+fac_se+"', fac_sec_desde='"+fac_des+"', fac_sec_hasta='"+fac_has+"', fac_cad_facturero='"+fac_cad+"', fac_autorizacion='"+fac_aut+"', num_fact_inicial="+num_fact_inicial+
                ", emision_efectivo="+emision_efectivo+", prepago="+prepago+", caja_virtual="+caja_virtual+", formato_hoja='"+formato_hoja+"' WHERE id_punto_emision="+id);
        return this.transacciones(sql);
    }
    public boolean setFormatoValor(String id, String campo, String valor)
    {
        return this.ejecutar("UPDATE tbl_punto_emision SET "+campo+"='"+valor+"' WHERE id_punto_emision="+id);
    }
    public String tblPuntosEmision(String id_sucursal)
    {
        String html = "<TABLE cellspacing='0' cellpadding='0'><TR>" +
                        "<TH class='jm_TH' width='120'>P. DE EMISION</TH>" +
                        "<TH class='jm_TH' width='100'>USUARIO</TH>" +
                        "<TH class='jm_TH' width='70'>VIRTUAL</TH>" +
                        "<TH class='jm_TH' width='70'>PREPAGO</TH>" +
                        "<TH class='jm_TH' width='60'>SALDO</TH>" +
                        "<TH class='jm_TH' width='100'>FACTURAS</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
        html += "<DIV style='overflow:auto;width:600px;height:250px;' id='sll'>" +
        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblI'>";
        try{
            int i=0;
            ResultSet rsPuntos = this.getPuntosEmision(id_sucursal);
            while(rsPuntos.next()){
                String id_punto_emision = (rsPuntos.getString("id_punto_emision")!=null) ? rsPuntos.getString("id_punto_emision") : "";
                String punto_emision = (rsPuntos.getString("punto_emision")!=null) ? rsPuntos.getString("punto_emision") : "";
                String ip_computador = (rsPuntos.getString("ip_computador")!=null) ? rsPuntos.getString("ip_computador") : "";
                boolean caja_virtual = (rsPuntos.getString("caja_virtual")!=null) ? rsPuntos.getBoolean("caja_virtual") : false;
                boolean prepago = (rsPuntos.getString("prepago")!=null) ? rsPuntos.getBoolean("prepago") : false;
                //int id_plan_cuenta = (rsPuntos.getString("id_plan_cuenta_caja")!=null) ? rsPuntos.getInt("id_plan_cuenta_caja") : -1;
                float saldo_punto_emision = 0;
                
                html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar'>";
                html += "<td width='120' style='cursor:pointer' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+punto_emision+"</td>";
                html += "<td width='100' style='cursor:pointer' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+ip_computador+"</td>";
                html += "<td width='70' style='cursor:pointer;text-align:center' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+(caja_virtual ? "✔" : "")+"</td>";
                html += "<td width='70' style='cursor:pointer;text-align:center' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+(prepago ? "✔" : "")+"</td>";
                html += "<td width='60' style='cursor:pointer;text-align:right' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+(prepago ? saldo_punto_emision : "")+"</td>";
                html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";
                i++;
            }
        }catch(Exception e){e.printStackTrace();}

        html += "</table>";

        return html;
    }
}