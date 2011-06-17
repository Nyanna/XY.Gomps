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
package net.xy.gps.data.converter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.xy.codebasel.Log;
import net.xy.codebasel.Utils;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.PoiData;
import net.xy.gps.data.converter.StaxOsmParser.IObjectListener;
import net.xy.gps.data.driver.HSQLDriver;
import net.xy.gps.data.tag.TagConfiguration;
import net.xy.gps.data.tag.TagFactory;

/**
 * onverter app converts osm xml to native data format
 * 
 * @author Xyan
 * 
 */
public class DBConverter {
    /**
     * progress in percentage
     */
    private static int state = 0;
    /**
     * xm source name
     */
    public static final Config CONF_XML_FILE = Cfg.register("converter.source", "osm/xml/bremen.osm");
    private static final Config TEXT_CONV_END = Cfg.register("converter.end", "Done with parsing Creating tiles");

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            new TagConfiguration(Cfg.string(TagConfiguration.CONF_TAG_CONF), new TagFactory());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
        final File in = new File(Cfg.string(CONF_XML_FILE));
        final Thread parse = new Thread(new Runnable() {

            public void run() {
                try {
                    final HSQLDriver hsql = new HSQLDriver();
                    hsql.resetTables();
                    StaxOsmParser.parse(in, new IObjectListener() {

                        public void put(final IDataObject data) {
                            if (data instanceof PoiData) {
                                hsql.addNode((PoiData) data);
                            }
                        }

                        public void putWay(final int id, final List nodes, final Integer[] tags) {
                            hsql.convertWay(id, nodes, tags);
                        }

                        public void state(final long per) {
                            DBConverter.state = (int) per;
                        }
                    });
                    hsql.cleanWayAssociated();
                } catch (final Throwable e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        parse.setPriority(Thread.MIN_PRIORITY);
        parse.start();
        while (parse.isAlive()) {
            System.out.println(state + "%");
            Utils.sleep(1000);
        }
        Log.trace(TEXT_CONV_END);
        TileCreator.main(args);
    }
}
