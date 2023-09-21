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
public class Configuracion extends DataBase {

    public Configuracion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public String getValor(String param) {
        String valor = "";
        try {
            ResultSet r = this.consulta("SELECT valor FROM tbl_configuracion where parametro='" + param + "';");
            if (r.next()) {
                valor = (r.getString("valor") != null) ? r.getString("valor") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valor;
    }

}
