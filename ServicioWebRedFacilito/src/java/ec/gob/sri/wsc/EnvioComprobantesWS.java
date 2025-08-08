/*
 * Copyright (C) 2014 jorjoluiso
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ec.gob.sri.wsc;


import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.Mensaje;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author jorjoluiso
 */
public class EnvioComprobantesWS {

    private static RecepcionComprobantesOfflineService service;
  
    public EnvioComprobantesWS(String wsdlLocation) throws MalformedURLException {
        
        URL url = new URL(wsdlLocation);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
         
        this.service = new RecepcionComprobantesOfflineService(url, qname);
    }

    public static final Object webService(String wsdlLocation) {
        try {
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
            URL url = new URL(wsdlLocation);
            service = new RecepcionComprobantesOfflineService(url, qname);
            return null;
        } catch (MalformedURLException ex) {
            Logger.getLogger(EnvioComprobantesWS.class.getName()).log(Level.SEVERE, null, ex);
            return ex;
        } catch (WebServiceException ws) {
            return ws;
        }
    }

    public RespuestaSolicitud enviarComprobante(File xmlFile, String versionXsd) {
        RespuestaSolicitud response = null;
        try {
            RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
            response = port.validarComprobante(ArchivoUtils.archivoToByte(xmlFile));
        } catch (Exception e) {
            Logger.getLogger(EnvioComprobantesWS.class.getName()).log(Level.SEVERE, null, e);
            response = new RespuestaSolicitud();
            response.setEstado(e.getMessage());
            return response;
        }

        return response;
    }

    public RespuestaSolicitud enviarComprobanteLotes(String ruc, byte[] xml, String tipoComprobante, String versionXsd) {
        RespuestaSolicitud response = null;
        try {
            RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();

            response = port.validarComprobante(xml);
        } catch (Exception e) {
            Logger.getLogger(EnvioComprobantesWS.class.getName()).log(Level.SEVERE, null, e);
            response = new RespuestaSolicitud();
            response.setEstado(e.getMessage());
            return response;
        }
        return response;
    }

    public RespuestaSolicitud enviarComprobanteLotes(String ruc, File xml, String tipoComprobante, String versionXsd) {
        RespuestaSolicitud response = null;
        try {
            RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
            response = port.validarComprobante(ArchivoUtils.archivoToByte(xml));
        } catch (Exception e) {
            Logger.getLogger(EnvioComprobantesWS.class.getName()).log(Level.SEVERE, null, e);
            response = new RespuestaSolicitud();
            response.setEstado(e.getMessage());
            return response;
        }
        return response;
    }

    public static RespuestaSolicitud obtenerRespuestaEnvio(File archivo, String claveDeAcceso, String urlWsdl) {
        RespuestaSolicitud respuesta = new RespuestaSolicitud();
        EnvioComprobantesWS cliente = null;
        try {
            cliente = new EnvioComprobantesWS(urlWsdl);
        } catch (Exception ex) {
            Logger.getLogger(EnvioComprobantesWS.class.getName()).log(Level.SEVERE, null, ex);
            respuesta.setEstado(ex.getMessage());
            return respuesta;
        }
        respuesta = cliente.enviarComprobante(archivo, "1.1.0");

        return respuesta;
    }

    public static void guardarRespuesta(String claveDeAcceso, String archivo, String estado, java.util.Date fecha) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());

            Respuesta item = new Respuesta(null, claveDeAcceso, archivo, estado, sqlDate);
            //Guardar respuesta
        } catch (Exception ex) {
            Logger.getLogger(EnvioComprobantesWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String obtenerMensajeRespuesta(RespuestaSolicitud respuesta) {
        StringBuilder mensajeDesplegable = new StringBuilder();
        //if (respuesta.getEstado().equals("DEVUELTA") == true) {
            RespuestaSolicitud.Comprobantes comprobantes = respuesta.getComprobantes();
            for (Comprobante comp : comprobantes.getComprobante()) {
                mensajeDesplegable.append(comp.getClaveAcceso());
                mensajeDesplegable.append("\n");
                for (Mensaje m : comp.getMensajes().getMensaje()) {
                    mensajeDesplegable.append(m.getMensaje()).append(" :\n");
                    mensajeDesplegable.append(m.getInformacionAdicional() != null ? m.getInformacionAdicional() : "");
                    mensajeDesplegable.append("\n");
                }
                mensajeDesplegable.append("\n");
            }
        //}

        return mensajeDesplegable.toString();
    }
}
