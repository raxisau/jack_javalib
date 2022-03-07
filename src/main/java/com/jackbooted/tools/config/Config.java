package com.jackbooted.tools.config;

/*
 * copyright (c) Dark Blue Sea
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jackbooted.tools.db.DAO;
import com.jackbooted.tools.db.DB;
import com.jackbooted.tools.security.Crypto;


/**
 *
 */
public class Config {

    /**
     * 
     */
    private static Logger log = Logger.getLogger ( Config.class );

    /**
     * 
     */
    private static final String DBHOST = "127.0.0.1";

    /**
     * 
     */
    private static final String DBUSER = "root";

    /**
     * 
     */
    private static final String DBPASS = "root";

    /**
     * 
     */
    private static String localDBName = "madqual";

    public static void setLocalDB ( String dbName ) {
        localDBName = dbName;
    }

    /**
     * 
     */
    private static Config globalInstance = null;
    private static Object mutex = new Object ();

    /**
     * @return
     */
    public static Config global () {
        if ( globalInstance != null ) return globalInstance;
        
        synchronized ( mutex ) {
            if ( globalInstance == null ) globalInstance = new Config ();
            return globalInstance;
        }
    }

    /**
     * 
     */
    private Map<String, ConfigItem> configItems = new LinkedHashMap<String, ConfigItem> ();

    /**
     * 
     */
    private String ipAddr = null;

    /**
     * 
     */
    private String ipName = null;

    /**
     * 
     */
    private String runType;

    /**
     * 
     */
    private Pattern splitPattern = Pattern.compile ( "\\s*[,;]\\s*" );

    /**
     * 
     */
    public Config () {
        // Try connecting to the local database;

        try {
            InetAddress addr = InetAddress.getLocalHost ();
            ipAddr = addr.getHostAddress ();
            ipName = addr.getHostName ();
            log.debug ( "Getting ipAddr: " + ipAddr + " ipName: " + ipName );
        }
        catch ( UnknownHostException e ) {
            throw new RuntimeException ( e );
        }

        log.debug ( "Loading config items into memory" );
        loadConfigIntoMemory ();
    }

    /**
     * @param key
     * @return
     */
    public Object get ( String key ) {
        ConfigItem item = configItems.get ( key );
        if ( item == null ) {
            return null;
        }
        return item.get ();
    }
    /**
     * @param key
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    public List<String> getList ( String key ) {
        return (List<String>)get ( key );
    }

    /**
     * 
     */
    @SuppressWarnings ( "unchecked" )
    public void loadDatabaseConfig ( String dbConfigKey ) {
        for ( String dbKey : (List<String>)get ( dbConfigKey ) ) {
            String host = (String)get ( dbKey + "-host" );
            String user = (String)get ( dbKey + "-user" );
            String pass = (String)get ( dbKey + "-pass" );
            String db   = (String)get ( dbKey + "-db" );

            DB.close ( dbKey );
            try {
                DB.addDB ( dbKey, host, user, pass, db );
                log.debug ( "Sucessful connect to - K:" + dbKey + ", H:" + host + ", U:" + user + ", D:" + db );
            }
            catch ( SQLException e ) {
                log.error ( "Unable to connect- K:" + dbKey + ", H:" + host + ", U:" + user + ", D:" + db );
                throw new RuntimeException ( e );
            }
        }
    }

    private void loadConfigIntoMemory () {
        DB.close ( DB.APP );
        
        // Try to connect to database
        try {
            DB.addDB ( DB.APP, DBHOST, DBUSER, DBPASS, localDBName );
        }
        catch ( SQLException e ) {
            try {
                DB.addDB ( DB.APP, DBHOST, DBUSER, "", localDBName );
            }
            catch ( SQLException e1 ) {
                log.error ( "Tries to connect to " + DBUSER + "@" + DBHOST + ":" + localDBName + " with u/p root/<yes> and root/<no>", e1 );
                throw new RuntimeException ( e1 );
            }
        }

        String qry = "SELECT c.id,c.fld_key,c.fld_value,c.fld_datatype,h.fld_type " + 
                     "FROM " + localDBName + ".config AS c, " + 
                     "     " + localDBName + ".host_type AS h " + 
                     "WHERE ( ? like h.fld_machine OR ? like h.fld_machine ) AND " + 
                     "      h.fld_type like c.fld_type";

        DB.Table configData = DB.fetchAll ( DB.query ( DB.APP, qry, ipAddr, ipName ) );
        for ( DB.Row row : configData ) {
            configItems.put ( (String)row.get ( "fld_key" ), new ConfigItem ( row ) );
        }

        runType = (String)configData.get ( 0 ).get ( "fld_type" );
        log.debug ( "This is a " + runType + " system." );
    }


    public boolean isLive () {
        return "live".equals ( runType );
    }

    public boolean isDev () {
        return "dev".equals ( runType );
    }

    public boolean isStaging () {
        return "stage".equals ( runType );
    }

    public void reload () {
        configItems.clear ();
        loadConfigIntoMemory ();
    }

    private class ConfigItem extends DAO {
        private String rawValue;

        private Object realValue;

        private String datatype;


        public ConfigItem ( DB.Row row ) {
            rawValue = (String)row.get ( "fld_value" );
            datatype = (String)row.get ( "fld_datatype" );

            convertRawToReal ();
        }

        private void convertRawToReal () {
            if ( "List".equals ( datatype ) ) {
                if ( rawValue == null || rawValue.length () == 0 ) {
                    realValue = new ArrayList<String>();
                }
                else {
                    realValue = Arrays.asList ( splitPattern.split ( rawValue ) );
                }
            }
            else if ( "Integer".equals ( datatype ) ) {
                realValue = Integer.valueOf ( rawValue );
            }
            else if ( "Long".equals ( datatype ) ) {
                realValue = Long.valueOf ( rawValue );
            }
            else if ( "Double".equals ( datatype ) ) {
                realValue = Double.valueOf ( rawValue );
            }
            else if ( "Boolean".equals ( datatype ) ) {
                realValue = Boolean.valueOf ( rawValue );
            }
            else if ( "Password".equals ( datatype ) ) {
                realValue = Crypto.global().decrypt ( rawValue );
            }
            else {
                realValue = rawValue;
            }
        }

        public Object get () {
            return realValue;
        }
    }
}
