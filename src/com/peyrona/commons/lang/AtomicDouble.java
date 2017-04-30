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

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author peyrona
 */
public final class AtomicDouble extends Number
{
    private AtomicLong bits;

    //----------------------------------------------------------------------------//

    public AtomicDouble()
    {
        this( 0d );
    }

    public AtomicDouble( double initialValue )
    {
        bits = new AtomicLong( Double.doubleToLongBits( initialValue ) );
    }

    //----------------------------------------------------------------------------//

    public boolean compareAndSet( double expect, double update )
    {
        return bits.compareAndSet( Double.doubleToLongBits( expect ),
                                   Double.doubleToLongBits( update ) );
    }

    public void set( double newValue )
    {
        bits.set( Double.doubleToLongBits( newValue ) );
    }

    public double get()
    {
        return Double.longBitsToDouble( bits.get() );
    }

    @Override
    public double doubleValue()
    {
        return get();
    }

    public double getAndSet( double newValue )
    {
        return Double.longBitsToDouble( bits.getAndSet( Double.doubleToLongBits( newValue ) ) );
    }

    public boolean weakCompareAndSet( double expect, double update )
    {
        return bits.weakCompareAndSet( Double.doubleToLongBits( expect ),
                                       Double.doubleToLongBits( update ) );
    }

    @Override
    public float floatValue()
    {
        return (float) doubleValue();
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