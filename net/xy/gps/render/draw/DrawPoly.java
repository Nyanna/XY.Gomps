package net.xy.gps.render.draw;

import net.xy.gps.render.IDrawAction;

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
    public final Integer[] color;

    /**
     * default constructor
     * 
     * @param path
     */
    public DrawPoly(final Double[][] path, final Integer[] color) {
        this.path = path;
        this.color = color;
    }

    public int getType() {
        return IDrawAction.ACTION_WAY;
    }
}
