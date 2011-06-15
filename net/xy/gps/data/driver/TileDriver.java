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
package net.xy.gps.data.driver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import net.xy.gps.render.draw.DrawArea;
import net.xy.gps.render.draw.DrawPoint;
import net.xy.gps.render.perspective.ActionListener;
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
    private static final ConfigKey CONF_TEXT_ERROR_TILE_READ = Config.registerValues("driver.error.tile.read",
            "Error on reading tile");
    private static final ConfigKey CONF_TEXT_TILE_READ_STAT = Config.registerValues("driver.tile.read.stat",
            "Loading tile tok milliseconds, dataset");
    private static final ConfigKey CONF_TEXT_TILE_READ_CACHE_OLD = Config.registerValues("driver.tile.read.cache.old",
            "Read tile from old cache");
    private static final ConfigKey CONF_TEXT_TILE_REMOVED = Config.registerValues("driver.tile.removed",
            "Removed unused tile");
    private static final ConfigKey CONF_TEXT_ERROR_TILE_WRITE = Config.registerValues("driver.error.tile.write",
            "Write tile");
    private static final ConfigKey CONF_TEXT_ABORT_RUN = Config.registerValues("driver.obsolete.return",
            "Run is outdated returning");
    private static final ConfigKey CONF_TEXT_ABORT_REVOKE = Config.registerValues("driver.obsolete.revoke",
            "Run is outdated returning from revoking");
    private static final ConfigKey CONF_TEXT_CALL_RANGE = Config.registerValues("driver.tile.call",
            "Following tile range is requested");
    private static final ConfigKey CONF_TEXT_ABORT_MEMORY = Config.registerValues("driver.error.memory",
            "Abort tile loading less than 10% memory free");
    /**
     * holds the serilization context
     */
    private final SerialContext ctx = new SerialContext(new Class[] { WayData.class, Point.class, Dimension.class,
            Rectangle.class, IDataObject.class, BasicTile.class, PoiData.class });
    /**
     * for progress display
     */
    private ActionListener drawListener = null;
    // if tile loaded from cache
    private static final Integer[] cacheLoaded = new Integer[] { Integer.valueOf(0), Integer.valueOf(255),
            Integer.valueOf(0), Integer.valueOf(15) };
    // if loaded from file
    private static final Integer[] fileLoaded = new Integer[] { Integer.valueOf(0), Integer.valueOf(0),
            Integer.valueOf(255), Integer.valueOf(15) };
    // error on loading tile
    private static final Integer[] errorLoaded = new Integer[] { Integer.valueOf(255), Integer.valueOf(0),
            Integer.valueOf(0), Integer.valueOf(150) };
    /**
     * tile size lat
     */
    private static final double LAT_SIZE = 0.005;
    /**
     * tile size lon
     */
    private static final double LON_SIZE = 0.005;
    /**
     * reduces loading time of already cached tiles, refreshes each request
     */
    private final Map tileCache = new HashMap();

    public void get(final Rectangle bounds, final IDataReceiver receiver) {
        final int latTilStart = (int) Math.floor(bounds.origin.lat / LAT_SIZE);
        final int lonTilStart = (int) Math.floor(bounds.origin.lon / LON_SIZE);
        final int latTilRange = (int) Math.ceil(bounds.dimension.width / LAT_SIZE) + 1;
        final int lonTilRange = (int) Math.ceil(bounds.dimension.height / LON_SIZE) + 1;
        drawListener.draw(new DrawPoint(bounds.origin.lat, bounds.origin.lon, cacheLoaded));
        Log.comment(CONF_TEXT_CALL_RANGE,
                new Object[] { Integer.valueOf(latTilStart), Integer.valueOf(lonTilStart), Integer.valueOf(latTilRange),
                        Integer.valueOf(lonTilRange) });

        // check and remove phase
        Iterator spiral = new SpiralStrategy(latTilStart - 1, lonTilStart - 1, latTilRange + 2, lonTilRange + 2);
        final List keepKeyList = new ArrayList();
        // final List loadKeyList = new ArrayList();
        while (spiral.hasNext()) { // copy over used tiles
            if (((Boolean) ThreadLocal.get()).booleanValue()) {
                Log.comment(CONF_TEXT_ABORT_REVOKE);
                return;
            }
            final Integer[] coords = (Integer[]) spiral.next();
            final Point tileKey = new Point(coords[0].doubleValue(), coords[1].doubleValue());
            if (tileCache.containsKey(tileKey)) {
                keepKeyList.add(tileKey);
            } else {
                // loadKeyList.add(tilename);
            }
        }

        for (final Iterator i = tileCache.entrySet().iterator(); i.hasNext();) {
            if (((Boolean) ThreadLocal.get()).booleanValue()) {
                Log.comment(CONF_TEXT_ABORT_REVOKE);
                return;
            }
            final Entry entry = (Entry) i.next();
            final Point point = (Point) entry.getKey();
            if (!keepKeyList.contains(entry.getKey())) {
                drawState((int) point.lat, (int) point.lon, errorLoaded);
                i.remove();
                receiver.revoke(((BasicTile) entry.getValue()).objects); // revoke
                                                                         // old
                Log.comment(CONF_TEXT_TILE_REMOVED, new Object[] { point });
            }
        }
        System.gc();

        // read and add phase
        spiral = new SpiralStrategy(latTilStart, lonTilStart, latTilRange, lonTilRange);
        while (spiral.hasNext()) {
            if (((Boolean) ThreadLocal.get()).booleanValue()) {
                Log.comment(CONF_TEXT_ABORT_RUN);
                return;
            }
            final Integer[] coords = (Integer[]) spiral.next();
            final int[] lattlon = new int[] { coords[0].intValue(), coords[1].intValue() };
            final Point tileKey = new Point(lattlon[0], lattlon[1]);

            BasicTile tile = (BasicTile) tileCache.get(tileKey);
            if (tile == null) {
                if (!isFreeMem()) {
                    Log.warning(CONF_TEXT_ABORT_MEMORY);
                    return;
                }
                final long start = System.currentTimeMillis();
                try {
                    final String tileName = getTileName(lattlon[0], lattlon[1]);
                    final DataInputStream oin = new DataInputStream(new FileInputStream(new File(tileName)));
                    tile = (BasicTile) ctx.deserialize(oin);
                    oin.close();
                    drawState(lattlon[0], lattlon[1], fileLoaded);
                    tileCache.put(tileKey, tile);
                    Log.comment(
                            CONF_TEXT_TILE_READ_STAT,
                            new Object[] { tileKey, Long.valueOf((System.currentTimeMillis() - start)),
                                    Integer.valueOf(tile.objects.length) });
                    receiver.accept(tile.objects);
                } catch (final FileNotFoundException e) {
                    drawState(lattlon[0], lattlon[1], errorLoaded);
                } catch (final OutOfMemoryError e) {
                    System.gc();
                    Log.warning(CONF_TEXT_ABORT_MEMORY);
                    return;
                } catch (final Exception e) {
                    drawState(lattlon[0], lattlon[1], errorLoaded);
                    Log.warning(CONF_TEXT_ERROR_TILE_READ, new Object[] { tileKey });
                    extendedLog(e);
                }
            } else { // from old cache
                drawState(lattlon[0], lattlon[1], cacheLoaded);
                Log.comment(CONF_TEXT_TILE_READ_CACHE_OLD, new Object[] { tileKey });
                receiver.accept(tile.objects);
            }
        }
    }

    /**
     * provides extended cause logging
     * 
     * @param e
     */
    private void extendedLog(final Exception e) {
        if (e.getCause() != null) {
            Log.log(Log.LVL_NOTICE, e.toString() + "\n" + Log.printStack(e.getStackTrace(), 3) + "\nCaused by: "
                    + e.getCause().toString() + "\n" + Log.printStack(e.getCause().getStackTrace(), 3), null);
        } else {
            Log.log(Log.LVL_NOTICE, e.toString() + "\n" + Log.printStack(e.getStackTrace(), 3), null);
        }
    }

    /**
     * drwas progress informations to the listener
     * 
     * @param latTil
     * @param lonTil
     */
    private void drawState(final int latTil, final int lonTil, final Integer[] color) {
        final ActionListener listener = drawListener;
        if (listener != null) {
            listener.draw(new DrawArea(new Double[][] {
                    { Double.valueOf(latTil * LAT_SIZE), Double.valueOf(lonTil * LON_SIZE) },
                    { Double.valueOf(latTil * LAT_SIZE), Double.valueOf(lonTil * LON_SIZE + LON_SIZE) },
                    { Double.valueOf(latTil * LAT_SIZE + LAT_SIZE), Double.valueOf(lonTil * LON_SIZE + LON_SIZE) },
                    { Double.valueOf(latTil * LAT_SIZE + LAT_SIZE), Double.valueOf(lonTil * LON_SIZE) }, }, color, false));
        }
    }

    /**
     * check if there more than 10% free memory to perform fetch operation
     * 
     * @return
     */
    private boolean isFreeMem() {
        final long freeMem = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()
                + Runtime.getRuntime().freeMemory();
        final long restPer = freeMem / (Runtime.getRuntime().maxMemory() / 100);
        if (restPer > 10) {
            return true;
        }
        return false;
    }

    /**
     * writes an globe to tiles
     * 
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public void writeTiles(final IDataProvider provider) throws IOException, IllegalArgumentException,
            IllegalAccessException {
        final int latTilStart, lonTilStart, latTilRange, lonTilRange;
        if (provider instanceof HSQLDriver) {
            final Rectangle bounds = ((HSQLDriver) provider).getDBBounds();
            latTilStart = (int) Math.floor(bounds.origin.lat / LAT_SIZE);
            lonTilStart = (int) Math.floor(bounds.origin.lon / LON_SIZE);
            latTilRange = (int) Math.ceil(bounds.dimension.width / LAT_SIZE);
            lonTilRange = (int) Math.ceil(bounds.dimension.height / LON_SIZE);
        } else {
            latTilStart = 0;
            lonTilStart = 0;
            latTilRange = (int) Math.floor(360 / LAT_SIZE);
            lonTilRange = (int) Math.floor(360 / LON_SIZE);
        }
        for (int latt = latTilStart; latt <= latTilStart + latTilRange; latt++) {
            for (int lont = lonTilStart; lont <= lonTilStart + lonTilRange; lont++) {
                final Rectangle bounds = new Rectangle(new Point(latt * LAT_SIZE, lont * LON_SIZE), new Dimension(LAT_SIZE,
                        LON_SIZE));
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

                    public void revoke(final Object[] data) { // not needed
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

    public void setListener(final ActionListener listener) {
        drawListener = listener;
    }

    public String toString() {
        return "Standard tile file driver";
    }
}