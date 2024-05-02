/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.model;

import java.util.List;

/**
 *
 * @author sistemas
 */
public class Factura {
    private long idFacturaVenta;
//    private final int id_sucursal = 1;
//    private final String vendedor = "pagomedios";
//    private id_comprobante_ingreso int8 NULL,
//    id_comprobante_diario int8 NULL,
    private long idCliente;
//    private final String fecha_emision = Fecha.getFecha("ISO");
    private String direccion;
    private String telefono;
//    private final String forma_pago = "s3";
    private String serieFactura;
//    private String autorizacion = "1119999999";
    private long numFactura;
    private double subtotal = 0;
    private double descuento = 0;
    private double subtotal0 = 0;
    private double iva2 = 0;
    private double total = 0;
    private double cxc = 0;
    private double deuda = 0;
    private String observacion;
    private int idPuntoEmision;
    private double subtotal2 = 0;
//    private double subtotal_6;
//    private double iva_0;
    private String documentoXml;
    private String claveAcceso;
    private String estadoDocumento;
//    contabilizado bool NULL DEFAULT false,
    private String numCompPago;
//    gastos_bancos numeric(13, 2) NULL DEFAULT 0,
//    private int idPlanCuentaBanco = 10;
//    detalle_diario text NULL,
//    asiento_diario text NULL,
//    detalle_ingreso text NULL,
//    asiento_ingreso text NULL,
//    costo_ventas numeric(13, 2) NULL DEFAULT 0,
//    asiento_bodega text NULL,
//    utilidad numeric(13, 2) NULL DEFAULT 0,
//    asiento_utilidad text NULL,
//    private double subtotal_1 numeric(13, 2) NULL DEFAULT 0,
//    private double iva_1 numeric(13, 2) NULL DEFAULT 0,
    private double subtotal3 = 0;
    private double iva3 = 0;
//    private double subtotal_4 numeric(13, 2) NULL DEFAULT 0,
//    private double iva_4 numeric(13, 2) NULL DEFAULT 0,
//    private double subtotal_5 numeric(13, 2) NULL DEFAULT 0,
//    private double iva_5 numeric(13, 2) NULL DEFAULT 0,
//    private double subtotal_7 numeric(13, 2) NULL DEFAULT 0,
//    private double iva_7 numeric(13, 2) NULL DEFAULT 0,
//    private String id_forma_pago = "24";
//    comision_cash numeric(13, 2) NULL,
//    hora_emision time NULL DEFAULT now(),
//    conciliado_fecha date NULL,
//    conciliado bool NULL DEFAULT true,
//    pago_diferirido_meses int4 NULL DEFAULT 0,
//    fecha_anulacion date NULL,
//    hora_anulacion time NULL,
//    nombre_boucher varchar(80) NULL,
//    usuario_concilia_boucher varchar(40) NULL,
//    conciliado_boucher bool NULL DEFAULT true,
    private String tipoDocumento;
    private String ruc;
    private String razonSocial;
    private String email;
//    novedades varchar(380) NULL,
//    private int idBanco = 1;
    private int idPlanCuenta;
    private int idPlanCuentaDescuento;
    private int idPlanCuentaIva;
    
    private List<FacturaDetalle> detalle;

    public long getIdFacturaVenta() {
        return idFacturaVenta;
    }

    public void setIdFacturaVenta(long idFacturaVenta) {
        this.idFacturaVenta = idFacturaVenta;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSerieFactura() {
        return serieFactura;
    }

    public void setSerieFactura(String serieFactura) {
        this.serieFactura = serieFactura;
    }

    public long getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(long numFactura) {
        this.numFactura = numFactura;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getSubtotal0() {
        return subtotal0;
    }

    public void setSubtotal0(double subtotal0) {
        this.subtotal0 = subtotal0;
    }

    public double getIva2() {
        return iva2;
    }

    public void setIva2(double iva2) {
        this.iva2 = iva2;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getCxc() {
        return cxc;
    }

    public void setCxc(double cxc) {
        this.cxc = cxc;
    }

    public double getDeuda() {
        return deuda;
    }

    public void setDeuda(double deuda) {
        this.deuda = deuda;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getIdPuntoEmision() {
        return idPuntoEmision;
    }

    public void setIdPuntoEmision(int idPuntoEmision) {
        this.idPuntoEmision = idPuntoEmision;
    }

    public double getSubtotal2() {
        return subtotal2;
    }

    public void setSubtotal2(double subtotal2) {
        this.subtotal2 = subtotal2;
    }

    public String getDocumentoXml() {
        return documentoXml;
    }

    public void setDocumentoXml(String documentoXml) {
        this.documentoXml = documentoXml;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(String estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public String getNumCompPago() {
        return numCompPago;
    }

    public void setNumCompPago(String numCompPago) {
        this.numCompPago = numCompPago;
    }

    public double getSubtotal3() {
        return subtotal3;
    }

    public void setSubtotal3(double subtotal3) {
        this.subtotal3 = subtotal3;
    }

    public double getIva3() {
        return iva3;
    }

    public void setIva3(double iva3) {
        this.iva3 = iva3;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdPlanCuenta() {
        return idPlanCuenta;
    }

    public void setIdPlanCuenta(int idPlanCuenta) {
        this.idPlanCuenta = idPlanCuenta;
    }

    public int getIdPlanCuentaDescuento() {
        return idPlanCuentaDescuento;
    }

    public void setIdPlanCuentaDescuento(int idPlanCuentaDescuento) {
        this.idPlanCuentaDescuento = idPlanCuentaDescuento;
    }

    public int getIdPlanCuentaIva() {
        return idPlanCuentaIva;
    }

    public void setIdPlanCuentaIva(int idPlanCuentaIva) {
        this.idPlanCuentaIva = idPlanCuentaIva;
    }
    
    public List<FacturaDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<FacturaDetalle> detalle) {
        this.detalle = detalle;
    }

}
