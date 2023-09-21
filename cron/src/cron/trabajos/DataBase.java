/**
* @version 1.0
* @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* FACTURAPYMES! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package cron.trabajos;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DataBase
{
    private Connection con = null;
    private String error = "";

    public DataBase ()
    {
        try{
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/db_isp", "us_isp", "g.sI._gR56#5s4d0St1");
        }catch(ClassNotFoundException e){
            System.out.println("Error: "+e.getMessage()+". El driver no puede ser cargado.");
        }catch(Exception ex){
            System.out.println("Error "+ex.getMessage()+". Al conectarse a la base de datos.");
        }
    }
    
/**
 * Constructor de la clase DataBase que crea una conexi�n a una base de datos
 * SqlServer2k5.
 * @param m. IP de la maquina del servisor de base de datos SqlServer2k5.
 * @param p. Puerto de escucha del servidor de base de datos.
 * @param db. Nombre de la base de datos a conectarse.
 * @param u. Nombre del usuario de la base de datos.
 * @param c. Contrase�a del usuario de la base de datos.
 */    
    public DataBase (String m, int p, String db, String u, String c) 
    {    
        try{ 
            Class.forName("org.postgresql.Driver"); 
            this.con = DriverManager.getConnection("jdbc:postgresql://" + m + ":" + p + "/" + db, u, c); 
        }catch(ClassNotFoundException nfe){
            System.out.println("Error: "+nfe.getMessage()+". El driver no puede ser cargado. " + m);
        }catch(Exception exp){
            System.out.println("Error: "+exp.getMessage() + m);
        }       
    }

    public Connection getConexion()
    {
        return this.con;
    }

/**
 * Funci�n que ejecuta una instrucci�n SELECT en el servidor de Base de datos.
 * @param cad. Cadena SQL - SELECT.
 * @return Retorna una objeto ResulSet(juego de registros).
 */
    public ResultSet consulta(String cad) 
    { 
        ResultSet r = null;
        try{
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql=this.decodificarURI(cad);
            r = st.executeQuery(sql); 
        }catch(Exception e){  
//            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error DataBase.consulta("+cad+") -> " + e.getMessage());
            this.error = e.getMessage();
        }     
        return r;
    } 

/**
 * Ejecuta un grupo de transacciones contenidas en un ArrayList,
 * manteniendo las propiedades ACID.
 * @param tr. Un ArrayList de instrucciones SQL.
 * @return true o falase seg�n si se han realizado todas las transaccciones con exito o no.
 */    
    public boolean transacciones(List tr)
    {
        String sql = "";
        try{
            if(!tr.isEmpty()){
                this.con.setAutoCommit(false);
                Statement st = this.con.createStatement();
                Iterator it = tr.iterator();
                while(it.hasNext()){
                    sql = (String)it.next();
                    if(sql.toLowerCase().indexOf("select") == 0 ){
                        st.executeQuery(sql);
                    }else{
                        st.executeUpdate(this.decodificarURI(sql));
                    }
                }
                this.con.commit();
                st.close();
            }
            return true;
        }catch (SQLException ex) {
            this.error = ex.getMessage();
            try {
                this.con.rollback();
//                System.out.println("Error de lote de transacciones.");
            }
            catch (SQLException se) {
                this.error = se.getMessage();
            }
        }catch (Exception e) {
            this.error = e.getMessage();
            try {
                this.con.rollback();
//                System.out.println("Error de lote de transacciones.");
            }
            catch (SQLException se) {
                this.error = se.getMessage();
            }
        }finally{
            try{
                this.con.setAutoCommit(true);
            }catch(SQLException e){}
        }

        return false;
    }  
    
/**
 * Funci�n que ejecuta una instrucci�n INSERT, UPDATE o DELETE en el servidor 
 * de Base de datos.
 * @param cad. Cadena SQL - INSERT, UPDATE o DELETE.
 * @return Retorna verdadero o false seg�n si se ejecut� o no la instrucci�n.
 */       
    public boolean ejecutar(String sql)
    { 
        int r = -1;
        try{
            Statement st = this.con.createStatement();
            r = st.executeUpdate(this.decodificarURI(sql));
            st.close();
            if(r>0){
                return true;
            }            
        }catch(Exception e){  
//            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error DataBase.ejecutar("+sql+") -> " + e.getMessage());
            this.error = e.getMessage();
            return false;
        }     
        return true;
    } 
    
/**
 * Funci�n que ejecuta una instrucci�n INSERT en el servidor para tablas con claves autogeneradas
 * de Base de datos.
 * @param cad. Cadena SQL - INSERT.
 * @return Retorna la clave primaria generada e por el comando insert.
 */       
    public String insert(String sql)
    { 
        String pk = "-1";
        if(sql.toLowerCase().indexOf("insert") == 0 ){
            try{
                Statement st = this.con.createStatement();
                int r = st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS);
                if(r>0){
                    ResultSet rs = st.getGeneratedKeys();
                    if(rs.next()){
                        pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                        rs.close();
                    }
                }
                st.close();
            }catch(Exception e){
                pk = "-1";
//                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Error DataBase.insert("+sql+") -> "  + e.getMessage());
                this.error = e.getMessage();
            }
        }
        return pk;
    } 
      
/**
 * Funci�n que codifica un objeto ResultSet(juego de registros) en una cadena
 * JSON.
 * @param r. Un objeto Resultset(juego de registros) que contiene el resultado.
 * de una seltencia SELECT. 
 * @return
 */    
    public String getJSON(ResultSet r) 
    { 
        String json = "{tbl:[";
        try{
            r.beforeFirst();
            ResultSetMetaData mdata = r.getMetaData();      
            int col = mdata.getColumnCount();      
            int i=0;
            while(r.next()){
                json += "{";
                i=0;
                for(int j=1; j<=col; j++){                   
                json += i + ":\"" + ((r.getString(j)!=null)?r.getString(j).replace('"', '~').replace("\n", ". ").replace("\t", " ").replace("\r", ". "):"") + "\",";
                i++;
                }
                json = json.substring(0, json.length()-1);
                json += "},";
            } 
            json = json.substring(0, json.length()-1);
            json += "]}";
            r.close();
        }catch(Exception e){
            this.error = e.getMessage();
        }     
        return json;
    }
 
/**
  * Función que retorna los datos de una tabla filtrada en formato JSON.
 * @param t nombre de la tabla
 * @param c nombre de los campos
 * @param w sentencia SQL WHERE
 * @return una cadena formateada a JSON.
 */
    public String getTablaJSON(String t, String c, String w)
    {
        String tbl_json = "{tbl:[";
        try{
            ResultSet tbl = this.consulta("SELECT "+c+" FROM "+t+" "+w+";");
            tbl_json = this.getJSON(tbl);
            tbl.close();
        }catch(Exception e){
            this.error = e.getMessage();
        }
        return tbl_json;
    }

/**
 * Funci�n que codifica una consulta SELECT paginada en una cadena en formato
 * JSON.
 * @param t. Nombre de la tabla para la consulta.
 * @param c. Nombre de los campos de la tabla. 
 * @param w. Clausula WHERE para la consulta.
 * @param p. El n�mero de la p�gina a retornar.
 * @param fxp. El n�mero de registros por p�gina.
 * @return Una cadena codificada en formato JSON y paginada.
 */
    public String paginar(String t, String c, String w, int p, int fxp)
    {
        ResultSet r = null;
        String json = "{tbl:[";
        long numPags = 0;
        try{
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            /*ResultSet rPag = st.executeQuery(this.decodificarURI("SELECT * FROM " + t +" "+ w));
            numPags = (this.getFilas(rPag)-1)/fxp;*/
            ResultSet rPag = st.executeQuery(this.decodificarURI("SELECT count(*) FROM " + t +" "+ w.replaceAll("order by .*", "")));
            if(rPag.next()){
                numPags = ( (rPag.getString(1)!=null?rPag.getLong(1):1) - 1 )/fxp;
                rPag.close();
            }
            rPag.close();
            if(p > numPags){
                p = 0;
            }
            r = st.executeQuery(this.decodificarURI("SELECT "+c+" FROM "+t+" "+w+" LIMIT "+fxp+" OFFSET "+(fxp*p)+";"));
            json = this.getJSON(r);
            st.close();
        }catch(Exception e){
            this.error = e.getMessage();
        }
        return numPags+"|"+json;
    }
  
/**
 * Funci�n que calcula el n�mero de filas de un juego de registros.
 * @param rs. Un objeto Resultset(juego de registros) que contiene el resultado.
 * @return el n�mero de filas de un juego de registros.
 */
    public int getFilas(ResultSet rs)
    {                
        int cont=0;
        try{
            rs.last();
            cont = rs.getRow();
            rs.beforeFirst();
        }catch(Exception e){    
            this.error = e.getMessage();
        }
        return cont;
    }

/**
 * Funci�n que calcula el n�mero de columnas de un juego de registros.
 * @param rs. Un objeto Resultset(juego de registros) que contiene el resultado.
 * @return el n�mero de columnas de un juego de registros.
 */
    public int getColumnas(ResultSet rs)
    {                
        int cont=0;
        try{
            ResultSetMetaData mdata = rs.getMetaData();
            cont = mdata.getColumnCount();
        }catch(Exception e){    
            this.error = e.getMessage();
        }
        return cont;
    }
 
/**
 * Cierra una conexi�n abierta a una base de datos SqlServer2k5.
 */
    public void cerrar()
    {
        try{
            this.con.close();
        }catch (Exception ec) {
            System.out.println(ec.getMessage());
        }
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
        //cad = cad.replace("\"", "''\''");
        return cad;
    }

    public void setError(String error)
    {
        this.error = error;
    }
    
    public String getError()
    {
        return this.error;
    }
    
}