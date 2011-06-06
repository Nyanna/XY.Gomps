package net.xy.gps.render;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.xy.codebasel.Debug;
import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.Utils;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.TileDriver;
import net.xy.gps.render.IDataProvider.IDataReceiver;
import net.xy.gps.render.perspective.Action2DView;
import net.xy.gps.render.perspective.AwtListener;
import net.xy.gps.type.Rectangle;

public class Example {
    private static final long serialVersionUID = -6720509574401297090L;
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
    private final IDataProvider data = new TileDriver();
    /**
     * layers should store and cache as much as possible
     */
    private final ILayer wayLayer = new ZoomWayLayer(perspective);
    private final ILayer nodeLayer = new ZoomNodeLayer(perspective);
    private final ILayer[] layers = new ILayer[] { new StatLayer(perspective), nodeLayer, wayLayer };

    /**
     * move indicators
     */
    private double moveX = 0;
    private double moveY = 0;
    private final double acceleration = 1.1;
    private final double limit = 18;
    // percentage stepping of viewport
    private static final double move = 0.05;
    private double[] dragStart = null;
    private boolean shouldRepaint = false;
    private static Thread paint;
    private static Thread db;

    public Example() {
        for (final ILayer layer : layers) {
            perspective.addLayer(layer);
        }
        MAIN.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
        MAIN.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dragStart = new double[] { e.getXOnScreen(), e.getYOnScreen(),
                            perspective.getViewPort().upperleft.lat,
                            perspective.getViewPort().upperleft.lon };
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dragStart = null;
                }
            }

            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                moveView(0, 0, e.getWheelRotation());
            }
        });
        MAIN.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                if (dragStart != null) {
                    final int disX = (int) (dragStart[0] - e.getXOnScreen());
                    final int disY = (int) (dragStart[1] - e.getYOnScreen());
                    // if (disX > 20 || disY > 20) {
                    final double moveLat = disX * perspective.getPixelSize().width;
                    final double moveLon = disY * perspective.getPixelSize().height;
                    perspective.setViewPort(dragStart[2] + moveLat, dragStart[3] + moveLon);
                    shouldRepaint = true;
                    ThreadLocal.set(true, paint);
                    // }
                }
            }
        });
        MAIN.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(final KeyEvent e) {
            }

            @Override
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

            @Override
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
                        moveY = -1;
                    } else if (Math.abs(moveY) < limit) {
                        moveY = moveY * acceleration;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (moveY == 0) {
                        moveY = 1;
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
            }
        });
    }

    public static void main(final String[] args) {
        final Example main = new Example();
        MAIN.setVisible(true);
        final GraphicsConfiguration gfxCfg = MAIN.getGraphicsConfiguration();
        db = new Thread(new Runnable() {
            private Rectangle oldRect = null;

            @Override
            public void run() {
                while (true) {
                    ThreadLocal.set(false);
                    final Rectangle actual = main.perspective.getViewPort();
                    if (oldRect == null || !oldRect.equals(actual)) {
                        oldRect = actual;
                        main.updateObjects();
                    }
                    if (!(Boolean) ThreadLocal.get()) {
                        Utils.sleep(1000);
                    }
                }
            }
        }, "DataCollector");
        db.setPriority(Thread.MIN_PRIORITY);
        db.start();
        final AwtListener listener = new AwtListener(MAIN.getGraphics(), main.perspective);
        main.perspective.setListener(listener);
        paint = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ThreadLocal.set(false);
                    if (main.shouldRepaint) {
                        main.shouldRepaint = false;
                        listener.update();
                        MAIN.setTitle(main.perspective.getViewPort().toString());
                    }
                    if (!(Boolean) ThreadLocal.get()) {
                        Utils.sleep(40);
                    }
                }
            }
        }, "PaintThread");
        paint.setPriority(Thread.MIN_PRIORITY);
        paint.start();
        main.shouldRepaint = true;
        while (true) {
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
            final double byX = perspective.getViewPort().dimension.height * move * x;
            perspective.setViewPort(perspective.getViewPort().upperleft.lat + byX,
                    perspective.getViewPort().upperleft.lon);
        }
        if (y > 0 || y < 0) {
            final double byY = perspective.getViewPort().dimension.width * move * y;
            perspective.setViewPort(perspective.getViewPort().upperleft.lat,
                    perspective.getViewPort().upperleft.lon + byY);
        }
        final double byW = perspective.getViewPort().dimension.width * 0.3;
        final double byH = perspective.getViewPort().dimension.height * 0.3;
        if (z > 0) {
            final double newLat = perspective.getViewPort().upperleft.lat + byW / 2;
            final double newLon = perspective.getViewPort().upperleft.lon + byH / 2;
            perspective.setViewPort(newLat, newLon,
                    perspective.getViewPort().dimension.width - byW,
                    perspective.getViewPort().dimension.height - byH);
        } else if (z < 0) {
            final double newLat = perspective.getViewPort().upperleft.lat - byW / 2;
            final double newLon = perspective.getViewPort().upperleft.lon - byH / 2;
            perspective.setViewPort(newLat, newLon,
                    perspective.getViewPort().dimension.width + byW,
                    perspective.getViewPort().dimension.height + byH);
        }
        perspective.setSize(MAIN.getWidth(), MAIN.getHeight());
        shouldRepaint = true;
        ThreadLocal.set(true, paint);
        ThreadLocal.set(true, db);
    }

    private void updateObjects() {
        nodeLayer.clear();
        // wayLayer.clear();
        System.out.println("Data layers cleared");
        data.get(perspective.getViewPort(), new IDataReceiver() {
            @Override
            public void accept(final Object[] data) {
                System.out.println(Debug.values("Accepting new data from provider",
                        new Object[] { data.length }));
                for (final Object dat : data) {
                    // do sorting and insertion into layers
                    nodeLayer.addObject((IDataObject) dat);
                    wayLayer.addObject((IDataObject) dat);
                }
            }
        });
    }
}