/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.pag.clas.Pagos;
import jm.seg.clas.Auditoria;
import jm.seg.clas.Cliente;
import jm.seg.clas.Usuario;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class FrmBuscarCliente extends HttpServlet {

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
        String txt = request.getParameter("ruc");
        String tok = (request.getParameter("tok") != null ? request.getParameter("tok") : "s");
        Pagos ObjPagos = new Pagos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Usuario ObjUsuario = new Usuario(this._ip, this._puerto, this._db, this._usuario, this._clave);
        Auditoria ObjAuditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            if (tok.compareTo("s") == 0) {
                int ok = ObjPagos.esCliente(txt);
                if (ok > 0) {
                    String id_instalacion = ObjUsuario.getClienteInstalacionRegistro(txt);
                    if (id_instalacion.compareTo("-1") != 0) {
                        ResultSet rs = ObjUsuario.getDatosCliente(id_instalacion);
                        if (rs.next()) {
                            id_instalacion = (rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "");
                            String establecimiento = (rs.getString("establecimiento") != null ? rs.getString("establecimiento") : "");
                            String id_cliente = (rs.getString("id_cliente") != null ? rs.getString("id_cliente") : "");
                            String razon_social = (rs.getString("razon_social") != null ? rs.getString("razon_social") : "");
                            String email = (rs.getString("email") != null ? rs.getString("email") : "");
                            if (email.trim().compareTo("") != 0) {
                                html.append("fun»existe('" + establecimiento + "','" + id_cliente + "','" + id_instalacion + "', '" + razon_social + "', '" + email + "');");
                            } else {
                                html.append("msg»Por favor contacte a soporte tecnico y actualize su direccion de correo electronico para poder acceder a este servicio.");
                            }
                        }
                    } else {
                        html.append("msg»Este numero de identificacion ya tiene una cuenta registrada");
                    }

                } else {
                    html.append("msg»No se ha encontrado servicios prestados a este número de identificacón.");
                }
                ObjAuditoria.setRegistro(request, "PORTAL WEB SE REALIZO BUSQUEDA DE INFORMACION RUC: " + txt);
            } else {
                int ok = ObjUsuario.esClienteSocioRuc(txt);
                if (ok == 0) {
                    ok = ObjPagos.esCliente(txt);
                    if (ok == 0) {
                        Cliente ObjCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);
                        try {
                            ok = ObjCliente.ExisteClienteRuc(txt);
                            if (ok > 0) {
                                ResultSet rs = ObjCliente.getClienteRuc(txt);
                                if (rs.next()) {
                                    String establecimiento = (rs.getString("establecimiento") != null ? rs.getString("establecimiento") : "");
                                    String razon_social = (rs.getString("razon_social") != null ? rs.getString("razon_social") : "");
                                    String telefono = (rs.getString("telefono") != null ? rs.getString("telefono") : "");
                                    String movil_claro = (rs.getString("movil_claro") != null ? rs.getString("movil_claro") : "");
                                    String email = (rs.getString("email") != null ? rs.getString("email") : "");
                                    String direccion = (rs.getString("direccion") != null ? rs.getString("direccion") : "");
                                    String id_cliente = (rs.getString("id_cliente") != null ? rs.getString("id_cliente") : "");
                                    html.append("fun»existe_nuevo('" + establecimiento + "','" + id_cliente + "','" + razon_social + "', '" + telefono + "', '" + movil_claro + "','" + email + "','" + direccion + "');");
                                    rs.close();
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("" + e.getMessage());
                        } finally {
                            ObjCliente.cerrar();
                        }
                    } else {
                        html.append("msg»Usted ya es tiene servicios como socio registrese aqui.^fun»limpiar('n');document.getElementById('defaultOpen').click();_('ruc').value='" + txt + "';buscarcliente();");
                    }
                } else {
                    html.append("msg»Este numero de identificacion ya tiene un cuenta.^fun»_('runu').value='';document.getElementById('btn_iniciar_sesion').click();");
                }
            }
        } catch (Exception e) {
            System.out.println("error al cargar el formulario");
        } finally {
            ObjPagos.cerrar();
            ObjUsuario.cerrar();
            ObjAuditoria.cerrar();
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
