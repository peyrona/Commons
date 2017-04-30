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

import com.peyrona.commons.util.UtilComm;
import com.peyrona.commons.util.UtilDebug;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author peyrona
 */
public class BroadcastProducer
{
    private final InetAddress              group;
    private final BroadcastMessage         message;
    private final long                     nInterval;
    private final int                      nPort;      // Broadcasting port (this is not the accepting connections port)
    private       byte[]                   msgData;
    private       ScheduledExecutorService executor;

    //----------------------------------------------------------------------------//

    public BroadcastProducer() throws SocketException, UnknownHostException
    {
        this( 30*1000 );
    }

    public BroadcastProducer( long nIntervalInMillis ) throws SocketException, UnknownHostException
    {
        this( nIntervalInMillis, UtilComm.getLowestRecommendedServerPort() );
    }

    public BroadcastProducer( long nIntervalInMillis, int nPort ) throws SocketException, UnknownHostException
    {
        this( nIntervalInMillis, nPort, null );
    }

    public BroadcastProducer( long nIntervalInMillis, int nPort, InetAddress group ) throws SocketException, UnknownHostException
    {
        if( group == null )
        {
            InetAddress ip = UtilComm.getLocalIntranetIP().iterator().next();  // FIXME <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            byte[]      ab = ip.getAddress();
                        ab[3] = (byte) 255;    // Changes last ip number to 255

            group = InetAddress.getByAddress( ab );
        }

        this.group     = group;
        this.nInterval = nIntervalInMillis;
        this.nPort     = UtilComm.ensureRecommendedPort( nPort );
        this.message   = new BroadcastMessage();

        this.message.setInterval( nIntervalInMillis );
    }

    //----------------------------------------------------------------------------//

    public InetAddress getGroup()
    {
        return group;
    }

    public int getPort()
    {
        return nPort;
    }

    public BroadcastMessage getMessage()
    {
        return message;
    }

    /**
     * Start broadcasting messages.
     * <p>
     * CARE: All Message configuration must be done prior to invoking this
     *       method. If futher configuration has to be done, the call stop(),
     *       make chanhes and call start().
     *
     * @throws SocketException
     */
    public synchronized void start() throws SocketException
    {
        msgData = message.getData().serialize().getBytes();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay( new DatagramSender(), 0, nInterval, TimeUnit.MILLISECONDS );
    }

    public synchronized void stop()
    {
        try
        {
            executor.shutdown();
            executor.awaitTermination( 2L, TimeUnit.SECONDS );
        }
        catch( InterruptedException ex )
        {
            // Nothing to do
        }
        finally
        {
            executor.shutdownNow();
        }
    }


    //----------------------------------------------------------------------------//
    // INNER CLASS
    //----------------------------------------------------------------------------//
    private class DatagramSender extends Thread
    {
        private final DatagramPacket packet;

        DatagramSender()
        {
            setName( BroadcastProducer.class.getSimpleName() +"-"+ getClass().getSimpleName() +"-"+ hashCode() );

            packet = new DatagramPacket( msgData, msgData.length, group, nPort );
        }

        @Override
        public void run()
        {
            try( DatagramSocket socket = new DatagramSocket() )
            {
                socket.setBroadcast( true );
                socket.setReuseAddress( true );
                socket.send( packet );
            }
            catch( IOException se )
            {
                UtilDebug.log( se );
            }
        }
    }
}