package net.xy.gps.data;

import net.xy.gps.type.Point;

/**
 * implements an point of interest
 * 
 * @author Xyan
 * 
 */
public class PoiData implements IDataObject {
    /**
     * stores position
     */
    private final Point position;
    public final String label;

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