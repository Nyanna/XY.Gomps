package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
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
    public int mustFit = 4;
    /**
     * reference to draw surface
     */
    private final ICanvas canvas;

    /**
     * default constructor
     * 
     * @param canvas
     */
    public ZoomWayLayer(final ICanvas canvas) {
        this.canvas = canvas;
    }

    
    public void addObject(final IDataObject object) {
        if (IDataObject.DATA_WAY == object.getType()) {
            super.addObject(object);
        }
    }

    
    void draw(final IDataObject robj) {
        final double tenWidth = canvas.getViewPort().dimension.width / 100 * mustFit;
        final double tenHeight = canvas.getViewPort().dimension.height / 100 * mustFit;
        final WayData way = (WayData) robj;
        if (way.bounds.dimension.width > tenWidth || way.bounds.dimension.height > tenHeight) {
            listener.draw(new DrawPoly(way.path, BASERGB));
        }
    }
}
