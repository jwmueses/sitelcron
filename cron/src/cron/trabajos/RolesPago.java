/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge
 */
public class RolesPago implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio para la creacion de Rubros de Nomina.");
        try{
            objDataBase.consulta("insert into tbl_rubro_cont_det (id_rubro_cont, id_empleado, rubro, periodo,monto,id_sucursal) " +
                                "(select p.id_rubro_cont,p.id_empleado, c.rubro, date_trunc('month',current_date), p.monto, p.id_sucursal from tbl_rubro_cont_per p " +
                                "join tbl_rubro_cont c on p.id_rubro_cont=c.id_rubro_cont " +
                                "where date 'now()'>c.fecha_inicio and c.tipo=false and p.id_empleado not in ( " +
                                "select d.id_empleado from tbl_rubro_cont_det d where d.periodo=date_trunc('month',current_date) and p.id_rubro_cont=d.id_rubro_cont));");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de la creacion de Rubros de Nomina");
        
        
        
        
            
        //      Roles de pago
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando cálculos de Roles de Pago");
        try{
            ResultSet rsSueldoBasico = objDataBase.consulta("select sbu from tbl_salario_basico_unificado where anio=date_part('year', now()) ::int;");
            String sueldoBasico="0";
            if(rsSueldoBasico.next()){
                sueldoBasico= (rsSueldoBasico.getString(1)!=null) ? rsSueldoBasico.getString(1) : "0";
            }
            ResultSet rsEmpleados = objDataBase.consulta("select e.id_empleado, (date_trunc('month',current_date)-cast('1 month' as interval)+cast('25 day' as interval)) as fecha_inicial,(date_trunc('month',current_date)+cast('24 day' as interval)) as fecha_final,\n" +
            "e.empleado, ((max(s.sueldo)*e.semana)/30)::numeric(13,2) as sueldo, \n" +
            "((s.sueldo/240)*2)::numeric(13,2) as extra, \n" +
            "((s.sueldo/240)*1.5)::numeric(13,2) as suplementaria,\n" +
            "(select (sum(num_horas)*60)+sum(num_minutos) from vta_empleado_horas_extras he where tipo='e' and he.id_empleado=e.id_empleado and he.estado='a' and he.id_rol_pagos is null group by empleado) as extraTot, \n" +
            "(select array_to_string(array_agg(id_empleado_hora_extra), ',') from vta_empleado_horas_extras he where tipo='e' and he.id_empleado=e.id_empleado and he.estado='a' and he.id_rol_pagos is null) as extraid, \n" +
            "(select (sum(num_horas)*60)+sum(num_minutos) from vta_empleado_horas_extras he where tipo='s' and he.id_empleado=e.id_empleado and he.estado='a' and he.id_rol_pagos is null group by empleado) as supleTot, \n" +
            "(select array_to_string(array_agg(id_empleado_hora_extra), ',') from vta_empleado_horas_extras he where tipo='s' and he.id_empleado=e.id_empleado and he.estado='a' and he.id_rol_pagos is null) as supleid, \n" +
            "(select sum(monto) from tbl_rubro_cont c join tbl_rubro_cont_det d on c.id_rubro_cont=d.id_rubro_cont where deducible=true and movimiento=true and d.id_empleado=e.id_empleado and d.periodo between (date_trunc('month',current_date)-cast('1 month' as interval)+cast('25 day' as interval)) and (date_trunc('month',current_date)+cast('24 day' as interval))) as sumDeducible,\n" +
            "case e.id_area \n" +
            "	when (select id_area from tbl_area where area like '%VENTAS%') then (select comision from tbl_instalacion_comision c where c.id_sucursal=e.id_sucursal and c.fecha_inicio=(date_trunc('month',current_date)-cast('1 month' as interval)+cast('25 day' as interval)))\n" +
            "	else -2\n" +
            "end as comisionVentas,\n" +
            "case e.id_rol \n" +
            "	when (select id_rol from tbl_rol where rol like '%JEFE_MAR%') then (select comision from tbl_instalacion_comision c where c.id_sucursal=0 and c.fecha_inicio=(date_trunc('month',current_date)-cast('1 month' as interval)+cast('25 day' as interval)))\n" +
            "	else -2\n" +
            "end as comisionJefeVentas,\n" +
            "cobra_14_mensual, cobra_f_r,cobra_13_mensual,\n" + 
            "(select valor from tbl_configuracion where parametro='aportacionPatrono') as aportacionPatrono,\n" +
            "(select valor from tbl_configuracion where parametro='aportacionIess') as aportacionIess,\n" +
            "(select valor from tbl_configuracion where parametro='aportacionIece') as aportacionIece,\n" +
            "(select valor from tbl_configuracion where parametro='aportacionSetec') as aportacionSetec, \n" + 
            "e.semana,e.fecha_ingreso, date_part('month', now()) - date_part('month', e.fecha_ingreso) +(date_part('year', now()) - date_part('year', e.fecha_ingreso))*12::int as meses, " + 
            "date_part('day', e.fecha_ingreso) as dia " +
            "from vta_empleado e \n" +
            "join tbl_escala_salarial s on e.id_cargo=s.id_cargo \n" +
            "where e.estado=true and generar_rol=true and s.vigencia_hasta is null \n" +
            "group by e.empleado,s.sueldo,e.id_empleado,periodo_14_sueldo, cobra_14_mensual, cobra_f_r,cobra_13_mensual,e.id_area,e.id_sucursal,e.id_rol,e.semana,e.fecha_ingreso;");
            while(rsEmpleados.next()){
               String cobraReservaGasto = "0.00";
               String id_empleado= (rsEmpleados.getString("id_empleado")!=null) ? rsEmpleados.getString("id_empleado") : "";
               String empleado= (rsEmpleados.getString("empleado")!=null) ? rsEmpleados.getString("empleado") : "";
               String fecha_inicial= (rsEmpleados.getString("fecha_inicial")!=null) ? rsEmpleados.getString("fecha_inicial") : "";
               String fecha_final= (rsEmpleados.getString("fecha_final")!=null) ? rsEmpleados.getString("fecha_final") : "";
               String sueldo= (rsEmpleados.getString("sueldo")!=null) ? rsEmpleados.getString("sueldo") : "0";
               String valorExtra= (rsEmpleados.getString("extra")!=null) ? rsEmpleados.getString("extra") : "0";
               String valorSuplementaria= (rsEmpleados.getString("suplementaria")!=null) ? rsEmpleados.getString("suplementaria") : "0";
               String TotalExtra= (rsEmpleados.getString("extratot")!=null) ? rsEmpleados.getString("extratot") : "0";
               String TotalSuplementaria= (rsEmpleados.getString("supletot")!=null) ? rsEmpleados.getString("supletot") : "0";
               String idsExtra= (rsEmpleados.getString("extraid")!=null) ? rsEmpleados.getString("extraid") : "-2";
               String idsSuplementaria= (rsEmpleados.getString("supleid")!=null) ? rsEmpleados.getString("supleid") : "-2";
               String cobra14= (rsEmpleados.getString("cobra_14_mensual")!=null) ? rsEmpleados.getString("cobra_14_mensual") : "0";
               String sumDeducible= (rsEmpleados.getString("sumDeducible")!=null) ? rsEmpleados.getString("sumDeducible") : "0";
               String comisionVentas= (rsEmpleados.getString("comisionVentas")!=null) ? rsEmpleados.getString("comisionVentas") : "0";
               String comisionJefeVentas= (rsEmpleados.getString("comisionJefeVentas")!=null) ? rsEmpleados.getString("comisionJefeVentas") : "0";
               String cobraReserva= (rsEmpleados.getString("cobra_f_r")!=null) ? rsEmpleados.getString("cobra_f_r") : "0";
               String cobra13= (rsEmpleados.getString("cobra_13_mensual")!=null) ? rsEmpleados.getString("cobra_13_mensual") : "0";
               String aportacionPatrono= (rsEmpleados.getString("aportacionPatrono")!=null) ? rsEmpleados.getString("aportacionPatrono") : "0";
               String aportacionIess= (rsEmpleados.getString("aportacionIess")!=null) ? rsEmpleados.getString("aportacionIess") : "0";
               String aportacionIece= (rsEmpleados.getString("aportacionIece")!=null) ? rsEmpleados.getString("aportacionIece") : "0";
               String aportacionSetec= (rsEmpleados.getString("aportacionSetec")!=null) ? rsEmpleados.getString("aportacionSetec") : "0";
               String semana= (rsEmpleados.getString("semana")!=null) ? rsEmpleados.getString("semana") : "0";
               String meses= (rsEmpleados.getString("meses")!=null) ? rsEmpleados.getString("meses") : "0";
               String dia= (rsEmpleados.getString("dia")!=null) ? rsEmpleados.getString("dia") : "0";
               Double extra = Double.parseDouble(TotalExtra)*(Double.parseDouble(valorExtra)/60);
               Double suple = Double.parseDouble(TotalSuplementaria)*(Double.parseDouble(valorSuplementaria)/60);
               Double comision=0.00;

               if(Double.parseDouble(comisionVentas)>0){
                   comision=Double.parseDouble(comisionVentas);
               }
               if(Double.parseDouble(comisionVentas)>0&&Double.parseDouble(comisionJefeVentas)>0){
                   comision=Double.parseDouble(comisionJefeVentas);
               }
               if(cobra14.compareTo("t")==0){
                   cobra14=""+(Double.parseDouble(sueldoBasico)/12);
               }
               if(cobra14.compareTo("f")==0){
                   cobra14="-"+(Double.parseDouble(sueldoBasico)/12);
               }
               if(Integer.parseInt(semana)<30){
                   cobra14=""+(Double.parseDouble(cobra14)/2);
               }
               if(cobra13.compareTo("t")==0){
                   cobra13=""+((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))/12);
               }
               if(cobra13.compareTo("f")==0){
                   cobra13="-"+((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))/12);
               }
               if(Integer.parseInt(meses)>=12)
               {
                   if(cobraReserva.compareTo("t")==0){
//                        cobraReserva=""+((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))/12);
                        cobraReserva=""+((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))  * 8.33);
                        if(Integer.parseInt(meses)==12&&Integer.parseInt(dia)<30){
                            cobraReserva =""+((Double.parseDouble(cobraReserva) * (30-Integer.parseInt(dia)))/30);
                        }
                   }
                   if(cobraReserva.compareTo("f")==0){
//                       cobraReserva=""+((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))/12);
                       cobraReserva=""+((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo)) * 8.33);
                        if(Integer.parseInt(meses)==12&&Integer.parseInt(dia)<30){
                            cobraReserva =""+((Double.parseDouble(cobraReserva) * (30-Integer.parseInt(dia)))/30);
                        }
                       cobraReservaGasto = cobraReserva;
                   }
               }
               if(Integer.parseInt(meses)<12){
                   cobraReserva="0.00";
                   cobraReservaGasto="0.00";
               }


               aportacionPatrono=(((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))*Double.parseDouble(aportacionPatrono))/100)+"";
               aportacionIess=(((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))*Double.parseDouble(aportacionIess))/100)+"";
               aportacionIece=(((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))*Double.parseDouble(aportacionIece))/100)+"";
               aportacionSetec=(((comision+extra+suple+Double.parseDouble(sumDeducible)+Double.parseDouble(sueldo))*Double.parseDouble(aportacionSetec))/100)+"";
               try{
                   String idRol=objDataBase.insert("insert into tbl_rol_pagos (id_empleado,fecha_inicial,fecha_final,salario,horas_extras,horas_suple,decimo_cuarto,decimo_tercero,comision,fondos_reserva,fondos_reserva_gasto,aporte_patronal,aporte_iess,iece,setec,estado)"
                           + "values ("+id_empleado+",'"+fecha_inicial+"','"+fecha_final+"',"+sueldo+","+extra+","+suple+","+cobra14+","+cobra13+","+comision+","+cobraReserva+","+cobraReservaGasto+","+aportacionPatrono+","+aportacionIess+","+aportacionIece+","+aportacionSetec+",'r')");
                   if(idsExtra.compareTo("-2")!=0){
                       objDataBase.ejecutar("update tbl_empleado_hora_extra set id_rol_pagos="+idRol+" where id_empleado_hora_extra in ("+idsExtra+")");
                   }
                   if(idsSuplementaria.compareTo("-2")!=0){
                       objDataBase.ejecutar("update tbl_empleado_hora_extra set id_rol_pagos="+idRol+" where id_empleado_hora_extra in ("+idsSuplementaria+")");
                   }
                   objDataBase.ejecutar("update tbl_prestamos_detalle d set id_rol_pagos="+idRol+", modo_pago='r' from tbl_prestamos p where p.id_empleado="+id_empleado+" and fecha_pago between '"+fecha_inicial+"' and '"+fecha_final+"' and d.numero_documento in ('') and p.id_prestamo=d.id_prestamo and p.estado='a'");
                   objDataBase.ejecutar("update tbl_rubro_cont_det set id_rol_pagos="+idRol+", estado='e' where id_empleado="+id_empleado+" and periodo between '"+fecha_inicial+"' and '"+fecha_final+"'");
                   objDataBase.ejecutar("update tbl_empleado_memo set id_rol_pagos="+idRol+" where id_empleado="+id_empleado+" and asunto=2 and estado='a' and fecha between '"+fecha_inicial+"' and '"+fecha_final+"'");

                   objDataBase.consulta("select proc_calcularrol("+idRol+", NULL, NULL, true)");
                   
               }catch (Exception ex) {
                   System.out.println("Error al generar rol de pagos de: "+empleado+" error: "+ex.getMessage());
               }
            }
            
        }catch (SQLException ex) {
            System.out.println("Error al generar rol de pagos: "+ex.getMessage());
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando cálculos de Roles de Pago");
            objDataBase.cerrar();
        }
        
    }
     
}
