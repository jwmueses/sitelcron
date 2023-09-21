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
import jm.cont.clas.Sector;
import jm.cont.clas.Sucursal;
import jm.cont.clas.Ubicacion;
import jm.ser.clas.Plan;
import jm.web.DatosDinamicos;

/**
 *
 * @author wilso
 */
public class FrmConsultaContratar extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        String op = request.getParameter("op");
        try {
            if (op.compareTo("1") == 0) {
                Sucursal ObjSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
                Sector ObjSector = new Sector(this._ip, this._puerto, this._db, this._usuario, this._clave);
                String id_sucursal = request.getParameter("id_sucursal");
                String objeto = request.getParameter("obj");
                try {
                    String direccion = ObjSucursal.getDireccion(id_sucursal);
                    String ciudad = ObjSucursal.getCiudad(Integer.parseInt(id_sucursal));
                    ResultSet rssectores = ObjSector.getSectores(Integer.parseInt(id_sucursal));
                    direccion = "Ubicación: " + ciudad + "<br> Dirección: " + direccion;
                    html.append("obj»" + objeto + "^fun»^frm»");
                    html.append("<br><h6 class=\"card-category\">" + direccion.toUpperCase() + "</h6>");
                    html.append("<br>");
                    html.append("<div class=\"row\">");
                    html.append("<div class=\"col-md-3\">");
                    html.append("<h6 class=\"card-category\" style='margin-top:12px;'>SECTORES</h6>");
                    html.append("</div>");
                    html.append("<div class=\"col-md-9\">");
                    html.append(DatosDinamicos.comboRequired(rssectores, "id_sector", "", "con_informacionplanes('id_sucursal','id_sector');", "Seleccione.."));
                    html.append("</div>");
                    html.append("</div>");
                } catch (Exception e) {
                    System.out.println("error");
                } finally {
                    ObjSucursal.cerrar();
                    ObjSector.cerrar();
                }
            } else if (op.compareTo("2") == 0) {
                Sector ObjSector = new Sector(this._ip, this._puerto, this._db, this._usuario, this._clave);
                Plan ObjPlan = new Plan(this._ip, this._puerto, this._db, this._usuario, this._clave);
                try {
                    String objeto = request.getParameter("obj");
                    String id_sector = request.getParameter("id_sector");
                    String id_sucursal = request.getParameter("id_sucursal");
                    html.append("obj»" + objeto + "^frm»");
                    html.append("<div class=\"card\">");
                    html.append("<div class=\"card-header card-header-tabs card-header-primary\">");
                    html.append("<div class=\"nav-tabs-navigation\">");
                    html.append("<div class=\"nav-tabs-wrapper\">");
                    html.append("<span class=\"nav-tabs-title\">PLANES </span>");
                    html.append("<ul class=\"nav nav-tabs\" data-tabs=\"tabs\">");
                    ResultSet rstipoplan = ObjPlan.getPlanesTipos();
                    int i = 0;
                    String atributo = "active";
                    while (rstipoplan.next()) {
                        String tipo_plan = (rstipoplan.getString("tipo_plan") != null ? rstipoplan.getString("tipo_plan") : "");
                        html.append("<li class=\"nav-item\">");
                        html.append("<a class=\"nav-link " + (i == 0 ? atributo : "") + "\" href=\"#" + tipo_plan.trim() + "\" data-toggle=\"tab\">");
                        html.append(tipo_plan);
                        html.append("<div class=\"ripple-container\"></div>");
                        html.append("</a>");
                        html.append("</li>");
                        i++;
                    }
                    rstipoplan.beforeFirst();
                    html.append("</ul>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div>");
                    ///
                    i = 0;
                    html.append("<div class=\"tab-content\">");
                    while (rstipoplan.next()) {
                        String tipo_plan = (rstipoplan.getString("tipo_plan") != null ? rstipoplan.getString("tipo_plan") : "");
                        html.append("<div class=\"tab-pane " + (i == 0 ? atributo : "") + "\" id=\"" + tipo_plan.trim() + "\">");
                        //////
                        ResultSet rsplanes = ObjPlan.getPlanesTodos(tipo_plan, id_sector);
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
                            html.append("<a href='javascript:void(0);' class='btn btn-primary btn-round' style='background-color: #ee7200;color: #ffffff;' onclick=\"con_contratarplanilla('" + id_sucursal + "', '" + id_sector + "', '" + id_plan + "')\" >Contratar</a>");
                            html.append("</div>");
                            html.append("</div>");
                            html.append("</div>");
                        }
                        html.append("</div></div></div>");
                        out.println(html.toString());
                        ////
                        html.append("</div>");
                        i++;
                    };
                    html.append("</div>");
                    html.append("</div>");

                    html.append("</div>");
                } catch (Exception e) {
                    System.out.println("error");
                } finally {
                    ObjSector.cerrar();
                    ObjPlan.cerrar();
                }
            } else if (op.compareTo("3") == 0) {
                Ubicacion ObjUbicacion = new Ubicacion(this._ip, this._puerto, this._db, this._usuario, this._clave
                );
                try {
                    String objeto = request.getParameter("obj");
                    String id_combo = request.getParameter("comb");
                    String id_padre = request.getParameter("pdr");
                    int ancho = request.getParameter("an") != null ? Integer.valueOf(request.getParameter("an")) : 100;
                    String onChange = request.getParameter("onCh") != null ? request.getParameter("onCh") : "";
                    String funcion = request.getParameter("fun") != null ? request.getParameter("fun") : "";
                    ResultSet rslista = ObjUbicacion.getUbicaciones(id_padre);
                    html.append("obj»" + objeto + "^frm»" + DatosDinamicos.combo(rslista, id_combo, "", onChange, "") + "");
                    if (funcion.compareTo("") != 0 && funcion.compareTo("0") != 0) {
                        html.append("^fun»" + funcion + "");
                    }
                } finally {
                    ObjUbicacion.cerrar();
                }
            }
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
