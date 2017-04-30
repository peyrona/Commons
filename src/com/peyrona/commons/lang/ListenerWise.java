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

package com.peyrona.commons.lang;

import com.peyrona.commons.util.UtilDebug;
import com.peyrona.commons.util.UtilLang;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * A base class to save code and effort when implementing a class that uses
 * listeners.
 *
 * @author peyrona
 * @param <T>
 */
public abstract class ListenerWise<T>
{
    private final Set<T> listeners = Collections.synchronizedSet( new HashSet<T>( 1 ) );   // Initally 1 to save memory

    //----------------------------------------------------------------------------//

    /**
     * Add a new listener to be informed.
     *
     * @param listener
     */
    public final void addListener( T listener )
    {
        if( ! listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }

    /**
     * Add one or more new listeners to be informed.
     *
     * @param listener
     */
    public final void addListener( T... listener )
    {
        for( T l : listener )
        {
            addListener( l );
        }
    }

    /**
     * Removes an existing listener.
     *
     * @param listener
     */
    public final void removeListener( T listener )
    {
        listeners.remove( listener );
    }

    /**
     * Removes one or more existing listeners.
     *
     * @param listener
     */
    public final void removeListener( T... listener )
    {
        for( T l : listener )
        {
            removeListener( l );
        }
    }

    /**
     * Removes all listeners.
     */
    public final void removeAllListeners()
    {
        listeners.clear();
    }

    /**
     * Returns an unmodifiable collection of all registered listeners.
     *
     * @return An unmodifiable collection of all registered listeners.
     */
    public final Collection<T> getAllListeners()
    {
        return Collections.unmodifiableSet( listeners );
    }

    //----------------------------------------------------------------------------//

    protected final void fire( String methodName, Object... args )
    {
        for( T listener : getAllListeners() )
        {
            try
            {
                Object ret = UtilLang.invoke( listener, methodName, new Object(), args );

                if( ret instanceof Exception )
                {
                    throw ((Exception) ret);     // Reflection exception
                }
            }
            catch( Exception exc )    // Exception inside the listener
            {
                UtilDebug.log( Level.SEVERE, exc, "Error while processing dispatched event." );
            }

        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        removeAllListeners();
        super.finalize();
    }
}