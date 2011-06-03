package net.xy.gps.render.draw;

public class DrawPoint implements IDrawAction {
    public final double lat;
    public final double lon;
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

    @Override
    public int getType() {
        return IDrawAction.ACTION_POINT;
    }
}
