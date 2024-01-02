/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.util.List;
import java.util.ArrayList;

        
        

/**
 *
 * @author jorge
 */
public class FinAnio {
    
    public void ejecutar() {
        
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        int mes = Fecha.getMes();
        int dia = Fecha.getDia();
           
        
        
        
        
        if(mes==12 && dia == 31){
            
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando revocado de privilegios");
            try{
                List sql = new ArrayList();
                sql.add("update tbl_empresa set clave=md5(now()::varchar);");
                sql.add("truncate table tbl_privilegio_31;");
                sql.add("insert into tbl_privilegio_31 select id_rol, id_pagina from tbl_privilegio;");
                if( objDataBase.transacciones(sql) ){
                    if(!objDataBase.ejecutar("delete from tbl_privilegio where id_rol not in(1, 2, 19, 20, 42, 52) and " +
                        "id_pagina in (select id_pagina from tbl_pagina where pagina in( " +
                        "'plan_cuentas','bancos','diario','mayor','comprobantes','caja_chica','bancos_pagos','anulados','sri','estados', " +
                        "'requesiciones','ordenesdecompra','ordenesdeconsumo','bodegapersonal', " +
                        "'ordenarProductos','liquidaciones','notasVenta','importaciones','pagos','pedidos','traspasos', " +
                        "'comprasActivos','ventas','perdida_activos','revalorizacionactivos', " +
                        "'arqueo','clientes','docsbancos','docsBancosTmp','cash_pichincha','cash_produbanco','deb_tarj_pichincha','docsBancosDebitos', " +
                        "'rubros','anticipos','recargas','prefacturacion','ventas','ventasServicioWeb','cobros','installNueva', 'suspensionNueva', 'compras' " +
                        ") );")){
                        System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error en revocado de privilegios. " + objDataBase.getError());
                    }
                }
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando quitando privilegios");
            }
        }




        if(mes==1 && dia == 1){
            
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Iniciando concediento privilegios");
            try{
                
                List sql = new ArrayList();
                sql.add("update tbl_empresa set clave=md5(codigo);");
                sql.add("truncate table tbl_privilegio;");
                sql.add("insert into tbl_privilegio select id_rol, id_pagina from tbl_privilegio_31;");
                
                if(!objDataBase.transacciones(sql)){
                    System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error concediendo privilegios. " + objDataBase.getError());
                }
                
            }finally{
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalizando concediento privilegios");
            }
        }


            


            objDataBase.cerrar();

        
        
    }
     
}
