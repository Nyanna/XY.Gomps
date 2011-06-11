package net.xy.gps.render.draw;

import net.xy.gps.render.IDrawAction;

/**
 * draws multipoint ways
 * 
 * @author Xyan
 * 
 */
public class DrawPoly implements IDrawAction {
  /**
   * int[] coordinate pairs
   * int[] x,y position in lat and lon
   */
  public final Double[][] path;
  /**
   * stores path color
   */
  public final Integer[] color;
  /**
   * stores the width of the line
   */
  public final Double width;

  /**
   * default constructor
   * 
   * @param path
   */
  public DrawPoly(final Double[][] path, final Integer[] color, final Double width) {
    this.path = path;
    this.color = color;
    this.width = width;
  }

  public int getType() {
    return IDrawAction.ACTION_WAY;
  }
}
