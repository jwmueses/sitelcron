/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class Utils {

    public static String codigoenlace(int longitud) {
        String codigo = "";
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            for (int i = 0; i < longitud; i++) {
                codigo += number.nextInt(21);
            }
        } catch (NoSuchAlgorithmException e) {
            codigo = "";
            System.out.println("" + e.getMessage());
        }
        return codigo;
    }

    public static String getCurrentUrl(HttpServletRequest req, String prmstr) {
        String url = getCurrentUrlWithoutParams(req);
        url += "?" + prmstr;
        return url;
    }

    public static String getCurrentUrlWithoutParams(HttpServletRequest request) {
        String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");
        if (uri == null) {
            return request.getRequestURL().toString();
        }
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String url = scheme + "://" + serverName + ":" + serverPort + uri;
        return url;
    }

    public static String getUrlActual(HttpServletRequest request, String uri) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String url = scheme + "://" + serverName + ":" + serverPort + uri;
        return url;
    }

    public static String putcardbody(String objeto, String html1) {
        StringBuilder html = new StringBuilder();
        html.append(objeto);
        html.append("<div class=\"col-md-12\">");
        html.append("<div class=\"card\">");
        html.append("<div class=\"card-body\">");
        html.append("<div class=\"row\">");
        html.append("<div class=\"col-md-12\">");
        html.append(html1);
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        return html.toString();
    }

    public static String getStringFromFile(String archivo) throws IOException {
        File file = new File(archivo);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            cadXml.append(linea);
        }
        return cadXml.toString();
    }
}
