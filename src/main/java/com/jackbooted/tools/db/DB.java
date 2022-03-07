package com.jackbooted.tools.db;

/*
 * copyright (c) Dark Blue Sea
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

/**
 *
 */
public class DB {

    /**
     * 
     */
    public static final String APP = "local";

    /**
     * Keep a cache of the connections
     */
    private static HashMap<String, SessionInfo> connections = new HashMap<String, SessionInfo> ();

    /**
     * 
     */
    private static Logger log = Logger.getLogger ( DB.class );

    /**
     * @param name
     * @param host
     * @param user
     * @param password
     * @param database
     * @return
     * @throws SQLException
     */
    public static SessionInfo addDB ( String name, String host, String user, String password, String database ) throws SQLException {
        SessionInfo info;

        if ( ( info = connectionFactory ( name ) ) != null ) {
            log.warn ( "DB: " + name + " already exists, ignoring" );
            return info;
        }

        try {
            Class.forName ( "com.mysql.jdbc.Driver" );
        }
        catch ( ClassNotFoundException e ) {
            throw new RuntimeException ( e );
        }

        String url = "jdbc:mysql://" + host + ":3306/" + database + "?autoReconnect=true";
        info = new SessionInfo ( DriverManager.getConnection ( url, user, password ) );
        connections.put ( name, info );
        return info;
    }

    /**
     * @param dbName
     * @return
     */
    public static SessionInfo connectionFactory ( String dbName ) {
        return connections.get ( dbName );
    }

    /**
     * @param values
     * @return
     */
    public static String in ( Collection<Object> values ) {
        return in ( values, null );
    }

    /**
     * @param values
     * @param params
     * @return
     */
    public static String in ( Collection<Object> values, Collection<Object> params ) {
        int numberOfValues = values.size ();
        if ( numberOfValues == 0 ) {
            return "";
        }

        if ( params != null ) {
            params.addAll ( values );
        }

        StringBuffer buf = new StringBuffer ( "?" );
        for ( int i = 1; i < numberOfValues; i++ ) {
            buf.append ( ",?" );
        }
        return buf.toString ();
    }

    /**
     * @param dbh
     * @param qry
     * @param params
     * @return
     */
    public static Object oneValue ( String dbh, String qry, Object... params ) {
        ResultSet rs = query ( dbh, qry, params );
        if ( rs == null ) {
            return null;
        }
        try {
            if ( !rs.next () ) {
                return null;
            }
            return rs.getObject ( 1 );
        }
        catch ( Exception e ) {
            return null;
        }
    }

    /**
     * @param dbh
     * @param qry
     * @param params
     * @return
     */
    public static Row oneRow ( String dbh, String qry, Object... params ) {
        ResultSet rs;
        if ( ( rs = query ( dbh, qry, params ) ) == null ) {
            return null;
        }

        Table table = fetchAll ( rs );
        if ( table == null || table.size () == 0 ) {
            return null;
        }
        return table.get ( 0 );
    }

    /**
     * @param rs
     * @return
     */
    public static Table fetchAll ( ResultSet rs ) {
        if ( rs == null ) {
            return null;
        }

        ResultSetMetaData rsmd;
        int numberOfColumns;
        try {
            rsmd = rs.getMetaData ();
            numberOfColumns = rsmd.getColumnCount ();

            Table table = new Table ();
            while ( rs.next () ) {
                Row row = new Row ();
                for ( int i = 1; i <= numberOfColumns; i++ ) {
                    String key = rsmd.getColumnName ( i );
                    Object val = null;
                    try {
                        val = rs.getObject ( i );
                    }
                    catch ( SQLException e ) {
                        log.error ( "Some problem with SQL", e );
                    }
                    row.put ( key, val );
                }
                table.add ( row );
            }
            return table;
        }
        catch ( SQLException e ) {
            log.error ( "Some major problem with SQL", e );
            return null;
        }
    }

    /**
     * @param dbh
     * @param qry
     * @param params
     * @return
     */
    public static ResultSet query ( String dbh, String qry, Object... params ) {
        SessionInfo info = connectionFactory ( dbh );
        try {
            if ( params == null || params.length == 0 ) {
                Statement statement = info.getStatement ();
                return statement.executeQuery ( qry );
            }
            else {
                return createPreparedStatement ( qry, info, params ).executeQuery ();
            }
        }
        catch ( Exception ex ) {
            log.error ( ex.getMessage () );
            return null;
        }
    }

    /**
     * @param qry
     * @param info
     * @param params
     * @return
     * @throws SQLException
     */
    private static PreparedStatement createPreparedStatement ( String qry, SessionInfo info, Object... params ) throws SQLException {
        PreparedStatement preparedStatement = info.getPreparedStatement ( qry );
        int idx = 1;
        for ( Object o : params ) {
            preparedStatement.setObject ( idx++, o );
        }
        return preparedStatement;
    }

    /**
     * @param dbh
     * @param qry
     * @param params
     * @return
     */
    public static int exec ( String dbh, String qry, Object... params ) {
        SessionInfo info = connectionFactory ( dbh );
        try {
            if ( params == null || params.length == 0 ) {
                Statement statement = info.getStatement ();
                return statement.executeUpdate ( qry );
            }
            else {
                return createPreparedStatement ( qry, info, params ).executeUpdate ();
            }
        }
        catch ( Exception ex ) {
            StringBuilder b = new StringBuilder ();
            if ( params != null ) {
                for ( Object o : params ) {
                    b.append ( o ).append ( ", " );
                }
            }
            log.error ( "Query: " + qry + ":" + b, ex );
            return -1;
        }
    }

    /**
     * 
     */
    public static void closeAll ( ) {
        for ( SessionInfo info: connections.values () ) info.closeAll ();
        connections.clear ();
    }

    /**
     * 
     */
    public static void resetAll ( ) {
        for ( SessionInfo info: connections.values () ) info.resetAll ();
    }

    /**
     * @param dbh
     */
    public static void close ( String dbh ) {
        SessionInfo info = connectionFactory ( dbh );
        if ( info != null ) info.closeAll ();
        connections.remove ( dbh );
    }
    /**
     *
     */
    public static class Row extends LinkedHashMap<String, Object> {
        private static final long serialVersionUID = 1L;
    }

    /**
     *
     */
    public static class Table extends ArrayList<Row> {
        private static final long serialVersionUID = 1L;
    }

    /**
     *
     */
    public static class SessionInfo {
        /**
         * 
         */
        public Connection con;

        /**
         * 
         */
        public HashMap<String, Statement> preparedStatementCache = new HashMap<String, Statement> ();

        /**
         * @param con
         */
        public SessionInfo ( Connection con ) {
            this.con = con;
        }

        /**
         * @return
         */
        public Statement getStatement () {
            String key = Thread.currentThread ().getName ();
            Statement statement = preparedStatementCache.get ( key );
            if ( statement == null ) {

                try {
                    statement = con.createStatement ();
                }
                catch ( SQLException e ) {
                    return null;
                }
                preparedStatementCache.put ( key, statement );
            }
            return statement;
        }

        /**
         * @param qry
         * @return
         */
        public PreparedStatement getPreparedStatement ( String qry ) {
            String key = Thread.currentThread ().getName () + ":" + qry;
            PreparedStatement preparedStatement = (PreparedStatement)preparedStatementCache.get ( key );
            if ( preparedStatement == null ) {

                try {
                    preparedStatement = con.prepareStatement ( qry );
                }
                catch ( SQLException e ) {
                    return null;
                }
                preparedStatementCache.put ( key, preparedStatement );
            }
            return preparedStatement;
        }
        
        /**
         * 
         */
        public void closeAll () {
            resetAll ();
            try {
                con.close ();
            }
            catch ( SQLException e ) {
                // ignore
            }
            con = null;
        }

        /**
         * 
         */
        public void resetAll () {
            for ( Statement s: preparedStatementCache.values () ) {
                try {
                    s.close ();
                }
                catch ( SQLException e ) {
                    // ignore
                }
            }
            preparedStatementCache.clear ();
        }
    }
}
