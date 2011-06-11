package net.xy.gps.render.draw;

import net.xy.gps.render.IDrawAction;

/**
 * draws multipoint areas
 * 
 * @author Xyan
 * 
 */
public class DrawArea implements IDrawAction {
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
     * should this area be filles with an color
     */
    public final boolean fill;

    /**
     * default constructor
     * 
     * @param path
     * @param fill
     */
    public DrawArea(final Double[][] path, final int[] color, final boolean fill) {
        this.path = path;
        this.color = color;
        this.fill = fill;
    }

    public int getType() {
        return IDrawAction.ACTION_AREA;
    }
}
