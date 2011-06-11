package net.xy.gps.client.awt;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamException;

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
import net.xy.gps.render.layer.ZoomAreaLayer;
import net.xy.gps.render.layer.ZoomNodeLayer;
import net.xy.gps.render.layer.ZoomWayLayer;
import net.xy.gps.render.perspective.Action2DView;
import net.xy.gps.type.Rectangle;

public class Example {
    private static final long serialVersionUID = -6720509574401297090L;

    /**
     * configuration
     */
    public static final int CONF_DATAPROVIDER_DB = 0; // conf const
    public static final int CONF_DATAPROVIDER_TILES = 1; // conf const
    private static final ConfigKey CONF_TEXT_LAYERS_CLEARED = Config.registerValues(
            "main.state.layer.cleared", "Data layers cleared");
    protected static final ConfigKey CONF_TEXT_IN_DATA = Config.registerValues(
            "main.state.in.data", "Accepting new data from provider");
    protected static final ConfigKey CONF_DATA_PROVIDER = Config.registerValues(
            "main.dataprovider", Integer.valueOf(CONF_DATAPROVIDER_DB));

    private static final Frame MAIN = new Frame("XY.GpsMid");
    private static final Color BGND = new Color(0, 0, 0);
    static {
        MAIN.setSize(400, 300);
        MAIN.setBackground(BGND);
    }
    /**
     * initialized via config
     */
    // 2d or 3d
    private final Action2DView perspective = new Action2DView(MAIN.getWidth(), MAIN.getHeight());
    // maybe an db or file
    private IDataProvider data = null;
    /**
     * layers should store and cache as much as possible
     */
    private PriorityDataReceiver dataReceiver = null;
    private static final Example main = new Example();

    /**
     * move indicators
     */
    private double moveX = 0;
    private double moveY = 0;
    private final double acceleration = 1.1;
    private final double limit = 18;
    // percentage stepping of viewport
    private static final double move = 0.2;
    private static final double zoom = 0.2;
    private double[] dragStart = null;
    private boolean shouldRepaint = false;
    private static Thread paint;
    private static Thread db;

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
        }
        MAIN.addComponentListener(new ComponentAdapter() {
            public void componentResized(final ComponentEvent e) {
                perspective.setSize(MAIN.getWidth(), MAIN.getHeight());
                shouldRepaint = true;
                ThreadLocal.set(Boolean.TRUE, paint);
                ThreadLocal.set(Boolean.TRUE, db);
            }
        });
        MAIN.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
        MAIN.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dragStart = new double[] { e.getX(), e.getY(),
                            perspective.getViewPort().origin.lat,
                            perspective.getViewPort().origin.lon };
                }
            }

            public void mouseReleased(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dragStart = null;
                }
            }

            public void mouseWheelMoved(final MouseWheelEvent e) {
                moveView(0, 0, e.getWheelRotation() * -1);
            }
        });
        MAIN.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(final MouseWheelEvent e) {
                moveView(0, 0, e.getWheelRotation() * -1);
            }
        });
        MAIN.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(final MouseEvent e) {
                if (dragStart != null) {
                    final int disX = (int) (dragStart[0] - e.getX());
                    final int disY = (int) (dragStart[1] - e.getY()) * -1;
                    // if (disX > 20 || disY > 20) {
                    final double moveLat = disY * perspective.getPixelSize().height;
                    final double moveLon = disX * perspective.getPixelSize().width;
                    perspective.setViewPort(dragStart[2] + moveLat, dragStart[3] + moveLon);
                    shouldRepaint = true;
                    ThreadLocal.set(Boolean.TRUE, paint);
                    // }
                }
            }

            public void mouseMoved(final MouseEvent e) {
                MAIN.setTitle(main.perspective.getLat(e.getY()) + " / "
                        + main.perspective.getLon(e.getX()));
            }
        });
        MAIN.addKeyListener(new KeyListener() {
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
                }
            }

            public void keyPressed(final KeyEvent e) {
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
                    moveView(0, 0, 1);
                    break;
                case KeyEvent.VK_MINUS:
                    moveView(0, 0, -1);
                    break;
                }
                MAIN.setTitle(main.perspective.getViewPort().toString());
            }
        });
    }

    public static void main(final String[] args) {
        Config.addDefaultRetrievers(args);
        try {
            Config.addRetriever(new TextPropertyRetriever("net/xy/gps/render/priorities.properties"));
            new TagConfiguration("net/xy/gps/data/tag/tags.conf.xml", new TagFactory());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
        MAIN.setVisible(true);
        final Graphics g = MAIN.getGraphics();
        if (g instanceof Graphics2D) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        final AwtListener listener = new AwtListener(g, main.perspective);
        main.perspective.setListener(listener);
        main.dataReceiver = new PriorityDataReceiver(main.perspective, new CreateLayersCallback() {
            public Collection createLayerSet() {
                final List res = new ArrayList();
                res.add(new ZoomAreaLayer(main.perspective));
                res.add(new ZoomWayLayer(main.perspective));
                res.add(new ZoomNodeLayer(main.perspective));
                return res;
            }
        });
        final GraphicsConfiguration gfxCfg = MAIN.getGraphicsConfiguration();
        db = new Thread(new Runnable() {
            private Rectangle oldRect = null;

            public void run() {
                while (true) {
                    ThreadLocal.set(Boolean.FALSE);
                    final Rectangle actual = main.perspective.getViewPort();
                    if (oldRect == null || !oldRect.equals(actual)) {
                        oldRect = actual;
                        Log.notice(CONF_TEXT_LAYERS_CLEARED);
                        main.data.get(main.perspective.getViewPort(), main.dataReceiver);
                        main.shouldRepaint = true;
                        ThreadLocal.set(Boolean.TRUE, paint);
                    }
                    if (!((Boolean) ThreadLocal.get()).booleanValue()) {
                        Utils.sleep(200);
                    }
                }
            }
        }, "DataCollector");
        db.setDaemon(true);
        db.setPriority(Thread.MIN_PRIORITY);
        db.start();
        paint = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    ThreadLocal.set(Boolean.FALSE);
                    if (main.shouldRepaint) {
                        main.shouldRepaint = false;
                        if (false) {
                            final BufferedImage buffer = gfxCfg.createCompatibleImage(
                                    MAIN.getWidth(), MAIN.getHeight());
                            listener.update(buffer.getGraphics());
                            MAIN.getGraphics().drawImage(buffer, 0, 0, null);
                        } else {
                            listener.update(MAIN.getGraphics());
                        }
                    }
                    if (!((Boolean) ThreadLocal.get()).booleanValue()) {
                        Utils.sleep(40);
                    }
                }
            }
        }, "PaintThread");
        // TODO [LAST] implement way width in meters and border handling
        paint.setDaemon(true);
        paint.setPriority(Thread.MIN_PRIORITY);
        paint.start();
        main.shouldRepaint = true;
        while (true) {
            if (!db.isAlive() || !paint.isAlive()) {
                System.exit(1);
            }
            if (main.moveX != 0 || main.moveY != 0) {
                main.moveView(main.moveX, main.moveY, 0);
            }
            Utils.sleep(40);
        }
    }

    /**
     * does discrete movement
     * 
     * @param x
     * @param y
     */
    private void moveView(final double x, final double y, final int z) {
        if (x > 0 || x < 0) {
            final double byX = perspective.getViewPort().dimension.width * move * x;
            perspective.setViewPort(perspective.getViewPort().origin.lat,
                    perspective.getViewPort().origin.lon + byX);
        }
        if (y > 0 || y < 0) {
            final double byY = perspective.getViewPort().dimension.height * move * y;
            perspective.setViewPort(perspective.getViewPort().origin.lat + byY,
                    perspective.getViewPort().origin.lon);
        }
        final double byW = perspective.getViewPort().dimension.width * zoom;
        final double byH = perspective.getViewPort().dimension.height * zoom;
        if (z > 0) {
            final double newLat = perspective.getViewPort().origin.lat + byW / 2;
            final double newLon = perspective.getViewPort().origin.lon + byH / 2;
            perspective.setViewPort(newLat, newLon,
                    perspective.getViewPort().dimension.width - byW,
                    perspective.getViewPort().dimension.height - byH);
        } else if (z < 0) {
            final double newLat = perspective.getViewPort().origin.lat - byW / 2;
            final double newLon = perspective.getViewPort().origin.lon - byH / 2;
            perspective.setViewPort(newLat, newLon,
                    perspective.getViewPort().dimension.width + byW,
                    perspective.getViewPort().dimension.height + byH);
        }
        perspective.setSize(MAIN.getWidth(), MAIN.getHeight());
        shouldRepaint = true;
        ThreadLocal.set(Boolean.TRUE, paint);
        ThreadLocal.set(Boolean.TRUE, db);
    }
}
