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

import java.util.List;

/**
 * Interface that defines what a CommClient is: it allows to send and receive
 * information using a protocol-neutral address in an asynchronously and
 * thread-safe way.
 * <p>
 * Note: SocketAddres is not protocol related. Subclasses can implement any
 * protocol: TCP/IP, Serial, I2C, ...
 *
 * @author peyrona
 * @param <T>
 */
public interface ICommClient<T>
{
    /**
     * Interface that defines CommClient handlers (listeners).
     *
     * @author peyrona
     */
    public interface Listener
    {
        void onConnected( ICommClient origin );

        void onDisconnected( ICommClient origin );

        /**
         *
         * @param origin
         * @param msg  The received message. Guarranted to be not null.
         */
        void onReceived( ICommClient origin, Object msg );

        void onSent( ICommClient origin, Object msg );

        void onException( ICommClient origin, Exception exc );
    }

    //----------------------------------------------------------------------------//

    void connect( T address );

    void disconnect();

    boolean isConnected();

    /**
     * Returns the
     * @return
     */
    T    getAddress();

    void send( List<Object> messages );

    void send( Object... messages );

    /**
     * Appends an EoL to every passed object and sends it.
     * @param messages
     */
    void sendLine( List<Object> messages );

    void sendLine( Object... messages );

    void sendLine();

    void addListener( ICommClient.Listener handler );

    void removeListener( ICommClient.Listener handler );
}