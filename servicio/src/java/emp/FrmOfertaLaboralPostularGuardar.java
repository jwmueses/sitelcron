/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import jm.web.Archivo;
import jm.web.DataBase;
import jm.web.Fecha;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author wilso
 */
@WebServlet("/upload")
@MultipartConfig
public class FrmOfertaLaboralPostularGuardar extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    private String _dir = null;
    private String _ipdocumental = null;
    private int _puertodocumental = 5432;
    private String _dbdocumental = null;
    private String _usuariodocumental = null;
    private String _clavedocumental = null;

    public void init(ServletConfig config) throws ServletException {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
        this._dir = config.getServletContext().getInitParameter("_DIR");
        this._ipdocumental = config.getServletContext().getInitParameter("_IP_DOCUMENTAL");
        this._puertodocumental = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO_DOCUMENTAL"));
        this._dbdocumental = config.getServletContext().getInitParameter("_DB_DOCUMENTAL");
        this._usuariodocumental = config.getServletContext().getInitParameter("_USUARIO_DOCUMENTAL");
        this._clavedocumental = config.getServletContext().getInitParameter("_CLAVE_DOCUMENTAL");
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
        
        String idOfertaEmpleo = request.getParameter("idOfertaEmpleo");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String fecha_nacimiento = request.getParameter("fecha_nacimiento");
        String ciudad = request.getParameter("ciudad");
        String profesion = request.getParameter("profesion");
        String celular = request.getParameter("celular");
        String aspiracion = request.getParameter("aspiracion");
        String carta = request.getParameter("carta");
        
        
        StringBuilder html = new StringBuilder();
        DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Archivo objDocumental = new Archivo(_ipdocumental, _puertodocumental, _dbdocumental, _usuariodocumental, _clavedocumental);
        
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
            html.append("<div class='panel' style='width:90%'>");

            String pk = objDataBase.insert("insert into tbl_oferta_empleo_aplica(id_oferta_empleo, nombres, apellidos, correo_electronico, fecha_nacimiento, cuidad_residencia, profesion, numero_celular, aspiracion_salarial, carta_presentacion) values("+
                    idOfertaEmpleo+", '"+nombre+"', '"+apellido+"', '"+correo+"', '"+fecha_nacimiento+"', '"+ciudad+"', '"+profesion+"', '"+celular+"', '"+aspiracion+"', '"+carta+"')");
            if( pk.compareTo("-1") !=0 ) {
                Part filePart = request.getPart("hoja_vida");
                String nombre_imagen = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String nombre_nuevo = "hojaVida" + Fecha.getFecha("ISO") + "" + Fecha.getHora();
                nombre_nuevo = (nombre_nuevo.replaceAll("-", "").replaceAll(":", "")) + "." + FilenameUtils.getExtension(nombre_imagen);
                InputStream fileContent = filePart.getInputStream();
                File archivo_entrada = new File(this._dir, nombre_nuevo);
                FileUtils.copyInputStreamToFile(fileContent, archivo_entrada);
                if( objDocumental.setArchivoDocumental("tbl_oferta_empleo_aplica", pk, "hoja_vida", nombre_nuevo, archivo_entrada, "public", "db_isp") ){
                    html.append("<div class='H1'>Postulaci&oacute;n enviada satisfactoriamente</div>");
                } else {
                    objDataBase.ejecutar("delete from tbl_oferta_empleo_aplica where id_oferta_empleo_aplica="+pk);
                    html.append("<div class='H1'>Se ha producido un error al enviar la postulaci&oacute;n, int&eacute;ntelo m&aacute;s tardes</div>");
                }
            }else{
                objDataBase.ejecutar("delete from tbl_oferta_empleo_aplica where id_oferta_empleo_aplica="+pk);
                html.append("<div class='H1'>Se ha producido un error al enviar la postulaci&oacute;n, int&eacute;ntelo m&aacute;s tardes</div>");
            }
            html.append("</div>");
            html.append("</div>");
            
            html.append("</body>");
            html.append("</html>");
            
            out.print( html );
            
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            objDocumental.cerrar();
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
