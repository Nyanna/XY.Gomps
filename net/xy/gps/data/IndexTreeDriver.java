package net.xy.gps.data;

import net.xy.gps.render.IDataProvider;
import net.xy.gps.type.Rectangle;

/**
 * datadriver that uses pregenerated indextree to quickly parse osm xml
 * 
 * @author Xyan
 * 
 */
public class IndexTreeDriver implements IDataProvider {

    @Override
    public Iterator get(final Rectangle bounds) {
        return null;
    }

}
