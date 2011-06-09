package net.xy.gps.render.layer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.xy.codebasel.ThreadLocal;
import net.xy.gps.data.IDataObject;
import net.xy.gps.render.ILayer;
import net.xy.gps.render.perspective.Action2DView.ActionListener;

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
     * base builtin colors
     */
    protected static final int[] BASERGB = new int[] { 0, 0, 0, 100 };
    /**
     * draw event listener
     */
    protected ActionListener listener = null;

    public void addObject(final IDataObject object) {
        synchronized (objs) {
            objs.put(Integer.valueOf(object.hashCode()), object);
        }
        draw(object);
    }

    /**
     * renders data to actions
     * 
     * @param robj
     * @param listener
     */
    abstract void draw(final IDataObject robj);

    public void setListener(final ActionListener listener) {
        this.listener = listener;
    }

    public void update() {
        synchronized (objs) {
            for (final Iterator iterator = objs.values().iterator(); iterator.hasNext();) {
                final Object obj = iterator.next();
                if (((Boolean) ThreadLocal.get()).booleanValue()) {
                    return;
                }
                draw((IDataObject) obj);
            }
        }
    }

    public void clear() {
        synchronized (objs) {
            objs = new HashMap();
        }
    }
}