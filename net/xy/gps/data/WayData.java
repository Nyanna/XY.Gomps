package net.xy.gps.data;

import net.xy.gps.type.Boundary;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

/**
 * implements an way of any kind
 * 
 * @author Xyan
 * 
 */
public class WayData implements IDataObject {
    private static final long serialVersionUID = 5551452899850463105L;

    /**
     * stores the path position pairs
     */
    public Double[][] path;
    /**
     * stores center position of the way
     */
    public Point center;
    /**
     * stores the boundingbox of this street
     */
    public Rectangle bounds;

    /**
     * serialization constructor
     */
    public WayData() {}

    /**
     * default constructor
     * 
     * @param path
     */
    public WayData(final Double[][] path) {
        this.path = path;
        final Boundary maxmin = new Boundary(new char[] { '<', '<', '>', '>' });
        for (int i = 0; i < path.length; i++) {
            final Double[] pair = path[i];
            maxmin.check(new double[] { pair[0].doubleValue(), pair[1].doubleValue(), pair[0].doubleValue(),
                    pair[1].doubleValue() });
        }
        final double minLat = maxmin.values[0];
        final double minLon = maxmin.values[1];
        final double maxLat = maxmin.values[2];
        final double maxLon = maxmin.values[3];
        final double midLat = minLat + (maxLat - minLat) / 2;
        final double midLon = minLon + (maxLon - minLon) / 2;
        center = new Point(midLat, midLon);
        bounds = new Rectangle(new Point(minLat, minLon), new Dimension(maxLat - minLat, maxLon - minLon));
    }

    public Point getPosition() {
        return center;
    }

    public int getType() {
        return IDataObject.DATA_WAY;
    }
}