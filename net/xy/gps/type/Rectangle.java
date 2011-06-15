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
package net.xy.gps.type;

import java.io.Serializable;

/**
 * implements an bounding box by just wrapping Dimension and Point
 * 
 * @author xyan
 * 
 */
public class Rectangle implements Serializable {
    private static final long serialVersionUID = 4302623671518569356L;

    /**
     * upper left starting point
     */
    public Point origin;

    /**
     * overall dimensions
     */
    public Dimension dimension;

    /**
     * serialization constructor
     */
    public Rectangle() {
    }

    /**
     * default constructor
     * 
     * @param start
     * @param dimension
     */
    public Rectangle(final Point start, final Dimension dimension) {
        origin = start;
        this.dimension = dimension;
    }

    /**
     * gets an point relative to the starting point in the rectangle space.
     * Where 0,0 means upper left and 1,1 bottom right.
     * 
     * @param x
     *            0-1
     * @param y
     *            0-1
     * @return
     */
    public Point getPoint(final float x, final float y) {
        throw new UnsupportedOperationException("Not implemented");
    }

    
    public String toString() {
        return new StringBuilder().append("Start: ").append(origin.lat).append(" by ")
                .append(origin.lon).append(" with ").append(dimension.width).append(" x ")
                .append(dimension.height).toString();
    }

    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Rectangle)) {
            return false;
        }
        final Rectangle oo = (Rectangle) obj;
        return (origin == oo.origin || origin != null && origin.equals(oo.origin)) && //
                (dimension == oo.dimension || dimension != null && dimension.equals(oo.dimension));
    }
}
