package net.xy.gps.render;

import net.xy.gps.type.Dimension;
import net.xy.gps.type.Rectangle;

/**
 * provides the canvas to the ngine to draw on will be implemented by an
 * projection
 * 
 * @author xyan
 * 
 */
public interface ICanvas {
    /**
     * adds an layer and returns its index position
     * 
     * @param layer
     * @return
     */
    public int addLayer(final ILayer layer);

    /**
     * gets actual displayed viewport
     * 
     * @return
     */
    public Rectangle getViewPort();

    /**
     * realigns to the new viewport can trigger animation
     * 
     * @param lat
     * @param lon
     */
    public void setViewPort(final double lat, final double lon);

    /**
     * realigns new viewport
     * 
     * @param lat
     * @param lon
     * @param width
     * @param height
     */
    public void setViewPort(final double lat, final double lon, final double width, final double height);

    /**
     * set new window size on resize operation
     */
    public void setSize(final int width, final int height);

    /**
     * returns calculated pixel dimensions
     * 
     * @return
     */
    public Dimension getPixelSize();
}
