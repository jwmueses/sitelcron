/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sistemas
 */
public class Notificacion extends DataBase {
    
    private String mensajeHtml= "";
    
    public Notificacion(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    
    public void setMensaje(String mensaje)
    {
        this.mensajeHtml = mensaje;
    }
    
    public boolean notificar(String nombreNotificacion, List remplazosJSON, List adjuntos)
    {
        try{
            ResultSet rs = this.consulta("select asunto, html_notificacion, email_notificacion from tbl_notificaciones_html where nombre_notificacion='"+nombreNotificacion+"';");
            
            if(rs.next()){
                String asunto = rs.getString("asunto")!=null ? rs.getString("asunto") : "";
                String mailDestino = rs.getString("email_notificacion")!=null ? rs.getString("email_notificacion") : "sistemas@saitel.ec";
                String mensaje = rs.getString("html_notificacion")!=null ? rs.getString("html_notificacion") : "";
                mensaje = this.mensajeHtml.compareTo("")==0 ? mensaje : this.mensajeHtml;
                rs.close();
                
                if(remplazosJSON != null){
                    Iterator it = remplazosJSON.iterator();
                    while(it.hasNext()){
                        String remplazo[] = (String[])it.next();
                        mensaje = mensaje.replace(remplazo[0], remplazo[1]);
                    }
                }
                
                if( Correo.enviar(Parametro.getSvrMail(), 
                            Parametro.getSvrMailPuerto(), 
                            Parametro.getRemitente(), 
                            Parametro.getRemitenteClave(), 
                            mailDestino, 
                            "", 
                            "", 
                            asunto, 
                            new StringBuilder(mensajeHtml), 
                            true,
                            adjuntos
                ) ){
                    this.mensajeHtml = "";
                    return true;
                }
            }
        } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
    
}
