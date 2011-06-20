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
package net.xy.gps.client.awt;

import gnu.io.CommPortIdentifier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.bluetooth.ServiceRecord;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.xy.codebasel.Log;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.Utils;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
import net.xy.codebasel.config.TextPropertyRetriever;
import net.xy.gps.client.awt.config.ConfigMenuBar;
import net.xy.gps.data.IDataProvider;
import net.xy.gps.data.driver.HSQLDriver;
import net.xy.gps.data.driver.TileDriver;
import net.xy.gps.data.gps.JSR82Connection;
import net.xy.gps.data.gps.NMEAHandler;
import net.xy.gps.data.gps.RxTxConnection;
import net.xy.gps.data.tag.TagConfiguration;
import net.xy.gps.data.tag.TagFactory;
import net.xy.gps.render.layer.GPSPosLayer;
import net.xy.gps.render.layer.PriorityDataReceiver;
import net.xy.gps.render.layer.PriorityDataReceiver.CreateLayersCallback;
import net.xy.gps.render.layer.StatLayer;
import net.xy.gps.render.layer.TagWayLayer;
import net.xy.gps.render.layer.ZoomAreaLayer;
import net.xy.gps.render.layer.ZoomNodeLayer;
import net.xy.gps.render.perspective.Action2DView;
import net.xy.gps.render.perspective.MoveUtils;
import net.xy.gps.render.perspective.MoveUtils.IChangeNotifier;
import net.xy.gps.type.Rectangle;

/**
 * main class for java se and swing
 * 
 * @author Xyan
 * 
 */
public class Example {
  private static final long serialVersionUID = -6720509574401297090L;

  /**
   * configuration
   */
  public static final int CONF_DATAPROVIDER_DB = 0; // conf const
  public static final int CONF_DATAPROVIDER_TILES = 1; // conf const
  public static final Config CONF_DATA_PROVIDER = Cfg
      .register("main.dataprovider", Integer.valueOf(CONF_DATAPROVIDER_TILES));
  public static final Config CONF_EVENT_ACCEL = Cfg.register("main.event.move.acceleration", Double.valueOf(1.1d));
  public static final Config CONF_EVENT_ACCEL_LIMIT = Cfg.register("main.event.move.acceleration.limt", Double.valueOf(18d));
  public static final Config CONF_EVENT_LOOPGAP = Cfg.register("main.event.loopgap", Integer.valueOf(40));
  /**
   * messages
   */
  private static final Config TEXT_INIT_DATAPROVIDER = Cfg.register("main.init.provider", "Initializing data provider");
  private static final Config TEXT_INIT_EVENT_LISTENERS = Cfg.register("main.init.events",
      "Initializing event listeners and adapters");
  private static final Config TEXT_INIT_CONFIGURATION = Cfg.register("main.init.conf",
      "Initializing application configuration");
  private static final Config TEXT_INIT_DATATHREAD = Cfg
      .register("main.init.thread.data", "Starting data collection thread");
  private static final Config TEXT_INIT_PAINTTHREAD = Cfg.register("main.init.thread.paint",
      "Starting painting and redering thread");
  private static final Config TEXT_ERROR_THREAD_CRASH = Cfg
      .register("main.error.thread.crashed", "An needed thread crashed");

  /**
   * mainframe
   */
  private static final JFrame MAIN = new JFrame("XY.Gomps");
  private static final JPanel CONTENT = new JPanel();
  static {
    MAIN.setSize(200, 150);
    MAIN.setBackground(Color.BLACK);
    MAIN.setLayout(new BorderLayout());
    MAIN.getContentPane().add(CONTENT);
    CONTENT.setBackground(Color.GRAY);
  }
  // Threads to control
  public static Thread db;
  public static Thread paint;

  /**
   * main infrastructure stacking
   */
  // 1. init an perspective
  private final Action2DView perspective = new Action2DView( //
      MAIN.getWidth(), MAIN.getHeight());
  // convience adapter for the perspective
  private final MoveUtils canvasAdapter = new MoveUtils(perspective, new IChangeNotifier() {
    public void notified() {
      ThreadLocal.set(Boolean.TRUE, db);
      ThreadLocal.set(Boolean.TRUE, paint);
    }
  });
  // 2. get ouput module
  final AwtListener listener = new AwtListener(perspective, CONTENT.getGraphics(), CONTENT.getGraphicsConfiguration());
  // 3. init data bridge for perspective
  private final PriorityDataReceiver dataReceiver = new PriorityDataReceiver(perspective, new CreateLayersCallback() {
    public Collection createLayerSet(final int prio) {
      // layer feature initialization
      final List res = new ArrayList();
      res.add(new ZoomAreaLayer(perspective));
      res.add(new TagWayLayer(perspective));
      res.add(new ZoomNodeLayer(perspective));
      return res;
    }
  });
  // 4. create an data provider an connect it to the receiver
  private IDataProvider data = null;

  /**
   * move indicators
   */
  private double moveX = 0;
  private double moveY = 0;
  private double moveZ = 0;
  private double[] dragStart = null;

  public Example() {
    addDataProvider();
    listener.addLayer(new StatLayer(perspective)); // add stats
    listener.addLayer(new GPSPosLayer(perspective)); // add stats
    MAIN.addWindowListener(new WindowAdapter() {
      public void windowClosing(final WindowEvent e) {
        System.exit(0);
      }
    });
    canvasAdapter.size(CONTENT.getWidth(), CONTENT.getHeight());
    MAIN.addComponentListener(new ComponentAdapter() {
      public void componentResized(final ComponentEvent e) {
        listener.setDisplayBuffer(CONTENT.getGraphics());
        canvasAdapter.size(CONTENT.getWidth(), CONTENT.getHeight());
      }
    });
    MAIN.addMouseListener(new MouseAdapter() {
      public void mousePressed(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          dragStart = new double[] { e.getX(), e.getY(), e.getX(), e.getY() };
        }
      }

      public void mouseReleased(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && dragStart != null) {
          canvasAdapter.moveBy((int) (dragStart[2] - e.getX()), (int) (dragStart[3] - e.getY()) * -1);
          dragStart = null;
        }
      }
    });
    MAIN.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(final MouseWheelEvent e) {
        canvasAdapter.move(0, 0, e.getWheelRotation() * -1);
      }
    });
    MAIN.addMouseMotionListener(new MouseMotionAdapter() {
      private long lastMove = System.currentTimeMillis();

      public void mouseDragged(final MouseEvent e) {
        if (dragStart != null && System.currentTimeMillis() > lastMove) {
          listener.moveBuffer((int) (dragStart[2] - e.getX()), (int) (dragStart[3] - e.getY()) * -1);
          dragStart = new double[] { e.getX(), e.getY(), dragStart[2], dragStart[3] };
          lastMove = System.currentTimeMillis() + Cfg.integer(CONF_EVENT_LOOPGAP).intValue();
        }
      }

      public void mouseMoved(final MouseEvent e) {
        MAIN.setTitle(perspective.getLat(e.getY()) + " / " + perspective.getLon(e.getX()));
      }
    });
    MAIN.addKeyListener(new KeyListener() {
      private long lastMove = System.currentTimeMillis();

      public void keyTyped(final KeyEvent e) {
      }

      public void keyReleased(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
          System.exit(0);
          break;
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_RIGHT:
          moveX = 0;
          break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
          moveY = 0;
          break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_ADD:
        case KeyEvent.VK_MINUS:
        case KeyEvent.VK_SUBTRACT:
          moveZ = 0;
          break;
        }
      }

      public void keyPressed(final KeyEvent e) {
        if (System.currentTimeMillis() < lastMove) {
          return;
        }
        final double acceleration = Cfg.doublet(CONF_EVENT_ACCEL).doubleValue();
        final double limit = Cfg.doublet(CONF_EVENT_ACCEL_LIMIT).doubleValue();
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
          if (moveX == 0) {
            moveX = -1;
          } else if (Math.abs(moveX) < limit) {
            moveX = moveX * acceleration;
          } else {
            moveX = limit * -1;
          }
          break;
        case KeyEvent.VK_RIGHT:
          if (moveX == 0) {
            moveX = 1;
          } else if (moveX < limit) {
            moveX = moveX * acceleration;
          } else {
            moveX = limit;
          }
          break;
        case KeyEvent.VK_UP:
          if (moveY == 0) {
            moveY = 1;
          } else if (Math.abs(moveY) < limit) {
            moveY = moveY * acceleration;
          } else {
            moveY = limit * 1;
          }
          break;
        case KeyEvent.VK_DOWN:
          if (moveY == 0) {
            moveY = -1;
          } else if (moveY < limit) {
            moveY = moveY * acceleration;
          } else {
            moveX = limit * -1;
          }
          break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_ADD:
          moveZ = 1;
          break;
        case KeyEvent.VK_MINUS:
        case KeyEvent.VK_SUBTRACT:
          moveZ = -1;
          break;
        }
        lastMove = System.currentTimeMillis() + Cfg.integer(CONF_EVENT_LOOPGAP).intValue();
      }
    });
    Log.comment(TEXT_INIT_EVENT_LISTENERS);
  }

  /**
   * reinits or adds an dataprovider
   */
  public void addDataProvider() {
    if (Cfg.integer(CONF_DATA_PROVIDER).intValue() == CONF_DATAPROVIDER_DB) {
      try {
        data = new HSQLDriver();
      } catch (final SQLException e1) {
        e1.printStackTrace();
        System.exit(1);
      }
    } else {
      data = new TileDriver();
      data.setListener(listener);
    }
    Log.comment(TEXT_INIT_DATAPROVIDER, new Object[] { data });
  }

  public static void main(final String[] args) {
    // configuration setup
    Cfg.addDefaultRetrievers(args);
    try {
      Cfg.addRetriever(new TextPropertyRetriever("net/xy/gps/render/priorities.properties"));
      Cfg.addRetriever(new TextPropertyRetriever("net/xy/gps/messages.properties"));
      loadTagConfig();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    Log.comment(TEXT_INIT_CONFIGURATION);
    MAIN.setJMenuBar(new ConfigMenuBar());
    MAIN.setVisible(true);

    final Example main = new Example();

    // init data collector thread
    db = new Thread(new Runnable() {
      private Rectangle oldRect = null;

      public void run() {
        while (true) {
          ThreadLocal.set(Boolean.FALSE);
          final Rectangle actual = main.perspective.getViewPort();
          if (oldRect == null || !oldRect.equals(actual)) {
            oldRect = actual;
            // main.dataReceiver.clearLayers(); ineffective
            main.data.get(main.perspective.getViewPort(), main.dataReceiver);
          }
          Utils.sleep(Cfg.integer(CONF_EVENT_LOOPGAP).intValue() * 5);
        }
      }
    }, "DataCollector");
    db.setDaemon(true);
    db.setPriority(Thread.MIN_PRIORITY);
    db.start();
    Log.comment(TEXT_INIT_DATATHREAD);

    // init painting thread
    paint = new Thread(new Runnable() {
      public void run() {
        while (true) {
          ThreadLocal.set(Boolean.FALSE);
          if (!main.perspective.isValid()) {
            main.perspective.update(); // call for update
          }
          if (!((Boolean) ThreadLocal.get()).booleanValue()) {
            Utils.sleep(Cfg.integer(CONF_EVENT_LOOPGAP).intValue());
          }
        }
      }
    }, "PaintThread");
    paint.setDaemon(true);
    paint.setPriority(Thread.MIN_PRIORITY);
    paint.start();
    Log.comment(TEXT_INIT_PAINTTHREAD);

    // init painting thread
    final Thread navigation = new Thread(new Runnable() {
      public void run() {
        while (true) {
          try {
            if (false) {
              JSR82Connection.init();
              final Object[] services = JSR82Connection.discoverer.services.get();
              for (int i = 0; i < services.length; i++) {
                final ServiceRecord rec = (ServiceRecord) services[i];
                JSR82Connection.connect(rec, new NMEAHandler());
                break;
              }
            } else {
              RxTxConnection.init();
              for (final Iterator i = RxTxConnection.ports.iterator(); i.hasNext();) {
                final CommPortIdentifier port = (CommPortIdentifier) i.next();
                if (port.getName().equals("COM4")) {
                  RxTxConnection.connect(port, new NMEAHandler());
                  break;
                } else {
                  System.out.println("Skip port " + port.getName());
                }
              }
            }
          } catch (final Throwable e) {
            e.printStackTrace();
          }
          Utils.sleep(500);
        }
      }
    }, "NavigationThread");
    navigation.setDaemon(true);
    navigation.setPriority(Thread.MIN_PRIORITY);
    navigation.start();

    while (true) {
      if (!db.isAlive() || !paint.isAlive()) {
        Log.fattal(TEXT_ERROR_THREAD_CRASH);
        System.exit(1);
      }
      if (main.moveX != 0 || main.moveY != 0 || main.moveZ != 0) {
        if (main.moveZ != 0) {
          // at the moment to inperformant
          // main.listener.zoomBuffer(main.moveZ);
        }
        main.canvasAdapter.move(main.moveX, main.moveY, main.moveZ);
        MAIN.setTitle(main.perspective.getViewPort().toString());
      }
      Utils.sleep(Cfg.integer(CONF_EVENT_LOOPGAP).intValue());
    }
  }

  /**
   * loads or reloads the tag style configuration
   */
  public static void loadTagConfig() {
    try {
      TagFactory.flush();
      new TagConfiguration(Cfg.string(TagConfiguration.CONF_TAG_CONF), new TagFactory());
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
