/**
 * @version 1.0
 * @package GOMAX.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2024 por Jorge Mueses. Todos los derechos
 * reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL. FACTURAPYMES! es un
 * software de libre distribución, que puede ser copiado y distribuido bajo los
 * términos de la Licencia Pública General GNU, de acuerdo con la publicada por
 * la Free Software Foundation, versión 2 de la licencia o cualquier versión
 * posterior.
 */
package ec.com.saitel.gomax.model;

/**
 *
 * @author sistemas
 */
public class PlanWeb 
{
    private int idPlanGomax;
    private String nombrePlan;	
    private String descripcion;
    private int meses;
    private float costo;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getMeses() {
        return meses;
    }

    public void setMeses(int meses) {
        this.meses = meses;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }
	
}
