/**
* @version 1.0
* @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* FACTURAPYMES! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package cron.trabajos;

import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import com.lowagie.text.*;

import java.util.*;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class Addons {
    static String datos[][] = null;
    
    public static Paragraph setParrafoPDF(String texto, int tipo, int tamanio, int estilo, int alineacion)
    {        
        Paragraph parrafo = new Paragraph(texto, new Font(tipo, tamanio, estilo));
        parrafo.setAlignment(alineacion);
        return parrafo;
    }
    public static PdfPCell setCeldaPDF(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo)));        
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.LIGHT_GRAY);
        celda.setBorderWidth(borde);
        return celda;
    }
    public static PdfPCell setCeldaPDF(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, int padding)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo)));        
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.LIGHT_GRAY);
        celda.setBorderWidth(borde);
        celda.setPadding(padding);
        return celda;
    }
    public static PdfPCell setCeldaPDFBottom(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, int padding, int colspan)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo,Color.DARK_GRAY)));   
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.black);
        celda.setColspan(colspan);
        celda.setBorder(borde);
        celda.setPadding(padding);
        return celda;
    }
    public static PdfPCell setCeldaPDFCarnetOpciones(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, int padding, int colspan,Color color)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo,Color.WHITE)));   
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.black);
        celda.setBackgroundColor(color);
        celda.setColspan(colspan);
        celda.setBorderWidth(borde);
        celda.setPadding(0);
        celda.setPaddingBottom(padding);
        return celda;
    }
    public static PdfPCell setCeldaPDFCarnet(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, int padding, int colspan)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo,Color.BLACK)));   
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.WHITE);
        celda.setColspan(colspan);
        celda.setBorderWidth(0);
        celda.setBorderWidthBottom(borde);
        celda.setPadding(0);
        celda.setPaddingBottom(padding);
        return celda;
    }
    public static PdfPCell setCeldaPDFCarnet(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, int padding, int colspan,Color color)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo,color)));   
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.WHITE);
        celda.setColspan(colspan);
        celda.setBorderWidth(0);
        celda.setBorderWidthBottom(borde);
        celda.setPadding(0);
        celda.setPaddingBottom(padding);
        return celda;
    }
    
    public static PdfPCell setCeldaPDF(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, Color color)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo)));        
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.LIGHT_GRAY);
        celda.setBorderWidth(borde);
        celda.setBackgroundColor(color);
        return celda;
    }
    public static PdfPCell setCeldaPDF(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, Color color, int padding, int colspan)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo)));        
        celda.setHorizontalAlignment(alineacion);
        celda.setPadding(padding);
        celda.setColspan(colspan);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.LIGHT_GRAY);
        celda.setBorderWidth(borde);
        celda.setBackgroundColor(color);
        return celda;
    }
    public static PdfPCell setCeldaPDF(String texto, int tipo, int tamanio, int estilo, int alineacion, int borde, int padding, int colspan)
    {        
        PdfPCell celda = new PdfPCell(new Paragraph(texto, new Font(tipo, tamanio, estilo)));        
        celda.setHorizontalAlignment(alineacion);
        celda.setPadding(padding);
        celda.setColspan(colspan);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.LIGHT_GRAY);
        celda.setBorderWidth(borde);
        return celda;
    }
    public static PdfPCell setCeldaPDF(PdfPTable tabla, int alineacion, int borde)
    {        
        PdfPCell celda = new PdfPCell(tabla);        
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorderColor(Color.LIGHT_GRAY);
        celda.setBorderWidth(borde);
        return celda;
    }
    public static PdfPCell setFilaBlanco(int colSpan, int alto)
    {
        PdfPCell celda = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, alto, Font.NORMAL)));
        celda.setBorderWidth(0);
        celda.setPadding(0);
        celda.setColspan(colSpan);
        return celda;
    }
    public static PdfPCell setBarra(int colSpan, float color)
    {
        PdfPCell celda = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 1, Font.NORMAL)));
        celda.setGrayFill(color);
        celda.setBorderWidth(0);
        celda.setPadding(0);
        celda.setColspan(colSpan);
        return celda;
    }
    public static PdfPTable setHistorial(ResultSet histo)
    {
        float bar [] = new float[103];
        bar[0] = 20f;
        bar[1] = 2f;
        for(int i=2; i<=101; i++){
            bar[i]=1f;
        }
        bar[102] = 20f;
        
        PdfPTable tabla = new PdfPTable(bar);
        tabla.addCell(Addons.setFilaBlanco(103, 1));
        
        String historial[][] = Matriz.ResultSetAMatriz(histo);
        float maximo = Matriz.valMaximo(historial, 1);  /* es el valor máximo de los consumos */
        
        int porcentaje = 100;
        if(maximo / 10000 >= 1){
            porcentaje = 100000;
        }
        if(maximo / 1000 >= 1){
            porcentaje = 10000;
        }
        if(maximo / 100 >= 1){
            porcentaje = 1000;
        }
        
        for(int i=0; i<historial.length; i++){
            tabla.addCell(Addons.setCeldaPDF(historial[i][0], Font.HELVETICA, 5, Font.NORMAL, Element.ALIGN_RIGHT, 0, 0, 1));
            tabla.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 5, Font.NORMAL, Element.ALIGN_RIGHT, 0, 0, 1));
            int ancho = Math.round(Float.parseFloat(historial[i][1]) * maximo / porcentaje);
            tabla.addCell(Addons.setBarra(ancho, 0f));   /* barra de consumo */
            if(ancho==0){
                ancho = 1;
            }
            tabla.addCell(Addons.setBarra(100-ancho, 10f));   /* barra complementaria blanca */
            tabla.addCell(Addons.setCeldaPDF(" "+historial[i][1], Font.HELVETICA, 3, Font.NORMAL, Element.ALIGN_LEFT, 0, 0, 1));
            tabla.addCell(Addons.setFilaBlanco(103, 2));
        }
        
        return tabla;
    }
    public static String truncar(double num)
    {
        /*String cad = String.valueOf(Math.scalb(num, 2));
        return cad;*/
        if(num>0){
            num = num + 0.0009f;
        }
        String cad2 = String.valueOf(num).replace(".", ":");
        String cad[] = cad2.split(":");
        String res = "";
        if(cad.length>1){
            cad[1] += "000";
            res = cad[1].substring(0,2);
        }
        return cad[0]+"."+res;
    }
    public static String truncar(double num, int decimales)
    {
        /*String cad = String.valueOf(Math.scalb(num, 2));
        return cad;*/
        if(num>0){
            num = num + 0.0009f;
        }
        String cad2 = String.valueOf(num).replace(".", ":");
        String cad[] = cad2.split(":");
        String res = "";
        if(cad.length>1){
            cad[1] += "0000000000000";
            res = cad[1].substring(0,decimales);
        }
        return cad[0]+"."+res;
    }
    public static String truncar(String num2)
    {
        double num = Double.valueOf(num2);
        if(num>0){
            num = num + 0.0009f;
        }
        String cad2 = String.valueOf(num).replace(".", ":");
        String cad[] = cad2.split(":");
        String res = "";
        if(cad.length>1){
            cad[1] += "000";
            res = cad[1].substring(0,2);
        }
        return cad[0]+"."+res;
    }

    public static PdfPCell setLogo(String logo, int ancho, int alto)
    {
        PdfPCell celdaImg = null;
        try{
            Image imagelogo = Image.getInstance(logo);
            imagelogo.scaleAbsolute(ancho, alto);
            celdaImg = new PdfPCell(imagelogo);
            celdaImg.setBorderWidth(0);
            celdaImg.setPadding(0);
        }catch(Exception e){
            celdaImg = null;
        }
        return celdaImg;
    }
    public static PdfPCell setLogo(String logo, int ancho, int alto, int alineacion)
    {
        PdfPCell celdaImg = null;
        try{
            Image imagelogo = Image.getInstance(logo);
            imagelogo.scaleAbsolute(ancho, alto);
            celdaImg = new PdfPCell(imagelogo);
            celdaImg.setBorderWidth(0);
            celdaImg.setHorizontalAlignment(alineacion);
            celdaImg.setPadding(0);
        }catch(Exception e){
            System.out.println("Error al insertar imagen");
            celdaImg = null;
        }
        return celdaImg;
    }
    public static PdfPCell setBarCode(Image logo)
    {
        PdfPCell celdaImg = null;
        try{
            Image imagelogo = Image.getInstance(logo);
            celdaImg = new PdfPCell(imagelogo);
            celdaImg.setBorderWidth(0);
            celdaImg.setPadding(5);
            celdaImg.setHorizontalAlignment(Element.ALIGN_CENTER);
        }catch(Exception e){
            celdaImg = null;
        }
        return celdaImg;
    }
    public static PdfPCell setLogoCarnet(String logo, int ancho, int alto)
    {
        PdfPCell celdaImg = null;
        try{
            Image imagelogo = Image.getInstance(logo);
            imagelogo.scaleAbsolute(ancho, alto);
            celdaImg = new PdfPCell(imagelogo);
            celdaImg.setBorderWidth(0);
            celdaImg.setPadding(0);
            celdaImg.setPaddingLeft(50);
        }catch(Exception e){
            celdaImg = null;
        }
        return celdaImg;
    }

    public static Image setMarcaAgua(String logo, int ancho, int alto)
    {
        try{
            Image imagelogo = Image.getInstance(logo);
            imagelogo.setTransparency( new int []{255 ,  255} );
            imagelogo.setRotationDegrees(57);
            imagelogo.scaleAbsolute(ancho, alto);
            imagelogo.setAbsolutePosition(0, 30);
            return imagelogo;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static PdfPTable setCabecera(String logo, String titulo, String ruc, String subtitulo, String direccion, String sucursal)
    {
        PdfPTable encabezado = new PdfPTable(new float[]{80f,400f});
        PdfPTable tbl_encab = new PdfPTable(1);
        Image imagelogo = null;
        try{ 
            imagelogo = Image.getInstance(logo);
            imagelogo.scaleAbsolute(150, 70);
            PdfPCell celdaImg = new PdfPCell(imagelogo);
            celdaImg.setBorderWidth(0);
            celdaImg.setPadding(0);
            encabezado.addCell(celdaImg);
        }catch(Exception e){
            encabezado.addCell("");
        }        
        tbl_encab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 13, Font.BOLD, Element.ALIGN_CENTER, 0));
        tbl_encab.addCell(Addons.setCeldaPDF(direccion, Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_CENTER, 0));
        if(ruc.compareTo("")!=0){
            tbl_encab.addCell(Addons.setCeldaPDF("RUC: "+ruc, Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_CENTER, 0));
        }
        tbl_encab.addCell(Addons.setCeldaPDF(subtitulo, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
        
        
        encabezado.addCell(Addons.setCeldaPDF(tbl_encab, Element.ALIGN_CENTER, 0));
        
        encabezado.addCell(Addons.setFilaBlanco(2, 6));
        
        if(sucursal.compareTo("")!=0){
            encabezado.addCell(Addons.setCeldaPDF("Sucursal: "+sucursal, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 0, 2));
        }

        encabezado.addCell(Addons.setCeldaPDF("Fecha de impresión: "+Fecha.getFecha("SQL"), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 0, 2));

        encabezado.addCell(Addons.setFilaBlanco(2, 4));
        
        return encabezado;
    }
    public static void setEncabezado(PdfWriter writer, Document document, String texto)
    {
        if(writer.getPageNumber() > 1){
            try{
                PdfPTable encabezado = new PdfPTable(1);
                encabezado.setTotalWidth(document.right() - document.left()-120);
                encabezado.addCell(Addons.setCeldaPDF(texto, Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 0));
                encabezado.writeSelectedRows(0, -1, 60, document.top()+25, writer.getDirectContent());
                
                PdfContentByte cb = writer.getDirectContent();
                cb.setLineWidth(2);
                cb.moveTo(60, document.top()+10);
                cb.lineTo(document.right() - document.left()-58, document.top()+10);
            }catch(Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    public static void setPie(PdfWriter writer, Document document, String rep_pie)
    {
        try{
            PdfContentByte cb = writer.getDirectContent();
            /*cb.setLineWidth(2);
            cb.moveTo(60, document.bottomMargin()-5);
            cb.lineTo(document.right() - document.left()-70, document.bottomMargin()-5);
            */
            PdfPTable pie = new PdfPTable(1);
            pie.setTotalWidth(document.right() - document.left()-120);
            pie.addCell(Addons.setCeldaPDF(rep_pie, Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 0));
            pie.addCell(Addons.setCeldaPDF("Pág " + String.valueOf(writer.getPageNumber()), Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
            pie.addCell(Addons.setCeldaPDF("Reporte diseñado por: Jorge Mueses Cevallos.      Móvil: 095204832     mail:jorge_mueses@yahoo.com", Font.HELVETICA, 5, Font.BOLD, Element.ALIGN_LEFT, 0));
            pie.writeSelectedRows(0, -1, 60, document.bottomMargin()-10, cb);
        }catch(Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    public static PdfPTable setCabeceraTabla(String [] titulo, float [] ancho)
    {
        PdfPTable encabezado = new PdfPTable(ancho);
        encabezado.setSpacingBefore(5f);
        for(int i=0; i<titulo.length; i++){
            encabezado.addCell(Addons.setCeldaPDF(titulo[i], Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));  /*  Color.CYAN  */
        }
        return encabezado;
    }
    public static PdfPTable setCabeceraTablaBalance(String [] titulo, float [] ancho)
    {
        PdfPTable encabezado = new PdfPTable(ancho);
        encabezado.setSpacingBefore(5f);
        encabezado.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan, 3, 3));
        encabezado.addCell(Addons.setCeldaPDF("SUMAS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan, 3, 2));
        encabezado.addCell(Addons.setCeldaPDF("SALDOS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan, 3, 2));
        for(int i=0; i<titulo.length; i++){
            encabezado.addCell(Addons.setCeldaPDF(titulo[i], Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));  /*  Color.CYAN  */
        }
        return encabezado;
    }
    public static PdfPTable setCabeceraTabla(java.util.List titulo, float [] anchos, float ancho)
    {
        PdfPTable encabezado = new PdfPTable(anchos);
        encabezado.setWidthPercentage(ancho);
        encabezado.setSpacingBefore(5f);
        java.util.Iterator it = titulo.iterator();
        while(it.hasNext()){
            encabezado.addCell(Addons.setCeldaPDF((String)it.next(), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));  /*  Color.CYAN  */
        }
        return encabezado;
    }
    
    public static String getTextFecha(String fecha)
    {
        String vec_fecha[] = fecha.indexOf("/")>0 ? fecha.split("/") : fecha.split("-");
        String mes = "diciembre";
        switch(Integer.parseInt(vec_fecha[1])){
                case 1: mes = "enero";	break;
                case 2: mes = "febrero";	break;
                case 3: mes = "marzo";	break;
                case 4: mes = "abril";	break;
                case 5: mes = "mayo";	break;
                case 6: mes = "junio";	break;
                case 7: mes = "julio";	break;
                case 8: mes = "agosto";	break;
                case 9: mes = "septiembre";	break;
                case 10: mes = "octubre";	break;
                case 11: mes = "noviembre";	break;
                default: mes = "diciembre"; 
        }
        if(fecha.indexOf("/")>0){
            return (vec_fecha[0] + " de " + mes + " de " + vec_fecha[2]);
        }
        return (vec_fecha[2] + " de " + mes + " de " + vec_fecha[0]);
    }
    public static String fechaAl(String fecha)
    {
        String anio = "2010";
        String mes = "01";
        String dia = "";
        if(fecha.indexOf("/")>0){
            String vec[] = fecha.split("/");
            anio = vec[2];
            mes = vec[1];
            dia = vec[0];
        }else{
            String vec[] = fecha.split("-");
            anio = vec[0];
            mes = vec[1];
            dia = vec[2];
        }
        String f = "";
        switch(Integer.parseInt(mes)){
            case 1: f = dia + " de enero de " + anio;    break;
            case 2: f = dia + " de febrero de " + anio;    break;
            case 3: f = dia + " de marzo de " + anio;    break;
            case 4: f = dia + " de abril de " + anio;    break;
            case 5: f = dia + " de mayo de " + anio;    break;
            case 6: f = dia + " de junio de " + anio;    break;
            case 7: f = dia + " de julio de " + anio;    break;
            case 8: f = dia + " de agosto de " + anio;    break;
            case 9: f = dia + " de septiembre de " + anio;    break;
            case 10: f = dia + " de octubre de " + anio;    break;
            case 11: f = dia + " de noviembre de " + anio;    break;
            case 12: f = dia + " de diciembre de " + anio;    break;
        }
        return f;
    }
    public static String fechaRolIndividual(String fecha)
    {
        Calendar calendario=Calendar.getInstance();
        String anio = "2010";
        String mes = "01";
        String dia = "1";
        if(fecha.indexOf("/")>0){
            String vec[] = fecha.split("/");
            anio = vec[2];
            mes = vec[1];
        }else{
            String vec[] = fecha.split("-");
            anio = vec[0];
            mes = vec[1];
        }
        calendario.set(Integer.parseInt(anio), Integer.parseInt(mes)-1, 1);
        String finalMes=calendario.getActualMaximum(Calendar.DAY_OF_MONTH)+"";
        String f = "";
        switch(Integer.parseInt(mes)){
            case 1: f = dia + " al "+finalMes+" de enero del " + anio;    break;
            case 2: f = dia + " al "+finalMes+" de febrero del " + anio;    break;
            case 3: f = dia + " al "+finalMes+" de marzo del " + anio;    break;
            case 4: f = dia + " al "+finalMes+" de abril del " + anio;    break;
            case 5: f = dia + " al "+finalMes+" de mayo del " + anio;    break;
            case 6: f = dia + " al "+finalMes+" de junio del " + anio;    break;
            case 7: f = dia + " al "+finalMes+" de julio del " + anio;    break;
            case 8: f = dia + " al "+finalMes+" de agosto del " + anio;    break;
            case 9: f = dia + " al "+finalMes+" de septiembre del " + anio;    break;
            case 10: f = dia + " al "+finalMes+" de octubre del " + anio;    break;
            case 11: f = dia + " al "+finalMes+" de noviembre del " + anio;    break;
            case 12: f = dia + " al "+finalMes+" de diciembre del " + anio;    break;
        }
        return f;
    }
    public static int enVector(int[] vector, int clave)
    {
        int Iarriba = vector.length - 1; 
        int Iabajo = 0;
        int Icentro = 0; 
        while(Iabajo <= Iarriba){ 
            Icentro = (Iarriba + Iabajo) / 2;
            if(vector[Icentro] == clave){
                return Icentro; 
            }else if(clave < vector[Icentro]){
                    Iarriba = Icentro - 1; 
                 }else {
                    Iabajo = Icentro + 1;      
                 }
        }
        return -1;        
    }
    public static double redondear(double valor)
    {
        return (Math.round(valor * Math.pow(10, 2)) / Math.pow(10, 2));
    }
    public static double redondear(double valor, int decimales)
    {
        return (Math.round(valor * Math.pow(10, decimales)) / Math.pow(10, decimales));
    }
    public static String redondear(String valor)
    {
        double res = (Math.round(Double.valueOf(valor) * Math.pow(10, 2)) / Math.pow(10, 2));
        return String.valueOf(res);
    }
    
    
    
    /*  FUNCIONES PARA RECURSION */
    
    
    
    public static String[][] calcularTotalesBalance(ResultSet registros, int raiz, int nivel)
    {
        Addons.datos = Matriz.ResultSetAMatriz(registros);
        Addons.calcularTotales(raiz, nivel);
        return Addons.datos;
    }
    
    public static String calcularTotales(int raiz, int nivel)
    {    
        String subtotal = "0";
        int l=Addons.datos[0].length - 1;
        if(Addons.esHoja(Addons.datos, Addons.datos[raiz][0], 1) == -1){
            for(int i=0; i<Addons.datos.length; i++){
                if(Addons.datos[i][1].compareTo(Addons.datos[raiz][1])==0 && Addons.datos[i][l].compareTo("f")==0){
                    subtotal = String.valueOf(Double.valueOf(subtotal) + Double.valueOf(Addons.datos[i][4]));
                    Addons.datos[i][l-1] = String.valueOf(nivel);
                    Addons.datos[i][l] = "t";
                }
            }
            return subtotal;	
        }
        Addons.datos[raiz][l-1] = String.valueOf(nivel);
        Addons.datos[raiz][l] = "t";
        java.util.List newRaices = Addons.raices(Addons.datos[raiz][0], Addons.datos);
        nivel++;
        Iterator it = newRaices.iterator();
        String aux = "";
        while(it.hasNext()){
            aux = it.next().toString();
            Addons.datos[raiz][4] = String.valueOf(Double.valueOf(Addons.datos[raiz][4]) + Double.valueOf(Addons.calcularTotales(Integer.parseInt(aux), nivel)));
        }
        return Addons.datos[raiz][4];
    }
    
    public static String[][] setValor(String[][] datos, String id_cuenta, double valor)
    {
        int pos = Matriz.enMatriz(datos, id_cuenta, 0);
        datos[pos][4] = String.valueOf(Double.parseDouble(datos[pos][4]) + valor );
        while(datos[pos][1].compareTo("0")!=0){
            pos = Matriz.enMatriz(datos, datos[pos][1], 0);
            datos[pos][4] = String.valueOf(Double.parseDouble(datos[pos][4]) + valor );
        }
        return datos;
    }
    public static String[][] setCodigoCuentaUtilidad(String[][] datos, String id_cuenta)
    {
        int pos = Matriz.enMatriz(datos, id_cuenta, 0) - 1;
        int limite = datos[pos][2].lastIndexOf(".") + 1;
        String ax1 = datos[pos][2].substring(0, limite);
        int ax2 = Integer.parseInt( datos[pos][2].substring(limite, datos[pos][2].length()) ) + 1;
        datos[pos+1][2] = ax1 + ax2;

        return datos;
    }
    
    
    
    public static int esHoja(String[][] matriz, String clave, int pos)
    {
        int Iarriba = matriz.length - 1; 
        int Iabajo = 0;
        int Icentro = 0; 
        while(Iabajo <= Iarriba){ 
            Icentro = (Iarriba + Iabajo) / 2;
            if(Integer.parseInt(matriz[Icentro][pos]) == Integer.parseInt(clave)){
                return Icentro; 
            }
            else if(Integer.parseInt(clave) < Integer.parseInt(matriz[Icentro][pos])){
                    Iarriba = Icentro - 1; 
                 }else {
                    Iabajo = Icentro + 1;      
                 }
        }
        return -1;
    } 
    
    public static java.util.List raices(String clave, String[][] a) 
    { 
        java.util.List li = new ArrayList();
        for(int i=0; i<a.length; i++){ 
            if(a[i][1].compareTo(clave)==0){  
                li.add(i);
            }
        }
        return li;
    } 
    

    
    public static int maxNivelCodigos(String[][] codigos) 
    { 
        int m=0;
        int l = codigos[0].length;
        for(int i=0; i<codigos.length; i++){
            if(Integer.parseInt(codigos[i][l-2]) > m){
                m = Integer.parseInt(codigos[i][l-2]);
            }
        }
        return m;
    }
    
    public static void ordenamientoQuicksort(String[][] matriz, int inf, int sup, int pos) 
    { 
        // Verificamos que no se crucen los límites
        if(inf>=sup){
            return;
        }
        // inicialización de variables
        String elem_div = matriz[sup][pos];
        int i = inf - 1;
        int j = sup;
        boolean bandera = true;
        String temp[] = new String[matriz[0].length];   
        //  Clasificamos la sublista
        while(bandera){
            while(matriz[++i][pos].compareTo(elem_div)<0);
            while(matriz[--j][pos].compareTo(elem_div)>0 && j>0);
            if(i < j){
                for(int k=0; k<temp.length; k++){
                    temp[k] = matriz[i][k];
                }
                for(int k=0; k<temp.length; k++){
                    matriz[i][k] = matriz[j][k];
                }
                for(int k=0; k<temp.length; k++){
                    matriz[j][k] = temp[k];
                }
            }else{
                bandera = false;   
            }
        }
        // Copiamos el elemento de división en su posición final
        for(int k=0; k<temp.length; k++){
            temp[k] = matriz[i][k];
        }
        for(int k=0; k<temp.length; k++){
            matriz[i][k] = matriz[sup][k];
        }
        for(int k=0; k<temp.length; k++){
            matriz[sup][k] = temp[k];
        }
        // Aplicamos el procedimiento recursivamente a cada sublista
        ordenamientoQuicksort(matriz, inf, i - 1, pos);
        ordenamientoQuicksort(matriz, i + 1, sup, pos);    
    }
    
    public static String getValorCampoSRI(String[][] matriz, String clave)
    {
        double valor = 0.00;
        for(int i=0; i<matriz.length; i++){
            if(matriz[i][5].compareTo(clave)==0){
                valor += Double.valueOf(matriz[i][4]);
            }
        }
        String res = String.valueOf(Addons.redondear(valor));
        return Addons.truncar(res);
    } 
    
    public static String toFechaSQL(String f) 
    {
        if(f.indexOf("-")>=0){
            String vec_f[] = f.split("-");
            return (vec_f[2] + "/" + vec_f[1] + "/" + vec_f[0]);
        }else if(f.indexOf("/")>=0){
                return f;
            } 
        return "00/00/0000";
    }
    
    public static String getMesSRI(int mes) 
    {
        String res = "";
        switch(mes){
            case 1: res = "ENE";    break;
            case 2: res = "FEB";    break;
            case 3: res = "MAR";    break;
            case 4: res = "ABR";    break;
            case 5: res = "MAY";    break;
            case 6: res = "JUN";    break;
            case 7: res = "JUL";    break;
            case 8: res = "AGO";    break;
            case 9: res = "SEP";    break;
            case 10: res = "OCT";    break;
            case 11: res = "NOV";    break;
            case 12: res = "DIC";    break;
        }
        return res;
    }
    
    public static String getMes(int mes) 
    {
        String res = "";
        switch(mes){
            case 1: res = "ENERO";    break;
            case 2: res = "FEBRERO";    break;
            case 3: res = "MARZO";    break;
            case 4: res = "ABRIL";    break;
            case 5: res = "MAYO";    break;
            case 6: res = "JUNIO";    break;
            case 7: res = "JULIO";    break;
            case 8: res = "AGOSTO";    break;
            case 9: res = "SEPTIEMBRE";    break;
            case 10: res = "OCTUBRE";    break;
            case 11: res = "NOVIEMBRE";    break;
            case 12: res = "DICIEMBRE";    break;
        }
        return res;
    }
    
    public static String rellenarCeros(float valor, int longitud)
    {
        String res = "";
        int nums_ocupados = String.valueOf(valor).length();
        for(int i=0; i<longitud-nums_ocupados; i++){
            res += "0";
        }
        return res + valor;
    }
      
    public static String rellenarCeros(String valor, int longitud)
    {
        String res = "";
        int nums_ocupados = valor.length();
        for(int i=0; i<longitud-nums_ocupados; i++){
            res += "0";
        }
        return res + valor;
    }
    
    public static float StringToFloat(String valor)
    {
        long entero = Long.parseLong( valor.substring(0, valor.length()-2) );
        String decimal = valor.substring(valor.length()-2, valor.length());
        String flotante = entero + "." + decimal;
        return Float.parseFloat(flotante);
    }
        
    public static String rellenarCeros(int valor, int longitud)
    {
        String res = "";
        int nums_ocupados = String.valueOf(valor).length();
        for(int i=0; i<longitud-nums_ocupados; i++){
            res += "0";
        }
        return res + valor;
    }
    public static String getMesFecha(String fecha)
    {
        String vec_fecha[] = fecha.indexOf("/")>0 ? fecha.split("/") : fecha.split("-");
        String mes = "DICIEMBRE";
        switch(Integer.parseInt(vec_fecha[1])){
                case 1: mes = "ENERO";	break;
                case 2: mes = "FEBRERO";	break;
                case 3: mes = "MARZO";	break;
                case 4: mes = "ABRIL";	break;
                case 5: mes = "MAYO";	break;
                case 6: mes = "JUNIO";	break;
                case 7: mes = "JULIO";	break;
                case 8: mes = "AGOSTO";	break;
                case 9: mes = "SEPTIEMBRE";	break;
                case 10: mes = "OCTUBRE";	break;
                case 11: mes = "NOVIEMBRE";	break;
                default: mes = "DICIEMBRE"; 
        }
        if(fecha.indexOf("/")>0){
            return (mes);
        }
        return (mes);
    }
}