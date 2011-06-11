package net.xy.gps.data;

import java.io.Serializable;

import net.xy.gps.type.Point;

/**
 * represents an dataobject for the layers
 * 
 * @author Xyan
 * 
 */
public interface IDataObject extends Serializable {
    /**
     * data type constants used for casting
     */
    public static final int DATA_POINT = 0;
    public static final int DATA_WAY = 1;
    public static final int DATA_AREA = 2;
    // TODO implement an proper java 1.3 enum clone
    // maximum number of data constants
    public static final int COUNT_DATA = 3;

    /**
     * returns type constant of this data
     * 
     * @return
     */
    public int getType();

    /**
     * returns center or edge position of this object
     * 
     * @return
     */
    public Point getPosition();

    /**
     * gets with this object associated tags
     * 
     * @return
     */
    public Integer[] getTags();
}
