package com.jackbooted.tools.db;

/*
 * copyright (c) Dark Blue Sea
 */

/**
 *
 */
public class DAO {
    /**
     * @param o
     * @return
     */
    protected Long getLong ( Object o ) {
        return ( o instanceof Integer ) ? ( (Integer)o ).longValue () : (Long)o;
    }
}
