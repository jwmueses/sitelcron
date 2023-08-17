/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.lib;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.ApiConnectionException;
import me.legrange.mikrotik.MikrotikApiException;

/**
 *
 * @author sistemas
 */
public class Mikrotik extends DataBase{
    
    private ApiConnection conexion = null;
    
    public Mikrotik(String m, int p, String db, String u, String c) 
    {
        super(m, p, db, u, c);
    }
    
    public boolean conectar(String idSucursal, String ip)
    {
        boolean ok = false;
        try{
            ResultSet rs = this.consulta("select servidor, usuario, clave, puerto from tbl_servidor_ftp where estado and id_sucursal="+idSucursal+" and position('"+ ip.replaceAll("\\d*[/]\\d*", "") +"' in subredes)>0 and id_servidor_ftp<>35");
//            ResultSet rs = this.consulta("select servidor, usuario, clave, puerto from tbl_servidor_ftp where id_servidor_ftp=35");
            if(rs.next()){
                String servidor = rs.getString("servidor")!=null ? rs.getString("servidor").replaceAll("[/]\\d*", "") : "";
                String usuario = rs.getString("usuario")!=null ? rs.getString("usuario") : "";
                String clave = rs.getString("clave")!=null ? rs.getString("clave") : "";
                ok = this.MikrotikConectar(servidor, usuario, clave);
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }  
        return ok;
    }
    
    public boolean conectar(String ip, String usuario, String clave)
    {
        return this.MikrotikConectar(ip, usuario, clave);
    }
    
    public ResultSet getInfoInstalacion(String idInstalacion)
    {
        return this.consulta("SELECT distinct id_sucursal, razon_social || ' ' || id_instalacion as razon_social, ip::varchar, P.burst_limit, \n" +
            "I.plan, P.max_limit, case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, estado_servicio, " + 
            "idMikrotikActivo, idMikrotikPlan, idMikrotikCola \n" +
            "FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio \n" +
            "where id_instalacion = " + idInstalacion);
    }
    
    public boolean altaDeInstalacionEnServidor(String idInstalacion)
    {
        try{
            ResultSet rs = this.getInfoInstalacion(idInstalacion);
            if(rs.next()){
                String cliente = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                String ip = rs.getString("ip")!=null ? rs.getString("ip") : "";
                String plan = rs.getString("plan")!=null ? rs.getString("plan") : "";
                String burst_limit = rs.getString("burst_limit")!=null ? rs.getString("burst_limit") : "";
                String max_limit = rs.getString("max_limit")!=null ? rs.getString("max_limit") : "";
                String prioridad = rs.getString("prioridad")!=null ? rs.getString("prioridad") : "";
                
                String idCola = "-1";
                String idPlan = "-1";
                String idActivo = this.MikrotikAdd("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=activos");
                if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                    idPlan = this.MikrotikAdd("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=corporativos");
                    idCola = this.MikrotikAdd("/queue/simple/add max-limit="+max_limit+"k/"+max_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                }else if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                    idPlan = this.MikrotikAdd("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=residenciales");
                    idCola = this.MikrotikAdd("/queue/simple/add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                    idPlan = this.MikrotikAdd("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=small");
                    idCola = this.MikrotikAdd("/queue/simple/add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                    idPlan = this.MikrotikAdd("/ip/firewall/address-list/add address="+ip+" comment=\""+cliente+"\" list=nocturnos");
                    idCola = this.MikrotikAdd("/queue/simple/add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad);
                }
                
                if(idCola.compareTo("-1")!=0 && idPlan.compareTo("-1")!=0 && idActivo.compareTo("-1")!=0) {
                    this.ejecutar("update tbl_instalacion set idMikrotikActivo='"+idActivo+"', idMikrotikPlan='"+idPlan+"', idMikrotikCola='"+idCola+"' where id_instalacion="+idInstalacion);
                }
                
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }  
        
        return false;
    }
    
    public boolean actualizarInstalacionEnServidor(String idInstalacion)
    {
        boolean ok = false;
        try{
            
//            this.consulta("select proc_generarprefacturaInstalacion("+idInstalacion+")");
            
            this.consulta("select setEstadoInstalacion("+idInstalacion+")");
            
            ResultSet rs = this.getInfoInstalacion(idInstalacion);
            if(rs.next()){
                
                String estadoServicio = rs.getString("estado_servicio")!=null ? rs.getString("estado_servicio") : "activos";
                String idMikrotikActivo = rs.getString("idMikrotikActivo")!=null ? rs.getString("idMikrotikActivo") : "";
                String idMikrotikPlan = rs.getString("idMikrotikPlan")!=null ? rs.getString("idMikrotikPlan") : "";
                String idMikrotikCola = rs.getString("idMikrotikCola")!=null ? rs.getString("idMikrotikCola") : "";
                
                if(estadoServicio.compareTo("a")==0) {
                    
                    String cliente = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                    String ip = rs.getString("ip")!=null ? rs.getString("ip") : "";
                    String plan = rs.getString("plan")!=null ? rs.getString("plan") : "";
                    String burst_limit = rs.getString("burst_limit")!=null ? rs.getString("burst_limit") : "";
                    String max_limit = rs.getString("max_limit")!=null ? rs.getString("max_limit") : "";
                    String prioridad = rs.getString("prioridad")!=null ? rs.getString("prioridad") : "";
                    
                    this.MikrotikAdd("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=activos .id=" + idMikrotikActivo);
                    if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                        this.MikrotikAdd("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=corporativos .id=" + idMikrotikPlan);
                        this.MikrotikAdd("/queue/simple/set max-limit="+max_limit+"k/"+max_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                    }else if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                        this.MikrotikAdd("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=residenciales .id=" + idMikrotikPlan);
                        this.MikrotikAdd("/queue/simple/set max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                    }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                        this.MikrotikAdd("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=small .id=" + idMikrotikPlan);
                        this.MikrotikAdd("/queue/simple/set max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                    }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                        this.MikrotikAdd("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=nocturnos .id=" + idMikrotikPlan);
                        this.MikrotikAdd("/queue/simple/set max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                    }
                    
                } else if(estadoServicio.compareTo("t")==0) {
                    
                            List<Map<String, String>> results = this.MikrotikEjecutar("/ip/firewall/address-list/remove .id=" + idMikrotikActivo);
                            List<Map<String, String>> results2 = this.MikrotikEjecutar("/ip/firewall/address-list/remove .id=" + idMikrotikPlan);
                            List<Map<String, String>> results3 = this.MikrotikEjecutar("/queue/simple/remove .id=" + idMikrotikCola);

                            this.ejecutar("update tbl_instalacion set idMikrotikActivo=null, idMikrotikPlan=null, idMikrotikCola=null where id_instalacion="+idInstalacion);
                            
                } else {    
                    
                    String listaEstado = this.getEstadoServicio(estadoServicio);
                    
                    List<Map<String, String>> results = this.MikrotikEjecutar("/ip/firewall/address-list/set list="+listaEstado+" .id=" + idMikrotikActivo);
//                    List<Map<String, String>> results2 = this.MikrotikEjecutar("/ip/firewall/address-list/set .id=" + idMikrotikPlan);
//                    List<Map<String, String>> results3 = this.MikrotikEjecutar("/queue/simple/set .id=" + idMikrotikCola);
                
                }
                
                rs.close();
                ok = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        } 
        return ok;
    }
    
    public boolean bajaDeInstalacionEnServidor(String idInstalacion)
    {
        try{
            ResultSet rs = this.getInfoInstalacion(idInstalacion);
            if(rs.next()){
                String idMikrotikActivo = rs.getString("idMikrotikActivo")!=null ? rs.getString("idMikrotikActivo") : "";
                String idMikrotikPlan = rs.getString("idMikrotikPlan")!=null ? rs.getString("idMikrotikPlan") : "";
                String idMikrotikCola = rs.getString("idMikrotikCola")!=null ? rs.getString("idMikrotikCola") : "";
                
                List<Map<String, String>> results = this.MikrotikEjecutar("/ip/firewall/address-list/remove .id=" + idMikrotikActivo);
                List<Map<String, String>> results2 = this.MikrotikEjecutar("/ip/firewall/address-list/remove .id=" + idMikrotikPlan);
                List<Map<String, String>> results3 = this.MikrotikEjecutar("/queue/simple/remove .id=" + idMikrotikCola);
                
//                if(idCola.compareTo("-1")!=0 && idPlan.compareTo("-1")!=0 && idActivo.compareTo("-1")!=0) {
                    this.ejecutar("update tbl_instalacion set idMikrotikActivo=null, idMikrotikPlan=null, idMikrotikCola=null where id_instalacion="+idInstalacion);
//                }
                
                rs.close();
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }  
        
        return false;
    }
   
    public StringBuilder getDataMikrotik(String idInstalacion)
    {
        StringBuilder data = new StringBuilder();
        try{
            ResultSet rs = this.getInfoInstalacion(idInstalacion);
            if(rs.next()){
                String idMikrotikActivo = rs.getString("idMikrotikActivo")!=null ? rs.getString("idMikrotikActivo") : "";
                String idMikrotikPlan = rs.getString("idMikrotikPlan")!=null ? rs.getString("idMikrotikPlan") : "";
                String idMikrotikCola = rs.getString("idMikrotikCola")!=null ? rs.getString("idMikrotikCola") : "";
                
                if(idMikrotikActivo.compareTo("")!=0) {
                    data.append( "<p>" );
                    data.append( this.MikrotikGetLista( idMikrotikActivo ) );
                    data.append( "</p>" );
                } else {
                    data.append( "<p>Lista [activos] no registrada</p>");
                }
                
                if(idMikrotikPlan.compareTo("")!=0) {
                    data.append( "<p>" );
                    data.append( this.MikrotikGetLista( idMikrotikPlan ) );
                    data.append( "</p>" );
                } else {
                    data.append( "<p>Lista [Plan] no registrada</p>");
                }
                
                if(idMikrotikCola.compareTo("")!=0) {
                    data.append( "<p>" );
                    data.append( this.MikrotikGetCola( idMikrotikCola ) );
                    data.append( "</p>" );
                } else {
                    data.append( "<p>Cola no registrada</p>");
                }
                
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }  
        return data;
    }
    
    public String getEstadoServicio(String estado)
    {
        String res = "activos";
        if (estado.compareTo("p")==0 || estado.compareTo("a")==0) {
            res = "activos";
        } else if (estado.compareTo("s")==0 ) {
                    res = "suspendidos";
        } else if (estado.compareTo("c")==0 ) {
                    res = "cortados";
        } else if (estado.compareTo("r")==0 ) {
                    res = "porRetirar";
        } else if (estado.compareTo("d")==0 ) {
                    res = "saldados";
        } else if (estado.compareTo("e")==0 ) {
                    res = "equiposDevueltos";
        } else if (estado.compareTo("t")==0 ) {
                    res = "terminadosSaldados";
        } else if (estado.compareTo("n")==0 ) {
                    res = "centralRiesgos";
        } else if (estado.compareTo("1")==0 ) {
                    res = "terminadosSaldadosResolucion";
        } else if (estado.compareTo("2")==0 ) {
                    res = "equiposDevueltosResolucion";
        } else if (estado.compareTo("3")==0 ) {
                    res = "cortadosResolucion";
        } else {
            res = "noDefinidos";
        }
        
        return res;
    }
    
    
    
    
//  funciones con Mikrotik    
    
    
    
    private boolean MikrotikConectar(String ip, String usuario, String clave)
    {
        try{
//            ApiConnection con = ApiConnection.connect(SSLSocketFactory.getDefault(), ip, ApiConnection.DEFAULT_TLS_PORT, ApiConnection.DEFAULT_CONNECTION_TIMEOUT);
            this.conexion = ApiConnection.connect( ip ); 
            this.conexion.login( usuario, clave );
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public ApiConnection MikrotikGetConexion() throws MikrotikApiException 
    {
        return this.conexion;
    }
    
    public String MikrotikAdd(String comando)
    {
        String id = "-1";
        try{
            List <Map<String, String>> res = this.conexion.execute( comando );
            for (Map<String, String> resId : res) {
                id = resId.get("ret");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    
    public List <Map<String, String>> MikrotikEjecutar(String comando)
    {
        List <Map<String, String>> ok = null;
        try{
            ok = this.conexion.execute( comando );
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public void Mikrotiklimpiar(String comando)
    {
        try{
            List<Map<String, String>> results =  this.conexion.execute( comando + "print" );
            for (Map<String, String> result : results) {
                try{    
                    this.conexion.execute(comando + "remove .id=" + result.get(".id"));
                }catch(Exception e){
                    e.printStackTrace();
                }    
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public StringBuilder MikrotikImprimir(String comando)
    {
        StringBuilder res = new StringBuilder();
        try{
            List<Map<String, String>> results =  this.conexion.execute( comando );
            for (Map<String, String> result : results) {
                res.append( result );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    
    public String MikrotikGetLista(String id)
    {
        String res = "";
        try {
            List<Map<String, String>> results =  this.conexion.execute( "/ip/firewall/address-list/print where .id=" + id );
            for (Map<String, String> result : results) {
                res += result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    
    public String MikrotikGetCola(String id) 
    {
        String res = "";
        try {
            List<Map<String, String>> results =  this.conexion.execute( "/queue/simple/print where .id=" + id );
            for (Map<String, String> result : results) {
                res += result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    
    public void MikrotikCerrar() 
    {
        this.cerrar();
        try{
            this.conexion.close();
        }catch(ApiConnectionException e){
            e.printStackTrace();
        }
    }
    
}
