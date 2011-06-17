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
import net.xy.gps.render.ICanvas;

/**
 * Layer accepts only nodes and hides all nodes above an specified ammount
 * 
 * @author Xyan
 * 
 */
public class ZoomNodeLayer extends NodeLayer {
    /**
     * configuration
     */
    // hides all nodes if count is above this percentage amount 100px > 20 = 20
    public static final Config CONF_NODES_LIMIT = Cfg.register("layer.nodes.zoom.limitPer", Integer.valueOf(75));
    /**
     * reference to draw surface
     */
    private final ICanvas canvas;
    /**
     * stores the limit recalculated on each update
     */
    private int limit = 100;

    /**
     * default constructor
     * 
     * @param canvas
     */
    public ZoomNodeLayer(final ICanvas canvas) {
        this.canvas = canvas;
        update();
    }

    public void addObject(final IDataObject object) {
        if (objs.size() < limit) {
            super.addObject(object);
        }
    }

    protected void draw(final IDataObject robj) {
        if (listener == null) {
            return;
        }
        if (objs.size() < limit) {
            super.draw(robj);
        }
    }

    public void update() {
        limit = (int) Math.round((Math.min(canvas.getSize().width, canvas.getSize().height) / 100 * Cfg.integer(
                CONF_NODES_LIMIT).intValue()));
        if (objs.size() < limit) {
            super.update();
        }
    }
}