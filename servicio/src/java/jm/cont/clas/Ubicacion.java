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
public class Ubicacion extends DataBase {

    public Ubicacion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getUbicacion(String padre) {
        return this.consulta("SELECT * FROM tbl_ubicacion WHERE id_ubicacion=" + padre + ";");
    }

    public ResultSet getUbicaciones() {
        return this.consulta("SELECT id_ubicacion,ubicacion FROM tbl_ubicacion WHERE id_padre > 0 order by ubicacion;");
    }

    public ResultSet getUbicaciones(String padre) {
        return this.consulta("SELECT id_ubicacion,ubicacion FROM tbl_ubicacion WHERE id_padre=" + padre + " order by ubicacion;");
    }

    public ResultSet getUbicacionSucursal(String id_sucursal) {
        return this.consulta("select u.id_ubicacion,u.ubicacion from tbl_ubicacion_sucursal as us "
                + " inner join tbl_ubicacion as u on u.id_ubicacion =us.id_ubicacion "
                + " where us.id_sucursal ='" + id_sucursal + "' and us.anulado =false and us.eliminado =false;");
    }

    public String getNombre(String id_ubicacion) {
        String nombre = "";
        try {
            ResultSet res = this.consulta("select ubicacion from tbl_ubicacion where id_ubicacion=" + id_ubicacion);
            if (res.next()) {
                nombre = (res.getString(1) != null) ? res.getString(1) : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nombre;
    }
}
