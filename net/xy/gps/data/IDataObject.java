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

import java.io.Serializable;

import net.xy.gps.type.Point;

/**
 * represents an dataobject for the layers
 * 
 * @author Xyan
 * 
 */
public interface IDataObject extends Serializable {
    /**
     * data type constants used for casting
     */
    public static final int DATA_POINT = 0;
    public static final int DATA_WAY = 1;
    public static final int DATA_AREA = 2;
    // TODO [9] implement an proper java 1.3 enum clone maybe
    // maximum number of data constants
    public static final int COUNT_DATA = 3;

    /**
     * returns type constant of this data
     * 
     * @return
     */
    public int getType();

    /**
     * returns center or edge position of this object
     * 
     * @return
     */
    public Point getPosition();

    /**
     * gets with this object associated tags
     * 
     * @return
     */
    public Integer[] getTags();
}
