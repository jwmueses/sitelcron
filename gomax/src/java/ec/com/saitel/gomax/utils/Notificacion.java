/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.utils;

/**
 *
 * @author sistemas
 */
public class Notificacion 
{
    private final String _MAIL_SVR = "mail.saitelapp.ec";
    private final int _MAIL_PUERTO = 465;
    private final String _MAIL_REMITENTE = "notificaciones@saitelapp.ec";
    private final String _MAIL_REMITENTE_CLAVE = "&~Hw-TRT)$6'X^2";
    
    public boolean enviarVerificacionCorreo(String destinatario, String jwt, String idClienteSuscripcionGomax)
    {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("<h2>SAITEL</h2>");
        mensaje.append("<h3>Verifica tu direcci&oacute;n de correo electr&oacute;nico</h3><p>&nbsp;</p>");
        mensaje.append("<p><a style=\"border:1px #8AC5FF solid;color:#333;background-color:#c7deff;padding:12px;text-decoration:none;font-size:16px;font-weight:bold;\" ");
        mensaje.append("href=\"http://192.168.217.16:8080/gomax/html/television_correo_verificacion.html?em=");
        mensaje.append(destinatario);
        mensaje.append("&jwt=");
        mensaje.append(jwt);
        mensaje.append("&idClienteSusGo=");
        mensaje.append(idClienteSuscripcionGomax);
        mensaje.append("\">&nbsp;&nbsp;&nbsp; Verificar Correo &nbsp;&nbsp;&nbsp; </a></p>");
        
        return Correo.enviar(this._MAIL_SVR, 
                this._MAIL_PUERTO, 
                this._MAIL_REMITENTE, 
                this._MAIL_REMITENTE_CLAVE, 
                destinatario, "", "", 
                "Verifica tu dirección de correo", mensaje, true, null, null);
    }
    
}
