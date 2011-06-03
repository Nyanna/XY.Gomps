package net.xy.gps.render;

import java.util.HashMap;
import java.util.Map;

import net.xy.codebase.ObjectArray;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.render.draw.DrawPoint;
import net.xy.gps.render.draw.DrawPoly;
import net.xy.gps.type.Rectangle;

/**
 * an simple layer
 * 
 * @author Xyan
 * 
 */
public class SimpleLayer implements ILayer {
    /**
     * simply holds all IDataObjects
     */
    private Map objs = new HashMap();
    /**
     * base builtin colors
     */
    private final int[] baseRgb = new int[] { 0, 0, 0, 100 };

    @Override
    public void addObject(final IDataObject object) {
        synchronized (objs) {
            objs.put(object.hashCode(), object);
        }
    }

    @Override
    public void clear() {
        synchronized (objs) {
            objs = new HashMap();
        }
    }

    @Override
    public Object[] getDrawActions(final Rectangle view) {
        final ObjectArray actions = new ObjectArray();
        synchronized (objs) {
            for (final Object obj : objs.values()) {
                final IDataObject robj = (IDataObject) obj;
                switch (robj.getType()) {
                case IDataObject.DATA_WAY:
                    final WayData way = (WayData) robj;
                    actions.add(new DrawPoint(robj.getPosition().lat, robj.getPosition().lon, baseRgb));
                    actions.add(new DrawPoly(way.path));
                    break;
                case IDataObject.DATA_POINT:
                default:
                    actions.add(new DrawPoint(robj.getPosition().lat, robj.getPosition().lon, baseRgb));
                    break;
                }
            }
        }
        return actions.get();
    }
}