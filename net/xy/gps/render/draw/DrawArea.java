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

/**
 * draws multipoint areas
 * 
 * @author Xyan
 * 
 */
public class DrawArea implements IDrawAction {
    /**
     * int[] coordinate pairs
     * int[] x,y position in lat and lon
     */
    public final Double[][] path;
    /**
     * stores path color
     */
    public final Integer[] color;
    /**
     * should this area be filles with an color
     */
    public final boolean fill;

    /**
     * default constructor
     * 
     * @param path
     * @param fill
     */
    public DrawArea(final Double[][] path, final Integer[] color, final boolean fill) {
        this.path = path;
        this.color = color;
        this.fill = fill;
    }

    public int getType() {
        return IDrawAction.ACTION_AREA;
    }
}
