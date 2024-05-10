/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.service;

import ec.com.saitel.gomax.dao.SuscripcionDAO;
import ec.com.saitel.gomax.model.Suscripcion;
import ec.com.saitel.gomax.utils.Jwt;
import ec.com.saitel.gomax.utils.Notificacion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author sistemas
 */
@Path("registro")
public class SuscripcionRegistroService 
{
    private final SuscripcionDAO suscripcionDao = new SuscripcionDAO();
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveSuscripcion(Suscripcion suscripcion)
    {
        List<Suscripcion> suscripciones = this.suscripcionDao.getSuscripciones("where lower(correo_cuenta) = '"+suscripcion.getCorreoCuenta().toLowerCase()+"'");
        if( suscripciones.isEmpty() ) {
            String pk = this.suscripcionDao.guardar(suscripcion);
            if( pk.compareTo("-1") != 0 ) {
                
                Jwt jwt = new Jwt();
                String cadJwt = jwt.generarJWT( suscripcion.getCorreoCuenta().replaceAll("@.*", ""), suscripcion.getCorreoCuenta(), 2880 ); // expira en 48 horas
                Key key = jwt.getKey();
                byte[] keyEncodes = key.getEncoded();
                
                this.suscripcionDao.setJwtKey( suscripcion.getCorreoCuenta().toLowerCase(), keyEncodes );
//                Key key2 = Keys.hmacShaKeyFor(keyEncodes);
                
                Notificacion notificacion = new Notificacion();
                notificacion.enviarVerificacionCorreo( suscripcion.getCorreoCuenta().toLowerCase(), cadJwt, pk );
                
                return Response.status(Response.Status.CREATED).build();
//                List<Suscripcion> suscripcionNuevo = this.suscripcionDao.getSuscripcion( Integer.parseInt(pk) );
//                this.suscripcionDao.cerrar();
//                return Response.ok(suscripcionNuevo).build();
            }
            
        } else {
            this.suscripcionDao.cerrar();
            return Response.status(Response.Status.CONFLICT).build();
        }
        this.suscripcionDao.cerrar();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmarCorreoSuscripcion(Suscripcion suscripcion)
    {
        Key key = Keys.hmacShaKeyFor( this.suscripcionDao.getJwtKey( suscripcion.getCorreoCuenta().toLowerCase() ) );
        
        Claims claims = Jwts.parser()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(suscripcion.getJwt())
                            .getBody();
        
        String jwtCorreoCuenta = (String) claims.get("correo");
        Date jwtFecha = claims.getExpiration();
        Date fecha = new Date();
        
        if( fecha.compareTo(jwtFecha) < 0 ) {
            if( suscripcion.getCorreoCuenta().compareTo(jwtCorreoCuenta) == 0 ) {
                if( this.suscripcionDao.setCorreoConfirmado(jwtCorreoCuenta) ) {
                    this.suscripcionDao.cerrar();
                    return Response.status(Response.Status.OK).build();
                } else {
                    this.suscripcionDao.cerrar();
                    return Response.status(Response.Status.NOT_MODIFIED).build();
                }
            } else {
                this.suscripcionDao.cerrar();
                return Response.status(Response.Status.CONFLICT).build();
            }
        } else {
            this.suscripcionDao.cerrar();
            return Response.status(Response.Status.REQUEST_TIMEOUT).build();
        }
        
//        this.suscripcionDao.cerrar();
//        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
    
    
}
