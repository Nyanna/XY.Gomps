package net.xy.gps.client.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import net.xy.codebasel.ThreadLocal;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.IDrawAction;
import net.xy.gps.render.draw.DrawArea;
import net.xy.gps.render.draw.DrawPoint;
import net.xy.gps.render.draw.DrawPoly;
import net.xy.gps.render.draw.DrawText;
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
    private Graphics g;
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
     * @param
     */
    public void update(final Graphics buffer) {
        // TODO use better mechanism for locking, data threat should check if he
        // can get a lock when not he should proceed without drawing
        final Graphics bak = g;
        synchronized (this) {
            g = buffer;
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, (int) canvas.getSize().width, (int) canvas.getSize().height);
            if (((Boolean) ThreadLocal.get()).booleanValue()) {
                return;
            }
            canvas.update();
            g = bak;
        }
    }

    public void draw(final IDrawAction action) {
        synchronized (this) {
            switch (action.getType()) {
            case IDrawAction.ACTION_POINT:
                final DrawPoint point = (DrawPoint) action;
                final int x = canvas.getX(point.lon);
                final int y = canvas.getY(point.lat);
                if (x < 0 || y < 0) { // TODO optimze not drawing points outside
                                      // of view
                    break;
                }
                g.setColor(new Color(point.color[0], point.color[1], point.color[2], point.color[3]));
                g.drawRect(x - 2, y - 2, 4, 4);
                break;
            case IDrawAction.ACTION_AREA:
                final DrawArea area = (DrawArea) action;
                final int[] nx = new int[area.path.length];
                final int[] ny = new int[area.path.length];
                for (int i = 0; i < area.path.length; i++) {
                    nx[i] = canvas.getX(area.path[i][1].doubleValue());
                    ny[i] = canvas.getY(area.path[i][0].doubleValue());
                }
                g.setColor(new Color(area.color[0], area.color[1], area.color[2], area.color[3]));
                if (area.fill) {
                    g.fillPolygon(nx, ny, area.path.length);
                } else {
                    g.drawPolygon(nx, ny, area.path.length);
                }
                break;
            case IDrawAction.ACTION_WAY:
                final DrawPoly poly = (DrawPoly) action;
                final int[] px = new int[poly.path.length];
                final int[] py = new int[poly.path.length];
                for (int i = 0; i < poly.path.length; i++) {
                    px[i] = canvas.getX(poly.path[i][1].doubleValue());
                    py[i] = canvas.getY(poly.path[i][0].doubleValue());
                }
                if (g instanceof Graphics2D) {
                    final Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            BasicStroke.JOIN_ROUND));
                }
                g.setColor(new Color(poly.color[0], poly.color[1], poly.color[2], poly.color[3]));
                g.drawPolyline(px, py, poly.path.length);
                break;
            case IDrawAction.ACTION_TEXT:
                final DrawText text = (DrawText) action;
                final int tx = canvas.getX(text.lon);
                final int ty = canvas.getY(text.lat);
                g.setColor(new Color(text.color[0], text.color[1], text.color[2], text.color[3]));
                g.drawString(text.text, tx, ty);
                break;
            }
        }
    }
}
