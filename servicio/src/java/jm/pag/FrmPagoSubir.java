/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.pag.clas.Pagos;
import jm.ser.clas.Plan;
import jm.web.DatosDinamicos;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmPagoSubir extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    public void init(ServletConfig config) throws ServletException {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
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
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        String id = request.getParameter("id");
        String indice = request.getParameter("indice");
        String id_prefactura = request.getParameter("id_prefactura");
        String proviene = request.getParameter("proviene") != null ? request.getParameter("proviene") : "0";
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        Pagos ObjPagos = new Pagos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            ResultSet canales_pago = ObjPagos.getCanalPago();
            ResultSet banco_pago = ObjPagos.getBancos();
            html.append("obj»cmpdetallepago_html^foc»^fun»pag_formapago();^frm»");
            html.append("<form id='FrmPagoSubir' action=\"FrmPagoSubirGuardar\" method=\"post\" enctype=\"multipart/form-data\" target='procesaTransferencia' onsubmit=\"return pag_subirpagoguardar(this);\" autocomplete='off'>");
            html.append("<input type='hidden' id='id_instalacion' name='id_instalacion' value='" + id + "' />");
            html.append("<input type='hidden' id='indice' name='indice' value='" + indice + "'/>");
            html.append("<input type='hidden' id='id_prefactura' name='id_prefactura' value='" + id_prefactura + "'/>");
            html.append("<input type='hidden' id='proviene' name='proviene' value='" + proviene + "'/>");
            html.append("<div class='row'>");
            html.append("<div class='col-md-12'>");
            html.append("<div class=\"card\">");
            html.append(" <div class=\"card-body\">");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">FORMA DE PAGO</label>");
            html.append(DatosDinamicos.comboRequired(canales_pago, "canal", "", "pag_formapago()", "Seleccione"));
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ///
            html.append("<div class=\"row\" style=\"display:none;\" id='div_banco'>");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">¿A QUÉ CUENTA DE SAITEL REALIZO SU DEPÓSITO O TRANSFERENCIA?</label>");
            html.append(DatosDinamicos.comboRequired(banco_pago, "id_banco", "", "", "Seleccione"));
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            //
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">N° DE DOCUMENTO</label>");
            html.append("<input type=\"text\" id='documento'name='documento' class=\"form-control\" onkeypress=\"_evaluar(event, '0123456789-/');\" required>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<label class=\"bmd-label-floating\">ADJUNTAR EL COMPROBATE DE PAGO</label>");
            html.append("<div class=\"fileinput fileinput-new text-center\" data-provides=\"fileinput\">");
            html.append("<div class=\"fileinput-preview fileinput-exists thumbnail\"></div>");
            html.append("<div>");
            html.append("<span class=\"btn btn-sm btn-rose btn-round btn-file\">");
            html.append("<input type=\"file\" name=\"file\" id=\"file\" required>");
            html.append("<div class=\"ripple-container\"></div></span>");
            html.append("<a href=\"javascript:void(0);\" class=\"btn btn-sm btn-danger btn-round fileinput-exists\" data-dismiss=\"fileinput\"><i class=\"fa fa-times\"></i>Quitar</a>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
//
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<iframe name='procesaTransferencia' src='#' style='width:0;height:0;border:0px solid #fff;'></iframe>");
            html.append("<button type=\"submit\" class=\"btn btn-primary pull-right\" id='btn_reportar' >REPORTAR</button>");
            html.append("</div>");
            html.append("</div>");
            /////
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</form>");

            out.println(html.toString());
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            ObjPagos.cerrar();
            out.close();
        }
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
