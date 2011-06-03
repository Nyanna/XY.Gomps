package net.xy.gps.render;

import net.xy.gps.data.IDataObject;
import net.xy.gps.type.Rectangle;

/**
 * specifies typicl layer management functionalities
 * 
 * @author Xyan
 * 
 */
public interface ILayer {

    /**
     * adds an object to the layer
     * 
     * @param object
     */
    public void addObject(final IDataObject object);

    /**
     * clears all objects from the layer
     */
    public void clear();

    /**
     * get all appropriated draw action for a certain boundary
     * 
     * @param view
     * @return
     */
    public Object[] getDrawActions(Rectangle view);
}
