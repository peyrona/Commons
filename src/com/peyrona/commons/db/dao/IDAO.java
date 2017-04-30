
package com.peyrona.commons.db.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author peyrona
 * @param <T>
 */
public interface IDAO <T extends BaseDTO>
{
    Connection getConnection();

    void setConnection( Connection connection );

    /**
     * Just a convenience method that calls its omonimous.
     *
     * @param dto
     * @return
     * @throws SQLException
     */
    T findByPK( T dto ) throws SQLException;

    T findByPK( long id ) throws SQLException;

    List<T> findAll() throws SQLException;

    void save( T... dto ) throws SQLException;

    void save( List<T> dtos ) throws SQLException;

    void deleteByPK( long id ) throws SQLException;

    void delete( T dto ) throws SQLException;

    void deleteAll() throws SQLException;
}