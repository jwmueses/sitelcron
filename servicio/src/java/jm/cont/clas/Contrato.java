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
public class Contrato extends DataBase {

    public Contrato(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getDocumento(String tipo) {
        return this.consulta("SELECT documento FROM tbl_documento where tipo='" + tipo + "';");
    }

    public String getDocumentoTextoId(String tipo) {
        String documento = "";
        ResultSet rs = this.consulta("SELECT documento FROM tbl_documento where id_documento='" + tipo + "';");
        try {
            if (rs.next()) {
                documento = (rs.getString("documento") != null ? rs.getString("documento") : "");
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return documento;

    }

    public String getDocumentoTextoTipo(String tipo) {
        String documento = "";
        ResultSet rs = this.consulta("SELECT documento FROM tbl_documento where tipo='" + tipo + "';");
        try {
            if (rs.next()) {
                documento = (rs.getString("documento") != null ? rs.getString("documento") : "");
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return documento;

    }

    public String getNumeroContrato(String id_sucursal) {
        String num_contrato = "-1";
        ResultSet rs = this.consulta("select case when max(C.num_contrato)>0 then max(C.num_contrato)+1 else 1 end as numero_contrato "
                + " from tbl_contrato as C where C.id_sucursal ='" + id_sucursal + "';");

        try {
            if (rs.next()) {
                num_contrato = (rs.getString("numero_contrato") != null ? rs.getString("numero_contrato") : "-1");
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return num_contrato;
    }

    public ResultSet getContrato(String id_contrato) {
        return this.consulta("SELECT * FROM vta_contrato where id_contrato=" + id_contrato + ";");
    }

    public String insertar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino,
            String ruc_representante, String representante, String contrato, String autorizacion, String id_instalacion) {
        id_instalacion = (id_instalacion.trim().compareTo("") == 0 ? "NULL" : "'" + id_instalacion + "'");
        return this.insert("INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante, contrato, autorizacion,id_instalacion) "
                + "VALUES(" + num_contrato + ", " + id_cliente + ", " + id_sucursal + ", '" + fecha_contrato + "', '" + fecha_termino + "', '" + ruc_representante + "', '" + representante + "', '" + contrato + "', '" + autorizacion + "', " + id_instalacion + ");");
    }

    public ResultSet getContratoInstalacion(String id_instalacion) {
        return this.consulta("SELECT * FROM vta_contrato where id_instalacion=" + id_instalacion + ";");
    }
}
