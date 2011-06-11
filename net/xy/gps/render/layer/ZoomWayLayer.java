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
public class ZoomWayLayer extends SimpleLayer {
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
        this.canvas = canvas;
        update();
    }

    public void addObject(final IDataObject object) {
        if (IDataObject.DATA_WAY == object.getType()) {
            super.addObject(object);
        }
    }

    protected void draw(final IDataObject robj) {
        if (listener == null) {
            return;
        }
        final WayData way = (WayData) robj;
        if (way.bounds.dimension.width > tenWidth || way.bounds.dimension.height > tenHeight) {
            Integer[] color = BASERGB;
            if (robj.getTags() != null && robj.getTags().length > 0) {
                final Tag tag = TagFactory.getTag(robj.getTags()[0]);
                color = tag.style.color;
            }
            listener.draw(new DrawPoly(way.path, color));
        }
    }

    public void update() {
        tenWidth = canvas.getViewPort().dimension.width / 100 * mustFit;
        tenHeight = canvas.getViewPort().dimension.height / 100 * mustFit;
        super.update();
    }
}