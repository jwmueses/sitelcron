/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.commons.net.ftp.FTP;  
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author jorge
 */
public class Ftp {
    private FTPClient ftp;
    private String error;
    private String servidor;
    
    public Ftp()
    {}
    
    public boolean conectar(String servidor, int puerto, String usuario, String clave)
    {
        try {
            this.ftp = new FTPClient();
            //ftp.connect(servidor);
            this.ftp.setDefaultTimeout(30000);
            this.ftp.setConnectTimeout(30000);
            this.ftp.connect(servidor, puerto);
            this.ftp.login(usuario, clave);
            
            int reply = this.ftp.getReplyCode();
            if(FTPReply.isPositiveCompletion(reply)){
                this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
                this.servidor = servidor;
                return true;
            }
        } catch (Exception e) {
            this.error = "El servidor "+servidor+" rechazo la conexion" + e.getMessage();
            System.out.println( this.error );
        }
        return false;
    }
    
    public boolean cd(String directorio)
    {
        try {
            this.ftp.changeWorkingDirectory(directorio);
            return true;
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return false;
    }
    
    public boolean subirArchivo(String rutaArchivo, String rutaArchivoRemoto)
    {
        try {
            BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(rutaArchivo));//Ruta del archivo para enviar
            this.ftp.enterLocalPassiveMode();
            this.ftp.storeFile(rutaArchivoRemoto, buffIn);//Ruta completa de alojamiento en el FTP
            buffIn.close(); //Cerrar envio de arctivos al FTP
            return true;
        } catch (Exception e) {
            this.error = "Error al subir archivo de " + rutaArchivoRemoto+ " al servidor "+this.servidor+": " + e.getMessage();
            System.out.println( this.error );
        }
        return false;
    }
    
    public boolean descargarArchivo(String rutaArchivo, String rutaArchivoRemoto)
    {
        return false;
    }
    public void desconectar()
    {
        try{
            this.ftp.logout(); //Cerrar sesión
            this.ftp.disconnect();//Desconectarse del servidor
        }catch(Exception e){
            this.error = "Error al desconectar servidor FTP "+this.servidor+": " + e.getMessage();
        }
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
