/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg.clas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author wilso
 */
public class Auditoria extends DataBase {

    public Auditoria(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public boolean setRegistro(String alias, String ip, String transaccion) {
        return this.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) "
                + "values('" + alias + "','" + ip + "','" + Fecha.getHora() + "','" + Fecha.getFecha("ISO") + "', '" + transaccion + "');");
    }

    public boolean setRegistro(HttpServletRequest request, String transaccion) {
        HttpSession sesion = request.getSession(true);
        String usuario = (String) sesion.getAttribute("usuario_proceso");
        return this.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) "
                + "values('" + usuario + "','" + request.getRemoteAddr() + "','" + Fecha.getHora() + "','" + Fecha.getFecha("ISO") + "', '" + transaccion + "');");
    }

}
