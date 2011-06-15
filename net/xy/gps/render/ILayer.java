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
package net.xy.gps.render;

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.perspective.ActionListener;

/**
 * specifies typicl layer management functionalities
 * 
 * @author Xyan
 * 
 */
public interface ILayer {

    /**
     * adds an object to the layer
     * 
     * @param object
     */
    public void addObject(final IDataObject object);

    /**
     * removes only one object from layer
     * 
     * @param object
     */
    public void removeObject(final IDataObject object);

    /**
     * clears all objects from the layer
     */
    public void clear();

    /**
     * adds an listener that is called on draw events
     * 
     * @param view
     * @return
     */
    public void setListener(final ActionListener listener);

    /**
     * causes an redraw of all actual data
     */
    public void update();

    /**
     * is the layers still empty and clean
     * 
     * @return
     */
    public boolean isEmpty();
}
