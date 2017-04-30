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

package com.peyrona.commons.comm.client;

import com.peyrona.commons.util.UtilDebug;
import com.peyrona.commons.util.UtilString;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.SocketFactory;

/**
 * This class allows to work with a client socket in a simple and clean way.
 * Depending on the constructor used, it will internally use socket.read() or
 * socket.readLine().
 *
 * The default implementation; it is based in traditional Java sockets (before
 * NIO API appeared).
 *
 * @author peyrona
 */
public class TCPClientClassic_OLD extends TCPClientAbstract<InetSocketAddress>
{
    private final AtomicReference<Socket>         socket   = new AtomicReference<>();   // Client
    private final AtomicReference<PrintWriter>    writer   = new AtomicReference<>();   // To send msgs
    private final AtomicReference<BufferedReader> reader   = new AtomicReference<>();   // To receive msgs
    private       ClientReceiverThread            receiver = null;                      // Running in background to receive msgs
    private final ExecutorService                 executor = Executors.newSingleThreadExecutor();

    //----------------------------------------------------------------------------//

    /**
     * By using this constructor, this class will use socket.readLine() to read
     * from socket.
     */
    public TCPClientClassic_OLD()
    {
        this( 0 );
    }

    /**
     * By using this constructor, this class will use socket.read() to read
     * from socket.
     *
     * @param nReadBufferSize The buffer size, if x < 1 then the behavior will
     *                        be the same as using the argument zero constructor.
     */
    public TCPClientClassic_OLD( int nReadBufferSize )
    {
        setReadBufferSize( nReadBufferSize );
    }

    //----------------------------------------------------------------------------//

    @Override
    public final void connect( InetSocketAddress socketAddress )
    {
        setSocketAddress( socketAddress );

        synchronized( this )
        {
            if( receiver == null )     // If the thread was already started, the invocation is ignored.
            {
                receiver = new ClientReceiverThread( getReadBufferSize() );
                executor.execute( receiver );
            }
        }
    }

    /**
     * Stops the background receiver; closes socket, reader and writer.
     */
    @Override
    public final synchronized void disconnect()
    {
        if( receiver != null )    // If the thread was already null, the invocation is ignored.
        {
            try
            {
                receiver.interrupt();
                executor.shutdown();
                executor.awaitTermination( 1L, TimeUnit.SECONDS );
                executor.shutdownNow();
            }
            catch( Exception ex )
            {
                // Nothing to do
            }
            finally
            {
                receiver = null;      // Must ensure receiver is null at the end
            }
        }
    }

    /**
     * Sends a list of Objects (null elements are ignored).
     * <p>
     * The method used is: print( ... )
     * @param message
     */
    @Override
    public void send( Object... message )
    {
        if( createWriter() )
        {
            for( Object msg : message )
            {
                if( msg != null )
                {
                    writer.get().print( msg );
                    fireSent( msg );
                }
            }
        }
    }

    /**
     * Sends a list of Objects (null elements are ignored).
     * <p>
     * The method used is println( ... ) tp send every passed object.
     * @param message
     */
    @Override
    public void sendLine( Object... message )
    {
        if( createWriter() )
        {
            for( Object msg : message )
            {
                if( msg != null )
                {
                    writer.get().println( msg );
                    fireSent( msg );
                }
            }
        }
    }

    /**
     * Sends a platform dependent EoL.
     * <p>
     * The method used is: println()
     */
    @Override
    public void sendLine()
    {
        if( createWriter() )
        {
            writer.get().println();
            fireSent( UtilString.sEOL );
        }
    }

    //----------------------------------------------------------------------------//

    @Override
    protected void finalize() throws Throwable
    {
        disconnect();
        super.finalize();
    }

    //----------------------------------------------------------------------------//

    /**
     * Creates a reader (only if needed).
     *
     * This method ends either with a valid reader or an exception.
     */
    private boolean createReader()
    {
        if( ! createSocket() )
        {
            return false;
        }

        if( (reader.get() != null) && socket.get().isInputShutdown() )
        {
            try
            {
                reader.get().close();
            }
            catch( IOException ex )
            {
                // Nothing to do
            }
            finally
            {
                reader.set( null );
            }
        }

        if( reader.get() == null )
        {
            try
            {
                reader.set( new BufferedReader( new InputStreamReader( socket.get().getInputStream() ) ) );
            }
            catch( IOException ioe )
            {
                UtilDebug.log( ioe );
                destroySocket();    // if can not create the stream --> something is going wrong --> better to disconnect the socket
                fireException( new IOException( "Error creating input stream.", ioe ) );
            }
        }

        return (reader.get() != null);
    }

    /**
     * Creates a writer (only if needed).
     * <p>
     * This method ends either with a valid writer or an exception.
     */
    private boolean createWriter()
    {
        if( ! createSocket() )
        {
            return false;
        }

        if( (writer.get() != null) && socket.get().isOutputShutdown() )
        {
            writer.get().close();
            writer.set( null );
        }

        if( writer.get() == null)
        {
            try
            {
                writer.set( new PrintWriter( socket.get().getOutputStream(), true ) );    // true == autoflush
            }
            catch( IOException ioe )
            {
                UtilDebug.log( ioe );
                destroySocket();    // if can not create the stream --> something is going wrong --> better to disconnect the socket
                fireException( new IOException( "Error creating output stream.", ioe ) );
            }
        }

        return (writer.get() != null);
    }

    /**
     * Creates a socket (only if needed).
     * <p>
     * This method ends either with a valid writer or an exception.
     */
    private boolean createSocket()
    {
        if( (socket.get() != null) && socket.get().isClosed() )
        {
            socket.set( null );
        }

        if( socket.get() == null )
        {
            try
            {
                Socket s = SocketFactory.getDefault().createSocket( getAddress().getHostString(),
                                                                    getAddress().getPort() );
                s.setKeepAlive( true );
                s.setReuseAddress( true );

                socket.set( s );
                fireConnected();
            }
            catch( IOException ioe )
            {
                UtilDebug.log( ioe );
                destroySocket();
                fireException( new IOException( "Error creating socket.", ioe ) );
            }
        }

        return (socket.get() != null);
    }

    private void destroySocket()
    {
        if( socket.get() != null )
        {
            try
            {
                socket.get().close();    // Closes its streams: input and output
            }
            catch( Exception ex )
            {
                /* Nothing to do */
            }
            finally
            {
                socket.set( null );
                reader.set( null );
                writer.set( null );
                fireDisconnected();
            }
        }
    }

    @Override
    public void send( List<Object> messages )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendLine( List<Object> messages )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    //----------------------------------------------------------------------------//
    // INNER CLASS
    //----------------------------------------------------------------------------//
    private class ClientReceiverThread extends Thread
    {
        private       boolean isRunning = false;
        private final char[]  buffer;

        ClientReceiverThread( int nBufferSize )
        {
            setName(TCPClientClassic_OLD.class.getSimpleName() +
                     "-->"+
                     getClass().getSimpleName()
                     +"-"+
                     hashCode() );

            this.buffer = ((nBufferSize <= 0) ? null : new char[ nBufferSize ]);
        }

        //----------------------------------------------------------------------------//

        @Override
        public void interrupt()
        {
            super.interrupt();
            isRunning = false;
            destroySocket();
        }

        @Override
        public void run()
        {
            isRunning = true;

            while( isRunning )
            {
                try
                {
                    if( createReader() )
                    {
                        String sReaded = null;

                        if( buffer == null )
                        {
                            sReaded = reader.get().readLine();
                        }
                        else
                        {
                            int nReadedChars = reader.get().read( buffer );

                            if( nReadedChars > -1 )
                            {
                                sReaded = new String( buffer, 0, nReadedChars );
                            }
                        }

                        if( sReaded != null )
                        {
                            fireReceived( sReaded );
                        }
                        else
                        {
                            destroySocket();
                        }
                    }
                }
                catch( InterruptedIOException iioe )
                {
                    Thread.currentThread().interrupt();
                }
                catch( IOException ioe )
                {
                    if( isRunning )
                    {
                        createReader();
                    }
                }
            }

            destroySocket();
        }
    }
}