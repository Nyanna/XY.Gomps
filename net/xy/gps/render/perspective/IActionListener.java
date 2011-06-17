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

import net.xy.gps.render.IDrawAction;

/**
 * implements an listener if action could aggregated
 * 
 * @author Xyan
 * 
 */
public interface IActionListener {
  /**
   * listener should prepare the view for an complete update
   */
  public void updateStart();

  /**
   * should be called after all drawing operations are completted
   * 
   * @param success
   *          if the update were succesfull and the buffer can copied
   */
  public void updateEnd(final boolean success);

  /**
   * receives draw actions
   * 
   * @param action
   */
  public void draw(IDrawAction action);
}