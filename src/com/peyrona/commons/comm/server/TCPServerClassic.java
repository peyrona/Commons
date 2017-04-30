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

package com.peyrona.commons.comm.server;

import com.peyrona.commons.util.UtilDebug;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This class implements a multithreaded and thread safe Socket Server.
 * <p>
 * When a connection comes, a client Socket is created and passed to an
 * instance that extends ProtocolHandler.
 *
 * @author peyrona
 */
public final class TCPServerClassic extends TCPServerAbstract
{
    private       ServerReceiverThread accepter = null;   // Running in background to accept connections
    private final ExecutorService      executor = Executors.newCachedThreadPool();

    //----------------------------------------------------------------------------//

    public TCPServerClassic( Class<? extends ProtocolHandler> protocol )
    {
        super( protocol );
    }

    //----------------------------------------------------------------------------//

    @Override
    public void connect( int nPort )
    {
        setPort( nPort );

        synchronized( this )
        {
            if( accepter == null )
            {
                accepter = new ServerReceiverThread();
                executor.execute( accepter );
                fireConnected();
            }
        }
    }

    @Override
    public synchronized void disconnect()
    {
        if( accepter != null )
        {
            try
            {
                accepter.interrupt();
                executor.shutdown();
                executor.awaitTermination( 2, TimeUnit.SECONDS );
            }
            catch( Exception ex )
            {
                // Nothing to do
            }
            finally
            {
                accepter = null;     // Tengo que asegurarme que accepter acaba siendo null

                try{ executor.shutdownNow(); }
                catch( Exception e ) { }

                fireDisconnected();
            }
        }
    }

    //----------------------------------------------------------------------------//
    // INNER CLASS
    //----------------------------------------------------------------------------//
    private class ServerReceiverThread extends Thread
    {
        private       boolean                             isRunning    = false;
        private       ServerSocket                        socketServer = null;
        private final WeakHashMap<Socket,ProtocolHandler> connections  = new WeakHashMap<>();
        private final Constructor<?>                      constructor;

        //----------------------------------------------------------------------------//

        ServerReceiverThread()
        {
            setName( TCPServerClassic.class.getSimpleName() +
                     "-->"+
                     getClass().getSimpleName()
                     +"-"+
                     hashCode() );

            Constructor<?> cons = null;

            try
            {
                cons = getProtocolHandler().getConstructor( Socket.class );
            }
            catch( NoSuchMethodException | SecurityException | IllegalArgumentException exc )
            {
                onMyCodeIsBad( exc );
            }

            constructor = cons;
            createSocketServer();
        }

        //----------------------------------------------------------------------------//

        @Override
        public void interrupt()    // Flow comes here when accepter.interrupt();
        {
            super.interrupt();

            isRunning = false;
            destroySocketServer();     // Not accepting more connections
            destroyConnections();
        }

        @Override
        public void run()
        {
            isRunning = true;

            while( isRunning )
            {
                try
                {
                    Socket          socketClient = socketServer.accept();
                    ProtocolHandler protocol     = (ProtocolHandler) constructor.newInstance( socketClient );

                    connections.put( socketClient, protocol );
                    executor.execute( protocol );
                    fireConnectionAccepted( protocol );
                }
                catch( InterruptedIOException iioe )     // Flow comes here when SocketServer is closed.
                {
                    Thread.currentThread().interrupt();
                }
                catch( IOException ioe )
                {
                    if( ioe instanceof SocketException )     // SocketServer was closed by someone
                    {
                        isRunning = ((socketServer != null) && socketServer.isClosed());
                    }

                    if( isRunning )
                    {
                        UtilDebug.log( Level.WARNING, ioe, "Error accepting an incoming connection on Server Socket." );
                        destroySocketServer();
                        createSocketServer();
                    }
                }
                catch( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exc )
                {
                    onMyCodeIsBad( exc );
                }
            }

            destroySocketServer();     // Not accepting more connections
            destroyConnections();
        }

        private void destroyConnections()
        {
            for( ProtocolHandler ph : connections.values() )
            {
                try
                {
                    ph.interrupt();
                }
                catch( Exception exc )
                {
                    // Nothing to do
                }
            }

            connections.clear();
        }

        private void createSocketServer()
        {
            if( socketServer == null )
            {
                try
                {
                    socketServer = new ServerSocket( getPort() );
                }
                catch( IOException ioe )
                {
                    socketServer = null;
                    UtilDebug.log( Level.WARNING, ioe, "Error creating a new Server Socket" );
                }
            }
        }

        private void destroySocketServer()
        {
            if( socketServer != null )
            {
                try
                {
                    socketServer.close();
                }
                catch( IOException ioe )
                {
                    // Nothing to do
                }
                finally
                {
                    socketServer = null;
                }
            }
        }

        private void onMyCodeIsBad( Exception exc )
        {
            UtilDebug.log( Level.SEVERE, exc, "The Java code is not written properly: "+
                                              "ProtocolHandler class can not be instantiated." );
            System.exit( 1 );
        }
    }
}