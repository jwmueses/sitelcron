/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.web;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class Fecha {

    /**
     * Funci�n que construye una cadena con la fecha actual.
     *
     * @param tipo. El tipo de format de la fecha: por defecto ISO o SQL.
     * @return una cadena con la fecha actual.
     */
    public static String getFecha(String tipo) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH) + 1;
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        String axMes = mes < 10 ? "0" + mes : String.valueOf(mes);
        String axDia = dia < 10 ? "0" + dia : String.valueOf(dia);
        String cad = anio + "-" + axMes + "-" + axDia;
        if (tipo.toUpperCase().compareTo("SQL") == 0) {
            cad = axDia + "/" + axMes + "/" + anio;
        }
        return cad;
    }

    public static String add(String fecha, int campo, int num) {
        Calendar cal = Calendar.getInstance();
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha);
        int dia = Fecha.datePart("dia", fecha);
        cal.set(anio, mes, dia);
        cal.add(campo, num);

        anio = cal.get(Calendar.YEAR);
        mes = cal.get(Calendar.MONTH);
        dia = cal.get(Calendar.DAY_OF_MONTH);
        String axMes = mes < 10 ? "0" + mes : String.valueOf(mes);
        String axDia = dia < 10 ? "0" + dia : String.valueOf(dia);
        String cad = anio + "-" + axMes + "-" + axDia;
        if (fecha.toUpperCase().indexOf("/") >= 0) {
            cad = dia + "/" + axMes + "/" + axDia;
        }

        return cad;
    }

    public static String getTxtMes(int mes) {
        String txtMmes = "";
        switch (mes) {
            case 1:
                txtMmes = "enero";
                break;
            case 2:
                txtMmes = "febrero";
                break;
            case 3:
                txtMmes = "marzo";
                break;
            case 4:
                txtMmes = "abril";
                break;
            case 5:
                txtMmes = "mayo";
                break;
            case 6:
                txtMmes = "junio";
                break;
            case 7:
                txtMmes = "julio";
                break;
            case 8:
                txtMmes = "agosto";
                break;
            case 9:
                txtMmes = "septiembre";
                break;
            case 10:
                txtMmes = "octubre";
                break;
            case 11:
                txtMmes = "noviembre";
                break;
            case 12:
                txtMmes = "diciembre";
                break;
        }
        return txtMmes;
    }

    public static String getTxtMeses(String fechaIni, int num) {
        //int anio = Fecha.datePart("anio", fechaIni);
        int ini = Fecha.datePart("mes", fechaIni);
        String meses = "";
        for (int i = ini; i < ini + num; i++) {
            int mes = i;
            //int axAnio = anio;
            if (i > 12) {
                //axAnio = anio+1;
                mes = i - 12;
            }
            meses += Fecha.getTxtMes(mes) + ", ";
        }
        if (meses.compareTo("") != 0) {
            meses = meses.substring(0, meses.length() - 2);
        }
        return meses;
    }

    public static String getDiaSemana() {
        String cad = "";
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_WEEK);
        switch (dia) {
            case 1:
                cad = "domingo";
                break;
            case 2:
                cad = "lunes";
                break;
            case 3:
                cad = "martes";
                break;
            case 4:
                cad = "miercoles";
                break;
            case 5:
                cad = "jueves";
                break;
            case 6:
                cad = "viernes";
                break;
            case 7:
                cad = "sabado";
                break;
        }
        return cad;
    }

    /**
     * Para la informacion que dia de la semana.
     *
     * @param fecha Fecha solicitada.
     * @return Dia de la semana en minusculas.
     */
    public static String getDiaSemana(String fecha) {
        Calendar cal = Calendar.getInstance();
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha) - 1;
        int dia = Fecha.datePart("dia", fecha);
        cal.set(anio, mes, dia);
        int dia_semana = cal.get(Calendar.DAY_OF_WEEK);
        String cad = "";
        switch (dia_semana) {
            case 1:
                cad = "domingo";
                break;
            case 2:
                cad = "lunes";
                break;
            case 3:
                cad = "martes";
                break;
            case 4:
                cad = "miercoles";
                break;
            case 5:
                cad = "jueves";
                break;
            case 6:
                cad = "viernes";
                break;
            case 7:
                cad = "sabado";
                break;
        }
        return cad;
    }

    public static String getDiaSemana(int dia) {
        Calendar cal = Calendar.getInstance();
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes() - 1;
        cal.set(anio, mes, dia);
        int dia_semana = cal.get(Calendar.DAY_OF_WEEK);
        String cad = "";
        switch (dia_semana) {
            case 1:
                cad = "domingo";
                break;
            case 2:
                cad = "lunes";
                break;
            case 3:
                cad = "martes";
                break;
            case 4:
                cad = "miercoles";
                break;
            case 5:
                cad = "jueves";
                break;
            case 6:
                cad = "viernes";
                break;
            case 7:
                cad = "sabado";
                break;
        }
        return cad;
    }

    public static String get5DiasLaborables(String fecha_ini) {
        String fecha_fin = Fecha.getFecha("ISO");
        try {
            DataBase objDB = new DataBase();
            ResultSet rs = objDB.consulta("select get5DiasLaborables('" + fecha_ini + "')");
            if (rs.next()) {
                fecha_fin = rs.getString(1) != null ? rs.getString(1) : fecha_fin;
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fecha_fin;
    }

    public static String getFecha(String tipo, int parteFecha, int valor) {
        Calendar cal = Calendar.getInstance();
        cal.set(parteFecha, valor);
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH) + 1;
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        String axMes = mes < 10 ? "0" + mes : String.valueOf(mes);
        String axDia = dia < 10 ? "0" + dia : String.valueOf(dia);
        String cad = anio + "-" + axMes + "-" + axDia;
        if (tipo.toUpperCase().compareTo("SQL") == 0) {
            cad = axDia + "/" + axMes + "/" + anio;
        }
        return cad;
    }

    public static long getTimeStamp(String fecha) {
        Calendar cal = Calendar.getInstance();
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha);
        int dia = Fecha.datePart("dia", fecha);
        cal.set(anio, mes, dia);
        return cal.getTimeInMillis();
    }

    public static long getTimeStamp(String fecha, String hora) {
        Calendar cal = Calendar.getInstance();
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha);
        int dia = Fecha.datePart("dia", fecha);
        String vecHora[] = hora.split(":");
        cal.set(anio, mes, dia, Integer.parseInt(vecHora[0]), Integer.parseInt(vecHora[1]), Integer.parseInt(vecHora[2]));
        return cal.getTimeInMillis();
    }

    /**
     * Funci�n que construye una cadena con la hora actual.
     *
     * @param tipo. El tipo de format de la hora HH:mm:ss.
     * @return una cadena con la hora actual.
     */
    public static String getHora() {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);
        int segundo = cal.get(Calendar.SECOND);
        String cad = hora + ":" + minuto + ":" + segundo;
        return cad;
    }

    public static String getHoraMili() {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);
        int segundo = cal.get(Calendar.SECOND);
        int milisegundo = cal.get(Calendar.MILLISECOND);
        String cad = hora + ":" + minuto + ":" + segundo + "" + milisegundo;
        return cad;
    }

    /**
     * Construye un entero con el a�o actual.
     *
     * @return el a�o actual.
     */
    public static int getAnio() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * Construye un entero con el mes actual.
     *
     * @return el mes actual.
     */
    public static int getMes() {
        Calendar cal = Calendar.getInstance();
        return (cal.get(Calendar.MONTH) + 1);
    }

    /**
     * Construye un entero con el d�a actual.
     *
     * @return el d�a actual.
     */
    public static int getDia() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Construye un entero con el �ltimo d�a del mes.
     *
     * @param anio. El a�o para calcular el �ltimo d�a del mes.
     * @param mes. El mes para calcular �ltimo d�a del mes.
     * @return El �ltimo d�a del mes.
     */
    public static int getUltimoDiaMes(int anio, int mes) {
        int dia = 0;
        switch (mes) {
            case 1:  // Enero
            case 3:  // Marzo
            case 5:  // Mayo
            case 7:  // Julio
            case 8:  // Agosto
            case 10:  // Octubre
            case 12: // Diciembre
                dia = 31;
                break;
            case 4:  // Abril
            case 6:  // Junio
            case 9:  // Septiembre
            case 11: // Noviembre
                dia = 30;
                break;
            case 2:  // Febrero
                if (((anio % 100 == 0) && (anio % 400 == 0)) || ((anio % 100 != 0) && (anio % 4 == 0))) {
                    dia = 29;  // Año Bisiesto
                } else {
                    dia = 28;
                }
                break;
            default:
                dia = 0;
        }
        return dia;
    }

    /**
     * Transforma una fecha de formato ISO a una fecha en format SQL.
     *
     * @param anio. fecha en formato ISO.
     * @return fecha en formato SQL.
     */
    public static String ISOaSQL(String fecha) {
        String fechaSQL = fecha;
        if (fecha.length() <= 10) {
            if (fecha.indexOf("-") > 0) {
                String vecFecha[] = fecha.split("-");
                fechaSQL = vecFecha[2] + "/" + vecFecha[1] + "/" + vecFecha[0];
            }
        }
        return fechaSQL;
    }

    public static String SQLaISO(String fecha) {
        String fechaISO = fecha;
        if (fecha.indexOf("/") > 0) {
            String vecFecha[] = fecha.split("/");
            fechaISO = vecFecha[0] + "-" + vecFecha[1] + "-" + vecFecha[2];
        }
        return fechaISO;
    }

    /**
     * Trunca una fecha y hora, y devuelve solo la parte de la fecha.
     *
     * @param anio. fecha y hora.
     * @return fecha.
     */
    public static String truncFecha(String fecha) {
        String fechaSQL[] = fecha.split(" ");
        return fechaSQL[0];
    }

    /**
     * Verifica si el día actual es fin de mes.
     *
     * @return verdadero si es fin de mes caso contrario falso.
     */
    public static boolean esFinMes() {
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        String fin_mes = String.valueOf(anio) + "-" + String.valueOf(mes) + "-" + String.valueOf(Fecha.getUltimoDiaMes(anio, mes));
        String hoy = Fecha.getFecha("ISO");
        if (fin_mes.compareTo(hoy) == 0) {
            return true;
        }
        return false;
    }

    /**
     * Retorna parte de una fecha.
     *
     * @param parte. Parte de la fecha que se quiere retornar ('anio', 'mes',
     * 'dia').
     * @param fecha. La fecha de la cual se va a tomar la parte.
     * @return un número entero con la parte de la fecha ingresada como
     * parámetro.
     */
    public static int datePart(String parte, String fecha) {
        if (fecha.compareTo("") != 0) {
            String vec_fecha[] = null;
            int anio = 0;
            int dia = 0;
            if (fecha.indexOf("-") > 0) {
                vec_fecha = fecha.split("-");
                anio = Integer.parseInt(vec_fecha[0]);
                dia = Integer.parseInt(vec_fecha[2]);
            } else {
                vec_fecha = fecha.split("/");
                anio = Integer.parseInt(vec_fecha[2]);
                dia = Integer.parseInt(vec_fecha[0]);
            }
            if (parte.compareTo("anio") == 0) {
                return anio;
            } else if (parte.compareTo("mes") == 0) {
                return Integer.parseInt(vec_fecha[1]);
            } else if (parte.compareTo("dia") == 0) {
                return dia;
            } else {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Ingresado el año e el mes construye el texto del periodo utilizada en la
     * facturación periodica tarifada.
     *
     * @param año. Año que será parte dell período.
     * @param mes. Mes que será parte dell período.
     * @return retorna el período en formato texto.
     */
    public static String getTxtPeriodo(int anio, int mes) {
        String anio_mes = "";
        switch (mes) {
            case 1:
                anio_mes = anio + " / enero";
                break;
            case 2:
                anio_mes = anio + " / febrero";
                break;
            case 3:
                anio_mes = anio + " / marzo";
                break;
            case 4:
                anio_mes = anio + " / abril";
                break;
            case 5:
                anio_mes = anio + " / mayo";
                break;
            case 6:
                anio_mes = anio + " / junio";
                break;
            case 7:
                anio_mes = anio + " / julio";
                break;
            case 8:
                anio_mes = anio + " / agosto";
                break;
            case 9:
                anio_mes = anio + " / septiembre";
                break;
            case 10:
                anio_mes = anio + " / octubre";
                break;
            case 11:
                anio_mes = anio + " / noviembre";
                break;
            case 12:
                anio_mes = anio + " / diciembre";
                break;
        }
        return anio_mes;
    }

    /**
     * Ingresado el año e el mes construye el texto del periodo utilizada en la
     * facturación periodica tarifada.
     *
     * @param Fecha. Fecha que será parte del período.
     * @return retorna el período en formato texto.
     */
    public static String getTxtPeriodo(String fecha) {
        String anio_mes = "";
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha);
        switch (mes) {
            case 1:
                anio_mes = anio + " / enero";
                break;
            case 2:
                anio_mes = anio + " / febrero";
                break;
            case 3:
                anio_mes = anio + " / marzo";
                break;
            case 4:
                anio_mes = anio + " / abril";
                break;
            case 5:
                anio_mes = anio + " / mayo";
                break;
            case 6:
                anio_mes = anio + " / junio";
                break;
            case 7:
                anio_mes = anio + " / julio";
                break;
            case 8:
                anio_mes = anio + " / agosto";
                break;
            case 9:
                anio_mes = anio + " / septiembre";
                break;
            case 10:
                anio_mes = anio + " / octubre";
                break;
            case 11:
                anio_mes = anio + " / noviembre";
                break;
            case 12:
                anio_mes = anio + " / diciembre";
                break;
        }
        return anio_mes;
    }

    /**
     * Formatea la fecha ingresada en formato de texto utilizada en solicitudes.
     *
     * @param fecha. La fecha a formatear.
     * @return fecha en formato de texto.
     */
    public static String getFechaSolicitud(String fecha) {
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha);
        int dia = Fecha.datePart("dia", fecha);
        String anio_mes = "";
        switch (mes) {
            case 1:
                anio_mes = dia + " de enero de " + anio;
                break;
            case 2:
                anio_mes = dia + " de febrero de " + anio;
                break;
            case 3:
                anio_mes = dia + " de marzo de " + anio;
                break;
            case 4:
                anio_mes = dia + " de abril de " + anio;
                break;
            case 5:
                anio_mes = dia + " de mayo de " + anio;
                break;
            case 6:
                anio_mes = dia + " de junio de " + anio;
                break;
            case 7:
                anio_mes = dia + " de julio de " + anio;
                break;
            case 8:
                anio_mes = dia + " de agosto de " + anio;
                break;
            case 9:
                anio_mes = dia + " de septiembre de " + anio;
                break;
            case 10:
                anio_mes = dia + " de octubre de " + anio;
                break;
            case 11:
                anio_mes = dia + " de noviembre de " + anio;
                break;
            case 12:
                anio_mes = dia + " de diciembre de " + anio;
                break;
        }
        return anio_mes;
    }

    public static String reFormatearFecha(String fecha) {
        if (fecha.compareTo("") != 0) {
            if (fecha.indexOf("-") > 0) {
                String vec_fecha[] = fecha.split("-");
                int anio = Integer.parseInt(vec_fecha[0]);
                int mes = Integer.parseInt(vec_fecha[1]);
                String mes1 = String.valueOf(mes);
                if (mes < 10) {
                    mes1 = "0" + mes;
                }
                int dia = Integer.parseInt(vec_fecha[2]);
                String dia1 = vec_fecha[2];
                if (dia < 10) {
                    dia1 = "0" + dia;
                }
                return anio + "-" + mes1 + "-" + dia1;
            } else {
                String vec_fecha[] = fecha.split("/");
                int anio = Integer.parseInt(vec_fecha[2]);
                int mes = Integer.parseInt(vec_fecha[1]);
                String mes1 = vec_fecha[1];
                if (mes < 10) {
                    mes1 = "0" + mes1;
                }
                int dia = Integer.parseInt(vec_fecha[0]);
                String dia1 = vec_fecha[0];
                if (dia < 10) {
                    dia1 = "0" + dia1;
                }
                return dia1 + "/" + mes1 + "/" + anio;
            }
        }
        return fecha;
    }

    public static int getMes(int meses, String tipo) {
        int mes = -1;
        Calendar cal = Calendar.getInstance();
        if (tipo.trim().compareTo("-") == 0) {
            mes = (cal.get(Calendar.MONTH) - meses);
        } else if (tipo.trim().compareTo("+") == 0) {
            mes = (cal.get(Calendar.MONTH) + meses);
        } else {
            mes = (cal.get(Calendar.MONTH) + 1);
        }
        return mes;
    }

    public static Calendar SumarTiempo(Date tiempo, String horas, String minutos) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(tiempo);
            if (horas.trim().compareTo("") != 0) {
                calendar.add(calendar.HOUR, Integer.parseInt(horas));
            }
            if (minutos.trim().compareTo("") != 0) {
                calendar.add(calendar.MINUTE, Integer.parseInt(minutos));
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return calendar;
    }

    public static long getTime(String fecha, String hora) {
        Date tmp;
        Calendar cal = Calendar.getInstance();
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha);
        int dia = Fecha.datePart("dia", fecha);
        String vecHora[] = hora.split(":");
        cal.set(anio, mes, dia, Integer.parseInt(vecHora[0]), Integer.parseInt(vecHora[1]), Integer.parseInt(vecHora[2]));
        tmp = cal.getTime();
        return tmp.getTime();
    }

    public static Date SumarTiempos(Date tiempo, String horas, String minutos) {
        Date tmp;
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(tiempo);
            if (horas.trim().compareTo("") != 0) {
                calendar.add(calendar.HOUR, Integer.parseInt(horas));
            }
            if (minutos.trim().compareTo("") != 0) {
                calendar.add(calendar.MINUTE, Integer.parseInt(minutos));
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        tmp = calendar.getTime();
        return tmp;
    }

    public static boolean EsMenorFecha(String fecha, String fechaa) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha_nueva = null;
        Date fecha_actual = null;
        try {
            fecha_nueva = formato.parse(fecha);
            fecha_actual = formato.parse(fechaa);
            if (fecha_nueva.before(fecha_actual)) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return false;
    }
}
