package net.xy.gps.render.perspective;

import net.xy.codebasel.Log;
import net.xy.codebasel.ObjectArray;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.IDrawAction;
import net.xy.gps.render.ILayer;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.GeoTools;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

public class Action2DView implements ICanvas {
    /**
     * message configuration
     */
    private static final ConfigKey CONF_TEXT_UPDATE_CALL = Config.registerValues("canvas.update.call",
            "Update was called preparing and relying to layers");
    private static final ConfigKey CONF_TEXT_SIZE_CHANGED = Config.registerValues("canvas.action.size.changed",
            "Canvas output view size has changed");
    /**
     * stores the layers
     */
    private ObjectArray layers = new ObjectArray(10, 10);
    /**
     * initial unit or pixel dimensions
     */
    protected Dimension displaySize;
    /**
     * coordinate space used
     */
    private Rectangle view = new Rectangle(new Point(53.08683729, 8.8188222616), new Dimension(0.007, 0.007));
    private Dimension unitSize = null;
    /**
     * rendering adapter
     */
    private ActionListener listener;
    /**
     * indicates if sicne tha last complete update the view was changed
     */
    private boolean isValid = true;

    /**
     * default constructor
     * 
     * @param width
     * @param height
     */
    public Action2DView(final int width, final int height) {
        setSize(width, height);
    }

    public int addLayer(final ILayer layer) {
        layers.add(layer);
        final int index = layers.getLastIndex();
        layer.setListener(new ActionListener() {

            public void draw(final IDrawAction action) {
                listener.draw(action);
            }

            public void updateStart() {
                // when layer request update do nothing at the moment
            }

            public void updateEnd(final boolean success) {
            }
        });
        if (!layer.isEmpty()) {
            isValid = false;
        }
        return index;
    }

    public void removeLayers() {
        layers = new ObjectArray(10, 10);
        isValid = false;
    }

    public Rectangle getViewPort() {
        return view;
    }

    public void setViewPort(final double lat, final double lon) {
        if (view.origin.lat != lat || view.origin.lon != lon) {
            view = new Rectangle(new Point(lat, lon), view.dimension);
            isValid = false;
        }
    }

    public void setViewPort(final double lat, final double lon, final double width, final double height) {
        if (view.origin.lat != lat || view.origin.lon != lon || //
                view.dimension.width != width || view.dimension.height != height) {
            view = new Rectangle(new Point(lat, lon), new Dimension(width, height));
            calculateUnitSize();
            isValid = false;
        }
    }

    public void setSize(final int width, final int height) {
        Log.comment(CONF_TEXT_SIZE_CHANGED);
        final double ratio = (double) width / height;
        if (displaySize == null || width != displaySize.width || height != (int) displaySize.width * ratio) {
            displaySize = new Dimension(width, height);
            view = new Rectangle(view.origin, new Dimension(view.dimension.width, view.dimension.width * ratio));
            calculateUnitSize();
            isValid = false;
        }
    }

    public Dimension getSize() {
        return displaySize;
    }

    public Dimension getPixelSize() {
        return unitSize;
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

    public double getWidth(final double meters) {
        final double lon = GeoTools.metersToLon(meters, view.origin.lat);
        final double calc = lon / unitSize.height * 3;
        // TODO dynamic scaling of width to zoom
        return calc > 0.5d ? calc : 0.5d;
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
        Log.comment(CONF_TEXT_UPDATE_CALL);
        listener.updateStart(); // prepare view
        final Object[] layers = this.layers.get();
        for (int i = 0; i < layers.length; i++) {
            if (((Boolean) ThreadLocal.get()).booleanValue()) {
                listener.updateEnd(false);
                return;
            }
            final ILayer layer = (ILayer) layers[i];
            layer.update();
        }
        isValid = true;
        listener.updateEnd(true);
    }

    public void setListener(final ActionListener listener) {
        this.listener = listener;
    }

    public boolean isValid() {
        return isValid;
    }
}