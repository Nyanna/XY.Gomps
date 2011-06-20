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
import net.xy.gps.render.ILayer;

/**
 * renders an GPS position and orientation point
 * 
 * @author Xyan
 * 
 */
public class GPSPosLayer extends SimpleLayer implements ILayer {
    /**
     * stores parent reference
     */
    private final ICanvas parent;

    public GPSPosLayer(final ICanvas parent) {
        this.parent = parent;
    }

    public void addObject(final IDataObject object) {
    }

    public void clear() {
    }

    protected void draw(final IDataObject robj) {
    }

    public void update() {
        draw(null);
    }

    public boolean isEmpty() {
        return false;
    }
}