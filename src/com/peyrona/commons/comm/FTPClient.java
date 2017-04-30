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

package com.peyrona.commons.comm;

import com.peyrona.commons.util.UtilDebug;
import com.peyrona.commons.util.UtilIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

/**
 * A simplistic FTP client based on java.net.URLConnection.
 *
 * @author peyrona
 */
public final class FTPClient
{
    public static enum FileType { Binary,       // i --> Image – binary mode
                                  Text }        // a --> Ascii – text mode
                               // Directory };  // d --> Directory listing   --> not in use. See ::list()

    //----------------------------------------------------------------------------//

    private static final String FTP_URL_MASK = "ftp://%s:%s@%s/%s;type=%s";
    private static final int    BUFFER_SIZE  = 1024*8;

    private final String host;
    private final String user;
    private final String pass;

    //----------------------------------------------------------------------------//

    public FTPClient( String host, String user, String pass )
    {
        this.host = host;
        this.user = user;
        this.pass = pass;
    }

    //----------------------------------------------------------------------------//

    public void upload( File file, FileType type, String hostPath ) throws IOException
    {
        InputStream is = UtilIO.fromStringsToInputStream( UtilIO.load( file ) );

        upload( is, file.getName(), type, hostPath );
    }

    /**
     * Este método lanza una thread para subir el contenido del stream y cuando
     * termina lo cierra.
     *
     * @param is
     * @param fileName
     * @param type
     * @param hostPath
     */
    public void upload( final InputStream is, final String fileName, final FileType type, final String hostPath )
    {
        new Thread( new Runnable()
        {
             @Override
             public void run()
             {
                try
                {
                    String sUrl = String.format( FTP_URL_MASK, user, pass, host, getPath( hostPath, fileName ), getType( type ) );
                    URL    url  = new URL( sUrl );
                    URLConnection conn = url.openConnection();

                    try( OutputStream os = conn.getOutputStream() )
                    {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;

                        while( (bytesRead = is.read( buffer )) != -1 )
                        {
                            os.write( buffer, 0, bytesRead );
                        }

                        is.close();
                    }
                }
                catch( Exception ex )
                {
                    UtilDebug.log( ex );
                }
             }
         }, "FTPClient:upload" ).start();
    }

    public void download( String hostPathAndFileName, FileType type, File localFile ) throws IOException
    {
        throw new UnsupportedOperationException( "Operation not yet implemented" );
    }

    public Set<File> list( String hostPath, boolean recursive ) throws IOException
    {
        throw new UnsupportedOperationException( "Operation not yet implemented" );
    }

    //----------------------------------------------------------------------------//

    private String getPath( String hostPath, String fileName )
    {
        hostPath = (hostPath.endsWith( "/" ) ? "" : "/");

        return hostPath + fileName;
    }

    private String getType( FileType type )
    {
        return type == FileType.Binary ? "i" : "a";
    }
}