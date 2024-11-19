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
public class Suspenciones1630 implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() ); 
        
        //  Genera ordenes de trabajo por retirar     
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de ordenes de trabajo por retirar del 16 al fin mes");
        try{
            objDataBase.consulta("select proc_generarinstalacionesporretirar161('"+Fecha.getFecha("ISO")+"');");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de registro de ordenes de trabajo por retirar del 16 al fin mes");
        }     

    }
     
    
    public int getParametroValor(DataBase objDB, String param)
    {
        int valor = 5;
        try{
            ResultSet r = objDB.consulta("SELECT valor FROM tbl_configuracion where parametro='"+param+"';");
            if(r.next()){
                valor = (r.getString("valor")!=null) ? r.getInt("valor") : 5;
                r.close();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return valor;
    }
}
