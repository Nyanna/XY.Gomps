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

import net.xy.gps.render.perspective.IActionListener;
import net.xy.gps.type.Rectangle;

/**
 * dataprovider for requesting all possible elements in an obmitted rectangle
 * 
 * @author xyan
 * 
 */
public interface IDataProvider {

    /**
     * blocks and calls receiver as more data becomes available
     * 
     * @param bounds
     * @return
     */
    public void get(Rectangle bounds, final IDataReceiver receiver);

    /**
     * sets an listener for additional visual tracking
     * 
     * @param listener
     */
    public void setListener(final IActionListener listener);

    /**
     * data listener
     * 
     * @author Xyan
     * 
     */
    public static interface IDataReceiver {

        /**
         * receives the data
         * 
         * @param data
         */
        public void accept(final Object[] data);

        /**
         * removes not needed or obsolte data from the receiver
         * 
         * @param data
         */
        public void revoke(final Object[] data);
    }
}
