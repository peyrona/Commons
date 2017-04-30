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

import com.peyrona.commons.lang.ListenerWise;
import java.net.SocketAddress;
import java.rmi.MarshalException;
import java.util.Arrays;

/**
 * Base class with common methods for all kind of Clients.
 *
 * @author peyrona
 * @param <T>
 */
public abstract class TCPClientAbstract<T extends SocketAddress>
       extends ListenerWise<ICommClient.Listener>
       implements ICommClient<T>
{
    private T       socketAddress   = null;
    private int     nReadBufferSize = 1204*4;
    private boolean bConnected      = false;

    //----------------------------------------------------------------------------//

    @Override
    public boolean isConnected()
    {
        return bConnected;
    }

    @Override
    public T getAddress()
    {
        return socketAddress;
    }

    //----------------------------------------------------------------------------//

    /**
     * Sends a list of Objects (null elements are sent as "null").
     * <p>
     * The method used is: print( ... )
     * @param message
     * @see #setSendNullValues(boolean)
     */
    @Override
    public void send( Object... message )
    {
        send( Arrays.asList( message ) );
    }

    /**
     * Sends a list of Objects (null elements are sent as "null").
     * <p>
     * The method used is: print( ... )
     * @param message
     * @see #setSendNullValues(boolean)
     */
    @Override
    public void sendLine( Object... message )
    {
        sendLine( Arrays.asList( message ) );
    }

    //----------------------------------------------------------------------------//

    protected synchronized void setSocketAddress( T socketAddress )
    {
        this.socketAddress = socketAddress;
    }

    protected int getReadBufferSize()
    {
        return nReadBufferSize;
    }

    protected synchronized void setReadBufferSize( int nReadBufferSize )
    {
        this.nReadBufferSize = nReadBufferSize;
    }

    protected void fireConnected()
    {
        bConnected = true;

        for( ICommClient.Listener listener : getAllListeners() )
        {
            try
            {
                listener.onConnected( this );
            }
            catch( Exception ex )
            {
                fireException( new MarshalException( "Exception thrown inside listener", ex ) );
            }
        }
    }

    protected void fireDisconnected()
    {
        bConnected = false;

        for( ICommClient.Listener listener : getAllListeners() )
        {
            try
            {
                listener.onDisconnected( this );
            }
            catch( Exception ex )
            {
                fireException( new MarshalException( "Exception thrown inside listener", ex ) );
            }
        }
    }

    protected void fireReceived( Object obj )
    {
        for( ICommClient.Listener listener : getAllListeners() )
        {
            try
            {
                listener.onReceived( this, obj );
            }
            catch( Exception ex )
            {
                fireException( new MarshalException( "Exception thrown inside listener", ex ) );
            }
        }
    }

    protected void fireSent( Object obj )
    {
        for( ICommClient.Listener listener : getAllListeners() )
        {
            try
            {
                listener.onSent( this, obj );
            }
            catch( Exception ex )
            {
                fireException( new MarshalException( "Exception thrown inside listener", ex ) );
            }
        }
    }

    protected void fireException( Exception exc )
    {
        for( ICommClient.Listener listener : getAllListeners() )
        {
            try
            {
                listener.onException( this, exc );
            }
            catch( Exception ex )
            {
                // Better to not report to handlers from here because it will go into an inifite loop.
            }
        }
    }
}