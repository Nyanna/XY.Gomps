package net.xy.gps.render.perspective;

import java.awt.Color;
import java.awt.Graphics;

import net.xy.codebase.ThreadLocal;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.draw.DrawPoint;
import net.xy.gps.render.draw.DrawPoly;
import net.xy.gps.render.draw.DrawText;
import net.xy.gps.render.draw.IDrawAction;
import net.xy.gps.render.perspective.Action2DView.ActionListener;

/**
 * perspective bound to AWT
 * 
 * @author Xyan
 * 
 */
public class AwtListener implements ActionListener {
    /**
     * holds graphics reference
     */
    private final Graphics g;
    /**
     * holds canvas reference
     */
    private final ICanvas canvas;

    /**
     * default
     * 
     * @param g
     */
    public AwtListener(final Graphics g, final ICanvas canvas) {
        this.g = g;
        this.canvas = canvas;
    }

    /**
     * paints to awt graphics
     * 
     * @param g8
     */
    public void update() {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) canvas.getSize().width, (int) canvas.getSize().height);
        if ((Boolean) ThreadLocal.get()) {
            return;
        }
        canvas.update();
    }

    @Override
    public void draw(final IDrawAction action) {
        switch (action.getType()) {
        case IDrawAction.ACTION_POINT:
            final DrawPoint point = (DrawPoint) action;
            final int x = canvas.getX(point.lat);
            final int y = canvas.getY(point.lon);
            g.setColor(new Color(point.color[0], point.color[1], point.color[2], point.color[3]));
            g.drawRect(x - 2, y - 2, 4, 4);
            break;
        case IDrawAction.ACTION_WAY:
            final DrawPoly poly = (DrawPoly) action;
            final int[] px = new int[poly.path.length];
            final int[] py = new int[poly.path.length];
            for (int i = 0; i < poly.path.length; i++) {
                px[i] = canvas.getX(poly.path[i][0]);
                py[i] = canvas.getY(poly.path[i][1]);
            }
            g.setColor(new Color(poly.color[0], poly.color[1], poly.color[2], poly.color[3]));
            g.drawPolyline(px, py, poly.path.length);
            break;
        case IDrawAction.ACTION_TEXT:
            final DrawText text = (DrawText) action;
            final int tx = canvas.getX(text.lat);
            final int ty = canvas.getY(text.lon);
            g.setColor(new Color(text.color[0], text.color[1], text.color[2], text.color[3]));
            g.drawString(text.text, tx, ty);
            break;
        }
    }
}
