/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.lib;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 *
 * @author desarrollo
 */
public class Xml{
    
    private Document xml = null;
    private String error = "";
    private Element elemento_activo = null;
    
    public Xml ()
    {
    }
    
    public Xml (String rutaArchivo)
    {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
            //this.xml = db.newDocument();
            File archivoXml = new File(rutaArchivo);
	    this.xml = db.parse(archivoXml);
	    this.xml.getDocumentElement().normalize();
        }catch(Exception e) {
            this.error = e.getMessage();
        }
    }
    
    public void setArchivoXml (String response)
    {

        String[] byteValues = response.substring(1, response.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i=0, len=bytes.length; i<len; i++) {
           bytes[i] = Byte.parseByte(byteValues[i].trim());     
        }
        this.setByteXml(bytes);
        //String str = new String(bytes);
    }
    
    public void setByteXml (byte[] documentoXml)
    {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
            //this.xml = db.newDocument();
            InputStream myInputStream = new ByteArrayInputStream(documentoXml); 
            this.xml = db.parse(myInputStream);
	    this.xml.getDocumentElement().normalize();
        }catch(Exception e) {
            this.error = e.getMessage();
        }
    }
    
    
    
    
    
    
    
    /*  lectura  */
    
    
    public void limpiar()
    {
        this.xml = null;
        this.error = "";
        this.elemento_activo = null;
        this.crear();
    }
    
    
    public void SetXml (String cadenaXml)
    {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = dbf.newDocumentBuilder();  
	    this.xml = builder.parse( new InputSource( new StringReader( cadenaXml ) ) );
	    this.xml.getDocumentElement().normalize();
        }catch(Exception e) {
            this.error = e.getMessage();
        }
    }
    
    public int getNumNodos(String tag)
    {
        NodeList nodos = this.xml.getDocumentElement().getElementsByTagName(tag);
        return nodos.getLength();
    }
    
    public String getValor(String tag)
    {
        String valor = "";
        try{
            valor = this.xml.getElementsByTagName(tag).item(0).getTextContent();
        }catch(Exception e){
            this.error = e.getMessage();
        }
        return valor;
    }
    
    public String getValor(String tag, int i)
    {
        String valor = "";
        try{
            valor = this.xml.getElementsByTagName(tag).item(i).getTextContent();
        }catch(Exception e){
            this.error = e.getMessage();
        }
        return valor;
    }
    
    public String getValor(String tagInicio, String tag, int pos)
    {
        NodeList nodo = this.xml.getElementsByTagName(tagInicio).item(pos).getChildNodes();
        for(int i=0; i<nodo.getLength(); i++){
            Node nodoHijo = nodo.item(i);
            if(nodoHijo.getNodeType() == Node.ELEMENT_NODE){
                if(nodoHijo.getNodeName().compareTo(tag) == 0){
                    return nodoHijo.getTextContent();
                }
                this.getValor(nodoHijo.getNodeName(), tag, pos);
            }
        }
        return "0";
    }
    
    public String getAtributo(String tag, String atributo)
    {
        String atributo_valor = "";
        try{
            Element elemento = (Element)this.xml.getElementsByTagName(tag).item(0);
            atributo_valor = elemento.getAttribute(atributo);
        }catch(Exception e){
            this.error = e.getMessage();
        }
        return atributo_valor;
    }
    
    public String getAtributo(String tag, int i, String atributo)
    {
        String atributo_valor = "";
        try{
            Element elemento = (Element)this.xml.getElementsByTagName(tag).item(i);
            atributo_valor = elemento.getAttribute(atributo);
        }catch(Exception e){
            this.error = e.getMessage();
        }
        return atributo_valor;
    }
    
    public String getError()
    {
        return this.error;
    }
    
    
    
    
    
    
    /* escritura */

    
    
    
    
    public void crear()
    {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.xml = db.newDocument();
            this.xml.setXmlVersion("1.0");
        } catch (ParserConfigurationException e) {
            this.error = e.getMessage();
        }
    }
    
    public Element setInfoTributaria(String raiz, String version, String ambiente, String tipoEmision, String razonSocial, String nombreComercial, String ruc, String claveAcceso, 
            String codDoc, String estab, String ptoEmi, String secuencial, String dirMatriz)
    {
        Element aux = this.nuevoElementoRaiz(raiz);
        this.setAtributo("id", "comprobante");
        this.setAtributo("version", version);           //      "1.0.0" -  "1.1.0"
        
        this.nuevoElementoInsertar("infoTributaria");
        this.nuevoElementoInsertar("ambiente", ambiente);    // parametro    1=pruebas   2=produccion
        this.nuevoElementoInsertar("tipoEmision", tipoEmision);  // parametro    1=normal    2=emision por indisponibilidad del sistema
        this.nuevoElementoInsertar("razonSocial", razonSocial);  // mas 300
        this.nuevoElementoInsertar("nombreComercial", nombreComercial);   //  max 300
        this.nuevoElementoInsertar("ruc", ruc);
        this.nuevoElementoInsertar("claveAcceso", claveAcceso); // numero de 49 digitos
        this.nuevoElementoInsertar("codDoc", codDoc);   // 01=factura   (02=nota de venta    03=liquidaciones)   04=nota de credito  05=nota de debitos  06=guia de remision    07=retencion
        this.nuevoElementoInsertar("estab", estab);     // long=3
        this.nuevoElementoInsertar("ptoEmi", ptoEmi);   // long=3
        this.nuevoElementoInsertar("secuencial", secuencial);   // long=9
        this.nuevoElementoInsertar("dirMatriz", dirMatriz); //  max 300
        
        this.elemento_activo = aux;
        return this.elemento_activo;
    }
    public Element nuevoElementoRaiz(String nombre)
    {
        this.elemento_activo = this.xml.createElement(nombre);
        this.xml.appendChild(this.elemento_activo);
        return this.elemento_activo;
    }
    public Element nuevoElemento(String nombre)
    {
        this.elemento_activo = this.xml.createElement(nombre);
        return this.elemento_activo;
    }
    public void nuevoElemento(Element elemento, String nombre, boolean set_elemento_padre)
    {
        this.elemento_activo = elemento;
        Element aux = this.xml.createElement(nombre);
        this.elemento_activo.appendChild(aux);
        if(!set_elemento_padre){
            this.elemento_activo = aux;
        }
    }
    public void nuevoElemento(Element elemento, String nombre, String valor, boolean set_elemento_padre)
    {
        this.elemento_activo = elemento;
        Element aux = this.xml.createElement(nombre);
        Text texto = this.xml.createTextNode(valor);
        aux.appendChild(texto);
        this.elemento_activo.appendChild(aux);
        if(!set_elemento_padre){
            this.elemento_activo = aux;
        }
    }
    public void nuevoElementoInsertar(String nombre)
    {
        if(this.elemento_activo!=null){
            Element aux = this.xml.createElement(nombre);
            this.elemento_activo.appendChild(aux);
            this.elemento_activo = aux;
        }
    }
    public void nuevoElementoInsertar(String nombre, String valor)
    {
        if(this.elemento_activo!=null){
            Element aux = this.xml.createElement(nombre);
            Text texto = this.xml.createTextNode(valor);
            aux.appendChild(texto);
            this.elemento_activo.appendChild(aux);
        }
    }
    
    public void setAtributo(String atributo, String valor)
    {
        this.elemento_activo.setAttribute(atributo, valor);
    }
    
    protected StringWriter formatear()
    {
        try {
            TransformerFactory transFact = TransformerFactory.newInstance();
            
            transFact.setAttribute("indent-number", new Integer(3));
            Transformer trans = transFact.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            DOMSource domSource = new DOMSource(this.xml);
            trans.transform(domSource, sr);
            
            return sw;
	} catch(Exception e) {
            this.error = e.getMessage();
	}
        return null;
    }
    
    public boolean salvar(String nombreArchivo)
    {
        try {
            FileWriter fw = new FileWriter(nombreArchivo);
            PrintWriter writer = new PrintWriter(fw);
            writer.print(this.formatear().toString());
            writer.close();
            return true;
	} catch (IOException e) {
            this.error = e.getMessage();
	}
        return false;
    }
    
    public Element getElementoActivo()
    {
        return this.elemento_activo;
    }
    
    public void setElementoActivo(Element elemento)
    {
        this.elemento_activo = elemento;
    }
    
    public String getXml()
    {
        return this.formatear().toString();
    }
    

}
