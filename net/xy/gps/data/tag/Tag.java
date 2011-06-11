package net.xy.gps.data.tag;

import java.util.ArrayList;
import java.util.List;

import net.xy.gps.data.tag.impl.Style;

/**
 * default tag representation
 * 
 * @author Xyan
 * 
 */
public class Tag {
    /**
     * the tag unique id will be computed from factory
     */
    public int id = 0;
    /**
     * an label like name
     */
    public String name = null;
    /**
     * if elements taged with this should be displayed
     */
    public Boolean enabled = null;
    /**
     * render priority of this tag top = 1, last >= 100
     */
    public Integer priority = null;
    /**
     * the corresponding style object of this tag
     */
    public Style style = null;
    /**
     * an conditionlist to check data object against
     */
    public List conditions = new ArrayList();

    public int hashCode() {
        return id;
    }
}