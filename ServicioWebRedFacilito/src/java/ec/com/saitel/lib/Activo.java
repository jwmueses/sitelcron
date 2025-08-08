/**
 ** @version 1.0
 ** @package FACTURAPYMES.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos
 * reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL. * FACTURAPYMES es un
 * software de libre distribuciÃ³n, que puede ser copiado y distribuido bajo los
 * tÃ©rminos de la Licencia Attribution-NonCommercial-NoDerivs 3.0 Unported, de
 * acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
 */
package ec.com.saitel.lib;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class Activo extends DataBase {

    public Activo(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public int getNumPedido() {
        int num = 1;
        try {
            ResultSet res = this.consulta("SELECT max(num_pedido) FROM tbl_pedido;");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : 0;
                num++;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public ResultSet getActivo(long id) {
        return this.consulta("SELECT * FROM tbl_activo where id_activo=" + id);
    }

    public ResultSet getActivo(String codigo) {
        return this.consulta("SELECT * FROM tbl_activo where id_activo='" + codigo + "'");
    }

    public ResultSet getActivoinfo(String codigo) {
        return this.consulta("SELECT * FROM tbl_activo where codigo_activo='" + codigo + "'");
    }

    public ResultSet getActivosId(String id_activo) {
        return this.consulta("select * from vta_activo_n where id_categoria='" + id_activo + "' order by descripcion;");
    }

    public String getIdActivo(String codigo) {
        String id = "";
        try {
            ResultSet rs = this.consulta("SELECT * FROM tbl_activo where upper(codigo_activo)='" + codigo.toUpperCase() + "'");
            if (rs.next()) {
                id = rs.getString("id_activo") != null ? rs.getString("id_activo") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            id = "";
        }
        return id;
    }

    public String getNomCategoria(String codigo) {
        String id = "";
        try {
            ResultSet rs = this.consulta("SELECT nombre_categorizacion FROM tbl_cat_activos where id_cat_activo='" + codigo + "'");
            if (rs.next()) {
                id = rs.getString("nombre_categorizacion") != null ? rs.getString("nombre_categorizacion") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            id = "";
        }
        return id;
    }

    public ResultSet getActivos() {
        return this.consulta("SELECT id_activo, codigo_activo, descripcion FROM vta_activo_n");
    }

    public ResultSet getActivoDepresiacion(String idActivo) {
        return this.consulta("select D.id_plan_cuenta_grupo, D.id_plan_cuenta, sum(A.valor_compra) as valor_compra, sum(A.valor_depreciado) as valor_depreciado, " + 
        "sum(valor_compra) - sum(valor_depreciado) as valor_util \n" +
        "from tbl_activo as A inner join tbl_tabla_depreciacion as D on A.id_tabla_depreciacion=D.id_tabla_depreciacion \n" +
        "where A.id_activo in (" + idActivo + ") group by D.id_plan_cuenta_grupo, D.id_plan_cuenta;");
    }
    
    public ResultSet getActivosArbolAll() {
        return this.consulta("SELECT * from tbl_cat_activos order by cod_categorizacion");
    }

    public ResultSet getActivosArbolAll(String descripcion) {
        return this.consulta("SELECT * from tbl_cat_activos where (cod_categorizacion like '%" + descripcion + "%' or lower(nombre_categorizacion) like '%" + descripcion + "%') order by cod_categorizacion");
    }

    public ResultSet getActivosArbol(String idCat) {
        return this.consulta("SELECT * from tbl_cat_activos where id_cat_activo=" + idCat + "");
    }

    public ResultSet getActivosArbolPadre(String idCat) {
        return this.consulta("SELECT * from tbl_cat_activos where id_cat_padre=" + idCat + "");
    }

    public ResultSet getActivos(String dni) {
        return this.consulta("select A.categoria, A.codigo_activo, A.descripcion, toDateSQL(C.fecha) "
                + "from (vta_activo_n as A inner join tbl_activo_custodio as AC on A.id_activo=AC.id_activo) "
                + "inner join tbl_activo_personalizacion as C on C.id_activo_personalizacion=AC.id_activo_personalizacion "
                + "where AC.actual=true and AC.eliminado=false and C.dni_recibe='" + dni + "' order by A.categoria, A.codigo_activo");
    }

    public ResultSet getDescripciones(String txt) {
        return this.consulta("SELECT distinct descripcion FROM tbl_activo WHERE lower(descripcion) like '" + txt + "%' limit 10 offset 0");
    }

    public ResultSet getCodigosActivos(String txt) {
        return this.consulta("SELECT max(codigo_activo) FROM tbl_activo WHERE lower(codigo_activo) like '" + txt + "%' limit 10 offset 0");
    }

    public ResultSet getActivos(int idSuc) {
        return this.consulta("SELECT id_activo, codigo_activo, descripcion FROM vta_activo_n where id_sucursal=" + idSuc + ";");
    }

    public boolean existe(String codigo) {
        boolean fl = false;
        try {
            ResultSet rs = this.consulta("SELECT * FROM tbl_activo where codigo_activo='" + codigo + "'");
            if (this.getFilas(rs) > 0) {
                fl = true;
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fl;
    }

    public boolean estaDuplicado(String id, String c) {
        ResultSet res = this.consulta("SELECT * FROM tbl_activo where codigo_activo='" + c + "' and id_activo<>" + id);
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

    public String insertar(String codigo_activo, String id_tabla_depreciacion, String descripcion, String id_sucursal, String id_categoria, String id_bodega, String ubicacion, String fecha_compra,
            String marca, String serie, String modelo, String num_partes, String color, String valor_compra, String valor_depreciado, String num_meses, String estado, String observacion,
            String proveedor) {
        String pk = this.insert("INSERT INTO tbl_activo(codigo_activo,id_tabla_depreciacion,descripcion,id_sucursal,id_categoria,id_bodega,ubicacion,fecha_compra,"
                + "marca,serie,modelo,num_partes,color,valor_compra,valor_depreciado,num_meses,estado,observacion, proveedor) "
                + "VALUES('" + codigo_activo + "', " + id_tabla_depreciacion + ", '" + descripcion + "', " + id_sucursal + ", '" + id_categoria + "', " + id_bodega + ", '" + ubicacion + "', '" + fecha_compra
                + "', '" + marca + "', '" + serie + "', '" + modelo + "', " + num_partes + ", '" + color + "', " + valor_compra + ", " + valor_depreciado + ", " + num_meses + ", '" + estado + "', '" + observacion + "', '" + proveedor + "');");
        return pk;
    }

    public String insertar(String codigo_activo, String id_tabla_depreciacion, String descripcion, String id_sucursal, String id_categoria, String id_bodega, String ubicacion, String fecha_compra,
            String marca, String serie, String modelo, String num_partes, String color, String valor_compra, String valor_depreciado, String num_meses, String estado, String observacion,
            String proveedor, String idactivosubministro) {
        String pk = this.insert("INSERT INTO tbl_activo(codigo_activo,id_tabla_depreciacion,descripcion,id_sucursal,id_categoria,id_bodega,ubicacion,fecha_compra,"
                + "marca,serie,modelo,num_partes,color,valor_compra,valor_depreciado,num_meses,estado,observacion, proveedor,id_activosubministro) "
                + "VALUES('" + codigo_activo + "', " + id_tabla_depreciacion + ", '" + descripcion + "', " + id_sucursal + ", '" + id_categoria + "', " + id_bodega + ", '" + ubicacion + "', '" + fecha_compra
                + "', '" + marca + "', '" + serie + "', '" + modelo + "', " + num_partes + ", '" + color + "', " + valor_compra + ", " + valor_depreciado + ", " + num_meses + ", '" + estado + "', '" + observacion + "', '" + proveedor + "', '" + idactivosubministro + "');");
        return pk;
    }

    public boolean actualizar(String id, String codigo_activo, String id_tabla_depreciacion, String descripcion, String id_sucursal, String id_categoria, String id_bodega, String ubicacion, String fecha_compra,
            String marca, String serie, String modelo, String num_partes, String color, String valor_compra, String valor_depreciado, String num_meses, String estado, String observacion,
            String proveedor) {
        return this.ejecutar("UPDATE tbl_activo SET codigo_activo='" + codigo_activo + "', id_tabla_depreciacion=" + id_tabla_depreciacion + ", descripcion='" + descripcion
                + "', id_sucursal=" + id_sucursal + ", id_categoria='" + id_categoria + "', id_bodega=" + id_bodega + ", ubicacion='" + ubicacion + "', fecha_compra='" + fecha_compra + "', marca='" + marca
                + "', serie='" + serie + "', modelo='" + modelo + "', num_partes=" + num_partes + ", color='" + color + "', valor_compra=" + valor_compra + ", valor_depreciado=" + valor_depreciado
                + ", num_meses=" + num_meses + ", estado='" + estado + "', observacion='" + observacion + "', proveedor='" + proveedor + "' WHERE id_activo=" + id + ";");
    }

    public ResultSet getCustodiosActivo(String id) {
        return this.consulta("select * from vta_activo_custodio where id_activo=" + id + " and eliminado=false order by fecha desc, id_activo_personalizacion desc;");
    }

    public boolean eliminar(int id_sucursal, String ids) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select proc_activoEliminar(" + id_sucursal + ", '" + ids + "');");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public ResultSet getMacActivos(int idSuc) {
        return this.consulta("SELECT codigo_activo as id_codigo_activo, codigo_activo FROM vta_activo_n where id_sucursal=" + idSuc + " "
                + "and upper(codigo_activo) not like 'SAI%'");
    }

    public ResultSet getMacActivosBodegas(String texto) {
        return this.consulta("SELECT distinct codigo_activo as id_codigo_activo, codigo_activo, A.descripcion, B.bodega, B.id_bodega "
                + "FROM (tbl_bodega as B inner join tbl_bodega_activo as AB on B.id_bodega=AB.id_bodega) "
                + "right outer join vta_activo_n as A on A.id_activo=AB.id_activo "
                + "where upper(codigo_activo) like '" + texto.toUpperCase() + "%' or upper(A.descripcion) like '%" + texto.toUpperCase() + "%' "
                + "limit 10 offset 0");
    }

    public ResultSet getMacActivosBodegas(String texto, String id_bodega) {
        return this.consulta("SELECT distinct codigo_activo as id_codigo_activo, codigo_activo, A.descripcion, B.bodega, B.id_bodega "
                + "FROM (tbl_bodega as B inner join tbl_bodega_activo as AB on B.id_bodega=AB.id_bodega) "
                + "right outer join vta_activo_n as A on A.id_activo=AB.id_activo "
                + "where B.id_bodega=" + id_bodega + " and (upper(codigo_activo) like '" + texto.toUpperCase() + "%' or upper(A.descripcion) like '%" + texto.toUpperCase() + "%') "
                + "limit 10 offset 0");
    }

    public ResultSet getcountMacActivosBodegas(String texto, String id_bodega) {
        return this.consulta("SELECT distinct count(codigo_activo) as conteo "
                + "FROM (tbl_bodega as B inner join tbl_bodega_activo as AB on B.id_bodega=AB.id_bodega) "
                + "right outer join vta_activo_n as A on A.id_activo=AB.id_activo "
                + "where B.id_bodega=" + id_bodega + " and (upper(codigo_activo) like '" + texto.toUpperCase() + "%' or upper(A.descripcion) like '%" + texto.toUpperCase() + "%') "
                + "limit 10 offset 0");
    }

    public ResultSet getMacActivosBodega(String idBodega, String texto) {
        return this.consulta("SELECT distinct codigo_activo as id_codigo_activo, codigo_activo FROM vta_activo_n as A inner join tbl_bodega_activo as AB on A.id_activo=AB.id_activo "
                + "where AB.id_bodega=" + idBodega + " and (upper(A.codigo_activo) like '" + texto.toUpperCase() + "%' or upper(A.descripcion) like '%" + texto.toUpperCase() + "%') "
                + "limit 10 offset 0");
    }

    public ResultSet getMacActivosInstalados(String texto) {
        return this.consulta("SELECT distinct codigo_activo as id_codigo_activo, codigo_activo FROM vta_activo_n "
                + "where upper(codigo_activo) like '" + texto.toUpperCase() + "%' and upper(codigo_activo) in (select distinct upper(mac) from tbl_instalacion where mac<>'' and anulado=false) "
                + "limit 10 offset 0");
    }

    public ResultSet getMacActivosAll(String texto) {
        return this.consulta("SELECT distinct codigo_activo as id_codigo_activo, codigo_activo FROM vta_activo_n "
                + "where upper(codigo_activo) like '" + texto.toUpperCase() + "%' "
                + "limit 10 offset 0");
    }

    ///ORDENES DE TRABAJO
    public String getDescripcion(String codigo) {
        String id = "";
        try {
            ResultSet rs = this.consulta("SELECT descripcion FROM tbl_activo where codigo_activo='" + codigo + "'");
            if (rs.next()) {
                id = rs.getString("descripcion") != null ? rs.getString("descripcion") : "";
                rs.close();
            }
        } catch (Exception e) {
            id = "";
            e.printStackTrace();
        }
        return id;
    }

    public String getIdActivoBodega(String id_install, String codigo) {
        String id = "-1";
        try {
            ResultSet rs = this.consulta("SELECT * FROM vta_activo_n where codigo_activo='" + codigo + "' and "
                    + "codigo_activo not in (select distinct mac from tbl_instalacion where id_instalacion<>" + id_install
                    + " and mac<>'' and estado_servicio not in ('t', 'e') and anulado=false)");
            if (rs.next()) {
                id = rs.getString("id_activo") != null ? rs.getString("id_activo") : "-1";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            id = "-1";
        }
        return id;
    }

    /*  COMPRAS  */
    public String getCodigosIngresados(String cod_activos) {
        String codigos = "";
        String grupo_activos[] = cod_activos.split(",");
        for (int i = 0; i < grupo_activos.length; i++) {
            String vec_activo[] = grupo_activos[i].split(";");
            for (int j = 0; j < vec_activo.length; j++) {
                String codigo[] = vec_activo[j].replace("|", ";").split(";");
                codigos += codigo[0] + ",";
            }
        }
        return (codigos.compareTo("") != 0 ? codigos.substring(0, codigos.length() - 1) : "-1");
    }

    public boolean codsDuplicados(String cod_activos) {
        String vecActivos[] = cod_activos.split(",");
        for (int i = 0; i < vecActivos.length - 1; i++) {
            for (int j = i + 1; j < vecActivos.length; j++) {
                if (vecActivos[i].compareTo(vecActivos[j]) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean codsDuplicados(String ids, String codigos) {
        ResultSet res = this.consulta("SELECT * FROM tbl_activo where codigo_activo in('" + codigos + "') and id_activo not in(" + ids + ")");
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

    public String concatenarValores(String cod_activos, String cantidades, String precios_unitarios, String subtotales, String descuentos,
            String ivas, String totales, String descripciones, String id_depresiaciones, String id_categorias, String ubicaciones, String de_activo) {
        String param = "";
        String vecActs[] = cod_activos.split(",");
        String vecCant[] = cantidades.split(",");
        String vecPU[] = precios_unitarios.split(",");
        String vecSubt[] = subtotales.split(",");
        String vecDes[] = descuentos.split(",");
        String vecIva[] = ivas.split(",");
        String vecTot[] = totales.split(",");
        String vecDesc[] = descripciones.split(",");
        String vecDepr[] = id_depresiaciones.split(",");
        String vecCats[] = id_categorias.split(",");
        String vecUbic[] = ubicaciones.split(",");
        String vecDeAc[] = de_activo.split(",");
        for (int i = 0; i < vecActs.length; i++) {
            param += "['" + vecActs[i] + "','" + vecCant[i] + "','" + vecPU[i] + "','" + vecSubt[i] + "','" + vecDes[i] + "','" + vecIva[i] + "','" + vecTot[i] + "','FALSE','" + vecDesc[i] + "','" + vecDepr[i] + "','" + vecCats[i] + "','" + vecUbic[i] + "','" + vecDeAc[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String concatenarValores(String id_retenciones, String bases_imponibles, String valores_retenidos, String codBI) {
        String param = "";
        String vecRet[] = id_retenciones.split(",");
        String vecBI[] = bases_imponibles.split(",");
        String vecVal[] = valores_retenidos.split(",");
        String vecCodBI[] = codBI.split(",");
        for (int i = 0; i < vecRet.length; i++) {
            param += "['" + vecRet[i] + "','" + vecBI[i] + "','" + vecVal[i] + "','" + vecCodBI[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String insertar(int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
            String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina,
            String total_pagado, String paramArtic, String sustento_tributario, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret,
            String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, String total_retenido, String paramRet, String paramAsiento,
            String id_rol, String id_bodega, String xmlFirmado) {
        String num = "-1:-1:-1";
        try {
            ResultSet res = this.consulta("select facturaCompraActivo(" + id_sucursal + ", '" + serie_factura + "', '" + autorizacion + "', " + num_factura + ", " + id_proveedor + ", '" + fecha_compra + "', "
                    + "'" + fecha_ven_factura + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_6 + ", " + descuento + ", " + iva_2 + ", " + propina + ", " + total_pagado + ", " + paramArtic + ", "
                    + "'" + sustento_tributario + "', '" + num_serie_ret + "', '" + autorizacion_ret + "', " + num_retencion + ", " + fecha_emision_ret + ", '" + ret_ejercicio_fiscal_mes + "', " + ejercicio_fiscal + ", " + total_retenido + ", " + paramRet
                    + ", " + paramAsiento + ", " + id_rol + ", " + id_bodega + ", '" + xmlFirmado + "');");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1:-1:-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String contabilizar(String id_factura_compra, int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
            String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina,
            String total_pagado, String paramArtic, String sustento_tributario, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret,
            String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, String total_retenido, String paramRet, String paramAsiento,
            String id_rol, String id_bodega, String xmlFirmado) {
        String num = "-1:-1:-1";
        try {
            ResultSet res = this.consulta("select facturaCompraActivoContabilizar(" + id_factura_compra + ", " + id_sucursal + ", '" + serie_factura + "', '" + autorizacion + "', " + num_factura + ", " + id_proveedor + ", '" + fecha_compra + "', "
                    + "'" + fecha_ven_factura + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_6 + ", " + descuento + ", " + iva_2 + ", " + propina + ", " + total_pagado + ", " + paramArtic + ", "
                    + "'" + sustento_tributario + "', '" + num_serie_ret + "', '" + autorizacion_ret + "', " + num_retencion + ", " + fecha_emision_ret + ", '" + ret_ejercicio_fiscal_mes + "', " + ejercicio_fiscal + ", " + total_retenido + ", " + paramRet
                    + ", " + paramAsiento + ", " + id_rol + ", " + id_bodega + ", '" + xmlFirmado + "');");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1:-1:-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

///modificado
    public String guardar(int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
            String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina,
            String total_pagado, String paramArtic, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret,
            String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, String total_retenido, String paramRet, String id_bodega, String ordendecompra) {
        double cxp = this.redondear(Float.valueOf(total_pagado) - Float.valueOf(total_retenido));
        String id_factura_compra = this.insert("INSERT INTO tbl_factura_compra(id_sucursal,id_proveedor,serie_factura,autorizacion,num_factura,fecha_ven_factura,"
                + "fecha,fecha_compra,subtotal,subtotal_0,subtotal_2,subtotal_6,descuento,iva_2,propina,total_compra,cxp,deuda,observacion, contabilizado, de_activo,id_ordendecompra) "
                + "VALUES(" + id_sucursal + ", " + id_proveedor + ", '" + serie_factura + "', '" + autorizacion + "', " + num_factura + ", '" + fecha_ven_factura
                + "', now(), '" + fecha_compra + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_6 + ", " + descuento + ", " + iva_2
                + ", " + propina + ", " + total_pagado + ", " + cxp + ", 0, '" + observacion + "', false, true,'" + ordendecompra + "');");
        if (id_factura_compra.compareTo("-1") != 0) {
            List sql = new ArrayList();
            paramArtic = paramArtic.replace("'", "").replace("],[", "_;_");

            String matArtic[] = paramArtic.split("_;_");
            for (int i = 0; i < matArtic.length; i++) {
                String vecArti[] = matArtic[i].replace("[", "").replace("]", "").split(",");
                sql.add("INSERT INTO tbl_factura_compra_activo_detalle(id_factura_compra, codigos_series, cantidad, p_u, p_st, descuento, "
                        + "iva, total, descripcion, id_tabla_depreciacion, id_categoria, id_bodega, ubicacion, de_activo, p_iva) "
                        + "values(" + id_factura_compra + ", '" + vecArti[0].trim() + "', " + vecArti[1].trim() + ", " + vecArti[2].trim() + ", " + vecArti[3].trim()
                        + ", " + vecArti[4].trim() + ", " + vecArti[5].trim() + ", " + vecArti[6].trim() + ", '" + vecArti[8].trim() + "', " + vecArti[9].trim()
                        + ", '" + vecArti[10].trim() + "', " + id_bodega + ", '" + vecArti[11].trim() + "', "
                        + (vecArti[12].trim().compareTo("1") == 0 ? "true" : "false") + ", " + vecArti[13].trim() + ");");
            }

            boolean reg_factura = this.transacciones(sql);
            if (!reg_factura) {
                id_factura_compra = "-1";
                this.ejecutar("delete from tbl_factura_compra where id_factura_compra=" + id_factura_compra);
            }
        }
        return id_factura_compra;
    }

//    public boolean actualizar(String id_factura_compra, int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
//            String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina,
//            String total_pagado, String paramArtic, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret,
//            String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, String total_retenido, String paramRet, String id_bodega) {
//        double cxp = this.redondear(Float.valueOf(total_pagado) - Float.valueOf(total_retenido));
//        List sql = new ArrayList();
//        sql.add("UPDATE tbl_factura_compra SET id_bodega=" + id_bodega + ", id_proveedor=" + id_proveedor + ", serie_factura='" + serie_factura + "', autorizacion='" + autorizacion + "', "
//                + "num_factura=" + num_factura + ", fecha_ven_factura='" + fecha_ven_factura + "', fecha_compra='" + fecha_compra + "', subtotal=" + subtotal
//                + ", subtotal_0=" + subtotal_0 + ", subtotal_2=" + subtotal_2 + ", subtotal_6=" + subtotal_6 + ", descuento=" + descuento + ", iva_2=" + iva_2
//                + ", total_compra=" + total_pagado + ", cxp=" + cxp + ", deuda=0, observacion='" + observacion + "', contabilizado=false WHERE id_factura_compra=" + id_factura_compra + ";");
//
//        sql.add("DELETE FROM tbl_factura_compra_activo_detalle WHERE id_factura_compra=" + id_factura_compra + ";");
//        paramArtic = paramArtic.replace("'", "").replace("],[", "_;_");
//        String matArtic[] = paramArtic.split("_;_");
//        for (int i = 0; i < matArtic.length; i++) {
//            String vecArti[] = matArtic[i].replace("[", "").replace("]", "").split(",");
//            sql.add("INSERT INTO tbl_factura_compra_activo_detalle(id_factura_compra, codigos_series, cantidad, p_u, p_st, descuento, "
//                    + "iva, total, descripcion, id_tabla_depreciacion, id_categoria, id_bodega, ubicacion, de_activo, p_iva) "
//                    + "values(" + id_factura_compra + ", '" + vecArti[0].trim() + "', " + vecArti[1].trim() + ", " + vecArti[2].trim() + ", " + vecArti[3].trim()
//                    + ", " + vecArti[4].trim() + ", " + vecArti[5].trim() + ", " + vecArti[6].trim() + ", '" + vecArti[8].trim() + "', " + vecArti[9].trim()
//                    + ", '" + vecArti[10].trim() + "', " + id_bodega + ", '" + vecArti[11].trim() + "', "
//                    + (vecArti[12].trim().compareTo("1") == 0 ? "true" : "false") + ", " + vecArti[13].trim() + ");");
//        }
//
//        return this.transacciones(sql);
//    }

    /*   DOCUMENTO DE PERSONALIZACION DE ENTREGA RECEPCION DE ACTIVOS   */
    public ResultSet getKardex(String id) {
        return this.consulta("SELECT * FROM vta_kardex_activo WHERE id_activo=" + id);
    }

    public ResultSet getPedido(String id, String usuario) {
        //return this.consulta("select * from vta_empleado where alias=(SELECT usuario FROM tbl_pedido WHERE id_pedido="+id+")");
        return this.consulta("select *,(select nombre from vta_empleado where alias='" + usuario + "') as us from vta_empleado where alias=(SELECT usuario FROM tbl_pedido WHERE id_pedido=" + id + ")");
    }

    public ResultSet getPedidoActivo(String cat, String usuario, String limite) {
        return this.consulta("select a.* from vta_activo_n a, tbl_bodega_activo ba \n"
                + "where a.id_categoria='" + cat + "' and ba.id_activo=a.id_activo \n"
                + "and ba.id_bodega=(select id_bodega from vta_bodega where id_responsable=(select id_empleado from vta_empleado where alias='" + usuario + "')) limit " + limite + "");
    }

    public ResultSet getPedidoDetalle(String id) {
        return this.consulta("select * from tbl_pedido_detalle where id_pedido=" + id + "");
    }

    public ResultSet getActivoFiltro(String txt, String id_bod, String idActs) {
        String where = "";
        if (idActs.compareTo("") != 0) {
            where = " and A.id_activo not in (" + idActs + ") ";
        }
        if (id_bod.compareTo("") != 0) {
            where = " and BA.id_bodega=" + id_bod;
        }
        txt = txt.toLowerCase();
        return this.consulta("SELECT A.id_activo, codigo_activo, descripcion, valor_compra, valor_depreciado, valor_compra, case when tiene_iva then '~' else '' end "
                + "FROM tbl_activo as A left outer join tbl_bodega_activo as BA on A.id_activo=BA.id_activo WHERE (codigo_activo='" + txt + "' or serie='" + txt + "') " + where);
    }

    public ResultSet getActivosFiltro(String txt, String idActs, int idSuc) {
        String where = "";
        if (idActs.compareTo("") != 0) {
            where += " and A.id_activo not in (" + idActs + ") ";
        }
        txt = txt.toLowerCase();
        return this.consulta("SELECT distinct A.id_activo, codigo_activo, descripcion, valor_compra, valor_depreciado, round(valor_compra-valor_depreciado, 2),tiene_iva "
                + "FROM tbl_activo as A WHERE (lower(codigo_activo) like '" + txt + "%' or lower(serie) like '" + txt + "%' or lower(descripcion) like '%" + txt + "%') "
                + "and eliminado=false  and A.id_activo not in (select id_activo from tbl_activo_personalizacion as P inner join tbl_activo_custodio_tmp as T on T.id_activo_personalizacion_tmp=P.id_activo_personalizacion where anulado=false and aceptada=false) " + where
                + " order by id_activo limit 20 offset 0");
    }

    public ResultSet getActivosFiltro(String txt, String id_bod, String idActs, int idSuc) {
        String where = "";
        if (idActs.compareTo("") != 0) {
            where += " and A.id_activo not in (" + idActs + ") ";
        }
        if (id_bod.compareTo("") != 0) {
            where += " and BA.id_bodega=" + id_bod;
        }
        txt = txt.toLowerCase();
        return this.consulta("SELECT distinct A.id_activo, codigo_activo, descripcion, valor_compra, valor_depreciado, round(valor_compra-valor_depreciado, 2), tiene_iva "
                + "FROM tbl_activo as A inner join tbl_bodega_activo as BA on A.id_activo=BA.id_activo WHERE (lower(codigo_activo) like '" + txt + "%' or lower(serie) like '" + txt + "%' or lower(descripcion) like '%" + txt + "%') "
                + "and eliminado=false and A.id_activo not in (select id_activo from tbl_activo_personalizacion as P inner join tbl_activo_custodio_tmp as T on T.id_activo_personalizacion_tmp=P.id_activo_personalizacion where anulado=false and aceptada=false) "
                + where + " order by id_activo limit 20 offset 0");
    }

    public String getIdBodegaActivo(String mac) {
        String id_bodega = "";
        try {
            ResultSet res = this.consulta("select AB.id_bodega from tbl_activo as A inner join tbl_bodega_activo as AB on A.id_activo=AB.id_activo where lower(A.codigo_activo)='" + mac.toLowerCase() + "'");
            if (res.next()) {
                id_bodega = res.getString(1) != null ? res.getString(1) : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_bodega;
    }

    public int getNunDocumento() {
        int num = 1;
        try {
            ResultSet res = this.consulta("SELECT max(num_documento)+1 FROM tbl_activo_personalizacion;");
            if (res.next()) {
                num = res.getString(1) != null ? res.getInt(1) : 1;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String ingresoCompra(String id_activos) {
        String msg = "";
        String codigos = "";
        try {
            ResultSet res = this.consulta("SELECT A.codigo_activo FROM (tbl_activo as A inner join tbl_activo_custodio as AC on A.id_activo=AC.id_activo) "
                    + "inner join tbl_activo_personalizacion as P on AC.id_activo_personalizacion=P.id_activo_personalizacion "
                    + "where AC.id_activo in (" + id_activos + ") and P.tipo_movimiento='c' and P.anulado=false and AC.eliminado=false");
            while (res.next()) {
                codigos += res.getString("codigo_activo") != null ? res.getString("codigo_activo") + ", " : "";
            }
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (codigos.compareTo("") != 0) {
            msg = "msg»Los códigos de los activos " + codigos.substring(0, codigos.length() - 2)
                    + " ya an sido registrados en formularios de personalización de compra previos";
        }
        return msg;
    }

    public ResultSet getCustodio(String dni) {
        return this.consulta("select dni, nombre || ' ' || apellido as razon_social from tbl_empleado where lower(dni)='" + dni.toLowerCase() + "'");
    }

    public ResultSet getDocumento(String id) {
        return this.consulta("SELECT * FROM vta_activo_personalizacion where id_activo_personalizacion=" + id);
    }

    public ResultSet getBodegasPersonalizacion(String razon_social) {
        return this.consulta("SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente, ubicacion from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where E.empleado like '%" + razon_social + "%' and es_responsable_cliente=false "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente, ubicacion from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where razon_social like '%" + razon_social + "%' and es_responsable_cliente=true "
                + "order by bodega limit 10 offset 0");
    }

    public ResultSet getBodegasPersonalizacion(String razon_social, String parm1) {
        return this.consulta("SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente, ubicacion from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where E.empleado like '%" + razon_social + "%' and es_responsable_cliente=false "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente, ubicacion from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where razon_social like '%" + razon_social + "%' and es_responsable_cliente=true "
                + "order by bodega limit 10 offset 0");
    }

    public ResultSet getDocumentoDetalle(String id) {
        return this.consulta("SELECT * FROM vta_activo_custodio where id_activo_personalizacion=" + id);
    }

    public ResultSet getEstanterias(String id) {
        return this.consulta("SELECT estanteria, estanteria FROM tbl_estanteria where id_bodega=" + id);
    }

    public ResultSet getDocumentoDetalleTmp(String id) {
        return this.consulta("SELECT * FROM vta_activo_custodio_tmp where id_activo_personalizacion=" + id);
    }

    public String insertarPersonalizacion(int id_sucursal, String num_documento, String tipo_movimiento, String fecha,
            String idBodEnt, String idBodRec, String bodega_entrega, String bodega_recibe, String ubicacion_entrega,
            String ubicacion_recibe, String dni_entrega, String dni_recibe,
            String persona_entrega, String persona_recibe, String observacion, String id_activos, String es_responsable_cliente) {
        String pk = "-1";
        Connection con = this.getConexion();
        try {
            idBodRec = idBodRec.compareTo("") != 0 ? idBodRec : "0";
            idBodEnt = idBodEnt.compareTo("") != 0 ? idBodEnt : "0";
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            String sql = "INSERT INTO tbl_activo_personalizacion(id_sucursal, num_documento, tipo_movimiento, fecha, id_bodega_entrega, id_bodega_recibe, bodega_entrega, bodega_recibe, ubicacion_entrega, ubicacion_recibe, dni_entrega, persona_entrega, dni_recibe, persona_recibe, observacion) "
                    + "VALUES(" + id_sucursal + ", " + num_documento + ", '" + tipo_movimiento + "', '" + fecha + "', " + idBodEnt + ", " + idBodRec + ", '" + bodega_entrega + "', '" + bodega_recibe + "', '" + ubicacion_entrega + "', '" + ubicacion_recibe + "', '" + dni_entrega + "', '" + persona_entrega + "', '" + dni_recibe + "', '" + persona_recibe + "', '" + observacion + "');";
            sql = this.decodificarURI(sql);
            int ok = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            if (ok > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
                String detalle = "";
                if (tipo_movimiento.compareTo("1") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS POR COMPRA ENTREGADO A " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("2") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS INTERPERSONAL DE " + persona_entrega + " A " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("3") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS ENTRE BODEGAS: DE " + bodega_entrega + " (" + persona_entrega + ") A " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("4") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA BODEGA " + bodega_entrega + " (" + persona_entrega + ") A LA PERSONA " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("5") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA PERSONA " + persona_entrega + " A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("6") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA BODEGA " + bodega_entrega + " (" + persona_entrega + ") AL CLIENTE " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("7") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DEL CLIENTE " + persona_entrega + " A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("11") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " EN MIGRACION DE EQUIPOS A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }

                if(id_activos.compareTo("")!=0){
                    String vecActivos[] = id_activos.split(",");
                    for (int i = 0; i < vecActivos.length; i++) {
                        //st.executeUpdate("update tbl_activo_custodio set actual=false where id_activo="+vecActivos[i]+";");
                        st.executeUpdate("insert into tbl_activo_custodio_tmp(id_activo_personalizacion_tmp, id_activo) values(" + pk + ", " + vecActivos[i] + ");");
                        /*st.executeUpdate("insert into tbl_kardex_activo(id_sucursal, id_activo, id_activo_personalizacion, fecha, detalle, id_bodega, ubicacion) "
                         + "values("+id_sucursal+", "+vecActivos[i]+", "+pk+", now(), '"+detalle+"', "+idBodRec+", '"+ubicacion_recibe+"');");
                         if(tipo_movimiento.compareTo("1")==0 || tipo_movimiento.compareTo("11")==0){ // compra
                         st.executeUpdate("insert into tbl_bodega_activo(id_bodega, id_activo, ubicacion) values("+idBodRec+", "+vecActivos[i]+", '"+ubicacion_recibe+"');");
                         }
                         if(tipo_movimiento.compareTo("2")==0){ // interpersonal
                         st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodEnt+" and id_activo="+vecActivos[i]+";");
                         st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodRec+" and id_activo="+vecActivos[i]+";");
                         }
                         if(tipo_movimiento.compareTo("3")==0 || tipo_movimiento.compareTo("5")==0 || tipo_movimiento.compareTo("7")==0){ 
                         st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodEnt+" and id_activo="+vecActivos[i]+";");
                         st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodRec+" and id_activo="+vecActivos[i]+";");
                         st.executeUpdate("insert into tbl_bodega_activo(id_bodega, id_activo, ubicacion) values("+idBodRec+", "+vecActivos[i]+", '"+ubicacion_recibe+"');");
                         }
                         if(tipo_movimiento.compareTo("4")==0 || tipo_movimiento.compareTo("6")==0){ // de bodega a persona
                         st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodEnt+" and id_activo="+vecActivos[i]+";");
                         }*/
                    }
                }

                con.commit();

                if (tipo_movimiento.compareTo("1") == 0 || tipo_movimiento.compareTo("6") == 0 || tipo_movimiento.compareTo("7") == 0
                        || tipo_movimiento.compareTo("11") == 0 || (tipo_movimiento.compareTo("3") == 0 && es_responsable_cliente.compareTo("t") == 0)) {
                    this.aceptarPersonalizacion(pk);
                }
            }

        } catch (Exception e) {
            this.setError(e.getMessage());
            pk = "-1";
            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return pk;
    }

    public String insertarPersonalizacionConGestion(int id_sucursal, String num_documento, String tipo_movimiento, String fecha,
            String idBodEnt, String idBodRec, String bodega_entrega, String bodega_recibe, String ubicacion_entrega,
            String ubicacion_recibe, String dni_entrega, String dni_recibe,
            String persona_entrega, String persona_recibe, String observacion, String id_activos, String gestion_envio, String es_responsable_cliente) {
        String pk = "-1";
        Connection con = this.getConexion();
        try {
            idBodRec = idBodRec.compareTo("") != 0 ? idBodRec : "0";
            idBodEnt = idBodEnt.compareTo("") != 0 ? idBodEnt : "0";
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            String sql = "INSERT INTO tbl_activo_personalizacion(id_sucursal, num_documento, tipo_movimiento, fecha, id_bodega_entrega, id_bodega_recibe, bodega_entrega, bodega_recibe, ubicacion_entrega, ubicacion_recibe, dni_entrega, persona_entrega, dni_recibe, persona_recibe, observacion, gestion_envio) "
                    + "VALUES(" + id_sucursal + ", " + num_documento + ", '" + tipo_movimiento + "', '" + fecha + "', " + idBodEnt + ", " + idBodRec + ", '" + bodega_entrega + "', '" + bodega_recibe + "', '" + ubicacion_entrega + "', '" + ubicacion_recibe + "', '" + dni_entrega + "', '" + persona_entrega + "', '" + dni_recibe + "', '" + persona_recibe + "', '" + observacion + "', '" + gestion_envio + "');";
            sql = this.decodificarURI(sql);
            int ok = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            if (ok > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
                String detalle = "";
                if (tipo_movimiento.compareTo("1") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS POR COMPRA ENTREGADO A " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("2") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS INTERPERSONAL DE " + persona_entrega + " A " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("3") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS ENTRE BODEGAS: DE " + bodega_entrega + " (" + persona_entrega + ") A " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("4") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA BODEGA " + bodega_entrega + " (" + persona_entrega + ") A LA PERSONA " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("5") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA PERSONA " + persona_entrega + " A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("6") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA BODEGA " + bodega_entrega + " (" + persona_entrega + ") AL CLIENTE " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("7") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DEL CLIENTE " + persona_entrega + " A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("11") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " EN MIGRACION DE EQUIPOS A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }

                String vecActivos[] = id_activos.split(",");
                for (int i = 0; i < vecActivos.length; i++) {
                    //st.executeUpdate("update tbl_activo_custodio set actual=false where id_activo="+vecActivos[i]+";");
                    st.executeUpdate("insert into tbl_activo_custodio_tmp(id_activo_personalizacion_tmp, id_activo) values(" + pk + ", " + vecActivos[i] + ");");
                    /*st.executeUpdate("insert into tbl_kardex_activo(id_sucursal, id_activo, id_activo_personalizacion, fecha, detalle, id_bodega, ubicacion) "
                     + "values("+id_sucursal+", "+vecActivos[i]+", "+pk+", now(), '"+detalle+"', "+idBodRec+", '"+ubicacion_recibe+"');");
                     if(tipo_movimiento.compareTo("1")==0 || tipo_movimiento.compareTo("11")==0){ // compra
                     st.executeUpdate("insert into tbl_bodega_activo(id_bodega, id_activo, ubicacion) values("+idBodRec+", "+vecActivos[i]+", '"+ubicacion_recibe+"');");
                     }
                     if(tipo_movimiento.compareTo("2")==0){ // interpersonal
                     st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodEnt+" and id_activo="+vecActivos[i]+";");
                     st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodRec+" and id_activo="+vecActivos[i]+";");
                     }
                     if(tipo_movimiento.compareTo("3")==0 || tipo_movimiento.compareTo("5")==0 || tipo_movimiento.compareTo("7")==0){ 
                     st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodEnt+" and id_activo="+vecActivos[i]+";");
                     st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodRec+" and id_activo="+vecActivos[i]+";");
                     st.executeUpdate("insert into tbl_bodega_activo(id_bodega, id_activo, ubicacion) values("+idBodRec+", "+vecActivos[i]+", '"+ubicacion_recibe+"');");
                     }
                     if(tipo_movimiento.compareTo("4")==0 || tipo_movimiento.compareTo("6")==0){ // de bodega a persona
                     st.executeUpdate("delete from tbl_bodega_activo where id_bodega="+idBodEnt+" and id_activo="+vecActivos[i]+";");
                     }*/
                }

                con.commit();

                if (tipo_movimiento.compareTo("1") == 0 || tipo_movimiento.compareTo("6") == 0 || tipo_movimiento.compareTo("7") == 0
                        || tipo_movimiento.compareTo("11") == 0 || (tipo_movimiento.compareTo("3") == 0 && es_responsable_cliente.compareTo("t") == 0)) {
                    this.aceptarPersonalizacion(pk);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            pk = "-1";
            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return pk;
    }

    public boolean aceptarPersonalizacion(String id) {
        try {
            ResultSet rsPer = this.consulta("select * from tbl_activo_personalizacion where id_activo_personalizacion=" + id);
            if (rsPer.next()) {
                String id_sucursal = rsPer.getString("id_sucursal") != null ? rsPer.getString("id_sucursal") : "0";
                String num_documento = rsPer.getString("num_documento") != null ? rsPer.getString("num_documento") : "0";
                String idBodEnt = rsPer.getString("id_bodega_entrega") != null ? rsPer.getString("id_bodega_entrega") : "";
                String bodega_entrega = rsPer.getString("bodega_entrega") != null ? rsPer.getString("bodega_entrega") : "";
                String persona_entrega = rsPer.getString("persona_entrega") != null ? rsPer.getString("persona_entrega") : "";
                String idBodRec = rsPer.getString("id_bodega_recibe") != null ? rsPer.getString("id_bodega_recibe") : "";
                String bodega_recibe = rsPer.getString("bodega_recibe") != null ? rsPer.getString("bodega_recibe") : "";
                String persona_recibe = rsPer.getString("persona_recibe") != null ? rsPer.getString("persona_recibe") : "";
                String ubicacion_recibe = rsPer.getString("ubicacion_recibe") != null ? rsPer.getString("ubicacion_recibe") : "";
                String tipo_movimiento = rsPer.getString("tipo_movimiento") != null ? rsPer.getString("tipo_movimiento") : "1";
                String detalle = "";
                if (tipo_movimiento.compareTo("1") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS POR COMPRA ENTREGADO A " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("2") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS INTERPERSONAL DE " + persona_entrega + " A " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("3") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE ACTIVOS ENTRE BODEGAS: DE " + bodega_entrega + " (" + persona_entrega + ") A " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("4") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA BODEGA " + bodega_entrega + " (" + persona_entrega + ") A LA PERSONA " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("5") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA PERSONA " + persona_entrega + " A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("6") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DE LA BODEGA " + bodega_entrega + " (" + persona_entrega + ") AL CLIENTE " + persona_recibe;
                }
                if (tipo_movimiento.compareTo("7") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " DEL CLIENTE " + persona_entrega + " A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }
                if (tipo_movimiento.compareTo("11") == 0) {
                    detalle = "PERSONALIZACION Nro. " + num_documento + " EN MIGRACION DE EQUIPOS A LA BODEGA " + bodega_recibe + " (" + persona_recibe + ")";
                }

                List sql = new ArrayList();

                sql.add("update tbl_activo_personalizacion set aceptada=true where id_activo_personalizacion=" + id + ";");

                ResultSet rsCusTmp = this.consulta("select * from tbl_activo_custodio_tmp where id_activo_personalizacion_tmp=" + id);
                String id_activo;
                while (rsCusTmp.next()) {
                    id_activo = rsCusTmp.getString("id_activo") != null ? rsCusTmp.getString("id_activo") : "";
                    sql.add("update tbl_activo_custodio set actual=false where id_activo=" + id_activo + ";");
                    sql.add("insert into tbl_activo_custodio(id_activo_personalizacion, id_activo) values(" + id + ", " + id_activo + ");");
                    sql.add("insert into tbl_kardex_activo(id_sucursal, id_activo, id_activo_personalizacion, fecha, detalle, id_bodega, ubicacion) "
                            + "values(" + id_sucursal + ", " + id_activo + ", " + id + ", now(), '" + detalle + "', " + idBodRec + ", '" + ubicacion_recibe + "');");
                    if (tipo_movimiento.compareTo("1") == 0 || tipo_movimiento.compareTo("11") == 0) { // compra
                        sql.add("delete from tbl_bodega_activo where id_activo=" + id_activo + ";");
                        sql.add("insert into tbl_bodega_activo(id_bodega, id_activo, ubicacion) values(" + idBodRec + ", " + id_activo + ", '" + ubicacion_recibe + "');");
                    }
                    if (tipo_movimiento.compareTo("2") == 0) { // interpersonal
                        sql.add("delete from tbl_bodega_activo where id_activo=" + id_activo + ";");
                        //sql.add("delete from tbl_bodega_activo where id_bodega="+idBodRec+" and id_activo="+id_activo+";");
                    }
                    if (tipo_movimiento.compareTo("3") == 0 || tipo_movimiento.compareTo("5") == 0 || tipo_movimiento.compareTo("7") == 0) {
                        sql.add("delete from tbl_bodega_activo where id_activo=" + id_activo + ";");
                        sql.add("insert into tbl_bodega_activo(id_bodega, id_activo, ubicacion) values(" + idBodRec + ", " + id_activo + ", '" + ubicacion_recibe + "');");
                    }
                    if (tipo_movimiento.compareTo("4") == 0 || tipo_movimiento.compareTo("6") == 0) { // de bodega a persona
                        sql.add("delete from tbl_bodega_activo where id_activo=" + id_activo + ";");
                    }
                }

                try {
                    rsPer.close();
                    rsCusTmp.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return this.transacciones(sql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean anularPersonalizacion(String id) {
        try {
            ResultSet rsPer = this.consulta("select * from tbl_activo_personalizacion where id_activo_personalizacion=" + id);
            if (rsPer.next()) {
                List sql = new ArrayList();
                sql.add("update tbl_activo_personalizacion set aceptada=false, anulado=true where id_activo_personalizacion=" + id + ";");

                try {
                    rsPer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return this.transacciones(sql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getPersonalizacionFact(String idFactura) {
        return this.consulta("SELECT * FROM tbl_activo_personalizacion where id_factura_compra=" + idFactura);
    }

    public boolean actualizarPersonalizacion(String id, int id_sucursal, String num_documento, String tipo_movimiento, String fecha, String dni_entrega, String dni_recibe,
            String persona_entrega, String persona_recibe, String observacion, String id_activos) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_activo_personalizacion SET tipo_movimiento='" + tipo_movimiento + "', fecha='" + fecha + "', dni_entrega='" + dni_entrega + "', persona_entrega='" + persona_entrega
                + "', dni_recibe='" + dni_recibe + "', persona_recibe='" + persona_recibe + "', observacion='" + observacion
                + "' WHERE id_activo_personalizacion=" + id);

        sql.add("DELETE FROM tbl_activo_custodio WHERE id_activo_personalizacion=" + id);
        String vecActivos[] = id_activos.split(",");
        for (int i = 0; i < vecActivos.length; i++) {
            sql.add("update tbl_activo_custodio set actual=false where id_activo=" + vecActivos[i]);
            sql.add("insert into tbl_activo_custodio(id_activo_personalizacion, id_activo) values(" + id + ", " + vecActivos[i] + ");");
        }
        boolean ok = this.transacciones(sql);
        if (ok) {
            try {
                ResultSet rsActivos = this.consulta("select C.id_activo_personalizacion, C.id_activo, P.fecha from tbl_activo_custodio as C inner join "
                        + "tbl_activo_personalizacion as P on C.id_activo_personalizacion=P.id_activo_personalizacion where C.eliminado=false "
                        + "and C.id_activo_personalizacion=" + id + " order by C.id_activo, P.fecha desc");
                String aux = "";
                String id_activo = "";
                String id_activo_personalizacion = "";
                while (rsActivos.next()) {
                    id_activo = rsActivos.getString("id_activo") != null ? rsActivos.getString("id_activo") : "-1";
                    if (id_activo.compareTo(aux) != 0) {
                        id_activo_personalizacion = rsActivos.getString("id_activo_personalizacion") != null ? rsActivos.getString("id_activo_personalizacion") : "-1";
                        this.ejecutar("update tbl_activo_custodio set actual=true where id_activo_personalizacion=" + id_activo_personalizacion + " and id_activo=" + id_activo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ok;
    }

    /*public String anularPersonalizacion(String id)
     {
     int id_sucursal = -1;
     String num_documento = String.valueOf(this.getNunDocumento());
     String id_bodega_entrega = "";
     String dni_entrega = "";
     String persona_entrega = "";
     String id_bodega_recibe = "";
     String dni_recibe = "";
     String persona_recibe = "";
     String fecha = Fecha.getFecha("ISO");
     String observacion = "";
     String bodega_entrega = "";
     String bodega_recibe = "";
     String ubicacion_entrega = "";
     String ubicacion_recibe = "";
     String tipo_movimiento = "";
     String id_activos = "";
     try{
     ResultSet rs = this.consulta("select * from tbl_activo_personalizacion where id_activo_personalizacion="+id);
     if(rs.next()){
     id_sucursal = rs.getString("id_sucursal")!=null ? rs.getInt("id_sucursal") : -1;
     id_bodega_entrega = rs.getString("id_bodega_entrega")!=null ? rs.getString("id_bodega_entrega") : "";
     dni_entrega = rs.getString("dni_entrega")!=null ? rs.getString("dni_entrega") : "";
     persona_entrega = rs.getString("persona_entrega")!=null ? rs.getString("persona_entrega") : "";
     id_bodega_recibe = rs.getString("id_bodega_recibe")!=null ? rs.getString("id_bodega_recibe") : "";
     dni_recibe = rs.getString("dni_recibe")!=null ? rs.getString("dni_recibe") : "";
     persona_recibe = rs.getString("persona_recibe")!=null ? rs.getString("persona_recibe") : "";
     observacion = rs.getString("observacion")!=null ? rs.getString("observacion") : "";
     bodega_entrega = rs.getString("bodega_entrega")!=null ? rs.getString("bodega_entrega") : "";
     bodega_recibe = rs.getString("bodega_recibe")!=null ? rs.getString("bodega_recibe") : "";
     ubicacion_entrega = rs.getString("ubicacion_entrega")!=null ? rs.getString("ubicacion_entrega") : "";
     ubicacion_recibe = rs.getString("ubicacion_recibe")!=null ? rs.getString("ubicacion_recibe") : "";
     tipo_movimiento = rs.getString("tipo_movimiento")!=null ? rs.getString("tipo_movimiento") : "";
     rs.close();
     }
            
     ResultSet rsCustodios = this.consulta("select * from tbl_activo_custodio where id_activo_personalizacion="+id);
     while(rsCustodios.next()){
     id_activos += rsCustodios.getString("id_activo")!=null ? rsCustodios.getString("id_activo") + "," : "";
     }
     if(id_activos.compareTo("")!=0){
     id_activos = id_activos.substring(0, id_activos.length()-1);
     rs.close();
     }
            
     }catch(Exception e){
     e.printStackTrace();
     }
        
     return this.insertarPersonalizacion(id_sucursal, num_documento, tipo_movimiento, fecha, id_bodega_recibe, id_bodega_entrega, 
     bodega_recibe, bodega_entrega, ubicacion_recibe, ubicacion_entrega, dni_recibe, dni_entrega, persona_recibe, 
     persona_entrega, observacion, id_activos, "f");*/

    /*List sql = new ArrayList();
     sql.add("update tbl_activo_custodio set eliminado=true where id_activo_personalizacion="+id);
     sql.add("update tbl_activo_personalizacion set anulado=true where id_activo_personalizacion="+id);
     boolean ok = this.transacciones(sql);
     if(ok){
     try{
     ResultSet rsActivos = this.consulta("select C.id_activo_personalizacion, C.id_activo, P.fecha from tbl_activo_custodio as C inner join "
     + "tbl_activo_personalizacion as P on C.id_activo_personalizacion=P.id_activo_personalizacion where C.eliminado=false and "
     + "C.id_activo in (select id_activo from tbl_activo_custodio where id_activo_personalizacion="+id+") order by C.id_activo, P.fecha desc");
     String aux = "";
     String id_activo = "";
     String id_activo_personalizacion = "";
     while(rsActivos.next()){
     id_activo = rsActivos.getString("id_activo")!=null ? rsActivos.getString("id_activo") : "-1";
     if(id_activo.compareTo(aux)!=0){
     id_activo_personalizacion = rsActivos.getString("id_activo_personalizacion")!=null ? rsActivos.getString("id_activo_personalizacion") : "-1";
     this.ejecutar("update tbl_activo_custodio set actual=true where id_activo_personalizacion="+id_activo_personalizacion+" and id_activo="+id_activo);
     aux = id_activo;
     }
     }
     }catch(Exception e){
     e.printStackTrace();
     }
     }
     return ok;*/
    //}
    /*   KARDEX   */
    public String getUltimaUbicacion(String id_activo) {
        String num = "";
        try {
            ResultSet res = this.consulta("SELECT ubicacion FROM tbl_kardex_activo where id_activo=(select max(id_activo) FROM tbl_kardex_activo where id_activo=" + id_activo + ")");
            if (res.next()) {
                num = res.getString(1) != null ? res.getString(1) : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /* venta de activos  */
    public String getClaveAcceso(String id_factura_venta) {
        String num = "0";
        try {
            ResultSet res = this.consulta("SELECT clave_acceso FROM tbl_factura_venta WHERE id_factura_venta=" + id_factura_venta);
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "0";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String concatenarValores(String id_articulos, String descripciones, String cantidades, String precios_costo, String precios_unitarios,
            String subtotales, String ivas, String totales, String pIvas, String codigoIvas) {
        String param = "";
        String vecArti[] = id_articulos.split(",");
        String vecCant[] = cantidades.split(",");
        String vecPC[] = precios_costo.split(",");
        String vecPU[] = precios_unitarios.split(",");
        String vecSubt[] = subtotales.split(",");
        String vecIva[] = ivas.split(",");
        String vecTot[] = totales.split(",");
        String vecDescrip[] = descripciones.split(",");
        String vecPIva[] = pIvas.split(",");
        String vecCIva[] = codigoIvas.split(",");
        for (int i = 0; i < vecArti.length; i++) {
            param += "['" + vecArti[i] + "','" + vecCant[i] + "','" + vecPU[i] + "','" + vecSubt[i] + "','" + vecIva[i] + "','" + vecTot[i] + "','" + vecDescrip[i] + "','" + vecPC[i] + "','" + vecPIva[i] + "','" + vecCIva[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String insertar(int id_sucursal, int id_punto_emision, String vendedor, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco,
            String num_cheque, String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion,
            String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String subtotal_6, String iva_2, String iva_3, String total, String id_productos,
            String descripciones, String cantidades, String precios_costo, String precios_unitarios, String subtotales, String ivas, String totales,
            String pIvas, String codigoIvas,
            String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes,
            String ret_ejercicio_fiscal, String ret_impuesto_retenido, String codBI, String id_retenciones, String bases_imponibles,
            String valores_retenidos, String xmlFirmado) {
        String num = "-1:-1";
        try {
            ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
            String paramProductos = this.concatenarValores(id_productos, descripciones, cantidades, precios_costo, precios_unitarios, subtotales, ivas, totales, pIvas, codigoIvas);
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos, codBI);
            ResultSet res = this.consulta("select facturaVentaActivo(" + id_sucursal + ", " + id_punto_emision + ", '" + vendedor + "', '" + serie_factura
                    + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                    + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                    + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + subtotal_6
                    + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramProductos + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion
                    + "', " + ret_fecha_emision + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRetencion + ", '" + xmlFirmado + "');"); // 38 param
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /*public String actualizar(String id, int id_sucursal, int id_punto_emision, String vendedor, String serie_factura, String num_factura, String autorizacion,
     String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String forma_pago, String banco,
     String num_cheque, String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion,
     String subtotal, String subtotal_0, String  subtotal_2, String  subtotal_6, String iva_12, String total, String id_productos, 
     String descripciones, String cantidades, String precios_costo, String precios_unitarios, String subtotales, String ivas, String totales,
     String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes,
     String ret_ejercicio_fiscal, String ret_impuesto_retenido, String codBI, String id_retenciones, String bases_imponibles, 
     String valores_retenidos, String xmlFirmado)
     {
     String num = "-1";
     try{
     ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
     String paramProductos = this.concatenarValores(id_productos, descripciones, cantidades, precios_costo, precios_unitarios, subtotales, ivas, totales);
     String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos, codBI);
     ResultSet res = this.consulta("select facturaVentaActivoActualizar("+id+", "+id_sucursal+", "+id_punto_emision+", '"+vendedor+"', '"+serie_factura+
     "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', '"+fecha_emision+"', '"+direccion+
     "', '"+telefono+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
     ", "+id_plan_cuenta_banco+", '"+son+"', '"+observacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_6+
     ", "+iva_12+", "+total+", "+paramProductos+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+
     "', "+ret_fecha_emision+", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRetencion+", '"+xmlFirmado+"');");
     if(res.next()){
     num = (res.getString(1)!=null) ? res.getString(1) : "-1";
     res.close();
     }
     }catch(Exception e){
     e.printStackTrace();
     }
     return num;
     }*/
    public String anular(String idFactura) {
        String id_personalizacion = "-1";
        try {
            ResultSet res = this.consulta("select proc_anularFacturaVentaActivo(" + idFactura + ");");
            if (res.next()) {
                id_personalizacion = (res.getString(1) != null) ? res.getString(1) : "-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_personalizacion;
    }

    public String anular(String idFactura, String por_edicion) {
        String id_personalizacion = "-1";
        try {
            ResultSet res = this.consulta("select proc_anularFacturaVentaActivo(" + idFactura + ", " + por_edicion + ");");
            if (res.next()) {
                id_personalizacion = (res.getString(1) != null) ? res.getString(1) : "-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_personalizacion;
    }


    /*  VAJA DE ACTIVOS */
    public ResultSet getVajaActivo(String id_activo_perdida) {
        return this.consulta("select A.*, P.usuario, P.fecha_registro, P.valor_perdida, P.motivo, P.contabilizado "
                + "from tbl_activo as A inner join tbl_activo_perdida as P on A.id_activo=P.id_activo where P.id_activo_perdida=" + id_activo_perdida);
    }

    public boolean darVaja(int id_sucursal, String id_activo, String usuario, String valor_perdida, String motivo) {
        String id_activo_perdida = this.insert("INSERT INTO tbl_activo_perdida(id_activo,usuario,valor_perdida,motivo) VALUES(" + id_activo + ", '" + usuario + "', " + valor_perdida + ", '" + motivo + "');");
        if (id_activo_perdida.compareTo("-1") != 0) {
            String id_bodega = "-1";
            try {
                ResultSet rs = this.consulta("select id_bodega from tbl_bodega_activo where id_activo=" + id_activo);
                if (rs.next()) {
                    id_bodega = rs.getString("id_bodega") != null ? rs.getString("id_bodega") : "-1";
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            List sql = new ArrayList();

            sql.add("insert into tbl_kardex_activo(id_sucursal, id_activo, id_activo_personalizacion, fecha, detalle, id_bodega, ubicacion) "
                    + "values(" + id_sucursal + ", " + id_activo + ", " + id_activo_perdida + ", now(), 'VAJA DE ACTIVO', " + id_bodega + ", '');");

            sql.add("update tbl_activo set id_factura_venta=" + id_activo_perdida + ", eliminado=true where id_activo=" + id_activo + ";");
            sql.add("update tbl_activo_custodio set actual=false where id_activo=" + id_activo + ";");
            sql.add("insert into tbl_activo_custodio(id_activo_personalizacion,id_activo,actual) values(" + id_activo_perdida + "," + id_activo + ",true);");
            if (!this.transacciones(sql)) {
                this.ejecutar("DELETE FROM tbl_activo_perdida WHERE id_activo_perdida=" + id_activo_perdida + ";");
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean anularVaja(String id_activo_perdida) {
        String id_activo = "";
        try {
            ResultSet rs = this.consulta("select id_activo from tbl_activo_perdida where id_activo_perdida=" + id_activo_perdida);
            if (rs.next()) {
                id_activo = rs.getString("id_activo") != null ? rs.getString("id_activo") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List sql = new ArrayList();
        sql.add("DELETE FROM tbl_activo_perdida WHERE id_activo_perdida=" + id_activo_perdida + ";");
        sql.add("DELETE FROM tbl_activo_custodio WHERE id_activo_personalizacion=(select max(id_activo_personalizacion) from tbl_activo_custodio where id_activo=" + id_activo + ");");
        sql.add("DELETE FROM tbl_kardex_activo WHERE id_kardex_activo=(select max(id_kardex_activo) from tbl_kardex_activo where id_activo=" + id_activo + ");");
        sql.add("update tbl_activo set id_factura_venta=null, eliminado=false where id_activo=" + id_activo + ";");
        sql.add("update tbl_activo_custodio set actual=true where id_activo_personalizacion=(select max(id_activo_personalizacion) from tbl_activo_custodio where id_activo=" + id_activo + ");");

        return this.transacciones(sql);
    }

    /*BODEGAS */
    public ResultSet getComboCat() {
        return this.consulta("SELECT id_cat_activo,cod_categorizacion,nombre_categorizacion FROM tbl_cat_activos WHERE eliminado=false order by cod_categorizacion;");
    }

    public ResultSet getComboPrincipal() {
        return this.consulta("SELECT id_cat_activo,cod_categorizacion,nombre_categorizacion FROM tbl_cat_activos \n"
                + "where id_cat_padre=(select id_cat_activo from tbl_cat_activos where UPPER(nombre_categorizacion) like '%GENERAL%');");
    }

    public ResultSet consultarCat(String where) {
        return this.consulta("SELECT id_cat_activo,cod_categorizacion,nombre_categorizacion, txt_tipo_categorizacion FROM vta_categorizacion_activo where " + where + " order by cod_categorizacion");
    }

    public ResultSet getDepresiacion() {
        return this.consulta("SELECT id_tabla_depreciacion,depreciacion FROM tbl_tabla_depreciacion WHERE eliminado=false order by depreciacion;");
    }

    public ResultSet getCategoria(String id) {
        return this.consulta("SELECT * FROM tbl_cat_activos where id_cat_activo=" + id + ";");
    }

    public ResultSet verificarCategoria(String id) {
        return this.consulta("SELECT * FROM tbl_cat_activos where id_cat_padre=" + id + " and eliminado=false;");
    }

    public ResultSet verificarActivos(String id) {
        return this.consulta("select * from tbl_activo where id_categoria in ('" + id + "');");
    }

    public ResultSet verificarActivosCategoria(String id) {
        return this.consulta("select * from tbl_activo where id_categoria in (select id_cat_activo::text from vta_categorizacion_activo  where id_cat_padre = '" + id + "');");
    }

    public boolean estaDuplicado(String id, String cod, String nom) {
        ResultSet res = this.consulta("SELECT * FROM tbl_cat_activos where (cod_categorizacion='" + cod + "' or nombre_categorizacion='" + nom + "') and id_cat_activo<>" + id + ";");
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

    public boolean insertarCategoria(String id_padre, String codigo_categorizacion, String nombre_categorizacion, String tipo, String depresiacion) {
        return this.ejecutar("insert into tbl_cat_activos (id_cat_padre, cod_categorizacion, nombre_categorizacion, tipo_categorizacion, fecha_creacion, fecha_modificacion, id_depreciacion, eliminado)\n"
                + "values (" + id_padre + ", '" + codigo_categorizacion + "' ,'" + nombre_categorizacion + "', " + tipo + ", date 'now()', date 'now()', " + depresiacion + ", false);");
    }

    public boolean actualizarCategoria(String id, String id_padre, String codigo_categorizacion, String nombre_categorizacion, String tipo, String depresiacion) {
        return this.ejecutar("update tbl_cat_activos set id_cat_padre=" + id_padre + ", cod_categorizacion='" + codigo_categorizacion + "', nombre_categorizacion='" + nombre_categorizacion + "', "
                + "tipo_categorizacion=" + tipo + ", fecha_modificacion=date 'now()', id_depreciacion=" + depresiacion + " where id_cat_activo=" + id + ";");
    }

    public boolean eliminarCategoria(String id) {
        return this.ejecutar("update tbl_cat_activos set eliminado=true where id_cat_activo in (" + id + ");");
    }

    /*TIPOS DE BODEGAS*/
    public ResultSet getNombreTipoBodega() {
        return this.consulta("SELECT id_bodega_tipo, nombre_bodega FROM tbl_bodega_tipo order by nombre_bodega;");
    }

    public ResultSet tipoBodega(String id) {
        return this.consulta("SELECT * FROM tbl_bodega_tipo WHERE id_bodega_tipo=" + id + " order by nombre_bodega;");
    }

    public ResultSet tipoBodegaActivo(String id) {
        return this.consulta("SELECT * FROM vta_bodega_tipo_activo WHERE id_bodega_tipo=" + id + " order by nombre_bodega;");
    }

    public boolean estaDuplicadoActivo(String id, String rubro) {
        ResultSet res = this.consulta("SELECT * FROM tbl_bodega_tipo where lower(nombre_bodega)='" + rubro.toLowerCase() + "' and id_bodega_tipo<>" + id);
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

    public String insertar(String nombre_bodega, String tipo, List activo, List min, List max) {
        String pk = this.insert("INSERT INTO tbl_bodega_tipo(nombre_bodega, fecha_creacion, tipo) "
                + "VALUES(upper('" + nombre_bodega + "'), date 'now()', " + tipo + ");");
        Iterator itActivo = activo.iterator();
        Iterator itMin = min.iterator();
        Iterator itMax = max.iterator();
        while (itActivo.hasNext()) {
            this.ejecutar("insert into tbl_bodega_tipo_activo(id_bodega_tipo, id_cat_activo, min, max) values("
                    + pk + ", " + (String) itActivo.next() + ", " + (String) itMin.next() + ", " + (String) itMax.next() + ")");
        }
        return pk;
    }

    public boolean actualizar(String id, String nombre_bodega, String tipo, List activo, List min, List max) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_bodega_tipo SET nombre_bodega=upper('" + nombre_bodega + "'), "
                + "tipo=" + tipo + " WHERE id_bodega_tipo=" + id);
        Iterator itActivo = activo.iterator();
        Iterator itMin = min.iterator();
        Iterator itMax = max.iterator();
        sql.add("delete from tbl_bodega_tipo_activo where id_bodega_tipo=" + id);
        while (itActivo.hasNext()) {
            sql.add("insert into tbl_bodega_tipo_activo(id_bodega_tipo, id_cat_activo, min, max) values("
                    + id + ", " + (String) itActivo.next() + ", " + (String) itMin.next() + ", " + (String) itMax.next() + ")");
        }
        return this.transacciones(sql);
    }

    /*ORGANIZAR ACTIVOS*/
    public ResultSet activosNuevos(String id) {
        return this.consulta("SELECT * FROM vta_activo_n WHERE id_categoria='" + id + "' order by descripcion;");
    }

    public boolean activosNuevosGuardar(String id_padre, String ids) {
        return this.ejecutar("update tbl_activo set id_categoria=" + id_padre + ", clasificado=true where id_activo in (" + ids + ");");
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

    public ResultSet agregarRubroprefactura(int id_sucursal, String id_instalacion, String codigoslista, String cantidadeslista, String descripcionlista, String periodo, String trubro, String cantidadpro) {
        String codigos[] = codigoslista.split(",");
        String cantidades[] = cantidadeslista.split(",");
        String descripciones[] = descripcionlista.split(",");
        String tiposrubos[] = trubro.split(",");
        String cantidadespro[] = cantidadpro.split(",");
        Connection con = this.getConexion();
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            for (int i = 0; i < codigos.length; i++) {
                st.executeUpdate("INSERT INTO tbl_prefactura_rubro( id_sucursal, id_instalacion, rubro,periodo, monto,tiporubro,idproductos,canproductos)VALUES ('" + id_sucursal + "', '" + id_instalacion + "', '" + descripciones[i] + "','" + periodo + "', '" + cantidades[i] + "', '" + tiposrubos[i] + "', '" + codigos[i] + "', '" + cantidadespro[i] + "');");
            }
            st.executeUpdate("update tbl_prefactura set recalcular=true where fecha_emision is null and id_instalacion="+id_instalacion);
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return this.consulta("");
    }

    ///revalorizacion
    public ResultSet getActivorevalorizar(long id) {
        return this.consulta("SELECT * FROM tbl_activo a where  a.id_activo=" + id);
    }

    public ResultSet getActivorevalorizar(int sucursal, long id) {
        return this.consulta("SELECT * FROM tbl_activo a where  a.id_activo=" + id);
    }

    public ResultSet getActivorevalorizar() {
        return this.consulta("select distinct a.codigo_activo as unicocod, a.*,(select fecha_compra from  tbl_revalorizarac where id_activo=a.id_activo order by id_revalorizarac desc limit 1)as fecha_coomprarev,\n"
                + "(select valor_revalorizado from  tbl_revalorizarac where id_activo=a.id_activo order by id_revalorizarac desc limit 1) from \n"
                + "(tbl_bodega as b inner join tbl_bodega_activo as ab on b.id_bodega=ab.id_bodega) right outer join tbl_activo as a on a.id_activo=ab.id_activo\n"
                + "where a.eliminado=false and a.valor_compra=a.valor_depreciado");
    }

    public ResultSet getActivorevalorizar(String depresiacion, String categoria, String sucursal) {
        String where = "";
        if (depresiacion.compareTo("") != 0) {
            where += " and a.id_tabla_depreciacion='" + depresiacion + "'";
        }
        if (categoria.compareTo("") != 0 && categoria.compareTo("-0") != 0) {
            where += " and a.id_categoria='" + categoria + "'";
        }
        if (sucursal.compareTo("") != 0 && sucursal.compareTo("-0") != 0) {
            where += " and a.id_sucursal='" + sucursal + "'";
        }
        System.out.println("where" + where);
        return this.consulta("select a.codigo_activo,a.id_activo,a.id_tabla_depreciacion,a.descripcion,a.id_sucursal,a.id_categoria,a.fecha_compra,a.valor_compra,a.valor_depreciado,\n"
                + "b.fecha_compra,b.valor_revalorizado,b.costocompra,b.costodepresiado\n"
                + "from (tbl_activo a left join tbl_revalorizarac b on a.id_activo=b.id_activo) where a.eliminado=false and a.valor_compra=a.valor_depreciado  \n"
                + "and (b.fecha_compra=(select max(fecha_compra) from tbl_revalorizarac where id_activo=a.id_activo)or b.fecha_compra is null) and (b.costocompra=b.costodepresiado or b.costocompra is null) " + where + " limit 5000");
    }

    public ResultSet getActivorevalorizar(String bodega, String depresiacion, String categoria, String sucursal) {
        String where = "";
        if (depresiacion.compareTo("") != 0) {
            where += " and a.id_tabla_depreciacion='" + depresiacion + "'";
        }
        if (categoria.compareTo("") != 0 && categoria.compareTo("-0") != 0) {
            where += " and a.id_categoria='" + categoria + "'";
        }
        if (sucursal.compareTo("") != 0 && sucursal.compareTo("-0") != 0) {
            where += " and a.id_sucursal='" + sucursal + "'";
        }
        System.out.println("where" + where);
        return this.consulta("select a.codigo_activo,a.id_activo,a.id_tabla_depreciacion,a.descripcion,a.id_sucursal,a.id_categoria,a.fecha_compra,a.valor_compra,a.valor_depreciado,\n"
                + "b.fecha_compra,b.valor_revalorizado,b.costocompra,b.costodepresiado\n"
                + "from (tbl_activo a left join tbl_revalorizarac b on a.id_activo=b.id_activo) where a.eliminado=false and a.valor_compra=a.valor_depreciado  \n"
                + "and (b.fecha_compra=(select max(fecha_compra) from tbl_revalorizarac where id_activo=a.id_activo)or b.fecha_compra is null) and (b.costocompra=b.costodepresiado or b.costocompra is null) " + where + " limit 5000");
    }

    public ResultSet getActivobtlrevalorizar() {
        return this.consulta("select * from tbl_revalorizarac");
    }

    public String concatenarValoresrevalorizacion(String idactivos, String valoresrevalorizados, String anios, String tasadepresiacion, String depresiacionanual, String costocompra,
            String costodepresiado, String iddepresiacion, String fechacompra, String fechacompraa, String valorcompraa, String valordepresaidoa, String debe, String haber, String depmesual) {
        String param = "";
        String vecidactivos[] = idactivos.split(",");
        String vecrevalorizados[] = valoresrevalorizados.split(",");
        String vecanios[] = anios.split(",");
        String vectasadepresiacion[] = tasadepresiacion.split(",");
        String vecdepresiacionanual[] = depresiacionanual.split(",");
        String vecostocompra[] = costocompra.split(",");
        String veccostodepresiado[] = costodepresiado.split(",");
        String veciddepresiacion[] = iddepresiacion.split(",");
        String vecfechacompra[] = fechacompra.split(",");
        String vecfechacompraa[] = fechacompraa.split(",");
        String vecvalorcompraa[] = valorcompraa.split(",");
        String vecvalordepresaidoa[] = valordepresaidoa.split(",");
        String vecdebe[] = debe.split(",");
        String vechaber[] = haber.split(",");
        String vecdepmensual[] = depmesual.split(",");
        for (int i = 0; i < vecidactivos.length; i++) {
            param += "['" + vecidactivos[i] + "','" + vecrevalorizados[i] + "','" + vecanios[i] + "','" + vectasadepresiacion[i] + "','" + vecdepresiacionanual[i] + "','" + vecostocompra[i] + "','" + veccostodepresiado[i] + "','" + veciddepresiacion[i] + "','" + vecfechacompra[i] + "','" + vecfechacompraa[i] + "','" + vecvalorcompraa[i] + "','" + vecvalordepresaidoa[i] + "','" + vecdebe[i] + "','" + vechaber[i] + "','" + vecdepmensual[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public int insertarrevalorizacion(String idactivos, String valoresrevalorizados, String anios, String tasadepresiacion, String depresiacionanual, String costocompra,
            String costodepresiado, String iddepresiacion, String fechacompra, String fechacompraa, String valorcompraa, String valordepresaidoa, String debe, String haber, String depmesual) {
        int num = -1;
        try {
            String param = this.concatenarValoresrevalorizacion(idactivos, valoresrevalorizados, anios, tasadepresiacion, depresiacionanual, costocompra, costodepresiado, iddepresiacion, fechacompra, fechacompraa, valorcompraa, valordepresaidoa, debe, haber, depmesual);
            ResultSet res = this.consulta("select proc_guardarevalorizacion(" + param + ");");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : -1;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String asientocontable(int idsucursal, String fechatraspaso, String observacion, String totales, String parametros) {
        String num = "-1:-1:-1";
        try {
            ResultSet res = this.consulta("select setComprobanteDiario(" + idsucursal + ",'" + fechatraspaso + "', '" + observacion + "'," + totales + "," + parametros + ");");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1:-1:-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public ResultSet getnumeroActivos(String id_bod) {
        return this.consulta("SELECT count(id_categoria),id_categoria FROM tbl_activo as A inner join tbl_bodega_activo as BA on A.id_activo=BA.id_activo WHERE (lower(codigo_activo) like '%' or lower(serie) like '%' or lower(descripcion) like '%%') \n"
                + "and eliminado=false and A.id_activo not in (select id_activo from tbl_activo_personalizacion as P inner join tbl_activo_custodio_tmp as T on T.id_activo_personalizacion_tmp=P.id_activo_personalizacion where anulado=false and aceptada=false) \n"
                + "and BA.id_bodega='" + id_bod + "' group by id_categoria");
    }

    public ResultSet getnumeroActivos(String id_bod, String categoria) {
        return this.consulta("SELECT count(id_categoria),id_categoria FROM tbl_activo as A inner join tbl_bodega_activo as BA on A.id_activo=BA.id_activo WHERE (lower(codigo_activo) like '%' or lower(serie) like '%' or lower(descripcion) like '%%') \n"
                + "and eliminado=false and A.id_activo not in (select id_activo from tbl_activo_personalizacion as P inner join tbl_activo_custodio_tmp as T on T.id_activo_personalizacion_tmp=P.id_activo_personalizacion where anulado=false and aceptada=false) \n"
                + "and BA.id_bodega='" + id_bod + "' and A.id_categoria='" + categoria + "' group by id_categoria");
    }

    public int getnumeroActivos(String id_bod, String categoria, String par1) {
        int valor = 0;
        ResultSet rs = this.consulta("SELECT count(id_categoria)as conteo,id_categoria FROM tbl_activo as A inner join tbl_bodega_activo as BA on A.id_activo=BA.id_activo WHERE (lower(codigo_activo) like '%' or lower(serie) like '%' or lower(descripcion) like '%%') \n"
                + "and eliminado=false and A.id_activo not in (select id_activo from tbl_activo_personalizacion as P inner join tbl_activo_custodio_tmp as T on T.id_activo_personalizacion_tmp=P.id_activo_personalizacion where anulado=false and aceptada=false) \n"
                + "and BA.id_bodega='" + id_bod + "' and A.id_categoria='" + categoria + "' group by id_categoria");
        try {
            if (rs.next()) {
                valor = rs.getInt("conteo");
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Activo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public ResultSet getCategoriaActivo(String idactivo) {
        return this.consulta("select id_categoria from tbl_activo where id_activo='" + idactivo + "'");
    }

    public String getCategoriaActivo(String idactivo, String par1) {
        String valor = "";
        ResultSet rs = this.consulta("select id_categoria from tbl_activo where id_activo='" + idactivo + "'");
        try {
            if (rs.next()) {
                valor = rs.getString("id_categoria") != null ? rs.getString("id_categoria") : "";
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Activo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public String getCategoriaActivo(String codigo, String par1, String par2) {
        String id = "";
        try {
            ResultSet rs = this.consulta("select id_categoria from tbl_activo where upper(codigo_activo)='" + codigo.toUpperCase() + "'");
            if (rs.next()) {
                id = rs.getString("id_categoria") != null ? rs.getString("id_categoria") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            id = "";
        }
        return id;
    }

    public ResultSet gettamanobodega(String idbodega) {
        return this.consulta("select bta.* from tbl_bodega a\n"
                + "join tbl_bodega_tipo as tb on tb.id_bodega_tipo=a.id_bodega_tipo\n"
                + "join tbl_bodega_tipo_activo as bta on bta.id_bodega_tipo=tb.id_bodega_tipo\n"
                + "where a.id_bodega_tipo='idbodega'");
    }

    public ResultSet gettamanobodega(String idbodega, String categoria) {
        return this.consulta("select bta.* from tbl_bodega a\n"
                + "join tbl_bodega_tipo as tb on tb.id_bodega_tipo=a.id_bodega_tipo\n"
                + "join tbl_bodega_tipo_activo as bta on bta.id_bodega_tipo=tb.id_bodega_tipo\n"
                + "where a.id_bodega_tipo='" + idbodega + "' and bta.id_cat_activo='" + categoria + "'");
    }

    public int gettamanobodega(String idbodega, String categoria, String par1) {
        int valor = 0;
        ResultSet rs = this.consulta("select bta.* from tbl_bodega a\n"
                + "join tbl_bodega_tipo as tb on tb.id_bodega_tipo=a.id_bodega_tipo\n"
                + "join tbl_bodega_tipo_activo as bta on bta.id_bodega_tipo=tb.id_bodega_tipo\n"
                + "where a.id_bodega='" + idbodega + "' and bta.id_cat_activo='" + categoria + "' limit 1");
        try {
            if (rs.next()) {
                valor = rs.getInt("max");
            } else {
                valor = -1;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Activo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public boolean gettipobodega(String idbodega) {
        boolean valor = true;
        ResultSet rs = this.consulta("select tb.* from tbl_bodega a\n"
                + "join tbl_bodega_tipo as tb on tb.id_bodega_tipo=a.id_bodega_tipo\n"
                + "join tbl_bodega_tipo_activo as bta on bta.id_bodega_tipo=tb.id_bodega_tipo\n"
                + "where a.id_bodega='" + idbodega + "' limit 1");
        try {
            if (rs.next()) {
                valor = rs.getBoolean("tipo");
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Activo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public ResultSet gettiposbodegas() {
        return this.consulta("SELECT id_bodega_tipo,nombre_bodega FROM tbl_bodega_tipo ORDER BY id_bodega_tipo ASC");
    }

    ///encrustando
    public String VerBodegaEmpCli(String nombre) {
        String nombrev[] = nombre.split(" ");
        String cambiando = "";
        for (int i = 0; i < nombrev.length; i++) {
            cambiando += nombrev[i] + " ";
        }
        nombre = nombre.substring(0, nombre.length() - 1);
        String idbodega = "";
        try {
            ResultSet rs = this.getBodegasPersonalizacion(nombre);
            if (rs.next()) {
                idbodega = (rs.getString("id_bodega") != null) ? rs.getString("id_bodega") : "";
            }
            rs.close();
            if (idbodega.compareTo("") == 0) {
                cambiando = "";
                for (int i = nombrev.length - 1; i >= 0; i--) {
                    cambiando += nombrev[i] + " ";
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Activo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cambiando;
    }

    public String VerBodegaEmpCli(String idempleado, String parm1) {
        String bodega = "";
        try {
            String nombre[] = new String[2];
            ResultSet rs = this.consulta("select (e.nombre || ' ' ||e.apellido)as apenom,(e.apellido|| ' ' ||e.nombre)as nomape from tbl_empleado e where id_empleado='" + idempleado + "'");
            if (rs.next()) {
                nombre[0] = (rs.getString("apenom") != null) ? rs.getString("apenom") : "";
                nombre[1] = (rs.getString("nomape") != null) ? rs.getString("nomape") : "";
            }
            String idbodega = "";
            rs = this.getBodegasPersonalizacion(nombre[0]);
            if (rs.next()) {
                idbodega = (rs.getString("id_bodega") != null) ? rs.getString("id_bodega") : "";
            }
            if (idbodega.compareTo("") == 0) {
                bodega = nombre[1];
            } else {
                bodega = nombre[0];
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Activo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bodega;
    }

    public String[] VerBodegaEmpCli(String idempleado, String parm1, String parm2) {
        String bodega[] = new String[2];
        try {
            String nombre[] = new String[2];
            ResultSet rs = this.consulta("select (e.nombre || ' ' ||e.apellido)as apenom,(e.apellido|| ' ' ||e.nombre)as nomape from tbl_empleado e where id_empleado='" + idempleado + "'");
            if (rs.next()) {
                nombre[0] = (rs.getString("apenom") != null) ? rs.getString("apenom") : "";
                nombre[1] = (rs.getString("nomape") != null) ? rs.getString("nomape") : "";
            }
            String idbodega = "";
            rs = this.getBodegasPersonalizacion(nombre[0]);
            if (rs.next()) {
                idbodega = (rs.getString("id_bodega") != null) ? rs.getString("id_bodega") : "";
            }
            if (idbodega.compareTo("") != 0) {
                bodega[0] = nombre[0];
                bodega[1] = idbodega;
            } else {
                rs = this.getBodegasPersonalizacion(nombre[1]);
                if (rs.next()) {
                    idbodega = (rs.getString("id_bodega") != null) ? rs.getString("id_bodega") : "";
                }
                bodega[0] = nombre[1];
                bodega[1] = idbodega;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Activo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bodega;
    }

    public ResultSet verbodegapersonal(String idsucursal, String idempleado) {
        return this.consulta("select distinct id_producto,descripcion from vta_bodegapersonal where id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "'");

    }

    public ResultSet personalbodegas() {
        return this.consulta("select distinct id_empleado,(nombre || ' ' ||apellido)as empleado from vta_bodegapersonal");
    }

    public ResultSet stockbodegacategoria(String pam1, String pam2, String pam3) {
        return this.consulta("select " + pam1 + " from tbl_empleado e\n"
                + "inner join tbl_bodega as b on b.id_responsable=e.id_empleado\n"
                + "inner join tbl_bodega_activo as ba on ba.id_bodega=b.id_bodega\n"
                + "inner join tbl_activo as a on a.id_activo=ba.id_activo\n"
                + "inner join tbl_cat_activos as ca on ca.id_cat_activo=a.id_categoria::integer\n"
                + "where e.eliminado=false and e.estado=true and a.eliminado=false\n"
                + "and b.estado=true and b.eliminado=false and es_responsable_cliente=false " + pam2 + " " + pam3 + "");

    }

    public ResultSet stockbodegacategoria(String pam1, String pam2, String pam3, String pam4) {
        return this.consulta("select  " + pam1 + "  from tbl_empleado e\n"
                + "right join tbl_bodega as b on b.id_responsable=e.id_empleado\n"
                + "inner join tbl_bodega_activo as ba on ba.id_bodega=b.id_bodega\n"
                + "inner join tbl_activo as a on a.id_activo=ba.id_activo\n"
                + "inner join tbl_cat_activos as ca on ca.id_cat_activo=a.id_categoria::integer\n"
                + "where  a.eliminado=false and b.estado=true and b.eliminado=false  and b.id_sucursal=1  \n"
                + "and (b.id_responsable=" + pam4 + " or b.id_responsable=(select id_cliente from tbl_cliente where ruc=(select dni from tbl_empleado where id_empleado=" + pam4 + "))) " + pam2 + " " + pam3 + "");

    }

    public ResultSet stockbodegacategoria(String pam1, String pam2, String pam3, String pam4, String pam5) {
        return this.consulta("select " + pam1 + " from tbl_empleado e\n"
                + "inner join tbl_bodega as b on b.id_responsable=e.id_empleado\n"
                + "inner join tbl_bodega_activo as ba on ba.id_bodega=b.id_bodega\n"
                + "inner join tbl_activo as a on a.id_activo=ba.id_activo\n"
                + "inner join tbl_cat_activos as ca on ca.id_cat_activo=a.id_categoria::integer\n"
                + "where e.eliminado=false and e.estado=true and a.eliminado=false\n"
                + "and b.estado=true and b.eliminado=false and es_responsable_cliente=false " + pam2 + " and b.id_sucursal=" + pam5 + " " + pam3 + "");
    }

    public ResultSet empleadoshojasruta(String consulta) {
        return this.consulta(consulta);

    }

    public ResultSet activosmovidos(String fi, String ff, String idempleado) {
        return this.consulta("select a.id_activo,a.codigo_activo,a.descripcion,a.valor_compra,('1')as cantidad,ap.bodega_recibe,ap.fecha::text from tbl_activo_personalizacion ap \n"
                + "inner join tbl_activo_custodio as ac on ac.id_activo_personalizacion=ap.id_activo_personalizacion\n"
                + "inner join tbl_activo as a on a.id_activo=ac.id_activo\n"
                + "where id_bodega_entrega=(select b.id_bodega from tbl_bodega b where id_responsable='" + idempleado + "') and (ap.fecha>='" + fi + "' and ap.fecha<='" + ff + "')\n"
                + "union\n"
                + "select p.id_producto,p.codigo,p.descripcion,p.precio_costo,(sum(otm.cantidad+otm.gastoadicional)),(select bodega from tbl_bodega where id_responsable= (select id_cliente from tbl_instalacion where id_instalacion=ot.id_instalacion) limit 1)::text,fecha_solucion::text from tbl_orden_trabajo_material otm\n"
                + "inner join tbl_orden_trabajo as ot on ot.id_orden_trabajo=otm.id_orden_trabajo\n"
                + "inner join tbl_hoja_ruta as hr on hr.id_hoja_ruta=ot.id_hoja_ruta\n"
                + "inner join tbl_material as m on m.id_material=otm.id_material\n"
                + "inner join tbl_producto as p on p.id_producto=m.id_producto\n"
                + "where (fecha>='" + fi + "' and fecha<='" + ff + "') and hr.id_tecnico_resp='" + idempleado + "'\n"
                + "group by p.id_producto,fecha_solucion,id_instalacion order by fecha desc");

    }

    public ResultSet activosmovidos(String fi, String ff, String idempleado, String version) {
        return this.consulta("select p.id_producto,p.codigo,p.descripcion,p.precio_costo,(sum(otm.cantidad+otm.gastoadicional)) from tbl_orden_trabajo_material otm\n"
                + "inner join tbl_orden_trabajo as ot on ot.id_orden_trabajo=otm.id_orden_trabajo\n"
                + "inner join tbl_hoja_ruta as hr on hr.id_hoja_ruta=ot.id_hoja_ruta\n"
                + "inner join tbl_material as m on m.id_material=otm.id_material\n"
                + "inner join tbl_producto as p on p.id_producto=m.id_producto\n"
                + "where (fecha>='" + fi + "' and fecha<='" + ff + "') and hr.id_tecnico_resp='" + idempleado + "'\n"
                + "group by p.id_producto order by id_producto desc");

    }
    
    
    public double redondear(double valor)
    {
        return (Math.round(valor * Math.pow(10, 2)) / Math.pow(10, 2));
    }
}
