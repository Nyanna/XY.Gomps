package net.xy.gps.render;

import java.util.HashMap;
import java.util.Map;

import net.xy.codebase.ThreadLocal;
import net.xy.gps.data.IDataObject;
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

    @Override
    public void addObject(final IDataObject object) {
        synchronized (objs) {
            objs.put(object.hashCode(), object);
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

    @Override
    public void setListener(final ActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void update() {
        synchronized (objs) {
            for (final Object obj : objs.values()) {
                if ((Boolean) ThreadLocal.get()) {
                    return;
                }
                draw((IDataObject) obj);
            }
        }
    }

    @Override
    public void clear() {
        synchronized (objs) {
            objs = new HashMap();
        }
    }
}