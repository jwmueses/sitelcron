/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package saitelcron;

import org.apache.commons.daemon.Daemon;  
import org.apache.commons.daemon.DaemonContext;


import cron.trabajos.*;
import java.util.Date;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


/**
 *
 * @author jorge
 */
public class SaitelCron implements Daemon{
    
    @Override  
    public void init(DaemonContext arg0) throws Exception {
        
        
        
        
        System.out.println("Iniciando SAITEL Cron...");  
        try{
            SchedulerFactory almacen = new StdSchedulerFactory();
            Scheduler ejecutar = almacen.getScheduler();
            
            

            //  SERVIDORES DE CORTES
            JobDetail trabajo1 = JobBuilder.newJob(ServidoresCorte.class)
                    .withIdentity("servidoresCorte", "saitel").build();
            //Date startTime = DateBuilder.nextGivenSecondDate(null, 10);
            Date inicio = DateBuilder.dateOf(1, 40, 00);
            SimpleTrigger disparador1 = TriggerBuilder.newTrigger()
                        .withIdentity("todoElTiempoCada1Horas", "saitel")
                        .startAt(inicio)
                        .withSchedule( SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInHours(1)  //  .withIntervalInHours(1)     pruebas . withIntervalInMinutes(5)
                                .repeatForever()
                        )
                        .build();
            
            ejecutar.scheduleJob(trabajo1, disparador1);
            ejecutar.start();
            
            
            
            
            
            
            
            //  EMISION DE FACTURAS PENDIENTES DEL CASH PICHINCHA
            JobDetail trabajo11 = JobBuilder.newJob(Facturar.class)
                    .withIdentity("facturar", "saitel").build();
            
            Trigger disparador11 = TriggerBuilder.newTrigger()
	                    .withIdentity("cadaDiaALas19Horas", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 0 19 * * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año 
	                    .build();
            
            ejecutar.scheduleJob(trabajo11, disparador11);
            ejecutar.start();
            
            
            
            
            
            //  EJECUTAR CONTABILIZACION
            JobDetail trabajo2 = JobBuilder.newJob(Contabilizar.class)
                    .withIdentity("contabilizar", "saitel").build();
            
            Trigger disparador2 = TriggerBuilder.newTrigger()
	                    .withIdentity("cadaDiaALas21Horas", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 0 21 * * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año 
	                    .build();
            
            ejecutar.scheduleJob(trabajo2, disparador2);
            ejecutar.start();
            
            
            
            
            
            //  GENERACION DE PRE-FACTURAS EL 1 DE CADA MES
            //  CALCULOS DE PREFACTURAS NUEVAS            
            JobDetail trabajo3 = JobBuilder.newJob(Prefactura.class)
                    .withIdentity("prefacturar", "saitel").build();
            
            Trigger disparador3 = TriggerBuilder.newTrigger()
	                    .withIdentity("cada1DeCadaMes", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "1 0 0 1 * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año
	                    .build();
            
            ejecutar.scheduleJob(trabajo3, disparador3);
            ejecutar.start();
            
            
            
            
            
            
            
            //  REGISTRO DE RECONEXIONES POSTPAGO  Y PREPAGO A EXCEPCION QUITO NORTE Y QUITO SUR
            //  RE-CALCULOS DE PREFACTURAS CON RECONEXION            
//            JobDetail trabajo31 = JobBuilder.newJob(SetReconexionesPostPago.class)
//                    .withIdentity("SetReconexionesPostPago", "saitel").build();
//            
//            Trigger disparador31 = TriggerBuilder.newTrigger()
//	                    .withIdentity("el5DeCadaMes", "saitel")
//	                    .startAt(new Date(System.currentTimeMillis()))
//	                    .withSchedule( CronScheduleBuilder.cronSchedule( "1 0 0 6 * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año
//	                    .build();
//            
//            ejecutar.scheduleJob(trabajo31, disparador31);
//            ejecutar.start();
            
            
            
            
            
            
            
            //  REGISTRO DE RECONEXIONES PREPAGO QUITOS
            //  RE-CALCULOS DE PREFACTURAS CON RECONEXION            
//            JobDetail trabajo32 = JobBuilder.newJob(SetReconexionesPrePago.class)
//                    .withIdentity("SetReconexionesPrePago", "saitel").build();
//            
//            Trigger disparador32 = TriggerBuilder.newTrigger()
//	                    .withIdentity("el16DeCadaMes", "saitel")
//	                    .startAt(new Date(System.currentTimeMillis()))
//	                    .withSchedule( CronScheduleBuilder.cronSchedule( "1 0 0 16 * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año
//	                    .build();
//            
//            ejecutar.scheduleJob(trabajo32, disparador32);
//            ejecutar.start();
            
            
            
            
            
            //  CALCULOS DE COMISIONES         
            JobDetail trabajo4 = JobBuilder.newJob(Comisiones.class)
                    .withIdentity("comisiones", "saitel").build();
            
            Trigger disparador4 = TriggerBuilder.newTrigger()
	                    .withIdentity("cada25DeCadaMes", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 0 0 L * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año      
                            //.withSchedule( CronScheduleBuilder.cronSchedule( "50 00 16 * * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año    
	                    .build();
            
            ejecutar.scheduleJob(trabajo4, disparador4);
            ejecutar.start();
            






            //  CALCULOS DE ROLES DE PAGO        
            JobDetail trabajo4_1 = JobBuilder.newJob(RolesPago.class)
                    .withIdentity("rolesPago", "saitel").build();
            
            Trigger disparador4_1 = TriggerBuilder.newTrigger()
	                    .withIdentity("cada25DeCadaMesALas23_30", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "00 30 00 L * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año      
//                            .withSchedule( CronScheduleBuilder.cronSchedule( "0 30 23 20 * ?")) //  fin de anio.  segundos, minuto, hora, dia del mes, mes, dia de la semana, año    
	                    .build();
            
            ejecutar.scheduleJob(trabajo4_1, disparador4_1);
            ejecutar.start();


            
            
            
           
            
            
            // CONTROL DE ASISTENCIAS
            JobDetail trabajo5 = JobBuilder.newJob(ActualizaAsistencias.class)
                    .withIdentity("ActualizaAsistencias", "saitel").build();
            
            Trigger disparador5 = TriggerBuilder.newTrigger()
	                    .withIdentity("cadaDiaALas23Horas", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 0 23 * * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año   
	                    .build();
            
            ejecutar.scheduleJob(trabajo5, disparador5);
            ejecutar.start();
            
            
            
            
            
            
            // CALCULO PARA DECIMO CUARTO
            JobDetail trabajo6 = JobBuilder.newJob(decimoCuarto.class)
                    .withIdentity("decimoCuarto", "saitel").build();
            
            Trigger disparador6 = TriggerBuilder.newTrigger()
	                    .withIdentity("cada31DeJulio", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 0 23 31 7 ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año  
                            //.withSchedule( CronScheduleBuilder.cronSchedule( "15 28 12 * * ?")) 
	                    .build();
            
            ejecutar.scheduleJob(trabajo6, disparador6);
            ejecutar.start();
            
            
            
            
            
            
            
            // CALCULO PARA DECIMO TERCERO
            JobDetail trabajo7 = JobBuilder.newJob(decimoTercero.class)
                    .withIdentity("decimoTercero", "saitel").build();
            
            Trigger disparador7 = TriggerBuilder.newTrigger()
	                    .withIdentity("cada30DeNoviembre", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 0 23 30 11 ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año     
                            //.withSchedule( CronScheduleBuilder.cronSchedule( "50 28 9 * * ?")) 
	                    .build();
            
            ejecutar.scheduleJob(trabajo7, disparador7);
            ejecutar.start();
            
            
            
            
            
            
            // CALCULO PARA LAS SUSPENCIONES DEL 16 A FIN DE MES
            /*JobDetail trabajo8 = JobBuilder.newJob(Suspenciones1630.class)
                    .withIdentity("Suspenciones115", "saitel").build();
            
            Trigger disparador8 = TriggerBuilder.newTrigger()
	                    .withIdentity("suspensionesCada16DeCadaMes", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 0 22 16 * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año     
                            //.withSchedule( CronScheduleBuilder.cronSchedule( "50 28 9 * * ?")) 
	                    .build();
            
            ejecutar.scheduleJob(trabajo8, disparador8);
            ejecutar.start();*/
            
            
            
            
            
            
            
            
            // Envío de facturas al SRI
//            JobDetail trabajo9 = JobBuilder.newJob(DocumentosElectronicosSri.class)
//                    .withIdentity("DocumentosElectronicosSri", "saitel").build();
//            
//            Trigger disparador9 = TriggerBuilder.newTrigger()
//	                    .withIdentity("todosLosDias1930Horas", "saitel")
//	                    .startAt(new Date(System.currentTimeMillis()))
//	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 30 19 * * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año     
//	                    .build();
//            
//            ejecutar.scheduleJob(trabajo9, disparador9);
//            ejecutar.start();
            
            
            
            
            
            
            
            
            //  CONTABILIZACION DE DEPRESIACIONES
            JobDetail trabajo310 = JobBuilder.newJob(FinMes.class)
                    .withIdentity("finMes", "saitel").build();
            
            Trigger disparador310 = TriggerBuilder.newTrigger()
	                    .withIdentity("cadaFinDeMes", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "00 01 00 L * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año 
	                    .build();
            
            ejecutar.scheduleJob(trabajo310, disparador310);
            ejecutar.start();
            
            
            
            
            
            
            
            
            
            //  VARIOS PROCESOS DIARIO
            
            //  GENERAR ORDENES DE TRABAJO POR RETIRAR
            //  ELIMINA PREFACTURAS DE INSTALACIONES CORTADAS EN MESES ANTERIORES
            //  ELIMINA PREFACTURAS DE INSTALACIONES CORTADAS EN MESES ANTERIORES DE FACTURAS A CREDITO
            //  ELIMINA PREFACTURAS DE INSTALACIONES SUSPENDIDAS
            //  RECALCULO DE PREFACTURAS QUEDADAS O GENERADAS FALTANTES TRAS ACTUALIZACION DE SERVIDORES DE CORTES
            JobDetail trabajo10 = JobBuilder.newJob(EjecutarProcesos.class)
                    .withIdentity("Ejecutar", "saitel").build();
            
            Trigger disparador10 = TriggerBuilder.newTrigger()
	                    .withIdentity("cadaDiaALas0Ejecuta", "saitel")
	                    .startAt(new Date(System.currentTimeMillis()))
	                    .withSchedule( CronScheduleBuilder.cronSchedule( "0 1 0 * * ?")) //  segundos, minuto, hora, dia del mes, mes, dia de la semana, año    
	                    .build();
            
            ejecutar.scheduleJob(trabajo10, disparador10);
            ejecutar.start();
            
            
            
            
            
        }catch(SchedulerException e){
            e.printStackTrace();
        }
    }

    @Override  
    public void destroy() {  
        System.out.println("Llamando a destroy");  
    }  
    
    @Override  
    public void start() throws Exception {  
        
//        VerificacionProcesos.verificar();

//    para pruebas
//        DataBase objDocumental = new DataBase( "127.0.0.1", Parametro.getDocumentalPuerto(), Parametro.getDocumentalBaseDatos(), "postgres", "postgres" );
//        DataBase objDataBase = new DataBase( "127.0.0.1", Parametro.getPuerto(), Parametro.getBaseDatos(), "postgres", "postgres" );
//        FacturaVenta objFacturaVenta = new FacturaVenta( objDocumental, "127.0.0.1", Parametro.getPuerto(), Parametro.getBaseDatos(), "postgres", "postgres" );



//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando prueba de envio de correo");
//        
//        Correo.enviar( Parametro.getSvrMail(), 
//                        Parametro.getSvrMailPuerto(), 
//                        Parametro.getRemitente(), 
//                        Parametro.getRemitenteClave(), 
//                        "sistemas@saitel.ec", 
//                        "", 
//                        "",
//                        "PRUEBA DE ENVIO DE CORREO",
//                        new StringBuilder("HOLA MUNDO"), 
//                        true, 
//                        null);
//        
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando prueba de envio de correo");
        
        
        
//        try{
//            
//            ServidoresCorte sv = new ServidoresCorte();
//            sv.ejecutar();
//
//        }catch(Exception e){
//            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
//        }finally{
//            objDocumental.cerrar();
//            objDataBase.cerrar();
//            objFacturaVenta.cerrarConexiones();
//            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de emisión de facturas a clientes con anticipos pruebas");
//        }
        
        System.out.println("Llamando a start");  
    }  

    @Override  
    public void stop() throws Exception {  
        System.out.println("Lamando a stop");  
    }  
    
}
