package com.jackbooted.tools.thread;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

public class ConcurrentEvent<V> {
    private SynchronousQueue<V> queue = new SynchronousQueue<V> ( );
    private String threadGroupName = getClass ().getSimpleName ();

    ArrayList<EventConsumerProcessorThread> list = new ArrayList<EventConsumerProcessorThread> ();

    public ConcurrentEvent ( int size, Class<? extends ConcurrentEvent.Processor<V>> clazz ) throws InstantiationException, IllegalAccessException {
        for ( int i = 0; i < size; i++ ) {
            list.add ( new EventConsumerProcessorThread ( clazz.newInstance (), i ) );
        }
    }
    public ConcurrentEvent ( int size, ConcurrentEvent.Factory<V> factory ) {
        for ( int i = 0; i < size; i++ ) {
            list.add ( new EventConsumerProcessorThread ( factory.create (), i ) );
        }
    }

    public void shutDown () {
        for ( EventConsumerProcessorThread t : list )  t.shutDown ();
    }

    public void dispatch ( V val ) {
        try {
            queue.put ( val );
        }
        catch ( InterruptedException e ) {
            // Ignore
        }
    }
    
    class EventConsumerProcessorThread extends Thread {
        private ConcurrentEvent.Processor<V> handler;
        private boolean keepProcessing = true;
        private boolean isProcessing = false;

        public EventConsumerProcessorThread ( ConcurrentEvent.Processor<V> handler, int threadCount ) {
            this.handler = handler;
            this.setName ( threadGroupName + "-" + threadCount );
            this.start ();
        }

        public void run () {
            while ( keepProcessing ) {
                try {
                    V obj = queue.take ();
                    isProcessing = true;
                    handler.process ( obj );
                    isProcessing = false;
                }
                catch ( InterruptedException e ) {
                    // Ignore
                }
            }
        }

        public void shutDown () {
            keepProcessing = false;
            
            if ( ! isProcessing ) this.interrupt ();
            
            try {
                this.join ( 10 * 10000 );
            }
            catch ( InterruptedException e ) {
                this.interrupt ();
            }
            handler.shutDown ();
        }
    }
    public static interface Processor<V> {
        public void process ( V val );
        public void shutDown ();
    }
    public interface Factory<V> {
        public ConcurrentEvent.Processor<V> create ();
    }

}

