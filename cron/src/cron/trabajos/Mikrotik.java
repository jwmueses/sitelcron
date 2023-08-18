/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.ApiConnectionException;
import me.legrange.mikrotik.MikrotikApiException;

/**
 *
 * @author sistemas
 */
public class Mikrotik {
    private ApiConnection conexion = null;
    
    public Mikrotik(String ip, String usuario, String clave)
    {
        try{
//            ApiConnection con = ApiConnection.connect(SSLSocketFactory.getDefault(), ip, ApiConnection.DEFAULT_TLS_PORT, ApiConnection.DEFAULT_CONNECTION_TIMEOUT);
            this.conexion = ApiConnection.connect( ip ); 
            this.conexion.login( usuario, clave );
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public ApiConnection getConexion() throws MikrotikApiException 
    {
        return this.conexion;
    }
    
    public String add(String comando)
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
    
    public List <Map<String, String>> ejecutar(String comando)  
    {
        List <Map<String, String>> ok = null;
        try{
            ok = this.conexion.execute( comando );
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public void limpiar(String comando)
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
    
    public StringBuilder imprimir(String comando) throws MikrotikApiException 
    {
        StringBuilder res = new StringBuilder();
        try {
            List<Map<String, String>> results =  this.conexion.execute( comando );
            for (Map<String, String> result : results) {
                res.append( result );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    
    public void cerrar() 
    {
        try{
            if( this.conexion != null ) {
                this.conexion.close();
            }
        }catch(ApiConnectionException e){
            e.printStackTrace();
        }
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
 
    public boolean actualizarInstalacionEnServidor(DataBase objDataBase, String idInstalacion, String cliente, String ip, String plan, String burst_limit, 
            String max_limit, String prioridad, String estadoServicio, String idMikrotikActivo, String idMikrotikPlan, String idMikrotikCola)
    {
        boolean ok = false;
        try{
            
            if(estadoServicio.compareTo("a")==0) {

                this.add("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=activos .id=" + idMikrotikActivo);
                if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                    this.add("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=corporativos .id=" + idMikrotikPlan);
                    this.add("/queue/simple/set max-limit="+max_limit+"k/"+max_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                }else if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                    this.add("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=residenciales .id=" + idMikrotikPlan);
                    this.add("/queue/simple/set max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                    this.add("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=small .id=" + idMikrotikPlan);
                    this.add("/queue/simple/set max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                    this.add("/ip/firewall/address-list/set address="+ip+" comment=\""+cliente+"\" list=nocturnos .id=" + idMikrotikPlan);
                    this.add("/queue/simple/set max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" priority="+prioridad+"/"+prioridad+" target="+ip+" total-priority="+prioridad + " .id=" + idMikrotikCola);
                }

            } else if(estadoServicio.compareTo("t")==0) {

                        List<Map<String, String>> results = this.ejecutar("/ip/firewall/address-list/remove .id=" + idMikrotikActivo);
                        List<Map<String, String>> results2 = this.ejecutar("/ip/firewall/address-list/remove .id=" + idMikrotikPlan);
                        List<Map<String, String>> results3 = this.ejecutar("/queue/simple/remove .id=" + idMikrotikCola);

                        objDataBase.ejecutar("update tbl_instalacion set idMikrotikActivo=null, idMikrotikPlan=null, idMikrotikCola=null where id_instalacion="+idInstalacion);

            } else {    

                String listaEstado = this.getEstadoServicio(estadoServicio);

                List<Map<String, String>> results = this.ejecutar("/ip/firewall/address-list/set list="+listaEstado+" .id=" + idMikrotikActivo);
//                    List<Map<String, String>> results2 = this.MikrotikEjecutar("/ip/firewall/address-list/set .id=" + idMikrotikPlan);
//                    List<Map<String, String>> results3 = this.MikrotikEjecutar("/queue/simple/set .id=" + idMikrotikCola);

            }

            ok = true;
        }catch(Exception e){
            e.printStackTrace();
        } 
        return ok;
    }
    
}
