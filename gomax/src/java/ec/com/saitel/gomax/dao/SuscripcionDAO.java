/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.dao;

import ec.com.saitel.gomax.model.Suscripcion;
import io.jsonwebtoken.SignatureAlgorithm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sistemas
 */
public class SuscripcionDAO extends BaseDatos 
{
    public Suscripcion autenticar(String correo, String clave) 
    {
        Suscripcion suscripcion = new Suscripcion();
        
        try( ResultSet rs = this.consulta("select * from vta_cliente_suscripcion_gomax where correo_cuenta = '"+correo.toLowerCase()+"' and clave_cuenta=('"+clave+"')") ) {
            if(rs.next()){
                suscripcion.setIdClienteSuscripcionGomax(rs.getString("id_cliente_suscripcion_gomax")!=null ? rs.getInt("id_cliente_suscripcion_gomax") : 0 );
                suscripcion.setIdCliente(rs.getString("id_cliente")!=null ? rs.getInt("id_cliente") : 0);
                suscripcion.setIdPlanGomax(rs.getString("id_plan_gomax")!=null ? rs.getInt("id_plan_gomax") : 0);
                suscripcion.setNumContrato(rs.getString("num_contrato")!=null ? rs.getString("num_contrato") : "");
                suscripcion.setFechaSuscripcion(rs.getString("fecha_suscripcion")!=null ? rs.getString("fecha_suscripcion") : "");
                suscripcion.setFechaTermino(rs.getString("fecha_termino")!=null ? rs.getString("fecha_termino") : "");
                suscripcion.setCobrar(rs.getString("cobrar")!=null ? rs.getBoolean("cobrar") : null);
                suscripcion.setCorreoCuenta(rs.getString("correo_cuenta")!=null ? rs.getString("correo_cuenta") : "");
                suscripcion.setCorreoConfirmado(rs.getString("correo_confirmado")!=null ? rs.getBoolean("correo_confirmado") : null);
                suscripcion.setJwt(rs.getString("jwt")!=null ? rs.getString("jwt") : "");
                suscripcion.setDebitoAutomatico(rs.getString("debito_automatico")!=null ? rs.getBoolean("debito_automatico") : null);
                suscripcion.setEstado(rs.getString("estado")!=null ? rs.getString("estado") : "");
                suscripcion.setTipoDocumento(rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "");
                suscripcion.setRuc(rs.getString("ruc")!=null ? rs.getString("ruc") : "");
                suscripcion.setRazonSocial(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "");
                suscripcion.setDireccion(rs.getString("direccion")!=null ? rs.getString("direccion") : "");
                suscripcion.setMovilClaro(rs.getString("movil_claro")!=null ? rs.getString("movil_claro") : "");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return suscripcion;
    }
    
    public Suscripcion getSuscripcionPorCorreo(String correo) 
    {
        Suscripcion suscripcion = new Suscripcion();
        
        try( ResultSet rs = this.consulta("select * from vta_cliente_suscripcion_gomax where correo_cuenta = '"+correo+"'") ) {
            if(rs.next()){
                suscripcion.setIdClienteSuscripcionGomax(rs.getString("id_cliente_suscripcion_gomax")!=null ? rs.getInt("id_cliente_suscripcion_gomax") : 0 );
                suscripcion.setIdCliente(rs.getString("id_cliente")!=null ? rs.getInt("id_cliente") : 0);
                suscripcion.setIdPlanGomax(rs.getString("id_plan_gomax")!=null ? rs.getInt("id_plan_gomax") : 0);
                suscripcion.setNumContrato(rs.getString("num_contrato")!=null ? rs.getString("num_contrato") : "");
                suscripcion.setFechaSuscripcion(rs.getString("fecha_suscripcion")!=null ? rs.getString("fecha_suscripcion") : "");
                suscripcion.setFechaTermino(rs.getString("fecha_termino")!=null ? rs.getString("fecha_termino") : "");
                suscripcion.setCobrar(rs.getString("cobrar")!=null ? rs.getBoolean("cobrar") : null);
                suscripcion.setCorreoCuenta(rs.getString("correo_cuenta")!=null ? rs.getString("correo_cuenta") : "");
                suscripcion.setCorreoConfirmado(rs.getString("correo_confirmado")!=null ? rs.getBoolean("correo_confirmado") : null);
//                suscripcion.setJwt(rs.getString("jwt")!=null ? rs.getString("jwt") : "");
                suscripcion.setDebitoAutomatico(rs.getString("debito_automatico")!=null ? rs.getBoolean("debito_automatico") : null);
                suscripcion.setEstado(rs.getString("estado")!=null ? rs.getString("estado") : "");
                suscripcion.setTipoDocumento(rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "");
                suscripcion.setRuc(rs.getString("ruc")!=null ? rs.getString("ruc") : "");
                suscripcion.setRazonSocial(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "");
                suscripcion.setDireccion(rs.getString("direccion")!=null ? rs.getString("direccion") : "");
                suscripcion.setMovilClaro(rs.getString("movil_claro")!=null ? rs.getString("movil_claro") : "");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return suscripcion;
    }
    
    public Suscripcion getSuscripcion(String idClienteSuscripcionGomax) 
    {
        Suscripcion suscripcion = new Suscripcion();
        
        try( ResultSet rs = this.consulta("select * from vta_cliente_suscripcion_gomax where id_cliente_suscripcion_gomax = '"+idClienteSuscripcionGomax+"'") ) {
            if(rs.next()){
                suscripcion.setIdClienteSuscripcionGomax(rs.getString("id_cliente_suscripcion_gomax")!=null ? rs.getInt("id_cliente_suscripcion_gomax") : 0 );
                suscripcion.setIdCliente(rs.getString("id_cliente")!=null ? rs.getInt("id_cliente") : 0);
                suscripcion.setIdPlanGomax(rs.getString("id_plan_gomax")!=null ? rs.getInt("id_plan_gomax") : 0);
                suscripcion.setNumContrato(rs.getString("num_contrato")!=null ? rs.getString("num_contrato") : "");
                suscripcion.setFechaSuscripcion(rs.getString("fecha_suscripcion")!=null ? rs.getString("fecha_suscripcion") : "");
                suscripcion.setFechaTermino(rs.getString("fecha_termino")!=null ? rs.getString("fecha_termino") : "");
                suscripcion.setCobrar(rs.getString("cobrar")!=null ? rs.getBoolean("cobrar") : null);
                suscripcion.setCorreoCuenta(rs.getString("correo_cuenta")!=null ? rs.getString("correo_cuenta") : "");
                suscripcion.setCorreoConfirmado(rs.getString("correo_confirmado")!=null ? rs.getBoolean("correo_confirmado") : null);
//                suscripcion.setJwt(rs.getString("jwt")!=null ? rs.getString("jwt") : "");
                suscripcion.setDebitoAutomatico(rs.getString("debito_automatico")!=null ? rs.getBoolean("debito_automatico") : null);
                suscripcion.setEstado(rs.getString("estado")!=null ? rs.getString("estado") : "");
                suscripcion.setTipoDocumento(rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "");
                suscripcion.setRuc(rs.getString("ruc")!=null ? rs.getString("ruc") : "");
                suscripcion.setRazonSocial(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "");
                suscripcion.setDireccion(rs.getString("direccion")!=null ? rs.getString("direccion") : "");
                suscripcion.setMovilClaro(rs.getString("movil_claro")!=null ? rs.getString("movil_claro") : "");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return suscripcion;
    }
    
    public Suscripcion getSuscripcionPorJwt(String jwt) 
    {
        Suscripcion suscripcion = new Suscripcion();
        
        try( ResultSet rs = this.consulta("select * from vta_cliente_suscripcion_gomax where jwt = '"+jwt+"'") ) {
            if(rs.next()){
                suscripcion.setIdClienteSuscripcionGomax(rs.getString("id_cliente_suscripcion_gomax")!=null ? rs.getInt("id_cliente_suscripcion_gomax") : 0 );
                suscripcion.setIdCliente(rs.getString("id_cliente")!=null ? rs.getInt("id_cliente") : 0);
                suscripcion.setIdPlanGomax(rs.getString("id_plan_gomax")!=null ? rs.getInt("id_plan_gomax") : 0);
                suscripcion.setNumContrato(rs.getString("num_contrato")!=null ? rs.getString("num_contrato") : "");
                suscripcion.setFechaSuscripcion(rs.getString("fecha_suscripcion")!=null ? rs.getString("fecha_suscripcion") : "");
                suscripcion.setFechaTermino(rs.getString("fecha_termino")!=null ? rs.getString("fecha_termino") : "");
                suscripcion.setCobrar(rs.getString("cobrar")!=null ? rs.getBoolean("cobrar") : null);
                suscripcion.setCorreoCuenta(rs.getString("correo_cuenta")!=null ? rs.getString("correo_cuenta") : "");
                suscripcion.setCorreoConfirmado(rs.getString("correo_confirmado")!=null ? rs.getBoolean("correo_confirmado") : null);
//                suscripcion.setJwt(rs.getString("jwt")!=null ? rs.getString("jwt") : "");
                suscripcion.setDebitoAutomatico(rs.getString("debito_automatico")!=null ? rs.getBoolean("debito_automatico") : null);
                suscripcion.setEstado(rs.getString("estado")!=null ? rs.getString("estado") : "");
                suscripcion.setTipoDocumento(rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "");
                suscripcion.setRuc(rs.getString("ruc")!=null ? rs.getString("ruc") : "");
                suscripcion.setRazonSocial(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "");
                suscripcion.setDireccion(rs.getString("direccion")!=null ? rs.getString("direccion") : "");
                suscripcion.setMovilClaro(rs.getString("movil_claro")!=null ? rs.getString("movil_claro") : "");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return suscripcion;
    }
    
    public List<Suscripcion> getSuscripciones(String where) 
    {
        List<Suscripcion> suscripciones = new ArrayList();
        
        try( ResultSet rs = this.consulta("select * from vta_cliente_suscripcion_gomax "+where+" order by razon_social, num_contrato") ) {
            while(rs.next()){
                Suscripcion suscripcion = new Suscripcion();
                suscripcion.setIdClienteSuscripcionGomax(rs.getString("id_cliente_suscripcion_gomax")!=null ? rs.getInt("id_cliente_suscripcion_gomax") : 0 );
                suscripcion.setIdCliente(rs.getString("id_cliente")!=null ? rs.getInt("id_cliente") : 0);
                suscripcion.setIdPlanGomax(rs.getString("id_plan_gomax")!=null ? rs.getInt("id_plan_gomax") : 0);
                suscripcion.setNumContrato(rs.getString("num_contrato")!=null ? rs.getString("num_contrato") : "");
                suscripcion.setFechaSuscripcion(rs.getString("fecha_suscripcion")!=null ? rs.getString("fecha_suscripcion") : "");
                suscripcion.setFechaTermino(rs.getString("fecha_termino")!=null ? rs.getString("fecha_termino") : "");
                suscripcion.setCobrar(rs.getString("cobrar")!=null ? rs.getBoolean("cobrar") : null);
                suscripcion.setCorreoCuenta(rs.getString("correo_cuenta")!=null ? rs.getString("correo_cuenta") : "");
                suscripcion.setCorreoConfirmado(rs.getString("correo_confirmado")!=null ? rs.getBoolean("correo_confirmado") : null);
//                suscripcion.setJwt(rs.getString("jwt")!=null ? rs.getString("jwt") : "");
                suscripcion.setDebitoAutomatico(rs.getString("debito_automatico")!=null ? rs.getBoolean("debito_automatico") : null);
                suscripcion.setEstado(rs.getString("estado")!=null ? rs.getString("estado") : "");
                suscripcion.setTipoDocumento(rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "");
                suscripcion.setRuc(rs.getString("ruc")!=null ? rs.getString("ruc") : "");
                suscripcion.setRazonSocial(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "");
                suscripcion.setDireccion(rs.getString("direccion")!=null ? rs.getString("direccion") : "");
                suscripcion.setMovilClaro(rs.getString("movil_claro")!=null ? rs.getString("movil_claro") : "");
                suscripciones.add(suscripcion);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return suscripciones;
    }
    
    public String guardar(Suscripcion suscripcion)
    {
//        List<Suscripcion> suscripciones = this.getSuscripciones("where correo_cuenta = '"+suscripcion.getCorreoCuenta()+"'");
//        if( !suscripciones.isEmpty() ){
//            Suscripcion planEliminado = suscripciones.get(0);
//            if(this.ejecutar("update tbl_cliente_suscripcion_gomax set estado='abierto' where correo_cuenta = '"+planEliminado.getCorreoCuenta()+"'") ) {
//                return String.valueOf( planEliminado.getIdPlanGomax() );
//            } else {
//                return "-1";
//            }
//        }
        return this.insertar("insert into tbl_cliente_suscripcion_gomax(correo_cuenta, clave_cuenta) values('"+
                suscripcion.getCorreoCuenta().toLowerCase()+"', '"+suscripcion.getClaveCuenta()+"' );");
    }
    
    public boolean setPlanSuscripcion(Suscripcion suscripcion)
    {
        if( suscripcion.getIdCliente() <= 0 ) {
            try{
                ResultSet rs = this.consulta("select * from tbl_cliente where ruc='"+suscripcion.getRuc()+"'");
                if( this.getFilas(rs) > 0 ) {
                    if(rs.next()) {
                        suscripcion.setIdCliente(rs.getString("id_cliente")!=null ? rs.getLong("id_cliente") : -1 );
                        suscripcion.setRuc( rs.getString("ruc")!=null ? rs.getString("ruc") : "ruc" );
                        suscripcion.setRazonSocial(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "" );
                        suscripcion.setDireccion(rs.getString("direccion")!=null ? rs.getString("direccion") : "" );
                        suscripcion.setMovilClaro(rs.getString("movil_claro")!=null ? rs.getString("movil_claro") : "" );
                        rs.close();
                    }
                } else {
                    String idCliente = this.insertar("insert into tbl_cliente(ruc, razon_social, direccion, movil_claro, email, observacion) values('"+suscripcion.getRuc() + 
                            "', '"+suscripcion.getRazonSocial()+"', '"+suscripcion.getDireccion()+"', '"+suscripcion.getMovilClaro() + 
                            "', '"+suscripcion.getCorreoCuenta()+"', 'REGISTRO VIA TV GOMAX')");
                    
                    suscripcion.setIdCliente( Long.parseLong(idCliente) );
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        if( suscripcion.getIdCliente() > 0 ) {
            return this.ejecutar("update tbl_cliente_suscripcion_gomax set id_cliente='"+suscripcion.getIdCliente()+"', id_plan_gomax='"+suscripcion.getIdPlanGomax() + 
                "', fecha_suscripcion='"+suscripcion.getFechaSuscripcion()+"', fecha_termino=('"+suscripcion.getFechaSuscripcion()+
                "'::date + '1 month'::interval - '1 day'::interval)::date, estado='pendiente pago' where correo_cuenta = '"+suscripcion.getCorreoCuenta()+"'");
        }
        
        return false;
    }
    
    public boolean setPlanSuscripcionEdicion(Suscripcion suscripcion)
    {
        if( suscripcion.getIdCliente() <= 0 ) {
            try{
                String idCliente = this.insertar("insert into tbl_cliente(ruc, razon_social, direccion, movil_claro, email, observacion) values('"+suscripcion.getRuc() + 
                        "', '"+suscripcion.getRazonSocial()+"', '"+suscripcion.getDireccion()+"', '"+suscripcion.getMovilClaro() + 
                        "', '"+suscripcion.getCorreoCuenta()+"', 'REGISTRO VIA TV GOMAX')");

                suscripcion.setIdCliente( Long.parseLong(idCliente) );
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        if( suscripcion.getIdCliente() > 0 ) {
            return this.ejecutar("update tbl_cliente_suscripcion_gomax set id_cliente='"+suscripcion.getIdCliente()+"', id_plan_gomax='"+suscripcion.getIdPlanGomax() + 
                "', fecha_suscripcion='"+suscripcion.getFechaSuscripcion()+"', fecha_termino=('"+suscripcion.getFechaSuscripcion()+
                "'::date + '1 month'::interval - '1 day'::interval)::date, estado='pendiente pago' where correo_cuenta = '"+suscripcion.getCorreoCuenta()+"'");
        }
        
        return false;
    }
    
    public boolean setAltaSuscripcionPlan(Suscripcion suscripcion)
    {
        return this.ejecutar("update tbl_cliente_suscripcion_gomax set fecha_suscripcion=now()::date, estado='" + suscripcion.getEstado() 
                + "', gomax_partner_id=" + suscripcion.getGomaxPartnerId() + ", cobrar=false where correo_cuenta = '"+suscripcion.getCorreoCuenta()+"'");
    }
    
    public boolean renovarSuscripcionPlan(Suscripcion suscripcion)
    {
        return this.ejecutar("update tbl_cliente_suscripcion_gomax set fecha_suscripcion = case when fecha_termino < now()::date then now()::date else (fecha_suscripcion + '1 month'::interval)::date end, id_plan_gomax='"
                + suscripcion.getIdPlanGomax() + "' where correo_cuenta = '"+suscripcion.getCorreoCuenta()+"'");
    }
    
    public boolean cancelarSuscripcion( String gomaxPartnerId, String estado )
    {
        return this.ejecutar("update tbl_cliente_suscripcion_gomax set estado='"+estado+"' where gomax_partner_id = '" + gomaxPartnerId + "'");
    }
    
    public boolean setJwt(String correCuenta, String jwt)
    {
        return this.ejecutar("update tbl_cliente_suscripcion_gomax set jwt='"+jwt+"' where correo_cuenta='" + correCuenta + "'" );
    }
    
    public boolean setCorreoConfirmado(String correCuenta)
    {
        return this.ejecutar("update tbl_cliente_suscripcion_gomax set correo_confirmado=true where correo_cuenta='" + correCuenta + "'" );
    }
    
    public byte[] getJwtKey(String correCuenta)
    {
        byte[] jwtKey = null;
        try{
            ResultSet rs = this.consulta("select jwt_key from tbl_cliente_suscripcion_gomax where correo_cuenta='" + correCuenta + "'");
            if(rs.next()) {
                jwtKey = rs.getBytes("jwt_key")!=null ? rs.getBytes("jwt_key") : null;
                rs.close();
            }
        } catch(Exception e) {
            e.printStackTrace();    
        }
        return jwtKey;
    }
    
    public boolean setJwtKey(String correCuenta, byte[] jwtKey)
    {
        boolean ok = false;
        try{
            Connection con = this.getConexion();
            PreparedStatement ps = con.prepareStatement("update tbl_cliente_suscripcion_gomax set jwt_key=? where correo_cuenta='" + correCuenta + "'");
            ps.setBytes(1, jwtKey);
            ps.executeUpdate();
            ps.close();
            ok=true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean cerrarSesion(String correCuenta)
    {
        return this.ejecutar("update tbl_cliente_suscripcion_gomax set jwt=null where correo_cuenta='" + correCuenta + "'" );
    }
    
    public String getJwt(long idClienteSuscripcionGomax){
        
        String jwt = "";
        try{
            ResultSet rs = this.consulta("select jwt from tbl_cliente_suscripcion_gomax where id_cliente_suscripcion_gomax=" + idClienteSuscripcionGomax);
            if(rs.next()) {
                jwt = rs.getString("jwt")!=null ? rs.getString("jwt") : "";
                rs.close();
            }
        } catch(Exception e) {
            e.printStackTrace();    
        }
        return jwt;
    }
    
}
