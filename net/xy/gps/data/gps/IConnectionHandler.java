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
package net.xy.gps.data.gps;

/**
 * communicator for the chanel
 * 
 * @author Xyan
 * 
 */
public interface IConnectionHandler {

  /**
   * input arived and will be proccessed by this method
   * 
   * @param buffer
   * @param inbuf
   */
  public void in(byte[] buffer, int inbuf);

  /**
   * handler flag to indicated if the connection should be closed
   * 
   * @return
   */
  public boolean close();

  /**
   * gets output from the handler to the connection
   * 
   * @return
   */
  public byte[] getOutput();

  /**
   * indicates that the handler has output to send
   * 
   * @return
   */
  public boolean isAvailable();
}