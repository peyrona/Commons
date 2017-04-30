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

package com.peyrona.commons.comm.broadcast;

import com.peyrona.commons.ParamMap;
import com.peyrona.commons.util.UtilConvert;

/**
 * Create and manipulate Broadcast messages.
 * <p>
 * This class does not needs to be thread-safe and it is not.
 * @author peyrona
 */
public class BroadcastMessage
{
    private final static String sINTERVAL = "interval";

    private       long     nInterval;  // Interval used to send two consecutive messaages.
    private final ParamMap mapParams;

    //----------------------------------------------------------------------------//
    // PROTECTED CONSTRUCTORS

    /**
     * Used by classes that use a BroadcastProducer and BroadcastConsumer.
     */
    protected BroadcastMessage()
    {
        this( null );
    }

    /**
     * Used by the BroadcastConsumer.
     *
     * @param buffer
     */
    protected BroadcastMessage( byte[] buffer )
    {
        if( buffer == null )
        {
            nInterval = 30 * UtilConvert.SECOND;
            mapParams = new ParamMap();
            mapParams.put( sINTERVAL, nInterval );
        }
        else
        {
            mapParams = ParamMap.deserialize( new String( buffer ) );
            nInterval = mapParams.getLong( sINTERVAL );
        }
    }

    //----------------------------------------------------------------------------//

    /**
     * Used by classes that uses a BroadcastProducer to add and retrieve extra
     * information to the message.
     *
     * @return
     */
    public ParamMap getData()
    {
        return mapParams;
    }

    /**
     * Returns the interval used to send two consecutive messaages.
     * <p>
     * Can be used to check if the connection is broken: when the time since
     * last received broadcast is greater than interval * 5, most probably
     * something is failing somewhere.
     * <p>
     * Frequently ssed by BroadcastConsumer.
     *
     * @return
     */
    public long getInterval()
    {
        return nInterval;
    }

    @Override
    public String toString()
    {
        return "BroadcastMessage{" + "interval = "  + nInterval + ", extraData = " + mapParams + '}';
    }

    //----------------------------------------------------------------------------//

    /**
     * Used by BroadcastProducer
     * @param millis
     */
    protected void setInterval( long millis )
    {
        nInterval = millis;
    }
}