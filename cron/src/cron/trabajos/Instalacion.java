/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;

/**
 *
 * @author sistemas
 */
public class Instalacion extends DataBase{
    public Instalacion(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    
    public void actualizarEstados(StringBuilder sql) 
    {
        if( sql.length() > 0 ) {
            
            ResultSet rs = this.consulta( sql.toString() );
            
            try{
                while ( rs.next() ) {
                    String paramConexion = rs.getString("conexion")!=null ? rs.getString("conexion") : "";
                    if( paramConexion.compareTo("")!=0 ) {

                        String idInstalacion = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "";
                        String cliente = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                        String ip = rs.getString("ip")!=null ? rs.getString("ip") : "";
                        String plan = rs.getString("plan")!=null ? rs.getString("plan") : "";
                        String burst_limit = rs.getString("burst_limit")!=null ? rs.getString("burst_limit") : "";
                        String max_limit = rs.getString("max_limit")!=null ? rs.getString("max_limit") : "";
                        String prioridad = rs.getString("prioridad")!=null ? rs.getString("prioridad") : "";
                        String estadoServicio = rs.getString("estado_servicio")!=null ? rs.getString("estado_servicio") : "activos";
                        String idMikrotikActivo = rs.getString("idMikrotikActivo")!=null ? rs.getString("idMikrotikActivo") : "";
                        String idMikrotikPlan = rs.getString("idMikrotikPlan")!=null ? rs.getString("idMikrotikPlan") : "";
                        String idMikrotikCola = rs.getString("idMikrotikCola")!=null ? rs.getString("idMikrotikCola") : "";

                        String vecConexion[] = paramConexion.split(",");

                        this.consulta("select setEstadoInstalacion("+idInstalacion+")");

                        Mikrotik objMikrotik = new Mikrotik( vecConexion[0], vecConexion[1], vecConexion[2]);
                        objMikrotik.actualizarInstalacionEnServidor( this, idInstalacion, cliente, ip, plan, burst_limit, 
                                    max_limit, prioridad, estadoServicio, idMikrotikActivo, idMikrotikPlan, idMikrotikCola);
                        objMikrotik.cerrar();
                    }
                }
                rs.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            
        }
        
    }
}
