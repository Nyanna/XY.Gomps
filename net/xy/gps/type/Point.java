package net.xy.gps.type;

import java.io.Serializable;

/**
 * an point in any space
 * 
 * @author xyan
 * 
 */
public class Point implements Serializable {
    private static final long serialVersionUID = -5258109189960208603L;

    /**
     * latitude or x position
     */
    public double lat;

    /**
     * longitude or y position
     */
    public double lon;

    /**
     * x position
     */
    public double x;

    /**
     * z-index
     */
    public double z;

    /**
     * serialization constructor
     */
    public Point() {
    }

    /**
     * simplke constructor
     * 
     * @param lat
     * @param lon
     */
    public Point(final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
        x = 0;
        z = 0;
    }

    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        final Point oo = (Point) obj;
        return lat == oo.lat && lon == oo.lon && x == oo.x && z == oo.z;
    }
}
