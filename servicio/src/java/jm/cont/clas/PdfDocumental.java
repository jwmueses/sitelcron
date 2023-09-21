/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cont.clas;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import jm.pag.clas.Configuracion;
import jm.pag.clas.Instalacion;
import jm.seg.clas.Cliente;
import jm.ser.clas.Plan;
import jm.web.Addons;
import jm.web.Archivo;
import jm.web.Cadena;
import jm.web.DataBase;
import jm.web.Fecha;
import jm.web.Matriz;

/**
 *
 * @author wilso
 */
public class PdfDocumental {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    private String _ipdocumental = null;
    private int _puertodocumental = 5432;
    private String _dbdocumental = null;
    private String _usuariodocumental = null;
    private String _clavedocumental = null;
    private String _URL_ANEXOS = null;
    private String _dir = null;
    String numero_contrato = null;
    byte[] logo = null;
    byte[] marca_agua = null;
    byte[] croquis_instalacion = null;
    public static String contrato_texto = "";

    public void setConexion(String ip, int puerto, String db, String usuario, String clave, String ipdocumental, int puertodocumental, String dbdocumental, String usuariodocumental, String clavedocumental) {
        this._ip = ip;
        this._puerto = puerto;
        this._db = db;
        this._usuario = usuario;
        this._clave = clave;
        this._ipdocumental = ipdocumental;
        this._puertodocumental = puertodocumental;
        this._dbdocumental = dbdocumental;
        this._usuariodocumental = usuariodocumental;
        this._clavedocumental = clavedocumental;
    }

    public void setConexionTransaccional(String ip, int puerto, String db, String usuario, String clave) {
        this._ip = ip;
        this._puerto = puerto;
        this._db = db;
        this._usuario = usuario;
        this._clave = clave;
    }

    public void setUDirAnexos(String url_anexos, String dir) {
        this._URL_ANEXOS = url_anexos;
        this._dir = dir;
    }

    public void setConexionDocumental(String ipdocumental, int puertodocumental, String dbdocumental, String usuariodocumental, String clavedocumental) {
        this._ipdocumental = ipdocumental;
        this._puertodocumental = puertodocumental;
        this._dbdocumental = dbdocumental;
        this._usuariodocumental = usuariodocumental;
        this._clavedocumental = clavedocumental;
    }

    public String Generarcontrato(String id_contrato, File fichero) {
        String nombre_fichero = null;
        try {
            String firmas[] = null;
            String[][] contrato = this.Modelo(id_contrato);
            firmas = this.Firmas(contrato[0][6], contrato[0][7], contrato[0][8]);
            PdfCotrato objPdfCotrato = new PdfCotrato();
            nombre_fichero = objPdfCotrato.imprimir(contrato, firmas, fichero);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            nombre_fichero = null;
        }
        return nombre_fichero;

    }

    public String Generarcontrato(String id_contrato, File fichero, byte[] croquis) {
        String nombre_fichero = null;
        try {
            croquis_instalacion = croquis;
            Archivo ObjArchivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
            try {
                logo = ObjArchivo.getArchivo(1);
                marca_agua = logo;
            } catch (Exception e) {

            } finally {
                ObjArchivo.cerrar();
            }
            String firmas[] = null;
            String[][] contrato = this.Modelo(id_contrato);
            firmas = this.Firmas(contrato[0][6], contrato[0][7], contrato[0][8]);
            PdfCotrato objPdfCotrato = new PdfCotrato();
            nombre_fichero = objPdfCotrato.imprimir(contrato, firmas, fichero);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            nombre_fichero = null;
        }
        return nombre_fichero;

    }

    public String[] Firmas(String cedula, String cliente, String representante) {
        String datosFirma[] = {"", "", "", "", "", ""};
        Configuracion ObjConfiguracion = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            datosFirma[0] = ObjConfiguracion.getValor("rep_cargo").toUpperCase();
            datosFirma[1] = representante.compareTo("") != 0 ? cliente.toUpperCase() : "EL SUSCRIPTOR";
            datosFirma[2] = ObjConfiguracion.getValor("rep_nombre");
            datosFirma[3] = representante.compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente);
            datosFirma[4] = "R.U.C.: " + ObjConfiguracion.getValor("ruc");
            datosFirma[5] = (cedula.length() == 13 ? "R.U.C.: " : "C.I.: ") + cedula;
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjConfiguracion.cerrar();
        }
        return datosFirma;
    }

    public String[][] Modelo(String id_contrato) {
        String[][] tipo_documento = {{"04", "Ruc"}, {"05", "Cedula"}, {"06", "Pasaporte"}};
        String anexos[][] = {
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}
        };
        String tmpcontrato = "";
        String tmpburo = "";
        String id_sucursal = "";
        String num_contrato = "";
        String tipo_documento1 = "04";
        String id_cliente = "";
        String cedula = "";
        String cliente = "";
        String representante = "";
        String id_instalacion = "";
        String direccion = "";
        String parroquia = "";
        String canton = "";
        String ciudad = "";
        String provincia = "";
        String email = "";
        String telefono = "";
        String movil_claro = "";
        String movil_movistar = "";
        String movil = "";
        String edad = "";
        String carne_conadis = "";
        String direccion_instalacion = "";
        String tipo_cliente_instalacion = "c";
        String fecha_actual = Fecha.getAnio() + "-" + Fecha.getMes() + "-" + Fecha.getDia();
        boolean especial = false;
        String costo_instalacion = "0";
        String tiempo_permanencia = "0";
        Contrato objContrato = new Contrato(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            ResultSet rsDoc = objContrato.getDocumento("x");
            if (rsDoc.next()) {
                tmpcontrato = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("w");
            if (rsDoc.next()) {
                anexos[0][1] = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("v");
            if (rsDoc.next()) {
                anexos[0][2] = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("q");
            if (rsDoc.next()) {
                tmpburo = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            ResultSet res = objContrato.getContrato(id_contrato);
            if (res.next()) {
                id_sucursal = res.getString("id_sucursal") != null ? res.getString("id_sucursal") : "";
                num_contrato = res.getString("num_contrato") != null ? res.getString("num_contrato") : "";
                cedula = res.getString("ruc") != null ? res.getString("ruc") : "";
                cliente = res.getString("razon_social") != null ? res.getString("razon_social") : "";
                representante = res.getString("representante") != null ? res.getString("representante") : "";
                id_instalacion = res.getString("id_instalacion") != null ? res.getString("id_instalacion") : "";
                id_cliente = res.getString("id_cliente") != null ? res.getString("id_cliente") : "";
                fecha_actual = res.getString("fecha_contrato") != null ? res.getString("fecha_contrato") : "";
                res.close();
            }
        } catch (Exception e) {
            System.out.println("datos del contrato");
        } finally {
            objContrato.cerrar();
        }
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            numero_contrato = id_sucursal + " - " + num_contrato;
            ciudad = objSucursal.getCiudad(Integer.parseInt(id_sucursal));
            costo_instalacion = objSucursal.getSucursalCampo(id_sucursal, "costo_instalacion");
            tiempo_permanencia = objSucursal.getSucursalCampo(id_sucursal, "tiempo_permanencia");
        } catch (Exception e) {
            System.out.println("datos del cliente" + e.getMessage());
        } finally {
            objSucursal.cerrar();
        }
        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            ResultSet res = objCliente.getCliente(id_cliente);
            if (res.next()) {
                anexos[2][0] = direccion = res.getString("direccion") != null ? res.getString("direccion") : "";
                anexos[2][1] = provincia = res.getString("id_provincia") != null ? res.getString("id_provincia") : "";
                anexos[2][2] = canton = res.getString("id_ciudad") != null ? res.getString("id_ciudad") : "";
                anexos[2][3] = parroquia = res.getString("id_parroquia") != null ? res.getString("id_parroquia") : "";
                anexos[2][4] = email = res.getString("email") != null ? res.getString("email") : "";
                anexos[2][5] = telefono = res.getString("telefono") != null ? res.getString("telefono") : "";
                anexos[2][6] = movil_claro = res.getString("movil_claro") != null ? res.getString("movil_claro") : "";
                anexos[2][7] = movil_movistar = res.getString("movil_movistar") != null ? res.getString("movil_movistar") : "";
                anexos[2][8] = edad = res.getString("edad") != null ? res.getString("edad") : "";
                anexos[2][9] = carne_conadis = res.getString("carne_conadis") != null ? res.getString("carne_conadis") : "";
                anexos[2][10] = tipo_documento1 = res.getString("tipo_documento") != null ? res.getString("tipo_documento") : "04";
                anexos[2][11] = id_sucursal;
                anexos[2][14] = res.getString("observacion") != null ? res.getString("observacion") : "";;
                res.close();
            }
            if (telefono.trim().compareTo("") != 0) {
                movil += telefono + " / ";
            }
            if (movil_claro.trim().compareTo("") != 0) {
                movil += movil_claro + " / ";
            }
            if (movil_movistar.trim().compareTo("") != 0) {
                movil += movil_movistar + " / ";
            }
            if (movil.trim().compareTo("") != 0) {
                movil = movil.substring(0, movil.length() - 2);
            }
            if (edad.trim().compareTo("") == 0) {
                edad = "0";
            }
            if (Integer.parseInt(edad) >= 65 || carne_conadis.trim().compareTo("") != 0) {
                especial = true;
            }
        } catch (Exception e) {
            System.out.println("datos del cliente");
        } finally {
            objCliente.cerrar();
        }
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String valor_instalacion_sector = "";
        try {
            ResultSet res = objInstalacion.getInstalacion(id_instalacion);
            if (res.next()) {
                anexos[1][0] = direccion_instalacion = res.getString("direccion_instalacion") != null ? res.getString("direccion_instalacion") : "";
                anexos[1][1] = tipo_cliente_instalacion = res.getString("tipo_cliente_instalacion") != null ? res.getString("tipo_cliente_instalacion") : "";
                anexos[1][2] = res.getString("tipo_instalacion") != null ? res.getString("tipo_instalacion") : "";
                anexos[1][3] = res.getString("plan") != null ? res.getString("plan") : "";
                anexos[1][4] = res.getString("plan_burst_limit") != null ? res.getString("plan_burst_limit") : "0";
                anexos[1][5] = res.getString("txt_comparticion") != null ? res.getString("txt_comparticion") : "";
                anexos[1][6] = res.getString("costo_instalacion") != null ? res.getString("costo_instalacion") : "0";
                anexos[1][7] = res.getString("id_plan_actual") != null ? res.getString("id_plan_actual") : "";
                anexos[1][8] = res.getString("txt_convenio_pago") != null ? res.getString("txt_convenio_pago") : "";
                anexos[1][9] = res.getString("forma_pago") != null ? res.getString("forma_pago") : "";
                anexos[1][10] = res.getString("id_provincia") != null ? res.getString("id_provincia") : "";
                anexos[1][11] = res.getString("id_ciudad") != null ? res.getString("id_ciudad") : "";
                anexos[1][12] = res.getString("id_parroquia") != null ? res.getString("id_parroquia") : "";
                anexos[1][13] = res.getString("latitud") != null ? res.getString("latitud") : "";
                anexos[1][14] = res.getString("longitud") != null ? res.getString("longitud") : "";
                valor_instalacion_sector = res.getString("costo_instalacion_facturado") != null ? res.getString("costo_instalacion_facturado") : "0";
                valor_instalacion_sector = String.valueOf(Addons.redondear(Double.parseDouble(valor_instalacion_sector) / 1.12));
//                id_sector = res.getString("id_sector") != null ? res.getString("id_sector") : "";

                anexos[1][18] = id_instalacion;
                anexos[1][4] = (anexos[1][4].trim().compareTo("") == 0 ? "0" : "" + Addons.redondearDecimales((Double.parseDouble(anexos[1][4]) / 1000), 2));
                anexos[1][6] = (anexos[1][6].trim().compareTo("") == 0 ? "0" : "" + Math.round(Double.parseDouble(anexos[1][6])));
                res.close();
            }
        } catch (Exception e) {
            System.out.println("datos del ubicacion");
        } finally {
            objInstalacion.cerrar();
        }
        Archivo objArchivo = new Archivo(this._ipdocumental, this._puertodocumental, this._dbdocumental, this._usuariodocumental, this._clavedocumental);
        try {
            anexos[1][17] = null;
            croquis_instalacion = objArchivo.getArchivoDocumental("tbl_instalacion", id_instalacion, "imgcroquis");
        } catch (Exception e) {
            System.out.println("datos del ubicacion");
        } finally {
            objArchivo.cerrar();
        }
        Plan objPlanServicio = new Plan(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {

            ResultSet res = objPlanServicio.getPlanDetalle(anexos[1][7]);
            if (res.next()) {
                anexos[1][15] = res.getString("costo_plan") != null ? res.getString("costo_plan") : "";
                anexos[1][16] = res.getString("plan") != null ? res.getString("plan") : "";
                res.close();
            }
        } catch (Exception e) {
            System.out.println("datos del ubicacion");
        } finally {
            objPlanServicio.cerrar();
        }
        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            provincia = objUbicacion.getNombre(provincia.compareTo("") != 0 ? provincia : "-1");
            canton = objUbicacion.getNombre(canton.compareTo("") != 0 ? canton : "-1");
            parroquia = objUbicacion.getNombre(parroquia.compareTo("") != 0 ? parroquia : "-1");
            anexos[1][10] = objUbicacion.getNombre(anexos[1][10].compareTo("") != 0 ? anexos[1][10] : "-1");
            anexos[1][11] = objUbicacion.getNombre(anexos[1][11].compareTo("") != 0 ? anexos[1][11] : "-1");
            anexos[1][12] = objUbicacion.getNombre(anexos[1][12].compareTo("") != 0 ? anexos[1][12] : "-1");
            anexos[2][15] = objUbicacion.getNombre(anexos[2][1].compareTo("") != 0 ? anexos[2][1] : "-1");
            anexos[2][16] = objUbicacion.getNombre(anexos[2][2].compareTo("") != 0 ? anexos[2][2] : "-1");
            anexos[2][17] = objUbicacion.getNombre(anexos[2][3].compareTo("") != 0 ? anexos[2][3] : "-1");

        } catch (Exception e) {
            System.out.println("datos del ubicacion" + e.getMessage());
        } finally {
            objUbicacion.cerrar();
        }
        String porcentaje_promocion = "";
        Promocion objPromocion = new Promocion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            ResultSet rs = objPromocion.getPromocionesInstalacion(id_instalacion);
            if (rs.next()) {
                anexos[2][18] = rs.getString(1) != null ? rs.getString(1) : "";
                anexos[2][19] = rs.getString(2) != null ? rs.getString(2) : "";
                anexos[2][20] = rs.getString(4) != null ? rs.getString(4) : "";
                porcentaje_promocion = rs.getString(10) != null ? rs.getString(10) : "";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            objPromocion.cerrar();
        }
        String iva_actual = "";
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            iva_actual = conf.getValor("p_iva1");;
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            conf.cerrar();
        }
        anexos[3][0] = (costo_instalacion.trim().compareTo("") == 0 ? "0" : costo_instalacion);
        anexos[3][1] = (tiempo_permanencia.trim().compareTo("") == 0 ? "0" : tiempo_permanencia);
        anexos[3][2] = (valor_instalacion_sector.trim().compareTo("") == 0 ? "0" : valor_instalacion_sector);
        anexos[3][3] = (iva_actual.trim().compareTo("") == 0 ? "12" : iva_actual);
        anexos[3][4] = (porcentaje_promocion.trim().compareTo("") == 0 ? "0" : porcentaje_promocion);
        String permisos_isp[][] = null;
        DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            permisos_isp = Matriz.ResultSetAMatriz(objDataBase.consulta("select codigo_permiso,nombre_permiso from tbl_permisos_isp where eliminado=false;"));
        } catch (Exception e) {
            System.out.println("datos del permisossp");
        } finally {
            objDataBase.cerrar();
        }
        boolean tieneDiferenciaCostoInstalacion = (Double.parseDouble(costo_instalacion) - Double.parseDouble(valor_instalacion_sector)) > 0;
        tmpcontrato = tmpcontrato.replaceAll("<<lugar>>", ciudad);
        tmpcontrato = tmpcontrato.replaceAll("<<fecha>>", Fecha.getFechaSolicitud(fecha_actual));
        tmpcontrato = tmpcontrato.replaceAll("<<rason_social>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
        tmpcontrato = tmpcontrato.replaceAll("<<cedula>>", cedula);
        tmpcontrato = tmpcontrato.replaceAll("<<email>>", email);
        tmpcontrato = tmpcontrato.replaceAll("<<movil>>", movil);
        tmpcontrato = tmpcontrato.replaceAll("<<direccion>>", direccion);
        tmpcontrato = tmpcontrato.replaceAll("<<parroquia>>", parroquia);
        tmpcontrato = tmpcontrato.replaceAll("<<canton>>", canton);
        tmpcontrato = tmpcontrato.replaceAll("<<ciudad>>", canton);
        tmpcontrato = tmpcontrato.replaceAll("<<provincia>>", provincia);
        tmpcontrato = tmpcontrato.replaceAll("<<siedad>>", (especial ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<noedad>>", (!especial ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<permanenciasi>>", ((anexos[2][18].trim().compareTo("") != 0 || tieneDiferenciaCostoInstalacion) ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<permanenciano>>", (anexos[2][18].trim().compareTo("") == 0 && !tieneDiferenciaCostoInstalacion ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<direccion_instalacion>>", direccion_instalacion);
        String permisos_isp1 = "";
        if (permisos_isp != null) {
            for (int i = 0; i < permisos_isp.length; i++) {
                permisos_isp1 += permisos_isp[i][1] + "                  " + (permisos_isp[i][0].compareTo(tipo_cliente_instalacion) == 0 ? "X" : "") + "  \n";
            }
        }

        tmpcontrato = tmpcontrato.replaceAll("<<permisos_isp>>", permisos_isp1);
        tmpburo = tmpburo.replaceAll("<<rason_social>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
        tmpburo = tmpburo.replaceAll("<<cedula>>", cedula);
        tmpburo = tmpburo.replace("<<tipo_documento>>", tipo_documento[Matriz.enMatriz(tipo_documento, tipo_documento1, 0)][1]);
        anexos[2][12] = tipo_documento[Matriz.enMatriz(tipo_documento, tipo_documento1, 0)][1];
        anexos[2][13] = movil;
        anexos[0][0] = tmpcontrato;
        contrato_texto = tmpcontrato;
        anexos[0][3] = tmpburo;
        anexos[0][4] = ciudad;
        anexos[0][5] = fecha_actual;
        anexos[0][6] = cedula;
        anexos[0][7] = cliente;
        anexos[0][8] = representante;
        return anexos;
    }

    public class PdfCotrato extends PdfPageEventHelper {

        public void onStartPage(PdfWriter writer, Document document) {
            try {
                PdfPTable encabezado = new PdfPTable(2);
                encabezado.setTotalWidth(document.right() - document.left() - 120);
                PdfPCell logoCel = Addons.setLogo(logo, 200, 60);
                if (logoCel != null) {
                    encabezado.addCell(logoCel);
                } else {
                    encabezado.addCell("");
                }
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF("Contrato Nro. " + numero_contrato, Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.writeSelectedRows(0, -1, 80, document.top() + 80, writer.getDirectContent());
                PdfContentByte cb = writer.getDirectContent();
                cb.setLineWidth(2);
                cb.moveTo(60, document.top() + 10);
                cb.lineTo(document.right() - document.left() - 58, document.top() + 10);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }

        }

        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Image img = Addons.setMarcaAgua(marca_agua, 681, 206);
                if (img != null) {
                    document.add(img);
                }
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        public String imprimir(String documentos[][], String firmas[], File fichero) {
            String nombre = null;
            try {
                Document document = new Document(PageSize.A4);// paso 1
                PdfWriter.getInstance(document, new FileOutputStream(fichero));
                document.setMargins(30, 0, 90, 30);
                document.open();
                /* todo el cuerpo del doc es el paso 4 */
                PdfPTable tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                PdfPTable tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);
                /*modelo de contrato */
                double iva_actual = Double.parseDouble(documentos[3][3]);
                double valor_instalacion = Double.parseDouble(documentos[3][0]);
                valor_instalacion = valor_instalacion + ((valor_instalacion * iva_actual) / 100);
                valor_instalacion = Addons.redondear(valor_instalacion, 2);
                int tiempo_permanencia = Integer.parseInt(documentos[3][1]);
                double costo_instalacion_sector = Double.parseDouble(documentos[3][2]);
                double porcentaje_descuento = Double.parseDouble(documentos[3][4]);
                costo_instalacion_sector = costo_instalacion_sector + ((costo_instalacion_sector * iva_actual) / 100);
                valor_instalacion = (costo_instalacion_sector > valor_instalacion ? costo_instalacion_sector : valor_instalacion);
                String tiempo_permanecia_final = (documentos[2][20].trim().compareTo("") == 0 ? "" + tiempo_permanencia : documentos[2][18]);
                double costo_instalacion_promocion = (valor_instalacion - ((valor_instalacion * porcentaje_descuento) / 100));
                double abono_final = (documentos[2][20].trim().compareTo("") == 0 ? costo_instalacion_sector : costo_instalacion_promocion);
                double valor_penal = (documentos[2][20].trim().compareTo("") == 0 ? (valor_instalacion - costo_instalacion_sector) : valor_instalacion - (costo_instalacion_promocion));

                //anexo depende del proveedor y cliente
                document.newPage();
                if (documentos[1][1].trim().compareTo("c") == 0) {
                    tbl_det = new PdfPTable(1);
                    //tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    //tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIO DE ACCESO A INTERNET", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("ANEXO 1", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL PLAN COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL PLAN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("NOMBRE DEL PLAN: " + documentos[1][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("RED DE ACCESO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    String[][] tipo_instalacion = {{"a", "Inalámbrico (antena)"}, {"f", "Fibra (punto a punto)"}};
                    if (Integer.parseInt(documentos[2][11]) == 9) {
                        tipo_instalacion = new String[][]{{"a", "Inalámbrico (antena)"}, {"n", "inalámbrico prepago (antena)"}, {"f", "Fibra (punto a punto)"}};
                    }
                    if (Integer.parseInt(documentos[2][11]) != 5 || Integer.parseInt(documentos[2][11]) != 9 || Integer.parseInt(documentos[2][11]) != 10) {
                        tipo_instalacion = new String[][]{{"a", "Inalámbrico (antena)"}, {"n", "inalámbrico PYMES (antena)"}, {"f", "Fibra (punto a punto)"}, {"g", "Fibra GEPON"}};
                    }
                    for (int i = 0; i < tipo_instalacion.length; i++) {
                        tbl_det.addCell(Addons.setCeldaPDF(tipo_instalacion[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((tipo_instalacion[i][0].compareTo(documentos[1][2]) == 0 ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    }
                    document.add(tbl_det);
                    ////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("TIPO DE CUENTA", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    String[][] tipo_cuenta = {{"residencial", "Residencial"}, {"corporativo", "Corporativo"}, {"small", "Negocio Small"}, {"otros", "Otros"}};
                    boolean ok = false;
                    for (int i = 0; i < tipo_cuenta.length; i++) {
                        tbl_det.addCell(Addons.setCeldaPDF(tipo_cuenta[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        if (tipo_cuenta[i][0].indexOf(documentos[1][16].toLowerCase()) >= 0) {
                            tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            ok = true;
                        } else {
                            if (!ok && tipo_cuenta.length - 1 == i) {
                                tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            } else {
                                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            }
                        }

                    }
                    document.add(tbl_det);
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("VELOCIDAD", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("Compartición " + documentos[1][5], Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    String[] velocidad = {"Comercial de bajada", "Comercial de subida", "Minima efectiva de bajada", "Minima efectiva de subida"};
                    for (int i = 0; i < velocidad.length; i++) {
                        tbl_det.addCell(Addons.setCeldaPDF(velocidad[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(documentos[1][4] + " Mbps", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    document.add(tbl_det);
                    ////
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS ADICIONALES QUE OFRECE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("CANT.", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    String servicios_adicionales[][] = ServiciosAdicionales(documentos[1][18]);
                    if (servicios_adicionales != null) {
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][2], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }

                    }
                    document.add(tbl_det);
                    ////
                    /////
                    //document.add(new Paragraph(" "));

                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("PLAZO DEL CONTRATO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("TIEMPO MESES", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("El contato incluye permanencia mínima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[2][20].trim().compareTo("") != 0 || valor_penal > 0 ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[2][20].trim().compareTo("") == 0 && valor_penal <= 0 ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + tiempo_permanecia_final, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Beneficios por permanencia mínima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Valor abonado $" + Addons.redondear(abono_final, 2) + " al costo de instalacion", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Valor a cancelar en caso de no completar el tiempo de permanencia minima $" + Addons.redondear(valor_penal, 2), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                    document.add(tbl_det);
                    ////
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("TARIFAS", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(2);
                    try {
                        tbl_det.addCell(Addons.setCeldaPDF("Instalación (Valor a pagar por una sola vez)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + valor_instalacion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Plazo para activar instalación (Horas ,Dias)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Pago mensual incluido iva", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + Math.round(Double.parseDouble(documentos[1][15]) + (Double.parseDouble(documentos[1][15]) * 12 / 100)), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Modalidad de pago", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][8], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Formas de pag", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[1][9].compareTo("") == 0 ? "Ninguna" : (documentos[1][9].compareTo("CTA") == 0 ? "Cuenta Bancaria" : "Tarjeta")), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    } catch (Exception e) {
                        System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                    }
                    document.add(tbl_det);
                    ///
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("OTROS VALORES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("VALOR (USD)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    if (servicios_adicionales != null) {
                        double total_servicios = 0;
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            try {
                                total_servicios += (Double.parseDouble(servicios_adicionales[i][3]) * Double.parseDouble(servicios_adicionales[i][2]));
                            } catch (Exception e) {
                                System.out.println("" + e.getLocalizedMessage() + "   " + e.getMessage());
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF("Total otros Servicios", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + Addons.redondearDecimales(total_servicios, 2), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    }

                    document.add(tbl_det);
                    ///
                    /////
                    document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web para consulta de tarifas", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("http://saitel.ec/planes/escoge-tu-mejor-plan/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web consulta calidad del servicio", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("http://saitel.ec/consulta_calidad/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    document.add(tbl_det);
                    ///
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    tbl_firma = new PdfPTable(2);
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    for (int i = 0; i < firmas.length; i++) {
                        tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    }
                    document.add(tbl_firma);
                } else {
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIO PORTADOR", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("ANEXO 1", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL PLAN COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    /////
                    document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("PLAZO DEL CONTRATO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("TIEMPO MESES", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("El contato incluye permanencia minima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[2][20].trim().compareTo("") == 0 ? "" : "X"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[2][20].trim().compareTo("") == 0 ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[2][18], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Beneficios por permanencia mínima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[2][19], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));
                    ////
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("ENLACES NACIONALES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("N°", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("MEDIO TRANSMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("VELOCIDAD DE TRASMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("COMPARTICIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("1", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[1][2].trim().compareTo("n") == 0 || documentos[1][2].trim().compareTo("a") == 0 ? "MEDIO INALAMBRICO" : "FIBRA OPTICA"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][4] + "  Mbps", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][5], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    document.add(tbl_det);
                    ////
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("ENLACES INTERNACIONALES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("N°", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("MEDIO TRANSMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("CELOCIDAD DE TRASMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("COMPARTICIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    document.add(tbl_det);
                    ////
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS ADICIONALES QUE OFRECE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("CANT.", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    String servicios_adicionales[][] = ServiciosAdicionales(documentos[1][18]);
                    if (servicios_adicionales != null) {
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][2], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }

                    }
                    document.add(tbl_det);
                    ////
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("TARIFAS", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    try {
                        tbl_det = new PdfPTable(2);
                        tbl_det.addCell(Addons.setCeldaPDF("Instalación (Valor a pagar por una sola vez)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][6], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Plazo para activar instalación (Horas ,Dias)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Pago mensual incluido iva", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + Math.round(Double.parseDouble(documentos[1][15]) + (Double.parseDouble(documentos[1][15]) * 12 / 100)), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Modalidad de pago", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][8], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Formas de pago", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[1][9].compareTo("") == 0 ? "Ninguna" : (documentos[1][9].compareTo("CTA") == 0 ? "Cuenta Bancaria" : "Tarjeta")), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    } catch (Exception e) {
                        System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                    }
                    document.add(tbl_det);
                    ///
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("OTROS VALORES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("VALOR (USD)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    if (servicios_adicionales != null) {
                        double total_servicios = 0;
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            try {
                                total_servicios += (Double.parseDouble(servicios_adicionales[i][3]) * Double.parseDouble(servicios_adicionales[i][2]));
                            } catch (Exception e) {
                                System.out.println("" + e.getLocalizedMessage() + "   " + e.getMessage());
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF("Total otros Servicios", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + Addons.truncar(total_servicios), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    }
                    document.add(tbl_det);
                    ///
                    document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web para consulta de tarifas", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("http://saitel.ec/planes/escoge-tu-mejor-plan/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web consulta calidad del servicio", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("http://saitel.ec/consulta_calidad/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    document.add(tbl_det);
                    ///
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    tbl_firma = new PdfPTable(2);
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    for (int i = 0; i < firmas.length; i++) {
                        tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    }
                    document.add(tbl_firma);
                }
                document.newPage();
                /////dartos del cliente y de la instalacion
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("DATOS DEL CLIENTE, DATOS DE LA INSTALACIÓN Y CONTACTOS PROVEEDOR", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF("ANEXO 2", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL ABONADO CLIENTE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("ABONADO/CLIENTE: " + (documentos[0][8].trim().compareTo("") != 0 ? Cadena.capital(documentos[0][8]) : Cadena.capital(documentos[0][7])), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("TIPO DE IDENTIFICACIÓN: " + documentos[2][12] + " N° " + documentos[0][6], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                ///////
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("CONTACTOS DEL CLIENTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                tbl_det = new PdfPTable(4);
                tbl_det.addCell(Addons.setCeldaPDF("Email", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][4], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Telefonos", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][13], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Dirección: " + documentos[2][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Referencia: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Parroquia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][17], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Cantón", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][16], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Provincia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][15], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                ////cambio
                document.add(tbl_det);
                ////
///////
                // document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("LUGAR DE INSTALACIÓN", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                tbl_det = new PdfPTable(4);
                tbl_det.addCell(Addons.setCeldaPDF("Dirección: " + documentos[1][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Referencia: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Parroquia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][12], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Cantón", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][11], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Provincia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][10], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                //cambio
                document.add(tbl_det);
                ////
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("PUNTOS GPS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Latitud: " + documentos[1][13], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Logitud: " + documentos[1][14], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                ////////
                document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("CROQUIS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                try {
                    PdfPCell celdaImg = Addons.setLogo(croquis_instalacion, 200, 60);
                    celdaImg.setBorderWidth(0);
                    celdaImg.setPadding(0);
                    tbl_det.addCell(celdaImg);

                } catch (Exception e) {
                    tbl_det.addCell("");
                }
                document.add(tbl_det);
                ////////
                document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("FACTURA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                tbl_det = new PdfPTable(4);
                tbl_det.addCell(Addons.setCeldaPDF("FÍSICA", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("ELECTRÓNICA", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("EMAIL", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][4], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                document.add(tbl_det);
                ////
                document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("Si su elección es la factura electrónica puede descargarse la misma en el link: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("http://saitelapp.ec/html/pags/facturaElectronica/index.php", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("CONSULTAS TUS PLANILLAS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("http://saitelapp.ec:8443/sitio/planilla.jsp", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Planes y tarifas:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Sitio web para consulta de tarifas: http://saitel.ec/planes/escoge-tu-mejor-plan/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("CONTACTOS PROVEEDOR", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email información SAITEL: info@saitel.ec", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email información sucursal:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email soporte SAITEL: soporte@saitel.ec", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email soporte sucursal:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Teléfonos: 1700(SAITEL)724835, Call Center 1996724835 ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("HORARIOS DE ATENCIÓN", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Lunes a Viernes: 08:00 a 18:00", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Sabados: 09:00 a 13:00", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                /////
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);
                ///datos del cliente y la instalacion

                //custodia de equipos y propiedad de saitel
                document.newPage();
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("CUSTODIA DE EQUIPOS PROPIEDAD DE SAITEL ENTREGADOS AL ABONADO/CLIENTE", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);

                //pagina acceso y seguridad de 
                document.newPage();
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("ACCESO Y SEGURIDAD DE INFORMACION", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][2], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);
                ///pagina del buro de credito
                document.newPage();

                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("AUTORIZACIÓN CONSULTA BURO DE CRÉDITO", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][3], Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(1);
                tbl_firma.addCell(Addons.setCeldaPDF("Nombres y apellidos:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("Cédula/Pasaporte:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("Firma:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));

                document.add(tbl_firma);
                document.close();
                nombre = fichero.getName();
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
                nombre = null;
            }
            return nombre;
        }

    }

    public String[][] ServiciosAdicionales(String id_instalacion) {
        DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            return Matriz.ResultSetAMatriz(objDataBase.consulta("select nombre_servicio,detalle_servicio,cantidad,round(valor_servicio,2) from vta_instalacion_servicio where id_instalacion='" + id_instalacion + "';"));
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage() + "  " + e.getMessage());
            return null;
        } finally {
            objDataBase.cerrar();
        }
    }
    /*public String[][] Modelo(String id_sucursal, String ruc, String razon_social, String representante, String ruc_representante, String id_cliente,
            String direccion_instalacion, String tipo_cliente, String tipo_instalacion, String plan, String velocidad_plan, String comparticion_plan,
            String costo_plan, String id_plan, String convenio_pago, String forma_pago, String id_provincia, String id_ciudad, String id_parroquia,
            String latitud, String longitud, String tiempo_promocion, String detalle_promocion, String id_promocion) {
        String[][] tipo_documento = {{"04", "Ruc"}, {"05", "Cedula"}, {"06", "Pasaporte"}};
        String anexos[][] = {
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}
        };
        Contrato ObjContrato = new Contrato(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            String contrato_final = "";
            String buro_credito = "";
            String cedula = ruc;
            String cliente = razon_social;
            String fecha_actual = Fecha.getFecha("ISO");
            String direccion = "";
            String provincia = "";
            String canton = "";
            String parroquia = "";
            String email = "";
            String telefono = "";
            String movil_claro = "";
            String movil_movistar = "";
            String edad = "";
            String carne_conadis = "";
            String tipo_documento1 = "";
            String movil = "";
            boolean especial = false;
            String ciudad = "";
            Sucursal ObjSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
            try {
                numero_contrato = ObjContrato.getNumeroContrato(id_sucursal);
                numero_contrato = id_sucursal + " - " + numero_contrato;
                ciudad = ObjSucursal.getCiudad(Integer.parseInt(id_sucursal));
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            } finally {
                ObjSucursal.cerrar();
            }
            try {
                ResultSet rsDoc = ObjContrato.getDocumento("x");
                if (rsDoc.next()) {
                    contrato_final = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                    rsDoc.close();
                }
                rsDoc = ObjContrato.getDocumento("w");
                if (rsDoc.next()) {
                    anexos[0][1] = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                    rsDoc.close();
                }
                rsDoc = ObjContrato.getDocumento("v");
                if (rsDoc.next()) {
                    anexos[0][2] = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                    rsDoc.close();
                }
                rsDoc = ObjContrato.getDocumento("q");
                if (rsDoc.next()) {
                    buro_credito = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                    rsDoc.close();
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            Cliente ObjCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);
            try {
                ResultSet res = ObjCliente.getCliente(id_cliente);
                if (res.next()) {
                    anexos[2][0] = direccion = res.getString("direccion") != null ? res.getString("direccion") : "";
                    anexos[2][1] = provincia = res.getString("id_provincia") != null ? res.getString("id_provincia") : "";
                    anexos[2][2] = canton = res.getString("id_ciudad") != null ? res.getString("id_ciudad") : "";
                    anexos[2][3] = parroquia = res.getString("id_parroquia") != null ? res.getString("id_parroquia") : "";
                    anexos[2][4] = email = res.getString("email") != null ? res.getString("email") : "";
                    anexos[2][5] = telefono = res.getString("telefono") != null ? res.getString("telefono") : "";
                    anexos[2][6] = movil_claro = res.getString("movil_claro") != null ? res.getString("movil_claro") : "";
                    anexos[2][7] = movil_movistar = res.getString("movil_movistar") != null ? res.getString("movil_movistar") : "";
                    anexos[2][8] = edad = res.getString("edad") != null ? res.getString("edad") : "";
                    anexos[2][9] = carne_conadis = res.getString("carne_conadis") != null ? res.getString("carne_conadis") : "";
                    anexos[2][10] = tipo_documento1 = res.getString("tipo_documento") != null ? res.getString("tipo_documento") : "04";
                    anexos[2][11] = id_sucursal;
                    anexos[2][14] = res.getString("observacion") != null ? res.getString("observacion") : "";;
                    res.close();
                }
                if (telefono.trim().compareTo("") != 0) {
                    movil += telefono + " / ";
                }
                if (movil_claro.trim().compareTo("") != 0) {
                    movil += movil_claro + " / ";
                }
                if (movil_movistar.trim().compareTo("") != 0) {
                    movil += movil_movistar + " / ";
                }
                if (movil.trim().compareTo("") != 0) {
                    movil = movil.substring(0, movil.length() - 2);
                }
                if (edad.trim().compareTo("") == 0) {
                    edad = "0";
                }
                if (Integer.parseInt(edad) >= 65 || carne_conadis.trim().compareTo("") != 0) {
                    especial = true;
                }
            } catch (Exception e) {
                System.out.println("datos del cliente");
            } finally {
                ObjCliente.cerrar();
            }

            try {
                anexos[1][0] = direccion_instalacion;
                anexos[1][1] = tipo_cliente;
                anexos[1][2] = tipo_instalacion;
                anexos[1][3] = plan;
                anexos[1][4] = velocidad_plan;
                anexos[1][5] = comparticion_plan;
                anexos[1][6] = costo_plan;
                anexos[1][7] = id_plan;
                anexos[1][8] = convenio_pago;
                anexos[1][9] = forma_pago;
                anexos[1][10] = id_provincia;
                anexos[1][11] = id_ciudad;
                anexos[1][12] = id_parroquia;
                anexos[1][13] = latitud;
                anexos[1][14] = longitud;
                anexos[1][18] = "-1";
                anexos[1][4] = (anexos[1][4].trim().compareTo("") == 0 ? "0" : "" + (Double.parseDouble(anexos[1][4]) / 1000));
                anexos[1][6] = (anexos[1][6].trim().compareTo("") == 0 ? "0" : "" + Math.round(Double.parseDouble(anexos[1][6])));
//                planes
                anexos[1][17] = null;
                anexos[1][15] = costo_plan;
                anexos[1][16] = plan;
            } catch (Exception e) {
                System.out.println("datos del ubicacion");
            }
            Ubicacion ObjUbicacion = new Ubicacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
            try {
                provincia = ObjUbicacion.getNombre(provincia.compareTo("") != 0 ? provincia : "-1");
                canton = ObjUbicacion.getNombre(canton.compareTo("") != 0 ? canton : "-1");
                parroquia = ObjUbicacion.getNombre(parroquia.compareTo("") != 0 ? parroquia : "-1");
                anexos[1][10] = ObjUbicacion.getNombre(anexos[1][10].compareTo("") != 0 ? anexos[1][10] : "-1");
                anexos[1][11] = ObjUbicacion.getNombre(anexos[1][11].compareTo("") != 0 ? anexos[1][11] : "-1");
                anexos[1][12] = ObjUbicacion.getNombre(anexos[1][12].compareTo("") != 0 ? anexos[1][12] : "-1");
                anexos[2][15] = ObjUbicacion.getNombre(anexos[2][1].compareTo("") != 0 ? anexos[2][1] : "-1");
                anexos[2][16] = ObjUbicacion.getNombre(anexos[2][2].compareTo("") != 0 ? anexos[2][2] : "-1");
                anexos[2][17] = ObjUbicacion.getNombre(anexos[2][3].compareTo("") != 0 ? anexos[2][3] : "-1");
            } catch (Exception e) {
                System.out.println("datos del ubicacion");
            } finally {
                ObjUbicacion.cerrar();
            }
//            promocion
            try {
                anexos[2][18] = tiempo_promocion;
                anexos[2][19] = detalle_promocion;
                anexos[2][20] = id_promocion;

            } catch (Exception e) {
                System.out.println("datos del promociones");
            }
//            permisos
            String permisos_isp[][] = null;
            try {
                permisos_isp = Matriz.ResultSetAMatriz(ObjContrato.consulta("select codigo_permiso,nombre_permiso from tbl_permisos_isp where eliminado=false;"));
            } catch (Exception e) {
                System.out.println("datos del permisossp");
            }
            contrato_final = contrato_final.replaceAll("<<lugar>>", ciudad);
            contrato_final = contrato_final.replaceAll("<<fecha>>", Fecha.getFechaSolicitud(fecha_actual));
            contrato_final = contrato_final.replaceAll("<<rason_social>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
            contrato_final = contrato_final.replaceAll("<<cedula>>", cedula);
            contrato_final = contrato_final.replaceAll("<<email>>", email);
            contrato_final = contrato_final.replaceAll("<<movil>>", movil);
            contrato_final = contrato_final.replaceAll("<<direccion>>", direccion);
            contrato_final = contrato_final.replaceAll("<<parroquia>>", parroquia);
            contrato_final = contrato_final.replaceAll("<<canton>>", canton);
            contrato_final = contrato_final.replaceAll("<<ciudad>>", canton);
            contrato_final = contrato_final.replaceAll("<<provincia>>", provincia);
            contrato_final = contrato_final.replaceAll("<<siedad>>", (especial ? "X" : ""));
            contrato_final = contrato_final.replaceAll("<<noedad>>", (!especial ? "X" : ""));
            contrato_final = contrato_final.replaceAll("<<permanenciasi>>", (anexos[2][18].trim().compareTo("") != 0 ? "X" : ""));
            contrato_final = contrato_final.replaceAll("<<permanenciano>>", (anexos[2][18].trim().compareTo("") == 0 ? "X" : ""));
            contrato_final = contrato_final.replaceAll("<<direccion_instalacion>>", direccion_instalacion);
            String permisos_isp1 = "";
            if (permisos_isp != null) {
                for (int i = 0; i < permisos_isp.length; i++) {
                    permisos_isp1 += permisos_isp[i][1] + "                  " + (permisos_isp[i][0].compareTo(tipo_cliente) == 0 ? "X" : "") + "  \n";
                }
            }
            contrato_final = contrato_final.replaceAll("<<permisos_isp>>", permisos_isp1);
            buro_credito = buro_credito.replaceAll("<<rason_social>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
            buro_credito = buro_credito.replaceAll("<<cedula>>", cedula);
            buro_credito = buro_credito.replace("<<tipo_documento>>", tipo_documento[Matriz.enMatriz(tipo_documento, tipo_documento1, 0)][1]);
            anexos[2][12] = tipo_documento[Matriz.enMatriz(tipo_documento, tipo_documento1, 0)][1];
            anexos[2][13] = movil;
            anexos[0][0] = contrato_final;
            anexos[0][3] = buro_credito;
            anexos[0][4] = ciudad;
            anexos[0][5] = fecha_actual;
            anexos[0][6] = cedula;
            anexos[0][7] = cliente;
            anexos[0][8] = representante;
        } catch (Exception e) {

        }
        return anexos;
    }
 public String Generarcontrato(String id_sucursal, String ruc, String razon_social, String representante, String ruc_representante, String id_cliente,
            String direccion_instalacion, String tipo_cliente, String tipo_instalacion, String plan, String velocidad_plan, String comparticion_plan,
            String costo_plan, String id_plan, String convenio_pago, String forma_pago, String id_provincia, String id_ciudad, String id_parroquia,
            String latitud, String longitud, String tiempo_promocion, String detalle_promocion, String id_promocion, File fichero, byte[] croquis) {
        String nombre_fichero = null;
        try {
            croquis_instalacion = croquis;
            String firmas[] = null;
            String[][] contrato = this.Modelo(id_sucursal, ruc, razon_social, representante, ruc_representante, id_cliente,
                    direccion_instalacion, tipo_cliente, tipo_instalacion, plan, velocidad_plan, comparticion_plan,
                    costo_plan, id_plan, convenio_pago, forma_pago, id_provincia, id_ciudad, id_parroquia,
                    latitud, longitud, tiempo_promocion, detalle_promocion, id_promocion);
            firmas = this.Firmas(contrato[0][6], contrato[0][7], contrato[0][8]);
            PdfCotrato objPdfCotrato = new PdfCotrato();
            nombre_fichero = objPdfCotrato.imprimir(contrato, firmas, fichero);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            nombre_fichero = null;
        }
        return nombre_fichero;

    }*/

}
