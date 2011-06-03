package net.xy.gps.render.perspective;

import java.awt.Color;
import java.awt.Graphics;

import net.xy.gps.render.draw.DrawPoint;
import net.xy.gps.render.draw.DrawPoly;
import net.xy.gps.render.draw.IDrawAction;

/**
 * perspective bount to AWT
 * 
 * @author Xyan
 * 
 */
public class Action2DViewAwt extends Action2DView {

    /**
     * default constructor
     * 
     * @param width
     * @param height
     */
    public Action2DViewAwt(final int width, final int height) {
        super(width, height);
    }

    /**
     * paints to awt graphics
     * 
     * @param g8
     */
    public void paintTo(final Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) displaySize.width, (int) displaySize.height);
        final Object[] actions = getDrawActions();
        for (final Object obj : actions) {
            final IDrawAction action = (IDrawAction) obj;
            switch (action.getType()) {
            case IDrawAction.ACTION_POINT:
                final DrawPoint point = (DrawPoint) action;
                final int x = getX(point.lat);
                final int y = getY(point.lon);
                g.setColor(new Color(point.color[0], point.color[1], point.color[2], point.color[3]));
                g.drawRect(x - 2, y - 2, 4, 4);
                break;
            case IDrawAction.ACTION_WAY:
                final DrawPoly poly = (DrawPoly) action;
                final int[] px = new int[poly.path.length];
                final int[] py = new int[poly.path.length];
                for (int i = 0; i < poly.path.length; i++) {
                    px[i] = getX(poly.path[i][0]);
                    py[i] = getY(poly.path[i][1]);
                }
                g.drawPolyline(px, py, poly.path.length);
                break;
            }
        }
    }
}
