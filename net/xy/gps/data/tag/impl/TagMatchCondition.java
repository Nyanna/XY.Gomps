package net.xy.gps.data.tag.impl;

/**
 * simple 3 vals based matching
 * 
 * @author Xyan
 * 
 */
public class TagMatchCondition {
    /**
     * tag key
     */
    public String key = null;

    /**
     * tag value
     */
    public String value = null;

    /**
     * type of data object: way, point or area
     */
    public Integer type = null;
}