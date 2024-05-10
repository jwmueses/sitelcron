/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.com.saitel.gomax.dao;

import ec.com.saitel.gomax.model.DatosPagomedio;
import ec.com.saitel.gomax.model.PagosPagomedio;
import ec.com.saitel.gomax.model.PagosPagomedio.Transactions;
import ec.com.saitel.gomax.model.RespuestaPagomedio.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author pc01
 */
public class DocumentoPagomedioDAO extends BaseDatos {
    
    public String guardar(DatosPagomedio datosPagomedio){
        return 
            this.insertar("INSERT INTO tbl_documento_pagomedios (id_prefactura, dni, tipo_documento, nombre, email, telefono, direccion, tipo, estado, total_prefactura, fecha_recibido, hora_recibido) VALUES('"+
        "TV"+ datosPagomedio.getCustom_value() +"', '"+datosPagomedio.getThird().getDocument()+"', '05', '"+
        datosPagomedio.getThird().getName() +"', '"+datosPagomedio.getThird().getEmail()+"', '"+
        datosPagomedio.getThird().getPhones()+"', '"+datosPagomedio.getThird().getAddress()+"', '"+
        datosPagomedio.getThird().getType()+"', '1', '"+ datosPagomedio.getAmount_with_tax()+"', '"+
        LocalDate.now()+"', '"+ LocalTime.now().withNano(0) +"');");
    }
    
    public boolean actualizarPago(Transactions transactions, String value){
        return 
          this.ejecutar("UPDATE tbl_documento_pagomedios SET estado='5', payment_id= '"+ transactions.getPayment_id()+"' ,"+
          " merchant_transaction_id= '" + transactions.getMerchant_transaction_id() + "' , value='"+ value + "', fecha_pagado= '"+LocalDate.now()
          +"' , hora_pagado= '"+LocalTime.now()+"', targeta="+ transactions.getAcquirer()
          +" , banco_targeta="+transactions.getCard_brand()+ "WHERE id_prefactura='TV"+value+"';");
    }
}
