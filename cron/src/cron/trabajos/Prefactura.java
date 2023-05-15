/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge
 */
public class Prefactura implements Job{
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de estados de instalaciones.");
        objDataBase.consulta("select proc_robot();");
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de estados de instalaciones");

        
        
        //  Genera ordenes de trabajo por retirar  del 1 al 15 de
        /*System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de ordenes de trabajo por retirar del 1 al 15");
        try{
            objDataBase.consulta("select proc_generarinstalacionesporretirar115('"+Fecha.getFecha("ISO")+"');");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de registro de ordenes de trabajo por retirar del 1 al 15");
        }*/
        
        
        
        
        
        
        //      Actualizar dias de conexion de Prefacturas de periodos anteriores
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando actualización de dias de conexion de prefacturas de periodos anteriores");
        try{
            //  postpago
            objDataBase.ejecutar("with tmp as( \n" +
                "	select id_prefactura,id_instalacion,periodo, dias_conexion from tbl_prefactura \n" +
                "	 where fecha_emision is null and periodo >= (now()::date - '2 month'::interval)::date \n" +
                "	and id_instalacion in(select distinct id_instalacion from tbl_prefactura \n" +
                "		where fecha_emision is null and periodo = (now()::date - '2 month'::interval)::date \n" +
                "		and id_instalacion in(select id_instalacion from tbl_instalacion where convenio_pago ='1' and estado_servicio = 'c') \n" +
                "		) \n" +
                "		order by id_instalacion,periodo \n" +
                ") \n" +
                "update tbl_prefactura as P set dias_conexion =5, recalcular =true, detalle_suspencion =' antes del corte' \n" +
                "where dias_conexion=30 and p.id_prefactura in(select id_prefactura from tmp where tmp.periodo = (now()::date - '1 month'::interval)::date);");
            
            //  prepago Quitos
            objDataBase.ejecutar("with tmp as( \n" +
                "	select id_prefactura,id_instalacion,periodo, dias_conexion from tbl_prefactura \n" +
                "	 where fecha_emision is null and periodo >=(now()::date - '1 month'::interval)::date and  dias_conexion=30 \n" +
                "	and id_instalacion in(select distinct id_instalacion from tbl_prefactura \n" +
                "		where fecha_emision is null and periodo =(now()::date - '1 month'::interval)::date \n" +
                "		and id_instalacion in(select id_instalacion from tbl_instalacion where convenio_pago ='0' and estado_servicio ='c' and id_sucursal in(7,11)) \n" +
                "		) \n" +
                "		order by id_instalacion,periodo \n" +
                ") \n" +
                "update tbl_prefactura as P set dias_conexion=15, recalcular =true, detalle_suspencion =' antes del corte' \n" +
                "where dias_conexion=30 and p.id_prefactura in(select id_prefactura from tmp where tmp.periodo=(now()::date - '1 month'::interval)::date)");
            
            //  prepago menos Quitos
            objDataBase.ejecutar("with tmp as( \n" +
                "	select id_prefactura,id_instalacion,periodo, dias_conexion from tbl_prefactura \n" +
                "	 where fecha_emision is null and periodo >=(now()::date - '1 month'::interval)::date and  dias_conexion=30 \n" +
                "	and id_instalacion in(select distinct id_instalacion from tbl_prefactura \n" +
                "		where fecha_emision is null and periodo =(now()::date - '1 month'::interval)::date \n" +
                "		and id_instalacion in(select id_instalacion from tbl_instalacion where convenio_pago ='0' and estado_servicio ='c' and id_sucursal not in(7,11)) \n" +
                "		) \n" +
                "		order by id_instalacion,periodo \n" +
                ") \n" +
                "update tbl_prefactura as P set dias_conexion=5, recalcular =true, detalle_suspencion =' antes del corte' \n" +
                "where dias_conexion=30 and p.id_prefactura in(select id_prefactura from tmp where tmp.periodo=(now()::date - '1 month'::interval)::date)");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando actualización de dias de conexion de prefacturas de periodos anteriores");
        }
        
        
        
        
        //      Generar Prefacturas
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de prefacturas");
        try{
            objDataBase.consulta("select proc_generarPreFacturas();");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando generación de prefacturas");
        }
        
        
        
        
        
        //      Generar Rubros permanentes 
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de rubros permanentes de instalaciones");
        try{
            String fechaPrefactura = Fecha.getAnio() + "-" + Fecha.getMes() + "-01";
//            try{
//                ResultSet rsPrefactura =  objDataBase.consulta("select max(fecha_prefactura) from tbl_prefactura");
//                if(rsPrefactura.next()){
//                    fechaPrefactura = rsPrefactura.getString(1)!=null ? rsPrefactura.getString(1) : fechaPrefactura;
//                    rsPrefactura.close();
//                }
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
            
// del periodo actual, prepago
            objDataBase.ejecutar("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, canproductos, monto) "
                    + "select distinct I.id_sucursal, RI.id_rubro, I.id_instalacion, '"+fechaPrefactura+"'::date, R.rubro, RI.cantidad, RI.monto "
                    + "from ((tbl_rubro as R inner join tbl_rubro_instalacion as RI on RI.id_rubro=R.id_rubro) "
                    + "inner join tbl_instalacion as I on I.id_instalacion=RI.id_instalacion) "
                    + "where '"+fechaPrefactura+"' between R.fecha_inicio and R.fecha_fin and R.temporal=false and R.bloqueado=false and I.convenio_pago='0' "
                    + "and I.id_instalacion not in (select id_instalacion from tbl_prefactura_rubro where id_rubro=RI.id_rubro and periodo='"+fechaPrefactura+"')");
            
// del periodo anterior, postpago
            objDataBase.ejecutar("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, canproductos, monto) "
                    + "select distinct I.id_sucursal, RI.id_rubro, I.id_instalacion, '"+fechaPrefactura+"'::date -'1 month'::interval, R.rubro, RI.cantidad, RI.monto "
                    + "from ((tbl_rubro as R inner join tbl_rubro_instalacion as RI on RI.id_rubro=R.id_rubro) "
                    + "inner join tbl_instalacion as I on I.id_instalacion=RI.id_instalacion) "
                    + "where '"+fechaPrefactura+"'::date-'1 month'::interval between R.fecha_inicio and R.fecha_fin and R.temporal=false and R.bloqueado=false and I.convenio_pago='1' "
                    + "and I.id_instalacion not in (select id_instalacion from tbl_prefactura_rubro where id_rubro=RI.id_rubro and periodo='"+fechaPrefactura+"'::date-'1 month'::interval)");
            
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando generación de rubros permanentes de instalaciones");
        }
        
        
        
        
        
        
        //      CARGO LOS DIAS DE CONEXION ANTES DEL CORTE AL PERIODO ACTUAL DE CLIENTES DE QUITO
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de dias de conexion antes del corte para clientes prepago");
            try{
                List sql = new ArrayList();

                String periodo = Fecha.getAnio() + "-" + Fecha.getMes() + "-01";
//                ResultSet rsPeriodo = objDataBase.consulta("(select max(periodo) from tbl_prefactura)");
//                if(rsPeriodo.next()){
//                    periodo = rsPeriodo.getString(1)!=null ? rsPeriodo.getString(1) : periodo;
//                    rsPeriodo.close();
//                }

                int diaCortesPrepago = 16;
                ResultSet rsDiaCorte = objDataBase.consulta("SELECT valor FROM tbl_configuracion where parametro='dia_cortes_prepago';");
                if(rsDiaCorte.next()){
                    diaCortesPrepago = rsDiaCorte.getString(1)!=null ? rsDiaCorte.getInt(1) : 16;
                    rsDiaCorte.close();
                }
                diaCortesPrepago--;

//                //  actualizo los rubros adicionales con dias de conexion antes del corte al ultimo periodo
//                objDataBase.ejecutar("update tbl_prefactura_rubro set periodo='"+periodo+"' where id_instalacion in( select id_instalacion from vta_prefactura where periodo < '"
//                        +periodo+"' and convenio_pago='0' and estado_servicio='c' and id_sucursal in('7','11') ) and id_rubro=15 and periodo < '"+periodo+"'");
//
//                //  registro de dias de conexion antes del corte como rubros adicionales
//                sql.add("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, canproductos, monto) "
//                        + "select id_sucursal, 15, id_instalacion, '"+periodo+"', '"+diaCortesPrepago+" días de servicio antes del corte del servicio en el período ' || txt_periodo || ' del plan ' || plan "
//                        + ", "+diaCortesPrepago+", (total_internet/30)*"+diaCortesPrepago+" from vta_prefactura where convenio_pago='0' and estado_servicio='c' and id_sucursal in('7','11') and periodo < '"
//                        +periodo+"' and id_instalacion not in (select id_instalacion from tbl_prefactura_rubro where periodo='"+periodo+"'::date and id_rubro=15);");

                sql.add("update tbl_prefactura set dias_conexion="+diaCortesPrepago+", recalcular=true where periodo = ('"+periodo + "'::date - '1 month'::interval)::date "
                        +" and fecha_emision is null and id_instalacion in (select id_instalacion from vta_prefactura where convenio_pago='0' and estado_servicio='c' and id_sucursal in('7','11') )");

                objDataBase.transacciones(sql);

            }catch(Exception e){    
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error: " + e.getMessage() );
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando generación de dias de conexion antes del corte para clientes prepago");
            }
        
        
        
        
        
            
            
            //      CARGO LOS DIAS DE CONEXION ANTES DEL CORTE AL PERIODO ACTUAL DE TODAS LAS SUCURALES EXEPTO QUITOS
//            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando generación de dias de conexion antes del corte para clientes prepago");
//            try{
//                List sql = new ArrayList();
//
//                String periodo = Fecha.getAnio() + "-" + Fecha.getMes() + "-01";
//                ResultSet rsPeriodo = objDataBase.consulta("(select max(periodo) from tbl_prefactura)");
//                if(rsPeriodo.next()){
//                    periodo = rsPeriodo.getString(1)!=null ? rsPeriodo.getString(1) : periodo;
//                    rsPeriodo.close();
//                }
//
//                int diaCortesPrepago = 6;
//                ResultSet rsDiaCorte = objDataBase.consulta("SELECT valor FROM tbl_configuracion where parametro='dia_cortes';");
//                if(rsDiaCorte.next()){
//                    diaCortesPrepago = rsDiaCorte.getString(1)!=null ? rsDiaCorte.getInt(1) : 6;
//                    rsDiaCorte.close();
//                }
//                diaCortesPrepago--;
//
//                //  actualizo los rubros adicionales con dias de conexion antes del corte al ultimo periodo
//                objDataBase.ejecutar("update tbl_prefactura_rubro set periodo='"+periodo+"' where id_instalacion in( select id_instalacion from vta_prefactura where periodo < '"
//                        +periodo+"' and convenio_pago='0' and estado_servicio='c' and id_sucursal not in('7','11') ) and id_rubro=15 and periodo < '"+periodo+"'");
//
//                //  registro de dias de conexion antes del corte como rubros adicionales
//                sql.add("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, periodo, rubro, canproductos, monto) "
//                        + "select id_sucursal, 15, id_instalacion, '"+periodo+"', '"+diaCortesPrepago+" días de servicio antes del corte del servicio en el período ' || txt_periodo || ' del plan ' || plan "
//                        + ","+diaCortesPrepago+" , (total_internet/30)*"+diaCortesPrepago+" from vta_prefactura where convenio_pago='0' and estado_servicio='c' and id_sucursal not in('7','11') and periodo < '"
//                        +periodo+"' and id_instalacion not in (select id_instalacion from tbl_prefactura_rubro where periodo='"+periodo+"'::date and id_rubro=15);");
//
//
//        //      se deberia actualizar el 5 en el corte
//                sql.add("update tbl_prefactura set dias_conexion="+diaCortesPrepago+", recalcular=true where periodo = ('"+periodo + "'::date - '2 month'::interval)::date " 
//                        +" and fecha_emision is null and id_instalacion in (select id_instalacion from vta_prefactura where convenio_pago='0' and estado_servicio='c' and id_sucursal not in('7','11') )");
//
//                objDataBase.transacciones(sql);
//
//            }catch(Exception e){    
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error: " + e.getMessage() );
//            }finally{
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando generación de dias de conexion antes del corte para clientes prepago");
//            }
        
        
        
        
        
        
        
        
        
        
        
        //  Cálculos de Prefacturas  
        String fecha = Fecha.getAnio() + "-" + Fecha.getMes() + "-01";
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando cálculos de prefacturas");
        try{
            
            //  actualizacion de rubros en instalaciones prepago
            objDataBase.ejecutar("update tbl_prefactura_rubro set periodo='"+fecha+"' where estadocobro=false and (idproductos is not null or id_rubro>=14) "
                    + "and id_rubro<>25 and id_instalacion in(select id_instalacion from tbl_instalacion where estado_servicio='a' and convenio_pago='0')");
            
            //  actualizacion de rubros en instalaciones postpago
            objDataBase.ejecutar("update tbl_prefactura_rubro set periodo=('"+fecha+"'::date - '1 month'::interval)::date where estadocobro=false and (idproductos is not null or id_rubro>=14) "
                    + "and id_rubro<>25 and id_instalacion in(select id_instalacion from tbl_instalacion where estado_servicio='a' and convenio_pago='1')");
            
            //  actualizar prefacturas para que se recalculen los cambios de periodos de rubros
            objDataBase.ejecutar("update tbl_prefactura set recalcular=true where fecha_emision is null and recalcular=false and "
                    + "id_instalacion in(select distinct id_instalacion from tbl_prefactura_rubro where estadocobro=false and (idproductos is not null or id_rubro>=14 or id_rubro=1) )");
            
            ResultSet rs = objDataBase.consulta("select id_prefactura from vta_prefactura where periodo>='"+fecha+"'::date or recalcular=true");
            while(rs.next()){
                String idPrefactura = rs.getString("id_prefactura")!=null ? rs.getString("id_prefactura") : "-1";
                try{
                    objDataBase.consulta("select proc_calcularPreFactura("+idPrefactura+", false);");
                }catch(Exception e){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefactura. " + e.getMessage());
                }
            }
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en el recalculo de prefacturas." + e.getMessage());
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de cálculos de prefacturas");
        }
        
        
        
        
        
        
        
        
        //      actualizacion de saldos de los libros mayores de todo el plan de cuentas
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando actualizacion de saldos de los libros mayores de todo el plan de cuentas");
        try{
            String fechaIni = (Fecha.getAnio()-1) + "-12-31";
            objDataBase.consulta("select proc_actualizaSaldosLibrosTodos('" + fechaIni + "'::date);");
        }finally{
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando actualizacion de saldos de los libros mayores de todo el plan de cuentas");
        }
        
        
        
        
        
        
        // emision de prefacturas con clientes que tienen anticipos
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando emisión de facturas a clientes con anticipos");
//        try{
//            ResultSet rsRubrosAdicionales = objDataBase.consulta("SELECT * FROM vta_prefactura_rubro WHERE id_instalacion in(select id_instalacion from vta_prefactura_todas as F where (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente) >=4 and fecha_emision is null)");
//            String rubrosAdicionales[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales);
//            
//            FacturaVenta objFacturaVenta = new FacturaVenta( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
//            
//            
//            //  anticipos
//            ResultSet rsAnticipos = objDataBase.consulta("select id_cliente_anticipo, id_cliente, saldo from tbl_cliente_anticipo where saldo >= 1 order by id_cliente, saldo desc;");
//            String matAnticipos[][] = Matriz.ResultSetAMatriz(rsAnticipos);
//            
//            
//            //  emisión con forma de pago anticipo
//            ResultSet rsFacturar = objDataBase.consulta("select *, total as total_comision from vta_prefactura_todas as F where (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente) >= F.total and fecha_emision is null;");
//             
            
            
            //  emisión con forma de pago crédito y cobrar proporcional con anticipo
//            int pminValorVajaCredito = 50;
//            try{
//                ResultSet rs = objDataBase.consulta("SELECT valor FROM tbl_configuracion where parametro='pmin_valor_vaja_credito'");
//                if(rs.next()){
//                    pminValorVajaCredito = rs.getString("valor")!=null ? Integer.parseInt( rs.getString("valor") ) : 50;
//                    rs.close();
//                }
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            ResultSet rsCredito = objDataBase.consulta("select *, total as total_comision from vta_prefactura_todas as F where (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente) >= (F.total*"+pminValorVajaCredito+"/100) and (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente) < F.total and fecha_emision is null;");
//            
            
//            objFacturaVenta.emitir(rsFacturar, rubrosAdicionales, matAnticipos, 100);
//            objFacturaVenta.emitir(rsCredito, rubrosAdicionales, matAnticipos, pminValorVajaCredito); 
            
//            objFacturaVenta.cerrarConexiones();
//        }finally{
//            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de emisión de facturas a clientes con anticipos");
//        }
        
        
        
        objDataBase.cerrar();
        
    }
     
}
