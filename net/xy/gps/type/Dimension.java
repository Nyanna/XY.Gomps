package net.xy.gps.type;

/**
 * dimension object
 * 
 * @author xyan
 * 
 */
public class Dimension {
    /**
     * stores width
     */
    public final double width;

    /**
     * stores height
     */
    public final double height;

    /**
     * stores depth
     */
    public final double depth;

    /**
     * stores z position
     */
    public final double z;

    /**
     * null constructor
     */
    public Dimension() {
        width = 0;
        height = 0;
        depth = 0;
        z = 0;
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
}
