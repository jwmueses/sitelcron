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
public class ConfiguracionDAO extends BaseDatos 
{
    public String[] getParametros()
    {
        String[] parametro = {"", "", "", "", "", "", "", "", "", "", ""};
        try{
            ResultSet rs = this.consulta("select parametro, valor from tbl_configuracion where parametro in('p_iva1', 'desc_venta', 'clave_certificado', "
                    + "'ambiente', 'tipo_emision', 'ruc', 'razon_social', 'nombre_comercial', 'num_resolucion', 'oblga_contabilidad', 'dir_matriz')");
            while(rs.next()){
                String param = rs.getString("parametro")!=null ? rs.getString("parametro") : "";
                if( param.compareTo("desc_venta") == 0 ) {
                    parametro[0] = rs.getString("valor")!=null ? rs.getString("valor") : "-1";
                }
                if( param.compareTo("p_iva1") == 0 ) {
                    int pIva = rs.getString("valor")!=null ? rs.getInt("valor") : -1;
                    ResultSet rs2 = this.consulta("select id_plan_cuenta_venta_servicio from tbl_iva where porcentaje=" + pIva);
                    if(rs2.next()){
                        parametro[1] = rs2.getString("id_plan_cuenta_venta_servicio")!=null ? rs2.getString("id_plan_cuenta_venta_servicio") : "-1";
                        rs2.close();
                    }
                }
                if( param.compareTo("clave_certificado") == 0 ) {
                    parametro[2] = rs.getString("valor")!=null ? rs.getString("valor") : "";
                }
                if( param.compareTo("ambiente") == 0 ) {
                    parametro[3] = rs.getString("valor")!=null ? rs.getString("valor") : "1";   // 1=pruebas
                }
                if( param.compareTo("tipo_emision") == 0 ) {
                    parametro[4] = rs.getString("valor")!=null ? rs.getString("valor") : "1";
                }
                if( param.compareTo("ruc") == 0 ) {
                    parametro[5] = rs.getString("valor")!=null ? rs.getString("valor") : "";
                }
                if( param.compareTo("razon_social") == 0 ) {
                    parametro[6] = rs.getString("valor")!=null ? rs.getString("valor") : "";
                }
                if( param.compareTo("nombre_comercial") == 0 ) {
                    parametro[7] = rs.getString("valor")!=null ? rs.getString("valor") : "";
                }
                if( param.compareTo("num_resolucion") == 0 ) {
                    parametro[8] = rs.getString("valor")!=null ? rs.getString("valor") : "";
                }
                if( param.compareTo("oblga_contabilidad") == 0 ) {
                    parametro[9] = rs.getString("valor")!=null ? rs.getString("valor") : "";
                }
                if( param.compareTo("dir_matriz") == 0 ) {
                    parametro[10] = rs.getString("valor")!=null ? rs.getString("valor") : "";
                }
            }
            rs.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return parametro;
    }
}
