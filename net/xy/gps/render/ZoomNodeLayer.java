package net.xy.gps.render;

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.draw.DrawPoint;

/**
 * Layer accepts only nodes and hides all nodes above an specified ammount
 * 
 * @author Xyan
 * 
 */
public class ZoomNodeLayer extends SimpleLayer {
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
     * default constructor
     * 
     * @param canvas
     */
    public ZoomNodeLayer(final ICanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void addObject(final IDataObject object) {
        if (IDataObject.DATA_POINT == object.getType()) {
            super.addObject(object);
        }
    }

    @Override
    void draw(final IDataObject robj) {
        final int limit = (int) (Math.min(canvas.getSize().width, canvas.getSize().height) / 100 * maxAmount);
        if (objs.size() < limit) {
            listener.draw(new DrawPoint(robj.getPosition().lat, robj.getPosition().lon, BASERGB));
        }
    }
}