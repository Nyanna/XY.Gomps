package net.xy.gps.client.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;

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
      final Stroke backup = g instanceof Graphics2D ? ((Graphics2D) g).getStroke() : null;
      switch (action.getType()) {
      case IDrawAction.ACTION_POINT:
        draw((DrawPoint) action);
        break;
      case IDrawAction.ACTION_AREA:
        draw((DrawArea) action);
        break;
      case IDrawAction.ACTION_WAY:
        draw((DrawPoly) action);
        break;
      case IDrawAction.ACTION_TEXT:
        draw((DrawText) action);
        break;
      }
      if (g instanceof Graphics2D && backup != null) {
        ((Graphics2D) g).setStroke(backup);
      }
    }
  }

  /**
   * draws an way or poly action
   * 
   * @param poly
   */
  private void draw(final DrawPoly poly) {
    // TODO draw dashed or surounded
    final float width = poly.width.floatValue() > 0 ? poly.width.floatValue() : 1;
    final Color mainColor = new Color(poly.color[0].intValue(), poly.color[1].intValue(),
        poly.color[2].intValue(), poly.color[3].intValue());

    if (true && g instanceof Graphics2D) { // surounded
      // construct outline
      final BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_ROUND,
          BasicStroke.JOIN_ROUND);
      final Path2D path = new Path2D.Double();
      path.moveTo(canvas.getX(poly.path[0][1].doubleValue()),
          canvas.getY(poly.path[0][0].doubleValue()));
      for (int i = 1; i < poly.path.length; i++) {
        path.lineTo(canvas.getX(poly.path[i][1].doubleValue()),
            canvas.getY(poly.path[i][0].doubleValue()));
      }
      final Shape street = stroke.createStrokedShape(path);
      // draw
      g.setColor(mainColor);
      ((Graphics2D) g).fill(street);
      if (width > 5) {
        g.setColor(Color.BLACK);
        ((Graphics2D) g).setStroke(new BasicStroke(0.25f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND));
        ((Graphics2D) g).draw(street);
      }
    } else {
      if (false && g instanceof Graphics2D) { // dashed
        // TODO implement dashes
        final float[] dash = new float[0];
        ((Graphics2D) g).setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1, dash, 0.0f));
      }
      g.setColor(mainColor);
      final int[] px = new int[poly.path.length];
      final int[] py = new int[poly.path.length];
      for (int i = 0; i < poly.path.length; i++) {
        px[i] = canvas.getX(poly.path[i][1].doubleValue());
        py[i] = canvas.getY(poly.path[i][0].doubleValue());
      }
      g.drawPolyline(px, py, poly.path.length);
    }
  }

  /**
   * draws an text action
   * 
   * @param text
   */
  private void draw(final DrawText text) {
    final int tx = canvas.getX(text.lon);
    final int ty = canvas.getY(text.lat);
    g.setColor(new Color(text.color[0].intValue(), text.color[1].intValue(), text.color[2]
        .intValue(), text.color[3].intValue()));
    g.drawString(text.text, tx, ty);
  }

  /**
   * draws an area action
   * 
   * @param area
   */
  private void draw(final DrawArea area) {
    final int[] nx = new int[area.path.length];
    final int[] ny = new int[area.path.length];
    for (int i = 0; i < area.path.length; i++) {
      nx[i] = canvas.getX(area.path[i][1].doubleValue());
      ny[i] = canvas.getY(area.path[i][0].doubleValue());
    }
    g.setColor(new Color(area.color[0].intValue(), area.color[1].intValue(), area.color[2]
        .intValue(), area.color[3].intValue()));
    if (area.fill) {
      g.fillPolygon(nx, ny, area.path.length);
    } else {
      if (false && g instanceof Graphics2D) { // TODO dashed area
        // ((Graphics2D) g).setStroke(new
        // BasicStroke(poly.width.floatValue()
        // > 0 ? poly.width
        // .floatValue() : 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
        // 1, dash, 0.0f));
      }
      g.drawPolygon(nx, ny, area.path.length);
    }
  }

  /**
   * draw an point action
   * 
   * @param point
   */
  private void draw(final DrawPoint point) {
    final int x = canvas.getX(point.lon);
    final int y = canvas.getY(point.lat);
    // TODO optimize not drawing shapes beyonds the view
    g.setColor(new Color(point.color[0].intValue(), point.color[1].intValue(), point.color[2]
        .intValue(), point.color[3].intValue()));
    g.drawRect(x - 2, y - 2, 4, 4);
  }
}