/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.web;

import java.util.Random;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class Cadena {

    public static String html_decode(String html) {
        html = html.replace("&lt;", "<");
        html = html.replace("&gt;", ">");
        html = html.replace("&amp;", "&");
        return html;
    }

    public static String quitaLatinas(String html) {
        html = html.replace("à", "a");
        html = html.replace("è", "e");
        html = html.replace("ì", "i");
        html = html.replace("ò", "o");
        html = html.replace("ù", "u");
        html = html.replace("À", "A");
        html = html.replace("È", "E");
        html = html.replace("Ì", "I");
        html = html.replace("Ò", "O");
        html = html.replace("Ù", "u");
        html = html.replace("á", "a");
        html = html.replace("é", "e");
        html = html.replace("í", "i");
        html = html.replace("ó", "o");
        html = html.replace("ú", "u");
        html = html.replace("Á", "A");
        html = html.replace("É", "E");
        html = html.replace("Í", "I");
        html = html.replace("Ó", "O");
        html = html.replace("Ú", "u");
        //html = html.replace(".", "");
        html = html.replace("ñ", "n");
        html = html.replace("Ñ", "N");
        html = html.replace("-", "");
        html = html.replace("/", "");
        html = html.replace("\\", "");
        html = html.replace("&", "Y");
        html = html.replace("`", "");
        html = html.replace("'", "");
        html = html.replace("'", "");
        html = html.replace("\"", "");
        return html;
    }

    public static String setSecuencial(String numero) {
        String relleno = "";
        for (int i = 0; i < (9 - numero.length()); i++) {
            relleno += "0";
        }
        return relleno + numero;
    }

    public static String setFecha(String fecha) {
        String vecFecha[];
        String anio = "0";
        String mes = "0";
        String dia = "0";
        if (fecha.indexOf("/") > 0) {
            vecFecha = fecha.replace("'", "").replace("\"", "").split("/");
            anio = vecFecha[2];
            mes = vecFecha[1];
            dia = vecFecha[0];
        } else {
            vecFecha = fecha.replace("'", "").replace("\"", "").split("-");
            anio = vecFecha[0];
            mes = vecFecha[1];
            dia = vecFecha[2];
        }
        String fechaRetorno = (dia.length() == 1 ? "0" + dia : dia) + "/" + (mes.length() == 1 ? "0" + mes : mes) + "/" + anio;

        return fechaRetorno;
    }

    public static String capital(String frase) {
        String cap = "";
        if (frase.compareTo("") != 0) {
            String vec[] = frase.split(" ");
            String aux = "";
            for (int i = 0; i < vec.length; i++) {
                if (vec[i].compareTo("") != 0) {
                    aux = vec[i].trim();
                    cap += aux.substring(0, 1).toUpperCase() + aux.substring(1, aux.length()).toLowerCase() + " ";
                }
            }
        }
        return cap.trim();
    }

    public static String getRandomClave(int limite) {
        String clave = "";
        String alfanumero = "0123456789ABCDEF";
        char caracter[] = alfanumero.toCharArray();
        Random rnd = new Random();
        int num_rand = 0;
        for (int i = 0; i < limite; i++) {
            num_rand = (int) (rnd.nextDouble() * caracter.length);
            clave += caracter[num_rand];
        }
        return clave;
    }

    public static int contarSubcadena(String cadena, String ocurencia) {
        int contador = 0;
        while (cadena.contains(ocurencia)) {
            cadena = cadena.substring(cadena.indexOf(ocurencia) + ocurencia.length(), cadena.length());
            contador++;
        }
        return contador;
    }

}
