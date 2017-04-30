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

import com.peyrona.commons.util.UtilString;
import java.util.Arrays;

/**
 * Esta clase define una MAC address.
 *
 * @author peyrona
 */
public final class MAC
{
    private final String[] mac;

    //----------------------------------------------------------------------------//

    /**
     * Constructor.
     *
     * v.g.: MAC( "01", "23", "45", "67", "89", "AB" );
     *
     * @param first
     * @param second
     * @param thrid
     * @param fourth
     * @param fifth
     * @param sixth
     */
    public MAC( String first, String second, String thrid, String fourth, String fifth, String sixth )
    {
        this( first +":"+ second +":"+ thrid +":"+ fourth +":"+ fifth +":"+ sixth );
    }

    /**
     * Constructor.
     *
     * v.g.: MAC( "01:23:45:67:89:AB" );
     *
     * @param mac
     */
    public MAC( String mac )
    {
        this.mac = toMAC( mac );

        if( this.mac == null )
        {
            throw new IllegalArgumentException( "Not valid MAC address" );
        }
    }

    //----------------------------------------------------------------------------//

    public String[] getAsStringArray()
    {
        String[] as = new String[6];

        System.arraycopy( mac, 0, as, 0, mac.length );    // Copia defensiva

        return as;
    }

    public int[] getAsIntArray()
    {
        int[] an = new int[6];

        for( int n = 0; n < 6; n++ )
        {
            an[n] = Integer.parseInt( mac[n], 16 );
        }

        return an;
    }

    @Override
    public String toString()
    {
        return mac[0] +":"+ mac[1] +":"+ mac[2] +":"+ mac[3] +":"+ mac[4] +":"+ mac[5];
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 29 * hash + Arrays.deepHashCode( this.mac );
        return hash;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj == null )
        {
            return false;
        }

        if( getClass() != obj.getClass() )
        {
            return false;
        }

        final MAC other = (MAC) obj;

        return Arrays.deepEquals( this.mac, other.mac );
    }

    //----------------------------------------------------------------------------//

    private String[] toMAC( String mac )
    {
        String[] _mac_ = new String[6];

        if( UtilString.isNotEmpty( mac ) && (UtilString.countChar( mac, ':' ) == 5) )
        {
            String[] elements = mac.split( ":" );

            for( int n = 0; n < 6; n++ )
            {
                String sGroup = (UtilString.isEmpty( elements[n] ) ? "00" : elements[n].trim().toUpperCase());

                if( (sGroup.length() == 2) && isRangeValid( mac ) )
                {
                    _mac_[n] = sGroup;
                }
                else
                {
                    _mac_ = null;     // Indica que no es una IPV4 valida
                    break;
                }
            }
        }

        return _mac_;
    }

    private boolean isRangeValid( String s )
    {
        return (s.charAt( 0 ) >= '0' &&
                s.charAt( 0 ) <= '9')
               ||
               (s.charAt( 0 ) >= 'A' &&
                s.charAt( 0 ) <= 'F');
    }
}