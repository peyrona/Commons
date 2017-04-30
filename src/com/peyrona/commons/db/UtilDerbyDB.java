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

import com.peyrona.commons.util.UtilDebug;
import com.peyrona.commons.util.UtilIO;
import com.peyrona.commons.util.UtilString;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author peyrona
 */
public class UtilDerbyDB
{
    // If a database is created in Derby using the embedded driver and no user name is specified,
    // the default schema used becomes APP. Therefore any tables created in the database have a
    // schema name of APP. However, when creating a Derby database using the Network Server, the
    // value for the schema becomes the value of the username used to connect with as part of the
    // database URL.

    public static final String DRIVER_NETWORK  = "org.apache.derby.jdbc.ClientDriver";
    public static final String DRIVER_EMBEDDED = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String URL_PREFIX      = "jdbc:derby://";

    public enum Driver { Embedded, Network };

    //------------------------------------------------------------------------//
    private UtilDerbyDB() {}  // Avoid this class instances creation
    //------------------------------------------------------------------------//

    public static void loadDriver( Driver driver )
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class.forName( ((driver == Driver.Embedded) ? DRIVER_EMBEDDED : DRIVER_NETWORK) ).newInstance();
    }

    public static void unloadDriver( String url )
    {
        try
        {
            DriverManager.deregisterDriver( DriverManager.getDriver( URL_PREFIX + url ) );
        }
        catch( SQLException ex )
        {
            UtilDebug.log( ex );
        }
    }

    public static Connection createConnection( String url, int port, String dbName, Properties properties ) throws SQLException
    {
        String s = URL_PREFIX + url +":"+ port +"/"+ dbName;

        return DriverManager.getConnection( s, properties );
    }

    public static Properties getDefaultProperties( String databaseName )
    {
        return getDefaultProperties( databaseName, null, null );
    }

    public static Properties getDefaultProperties( String databaseName, String user, String password )
    {
        Properties p = new Properties();
                   p.put( "serverName"  , "localhost" );
                   p.put( "portNumber"  , "1527" );
                   p.put( "databaseName", databaseName );
                   p.put( "create"      , "true" );
                   p.put( "user"        , (UtilString.isEmpty( user     ) ? "" : user) );
                   p.put( "password"    , (UtilString.isEmpty( password ) ? "" : password) );

        return p;
    }

    /**
     * Set the Derby home folder to <code>new File( System.getProperty( "user.home" ), "databases" )</code>
     *
     * @throws IOException
     */
    public static void setHomeFolder() throws IOException
    {
        setHomeFolder( new File( System.getProperty( "user.home" ), "databases" ) );
    }
    public static void setHomeFolder( String dbFolder ) throws IOException
    {
        setHomeFolder( new File( dbFolder ) );
    }

    public static void setHomeFolder( File dbFolder ) throws IOException
    {
        if( UtilIO.createFolder( dbFolder ) )
        {
            System.setProperty( "derby.system.home", dbFolder.getAbsolutePath() );
        }
        else
        {
            throw new IOException( "Can't create DB folder: '"+ dbFolder +"'" );
        }
    }

    public static void switchOffServer()
    {
        try
        {
            DriverManager.getConnection( "jdbc:derby:;shutdown=true" );
        }
        catch( SQLException ex )
        {
            // El shutdown de Derby lanza una exception: es parte del funcionamiento normal del mismo.
            // ex.printStackTrace( System.err );
        }
    }

    public static String getDefaultSchema()
    {
        return "APP";
    }
}
