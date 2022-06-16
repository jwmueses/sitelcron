/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;

/**
 *
 * @author sistemas
 */
public class Promocion {
    
    DataBase objDataBase = null;
    String matPromociones[][];
    String matPromocionesPlanes[][];
    double descuento=0;
    double iva=0;
    double total=0;
    
    
    public Promocion()
    {
        this.objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
        
        ResultSet rsPromociones = this.objDataBase.consulta("SELECT PS.id_sucursal, P.id_promocion, P.promocion, P.fecha_creacion, P.fecha_inicio, "
            + "P.fecha_termino, P.inst_prepago, P.inst_postpago, P.fp_tarjeta_credito, P.fp_tarjeta_debito, P.fp_cuenta_corriente, "
            + "P.fp_cuenta_ahorros, P.men_descuento, P.men_es_porcentaje, P.men_num_meses "
            + "FROM tbl_promocion as P inner join tbl_promocion_sucursal as PS on P.id_promocion=PS.id_promocion "
            + "where men_descuento > 0 and P.cerrada=false order by PS.id_sucursal, promocion");
        this.matPromociones = Matriz.ResultSetAMatriz(rsPromociones);
        
        StringBuilder sql = new StringBuilder();   
        sql.append("SELECT S.id_sucursal, PP.id_promocion,id_plan_servicio ");
        sql.append("FROM (tbl_promocion_plan as PP inner join tbl_promocion as P on PP.id_promocion=P.id_promocion) ");
        sql.append("inner join tbl_promocion_sucursal as S on S.id_promocion=P.id_promocion ");
        sql.append("where P.cerrada=false and men_descuento > 0 ");
        sql.append(" order by S.id_sucursal, PP.id_promocion,id_plan_servicio");
        ResultSet rsPromocionesPlanes = this.objDataBase.consulta(sql.toString());
        this.matPromocionesPlanes = Matriz.ResultSetAMatriz(rsPromocionesPlanes); 
    }
    
    public boolean aplicarPromocion(String id_sucursal, int edad, String carne_conadis, String convenio_pago, String forma_pago, String tipo_cuenta, 
            String num_cuenta, String tarjeta_credito_caduca, String id_plan_actual, double subt_internet, double descuento, int pIva)
    {
        if( (edad<65 || carne_conadis.compareTo("")==0) || ((edad>=65 || carne_conadis.compareTo("")!=0) && descuento==0) ){  //  verifico que no sea de la tercera edad 
            if(this.matPromociones.length>0){    // verificao si hay promociones

                boolean enConvenioPago = convenio_pago.compareTo("0")==0 ? Matriz.enMatriz(this.matPromociones, "t", 6)>=0 : Matriz.enMatriz(this.matPromociones, "t", 7)>=0;
                boolean enConvenioDebito = true;
                if(num_cuenta.compareTo("")!=0){
                    if(forma_pago.compareTo("CTA")==0){
                        enConvenioDebito = tipo_cuenta.compareTo("AHO")==0 ? Matriz.enMatriz(this.matPromociones, "t", 11)>=0 : Matriz.enMatriz(this.matPromociones, "t", 10)>=0;
                    } else if(forma_pago.compareTo("TAR")==0){
                               enConvenioDebito = Matriz.enMatriz(this.matPromociones, "t", 8)>=0 ? ( Fecha.getTimeStamp(tarjeta_credito_caduca) > Fecha.getTimeStamp(Fecha.getFecha("SQL") ) ) : false;
                    }
                }
                if(enConvenioPago && enConvenioDebito){ //  verifico si la promocion tiene algun convenio o forma de pago

                    int posPro = Matriz.enMatriz(this.matPromociones, id_sucursal, 0);   //  verifico si aplica a la sucursal
                    if(posPro != -1){
                        int posProPlan = Matriz.enMatriz(this.matPromocionesPlanes, new String[]{id_sucursal, this.matPromociones[posPro][1], id_plan_actual}, new int[]{0,1,2});
                        if(posProPlan !=-1){    //  verificao si aplica al plan actual

                            this.descuento = descuento;
                            int p_des0 = 0;
                            if( this.matPromociones[posPro][13].compareTo("t")==0 && Integer.parseInt(this.matPromociones[posPro][12]) < 100 ){
                                p_des0 = Integer.parseInt(this.matPromociones[posPro][12]);
                            }
                            if( this.matPromociones[posPro][13].compareTo("f")==0 && Integer.parseInt(this.matPromociones[posPro][12]) > 0 ){
                                this.descuento = Float.parseFloat( this.matPromociones[posPro][12] );
                            }

                            if(descuento==0 && p_des0>0){
                                this.descuento = Addons.redondear(subt_internet * p_des0 / 100);
                            }
                            this.iva = Addons.redondear( (subt_internet - descuento ) * pIva / 100 );
                            this.total = Addons.redondear(subt_internet - descuento + iva );
                            return true;

                        }
                    }

                }

            }
        }
        
        return false;
    }
    
    public String[][] getPromociones()
    {
        return this.matPromociones;
    }
    
    public String[][] getPromocionesPlanes()
    {
        return this.matPromocionesPlanes;
    }
    
    public double getDescuento()
    {
        return this.descuento;
    }
    
    public double getIva()
    {
        return this.iva;
    }
    
    public double getTotal()
    {
        return this.total;
    }
    
    public void cerrar()
    {
        this.objDataBase.cerrar();
    }
    
}
