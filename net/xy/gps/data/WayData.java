package net.xy.gps.data;

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
    /**
     * stores the path position pairs
     */
    public final double[][] path;
    /**
     * stores center position of the way
     */
    public final Point center;
    /**
     * stores the boundingbox of this street
     */
    public final Rectangle bounds;

    /**
     * default constructor
     * 
     * @param path
     */
    public WayData(final double[][] path) {
        this.path = path;
        double maxLat = 0;
        double minLat = 360;
        double maxLon = 0;
        double minLon = 360;
        for (final double[] pair : path) {
            if (pair[0] > maxLat) {
                maxLat = pair[0];
            }
            if (pair[0] < minLat) {
                minLat = pair[0];
            }
            if (pair[1] > maxLon) {
                maxLon = pair[0];
            }
            if (pair[1] < minLon) {
                minLon = pair[0];
            }
        }
        final double midLat = minLat + (maxLat - minLat) / 2;
        final double midLon = minLon + (maxLon - minLon) / 2;
        center = new Point(midLat, midLon);
        bounds = new Rectangle(new Point(minLat, minLon), new Dimension(maxLat - minLat, maxLon - minLon));
    }

    @Override
    public Point getPosition() {
        return center;
    }

    @Override
    public int getType() {
        return IDataObject.DATA_WAY;
    }
}