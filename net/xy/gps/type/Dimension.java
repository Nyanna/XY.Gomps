package net.xy.gps.type;

import java.io.Serializable;

/**
 * dimension object
 * 
 * @author xyan
 * 
 */
public class Dimension implements Serializable {
    private static final long serialVersionUID = 6778841508229080375L;

    /**
     * stores width
     */
    public double width;

    /**
     * stores height
     */
    public double height;

    /**
     * stores depth
     */
    public double depth;

    /**
     * stores z position
     */
    public double z;

    /**
     * null constructor
     */
    public Dimension() {
    }

    /**
     * usual consructor
     * 
     * @param width
     * @param height
     */
    public Dimension(final double width, final double height) {
        this.width = width;
        this.height = height;
        depth = 0;
        z = 0;
    }

    /**
     * usual integer consructor
     * 
     * @param width
     * @param height
     */
    public Dimension(final int width, final int height) {
        this.width = width;
        this.height = height;
        depth = 0;
        z = 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Dimension)) {
            return false;
        }
        final Dimension oo = (Dimension) obj;
        return width == oo.width && height == oo.height && depth == oo.depth && z == oo.z;
    }
}
