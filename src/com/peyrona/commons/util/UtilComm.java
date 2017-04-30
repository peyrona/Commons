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

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * This class has a bunch of utility methods to deal with common communications
 * issues.
 * <p>
 * About IP Addresses:
 * <ul>
 * <li>Any address in the range 127.xxx.xxx.xxx is a "loopback" address. It is
 *     only visible to "this" host.
 * <li>Any address in the range 192.168.xxx.xxx is a private (aka site local)
 *     IP address. These are reserved for use within an organization. The same
 *     applies to 10.xxx.xxx.xxx addresses, and 172.16.xxx.xxx through
 *     172.31.xxx.xxx.
 * <li>Addresses in the range 169.254.xxx.xxx are link local IP addresses.
 *     These are reserved for use on a single network segment.
 * <li>Addresses in the range 224.xxx.xxx.xxx through 239.xxx.xxx.xxx are
 *     multicast addresses.
 * <li>The address 255.255.255.255 is the broadcast address.
 * <li>Anything else should be a valid public point-to-point IPv4 address.
 * </ul>
 * @author peyrona
 */
public class UtilComm
{
    public static final int TCP_PORT_MIN         = 1;
    public static final int UDP_PORT_MIN         = 0;
    public static final int TCP_PORT_MAX         = 0xFFFF;
    public static final int UDP_PORT_MAX         = 0xFFFF;
    public static final int PORT_MIN_RECOMMENDED = 49152;

    private static final AtomicInteger nLastUsedValidPort = new AtomicInteger( PORT_MIN_RECOMMENDED );

    //------------------------------------------------------------------------//
    private UtilComm() {}  // Avoid this class instances creation
    //------------------------------------------------------------------------//

    public static boolean isValidPort( int port )
    {
        return ((port >= UDP_PORT_MIN) && (port <= UDP_PORT_MAX));
    }

    public static boolean isValidPort4TCP( int port )
    {
        return ((port >= TCP_PORT_MIN) && (port <= TCP_PORT_MAX));
    }

    public static boolean isValidPort4UDP( int port )
    {
        return ((port >= UDP_PORT_MIN) && (port <= UDP_PORT_MAX));
    }

    public static int makeValidPort4TCP( int port )
    {
        if( port < TCP_PORT_MIN || port > TCP_PORT_MAX )
        {
            int old = port;
            port = UtilConvert.between( TCP_PORT_MIN, port, TCP_PORT_MAX );

            UtilDebug.log( Level.WARNING,
                           new IllegalArgumentException( old +" is not a TCP valid port. It had been changed to: "+ port ) );

        }

        return port;
    }

    public static int makeValidPort4UDP( int port )
    {
        if( port < UDP_PORT_MIN || port > UDP_PORT_MAX )
        {
            int old = port;
            port = UtilConvert.between( UDP_PORT_MIN, port, UDP_PORT_MAX );

            UtilDebug.log( Level.WARNING,
                           new IllegalArgumentException( old +" is not a UDP valid port. It had been changed to: "+ port ) );

        }

        return port;
    }

    public static int ensureRecommendedPort( int port )
    {
        if( port < getLowestRecommendedServerPort() || port > TCP_PORT_MAX )
        {
            int old = port;
            port = nLastUsedValidPort.getAndAdd( 1 );

            UtilDebug.log( Level.WARNING,
                           new IllegalArgumentException( "Use of port: "+ old +" is not recommended. It had been changed to: "+ port ) );
        }

        return port;
    }

    public static int getLowestRecommendedServerPort()
    {
        return PORT_MIN_RECOMMENDED;
    }

    public static int getPort( String ip )
    {
        int n = ip.indexOf( ':' );

        if( (n > -1) && (n < ip.length() - 1) )    // Exists and is not the last char
        {
            ip = ip.substring( n + 1 );

            if( UtilString.isInteger( ip ) )
            {
                n = Integer.parseInt( ip );
            }
        }

        return n;
    }

    public static boolean isReachable( String hostname )
    {
        return isReachable( hostname, 750 );
    }

    /**
     *
     * @param hostname
     * @param timeout The time, in milliseconds, before the call aborts
     * @return A boolean indicating if the address is reachable.
     */
    public static boolean isReachable( String hostname, int timeout )
    {
        try
        {
            return InetAddress.getByName( hostname ).isReachable( timeout );
        }
        catch( IOException ex )
        {
            return false;
        }
    }

    /**
     * Returns this machine IP as viewed by other computers inside the Intranet.
     *
     * @return This machine IP as viewed by other computers inside the Intranet.
     *
     * @throws SocketException
     */
    public static Set<InetAddress> getLocalIntranetIP() throws SocketException
    {
        Enumeration<NetworkInterface> enumera = NetworkInterface.getNetworkInterfaces();
        Set<InetAddress>              locals  = new HashSet<>();

        while( enumera.hasMoreElements() )
        {
            NetworkInterface         ni = enumera.nextElement();
            Enumeration<InetAddress> ee = ni.getInetAddresses();

            while( ee.hasMoreElements() )
            {
                InetAddress address = ee.nextElement();

                if( address.isSiteLocalAddress() )
                {
                    locals.add( address );
//                    System.out.println( address +" --> isAnyLocalAddress="+ address.isAnyLocalAddress() +", "
//                                                +"isLinkLocalAddress="+ address.isLinkLocalAddress()    +", "
//                                                +"isLoopbackAddress=" + address.isLoopbackAddress()     +", "
//                                                +"isSiteLocalAddress="+ address.isSiteLocalAddress() );
                }
            }
        }

        return locals;
    }

    /**
     * Crea una IP en base a dos IPs la primera es una IP válida y la segunda
     * una IP en la que hay uno o más comodines.
     * Ejemplos:
     * <pre>
     * new IPV4( "192.168.1.1", "*" )         --> "192.168.1.1"
     * new IPV4( "192.168.1.1", "*.5" )       --> "192.168.1.5"
     * new IPV4( "192.168.1.1", "5.*" )       --> "192.168.5.1"
     * new IPV4( "192.168.1.1", "*.3.5" )     --> "192.168.3.5"
     * new IPV4( "192.168.1.1", "*.100.3.5" ) --> "192.100.3.5"
     * new IPV4( "192.168.1.1", "212.*.4.5" ) --> "212.168.4.5"
     * </pre>
     * @param parent IP base a utilizar para rellenar los huecos del 2º parámetro
     * @param template Una IP con comodines (wildcards: '*')
     * @return El resultado de aplicar las reglas especificadas en los dos parámetros.
     */
    public static String compose( String parent, String template )
    {
        if( UtilString.isEmpty( parent ) || UtilString.isEmpty( template ) )
        {
            throw new IllegalArgumentException( "Both arguments must be not null, neither empty." );
        }

        parent   = parent.trim();
        template = template.trim();

        if( UtilString.countChar( parent, '.' ) != 3 )
        {
            throw new IllegalArgumentException( "First parameter is not a valid IP v4." );
        }

        if( "*".equals( template ) )   // Si el 2º param es '*'
        {
            return parent;             // se devuelve el 1º
        }

        if( template.indexOf( '*' ) == -1 )    // Si el 2º param no contiene comodines ('*')
        {
            return template;                   // se devuelve el propio 2º param
        }

        if( template.indexOf( '.' ) == -1 )
        {
            throw new IllegalArgumentException( "Second parameter must have at least one '.'" );
        }

        // Inicializo valores
        int nPortParent = Math.max( getPort( parent   ), -1 );    // No puede ser x < -1
        int nPortTempla = Math.max( getPort( template ), -1 );    // No puede ser x < -1

        if( nPortParent > -1 )
        {
            int index = parent.indexOf( ":" );     // Este es el separador del puerto
            parent = (index > -1 ? parent.substring( 0, index ) : parent );
        }

        if( nPortTempla > -1 )
        {
            int index = template.indexOf( ":" );     // Este es el separador del puerto
            template = (index > -1 ? template.substring( 0, index ) : template );
        }

        String[] asParentIP  = parent.split( "\\." );
        String[] asTemplate  = template.split( "\\." );

        // Esto puede hacerse con un bucle, pero al haber tan pocos casos,
        // queda más claro con un switch
        switch( asTemplate.length )
        {
         // case 1: break;  -> para este salta una IllegalArgumentException
            case 2: asTemplate = new String[] { "*", "*", asTemplate[0], asTemplate[1] };           break;
            case 3: asTemplate = new String[] { "*", asTemplate[0], asTemplate[1], asTemplate[2] }; break;
         // case 3: break;  -> este no necesita rellenar nada
        }

        // Ahora sustituyo los '*' por sus valores en el parent
        for( int n = 0; n < asParentIP.length; n++ )
        {
            if( asTemplate[n].equals( "*" ) )
            {
                asTemplate[n] = asParentIP[n];
            }
        }

        int nPort = ((nPortTempla > -1) ? nPortTempla
                                        : (nPortParent > -1) ? nPortParent
                                                             : -1);

        if( ! isValidPort4UDP( nPort ) )    // UDP es más amplio pq permite 0
        {
            throw new IllegalArgumentException( "Port is out or bounds" );
        }

        return asTemplate[0] +"."+ asTemplate[1] +"."+ asTemplate[2] +"."+ asTemplate[3] +
               ((nPort > - 1) ? ":"+ nPort : "");
    }
}