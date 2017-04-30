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

public final class UtilConvert
{
    /** 1 millisecond */            public final static int MILLIS = 1;           // Just for code clarity
    /** 1 second in milliseconds */ public final static int SECOND = 1000;
    /** 1 minute in milliseconds */ public final static int MINUTE = SECOND * 60;
    /** 1 hour in milliseconds */   public final static int HOUR   = MINUTE * 60;
    /** 1 day in milliseconds */    public final static int DAY    = HOUR   * 24;

    public final static String KILOMETER = "km";
    public final static String METER     = "m";
    public final static String MILE      = "mi";
    public final static String YARD      = "yd";

    public final static String KILOMETERS_PER_HOUR = "kmh";
    public final static String MILES_PER_HOUR      = "mph";

    //------------------------------------------------------------------------//
    private UtilConvert() {}  // Avoid this class instances creation
    //------------------------------------------------------------------------//

    public static float celsius2fahrenheit( float celsius )
    {
        return ((celsius * 9) / 5) + 32;
    }

    public static float fahrenheit2celsius( float fahrenheit )
    {
        return ((fahrenheit - 32) * 5) / 9;
    }

    public static double celsius2fahrenheit( double celsius )
    {
        return ((celsius * 9) / 5) + 32;
    }

    public static double fahrenheit2celsius( double fahrenheit )
    {
        return ((fahrenheit - 32) * 5) / 9;
    }

    public static double metersPerSecond2kilometersPerHour( double mps )  // speed * 3.6f -> from m/s to Km/h
    {
        return( mps * 3.6f );
    }

    public static double kilometersPerHour2metersPerSecond( double kh )  // speed / 3.6f -> from Km/h to m/s
    {
        return( kh / 3.6f );
    }

    public static double miles2yards( double miles )
    {
        return( miles * 1760 );
    }

    public static double yards2miles( double yards )
    {
        return( yards * 0.000568181818 );
    }

    public static double meters2yards( double meters )
    {
        return( meters * 1.0936133 );
    }

    public static double yards2meters( double yards )
    {
        return( yards * 0.9144 );
    }

    public static double meters2miles( double meters )
    {
        return( meters * 0.000621371192 );
    }

    public static double miles2meters( double miles )
    {
        return( miles * 1609.344 );
    }

    /**
     * Pasa de un porcentage a un valor absoluto dentro de un rango.
     *
     * Por ejemplo el 50% de un rango entre 0 y 255 es 127:
     * <pre>
     *    percent2abs( 50, 0, 255 ) == 127
     * </pre>
     * @param percent
     * @param min
     * @param max
     * @return
     */
    public static int percent2abs( int percent, int min, int max )
    {
        percent = ((percent < 0) ? 0 : ((percent > 100) ? 100 : percent));

        return ((percent * (max - min)) / 100);
    }

    /**
     * Pasa de un porcentage a un valor absoluto dentro de un rango.
     *
     * @param percent
     * @param min
     * @param max
     * @return
     * @see
     */
    public static float percent2abs( float percent, float min, float max )
    {
        percent = ((percent < 0) ? 0 : ((percent > 100) ? 100 : percent));

        return ((percent * (max - min)) / 100);
    }

    /**
     * Pasa de un porcentage a un valor absoluto dentro de un rango.
     *
     * @param percent
     * @param min
     * @param max
     * @return
     * @see
     */
    public static double percent2abs( double percent, double min, double max )
    {
        percent = ((percent < 0) ? 0 : ((percent > 100) ? 100 : percent));

        return ((percent * (max - min)) / 100);
    }

    /**
     * Pasa de un valor absoluto dentro de un rango a su porcentaje.
     *
     * @param abs
     * @param min
     * @param max
     * @return
     */
    public static int abs2percent( int abs, int min, int max )
    {
        return (abs * 100) / (max - min);
    }

    /**
     * Pasa de un valor absoluto dentro de un rango a su porcentaje.
     *
     * @param abs
     * @param min
     * @param max
     * @return
     */
    public static float abs2percent( float abs, float min, float max )
    {
        return (abs * 100) / (max - min);
    }

    /**
     * Pasa de un valor absoluto dentro de un rango a su porcentaje.
     *
     * @param abs
     * @param min
     * @param max
     * @return
     */
    public static double abs2percent( double abs, double min, double max )
    {
        return (abs * 100) / (max - min);
    }

    /**
     * Normaliza value haciendo que no sea mayor que max ni menor que min.
     *
     * @param min Mínimo valor aceptado
     * @param value Valor a normalizar
     * @param max Máximo valor aceptado
     * @return El valor normalizado.
     */
    public static int between( int min, int value, int max )
    {
        return ((value > max) ? max
                              : ((value < min) ? min
                                               : value) );
    }

    /**
     * Normaliza value haciendo que no sea mayor que max ni menor que min.
     *
     * @param min Mínimo valor aceptado
     * @param value Valor a normalizar
     * @param max Máximo valor aceptado
     * @return El valor normalizado.
     */
    public static long between( long min, long value, long max )
    {
        return ((value > max) ? max
                              : ((value < min) ? min
                                               : value) );
    }

    /**
     * Normaliza value haciendo que no sea mayor que max ni menor que min.
     *
     * @param min Mínimo valor aceptado
     * @param value Valor a normalizar
     * @param max Máximo valor aceptado
     * @return El valor normalizado.
     */
    public static double between( double min, double value, double max )
    {
        return ((value > max) ? max
                              : ((value < min) ? min
                                               : value) );
    }

    //----------------------------------------------------------------------------//
    private final static String sTRUE = "true";
    private final static String sON   = "on";
    private final static String sYES  = "yes";

    /**
     * Devuelve true si la cadena pasada es una de estas: "1", "true", "on",
     * "yes" y false en caso contrario, null incluido (es case insensitive).
     *
     * @param value Cadena a comprobar.
     * @return true si es una cadena que representa un true, falso en otro caso.
     */
    public static Boolean string2Boolean( String value )
    {
        if( UtilString.isEmpty( value ) )
        {
            return Boolean.FALSE;
        }

        value = value.trim();

        if( value.charAt( 0 ) == '0' )
        {
            return false;
        }

        if( value.charAt( 0 ) == '1' )
        {
            return true;
        }

        value = value.toLowerCase();

        return (sTRUE.equals( value )
                ||
                sON.equals( value )
                ||
                sYES.equals( value ));
    }
    //----------------------------------------------------------------------------//

    public static Number string2Number( String value )
    {
        if( UtilString.isInteger( value ) )
        {
            return Integer.valueOf( value );
        }

        return Float.valueOf( value );
    }

    public static Float asFloat( boolean value )
    {
        return (value ? 1f : 0f);
    }

    public static Double asDouble( boolean value )
    {
        return (value ? 1d : 0d);
    }

    public static Boolean asBoolean( Double value )
    {
        return ((value == null) ? null : (Math.round( value ) == 1));
    }

    public static Boolean asBoolean( Float value )
    {
        return asBoolean( value.doubleValue() );
    }

    public static Boolean asBoolean( Integer value )
    {
        return value == 1;
    }

    public static Integer asInteger( Double value )
    {
        return ((value == null) ? null : value.intValue());
    }

    public static Integer asInteger( Float value )
    {
        return ((value == null) ? null : value.intValue());
    }
}