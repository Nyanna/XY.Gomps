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
    public Point upperleft;

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

    @Override
    public String toString() {
        return new StringBuilder().append("Start: ").append(upperleft.lat).append(" by ")
                .append(upperleft.lon).append(" with ").append(dimension.width).append(" x ")
                .append(dimension.height).toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Rectangle)) {
            return false;
        }
        final Rectangle oo = (Rectangle) obj;
        return (upperleft == oo.upperleft || upperleft != null && upperleft.equals(oo.upperleft)) && //
                (dimension == oo.dimension || dimension != null && dimension.equals(oo.dimension));
    }
}
