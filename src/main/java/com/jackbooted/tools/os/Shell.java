package com.jackbooted.tools.os;

/*
 * copyright (c) Dark Blue Sea
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.jackbooted.tools.thread.NeaterThread;


/**
 *
 */
public class Shell {
    /**
     * 
     */
    private static Logger log = Logger.getLogger ( Shell.class );

    /**
     * @param command
     * @return
     */
    public static int command ( String command ) {
        try {
            log.debug ( "ShellCmd: " + command );
            Process process = Runtime.getRuntime ().exec ( command );
            new StreamThread ( process.getInputStream (), "OUT" );
            new StreamThread ( process.getErrorStream (), "ERR" );
            process.waitFor ();
            return process.exitValue ();
        }
        catch ( Exception e ) {
            log.error ( e );
            return -1;
        }
    }
    
    /**
     * @param command
     * @return
     */
    public static String getCommand ( String command ) {
        try {
            log.debug ( "ShellCmd: " + command );
            Process process = Runtime.getRuntime ().exec ( command );
            StreamThread out = new StreamThread ( process.getInputStream (), "OUT" );
            StreamThread err = new StreamThread ( process.getErrorStream (), "ERR" );
            process.waitFor ();
            return out.toString () + err.toString ();
        }
        catch ( Exception e ) {
            log.error ( e );
            return "";
        }
    }

    /**
     *
     */
    private static class StreamThread extends NeaterThread {
        /**
         * 
         */
        private static Logger log = Logger.getLogger ( StreamThread.class );

        /**
         * 
         */
        private InputStream is;
        
        /**
         * 
         */
        private StringBuilder output = new StringBuilder ();

        /**
         * 
         */
        private String type;

        /**
         * @param is
         * @param type
         */
        public StreamThread ( InputStream is, String type ) {
            this.is = is;
            this.type = type + ">";
            setName ( this.type + "Consumer" );
            start ();
        }

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run () {
            BufferedReader br = null;
            try {
                br = new BufferedReader ( new InputStreamReader ( is ) );
                String line;
                while ( ( line = br.readLine () ) != null ) {
                    output.append ( line ).append ( "\n" );
                    log.debug ( line );
                }
            }
            catch ( IOException ioe ) {
                log.debug ( ioe );
            }
            finally {
                neatClose ( br );
            }
        }
        
        public String toString () {
            return output.toString ();
        }

    }
}

