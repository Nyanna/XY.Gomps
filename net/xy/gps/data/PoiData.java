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

import net.xy.gps.type.Point;

/**
 * implements an point of interest
 * 
 * @author Xyan
 * 
 */
public class PoiData implements IDataObject {
    private static final long serialVersionUID = 683804146614402551L;

    /**
     * stores position
     */
    private Point position;
    /**
     * stores the original osm id
     */
    public int osmid;
    /**
     * to this node corresponding tags
     */
    private Integer[] tags = new Integer[0];

    /**
     * serialization constructor
     */
    public PoiData() {
    }

    /**
     * default constructor
     * 
     * @param lat
     * @param lon
     * @param label
     */
    public PoiData(final double lat, final double lon, final int osmid, final Integer[] tags) {
        position = new Point(lat, lon);
        this.osmid = osmid;
        this.tags = tags;
    }

    public Point getPosition() {
        return position;
    }

    public int getType() {
        return IDataObject.DATA_POINT;
    }

    public Integer[] getTags() {
        return tags;
    }

    public int hashCode() {
        return osmid * IDataObject.DATA_POINT;
    }
}