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
 * @author jorge
 */
public class ActualizaAsistencias implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        
        
        
        TraerTimbradosBiometrico traerTimbrados = new TraerTimbradosBiometrico();
        traerTimbrados.obtener();
        
        
        
        
        
        
        
        
        
        
        
        
//        //Asistencias 14/7
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de Asistencias para Usuarios 14/7.");
//        try{
//            objDataBase.consulta("SELECT proc_asistencia147();");
//        }
//        catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de Asistencias para Usuarios 14/7");
//        
//        //Asistencias 5/2
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de Asistencias para Usuarios 5/2.");
//        try{
//            objDataBase.consulta("SELECT proc_asistencia52();");
//        }
//        catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de Asistencias para Usuarios 5/2");
        
//        //Calculo de Rubros
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio para la creacion de Rubros de Nomina.");
//        try{
//            objDataBase.consulta("insert into tbl_rubro_cont_det (id_rubro_cont, id_empleado, rubro, periodo,monto,id_sucursal) " +
//                                "(select p.id_rubro_cont,p.id_empleado, c.rubro, date_trunc('month',current_date), p.monto, p.id_sucursal from tbl_rubro_cont_per p " +
//                                "join tbl_rubro_cont c on p.id_rubro_cont=c.id_rubro_cont " +
//                                "where date 'now()'>c.fecha_inicio and c.tipo=false and p.id_empleado not in ( " +
//                                "select d.id_empleado from tbl_rubro_cont_det d where d.periodo=date_trunc('month',current_date) and p.id_rubro_cont=d.id_rubro_cont));");
//        }
//        catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de la creacion de Rubros de Nomina");
        
        objDataBase.cerrar();
    }
     
}
