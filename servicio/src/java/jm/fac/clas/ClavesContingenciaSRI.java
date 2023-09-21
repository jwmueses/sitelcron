/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.fac.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author wilso
 */
public class ClavesContingenciaSRI extends DataBase {

    long clave_contingencia = 0;

    public ClavesContingenciaSRI(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public String getSigClave(String fecha, String tipoComprobante, String ruc, String tipoAmb, String tipoEmis) {
        String axFecha = "";
        if (fecha.indexOf("/") > 0) {
            axFecha = fecha.replace("/", "");
        } else {
            String vec[] = fecha.split("-");
            axFecha = vec[2] + vec[1] + vec[0];
        }

        String claveContingencia = "";
        try {
            ResultSet res = this.consulta("SELECT clave FROM tbl_clave_contingencia where consumida=false and ambiente='" + tipoAmb + "' LIMIT 1 OFFSET 0;");
            if (res.next()) {
                claveContingencia = (res.getString(1) != null) ? res.getString(1) : "";
                res.close();
            }
            this.ejecutar("update tbl_clave_contingencia set consumida=true where clave='" + claveContingencia + "' and ambiente='" + tipoAmb + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.clave_contingencia = Long.parseLong(claveContingencia);

        String clave = axFecha + tipoComprobante + ruc + tipoAmb + claveContingencia + tipoEmis;
        clave += this.getDigitoVerificador(clave);
        return clave;
    }

    /*public boolean setConsumida(String clave, String ambiente)
    {
        String codigo_numerico = clave.substring(24, 47);
        return this.ejecutar("update tbl_clave_contingencia set consumida=true where clave='"+codigo_numerico+"' and ambiente='"+ambiente+"'");
    }*/
    public String getClaveContingencia() {
        return String.valueOf(this.clave_contingencia);
    }

    public int getDigitoVerificador(String digitos) {
        int digito = 0;
        int x = 2;
        char vec[] = digitos.toCharArray();
        //int mul[] = new int[vec.length];
        int suma = 0;
        for (int i = 47; i >= 0; i--) {
            if (x == 8) {
                x = 2;
            }
            //mul[i] = vec[i] * x;
            suma += (Integer.parseInt("" + vec[i]) * x);
            x++;
        }
        int mod = suma % 11;
        digito = 11 - mod;
        if (digito == 11) {
            digito = 0;
        }
        if (digito == 10) {
            digito = 1;
        }
        return digito;
    }

}
