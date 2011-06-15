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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.xy.codebasel.Log;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.IDataProvider.IDataReceiver;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.ILayer;

/**
 * priority
 * 
 * @author xyan
 * 
 */
public class PriorityDataReceiver implements IDataReceiver {
    /**
     * holds the types priority configurations
     */
    private static final ConfigKey[] CONF_TYPES_PRIORITY = new ConfigKey[IDataObject.COUNT_DATA];
    private static final ConfigKey[] CONF_TYPES_ENABLED = new ConfigKey[IDataObject.COUNT_DATA];
    static {
        final String baseTypesPrio = "type.priority.";
        final String baseTypesOn = "type.enabled.";
        CONF_TYPES_PRIORITY[0] = Config.registerValues(baseTypesPrio + "DATA_POINT", Integer.valueOf(103));
        CONF_TYPES_ENABLED[0] = Config.registerValues(baseTypesOn + "DATA_POINT", Boolean.TRUE);
        CONF_TYPES_PRIORITY[1] = Config.registerValues(baseTypesPrio + "DATA_WAY", Integer.valueOf(102));
        CONF_TYPES_ENABLED[1] = Config.registerValues(baseTypesOn + "DATA_WAY", Boolean.TRUE);
        CONF_TYPES_PRIORITY[2] = Config.registerValues(baseTypesPrio + "DATA_AREA", Integer.valueOf(101));
        CONF_TYPES_ENABLED[2] = Config.registerValues(baseTypesOn + "DATA_AREA", Boolean.TRUE);
    }
    /**
     * message configuration
     */
    private static final ConfigKey CONF_TEXT_LAYERS_CLEAR = Config.registerValues("data.adapter.layers.cleared",
            "Removing all data from all layers");
    /**
     * stores all the layers after their priority
     */
    private final Map layers = new TreeMap();
    private final CreateLayersCallback createLayers;
    private final ICanvas canvas;

    /**
     * default constructor
     * 
     * @param canvas
     * @param createLayers
     */
    public PriorityDataReceiver(final ICanvas canvas, final CreateLayersCallback createLayers) {
        this.canvas = canvas;
        this.createLayers = createLayers;
    }

    public void accept(final Object[] data) {
        for (int i = 0; i < data.length; i++) {
            final IDataObject dat = (IDataObject) data[i];
            final Integer prio = evalPriority(dat);
            if (prio != null) {
                addToLayer(prio, dat);
            }
        }
    }

    public void revoke(final Object[] data) {
        for (int i = 0; i < data.length; i++) {
            final IDataObject dat = (IDataObject) data[i];
            final Integer prio = evalPriority(dat);
            if (prio != null) {
                removeFromLayer(prio, dat);
            }
        }
    }

    /**
     * determines the priority for an object
     * 
     * @param dat
     * @return
     */
    private Integer evalPriority(final IDataObject dat) {
        // abbort if types is hidden
        if (!Config.getBoolean(CONF_TYPES_ENABLED[dat.getType()]).booleanValue()) {
            return null;
        }

        if (dat.getTags() != null && dat.getTags().length > 0) { // use tag
            int priority = -1;
            for (int j = 0; j < dat.getTags().length; j++) {
                final Tag tag = TagFactory.getTag(dat.getTags()[j]);
                if (!tag.enabled.booleanValue()) {
                    continue;
                }
                if (tag.priority.intValue() > priority) {
                    priority = tag.priority.intValue();
                }
            }
            if (priority > -1) {
                return Integer.valueOf(priority);
            }
            return null;
        }
        // else use type
        return Config.getInteger(CONF_TYPES_PRIORITY[dat.getType()]);
    }

    /**
     * adds to an layer or create new ones
     * 
     * @param index
     * @param dat
     */
    private void addToLayer(final Integer index, final IDataObject dat) {
        Collection layerList = (Collection) layers.get(index);
        if (layerList == null) {
            layerList = createLayers.createLayerSet(index.intValue());
            layers.put(index, layerList);
            updateCanvasLayers();
        }
        for (final Iterator iterator = layerList.iterator(); iterator.hasNext();) {
            final ILayer layer = (ILayer) iterator.next();
            layer.addObject(dat);
        }
    }

    /**
     * removes one object from the corresponding layer
     * 
     * @param index
     * @param dat
     */
    private void removeFromLayer(final Integer index, final IDataObject dat) {
        final Collection layerList = (Collection) layers.get(index);
        if (layerList != null) {
            for (final Iterator iterator = layerList.iterator(); iterator.hasNext();) {
                ((ILayer) iterator.next()).removeObject(dat);
            }
        }
    }

    /**
     * updates the canvas holded layer index
     */
    private void updateCanvasLayers() {
        canvas.removeLayers();
        for (final Iterator iterator = layers.values().iterator(); iterator.hasNext();) {
            final List layerList = (List) iterator.next();
            for (final Iterator iterator2 = layerList.iterator(); iterator2.hasNext();) {
                final ILayer layer = (ILayer) iterator2.next();
                canvas.addLayer(layer);
            }
        }
    }

    /**
     * calls clear method on all layers
     */
    public void clearLayers() {
        Log.comment(CONF_TEXT_LAYERS_CLEAR);
        for (final Iterator iterator = layers.values().iterator(); iterator.hasNext();) {
            final List layerList = (List) iterator.next();
            for (final Iterator iterator2 = layerList.iterator(); iterator2.hasNext();) {
                ((ILayer) iterator2.next()).clear();
            }
        }
    }

    /**
     * application is responsible to create the featured layers
     * 
     * @author xyan
     * 
     */
    public static interface CreateLayersCallback {

        /**
         * create layerset
         * 
         * @return
         */
        public Collection createLayerSet(final int prio);
    }
}