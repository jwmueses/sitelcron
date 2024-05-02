/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author sistemas
 */
public class Gomax 
{
    private String error;
    
    
    class GomaxData {
        public String name;
        public String debug;
        public String message;
        public String arguments;
        public String context;
    }
    class GomaxError {
        public String code;
        public String message;
        public GomaxData data;
    }
    class GomaxProductIds {
        public long _97;
    }
    class GomaxResult {
        public long partner_id;
        public boolean status;
        public GomaxProductIds product_ids;
    }
    class GomaxUsuario {
        public String jonrpc;
        public long id;
        public GomaxResult result;
        public GomaxError error;
    }
    class GomaxResultCorto {
        public String jonrpc;
        public String id;
        public boolean result;
    }
    class GomaxResultEd {
        public long partner_id;
        public boolean status;
        public String message;
    }
    class GomaxUsuarioEd {
        public String jonrpc;
        public long id;
        public GomaxResultEd result;
    }
    
    
    public long crearUsuario(String json)
    {
        StringBuilder resJson = this.enviarSolicitud( "http://154.53.56.122:8069/api/create_partner", "POST", json );
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        GomaxUsuario jsonUsuario = gson.fromJson( resJson.toString(), GomaxUsuario.class );
        if (jsonUsuario.result != null) {
            return jsonUsuario.result.partner_id;
        }
        if (jsonUsuario.error != null) {
            this.setError( jsonUsuario.error.message );
        }
        return -1;
    }
    
    public long actualizarUsuario(String json)
    {
        StringBuilder resJson = this.enviarSolicitud( "http://154.53.56.122:8069/api/set_partner", "POST", json );
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        GomaxUsuarioEd jsonUsuario = gson.fromJson( resJson.toString(), GomaxUsuarioEd.class );
        if (jsonUsuario.result != null) {
            if (jsonUsuario.result.message != null) {
                this.setError( jsonUsuario.result.message );
            } else {
                return jsonUsuario.result.partner_id;
            }
        }
        return -1;
    }
    
    
    
    public boolean crearContrato(String json)
    {
        StringBuilder resJson = this.enviarSolicitud( "http://154.53.56.122:8069/api/create_contract", "POST", json );
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        GomaxUsuario jsonUsuario = gson.fromJson( resJson.toString(), GomaxUsuario.class );
        if (jsonUsuario.result != null) {
            if (jsonUsuario.result.status) {
                return jsonUsuario.result.status;
            }
        }
        if (jsonUsuario.error != null) {
            this.setError( jsonUsuario.error.message );
        }
        return false;
    }
    
    public boolean suspenderContrato(String json)
    {
        StringBuilder resJson = this.enviarSolicitud( "http://154.53.56.122:8069/api/suspend_contract", "POST", json );
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        GomaxResultCorto jsonSuscripcion = gson.fromJson( resJson.toString(), GomaxResultCorto.class );
        return jsonSuscripcion.result;
    }
    
    public boolean reactivarContrato(String json)
    {
        StringBuilder resJson = this.enviarSolicitud( "http://154.53.56.122:8069/api/unsuspend_contract", "POST", json );
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        GomaxResultCorto jsonSuscripcion = gson.fromJson( resJson.toString(), GomaxResultCorto.class );
        return jsonSuscripcion.result;
    }
    
    public boolean cancelarContrato(String json)
    {
        StringBuilder resJson = this.enviarSolicitud( "http://154.53.56.122:8069/api/cancel_contract", "POST", json );
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        GomaxResultCorto jsonSuscripcion = gson.fromJson( resJson.toString(), GomaxResultCorto.class );
        return jsonSuscripcion.result;
    }
    
    public boolean cancelarProducto(String json)
    {
        StringBuilder resJson = this.enviarSolicitud( "http://131.161.221.37:5050/api_bc/add_cliente", "POST", json );
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        GomaxResultCorto jsonSuscripcion = gson.fromJson( resJson.toString(), GomaxResultCorto.class );
        return jsonSuscripcion.result;
    }
    
    
    
    
    public StringBuilder enviarSolicitud(String urlApi, String metodo, String json)
    {
        StringBuilder resJson = new StringBuilder();
        
        try {
            // Definir la URL del endpoint del API REST
            URL url = new URL( urlApi );

            // Abrir una conexión HTTPURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Establecer el método de solicitud (GET, POST, PUT, DELETE, etc.)
            connection.setRequestMethod( metodo );
            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("Authorization", "hipopotomonstrosesquipedaliofobia");

            // Habilitar el envío de datos
            connection.setDoOutput(true);

            // Escribir los datos en el cuerpo de la solicitud
            try ( DataOutputStream outputStream = new DataOutputStream( connection.getOutputStream() ) ) {
                outputStream.write( json.getBytes() );
            }

            // Leer la respuesta del servidor
            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                resJson.append(line);
            }
            reader.close();
            
//            System.out.println(user.ShowAsString());
//
//            res = gson.toJson(user);
//            System.out.println("User Object as string : " + str);

            
            
            // Cerrar la conexión
            connection.disconnect();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return resJson;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
}
