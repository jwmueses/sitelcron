/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.service;

import ec.com.saitel.gomax.dao.SuscripcionDAO;
import ec.com.saitel.gomax.model.Suscripcion;
import ec.com.saitel.gomax.model.Usuario;
import ec.com.saitel.gomax.utils.Jwt;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author sistemas
 */
@Path("seguridad")
public class SeguridadService 
{
    private final SuscripcionDAO suscripcionDao = new SuscripcionDAO();
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticar(Usuario usuario)
    {
        Suscripcion suscripcion = this.suscripcionDao.autenticar( usuario.getCorreoCuenta(), usuario.getClaveCuenta() );
        if(suscripcion.getCorreoCuenta().compareTo("") != 0) {
            
            Jwt jwt = new Jwt();
            String jwtTemp = jwt.generarJWT(suscripcion.getRazonSocial(), suscripcion.getCorreoCuenta()); //editado
            this.suscripcionDao.setJwt( suscripcion.getCorreoCuenta(), jwtTemp );
            
            if(suscripcion.getJwt() != null) {
                suscripcion.setJwt(jwtTemp); //editado
                this.suscripcionDao.cerrar();
                return Response.ok(suscripcion).build();
            }
            
//            return Response.ok(json).build();
        }
        
        this.suscripcionDao.cerrar();
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    
    
//    @POST
//    @Path("/clave")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response cambiarClave(SuscripcionClave suscripcionClave)
//    {
//        Suscripcion suscripcion = this.suscripcionDao.autenticar( suscripcionClave.getSuscripcion(), suscripcionClave.getClaveA() );
//        if(suscripcion.getSuscripcion().compareTo("") != 0) {
//            
//            if( !this.suscripcionDao.claveRegistrada( suscripcionClave.getSuscripcion(), suscripcionClave.getClaveN() ) ) {
//                
//                if( this.suscripcionDao.setClave( suscripcionClave.getSuscripcion(), suscripcionClave.getClaveN() ) ) {
//                    
//                    this.suscripcionDao.setClaveLog( suscripcionClave.getSuscripcion(), suscripcionClave.getClaveN() );
//                    this.suscripcionDao.cerrar();
//                    suscripcionClave.setClaveA("");
//                    suscripcionClave.setClaveN("");
//                    return Response.ok(suscripcionClave).build();
//                    
//                }
//                
//            } else{
//                
//                this.suscripcionDao.cerrar();
//                return Response.status(Response.Status.CONFLICT).build();
//                
//            }
//            
//        }
//        
//        this.suscripcionDao.cerrar();
//        return Response.status(Response.Status.NOT_FOUND).build();
//    }
    
//    @POST
//    @Path("/sesion")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response cerrarSesion(Suscripcion suscripcion)
//    {
//            
//        if( this.suscripcionDao.cerrarSesion( suscripcion.getCorreoCuenta() ) ) {
//
//            this.suscripcionDao.cerrar();
//            return Response.ok(suscripcion).build();
//
//        }
//        
//        this.suscripcionDao.cerrar();
//        return Response.status(Response.Status.NOT_FOUND).build();
//    }
    
    
}
