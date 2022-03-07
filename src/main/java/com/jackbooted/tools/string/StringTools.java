package com.jackbooted.tools.string;

import java.util.Collection;

/**
 *
 */
public class StringTools {

    /**
     * @param pieces
     * @param glue
     * @return
     */
    public static String join ( Collection<String> pieces, String glue ) {
        StringBuilder buf = new StringBuilder ();
        for ( String s : pieces ) {
            buf.append ( s ).append ( glue );
        }
        int bufLength = buf.length ();
        return buf.delete ( bufLength - glue.length (), bufLength ).toString ();
    }
    
    /**
     * @param pieces
     * @param glue
     * @return
     */
    public static String join ( String [] pieces, String glue ) {
        StringBuilder buf = new StringBuilder ();
        for ( String s : pieces ) {
            buf.append ( s ).append ( glue );
        }
        int bufLength = buf.length ();
        return buf.delete ( bufLength - glue.length (), bufLength ).toString ();
    }

    /**
     * @param s
     * @return
     */
    public static String escapeHTML ( String s ) {
        StringBuilder sb = new StringBuilder ();
        int n = s.length ();
        for ( int i = 0; i < n; i++ ) {
            char c = s.charAt ( i );
            switch ( c ) {
            case '<':
                sb.append ( "&lt;" );
                break;
            case '>':
                sb.append ( "&gt;" );
                break;
            case '&':
                sb.append ( "&amp;" );
                break;
            case '"':
                sb.append ( "&quot;" );
                break;
            case ' ':
                sb.append ( "&nbsp;" );
                break;
            default:
                sb.append ( c );
                break;
            }
        }
        return sb.toString ();
    }

}
