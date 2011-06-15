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
package net.xy.gps.data;

import java.util.Arrays;

import net.xy.gps.type.Boundary;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

/**
 * implements an way of any kind
 * 
 * @author Xyan
 * 
 */
public class WayData implements IDataObject {
    private static final long serialVersionUID = 5551452899850463105L;

    /**
     * stores the path position pairs
     */
    public Double[][] path;
    /**
     * stores center position of the way
     */
    public Point center;
    /**
     * stores the boundingbox of this street
     */
    public Rectangle bounds;
    /**
     * stores this way tags
     */
    private Integer[] tags = new Integer[0];
    /**
     * stores if this way is an area or way
     */
    public int type = IDataObject.DATA_WAY;
    /**
     * unique osm id
     */
    public int id;

    /**
     * serialization constructor
     */
    public WayData() {
    }

    /**
     * default constructor
     * 
     * @param path
     */
    public WayData(final int id, final Double[][] path, final Integer[] tags) {
        this.id = id;
        this.tags = tags;
        this.path = path;
        final Boundary maxmin = new Boundary(new char[] { '<', '<', '>', '>' });
        for (int i = 0; i < path.length; i++) {
            final Double[] pair = path[i];
            maxmin.check(new double[] { pair[0].doubleValue(), pair[1].doubleValue(),
                    pair[0].doubleValue(), pair[1].doubleValue() });
        }
        final double minLat = maxmin.values[0];
        final double minLon = maxmin.values[1];
        final double maxLat = maxmin.values[2];
        final double maxLon = maxmin.values[3];
        final double midLat = minLat + (maxLat - minLat) / 2;
        final double midLon = minLon + (maxLon - minLon) / 2;
        center = new Point(midLat, midLon);
        bounds = new Rectangle(new Point(minLat, minLon), new Dimension(maxLat - minLat, maxLon
                - minLon));
        if (path.length > 2 && Arrays.equals(path[0], path[path.length - 1])) {
            type = IDataObject.DATA_AREA;
        }
    }

    public Point getPosition() {
        return center;
    }

    public int getType() {
        return type;
    }

    public Integer[] getTags() {
        return tags;
    }

    public int hashCode() {
        return id;
    }
}