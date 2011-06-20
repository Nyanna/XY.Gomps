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
 * handles in outstream NMEA data
 * 
 * @author Xyan
 * 
 */
public class NMEAHandler implements IConnectionHandler {
  /**
   * flag to close connection
   */
  public boolean close = false;
  /**
   * buffer to hold actoal sentence
   */
  private final byte[] buffer = new byte[2048];
  private int inbuf = 0;

  /**
   * data model
   */

  public void in(final byte[] buffer, final int inbuf) {
    System.arraycopy(buffer, 0, this.buffer, this.inbuf + 1, inbuf);
    this.inbuf += inbuf;
    if (inArray(buffer, (byte) '\n') > -1) {
      final String sen = new String(buffer).trim();

      final int last = sen.lastIndexOf('\n');
      final String rest = last < sen.length() ? sen.substring(last + 1) : "";
      System.arraycopy(rest.getBytes(), 0, this.buffer, 0, rest.length());
      this.inbuf = rest.length();

      final String[] parts = sen.substring(0, last).split("\n");
      for (int i = 0; i < parts.length; i++) {
        decode(parts[i]);
      }
    }
  }

  /**
   * searches for an given value in an array and returns it index
   * 
   * @return
   */
  private static int inArray(final byte[] array, final byte ch) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == ch) {
        return i;
      }
    }
    return -1;
  }

  /**
   * decodes one nmea message
   * 
   * @param sentence
   */
  private void decode(final String sentence) {
    System.out.println(sentence);
  }

  public boolean close() {
    return close;
  }

  public byte[] getOutput() {
    return null;
  }

  public boolean isAvailable() {
    return false;
  }
}