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
package net.xy.gps.data.tag.impl;

/**
 * style objects for tags the render engine should enforce as much possible
 * styles
 * 
 * @author Xyan
 * 
 */
public class Style {
    /**
     * if an area should it be filled
     */
    public Boolean fill = Boolean.FALSE;
    /**
     * line color
     */
    public Integer[] color = new Integer[] { Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
            Integer.valueOf(255) };
    /**
     * if line width of the line in meters
     */
    public Integer width = Integer.valueOf(0);
    /**
     * if borders should be rendered
     */
    public Float[] border = new Float[0];
    /**
     * if borders enabled their color
     */
    public Integer[] borderColor = color;
    /**
     * if this an node render with an image instead
     */
    public String image = null;
}