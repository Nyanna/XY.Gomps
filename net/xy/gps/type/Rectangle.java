package net.xy.gps.type;


/**
 * implements an bounding box by just wrapping Dimension and Point
 * 
 * @author xyan
 * 
 */
public class Rectangle {

    /**
     * upper left starting point
     */
    public final Point upperleft;

    /**
     * overall dimensions
     */
    public final Dimension dimension;

    /**
     * default constructor
     * 
     * @param start
     * @param dimension
     */
    public Rectangle(final Point start, final Dimension dimension) {
        upperleft = start;
        this.dimension = dimension;
    }

    /**
     * gets an point relative to the starting point in the rectangle space.
     * Where 0,0 means upper left and 1,1 bottom right.
     * 
     * @param x
     *            0-1
     * @param y
     *            0-1
     * @return
     */
    public Point getPoint(final float x, final float y) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
