/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author sistemas
 */
public class FinMes implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException 
    {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        String modoSincronizacionMikrotiks = "scripts";
        try{
            ResultSet rs = objDataBase.consulta("select parametro, lower(valor) as valor from tbl_configuracion where parametro in('modoSincronizacionMikrotiks')");
            while(rs.next()){
                String parametro = rs.getString("parametro")!=null ? rs.getString("parametro") : "";

                if( parametro.compareTo("modoSincronizacionMikrotiks") == 0) {
                    modoSincronizacionMikrotiks = rs.getString("valor")!=null ? rs.getString("valor") : "scripts";
                }
            }
            rs.close();
        }catch(Exception e){
            System.out.println("Error obteniendo configuracion " + e.getMessage() );
            e.printStackTrace();
        }
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de ejecucion de depreciaciones.");
        objDataBase.consulta("select proc_ejecutardepreciaciones();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de ejecucion de depreciaciones");

        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de ejecucion de depreciaciones revalorizadas.");
        objDataBase.consulta("select proc_ejecutardepreciacionesrevalorizadas();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de ejecucion de depreciaciones revalorizadas");
        
        
        
        
        
        /*  terminar y saldar o equipos devueltos a instalaciones de morosos mayores a 1 año */
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando desinstalacion y baja de activos a clientes morosos + de 12 meses");
        try{
            
            objDataBase.consulta("select proc_terminarInstalacionVajarActivosMorosos();");
            
            if( modoSincronizacionMikrotiks.compareTo("apis") == 0 ) {
                
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT distinct I.id_sucursal, I.id_instalacion, razon_social || ' ' || id_instalacion as razon_social, ip::varchar, P.burst_limit, ");
                sql.append("	I.plan, P.max_limit, case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, estado_servicio, ");
                sql.append("	idMikrotikActivo, idMikrotikPlan, idMikrotikCola, ");
                sql.append("	(select servidor || ',' || usuario || ',' || clave || ',' || puerto as conexion from tbl_servidor_ftp as S where S.estado and S.id_sucursal=I.id_sucursal and position( regexp_replace(I.ip::varchar, '\\d*[/]\\d*', '') in subredes)>0 and id_servidor_ftp<>35 limit 1) ");
                sql.append("FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio ");
                sql.append("where I.id_instalacion in( select id_instalacion from tbl_instalacion_historial_estado where fecha=now()::date and hora between '00:01:00' and now()::time )");

                Instalacion objInstalacion = new Instalacion( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
                objInstalacion.actualizarEstados(sql);
                objInstalacion.cerrar();
                
            }
                                
        } finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización desinstalacion y baja de activos a clientes morosos + de 12 meses");
        }
        
        
        
        objDataBase.cerrar();
    }
    
}
