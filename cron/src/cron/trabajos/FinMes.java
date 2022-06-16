/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

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
        
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de ejecucion de depreciaciones.");
        objDataBase.consulta("select proc_ejecutardepreciaciones();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de ejecucion de depreciaciones");

        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de ejecucion de depreciaciones revalorizadas.");
        objDataBase.consulta("select proc_ejecutardepreciacionesrevalorizadas();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de ejecucion de depreciaciones revalorizadas");
        
        
        
        
        
        objDataBase.cerrar();
    }
    
}
