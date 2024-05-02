/**
 * @version 1.0
 * @package FACTURAPYMES.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos
 * reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL. FACTURAPYMES! es un
 * software de libre distribución, que puede ser copiado y distribuido bajo los
 * términos de la Licencia Pública General GNU, de acuerdo con la publicada por
 * la Free Software Foundation, versión 2 de la licencia o cualquier versión
 * posterior.
 */
package ec.com.saitel.gomax.utils;

import ec.com.saitel.gomax.dao.BaseDatos;
import java.sql.ResultSet;
import java.sql.Connection;

import java.sql.PreparedStatement;

import java.util.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import java.io.*;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Jorge
 */
public class Archivo extends BaseDatos {

    public Archivo() {

    }

    public Archivo(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    private String _directorio = "";
    private String _archivoNombre = "";
    private String _error = "";
    private File _archivo = null;

    /**
     * Ingresa una nueva ruta de directorio.
     *
     * @param directorio. directorio raiz para trabajar conlos archivos.
     */
    public void setDirectorio(String directorio) {
        this._directorio = directorio;
    }

    /**
     * Retorna la ruta del directorio.
     *
     * @return Retorna el path del directorio de trabajo.
     */
    public String getDirectorio() {
        return this._directorio;
    }

    /**
     * Retorna el nombre del archivo.
     *
     * @return Retorna el nombre del archivo subido.
     */
    public String getNombreArchivo() {
        return this._archivoNombre;
    }

    public void setNombreArchivo(String archivoNombre1) {
        this._archivoNombre = archivoNombre1;
    }

    /**
     * Retorna el nombre del archivo.
     *
     * @return Retorna el nombre del archivo subido.
     */
    public File getArchivo() {
        return this._archivo;
    }

    public void setArchivo(File archivo1) {
        this._archivo = archivo1;
    }

    /**
     * Retorna el mensaje de error provocado en el momento de la subida del
     * archivo.
     *
     * @return Retorna el mensaje de error.
     */
    public String getError() {
        return this._error;
    }

    /**
     * Sube un archivo del cliente al servidor Web. Si el archivo ya existe en
     * el servidor Web lo sobrescribe.
     *
     * @param request. Variable que contiene el request de un formulario.
     * @param tamanioMax. Tama�o m�ximo del archivo en megas.
     * @return Retorna true o false si se subi� o no el archivo.
     */
    public boolean subir(HttpServletRequest request, double tamanioMax, String[] formato) {
        boolean res = false;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List items = upload.parseRequest(request);
                Iterator iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (!item.isFormField()) {
                        String tipo = item.getContentType();
                        double tamanio = (double) item.getSize() / 1024 / 1024; // para tamaño en megas
                        this._archivoNombre = item.getName().replace(" ", "_");
                        this._archivoNombre = Fecha.getFechaHora() + "_" + Cadena.quitaLatinas(this._archivoNombre);
                        this._error = "Se ha excedido el tamaño máximo del archivo";
                        if (tamanio <= tamanioMax) {
                            this._error = "El formato del archivo es incorrecto. " + tipo;
                            boolean estaFormato = false;
                            for (int i = 0; i < formato.length; i++) {
                                if (tipo.compareTo(formato[i]) == 0) {
                                    estaFormato = true;
                                    break;
                                }
                            }
                            if (estaFormato) {
                                this._archivo = new File(this._directorio, this._archivoNombre);
                                item.write(this._archivo);
                                this._error = "";
                                res = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            }
        }
        return res;
    }

//    public boolean guardarArchivoCargado(HttpServletRequest request, String tagFile, String[] formato)
//    {
//        try{
//            Part filePart = request.getPart(tagFile);
//            String nombre = Paths.get(filePart.getSubmittedFileName()).getFileName().toString().toLowerCase();
//            String extension = FilenameUtils.getExtension(nombre);
//            boolean estaFormato = false;
//            for (int i = 0; i < formato.length; i++) {
//                if (extension.compareTo(formato[i]) == 0) {
//                    estaFormato = true;
//                    break;
//                }
//            }
//            if (!estaFormato) {
//                this._error = "El formato del archivo es incorrecto. " + extension;
//                return false;
//            }    
//            this._archivoNombre = Cadena.quitaLatinas(nombre).replace(" ", "_");
//            InputStream fileContent = filePart.getInputStream();
//            File archivo = new File(this._directorio, this._archivoNombre);
//            FileUtils.copyInputStreamToFile(fileContent, archivo);
//            
//        }catch(Exception e){
//            this._error = e.getMessage();
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
    
    public boolean renombrar(String dir, String usuario, boolean cambiar) {
        try {
            if (cambiar) {
                String nombre_nuevo = usuario + "" + Fecha.getFecha("ISO") + "" + Fecha.getHora();
                nombre_nuevo = (nombre_nuevo.replaceAll("-", "").replaceAll(":", "")) + "." + FilenameUtils.getExtension(this._archivoNombre);
                this._archivoNombre = nombre_nuevo;
                File file_nuevo = new File(dir + nombre_nuevo);
                File file_antiguo = this._archivo;
                file_antiguo.renameTo(file_nuevo);
                this._archivo = file_nuevo;
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            this._error = "" + e.getMessage();
            return false;
        }
    }

    /**
     * Guarda el registro del nombre y el archivo binario en una tabla de la
     * base de datos.
     *
     * @param nombre. Nombre del archivo subido.
     * @param archivo. Ruta del archivo subido.
     * @return Retorna true o false si se guarda o no el archivo en la DB.
     */
    public boolean setArchivoDB(String tabla, String campoNombre, String campoBytea, String clave, String nombre, File archivo) {
        boolean r = false;
        try {
            Connection conexion = this.getConexion();
            PreparedStatement ps = conexion.prepareStatement("UPDATE " + tabla + " SET " + campoNombre + "='" + nombre + "', " + campoBytea + "=? WHERE " + tabla.replace("tbl_", "id_") + "=" + clave + ";");
            conexion.setAutoCommit(false);
            FileInputStream archivoIS = new FileInputStream(this._archivo);
            try {
                /*ps.setBinaryStream(1, archivoIS, (int)archivo.length());*/
                byte buffer[] = new byte[(int) archivo.length()];
                archivoIS.read(buffer);
                ps.setBytes(1, buffer);

                ps.executeUpdate();
                conexion.commit();
                r = true;
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            } finally {
                archivoIS.close();
                ps.close();
            }

        } catch (Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }

    public String getArchivo(String path, int clave) {
        this._archivoNombre = "";
        try {
            ResultSet res = this.consulta("select * from tbl_archivo where id_archivo=" + clave + ";");
            if (res.next()) {
                this._archivoNombre = (res.getString("nombre") != null) ? res.getString("nombre") : "";
                try {
                    this._archivo = new File(path, this._archivoNombre);
                    //if (!this._archivo.exists()) {
                    byte[] bytes = (res.getString("archivo") != null) ? res.getBytes("archivo") : null;
                    RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre, "rw");
                    archivo.write(bytes);
                    archivo.close();
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this._archivoNombre;
    }

    public byte[] getArchivo(int clave) {
        byte[] bytes = null;
        try {
            ResultSet res = this.consulta("select * from tbl_archivo where id_archivo=" + clave + ";");
            if (res.next()) {
                try {
                    bytes = (res.getString("archivo") != null) ? res.getBytes("archivo") : null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public String getArchivo(String path, String tabla, String clave, String campoNombre, String campoBytea) {
        this._archivoNombre = "";
        try {
            ResultSet res = this.consulta("select * from " + tabla + " where " + tabla.replace("tbl_", "id_") + "=" + clave + ";");
            if (res.next()) {
                this._archivoNombre = res.getString(campoNombre) != null ? res.getString(campoNombre) : "";
                if (this._archivoNombre.compareTo("") != 0) {
                    try {
                        this._archivo = new File(path, this._archivoNombre);
//                        if (!this._archivo.exists()) {
                        byte[] bytes = (res.getString(campoBytea) != null) ? res.getBytes(campoBytea) : null;
                        RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre, "rw");
                        archivo.write(bytes);
                        archivo.close();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this._archivoNombre;
    }

    public String getArchivoXml(String path, String tabla, String clave, String campoNombre, String campo) {
        this._archivoNombre = "";
        try {
            ResultSet res = this.consulta("select * from " + tabla + " where clave_acceso='" + clave + "'   ;");
            if (res.next()) {
                this._archivoNombre = res.getString(campoNombre) != null ? res.getString(campoNombre) : "";
                if (this._archivoNombre.compareTo("") != 0) {
                    try {
                        this._archivo = new File(path, this._archivoNombre);
//                        if (!this._archivo.exists()) {
                        //byte[] bytes = (res.getString(campoBytea)!=null) ? res.getBytes(campoBytea) : null;
                        RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre + ".xml", "rw");
                        archivo.writeBytes(campo);
                        archivo.close();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this._archivoNombre;
    }

    //////////////////////////////////////////////
    public boolean existeArchivoDocumental(String tabla, String clave, String campo) {
        this._archivoNombre = "";
        try {
            ResultSet res = this.consulta("select documento,nombre_documento from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            return this.getFilas(res) > 0;
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("" + e.getMessage());
        }
        return false;
    }
    
    //////////////////////////////////////////////
    public String getArchivoDocumental(String path, String tabla, String clave, String campo) {
        this._archivoNombre = "";
        try {
            ResultSet res = this.consulta("select documento,nombre_documento from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            if (res.next()) {
                this._archivoNombre = res.getString(2) != null ? res.getString(2) : "";
                if (this._archivoNombre.compareTo("") != 0) {
                    try {
                        this._archivo = new File(path, this._archivoNombre);
//                        if (!this._archivo.exists()) {
                        byte[] bytes = (res.getString(1) != null) ? res.getBytes(1) : null;
                        RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre, "rw");
                        archivo.write(bytes);
                        archivo.close();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }
                res.close();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("" + e.getMessage());
        }
        return this._archivoNombre;
    }

    //////////////////////////////////////////////
    public byte[] getArchivoDocumentalBytes(String tabla, String clave, String campo) {
        byte[] bytes = null;
        try {
            ResultSet res = this.consulta("select documento,nombre_documento from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            if (res.next()) {
                try {
                    bytes = (res.getString(1) != null) ? res.getBytes(1) : null;
                } catch (Exception e) {
                    System.out.println("" + e.getMessage());
                    return null;
                }
                res.close();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("" + e.getMessage());
        }
        return bytes;
    }

    public ResultSet getArchivoDocumentales(String tabla, String clave, String campo) {
        return this.consulta("select documento,nombre_documento,id_documento from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "' and eliminado='false';");
    }

    public ResultSet getArchivoDocumentales(String path, String tabla, String clave, String campo, boolean cerrar) {
        this._archivoNombre = "";
        ResultSet res = null;
        try {
            res = this.consulta("select documento,nombre_documento,id_documento from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "' and eliminado='false';");
            while (res.next()) {
                this._archivoNombre = res.getString(2) != null ? res.getString(2) : "";
                if (this._archivoNombre.compareTo("") != 0) {
                    try {
                        this._archivo = new File(path, this._archivoNombre);
//                        if (!this._archivo.exists()) {
                        byte[] bytes = (res.getString(1) != null) ? res.getBytes(1) : null;
                        RandomAccessFile archivo = new RandomAccessFile(path + this._archivoNombre, "rw");
                        archivo.write(bytes);
                        archivo.close();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (cerrar) {
                res.close();
            } else {
                res.beforeFirst();
            }
            this._archivoNombre = "";
            this._archivo = null;
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("" + e.getMessage());
        }
        return res;
    }

    public boolean setArchivoDocumental(String tabla, String clave, String campo, String nombre, File archivo, String catalogo, String base) {
        boolean r = false;
        try {
            Connection conexion = this.getConexion();
            PreparedStatement ps = null;
            if (ExisteArchivo(tabla, clave, campo) > 0) {
                ps = conexion.prepareStatement("UPDATE tbl_documentos SET nombre_documento='" + nombre + "', documento=? WHERE tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            } else {
                ps = conexion.prepareStatement("insert into tbl_documentos(documento,nombre_documento,tabla,id_tabla,campo_tabla,catalogo,base)values(?,'" + nombre + "','" + tabla + "','" + clave + "','" + campo + "','" + catalogo + "','" + base + "');");
            }
            this._archivo = archivo;
            conexion.setAutoCommit(false);
            FileInputStream archivoIS = new FileInputStream(this._archivo);
            try {
                /*ps.setBinaryStream(1, archivoIS, (int)archivo.length());*/
                byte buffer[] = new byte[(int) archivo.length()];
                archivoIS.read(buffer);
                ps.setBytes(1, buffer);

                ps.executeUpdate();
                conexion.commit();
                r = true;
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            } finally {
                archivoIS.close();
                ps.close();
            }

        } catch (Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }

    public boolean setArchivoDocumental(String tabla, String clave, String campo, String nombre, String archivo1, String catalogo, String base) {
        boolean r = false;
        try {
            File archivo = new File(archivo1);
            Connection conexion = this.getConexion();
            PreparedStatement ps = null;
            if (ExisteArchivo(tabla, clave, campo) > 0) {
                ps = conexion.prepareStatement("UPDATE tbl_documentos SET nombre_documento='" + nombre + "', documento=? WHERE tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            } else {
                ps = conexion.prepareStatement("insert into tbl_documentos(documento,nombre_documento,tabla,id_tabla,campo_tabla,catalogo,base)values(?,'" + nombre + "','" + tabla + "','" + clave + "','" + campo + "','" + catalogo + "','" + base + "');");
            }
            conexion.setAutoCommit(false);
            //if (this._archivo == null) {
            this._archivo = archivo;
            //}
            FileInputStream archivoIS = new FileInputStream(this._archivo);
            try {
                /*ps.setBinaryStream(1, archivoIS, (int)archivo.length());*/
                byte buffer[] = new byte[(int) archivo.length()];
                archivoIS.read(buffer);
                ps.setBytes(1, buffer);

                ps.executeUpdate();
                conexion.commit();
                r = true;
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            } finally {
                archivoIS.close();
                ps.close();
            }

        } catch (Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }

    public boolean setArchivoDocumentales(String tabla, String clave, String campo, String nombre, File archivo, String catalogo, String base) {
        boolean r = false;
        try {
            Connection conexion = this.getConexion();
            PreparedStatement ps = null;
            ps = conexion.prepareStatement("insert into tbl_documentos(documento,nombre_documento,tabla,id_tabla,campo_tabla,catalogo,base)values(?,'" + nombre + "','" + tabla + "','" + clave + "','" + campo + "','" + catalogo + "','" + base + "');");
            conexion.setAutoCommit(false);
            // if (this._archivo == null) {
            this._archivo = archivo;
            // }
            FileInputStream archivoIS = new FileInputStream(this._archivo);
            try {
                byte buffer[] = new byte[(int) archivo.length()];
                archivoIS.read(buffer);
                ps.setBytes(1, buffer);
                ps.executeUpdate();
                conexion.commit();
                r = true;
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            } finally {
                archivoIS.close();
                ps.close();
            }

        } catch (Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }

    public boolean setArchivoDocumental(String tabla, String clave, String campo, String nombre, byte[] archivo, String catalogo, String base) {
        boolean r = false;
        try {
            Connection conexion = this.getConexion();
            PreparedStatement ps = null;
            if (ExisteArchivo(tabla, clave, campo) > 0) {
                ps = conexion.prepareStatement("UPDATE tbl_documentos SET nombre_documento='" + nombre + "', documento=? WHERE tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            } else {
                ps = conexion.prepareStatement("insert into tbl_documentos(documento,nombre_documento,tabla,id_tabla,campo_tabla,catalogo,base)values(?,'" + nombre + "','" + tabla + "','" + clave + "','" + campo + "','" + catalogo + "','" + base + "');");
            }
            conexion.setAutoCommit(false);
            try {
                /*ps.setBinaryStream(1, archivoIS, (int)archivo.length());*/

                ps.setBytes(1, archivo);

                ps.executeUpdate();
                conexion.commit();
                r = true;
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            } finally {
                ps.close();
            }

        } catch (Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }

    public boolean setArchivoDocumentales(String tabla, String clave, String campo, String nombre, String archivo1, String catalogo, String base) {
        boolean r = false;
        try {
            File archivo = new File(archivo1);
            Connection conexion = this.getConexion();
            PreparedStatement ps = null;
            ps = conexion.prepareStatement("insert into tbl_documentos(documento,nombre_documento,tabla,id_tabla,campo_tabla,catalogo,base)values(?,'" + nombre + "','" + tabla + "','" + clave + "','" + campo + "','" + catalogo + "','" + base + "');");
            conexion.setAutoCommit(false);
            if (this._archivo == null) {
                this._archivo = archivo;
            }
            FileInputStream archivoIS = new FileInputStream(this._archivo);
            try {
                byte buffer[] = new byte[(int) archivo.length()];
                archivoIS.read(buffer);
                ps.setBytes(1, buffer);
                ps.executeUpdate();
                conexion.commit();
                r = true;
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            } finally {
                archivoIS.close();
                ps.close();
            }

        } catch (Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }

    public int ExisteArchivo(String tabla, String clave, String campo) {
        int contador = 0;
        try {
            ResultSet r = this.consulta("SELECT count(*) from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "' and eliminado=false;");
            if (r.next()) {
                contador = (r.getString(1) != null) ? r.getInt(1) : 0;
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contador;

    }

    public String getArchivoDocumentaltTexto(String tabla, String clave, String campo) {
        String documento = "";
        try {
            ResultSet r = this.consulta("SELECT documentotexto from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "' and eliminado=false;");
            if (r.next()) {
                documento = (r.getString(1) != null) ? r.getString(1) : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documento;

    }

    public String getArchivoNombreDocumental(String columna, String tabla, String clave, String campo) {
        String documento = "";
        try {
            ResultSet r = this.consulta("SELECT " + columna + " from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "' and eliminado=false;");
            if (r.next()) {
                documento = (r.getString(1) != null) ? r.getString(1) : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documento;

    }

    public boolean setArchivoDocumentalTexto(String documento, String numero_documento, String id_sucursal, String tabla, String clave, String campo, String catalogo, String base) {
        boolean r = false;
        try {
            Connection conexion = this.getConexion();
            PreparedStatement ps = null;
            if (ExisteArchivo(tabla, clave, campo) > 0) {
                ps = conexion.prepareStatement("UPDATE tbl_documentos SET documentotexto='" + documento + "', numero_documento='" + numero_documento + "', id_sucursal='" + id_sucursal + "' WHERE tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            } else {
                ps = conexion.prepareStatement("insert into tbl_documentos(documentotexto,numero_documento,id_sucursal,tabla,id_tabla,campo_tabla,catalogo,base)values('" + documento + "','" + numero_documento + "','" + id_sucursal + "','" + tabla + "','" + clave + "','" + campo + "','" + catalogo + "','" + base + "');");
            }
            conexion.setAutoCommit(false);
            try {
                /*ps.setBinaryStream(1, archivoIS, (int)archivo.length());*/
                ps.executeUpdate();
                conexion.commit();
                r = true;
            } catch (Exception e) {
                this._error = e.getMessage();
                e.printStackTrace();
            } finally {
                ps.close();
            }

        } catch (Exception e) {
            this._error = e.getMessage();
            e.printStackTrace();
        }
        return r;
    }

    public boolean setElimnarDocumento(String tabla, String clave, String campo) {
        return this.ejecutar("update tbl_documentos set eliminado='true' where tabla='" + tabla + "' and id_documento='" + clave + "' and campo_tabla='" + campo + "' and eliminado='false';");
    }

    public String[] getArchivoDocumentalBase64(String tabla, String clave, String campo) {
        String archivo[] = {"", ""};
        String complemento = "";
        try {
            ResultSet res = this.consulta("select documento,nombre_documento from tbl_documentos where tabla='" + tabla + "' and id_tabla='" + clave + "' and campo_tabla='" + campo + "';");
            if (res.next()) {
                byte[] fichero = (res.getString(1) != null) ? res.getBytes(1) : null;
                archivo[1] = (res.getString(2) != null) ? res.getString(2) : "";
                if (archivo[1].toLowerCase().contains("png") || archivo[1].toLowerCase().contains("jpg") || archivo[1].toLowerCase().contains("jpeg")) {
                    complemento = "data:image/jpeg;base64,";
                }
                if (archivo[1].toLowerCase().contains("pdf")) {
                    complemento = "data:application/pdf;base64,";
                }
                archivo[0] = complemento + "" + Base64.getEncoder().encodeToString(fichero);
                res.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return archivo;
    }
    
    public boolean actualizaRefArchivo(String tabla, String clave, String campo, String tablaNueva, String claveNueva, String campoNueva)
    {
        return this.ejecutar("update tbl_documentos set tabla='"+tablaNueva+"', id_tabla='"+claveNueva+"', campo_tabla='"+campoNueva+"' where tabla='"+tabla+"' and id_tabla='"+clave+"' and campo_tabla='"+campo+"'");
    }
    
}
