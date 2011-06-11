package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.draw.DrawArea;

/**
 * layer accepts only areas and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class AreaLayer extends SimpleLayer {
  public void addObject(final IDataObject object) {
    if (IDataObject.DATA_AREA == object.getType()) {
      super.addObject(object);
    }
  }

  protected void draw(final IDataObject robj) {
    final WayData way = (WayData) robj;
    Integer[] color = BASERGB;
    boolean fill = true;
    if (robj.getTags() != null && robj.getTags().length > 0) {
      final Tag tag = TagFactory.getTag(robj.getTags()[0]);
      color = tag.style.color;
      fill = tag.style.fill.booleanValue();
    }
    listener.draw(new DrawArea(way.path, color, fill));
  }
}