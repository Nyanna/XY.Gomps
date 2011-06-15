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
package net.xy.gps.render.perspective;

import net.xy.gps.render.ICanvas;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

/**
 * movement and control facility for canvass
 * 
 * @author Xyan
 * 
 */
public class MoveUtils {
  /**
   * reference to canvas
   */
  private final ICanvas canvas;
  // relative move and zoom stepping of viewport
  private final double move = 0.05; // 5%
  private final double zoom = 0.2; // 20%
  /**
   * change listener
   */
  private final IChangeNotifier listener;

  /**
   * default
   * 
   * @param canvas
   */
  public MoveUtils(final ICanvas canvas) {
    this(canvas, null);
  }

  /**
   * add an listener
   * 
   * @param canvas
   * @param listener
   */
  public MoveUtils(final ICanvas canvas, final IChangeNotifier listener) {
    this.canvas = canvas;
    this.listener = listener;
  }

  /**
   * quanted movement with buildin settings
   * 
   * @param x
   * @param y
   */
  public void move(final double x, final double y, final double z) {
    final Dimension dimension = canvas.getViewPort().dimension;
    final Point origin = canvas.getViewPort().origin;
    if (x > 0 || x < 0) {
      final double byX = dimension.width * move * x;
      canvas.setViewPort(origin.lat, origin.lon + byX);
    }
    if (y > 0 || y < 0) {
      final double byY = dimension.height * move * y;
      canvas.setViewPort(origin.lat + byY, origin.lon);
    }
    final double byW = dimension.width * zoom;
    final double byH = dimension.height * zoom;
    if (z > 0) {
      final double newLat = origin.lat + byW / 2;
      final double newLon = origin.lon + byH / 2;
      canvas.setViewPort(newLat, newLon, dimension.width - byW, dimension.height - byH);
    } else if (z < 0) {
      final double newLat = origin.lat - byW / 2;
      final double newLon = origin.lon - byH / 2;
      canvas.setViewPort(newLat, newLon, dimension.width + byW, dimension.height + byH);
    }
    if (!canvas.isValid() && listener != null) {
      listener.notified();
    }
  }

  /**
   * discrete moving by pixel offsets
   * 
   * @param movX
   * @param movY
   */
  public void moveBy(final int movX, final int movY) {
    final Rectangle viewPort = canvas.getViewPort();
    final double moveLat = movY * canvas.getPixelSize().height;
    final double moveLon = movX * canvas.getPixelSize().width;
    canvas.setViewPort(viewPort.origin.lat + moveLat, viewPort.origin.lon + moveLon);
    if (!canvas.isValid() && listener != null) {
      listener.notified();
    }
  }

  /**
   * refreshes main content aread of canvas
   * 
   * @param height
   * @param width
   */
  public void size(final int width, final int height) {
    canvas.setSize(width, height);
    if (!canvas.isValid() && listener != null) {
      listener.notified();
    }
  }

  /**
   * callback to notify if the canvas was altered through these adapter
   * 
   * @author Xyan
   * 
   */
  public static interface IChangeNotifier {
    /**
     * an changed occures
     */
    public void notified();
  }
}