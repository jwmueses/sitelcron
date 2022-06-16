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
public class CalculosVariosDiario implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de estados de instalaciones.");
        objDataBase.consulta("select proc_robot();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de estados de instalaciones");

            
            
            
            
        
        
        //  Genera ordenes de trabajo por retirar     
        /*System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de ordenes de trabajo por retirar");
        try{
            objDataBase.consulta("select proc_generarInstalacionesPorRetirar('"+Fecha.getFecha("ISO")+"');");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de registro de ordenes de trabajo por retirar");
        }*/
        
        
        
        
        
        //  Eliminacion de PREFACTURAS DE INSTALACIONES CORTADAS EN MESES ANTERIORES    
        /*System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando eliminación de prefacturas de instalaciones cortadas");
        try{
            int anio = Fecha.getAnio();
            int mes = Fecha.getMes();
            int dia_cortes = this.getParametroValor(objDataBase, "dia_cortes");
            int hoy = Fecha.getDia();
            String dia_semana = Fecha.getDiaSemana(dia_cortes);
            if(dia_semana.compareTo("sábado")==0){
                hoy = hoy - 2;
            }
            if(dia_semana.compareTo("domingo")==0){
                hoy--;
            }
            
            if(hoy >= dia_cortes){
                String periodo_cortados_ini = anio + "-" + mes + "-01";
                String periodo_cortados_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        
                boolean ok = objDataBase.ejecutar("delete from tbl_prefactura where periodo between '"+periodo_cortados_ini+"' and '"+periodo_cortados_fin+"' and fecha_emision is null and "
                + "id_instalacion in (select id_instalacion from tbl_prefactura where periodo < '"+periodo_cortados_ini+"' and fecha_emision is null)");                
                if(ok){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de rubros de reconexión de instalaciones cortadas");
                    //objDataBase.consulta("select proc_setRubrosPrefacturasCortes();");
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generación de rubros de reconexión de instalaciones cortadas");
                }
            }
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de eliminación de prefacturas de instalaciones cortadas");
        }*/
        
        
        
        
        
        //  Eliminacion de PREFACTURAS DE INSTALACIONES CORTADAS CON FACTURAS A CREDITO EN MESES ANTERIORES    
       /* System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHoraExtendida() + ": Iniciando eliminación de prefacturas de instalaciones cortadas de facturas a crédito");
        try{
            int anio = Fecha.getAnio();
            int mes = Fecha.getMes();
            int dia_cortes_creditos = this.getParametroValor(objDataBase, "dia_cortes_creditos");
            int hoy = Fecha.getDia();
            String dia_semana = Fecha.getDiaSemana(dia_cortes_creditos);
            if(dia_semana.compareTo("sábado")==0){
                hoy = hoy - 2;
            }
            if(dia_semana.compareTo("domingo")==0){
                hoy--;
            }
            
            if(hoy >= dia_cortes_creditos){
                mes--;
                if(mes==0){
                    mes=12;
                    anio--;
                }
                String periodo_anterior_ini = anio + "-" + mes + "-01";
                String periodo_anterior_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);

                mes--;
                if(mes==0){
                    mes=12;
                    anio--;
                }
                String periodo_cortados_ini = anio + "-" + mes + "-01";
                String periodo_cortados_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);

                boolean ok = objDataBase.ejecutar("delete from tbl_prefactura where periodo between '"+periodo_anterior_ini+"' and '"+periodo_anterior_fin+"' and fecha_emision is null and "
                    + "id_instalacion in (select P.id_instalacion from tbl_prefactura as P inner join tbl_factura_venta as F on F.id_factura_venta=P.id_factura_venta where P.periodo between '"+
                    periodo_cortados_ini+"' and '"+periodo_cortados_fin+"' and P.fecha_emision is not null and F.forma_pago='d' and F.deuda>0)");
                if(ok){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHoraExtendida() + ": Proceso eliminación de prefacturas de instalaciones cortadas de facturas a crédito");
                    //objDataBase.consulta("select proc_setRubrosFacturasCreditoCortes();");
                }
            }
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHoraExtendida() + ": Finalización de eliminación de prefacturas de instalaciones cortadas de facturas a crédito");
            
        }
        
        
        
        
        
        //  Eliminacion de PREFACTURAS DE INSTALACIONES CORTADAS EN MESES ANTERIORES    
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando eliminación de prefacturas de instalaciones suspendidas");
        try{
            int anio = Fecha.getAnio();
            int mes = Fecha.getMes();
            mes--;
            if(mes==0){
                mes=12;
                anio--;
            }
            String periodo_anterior_ini = anio + "-" + mes + "-01";
            String periodo_anterior_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);

            objDataBase.ejecutar("delete from tbl_prefactura where periodo between '"+periodo_anterior_ini+"' and '"+periodo_anterior_fin+"' and fecha_emision is null and "
                + "id_instalacion in (select id_instalacion from tbl_instalacion_suspension where eliminado=false and tipo='t' and fecha_inicio between '"+periodo_anterior_ini+"' and '"+periodo_anterior_fin+"')");
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHoraExtendida() + ": Proceso eliminación de prefacturas de instalaciones suspendidas");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de eliminación de prefacturas de instalaciones suspendidas");
        }*/
        
        
        
        
        
        
        //  RECALCULO DE PREFACTURAS QUEDADAS O GENERADAS FALTANTES TRAS ACTUALIZACION DE SERVIDORES DE CORTES
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando recalculo de prefacturas faltantes");
        try{
            ResultSet rs = objDataBase.consulta("select id_prefactura from tbl_prefactura where recalcular=true and fecha_emision is null");
            while(rs.next()){
                String idPrefactura = rs.getString("id_prefactura")!=null ? rs.getString("id_prefactura") : "-1";
                try{
                    objDataBase.consulta("select proc_calcularPreFactura("+idPrefactura+", false);");
                }catch(Exception e){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefactura postgpago. " + e.getMessage());
                }
            }
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefacturas faltantes. " + e.getMessage());
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando recalculo de prefacturas faltantes");
        }
        
        
        
        
        
        
        
        //  Genera ordenes de trabajo por retirar     
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de ordenes de trabajo por retirar diarias");
        try{
            objDataBase.consulta("select proc_generarinstalacionesporretirardiarias();");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de registro de ordenes de trabajo por retirar diarias");
        }   
        
        
        objDataBase.cerrar();
        
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
