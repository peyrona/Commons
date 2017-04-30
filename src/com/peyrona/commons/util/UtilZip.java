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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class UtilZip
{
	//------------------------------------------------------------------------//
    private UtilZip() {}  // Avoid creation of instances of this class
    //------------------------------------------------------------------------//

    public static InputStream getEntryAsInputStream( ZipFile zip, String entryName ) throws IOException
    {
        InputStream is = null;
        ZipEntry    ze = zip.getEntry( entryName );

        if( (ze != null) && (! ze.isDirectory()) )
        {
            is = zip.getInputStream( ze );
        }

        return is;
    }

    /**
     * Return the contents of a ZIP entry as an array of bytes.
     *
     * @param zip Which contains the entry.
     * @param entryName Entry to be retrieved.
     * @return An array of bytes with the contents of the entry or null if the entry does not exists.
     * @throws java.io.IOException
     */
    public static byte[] getEntryAsByteArray( ZipFile zip, String entryName ) throws IOException
    {
        byte[] bytes = null;

        ZipEntry ze = zip.getEntry( entryName );

        if( (ze != null) && (! ze.isDirectory()) )
        {
            bytes = new byte[ (int) ze.getSize() ];

            InputStream is = zip.getInputStream( ze );
                        is.read( bytes, 0, bytes.length );
            is.close();
        }

        return bytes;
    }

    /**
     * Return all entries in passed ZIP file.
     *
     * @param zip
     * @return
     */
    public static List<ZipEntry> getEntries( ZipFile zip )
    {
        return getEntries( zip, null );
    }

    /**
     * Return all entries in passed ZIP file which name matches with passed
     * regular expression.
     *
     * @param zip
     * @param regExpr
     * @return
     */
    public static List<ZipEntry> getEntries( ZipFile zip, String regExpr )
    {
        List<ZipEntry> entries = new ArrayList<>( 64 );
        Pattern        pattern = null;

        if( regExpr != null )
        {
            pattern = Pattern.compile( regExpr );
        }

        for( Enumeration<? extends ZipEntry> enu = zip.entries(); enu.hasMoreElements(); )
        {
            ZipEntry entry = (ZipEntry) enu.nextElement();

            if( pattern != null  )
            {
                Matcher matcher = pattern.matcher( entry.getName() );

                if( matcher.matches() )
                {
                    entries.add( entry );
                }
            }
            else
            {
                entries.add( entry );
            }
        }

        return entries;
    }

    public static void pack( File fZip, File... files ) throws IOException
    {
    	pack( fZip, Arrays.asList( files ) );
    }

    public static void pack( File fZip, List<File> entries ) throws IOException
    {
		ZipOutputStream zos = new ZipOutputStream( new FileOutputStream( fZip ) );

		for( File entry : entries )
		{
			ZipEntry        ze     = new ZipEntry( entry.getName() );
			FileInputStream in     = new FileInputStream( entry );
			byte[]          buffer = new byte[1024*4];
			int             len;

			zos.putNextEntry( ze );

	        while( (len = in.read( buffer )) > 0 )
	        {
	        	zos.write( buffer, 0, len );
	    	}

			in.close();
			zos.closeEntry();
		}

		zos.close();
    }

    public static void packAll( File fDirectory ) throws IOException
    {
    	if( ! fDirectory.isDirectory() )
    	{
    		throw new IOException( fDirectory +" is not a directory.");
    	}

    	// TODO: packAll(...) -> implementarlo
    }

    public static void unpack( File fZip ) throws IOException
    {
    	unpack( fZip, null );
    }

    /**
     *
     * Nota: este método funciona para extraer uno o más ficheros que no forman
     * una estructura jeráquica, es decir, NO funciona para crear un árbol de
     * ficheros y directorios.
     *
     * @param fZip
     * @param whereDir
     * @throws IOException
     */
    // TODO: unpack(...) -> hacerlo bien. Está aquí:
    //       http://examples.javacodegeeks.com/core-java/util/zip/extract-zip-file-with-subdirectories/
    public static void unpack( File fZip, File whereDir ) throws IOException
    {
    	whereDir = ((whereDir == null) || (! whereDir.isDirectory()) || (! whereDir.exists()) ? new File( "." ) : whereDir);

		InputStream    is  = new FileInputStream( fZip );
		ZipInputStream zis = new ZipInputStream( new BufferedInputStream( is ) );
		ZipEntry       ze;

		while( (ze = zis.getNextEntry()) != null )
		{
			ByteArrayOutputStream baos   = new ByteArrayOutputStream();
			FileOutputStream      fout   = new FileOutputStream( new File( whereDir, ze.getName() ) );
			byte[]                buffer = new byte[1024*4];
			int count;

			// reading and writing
			while( (count = zis.read( buffer )) != -1 )
			{
				baos.write( buffer, 0, count );
				byte[] bytes = baos.toByteArray();
				fout.write( bytes );
				baos.reset();
			}

			fout.flush();
			fout.close();
			zis.closeEntry();
		}

		zis.close();
    }
}