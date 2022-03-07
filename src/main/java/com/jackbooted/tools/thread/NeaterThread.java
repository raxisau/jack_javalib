package com.jackbooted.tools.thread;

/*
 * copyright (c) Dark Blue Sea
 */

import java.io.IOException;
import java.io.Reader;

/**
 *
 */
public class NeaterThread extends Thread {
    /**
     * @param maxWaitTime
     */
    public void neatJoin ( long maxWaitTime ) {
        try {
            join ( maxWaitTime );
        }
        catch ( InterruptedException e ) {
            // ignore
        }
    }

    /**
     * @param shortSleep
     */
    public void neatSleep ( long shortSleep ) {
        try {
            Thread.sleep ( shortSleep );
        }
        catch ( InterruptedException e ) {
            // ignore
        }
    }

    /**
     * @param reader
     */
    protected void neatClose ( Reader reader ) {
        try {
            if ( reader!= null ) reader.close ();
        }
        catch ( IOException e ) {
            // ignore
        }
    }
}
