/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.pag;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.web.DatosDinamicos;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmReclamo extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        try {
            String tipo_reclamo[][] = {{"", "SELECCIONE.."}, {"1", "RECLAMOS POR POR CAPACIDAD DE CANAL (VELOCIDAD)"}, {"2", "TIEMPO PROMEDIO REPARACIÓN DE AVERIAS"}, {"3", "RECLAMOS POR FACTURAS"}};
            html.append("obj»cmpreclamo_html^foc»^fun»^frm»");
            html.append("<form id='FrmReclamo' action=\"FrmReclamoGuardar\" method=\"post\" enctype=\"multipart/form-data\" target='procesaTransferencia' onsubmit=\"return rec_guardareclamo(this);\" autocomplete='off'>");
            html.append("<input type='hidden' id='id_instalacion' name='id_instalacion' value='" + id + "' />");
            html.append("<input type='hidden' id='indice' name='indice' value='" + indice + "'/>");
            html.append("<div class='row'>");
            html.append("<div class='col-md-12'>");
            html.append("<div class=\"card\">");
            html.append(" <div class=\"card-body\">");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<h5 style=\"color:red; text-align:center\">Saitel desea escuchar a nuestros clientes, aquí podrás plantearnos tus problemas, hacernos consultas o sugerencias, que con gusto las atenderemos.</h5>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<label class=\"bmd-label-floating\">NÚMERO DE CONTACTO</label>");
            html.append("<input type=\"text\" id='contacto'name='contacto' class=\"form-control\" onkeypress=\"_numero(event);\" required>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">TIPO DE RECLAMO</label>");
            html.append(DatosDinamicos.comboRequired("tipo", "", tipo_reclamo, "rec_consultaropciones('div_encuenta',this.value);"));
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            ///
            html.append("<div class=\"row\" style=\"display:none;\" id='div_encuenta'>");
            html.append("</div>");
            html.append("<div class=\"row\" style=\"display:none;\" id='divimagen'>");
            html.append("<div class=\"col-md-12\">");
            html.append("<label class=\"bmd-label-floating\">ADJUNTE UNA IMAGEN DEL FALLO </label>");
            html.append("<div class=\"fileinput fileinput-new text-center\" data-provides=\"fileinput\">");
            html.append("<div class=\"fileinput-preview fileinput-exists thumbnail\"></div>");
            html.append("<div>");
            html.append("<span class=\"btn btn-sm btn-rose btn-round btn-file\">");
            html.append("<input type=\"file\" name=\"file\" id=\"file\">");
            html.append("<div class=\"ripple-container\"></div></span>");
            html.append("<a href=\"javascript:void(0);\" class=\"btn btn-sm btn-danger btn-round fileinput-exists\" data-dismiss=\"fileinput\"><i class=\"fa fa-times\"></i>Quitar</a>");
            html.append("</div>");
            html.append("</div>");
            html.append("<br><a href='http://saitel.ec/servicios/medidor-de-velocidad' target='_blank'>Medidor de Velocidad</a>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group bmd-form-group\">");
            html.append("<textarea id=\"descripcion\" name=\"descripcion\" cols=\"30\" rows=\"5\" placeholder=\"Descríbanos su problema por favor.\" class=\"form-control\" required></textarea>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            /////
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
