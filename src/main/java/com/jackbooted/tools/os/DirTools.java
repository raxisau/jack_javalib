package com.jackbooted.tools.os;

/*
 * copyright (c) Dark Blue Sea
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;

/**
 *
 */
public class DirTools {
    /**
     * 
     */
    private static final String TWODOT = "..";
    
    /**
     * 
     */
    private static final String ONEDOT = ".";
    
    /**
     * 
     */
    private static boolean useOS = true;

    public static void setUseOS ( boolean useOS ) {
        DirTools.useOS = useOS;
    }

    /**
     * @param dir
     */
    public static void mkdir ( File dir ) {
        if ( ! dir.exists () ) {
            dir.mkdir ();
        }
    }

    /**
     * @param dir
     */
    public static boolean clearDirectory ( File dir ) {
        if ( ! dir.exists () ) {
            dir.mkdir ();
            return true;
        }
        
        File [] filesToDelete = dir.listFiles ( new FileFilter () {
            public boolean accept ( File file ) {
                return ! file.isDirectory ();
            }
        } );

        for ( File f : filesToDelete ) f.delete ();
        
        return true;
    }

    /**
     * @param sourceDir
     * @param destDir
     * @return
     * @throws IOException
     */
    public static boolean copyDirectory ( String sourceDir, String destDir ) throws IOException {
        if ( useOS ) {
            return 0 == Shell.command ( "cp -R " + sourceDir + " " + destDir );
        }

        mkdir ( new File ( destDir ) );

        File srcDir = new File ( sourceDir );
        File [] filesToCopy = srcDir.listFiles ( new FileFilter () {
            public boolean accept ( File file ) {
                String name = file.getName ();
                return ( ! ONEDOT.equals ( name ) && ! TWODOT.equals ( name ) );
            }
        } );

        for ( File src : filesToCopy ) {
            File dest = new File ( destDir + "/" + src.getName () );
            if ( src.isDirectory () ) {
                copyDirectory ( src.getPath (), dest.getPath () );
            }
            else {
                copyFile ( src, dest );
            }
        }
        return true;
    }

    /**
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    private static boolean copyFile ( File in, File out ) throws IOException {
        if ( useOS ) {
            return 0 == Shell.command ( "cp " + in + " " + out );
        }

        FileChannel inChannel = new FileInputStream ( in ).getChannel ();
        FileChannel outChannel = new FileOutputStream ( out ).getChannel ();

        try {
            long position = 0;
            long size = inChannel.size ();
            while ( position < size ) {
                position += outChannel.transferFrom ( inChannel, position, size - position );
            }
            return true;
        }
        catch ( IOException e ) {
            throw e;
        }
        finally {
            neaterClose ( inChannel );
            neaterClose ( outChannel );
        }
    }

    /**
     * @param channel
     */
    public static void neaterClose ( AbstractInterruptibleChannel channel ) {
        try {
            if ( channel != null ) {
                channel.close ();
            }
        }
        catch ( IOException e ) {
            // ignore
        }
    }
    
    /**
     * @param from
     * @param to
     * @return
     */
    public static boolean symLink ( String from, String to ) {
        return 0 == Shell.command ( "ln -s " + from + " " + to );
    }
    

    public static boolean rename ( String from, String to ) {
        if ( useOS ) {
            return 0 == Shell.command ( "mv " + from + " " + to );
        }
        
        return new File ( from ).renameTo ( new File ( to ) );
    }

    public static boolean deleteDir ( String dir ) {
        if ( useOS ) {
            return 0 == Shell.command ( "rm -Rf " + dir );
        }
        
        File f = new File ( dir );
        if ( ! clearDirectory ( f ) ) return false;
        return f.delete ();
    }

}
