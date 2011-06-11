package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.ICanvas;

/**
 * Layer accepts only nodes and hides all nodes above an specified ammount
 * 
 * @author Xyan
 * 
 */
public class ZoomNodeLayer extends NodeLayer {
  /**
   * hides all nodes if count is above this percentage amount 100px > 20 = 20
   * nodes
   */
  public int maxAmount = 75;
  /**
   * reference to draw surface
   */
  private final ICanvas canvas;
  /**
   * stores the limit recalculated on each update
   */
  private int limit = 100;

  /**
   * default constructor
   * 
   * @param canvas
   */
  public ZoomNodeLayer(final ICanvas canvas) {
    this.canvas = canvas;
    update();
  }

  public void addObject(final IDataObject object) {
    if (objs.size() < limit) {
      super.addObject(object);
    }
  }

  protected void draw(final IDataObject robj) {
    if (listener == null) {
      return;
    }
    if (objs.size() < limit) {
      super.draw(robj);
    }
  }

  public void update() {
    limit = (int) Math
        .round((Math.min(canvas.getSize().width, canvas.getSize().height) / 100 * maxAmount));
    if (objs.size() < limit) {
      super.update();
    }
  }
}