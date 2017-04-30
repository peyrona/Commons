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

/**
 *
 * @author peyrona
 */
public class Chronometer
{
    private volatile long reset = 0;

    //----------------------------------------------------------------------------//

    public Chronometer reset()
    {
        reset = System.currentTimeMillis();

        return this;
    }

    public long getElapsed()
    {
        return System.currentTimeMillis() - reset;
    }

    public boolean isElepased( long amountInMillis )
    {
        return (getElapsed() >= amountInMillis);
    }
}