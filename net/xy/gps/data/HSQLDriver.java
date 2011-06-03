package net.xy.gps.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.xy.codebase.ObjectArray;
import net.xy.gps.render.IDataProvider;
import net.xy.gps.type.Rectangle;

/**
 * connector to integrate hsql db
 * 
 * @author Xyan
 * 
 */
public class HSQLDriver implements IDataProvider {
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
        c = DriverManager.getConnection("jdbc:hsqldb:file:osm/data", "SA", "");
    }

    @Override
    public Iterator get(final Rectangle bounds) {
        final Statement query;
        try {
            query = c.createStatement();
            final ObjectArray results = new ObjectArray();
            final ResultSet result = query.executeQuery("select * from NODES where lat > " + bounds.upperleft.lat
                    + " and lat < " + (bounds.upperleft.lat + bounds.dimension.width) + " and lon > " + bounds.upperleft.lon
                    + " and lon < " + (bounds.upperleft.lon + bounds.dimension.height));
            while (result.next()) {
                results.add(new PoiData(result.getDouble("lat"), result.getDouble("lon"), "Poi"));
            }
            return new ResultIterator(results.get());
        } catch (final SQLException e) {
            return null;
        }
    }

    /**
     * result iterator
     * 
     * @author Xyan
     * 
     */
    private static class ResultIterator implements Iterator {
        /**
         * render objects
         */
        private final Object[] objects;
        private int c = 0; // index

        /**
         * default constructor
         * 
         * @param objects
         */
        public ResultIterator(final Object[] objects) {
            this.objects = objects;
        }

        @Override
        public IDataObject next() {
            return (IDataObject) objects[c++];
        }

        @Override
        public boolean hasNext() {
            if (c < objects.length) {
                return true;
            }
            return false;
        }
    };
}