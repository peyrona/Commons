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

import com.peyrona.commons.lang.ListenerWise;
import com.peyrona.commons.util.UtilComm;
import com.peyrona.commons.util.UtilDebug;
import java.rmi.MarshalException;
import java.util.logging.Level;

/**
 * Base class with common methods for all kind of Socket Servers.
 * <p>
 * This class is inmutable and therefore threadsafe.
 *
 * @author peyrona
 */
public abstract class TCPServerAbstract
       extends ListenerWise<ICommServer.Listener>
       implements ICommServer
{
    private int nPort;
    private final Class<? extends ProtocolHandler> protocol;

    //----------------------------------------------------------------------------//

    /**
     * Creates a new instance.
     *
     * @param protocol A class implementing Thread interface and having a constructor
     *                 that receives an instance of Socket class.
     */
    protected TCPServerAbstract( Class<? extends ProtocolHandler> protocol )
    {
        this.protocol = protocol;
    }

    //----------------------------------------------------------------------------//

    @Override
    public int getPort()
    {
        return nPort;
    }

    @Override
    public Class<? extends ProtocolHandler> getProtocolHandler()
    {
        return protocol;
    }

    //----------------------------------------------------------------------------//

    protected synchronized void setPort( int nPort )
    {
        this.nPort = UtilComm.ensureRecommendedPort( nPort );
    }

    protected void fireConnected()
    {
        for( ICommServer.Listener listener : getAllListeners() )
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
        for( ICommServer.Listener listener : getAllListeners() )
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

    protected void fireConnectionAccepted( ProtocolHandler ph )
    {
        for( ICommServer.Listener listener : getAllListeners() )
        {
            try
            {
                listener.onConnectionAccepted( this, ph );
            }
            catch( Exception ex )
            {
                fireException( new MarshalException( "Exception thrown inside listener", ex ) );
            }
        }
    }

    protected void fireException( Exception exc )
    {
        for( ICommServer.Listener listener : getAllListeners() )
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

    //----------------------------------------------------------------------------//

    private void onMyCodeIsBad( Exception exc )
    {
        UtilDebug.log( Level.SEVERE, exc, "The Java code is not written properly: "+
                                          "ProtocolHandler class can not be instantiated." );
        System.exit( 1 );
    }
}
