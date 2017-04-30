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

package com.peyrona.commons.db;

import com.peyrona.commons.util.UtilIO;
import com.peyrona.commons.util.UtilString;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Procesa los comandos SQL almacenados en un fichero.
 * <p>
 * Los comandos tienen que terminar con ';'.<br>
 * Los comentarios ("--") y las líneas en blanco son ignoradas.
 *
 * @author Francisco Morero Peyrona
 */
public final class SQLExecutor
{
    private final static String sSQL_COMMENT = "--";
    private final static String sSQL_EOL     = ";";

    private Connection   dbConn;
    private List<String> lines;    // One element per each line in the file or Reader.
    private int          nLine = 0;

    //----------------------------------------------------------------------------//

    public SQLExecutor( Connection conn, String fileName ) throws IOException
    {
        this( conn, new File( fileName ) );
    }

    public SQLExecutor( Connection conn, File file ) throws IOException
    {
        this( conn, new FileInputStream( file ) );
    }

    public SQLExecutor( Connection conn, InputStream is ) throws IOException
    {
        this( conn, UtilIO.loadLines( is, false ) );   // false pq estas líneas puede llegar directamente y hay q hacerles trim()
    }

    public SQLExecutor( Connection conn, List<String> sqlCommands )
    {
        dbConn = conn;
        lines  = (sqlCommands == null ? new ArrayList<String>() : sqlCommands);
    }

    //----------------------------------------------------------------------------//

    public Map<String,SQLException> execute()
    {
        return execute( null );
    }

    public Map<String,SQLException> execute( OutputStream errorLog )
    {
        if( dbConn == null )
        {
            throw new IllegalArgumentException( "DB connection can't be null" );
        }

        Map<String,SQLException> errors = new HashMap<>();

        try( Statement stmt = dbConn.createStatement() )
        {
            String sql;

            while( (sql = getNextSQL()) != null )
            {
                try
                {
                    stmt.execute( sql );
                }
                catch( SQLException exc )
                {
                    errors.put( sql, exc );
                }
            }
        }
        catch( SQLException se )
        {
            errors.put( "Error creating SQL Statement.", se );
        }

        if( (errorLog != null) && (! errors.isEmpty()) )
        {
            try
            {
                for( Map.Entry<String,SQLException> entry : errors.entrySet() )
                {
                    errorLog.write( ("Error executing: "+ entry.getKey() + UtilString.sEOL).getBytes() );
                    errorLog.write( ("SQL error is: "+ entry.getValue().getLocalizedMessage() + UtilString.sEOL).getBytes() );
                }

                errorLog.flush(); // No lo puedo cerrar (errorLog.close()) porque es un parámetro y hay que dejarlo como estaba
            }
            catch( IOException ex )
            {
                // Nothing to do
            }
        }

        return errors;
    }

    //----------------------------------------------------------------------------//

    /**
     * Returns sequentally next SQL command or null if there are no more.
     *
     * @return Next SQL command or null if there are no more.
     */
    protected String getNextSQL()
    {
        StringBuilder sb    = new StringBuilder( 1024 );
        String        sLine = null;

        while( nLine < lines.size() )
        {
            sLine = lines.get( nLine++ );

            if( isSQL( sLine ) )
            {
                sLine = removeInLineComment( sLine );

                sb.append( sLine );

                if( sLine.endsWith( sSQL_EOL ) )
                {
                    sb.deleteCharAt( sb.length() - 1 );    // Borra la terminación (";")
                    sLine = sb.toString();
                    sb.setLength( 0 );
                    break;
                }
                else
                {
                    sb.append( " " );  // Hay q añadir " " para q la lin no se pegue a la sig.
                }
            }
        }

        return (isSQL( sLine ) ? sLine : null);   // Hay q volver a comprobar si es una SQL
    }

    protected boolean isSQL( String sLine )
    {
        sLine = sLine.trim();    // El trim() hay que hacerlo aquí dentro

        return (sLine.length() > 0) &&
               (! sLine.startsWith( sSQL_COMMENT ));
    }

    protected String removeInLineComment( String s )
    {
        int index = s.indexOf( sSQL_COMMENT );

        return ((index > - 1) ?  s.substring( 0, index ) : s).trim();    // Fundamental este trim()
    }
}