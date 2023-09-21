/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.seg.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author ADDISOFT <addisoft.ec>
 */
public class Usuario extends DataBase {

    public Usuario(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getDatosCliente(String txt) {
        return this.consulta("select * from vta_instalacion vi where vi.id_instalacion='" + txt + "' and vi.estado_servicio not in ('t');");
    }

    public int esClienteSocioId(String txt) {
        int ok = 0;
        ResultSet rs = this.consulta("select count(*)as conteo from tbl_cliente_portal where id_cliente='" + txt + "';");
        try {
            if (rs.next()) {
                ok = rs.getString("conteo") != null ? rs.getInt("conteo") : 0;
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return ok;
    }

    public int esClienteSocioRuc(String txt) {
        int ok = 0;
        ResultSet rs = this.consulta("select count(cp.*)as conteo from tbl_cliente_portal as cp "
                + " inner join tbl_cliente  as c on c.id_cliente =cp.id_cliente "
                + " where c.ruc='" + txt + "';");
        try {
            if (rs.next()) {
                ok = rs.getString("conteo") != null ? rs.getInt("conteo") : 0;
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return ok;
    }

    public String setClienteSocio(String id_cliente, String id_instalacion, String email, String usuario, String clave, String codigo_enlace) {
        return this.insert("insert into tbl_cliente_portal (id_cliente,id_instalacion,email,usuario,clave,codigo_enlace)values('" + id_cliente + "','" + id_instalacion + "','" + email + "','" + usuario + "',md5('" + clave + "'),'" + codigo_enlace + "');");
    }

    public String setClienteSocio(String id_cliente, String id_instalacion, String email, String usuario, String clave, String codigo_enlace, String clave_temporal, String proviene) {
        return this.insert("insert into tbl_cliente_portal (id_cliente,id_instalacion,email,usuario,clave,codigo_enlace,clave_temporal,proviene)values('" + id_cliente + "','" + id_instalacion + "','" + email + "','" + usuario + "',md5('" + clave + "'),'" + codigo_enlace + "','" + clave_temporal + "','" + proviene + "');");
    }

    public boolean esActivaEnlace(String codigo, String pk) {
        int ok = 0;
        boolean oki = false;
        ResultSet rs = this.consulta("select get_duracion_fechas(now()::timestamp,(fecha_registro||' '||hora_registro)::timestamp,'HORAS')::int8 as horas from vta_cliente_portal where codigo_enlace='" + codigo + "' and id_cliente_portal='" + pk + "' and confirmado=0;");
        try {
            if (rs.next()) {
                ok = rs.getString("horas") != null ? rs.getInt("horas") : 0;
                rs.close();
                if (ok < 48) {
                    oki = true;
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return oki;
    }

    public int esEnlaceActivo(String codigo, String pk) {
        int ok = -1;
        ResultSet rs = this.consulta("select confirmado from vta_cliente_portal where codigo_enlace='" + codigo + "' and id_cliente_portal='" + pk + "';");
        try {
            if (rs.next()) {
                ok = rs.getString("confirmado") != null ? rs.getInt("confirmado") : -1;
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return ok;
    }

    public ResultSet getAccesoUsuario(String email, String clave) {
        return this.consulta("select * from vta_cliente_portal cp where cp.usuario='" + email + "' and cp.clave=md5('" + clave + "');");
    }

    public String getClienteInstalacionRegistro(String txt) {
        String id_instalacion = "-1";
        ResultSet rs = this.consulta("select vi.id_instalacion from vta_instalacion vi where vi.ruc='" + txt + "' and vi.estado_servicio not in ('t') and vi.id_cliente not in (select x1.id_cliente from tbl_cliente_portal as x1);");
        try {
            if (rs.next()) {
                id_instalacion = rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "-1";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return id_instalacion;
    }

    public String getCorreoCienteActual(String id_cliente) {
        String id_instalacion = "";
        ResultSet rs = this.consulta("select email from tbl_cliente where id_cliente='" + id_cliente + "';");
        try {
            if (rs.next()) {
                id_instalacion = rs.getString("email") != null ? rs.getString("email") : "";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return id_instalacion;
    }

    public String getCorreoCienteActualRuc(String txt) {
        String id_instalacion = "";
        ResultSet rs = this.consulta("select c.email from tbl_cliente_portal as cp "
                + " inner join tbl_cliente  as c on c.id_cliente =cp.id_cliente "
                + " where cp.usuario='" + txt + "';");
        try {
            if (rs.next()) {
                id_instalacion = rs.getString("email") != null ? rs.getString("email") : "";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return id_instalacion;
    }

    public String getCampoCliente(String txt, String campo) {
        String id_instalacion = "";
        ResultSet rs = this.consulta("select c.* from tbl_cliente_portal as cp "
                + " inner join tbl_cliente  as c on c.id_cliente =cp.id_cliente "
                + " where cp.usuario='" + txt + "';");
        try {
            if (rs.next()) {
                id_instalacion = rs.getString(campo) != null ? rs.getString(campo) : "";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return id_instalacion;
    }

    public String getCampoClientePortal(String txt, String campo) {
        String id_instalacion = "";
        ResultSet rs = this.consulta("select cp.* from tbl_cliente_portal as cp "
                + " inner join tbl_cliente  as c on c.id_cliente =cp.id_cliente "
                + " where cp.usuario='" + txt + "';");
        try {
            if (rs.next()) {
                id_instalacion = rs.getString(campo) != null ? rs.getString(campo) : "";
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return id_instalacion;
    }

    public boolean esValidaClaveAnterior(String id_cliente_portal, String clave) {
        int ok = 0;
        boolean oki = false;
        ResultSet rs = this.consulta(" select count(*)as conteo from tbl_cliente_portal where id_cliente_portal ='" + id_cliente_portal + "' and clave =md5('" + clave + "');");
        try {
            if (rs.next()) {
                ok = rs.getString("conteo") != null ? rs.getInt("conteo") : 0;
                rs.close();
                if (ok > 0) {
                    oki = true;
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return oki;
    }

    public boolean setCambioClave(String id_cliente_portal, String clave) {
        return this.ejecutar("update tbl_cliente_portal set clave =md5('" + clave + "'),clave_temporal=false where id_cliente_portal ='" + id_cliente_portal + "';");
    }
}
