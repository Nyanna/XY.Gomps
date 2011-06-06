package net.xy.gps.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import net.xy.codebase.Debug;
import net.xy.codebase.ThreadLocal;
import net.xy.codebasel.Log;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.gps.render.IDataProvider;
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
    private static final ConfigKey CONF_DB_COLLECTED_NODES = Config.registerValues("osm.source.collected.nodes",
            "Got Nodes");
    private static final ConfigKey CONF_DB_COLLECTED_WAYS = Config.registerValues("osm.source.collected.ways",
            "Got Ways");
    private static final ConfigKey CONF_DB_ERROR_DO = Config.registerValues("osm.source.error.do",
            "Error on accessing DB");
    private static final ConfigKey CONF_DB_ERROR_RESET = Config.registerValues("osm.source.error.reset",
            "Error on reset DB");
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
        ThreadLocal.set(false);
        c = DriverManager.getConnection(Config.getString(CONF_DB_SOURCE), "SA", "");
    }

    @Override
    public void get(final Rectangle bounds, final IDataReceiver receiver) {
        final Statement query;
        try {
            query = c.createStatement();
            ResultSet result = query.executeQuery("select * from nodes where lat > " + bounds.upperleft.lat
                    + " and lat < " + (bounds.upperleft.lat + bounds.dimension.width) + " and lon > "
                    + bounds.upperleft.lon + " and lon < " + (bounds.upperleft.lon + bounds.dimension.height));
            int nodes = 0;
            while (result.next()) {
                if ((Boolean) ThreadLocal.get()) {
                    return;
                }
                receiver.accept(new IDataObject[] { new PoiData(result.getDouble("lat"), result.getDouble("lon"), "Poi") });
                nodes++;
            }
            Log.notice(CONF_DB_COLLECTED_NODES, new Object[] { nodes });
            // target boundingbox is in view //START
            final double boxlatstart = bounds.upperleft.lat;
            final double boxlonstart = bounds.upperleft.lon;
            final double boxlatend = bounds.upperleft.lat + bounds.dimension.width;
            final double boxlonend = bounds.upperleft.lon + bounds.dimension.height;
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
                if ((Boolean) ThreadLocal.get()) {
                    return;
                }
                final Object[] coordPairs = (Object[]) result.getArray("path").getArray();
                final Double[][] cords = new Double[coordPairs.length / 2][2];
                for (int i = 0; i < coordPairs.length; i++) {
                    cords[i / 2][0] = (Double) coordPairs[i];
                    cords[i / 2][1] = (Double) coordPairs[i + 1];
                    i++;
                }
                receiver.accept(new IDataObject[] { new WayData(cords) });
                ways++;
            }
            Log.notice(CONF_DB_COLLECTED_WAYS, new Object[] { ways });
        } catch (final SQLException e) {
            Log.fattal(CONF_DB_ERROR_DO, new Object[] { e });
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
            query.execute("CREATE TABLE ways (id INTEGER PRIMARY KEY, minlat DOUBLE, minlon DOUBLE,maxlat DOUBLE, maxlon DOUBLE, path DOUBLE ARRAY[4096])");
            query.execute("CREATE INDEX waypos ON ways (minlat,minlon,maxlat,maxlon)");
            query.execute("DROP TABLE IF EXISTS waynodes");
            query.execute("CREATE TABLE waynodes (wayid INTEGER, nodeid INTEGER)");
        } catch (final SQLException e) {
            throw new IllegalStateException("Could not reset Tables", e);
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
            query.execute("INSERT INTO nodes (id,lat,lon) VALUES (" + data.label + "," + data.getPosition().lat + ","
                    + data.getPosition().lon + ")");
            query.close();
        } catch (final SQLException e) {
            throw new IllegalStateException("Could not add node", e);
        }
    }

    /**
     * creates an way by using its nodes positions and removes the single nodes
     * from db
     * 
     * @param id
     * @param nodes
     */
    public void convertWay(final int id, final List nodes) {
        final Statement query;
        String qstr = null;
        try {
            query = c.createStatement();
            final StringBuilder path = new StringBuilder();
            final Boundary maxmin = new Boundary(new char[] { '<', '<', '>', '>' });
            for (int i = 0; i < nodes.size(); i++) {
                final Object nodeId = nodes.get(i);
                final ResultSet result = query.executeQuery("SELECT * FROM nodes WHERE id = " + nodeId.toString());
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
                query.execute("INSERT INTO waynodes (wayid,nodeid) VALUES (" + id + "," + nodeId.toString() + ")");
            }
            qstr = "INSERT INTO ways (id,minlat,minlon,maxlat,maxlon,path) VALUES (" + id + "," + maxmin.values[0]
                    + "," + maxmin.values[1] + "," + maxmin.values[2] + "," + maxmin.values[3] + ", ARRAY[" + path
                    + "])";
            query.execute(qstr);
            query.close();
        } catch (final SQLException e) {
            throw new IllegalStateException(Debug.values("Could not convert way", id, nodes, qstr), e);
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
            throw new IllegalStateException(Debug.values("Error on removing an node"), e);
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
            throw new IllegalStateException(Debug.values("Error accessing DB"), e);
        }
        return new Rectangle(new Point(0, 0), new Dimension());
    }
}