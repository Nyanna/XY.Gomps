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
package net.xy.gps.client.awt.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.xy.codebasel.ThreadLocal;
import net.xy.codebasel.TypeConverter;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
import net.xy.gps.client.awt.AwtListener;
import net.xy.gps.client.awt.Example;
import net.xy.gps.data.converter.DBConverter;
import net.xy.gps.data.driver.HSQLDriver;
import net.xy.gps.data.driver.TileDriver;
import net.xy.gps.data.tag.TagConfiguration;
import net.xy.gps.render.layer.TagWayLayer;
import net.xy.gps.render.layer.ZoomAreaLayer;
import net.xy.gps.render.layer.ZoomNodeLayer;
import net.xy.gps.render.layer.ZoomWayLayer;
import net.xy.gps.render.perspective.MoveUtils;

/**
 * main class for an swing main menu
 * 
 * @author xyan
 * 
 */
public class ConfigMenuBar extends JMenuBar {
    private static final long serialVersionUID = -166649388214603104L;
    /**
     * menu labels
     */
    private static final Config TEXT_LABEL_FILE = Cfg.register("main.menu.file", "File");
    private static final Config TEXT_MENU_CONTROLS = Cfg.register("menu.label.controls", "Controls");
    private static final Config TEXT_MENU_RENDERING = Cfg.register("menu.label.rendering", "Rendering");
    private static final Config TEXT_MENU_DRIVER = Cfg.register("menu.label.driver", "Driver");
    private static final Config TEXT_MENU_DRIVER_HSQL = Cfg.register("menu.label.driver.hsql", "HyperSQL");
    private static final Config TEXT_MENU_DRIVER_TILE = Cfg.register("menu.label.driver.tiles", "Tiles");
    private static final Config TEXT_MENU_MISC = Cfg.register("menu.label.misc", "Misc");

    /**
     * constructor builds menu
     */
    public ConfigMenuBar() {
        add(new ConfigMenu(TEXT_LABEL_FILE, new JMenuItem[] {//
                // File
                new CmdItem("main.action.exit", new ICmd() {
                    public void doit(final Config key, final CmdItem cmdItem) {
                        System.exit(0);
                    }
                }) }));
        add(new ConfigMenu(TEXT_MENU_CONTROLS, new JMenuItem[] {
                // Controls
                new CmdItem("main.render.abort", new ICmd() {
                    public void doit(final Config key, final CmdItem cmdItem) {
                        ThreadLocal.set(Boolean.TRUE, Example.db);
                        ThreadLocal.set(Boolean.TRUE, Example.paint);
                    }
                }),//
                new ConfigItem(MoveUtils.CONF_MOVE_SCALE),//
                new ConfigItem(MoveUtils.CONF_ZOOM_SCALE), //
                new ConfigItem(Example.CONF_EVENT_ACCEL), //
                new ConfigItem(Example.CONF_EVENT_ACCEL_LIMIT) //
                }));
        add(new ConfigMenu(TEXT_MENU_RENDERING, new JMenuItem[] {
                // Rendering
                new BoolItem(AwtListener.CONF_RENDER_HQ), //
                new BoolItem(AwtListener.CONF_RENDER_BUFFER), //
                new BoolItem(AwtListener.CONF_RENDER_ADDLAYER), //
                new ConfigItem(Example.CONF_EVENT_LOOPGAP),//
                Separator.instance,// ---
                new ConfigItem(ZoomNodeLayer.CONF_NODES_LIMIT), //
                new ConfigItem(ZoomWayLayer.CONF_AREA_MUSTFIT), //
                new ConfigItem(ZoomAreaLayer.CONF_AREA_MUSTFIT), //
                new BoolItem(TagWayLayer.CONF_DRAW_GRIDS) //
                }));
        add(new ConfigMenu(TEXT_MENU_DRIVER, new JMenuItem[] {
                // Driver
                new ConfigItem(Example.CONF_DATA_PROVIDER), //
                new ConfigMenu(TEXT_MENU_DRIVER_HSQL, new JMenuItem[] {//
                        // Driver - hsql
                        new ConfigItem(HSQLDriver.CONF_DB_SOURCE) }),//
                new ConfigMenu(TEXT_MENU_DRIVER_TILE, new JMenuItem[] {
                        // Driver - tiles
                        new ConfigItem(TileDriver.CONF_TILE_BASE), //
                        new ConfigItem(TileDriver.CONF_TILE_SIZE_LAT), //
                        new ConfigItem(TileDriver.CONF_TILE_SIZE_LON), //
                        new BoolItem(TileDriver.CONF_DRAW_DEBUGBOUND), //
                        new ConfigItem(TileDriver.CONF_MEM_LIMIT) }) }));
        add(new ConfigMenu(TEXT_MENU_MISC, new JMenuItem[] {
                // Misc
                new CmdItem("tag.conf.reload", new ICmd() {
                    public void doit(final Config key, final CmdItem cmdItem) {
                        Example.loadTagConfig();
                    }
                }),//
                new ConfigItem(TagConfiguration.CONF_TAG_CONF), //
                new ConfigItem(TagConfiguration.CONF_TAG_CONF_POI), //
                new ConfigItem(DBConverter.CONF_XML_FILE) //
                }));
    }

    /**
     * represents an config item that will be changed with an string converting
     * text input dialog
     * 
     * @author xyan
     * 
     */
    public static class ConfigItem extends JMenuItem implements ActionListener {
        private static final long serialVersionUID = 5904181305215169084L;
        private final Config key;
        private final Config description;

        public ConfigItem(final Config key) {
            this.key = key;
            description = Cfg.register("menu.description." + key.toString(), key.toString());
            setToolTipText(Cfg.string(description));
            setText(Cfg.string(Cfg.register("menu.label." + key.toString(), key.toString())) + "...");
            addActionListener(this);
        }

        public void actionPerformed(final ActionEvent e) {
            while (true) {
                try {
                    final String res = JOptionPane.showInputDialog(Cfg.string(description),
                            TypeConverter.type2String(Cfg.get(key)));
                    if (res != null) {
                        Cfg.setValue(key, TypeConverter.string2type(res));
                    }
                    break;
                } catch (final IllegalArgumentException ex) {
                }
            }
        }
    }

    /**
     * an item executing an command callback
     * 
     * @author xyan
     * 
     */
    public static class CmdItem extends JMenuItem implements ActionListener {
        private static final long serialVersionUID = 5904181305215169084L;
        private final Config key;
        private final Config description;
        private final ICmd cmd;

        public CmdItem(final Config key, final ICmd cmd) {
            this.key = key;
            description = Cfg.register("menu.description." + key.toString(), key.toString());
            setToolTipText(Cfg.string(description));
            this.cmd = cmd;
            setText(Cfg.string(Cfg.register("menu.label." + key.toString(), key.toString())));
            addActionListener(this);
        }

        public CmdItem(final String path, final ICmd cmd) {
            key = null;
            description = Cfg.register("menu.description." + path, path);
            setToolTipText(Cfg.string(description));
            this.cmd = cmd;
            setText(Cfg.string(Cfg.register("menu.label." + path, path)));
            addActionListener(this);
        }

        public void actionPerformed(final ActionEvent e) {
            cmd.doit(key, this);
        }
    }

    // TODO [6] add debug level settings to menu via dropdown
    // TODO [1] add radios and checkboxes

    /**
     * an checkbox item for boolean configs just toggeling the config
     * 
     * @author xyan
     * 
     */
    public static class BoolItem extends JCheckBoxMenuItem implements ActionListener {
        private static final long serialVersionUID = 5904181305215169084L;
        private final Config key;
        private final Config description;

        public BoolItem(final Config key) {
            this.key = key;
            description = Cfg.register("menu.description." + key.toString(), key.toString());
            setToolTipText(Cfg.string(description));
            setText(Cfg.string(Cfg.register("menu.label." + key.toString(), key.toString())));
            setSelected(Cfg.booleant(key).booleanValue());
            addActionListener(this);
        }

        public void actionPerformed(final ActionEvent e) {
            Cfg.setValue(key, Boolean.valueOf(isSelected()));
        }
    }

    /**
     * just an self handling sub or menu entry
     * 
     * @author xyan
     * 
     */
    public static class ConfigMenu extends JMenu {
        private static final long serialVersionUID = 8701629777187986266L;

        public ConfigMenu(final Config labelKey, final JMenuItem[] configItems) {
            setText(Cfg.string(labelKey));
            for (int i = 0; i < configItems.length; i++) {
                final JMenuItem itm = configItems[i];
                if (itm instanceof Separator) {
                    addSeparator();
                } else {
                    add(itm);
                }
            }
        }
    }

    /**
     * represents an separator line and will be replaced with an separator
     * 
     * @author xyan
     * 
     */
    public static class Separator extends JMenuItem {
        private static final long serialVersionUID = 6625103953809805202L;
        public static final Separator instance = new Separator();
    }

    /**
     * an command callback interface
     * 
     * @author xyan
     * 
     */
    public static interface ICmd {
        /**
         * execution, obmits the actual item and key
         * 
         * @param key
         * @param cmdItem
         */
        public void doit(final Config key, final CmdItem cmdItem);
    }
}