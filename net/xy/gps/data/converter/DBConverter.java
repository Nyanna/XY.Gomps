package net.xy.gps.data.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import net.xy.codebasel.Log;
import net.xy.codebasel.Utils;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.PoiData;
import net.xy.gps.data.converter.StaxOsmParser.IObjectListener;
import net.xy.gps.data.driver.HSQLDriver;

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
    private static final ConfigKey CONF_XML_FILE = Config.registerValues("source",
            "osm/xml/bremen.osm");
    private static final ConfigKey CONF_CONV_END = Config.registerValues("converter.end", "Done");

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final File in = new File(Config.getString(CONF_XML_FILE));
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

                        public void putWay(final int id, final List nodes) {
                            hsql.convertWay(id, nodes);
                        }

                        public void state(final long per) {
                            DBConverter.state = (int) per;
                        }
                    });
                    hsql.cleanWayAssociated();
                } catch (final FileNotFoundException e) {
                    e.printStackTrace();
                } catch (final XMLStreamException e) {
                    e.printStackTrace();
                } catch (final FactoryConfigurationError e) {
                    e.printStackTrace();
                } catch (final SQLException e) {
                    e.printStackTrace();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
        parse.setPriority(Thread.MIN_PRIORITY);
        parse.start();
        while (parse.isAlive()) {
            System.out.println(state + "%");
            Utils.sleep(1000);
        }
        Log.trace(CONF_CONV_END);
    }
}
