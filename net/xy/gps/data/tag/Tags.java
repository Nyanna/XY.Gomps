package net.xy.gps.data.tag;

import java.util.Map;

import net.xy.gps.data.IDataObject;

public class Tags {
    // dynamic ordinary counter
    private static int ordinary = -1;

    /**
     * returns an ordinary index like an enum
     * 
     * @return
     */
    private static int inc() {
        return ++ordinary;
    }

    /**
     * streets, only way data
     */
    public static class Streets {
        public static final Tag STREET_HIGHWAY_MOTORWAY = TagFactory.simple(inc(), "highway",
                "motorway", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_MOTORWAY_LINK = TagFactory.simple(inc(), "highway",
                "motorway_link", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_TRUNK = TagFactory.simple(inc(), "highway", "trunk",
                IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_TRUNK_LINK = TagFactory.simple(inc(), "highway",
                "trunk_link", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_PRIMARY = TagFactory.simple(inc(), "highway",
                "primary", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_PRIMARY_LINK = TagFactory.simple(inc(), "highway",
                "primary_link", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_SECONDARY = TagFactory.simple(inc(), "highway",
                "secondary", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_SECONDARY_LINK = TagFactory.simple(inc(), "highway",
                "secondary_link", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_TERTIARY = TagFactory.simple(inc(), "highway",
                "tertiary", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_RESIDENTAL = TagFactory.simple(inc(), "highway",
                "residential", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_UNCLASSIFIED = TagFactory.simple(inc(), "highway",
                "unclassified", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_ROAD = TagFactory.simple(inc(), "highway", "road",
                IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_LIVING = TagFactory.simple(inc(), "highway",
                "living_street", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_SERVICE = TagFactory.simple(inc(), "highway",
                "service", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_TRACK = TagFactory.simple(inc(), "highway", "track",
                IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_PEDESTRIAN = TagFactory.simple(inc(), "highway",
                "pedestrian", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_RACEWAY = TagFactory.simple(inc(), "highway",
                "raceway", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_SERVICES = TagFactory.simple(inc(), "highway",
                "services", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_BUS = TagFactory.simple(inc(), "highway",
                "bus_guideway", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_PATH = TagFactory.simple(inc(), "highway", "path",
                IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_CYCLEWAY = TagFactory.simple(inc(), "highway",
                "cycleway", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_FOOTWAY = TagFactory.simple(inc(), "highway",
                "footway", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_BRIDLEWAY = TagFactory.simple(inc(), "highway",
                "bridleway", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_BYWAY = TagFactory.simple(inc(), "highway", "byway",
                IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_STEPS = TagFactory.simple(inc(), "highway", "steps",
                IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_ROUNDABOUT = TagFactory.simple(inc(), "junction",
                "roundabout", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_FORD = TagFactory.simple(inc(), "highway", "ford",
                IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_CONSTRUCTION = TagFactory.simple(inc(), "highway",
                "construction", IDataObject.DATA_WAY);
        public static final Tag STREET_HIGHWAY_PROPOSED = TagFactory.simple(inc(), "highway",
                "proposed", IDataObject.DATA_WAY);

        public static Object[] getStreetTypes(final Map tags) {
            return null;
        }
    }

    /**
     * SIGNS or POINT objects, only nodes
     */
    public static class Signs {
        public static final Tag STREET_POINT_SERVICES = TagFactory.simple(inc(), "highway",
                "services", IDataObject.DATA_POINT);
        public static final Tag STREET_POINT_MROUNDABOUT = TagFactory.simple(inc(), "highway",
                "mini_roundabout", IDataObject.DATA_POINT);
        public static final Tag STREET_SIGN_STOP = TagFactory.simple(inc(), "highway", "stop",
                IDataObject.DATA_POINT);
        public static final Tag STREET_SIGN_GIVEWAY = TagFactory.simple(inc(), "highway",
                "give_way", IDataObject.DATA_POINT);
        public static final Tag STREET_SIGN_SIGNALS = TagFactory.simple(inc(), "highway",
                "traffic_signals", IDataObject.DATA_POINT);
        public static final Tag STREET_POINT_CROSSING = TagFactory.simple(inc(), "highway",
                "crossing", IDataObject.DATA_POINT);
        public static final Tag STREET_POINT_MOTORWAY_JUNCTION = TagFactory.simple(inc(),
                "highway", "motorway_junction", IDataObject.DATA_POINT);
        public static final Tag STREET_POINT_FORD = TagFactory.simple(inc(), "highway", "ford",
                IDataObject.DATA_POINT);
        public static final Tag STREET_SIGN_BUS = TagFactory.simple(inc(), "highway", "bus_stop",
                IDataObject.DATA_POINT);
        public static final Tag STREET_POINT_TURNING_CYCLE = TagFactory.simple(inc(), "highway",
                "turning_circle", IDataObject.DATA_POINT);
        public static final Tag STREET_SIGN_HELP = TagFactory.simple(inc(), "highway",
                "emergency_access_point", IDataObject.DATA_POINT);
        public static final Tag STREET_SIGN_SPEED_CAMERA = TagFactory.simple(inc(), "highway",
                "speed_camera", IDataObject.DATA_POINT);
        public static final Tag STREET_SIGN_LAMP = TagFactory.simple(inc(), "highway",
                "street_lamp", IDataObject.DATA_POINT);
    }

    /**
     * trafic related areas
     * 
     * @author Xyan
     * 
     */
    public static class TraficAreas {
        public static final Tag STREET_AREA_PEDESTRIAN = TagFactory.simple(inc(), "highway",
                "pedestrian", IDataObject.DATA_AREA);
        public static final Tag STREET_AREA_ROUNDABOUT = TagFactory.simple(inc(), "junction",
                "roundabout", IDataObject.DATA_AREA);
    }

}