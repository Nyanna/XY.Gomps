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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.xy.codebasel.Log;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.gps.render.ICanvas;
import net.xy.gps.render.IDrawAction;
import net.xy.gps.render.ILayer;
import net.xy.gps.render.draw.DrawArea;
import net.xy.gps.render.draw.DrawGrid;
import net.xy.gps.render.draw.DrawGrid.Path;
import net.xy.gps.render.draw.DrawPoint;
import net.xy.gps.render.draw.DrawPoly;
import net.xy.gps.render.draw.DrawText;
import net.xy.gps.render.perspective.ActionListener;
import net.xy.gps.type.Dimension;

/**
 * perspective bound to AWT
 * 
 * @author Xyan
 * 
 */
public class AwtListener implements ActionListener {
    /**
     * message configuration
     */
    private static final ConfigKey CONF_TEXT_ACTION_BUFFERZOOM = Config.registerValues("renderer.action.bufferzoom",
            "Bufferzoom was rendered");
    private static final ConfigKey CONF_TEXT_UPDATE_ABBORTED = Config.registerValues("renderer.action.update.prepare",
            "Update is obsolete abborting");
    private static final ConfigKey CONF_TEXT_UPDATE_END = Config.registerValues("renderer.action.update.end",
            "Complete update ended succesfull");
    private static final ConfigKey CONF_TEXT_UPDATE_START = Config.registerValues("renderer.action.update.abborted",
            "Prepare view for an complete update");
    /**
     * holds graphics references
     */
    private final GraphicsConfiguration gfxCfg;
    private final ICanvas canvas;
    private final Graphics displayBuffer;
    private Graphics drawBuffer;
    private VolatileImage drawBufferImage = null;
    // for move and repaint areas
    private Image backBufferImage = null;

    /**
     * settings
     */
    private final double overscann = 1; // relative
    private int overWidth = 0;
    private int overHeight = 0;

    /**
     * additional drawing operation to add to the display
     */
    private final List addLayers = new ArrayList();

    /**
     * default constructor
     * 
     * @param graphics
     * @param graphicsConfiguration
     */
    public AwtListener(final ICanvas canvas, final Graphics graphics, final GraphicsConfiguration gfxCfg) {
        this.canvas = canvas;
        this.gfxCfg = gfxCfg;
        setHints(graphics);
        drawBuffer = displayBuffer = graphics;
        canvas.setListener(this); // registers on canvas
    }

    /**
     * sets configured graphic hints
     * 
     * @param graphics
     */
    private Graphics setHints(final Graphics graphics) {
        if (graphics instanceof Graphics2D) {
            ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return graphics;
    }

    /**
     * adds additional stat layers to the display
     * 
     * @param layer
     */
    public void addLayer(final ILayer layer) {
        layer.setListener(this);
        addLayers.add(layer);
    }

    /**
     * internally querys all layers to update
     */
    private void callLayers() {
        for (final Iterator iterator = addLayers.iterator(); iterator.hasNext();) {
            final ILayer layer = (ILayer) iterator.next();
            layer.update();
        }
    }

    /**
     * paints to awt graphics
     * 
     * @param
     */
    public void updateStart() {
        Log.comment(CONF_TEXT_UPDATE_START);
        final Dimension size = canvas.getSize();
        synchronized (this) {
            overWidth = (int) (size.width * overscann);
            overHeight = (int) (size.height * overscann);
            drawBufferImage = gfxCfg.createCompatibleVolatileImage(//
                    (int) size.width + overWidth, (int) size.height + overHeight);
            drawBuffer = setHints(drawBufferImage.getGraphics());
            drawBuffer.setColor(Color.WHITE);
            drawBuffer.fillRect(0, 0, drawBufferImage.getWidth(), drawBufferImage.getHeight());
            drawBuffer.translate(overWidth / 2, overHeight / 2);
        }
    }

    public void updateEnd(final boolean success) {
        if (success) {
            Log.comment(CONF_TEXT_UPDATE_END);
            synchronized (this) {
                backBufferImage = drawBufferImage;
                displayBuffer.drawImage(drawBufferImage, overWidth / 2 * -1, overHeight / 2 * -1, null);
                drawBuffer = displayBuffer;
                callLayers(); // draw to display
            }
        } else {
            Log.comment(CONF_TEXT_UPDATE_ABBORTED);
            drawBuffer = displayBuffer;
        }
    }

    /**
     * just moves the buffer very fast
     * 
     * @param disX
     * @param disY
     */
    public void moveBuffer(final int disX, final int disY) {
        if (backBufferImage != null) {
            synchronized (this) {
                drawBuffer.setColor(Color.WHITE);
                drawBuffer.fillRect(0, 0, (int) canvas.getSize().width, (int) canvas.getSize().height);
                displayBuffer.drawImage(backBufferImage, overWidth / 2 * -1 + disX * -1, overHeight / 2 * -1 + disY, null);
                callLayers();
            }
        }
    }

    /**
     * just zoomes and scales the buffer very fast
     * 
     * @param disX
     * @param disY
     */
    public void zoomBuffer(final double factor) {
        if (backBufferImage != null) {
            Log.comment(CONF_TEXT_ACTION_BUFFERZOOM);
            // TODO [9] remove zoom constant, destroy scaled instance after run
            final double amount = 1 + 0.2 * factor;
            final int newWidth = (int) ((canvas.getSize().width + overWidth) * amount);
            final int newHeight = (int) ((canvas.getSize().height + overHeight) * amount);
            final int diffWidth = (int) (canvas.getSize().width + overWidth - newWidth); // 300
            final int diffHeight = (int) (canvas.getSize().height + overHeight - newHeight);
            if (newWidth > 10 && newHeight > 10) {
                synchronized (this) {
                    final Image scaled = backBufferImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
                    drawBuffer.setColor(Color.WHITE);
                    drawBuffer.fillRect(0, 0, (int) canvas.getSize().width, (int) canvas.getSize().height);
                    displayBuffer.drawImage(scaled, diffWidth / 2 - overWidth / 2, diffHeight / 2 - overHeight / 2, null);
                }
            }
        }
    }

    public void draw(final IDrawAction action) {
        synchronized (this) {
            final Stroke backup = drawBuffer instanceof Graphics2D ? ((Graphics2D) drawBuffer).getStroke() : null;
            switch (action.getType()) {
            case IDrawAction.ACTION_POINT:
                draw((DrawPoint) action);
                break;
            case IDrawAction.ACTION_AREA:
                draw((DrawArea) action);
                break;
            case IDrawAction.ACTION_WAY:
                draw((DrawPoly) action);
                break;
            case IDrawAction.ACTION_WAYGRID:
                draw((DrawGrid) action);
                break;
            case IDrawAction.ACTION_TEXT:
                draw((DrawText) action);
                break;
            }
            if (drawBuffer instanceof Graphics2D && backup != null) {
                ((Graphics2D) drawBuffer).setStroke(backup);
            }
        }
    }

    // TODO [5] gui draw only mode without size adjustions

    /**
     * draws multiple ways as one shape
     * 
     * @param action
     */
    private void draw(final DrawGrid action) {
        if (drawBuffer instanceof Graphics2D) {
            // construct outline
            final Area grid = new Area();
            for (final Iterator i = action.pathes.iterator(); i.hasNext();) {
                final Path way = (Path) i.next();
                final float width = (float) canvas.getWidth(way.width.floatValue());
                final BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                final Path2D path = new Path2D.Double();
                path.moveTo(canvas.getX(way.path[0][1].doubleValue()), canvas.getY(way.path[0][0].doubleValue()));
                for (int c = 1; c < way.path.length; c++) {
                    path.lineTo(canvas.getX(way.path[c][1].doubleValue()), canvas.getY(way.path[c][0].doubleValue()));
                }
                grid.add(new Area(stroke.createStrokedShape(path)));
            }
            // draw
            drawBuffer.setColor(new Color(action.color[0].intValue(), action.color[1].intValue(),
                    action.color[2].intValue(), action.color[3].intValue()));
            ((Graphics2D) drawBuffer).fill(grid);

            // draw border
            drawBuffer.setColor(Color.BLACK);
            ((Graphics2D) drawBuffer).setStroke(new BasicStroke(0.25f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            ((Graphics2D) drawBuffer).draw(grid);
        } else {
            // TODO [9] draw grid support for non j2d2
        }
    }

    /**
     * draws an way or poly action
     * 
     * @param poly
     */
    private void draw(final DrawPoly poly) {
        // TODO [5] draw dashed lines or surounded pathes with borders
        final float width = (float) canvas.getWidth(poly.width.floatValue());
        final Color mainColor = new Color(poly.color[0].intValue(), poly.color[1].intValue(), poly.color[2].intValue(),
                poly.color[3].intValue());

        if (true && drawBuffer instanceof Graphics2D) { // surounded
            // construct outline
            final BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            final Path2D path = new Path2D.Double();
            path.moveTo(canvas.getX(poly.path[0][1].doubleValue()), canvas.getY(poly.path[0][0].doubleValue()));
            for (int i = 1; i < poly.path.length; i++) {
                path.lineTo(canvas.getX(poly.path[i][1].doubleValue()), canvas.getY(poly.path[i][0].doubleValue()));
            }
            final Shape street = stroke.createStrokedShape(path);
            // draw
            drawBuffer.setColor(mainColor);
            ((Graphics2D) drawBuffer).fill(street);
            if (width > 4) {
                drawBuffer.setColor(Color.BLACK);
                ((Graphics2D) drawBuffer).setStroke(new BasicStroke(0.25f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                ((Graphics2D) drawBuffer).draw(street);
            }
        } else {
            if (false && drawBuffer instanceof Graphics2D) { // dashed
                final float[] dash = new float[0];
                ((Graphics2D) drawBuffer).setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1,
                        dash, 0.0f));
            }
            drawBuffer.setColor(mainColor);
            final int[] px = new int[poly.path.length];
            final int[] py = new int[poly.path.length];
            for (int i = 0; i < poly.path.length; i++) {
                px[i] = canvas.getX(poly.path[i][1].doubleValue());
                py[i] = canvas.getY(poly.path[i][0].doubleValue());
            }
            drawBuffer.drawPolyline(px, py, poly.path.length);
        }
    }

    /**
     * draws an text action
     * 
     * @param text
     */
    private void draw(final DrawText text) {
        final int tx = canvas.getX(text.lon);
        final int ty = canvas.getY(text.lat);
        drawBuffer.setColor(new Color(text.color[0].intValue(), text.color[1].intValue(), text.color[2].intValue(),
                text.color[3].intValue()));
        drawBuffer.drawString(text.text, tx, ty);
    }

    /**
     * draws an area action
     * 
     * @param area
     */
    private void draw(final DrawArea area) {
        final int[] nx = new int[area.path.length];
        final int[] ny = new int[area.path.length];
        for (int i = 0; i < area.path.length; i++) {
            nx[i] = canvas.getX(area.path[i][1].doubleValue());
            ny[i] = canvas.getY(area.path[i][0].doubleValue());
        }
        drawBuffer.setColor(new Color(area.color[0].intValue(), area.color[1].intValue(), area.color[2].intValue(),
                area.color[3].intValue()));
        if (area.fill) {
            drawBuffer.fillPolygon(nx, ny, area.path.length);
        } else {
            if (false && drawBuffer instanceof Graphics2D) {
                // TODO [9] dashed area
                // ((Graphics2D) g).setStroke(new
                // BasicStroke(poly.width.floatValue()
                // > 0 ? poly.width
                // .floatValue() : 1, BasicStroke.CAP_SQARE,
                // BasicStroke.JOIN_ROUND,
                // 1, dash, 0.0f));
            }
            drawBuffer.drawPolygon(nx, ny, area.path.length);
        }
    }

    /**
     * draw an point action
     * 
     * @param point
     */
    private void draw(final DrawPoint point) {
        final int x = canvas.getX(point.lon);
        final int y = canvas.getY(point.lat);
        // TODO [9] optimize not drawing shapes beyonds the view, needed ?
        drawBuffer.setColor(new Color(point.color[0].intValue(), point.color[1].intValue(), point.color[2].intValue(),
                point.color[3].intValue()));
        drawBuffer.drawRect(x - 2, y - 2, 4, 4);
    }
}