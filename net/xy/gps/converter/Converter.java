package net.xy.gps.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import net.xy.codebase.Utils;
import net.xy.gps.converter.StaxOsmParser.IObjectListener;
import net.xy.gps.data.HSQLDriver;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.PoiData;

/**
 * onverter app converts osm xml to native data format
 * 
 * @author Xyan
 * 
 */
public class Converter {
    /**
     * progress in percentage
     */
    private static int state = 0;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final File in = new File("osm/xml/bremen.osm");
        final Thread parse = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HSQLDriver hsql = new HSQLDriver();
                    hsql.resetTables();
                    StaxOsmParser.parse(in, new IObjectListener() {
                        @Override
                        public void put(final IDataObject data) {
                            if (data instanceof PoiData) {
                                hsql.addNode((PoiData) data);
                            }
                        }

                        @Override
                        public void putWay(final int id, final List nodes) {
                            hsql.convertWay(id, nodes);
                        }

                        @Override
                        public void state(final long per) {
                            Converter.state = (int) per;
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
        System.out.println("Done.");
    }
}