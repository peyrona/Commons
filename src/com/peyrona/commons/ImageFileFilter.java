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
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author peyrona
 */
public class ImageFileFilter extends FileFilter
{
    public static final Set<String> validFormats = init();

    //----------------------------------------------------------------------------//

    @Override
    public boolean accept( File file )
    {
        if( file == null )
        {
            return false;
        }

        if( file.isDirectory() )
        {
            return true;
        }

        String sExtension = UtilIO.getFileExtension( file ).toLowerCase();

        return validFormats.contains( sExtension );
    }

    @Override
    public String getDescription()
    {
        return "Images";
    }

    //----------------------------------------------------------------------------//

    private static Set<String> init()
    {
        Set<String> lst = new HashSet<>();
                    lst.add( "tiff" );
                    lst.add( "tif"  );
                    lst.add( "gif"  );
                    lst.add( "jpeg" );
                    lst.add( "jpg"  );
                    lst.add( "png"  );

        return Collections.unmodifiableSet( lst );
    }
}