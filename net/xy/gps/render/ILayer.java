package net.xy.gps.render;

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.perspective.ActionListener;

/**
 * specifies typicl layer management functionalities
 * 
 * @author Xyan
 * 
 */
public interface ILayer {

  /**
   * adds an object to the layer
   * 
   * @param object
   */
  public void addObject(final IDataObject object);

  /**
   * clears all objects from the layer
   */
  public void clear();

  /**
   * adds an listener that is called on draw events
   * 
   * @param view
   * @return
   */
  public void setListener(final ActionListener listener);

  /**
   * causes an redraw of all actual data
   */
  public void update();

  /**
   * is the layers still empty and clean
   * 
   * @return
   */
  public boolean isEmpty();
}
