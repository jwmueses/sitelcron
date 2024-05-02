/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.gomax.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

/**
 * Created by adi on 3/7/18.
 */
public class JavaKeyStore {

    private KeyStore keyStore;

    private String keyStoreName;    // nombre del archivo del almacen de claves
    private String keyStoreType;    //  tipo del almacen de claves
    private String keyStorePassword;    // clave para el almacen de claves

    JavaKeyStore(String keyStoreType, String keyStorePassword, String keyStoreName) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        this.keyStoreName = keyStoreName;
        this.keyStoreType = keyStoreType;
        this.keyStorePassword = keyStorePassword;
    }

    void createEmptyKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        if(keyStoreType ==null || keyStoreType.isEmpty()){
            keyStoreType = KeyStore.getDefaultType();
        }
        keyStore = KeyStore.getInstance(keyStoreType);
        //load
        char[] pwdArray = keyStorePassword.toCharArray();
        keyStore.load(null, pwdArray);

        // Save the keyStore
        FileOutputStream fos = new FileOutputStream(keyStoreName);
        keyStore.store(fos, pwdArray);
        fos.close();
    }

    void loadKeyStore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        char[] pwdArray = keyStorePassword.toCharArray();
        FileInputStream fis = new FileInputStream(keyStoreName);
        keyStore.load(fis, pwdArray);
        fis.close();
    }

    void setEntry(String alias, KeyStore.SecretKeyEntry secretKeyEntry, KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        keyStore.setEntry(alias, secretKeyEntry, protectionParameter);
    }

    KeyStore.Entry getEntry(String alias) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePassword.toCharArray());
        return keyStore.getEntry(alias, protParam);
    }

    void setKeyEntry(String alias, PrivateKey privateKey, String keyPassword, Certificate[] certificateChain) throws KeyStoreException {
        keyStore.setKeyEntry(alias, privateKey, keyPassword.toCharArray(), certificateChain);
    }

    void setCertificateEntry(String alias, Certificate certificate) throws KeyStoreException {
        keyStore.setCertificateEntry(alias, certificate);
    }

    Certificate getCertificate(String alias) throws KeyStoreException {
        return keyStore.getCertificate(alias);
    }

    void deleteEntry(String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    void deleteKeyStore() throws KeyStoreException, IOException {
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            keyStore.deleteEntry(alias);
        }
        keyStore = null;
        
        Path keyStoreFile = Paths.get(keyStoreName);
        Files.delete(keyStoreFile);
    }

    KeyStore getKeyStore() {
        return this.keyStore;
    }

}