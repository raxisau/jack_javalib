package com.jackbooted.tools.os;

/*
 * copyright (c) Dark Blue Sea
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class PHP {

    /**
     * @param input
     * @return
     */
    public static PHP.Element unserialize ( String input ) {
        return unserialize ( new ByteArrayInputStream ( input.getBytes () ) );
    }

    /**
     * @param input
     * @return
     */
    public static PHP.Element unserialize ( InputStream input ) {
        int type = neatRead ( input );
        neatSkip ( input, 1 );

        switch ( type ) {
        case 'i':
            return parseInt ( input );
        case 'd':
            return parseFloat ( input );
        case 'b':
            return parseBoolean ( input );
        case 's':
            return parseString ( input );
        case 'a':
            return parseArray ( input );
        case 'N':
            return new Element ();
        case 'O':
        default:
            throw new IllegalStateException ( "Encountered unknown type [" + type + "]" );
        }
    }

    /**
     * @param input
     * @return
     */
    private static Element parseArray ( InputStream input ) {
        int arrayLen = readLength ( input );
        neatSkip ( input, 1 ); // consume the {
        Element result = new Element ();
        for ( int i = 0; i < arrayLen; i++ ) {
            result.put ( unserialize ( input ).toString (), unserialize ( input ) );
        }
        neatSkip ( input, 1 ); // consume the }
        return result;
    }

    /**
     * @param input
     * @return
     */
    private static int readLength ( InputStream input ) {
        return Integer.valueOf ( readToDelimiter ( input, ':' ) );
    }

    /**
     * @param input
     * @return
     */
    private static Element parseString ( InputStream input ) {
        int strLen = readLength ( input );
        neatSkip ( input, 1 ); // consume the double quotes
        byte [] buf = new byte[strLen];
        try {
            input.read ( buf, 0, strLen );
        }
        catch ( IOException e ) {
            throw new IllegalStateException ( "Unexpected end of stream", e );
        }
        neatSkip ( input, 2 ); // consume the double quotes and consume the semi colon
        return new Element ( new String ( buf ) );
    }

    /**
     * @param input
     * @return
     */
    private static Element parseBoolean ( InputStream input ) {
        String value = readToDelimiter ( input );
        return new  Element ( value.equals ( "1" ) );
    }

    /**
     * @param input
     * @return
     */
    private static Element parseFloat ( InputStream input ) {
        return new Element ( Double.valueOf ( readToDelimiter ( input ) ) );
    }

    /**
     * @param input
     * @return
     */
    private static Element parseInt ( InputStream input ) {
        return new Element ( Integer.valueOf ( readToDelimiter ( input ) ) );
    }
    /**
     * @param input
     * @return
     */
    private static String readToDelimiter ( InputStream input ) {
        return readToDelimiter ( input, ';' );
    }

    /**
     * @param input
     * @param delim
     * @return
     */
    private static String readToDelimiter ( InputStream input, char delim ) {
        StringBuilder buf = new StringBuilder ();

        while ( true ) {
            int ch = neatRead ( input );
            if ( ch < 0 ) {
                throw new IllegalStateException ( "Unexpected end of stream" );
            }
            else if ( ch == delim ) {
                break;
            }
            else {
                buf.append ( (char)ch );
            }
        }
        return buf.toString ();
    }


    private static int neatRead ( InputStream input ) {
        try {
            return input.read ();
        }
        catch ( IOException e ) {
            throw new IllegalStateException ( "Unexpected end of stream", e );
        }
    }

    /**
     * @param input
     * @param n
     */
    private static void neatSkip ( InputStream input, int n ) {
        try {
            input.skip ( n );
        }
        catch ( IOException e ) {
            throw new IllegalStateException ( "Unexpected end of stream", e );
        }
    }

    /**
     *
     */
    public static class Element {
        private static final Element NULL = new Element ();
        
        Map<String, Element> arrayValue = null;
        String stringValue = null;
        Long longValue = null;
        Double doubleValue = null;
        Boolean booleanValue = null;
        
        public Element () {
        }

        public Element ( String value ) {
            stringValue = value;
        }

        public Element ( boolean value ) {
            booleanValue = value;
        }

        public Element ( Double value ) {
            doubleValue = value;
        }

        public Element ( Integer value ) {
            longValue = value.longValue ();
        }

        public void put ( String key, Element value ) {
            if ( arrayValue == null ) arrayValue = new LinkedHashMap<String, Element> ();
            arrayValue.put ( key, value );
        }
        
        public boolean isset () {
            return arrayValue != null || stringValue != null || longValue != null || doubleValue != null || booleanValue != null;
        }

        public Element get ( String key ) {
            if ( arrayValue == null ) return NULL;
            else {
                Element value = arrayValue.get ( key );
                return ( value == null ) ? NULL : value; 
            }
        }
        public Element get ( Integer key ) {
            return get ( key.toString () );
        }

        public String s () {
            return ( stringValue == null ) ? "" : stringValue;
        }
        public Boolean b () {
            return ( booleanValue == null ) ? null : booleanValue;
        }
        public Integer i () {
            return ( longValue == null ) ? null : longValue.intValue ();
        }
        public Long l () {
            return ( longValue == null ) ? null : longValue;
        }
        public Double d () {
            return ( doubleValue == null ) ? null : doubleValue;
        }

        public int size () {
            if ( arrayValue == null ) return -1;
            else {
                return arrayValue.size();
            }
        }

        public Object v () {
            if ( stringValue != null ) return stringValue;
            else if ( booleanValue != null ) return booleanValue;
            else if ( longValue != null ) return Integer.valueOf ( longValue.intValue () );
            else if ( doubleValue != null ) return doubleValue;
            else if ( arrayValue != null ) return arrayValue;
            else return NULL;
        }
        public String toString () {
            return v().toString ();
        }
    }
}
