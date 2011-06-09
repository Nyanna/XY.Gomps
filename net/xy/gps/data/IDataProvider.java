package net.xy.gps.data;

import net.xy.gps.type.Rectangle;

/**
 * dataprovider for requesting all possible elements in an obmitted rectangle
 * 
 * @author xyan
 * 
 */
public interface IDataProvider {

    /**
     * blocks and calls receiver as more data becomes available
     * 
     * @param bounds
     * @return
     */
    public void get(Rectangle bounds, final IDataReceiver receiver);

    /**
     * data listener
     * 
     * @author Xyan
     * 
     */
    public static interface IDataReceiver {

        /**
         * receives the data
         * 
         * @param data
         */
        public void accept(final Object[] data);
    }
}
