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

package com.peyrona.commons.comm.broadcast;

import com.peyrona.commons.comm.CommReceiver;
import com.peyrona.commons.lang.ListenerWise;
import com.peyrona.commons.util.UtilComm;
import com.peyrona.commons.util.UtilDebug;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Discover machines that are broadcasting UDP datagrams.
 *
 * @author peyrona
 */
public class BroadcastConsumer extends ListenerWise<BroadcastConsumer.Listener>
{
    //----------------------------------------------------------------------------//
    // INNER CLASS: Interface Declaration
    //----------------------------------------------------------------------------//
    public static interface Listener
    {
        void onBroadcastReceived( DatagramPacket packet, BroadcastMessage message );
    }

    //----------------------------------------------------------------------------//

    private       ExecutorService   executor = null;
    private final BroadcastListener listener = new BroadcastListener();
    private final int               nPort;
    private final int               nBufferSize;

    //----------------------------------------------------------------------------//

    /**
     * Creates an instance of this class with a buffer size: 1024 bytes.
     */
    public BroadcastConsumer()
    {
        this( -1 );
    }

    /**
     * Creates an instance of this class with desired port to receive UDP
     * datagrams.
     *
     * @param nPort
     */
    public BroadcastConsumer( int nPort )
    {
        this( nPort, 1024 );
    }

    /**
     * Creates an instance of this class with desired port to receive UDP
     * datagrams and desired buffer size.
     *
     * @param nPort
     * @param nBufferSize
     */
    public BroadcastConsumer( int nPort, int nBufferSize )
    {
        this.nPort       = UtilComm.ensureRecommendedPort( nPort );
        this.nBufferSize = nBufferSize;
    }

    //----------------------------------------------------------------------------//

    public synchronized boolean isRunning()
    {
        return (executor != null);
    }

    public int getPort()
    {
        return nPort;
    }

    /**
     * Starts a new thread (which creates the socket) to send broadcast datagram
     * packets.
     * <p>
     * Calling on an BroadcastConsumer that is already started has no effect.
     *
     * @throws IOException
     */
    public synchronized void start() throws IOException
    {
        if( ! isRunning() )
        {
            executor = Executors.newSingleThreadExecutor();
            executor.execute( listener );
        }
    }

    /**
     * Stops the thread (which closes the socket) that is sending broadcast datagram
     * packets.
     * <p>
     * Calling on an BroadcastConsumer that is already stopped has no effect.
     */
    public synchronized void stop()
    {
        if( isRunning() )
        {
            try
            {
                listener.interrupt();
                executor.shutdown();
                executor.awaitTermination( 3L, TimeUnit.SECONDS );
            }
            catch( InterruptedException ex )
            {
                // Nothing to do
            }
            finally
            {
                try{ executor.shutdownNow(); }
                catch( Exception e ) { }

                executor = null;
            }
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
    //----------------------------------------------------------------------------//
    private final class BroadcastListener extends CommReceiver
    {
        private DatagramSocket socket = null;

        //-----------------------------------------------------------------------//

        BroadcastListener()
        {
            super( BroadcastConsumer.class.getSimpleName() + "-->" );
        }

        //----------------------------------------------------------------------------//

        @Override
        public void readSocket() throws IOException
        {
            DatagramPacket packet = new DatagramPacket( new byte[nBufferSize], nBufferSize ); // Must be recreated (can't be reused)

            socket.receive( packet );

            if( packet.getLength() == 0 )
            {
                throw new IOException( "Channel is not operative." );
            }

            byte[]           data = Arrays.copyOfRange( packet.getData(), packet.getOffset(), packet.getLength() );
            BroadcastMessage msg  = new BroadcastMessage( data );

            // Fires event
            for( BroadcastConsumer.Listener listener : getAllListeners() )
            {
                try
                {
                    listener.onBroadcastReceived( packet, msg );
                }
                catch( Exception exc )   // Exception thrown inside listener
                {
                    UtilDebug.log( exc );
                }
            }
        }

        @Override
        protected void createSocket() throws SocketException
        {
            if( socket == null )
            {
                socket = new DatagramSocket( getPort() );
                socket.setReuseAddress( true );
            }
        }

        @Override
        protected void destroySocket()
        {
            if( socket != null )
            {
                socket.close();
                socket = null;
            }
        }
    }
}