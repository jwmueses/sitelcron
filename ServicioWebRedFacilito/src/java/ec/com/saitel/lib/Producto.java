/**
 * @version 1.0
 * @package FACTURAPYMES.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos
 * reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL. FACTURAPYMES! es un
 * software de libre distribución, que puede ser copiado y distribuido bajo los
 * términos de la Licencia Pública General GNU, de acuerdo con la publicada por
 * la Free Software Foundation, versión 2 de la licencia o cualquier versión
 * posterior.
 */
package ec.com.saitel.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class Producto extends DataBase {

    public Producto(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getProductos() {
        return this.consulta("SELECT id_producto, codigo, descripcion FROM vta_producto_n order by codigo;");
    }

    public ResultSet getBienes() {
        return this.consulta("SELECT id_producto, producto FROM vta_producto_n WHERE tipo='b' order by producto;");
    }

    public ResultSet getProductosImportacion() {
        return this.consulta("SELECT id_producto, codigo, descripcion, precio_costo FROM vta_producto_n where tipo in ('p', 'b') order by codigo;");
    }

    public ResultSet getPrecios(String id_producto) {
        return this.consulta("SELECT id_lista_precio, lista, utilidad from tbl_producto_precio where id_producto=" + id_producto + " order by utilidad desc");
    }

    /* factura de compra */
    public ResultSet getProductos(String idProveedor) {
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, I.porcentaje "
                + "FROM (tbl_producto as P inner join tbl_proveedor_producto as PP on P.id_producto=PP.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=P.id_iva "
                + "where PP.id_proveedor=" + idProveedor + " order by P.descripcion;");
    }

    public ResultSet getProductoFiltro(int idSucursal, String txt) {
        txt = txt.toLowerCase();
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, I.porcentaje "
                + "FROM tbl_producto as P inner join tbl_iva as I on I.id_iva=P.id_iva WHERE P.codigo='" + txt + "' " + (idSucursal != 1 ? " and tipo='s'" : ""));
    }

    public ResultSet getProductosFiltro(int idSucursal, String txt) {
        txt = txt.toLowerCase();
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, I.porcentaje "
                + "FROM tbl_producto as P inner join tbl_iva as I on I.id_iva=P.id_iva "
                + "WHERE (lower(P.codigo) like '" + txt + "%' or lower(P.descripcion) like '%" + txt + "%') " + (idSucursal != 1 ? "and tipo='s'" : "")
                + " and eliminado=false order by P.descripcion limit 10 offset 0;");
    }

    /* pedidos, traspasos */
    ///modificado de transpaso
    public ResultSet getProductos(int id_sucursal) {
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo,P.tipo "
                + "FROM (vta_producto_n as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                + "where SP.id_sucursal=" + id_sucursal + " and tipo in ('p','b','r') order by P.codigo;");
    }

    public ResultSet getActivos(int id_sucursal) {
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo "
                + "FROM (vta_producto_n as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                + "where SP.id_sucursal=" + id_sucursal + " and tipo in ('p','b') order by P.codigo;");
    }

    /* pre-facturas de ISP */
    public ResultSet getProductoISP(int id_sucursal, String id) {
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "
                + "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"
                + "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva "
                + "FROM ((vta_producto_n as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=SP.id_iva) "
                + "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "
                + "where SP.id_sucursal=" + id_sucursal + " and P.id_producto=" + id + " "
                + "group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, case when tiene_iva then '~' else '' end, "
                + "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo");
    }

    /* para el anticipo internet */
    public ResultSet getProductoAnticipo(int id_sucursal, String id) {
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "
                + "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"
                + "SP.descuento, case when tipo='s' then '~' else '' end as de_servicio, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) as ut_min, I.codigo as codigo_iva "
                + "FROM ((vta_producto_n as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=SP.id_iva) "
                + "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "
                + "where SP.id_sucursal=" + id_sucursal + " and P.id_producto=" + id + " "
                + "group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "
                + "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo");
    }

    /* facturas de ventas */
    public ResultSet getProductoVenta(int id_sucursal, String txt) {
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "
                + "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end),"
                + "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva,P.id_plan_cuenta_gasto "
                + "FROM ((vta_producto_n as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=SP.id_iva) "
                + "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "
                + "where SP.id_sucursal=" + id_sucursal + " and P.tipo<>'g' and (P.codigo = '" + txt + "' or P.codigo_fabricante = '" + txt + "') "
                + "group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, I.codigo,P.id_plan_cuenta_gasto, "
                + "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) ");
    }

    public ResultSet getProductosVenta(int id_sucursal, String txt) {
        txt = txt.toLowerCase();
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, \n"
                + "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end),\n"
                + "                SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva,P.id_plan_cuenta_gasto \n"
                + "                FROM ((vta_producto_n as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) \n"
                + "                inner join tbl_iva as I on I.id_iva=SP.id_iva)\n"
                + "                inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto \n"
                + "                where SP.id_sucursal=" + id_sucursal + " and P.tipo<>'g' and \n"
                + "                (lower(P.codigo) like '" + txt + "%' or lower(P.codigo_fabricante) like '" + txt + "%' or lower(P.descripcion) like '%" + txt + "%') \n"
                + "                group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, I.codigo,P.id_plan_cuenta_gasto, \n"
                + "                SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) \n"
                + "                order by P.codigo limit 10 offset 0");
    }

    /*public ResultSet getProductosVenta(int id_sucursal)
     {
     return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, case when tiene_iva then '~' else '' end, "+
     "case when tipo='s' then P.precio_venta_servicio else SP.precio_venta end, SP.descuento, case when tipo='s' then '~' else '' end, " +
     "U.utilidad FROM (vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
     "inner join tbl_lista_precio as U on SP.id_lista_precio=U.id_lista_precio " +
     "where SP.id_sucursal="+id_sucursal+" order by P.codigo;");
     }*/
    public ResultSet getProducto(String id) {
        return this.consulta("SELECT P.*, I.porcentaje FROM vta_producto_n as P inner join tbl_iva as I on I.id_iva=P.id_iva where id_producto=" + id + ";");
    }

    public boolean estaDuplicado(String id, String c) {
        ResultSet res = this.consulta("SELECT * FROM tbl_producto where codigo='" + c + "' and id_producto<>" + id + ";");
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

    public ResultSet getProducto(String c1, String c2) {
        return this.consulta("SELECT id_producto, codigo, descripcion, I.porcentaje "
                + "FROM tbl_producto as P inner join tbl_iva as I on I.id_iva=P.id_iva "
                + "where (codigo='" + c1 + "' or codigo_fabricante='" + c1 + "') or (codigo='" + c2 + "' or codigo_fabricante='" + c2 + "')");

    }

    public boolean insertar(String codigo, String codigo_fabricante, String descripcion, String tipo, String utilidad_min, String unidad_medida, String id_categoria,
            String stock_min, float stock, String precio_venta_servicio, String id_iva, String id_plan_cuenta_compra, String id_plan_cuenta_venta,
            String observacion, String proveedores, String sucursales, String ids_iva, String descuentos, String ubicaciones,
            String stocks, boolean aMatriz, int id_sucursal_sesion) {
        String id_producto = this.insert("INSERT INTO tbl_producto(codigo, codigo_fabricante, descripcion, tipo, utilidad_min, unidad_medida, id_categoria, "
                + "stock_min, stock, precio_venta_servicio, id_iva, id_plan_cuenta_compra, id_plan_cuenta_venta, observacion) "
                + "VALUES('" + codigo + "', '" + codigo_fabricante + "', '" + descripcion + "', '" + tipo + "', " + utilidad_min + ", '" + unidad_medida + "', '" + id_categoria + "', "
                + stock_min + ", " + stock + ", " + precio_venta_servicio + ", '" + id_iva + "', " + id_plan_cuenta_compra + ", " + id_plan_cuenta_venta + ", '" + observacion + "');");

        if (id_producto.compareTo("-1") != 0) {
            List sql = new ArrayList();
            sql.add("INSERT INTO tbl_producto_precio(id_producto,lista,utilidad) VALUES(" + id_producto + ", 'CONSUMIDOR FINAL', " + (tipo.compareTo("s") == 0 ? utilidad_min : "30") + ")");
            if (proveedores.compareTo("") != 0) {
                String vec[] = proveedores.split(",");
                for (int i = 0; i < vec.length; i++) {
                    sql.add("INSERT INTO tbl_proveedor_producto(id_proveedor, id_producto) values(" + vec[i] + ", " + id_producto + ");");
                }
            }

            //if(tipo.compareTo("b")!=0){
            if (sucursales.compareTo("") != 0) {
                String vecSuc[] = sucursales.split(",");
                //String vecLisPre[] = listas_precios.split(",");
                String vecIva[] = ids_iva.split(",");
                String vecDesc[] = descuentos.split(",");
                String vecUbic[] = ubicaciones.split(",");
                String vecStocks[] = stocks.split(",");
                for (int i = 0; i < vecSuc.length; i++) {
                    sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, id_iva, descuento, id_ubicacion, stock_sucursal) "
                            + "values(" + vecSuc[i] + ", " + id_producto + ", " + vecIva[i] + ", " + vecDesc[i] + ", '" + vecUbic[i] + "', " + vecStocks[i] + ");");
                    if (aMatriz) {
                        sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, id_iva, descuento, id_ubicacion, stock_sucursal) "
                                + "values(" + id_sucursal_sesion + ", " + id_producto + ", " + vecIva[i] + ", " + vecDesc[i] + ", '" + vecUbic[i] + "', " + vecStocks[i] + ");");
                    }
                }
            }
            //}

            return this.transacciones(sql);
        }
        return false;
    }

    public boolean actualizar(String id, String codigo, String codigo_fabricante, String descripcion, String tipo, String utilidad_min, String unidad_medida,
            String id_categoria, String stock_min, int stock, String precio_venta_servicio, String id_iva, String id_plan_cuenta_compra, String id_plan_cuenta_venta,
            String observacion, String proveedores, String sucursales, String ids_iva, String descuentos, String ubicaciones, String stocks) {
        List sql = new ArrayList();

        if (proveedores.compareTo("") != 0) {
            sql.add("DELETE FROM tbl_proveedor_producto WHERE id_producto=" + id + ";");
            String vec[] = proveedores.split(",");
            for (int i = 0; i < vec.length; i++) {
                sql.add("INSERT INTO tbl_proveedor_producto(id_proveedor, id_producto) values(" + vec[i] + ", " + id + ");");
            }
        }

        sql.add("DELETE FROM tbl_sucursal_producto WHERE id_producto=" + id + ";");

        //if(tipo.compareTo("b")!=0){
        if (sucursales.compareTo("") != 0) {
            String vecSuc[] = sucursales.split(",");
            //String vecLisPre[] = listas_precios.split(",");
            String vecIva[] = ids_iva.split(",");
            String vecDesc[] = descuentos.split(",");
            String vecUbic[] = ubicaciones.split(",");
            String vecStocks[] = stocks.split(",");
            for (int i = 0; i < vecSuc.length; i++) {
                sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, id_iva, descuento, id_ubicacion, stock_sucursal) "
                        + "values(" + vecSuc[i] + ", " + id + ", " + vecIva[i] + ", " + vecDesc[i] + ", '" + vecUbic[i] + "', " + vecStocks[i] + ");");
            }
        }
        //}
        /* porque el trigger de la tabla producto actualiza en la tabla tbl_sucursal_producto */
        sql.add("UPDATE tbl_producto SET codigo='" + codigo + "', codigo_fabricante='" + codigo_fabricante + "', descripcion='" + descripcion + "', tipo='" + tipo + "', "
                + "utilidad_min=" + utilidad_min + ", unidad_medida='" + unidad_medida + "', id_categoria='" + id_categoria + "', stock_min=" + stock_min + ", "
                + "stock=" + stock + ", precio_venta_servicio=" + precio_venta_servicio + ", id_iva='" + id_iva + "', id_plan_cuenta_compra=" + id_plan_cuenta_compra
                + ", id_plan_cuenta_venta=" + id_plan_cuenta_venta + ", observacion='" + observacion + "' WHERE id_producto=" + id + ";");

        return this.transacciones(sql);
    }

    public boolean insertar(String codigo, String codigo_fabricante, String descripcion, String tipo, String utilidad_min, String unidad_medida, String id_categoria,
            String stock_min, float stock, String precio_venta_servicio, String id_iva, String id_plan_cuenta_compra, String id_plan_cuenta_venta,
            String observacion, String proveedores, String sucursales, String ids_iva, String descuentos, String ubicaciones,
            String stocks, boolean aMatriz, int id_sucursal_sesion, String id_plan_cuenta_gasto, String limite_gasto, String tiempovida, String limitegastoinfra, String cant_unidades, String precio_venta_orden) {
        String id_producto = this.insert("INSERT INTO tbl_producto(codigo, codigo_fabricante, descripcion, tipo, utilidad_min, unidad_medida, id_categoria, "
                + "stock_min, stock, precio_venta_servicio, id_iva, id_plan_cuenta_compra, id_plan_cuenta_venta, observacion,id_plan_cuenta_gasto,limite_gasto,tiempovida,limitegastoinfra,cant_unidades,precio_venta_orden) "
                + "VALUES('" + codigo + "', '" + codigo_fabricante + "', '" + descripcion + "', '" + tipo + "', " + utilidad_min + ", '" + unidad_medida + "', '" + id_categoria + "', "
                + stock_min + ", " + stock + ", " + precio_venta_servicio + ", '" + id_iva + "', " + id_plan_cuenta_compra + ", " + id_plan_cuenta_venta + ", '" + observacion + "', " + id_plan_cuenta_gasto + ", " + limite_gasto + ", " + tiempovida + ", " + limitegastoinfra + ", " + cant_unidades + ", " + precio_venta_orden + ");");

        if (id_producto.compareTo("-1") != 0) {
            List sql = new ArrayList();
            sql.add("INSERT INTO tbl_producto_precio(id_producto,lista,utilidad) VALUES(" + id_producto + ", 'CONSUMIDOR FINAL', " + (tipo.compareTo("s") == 0 ? utilidad_min : "30") + ")");
            if (proveedores.compareTo("") != 0) {
                String vec[] = proveedores.split(",");
                for (int i = 0; i < vec.length; i++) {
                    sql.add("INSERT INTO tbl_proveedor_producto(id_proveedor, id_producto) values(" + vec[i] + ", " + id_producto + ");");
                }
            }

            //if(tipo.compareTo("b")!=0){
            if (sucursales.compareTo("") != 0) {
                String vecSuc[] = sucursales.split(",");
                //String vecLisPre[] = listas_precios.split(",");
                String vecIva[] = ids_iva.split(",");
                String vecDesc[] = descuentos.split(",");
                String vecUbic[] = ubicaciones.split(",");
                String vecStocks[] = stocks.split(",");
                for (int i = 0; i < vecSuc.length; i++) {
                    sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, id_iva, descuento, id_ubicacion, stock_sucursal) "
                            + "values(" + vecSuc[i] + ", " + id_producto + ", " + vecIva[i] + ", " + vecDesc[i] + ", '" + vecUbic[i] + "', " + vecStocks[i] + ");");
                    if (aMatriz) {
                        sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, id_iva, descuento, id_ubicacion, stock_sucursal) "
                                + "values(" + id_sucursal_sesion + ", " + id_producto + ", " + vecIva[i] + ", " + vecDesc[i] + ", '" + vecUbic[i] + "', " + vecStocks[i] + ");");
                    }
                }
            }
            //}

            return this.transacciones(sql);
        }
        return false;
    }

    public boolean actualizar(String id, String codigo, String codigo_fabricante, String descripcion, String tipo, String utilidad_min, String unidad_medida,
            String id_categoria, String stock_min, int stock, String precio_venta_servicio, String id_iva, String id_plan_cuenta_compra, String id_plan_cuenta_venta,
            String observacion, String proveedores, String sucursales, String ids_iva, String descuentos, String ubicaciones, String stocks, String id_plan_cuenta_gasto, String limite_gasto, String tiempovida, String limitegastoinfra, String cant_unidades, String precio_venta_orden) {
        List sql = new ArrayList();

        if (proveedores.compareTo("") != 0) {
            sql.add("DELETE FROM tbl_proveedor_producto WHERE id_producto=" + id + ";");
            String vec[] = proveedores.split(",");
            for (int i = 0; i < vec.length; i++) {
                sql.add("INSERT INTO tbl_proveedor_producto(id_proveedor, id_producto) values(" + vec[i] + ", " + id + ");");
            }
        }

        sql.add("DELETE FROM tbl_sucursal_producto WHERE id_producto=" + id + ";");

        //if(tipo.compareTo("b")!=0){
        if (sucursales.compareTo("") != 0) {
            String vecSuc[] = sucursales.split(",");
            //String vecLisPre[] = listas_precios.split(",");
            String vecIva[] = ids_iva.split(",");
            String vecDesc[] = descuentos.split(",");
            String vecUbic[] = ubicaciones.split(",");
            String vecStocks[] = stocks.split(",");
            for (int i = 0; i < vecSuc.length; i++) {
                sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, id_iva, descuento, id_ubicacion, stock_sucursal) "
                        + "values(" + vecSuc[i] + ", " + id + ", " + vecIva[i] + ", " + vecDesc[i] + ", '" + vecUbic[i] + "', " + vecStocks[i] + ");");
            }
        }
        //}
        /* porque el trigger de la tabla producto actualiza en la tabla tbl_sucursal_producto */
        sql.add("UPDATE tbl_producto SET codigo='" + codigo + "', codigo_fabricante='" + codigo_fabricante + "', descripcion='" + descripcion + "', tipo='" + tipo + "', "
                + "utilidad_min=" + utilidad_min + ", unidad_medida='" + unidad_medida + "', id_categoria='" + id_categoria + "', stock_min=" + stock_min + ", "
                + "stock=" + stock + ", precio_venta_servicio=" + precio_venta_servicio + ", id_iva='" + id_iva + "', id_plan_cuenta_compra=" + id_plan_cuenta_compra
                + ", id_plan_cuenta_venta=" + id_plan_cuenta_venta + ", observacion='" + observacion + "',id_plan_cuenta_gasto='" + id_plan_cuenta_gasto + "',limite_gasto='" + limite_gasto + "',tiempovida='" + tiempovida + "',limitegastoinfra='" + limitegastoinfra + "',cant_unidades='" + cant_unidades + "',precio_venta_orden='" + precio_venta_orden + "' WHERE id_producto=" + id + ";");

        return this.transacciones(sql);
    }

    public boolean aplicarDescuento(String idSuc, String idPds, String descuento) {
        String w = " WHERE id_producto in (" + idPds + ")";
        if (idSuc.compareTo("-0") != 0) {
            w += " and id_sucursal=" + idSuc;
        }
        return this.ejecutar("UPDATE tbl_sucursal_producto SET descuento=" + descuento + w);
    }

    public ResultSet getKardex(String id, String fecha_ini, String fecha_fin) {
        return this.consulta("SELECT * FROM tbl_kardex where id_producto=" + id + " and fecha between '" + fecha_ini + "' and '" + fecha_fin + "' order by id_kardex;");
    }

    public ResultSet getKardexSucursal(String idSuc, String id, String fecha_ini, String fecha_fin) {
        return this.consulta("SELECT * FROM tbl_kardex_sucursal where id_sucursal=" + idSuc + " and id_producto=" + id + " and fecha between '" + fecha_ini + "' and '" + fecha_fin + "' order by id_kardex_sucursal;");
    }

    public ResultSet getCodigoProveedores(String id) {
        return this.consulta("SELECT P.id_proveedor FROM tbl_proveedor as P inner join tbl_proveedor_producto as PA on "
                + "P.id_proveedor=PA.id_proveedor where PA.id_producto=" + id + " order by P.id_proveedor;");
    }

    public ResultSet getCodigoSucursales(String id) {
        return this.consulta("SELECT id_sucursal, descuento, id_ubicacion, stock_sucursal, id_iva FROM tbl_sucursal_producto where id_producto=" + id + " order by id_sucursal;");
    }

    public String concatenarValores(String idsArt, String cant, String vu, String vt) {
        String param = "";
        String vecproductos[] = idsArt.split(",");
        String cantidad[] = cant.split(",");
        String valorUnitario[] = vu.split(",");
        String valorTotal[] = vt.split(",");
        for (int i = 0; i < vecproductos.length; i++) {
            param += "['" + vecproductos[i] + "','" + cantidad[i] + "','" + valorUnitario[i] + "','" + valorTotal[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public int setInventarioInicial(String fecha, String inv, String idsArt, String cant, String vu, String vt) {
        int num = -1;
        try {
            String param = this.concatenarValores(idsArt, cant, vu, vt);
            ResultSet res = this.consulta("select proc_setInventarioInicial('" + fecha + "', '" + inv + "', " + param + ");");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : -1;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /*PRODUCTOS */
    public ResultSet getComboCat() {
        return this.consulta("SELECT id_cat_activo,cod_categorizacion,nombre_categorizacion FROM tbl_cat_activos WHERE eliminado=false order by cod_categorizacion;");
    }

    public boolean activosNuevosGuardar(String id_padre, String ids) {
        return this.ejecutar("update tbl_producto set id_categoria=" + id_padre + " where id_producto in (" + ids + ");");
    }

    /*TIPOS DE BODEGAS*/
    public ResultSet getNombreTipoBodega() {
        return this.consulta("SELECT id_bodega_tipo, nombre_bodega FROM tbl_bodega_tipo order by nombre_bodega;");
    }

    public ResultSet tipoBodega(String id) {
        return this.consulta("SELECT * FROM tbl_bodega_tipo WHERE id_bodega_tipo=" + id + " order by nombre_bodega;");
    }

    /*ORGANIZAR PRODUCTOS*/
    public ResultSet productosNuevos(String id) {
        return this.consulta("SELECT * FROM vta_producto_n WHERE id_categoria='" + id + "' order by descripcion;");
    }

    public boolean getAcceso(String id, String pag) {
        try {
            ResultSet res = this.consulta("select P.pagina from tbl_pagina as P inner join tbl_privilegio as PR on P.id_pagina=PR.id_pagina WHERE PR.id_rol=" + id + " and pagina='" + pag + "';");
            if (this.getFilas(res) > 0) {
                return true;
            }
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    ////notificaciones para producto
    public boolean notificacionstock(String stock, String stock_min, String diferencia) {
        double stock1 = Double.parseDouble(stock);
        double stock_min1 = Double.parseDouble(stock_min);
        double diferencia1 = Double.parseDouble(diferencia);
        stock_min1 = stock_min1 + diferencia1;
        if (stock1 < stock_min1) {
            return true;
        } else {
            return false;
        }
    }

    public String[] datosmovbodega(String bodega) {
        String datos[] = new String[3];
        try {

            ResultSet rs = this.consulta("select id_producto,id_sucursal,id_empleado from tbl_bodsubministro  where idbodsubministro='" + bodega + "'");
            if (rs.next()) {
                datos[0] = (rs.getString("id_producto") != null ? rs.getString("id_producto") : "0");
                datos[1] = (rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "0");
                datos[2] = (rs.getString("id_empleado") != null ? rs.getString("id_empleado") : "0");
            }

        } catch (SQLException ex) {
            Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return datos;
    }

    public ResultSet detallemovimiento(String id_sucursal, String idproducto, String idempleado, String fechai, String fechaf) {
        return this.consulta("select fecha,detalle, \n"
                + "case when (tipomovimento=1 or tipomovimento=2)  then cantidad::text else ''::text end as e1,\n"
                + "case when (tipomovimento=1 or tipomovimento=2) then costo::text else ''::text end as e2,\n"
                + "case when (tipomovimento=1 or tipomovimento=2) then (cantidad*costo)::text else ''::text end as e3,\n"
                + "case tipomovimento 	when 3 then cantidad::text else ''::text end as s1,\n"
                + "case tipomovimento 	when 3 then costo::text else ''::text end as s2,\n"
                + "case tipomovimento 	when 3 then (cantidad*costo)::text else ''::text end as s3,\n"
                + "(cantidadfinal) as t1,costo as t2,(cantidadfinal*costo) as t3,cantidad_stock\n"
                + "from tbl_movsubmnistro where id_sucursal=" + id_sucursal + " and id_usuarioe=" + idempleado + " and id_producto=" + idproducto + " and (fecha>='" + fechai + "' and fecha<='" + fechaf + "')");
    }

}
