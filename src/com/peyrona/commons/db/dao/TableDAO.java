
package com.peyrona.commons.db.dao;

import com.peyrona.commons.util.UtilString;

/**
 * Esta clase representa una tabla de una DB.
 * <p>
 * Nota; NO es threadsafe.
 *
 * @author peyrona
 */
public class TableDAO
{
    private final String   tableName;
    private final String   pkName;
    private       String   sSelect  = null;
    private       String   sInsert  = null;
    private       String   sUpdate  = null;
    private       String   sDelete  = null;
    private       String[] asInsert = null;
    private       String[] asUpdate = null;

    //----------------------------------------------------------------------------//

    public TableDAO( String tableName )
    {
        this( tableName, "id" );
    }

    public TableDAO( String tableName, String pkName )
    {
        this.tableName = (UtilString.isEmpty( tableName ) ? null : tableName.trim());
        this.pkName    = (UtilString.isEmpty( pkName    ) ? null : pkName.trim());
    }

    //----------------------------------------------------------------------------//

    public String getTableName()
    {
        return tableName;
    }

    public String getPK()
    {
        return pkName;
    }

    public String getSelectSQL()
    {
        return ((sSelect == null) ? "select * from "+ getTableName() : sSelect);
    }

    public void setSelectSQL( String s )
    {
        this.sSelect = (UtilString.isEmpty( s ) ? null : s.trim());
    }

    public String getWherePKSQL()
    {
        return " where "+ getPK() +" = ? ";
    }

    public String getWherePKSQL( long id )
    {
        return " where "+ getPK() +" = "+ id;
    }

    public String getInsertSQL()
    {
        if( sInsert != null )
        {
            return sInsert;
        }

        StringBuilder sb1 = new StringBuilder( 128 )
                                .append( "insert into " )
                                .append( tableName )
                                .append( " (" );

        StringBuilder sb2 = new StringBuilder( 128 )
                                .append( " values " )
                                .append( '(' );

        for( String field : asInsert )
        {
            sb1.append( field )
               .append( ", " );

            sb2.append( "?," );
        }

        sb1.deleteCharAt( sb1.lastIndexOf( "," ) )
           .append( ')' );

        sb2.deleteCharAt( sb2.lastIndexOf( "," ) )
           .append( ')' );

        return (sb1.append( sb2 ).toString());
    }

    public String getUpdateSQL()
    {
        if( sUpdate != null )
        {
            return sUpdate;
        }

        StringBuilder sb = new StringBuilder( 256 )
                               .append( "update " )
                               .append( tableName )
                               .append( " set "   );

        for( String field : asUpdate )
        {
            sb.append( field )
              .append( " = ?, " );
        }

        sb.deleteCharAt( sb.lastIndexOf( "," ) );

        return sb.toString();
    }

    /**
     * Establece la sentencia SQL que se utilizará para hacer INSERTs.
     * <p>
     * P.ej.:<br>
     * <code>insert into customer (name, phone, notes) values (?,?,?)</code><br>
     * <p>
     * Este método tiene preponderancia sobre ::setInsertFields( ... )
     *
     * @param s
     * @return
     */
    public TableDAO setInsertSQL( String s )
    {
        sInsert = (UtilString.isEmpty( s ) ? null : s.trim());

        return this;
    }

    /**
     * Establece la sentencia SQL que se utilizará para hacer UPDATEs.
     * <p>
     * P.ej.:<br>
     * <code>update customer set name = ?, phone = ?, notes = ?</code><br>
     * <p>
     * Este método tiene preponderancia sobre ::setUpdateFields( ... )
     *
     * @param s
     * @return
     */
    public TableDAO setUpdateSQL( String s )
    {
        sUpdate = (UtilString.isEmpty( s ) ? null : s.trim());

        return this;
    }

    public TableDAO setInsertFields( String... fieldName )
    {
        asInsert = ((fieldName == null || fieldName.length == 0) ? null : fieldName);

        return this;
    }

    public TableDAO setUpdateFields( String... fieldName )
    {
        asUpdate = ((fieldName == null || fieldName.length == 0) ? null : fieldName);

        return this;
    }

    public String getDeleteSQL()
    {
        return ((sDelete == null) ? "delete from "+ getTableName() : sDelete);
    }

    public TableDAO setDeleteSQL( String s )
    {
        sDelete = (UtilString.isEmpty( s ) ? null : s.trim());

        return this;
    }
}