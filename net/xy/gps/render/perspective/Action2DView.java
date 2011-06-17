/**
 * This file is part of XY.Gomps, Copyright 2011 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 * XY.Gomps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * XY.Gomps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with XY.Gomps. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.xy.gps.render.perspective;

import net.xy.codebasel.Log;
import net.xy.codebasel.ObjectArray;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
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
    private static final Config TEXT_UPDATE_CALL = Cfg.register("canvas.update.call",
            "Update was called preparing and relying to layers");
    private static final Config TEXT_SIZE_CHANGED = Cfg.register("canvas.action.size.changed",
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
    private IActionListener listener;
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
        layer.setListener(new IActionListener() {

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
        Log.comment(TEXT_SIZE_CHANGED);
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
        final double calc = lon / unitSize.height;
        return calc > 2d ? calc : 2d;
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
        Log.comment(TEXT_UPDATE_CALL);
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

    public void setListener(final IActionListener listener) {
        this.listener = listener;
    }

    public boolean isValid() {
        return isValid;
    }
}