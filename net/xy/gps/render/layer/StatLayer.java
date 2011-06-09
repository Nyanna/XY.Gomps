package net.xy.gps.render.layer;

import org.geotools.GeoTools;

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.ILayer;
import net.xy.gps.render.draw.DrawPoly;
import net.xy.gps.render.draw.DrawText;

/**
 * statlayer draws compass and scale and gps stats
 * 
 * @author Xyan
 * 
 */
public class StatLayer extends SimpleLayer implements ILayer {
    /**
     * stores parent reference
     */
    private final ICanvas parent;

    /**
     * needed reference to canvas to draw on correct position
     * 
     * @param parent
     */
    public StatLayer(final ICanvas parent) {
        this.parent = parent;
    }

    public void addObject(final IDataObject object) {}

    public void clear() {}

    void draw(final IDataObject robj) {
        final Double lon1 = Double.valueOf(parent.getViewPort().origin.lon + parent.getViewPort().dimension.height
                * 0.02);
        final Double lat1 = Double.valueOf(parent.getViewPort().origin.lat + parent.getViewPort().dimension.width
                * 0.02);
        Double lon2 = Double.valueOf(parent.getViewPort().origin.lon + parent.getViewPort().dimension.height * 0.4);
        // calculate scale
        Double distance = getDistance(lon1.doubleValue(), lon2.doubleValue(), lat1.doubleValue()); // 2753m
        final double roundBy = Integer.valueOf(distance.intValue()).toString().length() > 1 ? (int) Math.pow(10,
                (Integer.valueOf(distance.intValue()).toString().length() - 1)) : 1;
        distance = Double.valueOf((Math.round(distance.doubleValue() / roundBy) * roundBy)); // 3000m
        lon2 = Double.valueOf(lon1.doubleValue() + metersToLon(distance.doubleValue(), lat1.doubleValue()));
        final double sxt = parent.getViewPort().origin.lon + parent.getViewPort().dimension.height * 0.04;
        final Double len = Double.valueOf(parent.getViewPort().dimension.width * 0.05);
        listener.draw(new DrawPoly(new Double[][] { { lat1, lon1 },
                { Double.valueOf(lat1.doubleValue() + len.doubleValue()), lon1 } }, BASERGB)); // startline
        listener.draw(new DrawPoly(new Double[][] {
                { Double.valueOf(lat1.doubleValue() + len.doubleValue() / 2), lon1 },
                { Double.valueOf(lat1.doubleValue() + len.doubleValue() / 2), lon2 } }, BASERGB)); // midline
        listener.draw(new DrawPoly(new Double[][] { { lat1, lon2 },
                { Double.valueOf(lat1.doubleValue() + len.doubleValue()), lon2 } }, BASERGB)); // endline
        listener.draw(new DrawText(lat1.doubleValue(), sxt, convertMeters(distance.doubleValue()), BASERGB));

    }

    /**
     * converts meters in an latitude offset
     * 
     * @param distance
     * @return
     */
    private double metersToLon(final double distance, final double onLat) {
        // calc 1 lon degree distance
        final Double dis = GeoTools.orthodromicDistance(0, onLat, 1, onLat); // meters e.g. 1m
        if (dis != null) {
            return distance / dis.doubleValue();
        }
        return 0.1;
    }

    /**
     * gets the distance between two latitudes
     * 
     * @param sx1
     * @param sx2
     * @return
     */
    private Double getDistance(final double lon1, final double lon2, final double onLat) {
        final Double res = GeoTools.orthodromicDistance(lon1, onLat, lon2, onLat);
        if (res != null) {
            return res;
        }
        return Double.valueOf(10D);
    }

    /**
     * converts meters in an representive string
     * 
     * @param meters
     * @return
     */
    private String convertMeters(final double meters) {
        if (meters > 1000) {
            return (int) (meters / 1000) + "km";
        } else if (meters > 1) {
            return (int) meters + "m";
        } else {
            return (int) (meters / 100) + "cm";
        }
    }

    public void update() {
        draw(null);
    }
}