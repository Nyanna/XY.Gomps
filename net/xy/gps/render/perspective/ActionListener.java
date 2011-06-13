package net.xy.gps.render.perspective;

import net.xy.gps.render.IDrawAction;

/**
 * implements an listener if action could aggregated
 * 
 * @author Xyan
 * 
 */
public interface ActionListener {
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