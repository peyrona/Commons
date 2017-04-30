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

import java.io.File;
import java.io.IOException;

/**
 *
 * @author peyrona
 */
public class JavaTest
{
    public static void main( String[] as ) throws IOException
    {
        final int     mb = 1024 * 1024;
        final Runtime rt = Runtime.getRuntime();

        System.out.println( "This is a test to check that JRE/JDK is installed and working properly." );
        System.out.println();

        System.out.println( "General information" );
        System.out.println( "\tOS name        : " + System.getProperty( "os.name" ) );
        System.out.println( "\tOS version     : " + System.getProperty( "os.version" ) );
        System.out.println( "\tOS architecture: " + System.getProperty( "os.arch" ) );
        System.out.println( "\tProcessors     : " + rt.availableProcessors() + " cores" );
        System.out.println( "\tUser name      : " + System.getProperty( "user.name" ) );
        System.out.println();

        System.out.println( "Java information" );
        System.out.println( "\tJava home   : " + System.getProperty( "java.home" ) );
        System.out.println( "\tJava vendor : " + System.getProperty( "java.vendor" ) );
        System.out.println( "\tJava version: " + System.getProperty( "java.version" ) );
        System.out.println();

        System.out.println( "Memory (in Mb)" );
        System.out.println( "\tTotal  : " + rt.totalMemory() / mb);
        System.out.println( "\tFree   : " + rt.freeMemory() / mb );
        System.out.println( "\tUsed   : " + (rt.totalMemory() - rt.freeMemory()) / mb);
        System.out.println( "\tMaximun: " + rt.maxMemory() / mb);
        System.out.println();

        System.out.println( "File System (in Mb)" );
        File[] roots = File.listRoots();   // Get a list of all filesystem roots on this system

        for( File root : roots )   // For each filesystem root, print some info
        {
            System.out.println( "\tFS path     : " + root.getAbsolutePath() );
            System.out.println( "\tTotal space : " + root.getTotalSpace() / mb );
            System.out.println( "\tFree space  : " + root.getFreeSpace() / mb );
            System.out.println( "\tUsable space: " + root.getUsableSpace() / mb );
            System.out.println( "----------------------------------" );
        }
    }
}