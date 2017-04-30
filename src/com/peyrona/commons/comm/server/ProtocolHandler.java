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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;

/**
 * This class is a base implementation that is used by TCPServerXXX:
 * it creates and runs instances of subclasses of this class inside a thread.
 * <p>
 * Instances of this class are created and run at the server side, therefore
 * each time an exception is thrown, the client socket should be closed because
 * it is unstable and most probably it will be imposible to be used from this
 * moment. This is not a problem: a new connection will be requested at the
 * client side, accepted at the server side and aa new protocol instance created.
 * <p>
 * Note: even if this implementation is thread safe (it is inmutable), it is not
 * needed to be thread safe, because a new instance is created each time by the
 * Server Socket.
 *
 * @author peyrona
 */
public abstract class ProtocolHandler extends Thread
{
    private final Socket         socket;    // Client
    private final PrintWriter    writer;    // To send msgs
    private final BufferedReader reader;    // To receive msgs

    //----------------------------------------------------------------------------//

    public ProtocolHandler( Socket socket ) throws IOException
    {
        setName( getClass().getSimpleName()
                 +"-"+
                 hashCode() );

        socket.setKeepAlive( true );
        socket.setReuseAddress( true );

        this.socket = socket;
        this.reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        this.writer = new PrintWriter( socket.getOutputStream(), true );    // true == autoflush

        UtilDebug.debuggingTrace( "Socket open" );
    }

    //----------------------------------------------------------------------------//

    /**
     * Receives a message sent by the client and optionally returns an answer to
     * be sent back to the client.
     *
     * @param sMsg The message sent by the client.
     * @return The answer to be sent back to the client or null if no answer.
     */
    protected abstract Object process( String sMsg );

    //----------------------------------------------------------------------------//

    @Override
    public void interrupt()
    {
        super.interrupt();
        closeSocket();
    }

    @Override
    public void run()
    {
        try
        {
            while( ! socket.isClosed() )
            {
                String s = receive();

                if( s != null )
                {
                    send( process( s ) );
                }
            }
        }
        catch( IOException exc )
        {
            if( ! (exc instanceof SocketException) )     // If not caused by closing the socket
            {
                UtilDebug.log( Level.SEVERE, exc, null );
            }
        }
        finally
        {
            closeSocket();
        }
    }

    /**
     * Fills a StringBuilder with received chars until sEndOfMsg is found.
     * <p>
     * When sEndOfMsg is found the whole String is returned, in the mean time, null
     * is returned.
     *
     * @return
     * @throws IOException
     */
    public String receive() throws IOException
    {
        return reader.readLine();
    }

    public synchronized void send( Object message ) throws IOException
    {
        if( message != null )
        {
            String s = message.toString();

            writer.println( s );
        }
    }

    //----------------------------------------------------------------------------//

    protected void closeSocket()
    {
        if( ! socket.isClosed() )
        {
            UtilDebug.debuggingTrace();

            try
            {
                socket.close();    // Also closes the input & output streams
            }
            catch( Exception ex )
            {
                /* Nothing to do */
            }
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        closeSocket();
        super.finalize();
    }
}