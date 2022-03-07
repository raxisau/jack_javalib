package com.jackbooted.tools.security;

import static org.junit.Assert.*;

import org.junit.Test;

public class CryptoTest {

    @Test
    public void testMain () {
        Crypto crypto = new Crypto ();
        String plainText = "This is a test";
        String cipherText = crypto.encrypt ( plainText );
        assertNotSame ( plainText, cipherText );
        assertEquals ( "Should be the same", plainText, crypto.decrypt ( cipherText ) );
    }
}
