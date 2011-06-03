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
    public final double[][] path;

    /**
     * default constructor
     * 
     * @param path
     */
    public DrawPoly(final double[][] path) {
        this.path = path;
    }

    @Override
    public int getType() {
        return IDrawAction.ACTION_WAY;
    }
}