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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 *
 * @author peyrona
 */
public class UtilIO
{   // NEXT: Rehacer esta clase con el NIO 2 de la ver. 7

    //------------------------------------------------------------------------//
    protected UtilIO() {}  // Avoid creation of instances of this class
    //------------------------------------------------------------------------//

   /**
    * Devuelve una cadena donde se han sustituido todos los caracteres posiblemente
    * conflictivos en cualquier SO donde puede correr Java, por "_".
    * <p>
    * Sólo se normaliza el nombre, no la extensión, si es que la tuviese.
    *
    * @param fileName
    * @return El nombre de fichero normalizado.
    */
    public static String normalizeFileName( String fileName )
    {
        if( UtilString.isEmpty( fileName ) )
        {
            throw new IllegalArgumentException( "["+ fileName +"] invalid file name." );
        }

        final char XX = '_';

        String sExtension = getFileExtension( fileName );

        if( UtilString.isNotEmpty( sExtension ) )
        {
            fileName = getFileName( fileName );
        }

        char[] chars = fileName.toCharArray();

        for( int n = 0; n < chars.length; n++ )
        {
            if( chars[n] < '0' )
            {
                chars[n] = XX;
            }
            else if( chars[n] > '9' && chars[n] < 'A' )
            {
                chars[n] = XX;
            }
            else if( chars[n] > 'Z' && chars[n] < 'a' )
            {
                chars[n] = XX;
            }
            else if( chars[n] > 'z' )
            {
                chars[n] = XX;
            }
        }

        return String.copyValueOf( chars ) + sExtension;
    }

   /**
     * Devuelve el nombre del fichero (sin la extensión).
     *
     * @param file file Fichero del que se quiere su nombre simple.
     * @return Devuelve el nombre del fichero (sin la extensión).
     */
    public static String getFileName( final File file )
    {
        final String name  = file.getName();
        final int    index = name.lastIndexOf( '.' );

        return ((index == -1) ? name : name.substring( 0, index ));
    }

    /**
     * Devuelve el nombre del fichero (sin la extensión).
     *
     * @param file Nombre completo de fichero del que se quiere su nombre simple.
     * @return Devuelve el nombre del fichero (sin la extensión).
     */
    public static String getFileName( final String file )
    {
        return getFileName( new File( file ) );
    }

    /**
     * Returns the file extension without (not including) the '.' or "" if file
     * has no extension.
     *
     * @param file
     * @return
     */
    public static String getFileExtension( final File file )
    {
        final String name  = file.getName();
        final int    index = name.lastIndexOf( '.' );

        return (((index == -1) || name.endsWith( "." )) ? "" : name.substring( index+1 ));
    }

    /**
     * Returns the file extension without (not including) the '.' or "" if file
     * has no extension.
     *
     * @param file
     * @return
     */
    public static String getFileExtension( final String file )
    {
        return getFileExtension( new File( file ) );
    }

    public static InputStream fromStringsToInputStream( String...  strs )
    {
        StringBuilder sb = new StringBuilder( 1024 * strs.length );

        for( String s : strs )
        {
            sb.append( s );
        }

        return new ByteArrayInputStream( sb.toString().getBytes() );
    }

    public static String load( String fileName ) throws IOException
    {
        return load( new File( fileName ) );
    }

    public static String load( File file ) throws IOException
    {
        if( file.exists() )
        {
            if( file.canRead() )
            {
                try( FileInputStream fis = new FileInputStream( file ) )
                {
                    return load( fis );
                }
            }
            else
            {
                throw new IOException( "Requested file can not be readed: '"+ file +"'" );
            }
        }

        throw new IOException( "Requested file does not exists: '"+ file +"'" );
    }

    public static String load( InputStream is ) throws IOException
    {
        byte readBuf[] = new byte[32*1024];

        try( ByteArrayOutputStream bout = new ByteArrayOutputStream( 32*1024 ) )
        {
            int count = is.read( readBuf );

            while( count > 0 )
            {
                bout.write( readBuf, 0, count );
                count = is.read( readBuf );
            }

            return bout.toString();
        }
    }

    public static List<String> loadLines( InputStream is, boolean trim ) throws IOException
    {
        List<String> lines = new ArrayList<>( 1024*8 );

        try( Scanner scan = new Scanner( is ) )
        {
            while( scan.hasNextLine() )
            {
                String line = scan.nextLine();

                lines.add( (trim ? line.trim() : line) );
            }
        }

        return lines;
    }

    public static List<String> loadLines( Readable reader ) throws IOException
    {
        List<String> lines = new ArrayList<>( 1024*8 );

        try( Scanner scan = new Scanner( reader ) )
        {
            while( scan.hasNextLine() )
            {
                lines.add( scan.nextLine() );
            }
        }

        return lines;
    }

    public static void save( File file, String data ) throws IOException
    {
        save( file, data, false );
    }

    public static void save( File file, String data, boolean append ) throws IOException
    {
        try( FileWriter fw = new FileWriter( file, append ); )
        {
            fw.write( data );
            fw.flush();
        }
    }

    public static void save( File file, byte[] data ) throws IOException
    {
        save( file, data, false );
    }

    public static void save( File file, byte[] data, boolean append ) throws IOException
    {
        try( FileOutputStream fos = new FileOutputStream( file, append ); )
        {
            fos.write( data );
            fos.flush();
        }
    }

    public static byte[] serializeObject( Object obj ) throws IOException
    {
        try
        (
            ByteArrayOutputStream baos = new ByteArrayOutputStream( 1024 * 32 );
            ObjectOutputStream    oos  = new ObjectOutputStream( baos );
        )
        {
            oos.writeObject( obj );
            return baos.toByteArray();
        }
    }

    public static Object deserializeObject( byte[] doc ) throws IOException, ClassNotFoundException
    {
        try
        (
            ByteArrayInputStream bais = new ByteArrayInputStream( doc );
            ObjectInputStream    ois  = new ObjectInputStream( bais );
        )
        {
            return ois.readObject();
        }
    }

    /**
     * Creates the folder identfied by passed File (and its previous folders) if
     * it did not existed.
     *
     * @param folder
     * @return
     */
    public static boolean createFolder( File folder )
    {
        boolean bSuccess = true;

        if( folder.exists() )
        {
            if( folder.isFile() )
            {
                bSuccess = false;
            }
        }
        else
        {
            if( ! folder.mkdirs() )
            {
                bSuccess = false;
            }
        }

        return bSuccess;
    }

    /**
     * Returns the checksum for passed byte array.
     *
     * @param bytes
     * @return Returns the checksum for passes byte array.
     */
    public static long getChecksum( byte[] bytes )
    {
        if( bytes == null )
        {
            return -1;
        }

        Checksum checksum = new CRC32();
                 checksum.update( bytes, 0, bytes.length );

        return checksum.getValue();
    }
}