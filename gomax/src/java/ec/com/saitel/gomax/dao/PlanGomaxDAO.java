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
package ec.com.saitel.gomax.dao;

import ec.com.saitel.gomax.model.PlanGomax;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sistemas
 */
public class PlanGomaxDAO extends BaseDatos 
{
    
    public List<PlanGomax> getPlanesProducto(int idsPlanGomax)
    {
        List<PlanGomax> planes = new ArrayList();
        
        try( ResultSet rs = this.consulta("select PG.id_plan_gomax, PG.nombre_plan, PG.meses, PG.costo, PG.id_producto, P.id_iva, "
                + "(PG.costo * (I.porcentaje::numeric / 100))::numeric(13,2) as iva, PG.costo + (PG.costo * (I.porcentaje::numeric / 100))::numeric(13,2) as total, "
                + "P.id_plan_cuenta_venta as id_plan_cuenta_producto, I.id_plan_cuenta_venta_servicio as id_plan_cuenta_iva, I.codigo, I.porcentaje "
                + "from tbl_plan_gomax as PG inner join tbl_producto as P on PG.id_producto=P.id_producto "
                + "inner join tbl_iva as I on P.id_iva=I.id_iva "
                + "where PG.id_plan_gomax in(" + idsPlanGomax + ")") ) {
            while(rs.next()){
                PlanGomax plan = new PlanGomax();
                plan.setIdPlanGomax(rs.getString("id_plan_gomax")!=null ? rs.getInt("id_plan_gomax") : 0 );
                plan.setNombrePlan(rs.getString("nombre_plan")!=null ? rs.getString("nombre_plan") : "");
                plan.setMeses(rs.getString("meses")!=null ? rs.getInt("meses") : 1);
                plan.setCosto(rs.getString("costo")!=null ? rs.getDouble("costo") : 0);
                plan.setIva(rs.getString("iva")!=null ? rs.getDouble("iva") : 0);
                plan.setTotal(rs.getString("total")!=null ? rs.getDouble("total") : 0);
                plan.setIdProducto(rs.getString("id_producto")!=null ? rs.getInt("id_producto") : 0);
                plan.setIdIva(rs.getString("id_iva")!=null ? rs.getInt("id_iva") : 0);
                plan.setIdPlanCuentaProducto(rs.getString("id_plan_cuenta_producto")!=null ? rs.getInt("id_plan_cuenta_producto") : 0);
                plan.setIdPlanCuentaIva(rs.getString("id_plan_cuenta_iva")!=null ? rs.getInt("id_plan_cuenta_iva") : 0);
                plan.setCodigo(rs.getString("codigo")!=null ? rs.getInt("codigo") : 4);
                plan.setPorcentaje(rs.getString("porcentaje")!=null ? rs.getInt("porcentaje") : 15);
                planes.add(plan);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return planes;
    }
    
}
