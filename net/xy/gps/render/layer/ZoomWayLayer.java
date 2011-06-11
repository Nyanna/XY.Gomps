package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.render.ICanvas;

/**
 * layer accepts only ways and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class ZoomWayLayer extends WayLayer {
  /**
   * must fit at least of 10 in height or width to be displayed
   */
  public int mustFit = 5;
  /**
   * reference to draw surface
   */
  private final ICanvas canvas;
  /**
   * limits recalculated on update call
   */
  private double tenWidth = 0.2;
  private double tenHeight = 0.2;

  /**
   * default constructor
   * 
   * @param canvas
   */
  public ZoomWayLayer(final ICanvas canvas) {
    super(canvas);
    this.canvas = canvas;
    update();
  }

  protected void draw(final IDataObject robj) {
    if (listener == null) {
      return;
    }
    final WayData way = (WayData) robj;
    if (way.bounds.dimension.width > tenWidth || way.bounds.dimension.height > tenHeight) {
      super.draw(robj);
    }
  }

  public void update() {
    tenWidth = canvas.getViewPort().dimension.width / 100 * mustFit;
    tenHeight = canvas.getViewPort().dimension.height / 100 * mustFit;
    super.update();
  }
}