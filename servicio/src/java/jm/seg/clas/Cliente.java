/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author wilso
 */
public class Cliente extends DataBase {

    public Cliente(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getCliente(String id_cliente) {
        return this.consulta("select * from vta_cliente where id_cliente ='" + id_cliente + "';");
    }

    public ResultSet getClienteRuc(String ruc) {
        return this.consulta("select * from vta_cliente where ruc ='" + ruc + "';");
    }

    public int ExisteClienteRuc(String ruc) {
        int ok = 0;
        ResultSet rs = this.consulta("select count(*)as conteo from vta_cliente where ruc ='" + ruc + "';");
        try {
            if (rs.next()) {
                ok = rs.getString("conteo") != null ? rs.getInt("conteo") : 0;
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return ok;
    }

    public int ExisteClienteId(String id) {
        int ok = 0;
        ResultSet rs = this.consulta("select count(*)as conteo from vta_cliente where id_cliente ='" + id + "';");
        try {
            if (rs.next()) {
                ok = rs.getString("conteo") != null ? rs.getInt("conteo") : 0;
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return ok;
    }

    public String setCliente(String establecimiento, String tipo_documento, String ruc, String razon_social, String telefono, String movil_claro, String direccion, String email) {
        return this.insert("insert into tbl_cliente(establecimiento,tipo_documento ,ruc,razon_social ,telefono  ,movil_claro ,direccion,email )values('" + establecimiento + "','" + tipo_documento + "','" + ruc + "','" + razon_social + "','" + telefono + "','" + movil_claro + "','" + direccion + "','" + email + "');");
    }

    public boolean uptCliente(String id, String telefono, String movil_claro) {
        return this.ejecutar("update tbl_cliente set telefono='" + telefono + "',movil_claro='" + movil_claro + "' where id_cliente='" + id + "';");
    }
}
