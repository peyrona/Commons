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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class allows to easily work with debugging tasks in applications.
 * <p>
 * Where are log files stored:<br>
 * If the System property "logs.dir" is defined, then it will be used,
 * otherwise the behaviour will be as follows: when running inside an
 * AppServer (only Tomcat and Glassfish are recognized) it will be:
 * <code>File( System.getProperty( "user.home" ), "logs" )</code><br>
 * And when runnning as an independent application it will be:
 * <code>File( System.getProperty( "user.dir" ), "logs" )</code>.
 *
 * Created by peyrona on 21/07/13.
 */
public final class UtilDebug
{
    private static final String  sLOG_EXT    = ".log.txt";      // Extensión para el nombre de los ficheros Logs
    private static final File    fLOG_FOLDER = initLogFolder(); // This method has to be invoked prior to "initLogger()"
    private static final Logger  MYLOGGER    = initLogger();    // This methos uses LOG_FOLDER
	private static       boolean isDebugging = false;           // Default value, apps should set their own

    //------------------------------------------------------------------------//
    private UtilDebug() {}  // Avoid creation of instances of this class
    //------------------------------------------------------------------------//

    /**
     * Set the internal flag that indicates if application is running or not in
     * debug mode.
     *
     * @param debugging
     */
    public static void setDebugging( final boolean debugging )
    {
    	isDebugging = debugging;
    }

    /**
     * Returns an internal flag that indicates if application is running or not
     * in debug mode.
     *
     * @return true if application is running in debug mode.
     */
    public static boolean isDebugging()
    {
        return isDebugging;
    }

    /**
     * Muestra por consola la cadena pasada sólo si ::isDebugging() == true.
     *
     * @param string
     */
    public static void debuggingPrint( String... string )
    {
        if( isDebugging() )
        {
            for( String s : string )
            {
                System.out.println( s );
            }
        }
    }

    /**
     * Imprime los objetos pasados precediendo el nombre de la clase y del
     * método desde el que se llamó aquí, sólo si ::isDebugging() == true.
     *
     * @param o2print
     */
    public static void debuggingTrace( Object... o2print )
    {
        if( isDebugging() )
        {
            _trace_( o2print );
        }
    }

    //------------------------------------------------------------------------//
    // WORKING WITH LOG FILES

    public static List<File> getLogs()
    {
        List<File> lstLogs = new ArrayList<>();

        if( fLOG_FOLDER.exists() )   // _Almost_ always it will exists
        {
            lstLogs.addAll( Arrays.asList( fLOG_FOLDER.listFiles() ) );

            for( Iterator<File> itera = lstLogs.iterator(); itera.hasNext(); )
            {
                File f = itera.next();

                if( (! f.isFile()) ||
                    (! f.getName().endsWith( sLOG_EXT )) )   // The user could place other files in this folder
                {
                    itera.remove();
                }
            }
        }

        lstLogs.sort( null );  // Can be null because File implements comparator interface

        return lstLogs;
    }

    public static boolean deleteLog( String sName )
    {
        return (new File( fLOG_FOLDER, sName )).delete();
    }

    /**
     * Delete all log files.
     */
    public static void deleteAllLogs()
    {
        deleteAllLogs( 0 );
    }

    /**
     * Deletes Log files older than the argument (in days).
     *
     * @param days Number of days to decide which files has to be deleted.
     */
    public static void deleteAllLogs( long days )
    {
        long       now      = System.currentTimeMillis();
        long       max      = UtilConvert.DAY * days;          // Tiene q ser un long (el int sale negativo)
        List<File> lstFiles = getLogs();

        for( File f : lstFiles )
        {
            if( (now - f.lastModified()) > max )
            {
                f.delete();
            }
        }
    }

    //----------------------------------------------------------------------------//
    // LOGGING

    public static Level getLevel()
    {
        return MYLOGGER.getLevel();
    }

    public static void setLogLevel( Level level )
    {
        MYLOGGER.setLevel( level );
    }

    public static void log( String sMessage )
    {
        log( null, null, sMessage, false );
    }

    public static void log( Level level, String sMessage )
    {
        log( level, null, sMessage, false );
    }

    public static void log( Throwable th )
    {
        log( Level.SEVERE, th, null, false );
    }

    public static void log( Throwable th, String sMessage )
    {
        log( Level.SEVERE, th, sMessage, false );
    }

    public static void log( Level level, Throwable th )
    {
        log( Level.SEVERE, th, null, false );
    }

    public static void log( Level level, Throwable th, String sMessage )
    {
        log( level, th, sMessage, false );
    }

    public static void log( Level level, Throwable th, String sMessage, boolean bExit )
    {
        if( level == null )
        {
            level = java.util.logging.Level.WARNING;
        }

        if( MYLOGGER.isLoggable( level ) )
        {
            if( isDebugging() )    // Sólo se saca por consola si NO se está en producción
            {
                if( sMessage != null)
                {
                    System.out.println( sMessage );
                }

                if( th != null )
                {
                    th.printStackTrace( System.err );
                }
            }

            if( (sMessage != null) || (th != null) )
            {
                MYLOGGER.log( level, sMessage, th );
            }

            if( bExit )
            {
                System.exit( 1 );
            }
        }
    }

    //----------------------------------------------------------------------------//
    // AUX FUNCTIONS

    /**
     * Inicializa el sistema de Logging en base a un fichero de Properties y si
     * este no existiese, se utilizan ciertos valores por defecto.
     * <p>
     * Véase:
     *         http://www.hildeberto.com/2009/04/using-java-logging-configuration-file.html
     *         http://java.ociweb.com/mark/programming/JavaLogging.html
     *
     * @return El logger inicializado.
     */
    private static Logger initLogger()
    {
        Logger logger = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
               logger.setLevel( Level.FINEST );

        try
        {
            UtilIO.createFolder( fLOG_FOLDER );   // It is checked first if folder exists.

            FileHandler fh = new FileHandler( fLOG_FOLDER +"/application-%u.%g"+ sLOG_EXT, 5*1000*1024, 9, true );
                        fh.setFormatter( new SimpleFormatter() );

            logger.addHandler( fh );
        }
        catch( IOException ex )
        {
            System.err.println( "Error while configuring "+ UtilDebug.class.getSimpleName() +"::Logger." );
            ex.printStackTrace( System.err );
        }

        return logger;
    }

    /**
     * Sets the folder where the log files will be created.
     * <p>
     * If the System property "logs.dir" is defined, then it will be used,
     * otherwise it will act as follows:when running inside an AppServer
     * (only Tomcat and Glassfish are recognized) it will be:
     * <code>File( System.getProperty( "user.home" ), "logs" )</code><br>
     * And when runnning as an independent application it will be:
     * <code>File( System.getProperty( "user.dir" ), "logs" )</code>.
     */
    private static File initLogFolder()
    {
        File   fLogs;
        String sFolder = System.getProperty( "logs.dir", null );

        if( sFolder != null )
        {
            fLogs = new File( sFolder );
        }
        else if( isRunningUnderTomcat() || isRunningUnderGlassfish() )
        {
            fLogs = new File( System.getProperty( "user.home" ), "logs" );
        }
        else
        {
            fLogs = new File( System.getProperty( "user.dir" ), "logs" );
        }

        return fLogs;
    }

    private static boolean isRunningUnderTomcat()
    {
        boolean tomcat = _detect_( "/org/apache/catalina/startup/Bootstrap.class" );

        if( ! tomcat )
        {
            tomcat = _detect_( "/org/apache/catalina/startup/Embedded.class" );
        }

        return tomcat;
    }

    private static boolean isRunningUnderGlassfish()
    {
        String value = System.getProperty( "com.sun.aas.instanceRoot" );

        return (value != null);
    }

    private static boolean _detect_( String className )
    {
        try
        {
            ClassLoader.getSystemClassLoader().loadClass( className );

            return true;
        }
        catch( ClassNotFoundException cnfe )
        {
            UtilDebug ud = new UtilDebug();

            Class<?> c = ud.getClass();

            return (c.getResource( className ) != null);
        }
    }

    private static void _trace_( Object... objs )
    {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[3];

        System.out.print( ste.getClassName()  +"."+ ste.getMethodName() +"()[line:"+ ste.getLineNumber() +"] --> " );

        if( objs != null )
        {
            for( Object o : objs )
            {
                System.out.print( "{"+ ((o == null) ? "null" : o.toString()) +"} " );
            }
        }

        System.out.println();
    }
}