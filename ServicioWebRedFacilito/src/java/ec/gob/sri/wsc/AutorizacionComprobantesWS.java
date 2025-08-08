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


import com.thoughtworks.xstream.XStream;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/**
 *
 * @author jorjoluiso
 */
public class AutorizacionComprobantesWS {

    private AutorizacionComprobantesOfflineService service;
    private static String xmlAutorizacion;
    private static String error = "";

    public AutorizacionComprobantesWS(String wsdlLocation) throws MalformedURLException {
        /*
         URL url = new URL(wsdlLocation);
         service = new AutorizacionComprobantesService(url);
         */
        this.service = new AutorizacionComprobantesOfflineService(new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));

    }

    public RespuestaComprobante llamadaWSAutorizacionInd(String claveDeAcceso) {
        RespuestaComprobante response = null;
        try {
            AutorizacionComprobantesOffline port = this.service.getAutorizacionComprobantesOfflinePort();
            response = port.autorizacionComprobante(claveDeAcceso);
        } catch (Exception e) {
            Logger.getLogger(AutorizacionComprobantesWS.class.getName()).log(Level.SEVERE, null, e);
            this.error = e.getMessage();
            return response;
        }

        return response;
    }

    public static String autorizarComprobanteIndividual(String claveDeAcceso, String nombreArchivo, String urlWsdl) {
        StringBuilder mensaje = new StringBuilder();
        DirectorioConfiguracion dirConfig=new DirectorioConfiguracion();
        try {
            String dirAutorizados = dirConfig.getRutaArchivoAutorizado();
            String dirNoAutorizados = dirConfig.getRutaArchivoNoAutorizado();

            RespuestaComprobante respuesta = null;

            //for (int i = 0; i < 5; i++) {
                respuesta = new AutorizacionComprobantesWS(urlWsdl).llamadaWSAutorizacionInd(claveDeAcceso);

                /*if (!respuesta.getAutorizaciones().getAutorizacion().isEmpty()) {
                    break;
                }
                Thread.currentThread();
                Thread.sleep(300L);
            }*/
            int i;
            if (respuesta != null) {
                i = 0;
                for (Autorizacion item : respuesta.getAutorizaciones().getAutorizacion()) {
                    mensaje.append(item.getEstado());

                    item.setComprobante("<![CDATA[" + item.getComprobante() + "]]>");

                    XStream xstream = XStreamUtil.getRespuestaXStream();
                    Writer writer = null;
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    writer = new OutputStreamWriter(outputStream, "UTF-8");
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

                    xstream.toXML(item, writer);
                    xmlAutorizacion = outputStream.toString("UTF-8");

                    if ((i == 0) && (item.getEstado().equals("AUTORIZADO"))) {
                        ArchivoUtils.stringToArchivo(dirAutorizados + File.separator + nombreArchivo, xmlAutorizacion);
                        break;
                    }
                    if (item.getEstado().equals("NO AUTORIZADO") || item.getEstado().equals("RECHAZADO")) {
                        ArchivoUtils.stringToArchivo(dirNoAutorizados + File.separator + nombreArchivo, xmlAutorizacion);
                        mensaje.append("|" + obtieneMensajesAutorizacion(item));
                        break;
                    }
                    i++;
                }
            }else{
                mensaje.append(error);
            }

            if (respuesta.getAutorizaciones().getAutorizacion().isEmpty() == true) {
                mensaje.append("TRANSMITIDO SIN RESPUESTA|Ha ocurrido un error en el proceso de la Autorización");
            }
        /*} catch (InterruptedException | IOException ex) {
            Logger.getLogger(AutorizacionComprobantesWS.class.getName()).log(Level.SEVERE, null, ex);*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mensaje.toString();
    }

    public static String obtieneMensajesAutorizacion(Autorizacion autorizacion) {
        StringBuilder mensaje = new StringBuilder();
        for (Mensaje m : autorizacion.getMensajes().getMensaje()) {
            if (m.getInformacionAdicional() != null) {
                mensaje.append("\n" + m.getMensaje() + ": " + m.getInformacionAdicional());
            } else {
                mensaje.append("\n" + m.getMensaje());
            }
        }

        return mensaje.toString();
    }
    
    public static String getAutorizacionXml()
    {
        return xmlAutorizacion;
    }
    
}
