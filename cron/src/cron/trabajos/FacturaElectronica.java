/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import org.w3c.dom.Element;

/**
 *
 * @author desarrollo
 */
public class FacturaElectronica extends Xml{
    public FacturaElectronica()
    {
        super();
        this.crear();
    }
    
    public String generarXml(String claveAcceso, String ambiente, String tipoEmision, String razonSocial, String nombreComercial, String ruc, 
            String codDoc, String estab, String ptoEmi, String secuencial, String dirMatriz, String fechaEmision, String dirEstablecimiento, 
            String contribuyenteEspecial, String obligadoContabilidad, String tipoIdentificacionComprador, 
            String razonSocialComprador, String identificacionComprador, String totalSinImpuestos, String totalDescuento, 
            String subtotal_0, String subtotal_2, String iva_2, String subtotal_3, String iva_3, String importeTotal, String id_forma_pago,
            String codigos, String descripciones, String cantidades, String preciosUnitarios, String descuentos, String subtotales, String ivas, 
            String pIvas, String codigoIvas, String direccion, String plan)
    {
        //String claveAcceso = this.getClaveAcceso(fechaEmision, ruc, "1", estab+ptoEmi, secuencial, tipoEmision);
        Element factura = this.setInfoTributaria("factura", "1.1.0", ambiente, tipoEmision, razonSocial, nombreComercial, ruc, claveAcceso, 
                                                    codDoc, estab, ptoEmi, secuencial, dirMatriz);
        
        this.nuevoElemento(factura, "infoFactura", false);
        Element infoFactura = this.getElementoActivo();
            this.nuevoElementoInsertar("fechaEmision", fechaEmision);
            this.nuevoElementoInsertar("dirEstablecimiento", dirEstablecimiento);
            if(contribuyenteEspecial.compareTo("")!=0){
                this.nuevoElementoInsertar("contribuyenteEspecial", contribuyenteEspecial);
            }
            this.nuevoElementoInsertar("obligadoContabilidad", obligadoContabilidad);
            this.nuevoElementoInsertar("tipoIdentificacionComprador", tipoIdentificacionComprador); // 04=RUC   05=cedula    06=pasaporte   07=consumidor final  08=id del exterior    09=placa
            
            //this.nuevoElementoInsertar("guiaRemision", guiaRemision);
            
            this.nuevoElementoInsertar("razonSocialComprador", razonSocialComprador);
            this.nuevoElementoInsertar("identificacionComprador", identificacionComprador);
            this.nuevoElementoInsertar("totalSinImpuestos", Addons.truncar(Float.parseFloat(totalSinImpuestos) - Float.parseFloat(totalDescuento)  ) );
            this.nuevoElementoInsertar("totalDescuento", Addons.truncar(totalDescuento) );
        
            this.nuevoElemento(infoFactura, "totalConImpuestos", false);
                this.nuevoElementoInsertar("totalImpuesto");
                    this.nuevoElementoInsertar("codigo", "2");  //  IVA=2      ICE=3     IRBPNR=5
                    if(Float.parseFloat(subtotal_0)>0 ){
                        this.nuevoElementoInsertar("codigoPorcentaje", "0");
                        this.nuevoElementoInsertar("baseImponible", Addons.truncar(Float.parseFloat(subtotal_0) - Float.parseFloat(totalDescuento) ) );
                        this.nuevoElementoInsertar("valor", "0.00");
                    }
                    if(Float.parseFloat(subtotal_2)>0 ){
                        this.nuevoElementoInsertar("codigoPorcentaje", "2");
                        this.nuevoElementoInsertar("baseImponible", Addons.truncar(Float.parseFloat(subtotal_2) - Float.parseFloat(totalDescuento) ) );
                        this.nuevoElementoInsertar("valor", Addons.truncar(iva_2) );
                    }
                    if(Float.parseFloat(subtotal_3)>0 ){
                        this.nuevoElementoInsertar("codigoPorcentaje", "3");
                        this.nuevoElementoInsertar("baseImponible", Addons.truncar(Float.parseFloat(subtotal_3) - Float.parseFloat(totalDescuento) ) );
                        this.nuevoElementoInsertar("valor", Addons.truncar(iva_3) );
                    }
            
        this.setElementoActivo(infoFactura);
            this.nuevoElementoInsertar("propina", "0.00");
            this.nuevoElementoInsertar("importeTotal", Addons.truncar(importeTotal) );
            this.nuevoElementoInsertar("moneda", "DOLAR");
        
        this.nuevoElemento(infoFactura, "pagos", false);
            Element pagos = this.getElementoActivo();
                this.nuevoElemento(pagos, "pago", false);
                this.nuevoElementoInsertar("formaPago", id_forma_pago);   //  numerico maximo 2
                this.nuevoElementoInsertar("total", importeTotal);   //   numerico maximo 14
                this.nuevoElementoInsertar("plazo", "0");   //  numerico maximo 14
                this.nuevoElementoInsertar("unidadTiempo", "dias");   // maximo numerico 2
               
                
        this.nuevoElemento(factura, "detalles", false);
        Element detalles = this.getElementoActivo();
        
        String vec_codigo[] = codigos.split(",");
        String vec_descripcion[] = descripciones.split(",");
        String vec_cantidad[] = cantidades.split(",");
        String vec_precioUnitario[] = preciosUnitarios.split(",");
        String vec_descuento[] = descuentos.split(",");
        String vec_subtotal[] = subtotales.split(",");
        String vec_iva[] = ivas.split(",");
        String vec_pIva[] = pIvas.split(",");
        String vec_codIva[] = codigoIvas.split(",");
        for(int i=0; i<vec_codigo.length;i++){
            this.nuevoElemento(detalles, "detalle", false);
            this.nuevoElementoInsertar("codigoPrincipal", vec_codigo[i]);   // maximo 25 caracteres
            this.nuevoElementoInsertar("descripcion", vec_descripcion[i]);    // maximo 300
            this.nuevoElementoInsertar("cantidad", Addons.truncar(vec_cantidad[i]) );
            this.nuevoElementoInsertar("precioUnitario", String.valueOf ( Addons.redondear(Double.parseDouble(vec_precioUnitario[i]), 4) ) );
            this.nuevoElementoInsertar("descuento", Addons.truncar(vec_descuento[i]) );
            this.nuevoElementoInsertar("precioTotalSinImpuesto", Addons.truncar(Float.parseFloat(vec_subtotal[i]) - Float.parseFloat(vec_descuento[i]) ) );
            this.nuevoElementoInsertar("impuestos");
                this.nuevoElementoInsertar("impuesto");
                    this.nuevoElementoInsertar("codigo", "2");  //  IVA=2      ICE=3     IRBPNR=5
                    this.nuevoElementoInsertar("codigoPorcentaje", vec_codIva[i] );   //  codigo de IVA o de  ICE
                    this.nuevoElementoInsertar("tarifa", vec_pIva[i] );  // porcentaje aplicado
                    this.nuevoElementoInsertar("baseImponible", Addons.truncar(Float.parseFloat(vec_subtotal[i]) - Float.parseFloat(vec_descuento[i])));
                    this.nuevoElementoInsertar("valor", Addons.truncar(vec_iva[i]) );
        }
        
        this.nuevoElemento(factura, "infoAdicional", false);
        Element infoAdicional = this.getElementoActivo();
        
        this.nuevoElemento(infoAdicional, "campoAdicional", "info@saitel.ec", false);
        this.setAtributo("nombre", "EMAIL");
        
        this.nuevoElemento(infoAdicional, "campoAdicional", fechaEmision, false);
        this.setAtributo("nombre", "F_REACTIVACION");
        
        this.nuevoElemento(infoAdicional, "campoAdicional", plan.replaceAll("\\s(\\d+\\W?)+", ""), false);
        this.setAtributo("nombre", "TIPO_CLIENTE");
        
        direccion = direccion.compareTo("")!=0? direccion : "S/D";
        this.nuevoElemento(infoAdicional, "campoAdicional", direccion, false);  
        this.setAtributo("nombre", "DIRECCION");
        
            this.nuevoElemento(infoAdicional, "campoAdicional", "<p>Call Center SAITEL: 1700724835 o 0996724835</p>Para la atención de reclamos no resueltos por el prestador, ingrese su reclamo al link: http://reclamoconsumidor.arcotel.gob.ec/osTicket/, o para mayor información comuníquese con el número telefónico 1800 567 567", false);  
        this.setAtributo("nombre", "ARCOTEL");
        
        return this.getXml();
    }
    
    public String getClaveAcceso(String fecha, String tipoComprobante, String ruc, String tipoAmb, String serie, String secuencial, String tipoEmis)
    {
        String axFecha = "";
        if(fecha.indexOf("/")>0){
            axFecha = fecha.replace("/", "");
        }else{
            String vec[] = fecha.split("-");
            axFecha = vec[2]+vec[1]+vec[0];
        }
        String clave = axFecha+tipoComprobante+ruc+tipoAmb+serie+secuencial+"12345678"+tipoEmis;
        clave += this.getDigitoVerificador(clave);
        return clave;
    }
    
    public int getDigitoVerificador(String digitos)
    {
        int digito=0;
        int x=2;
        char vec[] = digitos.toCharArray();
        //int mul[] = new int[vec.length];
        int suma = 0;
        for(int i=47; i>=0; i--){
            if(x==8){
                x=2;
            }
            //mul[i] = vec[i] * x;
            suma += (Integer.parseInt(""+vec[i]) * x);
            x++;
        }
        int mod = suma % 11;
        digito = 11 - mod;
        if(digito==11){
            digito = 0;
        }
        if(digito==10){
            digito = 1;
        }
        return digito;
    }
    
    public boolean guardar(String nombreArchivo)
    {
        return this.salvar(nombreArchivo);
    }
    
}
