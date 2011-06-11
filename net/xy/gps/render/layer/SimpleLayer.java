package net.xy.gps.render.layer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.xy.codebasel.ThreadLocal;
import net.xy.gps.data.IDataObject;
import net.xy.gps.render.ILayer;
import net.xy.gps.render.perspective.Action2DView.ActionListener;

/**
 * an simple layer
 * 
 * @author Xyan
 * 
 */
public abstract class SimpleLayer implements ILayer {
  /**
   * simply holds all IDataObjects
   */
  protected Map objs = new HashMap();
  /**
   * base builtin colors of untagged objects
   */
  protected static final Integer[] BASERGB = new Integer[] { Integer.valueOf(0),
      Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(15) };
  /**
   * draw event listener
   */
  protected ActionListener listener = null;
  /**
   * base width
   */
  protected static final Double WIDTH = Double.valueOf(1);

  public void addObject(final IDataObject object) {
    final Integer hash = Integer.valueOf(object.hashCode());
    boolean exist;
    synchronized (objs) {
      exist = objs.containsKey(hash);
      if (!exist) {
        objs.put(hash, object);
      }
    }
    if (!exist) {
      draw(object);
    }
  }

  /**
   * renders data to actions
   * 
   * @param robj
   * @param listener
   */
  abstract protected void draw(final IDataObject robj);

  public void setListener(final ActionListener listener) {
    this.listener = listener;
  }

  public void update() {
    synchronized (objs) {
      for (final Iterator iterator = objs.values().iterator(); iterator.hasNext();) {
        final Object obj = iterator.next();
        if (((Boolean) ThreadLocal.get()).booleanValue()) {
          return;
        }
        draw((IDataObject) obj);
      }
    }
  }

  public void clear() {
    synchronized (objs) {
      objs = new HashMap();
    }
  }
}