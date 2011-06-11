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