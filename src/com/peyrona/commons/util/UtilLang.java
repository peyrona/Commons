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

    /**
     * This method performs follwing call:<br>
     * <code>invoke( obj, methodName, returnWhenNull, new Object[] {} )</code>
     *
     * @param obj The one to invoke the method to.
     * @param methodName To invoke on the object.
     * @param returnWhenNull Value to return if passed object was null.
     * @return Return of called method
     */
    public static Object invoke( Object obj, String methodName, Object returnWhenNull )
    {
        return invoke( obj, methodName, returnWhenNull, new Object[] {} );
    }

    /**
     * Invokes a method for an object, but if the object were null, last parameter is returned.
     * <p>
     * Note: if an exception occurs during method invocation (i.e. NoSuchMethodException), then
     * the exception instance is returned.
     *
     * @param obj The one to send the methos to.
     * @param methodName To invoke over the object.
     * @param returnWhenNull Value to return if passed object was null.
     * @param args Arguments to pass to called method.
     * @return The result to invoke the method over the object, or returnWhenNull if object was
     *         null, or the exception if any.
     */
    public static Object invoke( Object obj, String methodName, Object returnWhenNull, Object... args )
    {
        Object ret = returnWhenNull;

        if( obj != null )
        {
            Class params[] = {};

            try
            {
                Method method = obj.getClass().getMethod( methodName, params );
                ret = method.invoke( obj, args );
            }
            catch( NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException ex )
            {
                ret = ex;
            }
        }

        return ret;
    }

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