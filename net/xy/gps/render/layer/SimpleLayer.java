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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.xy.codebasel.ThreadLocal;
import net.xy.gps.data.IDataObject;
import net.xy.gps.render.ILayer;
import net.xy.gps.render.perspective.ActionListener;

/**
 * an simple layer
 * 
 * @author Xyan
 * 
 */
public abstract class SimpleLayer implements ILayer {
    /**
     * simply holds all IDataObjects
     */
    protected Map objs = new HashMap();
    /**
     * base builtin colors of untagged objects
     */
    protected static final Integer[] BASERGB = new Integer[] { Integer.valueOf(0), Integer.valueOf(0),
            Integer.valueOf(0), Integer.valueOf(15) };
    /**
     * draw event listener
     */
    protected ActionListener listener = null;
    /**
     * base width
     */
    protected static final Double WIDTH = Double.valueOf(1);

    public void addObject(final IDataObject object) {
        final Integer hash = Integer.valueOf(object.hashCode());
        boolean exist;
        synchronized (objs) {
            exist = objs.containsKey(hash);
            if (!exist) {
                objs.put(hash, object);
            }
        }
        if (!exist) {
            draw(object);
        }
    }

    public void removeObject(final IDataObject object) {
        final Integer hash = Integer.valueOf(object.hashCode());
        synchronized (objs) {
            objs.remove(hash);
        }
    }

    /**
     * renders data to actions
     * 
     * @param robj
     * @param listener
     */
    abstract protected void draw(final IDataObject robj);

    public void setListener(final ActionListener listener) {
        this.listener = listener;
    }

    public void update() {
        synchronized (objs) {
            for (final Iterator iterator = objs.values().iterator(); iterator.hasNext();) {
                final IDataObject obj = (IDataObject) iterator.next();
                if (((Boolean) ThreadLocal.get()).booleanValue()) {
                    return;
                }
                draw(obj);
            }
        }
    }

    public void clear() {
        synchronized (objs) {
            objs = new HashMap();
        }
    }

    public boolean isEmpty() {
        return objs.isEmpty();
    }
}