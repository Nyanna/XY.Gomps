package net.xy.gps.type;

import java.io.Serializable;

/**
 * implements an bounding box by just wrapping Dimension and Point
 * 
 * @author xyan
 * 
 */
public class Rectangle implements Serializable {
    private static final long serialVersionUID = 4302623671518569356L;

    /**
     * upper left starting point
     */
    public Point origin;

    /**
     * overall dimensions
     */
    public Dimension dimension;

    /**
     * serialization constructor
     */
    public Rectangle() {
    }

    /**
     * default constructor
     * 
     * @param start
     * @param dimension
     */
    public Rectangle(final Point start, final Dimension dimension) {
        origin = start;
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

    
    public String toString() {
        return new StringBuilder().append("Start: ").append(origin.lat).append(" by ")
                .append(origin.lon).append(" with ").append(dimension.width).append(" x ")
                .append(dimension.height).toString();
    }

    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Rectangle)) {
            return false;
        }
        final Rectangle oo = (Rectangle) obj;
        return (origin == oo.origin || origin != null && origin.equals(oo.origin)) && //
                (dimension == oo.dimension || dimension != null && dimension.equals(oo.dimension));
    }
}
