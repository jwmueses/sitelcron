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
public class VerificacionProcesos {
    
    public static void verificar(){
        
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        System.out.println("Verificación de procesos iniciada...");
        try{
            
            boolean verificarProcesosCron = false;
            try{
                ResultSet rs = objDataBase.consulta("select valor::boolean from tbl_configuracion where parametro='verificar_procesos_cron'");
                if(rs.next()){
                    verificarProcesosCron = rs.getString(1)!=null ? rs.getBoolean(1) : false;
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
            if( verificarProcesosCron ){
                Prefactura prefactura = new Prefactura();
                prefactura.execute(null);


                //  se controla que verifique a partir del dia 6, para prepago y postpago
                SetReconexionesPostPago reconecionesPostPago = new SetReconexionesPostPago();
                reconecionesPostPago.execute(null);


                //  se controla que verifique a partir del dia 16
                SetReconexionesPrePago reconecionesPrePago = new SetReconexionesPrePago();
                reconecionesPrePago.execute(null);


                //  se controla que verifique a partir del dia 25 a las 23 horas
                Comisiones comisiones = new Comisiones();
                comisiones.execute(null);


                FinMes finMes = new FinMes();
                finMes.execute(null);


                EjecutarProcesos diario = new EjecutarProcesos();
                diario.execute(null);


                //  actualiza asistencias desde las bases de SQL Server
                ActualizaAsistencias asistencias = new ActualizaAsistencias();
                asistencias.execute(null);
                
//                DocumentosElectronicosSri documentosElectronicosSri = new DocumentosElectronicosSri();
//                documentosElectronicosSri.execute(null);
            }
            
            
        }catch(Exception e){
            System.out.println("Error verificando " + e.getMessage());
        }finally{
            objDataBase.cerrar();
            System.out.println("Finalización de verificación de procesos...");
        }
        
    }
    
}
