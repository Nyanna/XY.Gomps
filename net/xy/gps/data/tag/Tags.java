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
     * political borders
     * 
     * @author xyan
     * 
     */
    public static class PoliticalAreas {
        public static final Tag POLITICAL_BOUNDARY_ADMIN = TagFactory.simple(inc(), "boundary",
                "administrative", IDataObject.DATA_WAY, "POLITICAL_BOUNDARY");
        public static final Tag POLITICAL_BOUNDARY_CIVIL = TagFactory.simple(inc(), "boundary",
                "civil", IDataObject.DATA_WAY, "POLITICAL_BOUNDARY_CIVIL");
        public static final Tag POLITICAL_BOUNDARY_POLITICAL = TagFactory.simple(inc(), "boundary",
                "political", IDataObject.DATA_WAY, "POLITICAL_BOUNDARY_POLITICAL");
        public static final Tag POLITICAL_BOUNDARY_MARITIME = TagFactory.simple(inc(), "boundary",
                "maritime", IDataObject.DATA_WAY, "POLITICAL_BOUNDARY_MARITIME");
        public static final Tag POLITICAL_BOUNDARY_PARK = TagFactory.simple(inc(), "boundary",
                "national_park", IDataObject.DATA_WAY, "POLITICAL_BOUNDARY_PARK");
        public static final Tag POLITICAL_BOUNDARY_PROTECTED = TagFactory.simple(inc(), "boundary",
                "protected_area", IDataObject.DATA_WAY, "POLITICAL_BOUNDARY_PROTECTED");
        public static final Tag POLITICAL_PLACE_CONTINENT = TagFactory.simple(inc(), "boundary",
                "continent", IDataObject.DATA_WAY, "POLITICAL_PLACE_CONTINENT");
        public static final Tag POLITICAL_PLACE_COUNTRY = TagFactory.simple(inc(), "boundary",
                "country", IDataObject.DATA_WAY, "POLITICAL_PLACE_COUNTRY");
        public static final Tag POLITICAL_PLACE_STATE = TagFactory.simple(inc(), "boundary",
                "state", IDataObject.DATA_WAY, "POLITICAL_PLACE_STATE");
        public static final Tag POLITICAL_PLACE_REGION = TagFactory.simple(inc(), "boundary",
                "region", IDataObject.DATA_WAY, "POLITICAL_PLACE_REGION");
        public static final Tag POLITICAL_PLACE_COUNTY = TagFactory.simple(inc(), "boundary",
                "county", IDataObject.DATA_WAY, "POLITICAL_PLACE_COUNTY");
        public static final Tag POLITICAL_PLACE_CITY = TagFactory.simple(inc(), "boundary", "city",
                IDataObject.DATA_WAY, "POLITICAL_PLACE_CITY");
        public static final Tag POLITICAL_PLACE_TOWN = TagFactory.simple(inc(), "boundary", "town",
                IDataObject.DATA_WAY, "POLITICAL_PLACE_TOWN");
        public static final Tag POLITICAL_PLACE_VILLAGE = TagFactory.simple(inc(), "boundary",
                "village", IDataObject.DATA_WAY, "POLITICAL_PLACE_VILLAGE");
        public static final Tag POLITICAL_PLACE_HAMLET = TagFactory.simple(inc(), "boundary",
                "hamlet", IDataObject.DATA_WAY, "POLITICAL_PLACE_HAMLET");
        public static final Tag POLITICAL_PLACE_DWELLING = TagFactory.simple(inc(), "boundary",
                "isolated_dwelling", IDataObject.DATA_WAY, "POLITICAL_PLACE_DWELLING");
        public static final Tag POLITICAL_PLACE_SUBURB = TagFactory.simple(inc(), "boundary",
                "suburb", IDataObject.DATA_WAY, "POLITICAL_PLACE_SUBURB");
        public static final Tag POLITICAL_PLACE_LOCALITY = TagFactory.simple(inc(), "boundary",
                "locality", IDataObject.DATA_WAY, "POLITICAL_PLACE_LOCALITY");
        public static final Tag POLITICAL_PLACE_ISLAND = TagFactory.simple(inc(), "boundary",
                "island", IDataObject.DATA_WAY, "POLITICAL_PLACE_ISLAND");
        public static final Tag POLITICAL_PLACE_ISLET = TagFactory.simple(inc(), "boundary",
                "islet", IDataObject.DATA_WAY, "POLITICAL_PLACE_ISLET");

    }

    /**
     * geographical or natural areas
     * 
     * @author xyan
     * 
     */
    public static class GeographicAreas {
        public static final Tag GEO_NATURAL_BAY = TagFactory.simple(inc(), "natural", "bay",
                IDataObject.DATA_WAY, "GEO_NATURAL_BAY");
        public static final Tag GEO_NATURAL_BEACH = TagFactory.simple(inc(), "natural", "beach",
                IDataObject.DATA_WAY, "GEO_NATURAL_BEACH");
        public static final Tag GEO_NATURAL_CAVE = TagFactory.simple(inc(), "natural",
                "cave_entrance", IDataObject.DATA_WAY, "GEO_NATURAL_CAVE");
        public static final Tag GEO_NATURAL_CLIFF = TagFactory.simple(inc(), "natural", "cliff",
                IDataObject.DATA_WAY, "GEO_NATURAL_CLIFF");
        public static final Tag GEO_NATURAL_COASTLINE = TagFactory.simple(inc(), "natural",
                "coastline", IDataObject.DATA_WAY, "GEO_NATURAL_COASTLINE");
        public static final Tag GEO_NATURAL_FELL = TagFactory.simple(inc(), "natural", "fell",
                IDataObject.DATA_WAY, "GEO_NATURAL_FELL");
        public static final Tag GEO_NATURAL_GLACIER = TagFactory.simple(inc(), "natural",
                "glacier", IDataObject.DATA_WAY, "GEO_NATURAL_GLACIER");
        public static final Tag GEO_NATURAL_HEATH = TagFactory.simple(inc(), "natural", "heath",
                IDataObject.DATA_WAY, "GEO_NATURAL_HEATH");
        public static final Tag GEO_NATURAL_LAND = TagFactory.simple(inc(), "natural", "land",
                IDataObject.DATA_WAY, "GEO_NATURAL_LAND");
        public static final Tag GEO_NATURAL_MARSH = TagFactory.simple(inc(), "natural", "marsh",
                IDataObject.DATA_WAY, "GEO_NATURAL_MARSH");
        public static final Tag GEO_NATURAL_MUD = TagFactory.simple(inc(), "natural", "mud",
                IDataObject.DATA_WAY, "GEO_NATURAL_MUD");
        public static final Tag GEO_NATURAL_SAND = TagFactory.simple(inc(), "natural", "sand",
                IDataObject.DATA_WAY, "GEO_NATURAL_SAND");
        public static final Tag GEO_NATURAL_SCREE = TagFactory.simple(inc(), "natural", "scree",
                IDataObject.DATA_WAY, "GEO_NATURAL_SCREE");
        public static final Tag GEO_NATURAL_SCRUB = TagFactory.simple(inc(), "natural", "scrub",
                IDataObject.DATA_WAY, "GEO_NATURAL_SCRUB");
        public static final Tag GEO_NATURAL_WATER = TagFactory.simple(inc(), "natural", "water",
                IDataObject.DATA_WAY, "GEO_NATURAL_WATER");
        public static final Tag GEO_NATURAL_WETLAND = TagFactory.simple(inc(), "natural",
                "wetland", IDataObject.DATA_WAY, "GEO_NATURAL_WETLAND");
        public static final Tag GEO_NATURAL_WOOD = TagFactory.simple(inc(), "natural", "wood",
                IDataObject.DATA_WAY, "GEO_NATURAL_WOOD");
    }

    /**
     * streets, only way data
     */
    public static class Streets {
        public static final Tag STREET_HIGHWAY_MOTORWAY = TagFactory.simple(inc(), "highway",
                "motorway", IDataObject.DATA_WAY, "STREET_HIGHWAY_MOTORWAY");
        public static final Tag STREET_HIGHWAY_MOTORWAY_LINK = TagFactory.simple(inc(), "highway",
                "motorway_link", IDataObject.DATA_WAY, "STREET_HIGHWAY_MOTORWAY_LINK");
        public static final Tag STREET_HIGHWAY_TRUNK = TagFactory.simple(inc(), "highway", "trunk",
                IDataObject.DATA_WAY, "STREET_HIGHWAY_TRUNK");
        public static final Tag STREET_HIGHWAY_TRUNK_LINK = TagFactory.simple(inc(), "highway",
                "trunk_link", IDataObject.DATA_WAY, "STREET_HIGHWAY_TRUNK_LINK");
        public static final Tag STREET_HIGHWAY_PRIMARY = TagFactory.simple(inc(), "highway",
                "primary", IDataObject.DATA_WAY, "STREET_HIGHWAY_PRIMARY");
        public static final Tag STREET_HIGHWAY_PRIMARY_LINK = TagFactory.simple(inc(), "highway",
                "primary_link", IDataObject.DATA_WAY, "STREET_HIGHWAY_PRIMARY_LINK");
        public static final Tag STREET_HIGHWAY_SECONDARY = TagFactory.simple(inc(), "highway",
                "secondary", IDataObject.DATA_WAY, "STREET_HIGHWAY_SECONDARY");
        public static final Tag STREET_HIGHWAY_SECONDARY_LINK = TagFactory.simple(inc(), "highway",
                "secondary_link", IDataObject.DATA_WAY, "STREET_HIGHWAY_SECONDARY_LINK");
        public static final Tag STREET_HIGHWAY_TERTIARY = TagFactory.simple(inc(), "highway",
                "tertiary", IDataObject.DATA_WAY, "STREET_HIGHWAY_TERTIARY");
        public static final Tag STREET_HIGHWAY_RESIDENTAL = TagFactory.simple(inc(), "highway",
                "residential", IDataObject.DATA_WAY, "STREET_HIGHWAY_RESIDENTAL");
        public static final Tag STREET_HIGHWAY_UNCLASSIFIED = TagFactory.simple(inc(), "highway",
                "unclassified", IDataObject.DATA_WAY, "STREET_HIGHWAY_UNCLASSIFIED");
        public static final Tag STREET_HIGHWAY_ROAD = TagFactory.simple(inc(), "highway", "road",
                IDataObject.DATA_WAY, "STREET_HIGHWAY_ROAD");
        public static final Tag STREET_HIGHWAY_LIVING = TagFactory.simple(inc(), "highway",
                "living_street", IDataObject.DATA_WAY, "STREET_HIGHWAY_LIVING");
        public static final Tag STREET_HIGHWAY_SERVICE = TagFactory.simple(inc(), "highway",
                "service", IDataObject.DATA_WAY, "STREET_HIGHWAY_SERVICE");
        public static final Tag STREET_HIGHWAY_TRACK = TagFactory.simple(inc(), "highway", "track",
                IDataObject.DATA_WAY, "STREET_HIGHWAY_TRACK");
        public static final Tag STREET_HIGHWAY_PEDESTRIAN = TagFactory.simple(inc(), "highway",
                "pedestrian", IDataObject.DATA_WAY, "STREET_HIGHWAY_PEDESTRIAN");
        public static final Tag STREET_HIGHWAY_RACEWAY = TagFactory.simple(inc(), "highway",
                "raceway", IDataObject.DATA_WAY, "STREET_HIGHWAY_RACEWAY");
        public static final Tag STREET_HIGHWAY_SERVICES = TagFactory.simple(inc(), "highway",
                "services", IDataObject.DATA_WAY, "STREET_HIGHWAY_SERVICES");
        public static final Tag STREET_HIGHWAY_BUS = TagFactory.simple(inc(), "highway",
                "bus_guideway", IDataObject.DATA_WAY, "STREET_HIGHWAY_BUS");
        public static final Tag STREET_HIGHWAY_PATH = TagFactory.simple(inc(), "highway", "path",
                IDataObject.DATA_WAY, "STREET_HIGHWAY_PATH");
        public static final Tag STREET_HIGHWAY_CYCLEWAY = TagFactory.simple(inc(), "highway",
                "cycleway", IDataObject.DATA_WAY, "STREET_HIGHWAY_CYCLEWAY");
        public static final Tag STREET_HIGHWAY_FOOTWAY = TagFactory.simple(inc(), "highway",
                "footway", IDataObject.DATA_WAY, "STREET_HIGHWAY_FOOTWAY");
        public static final Tag STREET_HIGHWAY_BRIDLEWAY = TagFactory.simple(inc(), "highway",
                "bridleway", IDataObject.DATA_WAY, "STREET_HIGHWAY_BRIDLEWAY");
        public static final Tag STREET_HIGHWAY_BYWAY = TagFactory.simple(inc(), "highway", "byway",
                IDataObject.DATA_WAY, "STREET_HIGHWAY_BYWAY");
        public static final Tag STREET_HIGHWAY_STEPS = TagFactory.simple(inc(), "highway", "steps",
                IDataObject.DATA_WAY, "STREET_HIGHWAY_STEPS");
        public static final Tag STREET_HIGHWAY_ROUNDABOUT = TagFactory.simple(inc(), "junction",
                "roundabout", IDataObject.DATA_WAY, "STREET_HIGHWAY_ROUNDABOUT");
        public static final Tag STREET_HIGHWAY_FORD = TagFactory.simple(inc(), "highway", "ford",
                IDataObject.DATA_WAY, "STREET_HIGHWAY_FORD");
        public static final Tag STREET_HIGHWAY_CONSTRUCTION = TagFactory.simple(inc(), "highway",
                "construction", IDataObject.DATA_WAY, "STREET_HIGHWAY_CONSTRUCTION");
        public static final Tag STREET_HIGHWAY_PROPOSED = TagFactory.simple(inc(), "highway",
                "proposed", IDataObject.DATA_WAY, "STREET_HIGHWAY_PROPOSED");
        public static final Tag STREET_HIGHWAY_PUBLIC_TRANSPORT = TagFactory.simple(inc(),
                "public_transport", "platform", IDataObject.DATA_WAY,
                "STREET_HIGHWAY_PUBLIC_TRANSPORT");

        public static Object[] getStreetTypes(final Map tags) {
            return null;
        }
    }

    /**
     * SIGNS or POINT objects, only nodes
     */
    public static class Signs {
        public static final Tag STREET_POINT_SERVICES = TagFactory.simple(inc(), "highway",
                "services", IDataObject.DATA_POINT, "STREET_POINT_SERVICES");
        public static final Tag STREET_POINT_MROUNDABOUT = TagFactory.simple(inc(), "highway",
                "mini_roundabout", IDataObject.DATA_POINT, "STREET_POINT_MROUNDABOUT");
        public static final Tag STREET_SIGN_STOP = TagFactory.simple(inc(), "highway", "stop",
                IDataObject.DATA_POINT, "STREET_SIGN_STOP");
        public static final Tag STREET_SIGN_GIVEWAY = TagFactory.simple(inc(), "highway",
                "give_way", IDataObject.DATA_POINT, "STREET_SIGN_GIVEWAY");
        public static final Tag STREET_SIGN_SIGNALS = TagFactory.simple(inc(), "highway",
                "traffic_signals", IDataObject.DATA_POINT, "STREET_SIGN_SIGNALS");
        public static final Tag STREET_POINT_CROSSING = TagFactory.simple(inc(), "highway",
                "crossing", IDataObject.DATA_POINT, "STREET_POINT_CROSSING");
        public static final Tag STREET_POINT_MOTORWAY_JUNCTION = TagFactory.simple(inc(),
                "highway", "motorway_junction", IDataObject.DATA_POINT,
                "STREET_POINT_MOTORWAY_JUNCTION");
        public static final Tag STREET_POINT_FORD = TagFactory.simple(inc(), "highway", "ford",
                IDataObject.DATA_POINT, "STREET_POINT_FORD");
        public static final Tag STREET_SIGN_BUS = TagFactory.simple(inc(), "highway", "bus_stop",
                IDataObject.DATA_POINT, "STREET_SIGN_BUS");
        public static final Tag STREET_POINT_TURNING_CYCLE = TagFactory.simple(inc(), "highway",
                "turning_circle", IDataObject.DATA_POINT, "STREET_POINT_TURNING_CYCLE");
        public static final Tag STREET_SIGN_HELP = TagFactory.simple(inc(), "highway",
                "emergency_access_point", IDataObject.DATA_POINT, "STREET_SIGN_HELP");
        public static final Tag STREET_SIGN_SPEED_CAMERA = TagFactory.simple(inc(), "highway",
                "speed_camera", IDataObject.DATA_POINT, "STREET_SIGN_SPEED_CAMERA");
        public static final Tag STREET_SIGN_LAMP = TagFactory.simple(inc(), "highway",
                "street_lamp", IDataObject.DATA_POINT, "STREET_SIGN_LAMP");
        public static final Tag STREET_SIGN_BUSSTATION = TagFactory.simple(inc(),
                "public_transport", "platform", IDataObject.DATA_POINT, "STREET_SIGN_BUSSTATION");
        public static final Tag STREET_SIGN_PASSING = TagFactory.simple(inc(), "highway",
                "passing_place", IDataObject.DATA_POINT, "STREET_SIGN_PASSING");
    }

    /**
     * trafic related areas
     * 
     * @author Xyan
     * 
     */
    public static class TraficAreas {
        public static final Tag STREET_AREA_PEDESTRIAN = TagFactory.simple(inc(), "highway",
                "pedestrian", IDataObject.DATA_AREA, "STREET_AREA_PEDESTRIAN");
        public static final Tag STREET_AREA_ROUNDABOUT = TagFactory.simple(inc(), "junction",
                "roundabout", IDataObject.DATA_AREA, "STREET_AREA_ROUNDABOUT");
        public static final Tag STREET_AREA_BUSSTATION = TagFactory.simple(inc(),
                "public_transport", "platform", IDataObject.DATA_AREA, "STREET_AREA_BUSSTATION");
    }

    /**
     * trafic calming
     * TODO implement simple method accepting two possible types nodes and ways
     * 
     * @author Xyan
     * 
     */
    public static class TraficCalming {

    }

    /**
     * barries uses combined point way
     * TODO implement simple method accepting two possible types nodes and ways
     * TODO implement image ico style to show for points
     * 
     * @author Xyan
     * 
     */
    public static class Barriers {

    }

    /**
     * bicycle ways dual tagged on roads
     * 
     * @author Xyan
     * 
     */
    public static class CycleWay {

    }

    /**
     * have all the same color and config key
     * 
     * @author Xyan
     * 
     */
    public static class WaterWays {
        // Waterway-related features
    }

    /**
     * railway types and stations
     * 
     * @author Xyan
     * 
     */
    public static class RailWays {

    }

    /**
     * airports mainly
     * 
     * @author Xyan
     * 
     */
    public static class AeroWays {

    }

    /*
     * goot to have:
     * aerialway
     * power
     * misc - leuchtturm messstation
     * Freizeit
     * Nutzung/Einrichtung/einkaufen/amenity
     * shop/craft
     * ermergency
     * tourism/Historisch
     * landuse/friedhof/military
     * TODO import germany
     */
}