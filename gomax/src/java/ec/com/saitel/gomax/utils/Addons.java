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


/**
 *
 * @author Jorge
 */
public class Addons {

    public static String truncar(double num) {
        /*String cad = String.valueOf(Math.scalb(num, 2));
        return cad;*/
        if (num > 0) {
            num = num + 0.0009f;
        }
        String cad2 = String.valueOf(num).replace(".", ":");
        String cad[] = cad2.split(":");
        String res = "";
        if (cad.length > 1) {
            cad[1] += "000";
            res = cad[1].substring(0, 2);
        }
        return cad[0] + "." + res;
    }

    public static String truncar(double num, int decimales) {
        /*String cad = String.valueOf(Math.scalb(num, 2));
        return cad;*/
        if (num > 0) {
            num = num + 0.0009f;
        }
        String cad2 = String.valueOf(num).replace(".", ":");
        String cad[] = cad2.split(":");
        String res = "";
        if (cad.length > 1) {
            cad[1] += "0000000000000";
            res = cad[1].substring(0, decimales);
        }
        return cad[0] + "." + res;
    }

    public static String truncar(String num2) {
        double num = Double.valueOf(num2);
        if (num > 0) {
            num = num + 0.0009f;
        }
        String cad2 = String.valueOf(num).replace(".", ":");
        String cad[] = cad2.split(":");
        String res = "";
        if (cad.length > 1) {
            cad[1] += "000";
            res = cad[1].substring(0, 2);
        }
        return cad[0] + "." + res;
    }

    public static String getTextFecha(String fecha) {
        String vec_fecha[] = fecha.indexOf("/") > 0 ? fecha.split("/") : fecha.split("-");
        String mes = "diciembre";
        switch (Integer.parseInt(vec_fecha[1])) {
            case 1:
                mes = "enero";
                break;
            case 2:
                mes = "febrero";
                break;
            case 3:
                mes = "marzo";
                break;
            case 4:
                mes = "abril";
                break;
            case 5:
                mes = "mayo";
                break;
            case 6:
                mes = "junio";
                break;
            case 7:
                mes = "julio";
                break;
            case 8:
                mes = "agosto";
                break;
            case 9:
                mes = "septiembre";
                break;
            case 10:
                mes = "octubre";
                break;
            case 11:
                mes = "noviembre";
                break;
            default:
                mes = "diciembre";
        }
        if (fecha.indexOf("/") > 0) {
            return (vec_fecha[0] + " de " + mes + " de " + vec_fecha[2]);
        }
        return (vec_fecha[2] + " de " + mes + " de " + vec_fecha[0]);
    }

    public static String fechaAl(String fecha) {
        String anio = "2010";
        String mes = "01";
        String dia = "";
        if (fecha.indexOf("/") > 0) {
            String vec[] = fecha.split("/");
            anio = vec[2];
            mes = vec[1];
            dia = vec[0];
        } else {
            String vec[] = fecha.split("-");
            anio = vec[0];
            mes = vec[1];
            dia = vec[2];
        }
        String f = "";
        switch (Integer.parseInt(mes)) {
            case 1:
                f = dia + " de enero de " + anio;
                break;
            case 2:
                f = dia + " de febrero de " + anio;
                break;
            case 3:
                f = dia + " de marzo de " + anio;
                break;
            case 4:
                f = dia + " de abril de " + anio;
                break;
            case 5:
                f = dia + " de mayo de " + anio;
                break;
            case 6:
                f = dia + " de junio de " + anio;
                break;
            case 7:
                f = dia + " de julio de " + anio;
                break;
            case 8:
                f = dia + " de agosto de " + anio;
                break;
            case 9:
                f = dia + " de septiembre de " + anio;
                break;
            case 10:
                f = dia + " de octubre de " + anio;
                break;
            case 11:
                f = dia + " de noviembre de " + anio;
                break;
            case 12:
                f = dia + " de diciembre de " + anio;
                break;
        }
        return f;
    }

    public static double redondear(double valor) {
        return (Math.round(valor * Math.pow(10, 2)) / Math.pow(10, 2));
    }

    public static double redondear(double valor, int decimales) {
        return (Math.round(valor * Math.pow(10, decimales)) / Math.pow(10, decimales));
    }

    public static String redondear(String valor) {
        double res = (Math.round(Double.valueOf(valor) * Math.pow(10, 2)) / Math.pow(10, 2));
        return String.valueOf(res);
    }

    
    public static String toFechaSQL(String f) {
        if (f.indexOf("-") >= 0) {
            String vec_f[] = f.split("-");
            return (vec_f[2] + "/" + vec_f[1] + "/" + vec_f[0]);
        } else if (f.indexOf("/") >= 0) {
            return f;
        }
        return "00/00/0000";
    }

    public static String getMesSRI(int mes) {
        String res = "";
        switch (mes) {
            case 1:
                res = "ENE";
                break;
            case 2:
                res = "FEB";
                break;
            case 3:
                res = "MAR";
                break;
            case 4:
                res = "ABR";
                break;
            case 5:
                res = "MAY";
                break;
            case 6:
                res = "JUN";
                break;
            case 7:
                res = "JUL";
                break;
            case 8:
                res = "AGO";
                break;
            case 9:
                res = "SEP";
                break;
            case 10:
                res = "OCT";
                break;
            case 11:
                res = "NOV";
                break;
            case 12:
                res = "DIC";
                break;
        }
        return res;
    }

    public static String getMes(int mes) {
        String res = "";
        switch (mes) {
            case 1:
                res = "ENERO";
                break;
            case 2:
                res = "FEBRERO";
                break;
            case 3:
                res = "MARZO";
                break;
            case 4:
                res = "ABRIL";
                break;
            case 5:
                res = "MAYO";
                break;
            case 6:
                res = "JUNIO";
                break;
            case 7:
                res = "JULIO";
                break;
            case 8:
                res = "AGOSTO";
                break;
            case 9:
                res = "SEPTIEMBRE";
                break;
            case 10:
                res = "OCTUBRE";
                break;
            case 11:
                res = "NOVIEMBRE";
                break;
            case 12:
                res = "DICIEMBRE";
                break;
        }
        return res;
    }

    public static String rellenarCeros(long valor, int longitud) {
        String res = "";
        int nums_ocupados = String.valueOf(valor).length();
        for (int i = 0; i < longitud - nums_ocupados; i++) {
            res += "0";
        }
        return res + valor;
    }
    
    public static String rellenarCeros(float valor, int longitud) {
        String res = "";
        int nums_ocupados = String.valueOf(valor).length();
        for (int i = 0; i < longitud - nums_ocupados; i++) {
            res += "0";
        }
        return res + valor;
    }

    public static String rellenarCeros(String valor, int longitud) {
        String res = "";
        int nums_ocupados = valor.length();
        for (int i = 0; i < longitud - nums_ocupados; i++) {
            res += "0";
        }
        return res + valor;
    }
    
    public static String rellenarEspaciosDerecha(String valor, int longitud) {
        String res = "";
        int nums_ocupados = valor.length();
        for (int i = nums_ocupados; i < longitud; i++) {
            res += " ";
        }
        return valor + res;
    }

    public static float StringToFloat(String valor) {
        long entero = Long.parseLong(valor.substring(0, valor.length() - 2));
        String decimal = valor.substring(valor.length() - 2, valor.length());
        String flotante = entero + "." + decimal;
        return Float.parseFloat(flotante);
    }

    public static String rellenarCeros(int valor, int longitud) {
        String res = "";
        int nums_ocupados = String.valueOf(valor).length();
        for (int i = 0; i < longitud - nums_ocupados; i++) {
            res += "0";
        }
        return res + valor;
    }

    public static String getMesFecha(String fecha) {
        String vec_fecha[] = fecha.indexOf("/") > 0 ? fecha.split("/") : fecha.split("-");
        String mes = "DICIEMBRE";
        switch (Integer.parseInt(vec_fecha[1])) {
            case 1:
                mes = "ENERO";
                break;
            case 2:
                mes = "FEBRERO";
                break;
            case 3:
                mes = "MARZO";
                break;
            case 4:
                mes = "ABRIL";
                break;
            case 5:
                mes = "MAYO";
                break;
            case 6:
                mes = "JUNIO";
                break;
            case 7:
                mes = "JULIO";
                break;
            case 8:
                mes = "AGOSTO";
                break;
            case 9:
                mes = "SEPTIEMBRE";
                break;
            case 10:
                mes = "OCTUBRE";
                break;
            case 11:
                mes = "NOVIEMBRE";
                break;
            default:
                mes = "DICIEMBRE";
        }
        if (fecha.indexOf("/") > 0) {
            return (mes);
        }
        return (mes);
    }

    public static String IPv4AIPv6(String ipv4) {
        String binario = "";
        String ipv6 = "::FFFF";
        String vecIp[] = ipv4.replace(".", ":").split(":");
        for (int i = 0; i < vecIp.length; i++) {
            int dividendo = Integer.parseInt(vecIp[i]);
            String resultado = "";
            while (dividendo > 0) {
                resultado += (dividendo % 2);
                dividendo = dividendo / 2;
            }
            resultado = Addons.rellenarCeros(Addons.reverse(resultado), 8);
            binario += resultado.substring(0, 4) + ":" + resultado.substring(4, 8) + ":";
        }

        String vecBinario[] = binario.split(":");
        char hexadecimal[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E'};
        char sumandos[] = {'8', '4', '2', '1'};
        String hexteto = "";
        for (int i = 0; i < vecBinario.length; i++) {
            if (i == 4) {
                ipv6 += ":" + hexteto;
                hexteto = "";
            }
            char bits[] = vecBinario[i].toCharArray();
            int sumatoria = 0;
            for (int j = 0; j < bits.length; j++) {
                if (bits[j] == '1') {
                    sumatoria += Integer.parseInt(String.valueOf(sumandos[j]));
                }
            }
            hexteto += hexadecimal[sumatoria];
        }

        return ipv6 + ":" + hexteto;
    }

    public static String reverse(String palabra) {
        String salida = "";
        for (int i = palabra.length() - 1; i >= 0; i--) {
            salida += palabra.charAt(i);
        }
        return salida;
    }

    public static String redondearDecimales(double num) {
        double parteEntera, resultado;
        resultado = num;
        parteEntera = Math.floor(resultado);
        resultado = (resultado - parteEntera) * Math.pow(10, 2);
        resultado = Math.round(resultado);
        resultado = (resultado / Math.pow(10, 2)) + parteEntera;
        return String.valueOf(resultado);
    }
}
