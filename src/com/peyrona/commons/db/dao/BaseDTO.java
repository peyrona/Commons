
package com.peyrona.commons.db.dao;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase base para los DTOs.
 * <p>
 * Nota; NO es threadsafe.
 * 
 * @author peyrona
 */
public abstract class BaseDTO implements Serializable
{
    public static final long NULL_ID = -1;

    private long   id;
    private String name = null;

    //----------------------------------------------------------------------------//

    public BaseDTO()
    {
        this( NULL_ID );
    }

    public BaseDTO( long id )
    {
        this.id = id;
    }

    //----------------------------------------------------------------------------//

    public boolean isNew()
    {
        return (getPK() == NULL_ID);
    }

    public final long getPK()
    {
        return id;
    }

    public final void setPK( long id )
    {
        this.id = ((id < -1) ? -1 : id);
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = (name == null ? null : name.trim());
    }

    public String getCaption()
    {
        return getName();
    }

    @Override
    public boolean equals( Object o )
    {
        if( o == null )
        {
            return false;
        }

        if( ! o.getClass().equals( this.getClass() ) )
        {
            return false;
        }

        BaseDTO obj = (BaseDTO) o;

        if( this.isNew() && obj.isNew() )
        {
            return Objects.equals( getName(), obj.getName() );
        }

        return this.getPK() == obj.getPK();
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 89 * hash + Objects.hashCode( this.name );
        return hash;
    }

    @Override
    public String toString()
    {
        return "id="+ getPK() +", name="+ getName();
    }
}