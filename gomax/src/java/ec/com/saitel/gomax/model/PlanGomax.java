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
public class PlanGomax 
{
    private int idPlanGomax;
    private String nombrePlan;	
    private int meses;
    private double costo;
    private double iva;
    private double total;
    private int idProducto;
    private int idIva;
    private int idPlanCuentaProducto;
    private int idPlanCuentaIva;
    private int codigo;
    private int porcentaje;
//    private boolean gravaIce;    
    private long gomaxId;
    private long gomaxProduct;
    private long gomaxPricelistId;
    private double gomaxPrice;

    public int getIdPlanGomax() {
        return idPlanGomax;
    }

    public void setIdPlanGomax(int idPlanGomax) {
        this.idPlanGomax = idPlanGomax;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public int getMeses() {
        return meses;
    }

    public void setMeses(int meses) {
        this.meses = meses;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
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

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdIva() {
        return idIva;
    }

    public void setIdIva(int idIva) {
        this.idIva = idIva;
    }

    public int getIdPlanCuentaProducto() {
        return idPlanCuentaProducto;
    }

    public void setIdPlanCuentaProducto(int idPlanCuentaProducto) {
        this.idPlanCuentaProducto = idPlanCuentaProducto;
    }

    public int getIdPlanCuentaIva() {
        return idPlanCuentaIva;
    }

    public void setIdPlanCuentaIva(int idPlanCuentaIva) {
        this.idPlanCuentaIva = idPlanCuentaIva;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public long getGomaxId() {
        return gomaxId;
    }

    public void setGomaxId(long gomaxId) {
        this.gomaxId = gomaxId;
    }

    public long getGomaxProduct() {
        return gomaxProduct;
    }

    public void setGomaxProduct(long gomaxProduct) {
        this.gomaxProduct = gomaxProduct;
    }

    public long getGomaxPricelistId() {
        return gomaxPricelistId;
    }

    public void setGomaxPricelistId(long gomaxPricelistId) {
        this.gomaxPricelistId = gomaxPricelistId;
    }

    public double getGomaxPrice() {
        return gomaxPrice;
    }

    public void setGomaxPrice(double gomaxPrice) {
        this.gomaxPrice = gomaxPrice;
    }

}
