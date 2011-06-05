package net.xy.gps.render.perspective;

import net.xy.codebase.ObjectArray;
import net.xy.codebase.ThreadLocal;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.ILayer;
import net.xy.gps.render.draw.IDrawAction;
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
    private Rectangle view = new Rectangle(new Point(53, 8), new Dimension(1, 1));
    private Dimension unitSize = null;
    private ActionListener listener;

    /**
     * default constructor
     * 
     * @param width
     * @param height
     */
    public Action2DView(final int width, final int height, final ActionListener listener) {
        setSize(width, height);
        this.listener = listener;
    }

    /**
     * default without listener
     * 
     * @param width
     * @param height
     */
    public Action2DView(final int width, final int height) {
        this(width, height, null);
    }

    @Override
    public int addLayer(final ILayer layer) {
        layers.add(layer);
        final int index = layers.getLastIndex();
        layer.setListener(new ActionListener() {

            @Override
            public void draw(final IDrawAction action) {
                listener.draw(action);
            }
        });
        return index;
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
    public void setViewPort(final double lat, final double lon, final double width,
            final double height) {
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
    public Dimension getSize() {
        return displaySize;
    }

    @Override
    public Dimension getPixelSize() {
        return unitSize;
    }

    /**
     * implements an listener if action could aggregated
     * 
     * @author Xyan
     * 
     */
    public static interface ActionListener {
        /**
         * receives draw actions
         * 
         * @param action
         */
        public void draw(IDrawAction action);
    }

    @Override
    public int getX(final double lat) {
        return (int) Math.round(((lat - view.upperleft.lat) / unitSize.width));
    }

    @Override
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
        unitSize = new Dimension(view.dimension.width / displaySize.width, view.dimension.height
                / displaySize.height);
    }

    @Override
    public void update() {
        for (final Object layer : layers.get()) {
            if ((Boolean) ThreadLocal.get()) {
                return;
            }
            ((ILayer) layer).update();
        }
    }

    @Override
    public void setListener(final ActionListener listener) {
        this.listener = listener;
    }
}
