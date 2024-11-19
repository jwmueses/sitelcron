/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

/**
 *
 * @author jorge
 */
public class Parametro {
    private static final String _ip = "127.0.0.1";      //  127.0.0.1     pruebas = 192.168.217.16     produccion = 192.168.217.21
    private static final int _puerto = 5432;
    private static final String _db = "db_isp";
    private static final String _usuario = "postgres";
    private static final String _clave = "Gi%9875.-*5+$)";    //  Gi%9875.-*5+$)      pruebas = A0Lpni2++
    
    private static final String doc_ip = "192.168.217.31";      //  127.0.0.1     pruebas = 192.168.217.16    produccion = 192.168.217.31
    private static final int doc_puerto = 5432;
    private static final String doc_db = "db_isp_documentos";
    private static final String doc_usuario = "postgres";
    private static final String doc_clave = "Gi%9875.-*5+$)";    //  Gi%9875.-*5+$)      pruebas = A0Lpni2++
    
    private static final String mssql_ip = "192.168.217.26";      
    private static final int mssql_puerto = 1433;
    private static final String mssql_db = "FASSQL";
    private static final String mssql_usuario = "userBio";
    private static final String mssql_clave = "sa2005"; 
    
    private static final String esta_ip = "127.0.0.1";      
    private static final int esta_puerto = 5432;
    private static final String esta_db = "db_isp_consulta";
    private static final String esta_usuario = "postgres";
    private static final String esta_clave = "Gi%9875.-*5+$)";  
    
    private static final String red_social_ip = "127.0.0.1";
    private static final String red_social_esquema = "correo";
    private static final int red_social_puerto = 5432;
    private static final String red_social_db = "db_red_social";
    private static final String red_social_usuario = "us_red_social";
    private static final String red_social_clave = "4tPQ5^1lnUu#gLuh1F0#";  
    
    private static final String _svrMail = "mail.saitel.ec";
    private static final int _svrMailPuerto = 465;
    private static final String _remitante = "notificaciones.financiero@saitel.ec";
    private static final String _remitanteClave = "saitel2022";
    private static final String _rutaArchivos = "/opt/lampp/htdocs/anexos/fe/"; //    /opt/lampp/htdocs/anexos/fe/        /home/sistemas/Documents/fe/    
    private static final String _servicioWebEnvio = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    private static final String _servicioWebAutoriza = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
    

    public static String getRed_social_ip() {
        return red_social_ip;
    }

    public static String getRed_social_esquema() {
        return red_social_esquema;
    }

    public static int getRed_social_puerto() {
        return red_social_puerto;
    }

    public static String getRed_social_db() {
        return red_social_db;
    }

    public static String getRed_social_usuario() {
        return red_social_usuario;
    }

    public static String getRed_social_clave() {
        return red_social_clave;
    }
    
    public static String getIp()
    {
        return _ip;
    }
    
    public static int getPuerto()
    {
        return _puerto;
    }
    
    public static String getBaseDatos()
    {
        return _db;
    }
    
    public static String getUsuario()
    {
        return _usuario;
    }
    
    public static String getClave()
    {
        return _clave;
    }
    
    public static String getDocumentalIp()
    {
        return doc_ip;
    }
    
    public static int getDocumentalPuerto()
    {
        return doc_puerto;
    }
    
    public static String getDocumentalBaseDatos()
    {
        return doc_db;
    }
    
    public static String getDocumentalUsuario()
    {
        return doc_usuario;
    }
    
    public static String getDocumentalClave()
    {
        return doc_clave;
    }
    
    public static String getMsSqlIp()
    {
        return mssql_ip;
    }
    
    public static int getMsSqlPuerto()
    {
        return mssql_puerto;
    }
    
    public static String getMsSqlBaseDatos()
    {
        return mssql_db;
    }
    
    public static String getMsSqlUsuario()
    {
        return mssql_usuario;
    }
    
    public static String getMsSqlClave()
    {
        return mssql_clave;
    }

    
    
    public static String getEstadisticaIp()
    {
        return esta_ip;
    }
    
    public static int getEstadisticaPuerto()
    {
        return esta_puerto;
    }
    
    public static String getEstadisticaBaseDatos()
    {
        return esta_db;
    }
    
    public static String getEstadisticaUsuario()
    {
        return esta_usuario;
    }
    
    public static String getEstadisticaClave()
    {
        return esta_clave;
    }
    
    
    
    public static String getSvrMail()
    {
        return _svrMail;
    }
    
    public static int getSvrMailPuerto()
    {
        return _svrMailPuerto;
    }
    
    public static String getRemitente()
    {
        return _remitante;
    }
    
    public static String getRemitenteClave()
    {
        return _remitanteClave;
    }
    
    public static String getRutaArchivos()
    {
        return _rutaArchivos;
    }
    
    public static String getServicioWebEnvio()
    {
        return _servicioWebEnvio;
    }
    
    public static String getServicioWebAutoriza()
    {
        return _servicioWebAutoriza;
    }
    
}
