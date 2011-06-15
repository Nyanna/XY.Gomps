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

import net.xy.gps.render.IDrawAction;

/**
 * draw text mostly used for gui creation
 * 
 * @author Xyan
 * 
 */
public class DrawText extends DrawPoint {
    /**
     * stores the text to render
     */
    public final String text;

    /**
     * default
     * 
     * @param lat
     * @param lon
     * @param color
     */
    public DrawText(final double lat, final double lon, final String text, final Integer[] color) {
        super(lat, lon, color);
        this.text = text;
    }

    public int getType() {
        return IDrawAction.ACTION_TEXT;
    }
}