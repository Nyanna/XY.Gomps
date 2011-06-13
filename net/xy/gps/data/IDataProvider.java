package net.xy.gps.data;

import net.xy.gps.render.perspective.ActionListener;
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
     * sets an listener for additional visual tracking
     * 
     * @param listener
     */
    public void setListener(final ActionListener listener);

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

        /**
         * removes not needed or obsolte data from the receiver
         * 
         * @param data
         */
        public void revoke(final Object[] data);
    }
}
