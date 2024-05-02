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
public class Suscripcion {
    private long idClienteSuscripcionGomax;
    private long idCliente;
    private int idPlanGomax;
    private String numContrato;
    private String fechaSuscripcion;
    private String fechaTermino;
    private boolean cobrar;
    private String correoCuenta;
    private boolean correoConfirmado;
    private String claveCuenta;
    private String jwt;
    private boolean debitoAutomatico;
    private String estado;
    private long gomaxPartnerId;
    
    private String tipoDocumento;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String movilClaro;

    public long getIdClienteSuscripcionGomax() {
        return idClienteSuscripcionGomax;
    }

    public void setIdClienteSuscripcionGomax(long idClienteSuscripcionGomax) {
        this.idClienteSuscripcionGomax = idClienteSuscripcionGomax;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdPlanGomax() {
        return idPlanGomax;
    }

    public void setIdPlanGomax(int idPlanGomax) {
        this.idPlanGomax = idPlanGomax;
    }

    public String getNumContrato() {
        return numContrato;
    }

    public void setNumContrato(String numContrato) {
        this.numContrato = numContrato;
    }

    public String getFechaSuscripcion() {
        return fechaSuscripcion;
    }

    public void setFechaSuscripcion(String fechaSuscripcion) {
        this.fechaSuscripcion = fechaSuscripcion;
    }

    public String getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(String fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public boolean isCobrar() {
        return cobrar;
    }

    public void setCobrar(boolean cobrar) {
        this.cobrar = cobrar;
    }

    public String getCorreoCuenta() {
        return correoCuenta;
    }

    public void setCorreoCuenta(String correoCuenta) {
        this.correoCuenta = correoCuenta;
    }

    public boolean isCorreoConfirmado() {
        return correoConfirmado;
    }

    public void setCorreoConfirmado(boolean correoConfirmado) {
        this.correoConfirmado = correoConfirmado;
    }

    public String getClaveCuenta() {
        return claveCuenta;
    }

    public void setClaveCuenta(String claveCuenta) {
        this.claveCuenta = claveCuenta;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public boolean isDebitoAutomatico() {
        return debitoAutomatico;
    }

    public void setDebitoAutomatico(boolean debitoAutomatico) {
        this.debitoAutomatico = debitoAutomatico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getGomaxPartnerId() {
        return gomaxPartnerId;
    }

    public void setGomaxPartnerId(long gomaxPartnerId) {
        this.gomaxPartnerId = gomaxPartnerId;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMovilClaro() {
        return movilClaro;
    }

    public void setMovilClaro(String movilClaro) {
        this.movilClaro = movilClaro;
    }
 
}
