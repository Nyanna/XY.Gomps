package net.xy.gps.data;

import net.xy.gps.type.Point;

/**
 * implements an point of interest
 * 
 * @author Xyan
 * 
 */
public class PoiData implements IDataObject {
    private static final long serialVersionUID = 683804146614402551L;

    /**
     * stores position
     */
    private Point position;
    /**
     * stores the original osm id
     */
    public int osmid;
    /**
     * to this node corresponding tags
     */
    private Integer[] tags = new Integer[0];

    /**
     * serialization constructor
     */
    public PoiData() {
    }

    /**
     * default constructor
     * 
     * @param lat
     * @param lon
     * @param label
     */
    public PoiData(final double lat, final double lon, final int osmid, final Integer[] tags) {
        position = new Point(lat, lon);
        this.osmid = osmid;
        this.tags = tags;
    }

    public Point getPosition() {
        return position;
    }

    public int getType() {
        return IDataObject.DATA_POINT;
    }

    public Integer[] getTags() {
        return tags;
    }

    public int hashCode() {
        return osmid * IDataObject.DATA_POINT;
    }
}