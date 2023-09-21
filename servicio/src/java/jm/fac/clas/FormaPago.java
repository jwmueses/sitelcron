/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.fac.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author wilso
 */
public class FormaPago extends DataBase {

    public FormaPago(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getFormasPago(String codigos) {
        return this.consulta("select codigo_interno, id_forma_pago, descripcion from tbl_forma_pago where (id_forma_pago in (" + codigos + ") or codigo_interno like 's%') and upper(descripcion)<>'RED FACILITO' and habilitada=true order by id_forma_pago");
    }

    public ResultSet getFormasPagoSRI(String codigos) {
        return this.consulta("select id_forma_pago, id_forma_pago, descripcion from tbl_forma_pago where id_forma_pago in (" + codigos + ") and habilitada=true order by id_forma_pago");
    }

    public String getCodigoFormaPago(String idFormaPago) {
        String codigo = "01";
        try {
            ResultSet rs = this.consulta("select codigo from tbl_forma_pago where id_forma_pago=" + idFormaPago);
            if (rs.next()) {
                codigo = rs.getString("codigo") != null ? rs.getString("codigo") : "01";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codigo;
    }

    public String getCodigoInternoFormaPago(String idFormaPago) {
        String codigo = "01";
        try {
            ResultSet rs = this.consulta("select codigo_interno from tbl_forma_pago where id_forma_pago=" + idFormaPago);
            if (rs.next()) {
                codigo = rs.getString("codigo_interno") != null ? rs.getString("codigo_interno") : "d";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codigo;
    }

}
