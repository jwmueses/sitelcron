/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.ser;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.ser.clas.Plan;

/**
 *
 * @author PC-ON
 */
public class FrmPlanes extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        String id = (request.getParameter("id") != null ? request.getParameter("id") : "");
        Plan ObjPlan = new Plan(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            html.append("obj»div_contenedor^frm»");
            ResultSet rsplanes = ObjPlan.getPlanesTodos(id);
            html.append("<div class='content' style=''>");
            html.append("<div class='container-fluid'>");
            html.append("<div class='row'>");
            while (rsplanes.next()) {
                String id_plan = (rsplanes.getString("id_plan_servicio") != null ? rsplanes.getString("id_plan_servicio") : "-1");
                String plan = (rsplanes.getString("plan") != null ? rsplanes.getString("plan") : "");
                String velocidad = (rsplanes.getString("txtvelocidad") != null ? rsplanes.getString("txtvelocidad") : "");
                String txtcostot12 = (rsplanes.getString("txtcosto12") != null ? rsplanes.getString("txtcosto12") : "");
                String txtcostot14 = (rsplanes.getString("txtcosto14") != null ? rsplanes.getString("txtcosto14") : "");
                html.append("<div class='col-md-4'>");
                html.append("<div class='card card-profile'>");
                html.append("<div class='card-body'>");
                html.append("<h4 class='card-category' style='background-color: #ee7200;color: #FFFFFF; height: auto; padding:10px; font-weight: bold;'>" + plan + " " + velocidad + " Megas</h4>");
                html.append("<h3 class='card-title' style='background-color: #354396;color: #FFFFFF; height: auto; padding:10px; font-weight: bold; '>$ " + txtcostot12 + "</h3>");
                html.append("<h6 class='card-description' style='background-color: #97a1db;color: #212121; height: auto; padding:10px;'>Velocidad de descarga hasta " + velocidad + " Mbps</h5>");
                html.append("<h6 class='card-description' style='background-color: #ced4f9;color: #000000; height: auto; padding:10px;'>Velocidad de carga hasta " + velocidad + " Mbps</h5>");
                html.append("<a href='javascript:void(0);' class='btn btn-primary btn-round' style='background-color: #ee7200;color: #ffffff;' onclick=\"abrir_detalle_plan('" + id_plan + "')\" >Ver detalles</a>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
            }
            html.append("</div></div></div>");
            out.println(html.toString());
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            ObjPlan.cerrar();
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
