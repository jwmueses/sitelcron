/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.utils;
import ec.com.saitel.gomax.dao.BaseDatos;
import java.sql.ResultSet;

public class Iva extends BaseDatos
{
    public Iva(){
        super();
    }
    
    public Iva(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public boolean estaDuplicado(String id, String codigo)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_iva where codigo='"+codigo+"' and id_iva<>"+id);
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
    public String getConcepto(String id){
        String concepto = "";
        try{
            ResultSet rs = this.consulta("SELECT * from tbl_iva where id_iva="+id);
            if(rs.next()){
                concepto = (rs.getString("concepto")!=null) ? rs.getString("concepto") : "";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return concepto;
    }
    public ResultSet getIva(String id)
    {
        return this.consulta("SELECT * from tbl_iva where id_iva="+id);
    }
    
    public String getIva(String id, String cuenta)
    {
        String id_cuenta = "";
        try{
            ResultSet rs = this.consulta("SELECT "+cuenta+" from tbl_iva where id_iva="+id+"");
            if(rs.next()){
                id_cuenta = rs.getString(1)!=null ? rs.getString(1) : "0";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_cuenta;
    }
    
    public String getMaxPIva()
    {
        String piva = "0";
        try{
            ResultSet rs = this.consulta("SELECT max(porcentaje) from tbl_iva");
            if(rs.next()){
                piva = rs.getString(1)!=null ? rs.getString(1) : "0";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return piva;
    }
    
    public String getIdPCIva(String porcentaje, String cuenta)
    {
        String id_cuenta = "";
        try{
            ResultSet rs = this.consulta("SELECT "+cuenta+" from tbl_iva where porcentaje="+porcentaje);
            if(rs.next()){
                id_cuenta = rs.getString(1)!=null ? rs.getString(1) : "0";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_cuenta;
    }
    
    public ResultSet getIvas()
    {
        return this.consulta("SELECT id_iva,concepto from tbl_iva;");
    }
    
    public ResultSet getCodigosIva()
    {
        return this.consulta("SELECT porcentaje, codigo from tbl_iva order by porcentaje;");
    }
    
    public ResultSet getIvasActivos()
    {
        return this.consulta("SELECT porcentaje,concepto from tbl_iva order by porcentaje desc;");
    }
    public boolean insertar(String codigo, String concepto, String porcentaje, String id_plan_cuenta_comp_bien, String id_plan_cuenta_comp_servicio, 
            String id_plan_cuenta_venta_bien, String id_plan_cuenta_venta_servicio, String id_plan_cuenta_venta_activo)
    {
        return this.ejecutar("INSERT INTO tbl_iva(codigo, concepto, porcentaje, id_plan_cuenta_comp_bien, id_plan_cuenta_comp_servicio, id_plan_cuenta_venta_bien, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_activo) "
                + "VALUES('"+codigo+"', '"+concepto+"', "+porcentaje+", "+id_plan_cuenta_comp_bien+", "+id_plan_cuenta_comp_servicio+", "+id_plan_cuenta_venta_bien+", "+id_plan_cuenta_venta_servicio+", "+id_plan_cuenta_venta_activo+")");
    }
    public boolean actualizar(String id, String codigo, String concepto, String porcentaje, String id_plan_cuenta_comp_bien, 
            String id_plan_cuenta_comp_servicio, String id_plan_cuenta_venta_bien, String id_plan_cuenta_venta_servicio, String id_plan_cuenta_venta_activo)
    {
        return this.ejecutar("UPDATE tbl_iva SET codigo='"+codigo+"', concepto='"+concepto+"', porcentaje="+porcentaje+", "
                + "id_plan_cuenta_comp_bien="+id_plan_cuenta_comp_bien+", id_plan_cuenta_comp_servicio="+id_plan_cuenta_comp_servicio+
                ", id_plan_cuenta_venta_bien="+id_plan_cuenta_venta_bien+", id_plan_cuenta_venta_servicio="+id_plan_cuenta_venta_servicio+
                ", id_plan_cuenta_venta_activo="+id_plan_cuenta_venta_activo+"  WHERE id_iva="+id);
    }
    public boolean eliminar(String id)
    {
        return this.ejecutar("delete from tbl_iva WHERE id_iva="+id);
    }
    
    public String getCodigoIva(String porcentajeIva)
    {
        String codigo = "2";
        try{
            ResultSet rs = this.consulta("SELECT codigo from tbl_iva where porcentaje="+porcentajeIva);
            if(rs.next()){
                codigo = rs.getString(1)!=null ? rs.getString(1) : "2";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return codigo;
    }
    
}
