/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.web.DataBase;

/**
 *
 * @author wilso
 */
public class FrmOfertaLaboralPostular extends HttpServlet {

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
        
        String idOfertaEmpleo = request.getParameter("id");
        
        StringBuilder html = new StringBuilder();
        DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        
        try {
            html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            html.append("<head>");
            html.append("<title>SAITEL</title>");
//            html.append("<link href=\"img/favicon.ico\" type=\"image/x-icon\" rel=\"shortcut icon\" />");
            html.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"nucleo/estilo.css\">");
            html.append("<SCRIPT type=\"text/javascript\" src=\"js2.js\"></SCRIPT>");
            html.append("</head>");
            
            html.append("<body>");
            
            html.append("<div class='panel-top' style='text-align:center'>");
                html.append("<div class='H2'>Trabaja con nosotros</div>");
                html.append("<div class='H4'>¡Nos alegra que estés interesado en formar parte de nosotros!</div>");
            html.append("</div>");
            
            html.append("<img id='logo' src='./img/logo_saitel.png' />");
            
            html.append("<div class='grid-container'>");

            ResultSet rs = objDataBase.consulta("select O.*, S.regional,S.ubicacion, S.sucursal from vta_oferta_empleo as O inner join tbl_sucursal as S on O.id_sucursal_oficina=S.id_sucursal where id_oferta_empleo = " + idOfertaEmpleo +
                    " union all " +
                    "select O.*, S.regional,S.ubicacion, SO.oficina as sucursal from vta_oferta_empleo as O inner join tbl_sucursal_oficina as SO on O.id_sucursal_oficina=SO.id_sucursal_oficina " +
                    "inner join tbl_sucursal as S on SO.id_sucursal = S.id_sucursal where id_oferta_empleo = " + idOfertaEmpleo);
            if( rs.next() ) {
                String regional = rs.getString("regional")!=null ? rs.getString("regional") : "";
                String ciudad = rs.getString("ciudad")!=null ? rs.getString("ciudad") : "";
                String direccion = rs.getString("ubicacion")!=null ? rs.getString("ubicacion") : "";
                String sucursal = rs.getString("sucursal")!=null ? rs.getString("sucursal") : "";
                String area_capacitacion = rs.getString("area_capacitacion")!=null ? rs.getString("area_capacitacion") : "";
                String cargo_solicitado = rs.getString("cargo_solicitado")!=null ? rs.getString("cargo_solicitado") : "";
                String conocimiento_cargo = rs.getString("conocimiento_cargo")!=null ? rs.getString("conocimiento_cargo") : "";
                String actividad_realizar = rs.getString("actividad_realizar")!=null ? rs.getString("actividad_realizar") : "";
                String area_experiencia = rs.getString("area_experiencia")!=null ? rs.getString("area_experiencia") : "";
                String txt_instruccion = rs.getString("txt_instruccion")!=null ? rs.getString("txt_instruccion") : "";
                String jornada_trabajo = rs.getString("jornada_trabajo")!=null ? rs.getString("jornada_trabajo") : "";
                String experiencia = rs.getString("experiencia")!=null ? rs.getString("experiencia") : "";
                String remuneracion = rs.getString("remuneracion")!=null ? rs.getString("remuneracion") : "";
                String informacion_adicional = rs.getString("informacion_adicional")!=null ? rs.getString("informacion_adicional") : "";
                
                html.append("<div class='panel' style='width:90%'>");
                
                    html.append("<div class='H1'>"+regional+": "+ciudad+"</div>");
                    
                    html.append("<div class='H5'>"+sucursal+": "+direccion+"</div>");
                    
                    html.append("<div class='H3'><hr />REQUIERE CONTRATAR<hr /></div>");
                    
                    
                    html.append("<table width='100%'>");
                        html.append("<tr><td>CARGO</td> <td>"+cargo_solicitado+"</td></tr>");
                        html.append("<tr><td>CONOCIMIENTOS</td> <td>"+conocimiento_cargo+"</td></tr>");
                        html.append("<tr><td>ACTIVIDAD A DESEMPEÑAR</td> <td>"+actividad_realizar+"</td></tr>");
                        html.append("<tr><td>AREA DE CAPACITACIÓN</td> <td>"+area_capacitacion+"</td></tr>");
                        html.append("<tr><td>AREA DE EXPERIENCIA</td> <td>"+area_experiencia+"</td></tr>");
                        html.append("<tr><td>INSTRUCCION</td> <td>"+txt_instruccion+"</td></tr>");
                        html.append("<tr><td>JORNADA DE TRABAJO</td> <td>"+jornada_trabajo+"</td></tr>");
                        html.append("<tr><td>EXPERIENCIA MÍNIMA (AÑOS)</td> <td>"+experiencia+"</td></tr>");
                        html.append("<tr><td>REMUNERACIÓN (USD)</td> <td>"+remuneracion+"</td></tr>");
                        html.append("<tr><td>INFORMACIÓN ADICIONAL</td> <td>"+informacion_adicional+"</td></tr>");
                    html.append("</table>");
                    
                    html.append("<div class='btn-oferta'><input class='boton' type='button' value='Postular' onclick=\"setFormulario("+idOfertaEmpleo+")\" /></div>");
                
                html.append("</div>");
                
                rs.close();
            }
            html.append("</div>");
            
            html.append("</body>");
            html.append("</html>");
            
            out.print( html );
            
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            objDataBase.cerrar();
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
