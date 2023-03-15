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
 * @author jorge
 */
public class Comisiones implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        int diaHoy = Fecha.getDia();
        int diaCorteComision = 28;
        try{
            ResultSet rs = objDataBase.consulta("select valor from tbl_configuracion where parametro='dia_corte_comision'");
            if(rs.next()){
                diaCorteComision = rs.getString(1)!=null ? rs.getInt(1) : 28;
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        if( diaCorteComision >= 32 ){
            diaCorteComision = Fecha.getUltimoDiaMes( Fecha.getAnio() , Fecha.getMes() );
        }
        
//        Calendar cal = Calendar.getInstance();
//        int hora = cal.get(Calendar.HOUR_OF_DAY);
        
        
        
        
//        if( (diaHoy == diaCorteComision && hora >= 23) || diaHoy > diaCorteComision ){
        if( diaHoy >= diaCorteComision ){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de estados de instalaciones.");
            objDataBase.consulta("select proc_robot();");
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de estados de instalaciones");


            
            
            //      comisiones trabajadores 
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando cálculos de comisiones");
            try{
                objDataBase.consulta("select generarComisiones();");
//                objDataBase.consulta("select generarcomisionesFreeLance();");
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando cálculos de comisiones");
            }

        }
        
        objDataBase.cerrar();
        
    }
     
}
