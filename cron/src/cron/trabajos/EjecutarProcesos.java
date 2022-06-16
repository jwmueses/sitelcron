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
 * @author sistemas
 */
public class EjecutarProcesos  implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException 
    {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        int dia = Fecha.getDia();
        String fecha = anio + "-" + mes + "-01";
        String fechaFinMes = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        
        
        //  SE EJECUTA BAJA DE PRIVILEGIOS
        
        FinAnio finAnio = new FinAnio();
        finAnio.ejecutar();
        
        
        
        
        
        
        
        if(dia == 15 && mes == 12){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": generacion de planificacion de vacaciones para el año " + (anio+1) );
            
            objDataBase.consulta("select proc_set_empleado_vacaciones_anio(" + (anio+1) + ");");
            
            objDataBase.ejecutar("with T as ( \n" +
                "select * from tbl_empleado_vacaciones_anio where anio = " + anio + " \n" +
                ") \n" +
                "update tbl_empleado_vacaciones_anio as V1 \n" +
                "set fecha_programada1 = (select T.fecha_programada1 + '1 year'::interval from T where V1.id_empleado=T.id_empleado), \n" +
                "fecha_programada2 = (select T.fecha_programada2 + '1 year'::interval from T where V1.id_empleado=T.id_empleado), \n" +
                "fecha_programada3 = (select T.fecha_programada3 + '1 year'::interval from T where V1.id_empleado=T.id_empleado), \n" +
                "fecha_programada4 = (select T.fecha_programada4 + '1 year'::interval from T where V1.id_empleado=T.id_empleado), \n" +
                "fecha_programada5 = (select T.fecha_programada5 + '1 year'::interval from T where V1.id_empleado=T.id_empleado), \n" +
                "dias_fecha1 = (select T.dias_fecha1 from T where V1.id_empleado=T.id_empleado), \n" +
                "dias_fecha2 = (select T.dias_fecha2 from T where V1.id_empleado=T.id_empleado), \n" +
                "dias_fecha3 = (select T.dias_fecha3 from T where V1.id_empleado=T.id_empleado), \n" +
                "dias_fecha4 = (select T.dias_fecha4 from T where V1.id_empleado=T.id_empleado), \n" +
                "dias_fecha5 = (select T.dias_fecha5 from T where V1.id_empleado=T.id_empleado) \n" +
                "where anio=" + (anio+1) );
            
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de planificacion de vacaciones para el año " + (anio+1) );
        }
        
        
        
        
        
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de estados de instalaciones.");
        objDataBase.consulta("select proc_robot();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de estados de instalaciones");

            
        
        
        
        
        int dia_reconexion = 6;
        int dia_reconexion_quito = 16;
        try{
            ResultSet rs = objDataBase.consulta("select * from tbl_configuracion where parametro in ('dia_reconexion', 'dia_reconexion_quito')");
            while(rs.next()){
                String parametro = rs.getString("parametro")!=null ? rs.getString("parametro") : "";
                if(parametro.compareTo("dia_reconexion")==0){
                    dia_reconexion = rs.getString("valor")!=null ? rs.getInt("valor") : 6;
                }
                if(parametro.compareTo("dia_reconexion_quito")==0){
                    dia_reconexion_quito = rs.getString("valor")!=null ? rs.getInt("valor") : 16;
                }
            }
            System.out.println("Configuracion dias reconexion: hoy= " + dia + " postpago= " + dia_reconexion + " prepago= " + dia_reconexion_quito);
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en obtención de parametros de configuraciones. " + e.getMessage());
        }
        
        
        
        
        
        
        
        
        
        
        
            //      GENERAR RECONEXIONES PREPAGO  QUITO      
        
        if(dia >= dia_reconexion_quito){
            
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de reconexiones prepago");
            try{
                if(!objDataBase.ejecutar("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, monto) \n" +
                    "select id_sucursal, 4, id_instalacion, '"+fecha+"'::date, 'Reconexión'::varchar, 0.89 from \n" +
                    "vta_prefactura as P where fecha_emision is null and periodo between '"+fecha+"'::date and '"+fechaFinMes+"'::date and txt_convenio_pago='prepago' and id_sucursal in('7','11') \n" +
//                    "and not (select case when count(*)>0 then true else false end from vta_prefactura_diferir as PD where P.periodo between desde and hasta) \n" + 
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
                objDataBase.ejecutar("update tbl_prefactura set recalcular=true where id_prefactura in ( select id_prefactura from vta_prefactura where periodo='"+fecha+"'::date and fecha_emision is null and txt_convenio_pago='prepago' )");
                ResultSet rs = objDataBase.consulta("select id_prefactura from vta_prefactura where recalcular=true");
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


        }
        
        
        
        
        
        
        
        
        
                
//      GENERAR RECONEXIONES POSPAGO  NACIONAL      
        


        if(dia >= dia_reconexion){
            
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de reconexiones prepago");
            try{
                if(!objDataBase.ejecutar("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, monto) \n" +
                    "select id_sucursal, 4, id_instalacion, '"+fecha+"'::date, 'Reconexión'::varchar, 0.89 from \n" +
                    "vta_prefactura as P where fecha_emision is null and periodo between '"+fecha+"'::date and '"+fechaFinMes+"'::date and txt_convenio_pago='prepago' and id_sucursal not in('7','11') \n" +
//                    "and not (select case when count(*)>0 then true else false end from vta_prefactura_diferir as PD where P.periodo between desde and hasta) \n" +         
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


            //      POSTPAGO    retrocedo un mes

            mes = Fecha.getMes()-1;
            if(mes==0){
                mes=12;
                anio--;
            }
            fecha = anio + "-" + mes + "-01";
            fechaFinMes = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);


            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de reconexiones postpago");
            try{
                if(!objDataBase.ejecutar("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, monto) \n" +
                    "select id_sucursal, 4, id_instalacion, '"+fecha+"'::date, 'Reconexión'::varchar, 0.89 from \n" +
                    "vta_prefactura as P where fecha_emision is null and periodo between '"+fecha+"'::date and '"+fechaFinMes+"'::date and txt_convenio_pago='postpago' \n" +
//                    "and not (select case when count(*)>0 then true else false end from vta_prefactura_diferir as PD where P.periodo between desde and hasta) \n" +         
                    "and id_instalacion not in (select id_instalacion from tbl_prefactura_rubro where periodo='"+fecha+"'::date and lower(rubro) like 'reconexi%');")){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en generación de reconexiones postgpago. " + objDataBase.getError());
                }

                if(!objDataBase.ejecutar("update tbl_prefactura_rubro as PR "
                        + "set id_rubro=(select id_rubro from tbl_rubro where replace(rubro, 'Reconexión ', '')::int=PR.id_sucursal and id_rubro between 2 and 13) "
                        + "where id_rubro between 2 and 13;")){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en reasignación del rubro reconexiones a la sucursal correspondiente postgpago. " + objDataBase.getError());
                }
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando generación de reconexiones postpago");
            }




            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando recalculo de prefacturas postpago");
            try{
                objDataBase.ejecutar("update tbl_prefactura set recalcular=true where id_prefactura in ( select id_prefactura from vta_prefactura where periodo='"+fecha+"'::date and fecha_emision is null and txt_convenio_pago='postpago' )");
                ResultSet rs = objDataBase.consulta("select id_prefactura from vta_prefactura where periodo>='"+fecha+"'::date");
                while(rs.next()){
                    String idPrefactura = rs.getString("id_prefactura")!=null ? rs.getString("id_prefactura") : "-1";
                    try{
                        objDataBase.consulta("select proc_calcularPreFactura("+idPrefactura+", false);");
                    }catch(Exception e){
                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefactura postgpago. " + e.getMessage());
                    }
                }
            }catch(Exception e){
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefacturas postpago. " + e.getMessage());
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando recalculo de prefacturas postpago");
            }

            
        }
        
        
        
        
        

        
        
        
        
        
        
        
        
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
        
        
        
        
        try{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de anulación de personalizaciones pendientes de aceptar");
            objDataBase.ejecutar("update tbl_activo_personalizacion set aceptada=false, anulado=true where aceptada=false and anulado=false and fecha + '1 month'::interval < now()::date");
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + "Error en el proceso de anulación de personalizaciones pendientes de aceptar: " + e.getMessage());
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de anulación de personalizaciones pendientes de aceptar");
        }  
        
        
        
        //  Genera ordenes de trabajo por retirar
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de ordenes de trabajo por retirar diariamente");
        try{
            objDataBase.consulta("select proc_generarinstalacionesporretirardiarias();");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generación de ordenes de trabajo por retirar para el dia: " + Fecha.getFecha("SQL"));
        }
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de ordenes de compra de pedidos");
        try{
            objDataBase.consulta("select proc_generarordenesdecompra();");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generación de ordenes de compra de pedidos");
        }
        
        
        
        
        
        //  liberar ips sin reutilizar
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de ips que no se utilizan en las redes");
        try{
            objDataBase.consulta("select proc_liberar_ips();");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generación de ips que no se utilizan en las redes");
        }
        
        
        
        
        
        
        //  ejecuta cierre de promociones
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando cierre de promociones");
        try{
            
            objDataBase.ejecutar("update tbl_promocion set cerrada=true where fecha_termino < now()::date and cerrada=false;");
            
            try{
                ResultSet rs1 = objDataBase.consulta("select id_promocion, count(id_instalacion), sum(costo_instalacion) "
                        + "from tbl_instalacion_promocion as P inner join tbl_instalacion as I on I.id_instalacion=P.id_instalacion "
                        + "where id_promocion in (select id_promocion from tbl_promocion where inst_objetivo_a_cumplir > 0 and cerrada=false) "
                        + "group by id_promocion order by id_promocion;");
                String matRs1[][] = Matriz.ResultSetAMatriz(rs1);
                ResultSet rs = objDataBase.consulta("select * from tbl_promocion where inst_objetivo_a_cumplir > 0 and cerrada=false;");
                while(rs.next()){
                    String id_promocion = rs.getString("id_promocion")!=null ? rs.getString("id_promocion") : "-1";
                    boolean inst_objetivo_es_porcentaje = rs.getString("inst_objetivo_es_porcentaje")!=null ? rs.getBoolean("inst_objetivo_es_porcentaje") : true;
                    int inst_objetivo_a_cumplir = rs.getString("inst_objetivo_a_cumplir")!=null ? rs.getInt("inst_objetivo_a_cumplir") : 0;
                    String inst_objetivo_basado_en = rs.getString("inst_objetivo_basado_en")!=null ? rs.getString("inst_objetivo_basado_en") : "n";
                    float inst_base_referencia_total = rs.getString("inst_base_referencia_total")!=null ? rs.getFloat("inst_base_referencia_total") : 0;
                    
                    int i = Matriz.enMatriz(matRs1, id_promocion, 0);
                    if(i>=0){
                        
                        //  si se basa en porcentaje y en numero de instalaciones
                        if(inst_objetivo_es_porcentaje && inst_objetivo_basado_en.compareTo("n")==0){   
                            double prorrateoReferencialTotal = inst_base_referencia_total * inst_objetivo_a_cumplir / 100;
                            objDataBase.ejecutar("update tbl_promocion set inst_objetivo_total="+matRs1[i][1]+
                                " "+(prorrateoReferencialTotal < Double.parseDouble(matRs1[i][1]) ? "true" : "false")+" where id_promocion="+id_promocion);
                        }
                        
                        // si se basa en un valor fijo y en numero de instalaciones
                        if(!inst_objetivo_es_porcentaje && inst_objetivo_basado_en.compareTo("n")==0){   //  si se basa en porcentaje y en numero de instalaciones
                            objDataBase.ejecutar("update tbl_promocion set inst_objetivo_total="+matRs1[i][1]+
                                " "+(inst_objetivo_a_cumplir < Double.parseDouble(matRs1[i][1]) ? "true" : "false")+" where id_promocion="+id_promocion);
                        }
                        
                        //  si se basa en porcentaje y en monto total de costo de instalaciones
                        if(inst_objetivo_es_porcentaje && inst_objetivo_basado_en.compareTo("m")==0){   
                            double prorrateoReferencialTotal = inst_base_referencia_total * inst_objetivo_a_cumplir / 100;
                            objDataBase.ejecutar("update tbl_promocion set inst_objetivo_total="+matRs1[i][2]+
                                " "+(prorrateoReferencialTotal < Double.parseDouble(matRs1[i][2]) ? "true" : "false")+" where id_promocion="+id_promocion);
                        }
                        
                        // si se basa en un valor fijo y en monto total de costo de instalaciones
                        if(!inst_objetivo_es_porcentaje && inst_objetivo_basado_en.compareTo("m")==0){   //  si se basa en porcentaje y en numero de instalaciones
                            objDataBase.ejecutar("update tbl_promocion set inst_objetivo_total="+matRs1[i][2]+
                                " "+(inst_objetivo_a_cumplir < Double.parseDouble(matRs1[i][2]) ? "true" : "false")+" where id_promocion="+id_promocion);
                        }
                        
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización cierre de promociones");
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generacion de anticipos de documentos cash");
        try{
            objDataBase.consulta("select proc_generar_anticipos_cash();");
        } finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generacion de anticipos de documentos cash");
        }
        
        
        
        
        
        
        
        
        
        
        
        
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de contabilizacion de amortizaciones.");
//        objDataBase.consulta("select proc_ejecutarAmotrtizacionesCreditosCompras();");
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de contabilizacion de amortizaciones");
        
        
        
        
        
        
        
        //  ENVIO DE NOTIFICACIONES DE VACACIONES VIA CORREO 
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando envio de notificaciones de vacaciones");
        try{
            ResultSet rs1 = objDataBase.consulta("select * from tbl_notificaciones_html where nombre_notificacion='mail_notificacion_planificacion_vacaciones';");
            String matRs1[][] = Matriz.ResultSetAMatriz(rs1);
            
            
            ResultSet rs = objDataBase.consulta("with A as(\n" +
                "	select id_empleado ,fecha_programada1 as fecha, dias_fecha1 as dias \n" +
                "	from tbl_empleado_vacaciones_anio \n" +
                "	where fecha_programada1 is not null \n" +
                "	union \n" +
                "	select id_empleado ,fecha_programada2 as fecha, dias_fecha2 as dias \n" +
                "	from tbl_empleado_vacaciones_anio \n" +
                "	where fecha_programada2 is not null \n" +
                "	union \n" +
                "	select id_empleado ,fecha_programada3 as fecha, dias_fecha3 as dias \n" +
                "	from tbl_empleado_vacaciones_anio \n" +
                "	where fecha_programada3 is not null \n" +
                "	union \n" +
                "	select id_empleado ,fecha_programada4 as fecha, dias_fecha4 as dias \n" +
                "	from tbl_empleado_vacaciones_anio \n" +
                "	where fecha_programada4 is not null \n" +
                "	union \n" +
                "	select id_empleado ,fecha_programada5 as fecha, dias_fecha5 as dias \n" +
                "	from tbl_empleado_vacaciones_anio \n" +
                "	where fecha_programada5 is not null \n" +
                ") \n" +
                "select A.*, E.id_sucursal, E.nombre, E.apellido, E.email from A inner join tbl_empleado as E on E.id_empleado = A.id_empleado \n" +
                "where estado and not eliminado and generar_rol and (fecha - '14 day'::interval)::date = now()::date;");
            while(rs.next()){
                int idSucursal = rs.getString("id_sucursal")!=null ? rs.getInt("id_sucursal") : 1;
                String nombre = rs.getString("nombre")!=null ? rs.getString("nombre") : "";
                String apellido = rs.getString("apellido")!=null ? rs.getString("apellido") : "";
                String fechaVacaciones = rs.getString("fecha")!=null ? rs.getString("fecha") : "";
                String dias = rs.getString("dias")!=null ? rs.getString("dias") : "";
                String email = rs.getString("email")!=null ? rs.getString("email") : "";
                
                String mailcc = matRs1[0][ 5+idSucursal ];
                String msg = matRs1[0][ 2 ]
                                .replace("_EMPLEADO_", nombre + " " + apellido)
                                .replace("_FECHA_", fechaVacaciones)
                                .replace("_DIAS_", dias);
                
//                System.out.println(email + ", " + mailcc + " => " + msg + "\n\r");
                
                Correo.enviar(Parametro.getSvrMail(), 
                            Parametro.getSvrMailPuerto(), 
                            Parametro.getRemitente(), 
                            Parametro.getRemitenteClave(), 
                            email, 
                            mailcc, 
                            "", 
                            "NOTIFICACION, AVISO DE SALIDA A VACACIONES", 
                            new StringBuilder(msg), 
                            true,
                            null
                );
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización envio de notificaciones de vacaciones");
        }
        
        
        
        
        
        
        
        if( dia == 15 ) {
        
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generacion de ordenes de trabajo de desinstalacion a clientes morosos + de 6 meses");
            try{
                objDataBase.consulta("select proc_generarInstalacionesPorRetirarMorososCR();");
            } finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generacion de ordenes de trabajo de desinstalacion a clientes morosos + de 6 meses");
            }
        
        }
        
        
        
        
        
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando actualzacion de obligatoriedad de certificado digital");
        try{
            objDataBase.consulta("update tbl_empleado set obligado_firmar =true where obligado_firmar =false and estado =true and eliminado =false and generar_rol =true  and get_duracion_fechas( now() ::timestamp,fecha_ingreso::timestamp , 'MM')::int>1;");
        } finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualzacion de obligatoriedad de certificado digital");
        }
        
        
        
        
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando carga de respaldo de timbrados de sucursales");
        SQLServer objSQL = new SQLServer( Parametro.getMsSqlIp(), Parametro.getMsSqlPuerto(), Parametro.getMsSqlBaseDatos(), Parametro.getMsSqlUsuario(), Parametro.getMsSqlClave() );
        try{
            
            ResultSet rs = objDataBase.consulta("select * from tbl_biometricos where not eliminado and activo");
            while(rs.next()){
                
                String idSucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "";
                String servidorSuc = rs.getString("ip_biometrico")!=null ? rs.getString("ip_biometrico") : "";
                int puertoSuc = rs.getString("puerto_biometrico")!=null ? rs.getInt("puerto_biometrico") : 1433;
                String baseSuc = rs.getString("db_biometrico")!=null ? rs.getString("db_biometrico") : "";
                String usuarioSuc = rs.getString("usuario_biometrico")!=null ? rs.getString("usuario_biometrico") : "";
                String claveSuc = rs.getString("clave_biometrico")!=null ? rs.getString("clave_biometrico") : "";
                
                SQLServer dbBiometricoSuc = new SQLServer(servidorSuc, puertoSuc, baseSuc, usuarioSuc, claveSuc);
                try{
                    ResultSet rs2 = dbBiometricoSuc.consulta("select * from RALog where convert(datetime, [date], 103) >= convert( datetime, dateadd(day, -1, getDate()) ,103)");
//                    ResultSet rs2 = dbBiometricoSuc.consulta("select * from RALog");
                    while(rs2.next()){
                                                
                        if( !objSQL.ejecutar("insert into ralog_sucursales(id_sucursal, RN, GID, FID, UID, Name, Date, Time, FacID, InOutOption, Other, Latitude, Longitude) values("
                                + "'"+idSucursal+"', "
                                + "'"+ (rs2.getString("RN")!=null ? rs2.getString("RN") : "") + "', "  
                                + "'"+ (rs2.getString("GID")!=null ? rs2.getString("GID") : "") + "', " 
                                + "'"+ (rs2.getString("FID")!=null ? rs2.getString("FID") : "") + "', " 
                                + "'"+ (rs2.getString("UID")!=null ? rs2.getString("UID") : "") + "', " 
                                + "'"+ (rs2.getString("Name")!=null ? rs2.getString("Name") : "") + "', " 
                                + "'"+ (rs2.getString("Date")!=null ? rs2.getString("Date") : "") + "', " 
                                + "'"+ (rs2.getString("Time")!=null ? rs2.getString("Time") : "") + "', "         
                                + "'"+ (rs2.getString("FacID")!=null ? rs2.getString("FacID") : "") + "', " 
                                + "'"+ (rs2.getString("InOutOption")!=null ? rs2.getString("InOutOption") : "" )+ "', " 
                                + "'"+ (rs2.getString("Other")!=null ? rs2.getString("Other") : "") + "', " 
                                + "'"+ (rs2.getString("Latitude")!=null ? rs2.getString("Latitude") : "") + "', " 
                                + "'"+ (rs2.getString("Longitude")!=null ? rs2.getString("Longitude") : "") + "' " 
                                + ")") ) {
                            
                            System.out.println("error insert: " + objSQL.getError() );
                            
                        }
                    }
                }catch(Exception ex2){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": error al conectarse al biomnetrico " + servidorSuc + ex2.getMessage());
                }finally{
                    dbBiometricoSuc.cerrar();
                }
            }
        }catch(Exception ex){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": error al conectarse al biomnetrico " + Parametro.getMsSqlIp() + ex.getMessage());   
        } finally{
            objSQL.cerrar();
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de carga de respaldo de timbrados de sucursales");
        }    
            
        
        
        
        objDataBase.cerrar();
    }
    
}
