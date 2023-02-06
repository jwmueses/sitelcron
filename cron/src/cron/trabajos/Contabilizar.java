/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import java.util.Calendar;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge
 */
public class Contabilizar implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de contabilización de ventas");
        try{
            int dia = Fecha.getDia();
            
            //  cajas fisicas
            ResultSet rs = objDataBase.consulta("select vendedor, count(*) as num_ventas from tbl_factura_venta where lower(vendedor) not like 'virtual%' and contabilizado=false and anulado=false group by vendedor "
                    + "union select cajero as vendedor, count(*) as num_ventas from tbl_comprobante_ingreso where lower(cajero) not like 'virtual%' and contabilizado=false and anulado=false group by vendedor order by num_ventas desc");
            while(rs.next()){
                
                Calendar cal = Calendar.getInstance();
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                if(hora>=6 && hora<=17){
                    break;
                }
                
                String vendedor = rs.getString("vendedor")!=null ? rs.getString("vendedor") : "";
                //String mailEmpleado = this.getEmailEmpleado(objDataBase, vendedor);
                if(vendedor.contains("virtual")){
                    if(dia >= 1 && dia <= 10){
                        continue;
                    }
                }
                try{
                    ResultSet rsContabilizar = objDataBase.consulta("select contabilizarVentas('"+vendedor+"')");
                    if(rsContabilizar.next()){
                        String ok = rsContabilizar.getString(1)!=null ? rsContabilizar.getString(1) : "error";
                        if(ok.compareTo("t")!=0){
                            System.out.println("Error en la contabilización del usuario " + vendedor + ". " + objDataBase.getError());
                            //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
                        }else{
                            System.out.println("Contabilización del usuario " + vendedor);
                        }
                        rsContabilizar.close();
                    }
                }catch(Exception e){
                    String msg = "Error en la contabilización del usuario " + vendedor + ". " + e.getMessage() + ". " + objDataBase.getError();
                    System.out.println(msg);
                    //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
                }
                
            }
            rs.close();
            
            
            // cajas virtuales
            ResultSet rs1 = objDataBase.consulta("select vendedor, count(*) as num_ventas from tbl_factura_venta where lower(vendedor) like 'virtual%' and contabilizado=false and anulado=false group by vendedor "
                    + "union select cajero as vendedor, count(*) as num_ventas from tbl_comprobante_ingreso where lower(cajero) like 'virtual%' and contabilizado=false and anulado=false group by vendedor order by num_ventas desc");
            while(rs1.next()){
                
                Calendar cal = Calendar.getInstance();
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                if(hora>=6 && hora<=17){
                    break;
                }
                
                String vendedor = rs1.getString("vendedor")!=null ? rs1.getString("vendedor") : "";
                //String mailEmpleado = this.getEmailEmpleado(objDataBase, vendedor);
                if(vendedor.contains("virtual")){
                    if(dia >= 1 && dia <= 10){
                        continue;
                    }
                }
                try{
                    ResultSet rsContabilizar = objDataBase.consulta("select contabilizarVentas('"+vendedor+"')");
                    if(rsContabilizar.next()){
                        String ok = rsContabilizar.getString(1)!=null ? rsContabilizar.getString(1) : "error";
                        if(ok.compareTo("t")!=0){
                            System.out.println("Error en la contabilización del usuario " + vendedor + ". " + objDataBase.getError());
                            //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
                        }else{
                            System.out.println("Contabilización del usuario " + vendedor);
                        }
                        rsContabilizar.close();
                    }
                }catch(Exception e){
                    String msg = "Error en la contabilización del usuario " + vendedor + ". " + e.getMessage() + ". " + objDataBase.getError();
                    System.out.println(msg);
                    //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
                }
                
            }
            rs1.close();

        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
        }finally{
            String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de contabilización de ventas";
            System.out.println(msg);
            objDataBase.cerrar();
            //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", "sistemas@saitel.ec", "", "CONTABILIZACION", new StringBuilder(msg), true);
        }
        
        
        
        
        
        
        
        
        
        
//        try{
//            ResultSet rs = objDataBase.consulta("select distinct fecha_emision from tbl_factura_venta where contabilizado=false and anulado=false and vendedor='virtualMatriz' order by fecha_emision desc");
//            while(rs.next()){
//                String fecha_emision = rs.getString("fecha_emision")!=null ? rs.getString("fecha_emision") : "";
//                try{
//                    ResultSet rsContabilizar = objDataBase.consulta("select contabilizarVentas('virtualMatriz', '"+fecha_emision+"')");
//                    if(rsContabilizar.next()){
//                        String ok = rsContabilizar.getString(1)!=null ? rsContabilizar.getString(1) : "error";
//                        if(ok.compareTo("t")!=0){
//                            System.out.println("Error en la contabilización del usuario virtualMatriz. " + objDataBase.getError());
//                        }else{
//                            System.out.println("Contabilización del usuario virtualMatriz de la fecha " + fecha_emision);
//                        }
//                        rsContabilizar.close();
//                    }
//                }catch(Exception e){
//                    String msg = "Error en la contabilización del usuario virtualMatriz. " + e.getMessage() + ". " + objDataBase.getError();
//                    System.out.println(msg);
//                    //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", mailEmpleado, "sistemas@saitel.ec", "NO CONTABILIZACION DEL USUARIO " + vendedor, new StringBuilder(msg), true);
//                }
//                
//            }
//            rs.close();
//            
//        }catch(Exception e){
//            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
//        }finally{
//            String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de contabilización de ventas";
//            System.out.println(msg);
//            objDataBase.cerrar();
//            //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "contabilidad@saitel.ec", "sistemas@saitel.ec", "", "CONTABILIZACION", new StringBuilder(msg), true);
//        }
        
        
        
    }
    
    
//    private String getEmailEmpleado (DataBase objDataBase, String alias)
//    {
//        String email = "";
//        try{
//            ResultSet rs = objDataBase.consulta("select email from tbl_empleado where alias='"+alias+"'");
//            if(rs.next()){
//                email = rs.getString(1)!=null ? rs.getString(1) : "";
//                rs.close();
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }    
//        return email;
//    }
     
}
