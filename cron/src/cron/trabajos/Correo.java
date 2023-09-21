/**
 * @version 1.0
 * @package jm.web.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2008 por Jorge W. Mueses Cevallos. Todos los
 * derechos reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
 */
package cron.trabajos;

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
        String ssl = parametros.getProperty("ssl");
        String starttls = parametros.getProperty("starttls");
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
            props.put("mail.transport.protocol", "smtp");
            if(ssl.compareTo("true")==0){
                props.put("mail.smtp.ssl.enable", ssl);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            if(starttls.compareTo("true")==0){
                props.put("mail.smtp.starttls.enable", starttls);
            }
            
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
    
//    public static String ModeloHtml(String tipo, String titulo, String generado, String cuerpo, String titulonota, String cuerponota) {
//        String html = "";
//        html += "<div id=':22c' class='a3s aXjCH ' role='gridcell' tabindex='-1'>";
//        html += " <table style='border-color:#1d54ff' width='550' cellspacing='0' cellpadding='0' border='0' align='center'>";
//        html += "     <tbody>";
//        html += "        <tr>";
//        html += "          <td width='573' valign='top'>";
//        html += "             <table width='550' cellspacing='0' cellpadding='0' border='0'>";
//        html += "                 <tbody>";
//        html += "                     <tr style='background: rgba(0, 0, 0, 0.20);'>";
//        html += "                        <td style='font-size:  35px; '><img src='https://138.185.137.120/html/images/logosfondo.png' alt='' tabindex='0' width='250' height='75' border='0'>#tipo#</td>";
//        html += "                    </tr>";
//        html += "                </tbody>";
//        html += "            </table>";
//        html += "         </td>";
//        html += "     </tr>";
//        html += "   <tr>";
//        html += "        <td width='550' valign='top'>";
//        html += "            <table width='550' cellspacing='0' cellpadding='0' border='0'>";
//        html += "                <tbody>";
//        html += "                  <tr>";
//        html += "                      <td style='width:1px;border-left:solid 1px #1d54ff' width='1' bgcolor='#FFFFFF'></td>";
//        html += "                      <td width='17'>&nbsp;</td>";
//        html += "                      <td class='m_4307618135234332710negro8' width='516' valign='top'>";
//        html += "                          <p>&nbsp;</p>";
//        html += "                          <p>#titulo#</p>";
//        html += "                          <p>#generado#</p>";
//        html += "                        <p><strong>Estimado/a</strong>&nbsp;.</p>";
//        html += "                        #cuerpo#";
//        html += "                        <p>Proveedor de Servicio de Internet: <strong> - </strong></p>";
//        html += "                       <p>En caso de no haber realizado esta operación comuníquese inmediatamente a <strong>1700 SAITEL - 0996724835</strong></p>";
//        html += "                      <p>Asesor Virtual<br><strong>Saitel</strong></p>";
//        html += "                 </td>";
//        html += "                   <td width='17'>&nbsp;</td>";
//        html += "                     <td style='width:1px;border-right:solid 1px #1d54ff' width='1' bgcolor='#FFFFFF'></td>";
//        html += "                  </tr>";
//        html += "                  <tr><td colspan='5' style='width:1px;border-top:solid 1px #1d54ff'></td></tr>";
//        html += "                 </tbody>";
//        html += "              </table>";
//        html += "            </td>";
//        html += "         </tr>";
//        html += "     </tbody>";
//        html += " </table>";
//        html += " <p>&nbsp;</p>";
//        html += "  <table width='100%' cellspacing='1' cellpadding='3' border='0' bgcolor='B1B0B1' align='center'>";
//        html += "      <tbody>";
//        html += "         <tr>";
//        html += "             <td class='m_4307618135234332710gris7' bgcolor='#FFFFFF'><strong><span class='m_4307618135234332710verde7'><span style='color:#89a83e'>#titulonota#</span></span></strong><span style='color:#89a83e'><span class='m_4307618135234332710verde7'>:</span></span>#cuerponota#</td>";
//        html += "         </tr>";
//        html += "      </tbody>";
//        html += "   </table>";
//        html += "</div>";
//        html = html.replace("#tipo#", tipo);
//        html = html.replace("#titulo#", titulo);
//        html = html.replace("#generado#", generado);
//        html = html.replace("#cuerpo#", cuerpo);
//        html = html.replace("#titulonota#", titulonota);
//        html = html.replace("#cuerponota#", cuerponota);
//        return html;
//    }
    
}
