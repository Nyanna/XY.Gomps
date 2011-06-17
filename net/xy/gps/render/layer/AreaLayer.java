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
import net.xy.gps.render.draw.DrawArea;

/**
 * layer accepts only areas and hides them if they are below a certain boundary
 * 
 * @author Xyan
 * 
 */
public class AreaLayer extends SimpleLayer {
    private static final Float[] EMPTYD = new Float[0];

    public void addObject(final IDataObject object) {
        if (IDataObject.DATA_AREA == object.getType()) {
            super.addObject(object);
        }
    }

    protected void draw(final IDataObject robj) {
        final WayData way = (WayData) robj;
        Integer[] color = BASERGB;
        boolean fill = true;
        Float[] border = EMPTYD;
        Integer[] borderColor = BASERGB;
        String image = null;
        if (robj.getTags() != null && robj.getTags().length > 0) {
            final Tag tag = TagFactory.getTag(robj.getTags()[0]);
            color = tag.style.color;
            fill = tag.style.fill.booleanValue();
            border = tag.style.border;
            borderColor = tag.style.borderColor;
            image = tag.style.image;
        }
        listener.draw(new DrawArea(way.path, color, fill, border, borderColor, image));
    }
}