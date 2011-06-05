package net.xy.gps.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.xy.codebase.Debug;
import net.xy.codebase.ObjectArray;
import net.xy.codebase.ThreadLocal;
import net.xy.codebase.serialization.SerialContext;
import net.xy.gps.render.IDataProvider;
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
    private Map tileCache = new HashMap();
    /**
     * holds the serilization context
     */
    private final SerialContext ctx = new SerialContext(new Class[] { WayData.class, Point.class,
            Dimension.class, Rectangle.class, IDataObject.class, BasicTile.class, PoiData.class });

    @Override
    public void get(final Rectangle bounds, final IDataReceiver receiver) {
        final int latTilStart = (int) Math.ceil(bounds.upperleft.lat / LAT_SIZE) - 1;
        final int lonTilStart = (int) Math.ceil(bounds.upperleft.lon / LON_SIZE) - 1;
        final int latTilRange = (int) Math.floor(bounds.dimension.width / LAT_SIZE) + 1;
        final int lonTilRange = (int) Math.floor(bounds.dimension.height / LON_SIZE) + 1;
        final Map newTileCache = new HashMap();
        for (int latt = latTilStart; latt <= latTilStart + latTilRange; latt++) {
            for (int lont = lonTilStart; lont <= lonTilStart + lonTilRange; lont++) {
                if ((Boolean) ThreadLocal.get()) {
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
                        } catch (final IOException e) {
                            throw new IllegalStateException(Debug.values("Tilefile not found",
                                    tilename), e);
                        } catch (final Exception e) {
                            System.out.println(Debug.values("Error on reading tile", tilename));
                            continue; // skip
                        }
                        newTileCache.put(tilename, tile);
                        System.out.println(Debug.values("Loading tile tok", tilename,
                                (System.currentTimeMillis() - start) / 1000));
                    } else {
                        System.out.println(Debug.values("Read tile from new cache", tilename));
                    }
                } else { // from old cache
                    System.out.println(Debug.values("Read tile from old cache", tilename));
                    newTileCache.put(tilename, tile);
                }
                // and return
                receiver.accept(tile.objects);
            }
        }
        System.out.println("Clearing old cache");
        tileCache = newTileCache; // copy over
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
            latTilStart = (int) Math.ceil(bounds.upperleft.lat / LAT_SIZE);
            lonTilStart = (int) Math.ceil(bounds.upperleft.lon / LON_SIZE);
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
                System.out.println("Write tile " + tilename);
                final ObjectArray objects = new ObjectArray();
                provider.get(bounds, new IDataReceiver() {
                    @Override
                    public void accept(final Object[] data) {
                        for (final Object dat : data) {
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