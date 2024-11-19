/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge
 */
public class decimoCuarto implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
                
        // Decimo Cuarto Sueldo
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando cálculos para el Decimo Cuarto Sueldo");
        try{
            ResultSet rsEmpleados = objDataBase.consulta("select distinct id_empleado, empleado, ((select extract(year from date 'now()')-1)||'-08-01') as fecha_inicial,(select extract(year from date 'now()')||'-07-31') as fecha_final from vta_rol_pagos \n" +
            "where fecha_inicial>=((select extract(year from date 'now()')-1)||'-08-01')::timestamp and fecha_final<=(select extract(year from date 'now()')||'-07-31')::timestamp \n" +
            "order by id_empleado;");
            while(rsEmpleados.next()){
               String id_empleado= (rsEmpleados.getString("id_empleado")!=null) ? rsEmpleados.getString("id_empleado") : "";
               String empleado= (rsEmpleados.getString("empleado")!=null) ? rsEmpleados.getString("empleado") : "";
               String fi= (rsEmpleados.getString("fecha_inicial")!=null) ? rsEmpleados.getString("fecha_inicial") : "";
               String ff= (rsEmpleados.getString("fecha_final")!=null) ? rsEmpleados.getString("fecha_final") : "";
               
               try{
                   String idDecimo=objDataBase.insert("insert into tbl_decimos (id_tipo, id_empleado, fecha_inicial, fecha_final)"
                           + "values (14,"+id_empleado+",'"+fi+"','"+ff+"')");
                   
                   objDataBase.ejecutar("update tbl_rol_pagos set id_decimo_cuarto="+idDecimo+" where id_empleado="+id_empleado+" and id_decimo_cuarto is null");
                   
               }catch (Exception ex) {
                   System.out.println("Error al generar el Decimo Cuarto de: "+empleado+" error: "+ex.getMessage());
               }
            }
        } 
        catch (SQLException ex) {
            System.out.println("Error al generar el Decimo Cuarto: "+ex.getMessage());
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando cálculos del Decimo Cuarto");
            objDataBase.cerrar();
        }
        
    }
     
}
