package net.xy.gps.render.draw;

/**
 * draws multipoint ways
 * 
 * @author Xyan
 * 
 */
public class DrawPoly implements IDrawAction {
    /**
     * int[] coordinate pairs
     * int[] x,y position in lat and lon
     */
    public final Double[][] path;
    /**
     * stores path color
     */
    public final int[] color;

    /**
     * default constructor
     * 
     * @param path
     */
    public DrawPoly(final Double[][] path, final int[] color) {
        this.path = path;
        this.color = color;
    }

    @Override
    public int getType() {
        return IDrawAction.ACTION_WAY;
    }
}
