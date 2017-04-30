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
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Information to config a proxy.
 * <p>
 * Instances of this class are used and returned by ProxyConfig panel.
 *
 * @see com.peyrona.swing.containers.ProxyConfig
 * @author peyrona
 */
public class Proxy
{// TODO: Proxy --> esta clase no ha sido probada
    private String httpHost;
    private int    httpPort;       // from 0 to 65535 (FFFF), -1 == no asignado
    private String httpExclude;

    private String ftpHost;
    private int    ftpPort;        // from 0 to 65535 (FFFF), -1 == no asignado
    private String ftpExclude;

    private String username;
    private char[] password;

    //----------------------------------------------------------------------------//

    public Proxy()
    {
        httpHost    = (String) System.getProperties().get( "http.proxyHost" );
        httpPort    = toInt(  System.getProperties().get( "http.proxyPort" ) );
        httpExclude = (String) System.getProperties().get( "http.nonProxyHosts" );

        ftpHost     = (String) System.getProperties().get( "ftp.proxyHost" );
        ftpPort     = toInt(  System.getProperties().get( "ftp.proxyPort" ) );
        ftpExclude  = (String) System.getProperties().get( "ftp.nonProxyHosts" );
    }

    //----------------------------------------------------------------------------//

    public void apply()
    {
        System.getProperties().remove( "http.proxyHost"     );
        System.getProperties().remove( "http.proxyPort"     );
        System.getProperties().remove( "http.nonProxyHosts" );

        System.getProperties().remove( "ftp.proxyHost"     );
        System.getProperties().remove( "ftp.proxyPort"     );
        System.getProperties().remove( "ftp.nonProxyHosts" );

        Authenticator.setDefault( null );

        System.getProperties().put( "http.proxyHost"    , getHttpHost() );
        System.getProperties().put( "http.proxyPort"    , getHttpPort() );
        System.getProperties().put( "http.nonProxyHosts", getHttpExclude() );

        System.getProperties().put( "ftp.proxyHost"     , getFtpHost() );
        System.getProperties().put( "ftp.proxyPort"     , getFtpPort() );
        System.getProperties().put( "ftp.nonProxyHosts" , getFtpExclude() );

        if( UtilString.isNotEmpty( getUsername() ) )
        {
            Authenticator.setDefault( new TheAuthenticator( getUsername(), getPassword() ) );
        }
    }

    //----------------------------------------------------------------------------//

    /**
     * @return the httpHost
     */
    public String getHttpHost()
    {
        return httpHost;
    }

    /**
     * @param httpHost the httpHost to set
     */
    public void setHttpHost( String httpHost )
    {
        this.httpHost = UtilString.isEmpty( httpHost ) ? null : httpHost;
    }

    /**
     * @return the httpPort
     */
    public int getHttpPort()
    {
        return httpPort;
    }

    /**
     * @param httpPort the httpPort to set
     */
    public void setHttpPort( int httpPort )
    {
        this.httpPort = Math.max( httpPort,     -1 );
        this.httpPort = Math.min( httpPort, 0xFFFF );
    }

    /**
     * @return the httpExclude
     */
    public String getHttpExclude()
    {
        return httpExclude;
    }

    /**
     * @param httpExclude the httpExclude to set
     */
    public void setHttpExclude( String httpExclude )
    {
        this.httpExclude = UtilString.isEmpty( httpExclude ) ? null : httpExclude;
    }

    /**
     * @return the ftpHost
     */
    public String getFtpHost()
    {
        return ftpHost;
    }

    /**
     * @param ftpHost the ftpHost to set
     */
    public void setFtpHost( String ftpHost )
    {
        this.ftpHost = UtilString.isEmpty( ftpHost ) ? null : ftpHost;
    }

    /**
     * @return the ftpPort
     */
    public int getFtpPort()
    {
        return ftpPort;
    }

    /**
     * @param ftpPort the ftpPort to set
     */
    public void setFtpPort( int ftpPort )
    {
        this.ftpPort = Math.max( ftpPort,     -1 );
        this.ftpPort = Math.min( ftpPort, 0xFFFF );
    }

    /**
     * @return the ftpExclude
     */
    public String getFtpExclude()
    {
        return ftpExclude;
    }

    /**
     * @param ftpExclude the ftpExclude to set
     */
    public void setFtpExclude( String ftpExclude )
    {
        this.ftpExclude = UtilString.isEmpty( ftpExclude ) ? null : ftpExclude;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername( String username )
    {
        this.username = UtilString.isEmpty( username ) ? null : username;
    }

    /**
     * @return the password
     */
    public char[] getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword( char[] password )
    {
        this.password = (password.length == 0) ? null : password;
    }

    //----------------------------------------------------------------------------//

    private int toInt( Object port )
    {
        String sPort = ((port == null) ? null : port.toString());

        if( UtilString.isInteger( sPort ) )
        {
            return Integer.parseInt( sPort );
        }

        return -1;
    }

    //-------------------------------------------------------------------------//
    // INNER CLASS: MyAuthenticator
    //-------------------------------------------------------------------------//
    private final static class TheAuthenticator extends Authenticator
    {
        private final PasswordAuthentication pa;

        public TheAuthenticator( String sUser, char[] acPassword )
        {
            this.pa = new PasswordAuthentication( sUser, acPassword );
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
            return this.pa;
        }
    }
}