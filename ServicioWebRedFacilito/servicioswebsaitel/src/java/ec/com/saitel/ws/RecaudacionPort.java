/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.ws;

import ec.com.saitel.ws_client.Recaudacion;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author sistemas
 */
@Path("recaudacionport")
public class RecaudacionPort {

    private Recaudacion port;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RecaudacionPort
     */
    public RecaudacionPort() {
        port = getPort();
    }

    /**
     * Invokes the SOAP method facturar
     * @param clave resource URI parameter
     * @param idRegistroConsulta resource URI parameter
     * @param numDocumento resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("facturar/")
    public String getFacturar(@QueryParam("clave") String clave, @QueryParam("idRegistroConsulta") String idRegistroConsulta, @QueryParam("numDocumento") String numDocumento) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.lang.String result = port.facturar(clave, idRegistroConsulta, numDocumento);
                return result;
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method consulta
     * @param clave resource URI parameter
     * @param dni resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("consulta/")
    public String getConsulta(@QueryParam("clave") String clave, @QueryParam("dni") String dni) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.lang.String result = port.consulta(clave, dni);
                return result;
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method reversar
     * @param clave resource URI parameter
     * @param idRegistroConsulta resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("reversar/")
    public String getReversar(@QueryParam("clave") String clave, @QueryParam("idRegistroConsulta") String idRegistroConsulta) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.lang.String result = port.reversar(clave, idRegistroConsulta);
                return result;
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     *
     */
    private Recaudacion getPort() {
        try {
            // Call Web Service Operation
            ec.com.saitel.ws_client.Recaudacion_Service service = new ec.com.saitel.ws_client.Recaudacion_Service();
            ec.com.saitel.ws_client.Recaudacion p = service.getRecaudacionPort();
            return p;
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }
}
