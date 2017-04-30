/*
 * Copyright (C) 2015 Francisco José Morero Peyrona. All Rights Reserved.
 *
 * GNU Classpath is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the free
 * Software Foundation; either version 3, or (at your option) any later version.
 *
 * This app is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this software; see the file COPYING.  If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.peyrona.commons;

import com.peyrona.commons.lang.ListenerWise;
import com.peyrona.commons.util.UtilCollections;
import com.peyrona.commons.util.UtilDebug;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * A simple FIFO in memory messaging system that dispatches received messages to
 * registered listeners.
 * <p>
 * This class is thread safe.
 *
 * @author peyrona
 * @param <T>
 */
public class DispatcherFIFO<T> extends ListenerWise<DispatcherFIFO.Listener>
{
    public interface Listener<T>
    {
        /**
         * Listener receives its instance of the Dispatcher, in case it wants to
         * reinsert the message (to provide another opportunity to process the
         * message).
         *
         * @param dispatcher The dispatcher that is delivering the message
         * @param message The message itself
         */
        void onMessage( DispatcherFIFO<T> dispatcher, T message );
    }

    //----------------------------------------------------------------------------//

    private final ConcurrentLinkedQueue<T> queue;
    private       ScheduledExecutorService excMsgMgr;
    private final int  nMaxStorage;
    private final long nInterval;

    //----------------------------------------------------------------------------//

    /**
     * Default constructor.
     *
     * Makes: this( 0 )
     */
    public DispatcherFIFO()
    {
        this( 0 );
    }

    /**
     * Constructor.
     *
     * @param maxStorage Maximum number of message stored pending to be
     *                   delivered. 0 == no limit.
     */
    public DispatcherFIFO( int maxStorage )
    {
        this( maxStorage, 50 );
    }

    /**
     * Constructor.
     *
     * @param maxStorage Maximum number of message stored pending to be
     *                   delivered. 0 == no limit.
     * @param interval How frequently (in millis) the queue will be inspected
     *                 and all pending messages sent.
     *                 When interval == 0, then messages are sent inmediately to
     *                 the listeners (no queue storage)
     */
    public DispatcherFIFO( int maxStorage, long interval )
    {
        this.nMaxStorage = Math.max( 0, maxStorage );
        this.nInterval   = Math.max( 0, interval );
        this.queue       = ((interval == 0) ? null : new ConcurrentLinkedQueue<>());
        this.excMsgMgr   = null;
    }

    //----------------------------------------------------------------------------//

    public void post( T message )
    {
        if( message == null )
        {
            throw new IllegalArgumentException( "Message can not be null" );
        }

        if( (nMaxStorage > 0) && (queue.size() >= nMaxStorage) )
        {
            throw new IllegalStateException( "Storage is full, message rejected: "+ message );
        }

        if( nInterval == 0 )
        {
            for( DispatcherFIFO.Listener<T> listener : DispatcherFIFO.this.getAllListeners() )
            {
                listener.onMessage( DispatcherFIFO.this, message );
            }
        }
        else
        {
            queue.add( message );
        }
    }

    public void post( List<T> messages )      // Must be a List: order is crucial
    {
        if( UtilCollections.isNotEmpty( messages ) )
        {
            for( T m : messages )
            {
                post( m );
            }
        }
    }

    public void post( final T message, final long delay )
    {
        if( delay <= 0 )
        {
            post( message );
        }
        else
        {
            Timer timer = new Timer();
            timer.schedule( new TimerTask()
                            {   @Override
                                public void run() { DispatcherFIFO.this.post( message ); }
                            },
                            delay );
        }
    }

    public void post( final List<T> messages, final long interval )       // Must be a List: order is normally crucial
    {
        new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                for( T msg : messages )
                {
                    try
                    {
                        DispatcherFIFO.this.post( msg );
                        TimeUnit.MILLISECONDS.sleep( interval );
                    }
                    catch( Exception exc )
                    {
                        break;
                    }
                }
            }
        }, getClass()+"::"+"post( messages, interval )" ).start();
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null
     * if this queue is empty.
     *
     * @return The head of this queue, or null if this queue is empty.
     */
    public T peek()
    {
        return queue.peek();
    }

    public synchronized void start()
    {
        if( (nInterval > 0) && (excMsgMgr == null) )
        {
            excMsgMgr = Executors.newSingleThreadScheduledExecutor();
            excMsgMgr.scheduleWithFixedDelay( new Deliverer(), 0, nInterval, TimeUnit.MILLISECONDS );
        }
    }

    public synchronized void stopNow()
    {
        stop( 0 );
    }

    public synchronized void stop( int nDelayInMillis )
    {
        if( (nInterval == 0) || (excMsgMgr == null) )
        {
            return;
        }

        try
        {
            if( nDelayInMillis > 0 )
            {
                excMsgMgr.shutdown();
                excMsgMgr.awaitTermination( nDelayInMillis, TimeUnit.MILLISECONDS );
            }
        }
        catch( InterruptedException ex )
        {
            // Nothing to do
        }
        finally
        {
            excMsgMgr.shutdownNow();
            excMsgMgr = null;
            queue.clear();
        }
    }

    //----------------------------------------------------------------------------//

    @Override
    protected void finalize() throws Throwable
    {
        stopNow();
        super.finalize();
    }

    //----------------------------------------------------------------------------//
    // INNER CLASS
    // Only one thread calls same instance: in fact only one instance of this
    // class is created.
    //----------------------------------------------------------------------------//
    private final class Deliverer extends Thread
    {
        Deliverer()
        {
            setName( DispatcherFIFO.class.getSimpleName() +"-"+ getClass().getSimpleName() +"-"+ hashCode() );
        }

        @Override
        public void run()
        {
            if( DispatcherFIFO.this.queue.isEmpty() )    // Only to accelerate (Iterator will not be created)
            {
                return;
            }

            for( Iterator<T> itera = DispatcherFIFO.this.queue.iterator(); itera.hasNext(); )
            {
                if( isInterrupted() )
                {
                    break;
                }

                T message = itera.next();
                            itera.remove();

                for( DispatcherFIFO.Listener<T> listener : DispatcherFIFO.this.getAllListeners() )
                {
                    try
                    {
                        listener.onMessage( DispatcherFIFO.this, message );
                    }
                    catch( Exception exc )     // Can't afford having an exception when dispatching
                    {
                        String msg = "Error invoking message.toString()";

                        try { msg = message.toString(); } catch( Exception e ) { /* Nothing to do */ }

                        UtilDebug.log( Level.SEVERE, exc, "Error while dispatching message: "+ msg );
                    }

                    if( isInterrupted() )
                    {
                        break;
                    }
                }
            }
        }
    }
}