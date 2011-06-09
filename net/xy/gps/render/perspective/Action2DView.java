package net.xy.gps.render.perspective;

import net.xy.codebasel.ObjectArray;
import net.xy.codebasel.ThreadLocal;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.IDrawAction;
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

    public int addLayer(final ILayer layer) {
        layers.add(layer);
        final int index = layers.getLastIndex();
        layer.setListener(new ActionListener() {

            public void draw(final IDrawAction action) {
                listener.draw(action);
            }
        });
        return index;
    }

    public Rectangle getViewPort() {
        return view;
    }

    public void setViewPort(final double lat, final double lon) {
        view = new Rectangle(new Point(lat, lon), view.dimension);
    }

    public void setViewPort(final double lat, final double lon, final double width, final double height) {
        view = new Rectangle(new Point(lat, lon), new Dimension(width, height));
        calculateUnitSize();
    }

    public void setSize(final int width, final int height) {
        final double ratio = (double) width / height;
        if (displaySize == null || width != displaySize.width || height != (int) displaySize.width * ratio) {
            displaySize = new Dimension(width, height);
            view = new Rectangle(view.origin, new Dimension(view.dimension.width, view.dimension.width * ratio));
            calculateUnitSize();
        }
    }

    public Dimension getSize() {
        return displaySize;
    }

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

    public int getX(final double lon) {
        return (int) Math.round(((lon - view.origin.lon) / unitSize.height));
    }

    public int getY(final double lat) {
        /*
         * osm origin is bottom left (equar meridian), displays and calculation
         * top left and graphics origin is top left
         */
        return (int) Math.round(displaySize.height - (lat - view.origin.lat) / unitSize.width);
    }

    public double getLon(final int xpos) {
        return xpos * unitSize.height;
    }

    public double getLat(final int ypos) {
        return (displaySize.height - ypos) * unitSize.width;
    }

    /**
     * calculates size of one pixel or unit in absolute space.
     * 10 degrees lat displayed with 100 pixel means one pixel will cover 0,1
     * degrees
     * 
     * @return
     */
    private void calculateUnitSize() {
        unitSize = new Dimension(view.dimension.width / displaySize.height, view.dimension.height / displaySize.width);
    }

    public void update() {
        final Object[] layers = this.layers.get();
        for (int i = 0; i < layers.length; i++) {
            final ILayer layer = (ILayer) layers[i];
            if (((Boolean) ThreadLocal.get()).booleanValue()) {
                return;
            }
            layer.update();
        }
    }

    public void setListener(final ActionListener listener) {
        this.listener = listener;
    }
}
