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
public class Pagos extends DataBase {

    public Pagos(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getCanalPago() {
        return this.consulta("select uno,dos from vta_canal_pago where tres=true;");
    }

    public ResultSet getBancos() {
        return this.consulta("select id_banco,banco from tbl_banco order by id_banco  asc;");
    }

    public String getIdPlanCuentaBanco(String banco) {
        String idPC = "0";
        try {
            ResultSet rs = this.consulta("SELECT * FROM vta_banco where id_banco='" + banco + "'");
            if (rs.next()) {
                idPC = rs.getString("id_plan_cuenta") != null ? rs.getString("id_plan_cuenta") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idPC;
    }

    public ResultSet getInstalaciones(String txt) {
        return this.consulta("select i.id_instalacion,i.sector ,i.direccion_instalacion ,i.ruc ,i.razon_social,i.ip,i.txt_estado_servicio ,i.cod_pichincha,p.txt_periodo ,p.total, p.id_prefactura,i.txt_convenio_pago  "
                + " from vta_instalacion as i "
                + " inner join vta_prefactura_todas  as p on p.id_instalacion =i.id_instalacion "
                + " where (lower(i.ruc)='" + txt + "') and p.fecha_emision  is null and por_emitir_factura =false and p.id_prefactura not in (select t1.id_prefactura from tbl_documento_sitio as t1  where t1.id_prefactura is not null);");
    }

    public ResultSet getInstalacionesClientePago(String id_cliente) {
        return this.consulta("select i.id_instalacion,i.sector ,i.direccion_instalacion ,i.ruc ,i.razon_social,i.ip,i.txt_estado_servicio ,i.cod_pichincha,p.txt_periodo ,p.total, p.id_prefactura  "
                + " from vta_instalacion as i "
                + " inner join vta_prefactura_todas  as p on p.id_instalacion =i.id_instalacion "
                + " where i.id_cliente='" + id_cliente + "' and p.fecha_emision  is null and por_emitir_factura =false and p.id_prefactura not in (select t1.id_prefactura from tbl_documento_sitio as t1 where t1.id_prefactura is not null);");
    }

    public ResultSet getInstalacionesClienteReclamo(String id_cliente) {
        return this.consulta("select i.* from vta_instalacion as i where i.id_cliente='" + id_cliente + "';");
    }

    public boolean estaDuplicado(String id_instalacion, String canal_pago, String id_banco, String id_prefactura, String id) {
        if (id_banco.compareTo("") == 0) {
            id_banco = " and id_banco is null ";
        } else {
            id_banco = " and id_banco='" + id_banco + "' ";
        }
        ResultSet res = this.consulta("SELECT * FROM tbl_documento_sitio where (id_instalacion='" + id_instalacion + "' and id_canal_pago='" + canal_pago + "' and id_prefactura='" + id_prefactura + "'  " + id_banco + ") and id_documento_sitio<>" + id);
        if (this.getFilas(res) > 0) {
            return true;
        }
        try {
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String setPagoInstalacionPrefactura(String id_instalacion, String canal_pago, String numero_documento, String id_prefactura) {
        return this.insert("insert into tbl_documento_sitio (id_instalacion,id_canal_pago,numero_documento,id_prefactura,proviente_pago) values('" + id_instalacion + "','" + canal_pago + "','" + numero_documento + "','" + id_prefactura + "');");
    }

    public String setPagoInstalacionVenta(String id_instalacion, String canal_pago, String numero_documento, String id_factura_venta) {
        return this.insert("insert into tbl_documento_sitio (id_instalacion,id_canal_pago,numero_documento,id_factura_venta) values('" + id_instalacion + "','" + canal_pago + "','" + numero_documento + "','" + id_factura_venta + "');");
    }

    public String setPagoInstalacionPrefactura(String id_instalacion, String canal_pago, String numero_documento, String id_prefactura, String id_banco, String id_banco_cuenta) {
        return this.insert("insert into tbl_documento_sitio (id_instalacion,id_canal_pago,numero_documento,id_prefactura,id_banco,id_banco_cuenta) values('" + id_instalacion + "','" + canal_pago + "','" + numero_documento + "','" + id_prefactura + "','" + id_banco + "','" + id_banco_cuenta + "');");
    }

    public String setPagoInstalacionVenta(String id_instalacion, String canal_pago, String numero_documento, String id_factura_venta, String id_banco, String id_banco_cuenta) {
        return this.insert("insert into tbl_documento_sitio (id_instalacion,id_canal_pago,numero_documento,id_factura_venta,id_banco,id_banco_cuenta) values('" + id_instalacion + "','" + canal_pago + "','" + numero_documento + "','" + id_factura_venta + "','" + id_banco + "','" + id_banco_cuenta + "');");
    }

    public int esCliente(String txt) {
        int ok = 0;
        ResultSet rs = this.consulta("select count(*)as conteo from vta_instalacion vi where vi.ruc='" + txt + "' and vi.estado_servicio not in ('t');");
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

    public ResultSet getPagoWeb(String id) {
        return this.consulta("select * from vta_documento_sitio where id_documento_sitio='" + id + "';");
    }

    public String getPrefacturaSucursal(String id_prefactura) {
        String id_sucursal = "";
        ResultSet rs = this.consulta("select id_sucursal from vta_prefactura  where id_prefactura ='" + id_prefactura + "';");
        try {
            if (rs.next()) {
                id_sucursal = (rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_sucursal;
    }

    public String[] getPrefacturaSucursalDatos(String id_prefactura) {
        String id_sucursal[] = {"1", ""};
        ResultSet rs = this.consulta("select id_sucursal,razon_social from vta_prefactura  where id_prefactura ='" + id_prefactura + "';");
        try {
            if (rs.next()) {
                id_sucursal[0] = (rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "1");
                id_sucursal[1] = (rs.getString("razon_social") != null ? rs.getString("razon_social") : "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_sucursal;
    }
}
