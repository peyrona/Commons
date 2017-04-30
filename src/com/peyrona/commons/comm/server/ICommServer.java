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

/**
 * Interface that defines what a CommServer is: it allows to accept connections
 * information using a protocol-neutral address in an asynchronously and
 * thread-safe way.
 *
 * @author peyrona
 */
public interface ICommServer
{
    /**
     * Interface that defines CommClient handlers (listeners).
     *
     * @author peyrona
     */
    public interface Listener
    {
        void onConnected( ICommServer origin );

        void onDisconnected( ICommServer origin );

        void onConnectionAccepted( ICommServer origin, ProtocolHandler ph );

        void onException( ICommServer origin, Exception exc );
    }

    //----------------------------------------------------------------------------//

    void connect( int port );

    void disconnect();

    int  getPort();

    Class<? extends ProtocolHandler> getProtocolHandler();

    void addListener( ICommServer.Listener handler );

    void removeListener( ICommServer.Listener handler );
}