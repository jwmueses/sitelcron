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
public class SetReconexionesPrePago implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        int dia = Fecha.getDia();
        String fecha = anio + "-" + mes + "-01";
        String fechaFinMes = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
            
        
        
        
        
        if(dia >= 16){
            
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de reconexiones prepago");
            try{
                if(!objDataBase.ejecutar("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, monto) \n" +
                    "select id_sucursal, 4, id_instalacion, '"+fecha+"'::date, 'Reconexión'::varchar, 0.89 from \n" +
                    "vta_prefactura where fecha_emision is null and periodo between '"+fecha+"'::date and '"+fechaFinMes+"'::date and txt_convenio_pago='prepago' and id_sucursal in('7','11') \n" +
                    "and id_instalacion not in (select id_instalacion from tbl_prefactura_rubro where periodo='"+fecha+"'::date and lower(rubro) like 'reconexi%');")){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en generación de reconexiones prepago. " + objDataBase.getError());
                }

                if(!objDataBase.ejecutar("update tbl_prefactura_rubro as PR "
                        + "set id_rubro=(select id_rubro from tbl_rubro where replace(rubro, 'Reconexión ', '')::int=PR.id_sucursal and id_rubro between 2 and 13) "
                        + "where id_rubro between 2 and 13;")){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en reasignación del rubro reconexiones a la sucursal correspondiente prepago. " + objDataBase.getError());
                }
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando generación de reconexiones prepago");
            }








            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando recalculo de prefacturas prepago");
            try{
                objDataBase.ejecutar("update tbl_prefactura set recalcular=true where id_prefactura in ( select id_prefactura from vta_prefactura where update tbl_prefactura set recalcular=true where periodo='"+fecha+"'::date and fecha_emision is null and txt_convenio_pago='prepago' )");
                ResultSet rs = objDataBase.consulta("select id_prefactura from vta_prefactura where periodo='"+fecha+"'::date");
                while(rs.next()){
                    String idPrefactura = rs.getString("id_prefactura")!=null ? rs.getString("id_prefactura") : "-1";
                    try{
                        objDataBase.consulta("select proc_calcularPreFactura("+idPrefactura+", false);");
                    }catch(Exception e){
                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefactura postgpago. " + e.getMessage());
                    }
                }
            }catch(Exception e){
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefacturas prepago. " + e.getMessage());
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando recalculo de prefacturas prepago");
            }


            objDataBase.cerrar();

        }
        
    }
     
}
