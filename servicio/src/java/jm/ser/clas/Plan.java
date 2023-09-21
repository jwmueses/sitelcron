/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.ser.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author PC-ON
 */
public class Plan extends DataBase {

    public Plan(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getPlanes() {
        return this.consulta("select id_plan_isp,plan,comparticion,div_max_limit,div_burst_threshold,burst_time,sim_subida,id_nivel_soporte,txttipo_cliente_plan from vta_plan_isp;");

    }

    public ResultSet getPlanesTipos() {
        return this.consulta("select distinct tps.tipo_plan from tbl_plan_servicio tps where (tps.tipo_plan<>'' and tps.tipo_plan  is not null);");

    }
//    public ResultSet getPlanesTipos() {
//        return this.consulta("select distinct tps.tipo_plan from tbl_plan_servicio tps where (tps.tipo_plan<>'' and tps.tipo_plan  is not null) "
//                + " and tps.tipo_plan not in ('FIBRA OPTICA') order by tps.tipo_plan desc;");
//    }

    public ResultSet getPlanesTodos(String id) {
        String clave = "";
        if (id.trim().compareTo("") != 0) {
            clave = " and V.tipo_plan like '%" + id + "%' ";
        }
        return this.consulta("select V.id_plan_servicio,P.plan, (((V.burst_limit::numeric)/1000)::numeric(8,1)) as txtvelocidad, ((T.costo_plan*1.12)::numeric(13,2))as txtcosto12, ((T.costo_plan*1.14)::numeric(13,2))as txtcosto14, '1 - ' || P.div_max_limit as comparticion, P.id_nivel_soporte "
                + "from (tbl_plan_isp as P inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp) "
                + "inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio "
                + "where P.eliminado=false and V.eliminado=false and (V.tipo_plan is not null and V.tipo_plan<>'')  " + clave + " "
                + "order by P.div_max_limit desc, P.plan desc, V.burst_limit::int;");

    }

    public ResultSet getPlanesTodos(String id, String id_sector) {
        String clave = "";
        if (id.trim().compareTo("") != 0) {
            clave = " and V.tipo_plan like '%" + id + "%' ";
        }
        return this.consulta("select distinct V.id_plan_servicio,P.plan, (((V.burst_limit::numeric)/1000)::numeric(8,1)) as txtvelocidad, ((T.costo_plan*1.12)::numeric(13,2))as txtcosto12, ((T.costo_plan*1.14)::numeric(13,2))as txtcosto14, '1 - ' || P.div_max_limit as comparticion, P.id_nivel_soporte  "
                + " from tbl_plan_isp as P  "
                + " inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp "
                + " inner join tbl_sector_plan_servicio as X on X.id_plan_servicio =V.id_plan_servicio "
                + " inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio "
                + " where P.eliminado=false and V.eliminado=false and (V.tipo_plan is not null and V.tipo_plan<>'') " + clave + " and X.id_sector ='" + id_sector + "' "
                + " order by P.plan,txtvelocidad asc");

    }

    public ResultSet getDetallePlan(String id_plan) {
        return this.consulta("select * from tbl_plan_servicio where id_plan_servicio='" + id_plan + "';");

    }

    public ResultSet getPrecioPlanSector(String id_plan, String id_sector) {
        return this.consulta("select distinct P.id_producto,t.costo_plan,Z.id_producto as id_producto_sector, "
                + " case when lower(P.plan) like '%alambri%' then Z.costo_instalacion "
                + "	 when lower(P.plan) like '%ibra%' then Z.costo_cambio_domicilio_fibra "
                + "	 when lower(P.plan) like '%pon%' then Z.costo_instalacion_gepon "
                + "	 else Z.costo_instalacion end as valor_instalacion,"
                + " P.plan, "
                + " case when lower(P.plan) like '%alambri%' then 'a' "
                + "	 when lower(P.plan) like '%ibra%' then 'f' "
                + "	 when lower(P.plan) like '%pon%' then 'g' "
                + "	 else 'a' end as tipo_instalacion, "
                + " (select w.costo_router from tbl_sucursal as w where w.id_sucursal=Z.id_sucursal) as costo_router,"
                + " (select w.costo_instalacion from tbl_sucursal as w where w.id_sucursal=Z.id_sucursal) as costo_instalacion,"
                + " (select w.tiempo_permanencia from tbl_sucursal as w where w.id_sucursal=Z.id_sucursal) as tiempo_permanencia"
                + " from tbl_plan_isp as P  "
                + " inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp "
                + " inner join tbl_sector_plan_servicio as X on X.id_plan_servicio =V.id_plan_servicio "
                + " inner join tbl_sector as Z on Z.id_sector =X.id_sector  "
                + " inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio  "
                + " where P.eliminado=false and V.eliminado=false and (V.tipo_plan is not null and V.tipo_plan<>'')  "
                + " and v.id_plan_servicio ='" + id_plan + "' and x.id_sector ='" + id_sector + "' limit 1;");

    }

    public ResultSet getPlanDetalle(String id_plan_servicio) {
        return this.consulta("select P.plan, V.burst_limit, T.costo_plan, '1 - ' || P.div_max_limit as comparticion, P.id_nivel_soporte, P.plan || ' ' || V.burst_limit as plan_servicio "
                + "from (tbl_plan_isp as P inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp) "
                + "inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio "
                + "where P.eliminado=false and V.eliminado=false and V.id_plan_servicio=" + id_plan_servicio);
    }

    public ResultSet getPlanes(String id_sector, String tipo_cliente_plan) {
        return this.consulta("SELECT id_plan_servicio, plan || ' (' || burst_limit || ')' from vta_sector_plan_servicio where id_sector=" + id_sector + " and tipo_cliente_plan='" + tipo_cliente_plan + "' order by plan || ' (' || burst_limit || ')', burst_limit::int");
    }
}
