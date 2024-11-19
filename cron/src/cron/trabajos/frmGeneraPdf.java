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
import java.io.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import java.sql.ResultSet;


/**
 *
 * @author Jorge
 */

public class frmGeneraPdf  {
        
    public void GenerarFactura(Archivo objDataBase, String ruta_xml, String dir, String _archivoNombre) throws FileNotFoundException {        
        File _archivo = null;
        Xml xml = new Xml(ruta_xml+".xml");
        
        
        String numeroAutorizacion =xml.getValor("numeroAutorizacion");
        String fechaAutorizacion =xml.getValor("fechaAutorizacion");
        
        String doc=xml.getValor("comprobante");
         try{
             
            _archivo = new File(dir, _archivoNombre+"xml.xml");
            if(!_archivo.exists()){
                 //byte[] bytes = (res.getString(campoBytea)!=null) ? res.getBytes(campoBytea) : null;
                 RandomAccessFile archivo = new RandomAccessFile(dir + _archivoNombre+"xml.xml","rw");
                 archivo.writeBytes(doc);
                 archivo.close();
             }
         }catch(Exception e){
             System.out.println("Error al re-guardar archivo XML" + e.getMessage() + "\n");
             e.printStackTrace();
         }
         
         //infoTributaria
         Xml xml2 = new Xml(ruta_xml+"xml.xml");
         String ambiente =xml2.getValor("ambiente");
         String tipoEmision =xml2.getValor("tipoEmision");
         String razonSocial =xml2.getValor("razonSocial");
         String nombreComercial =xml2.getValor("nombreComercial");
         String ruc =xml2.getValor("ruc");
         String claveAcceso =xml2.getValor("claveAcceso");
         String codDoc =xml2.getValor("codDoc");
         String numFac =xml2.getValor("estab")+"-"+xml2.getValor("ptoEmi")+"-"+xml2.getValor("secuencial");
         String dirMatriz =xml2.getValor("dirMatriz");
         
         String EMAIL="";
         String DIRECCION="";
         String ARCOTEL="";
         
         
         int limiteAdi=xml2.getNumNodos("campoAdicional"); 
         try{
         //Campos Adicionales
            for(int i=0; i<limiteAdi;i++){
                if(xml2.getAtributo("campoAdicional",i,"nombre").toUpperCase().compareTo("EMAIL")==0){
                    EMAIL=xml2.getValor("campoAdicional",i);
                }
                if(xml2.getAtributo("campoAdicional",i,"nombre").toUpperCase().compareTo("DIRECCION")==0){
                    DIRECCION=xml2.getValor("campoAdicional",i);
                }
                if(xml2.getAtributo("campoAdicional",i,"nombre").toUpperCase().compareTo("ARCOTEL")==0){
                    ARCOTEL=xml2.getValor("campoAdicional",i);
                }
            }
         }catch(Exception e){
             System.out.println("Error al generar campos adicionales " + e.getMessage() + "\n");
             e.printStackTrace();
         }
            
         
         if(ambiente.compareTo("1")==0){
                ambiente="Pruebas";
            }
            if(ambiente.compareTo("2")==0){
                ambiente="Producción";
            }
            if(tipoEmision.compareTo("1")==0){
                tipoEmision="Emisión Normal";
            }
            if(tipoEmision.compareTo("2")==0){
                tipoEmision="Emisión por Indisponibilidad del Sistema";
            }

         if(codDoc.compareTo("01")==0){
             //infoFactura
            String fechaEmision =xml2.getValor("fechaEmision");
            String dirEstablecimiento =xml2.getValor("dirEstablecimiento");
            String contribuyenteEspecial =xml2.getValor("contribuyenteEspecial");
            String obligadoContabilidad =xml2.getValor("obligadoContabilidad");
            String razonSocialComprador =xml2.getValor("razonSocialComprador");
            String identificacionComprador =xml2.getValor("identificacionComprador");
            String totalSinImpuestos =xml2.getValor("totalSinImpuestos");
            String totalDescuento =xml2.getValor("totalDescuento");
            String codigoPorcentaje =xml2.getValor("codigoPorcentaje");//0% 0 >>12% 2>>mo objeto de impuesto 6>>Excento al iva 7
            String baseImponible =xml2.getValor("baseImponible"); //Depende del codigo porcentaje
            String valor =xml2.getValor("valor");// va en el Iva 12%
            String importeTotal =xml2.getValor("importeTotal");//Sumatoria de todo
            String tipoPago = "-1";//Tipo de pago
            String valorPago =xml2.getValor("total");//Sumatoria de todo
            
            try {
                tipoPago =xml2.getValor("formaPago");//Tipo de pago
                ResultSet rsTipoPago = objDataBase.consulta("select descripcion from tbl_forma_pago where codigo='"+tipoPago+"' order by id_forma_pago limit 1");
                if(rsTipoPago.next()){
                   tipoPago= (rsTipoPago.getString("descripcion")!=null) ? rsTipoPago.getString("descripcion") : "";
                }
            } catch (Exception e) { 
                System.out.println("Error al obtener descripcion de la forma de pago " + e.getMessage() + "\n");
                System.out.println(e.getMessage() + "\n");
            }

            //detalles
            int limite=xml2.getNumNodos("detalle"); 
            String [][] array = new String[limite][7];
            for(int i=0; i<limite;i++)
            {
                array[i][0]=xml2.getValor("codigoPrincipal",i);
                array[i][1]=xml2.getValor("cantidad",i);
                array[i][2]=xml2.getValor("tarifa",i);
                array[i][3]=xml2.getValor("descripcion",i);
                array[i][4]=xml2.getValor("precioUnitario",i);
                array[i][5]=xml2.getValor("descuento",i);
                array[i][6]=xml2.getValor("precioTotalSinImpuesto",i);
            }

           /* inicio PDF */
            Document pdf = new Document(PageSize.A4);// paso 1
            pdf.setMargins(-35,-45,40,10); /*Izquierda, derecha, tope, pie */

            try{
                PdfWriter writer = PdfWriter.getInstance(pdf,new FileOutputStream(dir+claveAcceso+".pdf"));
                pdf.open();

                //pdf.open(); // paso 3
                // Para enviar a la impresora automÃ¡ticamente.
                //writer.addJavaScript("this.print(false);", false);

                /* todo el cuerpo del doc es el paso 4 */
                //PdfPTable tbl_titulo = new PdfPTable(1);
                PdfPTable tbl_titulo = new PdfPTable(new float[]{49f,2f,49f});
                PdfPTable tbl_1 = new PdfPTable(new float[]{37f,63f});
                PdfPTable tbl_2 = new PdfPTable(1);
                //titulos izquierda
                tbl_1.addCell(Addons.setLogo(dir+"logo.jpg", 250,83,Element.ALIGN_CENTER));//ancho,alto
                tbl_1.addCell(Addons.setCeldaPDF(" ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
                tbl_1.addCell(Addons.setCeldaPDF(razonSocial+" '"+nombreComercial+"' ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,2));
                tbl_1.addCell(Addons.setCeldaPDF("Dirección Matriz: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
                tbl_1.addCell(Addons.setCeldaPDF(dirMatriz, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
                tbl_1.addCell(Addons.setCeldaPDF("Dirección Sucursal: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
                tbl_1.addCell(Addons.setCeldaPDF(dirEstablecimiento, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
                if(contribuyenteEspecial.compareTo("")!=0){
                    tbl_1.addCell(Addons.setCeldaPDF("Contribuyente Especial Nro. "+contribuyenteEspecial, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
                }
                tbl_1.addCell(Addons.setCeldaPDF("OBLIGADO A LLEVAR CONTABILIDAD: "+obligadoContabilidad, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
                //Titulos derecha
                tbl_2.addCell(Addons.setCeldaPDF("R.U.C: "+ruc, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF("F A C T U R A ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF("No. "+numFac, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF("NÚMERO DE AUTORIZACIÓN", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF(numeroAutorizacion, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF("FECHA Y HORA DE AUTORIZACION", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF(fechaAutorizacion, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF("AMBIENTE: "+ambiente, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF("EMISIÓN:  "+tipoEmision, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setCeldaPDF("CLAVE DE ACCESO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
                tbl_2.addCell(Addons.setBarCode(this.getBarcode(writer, claveAcceso)));//ancho,alto

                tbl_titulo.addCell(Addons.setCeldaPDF(tbl_1, 0, 1));
                tbl_titulo.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 1, Font.NORMAL, 0,0));
                tbl_titulo.addCell(Addons.setCeldaPDF(tbl_2, 0, 1));
                tbl_titulo.addCell(Addons.setFilaBlanco(3, 20));
                pdf.add(tbl_titulo);

                //Informacion Cliente
                PdfPTable tbl_info = new PdfPTable(1);
                PdfPTable tbl_info1 = new PdfPTable(new float[]{32f,43f,25f});
                tbl_info1.addCell(Addons.setCeldaPDF("Razón Social / Nombres y Apellidos: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8));
                tbl_info1.addCell(Addons.setCeldaPDF(razonSocialComprador, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8));
                tbl_info1.addCell(Addons.setCeldaPDF("RUC/CI: "+identificacionComprador, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8));
                tbl_info1.addCell(Addons.setCeldaPDF("Fecha de emisión:    "+fechaEmision, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,1));
                tbl_info1.addCell(Addons.setCeldaPDF("DIRECCION: "+DIRECCION, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,2));
                tbl_info.addCell(Addons.setCeldaPDF(tbl_info1, 0, 1));
                tbl_info.addCell(Addons.setFilaBlanco(3, 20));
                pdf.add(tbl_info);

                //Detalles
                PdfPTable tbl_det = new PdfPTable(1);
                PdfPTable tbl_det1 = new PdfPTable(new float[]{10f,10f,10f,50f,10f,10f,10f});

                tbl_det1.addCell(Addons.setCeldaPDF("Cod. Principal", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
                tbl_det1.addCell(Addons.setCeldaPDF("Cant.", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
                tbl_det1.addCell(Addons.setCeldaPDF("Descripción", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
                tbl_det1.addCell(Addons.setCeldaPDF("Detalle Adicinal", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
                tbl_det1.addCell(Addons.setCeldaPDF("Precio Unitario", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
                tbl_det1.addCell(Addons.setCeldaPDF("Desc.", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
                tbl_det1.addCell(Addons.setCeldaPDF("Total", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
                for(int i=0; i<limite;i++)
                {
                    tbl_det1.addCell(Addons.setCeldaPDF(array[i][0], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_det1.addCell(Addons.setCeldaPDF(array[i][1], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_det1.addCell(Addons.setCeldaPDF(array[i][2], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_det1.addCell(Addons.setCeldaPDF(array[i][3], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                    tbl_det1.addCell(Addons.setCeldaPDF(array[i][4], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                    tbl_det1.addCell(Addons.setCeldaPDF(array[i][5], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                    tbl_det1.addCell(Addons.setCeldaPDF(array[i][6], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                }
                tbl_det.addCell(Addons.setCeldaPDF(tbl_det1, 0, 1));
                tbl_det.addCell(Addons.setFilaBlanco(6, 20));
                pdf.add(tbl_det);

                //Informacion Adicional
                PdfPTable tbl_info_ad = new PdfPTable(new float[]{40f,10f,50f});
                PdfPTable tbl_info_ad1 = new PdfPTable(new float[]{30f,70f});
                PdfPTable tbl_info_ad2 = new PdfPTable(new float[]{70f,30f});
                //titulos izquierda
                tbl_info_ad1.addCell(Addons.setCeldaPDF("Información Adicional", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,5,2));
                tbl_info_ad1.addCell(Addons.setCeldaPDF("Contacto: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                tbl_info_ad1.addCell(Addons.setCeldaPDF("062609177 / 062610330", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                tbl_info_ad1.addCell(Addons.setCeldaPDF("Email: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                tbl_info_ad1.addCell(Addons.setCeldaPDF(EMAIL, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                if(tipoPago.compareTo("")!=0){
                    tbl_info_ad1.addCell(Addons.setCeldaPDF("Tipo Pago: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                    tbl_info_ad1.addCell(Addons.setCeldaPDF(tipoPago, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                    tbl_info_ad1.addCell(Addons.setCeldaPDF("Valor Pago: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                    tbl_info_ad1.addCell(Addons.setCeldaPDF(valorPago, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                }
                tbl_info_ad1.addCell(Addons.setCeldaPDF("Sitio Web:", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                tbl_info_ad1.addCell(Addons.setCeldaPDF("www.saitel.ec", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
                tbl_info_ad1.addCell(Addons.setCeldaPDF(ARCOTEL, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,2));

                //Titulos derecha
                if(codigoPorcentaje.compareTo("0")==0){
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 12% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 0% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(baseImponible, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL No objeto de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL SIN IMPUESTOS", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(totalSinImpuestos, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL Exento de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(valor, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("DESCUENTO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(totalDescuento, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("IVA 12%", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));
                }

                if(codigoPorcentaje.compareTo("2")==0){
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 12% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(baseImponible, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 0% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL No objeto de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL SIN IMPUESTOS", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(totalSinImpuestos, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL Exento de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("DESCUENTO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(totalDescuento, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("IVA 12%", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(valor, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                }

                if(codigoPorcentaje.compareTo("3")==0){
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 14% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(baseImponible, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 0% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL No objeto de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL SIN IMPUESTOS", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(totalSinImpuestos, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL Exento de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("DESCUENTO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(totalDescuento, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                    tbl_info_ad2.addCell(Addons.setCeldaPDF("IVA 14%", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                    tbl_info_ad2.addCell(Addons.setCeldaPDF(valor, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                }

                tbl_info_ad2.addCell(Addons.setCeldaPDF("VALOR TOTAL", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                tbl_info_ad2.addCell(Addons.setCeldaPDF(Addons.redondear(importeTotal), Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                tbl_info_ad.addCell(Addons.setCeldaPDF(tbl_info_ad1, 0, 1));
                tbl_info_ad.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 1, Font.NORMAL, 0,0));
                tbl_info_ad.addCell(Addons.setCeldaPDF(tbl_info_ad2, 0, 1));
                pdf.add(tbl_info_ad);


            }catch(IllegalStateException ie){
                System.out.println("Error de estado ilegal " + ie.getMessage() + "\n");
                ie.printStackTrace();
            }catch(DocumentException e){
                System.out.println("Error al generar el documetno PDF " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            pdf.close(); // paso 5
            /* fin PDF */
         }
         
         
         //Nota de credito
         if(codDoc.compareTo("04")==0){
             //infoFactura
            String fechaEmision =xml2.getValor("fechaEmision");
            String dirEstablecimiento =xml2.getValor("dirEstablecimiento");
            String razonSocialComprador =xml2.getValor("razonSocialComprador");
            String identificacionComprador =xml2.getValor("identificacionComprador");
            String contribuyenteEspecial =xml2.getValor("contribuyenteEspecial");
            String obligadoContabilidad =xml2.getValor("obligadoContabilidad");
            String codDocModificado =xml2.getValor("codDocModificado");
            String numDocModificado =xml2.getValor("numDocModificado");
            String fechaEmisionDocSustento =xml2.getValor("fechaEmisionDocSustento");
            String motivo =xml2.getValor("motivo");
            String totalSinImpuestos =xml2.getValor("totalSinImpuestos");
            
            if(codDocModificado.compareTo("01")==0){
                codDocModificado="FACTURA";
            }
            if(codDocModificado.compareTo("02")==0){
                codDocModificado="NOTA DE VENTA";
            }
            if(codDocModificado.compareTo("03")==0){
                codDocModificado="LIQUIDACIÓN DE COMPRAS O SERVICIOS";
            }
            if(codDocModificado.compareTo("04")==0){
                codDocModificado="NOTA DE CRÉDITO";
            }
            if(codDocModificado.compareTo("05")==0){
                codDocModificado="NOTA DE DÉBITO";
            }
            if(codDocModificado.compareTo("06")==0){
                codDocModificado="GUÍA DE REMISION";
            }
            if(codDocModificado.compareTo("07")==0){
                codDocModificado="COMPROBANTE DE RETENCIÓN";
            }
            
            String codigoPorcentaje =xml2.getValor("codigoPorcentaje");//0% 0 >>12% 2>>mo objeto de impuesto 6>>Excento al iva 7
            String baseImponible =xml2.getValor("baseImponible"); //Depende del codigo porcentaje
            String valor =xml2.getValor("valor");// va en el Iva 12%

            //detalles
            int limite=xml2.getNumNodos("detalle"); 
            String [][] array = new String[limite][6];
            for(int i=0; i<limite;i++)
            {
                array[i][0]=xml2.getValor("codigoPrincipal");
                array[i][1]=xml2.getValor("descripcion");
                array[i][2]=xml2.getValor("cantidad");
                array[i][3]=xml2.getValor("precioUnitario");
                array[i][4]=xml2.getValor("descuento");
                array[i][5]=xml2.getValor("precioTotalSinImpuesto");
            }


           /* inicio PDF */
            Document pdf = new Document(PageSize.A4);// paso 1
           pdf.setMargins(-35,-45,40,10); /*Izquierda, derecha, tope, pie */

           try{
               PdfWriter writer = PdfWriter.getInstance(pdf,new FileOutputStream(dir+claveAcceso+".pdf"));
               pdf.open();

               //pdf.open(); // paso 3
               // Para enviar a la impresora automÃ¡ticamente.
               //writer.addJavaScript("this.print(false);", false);

               /* todo el cuerpo del doc es el paso 4 */
               //PdfPTable tbl_titulo = new PdfPTable(1);
               PdfPTable tbl_titulo = new PdfPTable(new float[]{49f,2f,49f});
               PdfPTable tbl_1 = new PdfPTable(new float[]{37f,63f});
               PdfPTable tbl_2 = new PdfPTable(1);
               //titulos izquierda
               tbl_1.addCell(Addons.setLogo(dir+"logo.jpg", 250,83,Element.ALIGN_CENTER));//ancho,alto
               tbl_1.addCell(Addons.setCeldaPDF(" ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
               tbl_1.addCell(Addons.setCeldaPDF(razonSocial+" '"+nombreComercial+"' ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,2));
               tbl_1.addCell(Addons.setCeldaPDF("Dirección Matriz: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               tbl_1.addCell(Addons.setCeldaPDF(dirMatriz, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               tbl_1.addCell(Addons.setCeldaPDF("Dirección Sucursal: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               tbl_1.addCell(Addons.setCeldaPDF(dirEstablecimiento, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               if(contribuyenteEspecial.compareTo("")!=0){
                   tbl_1.addCell(Addons.setCeldaPDF("Contribuyente Especial Nro. "+contribuyenteEspecial, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
               }
               tbl_1.addCell(Addons.setCeldaPDF("OBLIGADO A LLEVAR CONTABILIDAD: "+obligadoContabilidad, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));

               //Titulos derecha
               tbl_2.addCell(Addons.setCeldaPDF("R.U.C: "+ruc, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("F A C T U R A ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("No. "+numFac, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("NÚMERO DE AUTORIZACIÓN", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF(numeroAutorizacion, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("FECHA Y HORA DE AUTORIZACIÓN", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF(fechaAutorizacion, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("AMBIENTE: "+ambiente, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("EMISIÓN:  "+tipoEmision, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("CLAVE DE ACCESO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setBarCode(this.getBarcode(writer, claveAcceso)));//ancho,alto

               tbl_titulo.addCell(Addons.setCeldaPDF(tbl_1, 0, 1));
               tbl_titulo.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 1, Font.NORMAL, 0,0));
               tbl_titulo.addCell(Addons.setCeldaPDF(tbl_2, 0, 1));
               tbl_titulo.addCell(Addons.setFilaBlanco(3, 20));
               pdf.add(tbl_titulo);

               //Informacion Cliente
               PdfPTable tbl_info = new PdfPTable(1);
               PdfPTable tbl_info1 = new PdfPTable(new float[]{32f,43f,25f});
               tbl_info1.addCell(Addons.setCeldaPDF("Razón Social / Nombres y Apellidos: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_info1.addCell(Addons.setCeldaPDF(razonSocialComprador, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_info1.addCell(Addons.setCeldaPDF("RUC/CI: "+identificacionComprador, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_info1.addCell(Addons.setCeldaPDF("Fecha de emisión:"+fechaEmision, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,3));
               
               tbl_info1.addCell(Addons.setCeldaPDF("-------------------------------------------------------------------------------------------------------------", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,5,3));
               tbl_info1.addCell(Addons.setCeldaPDF("Comprobante que se Modifica:", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_info1.addCell(Addons.setCeldaPDF(codDocModificado+": "+numDocModificado, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
               tbl_info1.addCell(Addons.setCeldaPDF("Fecha emisión (Comprobante a Modificar)", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_info1.addCell(Addons.setCeldaPDF(fechaEmisionDocSustento, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
               tbl_info1.addCell(Addons.setCeldaPDF("Razón de Modificación: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_info1.addCell(Addons.setCeldaPDF(motivo, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
               tbl_info.addCell(Addons.setCeldaPDF(tbl_info1, 0, 1));
               tbl_info.addCell(Addons.setFilaBlanco(3, 20));
               pdf.add(tbl_info);

               //Detalles
               PdfPTable tbl_det = new PdfPTable(1);
               PdfPTable tbl_det1 = new PdfPTable(new float[]{10f,10f,50f,10f,10f,10f});

               tbl_det1.addCell(Addons.setCeldaPDF("Cod. Principal", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER , 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Cant.", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Descripción", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Precio Unitario", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Desc.", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Total", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 1,5));
               for(int i=0; i<limite;i++)
               {
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][0], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][2], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][1], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][3], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][4], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][5], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
               }
               tbl_det.addCell(Addons.setCeldaPDF(tbl_det1, 0, 1));
               tbl_det.addCell(Addons.setFilaBlanco(6, 20));
               pdf.add(tbl_det);

               //Informacion Adicional
               PdfPTable tbl_info_ad = new PdfPTable(new float[]{40f,10f,50f});
               PdfPTable tbl_info_ad1 = new PdfPTable(new float[]{30f,70f});
               PdfPTable tbl_info_ad2 = new PdfPTable(new float[]{70f,30f});
               //titulos izquierda
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Información Adicional", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,5,2));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Contacto: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("062609177 / 062610330", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Email: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF(EMAIL, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Sitio Web:", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("www.saitel.ec", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF(ARCOTEL, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,2));

               //Titulos derecha
               if(codigoPorcentaje.compareTo("0")==0){
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 12% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 0% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(baseImponible, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL No objeto de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL SIN IMPUESTOS", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(totalSinImpuestos, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL Exento de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(valor, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("IVA 12%", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,0));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0,5,2));
               }

               if(codigoPorcentaje.compareTo("2")==0){
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 12% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(baseImponible, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 0% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL No objeto de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL SIN IMPUESTOS", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(totalSinImpuestos, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL Exento de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("IVA 12%", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(valor, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
               }
               
               if(codigoPorcentaje.compareTo("3")==0){
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 14% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(baseImponible, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL 0% ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL No objeto de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL SIN IMPUESTOS", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(totalSinImpuestos, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("SUBTOTAL Exento de IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF("0.00", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));

                   tbl_info_ad2.addCell(Addons.setCeldaPDF("IVA 14%", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   tbl_info_ad2.addCell(Addons.setCeldaPDF(valor, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5));
               }

               tbl_info_ad.addCell(Addons.setCeldaPDF(tbl_info_ad1, 0, 1));
               tbl_info_ad.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 1, Font.NORMAL, 0,0));
               tbl_info_ad.addCell(Addons.setCeldaPDF(tbl_info_ad2, 0, 1));
               pdf.add(tbl_info_ad);


           }catch(IllegalStateException ie){
               ie.printStackTrace();
           }catch(DocumentException e){
               e.printStackTrace();
           }finally{
           }
           pdf.close(); // paso 5
            /* fin PDF */
         }
         
         //Comprobante de retencion
         if(codDoc.compareTo("07")==0){
             //infoCompRetencion
            String fechaEmision =xml2.getValor("fechaEmision");
            String dirEstablecimiento =xml2.getValor("dirEstablecimiento");
            String contribuyenteEspecial =xml2.getValor("contribuyenteEspecial");
            String obligadoContabilidad =xml2.getValor("obligadoContabilidad");
            String tipoIdentificacionSujetoRetenido =xml2.getValor("tipoIdentificacionSujetoRetenido");
            String razonSocialSujetoRetenido =xml2.getValor("razonSocialSujetoRetenido");
            String identificacionSujetoRetenido =xml2.getValor("identificacionSujetoRetenido");
            String periodoFiscal =xml2.getValor("periodoFiscal");

            //detalles
            int limite=xml2.getNumNodos("impuesto"); 
            String [][] array = new String[limite][7];
            for(int i=0; i<limite;i++)
            {
                array[i][0]=xml2.getValor("codigo",i);
                array[i][1]=xml2.getValor("codigoRetencion",i);
                array[i][2]=xml2.getValor("baseImponible",i);
                array[i][3]=xml2.getValor("porcentajeRetener",i);
                array[i][4]=xml2.getValor("valorRetenido",i);
                array[i][5]=xml2.getValor("codDocSustento",i);
                array[i][6]=xml2.getValor("numDocSustento",i);    
            }


           /* inicio PDF */
            Document pdf = new Document(PageSize.A4);// paso 1
           pdf.setMargins(-35,-45,40,10); /*Izquierda, derecha, tope, pie */

           try{
               PdfWriter writer = PdfWriter.getInstance(pdf,new FileOutputStream(dir+claveAcceso+".pdf"));
               pdf.open();

               //pdf.open(); // paso 3
               // Para enviar a la impresora automÃ¡ticamente.
               //writer.addJavaScript("this.print(false);", false);

               /* todo el cuerpo del doc es el paso 4 */
               //PdfPTable tbl_titulo = new PdfPTable(1);
               PdfPTable tbl_titulo = new PdfPTable(new float[]{49f,2f,49f});
               PdfPTable tbl_1 = new PdfPTable(new float[]{37f,63f});
               PdfPTable tbl_2 = new PdfPTable(1);
               //titulos izquierda
               tbl_1.addCell(Addons.setLogo(dir+"logo.jpg", 250,83,Element.ALIGN_CENTER));//ancho,alto
               tbl_1.addCell(Addons.setCeldaPDF(" ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
               tbl_1.addCell(Addons.setCeldaPDF(razonSocial+" '"+nombreComercial+"' ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,8,2));
               tbl_1.addCell(Addons.setCeldaPDF("Dirección Matriz: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               tbl_1.addCell(Addons.setCeldaPDF(dirMatriz, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               tbl_1.addCell(Addons.setCeldaPDF("Dirección Sucursal: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               tbl_1.addCell(Addons.setCeldaPDF(dirEstablecimiento, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,0));
               if(contribuyenteEspecial.compareTo("")!=0){
                   tbl_1.addCell(Addons.setCeldaPDF("Contribuyente Especial Nro. "+contribuyenteEspecial, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));
               }
               tbl_1.addCell(Addons.setCeldaPDF("OBLIGADO A LLEVAR CONTABILIDAD: "+obligadoContabilidad, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5,2));

               //Titulos derecha
               tbl_2.addCell(Addons.setCeldaPDF("R.U.C: "+ruc, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("COMPROBANTE DE RETENCIÓN", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("No. "+numFac, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("NÚMERO DE AUTORIZACIÓN", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF(numeroAutorizacion, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("FECHA Y HORA DE AUTORIZACIÓN", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF(fechaAutorizacion, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("AMBIENTE: "+ambiente, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("EMISIÓN:  "+tipoEmision, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setCeldaPDF("CLAVE DE ACCESO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,5));
               tbl_2.addCell(Addons.setBarCode(this.getBarcode(writer, claveAcceso)));//ancho,alto

               tbl_titulo.addCell(Addons.setCeldaPDF(tbl_1, 0, 1));
               tbl_titulo.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 1, Font.NORMAL, 0,0));
               tbl_titulo.addCell(Addons.setCeldaPDF(tbl_2, 0, 1));
               tbl_titulo.addCell(Addons.setFilaBlanco(3, 20));
               pdf.add(tbl_titulo);

               //Informacion Cliente
               PdfPTable tbl_info = new PdfPTable(1);
               PdfPTable tbl_info1 = new PdfPTable(new float[]{32f,43f,25f});
               tbl_info1.addCell(Addons.setCeldaPDF("Razón Social / Nombres y Apellidos: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8));
               tbl_info1.addCell(Addons.setCeldaPDF(razonSocialSujetoRetenido, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8));
               tbl_info1.addCell(Addons.setCeldaPDF("RUC/CI: "+identificacionSujetoRetenido, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8));
               tbl_info1.addCell(Addons.setCeldaPDF("Fecha de emisión: "+fechaEmision, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,3));
               tbl_info.addCell(Addons.setCeldaPDF(tbl_info1, 0, 1));
               tbl_info.addCell(Addons.setFilaBlanco(3, 20));
               pdf.add(tbl_info);

               //Detalles
               PdfPTable tbl_det = new PdfPTable(1);
               PdfPTable tbl_det1 = new PdfPTable(new float[]{14f,18f,12f,10f,13f,10f,13f,10f});

               tbl_det1.addCell(Addons.setCeldaPDF("Comprobante", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Número", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Fecha Emisión", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Ejercicio Fiscal", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Base Imponible para la retencion", Font.STRIKETHRU, 8, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Impuesto", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Porcentaje de Retención", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               tbl_det1.addCell(Addons.setCeldaPDF("Valor Retenido", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
               Double val_ret=0.0;
               
               //detalles
               for(int i=0; i<limite;i++)
               {
                   //Comprobante
                   if(array[i][5].compareTo("01")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("FACTURA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][5].compareTo("02")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("NOTA DE VENTA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][5].compareTo("03")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("LIQUIDACIÓN DE COMPRAS O SERVICIOS", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][5].compareTo("04")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("NOTA DE CRÉDITO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][5].compareTo("05")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("NOTA DE DÉBITO", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][5].compareTo("06")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("GUÍA DE REMISION", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][5].compareTo("07")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("COMPROBANTE DE RETENCIÓN", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   
                   //Número
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][6], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   
                   //Fecha Emisión
                   tbl_det1.addCell(Addons.setCeldaPDF(fechaEmision, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   
                   //Ejercicio Fiscal
                   tbl_det1.addCell(Addons.setCeldaPDF(periodoFiscal, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   
                   //Base Imponible para la retencion
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][2], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   
                   //Impuesto
                   if(array[i][0].compareTo("1")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("RENTA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][0].compareTo("2")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("IVA", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   if(array[i][0].compareTo("6")==0){
                        tbl_det1.addCell(Addons.setCeldaPDF("ISD", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   }
                   
                   //Porcentaje de Retención
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][3], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   
                   //Valor Retenido
                   tbl_det1.addCell(Addons.setCeldaPDF(array[i][4], Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5));
                   
                   val_ret+=Double.parseDouble(array[i][4]);
               }
               tbl_det1.addCell(Addons.setCeldaPDF("Valor Retenido", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_RIGHT, 1,5,7));
               tbl_det1.addCell(Addons.setCeldaPDF(Addons.redondear(val_ret)+"", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 1,5,1));
               tbl_det.addCell(Addons.setCeldaPDF(tbl_det1, 0, 1));
               tbl_det.addCell(Addons.setFilaBlanco(6, 20));
               pdf.add(tbl_det);

               //Informacion Adicional
               PdfPTable tbl_info_ad = new PdfPTable(new float[]{40f,10f,50f});
               PdfPTable tbl_info_ad1 = new PdfPTable(new float[]{30f,70f});
               //titulos izquierda
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Información Adicional", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_CENTER, 0,5,2));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Contacto: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("062609177 / 062610330", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Email: ", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF(EMAIL, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("Sitio Web:", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF("www.saitel.ec", Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,0));
               tbl_info_ad1.addCell(Addons.setCeldaPDF(ARCOTEL, Font.STRIKETHRU, 10, Font.NORMAL, Element.ALIGN_LEFT, 0,8,2));
               tbl_info_ad.addCell(Addons.setCeldaPDF(tbl_info_ad1, 0, 1));
               tbl_info_ad.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 1, Font.NORMAL, 0,0));
               tbl_info_ad.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 1, Font.NORMAL, 0,0));
               pdf.add(tbl_info_ad);
               


           }catch(IllegalStateException ie){
               ie.printStackTrace();
           }catch(DocumentException e){
               e.printStackTrace();
           }finally{
           }
           pdf.close(); // paso 5
            /* fin PDF */
         }

    }        
    private Image getBarcode(PdfWriter pdfWriter, String codigo) {
        PdfContentByte cimg = pdfWriter.getDirectContent();
        Barcode128 code128 = new Barcode128();
        code128.setCode(codigo);
        code128.setCodeType(Barcode128.CODE128);
        code128.setTextAlignment(Element.ALIGN_CENTER);
        Image image = code128.createImageWithBarcode(cimg, null, null);
        //float scaler = ((document.getPageSize().getWidth() - document.leftMargin()  - document.rightMargin() - 0) / image.getWidth()) * 70;
        //image.scalePercent(scaler);
        //image.scaleAbsolute(90f, 35f);
        image.scalePercent(90f);
        image.setAlignment(Element.ALIGN_CENTER);
        return image;
    }
    private float getConvertCmsToPoints(float cm) {
        return cm * 28.4527559067f;
    }
    
    public String quitarTildes(String doc){
        
        doc=doc.replaceAll("Á", "A");doc=doc.replaceAll("á", "a");
        doc=doc.replaceAll("É", "E");doc=doc.replaceAll("é", "e");
        doc=doc.replaceAll("Ó", "O");doc=doc.replaceAll("í", "i");
        doc=doc.replaceAll("Í", "I");doc=doc.replaceAll("ó", "o");
        doc=doc.replaceAll("Ú", "U");doc=doc.replaceAll("ú", "u");
        doc=doc.replaceAll("Ñ", "N");doc=doc.replaceAll("ñ", "n");
        doc=doc.replaceAll("&", "&amp;");
        
        return doc;
    }
}
