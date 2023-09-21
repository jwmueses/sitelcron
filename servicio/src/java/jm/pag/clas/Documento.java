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
public class Documento extends DataBase {

    public Documento(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getDocumento(String tipo) {
        return this.consulta("SELECT documento FROM tbl_documento where tipo='" + tipo + "';");
    }

    public String getDocumentoTexto(String tipo) {
        String documento = "";
        try {
            ResultSet res = this.getDocumento(tipo);
            if (res.next()) {
                documento = res.getString("documento") != null ? res.getString("documento") : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documento;
    }

}
