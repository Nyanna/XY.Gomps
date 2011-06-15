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
package net.xy.gps.data.driver;

import java.util.Iterator;

/**
 * spiral matrix iteration strategy
 * 
 * @author Xyan
 * 
 */
public class SpiralStrategy implements Iterator {
  /**
   * math
   */
  private final int width, height, centerX, centerY, startX, startY;
  private int posX, posY, length, left;
  private int direction; // see directions
  private static final int DOWN = 0;
  private static final int RIGHT = 1;
  private static final int UP = 2;
  private static final int LEFT = 3;
  private int bound = 0;

  /**
   * default, obmit matrix charachteristics
   * 
   * @param startX
   * @param startY
   * @param offsetX
   * @param offsetY
   */
  public SpiralStrategy(final int startX, final int startY, final int width, final int height) {
    this.startX = startX;
    this.startY = startY;
    this.width = width;
    this.height = height;
    posX = centerX = Math.round(width / 2);
    posY = centerY = Math.round(height / 2);
    length = (int) Math.pow(Math.max(width, height), 2);
    left = width * height;
  }

  public boolean hasNext() {
    return left > 0 ? true : false;
  }

  public Object next() {
    if (bound == 0) {
      bound++;
      length--;
      left--;
      direction = DOWN;
      return new Integer[] { Integer.valueOf(startX + posX), Integer.valueOf(startY + posY) };
    }
    while (length > 0) {
      final int minX = centerX - bound;
      final int maxX = centerX + bound;
      final int minY = centerY - bound;
      final int maxY = centerY + bound;
      // first check for correct direction
      if (direction == DOWN && posX - 1 < minX) {
        if (posX == minX && posY == maxY) {
          bound++; // next level
          continue;
        } else {
          direction = LEFT;
        }
      }
      if (direction == LEFT && posY - 1 < minY) {
        direction = UP;
      }
      if (direction == UP && posX + 1 > maxX) {
        direction = RIGHT;
      }
      if (direction == RIGHT && posY + 1 > maxY) {
        direction = DOWN;
      }
      // on end
      length--;
      switch (direction) {
      case DOWN:
        posX--;
        break;
      case RIGHT:
        posY++;
        break;
      case UP:
        posX++;
        break;
      case LEFT:
        posY--;
        break;
      }
      if (posX >= 0 && posX <= width && posY >= 0 && posY <= height) {
        left--;
        return new Integer[] { Integer.valueOf(startX + posX), Integer.valueOf(startY + posY) };
      }
    }
    return null; // ocurs after last call
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported by this Iterator");
  }
}
