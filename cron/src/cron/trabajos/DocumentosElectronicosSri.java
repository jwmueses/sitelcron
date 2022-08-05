/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;


import ec.gob.sri.wsc.EnvioComprobantesWS;
import ec.gob.sri.wsc.AutorizacionComprobantesWS;
import ec.gob.sri.wsc.DirectorioConfiguracion;
import java.io.File;
import java.io.RandomAccessFile;
import java.sql.ResultSet;

/**
 *
 * @author jorge
 */
public class DocumentosElectronicosSri{
    
    private String mails[][] = null;
    
    
    public void execute() {
        
        String doc_ip = Parametro.getDocumentalIp();      //  127.0.0.1     pruebas = 192.168.217.16
        int doc_puerto = Parametro.getDocumentalPuerto();
        String doc_db = Parametro.getDocumentalBaseDatos();
        String doc_usuario = Parametro.getDocumentalUsuario();
        String doc_clave = Parametro.getDocumentalClave();
        DataBase objDocumental = new DataBase( doc_ip, doc_puerto, doc_db, doc_usuario, doc_clave );
        
        Archivo objDataBase = new Archivo( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        String rutaArchivoFirmado = DirectorioConfiguracion.getRutaArchivoFirmado();
        
        
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de generación de facturas XML firmadas. " + rutaArchivoFirmado );
        
        
        
        FacturaVenta dbFacturaVenta = new FacturaVenta(objDocumental, Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave());
        dbFacturaVenta.procesarXmlSriTodos();
        dbFacturaVenta.cerrarConexiones();
            
        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Fin de generación de facturas XML firmadas. " + rutaArchivoFirmado );
        
        
        
        
        
        
        try{


            
////////////////    ENVIO DE DOCUMENTOS     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
            int dia = Fecha.getDia();
            if( dia > 1 && dia < 27){    

                //  Envio de facturas
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de envio de facturas al SRI. " + rutaArchivoFirmado );
                String pkFacturasRecibidos = "";
                try{
                    ResultSet rs = objDataBase.consulta("select id_factura_venta, serie_factura || '-' || num_factura as numero, clave_acceso from tbl_factura_venta where estado_documento='f' and fecha_emision >='2018-08-30'");
                    while(rs.next()){
                        try{
                            String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                            String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                            String id_factura_venta = rs.getString("id_factura_venta")!=null ? rs.getString("id_factura_venta") : "";

                            ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                            File ArchivoXML = new File(rutaArchivoFirmado + File.separatorChar + clave_acceso + ".xml");

                            respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, clave_acceso, Parametro.getServicioWebEnvio());
                            String estado = respuestaRecepcion.getEstado();
                            if(estado != null){
                                if(estado.equals("RECIBIDA")){
    //                                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + clave_acceso);
                                    pkFacturasRecibidos += id_factura_venta + ",";
                                }else {
                                    String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                                    if (estado.equals("DEVUELTA")) {
                                        objDataBase.ejecutar("update tbl_factura_venta set estado_documento='n', mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_factura_venta="+id_factura_venta);
                                    }else{
                                        objDataBase.ejecutar("update tbl_factura_venta set mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_factura_venta="+id_factura_venta);
                                    }
                                }
                            }else{
                                objDataBase.ejecutar("update tbl_factura_venta set mensaje=' Error en documento No. " + numero + ". " + EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion)+
                                    "' where id_factura_venta="+id_factura_venta);
                            }
                        }catch(Exception e){
                            System.out.println("Error en envio: " + e.getMessage());
                        }
                    }

                    rs.close();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                if(pkFacturasRecibidos.compareTo("")!=0){
                    pkFacturasRecibidos = pkFacturasRecibidos.substring( 0, pkFacturasRecibidos.length()-1 );
                    objDataBase.ejecutar("update tbl_factura_venta set estado_documento='r' where id_factura_venta in ("+pkFacturasRecibidos+")");
                }

                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizacion de envio de facturas al SRI");








                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de envio de retenciones al SRI");
                //  Envio de retenciones
                String pkRetencionesRecibidos = "";
                try{
                    ResultSet rs = objDataBase.consulta("select id_retencion_compra, ret_num_serie || '-' || ret_num_retencion as numero, clave_acceso from tbl_retencion_compra where estado_documento='f' and anulado=false");
                    while(rs.next()){
                        try{
                            String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                            String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                            String id_retencion_compra = rs.getString("id_retencion_compra")!=null ? rs.getString("id_retencion_compra") : "";

                            ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                            File ArchivoXML = new File(rutaArchivoFirmado + File.separatorChar + clave_acceso + ".xml");

                            respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, clave_acceso, Parametro.getServicioWebEnvio());
                            String estado = respuestaRecepcion.getEstado();
                            if(estado != null){
                                if(estado.equals("RECIBIDA")){
    //                                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + clave_acceso);
                                    pkRetencionesRecibidos += id_retencion_compra + ",";
                                }else {
                                    String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                                    if (estado.equals("DEVUELTA")) {
                                        objDataBase.ejecutar("update tbl_retencion_compra set estado_documento='n', mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_retencion_compra="+id_retencion_compra);
                                    }else{
                                        objDataBase.ejecutar("update tbl_retencion_compra set mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_retencion_compra="+id_retencion_compra);
                                    }
                                }
                            }else{
                                objDataBase.ejecutar("update tbl_retencion_compra set mensaje=' Error en documento No. " + numero + ". " + EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion)+
                                    "' where id_retencion_compra="+id_retencion_compra);
                            }
                        }catch(Exception e){
                            System.out.println("Error en envio: " + e.getMessage());
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                if(pkRetencionesRecibidos.compareTo("")!=0){
                    pkRetencionesRecibidos = pkRetencionesRecibidos.substring( 0, pkRetencionesRecibidos.length()-1 );
                    objDataBase.ejecutar("update tbl_retencion_compra set estado_documento='r' where id_retencion_compra in("+pkRetencionesRecibidos+")");
                }

                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de envio de retenciones al SRI");







                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de envio de notas de crédito al SRI");
                //  Envio de notas de credito
                String pkNotasCreditoRecibidos = "";
                try{
                    ResultSet rs = objDataBase.consulta("select id_nota_credito_venta, serie_nota || '-' || num_nota as numero, clave_acceso from tbl_nota_credito_venta where estado_documento='f' and anulado=false");
                    while(rs.next()){
                        try{
                            String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                            String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                            String id_nota_credito_venta = rs.getString("id_nota_credito_venta")!=null ? rs.getString("id_nota_credito_venta") : "";

                            ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                            File ArchivoXML = new File(rutaArchivoFirmado + File.separatorChar + clave_acceso + ".xml");

                            respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, clave_acceso, Parametro.getServicioWebEnvio());
                            String estado = respuestaRecepcion.getEstado();
                            if(estado != null){
                                if(estado.equals("RECIBIDA")){
    //                                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + clave_acceso);
                                    pkNotasCreditoRecibidos += id_nota_credito_venta + ",";
                                }else {
                                    String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                                    if (estado.equals("DEVUELTA")) {
                                        objDataBase.ejecutar("update tbl_nota_credito_venta set estado_documento='n', mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_nota_credito_venta="+id_nota_credito_venta);
                                    }else{
                                        objDataBase.ejecutar("update tbl_nota_credito_venta set mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_nota_credito_venta="+id_nota_credito_venta);
                                    }
                                }
                            }else{
                                objDataBase.ejecutar("update tbl_nota_credito_venta set mensaje=' Error en documento No. " + numero + ". " + EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion)+
                                    "' where id_nota_credito_venta="+id_nota_credito_venta);
                            }
                        }catch(Exception e){
                            System.out.println("Error en envio: " + e.getMessage());
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                if(pkNotasCreditoRecibidos.compareTo("")!=0){
                    pkNotasCreditoRecibidos = pkNotasCreditoRecibidos.substring( 0, pkNotasCreditoRecibidos.length()-1 );
                    objDataBase.ejecutar("update tbl_nota_credito_venta set estado_documento='r' where id_nota_credito_venta in("+pkNotasCreditoRecibidos+")");
                }

                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de envio de notas de crédito al SRI");












                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de envio de liquidaciones de compras y servicios al SRI");
                //  Envio de notas de credito
                String pkLiquidaciones = "";
                try{
                    ResultSet rs = objDataBase.consulta("select id_liquidacion_compra, serie_liquidacion || '-' || num_liquidacion as numero, clave_acceso from tbl_liquidacion_compra where estado_documento='f' and anulado=false");
                    while(rs.next()){
                        try{
                            String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                            String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                            String id_liquidacion_compra = rs.getString("id_liquidacion_compra")!=null ? rs.getString("id_liquidacion_compra") : "";

                            ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                            File ArchivoXML = new File(rutaArchivoFirmado + File.separatorChar + clave_acceso + ".xml");

                            respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, clave_acceso, Parametro.getServicioWebEnvio());
                            String estado = respuestaRecepcion.getEstado();
                            if(estado != null){
                                if(estado.equals("RECIBIDA")){
    //                                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + clave_acceso);
                                    pkLiquidaciones += id_liquidacion_compra + ",";
                                }else {
                                    String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                                    if (estado.equals("DEVUELTA")) {
                                        objDataBase.ejecutar("update tbl_liquidacion_compra set estado_documento='n', mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_liquidacion_compra="+id_liquidacion_compra);
                                    }else{
                                        objDataBase.ejecutar("update tbl_liquidacion_compra set mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_liquidacion_compra="+id_liquidacion_compra);
                                    }
                                }
                            }else{
                                objDataBase.ejecutar("update tbl_liquidacion_compra set mensaje=' Error en documento No. " + numero + ". " + EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion)+
                                    "' where id_liquidacion_compra="+id_liquidacion_compra);
                            }
                        }catch(Exception e){
                            System.out.println("Error en envio: " + e.getMessage());
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                if(pkLiquidaciones.compareTo("")!=0){
                    pkLiquidaciones = pkLiquidaciones.substring( 0, pkLiquidaciones.length()-1 );
                    objDataBase.ejecutar("update tbl_liquidacion_compra set estado_documento='r' where id_liquidacion_compra in("+pkLiquidaciones+")");
                }

                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de envio de liquidaciones de compras y servicios al SRI");













                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de envio de guias de remision al SRI");
                //  Envio de notas de credito
                String pkguiasRemision= "";
                try{
                    ResultSet rs = objDataBase.consulta("select id_guia_remision, serie || '-' || numero as numero, clave_acceso from tbl_guia_remision where estado_documento='f' and anulado=false");
                    while(rs.next()){
                        try{
                            String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                            String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                            String id_guia_remision = rs.getString("id_guia_remision")!=null ? rs.getString("id_guia_remision") : "";

                            ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                            File ArchivoXML = new File(rutaArchivoFirmado + File.separatorChar + clave_acceso + ".xml");

                            respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, clave_acceso, Parametro.getServicioWebEnvio());
                            String estado = respuestaRecepcion.getEstado();
                            if(estado != null){
                                if(estado.equals("RECIBIDA")){
    //                                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + clave_acceso);
                                    pkguiasRemision += id_guia_remision + ",";
                                }else {
                                    String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                                    if (estado.equals("DEVUELTA")) {
                                        objDataBase.ejecutar("update tbl_guia_remision set estado_documento='n', mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_guia_remision="+id_guia_remision);
                                    }else{
                                        objDataBase.ejecutar("update tbl_guia_remision set mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                            "' where id_guia_remision="+id_guia_remision);
                                    }
                                }
                            }else{
                                objDataBase.ejecutar("update tbl_guia_remision set mensaje=' Error en documento No. " + numero + ". " + EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion)+
                                    "' where id_guia_remision="+id_guia_remision);
                            }
                        }catch(Exception e){
                            System.out.println("Error en envio: " + e.getMessage());
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                if(pkguiasRemision.compareTo("")!=0){
                    pkguiasRemision = pkguiasRemision.substring( 0, pkguiasRemision.length()-1 );
                    objDataBase.ejecutar("update tbl_guia_remision set estado_documento='r' where id_guia_remision in("+pkguiasRemision+")");
                }

                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de envio de guias de remision al SRI");












    ////////////////////////        AUTORIZACIONES      /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////








            ResultSet rsMails = objDataBase.consulta("select alias, nombre || ' ' || apellido as empleado, email from tbl_empleado where estado=true and eliminado=false order by alias");
            this.mails =  Matriz.ResultSetAMatriz(rsMails);
            String autorizacionXml = "";
            String pkFacturasAutorizadas = "";
            String pkRetencionesAutorizadas = "";
            String pkNotasCreditoAutorizadas = "";
            String pkLiquidacionesAutorizadas = "";
            String pkGuiasRemisionAutorizadas = "";








            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de consulta de autorizaciones de facturas al SRI");
            //  Facturas

    //        if(pkFacturasRecibidos.compareTo("")!=0){
                try{
                    ResultSet rs = objDataBase.consulta("select id_sucursal, id_factura_venta as id, serie_factura || '-' || num_factura as numero, vendedor as alias, clave_acceso "
                        + "from tbl_factura_venta where estado_documento='r' and anulado=false");
                    while(rs.next()){
                        String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "";
                        String id_documento = rs.getString("id")!=null ? rs.getString("id") : "";
                        String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                        String alias = rs.getString("alias")!=null ? rs.getString("alias") : "";
                        String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";

                        String respuestaAutoriz = "";
                        //File ArchivoXML = new File(DirectorioConfiguracion.getRutaArchivoFirmado()+ File.separatorChar + clave_acceso + ".xml");
                        respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(clave_acceso, clave_acceso + ".xml", Parametro.getServicioWebAutoriza());

                        if (respuestaAutoriz.equals("AUTORIZADO")) {
                            autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
                            // obtengo en numero de autorizacion
                            Xml xml = new Xml();
                            xml.SetXml(autorizacionXml);
                            String numAutorizacion = xml.getValor("numeroAutorizacion");
                            pkFacturasAutorizadas += "'" + clave_acceso + "',";
                            if( objDataBase.ejecutar("update tbl_factura_venta set autorizacion_fecha=now()::date, estado_documento='a', numero_autorizacion='"+numAutorizacion+"', mensaje=null, documento_xml=null where id_factura_venta="+id_documento) ){
                                if( this.existeArchivo(objDocumental, "tbl_factura_venta", id_documento) ){
                                    objDocumental.ejecutar("update tbl_documentos set documentotexto='"+autorizacionXml+"' where tabla='tbl_factura_venta' and id_tabla="+id_documento);
                                }else{
                                    objDocumental.ejecutar("insert into tbl_documentos(documentotexto, numero_documento, id_sucursal, tabla, id_tabla, campo_tabla) values('"+autorizacionXml +
                                            "', "+id_sucursal+numero+", "+id_sucursal+", 'tbl_factura_venta', "+id_documento+", 'documentoxml')");
                                }
                            }

                            //this.setDocumentosMail(objArchivo, clave_acceso);

                        }else{
                            if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                objDataBase.ejecutar("update tbl_factura_venta set estado_documento='n', mensaje='"+
                                        respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ").replace("\t", " ")+
                                        "' where id_factura_venta="+id_documento);
                                this.enviarMailEmpleado(alias, respuestaAutoriz, "Factura", numero);
                            }
                            System.out.println(respuestaAutoriz);
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println( e.getMessage() );
                }
    //        }

            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de consulta de autorizaciones de facturas al SRI");









            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de consulta de autorizaciones de retenciones al SRI");
            //     Retenciones

    //        if(pkRetencionesRecibidos.compareTo("")!=0){
                try{
                    ResultSet rs = objDataBase.consulta("select id_sucursal, id_retencion_compra as id, ret_num_serie || '-' || ret_num_retencion as numero, usuario as alias, clave_acceso "
                                + "from tbl_retencion_compra where estado_documento='r' and anulado=false");
                    while(rs.next()){
                        String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "";
                        String id_documento = rs.getString("id")!=null ? rs.getString("id") : "";
                        String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                        String alias = rs.getString("alias")!=null ? rs.getString("alias") : "";
                        String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";

                        String respuestaAutoriz = "";
                        //File ArchivoXML = new File(DirectorioConfiguracion.getRutaArchivoFirmado()+ File.separatorChar + clave_acceso + ".xml");
                        respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(clave_acceso, clave_acceso + ".xml", Parametro.getServicioWebAutoriza());

                        if (respuestaAutoriz.equals("AUTORIZADO")) {
                            autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
                            // obtengo en numero de autorizacion
                            Xml xml = new Xml();
                            xml.SetXml(autorizacionXml);
                            String numAutorizacion = xml.getValor("numeroAutorizacion");
                            pkRetencionesAutorizadas += "'" + clave_acceso + "',";
                            if( objDataBase.ejecutar("update tbl_retencion_compra set autorizacion_fecha=now()::date, estado_documento='a', numero_autorizacion='"+numAutorizacion+"', mensaje=null, documento_xml=null where id_retencion_compra="+id_documento) ){
                                if( this.existeArchivo(objDocumental, "tbl_retencion_compra", id_documento) ){
                                    objDocumental.ejecutar("update tbl_documentos set documentotexto='"+autorizacionXml+"' where tabla='tbl_retencion_compra' and id_tabla="+id_documento);
                                }else{
                                    objDocumental.ejecutar("insert into tbl_documentos(documentotexto, numero_documento, id_sucursal, tabla, id_tabla, campo_tabla) values('"+autorizacionXml +
                                            "', "+id_sucursal+numero+", "+id_sucursal+", 'tbl_retencion_compra', "+id_documento+", 'documentoxml')");
                                }
                            }

                            //this.setDocumentosMail(objArchivo, clave_acceso);

                        }else{
                            if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                objDataBase.ejecutar("update tbl_retencion_compra set estado_documento='n', mensaje='"+
                                        respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ").replace("\t", " ")+
                                        "' where id_retencion_compra="+id_documento);
                                this.enviarMailEmpleado(alias, respuestaAutoriz, "Retención", numero);
                            }
                            System.out.println(respuestaAutoriz);
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println( e.getMessage() );
                }
    //        }

            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de consulta de autorizaciones de retenciones al SRI");








            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de consulta de autorizaciones de notas de crédito al SRI");

            //     Notas de credito

    //        if(pkNotasCreditoRecibidos.compareTo("")!=0){
                try{
                    ResultSet rs = objDataBase.consulta("select id_sucursal, id_nota_credito_venta as id, serie_nota || '-' || num_nota as numero, usuario as alias, clave_acceso "
                                + "from tbl_nota_credito_venta where estado_documento='r' and anulado=false");
                    while(rs.next()){
                        String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "";
                        String id_documento = rs.getString("id")!=null ? rs.getString("id") : "";
                        String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                        String alias = rs.getString("alias")!=null ? rs.getString("alias") : "";
                        String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";

                        String respuestaAutoriz = "";
                        //File ArchivoXML = new File(DirectorioConfiguracion.getRutaArchivoFirmado()+ File.separatorChar + clave_acceso + ".xml");
                        respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(clave_acceso, clave_acceso + ".xml", Parametro.getServicioWebAutoriza());

                        if (respuestaAutoriz.equals("AUTORIZADO")) {
                            autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
                            // obtengo en numero de autorizacion
                            Xml xml = new Xml();
                            xml.SetXml(autorizacionXml);
                            String numAutorizacion = xml.getValor("numeroAutorizacion");
                            pkNotasCreditoAutorizadas += "'" + clave_acceso + "',";
                            if( objDataBase.ejecutar("update tbl_nota_credito_venta set autorizacion_fecha=now()::date, estado_documento='a', numero_autorizacion='"+numAutorizacion+"', mensaje=null, documento_xml=null where id_nota_credito_venta="+id_documento) ){
                                if( this.existeArchivo(objDocumental, "tbl_nota_credito_venta", id_documento) ){
                                    objDocumental.ejecutar("update tbl_documentos set documentotexto='"+autorizacionXml+"' where tabla='tbl_nota_credito_venta' and id_tabla="+id_documento);
                                }else{
                                    objDocumental.ejecutar("insert into tbl_documentos(documentotexto, numero_documento, id_sucursal, tabla, id_tabla, campo_tabla) values('"+autorizacionXml +
                                            "', "+id_sucursal+numero+", "+id_sucursal+", 'tbl_nota_credito_venta', "+id_documento+", 'documentoxml')");
                                }
                            }

                            //this.setDocumentosMail(objArchivo, clave_acceso);

                        }else{
                            if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                objDataBase.ejecutar("update tbl_nota_credito_venta set estado_documento='n', mensaje='"+
                                        respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ").replace("\t", " ")+
                                        "' where id_nota_credito_venta="+id_documento);
                                this.enviarMailEmpleado(alias, respuestaAutoriz, "Nota de crédito", numero);
                            }
                            System.out.println(respuestaAutoriz);
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println( e.getMessage() );
                }
    //        }

            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de consulta de autorizaciones de notas de crédito al SRI");











            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de consulta de autorizaciones de liquidaciones de compras y servicios al SRI");

            //     Liquidaciones de compras

    //        if(pkLiquidaciones.compareTo("")!=0){
                try{
                    ResultSet rs = objDataBase.consulta("select id_sucursal, id_liquidacion_compra as id, serie_liquidacion || '-' || num_liquidacion as numero, usuario as alias, clave_acceso "
                                + "from tbl_liquidacion_compra where estado_documento='r' and anulado=false");
                    while(rs.next()){
                        String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "";
                        String id_documento = rs.getString("id")!=null ? rs.getString("id") : "";
                        String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                        String alias = rs.getString("alias")!=null ? rs.getString("alias") : "";
                        String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";

                        String respuestaAutoriz = "";
                        //File ArchivoXML = new File(DirectorioConfiguracion.getRutaArchivoFirmado()+ File.separatorChar + clave_acceso + ".xml");
                        respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(clave_acceso, clave_acceso + ".xml", Parametro.getServicioWebAutoriza());

                        if (respuestaAutoriz.equals("AUTORIZADO")) {
                            autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
                            // obtengo en numero de autorizacion
                            Xml xml = new Xml();
                            xml.SetXml(autorizacionXml);
                            String numAutorizacion = xml.getValor("numeroAutorizacion");
                            pkLiquidacionesAutorizadas += "'" + clave_acceso + "',";
                            if( objDataBase.ejecutar("update tbl_liquidacion_compra set autorizacion_fecha=now()::date, estado_documento='a', numero_autorizacion='"+numAutorizacion+"', mensaje=null where id_liquidacion_compra="+id_documento) ){
                                if( this.existeArchivo(objDocumental, "tbl_liquidacion_compra", id_documento) ){
                                    objDocumental.ejecutar("update tbl_documentos set documentotexto='"+autorizacionXml+"' where tabla='tbl_liquidacion_compra' and id_tabla="+id_documento);
                                }else{
                                    objDocumental.ejecutar("insert into tbl_documentos(documentotexto, numero_documento, id_sucursal, tabla, id_tabla, campo_tabla) values('"+autorizacionXml +
                                            "', "+id_sucursal+numero+", "+id_sucursal+", 'tbl_liquidacion_compra', "+id_documento+", 'documentoxml')");
                                }
                            }

                            //this.setDocumentosMail(objArchivo, clave_acceso);

                        }else{
                            if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                objDataBase.ejecutar("update tbl_liquidacion_compra set estado_documento='n', mensaje='"+
                                        respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ").replace("\t", " ")+
                                        "' where tbl_liquidacion_compra="+id_documento);
                                this.enviarMailEmpleado(alias, respuestaAutoriz, "Liquidación de compras y servicios", numero);
                            }
                            System.out.println(respuestaAutoriz);
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println( e.getMessage() );
                }
    //        }

            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de consulta de autorizaciones de liquidaciones de compras y servicios al SRI");













            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de consulta de autorizaciones de guias de remision al SRI");

            //     Guias de remision

            if(pkguiasRemision.compareTo("")!=0){
                try{
                    ResultSet rs = objDataBase.consulta("select id_sucursal, id_guia_remision as id, serie || '-' || numero as numero, usuario as alias, clave_acceso "
                                + "from tbl_guia_remision where estado_documento='r'");
                    while(rs.next()){
                        String id_sucursal = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "";
                        String id_documento = rs.getString("id")!=null ? rs.getString("id") : "";
                        String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                        String alias = rs.getString("alias")!=null ? rs.getString("alias") : "";
                        String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";

                        String respuestaAutoriz = "";
                        //File ArchivoXML = new File(DirectorioConfiguracion.getRutaArchivoFirmado()+ File.separatorChar + clave_acceso + ".xml");
                        respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(clave_acceso, clave_acceso + ".xml", Parametro.getServicioWebAutoriza());

                        if (respuestaAutoriz.equals("AUTORIZADO")) {
                            autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
                            // obtengo en numero de autorizacion
                            Xml xml = new Xml();
                            xml.SetXml(autorizacionXml);
                            String numAutorizacion = xml.getValor("numeroAutorizacion");
                            pkGuiasRemisionAutorizadas += "'" + clave_acceso + "',";
                            if( objDataBase.ejecutar("update tbl_guia_remision set autorizacion_fecha=now()::date, estado_documento='a', numero_autorizacion='"+numAutorizacion+"', mensaje=null where id_guia_remision="+id_documento) ){
                                if( this.existeArchivo(objDocumental, "tbl_guia_remision", id_documento) ){
                                    objDocumental.ejecutar("update tbl_documentos set documentotexto='"+autorizacionXml+"' where tabla='tbl_guia_remision' and id_tabla="+id_documento);
                                }else{
                                    objDocumental.ejecutar("insert into tbl_documentos(documentotexto, numero_documento, id_sucursal, tabla, id_tabla, campo_tabla) values('"+autorizacionXml +
                                            "', "+id_sucursal+numero+", "+id_sucursal+", 'tbl_guia_remision', "+id_documento+", 'documentoxml')");
                                }
                            }

                            //this.setDocumentosMail(objArchivo, clave_acceso);

                        }else{
                            if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                objDataBase.ejecutar("update tbl_guia_remision set estado_documento='n', mensaje='"+
                                        respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ").replace("\t", " ")+
                                        "' where id_guia_remision="+id_documento);
                                this.enviarMailEmpleado(alias, respuestaAutoriz, "Guía de Remisión", numero);
                            }
                            System.out.println(respuestaAutoriz);
                        }
                    }
                    rs.close();
                }catch(Exception e){
                    System.out.println( e.getMessage() );
                }
            }

            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de consulta de autorizaciones de guias de remision al SRI");

        
        
            
///////////////////////     ENVIO DE MAILS      ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        
        
        
//            Correo correo = new Correo( Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave() );


//            try{

//                String _DIR_PDF = Parametro.getRutaArchivos() + "pdfs/";


/*                PARA PRUEBAS 
                _DIR_PDF = "/home/sistemas/Documents/fe/pdfs/";
                String pkFacturasAutorizadas = "";
                try{
                    ResultSet rs = objDataBase.consulta("select clave_acceso from tbl_factura_venta where estado_documento='a' and fecha_emision between '2019-02-02' and '2019-02-02'");
                    while(rs.next()){
                        pkFacturasAutorizadas += "'" + rs.getString("clave_acceso") + "',";
                    }
                }catch(Exception e){
                }     
*/

//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Envío de correos de facturas a clientes");
//
//                if(pkFacturasAutorizadas.compareTo("")!=0){
//                    // FACTURAS
//                    try{
//                        ResultSet rsC = objDataBase.consulta("select fv.id_factura_venta, c.razon_social, c.email, fv.fecha_emision, fv.total ,clave_acceso, fv.serie_factura || '-' || fv.num_factura as numero_factura, id_factura_venta "
//                        + "from tbl_factura_venta fv,tbl_cliente c "
//                        + "where fv.id_cliente=c.id_cliente and fv.estado_documento='a' and c.email<>'' and clave_acceso in ("+pkFacturasAutorizadas.substring(0, pkFacturasAutorizadas.length()-1)+") order by fv.clave_acceso");
//                        while(rsC.next()){
//                            String id_factura_venta=rsC.getString("id_factura_venta");
//                            String nombres=rsC.getString("razon_social");
//                            String email=rsC.getString("email");
//                            String fecha_emision=rsC.getString("fecha_emision");
//                            String total=rsC.getString("total");
//                            String clave_acceso=rsC.getString("clave_acceso");
//                            String serie_factura=rsC.getString("numero_factura");
//
//                            String documento_xml="";
//                            try{
//                                ResultSet res = objDocumental.consulta("select documentotexto from tbl_documentos where tabla='tbl_factura_venta' and id_tabla="+id_factura_venta);
//                                if(res.next()){
//                                    documento_xml = res.getString("documentotexto")!=null ? res.getString("documentotexto") : "";
//                                    res.close();
//                                }
//                            }catch(Exception e){
//                                System.out.println("Error al leer xml de DB documental" + e.getMessage() + "\n");
//                                e.printStackTrace();
//                            }
//
//                            //String path = String.valueOf(request.getRequestURL());
//                            //path = path.substring(0, path.lastIndexOf("/"));
//                            frmGeneraPdf pdf = new frmGeneraPdf();
//                            documento_xml = pdf.quitarTildes(documento_xml);
//                            String xml = _DIR_PDF + this.getArchivoXml(_DIR_PDF, clave_acceso, documento_xml);
//                            pdf.GenerarFactura(objDataBase, xml, _DIR_PDF, clave_acceso);
//
//                            List adjuntos=new ArrayList();
//                            adjuntos.add(xml+".xml");
//                            adjuntos.add(xml+".pdf");
//                            StringBuilder mensaje=new StringBuilder();
//
//                            mensaje.append("<i>Estimado Cliente.</i><br />");
//                            mensaje.append("<b>"+nombres+"</b /><br /><br />");
//                            mensaje.append("Con el prop&oacute;sito de brindarle un mejor servicio, SAITEL cambi&oacute; sus facturas f&iacute;sicas por electr&oacute;nicas, lo que le permitir&aacute; contar con informaci&oacute;n "
//                                    + "inmediata sobre los valores facturados y fechas l&iacute;mites de pago. Con esta medida adem&aacute;s, contribuimos a la preservaci&oacute;n del medio ambiente.<br />"
//                                    +"El archivo adjunto corresponde a la Factura Electr&oacute;nica, tributaria y legalmente v&aacute;lida para las declaraciones de impuestos ante el SRI.<br /><br />");
//                            mensaje.append("<b>Resumen</b><br />");
//                            mensaje.append("<b>No. DE FACTURA: </b>"+serie_factura+"<br />");
//                            mensaje.append("<b>FECHA DE EMISION: </b>"+fecha_emision+"<br />");
//                            mensaje.append("<b>VALOR PAGADO: </b>"+total+"<br />");
//                            mensaje.append("<b>CLAVE DE ACCESO: </b>"+clave_acceso+"<br /><br />");
//                            mensaje.append("También puede realizar la impresi&oacute;n de su documento en pdf <a href='http://www.saitel.ec/pag/electronico.html' target='_blank'> www.saitel.ec</a><br><br>");
//                            mensaje.append("Atentamente.<br>");
//                            mensaje.append("<b>Soluciones Inform&aacute;ticas Avanzadas y Telecomunicaciones SAITEL</b><br />");
//                            mensaje.append("<b>IMPORTANTE:</b><br>Este correo es informativo, favor no responder al mismo, ya que no se encuentra habilitada para recibir mensajes.<b>");
//
////                            correo.enviar(email, "", "", "SAITEL - FACTURA ELECTRONICA", mensaje, true, adjuntos);
//                            Correo.enviar( Parametro.getSvrMail(), 
//                                    Parametro.getSvrMailPuerto(), 
//                                    Parametro.getRemitente(), 
//                                    Parametro.getRemitenteClave(), 
//                                    email, 
//                                    "", 
//                                    "",
//                                    "SAITEL - FACTURA ELECTRONICA",
//                                    mensaje, 
//                                    true, 
//                                    adjuntos);
//                            
//                            
//                        } 
//                    }catch(Exception e){
//                        System.out.println("Error al generar archivos " + e.getMessage() + "\n");
//                        e.printStackTrace();
//                    }
//                }
//
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de envío de correos de facturas a clientes");
//
//
//
//
//
//
//
//
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Envío de correos de retenciones a proveedores");
//                // RETENCIONES
//
//                if(pkRetencionesAutorizadas.compareTo("")!=0){
//                    try{
//                        ResultSet rsP = objDataBase.consulta("select rc.id_retencion_compra, p.razon_social, rc.ret_num_serie||'-'||rc.ret_num_retencion as num_retencion, "
//                            + "rc.ret_fecha_emision, rc.ret_ejercicio_fiscal, rc.ret_impuesto_retenido, rc.ret_ejercicio_fiscal_mes, rc.emision, rc.clave_acceso, p.email "
//                            + "from tbl_retencion_compra rc, tbl_factura_compra fc, tbl_proveedor p "
//                            + "where estado_documento='a' and rc.id_factura_compra=fc.id_factura_compra and fc.id_proveedor=p.id_proveedor "
//                            + "and rc.clave_acceso in ("+pkRetencionesAutorizadas.substring(0, pkRetencionesAutorizadas.length()-1)+");");
//
//                        while(rsP.next()){
//                            String id_retencion_compra=rsP.getString("id_retencion_compra");
//                            String nombres=rsP.getString("razon_social");
//                            String serie_factura=rsP.getString("num_retencion");
//                            String fecha_emision=rsP.getString("ret_fecha_emision");
//                            String retenido=rsP.getString("ret_impuesto_retenido");
//                            String ejercicio_fiscal=rsP.getString("ret_ejercicio_fiscal_mes")+"-"+rsP.getString("ret_ejercicio_fiscal");
//                            String clave_acceso=rsP.getString("clave_acceso");
//                            String email=rsP.getString("email");
//
//                            String documento_xml="";
//                            try{
//                                ResultSet res = objDocumental.consulta("select documentotexto from tbl_documentos where tabla='tbl_retencion_compra' and id_tabla="+id_retencion_compra);
//                                if(res.next()){
//                                    documento_xml = res.getString("documentotexto")!=null ? res.getString("documentotexto") : "";
//                                    res.close();
//                                }
//                            }catch(Exception e){
//                                e.printStackTrace();
//                            }
//
//                            //String path = String.valueOf(request.getRequestURL());
//                            //path = path.substring(0, path.lastIndexOf("/"));
//                            frmGeneraPdf pdf= new frmGeneraPdf();
//                            documento_xml=pdf.quitarTildes(documento_xml);
//                            String xml = _DIR_PDF + this.getArchivoXml(_DIR_PDF, clave_acceso, documento_xml);
//                            pdf.GenerarFactura(objDataBase, xml, _DIR_PDF, clave_acceso);
//
//                            List adjuntos=new ArrayList();
//                            adjuntos.add(xml+".xml");
//                            adjuntos.add(xml+".pdf");
//                            StringBuilder mensaje=new StringBuilder();
//
//                            mensaje.append("<i>Estimado Proveedor.</i><br />");
//                            mensaje.append("<b>"+nombres+"</b /><br /><br />");
//                            mensaje.append("Con el prop&oacute;sito de brindarle un mejor servicio, SAITEL cambi&oacute; sus facturas f&iacute;sicas por electr&oacute;nicas, lo que le permitir&aacute; contar con informaci&oacute;n "
//                                    + "inmediata sobre los valores facturados y fechas l&iacute;mites de pago. Con esta medida adem&aacute;s, contribuimos a la preservaci&oacute;n del medio ambiente.<br />"
//                                    +"El archivo adjunto corresponde a la Factura Electr&oacute;nica, tributaria y legalmente v&aacute;lida para las declaraciones de impuestos ante el SRI.<br /><br />");
//                            mensaje.append("<b>Resumen</b><br />");
//                            mensaje.append("<b>No. DE FACTURA: </b>"+serie_factura+"<br>");
//                            mensaje.append("<b>FECHA DE EMISION: </b>"+fecha_emision+"<br>");
//                            mensaje.append("<b>RETENCION DEL EJERCICIO FICAL: </b>"+ejercicio_fiscal+"<br>");
//                            mensaje.append("<b>IMPUESTO RETENIDO: </b>"+retenido+"<br>");
//                            mensaje.append("<b>CLAVE DE ACCESO: </b>"+clave_acceso+"<br /><br />");
//                            mensaje.append("También puede realizar la impresi&oacute;n de su documento en pdf <a href='http://www.saitel.ec/pag/electronico.html' target='_blank'> www.saitel.ec</a><br><br>");
//                            mensaje.append("Atentamente.<br>");
//                            mensaje.append("<b>Soluciones Inform&aacute;ticas Avanzadas y Telecomunicaciones SAITEL</b><br />");
//                            mensaje.append("<b>IMPORTANTE:</b><br>Este correo es informativo, favor no responder al mismo, ya que no se encuentra habilitada para recibir mensajes.<b>");
//
////                            correo.enviar(email, "", "", "SAITEL - RETENCION ELECTRONICA", mensaje, true, adjuntos);
//                            Correo.enviar( Parametro.getSvrMail(), 
//                                    Parametro.getSvrMailPuerto(), 
//                                    Parametro.getRemitente(), 
//                                    Parametro.getRemitenteClave(), 
//                                    email, 
//                                    "", 
//                                    "",
//                                    "SAITEL - RETENCION ELECTRONICA",
//                                    mensaje, 
//                                    true, 
//                                    adjuntos);
//
//                        } 
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de envío de correos de retenciones a proveedores");
//
//
//
//
//
//
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Envío de correos de notas de crédito a clientes");
//                // NOTAS DE CREDITO
//                if(pkNotasCreditoAutorizadas.compareTo("")!=0){
//                    try{
//                        ResultSet rsN = objDataBase.consulta("select nc.id_nota_credito_venta, nc.razon_social, c.email, nc.serie_nota||'-'||nc.num_nota as num_nota_credito, "
//                                + "nc.fecha_emision, nc.documento_modifica, nc.concepto, nc.cxp, nc.clave_acceso "
//                                + "from tbl_nota_credito_venta nc, tbl_cliente c "
//                                + "where nc.estado_documento='a' and c.id_cliente=nc.id_cliente and nc.clave_acceso in ("+pkNotasCreditoAutorizadas.substring(0, pkNotasCreditoAutorizadas.length()-1)+");");
//
//                        while(rsN.next()){
//                            String id_nota_credito_venta=rsN.getString("id_nota_credito_venta");
//                            String nombres=rsN.getString("razon_social");
//                            String nota_credito=rsN.getString("num_nota_credito");
//                            String fecha_emision=rsN.getString("fecha_emision");
//                            String doc_modifica=rsN.getString("documento_modifica");
//                            String concepto=rsN.getString("concepto");
//                            String valor_total=rsN.getString("cxp");             
//                            String clave_acceso=rsN.getString("clave_acceso");
//                            String email=rsN.getString("email");
//
//                            String documento_xml="";
//                            try{
//                                ResultSet res = objDocumental.consulta("select documentotexto from tbl_documentos where tabla='tbl_nota_credito_venta' and id_tabla="+id_nota_credito_venta);
//                                if(res.next()){
//                                    documento_xml = res.getString("documentotexto")!=null ? res.getString("documentotexto") : "";
//                                    res.close();
//                                }
//                            }catch(Exception e){
//                                e.printStackTrace();
//                            }
//
//                            //String path = String.valueOf(request.getRequestURL());
//                            //path = path.substring(0, path.lastIndexOf("/"));
//                            frmGeneraPdf pdf= new frmGeneraPdf();
//                            documento_xml=pdf.quitarTildes(documento_xml);
//                            String xml = _DIR_PDF + this.getArchivoXml(_DIR_PDF, clave_acceso, documento_xml);  // genro el archivo xml
//                            pdf.GenerarFactura(objDataBase, xml, _DIR_PDF, clave_acceso);
//
//                            List adjuntos = new ArrayList();
//                            adjuntos.add(xml+".xml");
//                            adjuntos.add(xml+".pdf");
//                            StringBuilder mensaje=new StringBuilder();
//
//                            mensaje.append("<i>Estimado Cliente.</i><br />");
//                            mensaje.append("<b>"+nombres+"</b /><br /><br />");
//                            mensaje.append("Con el prop&oacute;sito de brindarle un mejor servicio, SAITEL cambi&oacute; sus facturas f&iacute;sicas por electr&oacute;nicas, lo que le permitir&aacute; contar con informaci&oacute;n "
//                                    + "inmediata sobre los valores facturados y fechas l&iacute;mites de pago. Con esta medida adem&aacute;s, contribuimos a la preservaci&oacute;n del medio ambiente.<br />"
//                                    +"El archivo adjunto corresponde a la Factura Electr&oacute;nica, tributaria y legalmente v&aacute;lida para las declaraciones de impuestos ante el SRI.<br /><br />");
//                            mensaje.append("<b>Resumen</b><br />");
//                            mensaje.append("<b>No. DE NOTA DE CREDITO: </b>"+nota_credito+"<br>");
//                            mensaje.append("<b>FECHA DE EMISION: </b>"+fecha_emision+"<br>");
//                            mensaje.append("<b>No DOCUMENTO AL QUE MODIFICA: </b>"+doc_modifica+"<br>");
//                            mensaje.append("<b>CONCEPTO: </b>"+concepto+"<br>");
//                            mensaje.append("<b>VALOR TOTAL: </b>"+valor_total+"<br>");
//                            mensaje.append("<b>CLAVE DE ACCESO: </b>"+clave_acceso+"<br /><br />");
//                            mensaje.append("También puede realizar la impresi&oacute;n de su documento en pdf <a href='http://www.saitel.ec/pag/electronico.html' target='_blank'> www.saitel.ec</a><br><br>");
//                            mensaje.append("Atentamente.<br>");
//                            mensaje.append("<b>Soluciones Inform&aacute;ticas Avanzadas y Telecomunicaciones SAITEL</b><br />");
//                            mensaje.append("<b>IMPORTANTE:</b><br>Este correo es informativo, favor no responder al mismo, ya que no se encuentra habilitada para recibir mensajes.<b>");
//
////                            correo.enviar(email, "", "", "SAITEL - NOTA DE CREDITO ELECTRONICA", mensaje, true, adjuntos);
//                            Correo.enviar( Parametro.getSvrMail(), 
//                                    Parametro.getSvrMailPuerto(), 
//                                    Parametro.getRemitente(), 
//                                    Parametro.getRemitenteClave(), 
//                                    email, 
//                                    "", 
//                                    "",
//                                    "SAITEL - NOTA DE CREDITO ELECTRONICA",
//                                    mensaje, 
//                                    true, 
//                                    adjuntos);
//
//                        } 
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de envío de correos de notas de crédito a clientes");
//                
//            
////                }finally{
////                    correo.cerrar();
////                }
            
            }   //  del if que valida la hora
                       
            
        }catch(Exception e){
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": " + e.getMessage());
        }finally{
            objDataBase.cerrar();
            objDocumental.cerrar();
        }
        
    }
    
    
    public boolean existeArchivo(DataBase objDocumental, String tabla, String idTabla)
    {
        ResultSet rs = objDocumental.consulta("select * from tbl_documentos where tabla='"+tabla+"' and id_tabla='"+idTabla+"'");
        return objDocumental.getFilas(rs) > 0;
    }
    
    
    public String getArchivoXml(String path, String claveAcceso, String docXml)
    {
        
        if(docXml.compareTo("")!=0){
            try{
                File _archivo = new File(path, claveAcceso);
                if(!_archivo.exists()){
                    //byte[] bytes = (res.getString(campoBytea)!=null) ? res.getBytes(campoBytea) : null;
                    RandomAccessFile archivo = new RandomAccessFile(path + claveAcceso+".xml","rw");
                    archivo.writeBytes(docXml);
                    archivo.close();
                }
            }catch(Exception e){
                System.out.println("Error al obtener y guardar documento XML " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
       
        return claveAcceso;
    }
    
    private boolean enviarMailEmpleado (String alias, String respuestaAutoriz, String documento, String numero)
    {
        try{
            int i = Matriz.enMatriz(this.mails, alias, 0);
            if(i!=-1){
                StringBuilder mensaje = new StringBuilder();
                mensaje.append("Estimado(a) ");
                mensaje.append(this.mails[i][1]);
                mensaje.append("<br /><br />Se ha encontrado un inconveniente en la "+documento+" No. "+numero+". El SRI ha devuelto el siguiente mensaje de error: <br />");
                mensaje.append(respuestaAutoriz);
                mensaje.append(".<br/>Favor de corregir el error y vuelva a enviar el comprobante al SRI.");
                mensaje.append("<br/><br/>Att.<br/>ASESOR VIRTUAL <br />SAITEL");
//                return Correo.enviar(Parametro.getSvrMail(), Parametro.getSvrMailPuerto(), Parametro.getRemitente(), Parametro.getRemitenteClave(), 
//                        this.mails[i][2], "contabilidad@saitel.ec", "sistemas@saitel.ec", "NOTIFICACION DE NO AUTORIZACION", mensaje, true, null);
            }
        }catch(Exception e){
            e.printStackTrace();
        }    
        return false;
    }
     
}
