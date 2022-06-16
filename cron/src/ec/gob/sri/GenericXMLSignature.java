/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.gob.sri;

/**
 *
 * @author jorge
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
  
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
 
import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.javasign.pkstore.CertStoreException;
import es.mityc.javasign.pkstore.IPKStoreManager;
import es.mityc.javasign.pkstore.keystore.KSStore;
  

//Clase base que deberían extender los diferentes ejemplos para realizar firmas

public abstract class GenericXMLSignature {

    private String error = "";
            
            
    /**
    * Almacén PKCS12 con el que se desea realizar la firma
    */
    private String PKCS12_RESOURCE = "";

    /**
    * Constraseña de acceso a la clave privada del usuario
    */
    private String PKCS12_PASSWORD = "";

    /**
    * Directorio donde se almacenará el resultado de la firma
    */
    private String OUTPUT_DIRECTORY = "";

    
    public GenericXMLSignature(String certificado, String clave, String ruta_salida)
    {
        this.PKCS12_RESOURCE = certificado;
        this.PKCS12_PASSWORD = clave;
        this.OUTPUT_DIRECTORY = ruta_salida;
    }
    
    
    /**
    * Ejecución del ejemplo. La ejecución consistirá en la firma de los datos
    * creados por el método abstracto <code>createDataToSign</code> mediante el
    * certificado declarado en la constante <code>PKCS12_FILE</code>. El
    * resultado del proceso de firma será almacenado en un fichero XML en el
    * directorio correspondiente a la constante <code>OUTPUT_DIRECTORY</code>
    * del usuario bajo el nombre devuelto por el método abstracto
    * <code>getSignFileName</code>
    */
    public void execute() 
    {

        // Obtencion del gestor de claves
        IPKStoreManager storeManager = getPKStoreManager();
        if (storeManager == null) {
            System.err.println("El gestor de claves no se ha obtenido correctamente.");
            this.error = "El gestor de claves no se ha obtenido correctamente.";
            //return;
        }

        // Obtencion del certificado para firmar. Utilizaremos el primer
        // certificado del almacen.
        X509Certificate certificate = getFirstCertificate(storeManager);
        if (certificate == null) {
            System.err.println("No existe ningún certificado para firmar.");
            this.error = "No existe ningún certificado para firmar.";
            //return;
        }

        // Obtención de la clave privada asociada al certificado
        PrivateKey privateKey = null;
        try {
            privateKey = storeManager.getPrivateKey(certificate);
        } catch (CertStoreException e) {
            System.err.println("Error al acceder al almacén.");
            this.error = "Error al acceder al almacén.";
            //return;
        }

        // Obtención del provider encargado de las labores criptográficas
        Provider provider = storeManager.getProvider(certificate);

        /*
        * Creación del objeto que contiene tanto los datos a firmar como la
        * configuración del tipo de firma
        */
        DataToSign dataToSign = createDataToSign();

        /*
        * Creación del objeto encargado de realizar la firma
        */
        FirmaXML firma = new FirmaXML();

        // Firmamos el documento
        Document docSigned = null;
        try {
            Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
            docSigned = (Document) res[0];
        } catch (Exception ex) {
            System.err.println("Error realizando la firma");
            this.error = "Error realizando la firma";
            //return;
        }

        // Guardamos la firma a un fichero en el home del usuario2511201401109172885700110010010000439921234567818
        String filePath = OUTPUT_DIRECTORY + File.separatorChar + getSignatureFileName();
        saveDocumentToFile(docSigned, filePath);
        //System.out.println("Firma salvada en en: " + filePath);
    }

    /**
    151      * <p>
    152      * Crea el objeto DataToSign que contiene toda la información de la firma
    153      * que se desea realizar. Todas las implementaciones deberán proporcionar
    154      * una implementación de este método
    155      * </p>
    156      * 
    157      * @return El objeto DataToSign que contiene toda la información de la firma
    158      *         a realizar
    159      */
    protected abstract DataToSign createDataToSign();

    /**
    163      * <p>
    164      * Nombre del fichero donde se desea guardar la firma generada. Todas las
    165      * implementaciones deberán proporcionar este nombre.
    166      * </p>
    167      * 
    168      * @return El nombre donde se desea guardar la firma generada
    169      */
    protected abstract String getSignatureFileName();

    /**
    173      * <p>
    174      * Escribe el documento a un fichero.
    175      * </p>
    176      * 
    177      * @param document
    178      *            El documento a imprmir
    179      * @param pathfile
    180      *            El path del fichero donde se quiere escribir.
    181      */
    private void saveDocumentToFile(Document document, String pathfile) 
    {
        try {
            FileOutputStream fos = new FileOutputStream(pathfile);
            UtilidadTratarNodo.saveDocumentToOutputStream(document, fos, true);
        } catch (FileNotFoundException e) {
            System.err.println("Error al salvar el documento");
            this.error = e.getMessage();
            //System.exit(-1);
        }
    }

    /**
    194      * <p>
    195      * Escribe el documento a un fichero. Esta implementacion es insegura ya que
    196      * dependiendo del gestor de transformadas el contenido podría ser alterado,
    197      * con lo que el XML escrito no sería correcto desde el punto de vista de
    198      * validez de la firma.
    199      * </p>
    200      * 
    201      * @param document
    202      *            El documento a imprmir
    203      * @param pathfile
    204      *            El path del fichero donde se quiere escribir.
    205      */
    @SuppressWarnings("unused")
    private void saveDocumentToFileUnsafeMode(Document document, String pathfile) 
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            serializer.transform(new DOMSource(document), new StreamResult(new File(pathfile)));
        } catch (TransformerException e) {
            System.err.println("Error al salvar el documento");
            this.error = e.getMessage();
            //System.exit(-1);
        }
    }

    /**
    222      * <p>
    223      * Devuelve el <code>Document</code> correspondiente al
    224      * <code>resource</code> pasado como parámetro
    225      * </p>
    226      * 
    227      * @param resource
    228      *            El recurso que se desea obtener
    229      * @return El <code>Document</code> asociado al <code>resource</code>
    230      */
    protected Document getDocument(String resource) 
    {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            doc = dbf.newDocumentBuilder().parse(new java.io.FileInputStream(resource));
        } catch (ParserConfigurationException ex) {
            System.err.println("Error al parsear el documento");
            this.error = ex.getMessage();
            //System.exit(-1);
        } catch (SAXException ex) {
            System.err.println("Error al parsear el documento");
            this.error = ex.getMessage();
            //System.exit(-1);
        } catch (IOException ex) {
            System.err.println("Error al parsear el documento");
            this.error = ex.getMessage();
            //System.exit(-1);
        } catch (IllegalArgumentException ex) {
            System.err.println("Error al parsear el documento");
            this.error = ex.getMessage();
            //System.exit(-1);
        }
        return doc;
    }

    /**
    258      * <p>
    259      * Devuelve el contenido del documento XML
    260      * correspondiente al <code>resource</code> pasado como parámetro
    261      * </p> como un <code>String</code>
    262      * 
    263      * @param resource
    264      *            El recurso que se desea obtener
    265      * @return El contenido del documento XML como un <code>String</code>
    266      */
    protected String getDocumentAsString(String resource) 
    {
        Document doc = getDocument(resource);
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        StringWriter stringWriter = new StringWriter();
        try {
            serializer = tfactory.newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stringWriter));
        } catch (TransformerException e) {
            System.err.println("Error al imprimir el documento");
            this.error = e.getMessage();
            //System.exit(-1);
        }
        return stringWriter.toString();
    }

    /**
    285      * <p>
    286      * Devuelve el gestor de claves que se va a utilizar
    287      * </p>
    288      * 
    289      * @return El gestor de claves que se va a utilizar</p>
    290      */
    private IPKStoreManager getPKStoreManager() 
    {
        IPKStoreManager storeManager = null;
        try {
            KeyStore ks = java.security.KeyStore.getInstance("PKCS12");
            ks.load(new java.io.FileInputStream(PKCS12_RESOURCE), PKCS12_PASSWORD.toCharArray());
            storeManager = new KSStore(ks, new PassStoreKS(PKCS12_PASSWORD));
        } catch (KeyStoreException ex) {
            System.err.println("No se puede generar KeyStore PKCS12");
            this.error = ex.getMessage();
            //System.exit(-1);
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("No se puede generar KeyStore PKCS12");
            this.error = ex.getMessage();
            //System.exit(-1);
        } catch (CertificateException ex) {
            System.err.println("No se puede generar KeyStore PKCS12");
            this.error = ex.getMessage();
            //System.exit(-1);
        } catch (IOException ex) {
            System.err.println("No se puede generar KeyStore PKCS12");
            this.error = ex.getMessage();
            //System.exit(-1);
        }
        return storeManager;
    }

    /**
    318      * <p>
    319      * Recupera el primero de los certificados del almacén.
    320      * </p>
    321      * 
    322      * @param storeManager
    323      *            Interfaz de acceso al almacén
    324      * @return Primer certificado disponible en el almacén
    325      */
    private X509Certificate getFirstCertificate(final IPKStoreManager storeManager) 
    {
        List<X509Certificate> certs = null;
        try {
            certs = storeManager.getSignCertificates();
        } catch (CertStoreException ex) {
            System.err.println("Fallo obteniendo listado de certificados");
            this.error = "Fallo obteniendo listado de certificados";
            //System.exit(-1);
        }
        if ((certs == null) || (certs.size() == 0)) {
            System.err.println("Lista de certificados vacía");
            this.error = "Lista de certificados vacía";
            //System.exit(-1);
        }

        X509Certificate certificate = certs.get(0);
        return certificate;
    }

    public String getError()
    {
        return this.error;
    }
            
}
