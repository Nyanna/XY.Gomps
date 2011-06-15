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

/**
 * an tag supporting and separating objects per first tag
 * 
 * @author Xyan
 * 
 */
public abstract class TagSupportLayer extends SimpleLayer implements ILayer {
    /**
     * simply holds all IDataObjects tag separated
     */
    protected Map taggedObjects = new HashMap();

    public void addObject(final IDataObject object) {
        final Integer hash = Integer.valueOf(object.hashCode());
        boolean exist = false;
        if (object.getTags() != null) {
            for (final int i = 0; i < object.getTags().length;) {
                synchronized (taggedObjects) {
                    Map objectMap = (Map) taggedObjects.get(object.getTags()[i]);
                    if (objectMap == null) { // init objectmap
                        objectMap = new HashMap();
                        taggedObjects.put(object.getTags()[i], objectMap);
                        exist = false;
                    } else {
                        exist = objectMap.containsKey(hash);
                    }
                    if (!exist) {
                        objectMap.put(hash, object);
                    }
                }
                break;
            }
            if (!exist) {
                draw(object);
            }
        } else {
            super.addObject(object);
        }
    }

    public void removeObject(final IDataObject object) {
        final Integer hash = Integer.valueOf(object.hashCode());
        if (object.getTags() != null) {
            for (final int i = 0; i < object.getTags().length;) {
                synchronized (taggedObjects) {
                    final Map objectMap = (Map) taggedObjects.get(object.getTags()[i]);
                    if (objectMap == null) { // init objectmap
                        return;
                    }
                    objectMap.remove(hash);
                    if (objectMap.isEmpty()) {// remove objectmap
                        taggedObjects.remove(object.getTags()[i]);
                    }
                }
                break;
            }
        } else {
            super.removeObject(object);
        }
    }

    public void update() {
        synchronized (taggedObjects) {
            for (final Iterator iterator = taggedObjects.values().iterator(); iterator.hasNext();) {
                final Map objMap = (Map) iterator.next();
                for (final Iterator i2 = objMap.values().iterator(); i2.hasNext();) {
                    final IDataObject dat = (IDataObject) i2.next();
                    draw(dat);
                    if (((Boolean) ThreadLocal.get()).booleanValue()) {
                        return;
                    }
                }
            }
        }
        super.update();
    }

    public void clear() {
        synchronized (taggedObjects) {
            taggedObjects = new HashMap();
        }
        super.clear();
    }

    public boolean isEmpty() {
        return taggedObjects.isEmpty() && super.isEmpty();
    }
}