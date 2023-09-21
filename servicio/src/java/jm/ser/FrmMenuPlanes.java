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
public class FrmMenuPlanes extends HttpServlet {

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
        Plan ObjPlan = new Plan(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            html.append("obj»div_menu^foc»^fun»^frm»");

            html.append("<ul class='nav'>");
            html.append("<li class='nav-item  '>");
            html.append("<a class='nav-link' href='http://saitel.ec/'>");
            html.append("<i class='material-icons'>dashboard</i>");
            html.append("<p>Página Principal</p>");
            html.append("</a>");
            html.append("</li>");
            /////
            html.append("<li class='nav-item '>");
            html.append("<a class='nav-link' href='javascript:void(0);' onclick=\"cargarplanesfiltro('');\" >");
            html.append("<i class='material-icons'>done_outline</i>");
            html.append("<p style='font-size: 9px;' title='TODOS LOS PLANES'>TODOS LOS PLANES</p>");
            //
            ResultSet rs_planes = ObjPlan.getPlanesTipos();
            while (rs_planes.next()) {
                String tipo_plan = (rs_planes.getString("tipo_plan") != null ? rs_planes.getString("tipo_plan") : "");
                String tmp_tipo_plan1 = tipo_plan;
                tipo_plan = "PLANES " + tipo_plan;
                String tmp_tipo_plan = tipo_plan;
                html.append("<li class='nav-item '>");
                html.append("<a class='nav-link' href='javascript:void(0);' onclick=\"cargarplanesfiltro('" + tmp_tipo_plan1 + "');\" >");
                html.append("<i class='material-icons'>done_outline</i>");
                int l = tipo_plan.length();
                l = l > 25 ? 25 : l;
                html.append("<p style='font-size: 9px;' title='" + tmp_tipo_plan + "'>" + tipo_plan.substring(0, l) + "</p>");
            }
            /*
            html.append("<div class='dropdown-divider'></div>");
            html.append("<li class='nav-item '>");
            html.append("<a class='nav-link' href='Salir'>");
            html.append("<i class='material-icons'>location_ons</i>");
            html.append("<p>Cerrar Sesion</p>");
            html.append("</a>");
            html.append("</li>");*/
            html.append("</ul>");
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
