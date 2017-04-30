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

package com.peyrona.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Una ruleta que contiene un conjunto finito de valores y a la que se puede
 * hacer girar secuencial o aleatoriamente.
 * <p>
 * This implementation is thread-safe.
 *
 * @author peyrona
 * @param <T>
 */
public final class Roulette<T>
{
    private final List<T>       roulette;
    private final AtomicInteger selected;
    private final AtomicBoolean circular;

    //----------------------------------------------------------------------------//

    public Roulette()
    {
        this( (T) null );
    }

    public Roulette( T... element )
    {
        this( ((element == null) ? (List<T>) null
                                 : (List<T>) Arrays.asList( element ) ) );
    }

    public Roulette( List<T> elements )
    {
        roulette = Collections.synchronizedList( new ArrayList<T>() );
        selected = new AtomicInteger( 0 );
        circular = new AtomicBoolean( true );

        if( elements != null )
        {
            addAll( elements );
        }
    }

    //----------------------------------------------------------------------------//

    public T get()
    {
        return (roulette.isEmpty() ? null
                                   : roulette.get( selected.get() ));
    }

    /**
     * Selecciona elemento de la ruleta al azar.
     *
     * @return
     */
    public T spin()
    {
        Random rand = new Random();

        return rotateAndGet( rand.nextInt( roulette.size() ) );
    }

    public T forward()
    {
        return forward( 1 );
    }

    public T backward()
    {
        return backward( 1 );
    }

    public T forward( int elements )
    {
        return rotateAndGet( elements );
    }

    public T backward( int elements )
    {
        return rotateAndGet( -elements );
    }

    public List<T> getAll()
    {
        return Collections.unmodifiableList( roulette );
    }

    public void add( T... element )
    {
        if( element != null )
        {
            addAll( (List<T>) Arrays.asList( element ) );
        }
    }

    public void addAll( List<T> elements )
    {
        if( elements != null )
        {
            for( T t : elements )
            {
                add( t );
            }
        }
    }

    public void add( T element )
    {
        if( element != null )
        {
            roulette.add( element );
        }
    }

    public void clear()
    {
        roulette.clear();
    }

    /**
     * Removes current element and makes next the selected one
     *
     * @return Removed element.
     */
    public T remove()
    {
        if( roulette.isEmpty() )
        {
            return null;
        }

        T t = roulette.remove( selected.get() );

        selected.set( Math.min( selected.get(), roulette.size() - 1 ) );

        return t;
    }

    public boolean isCircular()
    {
        return circular.get();
    }

    public void setCircular( boolean b )
    {
        circular.set( b );
    }

    //----------------------------------------------------------------------------//

    private T rotateAndGet( int n )
    {
        if( roulette.isEmpty() )
        {
            return null;
        }

        int size = roulette.size();

        if( n == 0 || n == size )   // Pointer does not move
        {
            return get();
        }

        if( Math.abs( n ) > size )
        {
            n = Math.abs( n ) % size;
            n = ((n > 0) ? n : n*-1);
        }

        for( int x = 0; x < Math.abs( n ); x++ )
        {
            if( n > 0 )
            {
                if( selected.incrementAndGet() == size )   // last element == size -1
                {
                    if( circular.get() )
                    {
                        selected.set( 0 );
                    }
                    else
                    {
                        selected.set( size -1 );
                    }
                }
            }
            else
            {
                if( selected.decrementAndGet() < 0 )
                {
                    if( circular.get() )
                    {
                        selected.set( size - 1 );
                    }
                    else
                    {
                        selected.set( 0 );
                    }
                }
            }
        }

        return roulette.get( selected.get() );
    }
}