/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.pag.clas.Pagos;
import jm.web.Fecha;
import jm.web.Utils;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmFiltro extends HttpServlet {

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
        HttpSession sesion = request.getSession(true);
        String id_cliente = (String) sesion.getAttribute("id_cliente");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        Pagos ObjPagos = new Pagos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String objeto = "";
        int op = Integer.parseInt(request.getParameter("op"));
        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH) + 1;
        String axMes = mes < 10 ? "0" + mes : String.valueOf(mes);
        String fecha_uno = Fecha.getAnio() + "-" + axMes + "-01";
        String fecha_actual = Fecha.getFecha("ISO");
        try {
            if (op == 1) {
                String fun = request.getParameter("fun");
                objeto = "obj»div_filtro^fun»" + fun + "^frm»";
                html.append("<input type='hidden' id='id_cliente' name='id_cliente' value='" + id_cliente + "' >");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-md-3\">");
                html.append("<div class=\"form-group bmd-form-group\">");
                html.append("<label class=\"bmd-label-floating\">DESDE</label>");
                html.append("<input type=\"date\" id='fi'name='fi' class=\"form-control\" value='" + fecha_uno + "' required>");
                html.append("</div>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<div class=\"form-group bmd-form-group\">");
                html.append("<label class=\"bmd-label-floating\">HASTA</label>");
                html.append("<input type=\"date\" id='ff'name='ff' class=\"form-control\" value='" + fecha_actual + "' required>");
                html.append("</div>");
                html.append("</div>");
                html.append("<div class=\"col-md-3\">");
                html.append("<button type=\"button\" class=\"btn btn-primary pull-right\" id='btn_filtro' onclick=\"" + fun + "\" >Filtrar</button>");
                html.append("</div>");
                html.append("</div>");
            } else {

            }
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjPagos.cerrar();
            out.print(Utils.putcardbody(objeto, html.toString()));
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
