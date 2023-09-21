/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cont.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author wilso
 */
public class Sector extends DataBase {

    public Sector(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getSectores(int id) {
        return this.consulta("SELECT id_sector,sector from vta_sector where id_sucursal=" + id + " order by sector;");
    }

    public ResultSet getSectores(String id) {
        return this.consulta("SELECT id_sector,sector from vta_sector where id_sucursal=" + id + " order by sector;");
    }
}
