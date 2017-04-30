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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Simple class to create a map of params ({key,value} pairs), serialize and
 * deserialize them. It is useful when JSON can not be used (memory and or speed).
 * <p>
 * Note: key is case insensitive (ignores case) and only basic types plus String
 * and Date are accepted.
 *
 * @author peyrona
 */
public final class ParamMap
{
    private final Map<String,String> map = Collections.synchronizedMap( new HashMap<String,String>() );

    //----------------------------------------------------------------------------//

    public String serialize()
    {
        StringBuilder sb = new StringBuilder( 1024 * 4 );

        for( Map.Entry<String,String> entry : map.entrySet() )
        {
            sb.append( (char) entry.getKey().length() )      // Param name length
              .append( entry.getKey() )                      // Param name itself
              .append( (char) entry.getValue().length() )    // Param value length
              .append( entry.getValue() );                   // Param value itself
        }

        return sb.toString();
    }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public static ParamMap deserialize( String s )
    {
        ParamMap ret = new ParamMap();
        int      index = 0;
        int      keyLen;
        String   key;
        int      valueLen;
        String   value;

        while( index < s.length() )
        {
            keyLen   = (int) s.charAt( index++ );
            key      = s.substring( index, keyLen + index );
            index   += keyLen;
            valueLen = (int) s.charAt( index++ );
            value    = s.substring( index, valueLen + index );
            index   += valueLen;

            ret.map.put( key, value );
        }

        return ret;
    }

    //----------------------------------------------------------------------------//

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public boolean isNotEmpty()
    {
        return (! map.isEmpty());
    }

    /**
     * Returns the name for all parameters stored in the internal map.
     *
     * @return The name for all parameters stored in the internal map.
     */
    public Set<String> getParamNames()
    {
        return map.keySet();
    }

    /**
     * Returns true if the map contains a parameter with passed name.
     *
     * @param paramName
     * @return true if the map contains a parameter with passed name.
     */
    public boolean containsParam( String paramName )
    {
        return map.containsKey( paramName );
    }

    public String remove( String paramName )
    {
        return map.remove( paramName.trim().toLowerCase() );
    }

    //----------------------------------------------------------------------------//
    // PUTs

    public void put( String paramName, byte v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, short v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, int v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, long v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, float v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, double v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, boolean v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, char v )
    {
        map.put( paramName.trim().toLowerCase(), String.valueOf( v ) );
    }

    public void put( String paramName, String v )
    {
        map.put( paramName.trim().toLowerCase(), (v == null ? "null" : v) );
    }

    public void put( String paramName, Date v )
    {
        map.put( paramName.trim().toLowerCase(), (v == null ? "null" : String.valueOf( v.getTime() )) );
    }

////    public void put( String paramName, Set set )
////    {// TODO: implementarlo
////        throw new UnsupportedOperationException( "Pending to be implemented" );
////    }
////
////    public void put( String paramName, List list )
////    {// TODO: implementarlo
////        throw new UnsupportedOperationException( "Pending to be implemented" );
////    }
////
////    public void put( String paramName, Map map )
////    {// TODO: implementarlo
////        throw new UnsupportedOperationException( "Pending to be implemented" );
////    }

    //----------------------------------------------------------------------------//
    // GETs

    public Byte getByte( String paramName )
    {
        return Byte.valueOf( getStr4Param( paramName ) );
    }

    public Short getShort( String paramName )
    {
        return Short.valueOf( getStr4Param( paramName ) );
    }

    public Integer getInt( String paramName )
    {
        return Integer.valueOf( getStr4Param( paramName ) );
    }

    public Long getLong( String paramName )
    {
        return Long.valueOf( getStr4Param( paramName ) );
    }

    public Float getFloat( String paramName )
    {
        return Float.valueOf( getStr4Param( paramName ) );
    }

    /**
     * Devuelve como Double el value asociado a la key pasada.
     * <p>
     * Este método hace el cambio del decimal separator ',' por '.': en el mundo
     * sólo hay estas 2 posibilidades.
     *
     * @param paramName
     * @return
     */
    public Double getDouble( String paramName )
    {
        return Double.valueOf( getStr4Param( paramName ).replace( ',', '.' ) );
    }

    public Boolean getBoolean( String paramName )
    {
        return Boolean.valueOf( getStr4Param( paramName ) );
    }

    public Character getChar( String paramName )
    {
        return getStr4Param( paramName ).charAt( 0 );
    }

    public String getString( String paramName )
    {
        return getStr4Param( paramName );
    }

    public Date getDate( String paramName )
    {
        return new Date( Long.parseLong( getStr4Param( paramName ) ) );
    }

    public Byte getByte( String paramName, Byte defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : Byte.valueOf( s ) );
    }

    public Short getShort( String paramName, Short defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : Short.valueOf( s ) );
    }

    public Integer getInt( String paramName, Integer defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : Integer.valueOf( s ) );
    }

    public Long getLong( String paramName, Long defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : Long.valueOf( s ) );
    }

    public Float getFloat( String paramName, Float defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : Float.valueOf( s ) );
    }

    /**
     * Devuelve como Double el value asociado a la key pasada.
     * <p>
     * Este método hace el cambio del decimal separator ',' por '.': en el mundo
     * sólo hay estas 2 posibilidades.
     *
     * @param paramName
     * @param defaultValue
     * @return
     */
    public Double getDouble( String paramName, Double defaultValue )
    {
        String s = map.get( paramName.toLowerCase() );

        return ((s == null) ? defaultValue : Double.valueOf( s.replace( ',', '.' ) ));
    }

    public Boolean getBoolean( String paramName, Boolean defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : Boolean.valueOf( s ) );
    }

    public Character getChar( String paramName, Character defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : s.charAt( 0 ) );
    }

    public String getString( String paramName, String defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : ("null".equals( s ) ? null : s));
    }

    public Date getDate( String paramName, Date defaultValue )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        return ((s == null) ? defaultValue : ("null".equals( s ) ? null : new Date( Long.parseLong( s ) )));
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 47 * hash + this.map.hashCode();
        return hash;
    }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    @Override
    public boolean equals( Object obj )
    {
        if( obj == null )
        {
            return false;
        }

        if( getClass() != obj.getClass() )
        {
            return false;
        }

        final ParamMap other = (ParamMap) obj;

        return Objects.equals( this.map, other.map );
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + map.toString();
    }

    //----------------------------------------------------------------------------//

    private String getStr4Param( String paramName )
    {
        String s = map.get( paramName.trim().toLowerCase() );

        if( s == null )
        {
            throw new IllegalArgumentException( "Does not exists a parameter with name = '"+ paramName +"'" );
        }

        return s;
    }
}