package cron.trabajos;
 
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
 
public class ServidoresCorte implements Job{
    
//  PROMOCIONES NOCTURNAS
    long velocidadPromocion = 0;
    Date horaInicioPromocion;
    Date horaFinPromocion;
    Date horaActualPromocion = new Date( Fecha.getTimeStamp( Fecha.getFecha("ISO"), Fecha.getHora() ) );
    boolean noct_inst_prepago = false;
    boolean noct_inst_postpago = false;
    boolean noct_aplica_cambio_plan = false;
    CorreoSaitel objCorreo = null;
    
//            String tmp = System.getProperty("java.io.tmpdir");
    String tmp = "/opt/lampp/htdocs/anexos/ftp_cortes/";
    List hilosEnEjecucion = new ArrayList();
    
//  PROMOCIONES GENERALES
    
    String matPromociones[][];
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int anioActual = Fecha.getAnio();
        int mesActual = Fecha.getMes();
        int diaActual = Fecha.getDia();
        String fecha_actual = anioActual + "-" + mesActual + "-" + diaActual;
        long iniEjecucion = Fecha.getTimeStamp(fecha_actual, "05:29");
        long finEjecucion = Fecha.getTimeStamp(fecha_actual, "23:59");
        long iniEjecucionMikrotik = Fecha.getTimeStamp(fecha_actual, "00:00");
        long finEjecucionMikrotik = Fecha.getTimeStamp(fecha_actual, "00:59");
        long finEmisionFacturas = Fecha.getTimeStamp(fecha_actual, "18:55");
        long timeActual = Fecha.getTimeStamp(fecha_actual, Fecha.getHora());
        
        this.objCorreo = new CorreoSaitel( Parametro.getRed_social_ip(), Parametro.getRed_social_esquema(), Parametro.getRed_social_puerto(), Parametro.getRed_social_db(), Parametro.getRed_social_usuario(), Parametro.getRed_social_clave() );
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        Notificacion objNotificacion = new Notificacion( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        String doc_ip = Parametro.getDocumentalIp();      //  127.0.0.1     pruebas = 192.168.217.16
        int doc_puerto = Parametro.getDocumentalPuerto();
        String doc_db = Parametro.getDocumentalBaseDatos();
        String doc_usuario = Parametro.getDocumentalUsuario();
        String doc_clave = Parametro.getDocumentalClave();
        DataBase objDocumental = new DataBase( doc_ip, doc_puerto, doc_db, doc_usuario, doc_clave );
                
        String permitirCortesCron = "true";
        String modoSincronizacionMikrotiks = "scripts";
        int actualizarMikrotiksEnLinea = 10;
        boolean resincronizarMikrotiks = false;
        try{
            ResultSet rs = objDataBase.consulta("select parametro, lower(valor) as valor from tbl_configuracion where parametro in('permitir_cortes_cron', 'modoSincronizacionMikrotiks', 'actualizarMikrotiksEnLinea', 'resincronizarMikrotiks')");
            while(rs.next()){
                String parametro = rs.getString("parametro")!=null ? rs.getString("parametro") : "";

                if( parametro.compareTo("permitir_cortes_cron") == 0) {
                    permitirCortesCron = rs.getString("valor")!=null ? rs.getString("valor") : "true";
                }
                if( parametro.compareTo("modoSincronizacionMikrotiks") == 0) {
                    modoSincronizacionMikrotiks = rs.getString("valor")!=null ? rs.getString("valor") : "scripts";
                }
                if( parametro.compareTo("actualizarMikrotiksEnLinea") == 0) {
                    actualizarMikrotiksEnLinea = rs.getString("valor")!=null ? rs.getInt("valor") : 10;
                }
                if( parametro.compareTo("resincronizarMikrotiks") == 0) {
                    resincronizarMikrotiks = rs.getString("valor")!=null ? rs.getBoolean("valor") : false;
                }
            }
            rs.close();
        }catch(Exception e){
            System.out.println("Error obteniendo configuracion(permitir_cortes_cron) " + e.getMessage() );
            e.printStackTrace();
        }
        
        
                
        try{
        //  hora actual   >=  05:29       && hora actual <= 18:55 
            if(timeActual >= iniEjecucion && timeActual <= finEjecucion ){
                
                try{
                    ResultSet rs = objDataBase.consulta("SELECT * FROM tbl_promocion_megas order by fecha_creacion desc;");
                    if (rs.next()) {
                        
                        String fecha_inicio = (rs.getString("fecha_inicio") != null) ? rs.getString("fecha_inicio") : "";
                        if(fecha_inicio.compareTo("")!=0){
                            
                            String fecha_termino = (rs.getString("fecha_termino") != null) ? rs.getString("fecha_termino") : "";
                            if(fecha_termino.compareTo("")==0){
                                fecha_termino = Fecha.getFecha("ISO");
                            }
                            long timeActualFecha = Fecha.getTimeStamp( Fecha.getFecha("ISO") );
                            if( Fecha.getTimeStamp(fecha_inicio) <= timeActualFecha && timeActualFecha <= Fecha.getTimeStamp(fecha_termino) ){
                                
                                this.velocidadPromocion = rs.getString("megas_subir")!=null ? rs.getLong("megas_subir") : 0;
                                String hora_inicio = (rs.getString("hora_inicio") != null) ? rs.getString("hora_inicio") : "23:00";
                                String hora_fin = (rs.getString("hora_fin") != null) ? rs.getString("hora_fin") : "07:00";
                                this.noct_inst_prepago = (rs.getString("inst_prepago") != null) ? rs.getBoolean("inst_prepago") : false;
                                this.noct_inst_postpago = (rs.getString("inst_postpago") != null) ? rs.getBoolean("inst_postpago") : false;
                                this.noct_aplica_cambio_plan = (rs.getString("aplica_cambio_plan") != null) ? rs.getBoolean("aplica_cambio_plan") : false;
                                
                                this.horaInicioPromocion = new Date( Fecha.getTimeStamp(fecha_actual, hora_inicio) );
                                this.horaFinPromocion = new Date( Fecha.getTimeStamp(fecha_actual, hora_fin) );
                                
                            }
                            
                        }
                        
                        rs.close();
                    }
                }catch(Exception e){
                    System.out.println("Error obteniendo promociones Megas " + e.getMessage() );
                }
                
                
                try{
                    ResultSet rs = objDataBase.consulta("select PS.id_sucursal, PP.id_plan_servicio, inst_prepago, inst_postpago, aumento_megas \n" +
                        "FROM tbl_promocion as P inner join tbl_promocion_sucursal as PS on P.id_promocion = PS.id_promocion \n" +
                        "inner join tbl_promocion_plan as PP on P.id_promocion = PP.id_promocion \n" +
                        "where not cerrada and aumento_megas>0 and now()::date between fecha_inicio and fecha_termino \n" +
                        "order by PS.id_sucursal, PP.id_plan_servicio;");
                    if(objDataBase.getFilas(rs)>0){
                        this.matPromociones = Matriz.ResultSetAMatriz(rs);
                        rs.close();
                    }
                }catch(Exception e){
                    System.out.println("Error obteniendo promociones " + e.getMessage() );
                }
            
            



                

                
                
                
                if(permitirCortesCron.compareTo("true")==0) {

                    /*System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHoraExtendida()+ ": Inicio de eliminación de prefacturas de suspensiones del período actual");

                    // en el caso de haber hecho una suspension para el periodo
                    String periodo = Fecha.getFecha("ISO");
                    try{
                        ResultSet rs = objDataBase.consulta("select max(periodo) as periodo from tbl_prefactura");
                        if(rs.next()){
                            periodo = rs.getString("periodo")!=null ?  rs.getString("periodo") : periodo;
                            rs.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    int anio = Fecha.datePart("anio", periodo);
                    int mes = Fecha.datePart("mes", periodo);
                    String inicio = anio + "-" + mes + "-01";
                    String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
                    objDataBase.ejecutar("delete from tbl_prefactura where fecha_emision is null and periodo='"+inicio+"' and "
                            + "id_instalacion in (select id_instalacion from tbl_instalacion_suspension where eliminado=false and tipo='t' and fecha_inicio between '"+inicio+"' and '"+fin+"')" );

                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHoraExtendida() + ": Finalización de eliminación de prefacturas de suspensiones del período actual");
                    */





                    // generacion de prefacturas faltantes
                    long horaInicio = Fecha.getTimeStamp( Fecha.getFecha("ISO"), "17:30:00" );
                    if( (diaActual <= 10 && timeActual >= horaInicio) || diaActual >= 11 ){
                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de generación de prefacturas faltantes del período actual");
                        objDataBase.consulta("select proc_generarPrefacturasFaltantes();");
                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de generación de prefacturas faltantes del período actual");
                    }
                    









                    //  se factura hasta 15 min antes de las 19:00,  a esta hora empieza la emision de facturasde cash y anticipos
                    if(timeActual <= finEmisionFacturas){   
                        // emision de prefacturas con clientes que tienen anticipos
                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando emisión de facturas a clientes con anticipos");
                        FacturaVenta objFacturaVenta = new FacturaVenta( objDocumental, Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
                        try{
                            
        //                    ResultSet rsRubrosAdicionales = objDataBase.consulta("SELECT * FROM vta_prefactura_rubro WHERE id_instalacion in(select id_instalacion from vta_prefactura_todas as F where (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente) >= F.total and fecha_emision is null)");
        //                    String rubrosAdicionales[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales);


                            //  anticipos
                            ResultSet rsAnticipos = objDataBase.consulta("select id_cliente_anticipo, id_cliente, saldo, id_instalacion from tbl_cliente_anticipo where saldo > 0 and modo_bajada='i' order by id_cliente, saldo desc;");
//                            String matAnticipos[][] = Matriz.ResultSetAMatriz(rsAnticipos);
                            List matAnticipos = Matriz.ResultSetALista(rsAnticipos);

                            ResultSet rsConveniosTarjetas = objDataBase.consulta("select id_instalacion \n" +
                            "from vta_instalacion \n" +
                            "where num_cuenta<>'' and num_cuenta is not null and estado_servicio in ('a', 'c', 'r', 's') and fecha_instalacion is not null and ((forma_pago ='TAR' and set_convenio_tarjeta=true) or (forma_pago ='CTA')) \n" +
                            " and set_convenio_tarjeta=true \n"
                            + "union \n"
                            + "select F.id_instalacion from vta_prefactura_todas as F inner join tbl_instalacion as I  on F.id_instalacion=I.id_instalacion \n"
                            + "where num_cuenta<>'' and fecha_emision is null and num_cuenta is not null and I.estado_servicio in ('a', 'c', 'r', 's') and I.fecha_instalacion is not null and forma_pago in ('TAR', 'CTA')" +
                            "order by id_instalacion");
                            String matConveniosTarjetas[][] = Matriz.ResultSetAMatriz(rsConveniosTarjetas);


                            //  emisión con forma de pago anticipo
        //                    ResultSet rsFacturar = objDataBase.consulta("select *, total as total_comision from vta_prefactura_todas as F where (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente and F.id_instalacion=A.id_instalacion) >= F.total and fecha_emision is null order by periodo;");                   
                            ResultSet rsFacturar = objDataBase.consulta("with tmpPre as( \n" +
                                "select id_instalacion, min(periodo) as periodo \n" +
                                "from tbl_prefactura \n" +
                                "where fecha_emision is null \n" +
                                "group by id_instalacion "+ 
                                "), \n" +
                                "tmpAnt as( \n" +
                                "select id_instalacion, id_cliente, sum(saldo) as saldo \n" +
                                "from tbl_cliente_anticipo \n" +
                                "where saldo>1.7 and modo_bajada='i' \n" +
                                "group by id_instalacion, id_cliente \n" +
                                ") \n" +
                                "select *, total as total_comision from vta_prefactura_todas as F \n" +
                                "where (select saldo from tmpAnt as A where F.id_instalacion=A.id_instalacion and F.id_cliente=A.id_cliente) >= F.total \n" +
                                "and (id_instalacion, periodo) in(select id_instalacion, periodo from tmpPre) \n" +
                                "and fecha_emision is null order by periodo;");                   
                            objFacturaVenta.emitir(rsFacturar, matAnticipos, matConveniosTarjetas, 100);

                        }catch(Exception e){
                            String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage();
                            System.out.println(msg);
                            this.objCorreo.enviar("sistemas@saitel.ec", "Error emisión de facturas a clientes con anticipos" , msg, null);
                        }finally{
                            objFacturaVenta.cerrarConexiones();
                            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de emisión de facturas a clientes con anticipos");
                        }
                        
                    }







                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de cambios de razon social.");
                    objDataBase.consulta("select proc_cambio_cliente(null);");
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de cambios de razon social");






                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de estados de instalaciones.");
                    objDataBase.consulta("select proc_robot();");
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de estados de instalaciones");







                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de actualización de archivos de cortes en los servidores.");

//                    cortes normales, forma antigua, cada hora
                    if( modoSincronizacionMikrotiks.compareTo("scripts") == 0 ) {  
                        
                        try{
                            ResultSet rs = objDataBase.consulta("select valor from tbl_configuracion where parametro='bloqueo_servidores_ftp'");
                            if(rs.next()){
                                String bloqueo = rs.getString("valor")!=null ? rs.getString("valor") : "false";
                                if(bloqueo.toLowerCase().compareTo("false")==0) {
                                    objDataBase.ejecutar("update tbl_configuracion set valor='true' where parametro='bloqueo_servidores_ftp'");
//                                    break;



                                    ResultSet rsServidor = objDataBase.consulta("SELECT * from tbl_servidor_ftp where estado=true order by id_sucursal, id_servidor_ftp");


                                    while(rsServidor.next()){


                                        int id_servidor_ftp = rsServidor.getString("id_servidor_ftp")!=null ? rsServidor.getInt("id_servidor_ftp") : -1;
                                        String servidor = rsServidor.getString("servidor")!=null ? rsServidor.getString("servidor").replaceAll("[/]\\d*", "") : "";
                                        int puerto = rsServidor.getString("puerto")!=null ? rsServidor.getInt("puerto") : 26;
                                        String subredes = rsServidor.getString("subredes")!=null ? rsServidor.getString("subredes") : "";
                                        String usuario_ftp = rsServidor.getString("usuario")!=null ? rsServidor.getString("usuario") : "";
                                        String clave_ftp = rsServidor.getString("clave")!=null ? rsServidor.getString("clave") : "";
                                        String id_sucursal = rsServidor.getString("id_sucursal")!=null ? rsServidor.getString("id_sucursal") : "";
//                                        boolean use_api_mikrotik = rsServidor.getString("use_api_mikrotik")!=null ? rsServidor.getBoolean("use_api_mikrotik") : false;

//                                        if( use_api_mikrotik ){
//                                            ActualizaServidorApi hilo = new ActualizaServidorApi( id_servidor_ftp, servidor, puerto, usuario_ftp, clave_ftp, id_sucursal, subredes );
//                                            hilo.start();
//                                        } else {
                                            ActualizaServidorFtp hilo = new ActualizaServidorFtp( id_servidor_ftp, servidor, puerto, usuario_ftp, clave_ftp, id_sucursal, subredes );
                                            hilo.start();
//                                        }
                                        this.hilosEnEjecucion.add( id_servidor_ftp );

                                    }   //  while


                //                    int i=0;
                                    while ( !this.hilosEnEjecucion.isEmpty() ) {
                                        try{
                                            Thread.sleep(4000);
                                        }catch(InterruptedException e){
                                            e.printStackTrace();
                                        }
                //                        i++;
                                    }


                                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de actualización de archivos de cortes en los servidores.");



                                    


                //                    if(errConexion.compareTo("")!=0) {
                //                        objNotificacion.setMensaje( errConexion );
                //                        objNotificacion.notificar("ftp_conexion", null, null);
                //                    }
                //                    if(errColas.compareTo("")!=0 || errListas.compareTo("")!=0) {
                //                        objNotificacion.setMensaje( errColas + errListas );
                //                        objNotificacion.notificar("ftp_transferencia", null, null);
                //                    }


                                } else {   //  control de ejecucion de cortes
                                    String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Proceso de cortes en ejecución, parametro 'bloqueo_servidores_ftp' = true";
                                    this.objCorreo.enviar("sistemas@saitel.ec", "Proceso de cortes en ejecución" , msg, null);
                                    System.out.println(msg );
                                } 
                                rs.close();
                            } else {   //  control de ejecucion de cortes
                                String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error al leer parametro 'bloqueo_servidores_ftp' = true";
                                this.objCorreo.enviar("sistemas@saitel.ec", "Error al leer parametro 'bloqueo_servidores_ftp'" , msg, null);
                                System.out.println(msg );
                            }

                            Thread.sleep(1000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        } finally {
                            objDataBase.ejecutar("update tbl_configuracion set valor='false' where parametro='bloqueo_servidores_ftp'");
                        }
                    
  
                    } else if( modoSincronizacionMikrotiks.compareTo("apis") == 0 ) {  
                        
//                                hasta el 10 se actualizara cada hora, caso contrario se hara en linea por el cash  
                                if(diaActual < actualizarMikrotiksEnLinea) { 

                                        StringBuilder sql = new StringBuilder();
                                        sql.append("SELECT distinct I.id_sucursal, I.id_instalacion, razon_social || ' ' || id_instalacion as razon_social, ip::varchar, P.burst_limit, ");
                                        sql.append("	I.plan, P.max_limit, case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, estado_servicio, ");
                                        sql.append("	idMikrotikActivo, idMikrotikPlan, idMikrotikCola, ");
                                        sql.append("	(select servidor || ',' || usuario || ',' || clave || ',' || puerto as conexion from tbl_servidor_ftp as S where S.estado and S.id_sucursal=I.id_sucursal and position( regexp_replace(I.ip::varchar, '\\d*[/]\\d*', '') in subredes)>0 and id_servidor_ftp<>35 limit 1) ");
                                        sql.append("FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio ");
                                        sql.append("where I.id_instalacion in(select id_instalacion from tbl_prefactura where fecha_emision=now()::date and hora_emision >= (now() - '61 minutes'::interval )::time) and I.estado_servicio not in('t')");

                                        Instalacion objInstalacion = new Instalacion( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
                                        objInstalacion.actualizarEstados(sql);
                                        objInstalacion.cerrar();
                                }
                                
                    }

                    
            
                } else {   //  control de permitir cortes
                    String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Ejecucion de cortes desabilitado, parametro 'permitir_cortes_cron' = false";
                    this.objCorreo.enviar("sistemas@saitel.ec", "Ejecucion de cortes desabilitado" , msg, null);
                    System.out.println(msg );
                }
                
            }   //  control de fechas
            
            
            
            
            
            
            
            // RESET DE LISTAS Y COLAS DE INSTALACIONES EN MIKROTIKs
            //  hora actual   >=  00:00       &&         hora actual <= 00:59
            if( resincronizarMikrotiks && timeActual >= iniEjecucionMikrotik && timeActual <= finEjecucionMikrotik ){
                
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de RESET de los servidores mikrotik.");
                
                try{
                    ResultSet rs = objDataBase.consulta("select valor from tbl_configuracion where parametro='bloqueo_servidores_ftp'");
                    if(rs.next()){
                        String bloqueo = rs.getString("valor")!=null ? rs.getString("valor") : "false";
                        if(bloqueo.toLowerCase().compareTo("false")==0) {
                            objDataBase.ejecutar("update tbl_configuracion set valor='true' where parametro='bloqueo_servidores_ftp'");
//                                    break;



                            ResultSet rsServidor = objDataBase.consulta("SELECT * from tbl_servidor_ftp where estado=true and id_servidor_ftp <> 35 order by id_sucursal, id_servidor_ftp");
//                            ResultSet rsServidor = objDataBase.consulta("SELECT * from tbl_servidor_ftp where id_servidor_ftp = 35 order by id_sucursal, id_servidor_ftp");
                            

                            while(rsServidor.next()){


                                int id_servidor_ftp = rsServidor.getString("id_servidor_ftp")!=null ? rsServidor.getInt("id_servidor_ftp") : -1;
                                String servidor = rsServidor.getString("servidor")!=null ? rsServidor.getString("servidor").replaceAll("[/]\\d*", "") : "";
                                int puerto = rsServidor.getString("puerto")!=null ? rsServidor.getInt("puerto") : 26;
                                String subredes = rsServidor.getString("subredes")!=null ? rsServidor.getString("subredes") : "";
                                String usuario_ftp = rsServidor.getString("usuario")!=null ? rsServidor.getString("usuario") : "";
                                String clave_ftp = rsServidor.getString("clave")!=null ? rsServidor.getString("clave") : "";
                                String id_sucursal = rsServidor.getString("id_sucursal")!=null ? rsServidor.getString("id_sucursal") : "";
//                                        boolean use_api_mikrotik = rsServidor.getString("use_api_mikrotik")!=null ? rsServidor.getBoolean("use_api_mikrotik") : false;

                                ActualizaServidorApi hilo = new ActualizaServidorApi( id_servidor_ftp, servidor, puerto, usuario_ftp, clave_ftp, id_sucursal, subredes );
                                hilo.start();
                                   
                                this.hilosEnEjecucion.add( id_servidor_ftp );

                            }   //  while


        //                    int i=0;
                            while ( !this.hilosEnEjecucion.isEmpty() ) {
                                try{
                                    Thread.sleep(4000);
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }
        //                        i++;
                            }


                            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de RESET de los servidores mikrotik.");


                        } else {   //  control de ejecucion de cortes
                            String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Proceso de cortes en ejecución, parametro 'bloqueo_servidores_ftp' = true";
                            this.objCorreo.enviar("sistemas@saitel.ec", "Proceso de cortes en ejecución" , msg, null);
                            System.out.println(msg );
                        } 
                        rs.close();
                    } else {   //  control de ejecucion de cortes
                        String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error al leer parametro 'bloqueo_servidores_ftp' = true";
                        this.objCorreo.enviar("sistemas@saitel.ec", "Error al leer parametro 'bloqueo_servidores_ftp'" , msg, null);
                        System.out.println(msg );
                    }

                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    objDataBase.ejecutar("update tbl_configuracion set valor='false' where parametro='bloqueo_servidores_ftp'");
                    objDataBase.ejecutar("update tbl_configuracion set valor='apis' where parametro='modoSincronizacionMikrotiks'");
                    objDataBase.ejecutar("update tbl_configuracion set valor='false' where parametro='resincronizarMikrotiks'");
                }
                
            }   //  control de fechas 2
            
            
            
            
            
            try{
                DataBase objDataBaseEst = new DataBase( Parametro.getEstadisticaIp(), Parametro.getEstadisticaPuerto(), Parametro.getEstadisticaBaseDatos(), Parametro.getEstadisticaUsuario(), Parametro.getEstadisticaClave() );
                objDataBaseEst.consulta("select proc_robot();");
                objDataBaseEst.cerrar();
            }catch(Exception e){
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error en el proceso de copiar estados de instalaciones en estadistica: " + e.getMessage());
            }
            
        }catch(Exception e){
            String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error en el proceso general de script de cortes: " + e.getMessage();
            System.out.println(msg);
            this.objCorreo.enviar("sistemas@saitel.ec", "Error en el proceso general de script de cortes" , msg, null);
        }finally{
            objDocumental.cerrar();
            objDataBase.cerrar();
            this.objCorreo.cerrar();
            objNotificacion.cerrar();
        }
        
    }
    
    
    
    

    
    
    
    
    
    private class ActualizaServidorFtp extends Thread
    {
        private final int id_servidor_ftp;
        private final String servidor;
        private final int puerto;
        private final String usuario_ftp;
        private final String clave_ftp;
        private final String id_sucursal;
        private final String subredes;
        
        public ActualizaServidorFtp(int idServidorFtp, String svr, int p, String usuario, String clave, String idSucursal, String subRedes)
        {
            this.id_servidor_ftp = idServidorFtp;
            this.servidor = svr;
            this.puerto = p;
            this.usuario_ftp = usuario;
            this.clave_ftp = clave;
            this.id_sucursal = idSucursal;
            this.subredes = subRedes;
        }
        
        public void run()
        {
            StringBuilder colas = new StringBuilder();
            StringBuilder listas = new StringBuilder();
                        
            String vecSubredes[] = subredes.split(",");
            String cliente = "";
            String ip = "";
            String burst_limit = "";
            String max_limit = "";
            String prioridad = "";
            String plan = "";
            long ancho_resi = 0;
            long ancho_small = 0;
            long ancho_noct = 0;
            long ancho_corp = 0;
            colas.append("/queue simple\n");
            colas.append("remove [find]\n");
//                    colas.append("remove [find list=\"activos\"]\n");
//                    colas.append("remove [find list=\"residenciales\"]\n");
//                    colas.append("remove [find list=\"small\"]\n");
//                    colas.append("remove [find list=\"corporativos\"]\n");
//                    colas.append("remove [find list=\"nocturnos\"]\n");

            listas.append("/ip firewall address-list\n");
            //listas.append("remove [find]\n");
            listas.append("remove [find list=\"activos\"]\n");
            listas.append("remove [find list=\"residenciales\"]\n");
            listas.append("remove [find list=\"small\"]\n");
            listas.append("remove [find list=\"corporativos\"]\n");
            listas.append("remove [find list=\"nocturnos\"]\n");

            DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
            
            try{
                for(int i=0; i<vecSubredes.length; i++){
                    String res[][] = this.getColasServidor(objDataBase, vecSubredes[i], id_sucursal);
                    for(int j=0; j<res.length; j++){
                        if(res[j][2]!=null && res[j][5]!=null){
                            float ax_burst_limit = Float.parseFloat(res[j][2]);
                            plan = res[j][5];
                            if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                                ancho_resi +=  ax_burst_limit;
                            }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                                    ancho_small += ax_burst_limit;
                            }else if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                                    ancho_corp += ax_burst_limit;
                            }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                                    ancho_noct += ax_burst_limit;
                            }
                        }
                    }

                    ancho_resi = ancho_resi/1024/20;
                    ancho_small = ancho_small/1024/4;
                    ancho_corp = ancho_corp/1024/2;
                    ancho_noct = ancho_noct/1024/20;

//                            colas += "add max-limit="+ancho_resi+"M/"+ancho_resi+"M name=\"Residencial Total\" \\\n";
//                            colas += "priority=2/2 total-priority=2\n";
//                            colas += "add max-limit="+ancho_small+"M/"+ancho_small+"M name=\"Small Total\" \\\n";
//                            colas += "priority=2/2 total-priority=2\n";
//                            colas += "add max-limit="+ancho_noct+"M/"+ancho_noct+"M name=\"Nocturno Total\" \\\n";
//                            colas += "priority=2/2 total-priority=2\n";
//                            colas += "add max-limit="+ancho_corp+"M/"+ancho_corp+"M name=\"Corporativo Total\" \\\n";
//                            colas += "priority=2/2 total-priority=2\n";

                    for(int j=0; j<res.length; j++){
                        if(res[j][0]!=null && res[j][1]!=null && res[j][2]!=null 
                                && res[j][3]!=null && res[j][4]!=null && res[j][5]!=null){
                            cliente = res[j][0];
                            ip = res[j][1];
                            burst_limit = res[j][2];
                            max_limit = res[j][3];
                            prioridad = res[j][4];
                            plan = res[j][5];

                            listas.append("add address="+ip+" comment=\""+cliente+"\" \\\nlist=activos\n");
                            if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                                colas.append("add max-limit="+max_limit+"k/"+max_limit+"k name=\""+cliente+"\" \\\n");
                                colas.append("priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad+"\n");
                                listas.append("add address="+ip+" comment=\""+cliente+"\" \\\nlist=corporativos\n");
                            }else if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                                colas.append("add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" \\\n");
                                colas.append("priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad+"\n");
                                listas.append("add address="+ip+" comment=\""+cliente+"\" \\\nlist=residenciales\n");
                            }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                                colas.append("add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" \\\n");
                                colas.append("priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad+"\n");
                                listas.append("add address="+ip+" comment=\""+cliente+"\" \\\nlist=small\n");
                            }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                                colas.append("add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" \\\n");
                                colas.append("priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad+"\n");
                                listas.append("add address="+ip+" comment=\""+cliente+"\" \\\nlist=nocturnos\n");
                            }
                        }
                    }

                }    

            }catch(Exception e){
//                msg += Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error en la generación de scripts: " + e.getMessage();
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error en la generación de scripts: " + e.getMessage());
                objCorreo.enviar("jefetecnicoibarra@saitel.ec", "Error en la generación de scripts", e.getMessage(), null);
            }
            
            colas.append("# END");
            listas.append("# END");

            //  guardar datos en un archivo y enviar al servidor FTP
            String archivoColas = tmp + servidor + "_colas_" + Fecha.getFecha("ISO") + "_" + Fecha.getHora().replace(":", "") + ".txt";
            String archivoListas = tmp + servidor + "_listas_" + Fecha.getFecha("ISO") + "_" + Fecha.getHora().replace(":", "") + ".txt";
            if(this.grabarArchivo(archivoColas, colas) && this.grabarArchivo(archivoListas, listas) ){
                
                File fileColas = new File(archivoColas);
                long sizeColas = fileColas.length();
                StringBuilder tamColas = new StringBuilder();
                tamColas.append(sizeColas);
                File fileListas = new File(archivoListas);
                long sizeListas = fileListas.length();
                StringBuilder tamListas = new StringBuilder();
                tamListas.append(sizeListas);
                String archivoColasVerificar = tmp + servidor + "_colas_" + sizeColas;
                String archivoListasVerificar = tmp + servidor + "_listas_" + sizeListas;
                
                
                if( this.grabarArchivo(archivoColasVerificar, tamColas ) && this.grabarArchivo(archivoListasVerificar, tamListas) ){
                
                
                    objDataBase.ejecutar("UPDATE tbl_servidor_ftp SET megas_corporativo="+ancho_corp+", megas_small="+ancho_small+
                        ", megas_residencial="+ancho_resi+", megas_nocturno="+ancho_noct+" where id_servidor_ftp="+id_servidor_ftp);

                    Ftp ftp = new Ftp();
                    if(ftp.conectar(servidor, puerto, usuario_ftp, clave_ftp)){
                        String err_colas = "";
                        String err_listas = "";
                        if(!ftp.subirArchivo(archivoColas, "/colas.txt")){
                            err_colas = ftp.getError();
                        }
                        if(!ftp.subirArchivo(archivoListas, "/listas.txt")){
                            err_listas = ftp.getError();
                        }
                        ftp.subirArchivo(archivoColasVerificar, "/colas_" + tamColas);
                        ftp.subirArchivo(archivoListasVerificar, "/listas_" + tamListas);
                        
                        if(err_colas.compareTo("")!=0 || err_listas.compareTo("")!=0){
                            System.out.println( Fecha.getFecha("SQL") + " " + Fecha.getHora() + err_colas + ". " + err_listas );
                            objCorreo.enviar("jefetecnicoibarra@saitel.ec", "Error en archivos de listas o colas", err_colas + ". " + err_listas, null);
                            //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "gerencia@saitel.ec", "sistemas@saitel.ec", "", "ERROR EN LA TRANSFERENCIA DE ARCHIVOS FTP", new StringBuilder(msg), true);
//                        }else{
//                            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Actualización de archivos de cortes en el servidor " + servidor);
                        }
                    }else{
                        System.out.println( Fecha.getFecha("SQL") + " " + Fecha.getHora() + ftp.getError() );
                        objCorreo.enviar("jefetecnicoibarra@saitel.ec", "Error FTP", ftp.getError(), null);
                        //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "gerencia@saitel.ec", "sistemas@saitel.ec", "", "INCONVENIENTES EN CONEXION FTP", new StringBuilder(msg), true);
                    }
                    ftp.desconectar();
                }
            }
            
            objDataBase.cerrar();
            
            int i=0;
            Iterator it = hilosEnEjecucion.iterator();
            while (it.hasNext()) {
                int id = (int)it.next();
                if( id == id_servidor_ftp){
                    hilosEnEjecucion.remove(i);
                    break;
                }
                i++;
            }
                    
//            colas.delete(0, colas.length());  // limpio la variable de datos
//            listas.delete(0, listas.length());  // limpio la variable de datos
        } 
        
        public String [][] getColasServidor(DataBase objDataBase, String subred, String id_sucursal)
        {
            String ip = "127.0.0.1";
            String mascara = "24";
            if(subred.contains("/") && subred.length()>0){
                String vecRed[] = subred.split("/");
                ip = vecRed[0].trim();
                mascara = vecRed[1].trim();
            }else if(!subred.contains("/") && subred.length()>0){
                        ip = subred.trim();
                  }

            String octetos[] = ip.replace(".", ";").split(";");
            String ipRed = "";
            for(int i=0; i<octetos.length-1; i++){
                ipRed += octetos[i] + ".";
            }
            if(ipRed.compareTo("")==0){
                ipRed = "127.0.0.";
            }
            try{
                String where = id_sucursal.compareTo("-0")==0 || id_sucursal.compareTo("0")==0 ? "" : " and I.id_sucursal="+id_sucursal;
                ResultSet rs = objDataBase.consulta("SELECT distinct razon_social || ' ' || id_instalacion as razon_social, ip::varchar, P.burst_limit, "
                    + "P.max_limit, case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, I.plan "
                    + "FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio "
                    + "where estado_servicio in ('p', 'a') and ip::varchar like '"+ipRed+"%' " + where 
                    + " order by ip;");

                int rango_ips[] = this.getNumPcs(Integer.parseInt(octetos[3]), Integer.parseInt(mascara));

                int filas = objDataBase.getFilas(rs);
                String mat[][] = new String[filas][6];
                int i=0;
                while(rs.next()){
                    String ip_cliente_masc = rs.getString("ip")!=null ? rs.getString("ip") : "127.0.0.1/32";
                    String vec_ip_cliente[] = ip_cliente_masc.split("/");
                    String ip_cliente = vec_ip_cliente[0];

                    String octetos_ip_cli[] = ip_cliente.replace(".", ";").split(";");
                    int octeto_4 = Integer.parseInt(octetos_ip_cli[3]);
                    if(octeto_4 >= rango_ips[0] && octeto_4 <= rango_ips[1]){
                        mat[i][0] = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                        mat[i][1] = ip_cliente + "/" + vec_ip_cliente[1];
                        mat[i][2] = rs.getString("burst_limit")!=null ? rs.getString("burst_limit") : "0";
                        mat[i][3] = rs.getString("max_limit")!=null ? rs.getString("max_limit") : "0";
                        mat[i][4] = rs.getString("prioridad")!=null ? rs.getString("prioridad") : "3";
                        mat[i][5] = rs.getString("plan")!=null ? rs.getString("plan") : "";
                        i++;
                    }
                }
                rs.close();
                return mat;
            }catch(Exception e){
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + "Error en la generación de las colas: " + e.getMessage());
                objCorreo.enviar("jefetecnicoibarra@saitel.ec", "Error en la generación de las colas", e.getMessage(), null);
            }
            return null;
        }
        
        public int[] getNumPcs(int octeto_red, int mascara)
        {
            int ips[] = new int[]{0, 255};
            switch(mascara){
                case 25: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 127;
                break;
                case 26: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 63;
                break;
                case 27: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 31;
                break;
                case 28: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 15;
                break;
                case 29: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 7;
                break;
                case 30: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 3;
                break;
                case 31: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 1;
                break;
            }
            return ips;
        }
        
        public boolean grabarArchivo(String archivo, StringBuilder datos)
        {
            FileWriter fichero = null;
            PrintWriter pw = null;
            try{
                fichero = new FileWriter(archivo);
                pw = new PrintWriter(fichero);
                pw.print(datos);
                pw.close();
            }catch (Exception e) {
                String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + ". Error al tratar de escribir en el archivo " + archivo + ". " + e.getMessage();
                System.out.print(msg);
                //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "gerencia@saitel.ec", "sistemas@saitel.ec", "", "INCONVENIENTES EN GUARDADO DE ARCHIVOS DE CORTES", new StringBuilder(msg), true);
                return false;
            }finally {
                try {
                    if(null != fichero){
                        fichero.close();
                    }
                }catch(Exception e2) {
                    System.out.print(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error al tratar de cerrar el archivo " + archivo + ". " + e2.getMessage());
                }
            }
            return true;
        }
        
    }
    
    
    
    
    
    
   
    
    private class ActualizaServidorApi extends Thread
    {
        private final int id_servidor_ftp;
        private final String servidor;
//        private final int puerto;
        private final String usuario_ftp;
        private final String clave_ftp;
        private final String id_sucursal;
        private final String subredes;
        
        public ActualizaServidorApi(int idServidorFtp, String svr, int p, String usuario, String clave, String idSucursal, String subRedes)
        {
            this.id_servidor_ftp = idServidorFtp;
            this.servidor = svr;
//            this.puerto = p;
            this.usuario_ftp = usuario;
            this.clave_ftp = clave;
            this.id_sucursal = idSucursal;
            this.subredes = subRedes;
        }
        
        public void run()
        {
                        
            String vecSubredes[] = subredes.split(",");
            String cliente = "";
            String ip = "";
            String burst_limit = "";
            String max_limit = "";
            String prioridad = "";
            String plan = "";
            long ancho_resi = 0;
            long ancho_small = 0;
            long ancho_noct = 0;
            long ancho_corp = 0;

            DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
            Mikrotik mikrotik = new Mikrotik( servidor, usuario_ftp, clave_ftp );
            
            try{
                
                mikrotik.limpiar( "/ip/firewall/address-list/" );
                mikrotik.limpiar( "/queue/simple/" );
                
                for(int i=0; i<vecSubredes.length; i++){
                    String res[][] = this.getColasServidor(objDataBase, vecSubredes[i], id_sucursal);
                    for(int j=0; j<res.length; j++){
                        if(res[j][2]!=null && res[j][5]!=null){
                            float ax_burst_limit = Float.parseFloat(res[j][2]);
                            plan = res[j][5];
                            if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                                ancho_resi +=  ax_burst_limit;
                            }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                                    ancho_small += ax_burst_limit;
                            }else if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                                    ancho_corp += ax_burst_limit;
                            }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                                    ancho_noct += ax_burst_limit;
                            }
                        }
                    }

                    ancho_resi = ancho_resi/1024/20;
                    ancho_small = ancho_small/1024/4;
                    ancho_corp = ancho_corp/1024/2;
                    ancho_noct = ancho_noct/1024/20;

                    for(int j=0; j<res.length; j++){
                        if(res[j][0]!=null && res[j][1]!=null && res[j][2]!=null 
                                && res[j][3]!=null && res[j][4]!=null && res[j][5]!=null) {
                            cliente = res[j][0];
                            ip = res[j][1];
                            burst_limit = res[j][2];
                            max_limit = res[j][3];
                            prioridad = res[j][4];
                            plan = res[j][5];
                            String idInstalacion = res[j][6];
                            String estadoServicio = res[j][7];

                            String idCola = "";
                            String idPlan = "";
                            String idActivo = mikrotik.add("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=" + mikrotik.getEstadoServicio(estadoServicio) );
                            if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                                idPlan = mikrotik.add("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=corporativos");
                                idCola = mikrotik.add("/queue/simple/add max-limit="+max_limit+"k/"+max_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                            }else if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                                idPlan = mikrotik.add("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=residenciales");
                                idCola = mikrotik.add("/queue/simple/add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                            }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                                idPlan = mikrotik.add("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=small\n");
                                idCola = mikrotik.add("/queue/simple/add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                            }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                                idPlan = mikrotik.add("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=nocturnos\n");
                                idCola = mikrotik.add("/queue/simple/add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                            }
                            objDataBase.ejecutar("update tbl_instalacion set idMikrotikActivo='"+idActivo+"', idMikrotikPlan='"+idPlan+"', idMikrotikCola='"+idCola+"' where id_instalacion="+idInstalacion);

                        }
                    }

                }    

            }catch(Exception e){
//                msg += Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error en la generación de scripts: " + e.getMessage();
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error en la sincronizacion del servidor mikrotik: " + e.getMessage());
                objCorreo.enviar("jefetecnicoibarra@saitel.ec", "Error en la sincronizacion del servidor mikrotik", e.getMessage(), null);
            }

            
            
            mikrotik.cerrar();
            objDataBase.cerrar();
            
            int i=0;
            Iterator it = hilosEnEjecucion.iterator();
            while (it.hasNext()) {
                int id = (int)it.next();
                if( id == id_servidor_ftp){
                    hilosEnEjecucion.remove(i);
                    break;
                }
                i++;
            }
                    
        } 
        
        public String [][] getColasServidor(DataBase objDataBase, String subred, String id_sucursal)
        {
            String ip = "127.0.0.1";
            String mascara = "24";
            if(subred.contains("/") && subred.length()>0){
                String vecRed[] = subred.split("/");
                ip = vecRed[0].trim();
                mascara = vecRed[1].trim();
            }else if(!subred.contains("/") && subred.length()>0){
                        ip = subred.trim();
                  }

            String octetos[] = ip.replace(".", ";").split(";");
            String ipRed = "";
            for(int i=0; i<octetos.length-1; i++){
                ipRed += octetos[i] + ".";
            }
            if(ipRed.compareTo("")==0){
                ipRed = "127.0.0.";
            }
            try{
                String where = id_sucursal.compareTo("-0")==0 || id_sucursal.compareTo("0")==0 ? "" : " and I.id_sucursal="+id_sucursal;
                ResultSet rs = objDataBase.consulta("SELECT distinct razon_social || ' ' || id_instalacion as razon_social, ip::varchar, P.burst_limit, "
                    + "P.max_limit, case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, I.plan, I.id_instalacion, estado_servicio "
                    + "FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio "
                    + "where estado_servicio not in ('t', '1') and ip::varchar like '"+ipRed+"%' " + where + " order by ip;");

                int rango_ips[] = this.getNumPcs(Integer.parseInt(octetos[3]), Integer.parseInt(mascara));

                int filas = objDataBase.getFilas(rs);
                String mat[][] = new String[filas][8];
                int i=0;
                while(rs.next()){
                    String ip_cliente_masc = rs.getString("ip")!=null ? rs.getString("ip") : "127.0.0.1/32";
                    String vec_ip_cliente[] = ip_cliente_masc.split("/");
                    String ip_cliente = vec_ip_cliente[0];

                    String octetos_ip_cli[] = ip_cliente.replace(".", ";").split(";");
                    int octeto_4 = Integer.parseInt(octetos_ip_cli[3]);
                    if(octeto_4 >= rango_ips[0] && octeto_4 <= rango_ips[1]){
                        mat[i][0] = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                        mat[i][1] = ip_cliente + "/" + vec_ip_cliente[1];
                        mat[i][2] = rs.getString("burst_limit")!=null ? rs.getString("burst_limit") : "0";
                        mat[i][3] = rs.getString("max_limit")!=null ? rs.getString("max_limit") : "0";
                        mat[i][4] = rs.getString("prioridad")!=null ? rs.getString("prioridad") : "3";
                        mat[i][5] = rs.getString("plan")!=null ? rs.getString("plan") : "";
                        mat[i][6] = rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "-1";
                        mat[i][7] = rs.getString("estado_servicio")!=null ? rs.getString("estado_servicio") : "";
                        i++;
                    }
                }
                rs.close();
                return mat;
            }catch(Exception e){
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + "Error en obtencion de los datos de la instalacion: " + e.getMessage());
                objCorreo.enviar("jefetecnicoibarra@saitel.ec", "Error en obtencion de los datos de la instalacion", e.getMessage(), null);
            }
            return null;
        }
        
        public int[] getNumPcs(int octeto_red, int mascara)
        {
            int ips[] = new int[]{0, 255};
            switch(mascara){
                case 25: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 127;
                break;
                case 26: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 63;
                break;
                case 27: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 31;
                break;
                case 28: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 15;
                break;
                case 29: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 7;
                break;
                case 30: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 3;
                break;
                case 31: 
                    ips[0] = octeto_red;
                    ips[1] = octeto_red + 1;
                break;
            }
            return ips;
        }
        
        public boolean grabarArchivo(String archivo, StringBuilder datos)
        {
            FileWriter fichero = null;
            PrintWriter pw = null;
            try{
                fichero = new FileWriter(archivo);
                pw = new PrintWriter(fichero);
                pw.print(datos);
                pw.close();
            }catch (Exception e) {
                String msg = Fecha.getFecha("SQL") + " " + Fecha.getHora() + ". Error al tratar de escribir en el archivo " + archivo + ". " + e.getMessage();
                System.out.print(msg);
                //Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), "gerencia@saitel.ec", "sistemas@saitel.ec", "", "INCONVENIENTES EN GUARDADO DE ARCHIVOS DE CORTES", new StringBuilder(msg), true);
                return false;
            }finally {
                try {
                    if(null != fichero){
                        fichero.close();
                    }
                }catch(Exception e2) {
                    System.out.print(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error al tratar de cerrar el archivo " + archivo + ". " + e2.getMessage());
                }
            }
            return true;
        }
        
    }
    
    
    
    
    
    
    
    //    public void ejecutar()
//    {
//        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
//        Notificacion objNotificacion = new Notificacion( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
//        
//        try{
//            
//            // emision de prefacturas con clientes que tienen anticipos
//                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando emisión de facturas a clientes con anticipos");
//                    try{
//                        
//                        String doc_ip = Parametro.getDocumentalIp();      //  127.0.0.1     pruebas = 192.168.217.16
//                        int doc_puerto = Parametro.getDocumentalPuerto();
//                        String doc_db = Parametro.getDocumentalBaseDatos();
//                        String doc_usuario = Parametro.getDocumentalUsuario();
//                        String doc_clave = Parametro.getDocumentalClave();
//                        DataBase objDocumental = new DataBase( doc_ip, doc_puerto, doc_db, doc_usuario, doc_clave );
//                
//                        FacturaVenta objFacturaVenta = new FacturaVenta( objDocumental, Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
//
//    //                    ResultSet rsRubrosAdicionales = objDataBase.consulta("SELECT * FROM vta_prefactura_rubro WHERE id_instalacion in(select id_instalacion from vta_prefactura_todas as F where (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente) >= F.total and fecha_emision is null)");
//    //                    String rubrosAdicionales[][] = Matriz.ResultSetAMatriz(rsRubrosAdicionales);
//
//
//                        //  anticipos
//                        ResultSet rsAnticipos = objDataBase.consulta("select id_cliente_anticipo, id_cliente, saldo, id_instalacion from tbl_cliente_anticipo where saldo > 0 and modo_bajada='i' order by id_cliente, saldo desc;");
////                        String matAnticipos[][] = Matriz.ResultSetAMatriz(rsAnticipos);
//                        List matAnticipos = Matriz.ResultSetALista(rsAnticipos);
//                        
//
//                        ResultSet rsConveniosTarjetas = objDataBase.consulta("select id_instalacion \n" +
//                        "from vta_instalacion \n" +
//                        "where num_cuenta<>'' and num_cuenta is not null and estado_servicio in ('a', 'c', 'r', 's') and fecha_instalacion is not null and ((forma_pago ='TAR' and set_convenio_tarjeta=true) or (forma_pago ='CTA')) \n" +
//                        " and set_convenio_tarjeta=true \n"
//                        + "union \n"
//                        + "select F.id_instalacion from vta_prefactura_todas as F inner join tbl_instalacion as I  on F.id_instalacion=I.id_instalacion \n"
//                        + "where num_cuenta<>'' and fecha_emision is null and num_cuenta is not null and I.estado_servicio in ('a', 'c', 'r', 's') and I.fecha_instalacion is not null and forma_pago in ('TAR', 'CTA')" +
//                        "order by id_instalacion");
//                        String matConveniosTarjetas[][] = Matriz.ResultSetAMatriz(rsConveniosTarjetas);
//
//
//                        //  emisión con forma de pago anticipo
//    //                    ResultSet rsFacturar = objDataBase.consulta("select *, total as total_comision from vta_prefactura_todas as F where (select sum(A.saldo) from tbl_cliente_anticipo as A where F.id_cliente=A.id_cliente and F.id_instalacion=A.id_instalacion) >= F.total and fecha_emision is null order by periodo;");                   
//                        ResultSet rsFacturar = objDataBase.consulta("with tmpPre as( \n" +
//                            "select id_instalacion, min(periodo) as periodo \n" +
//                            "from tbl_prefactura \n" +
//                            "where fecha_emision is null \n" +
//                            "group by id_instalacion "+ 
//                            "), \n" +
//                            "tmpAnt as( \n" +
//                            "select id_instalacion, id_cliente, sum(saldo) as saldo \n" +
//                            "from tbl_cliente_anticipo \n" +
//                            "where saldo>1.7 and modo_bajada='i' \n" +
//                            "group by id_instalacion, id_cliente \n" +
//                            ") \n" +
//                            "select *, total as total_comision from vta_prefactura_todas as F \n" +
//                            "where (select saldo from tmpAnt as A where F.id_instalacion=A.id_instalacion and F.id_cliente=A.id_cliente) >= F.total \n" +
//                            "and (id_instalacion, periodo) in(select id_instalacion, periodo from tmpPre) \n" +
//                            "and fecha_emision is null order by periodo;");                   
//                        objFacturaVenta.emitir(rsFacturar, matAnticipos, matConveniosTarjetas, 100);
//
//                        objFacturaVenta.cerrarConexiones();
//                    }catch(Exception e){
//                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
//                    }finally{
//                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de emisión de facturas a clientes con anticipos");
//                    }
//            
//        }catch(Exception e){
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + " Error en el proceso general de script de cortes: " + e.getMessage());
//        }finally{
//            objDataBase.ejecutar("update tbl_configuracion set valor='false' where parametro='bloqueo_servidores_ftp'");
//            objDataBase.cerrar();
//            objNotificacion.cerrar();
//        }
//        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": finalizando emisión de facturas a clientes con anticipos");
//        
//    }
    
}