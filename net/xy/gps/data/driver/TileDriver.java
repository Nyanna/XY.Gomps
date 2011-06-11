package net.xy.gps.data.driver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.xy.codebasel.Log;
import net.xy.codebasel.ObjectArray;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.codebasel.serialization.SerialContext;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.IDataProvider;
import net.xy.gps.data.PoiData;
import net.xy.gps.data.WayData;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

/**
 * implements an tile reader
 * 
 * @author Xyan
 * 
 */
public class TileDriver implements IDataProvider {
    public static final ConfigKey CONF_TEXT_ERROR_TILE_READ = Config.registerValues(
            "driver.error.tile.read", "Error on reading tile");
    public static final ConfigKey CONF_TEXT_TILE_READ_STAT = Config.registerValues(
            "driver.tile.read.stat", "Loading tile tok milliseconds, dataset");
    public static final ConfigKey CONF_TEXT_TILE_READ_CACHE_NEW = Config.registerValues(
            "driver.tile.read.cache.new", "Read tile from new cache");
    public static final ConfigKey CONF_TEXT_TILE_READ_CACHE_OLD = Config.registerValues(
            "driver.tile.read.cache.old", "Read tile from old cache");
    public static final ConfigKey CONF_TEXT_CACHE_CLEAN = Config.registerValues(
            "driver.tile.cache.clean", "Clearing old cache");
    public static final ConfigKey CONF_TEXT_ERROR_TILE_WRITE = Config.registerValues(
            "driver.error.tile.write", "Write tile");
    /**
     * tile size lat
     */
    public static final double LAT_SIZE = 0.025;
    /**
     * tile size lon
     */
    public static final double LON_SIZE = 0.025;
    /**
     * reduces loading time of already cached tiles, refreshes each request
     */
    private final Map tileCache = new HashMap();
    /**
     * holds the serilization context
     */
    private final SerialContext ctx = new SerialContext(new Class[] { WayData.class, Point.class,
            Dimension.class, Rectangle.class, IDataObject.class, BasicTile.class, PoiData.class });

    public void get(final Rectangle bounds, final IDataReceiver receiver) {
        final int latTilStart = (int) Math.ceil(bounds.origin.lat / LAT_SIZE) - 1;
        final int lonTilStart = (int) Math.ceil(bounds.origin.lon / LON_SIZE) - 1;
        final int latTilRange = (int) Math.floor(bounds.dimension.width / LAT_SIZE) + 1;
        final int lonTilRange = (int) Math.floor(bounds.dimension.height / LON_SIZE) + 1;
        final Map newTileCache = new HashMap();
        for (int latt = latTilStart; latt <= latTilStart + latTilRange; latt++) {
            for (int lont = lonTilStart; lont <= lonTilStart + lonTilRange; lont++) {
                if (((Boolean) ThreadLocal.get()).booleanValue()) {
                    return;
                }
                // foreach x12y17 get data
                final String tilename = getTileName(latt, lont);
                BasicTile tile = (BasicTile) tileCache.get(tilename);
                if (tile == null) {
                    tile = (BasicTile) newTileCache.get(tilename);
                    if (tile == null) {
                        final long start = System.currentTimeMillis();
                        try {
                            final DataInputStream oin = new DataInputStream(new FileInputStream(
                                    new File(tilename)));
                            tile = (BasicTile) ctx.deserialize(oin);
                        } catch (final FileNotFoundException e) {
                            continue; // skip reading tile is empty
                        } catch (final Exception e) {
                            Log.warning(CONF_TEXT_ERROR_TILE_READ, new Object[] { tilename });
                            if (e.getCause() != null) {
                                Log.log(Log.LVL_NOTICE,
                                        e.toString() + "\n" + Log.printStack(e.getStackTrace(), 3)
                                                + "\nCaused by: " + e.getCause().toString() + "\n"
                                                + Log.printStack(e.getCause().getStackTrace(), 3),
                                        null);
                            } else {
                                Log.log(Log.LVL_NOTICE,
                                        e.toString() + "\n" + Log.printStack(e.getStackTrace(), 3),
                                        null);
                            }
                            continue; // skip
                        }
                        tileCache.put(tilename, tile);
                        newTileCache.put(tilename, tile);
                        Log.comment(
                                CONF_TEXT_TILE_READ_STAT,
                                new Object[] { tilename,
                                        Long.valueOf((System.currentTimeMillis() - start)),
                                        Integer.valueOf(tile.objects.length) });
                    } else {
                        Log.comment(CONF_TEXT_TILE_READ_CACHE_NEW, new Object[] { tilename });
                    }
                } else { // from old cache
                    Log.comment(CONF_TEXT_TILE_READ_CACHE_OLD, new Object[] { tilename });
                    newTileCache.put(tilename, tile);
                }
                // and return
                receiver.accept(tile.objects);
            }
        }
        Log.comment(CONF_TEXT_CACHE_CLEAN);
        // TODO configurable layer and cache clear
        // tileCache = newTileCache; // copy over
    }

    /**
     * writes an globe to tiles
     * 
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public void writeTiles(final IDataProvider provider) throws IOException,
            IllegalArgumentException, IllegalAccessException {
        final int latTilStart, lonTilStart, latTilRange, lonTilRange;
        if (provider instanceof HSQLDriver) {
            final Rectangle bounds = ((HSQLDriver) provider).getDBBounds();
            latTilStart = (int) Math.ceil(bounds.origin.lat / LAT_SIZE);
            lonTilStart = (int) Math.ceil(bounds.origin.lon / LON_SIZE);
            latTilRange = (int) Math.floor(bounds.dimension.width / LAT_SIZE);
            lonTilRange = (int) Math.floor(bounds.dimension.height / LON_SIZE);
        } else {
            latTilStart = 0;
            lonTilStart = 0;
            latTilRange = (int) Math.floor(360 / LAT_SIZE);
            lonTilRange = (int) Math.floor(360 / LON_SIZE);
        }
        for (int latt = latTilStart; latt <= latTilStart + latTilRange; latt++) {
            for (int lont = lonTilStart; lont <= lonTilStart + lonTilRange; lont++) {
                final Rectangle bounds = new Rectangle(new Point(latt * LAT_SIZE, lont * LON_SIZE),
                        new Dimension(LAT_SIZE, LON_SIZE));
                final String tilename = getTileName(latt, lont);
                Log.notice(CONF_TEXT_ERROR_TILE_WRITE, new Object[] { tilename });
                final ObjectArray objects = new ObjectArray();
                provider.get(bounds, new IDataReceiver() {

                    public void accept(final Object[] data) {
                        for (int i = 0; i < data.length; i++) {
                            final Object dat = data[i];
                            objects.add(dat);
                        }
                    }
                });
                if (objects.getLastIndex() > -1) { // if data available
                    new File("osm/tiles/" + latt + "/").mkdirs();
                    final File ofile = new File(tilename);
                    final DataOutputStream out = new DataOutputStream(new FileOutputStream(ofile));
                    final IDataObject[] objs = new IDataObject[objects.get().length];
                    objects.toType(objs);
                    ctx.serialize(out, new BasicTile(objs));
                }
            }
        }
    }

    /**
     * constructs tilename
     * 
     * @param latt
     * @param lont
     * @return
     */
    private String getTileName(final int latt, final int lont) {
        return "osm/tiles/" + latt + "/" + lont + ".tile";
    }
}