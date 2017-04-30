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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple in memory messaging system that dispatches received messages to
 * registered listeners.
 * <p>
 * This class is inmutable and therefore thread safe.
 * <p>
 * CARE: the invocation of onMessage(...) method is done inside its own thread
 * and therefore listeners implementation must be thread-safe. It also implies
 * that order or delivering can not be guarrateed (it is not a FIFO queue).
 *
 * @author peyrona
 * @param <T>
 */
public class Dispatcher<T> extends ListenerWise<Dispatcher.Listener>
{
    public interface Listener<T>
    {
        /**
         * Listener returns false if it wants to have another opportunity in the future to process the message.
         * @param message
         * @return
         */
        boolean onMessage( T message );
    }

    //----------------------------------------------------------------------------//

    private final ConcurrentLinkedQueue<Pending> queue;
    private       ScheduledExecutorService       excMsgMgr;
    private final long nMaxAge;
    private final int  nMaxStorage;
    private final long nInterval;

    //----------------------------------------------------------------------------//

    /**
     * Default constructor.
     *
     * Makes: this( 0 )
     */
    public Dispatcher()
    {
        this( 0 );
    }

    /**
     * Constructor.
     *
     * @param nMaxAge Maximum amount to time (in millis) for the message to be
     *                sent over and over to the listener. 0 == for ever.
     */
    public Dispatcher( int nMaxAge )
    {
        this( nMaxAge, 0 );
    }

    /**
     * Constructor.
     *
     * @param nMaxAge Maximum amount to time (in millis) for the message to be
     *                sent over and over to the listener. 0 == for ever.
     * @param maxStorage Maximum number of message stored pending to be
     *                   delivered. 0 == no limit.
     */
    public Dispatcher( int nMaxAge, int maxStorage )
    {
        this( nMaxAge, maxStorage, 50 );
    }

    /**
     * Constructor.
     *
     * @param maxAge Maximum amount to time (in millis) for the message to be
     *               sent over and over to the listener. 0 == for ever.
     * @param maxStorage Maximum number of message stored pending to be
     *                   delivered. 0 == no limit.
     * @param interval How frequesntly (in millis) will be called the internal
     *                 dispatcher thread .
     */
    public Dispatcher( long maxAge, int maxStorage, long interval )
    {
        this.nMaxAge     = maxAge;
        this.nMaxStorage = Math.max( 0, maxStorage );
        this.nInterval   = interval;
        this.queue       = new ConcurrentLinkedQueue<>();
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

        for( Dispatcher.Listener listener : getAllListeners() )
        {
            queue.add( new Pending( message, listener ) );
        }
    }

    public synchronized void start()
    {
        if( excMsgMgr == null )
        {
            excMsgMgr = Executors.newSingleThreadScheduledExecutor();
            excMsgMgr.scheduleAtFixedRate( new Deliverer(), 0, nInterval, TimeUnit.MILLISECONDS );
        }
    }

    public synchronized void stop()
    {
        if( excMsgMgr == null )
        {
            return;
        }

        try
        {
            excMsgMgr.shutdown();
            excMsgMgr.awaitTermination( 2L, TimeUnit.SECONDS );
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
        stop();
        super.finalize();
    }

    //----------------------------------------------------------------------------//
    // INNER CLASS
    // Only one thread calls same instance (in fact only one is created) of this class.
    //----------------------------------------------------------------------------//
    private final class Deliverer extends Thread
    {
        private final Map<Future<Boolean>, Pending> map  = new HashMap<>();
        private final ExecutorService               exec = Executors.newCachedThreadPool();

        Deliverer()
        {
            setName( Dispatcher.class.getSimpleName() +"-"+ getClass().getSimpleName() +"-"+ hashCode() );
        }

        @Override
        public void run()
        {
            if( ! Dispatcher.this.queue.isEmpty() )    // Only to accelerate (Iterator will not be created)
            {
                // Moves from items Dispatcher::Queue to Deliverer:Map
                for( Iterator<Pending> itera = Dispatcher.this.queue.iterator(); itera.hasNext(); )
                {
                    Pending pending = itera.next();

                    map.put( exec.submit( pending ), pending );

                    itera.remove();
                }
            }

            if( map.isEmpty() )
            {
                return;    // Nothing else to do
            }

            // Removes all entries that are already properly finished or over MaxRetries
            // and place back to the queue those entries that have a new chance.

            long now = System.currentTimeMillis();   // Placed outside of the the loop to save CPU
                                                     // (the error amount is more than acceptable)

            for( Iterator<Map.Entry<Future<Boolean>, Pending>> itera = map.entrySet().iterator(); itera.hasNext(); )
            {
                Map.Entry<Future<Boolean>, Pending> entry = itera.next();

                if( entry.getKey().isCancelled() )
                {
                    itera.remove();
                }
                else if( entry.getValue().isTooOld( now ) )    // Can't have more opportunities
                {
                    entry.getKey().cancel( true );             // Will be removed in next iteration
                }
                else if( entry.getKey().isDone() )
                {
                    try
                    {
                        if( entry.getKey().get() )    // true returned --> mission accomplished
                        {
                            itera.remove();
                        }
                        else                          // false returned --> wants more opportunities
                        {
                            Dispatcher.this.queue.add( entry.getValue() );
                        }
                    }
                    catch( CancellationException | InterruptedException | ExecutionException ex )
                    {
                        itera.remove();
                    }
                }
            }
        }
    }

    //----------------------------------------------------------------------------//
    // INNER CLASS
    //----------------------------------------------------------------------------//
    private final class Pending<T> implements Callable<Boolean>
    {
        T                   message;
        Dispatcher.Listener listener;
        long                created;

        Pending( T message, Dispatcher.Listener listener )
        {
            this.message  = message;
            this.listener = listener;
            this.created  = System.currentTimeMillis();
        }

        boolean isTooOld( long now )
        {
            return (Dispatcher.this.nMaxAge > 0)
                   &&
                   ((now - created) > Dispatcher.this.nMaxAge);
        }

        @Override
        public Boolean call() throws Exception
        {
            return listener.onMessage( message );
        }
    }
}