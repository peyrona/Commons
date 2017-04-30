
package com.peyrona.commons.comm;

import com.peyrona.commons.util.UtilDebug;

/**
 * This is a base class for those that read data from a socket inside a
 * thread::run() and use thread::interrupt() to gracefully exit the run() loop.
 *
 * @author peyrona
 */
public abstract class CommReceiver extends Thread
{
    protected final static long nMIN = 8;

    private boolean isRunning = false;

    //----------------------------------------------------------------------------//

    public CommReceiver()
    {
        this( "" );
    }

    public CommReceiver( String prefix )
    {
        setName( prefix +
                 getClass().getSimpleName() +
                 "-" +
                 hashCode() );
    }

    //----------------------------------------------------------------------------//

    protected abstract void createSocket()  throws Exception;
    protected abstract void destroySocket() throws Exception;
    protected abstract void readSocket()    throws Exception;

    //----------------------------------------------------------------------------//

    @Override
    public void interrupt()
    {
        super.interrupt();
        isRunning = false;      // It is better practice to use a flag than to check Thread.isInterrupted()
    }

    @Override
    public void run()
    {
        long delay = nMIN;

        isRunning = true;

        while( isRunning )
        {
            try
            {
                if( ! _createSocket_( delay ) )
                {
                    delay = ((delay > 0xFFFF) ? nMIN : delay*2);   // Max delay == aprox 65 seconds
                }
                else
                {
                    delay = nMIN;
                    readSocket();
                }
            }
            catch( Exception exc )
            {
                if( exc instanceof InterruptedException )
                {
                    interrupt();
                }
                else
                {
                    UtilDebug.debuggingTrace( exc );
                    _destroySocket_();
                }
            }
        }

        _destroySocket_();
    }

    //----------------------------------------------------------------------------//

    private boolean _createSocket_( long delay ) throws InterruptedException
    {
        try
        {
            createSocket();
            return true;
        }
        catch( Exception exc )
        {
            UtilDebug.debuggingTrace( "Can't create socket." );
            UtilDebug.log( exc, "Error creating socket." );
            Thread.sleep( delay );
            return false;
        }
    }

    private void _destroySocket_()
    {
        try
        {
            destroySocket();
        }
        catch( Exception exc )
        {
            UtilDebug.log( exc, "Error destroying socket." );
        }
    }
}