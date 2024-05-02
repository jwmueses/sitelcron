/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sistemas
 */
public class BaseDatos 
{
    protected Connection con = null;
    protected Statement st = null;
    private String error = "";
    
    private String ip = "192.168.217.16";    //  192.168.217.16     127.0.0.1
    private int puerto = 5432;
    private String DB = "db_isp";
    private String esquema = "public";              //  public
    private String usuario = "postgres";            //       postgres
    private String clave = "Gi%9875.-*5+$)("; //         postgres
    
//    private String SQL = "";

    public BaseDatos ()
    {
        try{
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection("jdbc:postgresql://"+this.ip+":"+this.puerto+"/" + this.DB, this.usuario, this.clave);
        }catch(ClassNotFoundException e){
            System.out.println("Error: "+e.getMessage()+". El driver no puede ser cargado.");
        }catch(Exception ex){
            System.out.println("Error "+ex.getMessage()+". Al conectarse a la base de datos.");
        }
    }
    
/**
 * Constructor de la clase DataBase que crea una conexi�n a una base de datos
 * SqlServer2k5.
 * @param ip. IP de la maquina del servisor de base de datos SqlServer2k5.
 * @param p. Puerto de escucha del servidor de base de datos.
 * @param db. Nombre de la base de datos a conectarse.
 * @param u. Nombre del usuario de la base de datos.
 * @param c. Contrase�a del usuario de la base de datos.
 */    
    public BaseDatos (String ip, int p, String db, String u, String c) 
    {
        this.ip = ip;
        this.puerto = p;
        this.DB = db;
        this.esquema = "public";
        this.usuario = u;
        this.clave = c;
        try{ 
            Class.forName("org.postgresql.Driver"); 
            this.con = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + p + "/" + db, u, c); 
        }catch(ClassNotFoundException nfe){
            System.out.println("Error: "+nfe.getMessage()+". El driver no puede ser cargado.");
        }catch(Exception exp){
            exp.printStackTrace();
        }       
    }
    
    /**
 * Constructor de la clase DataBase que crea una conexi�n a una base de datos
 * SqlServer2k5.
 * @param eq. Esquema de base de datos.
 * @param ip. IP de la maquina del servisor de base de datos SqlServer2k5.
 * @param p. Puerto de escucha del servidor de base de datos.
 * @param db. Nombre de la base de datos a conectarse.
 * @param u. Nombre del usuario de la base de datos.
 * @param c. Contrase�a del usuario de la base de datos.
 */    
    public BaseDatos (String eq, String ip, int p, String db, String u, String c) 
    {
        this.ip = ip;
        this.puerto = p;
        this.DB = db;
        this.esquema = eq;
        this.usuario = u;
        this.clave = c;
        try{ 
            Class.forName("org.postgresql.Driver"); 
            this.con = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + p + "/" + db, u, c); 
        }catch(ClassNotFoundException nfe){
            System.out.println("Error: "+nfe.getMessage()+". El driver no puede ser cargado.");
        }catch(Exception exp){
            exp.printStackTrace();
        }       
    }

    public Connection getConexion()
    {
        return this.con;
    }

    /**
 * Funci�n que ejecuta una instrucci�n SELECT en el servidor de Base de datos.
 * @param SQL. Cadena SQL - SELECT.
 * @return Retorna una objeto ResulSet(juego de registros).
 */
    public ResultSet consulta(String SQL) 
    { 
        ResultSet r = null;
        try{
            Statement statement = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            r = statement.executeQuery(this.decodificarURI(SQL)); 
        }catch(Exception e){  
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
        try{
            if(!tr.isEmpty()){
                this.con.setAutoCommit(false);
                Statement st = this.con.createStatement();
                Iterator it = tr.iterator();
                while(it.hasNext()){
                    String sql = (String)it.next();
                    if(sql.toLowerCase().indexOf("select") == 0 ){
                        st.executeQuery(this.decodificarURI(sql));
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
                System.out.println("Error de lote de transacciones.");
            }catch (SQLException se) {
                this.error = se.getMessage();
            }
        }catch (Exception e) {
            this.error = e.getMessage();
            try {
                this.con.rollback();
                System.out.println("Error de lote de transacciones.");
            }catch (SQLException se) {
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
 * @param sql. Cadena SQL - INSERT, UPDATE o DELETE.
 * @return Retorna verdadero o false seg�n si se ejecut� o no la instrucci�n.
 */       
    public boolean ejecutar(String sql)
    { 
        try{
            Statement statement = this.con.createStatement();
            int r = statement.executeUpdate(this.decodificarURI(sql));
            statement.close();
            if(r>0){
                return true;
            }            
        }catch(Exception e){  
            this.error = e.getMessage();
            return false;
        }     
        return true;
    } 
    
/**
 * Funci�n que ejecuta una instrucci�n INSERT en el servidor para tablas con claves autogeneradas
 * de Base de datos.
 * @param sql. Cadena SQL - INSERT.
 * @return Retorna la clave primaria generada e por el comando insert.
 */       
    public String insertar(String sql)
    { 
        String pk = "-1";
        if(sql.toLowerCase().indexOf("insert") == 0 ){
            try{
                Statement statement = this.con.createStatement();
                int r = statement.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS);
                if(r>0){
                    ResultSet rs = statement.getGeneratedKeys();
                    if(rs.next()){
                        pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                        rs.close();
                    }
                }
                statement.close();
            }catch(Exception e){
                pk = "-1";
                this.error = e.getMessage();
            }
        }
        return pk;
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
        //cad = cad.replace("|", "/");
        cad = cad.replace("\"", "''\''");
        cad = cad.replace("\n", ". ");
        cad = cad.replace("\r", ". ");
        cad = cad.replace("\t", " ");
//        this.SQL = cad;
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
    

////////////////////    BLOQUE DE TRANSACCIONES EXTERNO /////////////////////////////////////////////////
    
    
    /**
 * Ejecuta un grupo de transacciones contenidas en un ArrayList,
 * manteniendo las propiedades ACID.
 * @return true o falase seg�n si se han realizado todas las transaccciones con exito o no.
 */    
    public boolean IniciarTransacciones()
    {
        try{
            this.con.setAutoCommit(false);
            this.con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            this.st = this.con.createStatement();
            return true;
        }catch (SQLException ex) {
            this.error = ex.getMessage();
            ex.printStackTrace();
        }

        return false;
    } 

    /**
 * Funci�n que ejecuta una instrucci�n SELECT en el servidor de Base de datos.
 * @param cad. Cadena SQL - SELECT.
 * @return Retorna una objeto ResulSet(juego de registros).
 */
    public ResultSet consultaTransaccion(String cad) 
    { 
        ResultSet r = null;
        try{
            r = this.st.executeQuery(this.decodificarURI(cad)); 
            this.st.clearBatch();
        }catch(Exception e){  
            this.error = e.getMessage();
            e.printStackTrace();
        }     
        return r;
    } 
    
    /**
 * Funci�n que ejecuta una instrucci�n INSERT, UPDATE o DELETE en el servidor 
 * de Base de datos.
 * @param sql. Cadena SQL - INSERT, UPDATE o DELETE.
 * @return Retorna verdadero o false seg�n si se ejecut� o no la instrucci�n.
 */       
    public boolean ejecutarTransaccion(String sql)
    { 
        try{
            int r = this.st.executeUpdate(this.decodificarURI(sql));
            if(r>0){
                this.st.clearBatch();
                return true;
            }            
        }catch(Exception e){  
            this.error = e.getMessage();
            e.printStackTrace();
        }     
        return false;
    } 
    
/**
 * Funci�n que ejecuta una instrucci�n INSERT en el servidor para tablas con claves autogeneradas
 * de Base de datos.
 * @param sql. Cadena SQL - INSERT.
 * @return Retorna la clave primaria generada e por el comando insert.
 */       
    public String insertarTransaccion(String sql)
    { 
        String pk = "-1";
        if(sql.toLowerCase().indexOf("insert") == 0 ){
            try{
                int r = this.st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS);
                if(r>0){
                    ResultSet rs = st.getGeneratedKeys();
                    if(rs.next()){
                        pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                        rs.close();
                    }
                }
            }catch(Exception e){
                this.error = e.getMessage();
                e.printStackTrace();
            }
        }
        return pk;
    } 
    
    public boolean ConfirmarTransacciones()
    {
        try{
            this.con.commit();
            st.close();
            return true;
        }catch (SQLException ex) {
            this.error = ex.getMessage();
            try {
                this.con.rollback();
                System.out.println("Error en el commit de transacciones.");
            }catch (SQLException se) {
                this.error = se.getMessage();
            }
        }finally{
            try{
                this.con.setAutoCommit(true);
                this.con.setTransactionIsolation(Connection.TRANSACTION_NONE);
            }catch(SQLException e){}
        }
        return false;
    }
    
    public boolean CancelarTransacciones()
    {
        try{
            this.con.rollback();
            st.close();
            return true;
        }catch (SQLException ex) {
            this.error = ex.getMessage();
        }finally{
            try{
                this.con.setAutoCommit(true);
                this.con.setTransactionIsolation(Connection.TRANSACTION_NONE);
            }catch(SQLException e){}
        }
        return false;
    }
    
}
