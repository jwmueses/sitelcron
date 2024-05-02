/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sistemas
 */
public class Jwt 
{
    private Key key;
    
    public String generarJWT(String razonSocial, String correo)
    {
//        long tiempo = System.currentTimeMillis();
//        long tiempoAExpirar = tiempo + (EXPIRACION_EN_MINUTOS * 60000);
//        Date dateAExpirar = new Date(tiempoAExpirar);
        
        String jwt = Jwts.builder()                     // (1)
            .header()                                   // (2) optional
                .add("alg", "HS512")
                .add("typ", "JWT")
                .and()

            .subject( razonSocial )                             // (3) JSON Claims, or
            .claim("cliente", correo )
//            .expiration(dateAExpirar)
            //.content(aByteArray, "text/plain")        //     any byte[] content, with media type

            .signWith( secretKeyFor(SignatureAlgorithm.HS512) )                      // (4) if signing, or
            //.encryptWith(key, keyAlg, encryptionAlg)  //     if encrypting

            .compact();                                 // (5)
        return jwt;
    }
    
    public String generarJWT(String razonSocial, String correo, int expiraMinutos)
    {
        long tiempo = System.currentTimeMillis();
        long tiempoAExpirar = tiempo + (expiraMinutos * 60000);
        Date dateAExpirar = new Date(tiempoAExpirar);
        
        Map<String, String> claims = new HashMap();
        claims.put("cliente", razonSocial);
        claims.put("correo", correo);
        
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        
        String jwt = Jwts.builder()                     // (1)
            .header()                                   // (2) optional
                .add("alg", "HS512")
                .add("typ", "JWT")
                .and()

            .subject( razonSocial )                             // (3) JSON Claims, or
            .claims( claims )
//            .claim("correo", correo )
            .expiration(dateAExpirar)
            //.content(aByteArray, "text/plain")        //     any byte[] content, with media type

            .signWith( key )                      // (4) if signing, or
            //.encryptWith(key, keyAlg, encryptionAlg)  //     if encrypting

            .compact();                                 // (5)
        return jwt;
    }
    
    public Key getKey()
    {
        return key;
    }
    
    public boolean cumpleComplejidadClave(String clave)
    {
        clave = this.decodificarURI(clave);
        String patron = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&\\+\\-\\*=\\/!¡¿?\\}\\{\\]\\[\\)\\(\"·])(?=\\S+$).{8,}";
        return clave.matches(patron);
    }
    
    /**
 * Funci�n que decodifica una cadena previamente codificada en formato propietario.
 * @param cad cadena a decodificar.
 * @return una cadena decodificada.
 */
    public String decodificarURI(String cad)
    {
        cad = cad.replace("_^0;", "&");
        cad = cad.replace("_^1;", "+");
        cad = cad.replace("_^2;", "%");
        cad = cad.replace("_^3;", "''");
        cad = cad.replace("\\", "/");
        cad = cad.replace("^", "/");
        //cad = cad.replace("|", "/");
        cad = cad.replace("\"", "''\''");
        cad = cad.replace("\n", ". ");
        cad = cad.replace("\r", ". ");
        cad = cad.replace("\t", " ");
//        this.SQL = cad;
        return cad;
    }
    
}
