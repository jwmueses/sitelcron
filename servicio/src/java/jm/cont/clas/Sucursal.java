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
public class Sucursal extends DataBase {

    public Sucursal(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getSucursales() {
        return this.consulta("SELECT id_sucursal,sucursal from vta_sucursal where estado=true");
    }

    public ResultSet getDirecciones() {
        return this.consulta("SELECT id_sucursal,ubicacion from vta_sucursal");
    }

    public String getNombre(String id) {
        String ubicacion = "Empresa";
        if (id.compareTo("-0") != 0) {
            try {
                ResultSet r = this.consulta("SELECT * FROM tbl_sucursal where id_sucursal=" + id);
                if (r.next()) {
                    ubicacion = (r.getString("sucursal") != null) ? r.getString("sucursal") : "Empresa";
                    r.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ubicacion;
    }

    public String getCiudad(int id) {
        String ciudad = "Ibarra";
        try {
            ResultSet r = this.consulta("SELECT * FROM tbl_sucursal where id_sucursal=" + id);
            if (r.next()) {
                ciudad = (r.getString("ciudad") != null) ? r.getString("ciudad") : "Empresa";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciudad;
    }

    public String getCiudadesJSON() {
        ResultSet rs = this.consulta("select id_sucursal, ciudad from tbl_sucursal order by id_sucursal;");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public String getDireccion(String id) {
        String ubicacion = "";
        if (id.compareTo("-0") != 0) {
            try {
                ResultSet r = this.consulta("SELECT ubicacion FROM tbl_sucursal where id_sucursal=" + id);
                if (r.next()) {
                    ubicacion = (r.getString("ubicacion") != null) ? r.getString("ubicacion") : "";
                    r.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ubicacion;
    }

    public String getSucursalCampo(String id, String campo) {
        String valor = "";
        try {
            ResultSet r = this.consulta("SELECT " + campo + " FROM tbl_sucursal where id_sucursal=" + id);
            if (r.next()) {
                valor = (r.getString(campo) != null) ? r.getString(campo) : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valor;
    }
}
