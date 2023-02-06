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

public class Sucursal extends DataBase{
    public Sucursal(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getSucursal(String id)
    {
        return this.consulta("SELECT * from tbl_sucursal WHERE id_sucursal="+id);
    }
    public ResultSet getSucursales()
    {
        return this.consulta("SELECT id_sucursal,sucursal from vta_sucursal where estado=true");
    }
    public ResultSet getCorreos()
    {
        return this.consulta("SELECT email,empleado from vta_empleado");
    }
    public ResultSet getDirecciones()
    {
        return this.consulta("SELECT id_sucursal,ubicacion from vta_sucursal");
    }
    public String getNombre(String id)
    {
        String ubicacion = "Empresa";
        if(id.compareTo("-0")!=0){
            try{
                ResultSet r = this.consulta("SELECT * FROM tbl_sucursal where id_sucursal="+id);
                if(r.next()){
                    ubicacion = (r.getString("sucursal")!=null) ? r.getString("sucursal") : "Empresa";
                    r.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return ubicacion;
    }
    public String getCiudad(int id)
    {
        String ciudad = "Ibarra";
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_sucursal where id_sucursal="+id);
            if(r.next()){
                ciudad = (r.getString("ciudad")!=null) ? r.getString("ciudad") : "Empresa";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ciudad;
    }
    
    public String getCiudadesJSON()
    {
        ResultSet rs = this.consulta("select id_sucursal, ciudad from tbl_sucursal order by id_sucursal;");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }
    public String getDireccion(String id)
    {
        String ubicacion = "";
        if(id.compareTo("-0")!=0){
            try{
                ResultSet r = this.consulta("SELECT S.ubicacion, P.direccion_establecimiento FROM tbl_sucursal as S inner join tbl_punto_emision as P on S.id_sucursal=P.id_sucursal "
                        + "where P.id_punto_emision="+id);
                if(r.next()){
                    ubicacion = (r.getString("ubicacion")!=null) ? r.getString("ubicacion") : "";
                    String direccion_establecimiento = (r.getString("direccion_establecimiento")!=null) ? r.getString("direccion_establecimiento") : "DIRECCION DE LA SUCURSAL";
                    
                    if(direccion_establecimiento.compareTo("DIRECCION DE LA SUCURSAL")!=0){
                        ubicacion = direccion_establecimiento;
                    }
                    r.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return ubicacion;
    }
    public String getIPServidor(String ip)
    {
        ip = ip.substring(0, ip.lastIndexOf("."));
        String ip_svr = "";
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_sucursal where ips_red like '%"+ip+"%'");
            if(r.next()){
                ip_svr = (r.getString("ip_servidor")!=null) ? r.getString("ip_servidor") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ip_svr;
    }
    public int get_Id_Sucursal(String ip)
    {
        ip = ip.substring(0, ip.lastIndexOf("."));
        int id_sucursal = -1;
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_sucursal where ips_red like '%"+ip+"%'");
            if(r.next()){
                id_sucursal = (r.getString("id_sucursal")!=null) ? r.getInt("id_sucursal") : -1;
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_sucursal;
    }
    public String[][] getIPs(int id)
    {
        try{
            ResultSet rs = this.consulta("SELECT ips_computador FROM tbl_sucursal where id_sucursal="+id);
            if(rs.next()){
                 String ips_computador = (rs.getString("ips_computador")!=null) ? rs.getString("ips_computador") : "";
                 String vec[] = ips_computador.trim().split("-");
                 int fil = vec.length;
                 String mat[][] = new String[fil][2];
                 String aux = "";
                 for(int i=0; i<fil; i++){
                     aux = vec[i].trim();
                     if(aux.compareTo("")!=0){
                        mat[i][0] = mat[i][1] = aux;
                     }
                 }
                 return mat;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean estaDuplicado(String id, String sucursal)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_sucursal where lower(sucursal)='"+sucursal.toLowerCase()+"' and id_sucursal<>"+id);
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
            w += "ips_computador like '%"+vec_ip[i].trim()+"%' or ";
        }
        if(w.compareTo("")!=0){
            w = "(" + w.substring(0, w.length()-3) + ")";
        }
        ResultSet r = this.consulta("SELECT * FROM tbl_sucursal WHERE "+w+" and id_sucursal<>"+id);
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
    public String getIdCajaChica(int id_sucursal)
    {
        String id = "-1";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_caja_chica FROM tbl_sucursal WHERE id_sucursal="+id_sucursal);
            if(res.next()){
                id = res.getString(1)!=null ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public int getIdSucursal(String ip)
    {
        int id = -1;
        try{
            ResultSet res = this.consulta("SELECT id_sucursal FROM tbl_sucursal WHERE ips_computador like '%"+ip+"%'");
            if(res.next()){
                id = res.getString(1)!=null ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public int getIdSucursal(String ip, String usuario)
    {
        int id = -1;
        try{
            ResultSet res = this.consulta("SELECT id_rol FROM tbl_usuario WHERE alias='"+usuario+"' and autenticacion_ip_bio like '%"+ip+"%'");
            if(res.next()){
                id = res.getString(1)!=null ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
//    public String insertar(String suc, String ubi, String ciudad, String id_bodega_cliente, String ip, String ips_red, String ip_servidor, String ip_inicio, String fondo_caja_chica,
//            String min_caja_chica, String id_pc_caja_chica, String id_responsable_caja_chica, String servidor_ip, String servidor_puerto, String servidor_db, String liq_se, String liq_des,
//            String liq_has, String liq_cad, String liq_aut, String ret_se, String ret_des, String ret_has, String ret_cad,
//            String ret_aut, String not_cre_num_serie, String not_cre_sec_desde, String not_cre_sec_hasta, String not_cre_cad_libretin,
//            String not_cre_autorizacion, String not_deb_num_serie, String not_deb_sec_desde, String not_deb_sec_hasta,
//            String not_deb_cad_libretin, String not_deb_autorizacion, String estado)
//    {
//        liq_cad = liq_cad.compareTo("")!=0 ? "'"+liq_cad+"'" : "NULL";
//        ret_cad = ret_cad.compareTo("")!=0 ? "'"+ret_cad+"'" : "NULL";
//        not_cre_cad_libretin = not_cre_cad_libretin.compareTo("")!=0 ? "'"+not_cre_cad_libretin+"'" : "NULL";
//        not_deb_cad_libretin = not_deb_cad_libretin.compareTo("")!=0 ? "'"+not_deb_cad_libretin+"'" : "NULL";
//        return this.insert("INSERT INTO tbl_sucursal(sucursal,ubicacion,ciudad,id_bodega_cliente,ips_computador,ips_red,ip_servidor,ip_inicio,fondo_caja_chica"
//                + ",min_caja_chica,id_plan_cuenta_caja_chica,id_responsable_caja_chica,servidor_ip,servidor_puerto,servidor_db,liq_num_serie,liq_sec_desde,"
//                + "liq_sec_hasta,liq_cad_libretin,liq_autorizacion,ret_num_serie,ret_sec_desde,ret_sec_hasta,"
//                + "ret_cad_libretin,ret_autorizacion,not_cre_num_serie,not_cre_sec_desde,not_cre_sec_hasta,"
//                + "not_cre_cad_libretin,not_cre_autorizacion,not_deb_num_serie,not_deb_sec_desde,not_deb_sec_hasta,"
//                + "not_deb_cad_libretin,not_deb_autorizacion,estado) VALUES(" +
//                "'"+suc+"', '"+ubi+"', '"+ciudad+"', "+id_bodega_cliente+", '"+ip+"', '"+ips_red+"', '"+ip_servidor+"', '"+ip_inicio+"', "+fondo_caja_chica+", "+min_caja_chica+
//                ", "+id_pc_caja_chica+", "+id_responsable_caja_chica+", '"+servidor_ip+"', '"+servidor_puerto+"', '"+servidor_db+"', '"+liq_se+"', '"+liq_des+"', '"+liq_has+
//                "', "+liq_cad+", '"+liq_aut+"', '"+ret_se+"', '"+ret_des+"', '"+ret_has+"', "+ret_cad+", '"+ret_aut+
//                "', '"+not_cre_num_serie+"', '"+not_cre_sec_desde+"', '"+not_cre_sec_hasta+"', "+not_cre_cad_libretin+
//                ", '"+not_cre_autorizacion+"', '"+not_deb_num_serie+"', '"+not_deb_sec_desde+"', '"+not_deb_sec_hasta+
//                "', "+not_deb_cad_libretin+", '"+not_deb_autorizacion+"', "+estado+");");
//    }
//    public boolean actualizar(String id, String suc, String ubi, String ciudad, String id_bodega_cliente, String ip, String ips_red, String ip_servidor, String ip_inicio,
//            String fondo_caja_chica, String min_caja_chica, String id_pc_caja_chica,String id_responsable_caja_chica, String servidor_ip, String servidor_puerto, String servidor_db, 
//            String liq_se, String liq_des, String liq_has, String liq_cad, String liq_aut, String ret_se, String ret_des,
//            String ret_has, String ret_cad, String ret_aut, String not_cre_num_serie, String not_cre_sec_desde,
//            String not_cre_sec_hasta, String not_cre_cad_libretin, String not_cre_autorizacion, String not_deb_num_serie,
//            String not_deb_sec_desde, String not_deb_sec_hasta, String not_deb_cad_libretin, String not_deb_autorizacion, String estado)
//    {
//        liq_cad = liq_cad.compareTo("")!=0 ? "'"+liq_cad+"'" : "NULL";
//        ret_cad = ret_cad.compareTo("")!=0 ? "'"+ret_cad+"'" : "NULL";
//        not_cre_cad_libretin = not_cre_cad_libretin.compareTo("")!=0 ? "'"+not_cre_cad_libretin+"'" : "NULL";
//        not_deb_cad_libretin = not_deb_cad_libretin.compareTo("")!=0 ? "'"+not_deb_cad_libretin+"'" : "NULL";
//        return this.ejecutar("UPDATE tbl_sucursal SET sucursal='"+suc+"', ubicacion='"+ubi+"', ciudad='"+ciudad+"', id_bodega_cliente="+id_bodega_cliente+", ips_computador='"+ip+
//                "', ips_red='"+ips_red+"', ip_servidor='"+ip_servidor+"', ip_inicio='"+ip_inicio+"', fondo_caja_chica="+fondo_caja_chica+", min_caja_chica="+min_caja_chica+", id_plan_cuenta_caja_chica="+id_pc_caja_chica+
//                ", id_responsable_caja_chica="+id_responsable_caja_chica+",servidor_ip='"+servidor_ip+"', servidor_puerto='"+servidor_puerto+"', servidor_db='"+servidor_db+"', liq_num_serie='"+liq_se+"', "
//                + "liq_sec_desde='"+liq_des+"', liq_sec_hasta='"+liq_has+"', liq_cad_libretin="+liq_cad+", liq_autorizacion='"+liq_aut+"', "
//                + "ret_num_serie='"+ret_se+"', ret_sec_desde='"+ret_des+"', ret_sec_hasta='"+ret_has+"', ret_cad_libretin="+ret_cad+", "
//                + "ret_autorizacion='"+ret_aut+"', not_cre_num_serie='"+not_cre_num_serie+"', not_cre_sec_desde='"+not_cre_sec_desde
//                + "', not_cre_sec_hasta='"+not_cre_sec_hasta+"', not_cre_cad_libretin="+not_cre_cad_libretin+", not_cre_autorizacion='"+
//                not_cre_autorizacion+"', not_deb_num_serie='"+not_deb_num_serie+"', not_deb_sec_desde='"+not_deb_sec_desde+
//                "', not_deb_sec_hasta='"+not_deb_sec_hasta+"', not_deb_cad_libretin="+not_deb_cad_libretin+", not_deb_autorizacion='"+
//                not_deb_autorizacion+"', estado="+estado+" WHERE id_sucursal="+id);
//    }
    /*public String insertar(String suc, String ubi, String ciudad, String id_bodega_cliente, String ip, String ip_inicio, String fondo_caja_chica,
            String min_caja_chica, String id_pc_caja_chica, String id_responsable_caja_chica, String id_plan_cuenta_gasto, String liq_se, String liq_des,
            String liq_has, String liq_cad, String liq_aut, String ret_se, String ret_des, String ret_has, String ret_cad, 
            String ret_aut, String not_cre_num_serie, String not_cre_sec_desde, String not_cre_sec_hasta, String not_cre_cad_libretin,
            String not_cre_autorizacion, String not_deb_num_serie, String not_deb_sec_desde, String not_deb_sec_hasta,
            String not_deb_cad_libretin, String not_deb_autorizacion, String estado)
    {
        liq_cad = liq_cad.compareTo("")!=0 ? "'"+liq_cad+"'" : "NULL";
        ret_cad = ret_cad.compareTo("")!=0 ? "'"+ret_cad+"'" : "NULL";
        not_cre_cad_libretin = not_cre_cad_libretin.compareTo("")!=0 ? "'"+not_cre_cad_libretin+"'" : "NULL";
        not_deb_cad_libretin = not_deb_cad_libretin.compareTo("")!=0 ? "'"+not_deb_cad_libretin+"'" : "NULL";
        return this.insert("INSERT INTO tbl_sucursal(sucursal,ubicacion,ciudad,id_bodega_cliente,ips_computador,ip_inicio,fondo_caja_chica"
                + ",min_caja_chica,id_plan_cuenta_caja_chica,id_responsable_caja_chica,id_plan_cuenta_gasto,liq_num_serie,liq_sec_desde,"
                + "liq_sec_hasta,liq_cad_libretin,liq_autorizacion,ret_num_serie,ret_sec_desde,ret_sec_hasta,"
                + "ret_cad_libretin,ret_autorizacion,not_cre_num_serie,not_cre_sec_desde,not_cre_sec_hasta,"
                + "not_cre_cad_libretin,not_cre_autorizacion,not_deb_num_serie,not_deb_sec_desde,not_deb_sec_hasta,"
                + "not_deb_cad_libretin,not_deb_autorizacion,estado) VALUES(" +
                "'"+suc+"', '"+ubi+"', '"+ciudad+"', "+id_bodega_cliente+", '"+ip+"', '"+ip_inicio+"', "+fondo_caja_chica+", "+min_caja_chica+
                ", "+id_pc_caja_chica+", "+id_responsable_caja_chica+", "+id_plan_cuenta_gasto+", '"+liq_se+"', '"+liq_des+"', '"+liq_has+
                "', "+liq_cad+", '"+liq_aut+"', '"+ret_se+"', '"+ret_des+"', '"+ret_has+"', "+ret_cad+", '"+ret_aut+
                "', '"+not_cre_num_serie+"', '"+not_cre_sec_desde+"', '"+not_cre_sec_hasta+"', "+not_cre_cad_libretin+
                ", '"+not_cre_autorizacion+"', '"+not_deb_num_serie+"', '"+not_deb_sec_desde+"', '"+not_deb_sec_hasta+
                "', "+not_deb_cad_libretin+", '"+not_deb_autorizacion+"', "+estado+");");
    }
    public boolean actualizar(String id, String suc, String ubi, String ciudad, String id_bodega_cliente, String ip, String ip_inicio,
            String fondo_caja_chica, String min_caja_chica, String id_pc_caja_chica,String id_responsable_caja_chica, String id_plan_cuenta_gasto, 
            String liq_se, String liq_des, String liq_has, String liq_cad, String liq_aut, String ret_se, String ret_des,
            String ret_has, String ret_cad, String ret_aut, String not_cre_num_serie, String not_cre_sec_desde,
            String not_cre_sec_hasta, String not_cre_cad_libretin, String not_cre_autorizacion, String not_deb_num_serie,
            String not_deb_sec_desde, String not_deb_sec_hasta, String not_deb_cad_libretin, String not_deb_autorizacion, String estado)
    {
        liq_cad = liq_cad.compareTo("")!=0 ? "'"+liq_cad+"'" : "NULL";
        ret_cad = ret_cad.compareTo("")!=0 ? "'"+ret_cad+"'" : "NULL";
        not_cre_cad_libretin = not_cre_cad_libretin.compareTo("")!=0 ? "'"+not_cre_cad_libretin+"'" : "NULL";
        not_deb_cad_libretin = not_deb_cad_libretin.compareTo("")!=0 ? "'"+not_deb_cad_libretin+"'" : "NULL";
        return this.ejecutar("UPDATE tbl_sucursal SET sucursal='"+suc+"', ubicacion='"+ubi+"', ciudad='"+ciudad+"', id_bodega_cliente="+id_bodega_cliente+", ips_computador='"+ip+
                "', ip_inicio='"+ip_inicio+"', fondo_caja_chica="+fondo_caja_chica+", min_caja_chica="+min_caja_chica+", id_plan_cuenta_caja_chica="+id_pc_caja_chica+
                ", id_responsable_caja_chica="+id_responsable_caja_chica+", id_plan_cuenta_gasto="+id_plan_cuenta_gasto+", liq_num_serie='"+liq_se+"', "
                + "liq_sec_desde='"+liq_des+"', liq_sec_hasta='"+liq_has+"', liq_cad_libretin="+liq_cad+", liq_autorizacion='"+liq_aut+"', "
                + "ret_num_serie='"+ret_se+"', ret_sec_desde='"+ret_des+"', ret_sec_hasta='"+ret_has+"', ret_cad_libretin="+ret_cad+", "
                + "ret_autorizacion='"+ret_aut+"', not_cre_num_serie='"+not_cre_num_serie+"', not_cre_sec_desde='"+not_cre_sec_desde
                + "', not_cre_sec_hasta='"+not_cre_sec_hasta+"', not_cre_cad_libretin="+not_cre_cad_libretin+", not_cre_autorizacion='"+
                not_cre_autorizacion+"', not_deb_num_serie='"+not_deb_num_serie+"', not_deb_sec_desde='"+not_deb_sec_desde+
                "', not_deb_sec_hasta='"+not_deb_sec_hasta+"', not_deb_cad_libretin="+not_deb_cad_libretin+", not_deb_autorizacion='"+
                not_deb_autorizacion+"', estado="+estado+" WHERE id_sucursal="+id);
    }*/
    public boolean setFormatoValor(String id, String campo, String valor)
    {
        return this.ejecutar("UPDATE tbl_sucursal SET "+campo+"='"+valor+"' WHERE id_sucursal="+id);
    }



    public ResultSet getResponsablesCajas()
    {
        return this.consulta("SELECT distinct cajero,cajero as cajero1 from tbl_arqueo_caja where anulado=false order by cajero");
    }
    public ResultSet getVendedores()
    {
        return this.consulta("SELECT distinct vendedor, vendedor as vendedor1 from tbl_factura_venta order by vendedor");
    }
    public boolean enResponsablesCajas(String usuario)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_punto_emision where usuario_caja='"+usuario+"'");
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
}