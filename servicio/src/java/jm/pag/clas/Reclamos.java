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
 * @author ADDISOFT <addisoft.ec>
 */
public class Reclamos extends DataBase {

    public Reclamos(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getEncuestaArcotel(String id) {
        return this.consulta("select id_encuesta_arcotel,pregunta_arcotel from tbl_encuesta_arcotel where codigo='" + id + "';");
    }

    public ResultSet getInstalacion(String id) {
        return this.consulta("select * from vta_instalacion where id_instalacion='" + id + "';");
    }

    public ResultSet getSoportePendiente(String id) {
        return this.consulta("select * from tbl_soporte where id_instalacion='" + id + "' and estado='r';");
    }

    public String getNumeroSoporte(String id_sucursal) {
        String numero_soporte = "";
        try {
            ResultSet rs = this.consulta("select max(num_soporte)+1 as numero_soporte from tbl_soporte where id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                numero_soporte = rs.getString("numero_soporte") != null ? rs.getString("numero_soporte") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numero_soporte;
    }

    public String setSoporte(String id_instalacion, String id_sucursal, String razon_social, String contacto, String descripcion, String tipo, String codigo) {
        String numero_soporte = this.getNumeroSoporte(id_sucursal);
        return this.insert("insert into tbl_soporte (id_instalacion, id_sucursal, num_soporte, quien_llama,telefono_llama, "
                + " alias_contesta,problema,diagnostico,fecha_llamada,hora_llamada,tipo_soporte,id_encuesta_arcotel) "
                + " values('" + id_instalacion + "','" + id_sucursal + "','" + numero_soporte + "','" + razon_social + "','" + contacto + "', "
                + " 'administrador','(WEB): " + descripcion + "','',now(),now(),'" + tipo + "'," + codigo + ");");
    }

    public ResultSet getSoporte(String id) {
        return this.consulta("select * from vta_soporte where id_soporte='" + id + "';");
    }

    public ResultSet getOrdenTrabajo(String id_orden_trabajo) {
        return this.consulta("select * from vta_orden_trabajo where id_orden_trabajo='" + id_orden_trabajo + "';");
    }

    public String TieneOrdenSoporte(String id) {
        String id_orden_trabajo = "";
        try {
            ResultSet rs = this.consulta("select id_orden_trabajo from tbl_orden_trabajo where id_soporte='" + id + "';");
            if (rs.next()) {
                id_orden_trabajo = rs.getString("id_orden_trabajo") != null ? rs.getString("id_orden_trabajo") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_orden_trabajo;
    }
}
