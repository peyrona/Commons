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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class UtilCollections
{
    //------------------------------------------------------------------------//
    private UtilCollections() {}  // Avoid this class instances creation
    //------------------------------------------------------------------------//

    public static boolean isNotEmpty( Collection c )
    {
        return ((c != null) && (! c.isEmpty()));
    }

    public static boolean isEmpty( Collection c )
    {
        return ((c == null) || c.isEmpty());
    }

    /**
     * Devuelve todos los elementos de la Collection en una cadena separador por
     * UtilString.cSeparator.
     *
     * @param collection A procesar.
     * @return Todos los elementos de la Collection en una cadena separador por
     *         UtilString.cSeparator.
     */
    public static String toString( Collection<?> collection )
    {
        return toString( collection, UtilString.sSeparator );
    }

    /**
     * Devuelve todos los elementos de la Collection en una cadena separador por "separator".
     *
     * @param collection A procesar.
     * @param separator
     * @return Todos los elementos de la Collection en una cadena separador por separator.
     */
    public static String toString( Collection<?> collection, String separator )
    {
        StringBuilder sb = new StringBuilder( collection.size() * 16 );

        if( ! collection.isEmpty() )
        {
            for( Object obj : collection )
            {
                sb.append( obj.toString() ).append( separator );
            }
            sb.deleteCharAt( sb.length() - separator.length() );
        }

        return sb.toString();
    }

    /**
     * This method invokes: stringToList( String s, UtilString.sSeparator )
     * @param s
     * @return
     */
    public static List<String> stringToList( String s )
    {
        return stringToList( s, UtilString.sSeparator );
    }

    /**
     * Opposite functionality to ::toString( Collection<?> collection ).
     *
     * @param s
     * @param separator
     * @return
     */
    public static List<String> stringToList( String s, String separator )
    {
        if( UtilString.isEmpty( s ) )
        {
            return new ArrayList();
        }

        return new ArrayList( Arrays.asList( s.split( separator ) ) );
    }

    /**
     * This makes following call:
     * <pre>
     * return map2String( map, UtilString.sSeparator );
     * </pre>
     *
     * @param map
     * @return
     * @see #map2String(java.util.Map, java.lang.String)
     */
    public static String toString( Map<? extends Object, ? extends Object> map )
    {
        return toString( map, UtilString.sSeparator, "=" );
    }

    /**
     * From a Map returns a String with following format:<br>
     * <key1><sep><value1><sep><key2><sep><value2><sep>...<keyN><sep><valueN>
     * <p>
     * Null keys or values are converted to empty String ("").
     *
     * @param map Map to convert into its String representation.
     * @param sEntrySep Map entry separator.
     * @param sPairSep {Key,Value} pair separator.
     * @return A String with above described format.
     */
    public static String toString( Map<? extends Object, ? extends Object> map, String sEntrySep, String sPairSep )
    {
        StringBuilder sb = new StringBuilder( 1024 );

        for( Map.Entry<? extends Object, ? extends Object> entry : map.entrySet() )
        {
            sb.append( (UtilString.isEmpty( entry.getKey() ) ? "" : entry.getKey().toString() ) )
              .append( sPairSep )
              .append( (UtilString.isEmpty( entry.getValue() ) ? "" : entry.getValue().toString() ) )
              .append( sEntrySep );
        }

        sb.deleteCharAt( sb.length() - 1 );   // Borra el último sEntrySep

        return sb.toString();
    }

    /**
     * This makes following call:
     * <pre>
     * return string2Map( s, UtilString.sSeparator, "=" );
     * </pre>
     *
     * @param s
     * @return
     * @see #
     */
    public static Map<String,String> stringToMap( String s )
    {
        return stringToMap( s, UtilString.sSeparator, "=" );
    }

    /**
     * Returns a Map of Strings based on format described in map2String method.
     *
     * @param s String to convert into a Map.
     * @param sEntrySep Map entry separator.
     * @param sPairSep {Key,Value} pair separator.
     * @return The resulting Map.
     */
    public static Map<String,String> stringToMap( String s, String sEntrySep, String sPairSep )
    {
        if( Objects.equals( sEntrySep, sPairSep ) )
        {
            throw new IllegalArgumentException( "Both separators can not be the same." );
        }

        Map<String,String> map    = new HashMap<>();
        String[]           asPair = s.split( sPairSep );

        for( String pair : asPair )
        {
            int nIndex = pair.indexOf( sEntrySep );

            map.put( pair.substring( 0, nIndex ), pair.substring( nIndex + 1 ) );
        }

        return map;
    }

    /**
     * Returns a Map based on passed values assuming that first element is the
     * key and the element at second position is the value and so on.
     *
     * @param pairs { key1,value1, key2,value2, ... keyN,valueN }
     * @return The map represented by passed pairs.
     */
    public static Map<String,String> pairsToMap( Object... pairs )
    {
        Map<String,String> map = new HashMap<>();

        if( pairs.length > 1 )    // If only 1 object is passed: there is no {key,value} pair.
        {
            for( int n = 0; n < pairs.length; n += 2 )
            {
                map.put( pairs[n].toString(), pairs[n+1].toString() );
            }
        }

        return map;
    }

    //----------------------------------------------------------------------------//
    // INNER CLASS
    //----------------------------------------------------------------------------//

    // TODO: ponerle el interrupted (como a las clases de comunicaciones)

    /**
     * Allows to traverse recursively a List, for each element of the list, the
     * implementation of method ::visit( ... ) will be invoked passing to it
     * current element.
     *
     * @param <E>
     * @param <R>
     */
	public static abstract class ListVisitor<E,R>
	{
		private R       result = null;
		private boolean goOn   = true;

		public R getResult()
		{
			return result;
		}

		/**
		 * Recorre recursivamente una lista llamando para cada elemento de la
		 * misma a la implementación del método ::visit( ... )
		 * <p>
		 * Este recorrido se realiza mientras que no se llame al método ::stop()
		 * y no se hayan agotado todos los elementos de la lista.
		 * <p>
		 * Se puede almacenar un resultado llamando a ::setResult( ... ) y este
		 * resultado puede recogerse al finalizar la ejecución llamando a
		 * ::getResult().
		 *
		 * @param list  Lista a recorrer.
		 */
		@SuppressWarnings("unchecked")
        public void exec( List<E> list )
		{
            for( E list1 : list )
            {
                if( list1 instanceof List )
                {
                    exec( (List<E>) list1 );
                }
                else
                {
                    visit( list1 );
                    if( ! goOn )
                    {
                        break;
                    }
                }
            }
		}

		public abstract void visit( E element );

		//-----------------------------------------------------------//

		protected void stop() 	              { goOn = false; }
		protected void setResult( R result )  { this.result = result; }
	}
}