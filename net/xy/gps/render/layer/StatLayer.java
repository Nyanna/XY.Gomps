/**
 * This file is part of XY.Gomps, Copyright 2011 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.Gomps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * XY.Gomps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with XY.Gomps. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.xy.gps.render.layer;

import net.xy.gps.data.IDataObject;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.ILayer;
import net.xy.gps.render.draw.DrawPoly;
import net.xy.gps.render.draw.DrawText;
import net.xy.gps.type.GeoTools;

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
     * stat ui color
     */
    protected static final Integer[] BASERGB = new Integer[] { Integer.valueOf(0), Integer.valueOf(0),
            Integer.valueOf(0), Integer.valueOf(255) };

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

    protected void draw(final IDataObject robj) {
        if (listener == null) {
            return;
        }
        final Double lon1 = Double.valueOf(parent.getViewPort().origin.lon + parent.getViewPort().dimension.height
                * 0.02);
        final Double lat1 = Double.valueOf(parent.getViewPort().origin.lat + parent.getViewPort().dimension.width
                * 0.02);
        Double lon2 = Double.valueOf(parent.getViewPort().origin.lon + parent.getViewPort().dimension.height * 0.4);
        // calculate scale
        Double distance = GeoTools.getDistance(lon1.doubleValue(), lon2.doubleValue(), lat1.doubleValue()); // 2753m
        final double roundBy = Integer.valueOf(distance.intValue()).toString().length() > 1 ? (int) Math.pow(10,
                (Integer.valueOf(distance.intValue()).toString().length() - 1)) : 1;
        distance = Double.valueOf((Math.round(distance.doubleValue() / roundBy) * roundBy)); // 3000m
        lon2 = Double.valueOf(lon1.doubleValue() + GeoTools.metersToLon(distance.doubleValue(), lat1.doubleValue()));
        final double sxt = parent.getViewPort().origin.lon + parent.getViewPort().dimension.height * 0.04;
        final Double len = Double.valueOf(parent.getViewPort().dimension.width * 0.05);
        listener.draw(new DrawPoly(new Double[][] { { lat1, lon1 },
                { Double.valueOf(lat1.doubleValue() + len.doubleValue()), lon1 } }, BASERGB, WIDTH)); // startline
        listener.draw(new DrawPoly(new Double[][] {
                { Double.valueOf(lat1.doubleValue() + len.doubleValue() / 2), lon1 },
                { Double.valueOf(lat1.doubleValue() + len.doubleValue() / 2), lon2 } }, BASERGB, WIDTH)); // midline
        listener.draw(new DrawPoly(new Double[][] { { lat1, lon2 },
                { Double.valueOf(lat1.doubleValue() + len.doubleValue()), lon2 } }, BASERGB, WIDTH)); // endline
        listener.draw(new DrawText(lat1.doubleValue(), sxt, GeoTools.convertMeters(distance.doubleValue()), BASERGB));

        // TODO [5] make text with different render origins baseline topleft and so one to ensure displayment
        // memory
        final int freeMem = (int) ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime
                .getRuntime().freeMemory()) / 1024);
        final String memText = new StringBuilder().append(freeMem).append("b").toString();
        listener.draw(new DrawText(parent.getViewPort().origin.lat + parent.getViewPort().dimension.width * 0.9, lon1
                .doubleValue(), memText, BASERGB));
    }

    public void update() {
        draw(null);
    }

    public boolean isEmpty() {
        return false;
    }
}