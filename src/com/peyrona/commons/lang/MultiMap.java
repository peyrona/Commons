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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simplistic single-key with multiple-values map.
 *
 * @author peyrona
 * @param <K>
 * @param <V>
 */
public class MultiMap<K,V>
{
    protected final Map<K, List<V>> intern = new HashMap<>();

    //----------------------------------------------------------------------------//

    public void put( K key, V... value )
    {
        put( key, Arrays.asList( value ) );
    }

    public void put( K key, Collection<V> values )
    {
        List<V> lstValues = intern.get( key );

        if( lstValues == null )
        {
            lstValues = new ArrayList<>();
            intern.put( key, lstValues );
        }

        lstValues.addAll( values );
    }

    public List<V> get( K key )
    {
        return intern.get( key );
    }
}
