/**
 * @version 1.0
 * @package jm.web.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2008 por Jorge W. Mueses Cevallos. Todos los
 * derechos reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
 */
package ec.com.saitel.gomax.utils;

import java.io.File;
import java.util.Properties;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.InternetAddress;

public class Correo {

    static String error = "";

    /**
     * Funci�n que env�a un correo electr�nico desde un servidor SMTP de un
     * remitente a un destinatario.
     *
     * @param svrSMTP. Nombre completo o IP del servidor SMTP.
     * @param dirEnv. Direcci�n del remitente.
     * @param dirA. Direcci�n del destinatario.
     * @param dirAcc. Direcci�n de copia.
     * @param dirAbcc. Direcci�n de copia oculta.
     * @param asunto. El asunto del mensaje.
     * @param txt. Cuerpo del mensaje.
     * @param esHTML. Especifica si el mensaje debe ser interpretado como HTML o
     * solo texto.
     * @return TRue si el correo es enviado satisfactoriamente o false en caso
     * contrario.
     */

    public static boolean enviar(String svrSMTP, int puerto, String dirEnv, String clave, String dirA, String dirAcc, String dirAbcc, String asunto,
            StringBuilder txt, boolean esHTML, List adjuntos, Properties propiedades) {
        String CONFIGSET = "ConfigSet";
        Properties parametros = propiedades != null ? propiedades : new Properties();
        String ssl = parametros.getProperty("ssl")!=null ? parametros.getProperty("ssl") : "false";
        String starttls = parametros.getProperty("starttls")!=null ? parametros.getProperty("starttls") : "true";
        try {
            ////////
            InternetAddress[] destTo = null;
            InternetAddress[] destCc = null;
            InternetAddress[] destbcc = null;
            String[] tmp = null;
            if (dirA.trim().compareTo("") != 0) {
                dirA = dirA.replace(";", ",");
                tmp = dirA.split(",");
                destTo = new InternetAddress[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    destTo[i] = new InternetAddress(tmp[i]);
                }
            }
            if (dirAcc.trim().compareTo("") != 0) {
                dirAcc = dirAcc.replace(";", ",");
                tmp = dirAcc.split(",");
                destCc = new InternetAddress[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    destCc[i] = new InternetAddress(tmp[i]);
                }
            }
            if (dirAbcc.trim().compareTo("") != 0) {
                dirAbcc = dirAbcc.replace(";", ",");
                tmp = dirAbcc.split(",");
                destbcc = new InternetAddress[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    destbcc[i] = new InternetAddress(tmp[i]);
                }
            }
            /////////
            Properties props = System.getProperties();
            props.put("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.host", svrSMTP);
            props.put("mail.smtp.port", puerto);
            props.put("mail.smtp.socketFactory.port", puerto);
//            props.put("mail.transport.protocol", "smtp");
            if(ssl.compareTo("true")==0){
                props.put("mail.smtp.ssl.enable", ssl);
            }
            if(starttls.compareTo("true")==0){
                props.put("mail.smtp.starttls.enable", starttls);
            }
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            
            Session session = Session.getDefaultInstance(props);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(dirEnv, "SAITEL"));
            if (destTo != null) {
                msg.setRecipients(Message.RecipientType.TO, destTo);
            }
            if (destCc != null) {
                msg.setRecipients(Message.RecipientType.CC, destCc);
            }
            if (destbcc != null) {
                msg.setRecipients(Message.RecipientType.BCC, destbcc);
            }
            msg.setSubject(asunto);
            msg.setSentDate(new Date());
            msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(txt.toString(), "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            //Se adjuntan los archivos al correo
            if (adjuntos != null) {
                Iterator it = adjuntos.iterator();
                while (it.hasNext()) {
                    String rutaAdjunto = (String) it.next();
                    messageBodyPart = new MimeBodyPart();
                    File f = new File(rutaAdjunto);
                    if (f.exists()) {
                        DataSource source = new FileDataSource(rutaAdjunto);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(f.getName());
                        multipart.addBodyPart(messageBodyPart);
                    }
                }
            }

            //Se junta el mensaje y los archivos adjuntos
            msg.setContent(multipart);
            Transport t = session.getTransport();
            System.out.println("Sending...");
            t.connect(svrSMTP, dirEnv, clave);
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();
        } catch (Exception me) {
            Correo.error = me.getMessage();
            System.out.println("Error al enviar correo: " + me);
            return false;
        }
        return true;
    }

    public static String getError() {
        return Correo.error;
    }

}
