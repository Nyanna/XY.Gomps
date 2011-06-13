package net.xy.gps.data.driver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
import net.xy.gps.render.draw.DrawArea;
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
  public static final ConfigKey CONF_TEXT_ABORT_RUN = Config.registerValues(
      "driver.obsolete.return", "Run is outdated returning");
  /**
   * tile size lat
   */
  public static final double LAT_SIZE = 0.005;
  /**
   * tile size lon
   */
  public static final double LON_SIZE = 0.005;
  /**
   * reduces loading time of already cached tiles, refreshes each request
   */
  private Map tileCache = new HashMap();
  /**
   * holds the serilization context
   */
  private final SerialContext ctx = new SerialContext(new Class[] { WayData.class, Point.class,
      Dimension.class, Rectangle.class, IDataObject.class, BasicTile.class, PoiData.class });
  /**
   * for progress display
   */
  private ActionListener drawListener = null;
  // if tile loaded from cache
  private static final Integer[] cacheLoaded = new Integer[] { Integer.valueOf(0),
      Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(10) };
  // if loaded from file
  private static final Integer[] fileLoaded = new Integer[] { Integer.valueOf(0),
      Integer.valueOf(0), Integer.valueOf(255), Integer.valueOf(10) };
  // error on loading tile
  private static final Integer[] errorLoaded = new Integer[] { Integer.valueOf(255),
      Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(150) };

  public void get(final Rectangle bounds, final IDataReceiver receiver) {
    final int latTilStart = (int) Math.floor(bounds.origin.lat / LAT_SIZE);
    final int lonTilStart = (int) Math.floor(bounds.origin.lon / LON_SIZE);
    final int latTilRange = (int) Math.floor(bounds.dimension.width / LAT_SIZE) + 1;
    final int lonTilRange = (int) Math.floor(bounds.dimension.height / LON_SIZE) + 1;
    System.out.println(latTilStart + "," + lonTilStart + ":" + latTilRange + "," + lonTilRange);
    final Map newTileCache = new HashMap();
    final Iterator spiral = new SpiralStrategy(latTilStart, lonTilStart, latTilRange, lonTilRange);
    while (spiral.hasNext()) {
      final Integer[] coords = (Integer[]) spiral.next();
      final int latt = coords[0].intValue();
      final int lont = coords[1].intValue();
      final String tilename = getTileName(latt, lont);
      BasicTile tile = (BasicTile) tileCache.get(tilename);
      if (tile == null) {
        tile = (BasicTile) newTileCache.get(tilename);
        if (tile == null) {
          final long start = System.currentTimeMillis();
          try {
            final DataInputStream oin = new DataInputStream(new FileInputStream(new File(tilename)));
            tile = (BasicTile) ctx.deserialize(oin);
          } catch (final FileNotFoundException e) {
            drawState(latt, lont, errorLoaded);
            if (((Boolean) ThreadLocal.get()).booleanValue()) {
              Log.comment(CONF_TEXT_ABORT_RUN);
              return;
            }
            continue; // skip reading tile is empty
          } catch (final Exception e) {
            drawState(latt, lont, errorLoaded);
            Log.warning(CONF_TEXT_ERROR_TILE_READ, new Object[] { tilename });
            if (e.getCause() != null) {
              Log.log(
                  Log.LVL_NOTICE,
                  e.toString() + "\n" + Log.printStack(e.getStackTrace(), 3) + "\nCaused by: "
                      + e.getCause().toString() + "\n"
                      + Log.printStack(e.getCause().getStackTrace(), 3), null);
            } else {
              Log.log(Log.LVL_NOTICE, e.toString() + "\n" + Log.printStack(e.getStackTrace(), 3),
                  null);
            }
            continue; // skip
          }
          tileCache.put(tilename, tile);
          newTileCache.put(tilename, tile);
          drawState(latt, lont, fileLoaded);
          Log.comment(
              CONF_TEXT_TILE_READ_STAT,
              new Object[] { tilename, Long.valueOf((System.currentTimeMillis() - start)),
                  Integer.valueOf(tile.objects.length) });
        } else { // from new cache
          // drawState(latt, lont, cacheLoaded);
          Log.comment(CONF_TEXT_TILE_READ_CACHE_NEW, new Object[] { tilename });
        }
      } else { // from old cache
        // drawState(latt, lont, cacheLoaded);
        Log.comment(CONF_TEXT_TILE_READ_CACHE_OLD, new Object[] { tilename });
        newTileCache.put(tilename, tile);
      }
      if (((Boolean) ThreadLocal.get()).booleanValue()) {
        Log.comment(CONF_TEXT_ABORT_RUN);
        return;
      }
      // and return
      receiver.accept(tile.objects);
    }
    Log.comment(CONF_TEXT_CACHE_CLEAN);
    // TODO configurable layer and cache clear
    tileCache = newTileCache; // copy over
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
          { Double.valueOf(latTil * LAT_SIZE + LAT_SIZE),
              Double.valueOf(lonTil * LON_SIZE + LON_SIZE) },
          { Double.valueOf(latTil * LAT_SIZE + LAT_SIZE), Double.valueOf(lonTil * LON_SIZE) }, },
          color, true));
      // listener.draw(new DrawText(latTil * LAT_SIZE, lonTil * LON_SIZE, latTil
      // + "," + lonTil,
      // errorLoaded));
    }
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

  public void setListener(final ActionListener listener) {
    drawListener = listener;
  }
}