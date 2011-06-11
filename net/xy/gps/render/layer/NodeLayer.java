package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.draw.DrawPoint;

/**
 * Layer accepts only nodes and hides all nodes above an specified ammount
 * 
 * @author Xyan
 * 
 */
public class NodeLayer extends SimpleLayer {
  public void addObject(final IDataObject object) {
    if (IDataObject.DATA_POINT == object.getType()) {
      super.addObject(object);
    }
  }

  protected void draw(final IDataObject robj) {
    listener.draw(new DrawPoint(robj.getPosition().lat, robj.getPosition().lon, BASERGB));
  }
}