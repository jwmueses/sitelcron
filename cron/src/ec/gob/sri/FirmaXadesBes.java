/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.gob.sri;

import org.w3c.dom.Document;
 
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
//import es.mityc.firmaJava.role.SimpleClaimedRole;
import es.mityc.javasign.EnumFormatoFirma;
import es.mityc.javasign.xml.refs.AllXMLToSign;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;
 
public class FirmaXadesBes extends GenericXMLSignature {

    private String RESOURCE_TO_SIGN = "";

    //Fichero donde se desea guardar la firma
    private String SIGN_FILE_NAME = "";

    public FirmaXadesBes(String certificado, String clave, String recursoXml, String ruta_salida, String archivoSalida)
    {
        super(certificado, clave, ruta_salida);
        this.RESOURCE_TO_SIGN = recursoXml;
        this.SIGN_FILE_NAME = archivoSalida;
    }
    
    @Override
    protected DataToSign createDataToSign() {
        DataToSign dataToSign = new DataToSign();
        dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
        dataToSign.setEsquema(XAdESSchemas.XAdES_132);
        dataToSign.setXMLEncoding("UTF-8");
        // Se añade un rol de firma
        //dataToSign.addClaimedRol(new SimpleClaimedRole("Rol de firma"));
        dataToSign.setEnveloped(true);
        dataToSign.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "contenido comprobante", null, "text/xml", null));
        dataToSign.setParentSignNode("comprobante");
        Document docToSign = getDocument(RESOURCE_TO_SIGN);
        dataToSign.setDocument(docToSign);
        return dataToSign;
    }
  
    @Override
    protected String getSignatureFileName() {
        return SIGN_FILE_NAME;
    }
}

