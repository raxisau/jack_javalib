package com.jackbooted.tools.time;

/*
 * copyright (c) Dark Blue Sea
 */

import org.apache.log4j.Logger;

/**
 *
 */
public class StopWatch {
    /**
     * 
     */
    private static Logger log = Logger.getLogger ( StopWatch.class );

    /**
     * 
     */
    private long startTime;

    /**
     * 
     */
    private String msg;

    /**
     * @param msg
     */
    public StopWatch ( String msg ) {
        this ( msg, false );
    }
    
    /**
     * @param msg
     * @param displayStart
     */
    public StopWatch ( String msg, boolean displayStart ) {
        this.msg = msg;
        startTime = begin ();
        if ( displayStart ) {
            log.debug ( msg );
        }
    }

    /**
     * @return
     */
    public String stop () {
        long delta = end ( startTime );
        String returnedMessage = msg + ':' + msToStr ( delta );
        log.info ( returnedMessage );
        return returnedMessage;
    }

    /**
     * @return
     */
    public static long begin () {
        return System.currentTimeMillis ();
    }

    /**
     * @param startTime
     * @return
     */
    public static long end ( long startTime ) {
        return System.currentTimeMillis () - startTime;
    }

    /**
     * @param delta
     * @return
     */
    public static String msToStr ( long delta ) {
        long ms = delta;
        long sec = (long) ( ms / 1000 ); ms %= 1000;
        long min = (long) ( sec / 60  ); sec %= 60;
        long hr  = (long) ( min / 60  ); min %= 60;
        long day = (long) ( hr  / 24  ); hr  %= 24;
        StringBuilder msg = new StringBuilder ();
        if ( day > 0 ) msg.append ( day ).append ( "day" ).append ( plural ( day ) ).append ( " " );
        if ( hr  > 0 ) msg.append ( hr  ).append ( "hr"  ).append ( plural ( hr  ) ).append ( " " );
        if ( min > 0 ) msg.append ( min ).append ( "min" ).append ( plural ( min ) ).append ( " " );
        if ( sec > 0 ) msg.append ( sec ).append ( "sec" ).append ( plural ( sec ) ).append ( " " );
        if ( ms  > 0 ) msg.append ( ms  ).append ( "ms."  );
        return msg.toString ();
    }

    /**
     * @param val
     * @return
     */
    private static String plural ( long val ) {
        return ( val == 1 ) ? "" : "s";
    }
}
