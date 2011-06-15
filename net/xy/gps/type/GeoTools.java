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
package net.xy.gps.type;

/**
 * utility class
 * 
 * @author Xyan
 * 
 */
public class GeoTools {

  /**
   * converts meters in an latitude offset
   * 
   * @param distance
   * @return
   */
  public static double metersToLon(final double distance, final double onLat) {
    // calc 1 lon degree distance
    // meters e.g.1 m
    final Double dis = org.geotools.GeoTools.orthodromicDistance(0, onLat, 1, onLat);
    if (dis != null) {
      return distance / dis.doubleValue();
    }
    return 0.1;
  }

  /**
   * gets the distance between two latitudes
   * 
   * @param sx1
   * @param sx2
   * @return
   */
  public static Double getDistance(final double lon1, final double lon2, final double onLat) {
    final Double res = org.geotools.GeoTools.orthodromicDistance(lon1, onLat, lon2, onLat);
    if (res != null) {
      return res;
    }
    return Double.valueOf(10D);
  }

  /**
   * converts meters in an representive string
   * 
   * @param meters
   * @return
   */
  public static String convertMeters(final double meters) {
    if (meters > 1000) {
      return (int) (meters / 1000) + "km";
    } else if (meters > 1) {
      return (int) meters + "m";
    } else {
      return (int) (meters / 100) + "cm";
    }
  }
}