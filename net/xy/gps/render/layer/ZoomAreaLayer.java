package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.draw.DrawArea;

/**
 * layer accepts only areas and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class ZoomAreaLayer extends SimpleLayer {
    /**
     * must fit at least of 10 in height or width to be displayed
     */
    public int mustFit = 7;
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
     * area basecolor
     */
    protected static final Integer[] BASERGB = new Integer[] { Integer.valueOf(180),
            Integer.valueOf(180), Integer.valueOf(180), Integer.valueOf(50) };

    /**
     * default constructor
     * 
     * @param canvas
     */
    public ZoomAreaLayer(final ICanvas canvas) {
        this.canvas = canvas;
        update();
    }

    public void addObject(final IDataObject object) {
        if (IDataObject.DATA_AREA == object.getType()) {
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
            boolean fill = true;
            if (robj.getTags() != null && robj.getTags().length > 0) {
                final Tag tag = TagFactory.getTag(robj.getTags()[0]);
                color = tag.style.color;
                fill = tag.style.fill.booleanValue();
            }
            listener.draw(new DrawArea(way.path, color, fill));
        }
    }

    public void update() {
        tenWidth = canvas.getViewPort().dimension.width / 100 * mustFit;
        tenHeight = canvas.getViewPort().dimension.height / 100 * mustFit;
        super.update();
    }
}
