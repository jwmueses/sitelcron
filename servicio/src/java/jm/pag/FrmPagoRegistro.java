package jm.pag;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmPagoRegistro extends HttpServlet {

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
        try {
            html.append("obj»div_contenedor^frm»");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"card\">");
            html.append("<div class=\"card-header card-header-primary\">");
            html.append("<h4 class=\"card-title\">Pagos Realizados</h4>");
            html.append("<p class=\"card-category\">Registre su pago realizado por cualquiera de nuestros canales</p>");
            html.append("</div><br>");
            html.append("<div class=\"card-body\">");
            html.append("<form  id='FrmPagoRegistro' action='FrmPagoInstalaciones' onsubmit=\"return pag_ConsultarInstalacion(this);\" autocomplete='off'> ");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"form-group\">");
            html.append("<label class=\"bmd-label-floating\">CEDULA - RUC - PASAPORTE </label>");
            html.append("<input type=\"text\" id='txt' name='txt' class=\"form-control\" >");
            html.append("</div>");
            html.append("</div>");
            html.append("<button type=\"submit\" class=\"btn btn-primary pull-right\" >CONSULTAR</button>");
            html.append("<div class=\"clearfix\"></div>");
            html.append("</form>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            /*cargo las instalaciones que tenga el cliente*/
            html.append("<div class=\"col-md-12\">");
            html.append("<div class=\"card\">");
            html.append("<div class=\"card-body\">");
            html.append("<div class=\"row\">");
            html.append("<div class=\"col-md-12\" id='div_instalaciones'>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            out.print(html);
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
