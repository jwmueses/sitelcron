/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;
import java.sql.ResultSet;

public class Iva extends DataBase{
    public Iva(){
        super();
    }
    public Iva(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public String getCodigoIva(String porcentajeIva)
    {
        String codigo = "2";
        try{
            ResultSet rs = this.consulta("SELECT codigo from tbl_iva where porcentaje="+porcentajeIva);
            if(rs.next()){
                codigo = rs.getString(1)!=null ? rs.getString(1) : "2";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return codigo;
    }
    
}
