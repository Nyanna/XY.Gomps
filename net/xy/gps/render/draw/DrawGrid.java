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

import java.util.ArrayList;
import java.util.List;

import net.xy.gps.render.IDrawAction;

/**
 * draws multipoint ways
 * 
 * @author Xyan
 * 
 */
public class DrawGrid implements IDrawAction {
    /**
     * list of path objects
     */
    public final List pathes = new ArrayList();
    /**
     * stores path color
     */
    public final Integer[] color;

    /**
     * default constructor
     * 
     * @param path
     */
    public DrawGrid(final Integer[] color) {
        this.color = color;
    }

    /**
     * adds one path to the grid
     * 
     * @param path
     */
    public void addPath(final Path path) {
        pathes.add(path);
    }

    public int getType() {
        return IDrawAction.ACTION_WAYGRID;
    }

    /**
     * represents one way or way fragment
     * 
     * @author xyan
     * 
     */
    public static class Path {
        /**
         * int[] coordinate pairs
         * int[] x,y position in lat and lon
         */
        public final Double[][] path;
        /**
         * stores the width of the line
         */
        public final Double width;

        /**
         * default constructor
         * 
         * @param path
         */
        public Path(final Double[][] path, final Double width) {
            this.path = path;
            this.width = width;
        }
    }

    public String toString() {
        return new StringBuilder("DrawGrid: ").append(pathes != null ? pathes.size() : 0).append(" Pathes").toString();
    }
}
