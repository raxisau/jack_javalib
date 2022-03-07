package com.jackbooted.tools.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;

public class Crypto {
    private static Logger log = Logger.getLogger ( Crypto.class );

    private static byte [] desKeyData = "F4BUL0U5".getBytes ();
    
    private static Crypto globalInstance = null;
    private static Object mutex = new Object ();
    
    public static Crypto global () {
        if ( globalInstance == null ) {
            synchronized ( mutex ) {
                if ( globalInstance == null ) globalInstance = new Crypto ();
            }
        }
        return globalInstance;
    }

    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;
    
    public Crypto () {
        try {
            SecretKey key = SecretKeyFactory.getInstance ( "DES" ).generateSecret ( new DESKeySpec ( desKeyData ) );
            
            encryptCipher = Cipher.getInstance ( "DES/ECB/PKCS5Padding" );
            encryptCipher.init ( Cipher.ENCRYPT_MODE, key );
            
            decryptCipher = Cipher.getInstance ( "DES/ECB/PKCS5Padding" );
            decryptCipher.init ( Cipher.DECRYPT_MODE, key );
        }
        catch ( Exception e ) {
            log.error ( "Encryption unavailable", e );
        }
    }
    public String encrypt ( String plainText ) {
        if ( encryptCipher == null ) {
            log.error ( "Encryption unavailable" );
            return plainText; 
        }
        else {
            try {
                return Base64.encodeBytes ( encryptCipher.doFinal ( plainText.getBytes () ) );
            }
            catch ( Exception e ) {
                log.error ( "Encryption unavailable", e );
                return plainText; 
            }
        }
    }

    public String decrypt ( String cipherText ) {
        if ( encryptCipher == null ) {
            log.error ( "Decryption unavailable" );
            return cipherText; 
        }
        else {
            try {
                return new String ( decryptCipher.doFinal ( Base64.decode ( cipherText ) ) );
            }
            catch ( Exception e ) {
                log.error ( "Decryption unavailable", e );
                return cipherText; 
            }
        }
    }
}
