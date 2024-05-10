/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.com.saitel.gomax.service;

import ec.com.saitel.gomax.dao.ConfiguracionDAO;
import ec.com.saitel.gomax.dao.DocumentoPagomedioDAO;
import ec.com.saitel.gomax.model.DatosPagomedio;
import ec.com.saitel.gomax.utils.Pagomedio;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author pc01
 */

@Path("pagomedio")

public class PagomedioService {
    
    @POST
    @Path("/generarpagomedio")
    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagomedio(DatosPagomedio datosPagomedio)
    {
        Pagomedio pagomedio = new Pagomedio();
        ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();
        DocumentoPagomedioDAO documentoPagomedioDAO = new DocumentoPagomedioDAO();
        String urlPago = pagomedio.crearEnlacePago(datosPagomedio, configuracionDAO.getTokenPagomedio());
        if(!urlPago.equals("")){
           documentoPagomedioDAO.guardar(datosPagomedio);
           documentoPagomedioDAO.cerrar();
           configuracionDAO.cerrar();
           return Response.ok(urlPago).build();
        }
        configuracionDAO.cerrar();
        return Response.status(Response.Status.CONFLICT).build();
    }
    
    @POST
    @Path("/actualizarpagomedio")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void setPagomedio(MultivaluedMap<String, String> formParams, @Context HttpServletResponse response) throws IOException
    {
        String value = formParams.getFirst("customValue");
        Pagomedio pagomedio = new Pagomedio();
        ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();
        boolean actualizado = pagomedio.actualizarDatosPago( configuracionDAO.getTokenPagomedio(), value);
        if(actualizado){
           configuracionDAO.cerrar();
//           return Response.status(Response.Status.OK).build();
           response.sendRedirect("http://138.185.137.117:8080/gomax/html/television_pago_exitoso.html?idSus="+value);
        }
        configuracionDAO.cerrar();
        response.sendRedirect("http://138.185.137.117:8080/gomax/html/television_pago_exitoso.html");
    }
}
