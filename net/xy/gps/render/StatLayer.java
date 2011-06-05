package net.xy.gps.render;

import net.xy.gps.data.IDataObject;
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

    @Override
    public void addObject(final IDataObject object) {
    }

    @Override
    public void clear() {
    }

    @Override
    void draw(final IDataObject robj) {
        final double sx1 = parent.getViewPort().upperleft.lat
                + parent.getViewPort().dimension.width * 0.03;
        final double sy1 = parent.getViewPort().upperleft.lon
                + parent.getViewPort().dimension.height * 0.92;
        final double sx2 = parent.getViewPort().upperleft.lat
                + parent.getViewPort().dimension.width * 0.4;
        final double sxt = parent.getViewPort().upperleft.lat
                + parent.getViewPort().dimension.width * 0.04;
        final double len = parent.getViewPort().dimension.height * 0.05;
        listener.draw(new DrawPoly(new Double[][] { { sx1, sy1 }, { sx1, sy1 + len } }, BASERGB)); // startline
        listener.draw(new DrawPoly(
                new Double[][] { { sx1, sy1 + len / 2 }, { sx2, sy1 + len / 2 } }, BASERGB)); // midline
        listener.draw(new DrawPoly(new Double[][] { { sx2, sy1 }, { sx2, sy1 + len } }, BASERGB)); // endline
        listener.draw(new DrawText(sxt, sy1 + len / 2, "500m", BASERGB));
        // TODO calculate real scale based on UTM or so
    }

    @Override
    public void update() {
        draw(null);
    }
}