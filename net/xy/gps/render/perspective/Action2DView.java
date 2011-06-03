package net.xy.gps.render.perspective;

import net.xy.codebase.ObjectArray;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.ILayer;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

public class Action2DView implements ICanvas {
    /**
     * stores the layers
     */
    private final ObjectArray layers = new ObjectArray(10, 10);
    /**
     * initial unit or pixel dimensions
     */
    protected Dimension displaySize;
    /**
     * coordinate space used
     */
    private Rectangle view = new Rectangle(new Point(8, 53), new Dimension(1, 1));
    private Dimension unitSize = null;

    /**
     * default constructor
     * 
     * @param width
     * @param height
     */
    public Action2DView(final int width, final int height) {
        setSize(width, height);
    }

    @Override
    public int addLayer(final ILayer layer) {
        layers.add(layer);
        return layers.getLastIndex();
    }

    @Override
    public Rectangle getViewPort() {
        return view;
    }

    @Override
    public void setViewPort(final double lat, final double lon) {
        view = new Rectangle(new Point(lat, lon), view.dimension);
    }

    @Override
    public void setViewPort(final double lat, final double lon, final double width, final double height) {
        view = new Rectangle(new Point(lat, lon), new Dimension(width, height));
        calculateUnitSize();
    }

    @Override
    public void setSize(final int width, final int height) {
        if (displaySize == null || width != displaySize.width || height != displaySize.height) {
            displaySize = new Dimension(width, height);
            calculateUnitSize();
        }
    }

    @Override
    public Dimension getPixelSize() {
        return unitSize;
    }

    /**
     * returns all draw action for the corresponding viewport
     * 
     * @return
     */
    public Object[] getDrawActions() {
        final ObjectArray actions = new ObjectArray();
        for (final Object layer : layers.get()) {
            actions.add(((ILayer) layer).getDrawActions(view));
        }
        return actions.get();
    }

    /**
     * relativize lat and returns x value o current view
     * 
     * @param lat
     * @return
     */
    public int getX(final double lat) {
        return (int) Math.round(((lat - view.upperleft.lat) / unitSize.width));
    }

    /**
     * relativize lon and returns y value o current view
     * 
     * @param lat
     * @return
     */
    public int getY(final double lon) {
        return (int) Math.round(((lon - view.upperleft.lon) / unitSize.height));
    }

    /**
     * calculates size of one pixel or unit in absolute space.
     * 10 degrees lat displayed with 100 pixel means one pixel will cover 0,1
     * degrees
     * 
     * @return
     */
    private void calculateUnitSize() {
        unitSize = new Dimension(view.dimension.width / displaySize.width, view.dimension.height / displaySize.height);
    }
}
