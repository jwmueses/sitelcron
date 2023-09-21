/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.fac.clas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import jm.web.DataBase;
import jm.web.Matriz;

/**
 *
 * @author wilso
 */
public class FacturaVenta extends DataBase {

    public FacturaVenta(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public String[][] getPuntosEmisionVirtuales() {
        ResultSet rs = this.consulta("SELECT P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, "
                + "case when max(num_factura)>0 then max(num_factura)+1 else 1 end, direccion_establecimiento "
                + "from tbl_punto_emision as P inner join tbl_factura_venta as F on F.serie_factura=P.fac_num_serie "
                + "where caja_virtual=true "
                + "group by P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, direccion_establecimiento "
                + "order by id_sucursal");
        return Matriz.ResultSetAMatriz(rs);
    }

    public boolean InstalacionDeudaContrato(String id_factura_venta) {
        boolean ok = false;
        try {
            ResultSet rs = this.consulta("select id_instalacion from tbl_factura_venta where id_factura_venta=" + id_factura_venta + " and deuda>0 and anulado=false");
            if (rs != null) {
                if (this.getFilas(rs) > 0) {
                    ok = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public String InstalacionDeudaEstado(String id_factura_venta) {
        String estado = "";
        try {
            ResultSet rs = this.consulta("select txtestado_tramite from vta_documento_sitio as ds where id_prefactura = '" + id_factura_venta + "' and ds.anulado =false and ds.eliminado =false and proviene_pago='f';");
            if (rs != null) {
                if (rs.next()) {
                    estado = (rs.getString(1) != null ? rs.getString(1) : "");
                }
            }
        } catch (Exception e) {
            estado = "";
            e.printStackTrace();
        }
        return estado;
    }

    public ResultSet getFacturasCliente(String id_cliente) {
        return this.consulta("select * from vta_factura_venta where id_cliente ='" + id_cliente + "';");
    }

    public ResultSet getFacturasCliente(String id_cliente, String fi, String ff, String numero) {
        return this.consulta("select * from vta_factura_venta where id_cliente ='" + id_cliente + "' and fecha_emision between '" + fi + "' and '" + ff + "' and num_factura::text like '%" + numero + "%';");
    }

    public ResultSet getNotasCreditoVentaCliente(String id_cliente, String fi, String ff, String numero) {
        return this.consulta("select * from vta_nota_credito_venta where id_cliente ='" + id_cliente + "' and fecha_emision between '" + fi + "' and '" + ff + "' and num_nota ::text like '%" + numero + "%';");
    }

    public String insertar(String id_instalacion, String id_sucursal, String id_punto_emision, String id_cliente, String usuario, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos,
            String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie,
            String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado,
            String estadoDocumento, String claveAcceso, String autorizacionXml, String respuestaAutoriz, String id_documento_sitio) {
        String ins_fac = "-1;-1";
        Connection con = this.getConexion();
        try {
            String id_factura_venta = "-1";
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            boolean ok = true;
            double total_final1 = Double.parseDouble(total);
            String es_instalacion = "TRUE";
            String ip = "NULL";
            if (es_instalacion.compareTo("TRUE") == 0 && total_final1 > 0) {
                ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
                String sql = "select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + usuario + "', '" + serie_factura
                        + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                        + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                        + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
                        + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
                        + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado + "');";
                ResultSet rs = this.consulta(sql);
                if (rs.next()) {
                    id_factura_venta = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
                if (id_factura_venta.compareTo("-1") != 0) {
                    st.executeUpdate("UPDATE tbl_instalacion SET id_factura_venta=" + id_factura_venta + " WHERE id_instalacion=" + id_instalacion + ";");
                    st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion=" + id_instalacion + ", ip=" + ip + " WHERE id_factura_venta=" + id_factura_venta + ";");
                    st.executeUpdate("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', clave_acceso='" + claveAcceso + "', documento_xml='" + autorizacionXml + "', mensaje='" + respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ") + "' where id_factura_venta=" + id_factura_venta);
                    st.executeUpdate("update tbl_documento_sitio set id_factura_venta='" + id_factura_venta + "' where id_documento_sitio='" + id_documento_sitio + "';");

                }/*error en la creacion de la factura*/ else {
                    ok = false;
                }

            }
            if (ok) {
                ins_fac = id_instalacion + ";" + id_factura_venta;
                con.commit();
            } else {
                con.rollback();
            }
        } catch (Exception e) {
            System.out.println("Portal: " + e.getMessage());
            try {
                con.rollback();
            } catch (Exception ex) {
                System.out.println("Portal: " + ex.getMessage());
            }

        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Portal: " + e.getMessage());
            }
        }
        return ins_fac;
    }
}
