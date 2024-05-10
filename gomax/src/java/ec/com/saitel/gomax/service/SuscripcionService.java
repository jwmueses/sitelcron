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

import ec.com.saitel.gomax.dao.ConfiguracionDAO;
import ec.com.saitel.gomax.dao.FacturaDAO;
import ec.com.saitel.gomax.dao.PlanGomaxDAO;
import ec.com.saitel.gomax.dao.PuntoEmisionDAO;
import ec.com.saitel.gomax.dao.SuscripcionDAO;
import ec.com.saitel.gomax.model.PlanGomax;
import ec.com.saitel.gomax.model.Suscripcion;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author sistemas
 */
@Path("suscripciones")
public class SuscripcionService 
{
    SuscripcionDAO suscripcionDao = new SuscripcionDAO();
            
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getSuscripciones()
//    {
//        List<Suscripcion> suscripciones = this.suscripcionDao.getSuscripciones("");
//        return Response.ok(suscripciones).build();
//    }
    
//    @GET
//    @Path("/{suscripcion}/{estado}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getSuscripcions(@PathParam("suscripcion") String suscripcion, @PathParam("estado") String estado)
//    {
//        String where = suscripcion.compareTo("NULL")!=0 ? "where lower(nombreSuscripcion) like '%"+suscripcion.toLowerCase()+"%'" : "";
//        if( estado.compareTo("NULL")!=0 ) {
//            where += (where.compareTo("")!=0 ? " and " : "where ") + "estado= " + estado;
//        }
//        List<Suscripcion> suscripcions = this.suscripcionDao.getSuscripciones(where);
//        this.suscripcionDao.cerrar();
//        return Response.ok(suscripcions).build();
//    }
    
    
    @GET
    @Path("/{correo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSuscripcionPorCorreo(@PathParam("correo") String correo)
    {
        Suscripcion suscripcion = this.suscripcionDao.getSuscripcionPorCorreo(correo);
        if( suscripcion.getJwt() != null ) {
            this.suscripcionDao.cerrar();
            return Response.ok(suscripcion).build();
        }
        this.suscripcionDao.cerrar();
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    
    @POST
    @Path("/suscribirseplan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPlanSuscripcion(Suscripcion suscripcion)
    {
        if( this.suscripcionDao.setPlanSuscripcion(suscripcion) ) {
            Suscripcion suscripcionNuevo = this.suscripcionDao.getSuscripcionPorCorreo( suscripcion.getCorreoCuenta() );
            this.suscripcionDao.cerrar();
            return Response.ok( suscripcionNuevo.getIdClienteSuscripcionGomax() ).build();
        }
        this.suscripcionDao.cerrar();
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
    
    @POST
    @Path("/suscripcionactualizar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPlanSuscripcionEd(Suscripcion suscripcion)
    {
        if( this.suscripcionDao.setPlanSuscripcionEdicion(suscripcion) ) {
            this.suscripcionDao.cerrar();
            return Response.ok().build();
        }
        this.suscripcionDao.cerrar();
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
  
    
    @PUT
    @Path("/facturarplan")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response facturarSuscripcion(String idClienteSuscripcionGomax)
    {
        // proceso de emision de la factura
        Suscripcion suscripcion = this.suscripcionDao.getSuscripcion(idClienteSuscripcionGomax);
        
        ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();
        String[] parametros = configuracionDAO.getParametros();
        configuracionDAO.cerrar();
        
        PuntoEmisionDAO puntoEmisionDAO = new PuntoEmisionDAO();
        String puntoEmision[] = puntoEmisionDAO.getIdPlanCuentaCajaGOMAX();
        puntoEmisionDAO.cerrar();
        
        PlanGomaxDAO planGomaxDao = new PlanGomaxDAO();
        List<PlanGomax> planGomax = planGomaxDao.getPlanesProducto( suscripcion.getIdPlanGomax() );
        planGomaxDao.cerrar();
        
        FacturaDAO facturaDAO = new FacturaDAO();
        facturaDAO.setCabecera( suscripcion, puntoEmision, parametros );
        facturaDAO.setDetalle( planGomax );
        String numFactura = facturaDAO.facturar(parametros);
        facturaDAO.cerrar();
        
        if( numFactura.compareTo("-1") != 0 ) {
            
            //  actualizo estado en la base de datos    
            suscripcion.setEstado("abierto, pendiente alta en gomax");
            if( this.suscripcionDao.setAltaSuscripcionPlan( suscripcion ) ) {
                
//                Dar de alta suscripcion en GOMAX 
//                Gomax gomax = new Gomax();
//                long partnerId = -1;
//                
//                if( suscripcion.getGomaxPartnerId() > 0 ) {
//                    
//                    partnerId = suscripcion.getGomaxPartnerId();
//                    
//                } else {
//                
//                    StringBuilder jsonUsuario = new StringBuilder();
//                    jsonUsuario.append("{\"name\":\"");
//                    jsonUsuario.append( suscripcion.getRazonSocial() );
//                    jsonUsuario.append("\",\"second_name\":\"");
//                    jsonUsuario.append( suscripcion.getRazonSocial() );
//                    jsonUsuario.append("\",\"email\":\"");
//                    jsonUsuario.append( suscripcion.getCorreoCuenta() );
//                    jsonUsuario.append("\",\"identifier\":\"");
//                    jsonUsuario.append( suscripcion.getRuc() );
//                    jsonUsuario.append("\",\"phone\":\"");
//                    jsonUsuario.append( suscripcion.getMovilClaro() );
//                    jsonUsuario.append("\",\"password\":\"");
//                    jsonUsuario.append( suscripcion.getClaveCuenta() );
//                    jsonUsuario.append("\",\"country\":\"EC\",\"address\":\"");
//                    jsonUsuario.append( suscripcion.getDireccion() );
//                    jsonUsuario.append("\"}");
//
//                    partnerId = gomax.crearUsuario( jsonUsuario.toString() );
//                }
//                
//                if( partnerId >= 0 ) {
//                    
//                    StringBuilder jsonContrato = new StringBuilder();
//                    jsonContrato.append("{\"partner_id\":\"");
//                    jsonContrato.append( partnerId );
//                    jsonContrato.append("\",\"items\":[");
//                    
//                    Iterator it = planGomax.iterator();
//                    while(it.hasNext()) {
//                        PlanGomax planGomaxDet = (PlanGomax)it.next();
//                        jsonContrato.append("{\"product_id\":\"");
//                        jsonContrato.append( planGomaxDet.getGomaxProduct() );
//                        jsonContrato.append("\",\"price\":\"");
//                        jsonContrato.append( planGomaxDet.getGomaxPrice() );
//                        jsonContrato.append("\",\"pricelist_id\":\"");
//                        jsonContrato.append( planGomaxDet.getGomaxPricelistId() );
//                        jsonContrato.append("\"},");
//                    }
//                    jsonContrato.deleteCharAt( jsonContrato.length() -1 );
//                    jsonContrato.append("]}");
//                    
//                    if (gomax.crearContrato( jsonContrato.toString() )) {
//                        suscripcion.setEstado("abierto");
//                    }
//                    suscripcion.setGomaxPartnerId(partnerId);
//                    
//                }
                
                this.suscripcionDao.setAltaSuscripcionPlan(suscripcion);
                this.suscripcionDao.cerrar();
                return Response.ok().build();
            }
        }
        
        this.suscripcionDao.cerrar();
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
    
    @PUT
    @Path("/renovarplan")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response renovarSuscripcion(String idClienteSuscripcionGomaxIdPlan)
    {
        String vecIDs[] = idClienteSuscripcionGomaxIdPlan.split("/");
        
        // proceso de emision de la factura
        Suscripcion suscripcion = this.suscripcionDao.getSuscripcion(vecIDs[0]);
        
        ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();
        String[] parametros = configuracionDAO.getParametros();
        configuracionDAO.cerrar();
        
        PuntoEmisionDAO puntoEmisionDAO = new PuntoEmisionDAO();
        String puntoEmision[] = puntoEmisionDAO.getIdPlanCuentaCajaGOMAX();
        puntoEmisionDAO.cerrar();
        
        PlanGomaxDAO planGomaxDao = new PlanGomaxDAO();
        List<PlanGomax> planGomax = planGomaxDao.getPlanesProducto( suscripcion.getIdPlanGomax() );
        planGomaxDao.cerrar();
        
        FacturaDAO facturaDAO = new FacturaDAO();
        facturaDAO.setCabecera( suscripcion, puntoEmision, parametros );
        facturaDAO.setDetalle( planGomax );
        String numFactura = facturaDAO.facturar(parametros);
        facturaDAO.cerrar();
        
        if( numFactura.compareTo("-1") != 0 ) {
            
//            Gomax gomax = new Gomax();
//            if( gomax.cancelarContrato( "{\"partner_id\":"+ suscripcion.getGomaxPartnerId() +"}" ) ) {
                suscripcion.setEstado("abierto");
//            } else {
//                suscripcion.setEstado("abierto, pendiente alta en gomax");
//            }
                
            //  actualizo estado en la base de datos    
            if( this.suscripcionDao.renovarSuscripcionPlan( suscripcion ) ) {
                this.suscripcionDao.cerrar();
                return Response.ok().build();
            }
        }
        
        this.suscripcionDao.cerrar();
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
    
    
    @DELETE
    @Path("/cancelarsuscripcion")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelarSuscripcion(String gomaxPartnerId)
    {
        //  dar de baja suscripcion en GOMAX 
//        Gomax gomax = new Gomax();
//        if ( gomax.cancelarContrato( "{\"partner_id\":"+gomaxPartnerId+"}" ) ) {
            
            this.suscripcionDao.cancelarSuscripcion(gomaxPartnerId, "cancelado");
            this.suscripcionDao.cerrar();
            return Response.ok().build();

//        } else {
//            
//            this.suscripcionDao.cancelarSuscripcion(gomaxPartnerId, "cancelado, pendiente baja en gomax");
//            this.suscripcionDao.cerrar();
//            return Response.status(Response.Status.NOT_FOUND).build();
//
//        }
        
    }
    
    @DELETE
    @Path("/cancelarproducto")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelarProducto(String gomaxPartnerIdlineId)
    {
//        String vecIDs[] = gomaxPartnerIdlineId.split("/");
        //  dar de baja suscripcion en GOMAX 
//        Gomax gomax = new Gomax();
//        if ( gomax.cancelarContrato( "{\"partner_id\":"+vecIDs[0]+",\"line_id\":"+vecIDs[1]+"}" ) ) {
            
//            this.suscripcionDao.cancelarProducto(vecIDs[1], "cancelado");
//            this.suscripcionDao.cerrar();
            return Response.ok().build();

//        } else {
//            
//            this.suscripcionDao.cancelarSuscripcion(gomaxPartnerId, "cancelado, pendiente baja en gomax");
//            this.suscripcionDao.cerrar();
//            return Response.status(Response.Status.NOT_FOUND).build();
//
//        }
        
    }
    
}