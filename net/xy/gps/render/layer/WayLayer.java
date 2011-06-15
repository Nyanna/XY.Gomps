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
package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.draw.DrawPoly;

/**
 * layer accepts only ways and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class WayLayer extends TagSupportLayer {
    public void addObject(final IDataObject object) {
        if (IDataObject.DATA_WAY == object.getType()) {
            super.addObject(object);
        }
    }

    protected void draw(final IDataObject robj) {
        final WayData way = (WayData) robj;
        Integer[] color = BASERGB;
        Double width = WIDTH;
        if (robj.getTags() != null && robj.getTags().length > 0) {
            final Tag tag = TagFactory.getTag(robj.getTags()[0]);
            color = tag.style.color;
            width = Double.valueOf(tag.style.width.intValue() > 0 ? tag.style.width.intValue() : width.intValue());
        }
        listener.draw(new DrawPoly(way.path, color, Double.valueOf(width.intValue())));
    }
}