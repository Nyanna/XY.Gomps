package net.xy.gps.render.draw;

import net.xy.gps.render.IDrawAction;

public class DrawPoint implements IDrawAction {
    /**
     * point position
     */
    public final double lat;
    /**
     * point position
     */
    public final double lon;
    /**
     * point color
     */
    public final int[] color;

    /**
     * default constructor
     * 
     * @param lat
     * @param lon
     */
    public DrawPoint(final double lat, final double lon, final int[] color) {
        this.lat = lat;
        this.lon = lon;
        this.color = color;
    }

    
    public int getType() {
        return IDrawAction.ACTION_POINT;
    }
}
