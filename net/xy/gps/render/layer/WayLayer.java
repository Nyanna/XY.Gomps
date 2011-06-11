package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.draw.DrawPoly;

/**
 * layer accepts only ways and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class WayLayer extends SimpleLayer {
  /**
   * reference to draw surface
   */
  private final ICanvas canvas;

  /**
   * default constructor
   * 
   * @param canvas
   */
  public WayLayer(final ICanvas canvas) {
    this.canvas = canvas;
  }

  public void addObject(final IDataObject object) {
    if (IDataObject.DATA_WAY == object.getType()) {
      super.addObject(object);
    }
  }

  protected void draw(final IDataObject robj) {
    final WayData way = (WayData) robj;
    Integer[] color = BASERGB;
    Double width = WIDTH;
    if (robj.getTags() != null && robj.getTags().length > 0) {
      final Tag tag = TagFactory.getTag(robj.getTags()[0]);
      color = tag.style.color;
      width = Double.valueOf(tag.style.width.intValue() > 0 ? tag.style.width.intValue() : width
          .intValue());
    }
    listener.draw(new DrawPoly(way.path, color, Double.valueOf(canvas.getWidth(width.intValue()))));
  }
}