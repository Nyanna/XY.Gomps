package net.xy.gps.data;

import net.xy.gps.render.IDataProvider;
import net.xy.gps.type.Rectangle;

/**
 * empty driver for testing purpose
 * 
 * @author Xyan
 * 
 */
public class EmptyDataDriver implements IDataProvider {
    private final Object[] mocks = new Object[] {
            new PoiData(105, 105, "TestPoint"),
            new WayData(new double[][] { { 109, 109 }, { 108, 109 }, { 107, 106 }, { 105, 106 } })
    };
    private int i = 0;

    @Override
    public Iterator get(final Rectangle bounds) {
        return new Iterator() {

            @Override
            public IDataObject next() {
                return (IDataObject) mocks[i++];
            }

            @Override
            public boolean hasNext() {
                if (i < mocks.length) {
                    return true;
                }
                return false;
            }
        };
    }
}
