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

import com.peyrona.commons.db.dao.BaseDTO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author peyrona
 */
public class UtilDB
{
    //------------------------------------------------------------------------//
    private UtilDB() {}  // Avoid this class instances creation
    //------------------------------------------------------------------------//

    /**
     * Creates a Statement with appropriate types and using passed connection.
     *
     * @param connection
     * @return
     * @throws java.sql.SQLException
     */
    public static Statement createStatement( Connection connection ) throws SQLException
    {
        return connection.createStatement( ResultSet.TYPE_FORWARD_ONLY,
                                           ResultSet.CONCUR_READ_ONLY );
    }

    /**
     * Creates a PreparedStatement with appropriate types and using passed
     * connection.
     *
     * @param connection
     * @param sql
     * @return
     * @throws java.sql.SQLException
     */
    public static PreparedStatement createPreparedStatement( Connection connection, String sql ) throws SQLException
    {
        String cmd = sql.substring( 0,6 ).toLowerCase();

        if( cmd.equals( "select" ) )
        {
            return connection.prepareStatement( sql,
                                                ResultSet.TYPE_FORWARD_ONLY,
                                                ResultSet.CONCUR_READ_ONLY );
        }
        else
        {
            boolean returnPK = cmd.equals( "insert" );

            return connection.prepareStatement( sql,
                                                (returnPK ? Statement.RETURN_GENERATED_KEYS
                                                          : Statement.NO_GENERATED_KEYS) );
        }
    }

    public static void executeUpdate( PreparedStatement preparedStatement ) throws SQLException
    {
        executeUpdate( preparedStatement, null );
    }

    public static void executeUpdate( PreparedStatement preparedStatement, BaseDTO dto ) throws SQLException
    {
        preparedStatement.executeUpdate();

        if( (dto != null) && dto.isNew() )
        {
            try( ResultSet generatedKeys = preparedStatement.getGeneratedKeys() )
            {
                if( generatedKeys.next() )
                {
                    dto.setPK( generatedKeys.getLong( 1 ) );
                }
                else
                {
                    throw new SQLException( "Update failed: no PK obtained." );
                }
            }
        }
    }

    /**
     * Creates a Statement using this connection and executes passed SQL string
     * command.
     *
     * @param connection
     * @param sCommand
     * @throws java.sql.SQLException
     */
    public static void execute( Connection connection, String sCommand ) throws SQLException
    {
        try( Statement stmt = createStatement( connection ) )
        {
            stmt.execute( sCommand );
        }
    }

    /**
     * Set current (default) schema to the passed one.
     *
     * @param connection
     * @param schema The schema to set.
     * @throws java.sql.SQLException
     */
    public static void setSchema( Connection connection, String schema ) throws SQLException
    {
        execute( connection, "SET SCHEMA "+ schema );
    }

    public static boolean existsSchema( DatabaseMetaData dbmd, String sSchemaName ) throws SQLException
    {// FIXME: esto (al menos en Derby) no funciona
        boolean exists = false;

        sSchemaName = sSchemaName.trim().toUpperCase();

        try( ResultSet rs = dbmd.getCatalogs(); )
        {
            while( rs.next() )
            {
                if( rs.getString( "TABLE_CAT" ).toUpperCase().equals( sSchemaName ) )
                {
                    exists = true;
                    break;
                }
            }
        }

        return exists;
    }

    /**
     * Return true if passed schema does not exists or it contains zero tables,
     * otherwise it returns false.
     *
     * @param dbmd DB metadata
     * @param schema To be checked.
     * @return true if passed schema does not exists or it contains zero tables.
     * @throws SQLException
     */
    public static boolean isSchemaEmpty( DatabaseMetaData dbmd, String schema ) throws SQLException
    {
        boolean empty;

        //if( existsSchema( dbmd, schema) )   // FIXME: esto (al menos en Derby) no funciona
        //{
            try( final ResultSet rs = dbmd.getTables( null, schema.toUpperCase(), null, new String[]{"TABLE"} ) )
            {
                empty = ! rs.next();
            }
        //}

        return empty;
    }

    public static boolean existTable( DatabaseMetaData dbmd, String sTableName ) throws SQLException
    {
        boolean exists = false;

        sTableName = sTableName.trim().toUpperCase();

        try( ResultSet rs = dbmd.getTables( null, null, null, null ) )
        {
            while( rs.next() )
            {
                if( rs.getString( "TABLE_NAME" ).toUpperCase().equals( sTableName ) )
                {
                    exists = true;
                    break;
                }
            }
        }

        return exists;
    }

    public static java.sql.Date createSqlDate()
    {
        return new java.sql.Date( System.currentTimeMillis() );
    }

    public static java.sql.Date createSqlDate( java.util.Date date )
    {
        return ((date == null) ? null : new java.sql.Date( date.getTime() ));
    }

    public static void setLongOrNull( int index, PreparedStatement ps, Long value ) throws SQLException
    {
        if( value == null )
        {
            ps.setNull( index, java.sql.Types.BIGINT );
        }
        else
        {
            ps.setLong( index, value );
        }
    }

    //----------------------------------------------------------------------------//
    // OPERACIONES SOBRE BLOBS

    public static Image readImageFromBlob( ResultSet rs, String column ) throws SQLException
    {
        byte[] binImage = readFromBlob( rs, column );

        return ((binImage == null) ? null : new ImageIcon( binImage ).getImage());
    }

    public static void writeImageToBlob( int column, PreparedStatement psmt, Image image ) throws SQLException, IOException
    {
        ImageIcon icon = null;

        if( image != null )
        {
            icon = new ImageIcon( image );
        }

        writeToBlob( column, psmt, imageToBytes( icon ) );
    }

    public static byte[] readFromBlob( ResultSet rs, String column ) throws SQLException
    {
        byte[] ret  = null;
        Blob   blob = rs.getBlob( column );

        if( blob != null )
        {
            try
            {
                long length = blob.length();

                if( length == 0 )    // Cuando (length == 0) => returns byte[0] en lugar de null
                {
                    ret = new byte[0];
                }
                else
                {
                    ret = blob.getBytes( 1, (int) length );
                }
            }
            finally
            {
                blob.free();
            }
        }

        return ret;
    }

    /**
     * Adds to passed PrepparedStatement (ps.setXXX()) passed byte array.
     * @param index
     * @param ps
     * @param data
     * @throws SQLException
     */
    public static void writeToBlob( int index, PreparedStatement ps, byte[] data ) throws SQLException
    {
        ByteArrayInputStream bais = null;
        int                  len  = 0;

        if( data != null )
        {
            bais = new ByteArrayInputStream( data );
            len  = data.length;
        }

        ps.setBinaryStream( index, bais, len );
    }

    public static byte[] imageToBytes( ImageIcon icon ) throws IOException
    {
        if( icon == null )
        {
            return null;
        }

        Image img = icon.getImage();

        if( img.getWidth( null ) < 1 && img.getHeight( null ) < 1 )
        {
            return null;
        }

        BufferedImage         image = getBufferedImageFromImage( img );
        ByteArrayOutputStream baos  = new ByteArrayOutputStream();

        ImageIO.write( image, "png", baos );

        return baos.toByteArray();
    }

    //----------------------------------------------------------------------------//

    private static BufferedImage getBufferedImageFromImage( Image img )
    {
        // Crea un objeto BufferedImage con el ancho y alto de la Image
        BufferedImage bufferedImage = new BufferedImage( img.getWidth( null ), img.getHeight( null ),
                                                         BufferedImage.TYPE_INT_RGB );
        Graphics g = bufferedImage.createGraphics();
                 g.drawImage( img, 0, 0, null );
                 g.dispose();

        return bufferedImage;
    }
}