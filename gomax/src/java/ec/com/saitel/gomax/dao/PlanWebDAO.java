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

import ec.com.saitel.gomax.model.PlanWeb;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sistemas
 */
public class PlanWebDAO extends BaseDatos {
    
    public List<PlanWeb> getPlanes()
    {
        List<PlanWeb> planes = new ArrayList();
        
        try( ResultSet rs = this.consulta("select * from tbl_plan_gomax where estado order by nombre_plan") ) {
            while(rs.next()){
                PlanWeb plan = new PlanWeb();
                plan.setIdPlanGomax(rs.getString("id_plan_gomax")!=null ? rs.getInt("id_plan_gomax") : 0 );
                plan.setNombrePlan(rs.getString("nombre_plan")!=null ? rs.getString("nombre_plan") : "");
                plan.setDescripcion(rs.getString("descripcion")!=null ? rs.getString("descripcion") : "");
                plan.setMeses(rs.getString("meses")!=null ? rs.getInt("meses") : 1);
                plan.setCosto(rs.getString("costo")!=null ? rs.getFloat("costo") : 0);
                planes.add(plan);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return planes;
    }
    
}
