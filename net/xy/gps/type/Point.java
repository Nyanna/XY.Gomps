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
 * an point in any space
 * 
 * @author xyan
 * 
 */
public class Point implements Serializable {
    private static final long serialVersionUID = -5258109189960208603L;

    /**
     * latitude or x position
     */
    public double lat;

    /**
     * longitude or y position
     */
    public double lon;

    /**
     * x position
     */
    public double x;

    /**
     * z-index
     */
    public double z;

    /**
     * serialization constructor
     */
    public Point() {}

    /**
     * simplke constructor
     * 
     * @param lat
     * @param lon
     */
    public Point(final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
        x = 0;
        z = 0;
    }

    public int hashCode() {
        return (int) ((lat + 1 * 21) * (lon + 1 * 7) * (x + 1 * 3) * (z + 1 * 2));
    }

    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        final Point oo = (Point) obj;
        return lat == oo.lat && lon == oo.lon && x == oo.x && z == oo.z;
    }

    public String toString() {
        return new StringBuilder().append("lat = ").append(lat).append(",lon = ").append(lon).append(",x = ").append(x)
                .append(",z = ").append(z).toString();
    }
}