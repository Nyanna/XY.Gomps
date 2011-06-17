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
package net.xy.gps.render.draw;

import net.xy.gps.render.IDrawAction;

public class DrawPoint implements IDrawAction {
    /**
     * point position
     */
    public final double lat;
    /**
     * point position
     */
    public final double lon;
    /**
     * point color
     */
    public final Integer[] color;
    /**
     * image resource name
     */
    public final String image;

    /**
     * default constructor without image
     * 
     * @param lat
     * @param lon
     */
    public DrawPoint(final double lat, final double lon, final Integer[] color) {
        this(lat, lon, color, null);
    }

    /**
     * default constructor
     * 
     * @param lat
     * @param lon
     */
    public DrawPoint(final double lat, final double lon, final Integer[] color, final String image) {
        this.lat = lat;
        this.lon = lon;
        this.color = color;
        this.image = image;
    }

    public int getType() {
        return IDrawAction.ACTION_POINT;
    }
}
