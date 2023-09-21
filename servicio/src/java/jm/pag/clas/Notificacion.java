/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author wilso
 */
public class Notificacion extends DataBase {

    public Notificacion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public String getNotificacionHtml(String nombre) {
        String plantilla = "";
        try {
            ResultSet rs = this.consulta("select * from tbl_notificaciones_html where nombre_notificacion='" + nombre + "';");
            if (rs.next()) {
                plantilla = rs.getString("html_notificacion") != null ? rs.getString("html_notificacion") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plantilla;
    }

    public String getNotificacionEmail(String nombre) {
        String plantilla = "";
        try {
            ResultSet rs = this.consulta("select * from tbl_notificaciones_html where nombre_notificacion='" + nombre + "';");
            if (rs.next()) {
                plantilla = rs.getString("email_notificacion") != null ? rs.getString("email_notificacion") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plantilla;
    }

    public String getNotificacionEmail(String nombre, String id_sucursal) {
        String plantilla = "";
        try {
            ResultSet rs = this.consulta("select * from tbl_notificaciones_html where nombre_notificacion='" + nombre + "';");
            if (rs.next()) {
                plantilla = rs.getString("email_notificacion" + id_sucursal) != null ? rs.getString("email_notificacion" + id_sucursal) : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plantilla;
    }
}
