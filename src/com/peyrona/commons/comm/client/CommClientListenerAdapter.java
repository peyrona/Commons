/*
 * Copyright (C) 2015 Francisco José Morero Peyrona. All Rights Reserved.
 *
 * GNU Classpath is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the free
 * Software Foundation; either version 3, or (at your option) any later version.
 *
 * This app is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this software; see the file COPYING.  If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.peyrona.commons.comm.client;

/**
 * This class is just an Adapter for the interface ICommClientListener.
 *
 * @author peyrona
 */
public class CommClientListenerAdapter implements ICommClient.Listener
{
    @Override
    public void onConnected( ICommClient origin )
    {
    }

    @Override
    public void onDisconnected( ICommClient origin )
    {
    }

    @Override
    public void onReceived( ICommClient origin, Object msg )
    {
    }

    @Override
    public void onSent( ICommClient origin, Object msg )
    {
    }

    @Override
    public void onException( ICommClient origin, Exception exc )
    {
    }
}