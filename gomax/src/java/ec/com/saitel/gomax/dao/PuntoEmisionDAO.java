/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.dao;

import java.sql.ResultSet;

/**
 *
 * @author sistemas
 */
public class PuntoEmisionDAO extends BaseDatos 
{
    public String[] getIdPlanCuentaCajaGOMAX()
    {
        String[] puntoEmision = {"0", "0", "000-000"};
        try{
            ResultSet rs = this.consulta("select id_punto_emision, id_plan_cuenta_caja, fac_num_serie from tbl_punto_emision where punto_emision ='IPTV 26'");
            if(rs.next()){
                puntoEmision[0] = rs.getString("id_punto_emision")!=null ? rs.getString("id_punto_emision") : "0";
                puntoEmision[1] = rs.getString("id_plan_cuenta_caja")!=null ? rs.getString("id_plan_cuenta_caja") : "0";
                puntoEmision[2] = rs.getString("fac_num_serie")!=null ? rs.getString("fac_num_serie") : "0";
                rs.close();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return puntoEmision;
    }
   
}
