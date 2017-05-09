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

package com.peyrona.commons.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 *
 * @author Francisco Morero Peyrona
 */
public class UtilLang
{
    public final static Locale SPAIN = new Locale( "es", "ES" );

    //------------------------------------------------------------------------//
    private UtilLang() {}  // Avoid creation of instances of this class
    //------------------------------------------------------------------------//

    public static Field getField( Class<?> clazz, String name )
    {
        for( Field f : clazz.getDeclaredFields() )
        {
            if( f.getName().equalsIgnoreCase( name ) )
            {
                return f;
            }
        }

        return null;
    }

    public static Field getFieldInHierarchy( Class<?> clazz, String name )
    {
        for( Class<?> c = clazz; c != null; c = c.getSuperclass() )
        {
            Field f = getField( c, name );

            if( f != null )
            {
                return f;
            }
        }

        return null;
    }
	
    public static Method getMethod( Class<?> clazz, String name )
    {
        for( Method m : clazz.getDeclaredMethods() )
        {
            if( m.getName().equalsIgnoreCase( name ) )
            {
                return m;
            }
        }

        return null;
    }

    public static Method getMethodInHierarchy( Class<?> clazz, String name )
    {
        for( Class<?> c = clazz; c != null; c = c.getSuperclass() )
        {
            Method m = getMethod( c, name );

            if( m != null )
            {
                return m;
            }
        }

        return null;
    }

    public static boolean isWindows()
    {
        return (System.getProperty( "os.name" ).toLowerCase().contains( "win" ));
    }

    public static boolean isMac()
    {
        return (System.getProperty( "os.name" ).toLowerCase().contains( "mac" ));
    }

    public static boolean isUnix()
    {
        String os = System.getProperty( "os.name" ).toLowerCase();

		return (os.contains( "nix" ) || os.contains( "nux" ));
    }
}
