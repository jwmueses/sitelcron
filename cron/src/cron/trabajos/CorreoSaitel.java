/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Jorge
 */
public class CorreoSaitel extends DataBase {

//    SimpleDateFormat formateador = new SimpleDateFormat("yyyy/MM/dd");
    String esquema = "public";

    public CorreoSaitel(String m, String eq, int p, String db, String u, String c) 
    {
        super(m, p, db, u, c);
        this.esquema = eq;
    }

    public boolean enviar(String destinatario, String asunto, String cuerpo, List adjunto)
    {
        List sql = new ArrayList();
        sql.add("insert into "+this.esquema+".tbl_correo(usuario_remitente, usuario_destino, titulo, cuerpo) "
                + "values('cron', '"+destinatario+"', '"+asunto+"', '"+cuerpo+"');");
        if(adjunto != null){
            
        }
        return this.transacciones(sql);
    }

}
