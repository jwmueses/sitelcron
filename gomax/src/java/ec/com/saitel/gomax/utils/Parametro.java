/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.utils;

/**
 *
 * @author jorge
 */
public class Parametro 
{
//    private static final String _ip = "192.168.217.16";      //  127.0.0.1     pruebas = 192.168.217.16     produccion = 192.168.217.21
//    private static final int _puerto = 5432;
//    private static final String _db = "db_isp";
//    private static final String _usuario = "postgres";
//    private static final String _clave = "Gi%9875.-*5+$)(";    //  Gi%9875.-*5+$)      pruebas = A0Lpni2++
    
    private static final String doc_ip = "92.168.217.16";      //  127.0.0.1     pruebas = 192.168.217.16    produccion = 192.168.217.21
    private static final int doc_puerto = 5432;
    private static final String doc_db = "db_isp_documentos";
    private static final String doc_usuario = "postgres";
    private static final String doc_clave = "Gi%9875.-*5+$)(";    //  Gi%9875.-*5+$)      pruebas = A0Lpni2++
    
    private static final String _svrMail = "mail.saitel.ec";
    private static final int _svrMailPuerto = 465;
    private static final String _remitante = "notificaciones.financiero@saitel.ec";
    private static final String _remitanteClave = "saitel2022";
    private static final String _rutaArchivos = "/opt/lampp/htdocs/anexos/fe/"; //    /opt/lampp/htdocs/anexos/fe/        /home/sistemas/Documents/fe/    
    private static final String _DOCS_ELECTRONICOS = "/opt/lampp/htdocs/anexos/fe/";
    
//    public static String getIp()
//    {
//        return _ip;
//    }
//    
//    public static int getPuerto()
//    {
//        return _puerto;
//    }
//    
//    public static String getBaseDatos()
//    {
//        return _db;
//    }
//    
//    public static String getUsuario()
//    {
//        return _usuario;
//    }
//    
//    public static String getClave()
//    {
//        return _clave;
//    }
    
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
    
    public static String getDOCS_ELECTRONICOS() {
        return _DOCS_ELECTRONICOS;
    }
    
}
