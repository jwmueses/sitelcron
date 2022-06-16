/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;

/**
 *
 * @author PC-ON
 */
public class Biometricos extends DataBase {

    public Biometricos(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getBiometricos() {
        return this.consulta("SELECT * FROM vta_biometricos;");
    }

    public ResultSet getBiometricos(String id) {
        return this.consulta("SELECT * FROM vta_biometricos where id_biometrico='" + id + "';");
    }

    public ResultSet getBiometricosActivos() {
        return this.consulta("SELECT * FROM vta_biometricos where activo='true'");
    }

    public ResultSet getBiometricosActivos(int id_sucursal) {
        return this.consulta("SELECT * FROM vta_biometricos where activo='true' and id_sucursal='" + id_sucursal + "'");
    }

    public boolean estaDuplicado(String nombre, String ip, String id) {
        ResultSet res = this.consulta("SELECT * FROM vta_biometricos where nombre_biometrico='" + nombre + "' and ip_biometrico='" + ip + "' and id_biometrico<>" + id + ";");
        if (this.getFilas(res) > 0) {
            return true;
        }
        try {
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String insertar(String nombre, String ip, String puerto, String clave, String usuario, String db, String activo, String id_sucursal) {
        String id_biometrico = this.insert("INSERT INTO tbl_biometricos(nombre_biometrico, ip_biometrico, puerto_biometrico, usuario_biometrico, clave_biometrico, db_biometrico,activo,id_sucursal)\n"
                + "	VALUES ('" + nombre + "', '" + ip + "', '" + puerto + "', '" + usuario + "', '" + clave + "', '" + db + "','" + activo + "','" + id_sucursal + "');");
        return id_biometrico;
    }

    public boolean actualizar(String nombre, String ip, String puerto, String clave, String usuario, String db, String id, String activo, String id_sucursal) {
        return this.ejecutar("UPDATE tbl_biometricos SET  nombre_biometrico='" + nombre + "', ip_biometrico='" + ip + "', puerto_biometrico='" + puerto + "', usuario_biometrico='" + usuario + "', clave_biometrico='" + clave + "', db_biometrico='" + db + "', activo='" + activo + "', id_sucursal='" + id_sucursal + "'\n"
                + "WHERE id_biometrico='" + id + "';");

    }
}
