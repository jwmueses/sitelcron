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
public class Promocion extends DataBase {

    public Promocion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getPromocion(String id) {
        return this.consulta("SELECT * FROM tbl_promocion where id_promocion=" + id + ";");
    }

    public ResultSet getPromociones() {
        return this.consulta("SELECT * FROM tbl_promocion order by promocion;");
    }

    public ResultSet getPromocionesInstalaciones(int idSucursal) {
        return this.consulta("SELECT P.id_promocion, P.promocion, P.fecha_creacion, P.fecha_inicio, P.fecha_termino, P.inst_objetivo_es_porcentaje, P.inst_objetivo_a_cumplir, "
                + "P.inst_objetivo_basado_en, P.inst_costo_es_porcentaje, P.inst_costo_valor, P.inst_prepago, P.inst_postpago, P.men_tiempo_de_permanencia_min "
                + "FROM tbl_promocion as P inner join tbl_promocion_sucursal as PS on P.id_promocion=PS.id_promocion and case when fecha_inicio is not null then fecha_inicio else fecha_creacion end <= now() "
                + "where inst_objetivo_a_cumplir > 0 and P.cerrada=false and PS.id_sucursal=" + idSucursal + " and P.inst_costo_valor>0 and P.id_promocion not in (4) order by promocion");
    }

    public ResultSet getPromocionesPlanes(String idSucursal, char op) {
        String where = "";
        switch (op) {
            case 'i':   //  en instalaciones
                where = " and inst_objetivo_a_cumplir > 0 ";
                break;
            case 'p':   //  en prefacturas
                where = " and men_descuento > 0 ";
                break;
            case 'v':   //  en ventas de productos
                where = " and prod_descuento > 0 ";
                break;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT PP.id_promocion,id_plan_servicio ");
        sql.append("FROM (tbl_promocion_plan as PP inner join tbl_promocion as P on PP.id_promocion=P.id_promocion) ");
        sql.append("inner join tbl_promocion_sucursal as S on S.id_promocion=P.id_promocion ");
        sql.append("where P.cerrada=false and S.id_sucursal=");
        sql.append(idSucursal);
        sql.append(where);
        sql.append(" and P.id_promocion not in (4) and P.inst_costo_valor>0 order by PP.id_promocion,id_plan_servicio");
        return this.consulta(sql.toString());
    }

    public ResultSet getPromocionesPreFacturas(String idSucursal) {
        return this.consulta("SELECT P.id_promocion, P.promocion, P.fecha_creacion, P.fecha_inicio, P.fecha_termino, P.inst_prepago, P.inst_postpago, "
                + "P.fp_tarjeta_credito, P.fp_tarjeta_debito, P.fp_cuenta_corriente, P.fp_cuenta_ahorros, P.men_descuento, P.men_es_porcentaje, P.men_num_meses "
                + "FROM tbl_promocion as P inner join tbl_promocion_sucursal as PS on P.id_promocion=PS.id_promocion "
                + "where men_descuento > 0 and P.cerrada=false and PS.id_sucursal=" + idSucursal + " order by promocion");
    }

    public ResultSet getPromocionesProductos() {
        return this.consulta("SELECT * FROM tbl_promocion order by promocion;");
    }

    public ResultSet getPromocionesInstalacion(String id_instalacion) {
        return this.consulta("select * from vta_instalacion_promocion_contrato where id_instalacion='" + id_instalacion + "';");
    }

    public ResultSet getPromocionPlan(String id_sucursal, String id_sector, String id_plan_servicio, String id_promocion) {
        return this.consulta("select distinct P.promocion, P.inst_costo_es_porcentaje, P.inst_costo_valor, P.inst_prepago, P.inst_postpago, P.men_tiempo_de_permanencia_min from tbl_promocion as p "
                + " inner join tbl_promocion_plan as pp on pp.id_promocion =p.id_promocion  "
                + " inner join tbl_promocion_sucursal as ps on ps.id_promocion =p.id_promocion  "
                + " inner join tbl_sector as s on s.id_sucursal = ps.id_sucursal  "
                + " where ps.id_sucursal ='" + id_sucursal + "' and s.id_sector ='" + id_sector + "' and pp.id_plan_servicio ='" + id_plan_servicio + "' and p.id_promocion ='" + id_promocion + "';");
    }

}
