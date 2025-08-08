/*
 * Copyright (C) 2014 jorjoluiso
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ec.gob.sri.wsc;

/*import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
*/
/**
 *
 * @author jorjoluiso
 */
public class DirectorioConfiguracion {
/* 
    static String RutaArchivoGenerado = "/home/sistemas/Documents/fe/generados";
    static String RutaArchivoFirmado = "/home/sistemas/Documents/fe/firmados";
    static String RutaArchivoAutorizado = "/home/sistemas/Documents/fe/autorizados";
    static String RutaArchivoNoAutorizado = "/home/sistemas/Documents/fe/noautorizados";
*/   
    static String RutaArchivoGenerado = "/opt/lampp/htdocs/anexos/fe/generados";
    static String RutaArchivoFirmado = "/opt/lampp/htdocs/anexos/fe/firmados";
    static String RutaArchivoAutorizado = "/opt/lampp/htdocs/anexos/fe/autorizados";
    static String RutaArchivoNoAutorizado = "/opt/lampp/htdocs/anexos/fe/noautorizados";

    public static String getRutaArchivoGenerado() {
        /*try {
            PropertiesConfiguration config = new PropertiesConfiguration("Directorio.properties");
            if (config.getProperty("directorio.generado") == null) {
                config.setProperty("directorio.generado", "/app/quijotelu/generado");
                config.save();
            }
            RutaArchivoGenerado = (String) config.getProperty("directorio.generado");
            return RutaArchivoGenerado;
        } catch (ConfigurationException ex) {
            Logger.getLogger(DirectorioConfiguracion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;*/
        return RutaArchivoGenerado;
    }

    public static String getRutaArchivoFirmado() {
        /*try {
            PropertiesConfiguration config = new PropertiesConfiguration("Directorio.properties");
            if (config.getProperty("directorio.firmado") == null) {
                config.setProperty("directorio.firmado", "/app/quijotelu/firmado");
                config.save();
            }
            RutaArchivoFirmado = (String) config.getProperty("directorio.firmado");
            return RutaArchivoFirmado;
        } catch (ConfigurationException ex) {
            Logger.getLogger(DirectorioConfiguracion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;*/
        return RutaArchivoFirmado;
    }

    public static String getRutaArchivoAutorizado() {
        /*try {
            PropertiesConfiguration config = new PropertiesConfiguration("Directorio.properties");
            if (config.getProperty("directorio.autorizado") == null) {
                config.setProperty("directorio.autorizado", "/app/quijotelu/autorizado");
                config.save();
            }
            RutaArchivoAutorizado = (String) config.getProperty("directorio.autorizado");
            return RutaArchivoAutorizado;
        } catch (ConfigurationException ex) {
            Logger.getLogger(DirectorioConfiguracion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;*/
        return RutaArchivoAutorizado;
    }

    public static String getRutaArchivoNoAutorizado() {
        /*try {
            PropertiesConfiguration config = new PropertiesConfiguration("Directorio.properties");
            if (config.getProperty("directorio.noautorizado") == null) {
                config.setProperty("directorio.noautorizado", "/app/quijotelu/noautorizado");
                config.save();
            }
            RutaArchivoNoAutorizado = (String) config.getProperty("directorio.noautorizado");
            return RutaArchivoNoAutorizado;
        } catch (ConfigurationException ex) {
            Logger.getLogger(DirectorioConfiguracion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;*/
        return RutaArchivoNoAutorizado;
    }

}
