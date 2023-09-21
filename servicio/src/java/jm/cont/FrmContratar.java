/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cont;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.cont.clas.Sucursal;
import jm.pag.clas.Pagos;
import jm.web.DatosDinamicos;
import jm.web.Utils;

/**
 *
 * @author wilso
 */
public class FrmContratar extends HttpServlet {

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
        String parametros_url = (String) sesion.getAttribute("parametros_url");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        String objeto = "obj»div_contenedor^frm»";
        String tipo = request.getParameter("tipo") != null ? request.getParameter("tipo") : "";
        Sucursal ObjSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            String id_sucursal = "";
            String id_sector = "";
            try {
                if (parametros_url.trim().compareTo("") != 0) {
                    if (parametros_url.indexOf("FrmContratar") > 0) {
                        String[] parametros = parametros_url.split("");
                    }
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            ResultSet rssucursales = ObjSucursal.getSucursales();
            html.append("<div class=\"card-header card-header-primary\">");
            html.append("<h4 class=\"card-title \">USTED PUEDE CONTRATAR NUESTRO SERVICIOS EN:</h4>");
            html.append("<p class=\"card-category\">" + DatosDinamicos.comboRequired(rssucursales, "id_sucursal", id_sucursal, "con_informacioncontratar('id_sucursal','id_sector');", "Seleccione..") + "</p>");
            html.append("<div id='datos_sucursal'></div>");
            html.append("</div>");
            html.append("<div class=\"card-body\" id='planes_sector'>");

            html.append("</div>");
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjSucursal.cerrar();
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
