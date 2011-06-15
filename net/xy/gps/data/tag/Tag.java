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
package net.xy.gps.data.tag;

import java.util.ArrayList;
import java.util.List;

import net.xy.gps.data.tag.impl.Style;

/**
 * default tag representation
 * 
 * @author Xyan
 * 
 */
public class Tag {
  /**
   * the tag unique id will be computed from factory
   */
  public int id = 0;
  /**
   * an label like name
   */
  public String name = null;
  /**
   * if elements taged with this should be displayed
   */
  public Boolean enabled = null;
  /**
   * render priority of this tag top = 1, last >= 100
   */
  public Integer priority = null;
  /**
   * the corresponding style object of this tag
   */
  public Style style = null;
  /**
   * an conditionlist to check data object against
   */
  public List conditions = new ArrayList();
  /**
   * specifies the zoom level in lon/lat on which the tag would be displayed
   */
  public double zoom = 0;

  public int hashCode() {
    return id;
  }
}