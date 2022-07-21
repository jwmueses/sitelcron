/**
* @version 1.0
* @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* FACTURAPYMES! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package ec.com.saitel.lib;
import javax.servlet.http.*;

/**
 *
 * @author Jorge
 */
public class Auditoria  extends DataBase{
    public Auditoria(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public boolean setRegistro(String alias, String ip, String transaccion)
    {
        return this.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
                "values('"+alias+"','"+ip+"','"+Fecha.getHora()+"','"+Fecha.getFecha("ISO")+"', '"+transaccion+"');");
    }
    
    public boolean setRegistro(HttpServletRequest request, String transaccion)
    {
        HttpSession sesion = request.getSession(true);
        String usuario = (String)sesion.getAttribute("usuario");
        return this.ejecutar("INSERT INTO tbl_auditoria(alias,ip_maquina,hora,fecha,transaccion) " +
                "values('"+usuario+"','"+request.getRemoteAddr()+"','"+Fecha.getHora()+"','"+Fecha.getFecha("ISO")+"', '"+transaccion+"');");
    }
}
