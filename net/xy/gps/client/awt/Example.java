package net.xy.gps.client.awt;

import java.awt.Color;
import java.awt.Frame;
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
import java.util.List;

import net.xy.codebasel.Log;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.Utils;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.codebasel.config.TextPropertyRetriever;
import net.xy.gps.data.IDataProvider;
import net.xy.gps.data.driver.HSQLDriver;
import net.xy.gps.data.driver.TileDriver;
import net.xy.gps.data.tag.TagConfiguration;
import net.xy.gps.data.tag.TagFactory;
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

public class Example {
    private static final long serialVersionUID = -6720509574401297090L;

    /**
     * configuration
     */
    private static final int CONF_DATAPROVIDER_DB = 0; // conf const
    private static final int CONF_DATAPROVIDER_TILES = 1; // conf const
    private static final ConfigKey CONF_DATA_PROVIDER = Config.registerValues("main.dataprovider",
            Integer.valueOf(CONF_DATAPROVIDER_DB));
    /**
     * messages
     */
    private static final ConfigKey CONF_TEXT_IN_DATA = Config.registerValues("main.state.in.data",
            "Accepting new data from provider");
    private static final ConfigKey CONF_TEXT_INIT_DATAPROVIDER = Config.registerValues("main.init.provider",
            "Initializing data provider");
    private static final ConfigKey CONF_TEXT_INIT_EVENT_LISTENERS = Config.registerValues("main.init.events",
            "Initializing event listeners and adapters");
    private static final ConfigKey CONF_TEXT_INIT_CONFIGURATION = Config.registerValues("main.init.conf",
            "Initializing application configuration");
    private static final ConfigKey CONF_TEXT_INIT_DATATHREAD = Config.registerValues("main.init.thread.data",
            "Starting data collection thread");
    private static final ConfigKey CONF_TEXT_INIT_PAINTTHREAD = Config.registerValues("main.init.thread.paint",
            "Starting painting and redering thread");
    private static final ConfigKey CONF_TEXT_ERROR_THREAD_CRASH = Config.registerValues("main.error.thread.crashed",
            "An needed thread crashed");

    /**
     * mainframe
     */
    private static final Frame MAIN = new Frame("XY.GpsMid");
    static {
        MAIN.setSize(400, 300);
        MAIN.setBackground(Color.BLACK);
        MAIN.setVisible(true);
    }
    // Threads to control
    private static Thread db;
    private static Thread paint;

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
    final AwtListener listener = new AwtListener(perspective, MAIN.getGraphics(), MAIN.getGraphicsConfiguration());
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
    private final double acceleration = 1.1;
    private final double limit = 18;
    private double[] dragStart = null;

    public Example() {
        if (Config.getInteger(CONF_DATA_PROVIDER).intValue() == CONF_DATAPROVIDER_TILES) {
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
        listener.addLayer(new StatLayer(perspective)); // add stats
        Log.comment(CONF_TEXT_INIT_DATAPROVIDER, new Object[] { data });
        MAIN.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
        MAIN.addComponentListener(new ComponentAdapter() {
            public void componentResized(final ComponentEvent e) {
                canvasAdapter.size(MAIN.getWidth(), MAIN.getHeight());
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
                    lastMove = System.currentTimeMillis() + 40;
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
                switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (moveX == 0) {
                        moveX = -1;
                    } else if (Math.abs(moveX) < limit) {
                        moveX = moveX * acceleration;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (moveX == 0) {
                        moveX = 1;
                    } else if (moveX < limit) {
                        moveX = moveX * acceleration;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (moveY == 0) {
                        moveY = 1;
                    } else if (Math.abs(moveY) < limit) {
                        moveY = moveY * acceleration;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (moveY == 0) {
                        moveY = -1;
                    } else if (moveY < limit) {
                        moveY = moveY * acceleration;
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
                lastMove = System.currentTimeMillis() + 40;
            }
        });
        Log.comment(CONF_TEXT_INIT_EVENT_LISTENERS);
    }

    public static void main(final String[] args) {
        // configuration setup
        Config.addDefaultRetrievers(args);
        try {
            Config.addRetriever(new TextPropertyRetriever("net/xy/gps/render/priorities.properties"));
            new TagConfiguration("net/xy/gps/data/tag/tags.conf.xml", new TagFactory());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        Log.comment(CONF_TEXT_INIT_CONFIGURATION);

        final Example main = new Example();

        // init data collector thread
        db = new Thread(new Runnable() {
            private Rectangle oldRect = null;

            public void run() {
                while (true) {
                    ThreadLocal.set(Boolean.FALSE);
                    final Rectangle actual = main.perspective.getViewPort();
                    // TODO has changes trigger abort only be more than 20%
                    if (oldRect == null || !oldRect.equals(actual)) {
                        oldRect = actual;
                        main.dataReceiver.clearLayers();// TODO wrong place,
                                                        // dont remove data from
                                                        // actual view
                        main.data.get(main.perspective.getViewPort(), main.dataReceiver);
                    }
                    Utils.sleep(200);
                }
            }
        }, "DataCollector");
        db.setDaemon(true);
        db.setPriority(Thread.MIN_PRIORITY);
        db.start();
        Log.comment(CONF_TEXT_INIT_DATATHREAD);

        // init painting thread
        paint = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    ThreadLocal.set(Boolean.FALSE);
                    if (!main.perspective.isValid()) {
                        main.perspective.update(); // call for update
                    }
                    if (!((Boolean) ThreadLocal.get()).booleanValue()) {
                        Utils.sleep(40);
                    }
                }
            }
        }, "PaintThread");
        paint.setDaemon(true);
        paint.setPriority(Thread.MIN_PRIORITY);
        paint.start();
        Log.comment(CONF_TEXT_INIT_PAINTTHREAD);

        while (true) {
            if (!db.isAlive() || !paint.isAlive()) {
                Log.fattal(CONF_TEXT_ERROR_THREAD_CRASH);
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
            Utils.sleep(40);
        }
    }
}