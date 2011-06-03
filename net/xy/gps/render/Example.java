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
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import net.xy.codebase.Utils;
import net.xy.gps.data.HSQLDriver;
import net.xy.gps.data.IDataObject;
import net.xy.gps.render.IDataProvider.Iterator;
import net.xy.gps.render.perspective.Action2DViewAwt;

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
    private final Action2DViewAwt perspective = new Action2DViewAwt(MAIN.getWidth(), MAIN.getHeight());
    // maybe an db or file
    private final IDataProvider data;
    /**
     * layers should store and cache as much as possible
     */
    private final SimpleLayer[] layers = new SimpleLayer[] { new SimpleLayer() };

    /**
     * move indicators
     */
    private double moveX = 0;
    private double moveY = 0;
    private final double acceleration = 1.1;
    private final double limit = 6;
    // percentage stepping of viewport
    private static final double move = 0.01;
    private double[] dragStart = null;
    private boolean shouldRepaint = false;

    public Example() {
        try {
            data = new HSQLDriver();
        } catch (final SQLException e1) {
            throw new RuntimeException(e1);
        }
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
                    dragStart = new double[] { e.getXOnScreen(), e.getYOnScreen(), perspective.getViewPort().upperleft.lat,
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
        // main.perspective.paintTo(MAIN.getGraphics());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    main.updateObjects();
                    Utils.sleep(1000);
                }
            }
        }, "DataCollector").start();
        final GraphicsConfiguration gfxCfg = MAIN.getGraphicsConfiguration();
        while (true) {
            if (main.moveX != 0 || main.moveY != 0) {
                main.moveView(main.moveX, main.moveY, 0);
            }
            if (main.shouldRepaint) {
                main.shouldRepaint = false;
                final BufferedImage backBuffer = gfxCfg.createCompatibleImage(MAIN.getWidth(), MAIN.getHeight());
                main.perspective.paintTo(backBuffer.getGraphics());
                MAIN.getGraphics().drawImage(backBuffer, 0, 0, null);
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
            perspective.setViewPort(perspective.getViewPort().upperleft.lat + byX, perspective.getViewPort().upperleft.lon);
        }
        if (y > 0 || y < 0) {
            final double byY = perspective.getViewPort().dimension.width * move * y;
            perspective.setViewPort(perspective.getViewPort().upperleft.lat, perspective.getViewPort().upperleft.lon + byY);
        }
        final double byW = perspective.getViewPort().dimension.width * 0.3;
        final double byH = perspective.getViewPort().dimension.height * 0.3;
        if (z > 0) {
            final double newLat = perspective.getViewPort().upperleft.lat + byW / 2;
            final double newLon = perspective.getViewPort().upperleft.lon + byH / 2;
            perspective.setViewPort(newLat, newLon, perspective.getViewPort().dimension.width - byW,
                    perspective.getViewPort().dimension.height - byH);
        } else if (z < 0) {
            final double newLat = perspective.getViewPort().upperleft.lat - byW / 2;
            final double newLon = perspective.getViewPort().upperleft.lon - byH / 2;
            perspective.setViewPort(newLat, newLon, perspective.getViewPort().dimension.width + byW,
                    perspective.getViewPort().dimension.height + byH);
        }
        perspective.setSize(MAIN.getWidth(), MAIN.getHeight());
        shouldRepaint = true;
    }

    private void updateObjects() {
        final Iterator objects = data.get(perspective.getViewPort());
        layers[0].clear();
        while (objects.hasNext()) {
            final IDataObject obj = objects.next();
            // do sorting and insertion into layers
            layers[0].addObject(obj);
        }
        shouldRepaint = true;
    }
}