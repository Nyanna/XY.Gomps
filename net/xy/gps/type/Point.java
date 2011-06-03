package net.xy.gps.type;

/**
 * an point in any space
 * 
 * @author xyan
 * 
 */
public class Point {
    /**
     * latitude or x position
     */
    public final double lat;

    /**
     * longitude or y position
     */
    public final double lon;

    /**
     * x position
     */
    public final double x;

    /**
     * z-index
     */
    public final double z;

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
}
