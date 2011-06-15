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
package net.xy.gps.data.driver;

import java.io.Serializable;

import net.xy.gps.data.IDataObject;

/**
 * basic tile to save data
 * 
 * @author Xyan
 * 
 */
public class BasicTile implements Serializable {
    private static final long serialVersionUID = -3874813078277700536L;

    /**
     * holds the data
     */
    public IDataObject[] objects = null;

    /**
     * default
     */
    public BasicTile() {
    }

    /**
     * with data
     * 
     * @param objects
     */
    public BasicTile(final IDataObject[] objects) {
        this.objects = objects;
    }
}
