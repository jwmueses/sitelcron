/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.io.File;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge
 */
public class PruebasCron implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        String salida = "";
        //      registro secuencial 
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando pruebas en cron");
        try{
            /*objDataBase.ejecutar("insert into tbl_cron(id) select max(id)+1 from tbl_cron;");*/
            String sDirectorio = "/var/lib/tomcat7/webapps/anexos/docs_electronicos/pdfsprueba";
            File f = new File(sDirectorio);
            if (f.exists()){
                System.out.println("El Directorio esta vacio<br />");
                File[] ficheros = f.listFiles();
                for (int x=0;x<ficheros.length;x++){
                    if(ficheros[x].getName().compareTo("logo.jpg")!=0){
                        System.out.println("Fichero Eliminado: "+ficheros[x].getName());
                        ficheros[x].delete();
                    }
                    //System.out.println(ficheros[x].getName());

                }
            }
            else {
                System.out.println("No Existe el Directorio");
            }
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando pruebas en cron");
            objDataBase.cerrar();
        }
        
    }
     
}
