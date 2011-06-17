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
package net.xy.gps.render.layer;

import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.render.ICanvas;

/**
 * layer accepts only ways and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class ZoomWayLayer extends WayLayer {
    /**
     * configuration
     */
    public static final Config CONF_AREA_MUSTFIT = Cfg.register("layer.ways.zoom.mustfit", Integer.valueOf(5));
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

    protected void draw(final IDataObject robj) {
        if (listener == null) {
            return;
        }
        final WayData way = (WayData) robj;
        if (way.bounds.dimension.width > tenWidth || way.bounds.dimension.height > tenHeight) {
            super.draw(robj);
        }
    }

    public void update() {
        final int mustFit = Cfg.integer(CONF_AREA_MUSTFIT).intValue();
        tenWidth = canvas.getViewPort().dimension.width / 100 * mustFit;
        tenHeight = canvas.getViewPort().dimension.height / 100 * mustFit;
        super.update();
    }
}