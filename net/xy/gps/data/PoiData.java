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
    public String label;

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
    public PoiData(final double lat, final double lon, final String label) {
        position = new Point(lat, lon);
        this.label = label;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int getType() {
        return IDataObject.DATA_POINT;
    }
}