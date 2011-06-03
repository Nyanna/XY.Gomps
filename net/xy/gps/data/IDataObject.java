package net.xy.gps.data;

import net.xy.gps.type.Point;

/**
 * represents an dataobject for the layers
 * 
 * @author Xyan
 * 
 */
public interface IDataObject {
    /**
     * data type constants used for casting
     */
    public static final int DATA_POINT = 0;
    public static final int DATA_WAY = 1;
    public static final int DATA_AREA = 2;

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
}
