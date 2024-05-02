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
package ec.com.saitel.gomax.service;

import ec.com.saitel.gomax.dao.PlanWebDAO;
import ec.com.saitel.gomax.model.PlanWeb;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author sistemas
 */
@Path("planestv")
public class PlanWebService 
{
    PlanWebDAO planDao = new PlanWebDAO();
            
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlanes()
    {
        List<PlanWeb> planes = this.planDao.getPlanes();
        return Response.ok(planes).build();
    }
    
}