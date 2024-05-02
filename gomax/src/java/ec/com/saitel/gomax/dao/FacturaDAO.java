/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.dao;

import ec.com.saitel.gomax.model.Factura;
import ec.com.saitel.gomax.model.FacturaDetalle;
import ec.com.saitel.gomax.model.PlanGomax;
import ec.com.saitel.gomax.model.Suscripcion;
import ec.com.saitel.gomax.utils.Archivo;
import ec.com.saitel.gomax.utils.Cadena;
import ec.com.saitel.gomax.utils.FacturaElectronica;
import ec.com.saitel.gomax.utils.Fecha;
import ec.com.saitel.gomax.utils.Parametro;
import ec.gob.sri.FirmaXadesBes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sistemas
 */
public class FacturaDAO extends BaseDatos 
{
    private final Factura factura = new Factura();
    
    public void setCabecera(Suscripcion suscripcion, String[] puntoEmision, String[] idPlanCuentasDescIva)
    {
        this.factura.setIdPuntoEmision( Integer.parseInt(puntoEmision[0]) );
        this.factura.setIdPlanCuenta( Integer.parseInt(puntoEmision[1]) );
        this.factura.setSerieFactura( puntoEmision[2] );
        
        this.factura.setIdPlanCuentaDescuento( Integer.parseInt( idPlanCuentasDescIva[0] ) );
        this.factura.setIdPlanCuentaIva( Integer.parseInt( idPlanCuentasDescIva[1] ) );
        
        this.factura.setIdCliente( suscripcion.getIdCliente() );
        this.factura.setTipoDocumento( suscripcion.getTipoDocumento() );
        this.factura.setRuc( suscripcion.getRuc() );
        this.factura.setRazonSocial( suscripcion.getRazonSocial() );
        this.factura.setDireccion( suscripcion.getDireccion() );
        this.factura.setEmail( suscripcion.getCorreoCuenta() );
        this.factura.setTelefono( suscripcion.getMovilClaro() );
        this.factura.setNumCompPago("");
        this.factura.setObservacion( "Emisión de la factura por servicios de IPTV Nro. "+puntoEmision[2]+"-" );
        this.factura.setSubtotal(0);
        this.factura.setSubtotal0(0);
        this.factura.setSubtotal2(0);
        this.factura.setSubtotal3(0);
        this.factura.setDescuento(0);
        this.factura.setIva2(0);
        this.factura.setIva3(0);
        this.factura.setTotal(0);
        this.factura.setCxc(0);
    }
    
    public void setDetalle(List<PlanGomax> planesGomax)
    {
        List <FacturaDetalle> detalleFactura = new ArrayList();
        Iterator it = planesGomax.iterator();
        while(it.hasNext()){
            PlanGomax planGomax = (PlanGomax)it.next();
            FacturaDetalle detalle = new FacturaDetalle();
            
            detalle.setIdProducto( planGomax.getIdProducto() );
            detalle.setDescripcion_mas( "Plan TV " + planGomax.getNombrePlan() );
            detalle.setCantidad( 1 );
            detalle.setpC( planGomax.getCosto() );
            detalle.setpU( planGomax.getCosto() );
            detalle.setpSt( planGomax.getCosto() );
            detalle.setDescuento( 0 );
            detalle.setIva( planGomax.getIva() );
            detalle.setTotal( planGomax.getTotal() );
            detalle.setCodIva( planGomax.getCodigo() );
            detalle.setpIva( planGomax.getPorcentaje() );
            detalle.setpDescuento( 0 );
            detalle.setIdPlanCuenta( planGomax.getIdPlanCuentaProducto() );
            
            detalleFactura.add(detalle);
        }
        this.factura.setDetalle(detalleFactura);
    }
    
    public String facturar(String[] parametro)
    {
        String id_factura_ = "-1";
        String codigoFormaPago = "20";  // Otros con utilización del sistema financiero (PAGO MEDIOS)
        String estadoDocumento = "g";
        String claveAcceso = "";
        String autorizacionXml = "";
        String respuestaAutoriz = "";
        String paramArtic = "";
        String paramAsiento = "['" + this.factura.getIdPlanCuenta() + "', '" + this.factura.getTotal() + "', '0']";
        
        String ids_productos = "";
        String descripciones = "";
        String cantidades_prod = "";
        String preciosUnitarios = "";
        String descuentos = "";
        String subtotales = "";
        String ivas = "";
        String pIvas = "";
        String codigoIvas = "";
        
        String certificado = Parametro.getDOCS_ELECTRONICOS() + "certificado.p12";
        String rutaSalida = Parametro.getDOCS_ELECTRONICOS() + "firmados";
        
        if( this.factura.getDescuento() > 0 ) {
            paramAsiento += ",['" + this.factura.getIdPlanCuentaDescuento() + "', '" + this.factura.getDescuento() + "', '0']";
        }
        try {
            Iterator it = this.factura.getDetalle().iterator();
            while(it.hasNext()){
                FacturaDetalle facturaDetalle = (FacturaDetalle)it.next();
                
                this.factura.setSubtotal( this.factura.getSubtotal() + facturaDetalle.getpSt() ); 
                if( facturaDetalle.getCodIva() == 2 || facturaDetalle.getCodIva() == 4 || facturaDetalle.getCodIva() == 5 || facturaDetalle.getCodIva() == 10) {
                    this.factura.setSubtotal2( this.factura.getSubtotal2() + facturaDetalle.getpSt() );
                    this.factura.setIva2( this.factura.getIva2()+ facturaDetalle.getIva() );
                }
                if( facturaDetalle.getCodIva() == 3) {
                    this.factura.setSubtotal3( this.factura.getSubtotal3() + facturaDetalle.getpSt() );
                    this.factura.setIva3( this.factura.getIva3()+ facturaDetalle.getIva() );
                }
                this.factura.setTotal( this.factura.getTotal() + facturaDetalle.getTotal() );
                
                ids_productos += facturaDetalle.getIdProducto() + ",";
                descripciones += facturaDetalle.getDescripcion_mas().replace("\n", ". ").replace("\r", ". ").replace("\t", ". ").replace(",", "-") + ",";
                cantidades_prod += facturaDetalle.getCantidad() + ",";
                preciosUnitarios += facturaDetalle.getpU() + ",";
                descuentos += facturaDetalle.getDescuento() + ",";
                subtotales += facturaDetalle.getpSt() + ",";
                ivas += facturaDetalle.getIva() + ",";
                pIvas += facturaDetalle.getpIva() + ",";
                codigoIvas += facturaDetalle.getCodIva() + ",";
                
                paramArtic += "['" + facturaDetalle.getIdProducto() + "', '" + 
                                facturaDetalle.getCantidad() + "', '" + 
                                facturaDetalle.getpU() + "', '" + 
                                facturaDetalle.getpSt() + "', '" + 
                                facturaDetalle.getDescuento() + "', '" + 
                                facturaDetalle.getIva() + "', '" + 
                                facturaDetalle.getTotal() + "', '" + 
                                facturaDetalle.getDescripcion_mas() + "', '" + 
                                facturaDetalle.getpC() + "', '" + 
                                "NULL', '" + 
                                facturaDetalle.getpIva() + "', '" + 
                                facturaDetalle.getCodIva() + "', 'p','-1'],";
                
                paramAsiento += ",['" + facturaDetalle.getIdPlanCuenta() + "', '0', '" + facturaDetalle.getpSt() + "']";
            }
            paramAsiento += ",['" + this.factura.getIdPlanCuentaIva() + "', '0', '" + (this.factura.getIva2() + this.factura.getIva3()) + "']";
            
            this.factura.setCxc( this.factura.getTotal() );
            long numFactura = this.getNumFactura( this.factura.getSerieFactura() );
            this.factura.setNumFactura(numFactura);
            
            
            
            if (paramArtic.compareTo("") != 0) {
                
                ids_productos = ids_productos.substring(0, ids_productos.length() - 1);
                descripciones = descripciones.substring(0, descripciones.length() - 1);
                cantidades_prod = cantidades_prod.substring(0, cantidades_prod.length() - 1);
                preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length() - 1);
                descuentos = descuentos.substring(0, descuentos.length() - 1);
                subtotales = subtotales.substring(0, subtotales.length() - 1);
                ivas = ivas.substring(0, ivas.length() - 1);
                pIvas = pIvas.substring(0, pIvas.length() - 1);
                codigoIvas = codigoIvas.substring(0, codigoIvas.length() - 1);
                    
                FacturaElectronica objFE = new FacturaElectronica();
                String vecSerie[] = this.factura.getSerieFactura().split("-");

                claveAcceso = objFE.getClaveAcceso(Cadena.setFecha(Fecha.getFecha("ISO")), "01", parametro[5], parametro[3], vecSerie[0] + vecSerie[1], Cadena.setSecuencial( numFactura ), parametro[4]);

                objFE.generarXml(claveAcceso, parametro[3], parametro[4], parametro[6], parametro[7], parametro[5], "01", vecSerie[0], vecSerie[1],
                        Cadena.setSecuencial(numFactura), parametro[10], Cadena.setFecha(Fecha.getFecha("ISO")), parametro[10], parametro[8], parametro[9],
                        this.factura.getTipoDocumento(), this.factura.getRazonSocial(), this.factura.getRuc(), String.valueOf( this.factura.getSubtotal() ), String.valueOf( this.factura.getDescuento() ), 
                        String.valueOf( this.factura.getSubtotal0() ), String.valueOf( this.factura.getSubtotal2() ), String.valueOf( this.factura.getIva2() ), 
                        String.valueOf( this.factura.getSubtotal3() ), String.valueOf( this.factura.getIva3() ), String.valueOf( this.factura.getTotal() ), 
                        codigoFormaPago, ids_productos, descripciones, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, this.factura.getDireccion(), this.factura.getEmail() );
                String documentoXml = Parametro.getDOCS_ELECTRONICOS() + "generados/" + claveAcceso + ".xml";
                objFE.salvar(documentoXml);
                String error = objFE.getError();
                
                if (error.compareTo("") == 0) {
                    estadoDocumento = "g";
                    String archivoSalida = claveAcceso + ".xml";
                    FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, parametro[2], documentoXml, rutaSalida, archivoSalida);
                    firmaDigital.execute();
                    error = firmaDigital.getError();

                    if (error.compareTo("") == 0) {
                        estadoDocumento = "f";
                    }
                    autorizacionXml = this.getStringFromFile( Parametro.getDOCS_ELECTRONICOS() + "firmados/" + claveAcceso + ".xml" );
                }
            }
            
            
            
            
            ResultSet res = this.consulta("select facturaVenta(1, " + this.factura.getIdPuntoEmision() + ", " + this.factura.getIdCliente() + ", 'pagomedios', '" + this.factura.getSerieFactura()
                + "', '" + numFactura + "', '1119999999', '" + this.factura.getRuc() + "', '" + this.factura.getRazonSocial() + "', '" + Fecha.getFecha("ISO") + "', '" + this.factura.getDireccion()
                + "', '" + this.factura.getTelefono() + "', '24', 's3', 'PAGO MEDIOS', '', '" + this.factura.getNumCompPago() + "', 0, 10, '', '" + this.factura.getObservacion() + numFactura
                + "', " + this.factura.getSubtotal() + ", " + this.factura.getSubtotal0() + ", " + this.factura.getSubtotal2() + ", " + this.factura.getSubtotal3() + ", " + this.factura.getDescuento()
                + ", " + this.factura.getIva2() + ", " + this.factura.getIva3() + ", " + this.factura.getTotal() + ", array[" + paramArtic.substring( 0, paramArtic.length()-1 ) 
                + "], '', '0', '', NULL, '', null, 0, array[]::varchar[], array[" + paramAsiento + "], '', -1, array[]::varchar[], '');"); // 43 param
            if (res.next()) {
                id_factura_ = (res.getString(1) != null) ? res.getString(1) : "-1";
                res.close();
                
                if( id_factura_.compareTo("-1")!=0 ){
                    this.setDocumentoElectronico(id_factura_, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz);
                    Archivo archivo = new Archivo(Parametro.getDocumentalIp(), Parametro.getDocumentalPuerto(), Parametro.getDocumentalBaseDatos(), Parametro.getDocumentalUsuario(), Parametro.getDocumentalClave());
                    archivo.setArchivoDocumentalTexto(autorizacionXml, this.factura.getSerieFactura().replace("-", "") + this.factura.getNumFactura(), "1", "tbl_factura_venta", id_factura_, "documentoxml", "public", "db_isp");
                    archivo.cerrar();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_factura_;
    }
    
    
    
    
    
    
    public long getNumFactura(String serieFactura)
    {
        long numFactura = 0;
        try{
            ResultSet rs = this.consulta("select max(num_factura) from tbl_factura_venta where serie_factura = '"+serieFactura+"'");
            if(rs.next()){
                numFactura = rs.getString(1)!=null ? rs.getLong(1) : 0;
                rs.close();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return numFactura + 1;
    }
    
    public boolean setDocumentoElectronico(String id_factura_venta, String estado, String claveAcceso, String autorizacionXml, String mensaje) 
    {
        return this.ejecutar("update tbl_factura_venta set estado_documento='" + estado + "', clave_acceso='" + claveAcceso
                + "', documento_xml='" + autorizacionXml + "', mensaje='" + mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")
                + "' where id_factura_venta=" + id_factura_venta);
    }
    
    private String getStringFromFile(String archivo) throws IOException {
        File file = new File(archivo);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            cadXml.append(linea);
        }
        return cadXml.toString();
    }
    
}
