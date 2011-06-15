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

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.ICanvas;
import net.xy.gps.type.Dimension;

/**
 * layer accepts only ways and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class PriorityWayLayer extends WayLayer {
    /**
     * reference to draw surface
     */
    private final ICanvas canvas;

    /**
     * basic math
     */
    private final double lowerBound = 0.008d;
    private final double upperBound = 1; // viewport lat or lon
    private final int minPrio = 1; // at leat prio 16 will be displayed
    private final int maxPrio = 22; // when lowerbound at prio display all
    private final int ownPrio; // this layers prio
    // one prio is 0.01 lat/lon
    private final double onePrioIs = (upperBound - lowerBound) / (maxPrio - minPrio);
    // updated values
    private double bound = upperBound; // initial bound
    private boolean show = true; // show myself

    /**
     * default constructor
     * 
     * @param canvas
     */
    public PriorityWayLayer(final int ownPrio, final ICanvas canvas) {
        this.canvas = canvas;
        this.ownPrio = ownPrio;
        update();
    }

    protected void draw(final IDataObject robj) {
        if (!show || listener == null) {
            return;
        }
        super.draw(robj);
    }

    public void update() {
        final Dimension dim = canvas.getViewPort().dimension;
        bound = Math.max(dim.width, dim.height);
        check();
        if (show) {
            super.update();
        }
    }

    /**
     * check show conditions
     */
    private void check() {
        show = false;
        if (bound < lowerBound || //
                ownPrio <= minPrio) {
            show = true;
        } else if (bound <= upperBound) {
            final double zeroedBound = bound - lowerBound; // base actual bound
            // 20 => 80 prio to display
            final int showPrio = maxPrio - (int) Math.ceil(zeroedBound / onePrioIs);
            if (ownPrio < minPrio + showPrio) {
                show = true;
            }
        }
    }
}