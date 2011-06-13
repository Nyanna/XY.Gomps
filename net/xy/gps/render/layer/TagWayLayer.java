package net.xy.gps.render.layer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.ICanvas;
import net.xy.gps.type.Dimension;

/**
 * layer checks tag config when to display an object
 * 
 * @author Xyan
 * 
 */
public class TagWayLayer extends WayLayer {
  /**
   * reference to draw surface
   */
  private final ICanvas canvas;

  /**
   * tag index to precheck
   */
  private final Set tagIndex = new HashSet();

  /**
   * initial bound
   */
  private double bound = 1; // initial bound
  private final double defaultZoom = 0.002; // initial bound
  private boolean show = true; // show or skip
  private boolean containsTagless = false; // special handling

  /**
   * default constructor
   * 
   * @param canvas
   */
  public TagWayLayer(final ICanvas canvas) {
    super(canvas);
    this.canvas = canvas;
    update();
  }

  public void addObject(final IDataObject object) {
    super.addObject(object);
    // add tags for fast checking
    if (object.getTags() != null) {
      for (int i = 0; i < object.getTags().length; i++) {
        synchronized (tagIndex) {
          tagIndex.add(TagFactory.getTag(object.getTags()[i]));
        }
      }
    } else {
      containsTagless = true;
    }
  }

  protected void draw(final IDataObject robj) {
    if (!show || listener == null) {
      return;
    }
    // if bound below tag limits
    if (robj.getTags() != null && robj.getTags().length > 0) {
      boolean toShow = false;
      for (int i = 0; i < robj.getTags().length; i++) {
        final Tag tag = TagFactory.getTag(robj.getTags()[i]);
        if (tag.zoom > 0 && tag.zoom > bound || //
            tag.zoom <= 0 && defaultZoom > bound) {
          toShow = true;
          break;
        }
      }
      if (toShow) {// tag to display
        super.draw(robj);
      }
    } else if (defaultZoom > bound) {
      super.draw(robj);
    }
  }

  public void update() {
    final Dimension dim = canvas.getViewPort().dimension;
    bound = Math.max(dim.width, dim.height);
    check();
    if (containsTagless || show) {
      super.update();
    }
  }

  /**
   * check show conditions
   */
  private void check() {
    show = false;
    synchronized (tagIndex) {
      for (final Iterator i = tagIndex.iterator(); i.hasNext();) {
        final Tag tag = (Tag) i.next();
        if (tag.zoom > 0 && tag.zoom > bound || //
            tag.zoom <= 0 && defaultZoom > bound) {
          show = true;
          break;
        }
      }
    }
  }
}
