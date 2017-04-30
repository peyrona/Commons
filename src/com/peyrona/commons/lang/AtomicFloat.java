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

import static java.lang.Float.floatToIntBits;
import static java.lang.Float.intBitsToFloat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author peyrona
 */
public final class AtomicFloat extends Number
{
    private AtomicInteger bits;

    //----------------------------------------------------------------------------//

    public AtomicFloat()
    {
        this( 0f );
    }

    public AtomicFloat( float initialValue )
    {
        bits = new AtomicInteger( floatToIntBits( initialValue ) );
    }

    //----------------------------------------------------------------------------//

    public boolean compareAndSet( float expect, float update )
    {
        return bits.compareAndSet( floatToIntBits( expect ),
                                   floatToIntBits( update ) );
    }

    public void set( float newValue )
    {
        bits.set( floatToIntBits( newValue ) );
    }

    public float get()
    {
        return intBitsToFloat( bits.get() );
    }

    @Override
    public float floatValue()
    {
        return get();
    }

    public float getAndSet( float newValue )
    {
        return intBitsToFloat( bits.getAndSet( floatToIntBits( newValue ) ) );
    }

    public boolean weakCompareAndSet( float expect, float update )
    {
        return bits.weakCompareAndSet( floatToIntBits( expect ),
                                       floatToIntBits( update ) );
    }

    @Override
    public double doubleValue()
    {
        return (double) floatValue();
    }

    @Override
    public int intValue()
    {
        return (int) get();
    }

    @Override
    public long longValue()
    {
        return (long) get();
    }
}