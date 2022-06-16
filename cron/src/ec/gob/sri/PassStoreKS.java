/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.gob.sri;

import java.security.cert.X509Certificate;

import es.mityc.javasign.pkstore.IPassStoreKS;

/**
24   * <p>Permite automatizar el acceso a las contraseñas de los almacenes de certificados de testeo.</p>
25   * 
26   */
public class PassStoreKS implements IPassStoreKS {
	
    /** Contraseña de acceso al almacén. */
    private transient String password;

    /**
    33  	 * <p>Crea una instancia con la contraseña que se utilizará con el almacén relacionado.</p>
    34  	 * @param pass Contraseña del almacén
    35  	 */
    public PassStoreKS(final String pass) {
        this.password = new String(pass);
    }

    /**
    41  	 * <p>Devuelve la contraseña configurada para este almacén.</p>
    42  	 * @param certificate No se utiliza
    43  	 * @param alias no se utiliza
    44  	 * @return contraseña configurada para este almacén
    45  	 * @see es.mityc.javasign.pkstore.IPassStoreKS#getPassword(java.security.cert.X509Certificate, java.lang.String)
    46  	 */
    public char[] getPassword(final X509Certificate certificate, final String alias) {
        return password.toCharArray();
    }

}

