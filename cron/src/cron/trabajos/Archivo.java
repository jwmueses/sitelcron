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
import java.sql.ResultSet;
import java.sql.Connection;

import java.sql.PreparedStatement;

import java.util.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import java.io.*;

/**
 *
 * @author Jorge
 */
public class Archivo extends DataBase{
    
    public Archivo(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    
    private String _directorio="";
    private String _archivoNombre="";
    private String _error="";
    private File _archivo = null;
    

/**
 * Ingresa una nueva ruta de directorio.
 * @param directorio. directorio raiz para trabajar conlos archivos. 
 */    
    public void setDirectorio(String directorio)
    {
        this._directorio = directorio;
    }

/**
 * Retorna la ruta del directorio.
 * @return Retorna el path del directorio de trabajo.
 */ 
    public String getDirectorio()
    {
        return this._directorio;
    }
    
/**
 * Retorna el nombre del archivo.
 * @return Retorna el nombre del archivo subido.
 */ 
    public String getNombreArchivo()
    {
        return this._archivoNombre;
    }
  
/**
 * Retorna el nombre del archivo.
 * @return Retorna el nombre del archivo subido.
 */ 
    public File getArchivo()
    {
        return this._archivo;
    }
    
/**
 * Retorna el mensaje de error provocado en el momento de la subida del archivo.
 * @return Retorna el mensaje de error.
 */ 
    public String getError()
    {
        return this._error;
    }
    
/**
 * Sube un archivo del cliente al servidor Web. Si el archivo ya existe en el
 * servidor Web lo sobrescribe.
 * @param request. Variable que contiene el request de un formulario.
 * @param tamanioMax. Tama�o m�ximo del archivo en megas.
 * @return Retorna true o false si se subi� o no el archivo.
 */    
    public boolean subir(HttpServletRequest request, double tamanioMax, String [] formato)
    {
        boolean res = false;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart){
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try{
                List items = upload.parseRequest(request);
                Iterator iter = items.iterator();
                while(iter.hasNext()){
                    FileItem item = (FileItem) iter.next();
                    if(!item.isFormField()){
                        String tipo = item.getContentType();
                        double tamanio = (double)item.getSize()/1024/1024; // para tamaño en megas
                        this._archivoNombre = item.getName().replace(" ", "_");
                        this._error = "Se ha excedido el tamaño máximo del archivo";
                        if(tamanio<=tamanioMax){
                            this._error = "El formato del archivo es incorrecto. " + tipo;
                            boolean estaFormato = false;
                            for(int i=0; i<formato.length; i++){
                                if(tipo.compareTo(formato[i])==0){
                                    estaFormato = true;
                                    break;
                                }
                            }
                            if(estaFormato){
                                this._archivo = new File(this._directorio, this._archivoNombre);
                                item.write(this._archivo);
                                this._error = "";
                                res = true;
                            }
                        }
                    }
                }
            }catch(Exception e){
                this._error = e.getMessage();
                e.printStackTrace();
            }
        }
        return res;
    }


       
/**
 * Guarda el registro del nombre y el archivo binario en una tabla de la base de datos.
 * @param nombre. Nombre del archivo subido.
 * @param archivo. Ruta del archivo subido.
 * @return Retorna true o false si se guarda o no el archivo en la DB.
 */ 
    public boolean setArchivoDB(String tabla, String campoNombre, String campoBytea, String clave, String nombre, File archivo)
    {
        boolean r = false;
        try {
            Connection conexion = this.getConexion();
            PreparedStatement ps = conexion.prepareStatement("UPDATE "+tabla+" SET "+campoNombre+"='"+nombre+"', "+campoBytea+"=? WHERE "+tabla.replace("tbl_", "id_")+"="+clave+";");
            conexion.setAutoCommit(false);
            FileInputStream archivoIS = new FileInputStream(this._archivo);
            try{
                /*ps.setBinaryStream(1, archivoIS, (int)archivo.length());*/
                byte buffer[] = new byte[(int)archivo.length()];
                archivoIS.read(buffer);
                ps.setBytes(1, buffer);
                
                ps.executeUpdate();
                conexion.commit();
                r = true;
            }catch(Exception e){
                this._error = e.getMessage();
                e.printStackTrace();
            }finally {
                archivoIS.close();
                ps.close();
            }
            
        } catch(Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }
    
    public String getArchivo(String path, int clave)
    {
        this._archivoNombre = "";
        try{
            ResultSet res = this.consulta("select * from tbl_archivo where id_archivo="+clave+";");
            if(res.next()){
                this._archivoNombre = (res.getString("nombre")!=null) ? res.getString("nombre") : "";
                try{
                    this._archivo = new File(path, this._archivoNombre);
                    if(!this._archivo.exists()){
                        byte[] bytes = (res.getString("archivo")!=null) ? res.getBytes("archivo") : null;
                        RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre,"rw");
                        archivo.write(bytes);
                        archivo.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return this._archivoNombre;
    }

    public String getArchivo(String path, String tabla, String clave, String campoNombre, String campoBytea)
    {
        this._archivoNombre = "";
        try{
            ResultSet res = this.consulta("select * from "+tabla+" where "+tabla.replace("tbl_", "id_")+"="+clave+";");
            if(res.next()){
                this._archivoNombre = res.getString(campoNombre)!=null ? res.getString(campoNombre) : "";
                if(this._archivoNombre.compareTo("")!=0){
                    try{
                        this._archivo = new File(path, this._archivoNombre);
                        if(!this._archivo.exists()){
                            byte[] bytes = (res.getString(campoBytea)!=null) ? res.getBytes(campoBytea) : null;
                            RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre,"rw");
                            archivo.write(bytes);
                            archivo.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return this._archivoNombre;
    }
    
//    public String getArchivoXml(String path, String tabla, String clave, String campoNombre, String campo)
//    {
//        this._archivoNombre = "";
//        try{
//            ResultSet res = this.consulta("select * from "+tabla+" where clave_acceso='"+clave+"'   ;");
//            if(res.next()){
//                this._archivoNombre = res.getString(campoNombre)!=null ? res.getString(campoNombre) : "";
//                if(this._archivoNombre.compareTo("")!=0){
//                    try{
//                        this._archivo = new File(path, this._archivoNombre);
//                       if(!this._archivo.exists()){
//                            //byte[] bytes = (res.getString(campoBytea)!=null) ? res.getBytes(campoBytea) : null;
//                            RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre+".xml","rw");
//                            archivo.writeBytes(campo);
//                            archivo.close();
//                        }
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                res.close();
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return this._archivoNombre;
//    }
    
}