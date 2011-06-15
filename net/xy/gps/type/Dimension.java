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
 * dimension object
 * 
 * @author xyan
 * 
 */
public class Dimension implements Serializable {
    private static final long serialVersionUID = 6778841508229080375L;

    /**
     * stores width
     */
    public double width;

    /**
     * stores height
     */
    public double height;

    /**
     * stores depth
     */
    public double depth;

    /**
     * stores z position
     */
    public double z;

    /**
     * null constructor
     */
    public Dimension() {
    }

    /**
     * usual consructor
     * 
     * @param width
     * @param height
     */
    public Dimension(final double width, final double height) {
        this.width = width;
        this.height = height;
        depth = 0;
        z = 0;
    }

    /**
     * usual integer consructor
     * 
     * @param width
     * @param height
     */
    public Dimension(final int width, final int height) {
        this.width = width;
        this.height = height;
        depth = 0;
        z = 0;
    }

    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Dimension)) {
            return false;
        }
        final Dimension oo = (Dimension) obj;
        return width == oo.width && height == oo.height && depth == oo.depth && z == oo.z;
    }
}
