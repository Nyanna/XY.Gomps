package net.xy.gps.data;

import java.io.Serializable;

/**
 * basic tile to save data
 * 
 * @author Xyan
 * 
 */
public class BasicTile implements Serializable {
    private static final long serialVersionUID = -3874813078277700536L;

    /**
     * holds the data
     */
    public IDataObject[] objects = null;

    /**
     * default
     */
    public BasicTile() {
    }

    /**
     * with data
     * 
     * @param objects
     */
    public BasicTile(final IDataObject[] objects) {
        this.objects = objects;
    }
}
