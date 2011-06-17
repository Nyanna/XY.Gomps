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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.xy.codebasel.Log;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.draw.DrawGrid;
import net.xy.gps.render.draw.DrawGrid.Path;
import net.xy.gps.type.Dimension;

/**
 * layer checks tag config when to display an object
 * 
 * @author Xyan
 * 
 */
public class TagWayLayer extends WayLayer {
    /**
     * message config
     */
    private static final Config TEXT_GRID_CREATED = Cfg.register("layer.create.grid", "An grid was formed");
    /**
     * cofiguration
     */
    public static final Config CONF_DRAW_GRIDS = Cfg.register("layer.ways.drawGrids", Boolean.FALSE);
    /**
     * reference to draw surface
     */
    private final ICanvas canvas;

    /**
     * tag index to precheck
     */
    private final Set tagIndex = new HashSet();

    /**
     * initial bound
     */
    private double bound = 1; // initial bound
    private final double defaultZoom = 0.002; // initial bound
    private boolean show = true; // show or skip
    private boolean containsTagless = false; // special handling for not taged
                                             // data

    /**
     * default constructor
     * 
     * @param canvas
     */
    public TagWayLayer(final ICanvas canvas) {
        this.canvas = canvas;
        update();
    }

    public void addObject(final IDataObject object) {
        super.addObject(object);
        // add tags for fast checking
        if (object.getTags() != null) {
            for (int i = 0; i < object.getTags().length; i++) {
                synchronized (tagIndex) {
                    tagIndex.add(TagFactory.getTag(object.getTags()[i]));
                }
            }
        } else {
            containsTagless = true;
        }
    }

    protected void draw(final IDataObject robj) {
        if (!show || listener == null) {
            return;
        }
        // if bound below tag limits
        if (robj.getTags() != null && robj.getTags().length > 0) {
            boolean toShow = false;
            for (int i = 0; i < robj.getTags().length; i++) {
                final Tag tag = TagFactory.getTag(robj.getTags()[i]);
                if (tag.zoom > 0 && tag.zoom > bound || //
                        tag.zoom <= 0 && defaultZoom > bound) {
                    toShow = true;
                    break;
                }
            }
            if (toShow) {// tag to display
                super.draw(robj);
            }
        } else if (defaultZoom > bound) {
            super.draw(robj);
        }
    }

    public void update() {
        final Dimension dim = canvas.getViewPort().dimension;
        bound = Math.max(dim.width, dim.height);
        check();
        if (show) {
            synchronized (objs) {
                for (final Iterator iterator = objs.values().iterator(); iterator.hasNext();) {
                    final IDataObject obj = (IDataObject) iterator.next();
                    if (((Boolean) ThreadLocal.get()).booleanValue()) {
                        return;
                    }
                    draw(obj);
                }
            }
            synchronized (taggedObjects) {
                for (final Iterator i1 = taggedObjects.entrySet().iterator(); i1.hasNext();) {
                    final Entry entry = (Entry) i1.next();
                    final Map objMap = (Map) entry.getValue();
                    final Tag tag = TagFactory.getTag((Integer) entry.getKey());
                    if (tag.zoom > 0 && tag.zoom > bound || //
                            tag.zoom <= 0 && defaultZoom > bound) {
                        if (objMap.size() <= 3 || !Cfg.booleant(CONF_DRAW_GRIDS).booleanValue()) {
                            for (final Iterator ii = objMap.values().iterator(); ii.hasNext();) {
                                draw((IDataObject) ii.next());
                            }
                        } else {
                            DrawGrid grid = null;
                            for (final Iterator i = objMap.values().iterator(); i.hasNext();) {
                                final WayData way = (WayData) i.next();
                                if (grid == null) {
                                    grid = new DrawGrid(tag.style.color, tag.style.border);
                                }
                                grid.addPath(new Path(way.path,
                                        Double.valueOf(tag.style.width.intValue() > 0 ? tag.style.width.intValue() : 5)));
                            }
                            if (grid != null) {
                                Log.comment(TEXT_GRID_CREATED, new Object[] { grid });
                                listener.draw(grid);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * check show conditions
     */
    private void check() {
        if (containsTagless) {
            show = true;
            return;
        }
        show = false;
        synchronized (tagIndex) {
            for (final Iterator i = tagIndex.iterator(); i.hasNext();) {
                final Tag tag = (Tag) i.next();
                if (tag.zoom > 0 && tag.zoom > bound || //
                        tag.zoom <= 0 && defaultZoom > bound) {
                    show = true;
                    break;
                }
            }
        }
    }
}
