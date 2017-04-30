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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

/**
 * Basic image manipulation.
 *
 * @author peyrona
 */
public final class ImageProcessor
{
    private final File fImage;
    private final AtomicReference<BufferedImage> oImage = new AtomicReference<>();

    //----------------------------------------------------------------------------//

    public ImageProcessor( String imageName ) throws IOException
    {
        this( new File( imageName ) );
    }

    public ImageProcessor( File imageFile ) throws IOException
    {
        if( ! imageFile.exists() )
        {
            throw new IllegalArgumentException( "File does not exist: '"+ imageFile.getAbsolutePath() +"'" );
        }

        fImage = imageFile;
        oImage.set( ImageIO.read( fImage ) );
    }

    public int getWidth()
    {
        return oImage.get().getWidth();
    }

    public int getHeight()
    {
        return oImage.get().getHeight();
    }

    /**
     * Sets the thumbnail width: the height will be calculated to keep
     * proprotions of the original image.
     *
     * @param width
     * @return
     */
    public ImageProcessor setWidth( int width )
    {
        resizeProportionally( width, -1 );
        return this;
    }

    /**
     * Sets the thumbnail height: the width will be calculated to keep
     * proprotions of the original image.
     *
     * @param height
     * @return this
     */
    public ImageProcessor setHeight( int height )
    {
        resizeProportionally( -1, height );
        return this;
    }

    /**
     * Sets both dimensions: width and height. Image will be scaled (streched or
     * shrinked) to fit to its new size.
     *
     * @param width
     * @param height
     * @return
     */
    public ImageProcessor setSize( int width, int height )
    {
        oImage.set( scaled( oImage.get(), width, height ) );

        return this;
    }

    public ImageProcessor crop( int x, int y, int width, int height )
    {
        oImage.set( oImage.get().getSubimage( x, y, width, height ) );
        return this;
    }

    /**
     * Crops the center of the image, taken as meassure its shortest size.
     *
     * @return this
     */
    public ImageProcessor cropSquare()
    {
        if( getWidth() == getHeight() )
        {
            return this;
        }

        int width;
        int height;

        if( getHeight() < getWidth() )
        {
            width = height = getHeight();
        }
        else
        {
            height = width = getWidth();
        }

        int x = (getWidth()  - width ) / 2;
        int y = (getHeight() - height) / 2;

        return crop( x, y, width, height );
    }

    /**
     * Replaces image file.
     *
     * @return this
     * @throws IOException
     */
    public ImageProcessor save() throws IOException
    {
        return save( fImage );
    }

    /**
     * Saves image in same folder as original with a new name.
     * <p>
     * If extension is provided, it will be used as image file format, otherwise
     * the original file extension (and therefore format) will be used.
     *
     * @param imageName
     * @return this
     * @throws IOException
     */
    public ImageProcessor save( String imageName ) throws IOException
    {
        return save( new File( fImage.getParentFile(), imageName ) );
    }

    /**
     * Saves image in folder and with name described by passed File.
     * <p>
     * If extension is provided, it will be used as image file format, otherwise
     * the original file extension (and therefore format) will be used.
     *
     * @param fImage
     * @return this
     * @throws IOException
     */
    public ImageProcessor save( File fImage ) throws IOException
    {
        String sExt = UtilIO.getFileExtension( fImage );

        if( sExt.isEmpty() )
        {
            sExt = UtilIO.getFileExtension( fImage );

            if( sExt.isEmpty() )   // Todavía puede serlo pq fImage no tenga extensión
            {
                sExt = "jpg";
            }

            fImage = new File( fImage.getAbsolutePath() +'.'+ sExt );
        }

        if( ! ImageFileFilter.validFormats.contains( sExt ) )
        {
            throw new IllegalArgumentException( sExt +": is not a valid image format." );
        }

        ImageIO.write( oImage.get(), sExt.toLowerCase(), fImage );

        return this;
    }

    //----------------------------------------------------------------------------//

    private void resizeProportionally( int width, int height )
    {
        if( width == -1 )   // Known height we calculate width
        {
            double ratio = ((double) height / (double) getHeight());

            width = (int) (getWidth() * ratio);
        }
        else                 // Known width we calculate height
        {
            double ratio = ((double) width / (double) getWidth());

            height = (int) (getHeight() * ratio);
        }

        oImage.set( scaled( oImage.get(), width, height ) );
    }

    private BufferedImage scaled( BufferedImage img, int width, int height )
    {
        return getScaledInstance( img, width, height, true );
    }

    private BufferedImage getScaledInstance(BufferedImage img,
                                            int targetWidth,
                                            int targetHeight,
                                            boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE)
                   ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if( higherQuality )
        {
        // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        }
        else
        {
        // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do
        {
            if( higherQuality && w > targetWidth )
            {
                w /= 2;
                if( w < targetWidth )
                {
                    w = targetWidth;
                }
            }

            if( higherQuality && h > targetHeight )
            {
                h /= 2;
                if( h < targetHeight )
                {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage( w, h, type );
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
            g2.drawImage( ret, 0, 0, w, h, null );
            g2.dispose();

            ret = tmp;
        }
        while( w != targetWidth || h != targetHeight );

        return ret;
    }
}