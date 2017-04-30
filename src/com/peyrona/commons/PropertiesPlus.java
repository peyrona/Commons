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

import com.peyrona.commons.util.UtilConvert;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author peyrona
 */
public class PropertiesPlus extends Properties
{
    public PropertiesPlus()
    {
        super();
    }

    public PropertiesPlus( Properties defaults )
    {
        super( defaults );
    }

    //----------------------------------------------------------------------------//

    public boolean getBoolean( String key, boolean defaultValue )
    {
        String value = getProperty( key );

        return ((value == null) ? defaultValue : UtilConvert.string2Boolean( value ) );
    }

    public int getInteger( String key, int defaultValue )
    {
        String value = getProperty( key );

        return ((value == null) ? defaultValue : Integer.parseInt( value ) );
    }

    public long getLong( String key, long defaultValue )
    {
        String value = getProperty( key );

        return ((value == null) ? defaultValue : Long.parseLong( value ) );
    }

    public float getFloat( String key, float defaultValue )
    {
        String value = getProperty( key );

        return ((value == null) ? defaultValue : Float.parseFloat( value ) );
    }

    public double getDouble( String key, double defaultValue )
    {
        String value = getProperty( key );

        return ((value == null) ? defaultValue : Double.parseDouble( value ) );
    }

    public String getString( String key, String defaultValue )
    {
        return getProperty( key, defaultValue );
    }

    public Date getDate( String key, Date defaultValue )
    {
        String value = getProperty( key );

        if( value == null )
        {
            return defaultValue;
        }

        int millis = Integer.parseInt( value );    // It is assumed that is an integer value representing the date

        return new Date( millis );
    }
}