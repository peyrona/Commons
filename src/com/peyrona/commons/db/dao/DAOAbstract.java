
package com.peyrona.commons.db.dao;

import com.peyrona.commons.db.UtilDB;
import com.peyrona.commons.util.UtilString;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * La implementación base del interface.
 * <p>
 * Nota: Es responsabilidad de quien instancia esta clase cerrar la connection
 * que se pasa.
 * <p>
 * Nota; esta clase NO es thread-safe.
 *
 * @author peyrona
 * @param <T>
 */
public abstract class DAOAbstract<T extends BaseDTO> implements IDAO<T>, Closeable
{
    protected final TableDAO   tableDAO;
    private         Connection connection = null;
    private         boolean    bCanDelete = true;    // Se prmite o no borrar registros

    //----------------------------------------------------------------------------//

    protected DAOAbstract( TableDAO tableDAO )
    {
        this.tableDAO = tableDAO;
    }

    //----------------------------------------------------------------------------//

    @Override
    public Connection getConnection()
    {
        return connection;
    }

    @Override
    public void setConnection( Connection connection )
    {
        close();  // Just in case ::close() was not called prior to call here

        this.connection = connection;
    }

    @Override
    public void close()
    {
        if( connection != null )
        {
            try{ connection.commit(); } catch( SQLException se ) { /* Nothing to do */ }
            try{ connection.close();  } catch( SQLException se ) { /* Nothing to do */ }

            connection = null;
        }
    }

    @Override
    public T findByPK( T dto ) throws SQLException
    {
        return findByPK( dto.getPK() );
    }

    @Override
    public T findByPK( long id ) throws SQLException
    {
        List<T> entities;

        try( Statement stmt = UtilDB.createStatement( getConnection() ) )    // Nota: stmt.close() tb cierra los RS
        {
            ResultSet resultset = stmt.executeQuery( tableDAO.getSelectSQL() + tableDAO.getWherePKSQL( id ) );
                      entities  = fromRS2DTO( resultset );
        }

        return (entities.isEmpty() ? null : entities.get( 0 ));
    }

    @Override
    public List<T> findAll() throws SQLException
    {
        try( Statement stmt = UtilDB.createStatement( connection ) )    // Nota: stmt.close() tb cierra los RS
        {
            ResultSet resultset = stmt.executeQuery( tableDAO.getSelectSQL() );

            return fromRS2DTO( resultset );
        }
    }

    @Override
    public void save( T... dto ) throws SQLException
    {
        save( Arrays.asList( dto ) );
    }

    @Override
    public void save( List<T> dtos ) throws SQLException
    {
        String  sUpdateSQL  = tableDAO.getUpdateSQL() + tableDAO.getWherePKSQL();
        int     nParams     = UtilString.countChar( sUpdateSQL, '?' );
        boolean bAutoCommit = getConnection().getAutoCommit();

        try(
             PreparedStatement insert = UtilDB.createPreparedStatement( getConnection(), tableDAO.getInsertSQL() );
             PreparedStatement update = UtilDB.createPreparedStatement( getConnection(), sUpdateSQL );
           )
        {
            getConnection().setAutoCommit( false );

            for( T dto : dtos )
            {
                if( preSave( dto ) )
                {
                    PreparedStatement ps = (dto.isNew() ? insert : update);
                                      ps.clearParameters();

                    fromDTO2PS( ps, dto );                   // Se rellenan todos los ? suministrados

                    if( ! dto.isNew() )                      // ¿Es un UPDATE?
                    {
                        ps.setLong( nParams, dto.getPK() );  // Se rellena el último: "where id = ?"
                    }

                    UtilDB.executeUpdate( ps, dto );         // Se hace el update
                }
            }

            if( postSave( dtos ) )
            {
                getConnection().commit();
            }
            else
            {
                getConnection().rollback();
            }
        }
        catch( SQLException exc )
        {
            getConnection().rollback();
            throw exc;
        }
        finally
        {
            getConnection().setAutoCommit( bAutoCommit );
        }
    }

    @Override
    public void delete( T dto ) throws SQLException
    {
        deleteByPK( dto.getPK() );
    }

    @Override
    public void deleteByPK( long id ) throws SQLException
    {
        checkIfCanDelete();
        UtilDB.execute( getConnection(), tableDAO.getDeleteSQL() + tableDAO.getWherePKSQL( id ) );
    }

    /**
     * Borra todos los registros de la tabla.
     *
     * @throws SQLException
     */
    @Override
    public void deleteAll() throws SQLException
    {
        checkIfCanDelete();
        UtilDB.execute( getConnection(), tableDAO.getDeleteSQL() );
    }

    public boolean isDeleteAllowed()
    {
        return bCanDelete;
    }

    public void setDeleteAllowed( boolean b )
    {
        bCanDelete = b;
    }

    //----------------------------------------------------------------------------//
    // PROTECTED SCOPE

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    /**
     * Se invoca al comienzo del método ::save( ... ).
     * <br>
     * Si devuelve false, no se ejecuta el PreparedStatement para el DTO pasado
     * y se prosigue con el siguiente si lo hubiere.
     *
     * @param dto
     * @return Si devuelve false, no se ejecuta el PreparedStatement para el DTO pasado.
     * @throws java.sql.SQLException
     */
    protected boolean preSave( T dto ) throws SQLException
    {
        return true;
    }

    /**
     * Se invoca al final del método ::save( ... ).
     *
     * @param dtos
     * @return Si devuelve false, se hace un rolback() para todos los dtos pasados.
     * @throws java.sql.SQLException
     */
    protected boolean postSave( List<T> dtos ) throws SQLException
    {
        return true;
    }

    /**
     * Rellena el PreparedStatement con los datos del DTO.
     *
     * @param ps
     * @param dto
     * @throws SQLException
     */
    protected abstract void fromDTO2PS( PreparedStatement ps, T dto ) throws SQLException;

    /**
     * Crea una lista de entities a partir del ResultSet pasado (una por cada
     * registro del RS).
     *
     * @param rs
     * @return La entity que corresponda.
     * @throws SQLException
     */
    protected abstract List<T> fromRS2DTO( ResultSet rs ) throws SQLException;

    //----------------------------------------------------------------------------//

    private void checkIfCanDelete() throws SQLException
    {
        if( ! isDeleteAllowed() )
        {
            throw new SQLException( "Delete records is not allowed for table '"+ tableDAO.getTableName() +"'" );
        }
    }
}