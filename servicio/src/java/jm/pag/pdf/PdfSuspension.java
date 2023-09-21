/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.pag.clas.Configuracion;
import jm.pag.clas.Documento;
import jm.pag.clas.Instalacion;
import jm.web.Addons;
import jm.web.Cadena;
import jm.web.Fecha;

/**
 *
 * @author wilso
 */
public class PdfSuspension extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    private String _ip_documental = null;
    private int _puerto_documental = 5432;
    private String _db_documental = null;
    private String _usuario_documental = null;
    private String _clave_documental = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
        this._ip_documental = config.getServletContext().getInitParameter("_IP_DOCUMENTAL");
        this._puerto_documental = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO_DOCUMENTAL"));
        this._db_documental = config.getServletContext().getInitParameter("_DB_DOCUMENTAL");
        this._usuario_documental = config.getServletContext().getInitParameter("_USUARIO_DOCUMENTAL");
        this._clave_documental = config.getServletContext().getInitParameter("_CLAVE_DOCUMENTAL");
        this._dir = config.getServletContext().getInitParameter("_DIR");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        String id = request.getParameter("id");
        Configuracion ObjConfiguracion = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String rep_nombre = ObjConfiguracion.getValor("rep_nombre");
        String rep_cargo = ObjConfiguracion.getValor("rep_cargo");
        String cargo_simple[] = rep_cargo.split(" ");
        ObjConfiguracion.cerrar();
        Instalacion ObjInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        ResultSet rs = ObjInstalacion.getSuspencionInstalacion(id);
        ObjInstalacion.cerrar();
        String cliente = "";
        String ruc = "";
        String plan = "";
        String motivo = "";
        String ip = "";
        String num_cuenta = "";
        String provedor = "";
        String tipo = "";
        String fecha_inicio = "";
        String fecha_solicitud = "";
        int tiempo = 0;
        try {
            if (rs.next()) {
                cliente = (rs.getString("razon_social") != null ? rs.getString("razon_social") : "");
                ruc = (rs.getString("ruc") != null ? rs.getString("ruc") : "");
                plan = (rs.getString("plan") != null ? rs.getString("plan") : "");
                motivo = (rs.getString("observacion_orden_trabajo") != null ? rs.getString("observacion_orden_trabajo") : "");
                ip = (rs.getString("ip") != null ? rs.getString("ip") : "");
                num_cuenta = (rs.getString("num_cuenta") != null ? rs.getString("num_cuenta") : "");
                tipo = (rs.getString("tipo") != null ? rs.getString("tipo") : "");
                fecha_inicio = (rs.getString("fecha_inicio") != null ? rs.getString("fecha_inicio") : "");
                tiempo = (rs.getString("tiempo") != null ? rs.getInt("tiempo") : 0);
                fecha_solicitud = (rs.getString("fecha_solicitud") != null ? rs.getString("fecha_solicitud") : "");
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        Documento ObjDocumento = new Documento(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String documento = ObjDocumento.getDocumentoTexto(tipo.compareTo("t") == 0 ? "s" : "d");
        ObjDocumento.cerrar();
        documento = documento.replace("<<REPRESENTANTE>>", rep_nombre);
        documento = documento.replace("<<CARGO>>", rep_cargo.toUpperCase());
        documento = documento.replace("<<CARGO_SIMPLE>>", Cadena.capital(cargo_simple[0]));
        documento = documento.replace("<<CLIENTE>>", cliente);
        documento = documento.replace("<<CLIENTE_FIRMA>>", Cadena.capital(cliente));
        documento = documento.replace("<<CEDULA>>", ruc);
        documento = documento.replace("<<TIEMPO>>", "EL/LOS MES/MESES DE: " + Fecha.getTxtMeses(fecha_inicio, tiempo + 1));
        documento = documento.replace("<<PLAN>>", plan);

        if (tipo.compareTo("d") == 0) {
            documento += "\n\n\rMOTIVO DE LA DESINTALACIÓN: "
                    + "\n\n" + motivo + ""
                    + "\n" + provedor;
        }
        documento = documento.replace("Â", "");
        if (num_cuenta.compareTo("") != 0) {
            documento += "\n\n\rNota.- el cliente tiene convenio e débito.";
        } else {
            documento += "\n";
        }
        documento += "\n\rIP: " + ip;

        Document document = new Document(PageSize.A4);
        document.setMargins(50, 30, 90, 0);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            writer.addJavaScript("this.print(false);", false);
            PdfPTable tbl_det = new PdfPTable(1);
            tbl_det.addCell(Addons.setCeldaPDF(Fecha.getFechaSolicitud(fecha_solicitud), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
            tbl_det.addCell(Addons.setFilaBlanco(1, 25));
            tbl_det.addCell(Addons.setCeldaPDF(documento, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
            document.add(tbl_det);
        } catch (IllegalStateException ie) {
            ie.printStackTrace();
        } catch (DocumentException de) {
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        document.close();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
