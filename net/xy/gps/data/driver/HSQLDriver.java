package net.xy.gps.data.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import net.xy.codebasel.Log;
import net.xy.codebasel.LogCritical;
import net.xy.codebasel.LogError;
import net.xy.codebasel.LogWarning;
import net.xy.codebasel.ObjectArray;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.IDataProvider;
import net.xy.gps.data.PoiData;
import net.xy.gps.data.WayData;
import net.xy.gps.data.tag.Tag;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.perspective.ActionListener;
import net.xy.gps.type.Boundary;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

/**
 * connector to integrate hsql db
 * 
 * @author Xyan
 * 
 */
public class HSQLDriver implements IDataProvider {
  /**
   * configuration & messages
   */
  private static final ConfigKey CONF_DB_SOURCE = Config.registerValues("osm.source.db.connection",
      "jdbc:hsqldb:file:osm/data;shutdown=true");
  private static final ConfigKey CONF_DB_COLLECTED_NODES = Config.registerValues(
      "osm.source.collected.nodes", "Got Nodes");
  private static final ConfigKey CONF_DB_COLLECTED_WAYS = Config.registerValues(
      "osm.source.collected.ways", "Got Ways");
  private static final ConfigKey CONF_DB_ERROR_DO = Config.registerValues("osm.source.error.do",
      "Error on accessing DB");
  private static final ConfigKey CONF_DB_ERROR_RESET = Config.registerValues(
      "osm.source.error.reset", "Error on reset DB");
  private static final ConfigKey CONF_DB_ERROR_ADDNODE = Config.registerValues(
      "osm.source.error.addnode", "Error on adding an node");
  private static final ConfigKey CONF_DB_ERROR_WAY_COVERT = Config.registerValues(
      "osm.source.error.way.convert", "Error on converting an way");
  private static final ConfigKey CONF_DB_ERROR_CLEAN_NODES = Config.registerValues(
      "osm.source.error.clean.nodes", "Error on cleaning way covered nodes");
  private static final ConfigKey CONF_DB_ERROR_BOUNDS = Config.registerValues(
      "osm.source.error.bounds", "Error on retrieving db boundaries");
  /**
   * stores the connection
   */
  final Connection c;

  /**
   * default constructor
   * 
   * @throws SQLException
   */
  public HSQLDriver() throws SQLException {
    ThreadLocal.set(Boolean.FALSE);
    c = DriverManager.getConnection(Config.getString(CONF_DB_SOURCE), "SA", "");
  }

  public void get(final Rectangle bounds, final IDataReceiver receiver) {
    final Statement query;
    try {
      query = c.createStatement();
      ResultSet result = query.executeQuery("select * from nodes where lat > " + bounds.origin.lat
          + " and lat < " + (bounds.origin.lat + bounds.dimension.width) + " and lon > "
          + bounds.origin.lon + " and lon < " + (bounds.origin.lon + bounds.dimension.height));
      int nodes = 0;
      final Statement getTags = c.createStatement();
      while (result.next()) {
        if (((Boolean) ThreadLocal.get()).booleanValue()) {
          return;
        }
        final ResultSet nodeTags = getTags.executeQuery("select * from nodetags where nodeid = "
            + result.getInt("id"));
        final ObjectArray tags = new ObjectArray();
        while (nodeTags.next()) {
          tags.add(Integer.valueOf(nodeTags.getInt("type")));
        }
        final Integer[] itags = new Integer[tags.getLastIndex() + 1];
        tags.toType(itags);
        receiver.accept(new IDataObject[] { new PoiData(result.getDouble("lat"), result
            .getDouble("lon"), result.getInt("id"), itags) });
        nodes++;
      }
      Log.notice(CONF_DB_COLLECTED_NODES, new Object[] { Integer.valueOf(nodes) });
      // target boundingbox is in view //START
      final double boxlatstart = bounds.origin.lat;
      final double boxlonstart = bounds.origin.lon;
      final double boxlatend = bounds.origin.lat + bounds.dimension.width;
      final double boxlonend = bounds.origin.lon + bounds.dimension.height;
      final String minlatInRange = "minlat > boxlatstart AND minlat < boxlatend";
      final String maxlatInRange = "maxlat > boxlatstart AND maxlat < boxlatend";
      final String minlonInRange = "minlon > boxlonstart AND minlon < boxlonend";
      final String maxlonInRange = "maxlon > boxlonstart AND maxlon < boxlonend";
      String dynPart = new StringBuilder()
          .append("minlatInRange AND (minlonInRange OR maxlonInRange) OR ")
          .append("maxlatInRange AND (minlonInRange OR maxlonInRange) OR ")
          .append("minlat < boxlatstart AND maxlat > boxlatend AND ")
          .append("(minlonInRange OR maxlonInRange) OR ")
          // .append("minlonInRange AND (minlatInRange OR maxlonInRange) OR ")
          // .append("maxlonInRange AND (minlatInRange OR maxlonInRange) OR ")
          .append("minlon < boxlonstart AND maxlon > boxlonend AND ")
          .append("(minlatInRange OR maxlatInRange) OR ")
          .append("minlat < boxlatstart AND maxlat > boxlatend AND ")
          .append("minlon < boxlonstart AND maxlon > boxlonend").toString();
      dynPart = dynPart.replace("minlatInRange", minlatInRange);
      dynPart = dynPart.replace("maxlatInRange", maxlatInRange);
      dynPart = dynPart.replace("minlonInRange", minlonInRange);
      dynPart = dynPart.replace("maxlonInRange", maxlonInRange);
      dynPart = dynPart.replace("boxlatstart", String.valueOf(boxlatstart));
      dynPart = dynPart.replace("boxlonstart", String.valueOf(boxlonstart));
      dynPart = dynPart.replace("boxlatend", String.valueOf(boxlatend));
      dynPart = dynPart.replace("boxlonend", String.valueOf(boxlonend));
      result = query.executeQuery("select * from ways where " + dynPart);
      // target boundingbox is in view //END
      int ways = 0;
      while (result.next()) {
        if (((Boolean) ThreadLocal.get()).booleanValue()) {
          return;
        }
        final Object[] coordPairs = (Object[]) result.getArray("path").getArray();
        final Double[][] cords = new Double[coordPairs.length / 2][2];
        for (int i = 0; i < coordPairs.length; i++) {
          cords[i / 2][0] = (Double) coordPairs[i];
          cords[i / 2][1] = (Double) coordPairs[i + 1];
          i++;
        }
        final ResultSet wayTags = getTags.executeQuery("select * from waytags where wayid = "
            + result.getInt("id"));

        final ObjectArray tags = new ObjectArray();
        while (wayTags.next()) {
          tags.add(Integer.valueOf(wayTags.getInt("type")));
        }
        final Integer[] itags = new Integer[tags.getLastIndex() + 1];
        tags.toType(itags);
        receiver.accept(new IDataObject[] { new WayData(result.getInt("id"), cords, itags) });
        ways++;
      }
      Log.notice(CONF_DB_COLLECTED_WAYS, new Object[] { Integer.valueOf(ways) });
    } catch (final SQLException e) {
      throw new LogCritical(CONF_DB_ERROR_DO, e);
    }
  }

  /**
   * reinits the whole table structure
   */
  public void resetTables() {
    final Statement query;
    try {
      query = c.createStatement();
      query.execute("DROP TABLE IF EXISTS nodes");
      query.execute("CREATE TABLE nodes (id INTEGER PRIMARY KEY, lat DOUBLE, lon DOUBLE)");
      query.execute("CREATE INDEX pos ON nodes (lat,lon)");
      query.execute("DROP TABLE IF EXISTS ways");
      query
          .execute("CREATE TABLE ways (id INTEGER PRIMARY KEY, minlat DOUBLE, minlon DOUBLE,maxlat DOUBLE, maxlon DOUBLE, path DOUBLE ARRAY[4096])");
      query.execute("CREATE INDEX waypos ON ways (minlat,minlon,maxlat,maxlon)");
      query.execute("DROP TABLE IF EXISTS waynodes");
      query.execute("CREATE TABLE waynodes (wayid INTEGER, nodeid INTEGER)");
      // tags
      query.execute("DROP TABLE IF EXISTS nodetags");
      query.execute("CREATE TABLE nodetags (tagid IDENTITY, nodeid INTEGER, type INTEGER)");
      query.execute("DROP TABLE IF EXISTS waytags");
      query.execute("CREATE TABLE waytags (tagid IDENTITY, wayid INTEGER, type INTEGER)");
    } catch (final SQLException e) {
      throw new LogCritical(CONF_DB_ERROR_RESET, e);
    }
  }

  /**
   * inserts an node into db
   * 
   * @param data
   */
  public void addNode(final PoiData data) {
    final Statement query;
    try {
      query = c.createStatement();
      query.execute("INSERT INTO nodes (id,lat,lon) VALUES (" + data.osmid + ","
          + data.getPosition().lat + "," + data.getPosition().lon + ")");
      // insert tags
      if (data.getTags().length > 0) {
        final StringBuilder inserTags = new StringBuilder(
            "INSERT INTO nodetags (nodeid,type) VALUES (");
        for (int i = 0; i < data.getTags().length; i++) {
          if (i > 0) {
            inserTags.append(",");
          }
          inserTags.append("(" + data.osmid + "," + data.getTags()[i] + ")");
        }
        inserTags.append(")");
        query.execute(inserTags.toString());
      }
      query.close();
    } catch (final SQLException e) {
      throw new LogCritical(CONF_DB_ERROR_ADDNODE, e);
    }
  }

  /**
   * creates an way by using its nodes positions and removes the single nodes
   * from db
   * 
   * @param id
   * @param nodes
   */
  public void convertWay(final int id, final List nodes, final Integer[] tags) {
    final Statement query;
    // TODO support for nodetags, the ways childs
    String qstr = null;
    try {
      query = c.createStatement();
      final StringBuilder path = new StringBuilder();
      final Boundary maxmin = new Boundary(new char[] { '<', '<', '>', '>' });
      for (int i = 0; i < nodes.size(); i++) {
        final Object nodeId = nodes.get(i);
        final ResultSet result = query.executeQuery("SELECT * FROM nodes WHERE id = "
            + nodeId.toString());
        if (result.next()) {
          if (path.length() > 0) {
            path.append(",");
          }
          final double lat = result.getDouble("lat");
          final double lon = result.getDouble("lon");
          maxmin.check(new double[] { lat, lon, lat, lon });
          path.append(lat).append(",").append(lon);
        } else {
          // maybe out of borders
          // throw new
          // IllegalStateException("Way referenced node is not in DB");
        }
        query.execute("INSERT INTO waynodes (wayid,nodeid) VALUES (" + id + "," + nodeId.toString()
            + ")");
      }
      qstr = "INSERT INTO ways (id,minlat,minlon,maxlat,maxlon,path) VALUES (" + id + ","
          + maxmin.values[0] + "," + maxmin.values[1] + "," + maxmin.values[2] + ","
          + maxmin.values[3] + ", ARRAY[" + path + "])";
      query.execute(qstr);
      // insert tags
      if (tags.length > 0) {
        final StringBuilder inserTags = new StringBuilder(
            "INSERT INTO waytags (wayid,type) VALUES (");
        for (int i = 0; i < tags.length; i++) {
          if (i > 0) {
            inserTags.append(",");
          }
          final Tag tag = TagFactory.getTag(tags[i]);
          inserTags.append("(" + id + "," + tag.id + ")");
        }
        inserTags.append(")");
        query.execute(inserTags.toString());
      }
      query.close();
    } catch (final SQLException e) {
      throw new LogCritical(CONF_DB_ERROR_WAY_COVERT, e, new Object[] { Integer.valueOf(id), nodes,
          qstr });
    }
  }

  /**
   * cleans the db from all nodes related to at least one way
   */
  public void cleanWayAssociated() {
    final Statement query;
    try {
      query = c.createStatement();
      // final ResultSet result =
      query.execute("DELETE FROM nodes WHERE id IN (SELECT nodeid FROM waynodes)");
      // clear cache db
      query.execute("DROP TABLE IF EXISTS waynodes");
      query.execute("CREATE TABLE waynodes (wayid INTEGER, nodeid INTEGER)");
      query.close();
    } catch (final SQLException e) {
      throw new LogWarning(CONF_DB_ERROR_CLEAN_NODES, e);
    }
  }

  /**
   * returns the data borders contained in the db
   * 
   * @return
   */
  public Rectangle getDBBounds() {
    final Statement query;
    try {
      query = c.createStatement();
      ResultSet result = query.executeQuery("SELECT lat FROM nodes ORDER BY lat LIMIT 1");
      if (result.next()) {
        final double minLat = result.getDouble("lat");
        result = query.executeQuery("SELECT lat FROM nodes ORDER BY lat DESC LIMIT 1");
        if (result.next()) {
          final double maxLat = result.getDouble("lat");
          result = query.executeQuery("SELECT lon FROM nodes ORDER BY lon LIMIT 1");
          if (result.next()) {
            final double minLon = result.getDouble("lon");
            result = query.executeQuery("SELECT lon FROM nodes ORDER BY lon DESC LIMIT 1");
            if (result.next()) {
              final double maxLon = result.getDouble("lon");
              return new Rectangle(new Point(minLat, minLon), new Dimension(maxLat - minLat, maxLon
                  - minLon));
            }
          }
        }
      }
    } catch (final SQLException e) {
      throw new LogError(CONF_DB_ERROR_BOUNDS, e);
    }
    return new Rectangle(new Point(0, 0), new Dimension());
  }

  public void setListener(final ActionListener listener) {
    // No visual output for db
    // TODO visual output for db
  }
}