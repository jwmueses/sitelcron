/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.model;

/**
 *
 * @author sistemas
 */
public class FacturaDetalle {
    private long idFacturaVentaDetalle;
    private long idfacturaVenta;
    private int idProducto;
    private double cantidad;
    private double pU;
    private double pSt;
    private double pDescuento;
    private double descuento;
    private double iva;
    private double total;
    private String descripcion_mas;
    private double pC;
    private long idListaPrecio;
    private double pIva;
    private int codIva;
    private int idPlanCuenta;

    public long getIdFacturaVentaDetalle() {
        return idFacturaVentaDetalle;
    }

    public void setIdFacturaVentaDetalle(long idFacturaVentaDetalle) {
        this.idFacturaVentaDetalle = idFacturaVentaDetalle;
    }

    public long getIdfacturaVenta() {
        return idfacturaVenta;
    }

    public void setIdfacturaVenta(long idfacturaVenta) {
        this.idfacturaVenta = idfacturaVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getpU() {
        return pU;
    }

    public void setpU(double pU) {
        this.pU = pU;
    }

    public double getpSt() {
        return pSt;
    }

    public void setpSt(double pSt) {
        this.pSt = pSt;
    }

    public double getpDescuento() {
        return pDescuento;
    }

    public void setpDescuento(double pDescuento) {
        this.pDescuento = pDescuento;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDescripcion_mas() {
        return descripcion_mas;
    }

    public void setDescripcion_mas(String descripcion_mas) {
        this.descripcion_mas = descripcion_mas;
    }

    public double getpC() {
        return pC;
    }

    public void setpC(double pC) {
        this.pC = pC;
    }

    public long getIdListaPrecio() {
        return idListaPrecio;
    }

    public void setIdListaPrecio(long idListaPrecio) {
        this.idListaPrecio = idListaPrecio;
    }

    public double getpIva() {
        return pIva;
    }

    public void setpIva(double pIva) {
        this.pIva = pIva;
    }

    public int getCodIva() {
        return codIva;
    }

    public void setCodIva(int codIva) {
        this.codIva = codIva;
    }

    public int getIdPlanCuenta() {
        return idPlanCuenta;
    }

    public void setIdPlanCuenta(int idPlanCuenta) {
        this.idPlanCuenta = idPlanCuenta;
    }

}
