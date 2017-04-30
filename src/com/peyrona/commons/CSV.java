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

import com.peyrona.commons.util.UtilIO;
import com.peyrona.commons.util.UtilString;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class CSV
{
    private char columSep = ',';   // Traditional defaul CSV column    separator
    private char delimSep = '"';   // Traditional defaul CSV delimiter separator

    //------------------------------------------------------------------------//

    public CSV setColumnSeparator( char c )
    {
        columSep = c;

        return this;
    }

    public CSV setDelimiterSeparator( char c )
    {
        delimSep = c;

        return this;
    }

    /**
     * Devuelve una cadena en formato CSV con las cadenas pasadas.
     *
     * @param values
     * @return
     */
    public String toCSV( Object... values )
    {
        StringBuilder sb = new StringBuilder( 2048 );

        for( Object val : values )
        {
            String sVal = ((val == null) ? "null" : val.toString());

            boolean hasColumSep = sVal.indexOf( columSep ) > -1;

            if( hasColumSep )
            {
                sb.append( delimSep );
            }

            sb.append( sVal );

            if( hasColumSep )
            {
                sb.append( delimSep );
            }

            sb.append( columSep );
        }

        return sb.toString().substring( 0, sb.length() - 1 );    // -1 --> Quita la última coma
    }

    /**
     * Devuelve un array de cadenas en base a una tupa codificada en formato CSV.
     *
     * @param tupla
     * @return
     */
    public String[] fromCSV( String tupla )
    {
        List<String>  list    = new ArrayList<>();
        char[]        acTupla = tupla.toCharArray();
        boolean       isInStr = false;                      // true desde que se detecta la " de inicio hasta que se detecta la " de final
        StringBuilder value   = new StringBuilder( 64 );

        for( char c : acTupla )
        {
            if( c == columSep )
            {
                if( isInStr )
                {
                    value.append( c );
                }
                else
                {
                    list.add( value.toString() );
                    value.setLength( 0 );
                }
            }
            else if( c == delimSep )
            {
                isInStr = ! isInStr;
            }
            else
            {
                value.append( c );
            }
        }

        list.add( value.toString() );

        return list.toArray( new String[0] );
    }

    /**
     * Devuelve una lista con tantos elementos como líneas tiene el fichero y
     * cada línea es un array de cadenas con tantos elementos como columnas
     * tiene la línea.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public List<String[]> read( File file ) throws IOException
    {
        if( file.exists() && file.canRead() )
        {
            return read( new FileReader( file ) );
        }

        return new ArrayList<>();
    }

    public List<String[]> read( Reader reader ) throws IOException
    {
        List<String[]> list  = new ArrayList<>();
        List<String>   lines = UtilIO.loadLines( reader );

        for( String line : lines )
        {
            if( UtilString.isNotEmpty( line ) )
            {
                list.add( fromCSV( line ) );
            }
        }

        return list;
    }

    public void write( File file, List<Object[]> tuplas ) throws IOException
    {
        write( new FileWriter( file ), tuplas );
    }

    public void write( Writer writer, List<Object[]> tuplas ) throws IOException
    {
        try( BufferedWriter bw = new BufferedWriter( writer ) )
        {
            for( Object[] tupla : tuplas )
            {
                bw.write( toCSV( tupla ) );
                bw.newLine();
            }

            bw.flush();
        }
    }

    /**
     * Abre el fichero, añade una línea al final y lo cierra.
     *
     * @param file
     * @param values
     * @throws IOException
     */
    public void append( File file, Object... values ) throws IOException
    {
        try( BufferedWriter bw = new BufferedWriter( new FileWriter( file, true ) ) )
        {
            bw.write( toCSV( values ) );
            bw.newLine();
            bw.flush();
        }
    }
}