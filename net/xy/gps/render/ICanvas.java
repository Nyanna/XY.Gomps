package net.xy.gps.render;

import net.xy.gps.render.perspective.Action2DView.ActionListener;
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
  public void setViewPort(final double lat, final double lon, final double width,
      final double height);

  /**
   * set new window size on resize operation
   */
  public void setSize(final int width, final int height);

  /**
   * returns actual viewportsize in pixels
   * 
   * @return
   */
  public Dimension getSize();

  /**
   * returns calculated pixel dimensions
   * 
   * @return
   */
  public Dimension getPixelSize();

  /**
   * relativize lat and returns x value o current view
   * 
   * @param lat
   * @return
   */
  public int getX(final double lat);

  /**
   * relativize lon and returns y value o current view
   * 
   * @param lat
   * @return
   */
  public int getY(final double lon);

  /**
   * calculates the stroke size from an meters value
   * an street with 5 meters width will get an stroke of 3.3 on these view
   * origin
   * 
   * @param meters
   * @return
   */
  public double getWidth(final double meters);

  /**
   * redraws all actions
   */
  public void update();

  /**
   * sets the listener
   */
  public void setListener(final ActionListener listener);

  /**
   * removes all existing layers
   * 
   * @param objects
   */
  public void removeLayers();
}
