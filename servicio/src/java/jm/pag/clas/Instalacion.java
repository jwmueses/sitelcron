/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag.clas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import jm.web.Cadena;
import jm.web.DataBase;

/**
 *
 * @author wilso
 */
public class Instalacion extends DataBase {

    public Instalacion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getInstalacion(String id) {
        return this.consulta("SELECT * FROM vta_instalacion where id_instalacion=" + id);
    }

    public ResultSet getInstalacionCliente(String id_cliente) {
        return this.consulta("SELECT * FROM vta_instalacion where id_cliente=" + id_cliente);
    }

    public ResultSet getSuspencionInstalacion(String id) {
        return this.consulta("select i.*,si.* from vta_instalacion as i "
                + " inner join tbl_instalacion_suspension as si on si.id_instalacion =i.id_instalacion "
                + " where si.id_instalacion_suspension='" + id + "';");
    }

    public String setSuspension(String id_instalacion, String usuario, String tipo, String fecha_inicio, String fecha_termino, int tiempo, String obserSuspDefinitiva, String id_prefactura) {
        return this.insert("insert into tbl_instalacion_suspension(id_instalacion, usuario_solicitud, tipo, fecha_inicio, fecha_termino, tiempo, observacion_orden_trabajo,id_prefactura) "
                + "values(" + id_instalacion + ", '" + usuario + "', '" + tipo + "', '" + fecha_inicio + "', '" + fecha_termino + "', " + tiempo + ", '" + obserSuspDefinitiva + "', '" + id_prefactura + "');");

    }

    public boolean enConflictoSuspension(String id_suspension, String id_instalacion, String fecha_inicio) {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_suspension where '" + fecha_inicio + "' between fecha_inicio and fecha_termino and eliminado=false and id_instalacion=" + id_instalacion + " and id_instalacion_suspension<>" + id_suspension + ";");
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

    public boolean nocobroemergencia(String periodo) {
        ResultSet res = this.consulta("select * from tbl_prefactura_diferir  where periodo ='" + periodo + "';");
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

    public String getPrefacturaSuspencion(String periodo, String id_instalacion) {
        String id_prefactura = "-1";
        ResultSet rs = this.consulta("select id_prefactura from tbl_prefactura  where periodo ='" + periodo + "' and fecha_emision is null and id_factura_venta is null  and por_emitir_factura =false and id_instalacion ='" + id_instalacion + "';");
        try {
            if (rs.next()) {
                id_prefactura = (rs.getString("id_prefactura") != null ? rs.getString("id_prefactura") : "-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_prefactura;
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

    public int getSuspencionAnio(String id_instalacion) {
        int tiempos = 0;
        ResultSet rs = this.consulta("select coalesce(sum(tiempo+1),0)as tiempos  from tbl_instalacion_suspension as si   "
                + " inner join vta_instalacion_parametros as ip on ip.id_instalacion =si.id_instalacion "
                + " where si.id_instalacion ='" + id_instalacion + "' and eliminado=false and (si.fecha_solicitud between ip.fecha_inicio and ip.fecha_fin);");
        try {
            if (rs.next()) {
                tiempos = (rs.getString("tiempos") != null ? rs.getInt("tiempos") : 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tiempos;
    }

    public String getIpLibre(String id_sucursal) {
        String ip = "";
        try {
            ResultSet rs = this.consulta("select x.ips from tbl_ips_libres x where x.id_sucursal ='" + id_sucursal + "' "//and x.ips::varchar like '192.168.%' "
                    + " order by id_ips_libres limit 1;");
            if (rs.next()) {
                ip = rs.getString("ips") != null ? rs.getString("ips") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    public boolean ipDisponible(String id, String id_sucursal, String ip) {
        ResultSet res = this.consulta("select distinct ip from tbl_instalacion where upper(ip::varchar) = '" + ip + "' "
                + "and estado_servicio in ('p', 'a','s','c','r','d','n') and anulado=false "
                + "and id_sucursal=" + id_sucursal + " and id_instalacion<>" + id);
        if (this.getFilas(res) > 0) {
            return false;
        }
        try {
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getNumInstalacion(String id_sucursal) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_instalacion)is not null then max(num_instalacion)+1 else 1 end from tbl_instalacion where id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getString(1) : "1";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("Portal: " + e.getMessage());
        }
        return r;
    }

    public String getNumOrdenServicio(String id_sucursal) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_orden_servicio)is not null then max(num_orden_servicio)+1 else 1 end from tbl_instalacion where id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getString(1) : "1";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("Portal: " + e.getMessage());
        }
        return r;
    }

    public String getNumOrden(String id_sucursal) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_orden)is not null then max(num_orden)+1 else 1 end from tbl_orden_trabajo where id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getString(1) : "1";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("Portal: " + e.getMessage());
        }
        return r;
    }

    public String getNumSoporte(String id_sucursal) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_soporte)>0 then max(num_soporte)+1 else 1 end from tbl_soporte where id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getString(1) : "1";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("Portal: " + e.getMessage());
        }
        return r;
    }

//    public String insertar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino, String ruc_representante, String representante,
//            String convenio_pago, String id_provincia, String id_ciudad, String id_parroquia, String id_sector, String tipo_instalacion, String costo_instalacion,
//            String direccion_instalacion, String id_plan_contratado, String ruc, String id_punto_emision, String serie_factura, String num_factura, String autorizacion,
//            String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos,
//            String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total,
//            String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal,
//            String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado, String id_promocion, String estadoDocumento, String claveAcceso, String autorizacionXml, String respuestaAutoriz, String usuario) {
//        String ins_fac = "-1;-1;-1";
//        Connection con = this.getConexion();
//        try {
//            String id_contrato = "-1";
//            String id_instalacion = "-1";
//            String id_factura_venta = "-1";
//            con.setAutoCommit(false);
//            Statement st = con.createStatement();
//            boolean ok = true;
//            String sql = "INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante) "
//                    + "VALUES(" + num_contrato + ", " + id_cliente + ", " + id_sucursal + ", '" + fecha_contrato + "', '" + fecha_termino + "', '" + ruc_representante + "', '" + representante + "');";
//            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                ResultSet rs = st.getGeneratedKeys();
//                if (rs.next()) {
//                    id_contrato = rs.getString(1) != null ? rs.getString(1) : "-1";
//                    rs.close();
//                }
//                String num_instalacion = this.getNumInstalacion(id_sucursal);
//                String num_orden_servicio = this.getNumOrdenServicio(id_sucursal);
//                String num_orden = this.getNumOrden(id_sucursal);
//                String num_orden_soporte = this.getNumSoporte(id_sucursal);
//                String ip = this.getIpLibre(id_sucursal);
//                String ip_radio = "NULL";
//                String es_instalacion = "TRUE";
//                String cobrar = "TRUE";
//                String motivo_no_cobrar = "";
//                String radusername = ruc + "_" + id_sucursal + "_" + num_instalacion;
//                String radclave = Cadena.getRandomClave(10);
//                String deviceclave = Cadena.getRandomClave(10);
////                String usuario = "administrador";
//                String set_convenio_tarjeta = "FALSE";
//                String tipo_cliente_instalacion = "c";
//                String factura_credito = "TRUE";
//                sql = "INSERT INTO tbl_instalacion(num_instalacion, convenio_pago, num_orden_servicio, id_sucursal, id_cliente, id_provincia, id_ciudad, id_parroquia, "
//                        + " id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, id_contrato, fecha_instalacion, ip, ip_radio, id_plan_contratado, id_plan_establecido,"
//                        + " id_plan_actual, es_instalacion, cobrar, motivo_no_cobrar, estado_instalacion, radusername, radclave, deviceclave, alias, set_convenio_tarjeta,tipo_cliente_instalacion,factura_credito,proviene_instalacion) "
//                        + " VALUES"
//                        + " (" + num_instalacion + ", '" + convenio_pago + "', " + num_orden_servicio + ", " + id_sucursal + ", " + id_cliente + ", " + id_provincia + ","
//                        + " " + id_ciudad + ", " + id_parroquia + ", " + id_sector + ", '" + tipo_instalacion + "', " + costo_instalacion + ", '" + direccion_instalacion + "',"
//                        + " " + id_contrato + ", NULL, '" + ip + "', " + ip_radio + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + es_instalacion + ","
//                        + " " + cobrar + ", '" + motivo_no_cobrar + "', 'e', '" + radusername + "', '" + radclave + "', '" + deviceclave + "', '" + usuario + "', " + set_convenio_tarjeta + ","
//                        + " '" + tipo_cliente_instalacion + "'," + factura_credito + ",'1');";
//                if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                    rs = st.getGeneratedKeys();
//                    if (rs.next()) {
//                        id_instalacion = rs.getString(1) != null ? rs.getString(1) : "-1";
//                        rs.close();
//                    }
//                    sql = "INSERT INTO tbl_soporte(id_instalacion, id_sucursal, num_soporte, quien_llama, telefono_llama, "
//                            + " alias_contesta, problema, diagnostico, fecha_llamada, hora_llamada,capacidad_efectiva,tipo_servicio,recomendacion,estado,procedente) "
//                            + " VALUES(" + id_instalacion + ", " + id_sucursal + ", " + num_orden_soporte + ", '" + razon_social + "', '" + telefono + " ', "
//                            + " 'administrador', 'INSTALACION VIA WEB', 'INSTALACION VIA WEB', now()::date, now()::time,'0','" + tipo_instalacion + "','INSTALACION VIA WEB','s','false');";
//                    if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                        boolean oki = true;
//                        String id_soporte = "-1";
//                        rs = st.getGeneratedKeys();
//                        if (rs.next()) {
//                            id_soporte = rs.getString(1) != null ? rs.getString(1) : "-1";
//                            rs.close();
//                        }
//                        st.executeUpdate("update tbl_contrato set id_instalacion='" + id_instalacion + "' where id_contrato='" + id_contrato + "';");
//                        st.executeUpdate("delete from tbl_ips_libres where ips='" + ip.trim() + "' and id_sucursal=" + id_sucursal + ";");
//                        if (id_promocion.trim().compareTo("") != 0) {
//                            st.executeUpdate("insert into tbl_instalacion_promocion(id_instalacion, id_promocion) values(" + id_instalacion + ", " + id_promocion + ");");
//                        }
//                        double total_final1 = Double.parseDouble(total);
//                        String estado_orden = "1";
//                        if (total_final1 > 0) {
//                            estado_orden = "0";
//                        }
//                        st.execute(" INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo,id_soporte) "
//                                + " VALUES(" + id_instalacion + ", " + id_sucursal + ", " + num_orden + ", '3', 'administrador', now()::date, now()::time, 'administrador', now(), now(), 'Instalacion via web', '" + estado_orden + "', 'c','" + id_soporte + "');");
//                        if (es_instalacion.compareTo("TRUE") == 0 && total_final1 > 0) {
//                            ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
//                            sql = "select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + usuario + "', '" + serie_factura
//                                    + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
//                                    + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
//                                    + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
//                                    + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
//                                    + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado + "');";
//                            rs = this.consulta(sql);
//                            if (rs.next()) {
//                                id_factura_venta = rs.getString(1) != null ? rs.getString(1) : "-1";
//                                rs.close();
//                            }
//                            if (id_factura_venta.compareTo("-1") != 0) {
//                                st.executeUpdate("UPDATE tbl_instalacion SET id_factura_venta=" + id_factura_venta + " WHERE id_instalacion=" + id_instalacion + ";");
//                                st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion=" + id_instalacion + ", ip='" + ip + "' WHERE id_factura_venta=" + id_factura_venta + ";");
//                                st.executeUpdate("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', clave_acceso='" + claveAcceso + "', documento_xml='" + autorizacionXml + "', mensaje='" + respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ") + "' where id_factura_venta=" + id_factura_venta);
//                            }/*error en la creacion de la factura*/ else {
//                                ok = false;
//                            }
//
//                        }
//                        if (oki) {
//                            sql = "insert into tbl_bodega(id_sucursal, bodega, id_responsable, ubicacion, id_instalacion, es_responsable_cliente) values("
//                                    + id_sucursal + ", 'INSTALACION No. " + id_sucursal + "-" + num_instalacion + " " + razon_social + "', " + id_cliente + ", '" + direccion_instalacion
//                                    + "', " + id_instalacion + ", true);";
//                            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                                String id_bodega = "-1";
//                                rs = st.getGeneratedKeys();
//                                if (rs.next()) {
//                                    id_bodega = rs.getString(1) != null ? rs.getString(1) : "-1";
//                                    rs.close();
//                                }
//                                st.executeUpdate("insert into tbl_estanteria(id_bodega, estanteria) values (" + id_bodega + ", 'EST-" + id_instalacion + "');");
//                            }/*error en la creacion de la bodega*/ else {
//                                ok = false;
//                            }
//                        }/*error en la creacion de la factura*/ else {
//                            ok = false;
//                        }
//                    } /*error en la creacion el soporte*/ else {
//                        ok = false;
//                    }
//
//                }/*error en la creacion de la instalacion*/ else {
//                    ok = false;
//                }
//            } /*error en creacion del contrato*/ else {
//                ok = false;
//            }
//            if (ok) {
//                ins_fac = id_contrato + ";" + id_instalacion + ";" + id_factura_venta;
//                con.commit();
//            } else {
//                con.rollback();
//            }
//        } catch (Exception e) {
//            System.out.println("Portal: " + e.getMessage());
//            try {
//                con.rollback();
//            } catch (Exception ex) {
//                System.out.println("Portal: " + ex.getMessage());
//            }
//
//        } finally {
//            try {
//                con.setAutoCommit(true);
//            } catch (Exception e) {
//                System.out.println("Portal: " + e.getMessage());
//            }
//        }
//        return ins_fac;
//    }
//    public String insertar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino, String ruc_representante, String representante,
//            String convenio_pago, String id_provincia, String id_ciudad, String id_parroquia, String id_sector, String tipo_instalacion, String costo_instalacion,
//            String direccion_instalacion, String id_plan_contratado, String ruc, String id_punto_emision, String serie_factura, String num_factura, String autorizacion,
//            String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos,
//            String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total,
//            String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal,
//            String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado, String id_promocion, String estadoDocumento, String claveAcceso, String autorizacionXml, String respuestaAutoriz, String usuario) {
//        String ins_fac = "-1;-1;-1";
//        Connection con = this.getConexion();
//        try {
//            String id_contrato = "-1";
//            String id_instalacion = "-1";
//            String id_factura_venta = "-1";
//            con.setAutoCommit(false);
//            Statement st = con.createStatement();
//            boolean ok = true;
//            String sql = "INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante) "
//                    + "VALUES(" + num_contrato + ", " + id_cliente + ", " + id_sucursal + ", '" + fecha_contrato + "', '" + fecha_termino + "', '" + ruc_representante + "', '" + representante + "');";
//            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                ResultSet rs = st.getGeneratedKeys();
//                if (rs.next()) {
//                    id_contrato = rs.getString(1) != null ? rs.getString(1) : "-1";
//                    rs.close();
//                }
//                String num_instalacion = this.getNumInstalacion(id_sucursal);
//                String num_orden_servicio = this.getNumOrdenServicio(id_sucursal);
//                String num_orden = this.getNumOrden(id_sucursal);
//                String num_orden_soporte = this.getNumSoporte(id_sucursal);
//                //String ip = this.getIpLibre(id_sucursal);
//                String ip = "NULL";
//                String ip_radio = "NULL";
//                String es_instalacion = "TRUE";
//                String cobrar = "TRUE";
//                String motivo_no_cobrar = "";
//                String radusername = ruc + "_" + id_sucursal + "_" + num_instalacion;
//                String radclave = Cadena.getRandomClave(10);
//                String deviceclave = Cadena.getRandomClave(10);
////                String usuario = "administrador";
//                String set_convenio_tarjeta = "FALSE";
//                String tipo_cliente_instalacion = "c";
//                String factura_credito = "TRUE";
//                sql = "INSERT INTO tbl_instalacion(num_instalacion, convenio_pago, num_orden_servicio, id_sucursal, id_cliente, id_provincia, id_ciudad, id_parroquia, "
//                        + " id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, id_contrato, fecha_instalacion, ip, ip_radio, id_plan_contratado, id_plan_establecido,"
//                        + " id_plan_actual, es_instalacion, cobrar, motivo_no_cobrar, estado_instalacion, radusername, radclave, deviceclave, alias, set_convenio_tarjeta,tipo_cliente_instalacion,factura_credito,proviene_instalacion) "
//                        + " VALUES"
//                        + " (" + num_instalacion + ", '" + convenio_pago + "', " + num_orden_servicio + ", " + id_sucursal + ", " + id_cliente + ", " + id_provincia + ","
//                        + " " + id_ciudad + ", " + id_parroquia + ", " + id_sector + ", '" + tipo_instalacion + "', " + costo_instalacion + ", '" + direccion_instalacion + "',"
//                        + " " + id_contrato + ", NULL, " + ip + ", " + ip_radio + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + es_instalacion + ","
//                        + " " + cobrar + ", '" + motivo_no_cobrar + "', 'e', '" + radusername + "', '" + radclave + "', '" + deviceclave + "', '" + usuario + "', " + set_convenio_tarjeta + ","
//                        + " '" + tipo_cliente_instalacion + "'," + factura_credito + ",'1');";
//                if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                    rs = st.getGeneratedKeys();
//                    if (rs.next()) {
//                        id_instalacion = rs.getString(1) != null ? rs.getString(1) : "-1";
//                        rs.close();
//                    }
//                    sql = "INSERT INTO tbl_soporte(id_instalacion, id_sucursal, num_soporte, quien_llama, telefono_llama, "
//                            + " alias_contesta, problema, diagnostico, fecha_llamada, hora_llamada,capacidad_efectiva,tipo_servicio,recomendacion,estado,procedente) "
//                            + " VALUES(" + id_instalacion + ", " + id_sucursal + ", " + num_orden_soporte + ", '" + razon_social + "', '" + telefono + " ', "
//                            + " 'administrador', 'INSTALACION VIA WEB', 'INSTALACION VIA WEB', now()::date, now()::time,'0','" + tipo_instalacion + "','INSTALACION VIA WEB','s','false');";
//                    if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                        boolean oki = true;
//                        String id_soporte = "-1";
//                        rs = st.getGeneratedKeys();
//                        if (rs.next()) {
//                            id_soporte = rs.getString(1) != null ? rs.getString(1) : "-1";
//                            rs.close();
//                        }
//                        st.executeUpdate("update tbl_contrato set id_instalacion='" + id_instalacion + "' where id_contrato='" + id_contrato + "';");
//                        //st.executeUpdate("delete from tbl_ips_libres where ips='" + ip.trim() + "' and id_sucursal=" + id_sucursal + ";");
//                        if (id_promocion.trim().compareTo("") != 0) {
//                            st.executeUpdate("insert into tbl_instalacion_promocion(id_instalacion, id_promocion) values(" + id_instalacion + ", " + id_promocion + ");");
//                        }
//                        double total_final1 = Double.parseDouble(total);
//                        String estado_orden = "1";
//                        if (total_final1 > 0) {
//                            estado_orden = "0";
//                        }
//                        st.execute(" INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo,id_soporte) "
//                                + " VALUES(" + id_instalacion + ", " + id_sucursal + ", " + num_orden + ", '3', 'administrador', now()::date, now()::time, 'administrador', now(), now(), 'Instalacion via web', '" + estado_orden + "', 'c','" + id_soporte + "');");
//                        if (es_instalacion.compareTo("TRUE") == 0 && total_final1 > 0) {
//                            ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
//                            sql = "select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + usuario + "', '" + serie_factura
//                                    + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
//                                    + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
//                                    + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
//                                    + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
//                                    + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado + "');";
//                            rs = this.consulta(sql);
//                            if (rs.next()) {
//                                id_factura_venta = rs.getString(1) != null ? rs.getString(1) : "-1";
//                                rs.close();
//                            }
//                            if (id_factura_venta.compareTo("-1") != 0) {
//                                st.executeUpdate("UPDATE tbl_instalacion SET id_factura_venta=" + id_factura_venta + " WHERE id_instalacion=" + id_instalacion + ";");
//                                st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion=" + id_instalacion + ", ip=" + ip + " WHERE id_factura_venta=" + id_factura_venta + ";");
//                                st.executeUpdate("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', clave_acceso='" + claveAcceso + "', documento_xml='" + autorizacionXml + "', mensaje='" + respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ") + "' where id_factura_venta=" + id_factura_venta);
//                            }/*error en la creacion de la factura*/ else {
//                                ok = false;
//                            }
//
//                        }
//                        if (oki) {
//                            sql = "insert into tbl_bodega(id_sucursal, bodega, id_responsable, ubicacion, id_instalacion, es_responsable_cliente) values("
//                                    + id_sucursal + ", 'INSTALACION No. " + id_sucursal + "-" + num_instalacion + " " + razon_social + "', " + id_cliente + ", '" + direccion_instalacion
//                                    + "', " + id_instalacion + ", true);";
//                            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
//                                String id_bodega = "-1";
//                                rs = st.getGeneratedKeys();
//                                if (rs.next()) {
//                                    id_bodega = rs.getString(1) != null ? rs.getString(1) : "-1";
//                                    rs.close();
//                                }
//                                st.executeUpdate("insert into tbl_estanteria(id_bodega, estanteria) values (" + id_bodega + ", 'EST-" + id_instalacion + "');");
//                            }/*error en la creacion de la bodega*/ else {
//                                ok = false;
//                            }
//                        }/*error en la creacion de la factura*/ else {
//                            ok = false;
//                        }
//                    } /*error en la creacion el soporte*/ else {
//                        ok = false;
//                    }
//
//                }/*error en la creacion de la instalacion*/ else {
//                    ok = false;
//                }
//            } /*error en creacion del contrato*/ else {
//                ok = false;
//            }
//            if (ok) {
//                ins_fac = id_contrato + ";" + id_instalacion + ";" + id_factura_venta;
//                con.commit();
//            } else {
//                con.rollback();
//            }
//        } catch (Exception e) {
//            System.out.println("Portal: " + e.getMessage());
//            try {
//                con.rollback();
//            } catch (Exception ex) {
//                System.out.println("Portal: " + ex.getMessage());
//            }
//
//        } finally {
//            try {
//                con.setAutoCommit(true);
//            } catch (Exception e) {
//                System.out.println("Portal: " + e.getMessage());
//            }
//        }
//        return ins_fac;
//    }
    public String insertar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino, String ruc_representante, String representante,
            String convenio_pago, String id_provincia, String id_ciudad, String id_parroquia, String id_sector, String tipo_instalacion, String costo_instalacion,
            String direccion_instalacion, String id_plan_contratado, String ruc, String id_punto_emision, String serie_factura, String num_factura, String autorizacion,
            String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos,
            String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total,
            String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal,
            String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado, String id_promocion, String estadoDocumento, String claveAcceso, String autorizacionXml, String respuestaAutoriz, String usuario,
            String costo_instalacion_facturado, String tiempo_permanencia_contrato) {
        String ins_fac = "-1;-1;-1";
        Connection con = this.getConexion();
        try {
            String id_contrato = "-1";
            String id_instalacion = "-1";
            String id_factura_venta = "-1";
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            boolean ok = true;
            String sql = "INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante) "
                    + "VALUES(" + num_contrato + ", " + id_cliente + ", " + id_sucursal + ", '" + fecha_contrato + "', '" + fecha_termino + "', '" + ruc_representante + "', '" + representante + "');";
            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    id_contrato = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
                String num_instalacion = this.getNumInstalacion(id_sucursal);
                String num_orden_servicio = this.getNumOrdenServicio(id_sucursal);
                String num_orden = this.getNumOrden(id_sucursal);
                String num_orden_soporte = this.getNumSoporte(id_sucursal);
                //String ip = this.getIpLibre(id_sucursal);
                String ip = "NULL";
                String ip_radio = "NULL";
                String es_instalacion = "TRUE";
                String cobrar = "TRUE";
                String motivo_no_cobrar = "";
                String radusername = ruc + "_" + id_sucursal + "_" + num_instalacion;
                String radclave = Cadena.getRandomClave(10);
                String deviceclave = Cadena.getRandomClave(10);
//                String usuario = "administrador";
                String set_convenio_tarjeta = "FALSE";
                String tipo_cliente_instalacion = "c";
                String factura_credito = "TRUE";
                sql = "INSERT INTO tbl_instalacion(num_instalacion, convenio_pago, num_orden_servicio, id_sucursal, id_cliente, id_provincia, id_ciudad, id_parroquia, "
                        + " id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, id_contrato, fecha_instalacion, ip, ip_radio, id_plan_contratado, id_plan_establecido,"
                        + " id_plan_actual, es_instalacion, cobrar, motivo_no_cobrar, estado_instalacion, radusername, radclave, deviceclave, alias, set_convenio_tarjeta,tipo_cliente_instalacion,factura_credito,proviene_instalacion,costo_instalacion_facturado,tiempo_permanencia_contrato) "
                        + " VALUES"
                        + " (" + num_instalacion + ", '" + convenio_pago + "', " + num_orden_servicio + ", " + id_sucursal + ", " + id_cliente + ", " + id_provincia + ","
                        + " " + id_ciudad + ", " + id_parroquia + ", " + id_sector + ", '" + tipo_instalacion + "', " + costo_instalacion + ", '" + direccion_instalacion + "',"
                        + " " + id_contrato + ", NULL, " + ip + ", " + ip_radio + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + es_instalacion + ","
                        + " " + cobrar + ", '" + motivo_no_cobrar + "', 'e', '" + radusername + "', '" + radclave + "', '" + deviceclave + "', '" + usuario + "', " + set_convenio_tarjeta + ","
                        + " '" + tipo_cliente_instalacion + "'," + factura_credito + ",'1','" + costo_instalacion_facturado + "','" + tiempo_permanencia_contrato + "');";
                if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                    rs = st.getGeneratedKeys();
                    if (rs.next()) {
                        id_instalacion = rs.getString(1) != null ? rs.getString(1) : "-1";
                        rs.close();
                    }
                    sql = "INSERT INTO tbl_soporte(id_instalacion, id_sucursal, num_soporte, quien_llama, telefono_llama, "
                            + " alias_contesta, problema, diagnostico, fecha_llamada, hora_llamada,capacidad_efectiva,tipo_servicio,recomendacion,estado,procedente) "
                            + " VALUES(" + id_instalacion + ", " + id_sucursal + ", " + num_orden_soporte + ", '" + razon_social + "', '" + telefono + " ', "
                            + " 'administrador', 'INSTALACION VIA WEB', 'INSTALACION VIA WEB', now()::date, now()::time,'0','" + tipo_instalacion + "','INSTALACION VIA WEB','s','false');";
                    if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                        boolean oki = true;
                        String id_soporte = "-1";
                        rs = st.getGeneratedKeys();
                        if (rs.next()) {
                            id_soporte = rs.getString(1) != null ? rs.getString(1) : "-1";
                            rs.close();
                        }
                        st.executeUpdate("update tbl_contrato set id_instalacion='" + id_instalacion + "' where id_contrato='" + id_contrato + "';");
                        //st.executeUpdate("delete from tbl_ips_libres where ips='" + ip.trim() + "' and id_sucursal=" + id_sucursal + ";");
                        if (id_promocion.trim().compareTo("") != 0) {
                            st.executeUpdate("insert into tbl_instalacion_promocion(id_instalacion, id_promocion) values(" + id_instalacion + ", " + id_promocion + ");");
                        }
                        double total_final1 = Double.parseDouble(total);
                        String estado_orden = "1";
                        if (total_final1 > 0) {
                            estado_orden = "0";
                        }
                        st.execute(" INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo,id_soporte) "
                                + " VALUES(" + id_instalacion + ", " + id_sucursal + ", " + num_orden + ", '3', 'administrador', now()::date, now()::time, 'administrador', now(), now(), 'Instalacion via web', '" + estado_orden + "', 'c','" + id_soporte + "');");
                        if (tipo_instalacion.compareTo("f") == 0 || tipo_instalacion.compareTo("g") == 0) {
                            st.execute("insert into tbl_instalacion_aprobacion(id_instalacion)values('" + id_instalacion + "');");
                        }
                        if (oki) {
                            sql = "insert into tbl_bodega(id_sucursal, bodega, id_responsable, ubicacion, id_instalacion, es_responsable_cliente) values("
                                    + id_sucursal + ", 'INSTALACION No. " + id_sucursal + "-" + num_instalacion + " " + razon_social + "', " + id_cliente + ", '" + direccion_instalacion
                                    + "', " + id_instalacion + ", true);";
                            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                                String id_bodega = "-1";
                                rs = st.getGeneratedKeys();
                                if (rs.next()) {
                                    id_bodega = rs.getString(1) != null ? rs.getString(1) : "-1";
                                    rs.close();
                                }
                                st.executeUpdate("insert into tbl_estanteria(id_bodega, estanteria) values (" + id_bodega + ", 'EST-" + id_instalacion + "');");
                            }/*error en la creacion de la bodega*/ else {
                                ok = false;
                            }
                        }/*error en la creacion de la factura*/ else {
                            ok = false;
                        }
                    } /*error en la creacion el soporte*/ else {
                        ok = false;
                    }

                }/*error en la creacion de la instalacion*/ else {
                    ok = false;
                }
            } /*error en creacion del contrato*/ else {
                ok = false;
            }
            if (ok) {
                ins_fac = id_contrato + ";" + id_instalacion + ";" + id_factura_venta;
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

    public String[] getPromocionTiempo(String id_instalacion) {
        String tiempos[] = {"", "", "", "n", "n"};
        boolean promocion = false;
        try {
            ResultSet rs = this.consulta("select men_tiempo_de_permanencia_min,tiempo_trascurridotmp,promociontmp from vta_instalacion_promocion_contrato where id_instalacion='" + id_instalacion + "';");
            if (rs.next()) {
                tiempos[0] = (rs.getString(1) != null ? rs.getString(1) : "");
                tiempos[1] = (rs.getString(2) != null ? rs.getString(2) : "");
                tiempos[2] = (rs.getString(3) != null ? rs.getString(3) : "");
                promocion = true;
                rs.close();
            }
            if (promocion) {
                tiempos[0] = (tiempos[0].trim().compareTo("") != 0 ? tiempos[0] : "0");
                tiempos[1] = (tiempos[1].trim().compareTo("") != 0 ? tiempos[1] : "0");
                double uno = Double.parseDouble(tiempos[0]);
                double dos = Double.parseDouble(tiempos[1]);
                tiempos[3] = "s";
                if (dos > uno) {
                    tiempos[4] = "s";
                }
            }
        } catch (Exception e) {

        }
        return tiempos;
    }

    public String[] getInstalacionAprobado(String id_instalacion) {
        String columnas[] = {"", "", ""};
        try {
            ResultSet rs = this.consulta("select * from vta_instalacion_aprobacion where id_instalacion='" + id_instalacion + "';");
            if (rs.next()) {
                columnas[0] = rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "";
                columnas[1] = rs.getString("estado") != null ? rs.getString("estado") : "";
                columnas[2] = rs.getString("txtestado") != null ? rs.getString("txtestado") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnas;
    }

    public String getIdPromocionInstalacion(String id_instalacion) {
        String id_promocion = "";
        try {
            ResultSet rs = this.consulta("select * from tbl_instalacion_promocion where id_instalacion ='" + id_instalacion + "';");
            if (rs.next()) {
                id_promocion = rs.getString("id_promocion") != null ? rs.getString("id_promocion") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_promocion;
    }
}
