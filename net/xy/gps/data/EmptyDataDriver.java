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
            new WayData(new Double[][] { { 109.0, 109.0 }, { 108.0, 109.0 }, { 107.0, 106.0 },
                    { 105.0, 106.0 } }) };

    @Override
    public void get(final Rectangle bounds, final IDataReceiver receiver) {
        receiver.accept(mocks);
    }
}
