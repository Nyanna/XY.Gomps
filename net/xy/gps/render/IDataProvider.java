package net.xy.gps.render;

import net.xy.gps.data.IDataObject;
import net.xy.gps.type.Rectangle;

/**
 * dataprovider for requesting all possible elements in an obmitted rectangle
 * 
 * @author xyan
 * 
 */
public interface IDataProvider {

    /**
     * returns all objects laying even partly in the given box
     * 
     * @param bounds
     * @return
     */
    public Iterator get(Rectangle bounds);

    /**
     * iterator replacement to decouple from jlang
     * 
     * @author xyan
     * 
     */
    public static interface Iterator {

        /**
         * usual iterator operation
         * 
         * @return
         */
        public IDataObject next();

        /**
         * usual iterator operation
         * 
         * @return
         */
        public boolean hasNext();
    }
}
